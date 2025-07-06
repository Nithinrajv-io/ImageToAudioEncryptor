package com.imagecrypto;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.io.*;

public class ImageToGrayscale {
    public static int[][] convert(String input_path , String output_path){
        try {
            int width = 150;
            int height = 150;

            File inputFile = new File(input_path);
            BufferedImage OriginalImage = ImageIO.read(inputFile);
            
            Image tmp = OriginalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gd = resizedImage.createGraphics();
            gd.drawImage(tmp, 0, 0, null);
            gd.dispose();
   
            int[][] grayscale = new int[height][width];
   
            for(int y = 0 ; y < height ; y++){
                for(int x = 0 ; x < width ; x++){
                    Color color = new Color(resizedImage.getRGB(x, y));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    int gray = (int)(0.3 * r + 0.59 * g + 0.11 * b);
                    grayscale[y][x] = gray;

                    Color grayColor = new Color(gray, gray, gray);
                    resizedImage.setRGB(x, y, grayColor.getRGB());
                }
            }

            // ImageIO.write(resizedImage, "png", new File(output_path, "grayscale_image.png"));

            return grayscale;

        } catch (IOException e) {
            System.out.println("An error occured during grayscale conversion!");
            
            return null;
        }
    }
}