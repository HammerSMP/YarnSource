/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    private final SelectWorldScreen parent;
    @Nullable
    private List<LevelSummary> levels;

    public WorldListWidget(SelectWorldScreen arg, MinecraftClient arg2, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable WorldListWidget arg3) {
        super(arg2, i, j, k, l, m);
        this.parent = arg;
        if (arg3 != null) {
            this.levels = arg3.levels;
        }
        this.filter(supplier, false);
    }

    public void filter(Supplier<String> supplier, boolean bl) {
        this.clearEntries();
        LevelStorage lv = this.client.getLevelStorage();
        if (this.levels == null || bl) {
            try {
                this.levels = lv.getLevelList();
            }
            catch (LevelStorageException lv2) {
                LOGGER.error("Couldn't load level list", (Throwable)lv2);
                this.client.openScreen(new FatalErrorScreen(new TranslatableText("selectWorld.unable_to_load"), new LiteralText(lv2.getMessage())));
                return;
            }
            Collections.sort(this.levels);
        }
        if (this.levels.isEmpty()) {
            this.client.openScreen(new CreateWorldScreen(null));
            return;
        }
        String string = supplier.get().toLowerCase(Locale.ROOT);
        for (LevelSummary lv3 : this.levels) {
            if (!lv3.getDisplayName().toLowerCase(Locale.ROOT).contains(string) && !lv3.getName().toLowerCase(Locale.ROOT).contains(string)) continue;
            this.addEntry(new Entry(this, lv3, this.client.getLevelStorage()));
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected boolean isFocused() {
        return this.parent.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Entry arg) {
        super.setSelected(arg);
        if (arg != null) {
            LevelSummary lv = arg.level;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", new TranslatableText("narrator.select.world", lv.getDisplayName(), new Date(lv.getLastPlayed()), lv.isHardcore() ? new TranslatableText("gameMode.hardcore") : new TranslatableText("gameMode." + lv.getGameMode().getName()), lv.hasCheats() ? new TranslatableText("selectWorld.cheats") : LiteralText.EMPTY, lv.getVersion())).getString());
        }
    }

    @Override
    protected void moveSelection(int i) {
        super.moveSelection(i);
        this.parent.worldSelected(true);
    }

    public Optional<Entry> method_20159() {
        return Optional.ofNullable(this.getSelected());
    }

    public SelectWorldScreen getParent() {
        return this.parent;
    }

    @Environment(value=EnvType.CLIENT)
    public final class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
    implements AutoCloseable {
        private final MinecraftClient client;
        private final SelectWorldScreen screen;
        private final LevelSummary level;
        private final Identifier iconLocation;
        private File iconFile;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;

        public Entry(WorldListWidget arg2, LevelSummary arg3, LevelStorage arg4) {
            this.screen = arg2.getParent();
            this.level = arg3;
            this.client = MinecraftClient.getInstance();
            this.iconLocation = new Identifier("worlds/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)arg3.getName()) + "/icon");
            this.iconFile = arg3.getFile();
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            String string = this.level.getDisplayName();
            String string2 = this.level.getName() + " (" + DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = I18n.translate("selectWorld.world", new Object[0]) + " " + (i + 1);
            }
            Text lv = this.level.method_27429();
            this.client.textRenderer.draw(arg, string, (float)(k + 32 + 3), (float)(j + 1), 0xFFFFFF);
            this.client.textRenderer.getClass();
            this.client.textRenderer.draw(arg, string2, (float)(k + 32 + 3), (float)(j + 9 + 3), 0x808080);
            this.client.textRenderer.getClass();
            this.client.textRenderer.getClass();
            this.client.textRenderer.draw(arg, lv, (float)(k + 32 + 3), (float)(j + 9 + 9 + 3), 0x808080);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.client.options.touchscreen || bl) {
                int q;
                this.client.getTextureManager().bindTexture(WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(arg, k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = n - k;
                boolean bl2 = p < 32;
                int n2 = q = bl2 ? 32 : 0;
                if (this.level.isLocked()) {
                    DrawableHelper.drawTexture(arg, k, j, 96.0f, q, 32, 32, 256, 256);
                    if (bl2) {
                        MutableText lv2 = new TranslatableText("selectWorld.locked").formatted(Formatting.RED);
                        this.screen.setTooltip(this.client.textRenderer.wrapLines(lv2, 175));
                    }
                } else if (this.level.isDifferentVersion()) {
                    DrawableHelper.drawTexture(arg, k, j, 32.0f, q, 32, 32, 256, 256);
                    if (this.level.isLegacyCustomizedWorld()) {
                        DrawableHelper.drawTexture(arg, k, j, 96.0f, q, 32, 32, 256, 256);
                        if (bl2) {
                            MutableText lv3 = new TranslatableText("selectWorld.tooltip.unsupported", this.level.getVersion()).formatted(Formatting.RED);
                            this.screen.setTooltip(this.client.textRenderer.wrapLines(lv3, 175));
                        }
                    } else if (this.level.isFutureLevel()) {
                        DrawableHelper.drawTexture(arg, k, j, 96.0f, q, 32, 32, 256, 256);
                        if (bl2) {
                            this.screen.setTooltip(Arrays.asList(new TranslatableText("selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED), new TranslatableText("selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED)));
                        }
                    } else if (!SharedConstants.getGameVersion().isStable()) {
                        DrawableHelper.drawTexture(arg, k, j, 64.0f, q, 32, 32, 256, 256);
                        if (bl2) {
                            this.screen.setTooltip(Arrays.asList(new TranslatableText("selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD), new TranslatableText("selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD)));
                        }
                    }
                } else {
                    DrawableHelper.drawTexture(arg, k, j, 0.0f, q, 32, 32, 256, 256);
                }
            }
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (this.level.isLocked()) {
                return true;
            }
            WorldListWidget.this.setSelected(this);
            this.screen.worldSelected(WorldListWidget.this.method_20159().isPresent());
            if (d - (double)WorldListWidget.this.getRowLeft() <= 32.0) {
                this.play();
                return true;
            }
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.play();
                return true;
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public void play() {
            if (this.level.isLocked()) {
                return;
            }
            if (this.level.isOutdatedLevel() || this.level.isLegacyCustomizedWorld()) {
                TranslatableText lv = new TranslatableText("selectWorld.backupQuestion");
                TranslatableText lv2 = new TranslatableText("selectWorld.backupWarning", this.level.getVersion(), SharedConstants.getGameVersion().getName());
                if (this.level.isLegacyCustomizedWorld()) {
                    lv = new TranslatableText("selectWorld.backupQuestion.customized");
                    lv2 = new TranslatableText("selectWorld.backupWarning.customized");
                }
                this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
                    if (bl) {
                        String string = this.level.getName();
                        try (LevelStorage.Session lv = this.client.getLevelStorage().createSession(string);){
                            EditWorldScreen.backupLevel(lv);
                        }
                        catch (IOException iOException) {
                            SystemToast.addWorldAccessFailureToast(this.client, string);
                            LOGGER.error("Failed to backup level {}", (Object)string, (Object)iOException);
                        }
                    }
                    this.start();
                }, lv, lv2, false));
            } else if (this.level.isFutureLevel()) {
                this.client.openScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        try {
                            this.start();
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                            this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.futureworld.error.title"), new TranslatableText("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.client.openScreen(this.screen);
                    }
                }, new TranslatableText("selectWorld.versionQuestion"), new TranslatableText("selectWorld.versionWarning", this.level.getVersion(), new TranslatableText("selectWorld.versionJoinButton"), ScreenTexts.CANCEL)));
            } else {
                this.start();
            }
        }

        public void delete() {
            this.client.openScreen(new ConfirmScreen(bl -> {
                if (bl) {
                    this.client.openScreen(new ProgressScreen());
                    LevelStorage lv = this.client.getLevelStorage();
                    String string = this.level.getName();
                    try (LevelStorage.Session lv2 = lv.createSession(string);){
                        lv2.deleteSessionLock();
                    }
                    catch (IOException iOException) {
                        SystemToast.addWorldDeleteFailureToast(this.client, string);
                        LOGGER.error("Failed to delete world {}", (Object)string, (Object)iOException);
                    }
                    WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, new TranslatableText("selectWorld.deleteQuestion"), new TranslatableText("selectWorld.deleteWarning", this.level.getDisplayName()), new TranslatableText("selectWorld.deleteButton"), ScreenTexts.CANCEL));
        }

        public void edit() {
            String string = this.level.getName();
            try {
                LevelStorage.Session lv = this.client.getLevelStorage().createSession(string);
                this.client.openScreen(new EditWorldScreen(bl -> {
                    try {
                        lv.close();
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Failed to unlock level {}", (Object)string, (Object)iOException);
                    }
                    if (bl) {
                        WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                    }
                    this.client.openScreen(this.screen);
                }, lv));
            }
            catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, string);
                LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
                WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
            }
        }

        public void recreate() {
            try {
                this.client.openScreen(new ProgressScreen());
                try (LevelStorage.Session lv = this.client.getLevelStorage().createSession(this.level.getName());){
                    class_5219 lv2 = lv.readLevelProperties();
                    if (lv2 != null) {
                        CreateWorldScreen lv3 = new CreateWorldScreen((Screen)this.screen, lv2);
                        if (this.level.isLegacyCustomizedWorld()) {
                            this.client.openScreen(new ConfirmScreen(bl -> this.client.openScreen(bl ? lv3 : this.screen), new TranslatableText("selectWorld.recreate.customized.title"), new TranslatableText("selectWorld.recreate.customized.text"), ScreenTexts.PROCEED, ScreenTexts.CANCEL));
                        } else {
                            this.client.openScreen(lv3);
                        }
                    }
                }
            }
            catch (Exception exception) {
                LOGGER.error("Unable to recreate world", (Throwable)exception);
                this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.recreate.error.title"), new TranslatableText("selectWorld.recreate.error.text")));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.client.startIntegratedServer(this.level.getName(), null);
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            boolean bl2 = bl = this.iconFile != null && this.iconFile.isFile();
            if (!bl) {
                this.client.getTextureManager().destroyTexture(this.iconLocation);
                return null;
            }
            try (FileInputStream inputStream = new FileInputStream(this.iconFile);){
                NativeImage lv = NativeImage.read(inputStream);
                Validate.validState((lv.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                Validate.validState((lv.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                NativeImageBackedTexture lv2 = new NativeImageBackedTexture(lv);
                this.client.getTextureManager().registerTexture(this.iconLocation, lv2);
                NativeImageBackedTexture nativeImageBackedTexture = lv2;
                return nativeImageBackedTexture;
            }
            catch (Throwable throwable6) {
                LOGGER.error("Invalid icon for world {}", (Object)this.level.getName(), (Object)throwable6);
                this.iconFile = null;
                return null;
            }
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }
    }
}

