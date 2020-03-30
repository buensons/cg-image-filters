package org.openjfx.filters;

import java.util.Map;
import java.util.TreeMap;

public class CustomFilter {

    private TreeMap<Double, Double> pointsMap;

    public CustomFilter(TreeMap<Double, Double> pointsMap) {
        this.pointsMap = new TreeMap<>();

        pointsMap.forEach((k,v) -> {
            this.pointsMap.put(k,v);
        });
    }

    public TreeMap<Double, Double> getPointsMap() {
        return pointsMap;
    }

    public void setPointsMap(TreeMap<Double, Double> pointsMap) {
        this.pointsMap = pointsMap;
    }
}
