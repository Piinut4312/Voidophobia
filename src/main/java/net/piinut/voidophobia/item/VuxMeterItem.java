package net.piinut.voidophobia.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractVuxductBlock;
import net.piinut.voidophobia.block.VuxProvider;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;

public class VuxMeterItem extends Item {

    public VuxMeterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        int vux = 0;
        World world = context.getWorld();
        if(world.isClient()){
            return ActionResult.SUCCESS;
        }
        BlockState blockState = world.getBlockState(context.getBlockPos());
        Block block = blockState.getBlock();
        if(block instanceof VuxProvider){
            vux = ((VuxProvider) block).getVux(world, blockState, context.getBlockPos(), context.getSide(), world.getRandom());
        }else if(block instanceof AbstractVuxductBlock){
            AbstractVuxductBlockEntity be = (AbstractVuxductBlockEntity) world.getBlockEntity(context.getBlockPos());
            vux = be.getVuxContaining();
        }

        if(context.getPlayer() != null){
            context.getPlayer().sendMessage(Text.of("Vux Level: " + vux), false);
        }

        return super.useOnBlock(context);
    }
}
