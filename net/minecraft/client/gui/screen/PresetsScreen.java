/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.class_5311;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PresetsScreen
extends Screen {
    private static final Logger field_25043 = LogManager.getLogger();
    private static final List<SuperflatPreset> presets = Lists.newArrayList();
    private final CustomizeFlatLevelScreen parent;
    private Text shareText;
    private Text listText;
    private SuperflatPresetsListWidget listWidget;
    private ButtonWidget selectPresetButton;
    private TextFieldWidget customPresetField;
    private class_5311 field_25044;

    public PresetsScreen(CustomizeFlatLevelScreen arg) {
        super(new TranslatableText("createWorld.customize.presets.title"));
        this.parent = arg;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static FlatChunkGeneratorLayer method_29059(String string, int i) {
        void lv2;
        int k;
        String[] strings = string.split("\\*", 2);
        if (strings.length == 2) {
            try {
                int j = Math.max(Integer.parseInt(strings[0]), 0);
            }
            catch (NumberFormatException numberFormatException) {
                field_25043.error("Error while parsing flat world string => {}", (Object)numberFormatException.getMessage());
                return null;
            }
        } else {
            k = 1;
        }
        int l = Math.min(i + k, 256);
        int m = l - i;
        String string2 = strings[strings.length - 1];
        try {
            Block lv = Registry.BLOCK.getOrEmpty(new Identifier(string2)).orElse(null);
        }
        catch (Exception exception) {
            field_25043.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            return null;
        }
        if (lv2 == null) {
            field_25043.error("Error while parsing flat world string => Unknown block, {}", (Object)string2);
            return null;
        }
        FlatChunkGeneratorLayer lv3 = new FlatChunkGeneratorLayer(m, (Block)lv2);
        lv3.setStartY(i);
        return lv3;
    }

    private static List<FlatChunkGeneratorLayer> method_29058(String string) {
        ArrayList list = Lists.newArrayList();
        String[] strings = string.split(",");
        int i = 0;
        for (String string2 : strings) {
            FlatChunkGeneratorLayer lv = PresetsScreen.method_29059(string2, i);
            if (lv == null) {
                return Collections.emptyList();
            }
            list.add(lv);
            i += lv.getThickness();
        }
        return list;
    }

    public static FlatChunkGeneratorConfig method_29060(String string, class_5311 arg) {
        Iterator iterator = Splitter.on((char)';').split((CharSequence)string).iterator();
        if (!iterator.hasNext()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        FlatChunkGeneratorConfig lv = new FlatChunkGeneratorConfig(arg);
        List<FlatChunkGeneratorLayer> list = PresetsScreen.method_29058((String)iterator.next());
        if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        lv.getLayers().addAll(list);
        lv.updateLayerBlocks();
        Biome lv2 = Biomes.PLAINS;
        if (iterator.hasNext()) {
            try {
                Identifier lv3 = new Identifier((String)iterator.next());
                lv2 = Registry.BIOME.getOrEmpty(lv3).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + lv3));
            }
            catch (Exception exception) {
                field_25043.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            }
        }
        lv.setBiome(lv2);
        return lv;
    }

    private static String method_29062(FlatChunkGeneratorConfig arg) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arg.getLayers().size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(arg.getLayers().get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(Registry.BIOME.getId(arg.getBiome()));
        return stringBuilder.toString();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.shareText = new TranslatableText("createWorld.customize.presets.share");
        this.listText = new TranslatableText("createWorld.customize.presets.list");
        this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
        this.customPresetField.setMaxLength(1230);
        this.customPresetField.setText(PresetsScreen.method_29062(this.parent.method_29055()));
        this.field_25044 = this.parent.method_29055().getConfig();
        this.children.add(this.customPresetField);
        this.listWidget = new SuperflatPresetsListWidget();
        this.children.add(this.listWidget);
        this.selectPresetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("createWorld.customize.presets.select"), arg -> {
            FlatChunkGeneratorConfig lv = PresetsScreen.method_29060(this.customPresetField.getText(), this.field_25044);
            this.parent.method_29054(lv);
            this.client.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.updateSelectButton(this.listWidget.getSelected() != null);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return this.listWidget.mouseScrolled(d, e, f);
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.customPresetField.getText();
        this.init(arg, i, j);
        this.customPresetField.setText(string);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.listWidget.render(arg, i, j, f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 400.0f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawTextWithShadow(arg, this.textRenderer, this.shareText, 50, 30, 0xA0A0A0);
        this.drawTextWithShadow(arg, this.textRenderer, this.listText, 50, 70, 0xA0A0A0);
        RenderSystem.popMatrix();
        this.customPresetField.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }

    @Override
    public void tick() {
        this.customPresetField.tick();
        super.tick();
    }

    public void updateSelectButton(boolean bl) {
        this.selectPresetButton.active = bl || this.customPresetField.getText().length() > 1;
    }

    private static void addPreset(Text arg, ItemConvertible arg2, Biome arg3, List<StructureFeature<?>> list, boolean bl, boolean bl2, boolean bl3, FlatChunkGeneratorLayer ... args) {
        HashMap map = Maps.newHashMap();
        for (StructureFeature<?> lv : list) {
            map.put(lv, class_5311.field_24822.get(lv));
        }
        class_5311 lv2 = new class_5311(bl ? Optional.of(class_5311.field_24823) : Optional.empty(), map);
        FlatChunkGeneratorConfig lv3 = new FlatChunkGeneratorConfig(lv2);
        if (bl2) {
            lv3.method_28911();
        }
        if (bl3) {
            lv3.method_28916();
        }
        for (int i = args.length - 1; i >= 0; --i) {
            lv3.getLayers().add(args[i]);
        }
        lv3.setBiome(arg3);
        lv3.updateLayerBlocks();
        presets.add(new SuperflatPreset(arg2.asItem(), arg, lv3.method_28912(lv2)));
    }

    static {
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE), false, false, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(2, Blocks.DIRT), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList(StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.MINESHAFT), true, true, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(230, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList(StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.MONUMENT), false, false, false, new FlatChunkGeneratorLayer(90, Blocks.WATER), new FlatChunkGeneratorLayer(5, Blocks.SAND), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(5, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.MINESHAFT, StructureFeature.PILLAGER_OUTPOST, StructureFeature.RUINED_PORTAL), true, true, true, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK), false, false, false, new FlatChunkGeneratorLayer(1, Blocks.SNOW), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK), false, false, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(2, Blocks.COBBLESTONE));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.field_24851, StructureFeature.DESERT_PYRAMID, StructureFeature.JUNGLE_PYRAMID, StructureFeature.IGLOO, StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.MINESHAFT), true, true, false, new FlatChunkGeneratorLayer(8, Blocks.SAND), new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), false, false, false, new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        PresetsScreen.addPreset(new TranslatableText("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Collections.emptyList(), false, true, false, new FlatChunkGeneratorLayer(1, Blocks.AIR));
    }

    @Environment(value=EnvType.CLIENT)
    static class SuperflatPreset {
        public final Item icon;
        public final Text name;
        public final FlatChunkGeneratorConfig field_25045;

        public SuperflatPreset(Item arg, Text arg2, FlatChunkGeneratorConfig arg3) {
            this.icon = arg;
            this.name = arg2;
            this.field_25045 = arg3;
        }

        public Text method_27571() {
            return this.name;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatPresetsListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatPresetEntry> {
        public SuperflatPresetsListWidget() {
            super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);
            for (int i = 0; i < presets.size(); ++i) {
                this.addEntry(new SuperflatPresetEntry());
            }
        }

        @Override
        public void setSelected(@Nullable SuperflatPresetEntry arg) {
            super.setSelected(arg);
            if (arg != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", ((SuperflatPreset)presets.get(this.children().indexOf(arg))).method_27571()).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
            PresetsScreen.this.updateSelectButton(true);
        }

        @Override
        protected boolean isFocused() {
            return PresetsScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (super.keyPressed(i, j, k)) {
                return true;
            }
            if ((i == 257 || i == 335) && this.getSelected() != null) {
                ((SuperflatPresetEntry)this.getSelected()).setPreset();
            }
            return false;
        }

        @Environment(value=EnvType.CLIENT)
        public class SuperflatPresetEntry
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatPresetEntry> {
            @Override
            public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                SuperflatPreset lv = (SuperflatPreset)presets.get(i);
                this.method_2200(arg, k, j, lv.icon);
                PresetsScreen.this.textRenderer.draw(arg, lv.name, (float)(k + 18 + 5), (float)(j + 6), 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    this.setPreset();
                }
                return false;
            }

            private void setPreset() {
                SuperflatPresetsListWidget.this.setSelected(this);
                PresetsScreen.this.updateSelectButton(true);
                SuperflatPreset lv = (SuperflatPreset)presets.get(SuperflatPresetsListWidget.this.children().indexOf(this));
                PresetsScreen.this.customPresetField.setText(PresetsScreen.method_29062(lv.field_25045));
                PresetsScreen.this.customPresetField.setCursorToStart();
                PresetsScreen.this.field_25044 = lv.field_25045.getConfig();
            }

            private void method_2200(MatrixStack arg, int i, int j, Item arg2) {
                this.method_2198(arg, i + 1, j + 1);
                RenderSystem.enableRescaleNormal();
                PresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(arg2), i + 2, j + 2);
                RenderSystem.disableRescaleNormal();
            }

            private void method_2198(MatrixStack arg, int i, int j) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatPresetsListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
                DrawableHelper.drawTexture(arg, i, j, PresetsScreen.this.getZOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

