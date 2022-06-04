package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.model.Direction;
import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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

        for (int i = 0; i < 10; i++) doodle.moveY(platforms); // new frames calculated
        assertEquals(150, doodle.getX()); // coordinateX is not changed
        assertTrue(doodle.getY() < 150); // doodle moved up after jump
    }

    @Test
    public void moveXTest() {
        Doodle doodle = new Doodle(null);
        double lastX = 150, lastY = 150;
        doodle.setPosition(lastX, lastY);

        // move left
        doodle.moveX(Direction.LEFT);
        assertTrue(doodle.getX() < lastX); // after motion to left
        lastX = doodle.getX();

        // move right
        doodle.moveX(Direction.RIGHT);
        assertTrue(doodle.getX() > lastX); // after motion to right
        lastX = doodle.getX();

        assertEquals(lastY, doodle.getY()); // coordinateY is not changed
    }

    @Test
    public void intersectPlatformTest() {
        Doodle doodle = new Doodle(null);
        Platform platform = new Platform(null);
        doodle.setPosition(120, 300);
        platform.setPosition(doodle.getX() - 20, doodle.getY() + Doodle.HEIGHT - 5);

        assertTrue(doodle.intersectPlatform(platform));

        doodle.setPosition(20, 20);
        assertFalse(doodle.intersectPlatform(platform));
    }
}
