package com.example.doodlejumpwithmp.model.platform;

import javafx.scene.image.Image;

public class ZeroJumpPlatform extends Platform {
    private boolean haveJumped = false;

    public ZeroJumpPlatform(Image platformImage) {
        super(platformImage);
    }

    @Override
    public boolean canJumpToPlatform() {
        return false;
    }

    @Override
    public boolean jumpFromPlatform() {
        if (!haveJumped) {
            moveOutFromTheScreen();
            haveJumped = true;
        }
        return false;
    }
}
