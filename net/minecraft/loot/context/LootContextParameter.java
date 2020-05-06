/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.context;

import net.minecraft.util.Identifier;

public class LootContextParameter<T> {
    private final Identifier id;

    public LootContextParameter(Identifier arg) {
        this.id = arg;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public String toString() {
        return "<parameter " + this.id + ">";
    }
}

