/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Lifecycle;
import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5315;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelInfo field_25022;
    private final class_5315 field_25023;
    private final String name;
    private final boolean requiresConversion;
    private final boolean locked;
    private final File file;
    private final Lifecycle generatorType;
    @Nullable
    private Text field_24191;

    public LevelSummary(LevelInfo arg, class_5315 arg2, String string, boolean bl, boolean bl2, File file, Lifecycle lifecycle) {
        this.field_25022 = arg;
        this.field_25023 = arg2;
        this.name = string;
        this.locked = bl2;
        this.file = file;
        this.requiresConversion = bl;
        this.generatorType = lifecycle;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return StringUtils.isEmpty((CharSequence)this.field_25022.getLevelName()) ? this.name : this.field_25022.getLevelName();
    }

    public File getFile() {
        return this.file;
    }

    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    public long getLastPlayed() {
        return this.field_25023.method_29024();
    }

    @Override
    public int compareTo(LevelSummary arg) {
        if (this.field_25023.method_29024() < arg.field_25023.method_29024()) {
            return 1;
        }
        if (this.field_25023.method_29024() > arg.field_25023.method_29024()) {
            return -1;
        }
        return this.name.compareTo(arg.name);
    }

    public GameMode getGameMode() {
        return this.field_25022.getGameMode();
    }

    public boolean isHardcore() {
        return this.field_25022.hasStructures();
    }

    public boolean hasCheats() {
        return this.field_25022.isHardcore();
    }

    public MutableText getVersion() {
        if (ChatUtil.isEmpty(this.field_25023.method_29025())) {
            return new TranslatableText("selectWorld.versionUnknown");
        }
        return new LiteralText(this.field_25023.method_29025());
    }

    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.field_25023.method_29027() || this.isOutdatedLevel() || this.method_29020() || this.isLegacyCustomizedWorld();
    }

    public boolean isFutureLevel() {
        return this.field_25023.method_29026() > SharedConstants.getGameVersion().getWorldVersion();
    }

    public boolean method_29020() {
        return this.field_25022.getGeneratorOptions().isLegacyCustomizedType() && this.field_25023.method_29026() < 1466;
    }

    protected GeneratorOptions method_29021() {
        return this.field_25022.getGeneratorOptions();
    }

    public boolean isLegacyCustomizedWorld() {
        return this.generatorType != Lifecycle.stable();
    }

    public boolean isOutdatedLevel() {
        return this.field_25023.method_29026() < SharedConstants.getGameVersion().getWorldVersion();
    }

    public boolean isLocked() {
        return this.locked;
    }

    public Text method_27429() {
        if (this.field_24191 == null) {
            this.field_24191 = this.method_27430();
        }
        return this.field_24191;
    }

    private Text method_27430() {
        MutableText lv;
        if (this.isLocked()) {
            return new TranslatableText("selectWorld.locked").formatted(Formatting.RED);
        }
        if (this.requiresConversion()) {
            return new TranslatableText("selectWorld.conversion");
        }
        MutableText mutableText = lv = this.isHardcore() ? new LiteralText("").append(new TranslatableText("gameMode.hardcore").formatted(Formatting.DARK_RED)) : new TranslatableText("gameMode." + this.getGameMode().getName());
        if (this.hasCheats()) {
            lv.append(", ").append(new TranslatableText("selectWorld.cheats"));
        }
        MutableText lv2 = this.getVersion();
        MutableText lv3 = new LiteralText(", ").append(new TranslatableText("selectWorld.version")).append(" ");
        if (this.isDifferentVersion()) {
            lv3.append(lv2.formatted(this.isFutureLevel() ? Formatting.RED : Formatting.ITALIC));
        } else {
            lv3.append(lv2);
        }
        lv.append(lv3);
        return lv;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LevelSummary)object);
    }
}

