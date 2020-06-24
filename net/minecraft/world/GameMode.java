/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum GameMode {
    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator");

    private final int id;
    private final String name;

    private GameMode(int j, String string2) {
        this.id = j;
        this.name = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Text getTranslatableName() {
        return new TranslatableText("gameMode." + this.name);
    }

    public void setAbilities(PlayerAbilities arg) {
        if (this == CREATIVE) {
            arg.allowFlying = true;
            arg.creativeMode = true;
            arg.invulnerable = true;
        } else if (this == SPECTATOR) {
            arg.allowFlying = true;
            arg.creativeMode = false;
            arg.invulnerable = true;
            arg.flying = true;
        } else {
            arg.allowFlying = false;
            arg.creativeMode = false;
            arg.invulnerable = false;
            arg.flying = false;
        }
        arg.allowModifyWorld = !this.isBlockBreakingRestricted();
    }

    public boolean isBlockBreakingRestricted() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvivalLike() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameMode byId(int i) {
        return GameMode.byId(i, SURVIVAL);
    }

    public static GameMode byId(int i, GameMode arg) {
        for (GameMode lv : GameMode.values()) {
            if (lv.id != i) continue;
            return lv;
        }
        return arg;
    }

    public static GameMode byName(String string) {
        return GameMode.byName(string, SURVIVAL);
    }

    public static GameMode byName(String string, GameMode arg) {
        for (GameMode lv : GameMode.values()) {
            if (!lv.name.equals(string)) continue;
            return lv;
        }
        return arg;
    }
}

