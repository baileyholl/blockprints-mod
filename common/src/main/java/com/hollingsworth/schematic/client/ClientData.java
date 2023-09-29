package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;

public class ClientData {
    public static boolean showBoundary;
    public static BlockPos firstTarget;
    public static BlockPos secondTarget;
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);

    public static void openMenu(){
        Minecraft.getInstance().setScreen(new HomeScreen());
    }
}
