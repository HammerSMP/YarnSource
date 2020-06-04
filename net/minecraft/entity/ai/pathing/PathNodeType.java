/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.pathing;

public enum PathNodeType {
    BLOCKED(-1.0f),
    OPEN(0.0f),
    WALKABLE(0.0f),
    TRAPDOOR(0.0f),
    FENCE(-1.0f),
    LAVA(-1.0f),
    WATER(8.0f),
    WATER_BORDER(8.0f),
    RAIL(0.0f),
    UNPASSABLE_RAIL(-1.0f),
    DANGER_FIRE(8.0f),
    DAMAGE_FIRE(16.0f),
    DANGER_CACTUS(8.0f),
    DAMAGE_CACTUS(-1.0f),
    DANGER_OTHER(8.0f),
    DAMAGE_OTHER(-1.0f),
    DOOR_OPEN(0.0f),
    DOOR_WOOD_CLOSED(-1.0f),
    DOOR_IRON_CLOSED(-1.0f),
    BREACH(4.0f),
    LEAVES(-1.0f),
    STICKY_HONEY(8.0f),
    COCOA(0.0f);

    private final float defaultPenalty;

    private PathNodeType(float f) {
        this.defaultPenalty = f;
    }

    public float getDefaultPenalty() {
        return this.defaultPenalty;
    }
}

