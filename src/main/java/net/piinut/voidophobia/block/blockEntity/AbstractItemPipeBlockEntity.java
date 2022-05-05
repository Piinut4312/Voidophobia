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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;
import net.piinut.voidophobia.block.blockEntity.itemPipe.ItemPackage;
import net.piinut.voidophobia.block.blockEntity.itemPipe.ItemPipeNetwork;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractItemPipeBlockEntity extends BlockEntity{

    private final int maxCooldown;  //How long a pipe has to wait before transfer items
    private final int batchSize;    //How many items can a pipe transfer each time
    private final int bufferSize;
    public final SimpleInventory inventory;
    public final InventoryStorage inventoryStorage;
    public ItemPipeNetwork network;
    public final List<ItemPackage> itemPackages;


    public AbstractItemPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxCooldown, int bufferSize, int batchSize) {
        super(type, pos, state);
        this.maxCooldown = maxCooldown;
        //Size of inventory
        this.batchSize = batchSize;
        this.bufferSize = bufferSize;
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
        this.inventoryStorage = InventoryStorage.of(inventory, null);
        this.network = new ItemPipeNetwork(pos);
        this.itemPackages = new ArrayList<>();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", NbtCompound.LIST_TYPE));
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
        this.network.updateNetwork(world);
        this.tickingItemPackages();
        List<ItemPackage> readyPackages = this.getReadyPackages();
        if(!readyPackages.isEmpty()){
            for(ItemPackage readyPackage : readyPackages){
                if(readyPackage.getDestinationPos().equals(blockPos) && this.network.isValidInsertionNode(world, blockPos)){
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
                                        nextBlockEntity.itemPackages.add(new ItemPackage(resource.toStack((int) insertAmount), readyPackage.getDestinationPos(), this.maxCooldown));
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
                                    BlockPos destPos = this.network.getDestinationPos(world, resource, insertionAmount);
                                    if(destPos != null){
                                        ItemPackage itemPackage = new ItemPackage(resource.toStack((int) insertionAmount), destPos, this.maxCooldown);
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
