package com.example.paintproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class CADApplication extends Application {

    private Canvas canvas; // Canvas for drawing shapes
    private GraphicsContext gc; // GraphicsContext for drawing on the canvas

    // Enum to represent different shapes
    private enum Shape { LINE, RECTANGLE, CIRCLE, TRIANGLE }

    private Shape currentShape = Shape.LINE; // Current selected shape
    private Color currentColor = null; // Initial color is null for transparent fill
    private double[] vertexData; // Array to store vertex data for drawing shapes

    // Entry point of application
    public static void main(String[] args) {
        launch(args); // initializes the javafx application
    }

    // Initialization of the JavaFX application
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CAD Application");

        // creates a borderpane layout for the root of the scene and sets padding
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        canvas = new Canvas(800, 600); // Create canvas with initial size
        gc = canvas.getGraphicsContext2D(); // Get graphics context for drawing
        // gc is an instance variable of type GraphicsContext. It provides methods for drawing shapes, text, and images onto the canvas

        ColorPicker colorPicker = new ColorPicker(); // creates a Color picker for selecting drawing color and sets event handler
        colorPicker.setOnAction(e -> {
            currentColor = colorPicker.getValue(); // Update current color
            drawShape(); // Redraw the selected shape with the new color
        });

        // create a list for shape names
        List<String> shapeList = Arrays.asList("Line", "Rectangle", "Circle", "Triangle");
        ComboBox<String> shapeComboBox = new ComboBox<>(); // ComboBox for selecting drawing shape
        shapeComboBox.getItems().addAll(shapeList); // puts the shapes in the combobox
        shapeComboBox.setValue("Line"); // Default selection on the combobox

        // set event handler for the shape selection
        shapeComboBox.setOnAction(e -> {
            resetVertexData(); // Reset the vertex data and current color for the previous shape
            String selectedShape = shapeComboBox.getValue();
            currentShape = Shape.valueOf(selectedShape.toUpperCase()); // Update current shape
            promptForVertexData(); // Prompt user to enter vertex data for the selected shape
        });

        // sets the color picker at the top, canvas in the center, and shape combobox at the bottom of the borderpane
        root.setTop(colorPicker);
        root.setCenter(canvas);
        root.setBottom(shapeComboBox);

        // create a new scene
        primaryStage.setScene(new Scene(root, 800, 600)); // Set up the main scene
        primaryStage.show(); // Display the main window
    }

    // Event handler for mouse press
    /*private void handleMousePressed(MouseEvent event) {
        // No need to handle mouse press for drawing shapes in this implementation
    }*/

    // Event handler for mouse drag
    /*private void handleMouseDragged(MouseEvent event) {
        // No need to handle mouse drag for drawing shapes in this implementation
    }*/

    // Event handler for mouse release
    /*private void handleMouseReleased(MouseEvent event) {
        // No need to handle mouse release for drawing shapes in this implementation
    }*/

    // Method to prompt user for vertex data
    private void promptForVertexData() {
        TextInputDialog dialog = new TextInputDialog();//creates a dialog box for entering vertex data
        dialog.setHeaderText("Enter vertex data separated by commas:");

        // Show the dialog and wait for the user's input
        dialog.showAndWait().ifPresent(input -> {
            String[] data = input.split(",");
            try {
                // parsing the entered vertex data
                vertexData = Arrays.stream(data).mapToDouble(Double::parseDouble).toArray();
                drawShape(); // Draw the selected shape based on the entered vertex data
            } catch (NumberFormatException e) {
                // Handle invalid input (non-numeric values)
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Please enter valid numeric values separated by commas.");
                alert.showAndWait();
            }
        });
    }

    // Method to draw the selected shape
    private void drawShape() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas

        gc.setStroke(Color.BLACK); // Set stroke color to black

        // Check if a color is selected
        if (currentColor != null) {
            gc.setFill(currentColor); // Set fill color to the selected color
        } else {
            // If no color is selected, use a transparent fill
            gc.setFill(Color.TRANSPARENT);
        }

        // draw the selected shape based on the current shape and vertex data
        switch (currentShape) {
            case LINE:
                gc.setStroke(currentColor); // Set stroke color for the line
                gc.strokeLine(vertexData[0], vertexData[1], vertexData[2], vertexData[3]);
                break;
            case RECTANGLE:
                gc.fillRect(vertexData[0], vertexData[1], vertexData[2], vertexData[3]);
                gc.strokeRect(vertexData[0], vertexData[1], vertexData[2], vertexData[3]); // Draw stroke for rectangle
                break;
            case CIRCLE:
                double radius = vertexData[2];
                gc.fillOval(vertexData[0] - radius, canvas.getHeight() - (vertexData[1] + radius),
                        2 * radius, 2 * radius);
                gc.strokeOval(vertexData[0] - radius, canvas.getHeight() - (vertexData[1] + radius),
                        2 * radius, 2 * radius); // Draw stroke for circle
                break;
            case TRIANGLE:
                double[] xPoints = Arrays.copyOfRange(vertexData, 0, vertexData.length / 2);
                double[] yPoints = Arrays.copyOfRange(vertexData, vertexData.length / 2, vertexData.length);
                gc.fillPolygon(xPoints, Arrays.stream(yPoints).map(y -> canvas.getHeight() - y).toArray(), xPoints.length);
                gc.strokePolygon(xPoints, Arrays.stream(yPoints).map(y -> canvas.getHeight() - y).toArray(), xPoints.length); // Draw stroke for triangle
                break;
        }
    }

    // Method to reset vertex data and current color when a new shape is selected
    private void resetVertexData() {
        vertexData = null;
        currentColor = null;
    }
}
