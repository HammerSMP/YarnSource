/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.util.UUIDTypeAdapter
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.realms.util.SkinProcessor;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> textures = Maps.newHashMap();
    private static final Map<String, Boolean> skinFetchStatus = Maps.newHashMap();
    private static final Map<String, String> fetchedSkins = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier field_22730 = new Identifier("textures/gui/presets/isles.png");

    public static void bindWorldTemplate(String string, @Nullable String string2) {
        if (string2 == null) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(field_22730);
            return;
        }
        int i = RealmsTextureManager.getTextureId(string, string2);
        RenderSystem.bindTexture(i);
    }

    public static void withBoundFace(String string, Runnable runnable) {
        RenderSystem.pushTextureAttributes();
        try {
            RealmsTextureManager.bindFace(string);
            runnable.run();
        }
        finally {
            RenderSystem.popAttributes();
        }
    }

    private static void bindDefaultFace(UUID uUID) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultSkinHelper.getTexture(uUID));
    }

    private static void bindFace(final String string) {
        UUID uUID = UUIDTypeAdapter.fromString((String)string);
        if (textures.containsKey(string)) {
            RenderSystem.bindTexture(textures.get(string).textureId);
            return;
        }
        if (skinFetchStatus.containsKey(string)) {
            if (!skinFetchStatus.get(string).booleanValue()) {
                RealmsTextureManager.bindDefaultFace(uUID);
            } else if (fetchedSkins.containsKey(string)) {
                int i = RealmsTextureManager.getTextureId(string, fetchedSkins.get(string));
                RenderSystem.bindTexture(i);
            } else {
                RealmsTextureManager.bindDefaultFace(uUID);
            }
            return;
        }
        skinFetchStatus.put(string, false);
        RealmsTextureManager.bindDefaultFace(uUID);
        Thread thread = new Thread("Realms Texture Downloader"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                block17: {
                    block16: {
                        ByteArrayOutputStream byteArrayOutputStream;
                        BufferedImage bufferedImage2;
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(string);
                        if (!map.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) break block16;
                        MinecraftProfileTexture minecraftProfileTexture = map.get((Object)MinecraftProfileTexture.Type.SKIN);
                        String string2 = minecraftProfileTexture.getUrl();
                        HttpURLConnection httpURLConnection = null;
                        LOGGER.debug("Downloading http texture from {}", (Object)string2);
                        try {
                            httpURLConnection = (HttpURLConnection)new URL(string2).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 != 2) {
                                skinFetchStatus.remove(string);
                                return;
                            }
                            try {
                                BufferedImage bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
                            }
                            catch (Exception exception) {
                                skinFetchStatus.remove(string);
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                return;
                            }
                            finally {
                                IOUtils.closeQuietly((InputStream)httpURLConnection.getInputStream());
                            }
                            bufferedImage2 = new SkinProcessor().process(bufferedImage2);
                            byteArrayOutputStream = new ByteArrayOutputStream();
                        }
                        catch (Exception exception2) {
                            LOGGER.error("Couldn't download http texture", (Throwable)exception2);
                            skinFetchStatus.remove(string);
                        }
                        finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }
                        ImageIO.write((RenderedImage)bufferedImage2, "png", byteArrayOutputStream);
                        fetchedSkins.put(string, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
                        skinFetchStatus.put(string, true);
                        break block17;
                    }
                    skinFetchStatus.put(string, true);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private static int getTextureId(String string, String string2) {
        int j;
        if (textures.containsKey(string)) {
            RealmsTexture lv = textures.get(string);
            if (lv.image.equals(string2)) {
                return lv.textureId;
            }
            RenderSystem.deleteTexture(lv.textureId);
            int i = lv.textureId;
        } else {
            j = GlStateManager.genTextures();
        }
        IntBuffer intBuffer = null;
        int k = 0;
        int l = 0;
        try {
            void bufferedImage2;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new Base64().decode(string2));
            try {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
            }
            finally {
                IOUtils.closeQuietly((InputStream)inputStream);
            }
            k = bufferedImage2.getWidth();
            l = bufferedImage2.getHeight();
            int[] is = new int[k * l];
            bufferedImage2.getRGB(0, 0, k, l, is, 0, k);
            intBuffer = ByteBuffer.allocateDirect(4 * k * l).order(ByteOrder.nativeOrder()).asIntBuffer();
            intBuffer.put(is);
            intBuffer.flip();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(j);
        TextureUtil.uploadImage(intBuffer, k, l);
        textures.put(string, new RealmsTexture(string2, j));
        return j;
    }

    @Environment(value=EnvType.CLIENT)
    public static class RealmsTexture {
        private final String image;
        private final int textureId;

        public RealmsTexture(String string, int i) {
            this.image = string;
            this.textureId = i;
        }
    }
}

