/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;

@Environment(value=EnvType.CLIENT)
public class OptimizeWorldScreen
extends Screen {
    private static final Object2IntMap<RegistryKey<World>> DIMENSION_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityHashStrategy()), object2IntOpenCustomHashMap -> {
        object2IntOpenCustomHashMap.put(World.field_25179, -13408734);
        object2IntOpenCustomHashMap.put(World.field_25180, -10075085);
        object2IntOpenCustomHashMap.put(World.field_25181, -8943531);
        object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpdater updater;

    public static OptimizeWorldScreen method_27031(BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorage.Session arg, boolean bl) {
        SaveProperties lv = arg.readLevelProperties();
        return new OptimizeWorldScreen(booleanConsumer, dataFixer, arg, lv, bl);
    }

    private OptimizeWorldScreen(BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorage.Session arg, SaveProperties arg2, boolean bl) {
        super(new TranslatableText("optimizeWorld.title", arg2.getLevelName()));
        this.callback = booleanConsumer;
        this.updater = new WorldUpdater(arg, dataFixer, arg2, bl);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, 200, 20, ScreenTexts.CANCEL, arg -> {
            this.updater.cancel();
            this.callback.accept(false);
        }));
    }

    @Override
    public void tick() {
        if (this.updater.isDone()) {
            this.callback.accept(true);
        }
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.updater.cancel();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int k = this.width / 2 - 150;
        int l = this.width / 2 + 150;
        int m = this.height / 4 + 100;
        int n = m + 10;
        this.textRenderer.getClass();
        this.drawCenteredText(arg, this.textRenderer, this.updater.getStatus(), this.width / 2, m - 9 - 2, 0xA0A0A0);
        if (this.updater.getTotalChunkCount() > 0) {
            OptimizeWorldScreen.fill(arg, k - 1, m - 1, l + 1, n + 1, -16777216);
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), k, 40, 0xA0A0A0);
            this.textRenderer.getClass();
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), k, 40 + 9 + 3, 0xA0A0A0);
            this.textRenderer.getClass();
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("optimizeWorld.info.total", this.updater.getTotalChunkCount()), k, 40 + (9 + 3) * 2, 0xA0A0A0);
            int o = 0;
            for (RegistryKey lv : this.updater.method_28304()) {
                int p = MathHelper.floor(this.updater.getProgress(lv) * (float)(l - k));
                OptimizeWorldScreen.fill(arg, k + o, m, k + o + p, n, DIMENSION_COLORS.getInt((Object)lv));
                o += p;
            }
            int q = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
            this.textRenderer.getClass();
            this.drawCenteredString(arg, this.textRenderer, q + " / " + this.updater.getTotalChunkCount(), this.width / 2, m + 2 * 9 + 2, 0xA0A0A0);
            this.textRenderer.getClass();
            this.drawCenteredString(arg, this.textRenderer, MathHelper.floor(this.updater.getProgress() * 100.0f) + "%", this.width / 2, m + (n - m) / 2 - 9 / 2, 0xA0A0A0);
        }
        super.render(arg, i, j, f);
    }
}

