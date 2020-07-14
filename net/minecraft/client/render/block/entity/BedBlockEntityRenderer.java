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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
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
public class BedBlockEntityRenderer
extends BlockEntityRenderer<BedBlockEntity> {
    private final ModelPart field_20813;
    private final ModelPart field_20814;
    private final ModelPart[] legs = new ModelPart[4];

    public BedBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
        this.field_20813 = new ModelPart(64, 64, 0, 0);
        this.field_20813.addCuboid(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f, 0.0f);
        this.field_20814 = new ModelPart(64, 64, 0, 22);
        this.field_20814.addCuboid(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f, 0.0f);
        this.legs[0] = new ModelPart(64, 64, 50, 0);
        this.legs[1] = new ModelPart(64, 64, 50, 6);
        this.legs[2] = new ModelPart(64, 64, 50, 12);
        this.legs[3] = new ModelPart(64, 64, 50, 18);
        this.legs[0].addCuboid(0.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f);
        this.legs[1].addCuboid(0.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f);
        this.legs[2].addCuboid(-16.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f);
        this.legs[3].addCuboid(-16.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f);
        this.legs[0].pitch = 1.5707964f;
        this.legs[1].pitch = 1.5707964f;
        this.legs[2].pitch = 1.5707964f;
        this.legs[3].pitch = 1.5707964f;
        this.legs[0].roll = 0.0f;
        this.legs[1].roll = 1.5707964f;
        this.legs[2].roll = 4.712389f;
        this.legs[3].roll = (float)Math.PI;
    }

    @Override
    public void render(BedBlockEntity arg3, float f, MatrixStack arg22, VertexConsumerProvider arg32, int i, int j) {
        SpriteIdentifier lv = TexturedRenderLayers.BED_TEXTURES[arg3.getColor().getId()];
        World lv2 = arg3.getWorld();
        if (lv2 != null) {
            BlockState lv3 = arg3.getCachedState();
            DoubleBlockProperties.PropertySource<BedBlockEntity> lv4 = DoubleBlockProperties.toPropertySource(BlockEntityType.BED, BedBlock::getBedPart, BedBlock::getOppositePartDirection, ChestBlock.FACING, lv3, lv2, arg3.getPos(), (arg, arg2) -> false);
            int k = ((Int2IntFunction)lv4.apply(new LightmapCoordinatesRetriever())).get(i);
            this.method_3558(arg22, arg32, lv3.get(BedBlock.PART) == BedPart.HEAD, lv3.get(BedBlock.FACING), lv, k, j, false);
        } else {
            this.method_3558(arg22, arg32, true, Direction.SOUTH, lv, i, j, false);
            this.method_3558(arg22, arg32, false, Direction.SOUTH, lv, i, j, true);
        }
    }

    private void method_3558(MatrixStack matrix, VertexConsumerProvider arg2, boolean bl, Direction arg3, SpriteIdentifier arg4, int light, int overlay, boolean bl2) {
        this.field_20813.visible = bl;
        this.field_20814.visible = !bl;
        this.legs[0].visible = !bl;
        this.legs[1].visible = bl;
        this.legs[2].visible = !bl;
        this.legs[3].visible = bl;
        matrix.push();
        matrix.translate(0.0, 0.5625, bl2 ? -1.0 : 0.0);
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        matrix.translate(0.5, 0.5, 0.5);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f + arg3.asRotation()));
        matrix.translate(-0.5, -0.5, -0.5);
        VertexConsumer lv = arg4.getVertexConsumer(arg2, RenderLayer::getEntitySolid);
        this.field_20813.render(matrix, lv, light, overlay);
        this.field_20814.render(matrix, lv, light, overlay);
        this.legs[0].render(matrix, lv, light, overlay);
        this.legs[1].render(matrix, lv, light, overlay);
        this.legs[2].render(matrix, lv, light, overlay);
        this.legs[3].render(matrix, lv, light, overlay);
        matrix.pop();
    }
}

