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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TextureUtil {
    private static final Logger field_22547 = LogManager.getLogger();

    public static int method_24956() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GlStateManager.genTextures();
    }

    public static void method_24957(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.deleteTexture(i);
    }

    public static void method_24958(int i, int j, int k) {
        TextureUtil.method_24961(NativeImage.GLFormat.RGBA, i, 0, j, k);
    }

    public static void method_24960(NativeImage.GLFormat arg, int i, int j, int k) {
        TextureUtil.method_24961(arg, i, 0, j, k);
    }

    public static void method_24959(int i, int j, int k, int l) {
        TextureUtil.method_24961(NativeImage.GLFormat.RGBA, i, j, k, l);
    }

    public static void method_24961(NativeImage.GLFormat arg, int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        TextureUtil.method_24964(i);
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

    private static void method_24964(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(i);
    }

    public static ByteBuffer method_24962(InputStream inputStream) throws IOException {
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
    public static String method_24965(InputStream inputStream) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.method_24962(inputStream);
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

    public static void method_24963(IntBuffer intBuffer, int i, int j) {
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
