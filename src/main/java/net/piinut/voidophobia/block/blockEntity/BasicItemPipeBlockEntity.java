package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BasicItemPipeBlockEntity extends AbstractItemPipeBlockEntity{

    public static final int MAX_COOLDOWN = 2;
    public static final int BUFFER_SIZE = 5;
    public static final int BATCH_SIZE = 1;

    public BasicItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_ITEM_PIPE, pos, state, MAX_COOLDOWN, BUFFER_SIZE, BATCH_SIZE);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, BasicItemPipeBlockEntity blockEntity) {
        blockEntity.serverTick(world, blockPos, blockState);
    }
}
