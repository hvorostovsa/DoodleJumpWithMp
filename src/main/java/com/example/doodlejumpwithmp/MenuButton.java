package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

public class MenuButton {
    private final static int MENU_BUTTON_WIDTH = 150;
    private final static int MENU_BUTTON_HEIGHT = 50;

    private double coordinateCenterX;
    private double coordinateCenterY;
    private final String buttonText;

//    private final double radiusX;
//    private final double radiusY;

    static Image menuButtonImage = new Image(
            Main.getFilePathFromResources("Menu_Button.png"),
            MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, false, false
    );

    private final GraphicsContext gc;

    public MenuButton(GraphicsContext gc, String text) {

        this.buttonText = text;
        this.gc = gc;
    }

    public void createOnPosition(double x, double y) {
        coordinateCenterX = x;
        coordinateCenterY = y;

        gc.drawImage(menuButtonImage, x, y);
        Font font = new Font("Times New Roman", 18);
        gc.setFont(font);
        gc.fillText(buttonText, coordinateCenterX - buttonText.length() * 4, coordinateCenterY);

    }

    public ImageView getBoundary() {
        ImageView iv = new ImageView(menuButtonImage);
        iv.setX(coordinateCenterX);
        iv.setY(coordinateCenterY);
        return iv;
    }
}
