package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class CompactNetherPortalBlockEntity extends AbstractCompactPortalBlockEntity{

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public CompactNetherPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPACT_NETHER_PORTAL, pos, state);
        this.mode = IOMode.INPUT;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.items);
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.items);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }

        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return getMode() == IOMode.INPUT && dir != Direction.UP && dir != Direction.DOWN;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return getMode() == IOMode.OUTPUT && dir != Direction.UP;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, CompactNetherPortalBlockEntity be){
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        ServerWorld localWorld = (ServerWorld) world;
        if(aboveState.isOf(ModBlocks.ITEM_TDI)){
            MinecraftServer server = localWorld.getServer();
            ItemTDIBlockEntity be2 = (ItemTDIBlockEntity) localWorld.getBlockEntity(abovePos);
            if(be2.found_portal && be2.paired){
                ServerWorld destWorld;
                TDIDimensionType type = be2.paired_dim;
                switch(type){
                    case NETHER -> destWorld = server.getWorld(World.NETHER);
                    case END -> {
                        return;
                    }
                    default -> destWorld = server.getWorld(World.OVERWORLD);
                }
                BlockPos destPos = be2.paired_pos;
                BlockState destState = destWorld.getBlockState(destPos);
                if(!destState.isOf(ModBlocks.ITEM_TDI)) return;
                ItemTDIBlockEntity be4 = (ItemTDIBlockEntity) destWorld.getBlockEntity(destPos);
                if(!be4.found_portal || !be4.paired) return;
                BlockPos destPortalPos = destPos.down();
                ChunkPos chunkPos = new ChunkPos(destPortalPos);
                destWorld.setChunkForced(chunkPos.x, chunkPos.z, true);
                CompactNetherPortalBlockEntity be3 = (CompactNetherPortalBlockEntity) destWorld.getBlockEntity(destPortalPos);
                if(be3.getMode() == IOMode.OUTPUT && be.getMode() == IOMode.INPUT){
                    ItemStack stack = be.getStack(0).copy();
                    ItemStack stack1 = be3.getStack(0).copy();
                    ItemStack stack2 = stack.copy();
                    if(!stack.isEmpty()){
                        if(stack1.isEmpty() || stack1.getItem() == stack.getItem() && stack1.getCount() < stack1.getMaxCount()){
                            be.removeStack(0, 1);
                            if(stack1.isEmpty()){
                                stack2.setCount(1);
                            }else{
                                stack2.setCount(stack1.getCount()+1);
                            }
                            be3.setStack(0, stack2);
                            be.markDirty();
                            be3.markDirty();
                        }
                    }
                }
            }
        }
    }

}
