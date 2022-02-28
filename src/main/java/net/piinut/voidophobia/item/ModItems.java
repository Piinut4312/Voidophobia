package net.piinut.voidophobia.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.ModBlocks;

public class ModItems {

    public static final BlockItem SLIGHTLY_CRACKED_BEDROCK_ITEM = new BlockItem(ModBlocks.SLIGHTLY_CRACKED_BEDROCK, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem GODEL_CRYSTAL_ITEM = new BlockItem(ModBlocks.GODEL_CRYSTAL_BLOCK, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem WEAK_ARTIFICIAL_BEDROCK_ITEM = new BlockItem(ModBlocks.WEAK_ARTIFICIAL_BEDROCK, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem BASIC_VUXDUCT_ITEM = new BlockItem(ModBlocks.BASIC_VUXDUCT, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem VUX_LAMP_ITEM = new BlockItem(ModBlocks.VUX_LAMP, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem ETERNAL_FIRE_ITEM = new BlockItem(ModBlocks.ETERNAL_FIRE, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem REINFORCED_PISTON_ITEM = new BlockItem(ModBlocks.REINFORCED_PISTON, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem REINFORCED_STICKY_PISTON_ITEM = new BlockItem(ModBlocks.REINFORCED_STICKY_PISTON, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem COMPACT_NETHER_PORTAL_ITEM = new BlockItem(ModBlocks.COMPACT_NETHER_PORTAL, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final BlockItem ITEM_TDI_ITEM = new BlockItem(ModBlocks.ITEM_TDI, new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUX_METER = new VuxMeterItem(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item GODEL_CRYSTAL_SHARD = new GodelCrystalShardItem(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item ARTIFICIAL_BEDROCK_SCRAP = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item PARADOXIUM = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUXIN = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item VUXOUT = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item COMPACT_PORTAL_LINKER = new CompactPortalLinkerItem(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP).maxCount(1));

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
        register(VUX_METER, "vux_meter");
        register(GODEL_CRYSTAL_SHARD, "godel_crystal_shard");
        register(ARTIFICIAL_BEDROCK_SCRAP, "artificial_bedrock_scrap");
        register(PARADOXIUM, "paradoxium");
        register(VUXIN, "vuxin");
        register(VUXOUT, "vuxout");
        register(COMPACT_PORTAL_LINKER, "portal_linker");
    }

}
