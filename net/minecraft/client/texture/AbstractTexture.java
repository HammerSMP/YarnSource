/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTexture
implements AutoCloseable {
    protected int glId = -1;
    protected boolean bilinear;
    protected boolean mipmap;

    public void setFilter(boolean bilinear, boolean mipmap) {
        int l;
        int k;
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.bilinear = bilinear;
        this.mipmap = mipmap;
        if (bilinear) {
            int i = mipmap ? 9987 : 9729;
            int j = 9729;
        } else {
            k = mipmap ? 9986 : 9728;
            l = 9728;
        }
        GlStateManager.texParameter(3553, 10241, k);
        GlStateManager.texParameter(3553, 10240, l);
    }

    public int getGlId() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (this.glId == -1) {
            this.glId = TextureUtil.generateId();
        }
        return this.glId;
    }

    public void clearGlId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (this.glId != -1) {
                    TextureUtil.deleteId(this.glId);
                    this.glId = -1;
                }
            });
        } else if (this.glId != -1) {
            TextureUtil.deleteId(this.glId);
            this.glId = -1;
        }
    }

    public abstract void load(ResourceManager var1) throws IOException;

    public void bindTexture() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager.bindTexture(this.getGlId()));
        } else {
            GlStateManager.bindTexture(this.getGlId());
        }
    }

    public void registerTexture(TextureManager arg, ResourceManager arg2, Identifier arg3, Executor executor) {
        arg.registerTexture(arg3, this);
    }

    @Override
    public void close() {
    }
}

