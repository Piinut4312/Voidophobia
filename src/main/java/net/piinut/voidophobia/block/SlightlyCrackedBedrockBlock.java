package net.piinut.voidophobia.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class SlightlyCrackedBedrockBlock extends AbstractCrackedBedrockBlock{

    public SlightlyCrackedBedrockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {

        int bottomY = world.getBottomY();
        if(pos.getY()-bottomY <= 4){
            return 1.0;
        }

        return 0;
    }
}
