/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SignType {
    private static final Set<SignType> VALUES = new ObjectArraySet();
    public static final SignType OAK = SignType.register(new SignType("oak"));
    public static final SignType SPRUCE = SignType.register(new SignType("spruce"));
    public static final SignType BIRCH = SignType.register(new SignType("birch"));
    public static final SignType ACACIA = SignType.register(new SignType("acacia"));
    public static final SignType JUNGLE = SignType.register(new SignType("jungle"));
    public static final SignType DARK_OAK = SignType.register(new SignType("dark_oak"));
    public static final SignType CRIMSON = SignType.register(new SignType("crimson"));
    public static final SignType WARPED = SignType.register(new SignType("warped"));
    private final String name;

    protected SignType(String string) {
        this.name = string;
    }

    private static SignType register(SignType arg) {
        VALUES.add(arg);
        return arg;
    }

    @Environment(value=EnvType.CLIENT)
    public static Stream<SignType> stream() {
        return VALUES.stream();
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return this.name;
    }
}

