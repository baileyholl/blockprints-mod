package com.hollingsworth.schematic.client;

import net.minecraft.nbt.CompoundTag;

public class CafeClientData {
    public int score;
    public int customersRemaining;
    public int cafeTime;
    public String smState;


    public CafeClientData(CompoundTag tag){
        score = tag.getInt("score");
        customersRemaining = tag.getInt("customersRemaining");
        cafeTime = tag.getInt("cafeTime");
        smState = tag.getString("smState");
    }

    public CafeClientData(int score, int customersRemaining, int cafeTime, String smState){
        this.score = score;
        this.customersRemaining = customersRemaining;
        this.cafeTime = cafeTime;
        this.smState = smState;
    }

    public CompoundTag toTag(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("score", score);
        tag.putInt("customersRemaining", customersRemaining);
        tag.putInt("cafeTime", cafeTime);
        tag.putString("smState", smState);
        return tag;
    }
}
