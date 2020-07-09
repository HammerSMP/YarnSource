/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TextureUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static int generateId() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (SharedConstants.isDevelopment) {
            int[] is = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManager.method_30498(is);
            int i = GlStateManager.genTextures();
            GlStateManager.method_30499(is);
            return i;
        }
        return GlStateManager.genTextures();
    }

    public static void deleteId(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.deleteTexture(i);
    }

    public static void allocate(int i, int j, int k) {
        TextureUtil.allocate(NativeImage.GLFormat.ABGR, i, 0, j, k);
    }

    public static void allocate(NativeImage.GLFormat arg, int i, int j, int k) {
        TextureUtil.allocate(arg, i, 0, j, k);
    }

    public static void allocate(int i, int j, int k, int l) {
        TextureUtil.allocate(NativeImage.GLFormat.ABGR, i, j, k, l);
    }

    public static void allocate(NativeImage.GLFormat arg, int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        TextureUtil.bind(i);
        if (j >= 0) {
            GlStateManager.texParameter(3553, 33085, j);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, j);
            GlStateManager.texParameter(3553, 34049, 0.0f);
        }
        for (int m = 0; m <= j; ++m) {
            GlStateManager.texImage2D(3553, m, arg.getGlConstant(), k >> m, l >> m, 0, 6408, 5121, null);
        }
    }

    private static void bind(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(i);
    }

    public static ByteBuffer readAllToByteBuffer(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer2;
        if (inputStream instanceof FileInputStream) {
            FileInputStream fileInputStream = (FileInputStream)inputStream;
            FileChannel fileChannel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)((int)fileChannel.size() + 1));
            while (fileChannel.read(byteBuffer) != -1) {
            }
        } else {
            byteBuffer2 = MemoryUtil.memAlloc((int)8192);
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
            while (readableByteChannel.read(byteBuffer2) != -1) {
                if (byteBuffer2.remaining() != 0) continue;
                byteBuffer2 = MemoryUtil.memRealloc((ByteBuffer)byteBuffer2, (int)(byteBuffer2.capacity() * 2));
            }
        }
        return byteBuffer2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String readAllToString(InputStream inputStream) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readAllToByteBuffer(inputStream);
            int i = byteBuffer.position();
            byteBuffer.rewind();
            String string = MemoryUtil.memASCII((ByteBuffer)byteBuffer, (int)i);
            return string;
        }
        catch (IOException iOException) {
        }
        finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
        return null;
    }

    public static void uploadImage(IntBuffer intBuffer, int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelStorei((int)3312, (int)0);
        GL11.glPixelStorei((int)3313, (int)0);
        GL11.glPixelStorei((int)3314, (int)0);
        GL11.glPixelStorei((int)3315, (int)0);
        GL11.glPixelStorei((int)3316, (int)0);
        GL11.glPixelStorei((int)3317, (int)4);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)i, (int)j, (int)0, (int)32993, (int)33639, (IntBuffer)intBuffer);
        GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
    }
}

