package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.oauth.Login;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class LoginScreen extends BaseSchematicScreen {
    Runnable onLogin;
    Component message;

    public LoginScreen(Runnable onLogin) {
        this.onLogin = onLogin;
        message = Component.translatable("blockprints.login_desc");
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));

        addRenderableWidget(new GuiImageButton(bookLeft + 105, bookTop + 137, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            initLogin();
        }));
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_website.png"), bookLeft + 107, bookTop + 139, 0, 0, 11, 11, 11, 11);
        GuiUtils.drawOutlinedText(font, matrixStack, Component.translatable("blockprints.start_login").getVisualOrderText(), bookLeft + 128, bookTop + 141);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/container_loading_status.png"), 0, 0, 0, 0, 305, 209, 305, 209);
        graphics.drawWordWrap(font, message, 92, 77, 125, Constants.WHITE);

    }

    private void initLogin() {
        message = Component.translatable("blockprints.complete_login");
        try {
            Login.startOAuthFlow((token) -> {
                BlockprintsApi.getInstance().setToken(token);
                onLogin.run();
                Minecraft.getInstance().player.sendSystemMessage(Component.translatable("blockprints.login_success"));
            });
        }catch (IOException e){
            message = Component.literal("Encountered error: " + e.getMessage());
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        Login.abortAuth();
    }
}
