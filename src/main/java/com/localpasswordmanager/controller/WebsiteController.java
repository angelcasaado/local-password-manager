package com.localpasswordmanager.controller;

import com.localpasswordmanager.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebsiteController {

    private final WebsiteRepository websiteRepository;
    private final com.localpasswordmanager.repository.UserCredentialRepository userCredentialRepository;
    private final com.localpasswordmanager.service.EncryptionService encryptionService;

    private final com.localpasswordmanager.service.UserService userService;

    @GetMapping("/websites")
    public String listWebsites(java.security.Principal principal, Model model) {
        var user = userService.findByUsername(principal.getName());
        model.addAttribute("websites", userCredentialRepository.findDistinctWebsitesByUser(user));
        return "websites";
    }

    @GetMapping("/websites/{id}")
    public String websiteDetails(@org.springframework.web.bind.annotation.PathVariable Long id,
                                 @org.springframework.web.bind.annotation.RequestParam(required = false) String managerUserFilter,
                                 java.security.Principal principal,
                                 Model model) {
        var website = websiteRepository.findById(id).orElseThrow(() -> new RuntimeException("Website not found"));
        var user = userService.findByUsername(principal.getName());

        java.util.List<com.localpasswordmanager.model.UserCredential> userCredentials = userCredentialRepository.findByWebsiteIdAndUser(id, user);

        java.util.stream.Stream<com.localpasswordmanager.model.UserCredential> stream = userCredentials.stream();

        if (managerUserFilter != null && !managerUserFilter.isBlank()) {
            stream = stream.filter(c -> c.getManagerUser() != null && c.getManagerUser().toLowerCase().contains(managerUserFilter.toLowerCase()));
        }

        java.util.List<com.localpasswordmanager.dto.CredentialView> credentials = stream
                .map(c -> new com.localpasswordmanager.dto.CredentialView(
                        c.getId(),
                        c.getUsername(),
                        encryptionService.decrypt(c.getEncryptedPassword(), "defaultMasterKey"),
                        c.getManagerUser()
                ))
                .toList();

        model.addAttribute("website", website);
        model.addAttribute("credentials", credentials);
        model.addAttribute("managerUserFilter", managerUserFilter);
        return "website-details";
    }
    @GetMapping("/websites/{id}/edit")
    public String editWebsite(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        var website = websiteRepository.findById(id).orElseThrow(() -> new RuntimeException("Website not found"));
        model.addAttribute("website", website);
        return "website-edit";
    }

    @org.springframework.web.bind.annotation.PostMapping("/websites/{id}/update")
    public String updateWebsite(@org.springframework.web.bind.annotation.PathVariable Long id,
                                @org.springframework.web.bind.annotation.ModelAttribute com.localpasswordmanager.model.Website websiteForm) {
        var website = websiteRepository.findById(id).orElseThrow(() -> new RuntimeException("Website not found"));
        website.setName(websiteForm.getName());
        website.setUrl(websiteForm.getUrl());
        websiteRepository.save(website);
        return "redirect:/websites/" + id;
    }
}
