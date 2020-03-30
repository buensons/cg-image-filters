package org.openjfx.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Centroid extends RGBPoint {
    private List<RGBPoint> points;

    public Centroid(int r, int g, int b) {
        super(r,g,b);
        this.points = new ArrayList<>();
    }

    public static Centroid random(int [] buffer) {
        var random = new Random();
        var randomPixel = buffer[random.nextInt(buffer.length - 1)];

        var red = (randomPixel >> 16) & 0xFF;
        var green = (randomPixel >> 8) & 0xFF;
        var blue = randomPixel & 0xFF;

        return new Centroid(red, green, blue);
    }

    public void addPoint(RGBPoint point) {
        points.add(point);
    }

    public boolean recalculateAndCheckIfChanged() {
        int rSum = 0, gSum = 0, bSum = 0;
        for(RGBPoint point : points) {
            rSum += point.getR();
            gSum += point.getG();
            bSum += point.getB();
        }

        if(points.size() != 0) {

            int newRed = rSum / points.size();
            int newGreen = gSum / points.size();
            int newBlue = bSum / points.size();

            if(newRed == r && newGreen == g && newBlue == b) {
                return false;
            }

            this.r = newRed;
            this.g = newGreen;
            this.b = newBlue;
        }

        return true;
    }

    public void merge() {
        for(RGBPoint point : points) {
            point.r = r;
            point.g = g;
            point.b = b;
        }
    }

    public List<RGBPoint> getPoints() {
        return points;
    }

    public void setPoints(List<RGBPoint> points) {
        this.points = points;
    }
}
