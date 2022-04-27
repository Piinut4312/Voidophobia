package net.piinut.voidophobia.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.TDI.ItemTDIBlock;
import net.piinut.voidophobia.block.crackedBedrock.CrackedBedrockBlock;
import net.piinut.voidophobia.block.crackedBedrock.CreativeCrackedBedrockBlock;
import net.piinut.voidophobia.block.crackedBedrock.SlightlyCrackedBedrockBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonExtensionBlock;
import net.piinut.voidophobia.block.piston.ReinforcedPistonHeadBlock;

public class ModBlocks {

    public static final Block SLIGHTLY_CRACKED_BEDROCK = new SlightlyCrackedBedrockBlock(FabricBlockSettings.copy(Blocks.BEDROCK));
    public static final Block GODEL_CRYSTAL_BLOCK = new GodelCrystalBlock(FabricBlockSettings.of(Material.AMETHYST, MapColor.DARK_RED).luminance(7).strength(8.0f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK));
    public static final Block WEAK_ARTIFICIAL_BEDROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(100.0f,100.0f).requiresTool());
    public static final Block BASIC_VUXDUCT = new BasicVuxductBlock(FabricBlockSettings.of(Material.STONE).strength(80.0f, 80.0f).requiresTool());
    public static final Block VUX_LAMP = new VuxLampBlock(FabricBlockSettings.of(Material.METAL).strength(8.0f).requiresTool().luminance((blockState)-> blockState.get(VuxLampBlock.LIT)? 15:0).sounds(BlockSoundGroup.LANTERN));
    public static final Block REINFORCED_PISTON = createReinforcedPistonBlock(false);
    public static final Block REINFORCED_STICKY_PISTON = createReinforcedPistonBlock(true);
    public static final Block REINFORCED_PISTON_HEAD = new ReinforcedPistonHeadBlock(FabricBlockSettings.of(Material.PISTON).strength(7.5f).dropsNothing());
    public static final Block MOVING_REINFORCED_PISTON = new ReinforcedPistonExtensionBlock(FabricBlockSettings.of(Material.PISTON).strength(-1.0f).dynamicBounds().dropsNothing().nonOpaque().solidBlock(ModBlocks::never).suffocates(ModBlocks::never).blockVision(ModBlocks::never));
    public static final Block ETERNAL_FIRE = new AbstractEternalFireBlock(FabricBlockSettings.copy(Blocks.FIRE), 6.0f);
    public static final Block COMPACT_NETHER_PORTAL = new CompactNetherPortalBlock(FabricBlockSettings.of(Material.STONE, MapColor.BLACK).luminance(7).requiresTool().strength(50.0f, 1200.0f));
    public static final Block ITEM_TDI = new ItemTDIBlock(FabricBlockSettings.of(Material.DECORATION).strength(3.0f));
    public static final Block AZURE_SAND = new Block(FabricBlockSettings.of(Material.AGGREGATE).strength(0.8f).sounds(BlockSoundGroup.SAND));
    public static final Block AZURE_SANDSTONE = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0f, 4.0f));
    public static final Block AZURE_SANDSTONE_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0f, 4.0f));
    public static final Block BLOCK_OF_STARDUST = new FallingBlock(FabricBlockSettings.of(Material.AGGREGATE).strength(0.8f).luminance(12).sounds(BlockSoundGroup.SAND));
    public static final Block STARROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0f, 4.0f).luminance(15));
    public static final Block STARROCK_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).strength(4.0f).luminance(15).sounds(BlockSoundGroup.STONE));
    public static final Block CHROME_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f, 3.0f).requiresTool());
    public static final Block DEEPSLATE_CHROME_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(4.5f, 3.0f).sounds(BlockSoundGroup.DEEPSLATE).requiresTool());
    public static final Block RAW_CHROME_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0f, 6.0f).requiresTool());
    public static final Block NICKEL_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(3.0f, 3.0f).sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block DEEPSLATE_NICKEL_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(4.5f, 3.0f).sounds(BlockSoundGroup.DEEPSLATE).requiresTool());
    public static final Block RAW_NICKEL_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0f, 6.0f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final Block ALLOY_FURNACE = new AlloyFurnaceBlock(FabricBlockSettings.of(Material.METAL).strength(6.0f, 8.0f).requiresTool().sounds(BlockSoundGroup.NETHER_BRICKS).luminance((state)-> state.get(AlloyFurnaceBlock.LIT)? 15:0));
    public static final Block STAINLESS_STEEL_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(7.0f, 8.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block VUX_MACHINE_CORE = new Block(FabricBlockSettings.of(Material.METAL).strength(12f,12f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block VUX_FORMING_MACHINE = new VuxFormingMachineBlock(FabricBlockSettings.of(Material.METAL).strength(12.0f, 12.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block LASER_ENGRAVING_MACHINE = new LaserEngravingMachineBlock(FabricBlockSettings.of(Material.METAL).strength(12.0f, 12.0f).requiresTool().sounds(BlockSoundGroup.METAL).luminance((state)->state.get(LaserEngravingMachineBlock.LIT)? 15:0));
    public static final Block VUX_FILTER_MACHINE = new VuxFilterMachineBlock(FabricBlockSettings.of(Material.METAL).strength(12.0f, 12.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block COMPRESSED_NETHER_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(6.0f, 8.0f).sounds(BlockSoundGroup.NETHER_BRICKS));
    public static final Block CREATIVE_CRACKED_BEDROCK = new CreativeCrackedBedrockBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK));
    public static final Block VACUUM_COATER = new VacuumCoaterBlock(FabricBlockSettings.of(Material.METAL).strength(15.0f, 15.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block STRONG_ARTIFICIAL_BEDROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(200.0f, 200.0f).requiresTool().sounds(BlockSoundGroup.STONE));
    public static final Block SILVER_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(4.0f, 3.0f).sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block DEEPSLATE_SILVER_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.5f, 3.0f).sounds(BlockSoundGroup.DEEPSLATE).requiresTool());
    public static final Block CRACKED_BEDROCK = new CrackedBedrockBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK));
    public static final Block NICKEL_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 6.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block CHROME_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 6.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block SILVER_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f, 6.0f).requiresTool().sounds(BlockSoundGroup.METAL));
    public static final Block REINFORCED_GLASS = new ReinforcedGlassBlock(FabricBlockSettings.of(Material.GLASS).strength(3.0f, 24.0f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(ModBlocks::never).solidBlock(ModBlocks::never).blockVision(ModBlocks::never));
    public static final Block BLAST_CHAMBER = new BlastChamberBlock(FabricBlockSettings.of(Material.METAL).strength(15.0f, 48.0f).sounds(BlockSoundGroup.METAL).requiresTool());
    public static final Block LASER_TRANSMITTER = new LaserTransmitterBlock(FabricBlockSettings.of(Material.METAL).strength(12.0f, 12.0f).sounds(BlockSoundGroup.METAL).requiresTool());
    public static final Block END_SAND = new Block(FabricBlockSettings.of(Material.AGGREGATE).strength(1.0f).sounds(BlockSoundGroup.SAND));
    public static final Block PRECISION_VUX_MACHINE_CORE = new Block(FabricBlockSettings.of(Material.METAL).strength(16f, 16f).requiresTool().sounds(BlockSoundGroup.METAL));
    private static Block createReinforcedPistonBlock(boolean sticky) {
        AbstractBlock.ContextPredicate contextPredicate = (state, world, pos) -> !state.get(ReinforcedPistonBlock.EXTENDED);
        return new ReinforcedPistonBlock(sticky, FabricBlockSettings.of(Material.PISTON).strength(7.5f).solidBlock(ModBlocks::never).suffocates(contextPredicate).blockVision(contextPredicate));
    }

    private static boolean never(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
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
        register(AZURE_SAND, "azure_sand");
        register(BLOCK_OF_STARDUST, "block_of_stardust");
        register(STARROCK_BRICKS, "starrock_bricks");
        register(CHROME_ORE, "chrome_ore");
        register(DEEPSLATE_CHROME_ORE, "deepslate_chrome_ore");
        register(RAW_CHROME_BLOCK, "raw_chrome_block");
        register(STARROCK, "starrock");
        register(AZURE_SANDSTONE, "azure_sandstone");
        register(AZURE_SANDSTONE_BRICKS, "azure_sandstone_bricks");
        register(NICKEL_ORE, "nickel_ore");
        register(DEEPSLATE_NICKEL_ORE, "deepslate_nickel_ore");
        register(RAW_NICKEL_BLOCK, "raw_nickel_block");
        register(ALLOY_FURNACE, "alloy_furnace");
        register(STAINLESS_STEEL_BLOCK, "stainless_steel_block");
        register(VUX_FORMING_MACHINE, "vux_forming_machine");
        register(VUX_MACHINE_CORE, "vux_machine_core");
        register(LASER_ENGRAVING_MACHINE, "laser_engraving_machine");
        register(VUX_FILTER_MACHINE, "vux_filter_machine");
        register(COMPRESSED_NETHER_BRICKS, "compressed_nether_bricks");
        register(CREATIVE_CRACKED_BEDROCK, "creative_cracked_bedrock");
        register(VACUUM_COATER, "vacuum_coater");
        register(STRONG_ARTIFICIAL_BEDROCK, "strong_artificial_bedrock");
        register(SILVER_ORE, "silver_ore");
        register(DEEPSLATE_SILVER_ORE, "deepslate_silver_ore");
        register(CRACKED_BEDROCK, "cracked_bedrock");
        register(NICKEL_BLOCK, "nickel_block");
        register(CHROME_BLOCK, "chrome_block");
        register(SILVER_BLOCK, "silver_block");
        register(REINFORCED_GLASS, "reinforced_glass");
        register(BLAST_CHAMBER, "blast_chamber");
        register(LASER_TRANSMITTER, "laser_transmitter");
        register(END_SAND, "end_sand");
        register(PRECISION_VUX_MACHINE_CORE, "precision_vux_machine_core");
    }

}
