package org.openjfx.filters;

import javafx.scene.image.*;

import java.util.Map;

public class FunctionFilters extends ImageFilter {

    public FunctionFilters(Image image) {
        super(image);
    }

    public Image inversionFilter(Image image) {

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for(int i = 0; i < buffer.length; i++) {
            buffer[i] = (~buffer[i]) | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image brightnessCorrectionFilter(Image image, int value) {
        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for(int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            red = Math.max(Math.min(red + value, 255), 0);
            green = Math.max(Math.min(green + value, 255), 0);
            blue = Math.max(Math.min(blue + value, 255), 0);

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image gammaCorrectionFilter(Image image, double gamma) {

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for(int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            red = (int) (255 * Math.pow(red/255.0, 1.0/gamma));
            green = (int) (255 * Math.pow(green/255.0, 1.0/gamma));
            blue = (int) (255 * Math.pow(blue/255.0, 1.0/gamma));

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image contrastEnhancementFilter(Image image, double slope) {
        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0, 0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for (int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            red = Math.max(Math.min((int)(slope * (red - 128) + 128), 255), 0);
            green = Math.max(Math.min((int)(slope * (green - 128) + 128), 255), 0);
            blue = Math.max(Math.min((int)(slope * (blue - 128) + 128), 255), 0);

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image customFilter(Image image, Map<Integer, Integer> function) {
        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0, 0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for (int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            red = function.get(red);
            green = function.get(green);
            blue = function.get(blue);

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }
}

