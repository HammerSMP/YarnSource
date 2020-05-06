/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinTexture
extends ResourceTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private final File cacheFile;
    private final String url;
    private final boolean convertLegacy;
    @Nullable
    private final Runnable loadedCallback;
    @Nullable
    private CompletableFuture<?> loader;
    private boolean loaded;

    public PlayerSkinTexture(@Nullable File file, String string, Identifier arg, boolean bl, @Nullable Runnable runnable) {
        super(arg);
        this.cacheFile = file;
        this.url = string;
        this.convertLegacy = bl;
        this.loadedCallback = runnable;
    }

    private void onTextureLoaded(NativeImage arg) {
        if (this.loadedCallback != null) {
            this.loadedCallback.run();
        }
        MinecraftClient.getInstance().execute(() -> {
            this.loaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.uploadTexture(arg));
            } else {
                this.uploadTexture(arg);
            }
        });
    }

    private void uploadTexture(NativeImage arg) {
        TextureUtil.method_24958(this.getGlId(), arg.getWidth(), arg.getHeight());
        arg.upload(0, 0, 0, true);
    }

    @Override
    public void load(ResourceManager arg) throws IOException {
        NativeImage lv2;
        MinecraftClient.getInstance().execute(() -> {
            if (!this.loaded) {
                try {
                    super.load(arg);
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to load texture: {}", (Object)this.location, (Object)iOException);
                }
                this.loaded = true;
            }
        });
        if (this.loader != null) {
            return;
        }
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
            FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
            NativeImage lv = this.loadTexture(fileInputStream);
        } else {
            lv2 = null;
        }
        if (lv2 != null) {
            this.onTextureLoaded(lv2);
            return;
        }
        this.loader = CompletableFuture.runAsync(() -> {
            HttpURLConnection httpURLConnection = null;
            LOGGER.debug("Downloading http texture from {} to {}", (Object)this.url, (Object)this.cacheFile);
            try {
                InputStream inputStream2;
                httpURLConnection = (HttpURLConnection)new URL(this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 != 2) {
                    return;
                }
                if (this.cacheFile != null) {
                    FileUtils.copyInputStreamToFile((InputStream)httpURLConnection.getInputStream(), (File)this.cacheFile);
                    FileInputStream inputStream = new FileInputStream(this.cacheFile);
                } else {
                    inputStream2 = httpURLConnection.getInputStream();
                }
                MinecraftClient.getInstance().execute(() -> {
                    NativeImage lv = this.loadTexture(inputStream2);
                    if (lv != null) {
                        this.onTextureLoaded(lv);
                    }
                });
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't download http texture", (Throwable)exception);
            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }, Util.getServerWorkerExecutor());
    }

    @Nullable
    private NativeImage loadTexture(InputStream inputStream) {
        NativeImage lv = null;
        try {
            lv = NativeImage.read(inputStream);
            if (this.convertLegacy) {
                lv = PlayerSkinTexture.remapTexture(lv);
            }
        }
        catch (IOException iOException) {
            LOGGER.warn("Error while loading the skin texture", (Throwable)iOException);
        }
        return lv;
    }

    private static NativeImage remapTexture(NativeImage arg) {
        boolean bl;
        boolean bl2 = bl = arg.getHeight() == 32;
        if (bl) {
            NativeImage lv = new NativeImage(64, 64, true);
            lv.copyFrom(arg);
            arg.close();
            arg = lv;
            arg.fillRect(0, 32, 64, 32, 0);
            arg.copyRect(4, 16, 16, 32, 4, 4, true, false);
            arg.copyRect(8, 16, 16, 32, 4, 4, true, false);
            arg.copyRect(0, 20, 24, 32, 4, 12, true, false);
            arg.copyRect(4, 20, 16, 32, 4, 12, true, false);
            arg.copyRect(8, 20, 8, 32, 4, 12, true, false);
            arg.copyRect(12, 20, 16, 32, 4, 12, true, false);
            arg.copyRect(44, 16, -8, 32, 4, 4, true, false);
            arg.copyRect(48, 16, -8, 32, 4, 4, true, false);
            arg.copyRect(40, 20, 0, 32, 4, 12, true, false);
            arg.copyRect(44, 20, -8, 32, 4, 12, true, false);
            arg.copyRect(48, 20, -16, 32, 4, 12, true, false);
            arg.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        PlayerSkinTexture.stripAlpha(arg, 0, 0, 32, 16);
        if (bl) {
            PlayerSkinTexture.stripColor(arg, 32, 0, 64, 32);
        }
        PlayerSkinTexture.stripAlpha(arg, 0, 16, 64, 32);
        PlayerSkinTexture.stripAlpha(arg, 16, 48, 48, 64);
        return arg;
    }

    private static void stripColor(NativeImage arg, int i, int j, int k, int l) {
        for (int m = i; m < k; ++m) {
            for (int n = j; n < l; ++n) {
                int o = arg.getPixelRgba(m, n);
                if ((o >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int p = i; p < k; ++p) {
            for (int q = j; q < l; ++q) {
                arg.setPixelRgba(p, q, arg.getPixelRgba(p, q) & 0xFFFFFF);
            }
        }
    }

    private static void stripAlpha(NativeImage arg, int i, int j, int k, int l) {
        for (int m = i; m < k; ++m) {
            for (int n = j; n < l; ++n) {
                arg.setPixelRgba(m, n, arg.getPixelRgba(m, n) | 0xFF000000);
            }
        }
    }
}

