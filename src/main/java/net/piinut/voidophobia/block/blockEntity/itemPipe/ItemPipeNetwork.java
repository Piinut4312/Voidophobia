package net.piinut.voidophobia.block.blockEntity.itemPipe;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;

import java.util.*;


public class ItemPipeNetwork {

    public ItemPipeNode localNode;
    public List<ItemPipeNode> nodes;

    public ItemPipeNetwork(BlockPos root){
        this.localNode = new ItemPipeNode(root, null, true);
        this.nodes = new ArrayList<>();
    }

    public void updateNetwork(World world){
        if(isValidNode(world, localNode.pos)){
            bfs(world);
        }
    }

    private void bfs(World world){
        Queue<ItemPipeNode> unvisitedNodes = new LinkedList<>();
        Queue<ItemPipeNode> visitedNodes = new LinkedList<>();
        ItemPipeNode rootNode = localNode;
        unvisitedNodes.offer(rootNode);
        nodes.clear();
        while(!unvisitedNodes.isEmpty()){
            ItemPipeNode node = unvisitedNodes.poll();
            if(visitedNodes.contains(node)){
                continue;
            }
            visitedNodes.offer(node);
            nodes.add(node);
            for(Direction direction : Direction.values()){
                BlockPos blockPos = node.pos;
                BlockPos neighborPos = blockPos.offset(direction);
                if(canConnectTo(world, blockPos, direction, neighborPos)){
                    ItemPipeNode childNode = new ItemPipeNode(neighborPos, node, false);
                    node.childrenNodes.add(childNode);
                    unvisitedNodes.offer(childNode);
                }
            }
        }
    }

    private boolean canConnectTo(World world, BlockPos pos, Direction direction, BlockPos neighborPos){
        BlockState blockState = world.getBlockState(pos);
        BlockState neighborState = world.getBlockState(neighborPos);
        if(!isValidNode(world, pos) || !isValidNode(world, neighborPos)){
            return false;
        }
        return AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.TRANSFER
                && AbstractItemPipeBlock.getNodeTypeForDirection(neighborState, direction.getOpposite()) == ItemPipeNodeType.TRANSFER;
    }

    public boolean isValidNode(World world, BlockPos pos){
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() instanceof AbstractItemPipeBlock;
    }

    public Pair<Storage<ItemVariant>, ItemVariant> getValidExtractionNode(World world, BlockPos pos, Storage<ItemVariant> itemStorage){
        BlockState blockState = world.getBlockState(pos);
        if(isValidNode(world, pos)){
            for(Direction direction : Direction.values()){
                if(AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.EXTRACT){
                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
                    if(storage != null){
                        try(Transaction transaction = Transaction.openOuter()){
                            for(StorageView<ItemVariant> storageView : storage.iterable(transaction)){
                                if(!storageView.isResourceBlank()){
                                    ItemVariant resource = storageView.getResource();
                                    long tryInsertAmount = itemStorage.simulateInsert(resource, 1, transaction);
                                    if(tryInsertAmount > 0){
                                        return new Pair<>(storage, resource);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Storage<ItemVariant> getValidInsertionNode(World world, BlockPos pos, ItemVariant resource){
        BlockState blockState = world.getBlockState(pos);
        if(isValidNode(world, pos)){
            for(Direction direction : Direction.values()){
                if(AbstractItemPipeBlock.getNodeTypeForDirection(blockState, direction) == ItemPipeNodeType.INSERT){
                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
                    if(storage != null){
                        long tryInsertAmount = storage.simulateInsert(resource, 1, null);
                        if(tryInsertAmount > 0){
                            return storage;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Storage<ItemVariant> findValidTransferPath(World world, ItemVariant resource){
        if(!resource.isBlank()){
            Queue<ItemPipeNode> unvisitedNodes = new LinkedList<>();
            Queue<ItemPipeNode> visitedNodes = new LinkedList<>();
            ItemPipeNode rootNode = localNode;
            unvisitedNodes.offer(rootNode);
            while(!unvisitedNodes.isEmpty()){
                ItemPipeNode node = unvisitedNodes.poll();
                if(visitedNodes.contains(node)){
                    continue;
                }
                visitedNodes.offer(node);
                for(ItemPipeNode childNode : node.childrenNodes){
                    BlockPos blockPos = childNode.pos;
                    Storage<ItemVariant> storage = getValidInsertionNode(world, blockPos, resource);
                    if(storage != null){
                        ItemPipeNode targetNode = childNode.copy();
                        ItemPipeNode lastNode = targetNode.copy();
                        while(!targetNode.isRoot){
                            lastNode = targetNode.copy();
                            targetNode = targetNode.parentNode;
                        }
                        return ItemStorage.SIDED.find(world, lastNode.pos, Direction.fromVector(rootNode.pos.subtract(lastNode.pos)));
                    }
                    unvisitedNodes.offer(childNode);
                }
            }
        }
        return null;
    }

}
