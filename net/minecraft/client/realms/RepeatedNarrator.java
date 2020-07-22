/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<Parameters> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(String message) {
        Parameters lv = this.params.updateAndGet(arg -> {
            if (arg == null || !message.equals(((Parameters)arg).message)) {
                return new Parameters(message, RateLimiter.create((double)this.permitsPerSecond));
            }
            return arg;
        });
        if (lv.rateLimiter.tryAcquire(1)) {
            NarratorManager.INSTANCE.onChatMessage(MessageType.SYSTEM, new LiteralText(message), Util.NIL_UUID);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        private final String message;
        private final RateLimiter rateLimiter;

        Parameters(String message, RateLimiter rateLimiter) {
            this.message = message;
            this.rateLimiter = rateLimiter;
        }
    }
}

