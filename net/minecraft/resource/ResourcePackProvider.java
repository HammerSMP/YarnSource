/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.Map;
import net.minecraft.resource.ResourcePackProfile;

public interface ResourcePackProvider {
    public <T extends ResourcePackProfile> void register(Map<String, T> var1, ResourcePackProfile.Factory<T> var2);
}

