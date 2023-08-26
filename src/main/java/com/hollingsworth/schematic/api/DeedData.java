package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.common.util.ItemstackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class DeedData extends ItemstackData {
    private UUID uuid;

    public DeedData(ItemStack stack){
        super(stack);
        var itemTag = getItemTag(stack);
        if(itemTag != null && itemTag.contains("uuid")){
            uuid = itemTag.getUUID("uuid");
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putUUID("uuid", uuid);
    }

    @Override
    public String getTagString() {
        return "deed_data";
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        writeItem();
    }
}
