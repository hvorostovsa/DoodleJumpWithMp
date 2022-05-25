package com.example.doodlejumpwithmp;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuTextField {
    private final static double TEXT_FIELD_WIDTH = 350;
    private final static double TEXT_FIELD_HEIGHT = 110;

    private double coordinateX;
    private double coordinateY;

    private String text;
    private final GraphicsContext gc;
    private boolean isSelected;

    static Image textFieldImage = new Image(
            MainUtils.getFilePathFromResources("Nice_TextField.png"),
            TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, false, false
    );
    static Image textFieldSelectedImage = new Image(
            MainUtils.getFilePathFromResources("Selected_Text_Field.png"),
            TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, false, false
    );

    public MenuTextField(GraphicsContext gc) {
        this.gc = gc;
    }

    public void createTextField(double x, double y, String string) {
        text = string;

        coordinateX = x;
        coordinateY = y;
        if (isSelected) gc.drawImage(textFieldSelectedImage, x, y);
        else gc.drawImage(textFieldImage, x, y);
        Font font = new Font("Times New Roman", 18);
        gc.setFont(font);
        gc.setFill(Color.DARKBLUE);
        gc.fillText(text, x + TEXT_FIELD_WIDTH / 2 - text.length() * 4, y + TEXT_FIELD_HEIGHT / 2 + 20);

    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public ImageView getBoundary() {
        ImageView iv;

        if (isSelected) iv = new ImageView(textFieldSelectedImage);
        else iv = new ImageView(textFieldImage);

        iv.setX(coordinateX);
        iv.setY(coordinateY);
        return iv;
    }

}
