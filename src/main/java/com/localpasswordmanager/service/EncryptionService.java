package com.localpasswordmanager.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public String encrypt(String data, String masterKey) {
        try {
            SecretKey secretKey = getKeyFromPassword(masterKey);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting data", e);
        }
    }

    public String decrypt(String encryptedData, String masterKey) {
        try {
            SecretKey secretKey = getKeyFromPassword(masterKey);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting data", e);
        }
    }

    private SecretKey getKeyFromPassword(String password) throws Exception {
        // Simple key derivation. In production, use PBKDF2 with salt.
        // For this local basic request, we'll hash the key to 32 bytes (AES-256) or pad it.
        // Actually, let's use PBKDF2 for better practice, but with a fixed salt for simplicity since we don't store salt yet.
        // Or simpler: just use SHA-256 of the key.
        
        java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        return new SecretKeySpec(key, ALGORITHM);
    }
}
