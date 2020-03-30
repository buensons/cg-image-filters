package org.openjfx.filters;

import javafx.scene.image.Image;
import org.openjfx.components.Centroid;
import org.openjfx.components.RGBPoint;

import java.util.Random;

public class KMeans extends ImageFilter {

    public KMeans(Image image) {
        super(image);
    }

    public Image compute(Image image, int k) {

        if(k < 2) return image;

        pixelReader = image.getPixelReader();
        pixelReader.getPixels(0,0, imageWidth, imageHeight, format, buffer, 0, imageWidth);

        Centroid[] centroids = new Centroid[k];
        RGBPoint[] imageBuffer = RGBPoint.dataToRGB(buffer);

        // initialize the centroids
        for(int i = 0; i < k; i++) {
            centroids[i] = Centroid.random(buffer);
        }

        // return after 100 iterations if centroids are still not set
        for(int i = 0; i < 100; i++) {

            // clear centroids' points
            for(Centroid c : centroids) {
                c.getPoints().clear();
            }

            // assign a point to to the closest centroid
            for(RGBPoint point : imageBuffer) {

                Centroid centroid = null;
                var min = Double.MAX_VALUE;

                for(Centroid c : centroids) {
                    double distance = RGBPoint.distance(point, c);

                    if(distance < min) {
                        min = distance;
                        centroid = c;
                    }
                }
                centroid.addPoint(point);
            }

            // recompute centroids
            boolean hasChanged = false;

            for(Centroid c : centroids) {
                if(c.recalculateAndCheckIfChanged()) {
                    hasChanged = true;
                }
            }

            // return if centroids did not changed
            if(!hasChanged) break;
        }

        // assign RGB of centroid to all of its points
        for(Centroid c : centroids) {
            c.merge();
        }
        // retrieve the raw data and update the image
        for(int i = 0; i < buffer.length; i++) {
            buffer[i] = RGBPoint.rgbToRawData(imageBuffer[i]);
        }

        pixelWriter.setPixels(0,0, imageWidth, imageHeight, format, buffer,0, imageWidth);
        return newImage;
    }
}
