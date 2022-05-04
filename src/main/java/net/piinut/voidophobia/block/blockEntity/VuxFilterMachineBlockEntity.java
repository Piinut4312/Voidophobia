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
import net.piinut.voidophobia.gui.handler.VuxFilterMachineScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.item.recipe.VuxFilteringRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class VuxFilterMachineBlockEntity extends AbstractVuxContainerBlockEntity implements BasicInventory, NamedScreenHandlerFactory, SidedInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    private int processTime;
    private int totalProcessTime;
    private int vuxConsumptionRate;
    public static final int DEFAULT_TOTAL_PROCESS_TIME = 200;
    public static final int DEFAULT_VUX_CAPACITY = 60000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 600;
    private static final int DEFAULT_VUX_CONSUMPTION_RATE = 120;
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
                    return VuxFilterMachineBlockEntity.this.totalProcessTime;
                }
                case 2 -> {
                    return VuxFilterMachineBlockEntity.this.getVuxStored();
                }
                case 3 -> {
                    return VuxFilterMachineBlockEntity.this.getVuxCapacity();
                }
                case 4 -> {
                    return VuxFilterMachineBlockEntity.this.getVuxTransferRate();
                }
                case 5 -> {
                    return VuxFilterMachineBlockEntity.this.vuxConsumptionRate;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFilterMachineBlockEntity.this.processTime = value;
                case 1 -> VuxFilterMachineBlockEntity.this.totalProcessTime = value;
                case 2 -> VuxFilterMachineBlockEntity.this.setVuxStored(value);
                case 3 -> VuxFilterMachineBlockEntity.this.setVuxCapacity(value);
                case 4 -> VuxFilterMachineBlockEntity.this.setVuxTransferRate(value);
                case 5 -> VuxFilterMachineBlockEntity.this.vuxConsumptionRate = value;
            }
        }

        @Override
        public int size() {
            return 6;
        }
    };

    public VuxFilterMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VUX_FILTER_MACHINE, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.totalProcessTime = nbt.getInt("TotalProcessTime");
        this.vuxConsumptionRate = nbt.getInt("VuxConsumptionRate");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("TotalProcessTime", this.totalProcessTime);
        nbt.putInt("VuxConsumptionRate", this.vuxConsumptionRate);
    }

    public int requestVuxConsume() {
        if(this.getVuxStored() >= this.getVuxCapacity()){
            return 0;
        }
        return Math.min(this.getVuxCapacity() - this.getVuxStored(), this.getVuxTransferRate());
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

    private static float calculateFilterDamageChance(int modifier){
        return Math.max(1.0f - 0.2f*modifier, 0);
    }

    private void updateVuxCapacity(int modifiers){
        this.setVuxCapacity((int) (VuxFilterMachineBlockEntity.DEFAULT_VUX_CAPACITY * Math.pow(1.2, modifiers)));
    }

    private void updateTotalProcessTime(int bias){
        this.totalProcessTime = Math.max(0, VuxFilterMachineBlockEntity.DEFAULT_TOTAL_PROCESS_TIME + bias);
    }

    private void updateVuxConsumptionRate(int bias){
        this.vuxConsumptionRate = Math.max(0, VuxFilterMachineBlockEntity.DEFAULT_VUX_CONSUMPTION_RATE + bias);
    }

    public static void tick(World world, BlockPos pos, BlockState state, VuxFilterMachineBlockEntity blockEntity){
        if(!world.isClient){
            boolean bl = blockEntity.isProcessing();
            boolean bl2 = false;
            int filterDamageModifier = 0;
            int processTimeBias = 0;
            int vuxConsumeBias = 0;
            int vuxCapacityModifier = 0;
            for(int i = 3; i < 6; i++){
                ItemStack itemStack = blockEntity.getStack(i);
                if(itemStack.isOf(ModItems.DELICATE_FILTERING_MODIFIER_MODULE)){
                    filterDamageModifier += 1;
                    processTimeBias += 60;
                    vuxConsumeBias -= 20;
                }
                if(itemStack.isOf(ModItems.PROCESSING_SPEED_BOOST_MODIFIER_MODULE)){
                    processTimeBias -= 40;
                    vuxConsumeBias += 60;
                }
                if(itemStack.isOf(ModItems.VUX_CAPACITY_UPGRADE_MODIFIER_MODULE)){
                    vuxCapacityModifier += 1;
                }
            }
            blockEntity.updateVuxCapacity(vuxCapacityModifier);
            blockEntity.updateTotalProcessTime(processTimeBias);
            blockEntity.updateVuxConsumptionRate(vuxConsumeBias);
            if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
                VuxFilteringRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.VUX_FILTERING, blockEntity, world).orElse(null);
                int i = blockEntity.getMaxCountPerStack();
                if (!blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                    if (blockEntity.isProcessing()) {
                        bl2 = true;
                    }
                }
                if (blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i) && blockEntity.getVuxStored() >= blockEntity.vuxConsumptionRate) {
                    ++blockEntity.processTime;
                    blockEntity.removeVux(blockEntity.vuxConsumptionRate);
                    if (blockEntity.processTime >= blockEntity.totalProcessTime) {
                        blockEntity.processTime = 0;
                        VuxFilterMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory, i, filterDamageModifier);
                        bl2 = true;
                    }
                } else {
                    blockEntity.processTime = 0;
                }
            } else if (blockEntity.processTime > 0) {
                blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, blockEntity.totalProcessTime);
            }
            if (bl != blockEntity.isProcessing()) {
                bl2 = true;
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
            if (bl2) {
                VuxFilterMachineBlockEntity.markDirty(world, pos, state);
            }
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

    private static void craftRecipe(VuxFilteringRecipe recipe, DefaultedList<ItemStack> slots, int count, int modifier) {
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
        float filterDamageChance = VuxFilterMachineBlockEntity.calculateFilterDamageChance(modifier);
        Random random = new Random();
        if(random.nextFloat() < filterDamageChance){
            itemStack1.setDamage(itemStack1.getDamage()+1);
        }
        if(itemStack1.getDamage() == itemStack1.getMaxDamage()){
            itemStack1.decrement(1);
        }
    }

    private boolean isProcessing() {
        return this.processTime < this.totalProcessTime;
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
        if(slot >= 3) return false;
        return (stack.isOf(ModItems.VUX_FILTER) && slot == 1) || (!stack.isOf(ModItems.VUX_FILTER) && slot == 0);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2 && dir == Direction.DOWN;
    }
}
