package com.hollingsworth.schematic.common.block;

import com.hollingsworth.schematic.common.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CashRegisterEntity extends BlockEntity implements ITickable {
    public CashRegisterEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(CafeBlocks.CASH_REGISTER_ENTITY.get(), p_155229_, p_155230_);
    }
}
