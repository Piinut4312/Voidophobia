package net.piinut.voidophobia.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.TDI.ItemTDIBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonExtensionBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonHeadBlock;

public class ModBlocks {

    public static final Block SLIGHTLY_CRACKED_BEDROCK = new SlightlyCrackedBedrockBlock(FabricBlockSettings.copy(Blocks.BEDROCK));
    public static final Block GODEL_CRYSTAL_BLOCK = new GodelCrystalBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.DARK_RED).luminance(7).strength(10.0f).requiresTool());
    public static final Block WEAK_ARTIFICIAL_BEDROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(100.0f,100.0f).requiresTool());
    public static final Block BASIC_VUXDUCT = new BasicVuxductBlock(FabricBlockSettings.of(Material.STONE).strength(80.0f, 80.0f).requiresTool());
    public static final Block VUX_LAMP = new VuxLampBlock(FabricBlockSettings.of(Material.METAL).strength(8.0f).requiresTool().luminance((blockState)-> blockState.get(VuxLampBlock.LIT)? 15:0));
    public static final Block REINFORCED_PISTON = createReinforcedPistonBlock(false);
    public static final Block REINFORCED_STICKY_PISTON = createReinforcedPistonBlock(true);
    public static final Block REINFORCED_PISTON_HEAD = new ReinforcedPistonHeadBlock(FabricBlockSettings.of(Material.PISTON).strength(7.5f).dropsNothing());
    public static final Block MOVING_REINFORCED_PISTON = new ReinforcedPistonExtensionBlock(FabricBlockSettings.of(Material.PISTON).strength(-1.0f).dynamicBounds().dropsNothing().nonOpaque().solidBlock(ModBlocks::never).suffocates(ModBlocks::never).blockVision(ModBlocks::never));
    public static final Block ETERNAL_FIRE = new AbstractEternalFireBlock(FabricBlockSettings.copy(Blocks.FIRE), 6.0f);
    public static final Block COMPACT_NETHER_PORTAL = new CompactNetherPortalBlock(FabricBlockSettings.of(Material.STONE, MapColor.BLACK).luminance(7).requiresTool().strength(50.0f, 1200.0f));
    public static final Block ITEM_TDI = new ItemTDIBlock(FabricBlockSettings.of(Material.DECORATION).strength(3.0f));

    private static Block createReinforcedPistonBlock(boolean sticky) {
        AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !state.get(ReinforcedPistonBlock.EXTENDED);
        return new ReinforcedPistonBlock(sticky, FabricBlockSettings.of(Material.PISTON).strength(7.5f).solidBlock(ModBlocks::never).suffocates(contextPredicate).blockVision(contextPredicate));
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    private static void register(Block block, String id){
        Registry.register(Registry.BLOCK, new Identifier(Voidophobia.MODID, id), block);
    }

    public static void registerAll(){
        register(SLIGHTLY_CRACKED_BEDROCK, "slightly_cracked_bedrock");
        register(GODEL_CRYSTAL_BLOCK, "godel_crystal_block");
        register(WEAK_ARTIFICIAL_BEDROCK, "weak_artificial_bedrock");
        register(BASIC_VUXDUCT, "basic_vuxduct");
        register(VUX_LAMP, "vux_lamp");
        register(REINFORCED_PISTON, "reinforced_piston");
        register(REINFORCED_STICKY_PISTON, "reinforced_sticky_piston");
        register(REINFORCED_PISTON_HEAD, "reinforced_piston_head");
        register(MOVING_REINFORCED_PISTON, "moving_reinforced_piston");
        register(ETERNAL_FIRE, "eternal_fire");
        register(COMPACT_NETHER_PORTAL, "compact_nether_portal");
        register(ITEM_TDI, "item_tdi");
    }

}
