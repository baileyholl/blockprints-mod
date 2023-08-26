package com.hollingsworth.schematic.common.network;

import net.minecraft.network.FriendlyByteBuf;

public class EditCafeServer implements Message{

    public EditCafeServer(FriendlyByteBuf buf) {
        this.decode(buf);
    }


    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    @Override
    public void decode(FriendlyByteBuf buf) {

    }
}
