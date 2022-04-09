package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.handler.VuxFilterMachineScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.item.recipe.VuxFilteringRecipe;
import org.jetbrains.annotations.Nullable;

public class VuxFilterMachineBlockEntity extends BlockEntity implements BasicInventory, NamedScreenHandlerFactory, SidedInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    int processTime;
    public static final int TOTAL_PROCESS_TIME = 200;
    float vuxStored;
    public static final float MAX_VUX_CAPACITY = 60000;
    private static final float VUX_CONSUME_PER_TICK = 120;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2};
    private static final int[] SIDE_SLOTS = new int[]{1};

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return VuxFilterMachineBlockEntity.this.processTime;
                }
                case 1 -> {
                    return (int) VuxFilterMachineBlockEntity.this.vuxStored;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFilterMachineBlockEntity.this.processTime = value;
                case 1 -> VuxFilterMachineBlockEntity.this.vuxStored = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public VuxFilterMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VUX_FILTER_MACHINE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.vuxStored = nbt.getFloat("VuxStored");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putFloat("VuxStored", this.vuxStored);
    }

    public double requestVuxConsume() {
        if(this.vuxStored >= VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY){
            return 0;
        }
        return Math.min(VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 800);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 800);
        if(this.vuxStored > VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY){
            this.vuxStored = VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY;
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Vux Filtering");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new VuxFilterMachineScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public static void tick(World world, BlockPos pos, BlockState state, VuxFilterMachineBlockEntity blockEntity){
        boolean bl = blockEntity.isProcessing();
        boolean bl2 = false;
        if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
            VuxFilteringRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.VUX_FILTERING, blockEntity, world).orElse(null);
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                if (blockEntity.isProcessing()) {
                    bl2 = true;
                }
            }
            if (blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i) && blockEntity.vuxStored >= VuxFilterMachineBlockEntity.VUX_CONSUME_PER_TICK) {
                ++blockEntity.processTime;
                blockEntity.vuxStored -= VuxFilterMachineBlockEntity.VUX_CONSUME_PER_TICK;
                if (blockEntity.processTime == VuxFilterMachineBlockEntity.TOTAL_PROCESS_TIME) {
                    blockEntity.processTime = 0;
                    VuxFilterMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory, i);
                    bl2 = true;
                }
            } else {
                blockEntity.processTime = 0;
            }
        } else if (blockEntity.processTime > 0) {
            blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, VuxFilterMachineBlockEntity.TOTAL_PROCESS_TIME);
        }
        if (bl != blockEntity.isProcessing()) {
            bl2 = true;
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (bl2) {
            VuxFilterMachineBlockEntity.markDirty(world, pos, state);
        }
    }

    private static boolean canAcceptRecipeOutput(VuxFilteringRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (slots.get(0).isEmpty() || slots.get(1).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack1 = slots.get(2);
        if(itemStack1.isEmpty()){
            return true;
        }
        if (!itemStack1.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }

        return itemStack1.getCount() < count && itemStack1.getCount() < itemStack1.getMaxCount();
    }

    private static void craftRecipe(VuxFilteringRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null || !VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, slots, count)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = slots.get(1);
        ItemStack itemStack2 = recipe.getOutput();
        ItemStack itemStack3 = slots.get(2);
        if(itemStack3.isEmpty()){
            slots.set(2, itemStack2.copy());
        }else if(itemStack3.isOf(itemStack2.getItem())){
            itemStack3.increment(itemStack2.getCount());
        }
        itemStack.decrement(1);
        itemStack1.setDamage(itemStack1.getDamage()+1);
        if(itemStack1.getDamage() == itemStack1.getMaxDamage()){
            itemStack1.decrement(1);
        }
    }

    private boolean isProcessing() {
        return this.processTime < TOTAL_PROCESS_TIME;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return (stack.isOf(ModItems.VUX_FILTER) && slot == 1) || (!stack.isOf(ModItems.VUX_FILTER) && slot == 0);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2 && dir == Direction.DOWN;
    }
}
