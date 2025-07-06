package com.imagecrypto;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Base64;
import java.util.Scanner;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

public class Decryption {

    public static byte[] extractedData(String wavPath) throws Exception{
       RandomAccessFile randomFile = new RandomAccessFile(wavPath, "r");
       long fileLength = randomFile.length();
       randomFile.seek(fileLength - 4);
       int encryptedLength = Integer.reverseBytes(randomFile.readInt());
       randomFile.seek(fileLength - 4 - encryptedLength);
       byte[] encryptedData = new byte[encryptedLength];
       randomFile.readFully(encryptedData);
       randomFile.close();

       return encryptedData;
    }

    public static byte[] keyRecognition(byte[] encryptedData, byte[] keyBytes) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }


    public static int[][] reconstructed_2D (byte [] decryptedData){
        int width = 150;
        int height = 150;

        int [][] convertedGrayscale = new int[height][width];

        for (int y = 0; y < height; y++){
            for(int x =0; x < width; x++){
                int index = y*width + x;

             convertedGrayscale[y][x] = decryptedData[index] & 0xFF ;
            }
        }
        return convertedGrayscale;
    }
    public static void createImage(int [][] convertedGrayscale , String outputPath) throws Exception{
        int height = convertedGrayscale.length;
        int width = convertedGrayscale[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++){
            for(int x =0; x < width; x++){
                int new_gray = convertedGrayscale[y][x];
                Color colour = new Color(new_gray, new_gray, new_gray);
                image.setRGB(x, y, colour.getRGB());
            }
        }

        File newOutputFile = new File(outputPath);
        ImageIO.write(image, "png", newOutputFile);
        System.out.println("Decrypted Image Saved");
    }
}
