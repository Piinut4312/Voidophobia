package net.piinut.voidophobia.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.ModBlocks;

import java.util.Random;

public class CorruptionCatalystItem extends Item {

    public CorruptionCatalystItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        boolean success = false;
        Random random = world.getRandom();
        int bedrockScraps = 0;
        if(blockState.isOf(Blocks.BEDROCK)){
            world.setBlockState(blockPos, ModBlocks.SLIGHTLY_CRACKED_BEDROCK.getDefaultState());
            bedrockScraps = random.nextInt(1, 2);
            success = true;
        }else if(blockState.isOf(ModBlocks.SLIGHTLY_CRACKED_BEDROCK)){
            world.setBlockState(blockPos, ModBlocks.CRACKED_BEDROCK.getDefaultState());
            bedrockScraps = random.nextInt(1, 3);
            success = true;
        }else if(blockState.isOf(ModBlocks.CRACKED_BEDROCK)){
            world.breakBlock(blockPos, false);
            bedrockScraps = random.nextInt(3, 8);
            success = true;
        }
        if(success){
            if(!world.isClient()){
                world.playSound(null, blockPos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                context.getPlayer().giveItemStack(new ItemStack(ModItems.ARTIFICIAL_BEDROCK_SCRAP, bedrockScraps));
                context.getStack().decrement(1);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }
}
