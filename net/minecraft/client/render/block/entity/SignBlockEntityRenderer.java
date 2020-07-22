/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.SignType;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends BlockEntityRenderer<SignBlockEntity> {
    private final SignModel model = new SignModel();

    public SignBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(SignBlockEntity arg, float f, MatrixStack arg22, VertexConsumerProvider arg3, int i, int j) {
        BlockState lv = arg.getCachedState();
        arg22.push();
        float g = 0.6666667f;
        if (lv.getBlock() instanceof SignBlock) {
            arg22.translate(0.5, 0.5, 0.5);
            float h = -((float)(lv.get(SignBlock.ROTATION) * 360) / 16.0f);
            arg22.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
            this.model.foot.visible = true;
        } else {
            arg22.translate(0.5, 0.5, 0.5);
            float k = -lv.get(WallSignBlock.FACING).asRotation();
            arg22.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k));
            arg22.translate(0.0, -0.3125, -0.4375);
            this.model.foot.visible = false;
        }
        arg22.push();
        arg22.scale(0.6666667f, -0.6666667f, -0.6666667f);
        SpriteIdentifier lv2 = SignBlockEntityRenderer.getModelTexture(lv.getBlock());
        VertexConsumer lv3 = lv2.getVertexConsumer(arg3, this.model::getLayer);
        this.model.field.render(arg22, lv3, i, j);
        this.model.foot.render(arg22, lv3, i, j);
        arg22.pop();
        TextRenderer lv4 = this.dispatcher.getTextRenderer();
        float l = 0.010416667f;
        arg22.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        arg22.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int m = arg.getTextColor().getSignColor();
        double d = 0.4;
        int n = (int)((double)NativeImage.getRed(m) * 0.4);
        int o = (int)((double)NativeImage.getGreen(m) * 0.4);
        int p = (int)((double)NativeImage.getBlue(m) * 0.4);
        int q = NativeImage.getAbgrColor(0, p, o, n);
        int r = 20;
        for (int s = 0; s < 4; ++s) {
            StringRenderable lv5 = arg.getTextBeingEditedOnRow(s, arg2 -> {
                List<StringRenderable> list = lv4.getTextHandler().wrapLines((StringRenderable)arg2, 90, Style.EMPTY);
                return list.isEmpty() ? StringRenderable.EMPTY : list.get(0);
            });
            if (lv5 == null) continue;
            float t = -lv4.getWidth(lv5) / 2;
            lv4.draw(lv5, t, (float)(s * 10 - 20), q, false, arg22.peek().getModel(), arg3, false, 0, i);
        }
        arg22.pop();
    }

    public static SpriteIdentifier getModelTexture(Block arg) {
        SignType lv2;
        if (arg instanceof AbstractSignBlock) {
            SignType lv = ((AbstractSignBlock)arg).getSignType();
        } else {
            lv2 = SignType.OAK;
        }
        return TexturedRenderLayers.getSignTextureId(lv2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SignModel
    extends Model {
        public final ModelPart field = new ModelPart(64, 32, 0, 0);
        public final ModelPart foot;

        public SignModel() {
            super(RenderLayer::getEntityCutoutNoCull);
            this.field.addCuboid(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f, 0.0f);
            this.foot = new ModelPart(64, 32, 0, 14);
            this.foot.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f, 0.0f);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            this.field.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            this.foot.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}

