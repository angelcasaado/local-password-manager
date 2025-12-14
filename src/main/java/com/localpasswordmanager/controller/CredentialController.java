package com.localpasswordmanager.controller;

import com.localpasswordmanager.model.UserCredential;
import com.localpasswordmanager.model.Website;
import com.localpasswordmanager.repository.UserCredentialRepository;
import com.localpasswordmanager.repository.WebsiteRepository;
import com.localpasswordmanager.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final WebsiteRepository websiteRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final EncryptionService encryptionService;
    private final com.localpasswordmanager.service.UserService userService;

    @GetMapping("/create")
    public String createCredentialForm(Model model) {
        return "create-credential";
    }

    @PostMapping
    public String saveCredential(@RequestParam String websiteName,
                                 @RequestParam String websiteUrl,
                                 @RequestParam String username,
                                 @RequestParam String password,
                                 java.security.Principal principal) {
        
        // 1. Find or create website
        Website website = websiteRepository.findByName(websiteName)
                .orElseGet(() -> websiteRepository.save(new Website(websiteName, websiteUrl)));

        // 2. Encrypt password
        String encryptedPassword = encryptionService.encrypt(password, "defaultMasterKey");

        // 3. Get current user
        var user = userService.findByUsername(principal.getName());

        // 4. Create and save credential
        UserCredential credential = new UserCredential(username, encryptedPassword, principal.getName(), website);
        credential.setUser(user);
        userCredentialRepository.save(credential);

        return "redirect:/websites/" + website.getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteCredential(@org.springframework.web.bind.annotation.PathVariable Long id, java.security.Principal principal) {
        UserCredential credential = userCredentialRepository.findById(id).orElseThrow(() -> new RuntimeException("Credential not found"));
        
        // Security check: Match user
        if (!credential.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }
        
        Long websiteId = credential.getWebsite().getId();
        userCredentialRepository.delete(credential);
        
        return "redirect:/websites/" + websiteId;
    }
}
