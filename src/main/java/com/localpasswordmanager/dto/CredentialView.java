package com.localpasswordmanager.dto;

public record CredentialView(Long id, String username, String decryptedPassword, String managerUser) {
}
