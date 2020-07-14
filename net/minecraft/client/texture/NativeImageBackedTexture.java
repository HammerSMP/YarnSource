/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NativeImageBackedTexture
extends AbstractTexture {
    private static final Logger field_25794 = LogManager.getLogger();
    @Nullable
    private NativeImage image;

    public NativeImageBackedTexture(NativeImage image) {
        this.image = image;
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

    public NativeImageBackedTexture(int width, int height, boolean useStb) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        this.image = new NativeImage(width, height, useStb);
        TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
    }

    @Override
    public void load(ResourceManager manager) {
    }

    public void upload() {
        if (this.image != null) {
            this.bindTexture();
            this.image.upload(0, 0, 0, false);
        } else {
            field_25794.warn("Trying to upload disposed texture {}", (Object)this.getGlId());
        }
    }

    @Nullable
    public NativeImage getImage() {
        return this.image;
    }

    public void setImage(NativeImage image) {
        if (this.image != null) {
            this.image.close();
        }
        this.image = image;
    }

    @Override
    public void close() {
        if (this.image != null) {
            this.image.close();
            this.clearGlId();
            this.image = null;
        }
    }
}

