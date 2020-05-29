/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureHolder {
    @Nullable
    public StructureStart<?> getStructureStart(StructureFeature<?> var1);

    public void setStructureStart(StructureFeature<?> var1, StructureStart<?> var2);

    public LongSet getStructureReferences(StructureFeature<?> var1);

    public void addStructureReference(StructureFeature<?> var1, long var2);

    public Map<StructureFeature<?>, LongSet> getStructureReferences();

    public void setStructureReferences(Map<StructureFeature<?>, LongSet> var1);
}

