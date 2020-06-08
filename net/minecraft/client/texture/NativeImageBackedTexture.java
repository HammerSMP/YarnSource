/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;

@Environment(value=EnvType.CLIENT)
public class NativeImageBackedTexture
extends AbstractTexture
implements AutoCloseable {
    private NativeImage image;

    public NativeImageBackedTexture(NativeImage arg) {
        this.image = arg;
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
                this.upload();
            });
        } else {
            TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
            this.upload();
        }
    }

    public NativeImageBackedTexture(int i, int j, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        this.image = new NativeImage(i, j, bl);
        TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
    }

    @Override
    public void load(ResourceManager arg) throws IOException {
    }

    public void upload() {
        this.bindTexture();
        this.image.upload(0, 0, 0, false);
    }

    @Nullable
    public NativeImage getImage() {
        return this.image;
    }

    public void setImage(NativeImage arg) throws Exception {
        this.image.close();
        this.image = arg;
    }

    @Override
    public void close() {
        this.image.close();
        this.clearGlId();
        this.image = null;
    }
}

