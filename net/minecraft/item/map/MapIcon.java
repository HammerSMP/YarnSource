/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item.map;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class MapIcon {
    private final Type type;
    private byte x;
    private byte z;
    private byte rotation;
    private final Text text;

    public MapIcon(Type arg, byte b, byte c, byte d, @Nullable Text arg2) {
        this.type = arg;
        this.x = b;
        this.z = c;
        this.rotation = d;
        this.text = arg2;
    }

    @Environment(value=EnvType.CLIENT)
    public byte getTypeId() {
        return this.type.getId();
    }

    public Type getType() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getZ() {
        return this.z;
    }

    public byte getRotation() {
        return this.rotation;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isAlwaysRendered() {
        return this.type.isAlwaysRendered();
    }

    @Nullable
    public Text getText() {
        return this.text;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof MapIcon)) {
            return false;
        }
        MapIcon lv = (MapIcon)object;
        if (this.type != lv.type) {
            return false;
        }
        if (this.rotation != lv.rotation) {
            return false;
        }
        if (this.x != lv.x) {
            return false;
        }
        if (this.z != lv.z) {
            return false;
        }
        return Objects.equals(this.text, lv.text);
    }

    public int hashCode() {
        int i = this.type.getId();
        i = 31 * i + this.x;
        i = 31 * i + this.z;
        i = 31 * i + this.rotation;
        i = 31 * i + Objects.hashCode(this.text);
        return i;
    }

    public static enum Type {
        PLAYER(false),
        FRAME(true),
        RED_MARKER(false),
        BLUE_MARKER(false),
        TARGET_X(true),
        TARGET_POINT(true),
        PLAYER_OFF_MAP(false),
        PLAYER_OFF_LIMITS(false),
        MANSION(true, 5393476),
        MONUMENT(true, 3830373),
        BANNER_WHITE(true),
        BANNER_ORANGE(true),
        BANNER_MAGENTA(true),
        BANNER_LIGHT_BLUE(true),
        BANNER_YELLOW(true),
        BANNER_LIME(true),
        BANNER_PINK(true),
        BANNER_GRAY(true),
        BANNER_LIGHT_GRAY(true),
        BANNER_CYAN(true),
        BANNER_PURPLE(true),
        BANNER_BLUE(true),
        BANNER_BROWN(true),
        BANNER_GREEN(true),
        BANNER_RED(true),
        BANNER_BLACK(true),
        RED_X(true);

        private final byte id = (byte)this.ordinal();
        private final boolean alwaysRender;
        private final int tintColor;

        private Type(boolean bl) {
            this(bl, -1);
        }

        private Type(boolean bl, int j) {
            this.alwaysRender = bl;
            this.tintColor = j;
        }

        public byte getId() {
            return this.id;
        }

        @Environment(value=EnvType.CLIENT)
        public boolean isAlwaysRendered() {
            return this.alwaysRender;
        }

        public boolean hasTintColor() {
            return this.tintColor >= 0;
        }

        public int getTintColor() {
            return this.tintColor;
        }

        public static Type byId(byte b) {
            return Type.values()[MathHelper.clamp(b, 0, Type.values().length - 1)];
        }
    }
}

