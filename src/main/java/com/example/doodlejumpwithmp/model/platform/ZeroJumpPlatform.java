package com.example.doodlejumpwithmp.model.platform;

import javafx.scene.image.Image;

public class ZeroJumpPlatform extends Platform {
    private boolean haveJumped = false;

    public ZeroJumpPlatform(Image platformImage) {
        super(platformImage);
    }

    @Override
    public boolean canJumpToPlatform() {
        if (!haveJumped) {
            haveJumped = true;
            moveOutFromTheScreen();
        }
        return false;
    }
}
