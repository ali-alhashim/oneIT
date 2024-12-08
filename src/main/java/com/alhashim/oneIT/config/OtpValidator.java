package com.alhashim.oneIT.config;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class OtpValidator {

    public static boolean validateOtp(String secret, int otp) {
        long timeWindow = System.currentTimeMillis() / 30000; // 30-second time steps

        for (int i = -1; i <= 1; i++) { // Allow small time drift (previous, current, next step)
            int generatedOtp = generateOtp(secret, timeWindow + i);
            if (generatedOtp == otp) {
                return true;
            }
        }
        return false; // OTP does not match any valid range
    }

    private static int generateOtp(String secret, long time) {
        try {
            // Decode the Base32 secret
            byte[] key = new Base32().decode(secret);

            // Convert time to byte array (8-byte array, Big Endian)
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(time);
            byte[] timeBytes = buffer.array();

            // Create HMAC-SHA1 instance
            SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);

            // Generate HMAC hash
            byte[] hash = mac.doFinal(timeBytes);

            // Dynamically truncate the hash
            int offset = hash[hash.length - 1] & 0xF;
            int otp = ((hash[offset] & 0x7F) << 24 |
                    (hash[offset + 1] & 0xFF) << 16 |
                    (hash[offset + 2] & 0xFF) << 8 |
                    (hash[offset + 3] & 0xFF));

            // Return the last 6 digits of the OTP
            return otp % 1_000_000;
        } catch (Exception e) {
            throw new RuntimeException("Error generating OTP", e);
        }
    }
}