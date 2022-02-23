package net.piinut.voidophobia.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;

public class ModBlocks {

    public static final Block SLIGHTLY_CRACKED_BEDROCK = new SlightlyCrackedBedrockBlock(FabricBlockSettings.copy(Blocks.BEDROCK));
    public static final Block GODEL_CRYSTAL_BLOCK = new GodelCrystalBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.DARK_RED).luminance(7).strength(10.0f).requiresTool());

    private static void register(Block block, String id){
        Registry.register(Registry.BLOCK, new Identifier(Voidophobia.MODID, id), block);
    }

    public static void registerAll(){
        register(SLIGHTLY_CRACKED_BEDROCK, "slightly_cracked_bedrock");
        register(GODEL_CRYSTAL_BLOCK, "godel_crystal_block");
    }

}
