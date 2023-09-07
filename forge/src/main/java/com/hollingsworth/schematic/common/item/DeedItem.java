package com.hollingsworth.schematic.common.item;

import com.hollingsworth.schematic.common.util.ModItem;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;

public class DeedItem extends ModItem {

    public DeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
//        if(pLevel.isClientSide){
//            Minecraft.getInstance().setScreen(new CreateCafeScreen());
//        }
        if(pLevel.isClientSide){
            System.out.println("making scene");
            WrappedScene wrappedScene = new WrappedScene();
            Scene scene = new Scene(new GuidebookLevel(), new CameraSettings());
            wrappedScene.setScene(scene);
            wrappedScene.placeStructure();
            scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
            scene.centerScene();
            var data = wrappedScene.exportAsPng(1.0f);
            try {
                FileOutputStream fos = new FileOutputStream(Paths.get("./schematics/test.png").toFile());
                fos.write(data);
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
