/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.brain;

import net.minecraft.util.registry.Registry;

public class Activity {
    public static final Activity CORE = Activity.register("core");
    public static final Activity IDLE = Activity.register("idle");
    public static final Activity WORK = Activity.register("work");
    public static final Activity PLAY = Activity.register("play");
    public static final Activity REST = Activity.register("rest");
    public static final Activity MEET = Activity.register("meet");
    public static final Activity PANIC = Activity.register("panic");
    public static final Activity RAID = Activity.register("raid");
    public static final Activity PRE_RAID = Activity.register("pre_raid");
    public static final Activity HIDE = Activity.register("hide");
    public static final Activity FIGHT = Activity.register("fight");
    public static final Activity CELEBRATE = Activity.register("celebrate");
    public static final Activity ADMIRE_ITEM = Activity.register("admire_item");
    public static final Activity AVOID = Activity.register("avoid");
    public static final Activity RIDE = Activity.register("ride");
    private final String id;
    private final int field_23827;

    private Activity(String string) {
        this.id = string;
        this.field_23827 = string.hashCode();
    }

    public String getId() {
        return this.id;
    }

    private static Activity register(String string) {
        return Registry.register(Registry.ACTIVITY, string, new Activity(string));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Activity lv = (Activity)object;
        return this.id.equals(lv.id);
    }

    public int hashCode() {
        return this.field_23827;
    }

    public String toString() {
        return this.getId();
    }
}

