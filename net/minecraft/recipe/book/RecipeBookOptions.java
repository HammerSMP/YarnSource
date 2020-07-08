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
package net.minecraft.recipe.book;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Util;

public final class RecipeBookOptions {
    private static final Map<RecipeBookCategory, Pair<String, String>> CATEGORY_OPTION_NAMES = ImmutableMap.of((Object)((Object)RecipeBookCategory.CRAFTING), (Object)Pair.of((Object)"isGuiOpen", (Object)"isFilteringCraftable"), (Object)((Object)RecipeBookCategory.FURNACE), (Object)Pair.of((Object)"isFurnaceGuiOpen", (Object)"isFurnaceFilteringCraftable"), (Object)((Object)RecipeBookCategory.BLAST_FURNACE), (Object)Pair.of((Object)"isBlastingFurnaceGuiOpen", (Object)"isBlastingFurnaceFilteringCraftable"), (Object)((Object)RecipeBookCategory.SMOKER), (Object)Pair.of((Object)"isSmokerGuiOpen", (Object)"isSmokerFilteringCraftable"));
    private final Map<RecipeBookCategory, CategoryOption> categoryOptions;

    private RecipeBookOptions(Map<RecipeBookCategory, CategoryOption> map) {
        this.categoryOptions = map;
    }

    public RecipeBookOptions() {
        this(Util.make(Maps.newEnumMap(RecipeBookCategory.class), enumMap -> {
            for (RecipeBookCategory lv : RecipeBookCategory.values()) {
                enumMap.put(lv, new CategoryOption(false, false));
            }
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen(RecipeBookCategory arg) {
        return this.categoryOptions.get((Object)arg).guiOpen;
    }

    public void setGuiOpen(RecipeBookCategory arg, boolean bl) {
        this.categoryOptions.get((Object)arg).guiOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(RecipeBookCategory arg) {
        return this.categoryOptions.get((Object)arg).filteringCraftable;
    }

    public void setFilteringCraftable(RecipeBookCategory arg, boolean bl) {
        this.categoryOptions.get((Object)arg).filteringCraftable = bl;
    }

    public static RecipeBookOptions fromPacket(PacketByteBuf arg) {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory lv : RecipeBookCategory.values()) {
            boolean bl = arg.readBoolean();
            boolean bl2 = arg.readBoolean();
            map.put(lv, new CategoryOption(bl, bl2));
        }
        return new RecipeBookOptions(map);
    }

    public void toPacket(PacketByteBuf arg) {
        for (RecipeBookCategory lv : RecipeBookCategory.values()) {
            CategoryOption lv2 = this.categoryOptions.get((Object)lv);
            if (lv2 == null) {
                arg.writeBoolean(false);
                arg.writeBoolean(false);
                continue;
            }
            arg.writeBoolean(lv2.guiOpen);
            arg.writeBoolean(lv2.filteringCraftable);
        }
    }

    public static RecipeBookOptions fromTag(CompoundTag arg) {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        CATEGORY_OPTION_NAMES.forEach((arg2, pair) -> {
            boolean bl = arg.getBoolean((String)pair.getFirst());
            boolean bl2 = arg.getBoolean((String)pair.getSecond());
            map.put(arg2, new CategoryOption(bl, bl2));
        });
        return new RecipeBookOptions(map);
    }

    public void toTag(CompoundTag arg) {
        CATEGORY_OPTION_NAMES.forEach((arg2, pair) -> {
            CategoryOption lv = this.categoryOptions.get(arg2);
            arg.putBoolean((String)pair.getFirst(), lv.guiOpen);
            arg.putBoolean((String)pair.getSecond(), lv.filteringCraftable);
        });
    }

    public RecipeBookOptions copy() {
        EnumMap map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory lv : RecipeBookCategory.values()) {
            CategoryOption lv2 = this.categoryOptions.get((Object)lv);
            map.put(lv, lv2.copy());
        }
        return new RecipeBookOptions(map);
    }

    public void copyFrom(RecipeBookOptions arg) {
        this.categoryOptions.clear();
        for (RecipeBookCategory lv : RecipeBookCategory.values()) {
            CategoryOption lv2 = arg.categoryOptions.get((Object)lv);
            this.categoryOptions.put(lv, lv2.copy());
        }
    }

    public boolean equals(Object object) {
        return this == object || object instanceof RecipeBookOptions && this.categoryOptions.equals(((RecipeBookOptions)object).categoryOptions);
    }

    public int hashCode() {
        return this.categoryOptions.hashCode();
    }

    static final class CategoryOption {
        private boolean guiOpen;
        private boolean filteringCraftable;

        public CategoryOption(boolean bl, boolean bl2) {
            this.guiOpen = bl;
            this.filteringCraftable = bl2;
        }

        public CategoryOption copy() {
            return new CategoryOption(this.guiOpen, this.filteringCraftable);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof CategoryOption) {
                CategoryOption lv = (CategoryOption)object;
                return this.guiOpen == lv.guiOpen && this.filteringCraftable == lv.filteringCraftable;
            }
            return false;
        }

        public int hashCode() {
            int i = this.guiOpen ? 1 : 0;
            i = 31 * i + (this.filteringCraftable ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + ']';
        }
    }
}

