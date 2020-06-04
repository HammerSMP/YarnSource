/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5359;
import net.minecraft.class_5384;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    private static final Logger field_23776 = LogManager.getLogger();
    private static final Gson field_25481 = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private ButtonWidget saveButton;
    private final BooleanConsumer callback;
    private TextFieldWidget levelNameTextField;
    private final LevelStorage.Session field_23777;

    public EditWorldScreen(BooleanConsumer booleanConsumer, LevelStorage.Session arg) {
        super(new TranslatableText("selectWorld.edit.title"));
        this.callback = booleanConsumer;
        this.field_23777 = arg;
    }

    @Override
    public void tick() {
        this.levelNameTextField.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        ButtonWidget lv = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableText("selectWorld.edit.resetIcon"), arg -> {
            FileUtils.deleteQuietly((File)this.field_23777.getIconFile());
            arg.active = false;
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableText("selectWorld.edit.openFolder"), arg -> Util.getOperatingSystem().open(this.field_23777.getDirectory(WorldSavePath.ROOT).toFile())));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableText("selectWorld.edit.backup"), arg -> {
            boolean bl = EditWorldScreen.backupLevel(this.field_23777);
            this.callback.accept(!bl);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableText("selectWorld.edit.backupFolder"), arg -> {
            LevelStorage lv = this.client.getLevelStorage();
            Path path = lv.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path.toFile());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableText("selectWorld.edit.optimize"), arg -> this.client.openScreen(new BackupPromptScreen(this, (bl, bl2) -> {
            if (bl) {
                EditWorldScreen.backupLevel(this.field_23777);
            }
            this.client.openScreen(OptimizeWorldScreen.method_27031(this.client, this.callback, this.client.getDataFixer(), this.field_23777, bl2));
        }, new TranslatableText("optimizeWorld.confirm.title"), new TranslatableText("optimizeWorld.confirm.description"), true))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableText("selectWorld.edit.export_worldgen_settings"), arg -> {
            DataResult dataResult4;
            DimensionTracker.Modifiable lv = DimensionTracker.create();
            try (MinecraftClient.class_5367 lv2 = this.client.method_29604(lv, MinecraftClient::method_29598, (Function4<LevelStorage.Session, DimensionTracker.Modifiable, ResourceManager, class_5359, SaveProperties>)((Function4)MinecraftClient::method_29599), false, this.field_23777);){
                class_5384 dynamicOps = class_5384.method_29771(JsonOps.INSTANCE, lv);
                DataResult dataResult = GeneratorOptions.CODEC.encodeStart(dynamicOps, (Object)lv2.method_29614().getGeneratorOptions());
                DataResult dataResult2 = dataResult.flatMap(jsonElement -> {
                    Path path = this.field_23777.getDirectory(WorldSavePath.ROOT).resolve("worldgen_settings_export.json");
                    try (JsonWriter jsonWriter = field_25481.newJsonWriter((Writer)Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]));){
                        field_25481.toJson(jsonElement, jsonWriter);
                    }
                    catch (JsonIOException | IOException exception) {
                        return DataResult.error((String)("Error writing file: " + exception.getMessage()));
                    }
                    return DataResult.success((Object)path.toString());
                });
            }
            catch (InterruptedException | ExecutionException exception) {
                dataResult4 = DataResult.error((String)"Could not parse level data!");
            }
            LiteralText lv3 = new LiteralText((String)dataResult4.get().map(Function.identity(), DataResult.PartialResult::message));
            TranslatableText lv4 = new TranslatableText(dataResult4.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
            dataResult4.error().ifPresent(partialResult -> field_23776.error("Error exporting world settings: {}", partialResult));
            this.client.getToastManager().add(SystemToast.method_29047(this.client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, lv4, lv3));
        }));
        this.saveButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableText("selectWorld.edit.save"), arg -> this.commit()));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, ScreenTexts.CANCEL, arg -> this.callback.accept(false)));
        lv.active = this.field_23777.getIconFile().isFile();
        LevelSummary lv2 = this.field_23777.method_29584();
        String string2 = lv2 == null ? "" : lv2.getDisplayName();
        this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 38, 200, 20, new TranslatableText("selectWorld.enterName"));
        this.levelNameTextField.setText(string2);
        this.levelNameTextField.setChangedListener(string -> {
            this.saveButton.active = !string.trim().isEmpty();
        });
        this.children.add(this.levelNameTextField);
        this.setInitialFocus(this.levelNameTextField);
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.levelNameTextField.getText();
        this.init(arg, i, j);
        this.levelNameTextField.setText(string);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void commit() {
        try {
            this.field_23777.save(this.levelNameTextField.getText().trim());
            this.callback.accept(true);
        }
        catch (IOException iOException) {
            field_23776.error("Failed to access world '{}'", (Object)this.field_23777.getDirectoryName(), (Object)iOException);
            SystemToast.addWorldAccessFailureToast(this.client, this.field_23777.getDirectoryName());
            this.callback.accept(true);
        }
    }

    public static boolean backupLevel(LevelStorage.Session arg) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = arg.createBackup();
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            TranslatableText lv = new TranslatableText("selectWorld.edit.backupFailed");
            LiteralText lv2 = new LiteralText(iOException.getMessage());
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, lv, lv2));
            return false;
        }
        TranslatableText lv3 = new TranslatableText("selectWorld.edit.backupCreated", arg.getDirectoryName());
        TranslatableText lv4 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, lv3, lv4));
        return true;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 24, 0xA0A0A0);
        this.levelNameTextField.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }
}

