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
    public static final Item VUX_METER = new VuxMeterItem(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item GODEL_CRYSTAL_SHARD = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));
    public static final Item ARTIFICIAL_BEDROCK_SCRAP = new Item(new FabricItemSettings().group(Voidophobia.VOIDOPHOBIA_DEFAULT_GROUP));

    private static void register(Item item, String id){
        Registry.register(Registry.ITEM, new Identifier(Voidophobia.MODID, id), item);
    }

    public static void registerAll(){
        register(SLIGHTLY_CRACKED_BEDROCK_ITEM, "slightly_cracked_bedrock");
        register(GODEL_CRYSTAL_ITEM, "godel_crystal_block");
        register(VUX_METER, "vux_meter");
        register(GODEL_CRYSTAL_SHARD, "godel_crystal_shard");
        register(ARTIFICIAL_BEDROCK_SCRAP, "artificial_bedrock_scrap");
    }

}
