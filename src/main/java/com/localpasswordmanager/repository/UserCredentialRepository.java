package com.localpasswordmanager.repository;

import com.localpasswordmanager.model.UserCredential;
import com.localpasswordmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByUsername(String username);
    java.util.List<UserCredential> findByUser(User user);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT uc.website FROM UserCredential uc WHERE uc.user = :user")
    java.util.List<com.localpasswordmanager.model.Website> findDistinctWebsitesByUser(@org.springframework.data.repository.query.Param("user") User user);
    
    java.util.List<UserCredential> findByWebsiteIdAndUser(Long websiteId, User user);
}
