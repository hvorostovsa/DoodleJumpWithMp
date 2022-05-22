package com.example.doodlejumpwithmp;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MenuTextField {
    private final static int TEXT_FIELD_WIDTH = 350;
    private final static int TEXT_FIELD_HEIGHT = 20;

    private double coordinateX;
    private double coordinateY;

    private String text;
    private final GraphicsContext gc;

    static Image textFieldImage = new Image(
            Main.getFilePathFromResources("Text_Field.png"),
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
        gc.fillText(text, x + 10, y + 15);

    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(coordinateX, coordinateY, textFieldImage.getWidth(), textFieldImage.getHeight());
    }

}
