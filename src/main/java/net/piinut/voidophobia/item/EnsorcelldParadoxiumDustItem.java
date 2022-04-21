package net.piinut.voidophobia.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.entity.ModEntities;
import net.piinut.voidophobia.mixin.accessor.MobSpawnEntryAccessor;

import java.util.Objects;

public class EnsorcelldParadoxiumDustItem extends Item {

    public EnsorcelldParadoxiumDustItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity blockEntity;
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SPAWNER) && (blockEntity = world.getBlockEntity(blockPos)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
            MobSpawnerEntry mobSpawnerEntry = ((MobSpawnEntryAccessor)mobSpawnerLogic).getSpawnerEntry();
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(mobSpawnerEntry.getNbt().getString("id")));
            if(entityType == EntityType.SPIDER || entityType == EntityType.CAVE_SPIDER){
                mobSpawnerLogic.setEntityId(ModEntities.ABYSS_SPIDER);
                blockEntity.markDirty();
                world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                itemStack.decrement(1);
            }else if(entityType == EntityType.ZOMBIE){
                mobSpawnerLogic.setEntityId(ModEntities.ENSORCELLED);
                blockEntity.markDirty();
                world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                itemStack.decrement(1);
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.CONSUME;
    }
}
