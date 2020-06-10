/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class SkullBlockEntityRenderer
extends BlockEntityRenderer<SkullBlockEntity> {
    private static final Map<SkullBlock.SkullType, SkullEntityModel> MODELS = Util.make(Maps.newHashMap(), hashMap -> {
        SkullEntityModel lv = new SkullEntityModel(0, 0, 64, 32);
        SkullOverlayEntityModel lv2 = new SkullOverlayEntityModel();
        DragonHeadEntityModel lv3 = new DragonHeadEntityModel(0.0f);
        hashMap.put(SkullBlock.Type.SKELETON, lv);
        hashMap.put(SkullBlock.Type.WITHER_SKELETON, lv);
        hashMap.put(SkullBlock.Type.PLAYER, lv2);
        hashMap.put(SkullBlock.Type.ZOMBIE, lv2);
        hashMap.put(SkullBlock.Type.CREEPER, lv);
        hashMap.put(SkullBlock.Type.DRAGON, lv3);
    });
    private static final Map<SkullBlock.SkullType, Identifier> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(SkullBlock.Type.SKELETON, new Identifier("textures/entity/skeleton/skeleton.png"));
        hashMap.put(SkullBlock.Type.WITHER_SKELETON, new Identifier("textures/entity/skeleton/wither_skeleton.png"));
        hashMap.put(SkullBlock.Type.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
        hashMap.put(SkullBlock.Type.CREEPER, new Identifier("textures/entity/creeper/creeper.png"));
        hashMap.put(SkullBlock.Type.DRAGON, new Identifier("textures/entity/enderdragon/dragon.png"));
        hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
    });

    public SkullBlockEntityRenderer(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(SkullBlockEntity arg, float f, MatrixStack arg2, VertexConsumerProvider arg3, int i, int j) {
        float g = arg.getTicksPowered(f);
        BlockState lv = arg.getCachedState();
        boolean bl = lv.getBlock() instanceof WallSkullBlock;
        Direction lv2 = bl ? lv.get(WallSkullBlock.FACING) : null;
        float h = 22.5f * (float)(bl ? (2 + lv2.getHorizontal()) * 4 : lv.get(SkullBlock.ROTATION));
        SkullBlockEntityRenderer.render(lv2, h, ((AbstractSkullBlock)lv.getBlock()).getSkullType(), arg.getOwner(), g, arg2, arg3, i);
    }

    public static void render(@Nullable Direction arg, float f, SkullBlock.SkullType arg2, @Nullable GameProfile gameProfile, float g, MatrixStack arg3, VertexConsumerProvider arg4, int i) {
        SkullEntityModel lv = MODELS.get(arg2);
        arg3.push();
        if (arg == null) {
            arg3.translate(0.5, 0.0, 0.5);
        } else {
            float h = 0.25f;
            arg3.translate(0.5f - (float)arg.getOffsetX() * 0.25f, 0.25, 0.5f - (float)arg.getOffsetZ() * 0.25f);
        }
        arg3.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer lv2 = arg4.getBuffer(SkullBlockEntityRenderer.method_3578(arg2, gameProfile));
        lv.method_2821(g, f, 0.0f);
        lv.render(arg3, lv2, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        arg3.pop();
    }

    private static RenderLayer method_3578(SkullBlock.SkullType arg, @Nullable GameProfile gameProfile) {
        Identifier lv = TEXTURES.get(arg);
        if (arg != SkullBlock.Type.PLAYER || gameProfile == null) {
            return RenderLayer.getEntityCutoutNoCull(lv);
        }
        MinecraftClient lv2 = MinecraftClient.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = lv2.getSkinProvider().getTextures(gameProfile);
        if (map.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) {
            return RenderLayer.getEntityTranslucent(lv2.getSkinProvider().loadSkin(map.get((Object)MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
        }
        return RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile)));
    }
}

