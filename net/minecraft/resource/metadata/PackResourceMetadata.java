/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource.metadata;

import net.minecraft.resource.metadata.PackResourceMetadataReader;
import net.minecraft.text.Text;

public class PackResourceMetadata {
    public static final PackResourceMetadataReader READER = new PackResourceMetadataReader();
    private final Text description;
    private final int packFormat;

    public PackResourceMetadata(Text arg, int i) {
        this.description = arg;
        this.packFormat = i;
    }

    public Text getDescription() {
        return this.description;
    }

    public int getPackFormat() {
        return this.packFormat;
    }
}

