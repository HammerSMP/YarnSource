/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.Searchable;

@Environment(value=EnvType.CLIENT)
public interface SearchableContainer<T>
extends Searchable<T> {
    public void add(T var1);

    public void clear();

    public void reload();
}

