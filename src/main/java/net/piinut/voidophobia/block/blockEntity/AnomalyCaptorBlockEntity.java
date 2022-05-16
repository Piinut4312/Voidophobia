package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.handler.AnomalyCaptorScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class AnomalyCaptorBlockEntity extends BlockEntity implements SidedInventory, BasicInventory, NamedScreenHandlerFactory {

    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3, 4, 5};
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    int cooldown;
    int cooldownTotal;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return AnomalyCaptorBlockEntity.this.cooldown;
                }
                case 1 -> {
                    return AnomalyCaptorBlockEntity.this.cooldownTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AnomalyCaptorBlockEntity.this.cooldown = value;
                case 1 -> AnomalyCaptorBlockEntity.this.cooldownTotal = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public AnomalyCaptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANOMALY_CAPTOR, pos, state);
        this.cooldown = 0;
        this.cooldownTotal = 200;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if(side == Direction.DOWN){
            return BOTTOM_SLOTS;
        }
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot < 6;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.voidophobia.anomaly_captor");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AnomalyCaptorScreenHandler(syncId, this, inv, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.cooldown = nbt.getInt("Cooldown");
        this.cooldownTotal = nbt.getInt("CooldownTotal");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("Cooldown", this.cooldown);
        nbt.putInt("CooldownTotal", this.cooldownTotal);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AnomalyCaptorBlockEntity blockEntity) {
        if(blockEntity.canCapture()){
            blockEntity.cooldown++;
            if(blockEntity.cooldown >= blockEntity.cooldownTotal){
                blockEntity.insertCapturedItem(new ItemStack(ModItems.GODEL_CRYSTAL_FRAGMENT));
                blockEntity.cooldown = 0;
            }
        }
    }

    private void insertCapturedItem(ItemStack itemStack) {
        for(int i = 0; i < 6; i++){
            ItemStack itemStack1 = this.inventory.get(i);
            if(itemStack1.isEmpty()){
                this.inventory.set(i, itemStack);
                break;
            }else if(itemStack1.isItemEqual(itemStack)){
                if(itemStack.getCount() + itemStack1.getCount() <= 64){
                    itemStack1.increment(itemStack.getCount());
                    break;
                }
            }
        }
    }

    private boolean canCapture() {
        boolean flag = false;
        for(int i = 0; i < 6; i++){
            if(this.inventory.get(i).getCount() < 64){
                flag = true;
            }
        }
        return flag;
    }
}
