/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5421;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

public final class class_5411 {
    private static final Map<class_5421, Pair<String, String>> field_25735 = ImmutableMap.of((Object)((Object)class_5421.CRAFTING), (Object)Pair.of((Object)"isGuiOpen", (Object)"isFilteringCraftable"), (Object)((Object)class_5421.FURNACE), (Object)Pair.of((Object)"isFurnaceGuiOpen", (Object)"isFurnaceFilteringCraftable"), (Object)((Object)class_5421.BLAST_FURNACE), (Object)Pair.of((Object)"isBlastingFurnaceGuiOpen", (Object)"isBlastingFurnaceFilteringCraftable"), (Object)((Object)class_5421.SMOKER), (Object)Pair.of((Object)"isSmokerGuiOpen", (Object)"isSmokerFilteringCraftable"));
    private final Map<class_5421, class_5412> field_25736;

    private class_5411(Map<class_5421, class_5412> map) {
        this.field_25736 = map;
    }

    public class_5411() {
        this(Util.make(Maps.newEnumMap(class_5421.class), enumMap -> {
            for (class_5421 lv : class_5421.values()) {
                enumMap.put(lv, new class_5412(false, false));
            }
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30180(class_5421 arg) {
        return this.field_25736.get((Object)arg).field_25737;
    }

    public void method_30181(class_5421 arg, boolean bl) {
        this.field_25736.get((Object)arg).field_25737 = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30187(class_5421 arg) {
        return this.field_25736.get((Object)arg).field_25738;
    }

    public void method_30188(class_5421 arg, boolean bl) {
        this.field_25736.get((Object)arg).field_25738 = bl;
    }

    public static class_5411 method_30186(PacketByteBuf arg) {
        EnumMap map = Maps.newEnumMap(class_5421.class);
        for (class_5421 lv : class_5421.values()) {
            boolean bl = arg.readBoolean();
            boolean bl2 = arg.readBoolean();
            map.put(lv, new class_5412(bl, bl2));
        }
        return new class_5411(map);
    }

    public void method_30190(PacketByteBuf arg) {
        for (class_5421 lv : class_5421.values()) {
            class_5412 lv2 = this.field_25736.get((Object)lv);
            if (lv2 == null) {
                arg.writeBoolean(false);
                arg.writeBoolean(false);
                continue;
            }
            arg.writeBoolean(lv2.field_25737);
            arg.writeBoolean(lv2.field_25738);
        }
    }

    public static class_5411 method_30183(CompoundTag arg) {
        EnumMap map = Maps.newEnumMap(class_5421.class);
        field_25735.forEach((arg2, pair) -> {
            boolean bl = arg.getBoolean((String)pair.getFirst());
            boolean bl2 = arg.getBoolean((String)pair.getSecond());
            map.put(arg2, new class_5412(bl, bl2));
        });
        return new class_5411(map);
    }

    public void method_30189(CompoundTag arg) {
        field_25735.forEach((arg2, pair) -> {
            class_5412 lv = this.field_25736.get(arg2);
            arg.putBoolean((String)pair.getFirst(), lv.field_25737);
            arg.putBoolean((String)pair.getSecond(), lv.field_25738);
        });
    }

    public class_5411 method_30178() {
        EnumMap map = Maps.newEnumMap(class_5421.class);
        for (class_5421 lv : class_5421.values()) {
            class_5412 lv2 = this.field_25736.get((Object)lv);
            map.put(lv, lv2.method_30191());
        }
        return new class_5411(map);
    }

    public void method_30179(class_5411 arg) {
        this.field_25736.clear();
        for (class_5421 lv : class_5421.values()) {
            class_5412 lv2 = arg.field_25736.get((Object)lv);
            this.field_25736.put(lv, lv2.method_30191());
        }
    }

    public boolean equals(Object object) {
        return this == object || object instanceof class_5411 && this.field_25736.equals(((class_5411)object).field_25736);
    }

    public int hashCode() {
        return this.field_25736.hashCode();
    }

    static final class class_5412 {
        private boolean field_25737;
        private boolean field_25738;

        public class_5412(boolean bl, boolean bl2) {
            this.field_25737 = bl;
            this.field_25738 = bl2;
        }

        public class_5412 method_30191() {
            return new class_5412(this.field_25737, this.field_25738);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof class_5412) {
                class_5412 lv = (class_5412)object;
                return this.field_25737 == lv.field_25737 && this.field_25738 == lv.field_25738;
            }
            return false;
        }

        public int hashCode() {
            int i = this.field_25737 ? 1 : 0;
            i = 31 * i + (this.field_25738 ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.field_25737 + ", filtering=" + this.field_25738 + ']';
        }
    }
}

