package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BasicVuxductBlockEntity extends AbstractVuxductBlockEntity{

    public BasicVuxductBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_VUXDUCT, pos, state, 10000, 500);
    }

}
