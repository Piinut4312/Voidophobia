package net.piinut.voidophobia.block.crackedBedrock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.VuxProvider;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.util.data.DoubleValueMap;

import java.util.Random;

public abstract class AbstractCrackedBedrockBlock extends Block implements VuxProvider {

    protected DoubleValueMap<Item, Item, Float> itemConversionMap = new DoubleValueMap<>();

    public AbstractCrackedBedrockBlock(Settings settings) {
        super(settings);
    }

    protected void registerConvertibleItems(Item source, Item target, float chance){
        itemConversionMap.put(source, target, chance);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1, (double)pos.getZ() + random.nextDouble(), 0.1, 0.1, 0.1);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        ItemStack itemStack = player.getMainHandStack();
        Item item = itemStack.getItem();

        if(world.isClient()){
            if(itemConversionMap.containsKey(item)){
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        if(itemConversionMap.containsKey(item)){
            Pair<Item, Float> resultPair = itemConversionMap.get(item);
            if(world.getRandom().nextFloat() < resultPair.getRight()){
                ItemStack itemStack1 = new ItemStack(resultPair.getLeft(), 1);
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
