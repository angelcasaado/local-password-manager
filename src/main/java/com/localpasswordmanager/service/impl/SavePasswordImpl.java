package com.localpasswordmanager.service.impl;

import com.localpasswordmanager.model.UserCredential;
import com.localpasswordmanager.repository.UserCredentialRepository;
import com.localpasswordmanager.service.EncryptionService;
import com.localpasswordmanager.service.SavePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavePasswordImpl implements SavePassword {

    private final UserCredentialRepository userCredentialRepository;
    private final com.localpasswordmanager.repository.WebsiteRepository websiteRepository;
    private final EncryptionService encryptionService;

    @Override
    public void savePassword(String username, String password, String masterKey, String managerUser, String websiteName) {
        String encryptedPassword = encryptionService.encrypt(password, masterKey);
        com.localpasswordmanager.model.Website website = websiteRepository.findByName(websiteName)
                .orElseThrow(() -> new RuntimeException("Website not found: " + websiteName));

        UserCredential credential = new UserCredential(username, encryptedPassword, managerUser, website);
        userCredentialRepository.save(credential);
    }

}