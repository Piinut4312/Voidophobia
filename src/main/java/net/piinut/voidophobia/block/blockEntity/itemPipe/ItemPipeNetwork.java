package net.piinut.voidophobia.block.blockEntity.itemPipe;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.AbstractItemPipeBlock;
import net.piinut.voidophobia.block.ItemPipeNodeType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ItemPipeNetwork {

    public ItemPipeNode localNode;
    public List<ItemPipeNode> nodes;

    public ItemPipeNetwork(BlockPos root){
        this.localNode = new ItemPipeNode(root, Direction.NORTH, true);
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
                    ItemPipeNode childNode = new ItemPipeNode(neighborPos, direction.getOpposite(), false);
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

    public boolean isValidExtractionNode(World world, BlockPos pos){
        BlockState blockState = world.getBlockState(pos);
        if(isValidNode(world, pos)){
            for(Direction direction : Direction.values()){
                if(blockState.get(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(direction)) == ItemPipeNodeType.EXTRACT){
                    return ItemStorage.SIDED.find(world, pos, direction.getOpposite()) != null;
                }
            }
        }
        return false;
    }

    public boolean isValidInsertionNode(World world, BlockPos pos){
        BlockState blockState = world.getBlockState(pos);
        if(isValidNode(world, pos)){
            for(Direction direction : Direction.values()){
                if(blockState.get(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(direction)) == ItemPipeNodeType.INSERT){
                    return ItemStorage.SIDED.find(world, pos, direction.getOpposite()) != null;
                }
            }
        }
        return false;
    }

    public boolean isValidInsertionNodeAsDestination(World world, BlockPos pos, ItemVariant resource, long amount, Transaction transaction){
        if(!isValidInsertionNode(world, pos)){
            return false;
        }
        BlockState blockState = world.getBlockState(pos);
        if(isValidNode(world, pos)){
            for(Direction direction : Direction.values()){
                if(blockState.get(AbstractItemPipeBlock.DIRECTION_ENUM_PROPERTY_MAP.get(direction)) == ItemPipeNodeType.INSERT){
                    Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(world, pos, direction.getOpposite());
                    try(Transaction nestedTransaction = Transaction.openNested(transaction)){
                        long insertionAmount = itemStorage.simulateInsert(resource, amount, nestedTransaction);
                        if(insertionAmount > 0){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public Direction getDirectionForPackage(BlockPos targetPos) {
        ItemPipeNode targetNode = this.findNodeFromPos(targetPos);
        if(targetNode == null){
            return null;
        }
        Direction lastDirection = null;
        int count = this.nodes.size();
        while(!ItemPipeNode.matches(targetNode, localNode.pos) && !targetNode.isRoot && count > 0){
            lastDirection = targetNode.predecessor;
            BlockPos predecessorPos = targetNode.pos.offset(lastDirection);
            targetNode = findNodeFromPos(predecessorPos);
            if(targetNode == null){
                break;
            }
            count--;
        }
        if(!ItemPipeNode.matches(targetNode, localNode.pos) || lastDirection == null){
            return null;
        }
        return lastDirection.getOpposite();
    }


    private ItemPipeNode findNodeFromPos(BlockPos targetPos) {
        for(ItemPipeNode node : nodes){
            if(ItemPipeNode.matches(node, targetPos)){
                return node;
            }
        }
        return null;
    }



    public BlockPos getDestinationPos(World world, ItemVariant resource, long amount, Transaction transaction) {
        for(ItemPipeNode node : nodes){
            if(isValidInsertionNodeAsDestination(world, node.pos, resource, amount, transaction)){
                return node.pos;
            }
        }
        return null;
    }
}
