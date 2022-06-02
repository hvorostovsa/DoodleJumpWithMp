package com.example.doodlejumpwithmp.model.platform;

import javafx.scene.image.Image;

public class OneJumpPlatform extends Platform {
    private boolean haveJumped = false;

    public OneJumpPlatform(Image platformImage) {
        super(platformImage);
    }

    @Override
    public boolean canJumpToPlatform() {
        return !haveJumped;
    }

    @Override
    public boolean jumpFromPlatform() {
        if (canJumpToPlatform()) {
            moveOutFromTheScreen();
            haveJumped = true;
            return true;
        }
        return false;
    }
}
