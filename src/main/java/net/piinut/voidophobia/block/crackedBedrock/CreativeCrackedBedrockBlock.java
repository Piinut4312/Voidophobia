package net.piinut.voidophobia.block.crackedBedrock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.item.ModItems;

import java.util.Random;

public class CreativeCrackedBedrockBlock extends AbstractCrackedBedrockBlock {
    public CreativeCrackedBedrockBlock(Settings settings) {
        super(settings);
        this.registerConvertibleItems(ModItems.GODEL_CRYSTAL_SHARD, ModItems.ARTIFICIAL_BEDROCK_SCRAP, 1.0f);
        this.registerConvertibleItems(ModItems.REDSTONE_QUARTZ, ModItems.RESONATING_QUARTZ, 1.0f);
    }

    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {
        return Float.MAX_VALUE-1;
    }
}
