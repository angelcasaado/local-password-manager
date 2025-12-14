package com.localpasswordmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String encryptedPassword;

    private String managerUser;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "website_id")
    private Website website;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "user_id")
    private User user;

    // Default constructor required by JPA
    public UserCredential() {
    }

    public UserCredential(String username, String encryptedPassword, String managerUser, Website website) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.managerUser = managerUser;
        this.website = website;
    }
}
