package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuTextField {
    private final static int TEXT_FIELD_WIDTH = 350;
    private final static int TEXT_FIELD_HEIGHT = 110;

    private double coordinateX;
    private double coordinateY;

    private String text;
    private final GraphicsContext gc;

    static Image textFieldImage = new Image(
            Main.getFilePathFromResources("Nice_TextField.png"),
            TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, false, false
    );

    public MenuTextField(GraphicsContext gc) {
        this.gc = gc;
    }

    public void createTextField(double x, double y, String string) {
        text = string;

        coordinateX = x;
        coordinateY = y;

        gc.drawImage(textFieldImage, x, y);
        Font font = new Font("Times New Roman", 18);
        gc.setFont(font);
        gc.setFill(Color.DARKBLUE);
        gc.fillText(text, x + TEXT_FIELD_WIDTH / 2 - text.length() * 4, y + TEXT_FIELD_HEIGHT / 2 + 20);

    }

    public ImageView getBoundary() {
        ImageView iv = new ImageView(textFieldImage);
        iv.setX(coordinateX);
        iv.setY(coordinateY);
        return iv;
    }

}
