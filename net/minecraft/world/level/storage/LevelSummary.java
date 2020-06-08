/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary
implements Comparable<LevelSummary> {
    private final LevelInfo field_25022;
    private final SaveVersionInfo field_25023;
    private final String name;
    private final boolean requiresConversion;
    private final boolean locked;
    private final File file;
    @Nullable
    @Environment(value=EnvType.CLIENT)
    private Text field_24191;

    public LevelSummary(LevelInfo arg, SaveVersionInfo arg2, String string, boolean bl, boolean bl2, File file) {
        this.field_25022 = arg;
        this.field_25023 = arg2;
        this.name = string;
        this.locked = bl2;
        this.file = file;
        this.requiresConversion = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public String getDisplayName() {
        return StringUtils.isEmpty((CharSequence)this.field_25022.getLevelName()) ? this.name : this.field_25022.getLevelName();
    }

    @Environment(value=EnvType.CLIENT)
    public File getFile() {
        return this.file;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed() {
        return this.field_25023.getLastPlayed();
    }

    @Override
    public int compareTo(LevelSummary arg) {
        if (this.field_25023.getLastPlayed() < arg.field_25023.getLastPlayed()) {
            return 1;
        }
        if (this.field_25023.getLastPlayed() > arg.field_25023.getLastPlayed()) {
            return -1;
        }
        return this.name.compareTo(arg.name);
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.field_25022.getGameMode();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.field_25022.hasStructures();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasCheats() {
        return this.field_25022.isHardcore();
    }

    @Environment(value=EnvType.CLIENT)
    public MutableText getVersion() {
        if (ChatUtil.isEmpty(this.field_25023.getVersionName())) {
            return new TranslatableText("selectWorld.versionUnknown");
        }
        return new LiteralText(this.field_25023.getVersionName());
    }

    public SaveVersionInfo method_29586() {
        return this.field_25023;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.field_25023.isStable() || this.isOutdatedLevel();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFutureLevel() {
        return this.field_25023.getVersionId() > SharedConstants.getGameVersion().getWorldVersion();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isOutdatedLevel() {
        return this.field_25023.getVersionId() < SharedConstants.getGameVersion().getWorldVersion();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLocked() {
        return this.locked;
    }

    @Environment(value=EnvType.CLIENT)
    public Text method_27429() {
        if (this.field_24191 == null) {
            this.field_24191 = this.method_27430();
        }
        return this.field_24191;
    }

    @Environment(value=EnvType.CLIENT)
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

