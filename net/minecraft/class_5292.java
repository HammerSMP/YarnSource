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
package net.minecraft;

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
import net.minecraft.class_5285;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@Environment(value=EnvType.CLIENT)
public class class_5292
implements TickableElement,
Drawable {
    private static final Logger field_25046 = LogManager.getLogger();
    private static final Text field_25047 = new TranslatableText("generator.custom");
    private static final Text field_24591 = new TranslatableText("generator.amplified.info");
    private TextRenderer field_24592;
    private int field_24593;
    private TextFieldWidget field_24594;
    private ButtonWidget field_24595;
    public ButtonWidget field_24589;
    private ButtonWidget field_24596;
    private ButtonWidget field_24597;
    private ButtonWidget field_25048;
    private class_5285 field_24598;
    private Optional<class_5317> field_25049;
    private String field_24600;

    public class_5292() {
        this.field_24598 = class_5285.method_28009();
        this.field_25049 = Optional.of(class_5317.field_25050);
        this.field_24600 = "";
    }

    public class_5292(class_5285 arg) {
        this.field_24598 = arg;
        this.field_25049 = class_5317.method_29078(arg);
        this.field_24600 = Long.toString(arg.method_28028());
    }

    public void method_28092(final CreateWorldScreen arg4, MinecraftClient arg22, TextRenderer arg33) {
        this.field_24592 = arg33;
        this.field_24593 = arg4.width;
        this.field_24594 = new TextFieldWidget(this.field_24592, this.field_24593 / 2 - 100, 60, 200, 20, new TranslatableText("selectWorld.enterSeed"));
        this.field_24594.setText(this.field_24600);
        this.field_24594.setChangedListener(string -> {
            this.field_24600 = this.field_24594.getText();
        });
        arg4.addChild(this.field_24594);
        this.field_24595 = arg4.addButton(new ButtonWidget(this.field_24593 / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.mapFeatures"), arg -> {
            this.field_24598 = this.field_24598.method_28037();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(class_5292.this.field_24598.method_28029()));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.mapFeatures.info"));
            }
        });
        this.field_24595.visible = false;
        this.field_24596 = arg4.addButton(new ButtonWidget(this.field_24593 / 2 + 5, 100, 150, 20, new TranslatableText("selectWorld.mapType"), arg2 -> {
            while (this.field_25049.isPresent()) {
                int i = class_5317.field_25052.indexOf(this.field_25049.get()) + 1;
                if (i >= class_5317.field_25052.size()) {
                    i = 0;
                }
                class_5317 lv = class_5317.field_25052.get(i);
                this.field_25049 = Optional.of(lv);
                this.field_24598 = lv.method_29077(this.field_24598.method_28028(), this.field_24598.method_28029(), this.field_24598.method_28030());
                if (this.field_24598.method_28033() && !Screen.hasShiftDown()) continue;
            }
            arg4.method_28084();
            arg2.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(class_5292.this.field_25049.map(class_5317::method_29075).orElse(field_25047));
            }

            @Override
            protected MutableText getNarrationMessage() {
                if (Objects.equals(class_5292.this.field_25049, Optional.of(class_5317.field_25051))) {
                    return super.getNarrationMessage().append(". ").append(field_24591);
                }
                return super.getNarrationMessage();
            }
        });
        this.field_24596.visible = false;
        this.field_24596.active = this.field_25049.isPresent();
        this.field_24597 = arg4.addButton(new ButtonWidget(arg4.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), arg3 -> {
            class_5317.class_5293 lv = class_5317.field_25053.get(this.field_25049);
            if (lv != null) {
                arg22.openScreen(lv.createEditScreen(arg4, this.field_24598));
            }
        }));
        this.field_24597.visible = false;
        this.field_24589 = arg4.addButton(new ButtonWidget(arg4.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), arg -> {
            this.field_24598 = this.field_24598.method_28038();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(class_5292.this.field_24598.method_28030() && !arg4.hardcore));
            }
        });
        this.field_24589.visible = false;
        this.field_25048 = arg4.addButton(new ButtonWidget(this.field_24593 / 2 - 155, 185, 150, 20, new TranslatableText("selectWorld.import_worldgen_settings"), arg32 -> {
            DataResult dataResult3;
            TranslatableText lv = new TranslatableText("selectWorld.import_worldgen_settings.select_file");
            String string = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)lv.getString(), null, null, null, (boolean)false);
            if (string == null) {
                return;
            }
            JsonParser jsonParser = new JsonParser();
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string, new String[0]));){
                JsonElement jsonElement = jsonParser.parse((Reader)bufferedReader);
                DataResult dataResult = class_5285.field_24826.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
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
                        this.method_29073((class_5285)arg3);
                    }
                };
                if (lifecycle == Lifecycle.stable()) {
                    this.method_29073((class_5285)arg3);
                } else if (lifecycle == Lifecycle.experimental()) {
                    arg22.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableText("selectWorld.import_worldgen_settings.experimental.question")));
                } else {
                    arg22.openScreen(new ConfirmScreen(booleanConsumer, new TranslatableText("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableText("selectWorld.import_worldgen_settings.deprecated.question")));
                }
            });
        }));
        this.field_25048.visible = false;
    }

    private void method_29073(class_5285 arg) {
        this.field_24598 = arg;
        this.field_25049 = class_5317.method_29078(arg);
        this.field_24600 = Long.toString(arg.method_28028());
        this.field_24594.setText(this.field_24600);
        this.field_24596.active = this.field_25049.isPresent();
    }

    @Override
    public void tick() {
        this.field_24594.tick();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.field_24595.visible) {
            this.field_24592.drawWithShadow(arg, I18n.translate("selectWorld.mapFeatures.info", new Object[0]), (float)(this.field_24593 / 2 - 150), 122.0f, -6250336);
        }
        this.field_24594.render(arg, i, j, f);
        if (this.field_25049.equals(Optional.of(class_5317.field_25051))) {
            this.field_24592.drawTrimmed(field_24591, this.field_24596.x + 2, this.field_24596.y + 22, this.field_24596.getWidth(), 0xA0A0A0);
        }
    }

    protected void method_28086(class_5285 arg) {
        this.field_24598 = arg;
    }

    private static OptionalLong method_28095(String string) {
        try {
            return OptionalLong.of(Long.parseLong(string));
        }
        catch (NumberFormatException numberFormatException) {
            return OptionalLong.empty();
        }
    }

    public class_5285 method_28096(boolean bl) {
        OptionalLong optionalLong4;
        String string = this.field_24594.getText();
        if (StringUtils.isEmpty((CharSequence)string)) {
            OptionalLong optionalLong = OptionalLong.empty();
        } else {
            OptionalLong optionalLong2 = class_5292.method_28095(string);
            if (optionalLong2.isPresent() && optionalLong2.getAsLong() != 0L) {
                OptionalLong optionalLong3 = optionalLong2;
            } else {
                optionalLong4 = OptionalLong.of(string.hashCode());
            }
        }
        return this.field_24598.method_28024(bl, optionalLong4);
    }

    public boolean method_28085() {
        return this.field_24598.method_28033();
    }

    public void method_28101(boolean bl) {
        this.field_24596.visible = bl;
        if (this.field_24598.method_28033()) {
            this.field_24595.visible = false;
            this.field_24589.visible = false;
            this.field_24597.visible = false;
            this.field_25048.visible = false;
        } else {
            this.field_24595.visible = bl;
            this.field_24589.visible = bl;
            this.field_24597.visible = bl && class_5317.field_25053.containsKey(this.field_25049);
            this.field_25048.visible = bl;
        }
        this.field_24594.setVisible(bl);
    }
}

