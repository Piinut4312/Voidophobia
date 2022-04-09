package net.piinut.voidophobia.item;

import com.sun.jna.platform.win32.WinNT;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.ModBlocks;
import org.lwjgl.system.CallbackI;

public class ModItems {

    public static final ItemGroup VOIDOPHOBIA_DEFAULT_GROUP = FabricItemGroupBuilder.build(
            new Identifier(Voidophobia.MODID, "default"),
            () -> new ItemStack(ModBlocks.SLIGHTLY_CRACKED_BEDROCK));

    public static final BlockItem SLIGHTLY_CRACKED_BEDROCK_ITEM = new BlockItem(ModBlocks.SLIGHTLY_CRACKED_BEDROCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem GODEL_CRYSTAL_ITEM = new BlockItem(ModBlocks.GODEL_CRYSTAL_BLOCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem WEAK_ARTIFICIAL_BEDROCK_ITEM = new BlockItem(ModBlocks.WEAK_ARTIFICIAL_BEDROCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem BASIC_VUXDUCT_ITEM = new BlockItem(ModBlocks.BASIC_VUXDUCT, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VUX_LAMP_ITEM = new BlockItem(ModBlocks.VUX_LAMP, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem ETERNAL_FIRE_ITEM = new BlockItem(ModBlocks.ETERNAL_FIRE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem REINFORCED_PISTON_ITEM = new BlockItem(ModBlocks.REINFORCED_PISTON, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem REINFORCED_STICKY_PISTON_ITEM = new BlockItem(ModBlocks.REINFORCED_STICKY_PISTON, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem COMPACT_NETHER_PORTAL_ITEM = new BlockItem(ModBlocks.COMPACT_NETHER_PORTAL, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem ITEM_TDI_ITEM = new BlockItem(ModBlocks.ITEM_TDI, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem AZURE_SAND_ITEM = new BlockItem(ModBlocks.AZURE_SAND, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem AZURE_SANDSTONE_ITEM = new BlockItem(ModBlocks.AZURE_SANDSTONE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem AZURE_SANDSTONE_BRICKS_ITEM = new BlockItem(ModBlocks.AZURE_SANDSTONE_BRICKS, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem STARDUST_BLOCK_ITEM = new BlockItem(ModBlocks.BLOCK_OF_STARDUST, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem STARROCK_ITEM = new BlockItem(ModBlocks.STARROCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem STARROCK_BRICKS_BLOCK_ITEM = new BlockItem(ModBlocks.STARROCK_BRICKS, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem CHROME_ORE_BLOCK_ITEM = new BlockItem(ModBlocks.CHROME_ORE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem DEEPSLATE_CHROME_ORE_BLOCK_ITEM = new BlockItem(ModBlocks.DEEPSLATE_CHROME_ORE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem RAW_CHROME_BLOCK_ITEM = new BlockItem(ModBlocks.RAW_CHROME_BLOCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem NICKEL_ORE_BLOCK_ITEM = new BlockItem(ModBlocks.NICKEL_ORE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem DEEPSLATE_NICKEL_ORE_BLOCK_ITEM = new BlockItem(ModBlocks.DEEPSLATE_NICKEL_ORE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem RAW_NICKEL_BLOCK = new BlockItem(ModBlocks.RAW_NICKEL_BLOCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem ALLOY_FURNACE_BLOCK = new BlockItem(ModBlocks.ALLOY_FURNACE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem STAINLESS_STEEL_BLOCK = new BlockItem(ModBlocks.STAINLESS_STEEL_BLOCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VUX_MACHINE_CORE_BLOCK_ITEM = new BlockItem(ModBlocks.VUX_MACHINE_CORE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VUX_FORMING_MACHINE = new BlockItem(ModBlocks.VUX_FORMING_MACHINE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem LASER_ENGRAVING_MACHINE = new BlockItem(ModBlocks.LASER_ENGRAVING_MACHINE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VUX_FILTER_MACHINE_BLOCK_ITEM = new BlockItem(ModBlocks.VUX_FILTER_MACHINE, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem COMPRESSED_NETHER_BRICKS_BLOCK_ITEM = new BlockItem(ModBlocks.COMPRESSED_NETHER_BRICKS, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem CREATIVE_CRACKED_BEDROCK_BLOCK_ITEM = new BlockItem(ModBlocks.CREATIVE_CRACKED_BEDROCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VACUUM_COATER = new BlockItem(ModBlocks.VACUUM_COATER, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem STRONG_ARTIFICIAL_BEDROCK_BLOCK_ITEM = new BlockItem(ModBlocks.STRONG_ARTIFICIAL_BEDROCK, new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUX_METER = new VuxMeterItem(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item GODEL_CRYSTAL_SHARD = new GodelCrystalShardItem(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item ARTIFICIAL_BEDROCK_SCRAP = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item PARADOXIUM = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item COMPACT_PORTAL_LINKER = new CompactPortalLinkerItem(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP).maxCount(1));
    public static final Item INFERNIUM = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item LEVITATIUM = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item STARDUST = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item ETHER_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item RAW_CHROME = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item CHROME_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item RAW_NICKEL = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item NICKEL_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item RESONATING_QUARTZ = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item NICHROME_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item STAINLESS_STEEL_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VPU_CHIP = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUXIN = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUXOUT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUX_LASER_COMPONENT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUX_FILTER_TEMPLATE = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUX_FILTER = new VuxFilterItem(new FabricItemSettings().maxCount(1).maxDamage(64));
    public static final Item WEAK_VUX_VALVE = new AbstractVuxValveItem(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item NICHROME_WIRE = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item BASIC_HEATING_COIL = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item ADVANCED_HEATING_COIL = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VACUUM_PUMP = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item COPPER_NUGGET = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item AMETHYST_LENS = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item SILVER_COATED_AMETHYST_SHARD = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item SILVER_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item INVAR_INGOT = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item REDSTONE_QUARTZ = new Item(new FabricItemSettings().group(VOIDOPHOBIA_DEFAULT_GROUP));


    private static void register(Item item, String id){
        Registry.register(Registry.ITEM, new Identifier(Voidophobia.MODID, id), item);
    }

    public static void registerAll(){
        register(SLIGHTLY_CRACKED_BEDROCK_ITEM, "slightly_cracked_bedrock");
        register(GODEL_CRYSTAL_ITEM, "godel_crystal_block");
        register(WEAK_ARTIFICIAL_BEDROCK_ITEM, "weak_artificial_bedrock");
        register(BASIC_VUXDUCT_ITEM, "basic_vuxduct");
        register(VUX_LAMP_ITEM, "vux_lamp");
        register(REINFORCED_PISTON_ITEM, "reinforced_piston");
        register(REINFORCED_STICKY_PISTON_ITEM, "reinforced_sticky_piston");
        register(ETERNAL_FIRE_ITEM, "eternal_fire");
        register(COMPACT_NETHER_PORTAL_ITEM, "compact_nether_portal");
        register(ITEM_TDI_ITEM, "item_tdi");
        register(AZURE_SAND_ITEM, "azure_sand");
        register(AZURE_SANDSTONE_ITEM, "azure_sandstone");
        register(AZURE_SANDSTONE_BRICKS_ITEM, "azure_sandstone_bricks");
        register(STARDUST_BLOCK_ITEM, "block_of_stardust");
        register(STARROCK_ITEM, "starrock");
        register(STARROCK_BRICKS_BLOCK_ITEM, "starrock_bricks");
        register(VUX_METER, "vux_meter");
        register(GODEL_CRYSTAL_SHARD, "godel_crystal_shard");
        register(ARTIFICIAL_BEDROCK_SCRAP, "artificial_bedrock_scrap");
        register(PARADOXIUM, "paradoxium");
        register(COMPACT_PORTAL_LINKER, "portal_linker");
        register(INFERNIUM, "infernium");
        register(LEVITATIUM, "levitatium");
        register(STARDUST, "stardust");
        register(ETHER_INGOT, "ether_ingot");
        register(CHROME_ORE_BLOCK_ITEM, "chrome_ore");
        register(DEEPSLATE_CHROME_ORE_BLOCK_ITEM, "deepslate_chrome_ore");
        register(RAW_CHROME, "raw_chrome");
        register(RAW_CHROME_BLOCK_ITEM, "raw_chrome_block");
        register(CHROME_INGOT, "chrome_ingot");
        register(NICKEL_ORE_BLOCK_ITEM, "nickel_ore");
        register(DEEPSLATE_NICKEL_ORE_BLOCK_ITEM, "deepslate_nickel_ore");
        register(RAW_NICKEL, "raw_nickel");
        register(RAW_NICKEL_BLOCK, "raw_nickel_block");
        register(NICKEL_INGOT, "nickel_ingot");
        register(RESONATING_QUARTZ, "resonating_quartz");
        register(ALLOY_FURNACE_BLOCK, "alloy_furnace");
        register(NICHROME_INGOT, "nichrome_ingot");
        register(STAINLESS_STEEL_INGOT, "stainless_steel_ingot");
        register(STAINLESS_STEEL_BLOCK, "stainless_steel_block");
        register(VPU_CHIP, "vpu_chip");
        register(VUXIN, "vuxin");
        register(VUXOUT, "vuxout");
        register(VUX_LASER_COMPONENT, "vux_laser_component");
        register(VUX_MACHINE_CORE_BLOCK_ITEM, "vux_machine_core");
        register(VUX_FORMING_MACHINE, "vux_forming_machine");
        register(VUX_FILTER_TEMPLATE, "vux_filter_template");
        register(VUX_FILTER, "vux_filter");
        register(LASER_ENGRAVING_MACHINE, "laser_engraving_machine");
        register(WEAK_VUX_VALVE, "weak_vux_valve");
        register(VUX_FILTER_MACHINE_BLOCK_ITEM, "vux_filter_machine");
        register(COMPRESSED_NETHER_BRICKS_BLOCK_ITEM, "compressed_nether_bricks");
        register(NICHROME_WIRE, "nichrome_wire");
        register(BASIC_HEATING_COIL, "basic_heating_coil");
        register(ADVANCED_HEATING_COIL, "advanced_heating_coil");
        register(VACUUM_PUMP, "vacuum_pump");
        register(CREATIVE_CRACKED_BEDROCK_BLOCK_ITEM, "creative_cracked_bedrock");
        register(VACUUM_COATER, "vacuum_coater");
        register(STRONG_ARTIFICIAL_BEDROCK_BLOCK_ITEM, "strong_artificial_bedrock");
        register(COPPER_NUGGET, "copper_nugget");
        register(AMETHYST_LENS, "amethyst_lens");
        register(SILVER_COATED_AMETHYST_SHARD, "silver_coated_amethyst_shard");
        register(SILVER_INGOT, "silver_ingot");
        register(INVAR_INGOT, "invar_ingot");
        register(REDSTONE_QUARTZ, "redstone_quartz");
    }

}
