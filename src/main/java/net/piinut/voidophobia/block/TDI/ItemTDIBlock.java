package net.piinut.voidophobia.block.TDI;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.ItemTDIBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ItemTDIBlock extends AbstractTDIBlock {

    public ItemTDIBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemTDIBlockEntity(pos, state);
    }

}
