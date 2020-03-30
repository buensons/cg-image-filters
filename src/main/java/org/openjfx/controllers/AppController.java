package org.openjfx.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.openjfx.App;
import org.openjfx.components.FunctionArea;
import org.openjfx.components.Point;
import org.openjfx.filters.ConvolutionFilters;
import org.openjfx.filters.FunctionFilters;
import org.openjfx.filters.DitheringFilters;
import org.openjfx.filters.KMeans;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AppController {
    @FXML public ImageView originalImageView;
    @FXML public ImageView filteredImageView;
    @FXML public Button uploadImageBtn;
    @FXML public Button inverseBtn;
    @FXML public Button brightnessUpBtn;
    @FXML public Button brightnessDownBtn;
    @FXML public Button gammaBtn;
    @FXML public Button contrastBtn;
    @FXML public TextField gammaText;
    @FXML public Button resetImageBtn;
    @FXML public FunctionArea functionRectangle;
    @FXML public StackPane stackPane;
    @FXML public Button applyCustomBtn;
    @FXML public Button resetFunctionBtn;
    @FXML public ChoiceBox<Point> pointSelect;
    @FXML public Button removeButton;
    @FXML public Button moveBtn;
    @FXML public TextField xText;
    @FXML public TextField yText;
    @FXML public ChoiceBox<String> filterSelect;
    @FXML public Button blurBtn;
    @FXML public Button gaussBtn;
    @FXML public Button embossBtn;
    @FXML public Button edgeBtn;
    @FXML public Button sharpenBtn;
    @FXML public Button saveImageBtn;
    @FXML public TextField customFilterText;
    @FXML public Button saveFilterBtn;
    @FXML public Button medianBtn;
    @FXML public Button greyScaleBtn;
    @FXML public Button averageDitheringBtn;
    @FXML public TextField averageDitheringText;
    @FXML public Button kMeansBtn;
    @FXML public TextField kMeansText;
    @FXML public Button YCbCrBtn;
    @FXML public TextField YcbCrText;

    private ConvolutionFilters convolutionFilters;
    private FunctionFilters functionFilters;
    private DitheringFilters ditheringFilters;
    private KMeans kMeans;

    private Image nullTransform;
    private FileChooser fileChooser;
    private PixelReader reader;
    private WritableImage newImage;

    public AppController() {
        fileChooser = new FileChooser();
    }

    @FXML
    public void initialize() {

        nullTransform = new Image("/a.jpeg");
        reader = nullTransform.getPixelReader();

//        newImage = new WritableImage(reader, 0, 0, 500, 300);
//        nullTransform = newImage;

        originalImageView.setImage(nullTransform);
        filteredImageView.setImage(nullTransform);
        functionFilters = new FunctionFilters(nullTransform);
        convolutionFilters = new ConvolutionFilters(nullTransform);
        ditheringFilters = new DitheringFilters(nullTransform);
        kMeans = new KMeans(nullTransform);

        functionRectangle.init(stackPane, pointSelect);

        uploadImageBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(App.getMainStage());
            if(file != null) {
                nullTransform = new Image(file.toURI().toString());
                PixelReader reader = nullTransform.getPixelReader();
//                WritableImage newImage = new WritableImage(reader, 0, 0, 500, 300);
//                nullTransform = newImage;
                originalImageView.setImage(nullTransform);
                filteredImageView.setImage(nullTransform);

                functionFilters = new FunctionFilters(nullTransform);
                convolutionFilters = new ConvolutionFilters(nullTransform);
                ditheringFilters = new DitheringFilters(nullTransform);
                kMeans = new KMeans(nullTransform);
            }
        });

        inverseBtn.setOnAction(e -> {
            filteredImageView.setImage(functionFilters.inversionFilter(filteredImageView.getImage()));
        });

        brightnessUpBtn.setOnAction(e -> {
            filteredImageView.setImage(functionFilters.brightnessCorrectionFilter(filteredImageView.getImage(), 16));
        });

        brightnessDownBtn.setOnAction(e -> {
            filteredImageView.setImage(functionFilters.brightnessCorrectionFilter(filteredImageView.getImage(), -16));
        });

        gammaBtn.setOnAction(e -> {
            try {
                double coefficient = Double.parseDouble(gammaText.getText());
                filteredImageView.setImage(functionFilters.gammaCorrectionFilter(filteredImageView.getImage(), coefficient));
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        });

        contrastBtn.setOnAction(e -> {
            filteredImageView.setImage(functionFilters.contrastEnhancementFilter(filteredImageView.getImage(), 1.1));
        });

        resetImageBtn.setOnAction(e -> {
            filteredImageView.setImage(nullTransform);
        });

        applyCustomBtn.setOnAction(e -> {
            filteredImageView.setImage(functionFilters.customFilter(filteredImageView.getImage(), functionRectangle.getFunction()));
        });

        blurBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.applyFilter(filteredImageView.getImage(), new int []{1,1,1,1,1,1,1,1,1}, 3, 0));
        });

        gaussBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.applyFilter(filteredImageView.getImage(), new int []{0,1,0,1,4,1,0,1,0}, 3, 0));
        });

        embossBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.applyFilter(filteredImageView.getImage(), new int []{-1,-1,-1,0,1,0,1,1,1}, 3, 0));
        });

        edgeBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.applyFilter(filteredImageView.getImage(), new int []{1,0,-1,1,0,-1,1,0,-1}, 3, 90));
        });

        sharpenBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.applyFilter(filteredImageView.getImage(), new int []{0,-1,0,-1,5,-1,0,-1,-0}, 3, 0));
        });

        resetFunctionBtn.setOnAction(e -> {
            functionRectangle.resetFunction();
            filterSelect.setValue("Null Transform");
        });

        removeButton.setOnAction(e -> {
            functionRectangle.removePoint(pointSelect.getValue());
        });

        moveBtn.setOnAction(e -> {
            double x = Double.parseDouble(xText.getText());
            double y = Double.parseDouble(yText.getText());
            functionRectangle.movePoint(pointSelect.getValue(), x, y);
        });

        filterSelect.getItems().addAll(Arrays.asList(
                "Null Transform", "Inverse", "Brightness Up", "Brightness Down", "Contrast Enhancement"
        ));

        filterSelect.setValue("Null Transform");

        filterSelect.getSelectionModel().selectedItemProperty().addListener((e, old, selected) -> {
            functionRectangle.loadCustomFilter(selected);
        });

        saveImageBtn.setOnAction(e -> {
            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("images", Arrays.asList("*.png", "*.jpg", "*.jpeg", "*.svg"));
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(App.getMainStage());

            if(file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(filteredImageView.getImage(), null), "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        saveFilterBtn.setOnAction(e -> {
            if(customFilterText.getText() == null || customFilterText.getText().isEmpty()
                    || filterSelect.getItems().contains(customFilterText.getText())) {

                return;
            }
            String s = functionRectangle.createCustomFilter(customFilterText.getText());
            if(s.contentEquals("")) {
                return;
            }
            filterSelect.getItems().add(s);
            customFilterText.clear();
            filterSelect.setValue(s);
        });

        medianBtn.setOnAction(e -> {
            filteredImageView.setImage(convolutionFilters.medianFilter(filteredImageView.getImage(), 3));
        });

        greyScaleBtn.setOnAction(e -> {
            filteredImageView.setImage(ditheringFilters.convertToGreyScale(filteredImageView.getImage()));
        });

        averageDitheringBtn.setOnAction(e -> {
            try {
                int k = Integer.parseInt(averageDitheringText.getText());
                filteredImageView.setImage(ditheringFilters.averageDithering(filteredImageView.getImage(), k));
            } catch(NumberFormatException ex) {
               ex.printStackTrace();
            }
        });

        kMeansBtn.setOnAction(e -> {
            try {
                int k = Integer.parseInt(kMeansText.getText());
                filteredImageView.setImage(kMeans.compute(filteredImageView.getImage(), k));
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        });

        YCbCrBtn.setOnAction(e -> {
            try {
                int k = Integer.parseInt(YcbCrText.getText());
                filteredImageView.setImage(ditheringFilters.averageDitheringYCbCr(filteredImageView.getImage(), k));
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        });

        removeButton.setDisable(true);
        moveBtn.setDisable(true);
        pointSelect.setDisable(true);
    }
}
