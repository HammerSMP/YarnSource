/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;

@Environment(value=EnvType.CLIENT)
public final class WindowProvider
implements AutoCloseable {
    private final MinecraftClient client;
    private final MonitorTracker monitorTracker;

    public WindowProvider(MinecraftClient arg) {
        this.client = arg;
        this.monitorTracker = new MonitorTracker(Monitor::new);
    }

    public Window createWindow(WindowSettings arg, @Nullable String string, String string2) {
        return new Window(this.client, this.monitorTracker, arg, string, string2);
    }

    @Override
    public void close() {
        this.monitorTracker.stop();
    }
}

