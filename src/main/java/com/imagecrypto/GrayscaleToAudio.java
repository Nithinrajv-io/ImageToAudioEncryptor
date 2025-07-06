package com.imagecrypto;

import java.io.*;

public class GrayscaleToAudio {

    final private static double minFreq = 300;
    final private static double maxFreq = 1000;
    final private static int sampleRate = 44100;
    final private static int amplitude = 32767;

    private static byte[] generateTone(int gray){
        double duration = 0.2;
        int numberOfSamples = (int)(sampleRate*duration);
        
        double freq = minFreq + (gray / 255) * (maxFreq - minFreq);
        byte[] audioData = new byte[2*numberOfSamples];

        for(int i = 0; i < numberOfSamples ; i++){
            double angle = (2*Math.PI*freq * i/sampleRate);

            short sample = (short)(amplitude*Math.sin(angle));

            audioData[2*i] = (byte)(sample & 0xFF);
            audioData[2*i + 1] = (byte)((sample >> 8) & 0xFF);
        }
        return audioData;
    }

    private static byte[] silenceGenerate(){
        double duration = 0.05;
        int numberOfSamples = (int)(sampleRate*duration);

        byte[] silence = new byte[2*numberOfSamples];

        return silence;
    }

    public static byte[] convertGrayscaleToAudio(int[][] grayscale){
        int height = grayscale.length;
        int width = grayscale[0].length;

        byte[] toneSample = generateTone(0);
        byte[] silenceSample = silenceGenerate();
        int samplesPerPixel = toneSample.length + silenceSample.length;

        byte[] fullAudio = new byte[samplesPerPixel * height * width];

        int index = 0;

        for(int y = 0 ; y < height ; y++){
            for(int x = 0; x < width ; x++){
                int gray = grayscale[y][x];
                byte[] tone = generateTone(gray);
                byte[] silence = silenceGenerate();

                System.arraycopy(tone, 0, fullAudio, index, tone.length);
                index += tone.length;

                System.arraycopy(silence, 0, fullAudio, index, silence.length);
                index += silence.length;
            }
        }
        return fullAudio;
    }

    public static void writeWAVWithEncryption(byte[] audioData, byte[] encryptedData, String outputPath) {
    try {
        FileOutputStream fos = new FileOutputStream(outputPath);
        DataOutputStream dos = new DataOutputStream(fos);

        int bitsPerSample = 16;
        int channels = 1;
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        int audioDataSize = audioData.length;
        int encryptedDataSize = encryptedData.length;
        int fullDataSize = audioDataSize + encryptedDataSize;
        int chunkSize = 36 + fullDataSize;

        // RIFF header
        dos.writeBytes("RIFF");
        dos.writeInt(Integer.reverseBytes(chunkSize));
        dos.writeBytes("WAVE");

        // fmt subchunk
        dos.writeBytes("fmt ");
        dos.writeInt(Integer.reverseBytes(16));
        dos.writeShort(Short.reverseBytes((short) 1));
        dos.writeShort(Short.reverseBytes((short) channels));
        dos.writeInt(Integer.reverseBytes(sampleRate));
        dos.writeInt(Integer.reverseBytes(byteRate));
        dos.writeShort(Short.reverseBytes((short) blockAlign));
        dos.writeShort(Short.reverseBytes((short) bitsPerSample));

        // data subchunk
        dos.writeBytes("data");
        dos.writeInt(Integer.reverseBytes(fullDataSize));
        dos.write(audioData);
        dos.write(encryptedData);
        dos.writeInt(Integer.reverseBytes(encryptedDataSize));

        dos.close();
        fos.close();

        System.out.println("Encrypted WAV file saved at: " + outputPath);
        } 
        catch (IOException e) {
        System.out.println("Failed to write encrypted WAV.");
        e.printStackTrace();
        }
    }

}
