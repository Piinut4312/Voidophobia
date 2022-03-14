package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ItemTDIBlockEntity extends AbstractTDIBlockEntity{

    public ItemTDIBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_TDI, pos, state);
    }

}
