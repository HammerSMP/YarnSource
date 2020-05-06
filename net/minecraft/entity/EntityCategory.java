/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntityCategory {
    MONSTER("monster", 70, false, 128),
    CREATURE("creature", 10, true),
    AMBIENT("ambient", 15, true, 128),
    WATER_CREATURE("water_creature", 5, true, 128),
    WATER_AMBIENT("water_ambient", 20, true, 64),
    MISC("misc", -1, true);

    private static final Map<String, EntityCategory> BY_NAME;
    private final int spawnCap;
    private final boolean peaceful;
    private final boolean animal;
    private final String name;
    private final int field_24461 = 32;
    private final int field_24462;

    private EntityCategory(String string2, int j, boolean bl) {
        this.name = string2;
        this.spawnCap = j;
        this.peaceful = bl;
        this.animal = true;
        this.field_24462 = Integer.MAX_VALUE;
    }

    private EntityCategory(String string2, int j, boolean bl, int k) {
        this.name = string2;
        this.spawnCap = j;
        this.peaceful = bl;
        this.animal = false;
        this.field_24462 = k;
    }

    public String getName() {
        return this.name;
    }

    public int getSpawnCap() {
        return this.spawnCap;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }

    public boolean isAnimal() {
        return this.animal;
    }

    public int method_27919() {
        return this.field_24462;
    }

    public int method_27920() {
        return 32;
    }

    static {
        BY_NAME = Arrays.stream(EntityCategory.values()).collect(Collectors.toMap(EntityCategory::getName, arg -> arg));
    }
}

