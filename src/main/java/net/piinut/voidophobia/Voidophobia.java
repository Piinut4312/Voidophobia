package net.piinut.voidophobia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Voidophobia implements ModInitializer {

    public static final String MODID = "voidophobia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final ItemGroup VOIDOPHOBIA_DEFAULT_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MODID, "default"),
            () -> new ItemStack(ModBlocks.SLIGHTLY_CRACKED_BEDROCK));

    @Override
    public void onInitialize() {
        LOGGER.info("Voidophobia initialized.");
        ModBlocks.registerAll();
        ModItems.registerAll();
    }
}
