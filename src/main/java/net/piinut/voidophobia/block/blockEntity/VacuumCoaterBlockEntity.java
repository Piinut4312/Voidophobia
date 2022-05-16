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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.handler.VacuumCoaterScreenHandler;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.item.recipe.VacuumCoatingRecipe;
import org.jetbrains.annotations.Nullable;

public class VacuumCoaterBlockEntity extends AbstractVuxContainerBlockEntity implements SidedInventory, BasicInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private static final int[] TOP_SLOTS = new int[]{0, 1};
    private static final int[] BOTTOM_SLOTS = new int[]{2};
    private static final int[] SIDE_SLOTS = new int[]{0, 1};
    private int processTime;
    private int processTimeTotal;
    public static final int DEFAULT_VUX_CAPACITY = 160000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 1000;
    private static final int VUX_CONSUME_PER_TICK = 200;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return VacuumCoaterBlockEntity.this.processTime;
                }
                case 1 -> {
                    return VacuumCoaterBlockEntity.this.processTimeTotal;
                }
                case 2 -> {
                    return VacuumCoaterBlockEntity.this.getVuxStored();
                }
                case 3 -> {
                    return VacuumCoaterBlockEntity.this.getVuxCapacity();
                }
                case 4 -> {
                    return VacuumCoaterBlockEntity.this.getVuxTransferRate();
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VacuumCoaterBlockEntity.this.processTime = value;
                case 1 -> VacuumCoaterBlockEntity.this.processTimeTotal = value;
                case 2 -> VacuumCoaterBlockEntity.this.setVuxStored(value);
                case 3 -> VacuumCoaterBlockEntity.this.setVuxCapacity(value);
                case 4 -> VacuumCoaterBlockEntity.this.setVuxTransferRate(value);
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };
    
    public VacuumCoaterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VACUUM_COATER, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.processTimeTotal = nbt.getInt("ProcessTimeTotal");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("ProcessTimeTotal", this.processTimeTotal);
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
        return slot != 2;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2 && dir == Direction.DOWN;
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new VacuumCoaterScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public int requestVuxConsume() {
        if(this.getVuxStored() >= this.getVuxCapacity()){
            return 0;
        }
        return Math.min(this.getVuxCapacity() - this.getVuxStored(), this.getVuxTransferRate());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.voidophobia.vacuum_coating");
    }

    private static boolean canAcceptRecipeOutput(VacuumCoatingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (slots.get(0).isEmpty() || slots.get(1).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    private static void craftRecipe(VacuumCoatingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (recipe == null || !VacuumCoaterBlockEntity.canAcceptRecipeOutput(recipe, slots)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = slots.get(1);
        ItemStack itemStack2 = recipe.getOutput();
        ItemStack itemStack3 = slots.get(2);
        if (itemStack3.isEmpty()) {
            slots.set(2, itemStack2.copy());
        } else if (itemStack3.isOf(itemStack2.getItem())) {
            itemStack3.increment(1);
        }
        itemStack.decrement(1);
        itemStack1.decrement(1);
    }

    private boolean isProcessing() {
        return this.processTime < this.processTimeTotal;
    }

    private static int getProcessTime(World world, BasicInventory inventory) {
        return world.getRecipeManager().getFirstMatch(ModRecipeTypes.VACUUM_COATING, inventory, world).map(VacuumCoatingRecipe::getProcessTime).orElse(200);
    }
    
    public static void tick(World world, BlockPos blockPos, BlockState blockState, VacuumCoaterBlockEntity blockEntity) {
        boolean bl = blockEntity.isProcessing();
        boolean bl2 = false;

        if (!blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
            VacuumCoatingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.VACUUM_COATING, blockEntity, world).orElse(null);
            if (VacuumCoaterBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory)) {
                if (blockEntity.isProcessing()) {
                    bl2 = true;
                }
            }
            if (VacuumCoaterBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory) && blockEntity.getVuxStored() >= VacuumCoaterBlockEntity.VUX_CONSUME_PER_TICK) {
                ++blockEntity.processTime;
                blockEntity.removeVux(VacuumCoaterBlockEntity.VUX_CONSUME_PER_TICK);
                if (blockEntity.processTime == blockEntity.processTimeTotal) {
                    blockEntity.processTime = 0;
                    blockEntity.processTimeTotal = VacuumCoaterBlockEntity.getProcessTime(world, blockEntity);
                    VacuumCoaterBlockEntity.craftRecipe(recipe, blockEntity.inventory);
                    bl2 = true;
                }
            } else {
                blockEntity.processTime = 0;
            }
        } else if (blockEntity.processTime > 0) {
            blockEntity.processTime = MathHelper.clamp(blockEntity.processTimeTotal - 2, 0, blockEntity.processTimeTotal);
        }
        if (bl != blockEntity.isProcessing()) {
            bl2 = true;
            world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
        }
        if (bl2) {
            VacuumCoaterBlockEntity.markDirty(world, blockPos, blockState);
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
        if ((slot == 0 || slot == 1) && !bl) {
            this.processTimeTotal = VacuumCoaterBlockEntity.getProcessTime(this.world, this);
            this.processTime = 0;
            this.markDirty();
        }
    }
    
}
