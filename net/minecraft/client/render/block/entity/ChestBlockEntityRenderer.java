/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
extends BlockEntityRenderer<T> {
    private final ModelPart field_20817;
    private final ModelPart field_20818;
    private final ModelPart field_20819;
    private final ModelPart field_20820;
    private final ModelPart field_20821;
    private final ModelPart field_20822;
    private final ModelPart field_21479;
    private final ModelPart field_21480;
    private final ModelPart field_21481;
    private boolean christmas;

    public ChestBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.christmas = true;
        }
        this.field_20818 = new ModelPart(64, 64, 0, 19);
        this.field_20818.addCuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f, 0.0f);
        this.field_20817 = new ModelPart(64, 64, 0, 0);
        this.field_20817.addCuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f, 0.0f);
        this.field_20817.pivotY = 9.0f;
        this.field_20817.pivotZ = 1.0f;
        this.field_20819 = new ModelPart(64, 64, 0, 0);
        this.field_20819.addCuboid(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f, 0.0f);
        this.field_20819.pivotY = 8.0f;
        this.field_20821 = new ModelPart(64, 64, 0, 19);
        this.field_20821.addCuboid(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.field_20820 = new ModelPart(64, 64, 0, 0);
        this.field_20820.addCuboid(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.field_20820.pivotY = 9.0f;
        this.field_20820.pivotZ = 1.0f;
        this.field_20822 = new ModelPart(64, 64, 0, 0);
        this.field_20822.addCuboid(15.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.field_20822.pivotY = 8.0f;
        this.field_21480 = new ModelPart(64, 64, 0, 19);
        this.field_21480.addCuboid(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.field_21479 = new ModelPart(64, 64, 0, 0);
        this.field_21479.addCuboid(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.field_21479.pivotY = 9.0f;
        this.field_21479.pivotZ = 1.0f;
        this.field_21481 = new ModelPart(64, 64, 0, 0);
        this.field_21481.addCuboid(0.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.field_21481.pivotY = 8.0f;
    }

    @Override
    public void render(T arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        DoubleBlockProperties.PropertySource<ChestBlockEntity> lv7;
        World lv = ((BlockEntity)arg).getWorld();
        boolean bl = lv != null;
        BlockState lv2 = bl ? ((BlockEntity)arg).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType lv3 = lv2.contains(ChestBlock.CHEST_TYPE) ? lv2.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        Block lv4 = lv2.getBlock();
        if (!(lv4 instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock lv5 = (AbstractChestBlock)lv4;
        boolean bl2 = lv3 != ChestType.SINGLE;
        arg2.push();
        float g = lv2.get(ChestBlock.FACING).asRotation();
        arg2.translate(0.5, 0.5, 0.5);
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-g));
        arg2.translate(-0.5, -0.5, -0.5);
        if (bl) {
            DoubleBlockProperties.PropertySource<ChestBlockEntity> lv6 = lv5.getBlockEntitySource(lv2, lv, ((BlockEntity)arg).getPos(), true);
        } else {
            lv7 = DoubleBlockProperties.PropertyRetriever::getFallback;
        }
        float h = lv7.apply(ChestBlock.getAnimationProgressRetriever((ChestAnimationProgress)arg)).get(f);
        h = 1.0f - h;
        h = 1.0f - h * h * h;
        int k = ((Int2IntFunction)lv7.apply(new LightmapCoordinatesRetriever())).applyAsInt(i);
        SpriteIdentifier lv8 = TexturedRenderLayers.getChestTexture(arg, lv3, this.christmas);
        VertexConsumer lv9 = lv8.getVertexConsumer(arg3, RenderLayer::getEntityCutout);
        if (bl2) {
            if (lv3 == ChestType.LEFT) {
                this.method_22749(arg2, lv9, this.field_21479, this.field_21481, this.field_21480, h, k, j);
            } else {
                this.method_22749(arg2, lv9, this.field_20820, this.field_20822, this.field_20821, h, k, j);
            }
        } else {
            this.method_22749(arg2, lv9, this.field_20817, this.field_20819, this.field_20818, h, k, j);
        }
        arg2.pop();
    }

    private void method_22749(MatrixStack arg, VertexConsumer arg2, ModelPart arg3, ModelPart arg4, ModelPart arg5, float f, int i, int j) {
        arg4.pitch = arg3.pitch = -(f * 1.5707964f);
        arg3.render(arg, arg2, i, j);
        arg4.render(arg, arg2, i, j);
        arg5.render(arg, arg2, i, j);
    }
}

