package net.piinut.voidophobia.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;

public class ModEntities {

    public static final EntityType<AbyssSpiderEntity> ABYSS_SPIDER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Voidophobia.MODID, "abyss_spider"), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AbyssSpiderEntity::new).dimensions(EntityDimensions.fixed(1.0f, 0.75f)).build());

    public static void registerAttributes(){
        FabricDefaultAttributeRegistry.register(ABYSS_SPIDER, AbyssSpiderEntity.createAbyssSpiderAttributes());
    }

}
