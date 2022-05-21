package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;

public class OneJumpPlatform extends Platform {
    private boolean haveJumped = false;

    public OneJumpPlatform(Image platformImage) {
        super(platformImage);
    }

    @Override
    public boolean canJumpToPlatform() {
        if (haveJumped) {
            return false;
        }
        moveOutFromTheScreen();
        haveJumped = true;
        return true;
    }
}
