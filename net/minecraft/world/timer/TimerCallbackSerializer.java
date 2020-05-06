/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.timer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.TimerCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackSerializer<C> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TimerCallbackSerializer<MinecraftServer> INSTANCE = new TimerCallbackSerializer<MinecraftServer>().registerSerializer(new FunctionTimerCallback.Serializer()).registerSerializer(new FunctionTagTimerCallback.Serializer());
    private final Map<Identifier, TimerCallback.Serializer<C, ?>> serializersByType = Maps.newHashMap();
    private final Map<Class<?>, TimerCallback.Serializer<C, ?>> serializersByClass = Maps.newHashMap();

    @VisibleForTesting
    public TimerCallbackSerializer() {
    }

    public TimerCallbackSerializer<C> registerSerializer(TimerCallback.Serializer<C, ?> arg) {
        this.serializersByType.put(arg.getId(), arg);
        this.serializersByClass.put(arg.getCallbackClass(), arg);
        return this;
    }

    private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> arg) {
        return this.serializersByClass.get(arg);
    }

    public <T extends TimerCallback<C>> CompoundTag serialize(T arg) {
        TimerCallback.Serializer<T, T> lv = this.getSerializer(arg.getClass());
        CompoundTag lv2 = new CompoundTag();
        lv.serialize(lv2, arg);
        lv2.putString("Type", lv.getId().toString());
        return lv2;
    }

    @Nullable
    public TimerCallback<C> deserialize(CompoundTag arg) {
        Identifier lv = Identifier.tryParse(arg.getString("Type"));
        TimerCallback.Serializer<C, ?> lv2 = this.serializersByType.get(lv);
        if (lv2 == null) {
            LOGGER.error("Failed to deserialize timer callback: " + arg);
            return null;
        }
        try {
            return lv2.deserialize(arg);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to deserialize timer callback: " + arg, (Throwable)exception);
            return null;
        }
    }
}

