/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.Map;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

public class VanillaDataPackProvider
implements ResourcePackProvider {
    private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

    @Override
    public <T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> arg) {
        T lv = ResourcePackProfile.of("vanilla", false, () -> this.pack, arg, ResourcePackProfile.InsertionPosition.BOTTOM);
        if (lv != null) {
            map.put("vanilla", lv);
        }
    }
}

