package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

public class MenuButton {
    private double coordinateCenterX;
    private double coordinateCenterY;
    private final String buttonText;

    private final double radiusX;
    private final double radiusY;

    private Image buttonImage;
    private final GraphicsContext gc;

    public MenuButton(GraphicsContext gc, Image image, String text) {
        this.buttonImage = image;

        this.buttonText = text;

        this.radiusX = buttonImage.getWidth() / 2;
        this.radiusY = buttonImage.getHeight() / 2;

        this.gc = gc;
    }

    public void createOnPosition(double x, double y) {
        coordinateCenterX = x + radiusX;
        coordinateCenterY = y + radiusY;

        gc.drawImage(buttonImage, x, y);
        Font font = new Font("Shlapak Script", 24);
        gc.setFont(font);
        gc.fillText(buttonText, coordinateCenterX - buttonText.length() * 4, coordinateCenterY);

    }

    public Ellipse getBoundary() {
        return new Ellipse(coordinateCenterX, coordinateCenterY, radiusX, radiusY);
    }
}
