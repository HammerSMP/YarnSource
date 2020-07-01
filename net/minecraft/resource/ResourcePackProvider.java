/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.resource.ResourcePackProfile;

public interface ResourcePackProvider {
    public void register(Consumer<ResourcePackProfile> var1, ResourcePackProfile.Factory var2);
}

