package com.imagecrypto;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionAndEmbedding {
    public static byte[] flattenedGrayscale(int[][] grayscale){
        int height = grayscale.length;
        int width = grayscale[0].length;
        byte[] flat = new byte[height*width];

        for(int y = 0 ; y < height ; y++){
            for(int x = 0 ; x < width ; x ++){
                flat[y*width + x] = (byte)grayscale[y][x];
            }
        }
        return flat;
    }

    public static byte[] generateRandomKey(){
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static byte[] encrypt(byte[] data , byte[] keyBytes) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher code = Cipher.getInstance("AES/ECB/PKCS5Padding");
        code.init(Cipher.ENCRYPT_MODE, secretKey);

        return code.doFinal(data);
    }

    public static void keyFile(byte[] key , String filePath) throws IOException{
        String encodedKey = Base64.getEncoder().encodeToString(key);
        try (FileWriter fw = new FileWriter(filePath)){
            fw.write(encodedKey);
        }            
    }
    
}
