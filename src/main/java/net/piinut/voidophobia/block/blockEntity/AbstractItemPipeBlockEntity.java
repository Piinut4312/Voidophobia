package net.piinut.voidophobia.block.blockEntity;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;
import net.piinut.voidophobia.block.blockEntity.itemPipe.ItemPackage;
import net.piinut.voidophobia.block.blockEntity.itemPipe.ItemPipeNetwork;
import net.piinut.voidophobia.item.ModItems;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractItemPipeBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private final int maxCooldown;  //How long a pipe has to wait before transfer items
    private final int batchSize;    //How many items can a pipe transfer each time
    public final SimpleInventory inventory;
    public final SimpleInventory socketInventory;
    public final InventoryStorage inventoryStorage;
    public ItemPipeNetwork network;
    public final List<ItemPackage> itemPackages;
    private final Direction[] pluginDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};

    public AbstractItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCooldown, int bufferSize, int batchSize) {
        super(type, pos, state);
        this.maxCooldown = maxCooldown;
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
        this.itemPackages = new ArrayList<>();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtCompound.COMPOUND_TYPE));

        NbtList nbtList = nbt.getList("SocketInventory", NbtCompound.COMPOUND_TYPE);
        for (int slot = 0; slot < nbtList.size(); ++slot) {
            ItemStack stack = ItemStack.fromNbt(nbtList.getCompound(slot));
            this.socketInventory.setStack(slot, stack);
        }

        this.readItemPackages(nbt.getList("ItemPackages", NbtCompound.LIST_TYPE));
    }

    private void readItemPackages(NbtList nbtList) {
        this.itemPackages.clear();
        for(int i = 0; i < nbtList.size(); i++){
            NbtCompound compound = nbtList.getCompound(i);
            int cooldown = compound.getInt("Cooldown");
            ItemStack itemStack = ItemStack.fromNbt(compound);
            NbtCompound posCompound = compound.getCompound("DestinationPos");
            BlockPos destinationPos = NbtHelper.toBlockPos(posCompound);
            this.itemPackages.add(new ItemPackage(itemStack, destinationPos, cooldown));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        NbtList nbtList = new NbtList();
        for (int slot = 0; slot < this.socketInventory.size(); ++slot) {
            ItemStack stack = this.socketInventory.getStack(slot);
            nbtList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("SocketInventory", nbtList);
        nbt.put("ItemPackages", this.getItemPackagesAsNbtList());
    }

    private NbtList getItemPackagesAsNbtList() {
        NbtList nbtList = new NbtList();
        for (ItemPackage itemPackage : this.itemPackages) {
            NbtCompound nbtCompound = itemPackage.getItemStack().writeNbt(new NbtCompound());
            nbtCompound.putInt("Cooldown", itemPackage.getCooldown());
            NbtCompound posCompound = NbtHelper.fromBlockPos(itemPackage.getDestinationPos());
            nbtCompound.put("DestinationPos", posCompound);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }

    protected void serverTick(World world, BlockPos blockPos, BlockState blockState) {

        BlockState state = blockState;

        for(int i = 0; i < 6; i++){
            if(this.socketInventory.getStack(i).getItem() == ModItems.EXTRACTION_SOCKET){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), ItemPipeNodeType.EXTRACT);
            }else if(this.socketInventory.getStack(i).getItem() == ModItems.INSERTION_SOCKET){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), ItemPipeNodeType.INSERT);
            }else if(this.socketInventory.getStack(i).isEmpty() && AbstractItemPipeBlock.getNodeTypeForDirection(blockState, pluginDirs[i]) != ItemPipeNodeType.TRANSFER){
                state = state.with(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(pluginDirs[i]), AbstractItemPipeBlock.getConnectionType(world.getBlockState(blockPos.offset(pluginDirs[i]))));
            }
        }

        world.setBlockState(blockPos, state);

        this.network.updateNetwork(world);
        this.tickingItemPackages();
        List<ItemPackage> readyPackages = this.getReadyPackages();
        if(!readyPackages.isEmpty()){
            for(ItemPackage readyPackage : readyPackages){
                if(readyPackage.getDestinationPos() != null && readyPackage.getDestinationPos().equals(blockPos) && this.network.isValidInsertionNode(world, blockPos)){
                    //Item package has arrived to its desired destination. Try to insert it to nearby inventories
                    for(Direction direction : Direction.values()){
                        if(AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.INSERT){
                            Storage<ItemVariant> externalStorage = ItemStorage.SIDED.find(world, blockPos.offset(direction), direction.getOpposite());
                            if(externalStorage != null){
                                ItemVariant resource = ItemVariant.of(readyPackage.getItemStack());
                                try(Transaction transaction = Transaction.openOuter()){
                                    long extractionAmount = readyPackage.getItemStack().getCount();
                                    long insertAmount = externalStorage.insert(resource, extractionAmount, transaction);
                                    this.inventoryStorage.extract(resource, insertAmount, transaction);
                                    if(insertAmount == extractionAmount){
                                        this.itemPackages.remove(readyPackage);
                                    }
                                    if(insertAmount < extractionAmount){
                                        readyPackage.getItemStack().decrement((int) insertAmount);
                                    }
                                    transaction.commit();
                                    break;
                                }
                            }
                        }
                    }
                }else{
                    //Item Package hasn't reached its final destination. Try to transfer it to neighbor pipe.
                    Direction direction = this.network.getDirectionForPackage(readyPackage.getDestinationPos());
                    if(direction != null){
                        if(AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.TRANSFER){
                            Storage<ItemVariant> nextPipe = ItemStorage.SIDED.find(world, blockPos.offset(direction), direction.getOpposite());
                            if(nextPipe != null){
                                AbstractItemPipeBlockEntity nextBlockEntity = (AbstractItemPipeBlockEntity) world.getBlockEntity(blockPos.offset(direction));
                                ItemVariant resource = ItemVariant.of(readyPackage.getItemStack());
                                try(Transaction transaction = Transaction.openOuter()){
                                    long extractionAmount = readyPackage.getItemStack().getCount();
                                    long insertAmount = nextPipe.insert(resource, extractionAmount, transaction);
                                    this.inventoryStorage.extract(resource, insertAmount, transaction);
                                    if(insertAmount > 0){
                                        nextBlockEntity.itemPackages.add(this.createItemPackage(resource, insertAmount, readyPackage.getDestinationPos()));
                                    }
                                    if(insertAmount == extractionAmount){
                                        this.itemPackages.remove(readyPackage);
                                    }
                                    if(insertAmount < extractionAmount){
                                        readyPackage.getItemStack().decrement((int) insertAmount);
                                    }

                                    transaction.commit();
                                }
                            }
                        }
                    }else{
                        try(Transaction transaction = Transaction.openOuter()){
                            readyPackage.setDestinationPos(this.network.getDestinationPos(world, ItemVariant.of(readyPackage.getItemStack()),readyPackage.getItemStack().getCount(),  transaction));
                        }
                    }
                }
            }
        }else if(!this.inventory.isEmpty() && this.itemPackages.isEmpty()){
            for(int i = 0; i < this.inventory.size(); i++){
                ItemStack itemStack = this.inventory.getStack(i);
                if(!itemStack.isEmpty()){
                    try(Transaction transaction = Transaction.openOuter()){
                        ItemVariant resource = ItemVariant.of(itemStack);
                        BlockPos destPos = this.network.getDestinationPos(world, resource, itemStack.getCount(), transaction);
                        ItemPackage itemPackage = this.createItemPackage(resource, itemStack.getCount(), destPos);
                        this.itemPackages.add(itemPackage);
                    }
                }
            }
        }

        if(this.network.isValidExtractionNode(world, blockPos)){
            for(Direction direction : Direction.values()){
                if(AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.EXTRACT){
                    try(Transaction transaction = Transaction.openOuter()){
                        Storage<ItemVariant> externalStorage = ItemStorage.SIDED.find(world, blockPos.offset(direction), direction.getOpposite());
                        if(externalStorage != null){
                            for(StorageView<ItemVariant> storageView : externalStorage.iterable(transaction)){
                                if(storageView.isResourceBlank()){
                                    continue;
                                }
                                ItemVariant resource = storageView.getResource();
                                long extractionAmount = externalStorage.simulateExtract(resource, this.batchSize, transaction);
                                long insertionAmount = this.inventoryStorage.insert(resource, extractionAmount, transaction);
                                if(insertionAmount > 0){
                                    externalStorage.extract(resource, insertionAmount, transaction);
                                    BlockPos destPos = this.network.getDestinationPos(world, resource, insertionAmount, transaction);
                                    if(destPos != null){
                                        ItemPackage itemPackage = this.createItemPackage(resource, insertionAmount, destPos);
                                        this.itemPackages.add(itemPackage);
                                        transaction.commit();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.markDirty();
    }



    private ItemPackage createItemPackage(ItemVariant resource, long amount, BlockPos destPos){
        return new ItemPackage(resource.toStack((int) amount), destPos, this.maxCooldown);
    }

    private List<ItemPackage> getReadyPackages() {
        List<ItemPackage> packages = new ArrayList<>();
        for(ItemPackage itemPackage : this.itemPackages){
            if(itemPackage.getCooldown() <= 0){
                packages.add(itemPackage);
            }
        }
        return packages;
    }

    private void tickingItemPackages() {
        for(ItemPackage itemPackage : this.itemPackages){
            itemPackage.updateCooldown();
        }
    }
}
