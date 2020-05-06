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

public interface StructureHolder {
    @Nullable
    public StructureStart getStructureStart(String var1);

    public void setStructureStart(String var1, StructureStart var2);

    public LongSet getStructureReferences(String var1);

    public void addStructureReference(String var1, long var2);

    public Map<String, LongSet> getStructureReferences();

    public void setStructureReferences(Map<String, LongSet> var1);
}

