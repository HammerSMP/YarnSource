/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.level;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import net.minecraft.world.level.LevelGeneratorOptions;

public class LevelGeneratorType {
    public static final LevelGeneratorType[] TYPES = new LevelGeneratorType[16];
    private static final Dynamic<?> EMPTY_COMPOUND_NBT_DYNAMIC = new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)new CompoundTag());
    public static final LevelGeneratorType DEFAULT = new LevelGeneratorType(0, "default", 1, LevelGeneratorOptions::createDefault).setVersioned();
    public static final LevelGeneratorType FLAT = new LevelGeneratorType(1, "flat", LevelGeneratorOptions::createFlat).setCustomizable(true);
    public static final LevelGeneratorType LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes", LevelGeneratorOptions::createDefault);
    public static final LevelGeneratorType AMPLIFIED = new LevelGeneratorType(3, "amplified", LevelGeneratorOptions::createDefault).setHasInfo();
    public static final LevelGeneratorType CUSTOMIZED = new LevelGeneratorType(4, "customized", "normal", 0, LevelGeneratorOptions::createDefault).setCustomizable(true).setVisible(false);
    public static final LevelGeneratorType BUFFET = new LevelGeneratorType(5, "buffet", LevelGeneratorOptions::createBuffet).setCustomizable(true);
    public static final LevelGeneratorType DEBUG_ALL_BLOCK_STATES = new LevelGeneratorType(6, "debug_all_block_states", LevelGeneratorOptions::createDebug);
    public static final LevelGeneratorType DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0, LevelGeneratorOptions::createDefault).setVisible(false);
    private final int id;
    private final String name;
    private final String storedName;
    private final int version;
    private final Function<Dynamic<?>, LevelGeneratorOptions> optionsLoader;
    private final Lazy<LevelGeneratorOptions> defaultOptions;
    private boolean visible;
    private boolean versioned;
    private final Text field_24108;
    private boolean info;
    private final Text field_24109;
    private boolean customizable;

    private LevelGeneratorType(int i, String string, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> biFunction) {
        this(i, string, string, 0, biFunction);
    }

    private LevelGeneratorType(int i, String string, int j, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> biFunction) {
        this(i, string, string, j, biFunction);
    }

    private LevelGeneratorType(int i, String string, String string2, int j, BiFunction<LevelGeneratorType, Dynamic<?>, LevelGeneratorOptions> biFunction) {
        this.name = string;
        this.storedName = string2;
        this.version = j;
        this.optionsLoader = dynamic -> (LevelGeneratorOptions)biFunction.apply(this, (Dynamic<?>)dynamic);
        this.defaultOptions = new Lazy<LevelGeneratorOptions>(() -> (LevelGeneratorOptions)biFunction.apply(this, EMPTY_COMPOUND_NBT_DYNAMIC));
        this.visible = true;
        this.id = i;
        LevelGeneratorType.TYPES[i] = this;
        this.field_24108 = new TranslatableText("generator." + string);
        this.field_24109 = new TranslatableText("generator." + string + ".info");
    }

    public String getName() {
        return this.name;
    }

    public String getStoredName() {
        return this.storedName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getTranslationKey() {
        return this.field_24108;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getInfoTranslationKey() {
        return this.field_24109;
    }

    public int getVersion() {
        return this.version;
    }

    public LevelGeneratorType getTypeForVersion(int i) {
        if (this == DEFAULT && i == 0) {
            return DEFAULT_1_1;
        }
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCustomizable() {
        return this.customizable;
    }

    public LevelGeneratorType setCustomizable(boolean bl) {
        this.customizable = bl;
        return this;
    }

    private LevelGeneratorType setVisible(boolean bl) {
        this.visible = bl;
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isVisible() {
        return this.visible;
    }

    private LevelGeneratorType setVersioned() {
        this.versioned = true;
        return this;
    }

    public boolean isVersioned() {
        return this.versioned;
    }

    @Nullable
    public static LevelGeneratorType getTypeFromName(String string) {
        for (LevelGeneratorType lv : TYPES) {
            if (lv == null || !lv.name.equalsIgnoreCase(string)) continue;
            return lv;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasInfo() {
        return this.info;
    }

    private LevelGeneratorType setHasInfo() {
        this.info = true;
        return this;
    }

    public LevelGeneratorOptions loadOptions(Dynamic<?> dynamic) {
        return this.optionsLoader.apply(dynamic);
    }

    public LevelGeneratorOptions getDefaultOptions() {
        return this.defaultOptions.get();
    }
}

