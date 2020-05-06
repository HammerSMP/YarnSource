/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.boss;

import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class BossBar {
    private final UUID uuid;
    protected Text name;
    protected float percent;
    protected Color color;
    protected Style style;
    protected boolean darkenSky;
    protected boolean dragonMusic;
    protected boolean thickenFog;

    public BossBar(UUID uUID, Text arg, Color arg2, Style arg3) {
        this.uuid = uUID;
        this.name = arg;
        this.color = arg2;
        this.style = arg3;
        this.percent = 1.0f;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Text getName() {
        return this.name;
    }

    public void setName(Text arg) {
        this.name = arg;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float f) {
        this.percent = f;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color arg) {
        this.color = arg;
    }

    public Style getOverlay() {
        return this.style;
    }

    public void setOverlay(Style arg) {
        this.style = arg;
    }

    public boolean getDarkenSky() {
        return this.darkenSky;
    }

    public BossBar setDarkenSky(boolean bl) {
        this.darkenSky = bl;
        return this;
    }

    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    public BossBar setDragonMusic(boolean bl) {
        this.dragonMusic = bl;
        return this;
    }

    public BossBar setThickenFog(boolean bl) {
        this.thickenFog = bl;
        return this;
    }

    public boolean getThickenFog() {
        return this.thickenFog;
    }

    public static enum Style {
        PROGRESS("progress"),
        NOTCHED_6("notched_6"),
        NOTCHED_10("notched_10"),
        NOTCHED_12("notched_12"),
        NOTCHED_20("notched_20");

        private final String name;

        private Style(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Style byName(String string) {
            for (Style lv : Style.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            return PROGRESS;
        }
    }

    public static enum Color {
        PINK("pink", Formatting.RED),
        BLUE("blue", Formatting.BLUE),
        RED("red", Formatting.DARK_RED),
        GREEN("green", Formatting.GREEN),
        YELLOW("yellow", Formatting.YELLOW),
        PURPLE("purple", Formatting.DARK_BLUE),
        WHITE("white", Formatting.WHITE);

        private final String name;
        private final Formatting format;

        private Color(String string2, Formatting arg) {
            this.name = string2;
            this.format = arg;
        }

        public Formatting getTextFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        public static Color byName(String string) {
            for (Color lv : Color.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            return WHITE;
        }
    }
}

