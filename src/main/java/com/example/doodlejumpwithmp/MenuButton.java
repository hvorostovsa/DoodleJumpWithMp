package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

public class MenuButton {
    private final static int MENU_BUTTON_WIDTH = 150;
    private final static int MENU_BUTTON_HEIGHT = 100;

    private double coordinateCenterX;
    private double coordinateCenterY;
    private final String buttonText;

    private final double radiusX;
    private final double radiusY;

    static Image menuButtonImage = new Image(
            Main.getFilePathFromResources("MenuButton.png"),
            MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, false, false
    );

    private final GraphicsContext gc;

    public MenuButton(GraphicsContext gc, String text) {

        this.buttonText = text;

        this.radiusX = menuButtonImage.getWidth() / 2;
        this.radiusY = menuButtonImage.getHeight() / 2;

        this.gc = gc;
    }

    public void createOnPosition(double x, double y) {
        coordinateCenterX = x + radiusX;
        coordinateCenterY = y + radiusY;

        gc.drawImage(menuButtonImage, x, y);
        Font font = new Font("Shlapak Script", 24);
        gc.setFont(font);
        gc.fillText(buttonText, coordinateCenterX - buttonText.length() * 4, coordinateCenterY);

    }

    public Ellipse getBoundary() {
        return new Ellipse(coordinateCenterX, coordinateCenterY, radiusX, radiusY);
    }
}
