package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.common.util.GuiEntityInfoHUD;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SchematicMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class CafeRenders {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
//        event.registerBlockEntityRenderer(CafeBlocks.MANAGEMENT_DESK_ENTITY.get(), DeskRenderer::new);
//        event.registerBlockEntityRenderer(CafeBlocks.HOUSE_ENTITY.get(), BuildingRenderer::new);
//        event.registerBlockEntityRenderer(CafeBlocks.DISPLAY_ENTITY.get(), DisplayCaseRenderer::new);
//        event.registerBlockEntityRenderer(CafeBlocks.PLATE_ENTITY.get(), PlateRenderer::new);
//        event.registerBlockEntityRenderer(CafeBlocks.FARM_ENTITY.get(), BuildingRenderer::new);
//
////
//        event.registerEntityRenderer(CafeEntities.SEAT.get(), SeatRender::new);
//        event.registerEntityRenderer(CafeEntities.VILLAGER.get(), VillRenderer::new);
    }
    @SubscribeEvent
    public static void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "tooltip", GuiEntityInfoHUD.OVERLAY);
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "cafe_hud", CafeHUD.OVERLAY);
    }
    public static MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));


}
