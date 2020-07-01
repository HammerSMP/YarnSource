/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

public class VanillaDataPackProvider
implements ResourcePackProvider {
    private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory arg) {
        ResourcePackProfile lv = ResourcePackProfile.of("vanilla", false, () -> this.pack, arg, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN);
        if (lv != null) {
            consumer.accept(lv);
        }
    }
}

