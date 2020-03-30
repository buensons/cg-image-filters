package org.openjfx.filters;

import javafx.scene.image.*;

import java.nio.IntBuffer;

abstract class ImageFilter {

    WritablePixelFormat<IntBuffer> format;
    WritableImage newImage;
    PixelReader pixelReader;
    PixelWriter pixelWriter;
    int imageWidth;
    int imageHeight;
    int [] buffer;

    ImageFilter(Image image) {
        imageWidth = (int) image.getWidth();
        imageHeight = (int) image.getHeight();

        format = PixelFormat.getIntArgbPreInstance();
        newImage = new WritableImage(imageWidth, imageHeight);
        buffer = new int [imageWidth * imageHeight];

        pixelReader = image.getPixelReader();
        pixelWriter = newImage.getPixelWriter();
    }
}
