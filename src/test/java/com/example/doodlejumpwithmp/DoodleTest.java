package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoodleTest {

    @Test
    public void moveYTest() {
        Doodle doodle = new Doodle(null);
        doodle.setPosition(150, 150);

        Platform platform = new Platform(null);
        platform.setPosition(doodle.getX(), doodle.getY() + Doodle.HEIGHT);
        ArrayList<Platform> platforms = new ArrayList<>();
        platforms.add(platform);

        doodle.moveY(platforms);
        assertEquals(-11, doodle.getDiffY()); // if jumped;

        for (int i = 0; i < 10; i++) doodle.moveY(platforms);
        assertEquals(150, doodle.getX()); // x coordinate not changed
        assertTrue(doodle.getY() < 150); // doodle moved up after jump
    }

    @Test
    public void moveXTest() {
        Doodle doodle = new Doodle(null);

    }

    @Test
    public void intersectPlatformTest() {

    }
}
