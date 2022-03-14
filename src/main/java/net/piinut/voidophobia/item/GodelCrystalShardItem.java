package net.piinut.voidophobia.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.VuxProvider;

import java.util.Random;

public class GodelCrystalShardItem extends Item {
    public GodelCrystalShardItem(Settings settings) {
        super(settings);
    }

    private static float convertChance(double vux){
        return (float) Math.min(1.0f, 0.3*Math.sqrt(vux));
    }

    private static boolean canConvert(double vux, Random random){
        return random.nextFloat() < convertChance(vux);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World world = context.getWorld();

        if(!world.isClient()){
            BlockPos blockPos = context.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            Random random = world.getRandom();
            if(blockState.getBlock() instanceof VuxProvider){
                double vux = ((VuxProvider) blockState.getBlock()).getVux(world, blockState, blockPos, context.getSide(), random);
                if(canConvert(vux, random)){
                    context.getPlayer().giveItemStack(new ItemStack(ModItems.ARTIFICIAL_BEDROCK_SCRAP));
                }else{
                    context.getPlayer().damage(DamageSource.MAGIC, 2.0f);
                }
                context.getStack().decrement(1);
                return ActionResult.SUCCESS;
            }
        }

        return super.useOnBlock(context);
    }
}
