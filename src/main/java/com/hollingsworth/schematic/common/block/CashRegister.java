package com.hollingsworth.schematic.common.block;

import com.hollingsworth.schematic.common.util.ITickableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CashRegister extends Block implements ITickableBlock {
    public CashRegister(Properties p_49795_) {
        super(p_49795_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CashRegisterEntity(p_153215_, p_153216_);
    }
}
