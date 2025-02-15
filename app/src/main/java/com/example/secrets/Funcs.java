package com.example.secrets;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Funcs {

    public static Bitmap hide_image(Bitmap cover_image, Bitmap secret_image) {
        int coverWidth = cover_image.getWidth();
        int coverHeight = cover_image.getHeight();
        int secretWidth = secret_image.getWidth();
        int secretHeight = secret_image.getHeight();

        // Create a new Bitmap with the dimensions of the cover image
        Bitmap stegoImage = Bitmap.createBitmap(coverWidth, coverHeight, Bitmap.Config.ARGB_8888);

        // Copy the cover image to the new stego image
        for (int x = 0; x < coverWidth; x++) {
            for (int y = 0; y < coverHeight; y++) {
                stegoImage.setPixel(x, y, cover_image.getPixel(x, y));
            }
        }

        // Hide the secret image within the stego image
        for (int x = 0; x < coverWidth; x++) {
            for (int y = 0; y < coverHeight; y++) {

                if (x < secretWidth && y < secretHeight) {
                    int coverPixel = stegoImage.getPixel(x, y);
                    int secretPixel = secret_image.getPixel(x, y);

                    // Extract the color components from the pixels
                    int coverRed = (coverPixel >> 16) & 0xFF;
                    int coverGreen = (coverPixel >> 8) & 0xFF;
                    int coverBlue = coverPixel & 0xFF;

                    int secretRed = (secretPixel >> 16) & 0xFF;
                    int secretGreen = (secretPixel >> 8) & 0xFF;
                    int secretBlue = secretPixel & 0xFF;

                    // Embed the MSB of the secret image into the LSB of the cover image
                    coverRed = (coverRed & 0xFE) | (secretRed >> 7);
                    coverGreen = (coverGreen & 0xFE) | (secretGreen >> 7);
                    coverBlue = (coverBlue & 0xFE) | (secretBlue >> 7);

                    // Create a new pixel with the modified color components
                    int newPixel = Color.rgb(coverRed, coverGreen, coverBlue);
                    stegoImage.setPixel(x, y, newPixel);
                }
            }
        }

        return stegoImage;
    }

    public static Bitmap hide_text(Bitmap cover_image, String message) {
        int width = cover_image.getWidth();
        int height = cover_image.getHeight();
        int totalPixels = width * height;

        // Check if the image has enough space
        if (totalPixels < message.length() + 4) { //Increased space for message
            throw new IllegalArgumentException("Image not large enough to hide text");
        }

        byte[] messageBytes = message.getBytes();
        int messageLength = messageBytes.length;

        // Store the length of the message in the first 4 pixels
        cover_image.setPixel(0, 0, (messageLength >> 24) & 0xFF);
        cover_image.setPixel(0, 1, (messageLength >> 16) & 0xFF);
        cover_image.setPixel(0, 2, (messageLength >> 8) & 0xFF);
        cover_image.setPixel(0, 3, messageLength & 0xFF);

        int charIndex = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (charIndex < messageLength) {
                    // Skip the first 4 pixels
                    if ((i == 0 && j < 4)) continue;

                    int pixel = cover_image.getPixel(i, j);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = messageBytes[charIndex]; //Store message bytes

                    cover_image.setPixel(i, j, Color.rgb(red, green, blue));
                    charIndex++;
                }
            }
        }

        return cover_image;
    }

    public static Bitmap reveal_image(Bitmap stego_image, int secretWidth, int secretHeight) {
        // Create a new Bitmap to hold the revealed secret image
        Bitmap revealedImage = Bitmap.createBitmap(secretWidth, secretHeight, Bitmap.Config.ARGB_8888);

        // Extract the secret image from the stego image
        for (int x = 0; x < secretWidth; x++) {
            for (int y = 0; y < secretHeight; y++) {
                int stegoPixel = stego_image.getPixel(x, y);

                // Extract the LSBs from the stego image's pixel
                int stegoRed = (stegoPixel >> 16) & 0xFF;
                int stegoGreen = (stegoPixel >> 8) & 0xFF;
                int stegoBlue = stegoPixel & 0xFF;

                // Reconstruct the secret image's pixel
                int secretRed = (stegoRed & 0x01) << 7;
                int secretGreen = (stegoGreen & 0x01) << 7;
                int secretBlue = (stegoBlue & 0x01) << 7;

                // Create the secret image pixel
                int secretPixel = Color.rgb(secretRed, secretGreen, secretBlue);
                revealedImage.setPixel(x, y, secretPixel);
            }
        }

        return revealedImage;
    }

    public static String reveal_text(Bitmap cover_image) {
        int width = cover_image.getWidth();
        int height = cover_image.getHeight();

        // Get the length of the message from the first 4 pixels
        int messageLength = (cover_image.getPixel(0, 0) << 24) |
                (cover_image.getPixel(0, 1) << 16) |
                (cover_image.getPixel(0, 2) << 8) |
                cover_image.getPixel(0, 3);

        byte[] messageBytes = new byte[messageLength];
        int charIndex = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (charIndex < messageLength) {
                    // Skip the first 4 pixels
                    if ((i == 0 && j < 4)) continue;

                    int pixel = cover_image.getPixel(i, j);
                    messageBytes[charIndex] = (byte) (Color.blue(pixel) & 0xFF); //Extract bytes

                    charIndex++;
                }
            }
        }
        return new String(messageBytes);
    }
}
