/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class DynamicSerializableUuid {
    public static final Codec<UUID> field_25122 = Codec.INT_STREAM.comapFlatMap(intStream -> Util.toIntArray(intStream, 4).map(DynamicSerializableUuid::toUuid), uUID -> Arrays.stream(DynamicSerializableUuid.toIntArray(uUID)));

    public static UUID toUuid(int[] is) {
        return new UUID((long)is[0] << 32 | (long)is[1] & 0xFFFFFFFFL, (long)is[2] << 32 | (long)is[3] & 0xFFFFFFFFL);
    }

    public static int[] toIntArray(UUID uUID) {
        long l = uUID.getMostSignificantBits();
        long m = uUID.getLeastSignificantBits();
        return DynamicSerializableUuid.toIntArray(l, m);
    }

    private static int[] toIntArray(long l, long m) {
        return new int[]{(int)(l >> 32), (int)l, (int)(m >> 32), (int)m};
    }
}

