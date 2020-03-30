package org.openjfx.filters;

import javafx.scene.image.Image;

public class DitheringFilters extends ImageFilter {

    public DitheringFilters(Image image) {
        super(image);
    }

    public Image convertToGreyScale(Image image) {
        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        for(int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            int greyScale = (int )Math.min(0.3 * red + 0.6 * green + 0.1 * blue, 255);

            int newPixel = (greyScale << 16) | (greyScale << 8) | greyScale;
            buffer[i] = newPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    private int[] convertToYCbCr(int [] buffer) {

        int [] newBuffer = new int[buffer.length];

        for(int i = 0; i < buffer.length; i++) {
            int R = (buffer[i] >> 16) & 0xFF;
            int G = (buffer[i] >> 8) & 0xFF;
            int B = buffer[i] & 0xFF;

            int Y = (int)(16 + 1.0/256.0 * ( 65.738 * R + 129.057 * G + 25.064 * B ));
            int Cb = (int)(128 + 1.0/256.0 * ( -1 * 37.945 * R - 74.494 * G + 112.439 * B));
            int Cr = (int)(128 + 1.0/256.0 * ( 112.439 * R - 94.154 * G - 18.285 * B));

            Y = Math.max(Math.min(Y, 235), 16);
            Cb = Math.max(Math.min(Cb, 240), 16);
            Cr = Math.max(Math.min(Cr, 240), 16);

            newBuffer[i] = (Y << 16) | (Cb << 8) | Cr;
        }
        return newBuffer;
    }

    private int convertToRGB(int pixel) {

            int Y = (pixel >> 16) & 0xFF;
            int Cb = (pixel >> 8) & 0xFF;
            int Cr = pixel & 0xFF;

            int R = (int)((298.082 * Y + 408.583 * Cr ) / 256.0 - 222.921);
            int G = (int)((298.082 * Y - 100.291 * Cb - 208.120 * Cr ) / 256.0 + 135.576);
            int B = (int)((298.082 * Y + 516.412 * Cb ) / 256.0 - 276.836);

            R = Math.max(Math.min(R, 255), 0);
            G = Math.max(Math.min(G, 255), 0);
            B = Math.max(Math.min(B, 255), 0);

            return (R << 16) | (G << 8) | B;
    }

    public Image averageDitheringYCbCr(Image image, int k) {
        if(k < 2) return image;

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        int [] YCbCrBuffer = convertToYCbCr(buffer);

        long [][] thresholds = new long [3][k-1];
        int [][] counts = new int [3][k-1];

        int intervalCbCr = 224 / (k-1);
        int intervalY = 219 / (k-1);

        for(int pixel : YCbCrBuffer) {
            int Y = (pixel >> 16) & 0xFF;
            int Cb = (pixel >> 8) & 0xFF;
            int Cr = pixel & 0xFF;

            int indexY = Math.min(Y / intervalY, k - 2);
            int indexCb = Math.min(Cb / intervalCbCr, k - 2);
            int indexCr = Math.min(Cr / intervalCbCr, k - 2);

            thresholds[0][indexY] += Y;
            thresholds[1][indexCb] += Cb;
            thresholds[2][indexCr] += Cr;

            counts[0][indexY]++;
            counts[1][indexCb]++;
            counts[2][indexCr]++;
        }

        for(int j = 0; j < 3; j++) {
            for (int i = 0; i < thresholds[j].length; i++) {
                if (counts[j][i] != 0) {
                    thresholds[j][i] /= counts[j][i];
                }
            }
        }

        for(int i = 0; i < YCbCrBuffer.length; i++) {

            int Y = (YCbCrBuffer[i] >> 16) & 0xFF;
            int Cb = (YCbCrBuffer[i] >> 8) & 0xFF;
            int Cr = YCbCrBuffer[i] & 0xFF;

            int indexY = Math.min(Y / intervalY, k - 2);
            int indexCb = Math.min(Cb / intervalCbCr, k - 2);
            int indexCr = Math.min(Cr / intervalCbCr, k - 2);

            Y = Y > thresholds[0][indexY] ? (indexY + 1) * intervalY : indexY * intervalY;
            Cb = Cb > thresholds[1][indexCb] ? (indexCb + 1) * intervalCbCr : indexCb * intervalCbCr;
            Cr = Cr > thresholds[2][indexCr] ? (indexCr + 1) * intervalCbCr : indexCr * intervalCbCr;

            int rgbPixel = convertToRGB((Y << 16) | (Cb << 8) | Cr);
            buffer[i] = rgbPixel | 0xFF000000;
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);

        return newImage;
    }

    public Image averageDithering(Image image, int k) {

        if(k < 2) return image;

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        long [][] thresholds = new long [3][k-1];
        int [][] counts = new int [3][k-1];
        int [][] indices = new int [3][buffer.length];
        int interval = 255 / (k-1);

        for (int i = 0; i < buffer.length; i++) {

            for(int j = 0; j < 3; j++) {
                int channel = (buffer[i] >> (16 - 8 * j)) & 0xFF;
                int index = Math.min( channel / interval, k - 2);
                indices[j][i] = index;
                thresholds[j][index] += channel;
                counts[j][index]++;
            }
        }

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < thresholds[i].length; j++) {
                if (counts[i][j] != 0) {
                    thresholds[i][j] /= counts[i][j];
                }
            }
        }

        for(int i = 0; i < buffer.length; i++) {
            int newPixel = 0;

            for(int j = 0; j < 3; j++) {
                int channel = (buffer[i] >> (16 - 8 * j)) & 0xFF;
                int idx = indices[j][i];
                channel = channel > thresholds[j][idx] ? (idx + 1) * interval : idx * interval;
                newPixel |= channel << (16 - 8 * j);
            }

            buffer[i] = newPixel | 0xFF000000;
        }
        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);
        return newImage;
    }

}
