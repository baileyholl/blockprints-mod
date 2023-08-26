package com.hollingsworth.schematic.common.item;

import com.hollingsworth.schematic.api.DeedData;
import com.hollingsworth.schematic.common.util.ModItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeedItem extends ModItem {

    public DeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var deedData = new DeedData(pPlayer.getItemInHand(pUsedHand));
//        if(pLevel instanceof ServerLevel serverLevel && deedData.getUuid() == null){
//            deedData.setUuid(UUID.randomUUID());
//            CafeSavedData.from(serverLevel).addCafe(new Cafe(deedData.getUuid()));
//        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        var deedData = new DeedData(stack);
        tooltip2.add(Component.literal("UUID " +  deedData.getUuid()));
    }
}
