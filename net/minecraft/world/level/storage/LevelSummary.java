/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.level.storage;

import java.io.File;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;

@Environment(value=EnvType.CLIENT)
public class LevelSummary
implements Comparable<LevelSummary> {
    private final String name;
    private final String displayName;
    private final long lastPlayed;
    private final long getSizeOnDisk;
    private final boolean requiresConversion;
    private final GameMode gameMode;
    private final boolean hardcore;
    private final boolean commandsAllowed;
    private final String versionName;
    private final int versionId;
    private final boolean snapshot;
    private final LevelGeneratorType generatorType;
    private final boolean locked;
    private final File file;
    @Nullable
    private Text field_24191;

    public LevelSummary(class_5219 arg, String string, String string2, long l, boolean bl, boolean bl2, File file) {
        this.name = string;
        this.displayName = string2;
        this.locked = bl2;
        this.file = file;
        this.lastPlayed = arg.getLastPlayed();
        this.getSizeOnDisk = l;
        this.gameMode = arg.getGameMode();
        this.requiresConversion = bl;
        this.hardcore = arg.isHardcore();
        this.commandsAllowed = arg.areCommandsAllowed();
        this.versionName = arg.getVersionName();
        this.versionId = arg.getVersionId();
        this.snapshot = arg.isVersionSnapshot();
        this.generatorType = arg.method_27859().getGeneratorType();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public File getFile() {
        return this.file;
    }

    public boolean requiresConversion() {
        return this.requiresConversion;
    }

    public long getLastPlayed() {
        return this.lastPlayed;
    }

    @Override
    public int compareTo(LevelSummary arg) {
        if (this.lastPlayed < arg.lastPlayed) {
            return 1;
        }
        if (this.lastPlayed > arg.lastPlayed) {
            return -1;
        }
        return this.name.compareTo(arg.name);
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public boolean hasCheats() {
        return this.commandsAllowed;
    }

    public MutableText getVersion() {
        if (ChatUtil.isEmpty(this.versionName)) {
            return new TranslatableText("selectWorld.versionUnknown");
        }
        return new LiteralText(this.versionName);
    }

    public boolean isDifferentVersion() {
        return this.isFutureLevel() || !SharedConstants.getGameVersion().isStable() && !this.snapshot || this.isOutdatedLevel() || this.isLegacyCustomizedWorld();
    }

    public boolean isFutureLevel() {
        return this.versionId > SharedConstants.getGameVersion().getWorldVersion();
    }

    public boolean isLegacyCustomizedWorld() {
        return this.generatorType == LevelGeneratorType.CUSTOMIZED && this.versionId < 1466;
    }

    public boolean isOutdatedLevel() {
        return this.versionId < SharedConstants.getGameVersion().getWorldVersion();
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

