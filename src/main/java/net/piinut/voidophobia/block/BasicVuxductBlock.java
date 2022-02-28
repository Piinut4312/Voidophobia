package net.piinut.voidophobia.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.BasicVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BasicVuxductBlock extends AbstractVuxductBlock{

    public BasicVuxductBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasicVuxductBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.BASIC_VUXDUCT, (BasicVuxductBlockEntity::tick));
    }
}
