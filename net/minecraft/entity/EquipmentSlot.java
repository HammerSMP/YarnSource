/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

public enum EquipmentSlot {
    MAINHAND(Type.HAND, 0, 0, "mainhand"),
    OFFHAND(Type.HAND, 1, 5, "offhand"),
    FEET(Type.ARMOR, 0, 1, "feet"),
    LEGS(Type.ARMOR, 1, 2, "legs"),
    CHEST(Type.ARMOR, 2, 3, "chest"),
    HEAD(Type.ARMOR, 3, 4, "head");

    private final Type type;
    private final int entityId;
    private final int armorStandId;
    private final String name;

    private EquipmentSlot(Type arg, int j, int k, String string2) {
        this.type = arg;
        this.entityId = j;
        this.armorStandId = k;
        this.name = string2;
    }

    public Type getType() {
        return this.type;
    }

    public int getEntitySlotId() {
        return this.entityId;
    }

    public int getArmorStandSlotId() {
        return this.armorStandId;
    }

    public String getName() {
        return this.name;
    }

    public static EquipmentSlot byName(String string) {
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            if (!lv.getName().equals(string)) continue;
            return lv;
        }
        throw new IllegalArgumentException("Invalid slot '" + string + "'");
    }

    public static EquipmentSlot fromTypeIndex(Type arg, int i) {
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            if (lv.getType() != arg || lv.getEntitySlotId() != i) continue;
            return lv;
        }
        throw new IllegalArgumentException("Invalid slot '" + (Object)((Object)arg) + "': " + i);
    }

    public static enum Type {
        HAND,
        ARMOR;

    }
}

