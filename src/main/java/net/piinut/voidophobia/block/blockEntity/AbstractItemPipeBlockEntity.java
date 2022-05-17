package net.piinut.voidophobia.block.blockEntity;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;
import net.piinut.voidophobia.block.blockEntity.itemPipe.ItemPipeNetwork;
import net.piinut.voidophobia.item.ModItems;


public abstract class AbstractItemPipeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private final int maxCooldown;  //How long a pipe has to wait before transfer items
    private final int batchSize;    //How many items can a pipe transfer each time
    public final SimpleInventory inventory;
    public final SimpleInventory socketInventory;
    public final InventoryStorage inventoryStorage;
    public ItemPipeNetwork network;
    private int cooldown;
    private final Direction[] pluginDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

    public AbstractItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCooldown, int bufferSize, int batchSize) {
        super(type, pos, state);
        this.maxCooldown = maxCooldown;
        this.cooldown = 0;
        //Size of inventory
        this.batchSize = batchSize;
        this.inventory = new SimpleInventory(bufferSize){

            @Override
            public int getMaxCountPerStack() {
                return AbstractItemPipeBlockEntity.this.batchSize;
            }

            @Override
            public void markDirty() {
                AbstractItemPipeBlockEntity.this.markDirty();
            }
        };
        this.socketInventory = new SimpleInventory(6){

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public void markDirty() {
                AbstractItemPipeBlockEntity.this.markDirty();
            }
        };
        this.inventoryStorage = InventoryStorage.of(inventory, null);
        this.network = new ItemPipeNetwork(pos);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt("Cooldown");
        this.inventory.readNbtList(nbt.getList("Inventory", NbtCompound.COMPOUND_TYPE));
        NbtList nbtList = nbt.getList("SocketInventory", NbtCompound.COMPOUND_TYPE);
        for (int slot = 0; slot < nbtList.size(); ++slot) {
            ItemStack stack = ItemStack.fromNbt(nbtList.getCompound(slot));
            this.socketInventory.setStack(slot, stack);
        }

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Cooldown", this.cooldown);
        nbt.put("Inventory", this.inventory.toNbtList());
        NbtList nbtList = new NbtList();
        for (int slot = 0; slot < this.socketInventory.size(); ++slot) {
            ItemStack stack = this.socketInventory.getStack(slot);
            nbtList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("SocketInventory", nbtList);
    }


    protected void serverTick(World world, BlockPos blockPos, BlockState blockState) {

        BlockState state = blockState;

        for(int i = 0; i < 6; i++){
            if(this.socketInventory.getStack(i).getItem() == ModItems.EXTRACTION_SOCKET){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), ItemPipeNodeType.EXTRACT);
            }else if(this.socketInventory.getStack(i).getItem() == ModItems.INSERTION_SOCKET){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), ItemPipeNodeType.INSERT);
            }else if(this.socketInventory.getStack(i).isEmpty() && AbstractItemPipeBlock.getNodeTypeForDirection(blockState, pluginDirs[i]) != ItemPipeNodeType.TRANSFER){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), AbstractItemPipeBlock.getConnectionType(blockState, world.getBlockState(blockPos.offset(pluginDirs[i])), pluginDirs[i]));
            }
        }

        if(this.cooldown > 0){
            this.cooldown--;
        }else{
            this.network.updateNetwork(world);

            boolean bl = false;
            boolean bl2 = false;

            for(StorageView<ItemVariant> storage : this.inventoryStorage.getSlots()){
                if(!storage.isResourceBlank()){
                    ItemVariant itemVariant = storage.getResource();
                    bl = this.tryInsertItem(world, blockPos, itemVariant, storage.getAmount());
                    bl2 = this.tryTransferItem(world, blockPos, itemVariant, storage.getAmount());
                }
                if(bl || bl2){
                    break;
                }
            }

            this.tryExtractItem(world, blockPos);
            this.cooldown = maxCooldown;
        }

        world.setBlockState(blockPos, state);

        this.markDirty();
    }

    private boolean tryTransferItem(World world, BlockPos blockPos, ItemVariant resource, long count) {
        Storage<ItemVariant> storage = this.network.findValidTransferPath(world, resource);
        if(storage != null){
            try(Transaction transaction = Transaction.openOuter()){
                long insertedAmount = storage.insert(resource, count, transaction);
                this.inventoryStorage.extract(resource, insertedAmount, transaction);
                transaction.commit();
                return true;
            }
        }
        return false;
    }

    private void tryExtractItem(World world, BlockPos blockPos) {
        Pair<Storage<ItemVariant>, ItemVariant> context = this.network.getValidExtractionNode(world, blockPos, this.inventoryStorage);
        if(context != null){
            Storage<ItemVariant> storage = context.getLeft();
            ItemVariant resource = context.getRight();
            if(!resource.isBlank()){
                try(Transaction transaction = Transaction.openOuter()){
                    long extractionAmount = this.inventoryStorage.insert(resource, this.batchSize, transaction);
                    storage.extract(resource, extractionAmount, transaction);
                    transaction.commit();
                }
            }
        }
    }

    private boolean tryInsertItem(World world, BlockPos blockPos, ItemVariant resource, long count) {
        Storage<ItemVariant> storage = this.network.getValidInsertionNode(world, blockPos, resource);
        if(storage != null){
            try(Transaction transaction = Transaction.openOuter()){
                long insertedAmount = storage.insert(resource, count, transaction);
                this.inventoryStorage.extract(resource, insertedAmount, transaction);
                transaction.commit();
                return true;
            }
        }
        return false;
    }
}
