package net.piinut.voidophobia.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public interface VuxProvider {

    //Vux stands for "Void Flux"
    //Any block that produces vux must implement this interface

    double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random);

    void handleVuxConsumption(World world, BlockState state, BlockPos pos, double input);
}
