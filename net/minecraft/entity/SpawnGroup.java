/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SpawnGroup {
    MONSTER("monster", 70, false, 128),
    CREATURE("creature", 10, true),
    AMBIENT("ambient", 15, true, 128),
    WATER_CREATURE("water_creature", 5, true, 128),
    WATER_AMBIENT("water_ambient", 20, true, 64),
    MISC("misc", -1, true);

    private static final Map<String, SpawnGroup> BY_NAME;
    private final int capacity;
    private final boolean peaceful;
    private final boolean animal;
    private final String name;
    private final int despawnStartRange = 32;
    private final int immediateDespawnRange;

    private SpawnGroup(String string2, int j, boolean bl) {
        this.name = string2;
        this.capacity = j;
        this.peaceful = bl;
        this.animal = true;
        this.immediateDespawnRange = Integer.MAX_VALUE;
    }

    private SpawnGroup(String string2, int j, boolean bl, int k) {
        this.name = string2;
        this.capacity = j;
        this.peaceful = bl;
        this.animal = false;
        this.immediateDespawnRange = k;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }

    public boolean isAnimal() {
        return this.animal;
    }

    public int getImmediateDespawnRange() {
        return this.immediateDespawnRange;
    }

    public int getDespawnStartRange() {
        return 32;
    }

    static {
        BY_NAME = Arrays.stream(SpawnGroup.values()).collect(Collectors.toMap(SpawnGroup::getName, arg -> arg));
    }
}

