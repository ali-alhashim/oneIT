package com.alhashim.oneIT.config;

import org.apache.commons.codec.binary.Base32;

import java.security.SecureRandom;

public class OtpUtil {
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] buffer = new byte[10];
        random.nextBytes(buffer);
        Base32 base32 = new Base32();
        return base32.encodeToString(buffer).replace("=", "");
    }

    public static String getOtpAuthUrl(String secret, String username) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=one-IT", username, secret);
    }
}
