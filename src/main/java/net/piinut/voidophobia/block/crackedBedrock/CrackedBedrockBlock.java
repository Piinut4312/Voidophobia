package net.piinut.voidophobia.block.crackedBedrock;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.item.ModItems;

import java.util.Random;

public class CrackedBedrockBlock extends AbstractCrackedBedrockBlock{

    public CrackedBedrockBlock(Settings settings) {
        super(settings);
    }
    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {
        return 30.0;
    }

    @Override
    protected float getConversionChance(ItemStack itemStack){
        if(itemStack.isOf(ModItems.GODEL_CRYSTAL_SHARD)){
            return 0.75f;
        }
        if(itemStack.isOf(ModItems.REDSTONE_QUARTZ)){
            return 0.8f;
        }
        return 0.0f;
    }
}
