/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

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

    public void narrate(String string) {
        Parameters lv = this.params.updateAndGet(arg -> {
            if (arg == null || !string.equals(((Parameters)arg).message)) {
                return new Parameters(string, RateLimiter.create((double)this.permitsPerSecond));
            }
            return arg;
        });
        if (lv.rateLimiter.tryAcquire(1)) {
            NarratorManager.INSTANCE.onChatMessage(MessageType.SYSTEM, new LiteralText(string), Util.field_25140);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        private final String message;
        private final RateLimiter rateLimiter;

        Parameters(String string, RateLimiter rateLimiter) {
            this.message = string;
            this.rateLimiter = rateLimiter;
        }
    }
}

