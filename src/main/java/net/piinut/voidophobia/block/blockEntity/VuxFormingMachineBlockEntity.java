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
import net.piinut.voidophobia.gui.handler.VuxFormingMachineScreenHandler;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.item.recipe.VuxFormingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VuxFormingMachineBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, BasicInventory, SidedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int processTime;
    int recipeSelected;
    int vuxStored;
    public static final int TOTAL_PROCESS_TIME = 100;
    public static final int VUX_CONSUME_PER_TICK = 40;
    public static final int MAX_VUX_CAPACITY = 32000;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};
    private static final int[] SIDE_SLOTS = new int[]{0};

    /*
    * Vux consumption per tick: 40
    * Vux consumption per operation: 4000
    * Total operating ticks before run out of vux when machine is full: 400
    * Total operations before run out of vux when machine is full: 8
    * */

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return VuxFormingMachineBlockEntity.this.recipeSelected;
                }
                case 1 -> {
                    return VuxFormingMachineBlockEntity.this.processTime;
                }
                case 2 -> {
                    return VuxFormingMachineBlockEntity.this.vuxStored;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFormingMachineBlockEntity.this.recipeSelected = value;
                case 1 -> VuxFormingMachineBlockEntity.this.processTime = value;
                case 2 -> VuxFormingMachineBlockEntity.this.vuxStored = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public VuxFormingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VUX_FORMING_MACHINE, pos, state);
        this.recipeSelected = -1;
        this.vuxStored = 0;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.vuxStored = nbt.getInt("VuxStored");
        this.processTime = nbt.getInt("ProcessTime");
        this.recipeSelected = nbt.getInt("RecipeSelected");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("VuxStored", this.vuxStored);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("RecipeSelected", this.recipeSelected);
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Forming");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new VuxFormingMachineScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private boolean isProcessing(){
        return this.processTime < TOTAL_PROCESS_TIME;
    }

    public static void tick(World world, BlockPos pos, BlockState state, VuxFormingMachineBlockEntity blockEntity) {
        boolean bl = blockEntity.isProcessing();
        boolean bl2 = false;
        if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && blockEntity.recipeSelected >= 0) {
            List<VuxFormingRecipe> recipeList = world.getRecipeManager().getAllMatches(ModRecipeTypes.VUX_FORMING, blockEntity, world);
            VuxFormingRecipe recipe = recipeList.isEmpty() || blockEntity.recipeSelected >= recipeList.size() || blockEntity.recipeSelected < 0? null : recipeList.get(blockEntity.recipeSelected);
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isProcessing() && VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                if (blockEntity.isProcessing()) {
                    bl2 = true;
                }
            }
            if (blockEntity.isProcessing() && VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i) && blockEntity.vuxStored >= VuxFormingMachineBlockEntity.VUX_CONSUME_PER_TICK) {
                ++blockEntity.processTime;
                blockEntity.vuxStored -= VuxFormingMachineBlockEntity.VUX_CONSUME_PER_TICK;
                if (blockEntity.processTime == VuxFormingMachineBlockEntity.TOTAL_PROCESS_TIME) {
                    blockEntity.processTime = 0;
                    VuxFormingMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory, i);
                    bl2 = true;
                }
            } else {
                blockEntity.processTime = 0;
            }
        } else if (blockEntity.processTime > 0) {
            blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, VuxFormingMachineBlockEntity.TOTAL_PROCESS_TIME);
        }
        if (bl != blockEntity.isProcessing()) {
            bl2 = true;
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (bl2) {
            VuxFormingMachineBlockEntity.markDirty(world, pos, state);
        }
    }

    private static void craftRecipe(VuxFormingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null || !VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, slots, count)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = recipe.getOutput();
        ItemStack itemStack2 = slots.get(1);
        if (itemStack2.isEmpty()) {
            slots.set(1, itemStack1.copy());
        } else if (itemStack2.isOf(itemStack1.getItem())) {
            itemStack2.increment(recipe.getCount());
        }
        itemStack.decrement(1);
    }

    private static boolean canAcceptRecipeOutput(VuxFormingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (slots.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(1);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    public double requestVuxConsume() {
        if(this.vuxStored >= VuxFormingMachineBlockEntity.MAX_VUX_CAPACITY){
            return 0;
        }
        return Math.min(VuxFormingMachineBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 200);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 200);
        if(this.vuxStored > VuxFormingMachineBlockEntity.MAX_VUX_CAPACITY){
            this.vuxStored = VuxFormingMachineBlockEntity.MAX_VUX_CAPACITY;
        }
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
        return slot == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 1;
    }


}
