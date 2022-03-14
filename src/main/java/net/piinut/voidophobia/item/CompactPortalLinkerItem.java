package net.piinut.voidophobia.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractCompactPortalBlock;
import net.piinut.voidophobia.block.TDI.AbstractTDIBlock;
import net.piinut.voidophobia.block.blockEntity.AbstractCompactPortalBlockEntity;
import net.piinut.voidophobia.block.blockEntity.AbstractTDIBlockEntity;
import net.piinut.voidophobia.block.blockEntity.TDIDimensionType;

import java.util.Objects;

public class CompactPortalLinkerItem extends Item {

    private static final String[] POS = {"x", "y", "z"};
    private static final String DIM = "dimension";

    public CompactPortalLinkerItem(Settings settings) {
        super(settings);
    }

    private static void saveBlockPos(NbtCompound nbt, BlockPos pos){
        nbt.putInt(POS[0], pos.getX());
        nbt.putInt(POS[1], pos.getY());
        nbt.putInt(POS[2], pos.getZ());
    }

    private static void removeBlockPos(NbtCompound nbt){
        for(int i = 0; i < 3; i++){
            nbt.remove(POS[i]);
        }
    }

    private static BlockPos loadBlockPos(NbtCompound nbt){
        int x = nbt.getInt(POS[0]);
        int y = nbt.getInt(POS[1]);
        int z = nbt.getInt(POS[2]);
        return new BlockPos(x, y, z);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        NbtCompound nbt = context.getStack().getOrCreateNbt();
        World world = context.getWorld();
        if(world.isClient()){
            return ActionResult.SUCCESS;
        }
        if(state.getBlock() instanceof AbstractTDIBlock){
            if(nbt.contains("x")){
                BlockPos pos1 = loadBlockPos(nbt);
                if(pos == pos1){
                    removeBlockPos(nbt);
                    context.getPlayer().sendMessage(Text.of("Selection canceled!"), false);
                }else{
                    ServerWorld serverWorld = (ServerWorld)context.getWorld();
                    MinecraftServer minecraftServer = serverWorld.getServer();
                    ServerWorld serverWorld2;
                    if(Objects.equals(nbt.getString(DIM), TDIDimensionType.NETHER.toString())){
                        serverWorld2 = minecraftServer.getWorld(World.NETHER);
                    }else if(Objects.equals(nbt.getString(DIM), TDIDimensionType.END.toString())){
                        serverWorld2 = minecraftServer.getWorld(World.END);
                    }else{
                        serverWorld2 = minecraftServer.getWorld(World.OVERWORLD);
                    }
                    BlockPos firstPortalPos = loadBlockPos(nbt);
                    BlockState firstPortalState = serverWorld2.getBlockState(firstPortalPos);
                    if(firstPortalState.getBlock() == state.getBlock()){

                        AbstractTDIBlockEntity be1 = (AbstractTDIBlockEntity) serverWorld2.getBlockEntity(firstPortalPos);
                        AbstractTDIBlockEntity be2 = (AbstractTDIBlockEntity) serverWorld.getBlockEntity(pos);
                        TDIDimensionType dim1 = be1.getLocalDimension();
                        TDIDimensionType dim2 = be2.getLocalDimension();

                        if((dim1 == TDIDimensionType.OVERWORLD && dim1!= dim2) || (dim2 == TDIDimensionType.OVERWORLD && dim1 != dim2)){
                            be1.pairing(pos, dim2);
                            be2.pairing(firstPortalPos, dim1);
                            context.getPlayer().sendMessage(Text.of("Successfully paired!"), false);
                            removeBlockPos(nbt);
                        }else{
                            context.getPlayer().sendMessage(Text.of("Cannot pair two TDIs in the same dimension, or pair Nether and End TDI together!"), false);
                        }
                    }
                }
            }else{
                saveBlockPos(nbt, pos);
                if(world.getDimension().isPiglinSafe()){
                    nbt.putString(DIM, TDIDimensionType.NETHER.toString());
                }else if(world.getDimension().hasEnderDragonFight()){
                    nbt.putString(DIM, TDIDimensionType.END.toString());
                }else{
                    nbt.putString(DIM, TDIDimensionType.OVERWORLD.toString());
                }
                context.getPlayer().sendMessage(Text.of("Position saved!"), false);
            }
        }else if(state.getBlock() instanceof AbstractCompactPortalBlock){
            AbstractCompactPortalBlockEntity be = (AbstractCompactPortalBlockEntity) world.getBlockEntity(pos);
            be.switchMode();
            context.getPlayer().sendMessage(Text.of("Mode has been set to "+be.getMode().toString()), false);
        }
        return super.useOnBlock(context);
    }
}
