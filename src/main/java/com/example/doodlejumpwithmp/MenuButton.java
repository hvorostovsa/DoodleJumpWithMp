package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuButton {
    private final double width;
    private final double height;

    private double coordinateX;
    private double coordinateY;
    private String buttonText;

    private Image menuButtonImage;

    private final GraphicsContext gc;

    public MenuButton(GraphicsContext gc, double width) {
        this.width = width;
        this.height = width / 1.5;

        this.menuButtonImage = new Image(
                Main.getFilePathFromResources("Button_Mouth.png"),
                width, height, false, false
        );

        this.gc = gc;
    }

    public void createOnPosition(double x, double y, String text) {

        buttonText = text;
        coordinateX = x;
        coordinateY = y;

        gc.drawImage(menuButtonImage, x, y);
        Font font = new Font("Times New Roman", 18);
        gc.setFont(font);
        gc.setFill(Color.WHITE);
        gc.fillText(buttonText, (x + width / 2) - buttonText.length() * 4, y + height / 2 + 10);
    }

    public ImageView getBoundary() {
        ImageView iv = new ImageView(menuButtonImage);
        iv.setX(coordinateX);
        iv.setY(coordinateY);
        return iv;
    }
}
