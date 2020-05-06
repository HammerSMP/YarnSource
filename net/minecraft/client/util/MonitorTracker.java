/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWMonitorCallback
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorFactory;
import net.minecraft.client.util.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

@Environment(value=EnvType.CLIENT)
public class MonitorTracker {
    private final Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap();
    private final MonitorFactory monitorFactory;

    public MonitorTracker(MonitorFactory arg) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.monitorFactory = arg;
        GLFW.glfwSetMonitorCallback((arg_0, arg_1) -> this.handleMonitorEvent(arg_0, arg_1));
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; i < pointerBuffer.limit(); ++i) {
                long l = pointerBuffer.get(i);
                this.pointerToMonitorMap.put(l, (Object)arg.createMonitor(l));
            }
        }
    }

    private void handleMonitorEvent(long l, int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i == 262145) {
            this.pointerToMonitorMap.put(l, (Object)this.monitorFactory.createMonitor(l));
        } else if (i == 262146) {
            this.pointerToMonitorMap.remove(l);
        }
    }

    @Nullable
    public Monitor getMonitor(long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return (Monitor)this.pointerToMonitorMap.get(l);
    }

    @Nullable
    public Monitor getMonitor(Window arg) {
        long l = GLFW.glfwGetWindowMonitor((long)arg.getHandle());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        int i = arg.getX();
        int j = i + arg.getWidth();
        int k = arg.getY();
        int m = k + arg.getHeight();
        int n = -1;
        Monitor lv = null;
        for (Monitor lv2 : this.pointerToMonitorMap.values()) {
            int x;
            int o = lv2.getViewportX();
            int p = o + lv2.getCurrentVideoMode().getWidth();
            int q = lv2.getViewportY();
            int r = q + lv2.getCurrentVideoMode().getHeight();
            int s = MonitorTracker.clamp(i, o, p);
            int t = MonitorTracker.clamp(j, o, p);
            int u = MonitorTracker.clamp(k, q, r);
            int v = MonitorTracker.clamp(m, q, r);
            int w = Math.max(0, t - s);
            int y = w * (x = Math.max(0, v - u));
            if (y <= n) continue;
            lv = lv2;
            n = y;
        }
        return lv;
    }

    public static int clamp(int i, int j, int k) {
        if (i < j) {
            return j;
        }
        if (i > k) {
            return k;
        }
        return i;
    }

    public void stop() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

