/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends DrownedEntity>
extends FeatureRenderer<T, DrownedEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedEntityModel<T> model = new DrownedEntityModel(0.25f, 0.0f, 64, 64);

    public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        DrownedOverlayFeatureRenderer.render(this.getContextModel(), this.model, SKIN, arg, arg2, i, arg3, f, g, j, k, l, h, 1.0f, 1.0f, 1.0f);
    }
}

