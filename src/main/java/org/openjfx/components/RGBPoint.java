package org.openjfx.components;

import java.util.Random;

public class RGBPoint {

    protected int r, g, b;

    public RGBPoint(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static RGBPoint[] dataToRGB(int [] buffer) {

        RGBPoint [] points = new RGBPoint[buffer.length];

        for(int i = 0; i < buffer.length; i++) {

            var red = (buffer[i] >> 16) & 0xFF;
            var green = (buffer[i] >> 8) & 0xFF;
            var blue = buffer[i] & 0xFF;

            points[i] = new RGBPoint(red, green, blue);
        }

        return points;
    }

    public static int rgbToRawData(RGBPoint point) {
        int newPixel = (point.r << 16) | (point.g << 8) | point.b;
        return newPixel | 0xFF000000;
    }

    public static double distance(RGBPoint a, RGBPoint b) {
        return Math.sqrt(Math.pow((a.r - b.r), 2) + Math.pow((a.g - b.g), 2) + Math.pow((a.b - b.b), 2));
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
