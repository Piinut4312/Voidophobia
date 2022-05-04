package net.piinut.voidophobia.block.blockEntity;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;

import java.util.Map;

public abstract class AbstractItemPipeBlockEntity extends BlockEntity{

    private int cooldown;
    private final int maxCooldown;  //How long a pipe has to wait before transfer items
    private final int bufferSize;   //Size of inventory
    private final int batchSize;    //How many items can a pipe transfer each time
    private Map<Direction, Boolean> lastInsertion = new Object2BooleanOpenHashMap<>();
    private final SimpleInventory inventory;
    public final InventoryStorage inventoryStorage;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
           if(index == 0){
               return AbstractItemPipeBlockEntity.this.cooldown;
           }else{
               return 0;
           }
        }

        @Override
        public void set(int index, int value) {
            if(index == 0){
                AbstractItemPipeBlockEntity.this.cooldown = value;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public AbstractItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCooldown, int bufferSize, int batchSize) {
        super(type, pos, state);
        this.cooldown = 0;
        this.maxCooldown = maxCooldown;
        this.bufferSize = bufferSize;
        this.batchSize = batchSize;
        for(Direction direction : Direction.values()){
            lastInsertion.put(direction, false);
        }
        this.inventory = new SimpleInventory(bufferSize){
            @Override
            public void markDirty() {
                AbstractItemPipeBlockEntity.this.markDirty();
            }
        };
        this.inventoryStorage = InventoryStorage.of(inventory, null);
    }

    private String getInsertionNbtKey(Direction direction){
        switch(direction){
            case UP -> {
                return "LastInsertionFromUp";
            }
            case DOWN -> {
                return "LastInsertionFromDown";
            }
            case NORTH -> {
                return "LastInsertionFromNorth";
            }
            case EAST -> {
                return "LastInsertionFromEast";
            }
            case SOUTH -> {
                return "LastInsertionFromSouth";
            }
            case WEST -> {
                return "LastInsertionFromWest";
            }
        }
        return "";
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt("Cooldown");
        for(Direction direction : Direction.values()){
            this.lastInsertion.put(direction, nbt.getBoolean(getInsertionNbtKey(direction)));
        }
        this.inventory.readNbtList(nbt.getList("Inventory", NbtCompound.LIST_TYPE));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Cooldown", this.cooldown);
        for(Direction direction : Direction.values()){
            nbt.putBoolean(getInsertionNbtKey(direction), this.lastInsertion.get(direction));
        }
        nbt.put("Inventory", this.inventory.toNbtList());
    }

    protected void serverTick(World world, BlockPos blockPos, BlockState blockState) {
        this.cooldown--;
        if(this.cooldown <= 0){
            try(Transaction transaction = Transaction.openOuter()){
                for(Direction direction : Direction.values()){
                    Storage<ItemVariant> targetStorage = ItemStorage.SIDED.find(world, blockPos.offset(direction), direction.getOpposite());
                    for(StorageView<ItemVariant> view : this.inventoryStorage.iterable(transaction)){
                        if(view.isResourceBlank()){
                            continue;
                        }
                        ItemVariant resource = view.getResource();
                        try(Transaction nestedTransaction = Transaction.openNested(transaction)){

                            ItemPipeNodeType itemPipeNodeType = blockState.get(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(direction));
                            if(itemPipeNodeType == ItemPipeNodeType.NONE){
                                continue;
                            }
                            if(targetStorage == null){
                                continue;
                            }

                            boolean isTargetItemPipe = (world.getBlockState(pos.offset(direction)).getBlock() instanceof AbstractItemPipeBlock);

                            if(itemPipeNodeType == ItemPipeNodeType.INSERT && !isTargetItemPipe){
                                long extractionAmount = this.inventoryStorage.simulateExtract(resource, this.batchSize, nestedTransaction);
                                if(extractionAmount > 0){
                                    long insertionAmount = targetStorage.simulateInsert(resource, extractionAmount, nestedTransaction);
                                    if(insertionAmount > 0){
                                        long actualTransferAmount = Math.min(insertionAmount, extractionAmount);
                                        this.inventoryStorage.extract(resource, actualTransferAmount, nestedTransaction);
                                        targetStorage.insert(resource, actualTransferAmount, nestedTransaction);
                                        nestedTransaction.commit();
                                    }
                                }
                            }

                            if(itemPipeNodeType == ItemPipeNodeType.TRANSFER && isTargetItemPipe){
                                AbstractItemPipeBlockEntity targetBlockEntity = (AbstractItemPipeBlockEntity) world.getBlockEntity(blockPos.offset(direction));
                                if(!this.lastInsertion.get(direction)){
                                    long extractionAmount = this.inventoryStorage.simulateExtract(resource, this.batchSize, nestedTransaction);
                                    if(extractionAmount > 0){
                                        long insertionAmount = targetStorage.simulateInsert(resource, extractionAmount, nestedTransaction);
                                        if(insertionAmount > 0){
                                            long actualTransferAmount = Math.min(insertionAmount, extractionAmount);
                                            this.inventoryStorage.extract(resource, actualTransferAmount, nestedTransaction);
                                            targetStorage.insert(resource, actualTransferAmount, nestedTransaction);
                                            targetBlockEntity.lastInsertion.put(direction.getOpposite(), true);
                                            nestedTransaction.commit();
                                        }else{
                                            targetBlockEntity.lastInsertion.put(direction.getOpposite(), false);
                                        }
                                    }
                                }else{
                                    targetBlockEntity.lastInsertion.put(direction.getOpposite(), false);
                                }
                            }
                        }
                    }

                    if(targetStorage != null){
                        for(StorageView<ItemVariant> view : targetStorage.iterable(transaction)){
                            if(view.isResourceBlank()){
                                continue;
                            }
                            ItemVariant resource = view.getResource();
                            try(Transaction nestedTransaction = Transaction.openNested(transaction)){

                                ItemPipeNodeType itemPipeNodeType = blockState.get(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(direction));
                                if(itemPipeNodeType == ItemPipeNodeType.NONE){
                                    continue;
                                }

                                boolean isTargetItemPipe = (world.getBlockState(pos.offset(direction)).getBlock() instanceof AbstractItemPipeBlock);

                                if(itemPipeNodeType == ItemPipeNodeType.EXTRACT && !isTargetItemPipe){
                                    long extractionAmount = targetStorage.simulateExtract(resource, this.batchSize, nestedTransaction);
                                    if(extractionAmount > 0){
                                        long insertionAmount = this.inventoryStorage.simulateInsert(resource, extractionAmount, nestedTransaction);
                                        if(insertionAmount > 0){
                                            long actualTransferAmount = Math.min(insertionAmount, extractionAmount);
                                            targetStorage.extract(resource, actualTransferAmount, nestedTransaction);
                                            this.inventoryStorage.insert(resource, actualTransferAmount, nestedTransaction);
                                            nestedTransaction.commit();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                transaction.commit();
            }
            this.cooldown = maxCooldown;
        }
    }
}
