/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5285;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class class_5292
implements TickableElement,
Drawable {
    private static final Map<class_5285.class_5288, class_5293> field_24590 = ImmutableMap.of((Object)class_5285.class_5288.field_24553, (arg, arg2) -> new CustomizeFlatLevelScreen(arg, arg3 -> arg.field_24588.method_28086(arg2.method_28019((FlatChunkGeneratorConfig)arg3)), arg2.method_28040()), (Object)class_5285.class_5288.field_24555, (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, pair -> arg.field_24588.method_28086(arg2.method_28012((class_5285.class_5286)((Object)((Object)((Object)pair.getFirst()))), (Set)pair.getSecond())), arg2.method_28041()));
    private static final Text field_24591 = new TranslatableText("generator.amplified.info");
    private TextRenderer field_24592;
    private int field_24593;
    private TextFieldWidget field_24594;
    private ButtonWidget field_24595;
    public ButtonWidget field_24589;
    private ButtonWidget field_24596;
    private ButtonWidget field_24597;
    private class_5285 field_24598;
    private int field_24599;
    private String field_24600;

    public class_5292() {
        this(class_5285.method_28009(), "");
    }

    public class_5292(class_5285 arg) {
        this(arg, Long.toString(arg.method_28028()));
    }

    private class_5292(class_5285 arg, String string) {
        this.field_24598 = arg;
        this.field_24599 = class_5285.class_5288.field_24556.indexOf(arg.method_28039());
        this.field_24600 = string;
    }

    public void method_28092(final CreateWorldScreen arg4, MinecraftClient arg22, TextRenderer arg32) {
        this.field_24592 = arg32;
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
            do {
                ++this.field_24599;
                if (this.field_24599 >= class_5285.class_5288.field_24556.size()) {
                    this.field_24599 = 0;
                }
                this.field_24598 = this.field_24598.method_28015(class_5285.class_5288.field_24556.get(this.field_24599));
            } while (this.field_24598.method_28033() && !Screen.hasShiftDown());
            arg4.method_28084();
            arg2.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(class_5292.this.field_24598.method_28039().method_28049());
            }

            @Override
            protected MutableText getNarrationMessage() {
                if (class_5292.this.field_24598.method_28039() == class_5285.class_5288.field_24554) {
                    return super.getNarrationMessage().append(". ").append(field_24591);
                }
                return super.getNarrationMessage();
            }
        });
        this.field_24596.visible = false;
        this.field_24597 = arg4.addButton(new ButtonWidget(arg4.width / 2 + 5, 120, 150, 20, new TranslatableText("selectWorld.customizeType"), arg3 -> {
            class_5293 lv = field_24590.get(this.field_24598.method_28039());
            if (lv != null) {
                arg22.openScreen(lv.createEditScreen(arg4, this.field_24598));
            }
        }));
        this.field_24597.visible = false;
        this.field_24589 = arg4.addButton(new ButtonWidget(arg4.width / 2 + 5, 151, 150, 20, new TranslatableText("selectWorld.bonusItems"), arg -> {
            this.field_24598 = this.field_24598.method_28038();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(class_5292.this.field_24598.method_28030() && !arg4.hardcore));
            }
        });
        this.field_24589.visible = false;
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
        if (this.field_24598.method_28039() == class_5285.class_5288.field_24554) {
            this.field_24592.drawTrimmed(field_24591, this.field_24596.x + 2, this.field_24596.y + 22, this.field_24596.getWidth(), 0xA0A0A0);
        }
    }

    private void method_28086(class_5285 arg) {
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
            if (optionalLong2.isPresent() && optionalLong2.getAsLong() == 0L) {
                OptionalLong optionalLong3 = OptionalLong.empty();
            } else {
                optionalLong4 = optionalLong2;
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
        } else {
            this.field_24595.visible = bl;
            this.field_24589.visible = bl;
            this.field_24597.visible = bl && field_24590.containsKey(this.field_24598.method_28039());
        }
        this.field_24594.setVisible(bl);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_5293 {
        public Screen createEditScreen(CreateWorldScreen var1, class_5285 var2);
    }
}

