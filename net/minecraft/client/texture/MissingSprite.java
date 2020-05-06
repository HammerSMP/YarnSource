/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

@Environment(value=EnvType.CLIENT)
public final class MissingSprite
extends Sprite {
    private static final Identifier MISSINGNO = new Identifier("missingno");
    @Nullable
    private static NativeImageBackedTexture texture;
    private static final Lazy<NativeImage> IMAGE;
    private static final Sprite.Info INFO;

    private MissingSprite(SpriteAtlasTexture arg, int i, int j, int k, int l, int m) {
        super(arg, INFO, i, j, k, l, m, IMAGE.get());
    }

    public static MissingSprite getMissingSprite(SpriteAtlasTexture arg, int i, int j, int k, int l, int m) {
        return new MissingSprite(arg, i, j, k, l, m);
    }

    public static Identifier getMissingSpriteId() {
        return MISSINGNO;
    }

    public static Sprite.Info getMissingInfo() {
        return INFO;
    }

    @Override
    public void close() {
        for (int i = 1; i < this.images.length; ++i) {
            this.images[i].close();
        }
    }

    public static NativeImageBackedTexture getMissingSpriteTexture() {
        if (texture == null) {
            texture = new NativeImageBackedTexture(IMAGE.get());
            MinecraftClient.getInstance().getTextureManager().registerTexture(MISSINGNO, texture);
        }
        return texture;
    }

    static {
        IMAGE = new Lazy<NativeImage>(() -> {
            NativeImage lv = new NativeImage(16, 16, false);
            int i = -16777216;
            int j = -524040;
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (k < 8 ^ l < 8) {
                        lv.setPixelRgba(l, k, -524040);
                        continue;
                    }
                    lv.setPixelRgba(l, k, -16777216);
                }
            }
            lv.untrack();
            return lv;
        });
        INFO = new Sprite.Info(MISSINGNO, 16, 16, new AnimationResourceMetadata(Lists.newArrayList((Object[])new AnimationFrameResourceMetadata[]{new AnimationFrameResourceMetadata(0, -1)}), 16, 16, 1, false));
    }
}

