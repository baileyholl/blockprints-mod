package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;

public class BaseSchematicScreen extends ModScreen {
    public static ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/background.png");

    public BaseSchematicScreen() {
        super(305, 209);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookRight - 23, bookBottom - 23, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_discord.png"), b -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.gg/yT84NQdg6A"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("blockprints.discord")));
        addRenderableWidget(new GuiImageButton(bookRight - 39, bookBottom - 23, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_website.png"), b -> {

        }).withTooltip(Component.translatable("blockprints.website")));
    }


    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof NestedRenderables nestedRenderables) {
            for (AbstractWidget renderable : nestedRenderables.getExtras()) {
                super.addRenderableWidget(renderable);
            }
        }
        return super.addRenderableWidget(widget);
    }

    @Override
    protected void removeWidget(GuiEventListener $$0) {
        if ($$0 instanceof NestedRenderables nestedRenderables) {
            for (AbstractWidget renderable : nestedRenderables.getExtras()) {
                super.removeWidget(renderable);
            }
        }
        super.removeWidget($$0);
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }
}
