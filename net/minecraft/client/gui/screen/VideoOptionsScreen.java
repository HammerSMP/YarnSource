/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5407;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GraphicsConfirmationScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class VideoOptionsScreen
extends GameOptionsScreen {
    private static final Text GRAPHICS_FABULOUS_TEXT = new TranslatableText("options.graphics.fabulous").formatted(Formatting.ITALIC);
    private static final Text GRAPHICS_WARNING_MESSAGE_TEXT = new TranslatableText("options.graphics.warning.message", GRAPHICS_FABULOUS_TEXT, GRAPHICS_FABULOUS_TEXT);
    private static final Text GRAPHICS_WARNING_TITLE_TEXT = new TranslatableText("options.graphics.warning.title").formatted(Formatting.RED);
    private static final Text GRAPHICS_WARNING_ACCEPT_TEXT = new TranslatableText("options.graphics.warning.accept");
    private static final Text GRAPHICS_WARNING_CANCEL_TEXT = new TranslatableText("options.graphics.warning.cancel");
    private static final Text NEWLINE_TEXT = new LiteralText("\n");
    private static final Option[] OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AO, Option.FRAMERATE_LIMIT, Option.VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.CLOUDS, Option.FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.ENTITY_DISTANCE_SCALING};
    @Nullable
    private List<StringRenderable> tooltip;
    private ButtonListWidget list;
    private final class_5407 field_25688;
    private final int mipmapLevels;

    public VideoOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("options.videoTitle"));
        this.field_25688 = arg.client.method_30049();
        this.field_25688.method_30143();
        if (arg2.graphicsMode == GraphicsMode.FABULOUS) {
            this.field_25688.method_30139();
        }
        this.mipmapLevels = arg2.mipmapLevels;
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSingleOptionEntry(new FullScreenOption(this.client.getWindow()));
        this.list.addSingleOptionEntry(Option.BIOME_BLEND_RADIUS);
        this.list.addAll(OPTIONS);
        this.children.add(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, arg -> {
            this.client.options.write();
            this.client.getWindow().applyVideoMode();
            this.client.openScreen(this.parent);
        }));
    }

    @Override
    public void removed() {
        if (this.gameOptions.mipmapLevels != this.mipmapLevels) {
            this.client.resetMipmapLevels(this.gameOptions.mipmapLevels);
            this.client.reloadResourcesConcurrently();
        }
        super.removed();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int j = this.gameOptions.guiScale;
        GraphicsMode lv = this.gameOptions.graphicsMode;
        if (super.mouseClicked(d, e, i)) {
            if (this.gameOptions.guiScale != j) {
                this.client.onResolutionChanged();
            }
            if (this.field_25688.method_30141()) {
                String string3;
                String string2;
                ArrayList list = Lists.newArrayList((Object[])new StringRenderable[]{GRAPHICS_WARNING_MESSAGE_TEXT, NEWLINE_TEXT});
                String string = this.field_25688.method_30060();
                if (string != null) {
                    list.add(NEWLINE_TEXT);
                    list.add(new TranslatableText("options.graphics.warning.renderer", string).formatted(Formatting.GRAY));
                }
                if ((string2 = this.field_25688.method_30063()) != null) {
                    list.add(NEWLINE_TEXT);
                    list.add(new TranslatableText("options.graphics.warning.vendor", string2).formatted(Formatting.GRAY));
                }
                if ((string3 = this.field_25688.method_30062()) != null) {
                    list.add(NEWLINE_TEXT);
                    list.add(new TranslatableText("options.graphics.warning.version", string3).formatted(Formatting.GRAY));
                }
                this.client.openScreen(new GraphicsConfirmationScreen(GRAPHICS_WARNING_TITLE_TEXT, list, (ImmutableList<GraphicsConfirmationScreen.ChoiceButton>)ImmutableList.of((Object)new GraphicsConfirmationScreen.ChoiceButton(GRAPHICS_WARNING_ACCEPT_TEXT, arg -> {
                    this.gameOptions.graphicsMode = GraphicsMode.FABULOUS;
                    MinecraftClient.getInstance().worldRenderer.reload();
                    this.field_25688.method_30139();
                    this.client.openScreen(this);
                }), (Object)new GraphicsConfirmationScreen.ChoiceButton(GRAPHICS_WARNING_CANCEL_TEXT, arg -> {
                    this.field_25688.method_30140();
                    this.client.openScreen(this);
                }))));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        int j = this.gameOptions.guiScale;
        if (super.mouseReleased(d, e, i)) {
            return true;
        }
        if (this.list.mouseReleased(d, e, i)) {
            if (this.gameOptions.guiScale != j) {
                this.client.onResolutionChanged();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.tooltip = null;
        Optional<AbstractButtonWidget> optional = this.list.getHoveredButton(i, j);
        if (optional.isPresent() && optional.get() instanceof OptionButtonWidget) {
            Optional<List<StringRenderable>> optional2 = ((OptionButtonWidget)optional.get()).getOption().getTooltip();
            optional2.ifPresent(list -> {
                this.tooltip = list;
            });
        }
        this.renderBackground(arg);
        this.list.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(arg, i, j, f);
        if (this.tooltip != null) {
            this.renderTooltip(arg, this.tooltip, i, j);
        }
    }
}

