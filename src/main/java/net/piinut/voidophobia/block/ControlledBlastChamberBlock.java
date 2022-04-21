package net.piinut.voidophobia.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ControlledBlastChamberBlock extends BlockWithEntity implements VuxProvider {

    public ControlledBlastChamberBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {
        return 0;
    }
}
