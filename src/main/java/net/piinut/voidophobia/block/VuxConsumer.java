package net.piinut.voidophobia.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface VuxConsumer {

    //Vux stands for "Void Flux"
    //Any block that consumes vux must implement this interface

    int consumeVux(World world, BlockState state, BlockPos pos, Random random);
}
