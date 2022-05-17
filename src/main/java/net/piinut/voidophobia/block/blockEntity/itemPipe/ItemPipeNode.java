package net.piinut.voidophobia.block.blockEntity.itemPipe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class ItemPipeNode {

    public BlockPos pos;
    public boolean isRoot;
    public List<ItemPipeNode> childrenNodes;
    public ItemPipeNode parentNode;

    public ItemPipeNode(BlockPos pos, ItemPipeNode parentNode, boolean isRoot){
        this.pos = pos;
        this.parentNode = parentNode;
        this.isRoot = isRoot;
        this.childrenNodes = new ArrayList<>();
    }

    public static boolean matches(ItemPipeNode node, BlockPos pos){
        if(node == null || pos == null){
            return false;
        }
        return node.pos.getX() == pos.getX() && node.pos.getY() == pos.getY() && node.pos.getZ() == pos.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemPipeNode itemPipeNode)){
            return false;
        }
        return matches(itemPipeNode, this.pos);
    }

    public ItemPipeNode copy() {
        return new ItemPipeNode(this.pos, this.parentNode, this.isRoot);
    }
}
