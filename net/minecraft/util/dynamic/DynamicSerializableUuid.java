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

    public static UUID toUuid(int[] array) {
        return new UUID((long)array[0] << 32 | (long)array[1] & 0xFFFFFFFFL, (long)array[2] << 32 | (long)array[3] & 0xFFFFFFFFL);
    }

    public static int[] toIntArray(UUID uuid) {
        long l = uuid.getMostSignificantBits();
        long m = uuid.getLeastSignificantBits();
        return DynamicSerializableUuid.toIntArray(l, m);
    }

    private static int[] toIntArray(long uuidMost, long uuidLeast) {
        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
    }
}

