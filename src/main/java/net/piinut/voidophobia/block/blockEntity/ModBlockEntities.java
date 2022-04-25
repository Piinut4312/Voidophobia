package net.piinut.voidophobia.block.blockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.ModBlocks;


public class ModBlockEntities {

    public static BlockEntityType<BasicVuxductBlockEntity> BASIC_VUXDUCT;
    public static BlockEntityType<ReinforcedPistonBlockEntity> REINFORCED_PISTON;
    public static BlockEntityType<CompactNetherPortalBlockEntity> COMPACT_NETHER_PORTAL;
    public static BlockEntityType<ItemTDIBlockEntity> ITEM_TDI;
    public static BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE;
    public static BlockEntityType<VuxFormingMachineBlockEntity> VUX_FORMING_MACHINE;
    public static BlockEntityType<LaserEngravingMachineBlockEntity> LASER_ENGRAVING_MACHINE;
    public static BlockEntityType<VuxFilterMachineBlockEntity> VUX_FILTER_MACHINE;
    public static BlockEntityType<VacuumCoaterBlockEntity> VACUUM_COATER;
    public static BlockEntityType<BlastChamberBlockEntity> BLAST_CHAMBER;
    public static void registerAll(){
        BASIC_VUXDUCT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "basic_vuxduct")
                , FabricBlockEntityTypeBuilder.create(BasicVuxductBlockEntity::new, ModBlocks.BASIC_VUXDUCT).build(null));
        REINFORCED_PISTON = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "reinforced_piston")
                , FabricBlockEntityTypeBuilder.create(ReinforcedPistonBlockEntity::new, ModBlocks.MOVING_REINFORCED_PISTON).build(null));
        COMPACT_NETHER_PORTAL = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "compact_nether_portal")
                , FabricBlockEntityTypeBuilder.create(CompactNetherPortalBlockEntity::new, ModBlocks.COMPACT_NETHER_PORTAL).build(null));
        ITEM_TDI = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "item_tdi")
                , FabricBlockEntityTypeBuilder.create(ItemTDIBlockEntity::new, ModBlocks.ITEM_TDI).build(null));
        ALLOY_FURNACE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "alloy_furnace")
                , FabricBlockEntityTypeBuilder.create(AlloyFurnaceBlockEntity::new, ModBlocks.ALLOY_FURNACE).build(null));
        VUX_FORMING_MACHINE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "vux_forming_machine")
                , FabricBlockEntityTypeBuilder.create(VuxFormingMachineBlockEntity::new, ModBlocks.VUX_FORMING_MACHINE).build(null));
        LASER_ENGRAVING_MACHINE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "laser_engraving")
                , FabricBlockEntityTypeBuilder.create(LaserEngravingMachineBlockEntity::new, ModBlocks.LASER_ENGRAVING_MACHINE).build(null));
        VUX_FILTER_MACHINE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "vux_filter_machine")
                , FabricBlockEntityTypeBuilder.create(VuxFilterMachineBlockEntity::new, ModBlocks.VUX_FILTER_MACHINE).build(null));
        VACUUM_COATER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "vacuum_coater")
                , FabricBlockEntityTypeBuilder.create(VacuumCoaterBlockEntity::new, ModBlocks.VACUUM_COATER).build(null));
        BLAST_CHAMBER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Voidophobia.MODID, "blast_chamber")
                , FabricBlockEntityTypeBuilder.create(BlastChamberBlockEntity::new, ModBlocks.BLAST_CHAMBER).build(null));
    }

}
