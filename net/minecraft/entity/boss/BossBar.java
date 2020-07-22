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

    public BossBar(UUID uuid, Text name, Color color, Style style) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.style = style;
        this.percent = 1.0f;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Text getName() {
        return this.name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public float getPercent() {
        return this.percent;
    }

    public void setPercent(float percentage) {
        this.percent = percentage;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Style getOverlay() {
        return this.style;
    }

    public void setOverlay(Style style) {
        this.style = style;
    }

    public boolean getDarkenSky() {
        return this.darkenSky;
    }

    public BossBar setDarkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }

    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    public BossBar setDragonMusic(boolean dragonMusic) {
        this.dragonMusic = dragonMusic;
        return this;
    }

    public BossBar setThickenFog(boolean thickenFog) {
        this.thickenFog = thickenFog;
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

        private Style(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static Style byName(String name) {
            for (Style lv : Style.values()) {
                if (!lv.name.equals(name)) continue;
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

        private Color(String name, Formatting format) {
            this.name = name;
            this.format = format;
        }

        public Formatting getTextFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        public static Color byName(String name) {
            for (Color lv : Color.values()) {
                if (!lv.name.equals(name)) continue;
                return lv;
            }
            return WHITE;
        }
    }
}

