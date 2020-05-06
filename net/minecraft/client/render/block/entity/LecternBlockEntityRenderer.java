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
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
extends BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel book = new BookModel();

    public LecternBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(LecternBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        BlockState lv = arg.getCachedState();
        if (!lv.get(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        arg2.push();
        arg2.translate(0.5, 1.0625, 0.5);
        float g = lv.get(LecternBlock.FACING).rotateYClockwise().asRotation();
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-g));
        arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(67.5f));
        arg2.translate(0.0, -0.125, 0.0);
        this.book.setPageAngles(0.0f, 0.1f, 0.9f, 1.2f);
        VertexConsumer lv2 = EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getVertexConsumer(arg3, RenderLayer::getEntitySolid);
        this.book.method_24184(arg2, lv2, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        arg2.pop();
    }
}

