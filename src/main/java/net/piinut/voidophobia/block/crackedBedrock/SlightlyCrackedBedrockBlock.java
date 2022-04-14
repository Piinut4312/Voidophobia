package net.piinut.voidophobia.block.crackedBedrock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.crackedBedrock.AbstractCrackedBedrockBlock;
import net.piinut.voidophobia.item.ModItems;

import java.util.Random;

public class SlightlyCrackedBedrockBlock extends AbstractCrackedBedrockBlock {

    public SlightlyCrackedBedrockBlock(Settings settings) {
        super(settings);
        this.registerConvertibleItems(ModItems.GODEL_CRYSTAL_SHARD, ModItems.ARTIFICIAL_BEDROCK_SCRAP, 0.2f);
        this.registerConvertibleItems(ModItems.REDSTONE_QUARTZ, ModItems.RESONATING_QUARTZ, 0.4f);
    }

    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {

        int bottomY = world.getBottomY();
        if(pos.getY()-bottomY <= 4){
            return 5.0;
        }

        return 0;
    }
}
