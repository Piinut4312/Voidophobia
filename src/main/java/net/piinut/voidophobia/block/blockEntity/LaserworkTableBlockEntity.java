package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaserworkTableBlockEntity extends BlockEntity implements BasicInventory, SidedInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2};
    private static final int[] BOTTOM_SLOTS = new int[]{1, 2};

    public LaserworkTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASERWORK_TABLE, pos, state);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        switch(side){
            case UP -> {
                return TOP_SLOTS;
            }
            case DOWN -> {
                return BOTTOM_SLOTS;
            }
            default -> {
                return SIDE_SLOTS;
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if(slot == 0){
            return dir != Direction.DOWN;
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if(slot == 1 || slot == 2){
            return dir != Direction.UP;
        }
        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void craftItem(ServerWorld world, ItemStack result){
        if(this.getStack(0).isEmpty()){
            return;
        }
        ItemStack ingredientSlot = this.getStack(0);
        ItemStack outputSlot = this.getStack(1);
        ItemStack remainderSlot = this.getStack(2);
        Item remainderItem = ingredientSlot.getItem().getRecipeRemainder();
        if(!outputSlot.isEmpty() && (outputSlot.getCount() == outputSlot.getMaxCount()) || (!remainderSlot.isEmpty() && remainderSlot.getCount() == remainderSlot.getMaxCount()) || (!outputSlot.isEmpty() && !outputSlot.isOf(result.getItem())) || (!remainderSlot.isEmpty() && !remainderSlot.isOf(remainderItem))){
            return;
        }

        this.getStack(0).decrement(1);

        if(outputSlot.isEmpty()){
            ItemStack resultStack = result.copy();
            resultStack.setCount(1);
            this.setStack(1, resultStack);
        }else{
            this.getStack(1).increment(1);
        }

        if(remainderSlot.isEmpty()){
            ItemStack remainderStack = new ItemStack(remainderItem, 1);
            this.setStack(2, remainderStack);
        }else{
            this.getStack(2).increment(1);
        }

        this.markDirty();
        world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }



    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, LaserworkTableBlockEntity blockEntity) {
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, LaserworkTableBlockEntity blockEntity) {
        ((ServerWorld) world).getChunkManager().markForUpdate(blockEntity.getPos());
    }
}
