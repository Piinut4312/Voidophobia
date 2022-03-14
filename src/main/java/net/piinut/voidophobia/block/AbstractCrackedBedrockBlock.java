package net.piinut.voidophobia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.item.ModItems;

import java.util.Random;

public abstract class AbstractCrackedBedrockBlock extends Block implements VuxProvider{

    public AbstractCrackedBedrockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if(world.isClient()){
            return ActionResult.SUCCESS;
        }

        ItemStack itemStack = player.getMainHandStack();
        if(itemStack.isOf(Items.QUARTZ)){
            Random random = world.getRandom();
            double vux = getVux(world, state, pos, hit.getSide(), random);
            if(random.nextFloat() < Math.min(1.0f, 0.4*Math.sqrt(vux))){
                player.giveItemStack(new ItemStack(ModItems.RESONATING_QUARTZ));
            }else{
                player.damage(DamageSource.MAGIC, 2.0f);
            }
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }
}
