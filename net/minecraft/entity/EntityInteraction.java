/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity;

public interface EntityInteraction {
    public static final EntityInteraction ZOMBIE_VILLAGER_CURED = EntityInteraction.create("zombie_villager_cured");
    public static final EntityInteraction GOLEM_KILLED = EntityInteraction.create("golem_killed");
    public static final EntityInteraction VILLAGER_HURT = EntityInteraction.create("villager_hurt");
    public static final EntityInteraction VILLAGER_KILLED = EntityInteraction.create("villager_killed");
    public static final EntityInteraction TRADE = EntityInteraction.create("trade");

    public static EntityInteraction create(final String key) {
        return new EntityInteraction(){

            public String toString() {
                return key;
            }
        };
    }
}

