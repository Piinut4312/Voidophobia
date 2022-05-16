package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.handler.BasicItemPipeScreenHandler;
import org.jetbrains.annotations.Nullable;

public class BasicItemPipeBlockEntity extends AbstractItemPipeBlockEntity{

    public static final int MAX_COOLDOWN = 50;
    public static final int BUFFER_SIZE = 5;
    public static final int BATCH_SIZE = 1;

    public BasicItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_ITEM_PIPE, pos, state, MAX_COOLDOWN, BUFFER_SIZE, BATCH_SIZE);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, BasicItemPipeBlockEntity blockEntity) {
        blockEntity.serverTick(world, blockPos, blockState);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.voidophobia.basic_item_pipe");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BasicItemPipeScreenHandler(syncId, this.socketInventory, inv);
    }
}
