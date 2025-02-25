package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.ClientConstants;
import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.client.RenderStructureHandler;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlaceSchematicScreen extends Screen {
    public static PlaceTool placeTool = new PlaceTool();
    public static RotateTool rotateTool = new RotateTool();
    public static MoveHorizontalTool moveHorizontalTool = new MoveHorizontalTool();
    public static MoveVerticalTool moveVerticalTool = new MoveVerticalTool();
    public static ConfirmTool confirmTool = new ConfirmTool();
    public static PrintTool printTool = new PrintTool();
    public static DeleteTool deleteTool = new DeleteTool();
    public static MirrorTool mirrorTool = new MirrorTool();

    public final String holdToFocus = "blockprints.gui.toolmenu.focusKey";

    public boolean focused;
    private float yOffset;
    protected int selection;
    private boolean initialized;

    protected int w;
    protected int h;
    public List<ToolType> tools = new ArrayList<>();

    public PlaceSchematicScreen() {
        super(Component.literal("Tool Selection"));
        this.minecraft = Minecraft.getInstance();
        focused = false;
        yOffset = 0;
        selection = 0;
        initialized = false;
        h = 34;
        tools.add(new PlaceTool());
    }

    public void setupManipulationTools(){
        tools.clear();
        tools.add(moveHorizontalTool);
        tools.add(moveVerticalTool);
        tools.add(rotateTool);
//        tools.add(mirrorTool);
        tools.add(confirmTool);
        if(Minecraft.getInstance().player.isCreative() && ClientConstants.blockprintsServerside) {
            tools.add(printTool);
        }
        tools.add(deleteTool);
    }

    public void setSelectedElement(ToolType tool) {
        if (!tools.contains(tool))
            return;
        selection = tools.indexOf(tool);
    }

    public ToolType getSelectedElement(){
        return tools.get(selection);
    }

    public void cycle(int direction) {
        selection += (direction < 0) ? 1 : -1;
        selection = (selection + tools.size()) % tools.size();
    }

    private void draw(GuiGraphics graphics, float partialTicks) {
        w = Math.max(tools.size() * 50 + 32, 220);
        PoseStack matrixStack = graphics.pose();
        Window mainWindow = minecraft.getWindow();
        if (!initialized)
            init(minecraft, mainWindow.getGuiScaledWidth(), mainWindow.getGuiScaledHeight());

        int x = (mainWindow.getGuiScaledWidth() - w) / 2 + 14;
        int y = mainWindow.getGuiScaledHeight() - h - 34;

        matrixStack.pushPose();
        matrixStack.translate(0, -yOffset, focused ? 100 : 0);

        graphics.blit(SchematicMod.prefix("textures/gui/hud_background.png"), x - 15, y, 0, 0, w, h, 16, 16);

        float toolTipAlpha = yOffset / 10;
        List<Component> toolTip = tools.get(selection)
                .getDescription();

        if (toolTipAlpha > 0.25f) {
            graphics.blit(SchematicMod.prefix("textures/gui/hud_background.png"), x - 15, y + 16, 0, 0, w, h, 16, 16);
            if (!toolTip.isEmpty())
                GuiUtils.drawOutlinedText(minecraft.font, graphics, toolTip.get(0), x - 10, y + 38);
            if (toolTip.size() > 1)
                GuiUtils.drawOutlinedText(minecraft.font, graphics, toolTip.get(1), x - 10, y + 50);
        }

        if (tools.size() > 1) {
            String keyName = ClientData.TOOL_MENU.getTranslatedKeyMessage().getString();
            int width = minecraft.getWindow().getGuiScaledWidth();
            if (!focused)
                GuiUtils.drawCenteredOutlinedText(minecraft.font, graphics, Component.translatable(holdToFocus, keyName), width / 2, y - 10);
            else {
                GuiUtils.drawCenteredOutlinedText(minecraft.font, graphics, Component.translatable("blockprints.scroll"), width / 2, y - 10);
            }
        } else {
            GuiUtils.drawCenteredOutlinedText(minecraft.font, graphics, Component.translatable("blockprints.place_description"), width / 2, y - 10);
            x += 65;
        }


        for (int i = 0; i < tools.size(); i++) {
            matrixStack.pushPose();

            if (i == selection) {
                matrixStack.translate(0, -10, 0);

                GuiUtils.drawCenteredOutlinedText(minecraft.font, graphics, tools.get(i)
                        .getDisplayName(), x + i * 50 + 26, y + 28);
            }
            ResourceLocation icon = tools.get(i)
                    .getIcon();

            graphics.blit(icon,  x + i * 50 + 16, y + 11, 0, 0, 16, 16, 16, 16);

            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    public void update() {
        if (focused)
            yOffset += (10 - yOffset) * .1f;
        else
            yOffset *= .9f;
    }

    public boolean scroll(double delta) {
        if(focused){
            cycle((int) delta);
            return true;
        }else if(hasControlDown()){
            return tools.get(selection)
                    .handleMouseWheel(delta);
        }
        return false;
    }

    public void renderPassive(GuiGraphics graphics, float partialTicks) {
        draw(graphics, partialTicks);
    }

    @Override
    public void onClose() {
       // callback.accept(tools.get(selection));
    }

    @Override
    protected void init() {
        super.init();
        initialized = true;
    }

    public static class DeleteTool extends ToolType {

        public DeleteTool() {
            super(Component.translatable("blockprints.delete_tool"), SchematicMod.prefix("textures/gui/visualizer_trash.png"));
        }

        @Override
        public void onClick() {
            RenderStructureHandler.cancelRender();
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.delete_description"));
            return list;
        }
    }

    public static class MoveHorizontalTool extends ToolType {

        public MoveHorizontalTool() {
            super(Component.translatable("blockprints.move_horizontal_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_horizontal.png"));
        }

        @Override
        public boolean handleMouseWheel(double delta) {
            Direction direction = Minecraft.getInstance().player.getNearestViewDirection();
            BlockPos offset = new BlockPos((int) delta * direction.getStepX(), 0, (int) delta * direction.getStepZ());
            RenderStructureHandler.offsetAnchor(offset);
            return true;
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.move_horizontal_description"));
            return list;
        }
    }

    public static class MoveVerticalTool extends ToolType {

        public MoveVerticalTool() {
            super(Component.translatable("blockprints.move_vertical_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_vertical.png"));
        }

        @Override
        public boolean handleMouseWheel(double delta) {
            RenderStructureHandler.offsetAnchor(new BlockPos(0, (int) delta, 0));
            return true;
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.move_vertical_description"));
            return list;
        }
    }

    public static class MirrorTool extends ToolType {

        public MirrorTool() {
            super(Component.translatable("blockprints.mirror_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_mirror.png"));
        }

        @Override
        public boolean handleMouseWheel(double delta) {
            RenderStructureHandler.onFlip();
            return true;
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.mirror_description"));
            return list;
        }
    }


    public static class ConfirmTool extends ToolType{

        public ConfirmTool() {
            super(Component.translatable("blockprints.confirm_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_confirm.png"));
        }

        @Override
        public void onClick() {
            RenderStructureHandler.onConfirmHit();
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.confirm_description"));
            return list;
        }
    }

    public static class PrintTool extends ToolType{

        public PrintTool() {
            super(Component.translatable("blockprints.print_tool"), SchematicMod.prefix("textures/gui/visualizer_print.png"));
        }

        @Override
        public void onClick() {
            RenderStructureHandler.placeOnServer();
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.print_description"));
            return list;
        }
    }


    public static class RotateTool extends ToolType{

        public RotateTool() {
            super(Component.translatable("blockprints.rotate_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_rotate.png"));
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.rotate_description"));
            return list;
        }

        @Override
        public boolean handleMouseWheel(double delta) {
            RenderStructureHandler.onRotateHit(delta > 0);
            return true;
        }
    }

    public static class PlaceTool extends ToolType{


        public PlaceTool() {
            super(Component.translatable("blockprints.place_tool"), SchematicMod.prefix("textures/gui/visualizer_icon_place.png"));
        }

        @Override
        List<Component> getDescription() {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("blockprints.place_description"));
            return list;
        }

        @Override
        public void onClick() {
            RenderStructureHandler.setAnchor();
        }

        @Override
        public boolean handleMouseWheel(double delta) {
            RenderStructureHandler.onZoom(delta > 0);
            return true;
        }
    }



    public abstract static class ToolType{

        Component name;
        ResourceLocation icon;

        public ToolType(Component name, ResourceLocation icon){
            this.name = name;
            this.icon = icon;
        }

        public void onClick(){

        }


        public Component getDisplayName(){
            return name;
        }

        abstract List<Component> getDescription();

        public ResourceLocation getIcon(){
            return icon;
        }

        public boolean handleMouseWheel(double delta){
            return false;
        }
    }

}
