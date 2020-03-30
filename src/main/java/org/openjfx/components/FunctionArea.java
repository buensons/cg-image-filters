package org.openjfx.components;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import org.openjfx.components.Point;
import org.openjfx.filters.CustomFilter;

import java.util.*;

public class FunctionArea extends Rectangle {

    private Polyline line;
    private Map<Integer, Integer> function;
    private Map<String, CustomFilter> filters;
    private TreeMap<Double, Double> pointsMap;
    private ChoiceBox<Point> pointSelect;
    private Pane pane;
    private Circle previousPoint;
    private EventHandler<MouseEvent> circleOnMousePressedEventHandler;
    private EventHandler<MouseEvent> circleOnMouseDraggedEventHandler;

    public FunctionArea() {
        circleOnMousePressedEventHandler = t -> {
            previousPoint = (Circle)t.getSource();
            if(t.getButton().equals(MouseButton.SECONDARY)) {
                pane.getChildren().remove(previousPoint);
                pointsMap.remove(previousPoint.getCenterX());
                generateFunction();
                return;
            }

            if(pointsMap.remove(previousPoint.getCenterX()) == null) {
                System.out.println("No key");
            }
        };

        circleOnMouseDraggedEventHandler = t -> {
            previousPoint.setVisible(false);
        };
    }

    public void init(StackPane stackPane, ChoiceBox<Point> pointSelect) {

        this.pointSelect = pointSelect;
        function = new HashMap<>();
        filters = new HashMap<>();
        pointsMap = new TreeMap<>();
        line = new Polyline();
        pane = new Pane();

        initializeCustomFilters();
        resetFunction();

        pane.setPrefSize(256, 256);
        pane.setMinSize(256, 256);
        pane.setMaxSize(256,256);

        line.setStroke(Color.rgb(0xdc, 0x35, 0x45));

        pane.cursorProperty().setValue(Cursor.HAND);

        pane.setOnMouseClicked(e -> {

            double x = Math.max(Math.min(e.getX(), 255.0), 0);
            double y = Math.max(Math.min(e.getY(), 255.0), 0);

            pointsMap.put(x,y);
            pointSelect.getItems().add(new Point(x,y));

            generateFunction();
        });

        Polyline originalLine = new Polyline();
        originalLine.getPoints().addAll(this.getX(), this.getHeight() - 1, this.getWidth() - 1, this.getY());
        originalLine.setStroke(Color.rgb(80,80,80));

        stackPane.getChildren().addAll(originalLine, line, pane);

    }

    public Map<Integer, Integer> getFunction() {
        return function;
    }

    public void resetFunction() {
        function.clear();
        line.getPoints().clear();
        pointsMap.clear();
        pane.getChildren().clear();

        pointsMap.put(this.getX(), this.getY() + this.getHeight() - 1);
        pointsMap.put(this.getWidth() - 1, this.getY());

        for(int i = 0; i < 256; i++) {
            function.put(i, i);
        }

        pointSelect.getItems().clear();

        pointsMap.descendingKeySet().forEach(e -> {
            line.getPoints().add(e);
            line.getPoints().add(pointsMap.get(e));
            pointSelect.getItems().add(new Point(e, pointsMap.get(e)));
        });

        var dotX = new Circle(this.getX(), this.getY() + this.getHeight() - 1, 3, Color.rgb(255,255,255));
        var dotY = new Circle(this.getWidth() - 1, this.getY(), 3, Color.rgb(255,255,255));

        pane.getChildren().addAll(dotX, dotY);
    }

    public void removePoint(Point point) {
        pointsMap.remove(point.getX());
        pointSelect.getItems().remove(point);
        generateFunction();
    }

    public void movePoint(Point point, double x, double y) {
        pointsMap.remove(point.getX());

        pointsMap.put(x, 255.0 - y);

        pointSelect.getItems().remove(point);
        pointSelect.getItems().add(new Point(x, 255.0 - y));
        generateFunction();
    }

    private void generateFunction() {
        Map.Entry<Double, Double> previous = pointsMap.firstEntry();
        Map.Entry<Double, Double> current = pointsMap.higherEntry(previous.getKey());

        for(int i = 0; i < pointsMap.size() - 1; i++) {
            double rise = (255 - current.getValue()) - (255 - previous.getValue());
            double run = current.getKey() - previous.getKey();
            double a = rise / run;
            double b;

            if(a < 0) {
                b = a * previous.getKey() + (255 - previous.getValue());
            } else {
                b = (255 - current.getValue()) - (a * current.getKey());
            }

            for(int j = (int)Math.round(previous.getKey()); j <= (int)Math.round(current.getKey()); j++) {
                function.replace(j, (int) Math.round(a * j + b));
            }

            previous = current;
            current = pointsMap.higherEntry(current.getKey());
        }

        pane.getChildren().clear();
        pointSelect.getItems().clear();
        line.getPoints().clear();

        for(Double d: pointsMap.descendingKeySet()) {
            line.getPoints().addAll(d, pointsMap.get(d));
            pointSelect.getItems().add(new Point(d, 255.0 - pointsMap.get(d)));
            var dot = new Circle(d, pointsMap.get(d), 3, Color.rgb(255,255,255));

            if(d != 0.0 && d != 255.0) {
                dot.setOnMouseDragged(circleOnMouseDraggedEventHandler);
                dot.setOnMousePressed(circleOnMousePressedEventHandler);
                dot.setCursor(Cursor.CLOSED_HAND);
            }

            pane.getChildren().add(dot);
        }

    }

    public String createCustomFilter(String s) {
        if(!filters.containsKey(s)) {
            filters.put(s, new CustomFilter(pointsMap));
            return s;
        }

        return "";
    }

    public void loadCustomFilter(String s) {
        pointsMap.clear();
        filters.get(s).getPointsMap().forEach((k,v) -> {
            pointsMap.put(k,v);
        });
        generateFunction();
    }

    private void initializeCustomFilters() {

        TreeMap<Double, Double> map = new TreeMap<>();

        map.put(this.getX(), this.getHeight() - 1);
        map.put(this.getWidth() - 1, this.getY());

        filters.put("Null Transform", new CustomFilter(map));

        map.clear();
        map.put(this.getX(), this.getY());
        map.put(this.getWidth() - 1, this.getHeight() - 1);

        filters.put("Inverse", new CustomFilter(map));

        map.clear();
        map.put(this.getX(), this.getHeight() - 17);
        map.put(this.getWidth() - 1, this.getY());
        map.put(this.getWidth() - 17, this.getY());

        filters.put("Brightness Up", new CustomFilter(map));

        map.clear();
        map.put(this.getX(), this.getHeight() - 1);
        map.put(this.getWidth() - 1, this.getY() + 16);
        map.put(this.getX() + 16, this.getHeight() - 1);

        filters.put("Brightness Down", new CustomFilter(map));

        map.clear();
        map.put(this.getX(), this.getHeight() - 1);
        map.put(this.getX() + 16, this.getHeight() - 1);
        map.put(this.getWidth() - 1, this.getY());
        map.put(this.getWidth() - 17, this.getY());

        filters.put("Contrast Enhancement", new CustomFilter(map));

    }
}
