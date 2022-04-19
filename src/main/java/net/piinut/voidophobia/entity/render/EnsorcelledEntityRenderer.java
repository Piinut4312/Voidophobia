package net.piinut.voidophobia.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;

@Environment(value = EnvType.CLIENT)
public class EnsorcelledEntityRenderer extends ZombieEntityRenderer {

    private static final Identifier TEXTURE = new Identifier(Voidophobia.MODID, "textures/entity/ensorcelled.png");

    public EnsorcelledEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ZombieEntity zombieEntity) {
        return TEXTURE;
    }


}
