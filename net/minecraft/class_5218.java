/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

public class class_5218 {
    public static final class_5218 ADVANCEMENTS = new class_5218("advancements");
    public static final class_5218 STATS = new class_5218("stats");
    public static final class_5218 PLAYERDATA = new class_5218("playerdata");
    public static final class_5218 PLAYERS = new class_5218("players");
    public static final class_5218 LEVEL_DAT = new class_5218("level.dat");
    public static final class_5218 GENERATED = new class_5218("generated");
    public static final class_5218 DATAPACKS = new class_5218("datapacks");
    public static final class_5218 RESOURCES_ZIP = new class_5218("resources.zip");
    public static final class_5218 field_24188 = new class_5218(".");
    private final String field_24189;

    private class_5218(String string) {
        this.field_24189 = string;
    }

    public String method_27423() {
        return this.field_24189;
    }

    public String toString() {
        return "/" + this.field_24189;
    }
}

