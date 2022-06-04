package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.model.platform.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlatformTest {
    @Test
    public void intersectPlatformTest() {
        Platform firstPlatform = new Platform(null);
        Platform secondPlatform = new Platform(null);
        firstPlatform.setPosition(120, 300);
        secondPlatform.setPosition(130, 290);
        assertTrue(firstPlatform.intersectPlatform(secondPlatform));

        firstPlatform.setPosition(0, 350);
        assertFalse(firstPlatform.intersectPlatform(secondPlatform));
    }
}
