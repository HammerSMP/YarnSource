/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.FullScreenOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class VideoOptionsScreen
extends GameOptionsScreen {
    @Nullable
    private List<class_5348> field_25453;
    private ButtonListWidget list;
    private static final Option[] OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AO, Option.FRAMERATE_LIMIT, Option.VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.CLOUDS, Option.FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.ENTITY_DISTANCE_SCALING};
    private int mipmapLevels;

    public VideoOptionsScreen(Screen arg, GameOptions arg2) {
        super(arg, arg2, new TranslatableText("options.videoTitle"));
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
        if (super.mouseClicked(d, e, i)) {
            if (this.gameOptions.guiScale != j) {
                this.client.onResolutionChanged();
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
        Optional<TranslatableText> optional2;
        this.field_25453 = null;
        Optional<AbstractButtonWidget> optional = this.list.method_29624(i, j);
        if (optional.isPresent() && optional.get() instanceof OptionButtonWidget && (optional2 = ((OptionButtonWidget)optional.get()).method_29623().method_29619()).isPresent()) {
            ImmutableList.Builder builder = ImmutableList.builder();
            this.textRenderer.wrapLines(optional2.get(), 200).forEach(((ImmutableList.Builder)builder)::add);
            this.field_25453 = builder.build();
        }
        this.renderBackground(arg);
        this.list.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(arg, i, j, f);
        if (this.field_25453 != null) {
            this.renderTooltip(arg, this.field_25453, i, j);
        }
    }
}

