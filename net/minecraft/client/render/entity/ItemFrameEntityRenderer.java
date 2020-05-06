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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer
extends EntityRenderer<ItemFrameEntity> {
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
    private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public ItemFrameEntityRenderer(EntityRenderDispatcher arg, ItemRenderer arg2) {
        super(arg);
        this.itemRenderer = arg2;
    }

    @Override
    public void render(ItemFrameEntity arg, float f, float g, MatrixStack arg2, VertexConsumerProvider arg3, int i) {
        ItemStack lv6;
        super.render(arg, f, g, arg2, arg3, i);
        arg2.push();
        Direction lv = arg.getHorizontalFacing();
        Vec3d lv2 = this.getPositionOffset(arg, g);
        arg2.translate(-lv2.getX(), -lv2.getY(), -lv2.getZ());
        double d = 0.46875;
        arg2.translate((double)lv.getOffsetX() * 0.46875, (double)lv.getOffsetY() * 0.46875, (double)lv.getOffsetZ() * 0.46875);
        arg2.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(arg.pitch));
        arg2.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - arg.yaw));
        boolean bl = arg.isInvisible();
        if (!bl) {
            BlockRenderManager lv3 = this.client.getBlockRenderManager();
            BakedModelManager lv4 = lv3.getModels().getModelManager();
            ModelIdentifier lv5 = arg.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
            arg2.push();
            arg2.translate(-0.5, -0.5, -0.5);
            lv3.getModelRenderer().render(arg2.peek(), arg3.getBuffer(TexturedRenderLayers.getEntitySolid()), null, lv4.getModel(lv5), 1.0f, 1.0f, 1.0f, i, OverlayTexture.DEFAULT_UV);
            arg2.pop();
        }
        if (!(lv6 = arg.getHeldItemStack()).isEmpty()) {
            boolean bl2;
            boolean bl3 = bl2 = lv6.getItem() == Items.FILLED_MAP;
            if (bl) {
                arg2.translate(0.0, 0.0, 0.5);
            } else {
                arg2.translate(0.0, 0.0, 0.4375);
            }
            int j = bl2 ? arg.getRotation() % 4 * 2 : arg.getRotation();
            arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0f / 8.0f));
            if (bl2) {
                arg2.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                float h = 0.0078125f;
                arg2.scale(0.0078125f, 0.0078125f, 0.0078125f);
                arg2.translate(-64.0, -64.0, 0.0);
                MapState lv7 = FilledMapItem.getOrCreateMapState(lv6, arg.world);
                arg2.translate(0.0, 0.0, -1.0);
                if (lv7 != null) {
                    this.client.gameRenderer.getMapRenderer().draw(arg2, arg3, lv7, true, i);
                }
            } else {
                arg2.scale(0.5f, 0.5f, 0.5f);
                this.itemRenderer.renderItem(lv6, ModelTransformation.Mode.FIXED, i, OverlayTexture.DEFAULT_UV, arg2, arg3);
            }
        }
        arg2.pop();
    }

    @Override
    public Vec3d getPositionOffset(ItemFrameEntity arg, float f) {
        return new Vec3d((float)arg.getHorizontalFacing().getOffsetX() * 0.3f, -0.25, (float)arg.getHorizontalFacing().getOffsetZ() * 0.3f);
    }

    @Override
    public Identifier getTexture(ItemFrameEntity arg) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }

    @Override
    protected boolean hasLabel(ItemFrameEntity arg) {
        if (!MinecraftClient.isHudEnabled() || arg.getHeldItemStack().isEmpty() || !arg.getHeldItemStack().hasCustomName() || this.dispatcher.targetedEntity != arg) {
            return false;
        }
        double d = this.dispatcher.getSquaredDistanceToCamera(arg);
        float f = arg.isSneaky() ? 32.0f : 64.0f;
        return d < (double)(f * f);
    }

    @Override
    protected void renderLabelIfPresent(ItemFrameEntity arg, Text arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        super.renderLabelIfPresent(arg, arg.getHeldItemStack().getName(), arg3, arg4, i);
    }

    @Override
    public /* synthetic */ Vec3d getPositionOffset(Entity arg, float f) {
        return this.getPositionOffset((ItemFrameEntity)arg, f);
    }
}

