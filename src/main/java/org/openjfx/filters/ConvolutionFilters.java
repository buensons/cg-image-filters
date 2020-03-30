package org.openjfx.filters;

import javafx.collections.transformation.SortedList;
import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

public class ConvolutionFilters extends ImageFilter {

    public ConvolutionFilters(Image image) {
        super(image);
    }

    public Image applyFilter(Image image, int [] kernel, int n, int offset) {

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        int [] extractedBuffer = extractRGB();

        int kernelSum = 0;

        for (int value : kernel) {
            kernelSum += value;
        }

        int x = 0;
        int y = 0;
        for(int i = 0; i < buffer.length; i++) {

            int red = 0;
            int green = 0;
            int blue = 0;

            int kernelIdx = 0;

            for(int j = -1 * n/2; j <= n/2; j++) {
                for(int k = -1 * n/2; k <= n/2; k++) {
                    int index = 3*i + (3*(int)image.getWidth()*j) + 3*k;

                    if(index < 0 || index >= extractedBuffer.length) {
                        // ignore
                    } else {

                        if(x - 1 < 0 || y - 1 < 0 || x + 1 > image.getWidth() - 1 || y + 1 > image.getHeight() - 1) {
                            // ignore
                        } else {
                            red += extractedBuffer[index] * kernel[kernelIdx];
                            green += extractedBuffer[index + 1] * kernel[kernelIdx];
                            blue += extractedBuffer[index + 2] * kernel[kernelIdx];
                        }
                    }
                    kernelIdx++;
                }
            }

            red += offset;
            green += offset;
            blue += offset;

            if(kernelSum != 0) {
                red /= kernelSum;
                green /= kernelSum;
                blue /= kernelSum;
            }

            red = Math.min(Math.max(red, 0), 255);
            green = Math.min(Math.max(green, 0), 255);
            blue = Math.min(Math.max(blue, 0), 255);

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;

            if(++x > image.getWidth() - 1) {
                x = 0;
                ++y;
            }
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image medianFilter(Image image, int n) {
        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0, 0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        int[] extractedBuffer = extractRGB();

        for (int i = 0; i < buffer.length; i++) {

            int red = 0;
            int green = 0;
            int blue = 0;

            TreeMap<Integer, int[]> sortedMap = new TreeMap<>();

            for (int j = -1 * n / 2; j <= n / 2; j++) {
                for (int k = -1 * n / 2; k <= n / 2; k++) {
                    int index = 3 * i + (3 * (int) image.getWidth() * j) + 3 * k;

                    if (index < 0 || index >= extractedBuffer.length) {
                        // ignore for now
                        // TO-DO bound checking
                    } else {

                        int greyScale = (int)(0.3 * extractedBuffer[index]
                                + 0.6 * extractedBuffer[index + 1] + 0.1 * extractedBuffer[index + 1]);

                        sortedMap.put(greyScale, new int [] {extractedBuffer[index], extractedBuffer[index + 1],
                                extractedBuffer[index + 2]});
                    }
                }
            }

            int l = 0;
            for(Integer key : sortedMap.descendingKeySet()) {

                if(l == (sortedMap.size()/2)) {
                    red = sortedMap.get(key)[0];
                    green = sortedMap.get(key)[1];
                    blue = sortedMap.get(key)[2];
                }

                l++;
            }

            red = Math.min(Math.max(red, 0), 255);
            green = Math.min(Math.max(green, 0), 255);
            blue = Math.min(Math.max(blue, 0), 255);

            int newPixel = (red << 16) | (green << 8) | blue;

            buffer[i] = newPixel | 0xFF000000;

        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;

    }

    private int[] extractRGB() {
        int [] buff = new int [buffer.length * 3];

        for(int i = 0; i < buffer.length; i++) {
            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            buff[3*i + 0] = red;
            buff[3*i + 1] = green;
            buff[3*i + 2] = blue;
        }

        return buff;
    }

}
