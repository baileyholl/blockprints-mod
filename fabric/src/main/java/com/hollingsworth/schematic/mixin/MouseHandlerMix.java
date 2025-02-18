package com.hollingsworth.schematic.mixin;

import com.hollingsworth.schematic.client.ClientData;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMix {
    @Inject(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void blockprints$beforeMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, boolean bl, double d, double e, double f, int i, int j, int k ) {
        if(ClientData.mouseScrolled(yOffset)) ci.cancel();
    }
}
