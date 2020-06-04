/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

public class WorldSavePath {
    public static final WorldSavePath ADVANCEMENTS = new WorldSavePath("advancements");
    public static final WorldSavePath STATS = new WorldSavePath("stats");
    public static final WorldSavePath PLAYERDATA = new WorldSavePath("playerdata");
    public static final WorldSavePath PLAYERS = new WorldSavePath("players");
    public static final WorldSavePath LEVEL_DAT = new WorldSavePath("level.dat");
    public static final WorldSavePath GENERATED = new WorldSavePath("generated");
    public static final WorldSavePath DATAPACKS = new WorldSavePath("datapacks");
    public static final WorldSavePath RESOURCES_ZIP = new WorldSavePath("resources.zip");
    public static final WorldSavePath ROOT = new WorldSavePath(".");
    private final String relativePath;

    private WorldSavePath(String string) {
        this.relativePath = string;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String toString() {
        return "/" + this.relativePath;
    }
}
