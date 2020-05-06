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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PufferfishEntityRenderer
extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fish/pufferfish.png");
    private int modelSize = 3;
    private final SmallPufferfishEntityModel<PufferfishEntity> smallModel = new SmallPufferfishEntityModel();
    private final MediumPufferfishEntityModel<PufferfishEntity> mediumModel = new MediumPufferfishEntityModel();
    private final LargePufferfishEntityModel<PufferfishEntity> largeModel = new LargePufferfishEntityModel();

    public PufferfishEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new LargePufferfishEntityModel(), 0.2f);
    }

    @Override
    public Identifier getTexture(PufferfishEntity arg) {
        return TEXTURE;
    }

    @Override
    public void render(PufferfishEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        int j = arg.getPuffState();
        if (j != this.modelSize) {
            this.model = j == 0 ? this.smallModel : (j == 1 ? this.mediumModel : this.largeModel);
        }
        this.modelSize = j;
        this.shadowRadius = 0.1f + 0.1f * (float)j;
        super.render(arg, f, g, arg2, arg3, i);
    }

    @Override
    protected void setupTransforms(PufferfishEntity arg, MatrixStack arg2, float f, float g, float h) {
        arg2.translate(0.0, MathHelper.cos(f * 0.05f) * 0.08f, 0.0);
        super.setupTransforms(arg, arg2, f, g, h);
    }
}

