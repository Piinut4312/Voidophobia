package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class VuxFormingMachineBlockEntity extends AbstractVuxContainerBlockEntity implements NamedScreenHandlerFactory, BasicInventory, SidedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private int processTime;
    private int recipeSelected;
    public static final int TOTAL_PROCESS_TIME = 100;
    public static final int VUX_CONSUME_PER_TICK = 40;
    public static final int DEFAULT_VUX_CAPACITY = 10000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 200;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};
    private static final int[] SIDE_SLOTS = new int[]{0};

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
                    return VuxFormingMachineBlockEntity.this.getVuxStored();
                }
                case 3 -> {
                    return VuxFormingMachineBlockEntity.this.getVuxCapacity();
                }
                case 4 -> {
                    return VuxFormingMachineBlockEntity.this.getVuxTransferRate();
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFormingMachineBlockEntity.this.recipeSelected = value;
                case 1 -> VuxFormingMachineBlockEntity.this.processTime = value;
                case 2 -> VuxFormingMachineBlockEntity.this.setVuxStored(value);
                case 3 -> VuxFormingMachineBlockEntity.this.setVuxCapacity(value);
                case 4 -> VuxFormingMachineBlockEntity.this.setVuxTransferRate(value);
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public VuxFormingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VUX_FORMING_MACHINE, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
        this.recipeSelected = -1;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.recipeSelected = nbt.getInt("RecipeSelected");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
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
            if (!blockEntity.isProcessing() && VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory)) {
                if (blockEntity.isProcessing()) {
                    bl2 = true;
                }
            }
            if (blockEntity.isProcessing() && VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory) && blockEntity.getVuxStored() >= VuxFormingMachineBlockEntity.VUX_CONSUME_PER_TICK) {
                ++blockEntity.processTime;
                blockEntity.removeVux(VuxFormingMachineBlockEntity.VUX_CONSUME_PER_TICK);
                if (blockEntity.processTime == VuxFormingMachineBlockEntity.TOTAL_PROCESS_TIME) {
                    blockEntity.processTime = 0;
                    VuxFormingMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory);
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

    private static void craftRecipe(VuxFormingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (recipe == null || !VuxFormingMachineBlockEntity.canAcceptRecipeOutput(recipe, slots)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = recipe.getOutput();
        ItemStack itemStack2 = slots.get(1);
        if (itemStack2.isEmpty()) {
            slots.set(1, itemStack1.copy());
        } else if (itemStack2.isOf(itemStack1.getItem())) {
            itemStack2.increment(recipe.getOutputCount());
        }
        itemStack.decrement(recipe.getInputCount());
    }

    private static boolean canAcceptRecipeOutput(VuxFormingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (slots.get(0).isEmpty() || recipe == null || slots.get(0).getCount() < recipe.getInputCount()) {
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
        return itemStack2.getCount() + recipe.getOutputCount() <= itemStack2.getMaxCount();
    }

    public double requestVuxConsume() {
        if(this.getVuxStored() >= this.getVuxCapacity()){
            return 0;
        }
        return Math.min(this.getVuxCapacity()- this.getVuxStored(), this.getVuxTransferRate());
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
