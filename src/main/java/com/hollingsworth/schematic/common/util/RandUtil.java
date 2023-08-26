package com.hollingsworth.schematic.common.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandUtil {
    public static Random r = new Random();

    public static double inRange(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    // Generates a random number between min and max, inclusive
    public static int inclusiveRange(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
