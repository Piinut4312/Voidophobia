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

import java.util.Random;

public class VuxFilterMachineBlockEntity extends BlockEntity implements BasicInventory, NamedScreenHandlerFactory, SidedInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    int processTime;
    public static final int TOTAL_PROCESS_TIME = 200;
    int vuxStored;
    public static final int MAX_VUX_CAPACITY = 60000;
    private static final int VUX_CONSUME_PER_TICK = 120;
    int vuxConsumeBias = 0;
    int processTimeBias = 0;
    int filterDamageModifier = 0;
    int vuxCapacityModifier = 0;
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
                    return VuxFilterMachineBlockEntity.this.vuxStored;
                }
                case 2 -> {
                    return VuxFilterMachineBlockEntity.this.vuxConsumeBias;
                }
                case 3 -> {
                    return VuxFilterMachineBlockEntity.this.processTimeBias;
                }
                case 4 -> {
                    return VuxFilterMachineBlockEntity.this.filterDamageModifier;
                }
                case 5 -> {
                    return VuxFilterMachineBlockEntity.this.vuxCapacityModifier;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> VuxFilterMachineBlockEntity.this.processTime = value;
                case 1 -> VuxFilterMachineBlockEntity.this.vuxStored = value;
                case 2 -> VuxFilterMachineBlockEntity.this.vuxConsumeBias = value;
                case 3 -> VuxFilterMachineBlockEntity.this.processTimeBias = value;
                case 4 -> VuxFilterMachineBlockEntity.this.filterDamageModifier = value;
                case 5 -> VuxFilterMachineBlockEntity.this.vuxCapacityModifier = value;
            }
        }

        @Override
        public int size() {
            return 6;
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
        this.vuxStored = nbt.getInt("VuxStored");
        this.vuxConsumeBias = nbt.getInt("VuxConsumeBias");
        this.processTimeBias = nbt.getInt("ProcessTimeBias");
        this.filterDamageModifier = nbt.getInt("FilterDamageModifier");
        this.vuxCapacityModifier = nbt.getInt("VuxCapacityModifier");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("VuxStored", this.vuxStored);
        nbt.putInt("VuxConsumeBias", this.vuxConsumeBias);
        nbt.putInt("ProcessTimeBias", this.processTimeBias);
        nbt.putInt("FilterDamageModifier", this.filterDamageModifier);
        nbt.putInt("VuxCapacityModifier", this.vuxCapacityModifier);
    }

    public int requestVuxConsume() {
        if(this.vuxStored >= this.getMaxVuxCapacity()){
            return 0;
        }
        return Math.min(this.getMaxVuxCapacity() - this.vuxStored, 800);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 800);
        if(this.vuxStored > this.getMaxVuxCapacity()){
            this.vuxStored = this.getMaxVuxCapacity();
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

    private static float calculateFilterDamageChance(int modifier){
        return Math.max(1.0f - 0.2f*modifier, 0);
    }

    private void resetModifiers(){
        this.vuxConsumeBias = 0;
        this.processTimeBias = 0;
        this.filterDamageModifier = 0;
        this.vuxCapacityModifier = 0;
    }

    private int getMaxVuxCapacity(){
        return (int) (VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY * Math.pow(1.2, vuxCapacityModifier));
    }

    private int getTotalProcessTime(){
        return VuxFilterMachineBlockEntity.TOTAL_PROCESS_TIME + this.processTimeBias;
    }

    private float getVuxConsume(){
        return VuxFilterMachineBlockEntity.VUX_CONSUME_PER_TICK + this.vuxConsumeBias;
    }

    public static void tick(World world, BlockPos pos, BlockState state, VuxFilterMachineBlockEntity blockEntity){
        if(!world.isClient){
            boolean bl = blockEntity.isProcessing();
            boolean bl2 = false;
            blockEntity.resetModifiers();
            for(int i = 3; i < 6; i++){
                ItemStack itemStack = blockEntity.getStack(i);
                if(itemStack.isOf(ModItems.DELICATE_FILTERING_MODIFIER_MODULE)){
                    blockEntity.filterDamageModifier += 1;
                    blockEntity.processTimeBias += 60;
                    blockEntity.vuxConsumeBias -= 20;
                }
                if(itemStack.isOf(ModItems.PROCESSING_SPEED_BOOST_MODIFIER_MODULE)){
                    blockEntity.processTimeBias -= 40;
                    blockEntity.vuxConsumeBias += 60;
                }
                if(itemStack.isOf(ModItems.VUX_CAPACITY_UPGRADE_MODIFIER_MODULE)){
                    blockEntity.vuxCapacityModifier += 1;
                }
            }
            if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
                VuxFilteringRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.VUX_FILTERING, blockEntity, world).orElse(null);
                int i = blockEntity.getMaxCountPerStack();
                if (!blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                    if (blockEntity.isProcessing()) {
                        bl2 = true;
                    }
                }
                if (blockEntity.isProcessing() && VuxFilterMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i) && blockEntity.vuxStored >= blockEntity.getVuxConsume()) {
                    ++blockEntity.processTime;
                    blockEntity.vuxStored -= blockEntity.getVuxConsume();
                    if (blockEntity.processTime >= blockEntity.getTotalProcessTime()) {
                        blockEntity.processTime = 0;
                        VuxFilterMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory, i, blockEntity.filterDamageModifier);
                        bl2 = true;
                    }
                } else {
                    blockEntity.processTime = 0;
                }
            } else if (blockEntity.processTime > 0) {
                blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, blockEntity.getTotalProcessTime());
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
        return this.processTime < this.getTotalProcessTime();
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
