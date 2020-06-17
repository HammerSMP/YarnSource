/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(value=EnvType.CLIENT)
public final class Window
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create((arg_0, arg_1) -> this.logGlError(arg_0, arg_1));
    private final WindowEventHandler eventHandler;
    private final MonitorTracker monitorTracker;
    private final long handle;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> videoMode;
    private boolean fullscreen;
    private boolean currentFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int scaledWidth;
    private int scaledHeight;
    private double scaleFactor;
    private String phase = "";
    private boolean videoModeDirty;
    private int framerateLimit;
    private boolean vsync;

    public Window(WindowEventHandler arg, MonitorTracker arg2, WindowSettings arg3, @Nullable String string, String string2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.monitorTracker = arg2;
        this.throwOnGlError();
        this.setPhase("Pre startup");
        this.eventHandler = arg;
        Optional<VideoMode> optional = VideoMode.fromString(string);
        this.videoMode = optional.isPresent() ? optional : (arg3.fullscreenWidth.isPresent() && arg3.fullscreenHeight.isPresent() ? Optional.of(new VideoMode(arg3.fullscreenWidth.getAsInt(), arg3.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.currentFullscreen = this.fullscreen = arg3.fullscreen;
        Monitor lv = arg2.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = arg3.width > 0 ? arg3.width : 1;
        this.windowedWidth = this.width;
        this.height = arg3.height > 0 ? arg3.height : 1;
        this.windowedHeight = this.height;
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint((int)139265, (int)196609);
        GLFW.glfwWindowHint((int)139275, (int)221185);
        GLFW.glfwWindowHint((int)139266, (int)2);
        GLFW.glfwWindowHint((int)139267, (int)0);
        GLFW.glfwWindowHint((int)139272, (int)0);
        this.handle = GLFW.glfwCreateWindow((int)this.width, (int)this.height, (CharSequence)string2, (long)(this.fullscreen && lv != null ? lv.getHandle() : 0L), (long)0L);
        if (lv != null) {
            VideoMode lv2 = lv.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
            this.windowedX = this.x = lv.getViewportX() + lv2.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = lv.getViewportY() + lv2.getHeight() / 2 - this.height / 2;
        } else {
            int[] is = new int[1];
            int[] js = new int[1];
            GLFW.glfwGetWindowPos((long)this.handle, (int[])is, (int[])js);
            this.windowedX = this.x = is[0];
            this.windowedY = this.y = js[0];
        }
        GLFW.glfwMakeContextCurrent((long)this.handle);
        GL.createCapabilities();
        this.updateWindowRegion();
        this.updateFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback((long)this.handle, (arg_0, arg_1, arg_2) -> this.onFramebufferSizeChanged(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowPosCallback((long)this.handle, (arg_0, arg_1, arg_2) -> this.onWindowPosChanged(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowSizeCallback((long)this.handle, (arg_0, arg_1, arg_2) -> this.onWindowSizeChanged(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowFocusCallback((long)this.handle, (arg_0, arg_1) -> this.onWindowFocusChanged(arg_0, arg_1));
        GLFW.glfwSetCursorEnterCallback((long)this.handle, (arg_0, arg_1) -> this.method_30132(arg_0, arg_1));
    }

    public int getRefreshRate() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GLX._getRefreshRate(this);
    }

    public boolean shouldClose() {
        return GLX._shouldClose(this);
    }

    public static void acceptError(BiConsumer<Integer, String> biConsumer) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int i = GLFW.glfwGetError((PointerBuffer)pointerBuffer);
            if (i != 0) {
                long l = pointerBuffer.get();
                String string = l == 0L ? "" : MemoryUtil.memUTF8((long)l);
                biConsumer.accept(i, string);
            }
        }
    }

    public void setIcon(InputStream inputStream, InputStream inputStream2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            if (inputStream == null) {
                throw new FileNotFoundException("icons/icon_16x16.png");
            }
            if (inputStream2 == null) {
                throw new FileNotFoundException("icons/icon_32x32.png");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            GLFWImage.Buffer buffer = GLFWImage.mallocStack((int)2, (MemoryStack)memoryStack);
            ByteBuffer byteBuffer = this.readImage(inputStream, intBuffer, intBuffer2, intBuffer3);
            if (byteBuffer == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer.position(0);
            buffer.width(intBuffer.get(0));
            buffer.height(intBuffer2.get(0));
            buffer.pixels(byteBuffer);
            ByteBuffer byteBuffer2 = this.readImage(inputStream2, intBuffer, intBuffer2, intBuffer3);
            if (byteBuffer2 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer.position(1);
            buffer.width(intBuffer.get(0));
            buffer.height(intBuffer2.get(0));
            buffer.pixels(byteBuffer2);
            buffer.position(0);
            GLFW.glfwSetWindowIcon((long)this.handle, (GLFWImage.Buffer)buffer);
            STBImage.stbi_image_free((ByteBuffer)byteBuffer);
            STBImage.stbi_image_free((ByteBuffer)byteBuffer2);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private ByteBuffer readImage(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readAllToByteBuffer(inputStream);
            byteBuffer.rewind();
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)0);
            return byteBuffer2;
        }
        finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
    }

    public void setPhase(String string) {
        this.phase = string;
    }

    private void throwOnGlError() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLFW.glfwSetErrorCallback(Window::throwGlError);
    }

    private static void throwGlError(int i, long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        String string = "GLFW error " + i + ": " + MemoryUtil.memUTF8((long)l);
        TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)(string + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        throw new GlErroredException(string);
    }

    public void logGlError(int i, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        String string = MemoryUtil.memUTF8((long)l);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.phase);
        LOGGER.error("{}: {}", (Object)i, (Object)string);
    }

    public void logOnGlError() {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.errorCallback);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public void setVsync(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.vsync = bl;
        GLFW.glfwSwapInterval((int)(bl ? 1 : 0));
    }

    @Override
    public void close() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Callbacks.glfwFreeCallbacks((long)this.handle);
        this.errorCallback.close();
        GLFW.glfwDestroyWindow((long)this.handle);
        GLFW.glfwTerminate();
    }

    private void onWindowPosChanged(long l, int i, int j) {
        this.x = i;
        this.y = j;
    }

    private void onFramebufferSizeChanged(long l, int i, int j) {
        if (l != this.handle) {
            return;
        }
        int k = this.getFramebufferWidth();
        int m = this.getFramebufferHeight();
        if (i == 0 || j == 0) {
            return;
        }
        this.framebufferWidth = i;
        this.framebufferHeight = j;
        if (this.getFramebufferWidth() != k || this.getFramebufferHeight() != m) {
            this.eventHandler.onResolutionChanged();
        }
    }

    private void updateFramebufferSize() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        int[] is = new int[1];
        int[] js = new int[1];
        GLFW.glfwGetFramebufferSize((long)this.handle, (int[])is, (int[])js);
        this.framebufferWidth = is[0];
        this.framebufferHeight = js[0];
    }

    private void onWindowSizeChanged(long l, int i, int j) {
        this.width = i;
        this.height = j;
    }

    private void onWindowFocusChanged(long l, boolean bl) {
        if (l == this.handle) {
            this.eventHandler.onWindowFocusChanged(bl);
        }
    }

    private void method_30132(long l, boolean bl) {
        if (bl) {
            this.eventHandler.method_30133();
        }
    }

    public void setFramerateLimit(int i) {
        this.framerateLimit = i;
    }

    public int getFramerateLimit() {
        return this.framerateLimit;
    }

    public void swapBuffers() {
        RenderSystem.flipFrame(this.handle);
        if (this.fullscreen != this.currentFullscreen) {
            this.currentFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync);
        }
    }

    public Optional<VideoMode> getVideoMode() {
        return this.videoMode;
    }

    public void setVideoMode(Optional<VideoMode> optional) {
        boolean bl = !optional.equals(this.videoMode);
        this.videoMode = optional;
        if (bl) {
            this.videoModeDirty = true;
        }
    }

    public void applyVideoMode() {
        if (this.fullscreen && this.videoModeDirty) {
            this.videoModeDirty = false;
            this.updateWindowRegion();
            this.eventHandler.onResolutionChanged();
        }
    }

    private void updateWindowRegion() {
        boolean bl;
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        boolean bl2 = bl = GLFW.glfwGetWindowMonitor((long)this.handle) != 0L;
        if (this.fullscreen) {
            Monitor lv = this.monitorTracker.getMonitor(this);
            if (lv == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                VideoMode lv2 = lv.findClosestVideoMode(this.videoMode);
                if (!bl) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = lv2.getWidth();
                this.height = lv2.getHeight();
                GLFW.glfwSetWindowMonitor((long)this.handle, (long)lv.getHandle(), (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)lv2.getRefreshRate());
            }
        } else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor((long)this.handle, (long)0L, (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)-1);
        }
    }

    public void toggleFullscreen() {
        this.fullscreen = !this.fullscreen;
    }

    private void updateFullscreen(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        try {
            this.updateWindowRegion();
            this.eventHandler.onResolutionChanged();
            this.setVsync(bl);
            this.swapBuffers();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    public int calculateScaleFactor(int i, boolean bl) {
        int j;
        for (j = 1; j != i && j < this.framebufferWidth && j < this.framebufferHeight && this.framebufferWidth / (j + 1) >= 320 && this.framebufferHeight / (j + 1) >= 240; ++j) {
        }
        if (bl && j % 2 != 0) {
            ++j;
        }
        return j;
    }

    public void setScaleFactor(double d) {
        this.scaleFactor = d;
        int i = (int)((double)this.framebufferWidth / d);
        this.scaledWidth = (double)this.framebufferWidth / d > (double)i ? i + 1 : i;
        int j = (int)((double)this.framebufferHeight / d);
        this.scaledHeight = (double)this.framebufferHeight / d > (double)j ? j + 1 : j;
    }

    public void setTitle(String string) {
        GLFW.glfwSetWindowTitle((long)this.handle, (CharSequence)string);
    }

    public long getHandle() {
        return this.handle;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }

    @Nullable
    public Monitor getMonitor() {
        return this.monitorTracker.getMonitor(this);
    }

    public void setRawMouseMotion(boolean bl) {
        InputUtil.setRawMouseMotionMode(this.handle, bl);
    }

    @Environment(value=EnvType.CLIENT)
    public static class GlErroredException
    extends GlException {
        private GlErroredException(String string) {
            super(string);
        }
    }
}

