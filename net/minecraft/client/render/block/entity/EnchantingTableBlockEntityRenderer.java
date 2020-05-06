/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
extends BlockEntityRenderer<EnchantingTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/enchanting_table_book"));
    private final BookModel book = new BookModel();

    public EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(EnchantingTableBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        float h;
        arg2.push();
        arg2.translate(0.5, 0.75, 0.5);
        float g = (float)arg.ticks + f;
        arg2.translate(0.0, 0.1f + MathHelper.sin(g * 0.1f) * 0.01f, 0.0);
        for (h = arg.field_11964 - arg.field_11963; h >= (float)Math.PI; h -= (float)Math.PI * 2) {
        }
        while (h < (float)(-Math.PI)) {
            h += (float)Math.PI * 2;
        }
        float k = arg.field_11963 + h * f;
        arg2.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(-k));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(80.0f));
        float l = MathHelper.lerp(f, arg.pageAngle, arg.nextPageAngle);
        float m = MathHelper.fractionalPart(l + 0.25f) * 1.6f - 0.3f;
        float n = MathHelper.fractionalPart(l + 0.75f) * 1.6f - 0.3f;
        float o = MathHelper.lerp(f, arg.pageTurningSpeed, arg.nextPageTurningSpeed);
        this.book.setPageAngles(g, MathHelper.clamp(m, 0.0f, 1.0f), MathHelper.clamp(n, 0.0f, 1.0f), o);
        VertexConsumer lv = BOOK_TEXTURE.getVertexConsumer(arg3, RenderLayer::getEntitySolid);
        this.book.method_24184(arg2, lv, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
    }
}

