/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.snooper.SnooperListener;

public class Snooper {
    private final Map<String, Object> initialInfo = Maps.newHashMap();
    private final Map<String, Object> info = Maps.newHashMap();
    private final String token = UUID.randomUUID().toString();
    private final URL snooperUrl;
    private final SnooperListener listener;
    private final Timer timer = new Timer("Snooper Timer", true);
    private final Object syncObject = new Object();
    private final long startTime;
    private boolean active;

    public Snooper(String string, SnooperListener arg, long l) {
        try {
            this.snooperUrl = new URL("http://snoop.minecraft.net/" + string + "?version=" + 2);
        }
        catch (MalformedURLException malformedURLException) {
            throw new IllegalArgumentException();
        }
        this.listener = arg;
        this.startTime = l;
    }

    public void method_5482() {
        if (!this.active) {
            // empty if block
        }
    }

    public void update() {
        this.addInitialInfo("memory_total", Runtime.getRuntime().totalMemory());
        this.addInitialInfo("memory_max", Runtime.getRuntime().maxMemory());
        this.addInitialInfo("memory_free", Runtime.getRuntime().freeMemory());
        this.addInitialInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.listener.addSnooperInfo(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInfo(String string, Object object) {
        Object object2 = this.syncObject;
        synchronized (object2) {
            this.info.put(string, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInitialInfo(String string, Object object) {
        Object object2 = this.syncObject;
        synchronized (object2) {
            this.initialInfo.put(string, object);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void cancel() {
        this.timer.cancel();
    }

    @Environment(value=EnvType.CLIENT)
    public String getToken() {
        return this.token;
    }

    public long getStartTime() {
        return this.startTime;
    }
}

