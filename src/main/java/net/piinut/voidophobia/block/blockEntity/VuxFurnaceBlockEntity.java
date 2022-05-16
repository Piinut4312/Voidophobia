package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.VuxFurnaceBlock;
import net.piinut.voidophobia.gui.handler.VuxFurnaceScreenHandler;
import org.jetbrains.annotations.Nullable;

public class VuxFurnaceBlockEntity extends AbstractVuxContainerBlockEntity implements BasicInventory, SidedInventory, NamedScreenHandlerFactory {

    public static final int DEFAULT_VUX_CAPACITY = 64000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 400;
    public static final int DEFAULT_VUX_CONSUMPTION_RATE = 40;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{1};
    private static final int[] SIDE_SLOTS = new int[]{0};
    int cookTime;
    int cookTimeTotal;
    int vuxConsumptionRate;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return VuxFurnaceBlockEntity.this.cookTime;
                }
                case 1 -> {
                    return VuxFurnaceBlockEntity.this.cookTimeTotal;
                }
                case 2 -> {
                    return VuxFurnaceBlockEntity.this.getVuxStored();
                }
                case 3 -> {
                    return VuxFurnaceBlockEntity.this.getVuxCapacity();
                }
                case 4 -> {
                    return VuxFurnaceBlockEntity.this.getVuxTransferRate();
                }
                case 5 -> {
                    return VuxFurnaceBlockEntity.this.vuxConsumptionRate;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFurnaceBlockEntity.this.cookTime = value;
                case 1 -> VuxFurnaceBlockEntity.this.cookTimeTotal = value;
                case 2 -> VuxFurnaceBlockEntity.this.setVuxStored(value);
                case 3 -> VuxFurnaceBlockEntity.this.setVuxCapacity(value);
                case 4 -> VuxFurnaceBlockEntity.this.setVuxTransferRate(value);
                case 5 -> VuxFurnaceBlockEntity.this.vuxConsumptionRate = value;
            }
        }

        @Override
        public int size() {
            return 6;
        }
    };

    public VuxFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VUX_FURNACE, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
        this.cookTime = 0;
        this.cookTimeTotal = 200;
        this.vuxConsumptionRate = DEFAULT_VUX_CONSUMPTION_RATE;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.vuxConsumptionRate = nbt.getInt("VuxConsumptionRate");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        nbt.putInt("VuxConsumptionRate", this.vuxConsumptionRate);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if(side == Direction.UP){
            return TOP_SLOTS;
        }
        if(side == Direction.DOWN){
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Vux Furnace");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new VuxFurnaceScreenHandler(syncId, this, inv, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private static boolean canAcceptRecipeOutput(@Nullable SmeltingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null) {
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
        return itemStack2.getCount() + itemStack.getCount() <= count;
    }

    private static void craftRecipe(@Nullable SmeltingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (!VuxFurnaceBlockEntity.canAcceptRecipeOutput(recipe, slots, count)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = recipe.getOutput();
        ItemStack itemStack2 = slots.get(1);
        if (itemStack2.isEmpty()) {
            slots.set(1, itemStack1.copy());
        } else if (itemStack2.isOf(itemStack1.getItem())) {
            itemStack2.increment(itemStack1.getCount());
        }
        itemStack.decrement(1);
    }

    private static int getCookTime(World world, BasicInventory inventory) {
        return world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, inventory, world).map(SmeltingRecipe::getCookTime).orElse(200);
    }

    private boolean canHeat(){
        return this.getVuxStored() >= this.vuxConsumptionRate;
    }

    public static void tick(World world, BlockPos pos, BlockState state, VuxFurnaceBlockEntity blockEntity) {

        boolean bl = blockEntity.cookTime > 0;
        boolean bl2 = false;
        if (blockEntity.canHeat() && !blockEntity.inventory.get(0).isEmpty()) {
            SmeltingRecipe recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, blockEntity, world).orElse(null);
            int i = blockEntity.getMaxCountPerStack();
            if (blockEntity.canHeat() && VuxFurnaceBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.cookTime;
                blockEntity.removeVux(blockEntity.vuxConsumptionRate);
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    blockEntity.cookTimeTotal = VuxFurnaceBlockEntity.getCookTime(world, blockEntity);
                    VuxFurnaceBlockEntity.craftRecipe(recipe, blockEntity.inventory, i);
                    bl2 = true;
                }
            } else {
                blockEntity.cookTime = 0;
            }
        } else if (blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }
        if (bl != (blockEntity.cookTime > 0)) {
            bl2 = true;
            state = state.with(VuxFurnaceBlock.ON, blockEntity.cookTime > 0);
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (bl2) {
            VuxFurnaceBlockEntity.markDirty(world, pos, state);
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if ((slot == 0) && !bl) {
            this.cookTimeTotal = VuxFurnaceBlockEntity.getCookTime(this.world, this);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    public int requestVuxConsume() {
        if(this.getVuxStored() >= this.getVuxCapacity()){
            return 0;
        }
        return Math.min(this.getVuxCapacity() - this.getVuxStored(), this.getVuxTransferRate());
    }
}
