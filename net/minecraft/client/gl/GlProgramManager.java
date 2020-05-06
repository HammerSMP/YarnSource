/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlProgram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlProgramManager {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void useProgram(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.useProgram(i);
    }

    public static void deleteProgram(GlProgram arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        arg.getFragmentShader().release();
        arg.getVertexShader().release();
        GlStateManager.deleteProgram(arg.getProgramRef());
    }

    public static int createProgram() throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        int i = GlStateManager.createProgram();
        if (i <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + i + ")");
        }
        return i;
    }

    public static void linkProgram(GlProgram arg) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        arg.getFragmentShader().attachTo(arg);
        arg.getVertexShader().attachTo(arg);
        GlStateManager.linkProgram(arg.getProgramRef());
        int i = GlStateManager.getProgram(arg.getProgramRef(), 35714);
        if (i == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", (Object)arg.getVertexShader().getName(), (Object)arg.getFragmentShader().getName());
            LOGGER.warn(GlStateManager.getProgramInfoLog(arg.getProgramRef(), 32768));
        }
    }
}

