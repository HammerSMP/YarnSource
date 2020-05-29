/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5317;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(value=EnvType.CLIENT)
public class MoreOptionsDialog
implements TickableElement,
Drawable {
    private static final Logger field_25046 = LogManager.getLogger();
    private static final Text field_25047 = new TranslatableText("generator.custom");
    private static final Text AMPLIFIED_INFO_TEXT = new TranslatableText("generator.amplified.info");
    private TextRenderer textRenderer;
    private int parentWidth;
    private TextFieldWidget seedTextField;
    private ButtonWidget mapFeaturesButton;
    public ButtonWidget bonusItemsButton;
    private ButtonWidget mapTypeButton;
    private ButtonWidget customizeTypeButton;
    private ButtonWidget field_25048;
    private GeneratorOptions generatorOptions;
    private Optional<class_5317> field_25049;
    private String seedText;

    public MoreOptionsDialog() {
        this.generatorOptions = GeneratorOptions.getDefaultOptions();
        this.field_25049 = Optional.of(class_5317.field_25050);
        this.seedText = "";
    }

    public MoreOptionsDialog(GeneratorOptions arg) {
        this.generatorOptions = arg;
        this.field_25049 = class_5317.method_29078(arg);
        this.seedText = Long.toString(arg.getSeed());
    }

    public void method_28092(final CreateWorldScreen arg4, MinecraftClient arg22, TextRenderer arg33) {
        this.textRenderer = arg33;
        this.parentWidth = arg4.width;
        this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.seedTextField.setText(this.seedText);
        this.seedTextField.setChangedListener(string -> {
            this.seedText = this.seedTextField.getText();
        });
        arg4.addChild(this.seedTextField);
        this.mapFeaturesButton = arg4.addButton(new ButtonWidget(this.parentWidth / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), arg -> {
            this.generatorOptions = this.generatorOptions.toggleGenerateStructures();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(MoreOptionsDialog.this.generatorOptions.shouldGenerateStructures()));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
            }
        });
        this.mapFeaturesButton.visible = false;
        this.mapTypeButton = arg4.addButton(new ButtonWidget(this.parentWidth / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.mapType"), arg2 -> {
            while (this.field_25049.isPresent()) {
                int i = class_5317.field_25052.indexOf(this.field_25049.get()) + 1;
                if (i >= class_5317.field_25052.size()) {
                    i = 0;
                }
                class_5317 lv = class_5317.field_25052.get(i);
                this.field_25049 = Optional.of(lv);
                this.generatorOptions = lv.method_29077(this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
                if (this.generatorOptions.isDebugWorld() && !Screen.hasShiftDown()) continue;
            }
            arg4.setMoreOptionsOpen();
            arg2.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(MoreOptionsDialog.this.field_25049.map(class_5317::method_29075).orElse(field_25047));
            }

            @Override
            protected MutableText getNarrationMessage() {
                if (Objects.equals(MoreOptionsDialog.this.field_25049, Optional.of(class_5317.field_25051))) {
                    return super.getNarrationMessage().append(". ").append(AMPLIFIED_INFO_TEXT);
                }
                return super.getNarrationMessage();
            }
        });
        this.mapTypeButton.visible = false;
        this.mapTypeButton.active = this.field_25049.isPresent();
        this.customizeTypeButton = arg4.addButton(new ButtonWidget(arg4.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), arg3 -> {
            class_5317.class_5293 lv = class_5317.field_25053.get(this.field_25049);
            if (lv != null) {
                arg22.openScreen(lv.createEditScreen(arg4, this.generatorOptions));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = arg4.addButton(new ButtonWidget(arg4.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), arg -> {
            this.generatorOptions = this.generatorOptions.toggleBonusChest();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(MoreOptionsDialog.this.generatorOptions.hasBonusChest() && !arg4.hardcore));
            }
        });
        this.bonusItemsButton.visible = false;
        this.field_25048 = arg4.addButton(new ButtonWidget(this.parentWidth / 2 - 155, 185, 150, 20, new TranslatableText("selectWorld.import_worldgen_settings"), arg32 -> {
            DataResult dataResult3;
            TranslatableText lv = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
            String string = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)lv.getString(), null, null, null, (boolean)false);
            if (string == null) {
                return;
            }
            JsonParser jsonParser = new JsonParser();
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string, new String[0]));){
                JsonElement jsonElement = jsonParser.parse((Reader)bufferedReader);
                DataResult dataResult = GeneratorOptions.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
            }
            catch (JsonIOException | JsonSyntaxException | IOException exception) {
                dataResult3 = DataResult.error((String)("Failed to parse file: " + exception.getMessage()));
            }
            if (dataResult3.error().isPresent()) {
                TranslatableText lv2 = new TranslatableText("selectWorld.import_worldgen_settings.failure");
                String string2 = ((DataResult.PartialResult)dataResult3.error().get()).message();
                field_25046.error("Error parsing world settings: {}", (Object)string2);
                LiteralText lv3 = new LiteralText(string2);
                arg22.getToastManager().add(SystemToast.method_29047(SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, lv2, lv3));
            }
            Lifecycle lifecycle = dataResult3.lifecycle();
            dataResult3.resultOrPartial(((Logger)field_25046)::error).ifPresent(arg3 -> {
                BooleanConsumer booleanConsumer = bl -> {
                    arg22.openScreen(arg4);
                    if (bl) {
                        this.method_29073((GeneratorOptions)arg3);
                    }
                };
                if (lifecycle == Lifecycle.stable()) {
                    this.method_29073((GeneratorOptions)arg3);
                } else if (lifecycle == Lifecycle.experimental()) {
                    arg22.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")));
                } else {
                    arg22.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")));
                }
            });
        }));
        this.field_25048.visible = false;
    }

    private void method_29073(GeneratorOptions arg) {
        this.generatorOptions = arg;
        this.field_25049 = class_5317.method_29078(arg);
        this.seedText = Long.toString(arg.getSeed());
        this.seedTextField.setText(this.seedText);
        this.mapTypeButton.active = this.field_25049.isPresent();
    }

    @Override
    public void tick() {
        this.seedTextField.tick();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.mapFeaturesButton.visible) {
            this.textRenderer.drawWithShadow(arg, I18n.translate("selectWorld.mapFeatures.info", new Object[0]), (float)(this.parentWidth / 2 - 150), 122.0f, -6250336);
        }
        this.seedTextField.render(arg, i, j, f);
        if (this.field_25049.equals(Optional.of(class_5317.field_25051))) {
            this.textRenderer.drawTrimmed(AMPLIFIED_INFO_TEXT, this.mapTypeButton.x + 2, this.mapTypeButton.y + 22, this.mapTypeButton.getWidth(), 0xA0A0A0);
        }
    }

    protected void setGeneratorOptions(GeneratorOptions arg) {
        this.generatorOptions = arg;
    }

    private static OptionalLong tryParseLong(String string) {
        try {
            return OptionalLong.of(Long.parseLong(string));
        }
        catch (NumberFormatException numberFormatException) {
            return OptionalLong.empty();
        }
    }

    public GeneratorOptions getGeneratorOptions(boolean bl) {
        OptionalLong optionalLong4;
        String string = this.seedTextField.getText();
        if (StringUtils.isEmpty((CharSequence)string)) {
            OptionalLong optionalLong = OptionalLong.empty();
        } else {
            OptionalLong optionalLong2 = MoreOptionsDialog.tryParseLong(string);
            if (optionalLong2.isPresent() && optionalLong2.getAsLong() != 0L) {
                OptionalLong optionalLong3 = optionalLong2;
            } else {
                optionalLong4 = OptionalLong.of(string.hashCode());
            }
        }
        return this.generatorOptions.withHardcore(bl, optionalLong4);
    }

    public boolean isDebugWorld() {
        return this.generatorOptions.isDebugWorld();
    }

    public void setVisible(boolean bl) {
        this.mapTypeButton.visible = bl;
        if (this.generatorOptions.isDebugWorld()) {
            this.mapFeaturesButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.field_25048.visible = false;
        } else {
            this.mapFeaturesButton.visible = bl;
            this.bonusItemsButton.visible = bl;
            this.customizeTypeButton.visible = bl && class_5317.field_25053.containsKey(this.field_25049);
            this.field_25048.visible = bl;
        }
        this.seedTextField.setVisible(bl);
    }
}

