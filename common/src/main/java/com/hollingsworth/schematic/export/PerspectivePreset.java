package com.hollingsworth.schematic.export;

import net.minecraft.util.StringRepresentable;

/**
 * Camera pre-sets to easily change the orientation of a scene.
 */
public enum PerspectivePreset implements StringRepresentable {
    /**
     * An isometric camera where the northeast corner of blocks faces forward.
     */
    ISOMETRIC_NORTH_EAST("isometric-north-east", 225, 30, 0),
    /**
     * An isometric camera where the northwest corner of blocks faces forward.
     */
    ISOMETRIC_NORTH_WEST("isometric-north-west", 135, 30, 0),
    ISOMETRIC_SOUTH_EAST("isometric-south-east", 315, 30, 0),
    ISOMETRIC_SOUTH_WEST("isometric-south-west", 45, 30, 0);
//    /**
//     * An isometric camera where the northeast corner of blocks faces up
//     */
//    UP("up", 120, 0, 45);


    private final String serializedName;
    private final int yaw;
    private final int pitch;
    private final int roll;

    PerspectivePreset(String serializedName, int yaw, int pitch, int roll) {
        this.serializedName = serializedName;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public int yaw() {
        return yaw;
    }

    public int pitch() {
        return pitch;
    }

    public int roll() {
        return roll;
    }
}
