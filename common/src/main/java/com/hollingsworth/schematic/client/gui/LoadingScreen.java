package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.ApiError;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoadingScreen<T> extends BaseSchematicScreen {
    int ticksRunning;
    boolean backAdded = false;
    boolean addHomeButton = false;
    int anim;
    Supplier<ApiResponse<T>> future;
    Consumer<T> onSuccess;
    String error = null;
    public boolean responseHandled = false;
    Response<T> response;
    public Screen previousScreen = null;

    CompletableFuture<ApiResponse<T>> completableFuture;

    public LoadingScreen(Supplier<ApiResponse<T>> future, Consumer<T> onSuccess, Screen previousScreen) {
        this(future, onSuccess);
        this.previousScreen = previousScreen;
    }

    public LoadingScreen(Supplier<ApiResponse<T>> future, Consumer<T> onSuccess, Screen previousScreen, int ticksRunning) {
        this(future, onSuccess, previousScreen);
        this.ticksRunning = ticksRunning;
    }


    public LoadingScreen(Supplier<ApiResponse<T>> future, Consumer<T> onSuccess) {
        super();
        this.future = future;
        this.onSuccess = onSuccess;
        this.completableFuture = CompletableFuture.supplyAsync(future, Util.backgroundExecutor()).whenCompleteAsync((result, err) -> response = new Response<>(result, err), Minecraft.getInstance());
    }

    @Override
    public void init() {
        super.init();
    }

    public void handleResponse() {
        if (response == null) {
            return;
        }
        responseHandled = true;
        var apiResponse = response.response();

        // If the completable future failed or an unknown error was thrown
        if(response.throwable != null){
            // If an api error is abruptly thrown, override the api response
            if(response.throwable instanceof CompletionException completionException
                    && completionException.getCause() instanceof ApiError apiError) {
                apiResponse = apiError.toApiResponse();
            }else {
                response.throwable.printStackTrace();
                error = Component.translatable("blockprints.unexpected_error", response.throwable.getMessage()).getString();
                addHomeButton();
                return;
            }
        }

        // Invalid api response
        if(apiResponse == null || (apiResponse.response == null && apiResponse.error == null)){
            Constants.LOG.error("Empty response with no error.");
            error = Component.translatable("blockprints.unexpected_error").getString();
            addHomeButton();
            return;
        }

        if(apiResponse.error != null){
            error = apiResponse.error;
            Constants.LOG.error("Error: " + apiResponse.error);
            addHomeButton();
        }else {
            onSuccess.accept(apiResponse.response);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ticksRunning++;
        anim++;
        if (anim > 50) {
            anim = 0;
        }
        if (response != null && !responseHandled) {
            handleResponse();
        }
        if ((ticksRunning == 20 * 5 || error != null) && !backAdded) {
            addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
                Minecraft.getInstance().setScreen(this.previousScreen != null ? previousScreen : new HomeScreen());
            }));
            backAdded = true;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if(this.completableFuture != null && !this.completableFuture.isDone() && !this.completableFuture.isCancelled()) {
            this.completableFuture.cancel(true);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return canCancel();
    }

    public boolean canCancel() {
        return ticksRunning > 20 * 5 || error != null;
    }

    public void addHomeButton() {
        addRenderableWidget(new GuiImageButton(bookLeft + 105, bookTop + 137, 95, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));
        addHomeButton = true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        if (addHomeButton) {
            graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_home.png"), bookLeft + 108, bookTop + 140, 0, 0, 9, 8, 9, 8);
            GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.home").getVisualOrderText(), bookLeft + 128, bookTop + 141);
        }
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container_loading_status.png"), 0, 0, 0, 0, 305, 209, 305, 209);
        if (error != null) {
            graphics.drawWordWrap(font, Component.literal(error), 92, 77, 125, Constants.WHITE);
            return;
        }
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/animation_logo_loading.png"), 144, 91, 0, 26 * (anim / 2), 14, 26, 14, 650);
        String dots = "";
        if (anim < 13) {
            dots = "";
        } else if (anim < 26) {
            dots = ".";
        } else if (anim < 39) {
            dots = "..";
        } else {
            dots = "...";
        }
        Component component = Component.translatable("blockprints.loading");
        Component compWithDots = Component.literal(component.getString() + dots);
        int loadingX = 89 + 126 / 2;
        int centered = loadingX - font.width(component) / 2;

        graphics.drawString(font, compWithDots, centered, 120, Constants.WHITE, false);
    }

    protected record Response<T>(ApiResponse<T> response, @Nullable Throwable throwable) {
    }
}
