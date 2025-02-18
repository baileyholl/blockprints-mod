package com.hollingsworth.schematic.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public record KeyEvent(KeyMapping key, int action) {

    boolean isDown() {
        return action != InputConstants.RELEASE;
    }

    boolean isRelease() {
        return action == InputConstants.RELEASE;
    }
}
