package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuButton {
    private final static int MENU_BUTTON_WIDTH = 180;
    private final static int MENU_BUTTON_HEIGHT = 120;

    private double coordinaterX;
    private double coordinateY;
    private final String buttonText;

//    private final double radiusX;
//    private final double radiusY;

    static Image menuButtonImage = new Image(
            Main.getFilePathFromResources("Button_Mouth.png"),
            MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, false, false
    );

    private final GraphicsContext gc;

    public MenuButton(GraphicsContext gc, String text) {

        this.buttonText = text;
        this.gc = gc;
    }

    public void createOnPosition(double x, double y) {
        coordinaterX = x;
        coordinateY = y;

        gc.drawImage(menuButtonImage, x, y);
        Font font = new Font("Times New Roman", 18);
        gc.setFont(font);
        gc.setFill(Color.WHITE);
        gc.fillText(buttonText, (x + MENU_BUTTON_WIDTH / 2) - buttonText.length() * 4, y + MENU_BUTTON_HEIGHT / 2 + 10);
    }

    public ImageView getBoundary() {
        ImageView iv = new ImageView(menuButtonImage);
        iv.setX(coordinaterX);
        iv.setY(coordinateY);
        return iv;
    }
}
