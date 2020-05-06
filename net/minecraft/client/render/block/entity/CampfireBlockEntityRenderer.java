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
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class CampfireBlockEntityRenderer
extends BlockEntityRenderer<CampfireBlockEntity> {
    public CampfireBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(CampfireBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        Direction lv = arg.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> lv2 = arg.getItemsBeingCooked();
        for (int k = 0; k < lv2.size(); ++k) {
            ItemStack lv3 = lv2.get(k);
            if (lv3 == ItemStack.EMPTY) continue;
            arg2.push();
            arg2.translate(0.5, 0.44921875, 0.5);
            Direction lv4 = Direction.fromHorizontal((k + lv.getHorizontal()) % 4);
            float g = -lv4.asRotation();
            arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(g));
            arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            arg2.translate(-0.3125, -0.3125, 0.0);
            arg2.scale(0.375f, 0.375f, 0.375f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(lv3, ModelTransformation.Mode.FIXED, i, j, arg2, arg3);
            arg2.pop();
        }
    }
}

