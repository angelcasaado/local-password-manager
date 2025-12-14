package com.localpasswordmanager.service;

public interface SavePassword {
    void savePassword(String username, String password, String masterKey, String managerUser, String websiteName);
}