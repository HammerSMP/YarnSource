/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.Version
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  oshi.SystemInfo
 *  oshi.hardware.Processor
 */
package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@Environment(value=EnvType.CLIENT)
public class GLX {
    private static final Logger LOGGER = LogManager.getLogger();
    private static String capsString = "";
    private static String cpuInfo;
    private static final Map<Integer, String> LOOKUP_MAP;

    public static String getOpenGLVersionString() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
    }

    public static int _getRefreshRate(Window window) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        long l = GLFW.glfwGetWindowMonitor((long)window.getHandle());
        if (l == 0L) {
            l = GLFW.glfwGetPrimaryMonitor();
        }
        GLFWVidMode gLFWVidMode = l == 0L ? null : GLFW.glfwGetVideoMode((long)l);
        return gLFWVidMode == null ? 0 : gLFWVidMode.refreshRate();
    }

    public static String _getLWJGLVersion() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return Version.getVersion();
    }

    /*
     * WARNING - void declaration
     */
    public static LongSupplier _initGlfw() {
        void longSupplier2;
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        Window.acceptError((integer, string) -> {
            throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", integer, string));
        });
        ArrayList list = Lists.newArrayList();
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((i, l) -> list.add(String.format("GLFW error during init: [0x%X]%s", i, l)));
        if (GLFW.glfwInit()) {
            LongSupplier longSupplier = () -> (long)(GLFW.glfwGetTime() * 1.0E9);
            for (String string2 : list) {
                LOGGER.error("GLFW error collected during initialization: {}", (Object)string2);
            }
        } else {
            throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on((String)",").join((Iterable)list));
        }
        RenderSystem.setErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        return longSupplier2;
    }

    public static void _setGlfwErrorCallback(GLFWErrorCallbackI callback) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)callback);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public static boolean _shouldClose(Window window) {
        return GLFW.glfwWindowShouldClose((long)window.getHandle());
    }

    public static void _setupNvFogDistance() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (GL.getCapabilities().GL_NV_fog_distance) {
            GlStateManager.fogi(34138, 34139);
        }
    }

    public static void _init(int debugVerbosity, boolean debugSync) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLCapabilities gLCapabilities = GL.getCapabilities();
        capsString = "Using framebuffer using " + GlStateManager.initFramebufferSupport(gLCapabilities);
        try {
            Processor[] processors = new SystemInfo().getHardware().getProcessors();
            cpuInfo = String.format("%dx %s", new Object[]{processors.length, processors[0]}).replaceAll("\\s+", " ");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        GlDebug.enableDebug(debugVerbosity, debugSync);
    }

    public static String _getCapsString() {
        return capsString;
    }

    public static String _getCpuInfo() {
        return cpuInfo == null ? "<unknown>" : cpuInfo;
    }

    public static void _renderCrosshair(int size, boolean drawX, boolean drawY, boolean drawZ) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        Tessellator lv = RenderSystem.renderThreadTesselator();
        BufferBuilder lv2 = lv.getBuffer();
        GL11.glLineWidth((float)4.0f);
        lv2.begin(1, VertexFormats.POSITION_COLOR);
        if (drawX) {
            lv2.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            lv2.vertex(size, 0.0, 0.0).color(0, 0, 0, 255).next();
        }
        if (drawY) {
            lv2.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            lv2.vertex(0.0, size, 0.0).color(0, 0, 0, 255).next();
        }
        if (drawZ) {
            lv2.vertex(0.0, 0.0, 0.0).color(0, 0, 0, 255).next();
            lv2.vertex(0.0, 0.0, size).color(0, 0, 0, 255).next();
        }
        lv.draw();
        GL11.glLineWidth((float)2.0f);
        lv2.begin(1, VertexFormats.POSITION_COLOR);
        if (drawX) {
            lv2.vertex(0.0, 0.0, 0.0).color(255, 0, 0, 255).next();
            lv2.vertex(size, 0.0, 0.0).color(255, 0, 0, 255).next();
        }
        if (drawY) {
            lv2.vertex(0.0, 0.0, 0.0).color(0, 255, 0, 255).next();
            lv2.vertex(0.0, size, 0.0).color(0, 255, 0, 255).next();
        }
        if (drawZ) {
            lv2.vertex(0.0, 0.0, 0.0).color(127, 127, 255, 255).next();
            lv2.vertex(0.0, 0.0, size).color(127, 127, 255, 255).next();
        }
        lv.draw();
        GL11.glLineWidth((float)1.0f);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    public static String getErrorString(int code) {
        return LOOKUP_MAP.get(code);
    }

    public static <T> T make(Supplier<T> factory) {
        return factory.get();
    }

    public static <T> T make(T object, Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

    static {
        LOOKUP_MAP = GLX.make(Maps.newHashMap(), hashMap -> {
            hashMap.put(0, "No error");
            hashMap.put(1280, "Enum parameter is invalid for this function");
            hashMap.put(1281, "Parameter is invalid for this function");
            hashMap.put(1282, "Current state is invalid for this function");
            hashMap.put(1283, "Stack overflow");
            hashMap.put(1284, "Stack underflow");
            hashMap.put(1285, "Out of memory");
            hashMap.put(1286, "Operation on incomplete framebuffer");
            hashMap.put(1286, "Operation on incomplete framebuffer");
        });
    }
}

