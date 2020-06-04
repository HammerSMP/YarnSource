/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.resource.ResourcePackProfile;

public interface ResourcePackProvider {
    public <T extends ResourcePackProfile> void register(Consumer<T> var1, ResourcePackProfile.class_5351<T> var2);
}

