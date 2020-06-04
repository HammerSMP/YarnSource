/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BeeEntityRenderer
extends MobEntityRenderer<BeeEntity, BeeEntityModel<BeeEntity>> {
    private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/bee/bee_angry.png");
    private static final Identifier ANGRY_NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_angry_nectar.png");
    private static final Identifier PASSIVE_TEXTURE = new Identifier("textures/entity/bee/bee.png");
    private static final Identifier NECTAR_TEXTURE = new Identifier("textures/entity/bee/bee_nectar.png");

    public BeeEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new BeeEntityModel(), 0.4f);
    }

    @Override
    public Identifier getTexture(BeeEntity arg) {
        if (arg.method_29511()) {
            if (arg.hasNectar()) {
                return ANGRY_NECTAR_TEXTURE;
            }
            return ANGRY_TEXTURE;
        }
        if (arg.hasNectar()) {
            return NECTAR_TEXTURE;
        }
        return PASSIVE_TEXTURE;
    }
}

