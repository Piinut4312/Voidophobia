package net.piinut.voidophobia.block.crackedBedrock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.VuxProvider;
import net.piinut.voidophobia.item.ModItems;

import java.util.Random;

public abstract class AbstractCrackedBedrockBlock extends Block implements VuxProvider {

    public AbstractCrackedBedrockBlock(Settings settings) {
        super(settings);
    }

    public static boolean canConvert(ItemStack itemStack){
        return itemStack.isOf(ModItems.GODEL_CRYSTAL_SHARD) || itemStack.isOf(ModItems.REDSTONE_QUARTZ);
    }

    public static Item conversionResult(ItemStack itemStack){
        if(itemStack.isOf(ModItems.GODEL_CRYSTAL_SHARD)){
            return ModItems.ARTIFICIAL_BEDROCK_SCRAP;
        }
        if(itemStack.isOf(ModItems.REDSTONE_QUARTZ)){
            return ModItems.RESONATING_QUARTZ;
        }
        return Items.AIR;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1, (double)pos.getZ() + random.nextDouble(), 0.1, 0.1, 0.1);
        }
    }

    protected abstract float getConversionChance(ItemStack itemStack);

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        ItemStack itemStack = player.getMainHandStack();

        if(world.isClient()){
            if(canConvert(itemStack)){
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        if(canConvert(itemStack)){
            if(world.getRandom().nextFloat() < getConversionChance(itemStack)){
                ItemStack itemStack1 = new ItemStack(conversionResult(itemStack), 1);
                if(!player.giveItemStack(itemStack1)){
                    player.dropItem(itemStack1.getItem());
                }
            }else{
                player.damage(DamageSource.MAGIC, 2.0f);
            }
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }


}
