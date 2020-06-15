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
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceTexture
extends AbstractTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Identifier location;

    public ResourceTexture(Identifier arg) {
        this.location = arg;
    }

    @Override
    public void load(ResourceManager arg) throws IOException {
        boolean bl4;
        boolean bl3;
        TextureData lv = this.loadTextureData(arg);
        lv.checkException();
        TextureResourceMetadata lv2 = lv.getMetadata();
        if (lv2 != null) {
            boolean bl = lv2.shouldBlur();
            boolean bl2 = lv2.shouldClamp();
        } else {
            bl3 = false;
            bl4 = false;
        }
        NativeImage lv3 = lv.getImage();
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.upload(lv3, bl3, bl4));
        } else {
            this.upload(lv3, bl3, bl4);
        }
    }

    private void upload(NativeImage arg, boolean bl, boolean bl2) {
        TextureUtil.allocate(this.getGlId(), 0, arg.getWidth(), arg.getHeight());
        arg.upload(0, 0, 0, 0, 0, arg.getWidth(), arg.getHeight(), bl, bl2, false, true);
    }

    protected TextureData loadTextureData(ResourceManager arg) {
        return TextureData.load(arg, this.location);
    }

    @Environment(value=EnvType.CLIENT)
    public static class TextureData
    implements Closeable {
        @Nullable
        private final TextureResourceMetadata metadata;
        @Nullable
        private final NativeImage image;
        @Nullable
        private final IOException exception;

        public TextureData(IOException iOException) {
            this.exception = iOException;
            this.metadata = null;
            this.image = null;
        }

        public TextureData(@Nullable TextureResourceMetadata arg, NativeImage arg2) {
            this.exception = null;
            this.metadata = arg;
            this.image = arg2;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public static TextureData load(ResourceManager arg, Identifier arg2) {
            try (Resource lv = arg.getResource(arg2);){
                NativeImage lv2 = NativeImage.read(lv.getInputStream());
                TextureResourceMetadata lv3 = null;
                try {
                    lv3 = lv.getMetadata(TextureResourceMetadata.READER);
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.warn("Failed reading metadata of: {}", (Object)arg2, (Object)runtimeException);
                }
                TextureData textureData = new TextureData(lv3, lv2);
                return textureData;
            }
            catch (IOException iOException) {
                return new TextureData(iOException);
            }
        }

        @Nullable
        public TextureResourceMetadata getMetadata() {
            return this.metadata;
        }

        public NativeImage getImage() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
            return this.image;
        }

        @Override
        public void close() {
            if (this.image != null) {
                this.image.close();
            }
        }

        public void checkException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

