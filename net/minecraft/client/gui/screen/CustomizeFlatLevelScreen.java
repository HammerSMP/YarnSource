/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(value=EnvType.CLIENT)
public class CustomizeFlatLevelScreen
extends Screen {
    private final Screen parent;
    private final Consumer<FlatChunkGeneratorConfig> field_24565;
    private FlatChunkGeneratorConfig config;
    private Text tileText;
    private Text heightText;
    private SuperflatLayersListWidget layers;
    private ButtonWidget widgetButtonRemoveLayer;

    public CustomizeFlatLevelScreen(Screen arg, Consumer<FlatChunkGeneratorConfig> consumer, FlatChunkGeneratorConfig arg2) {
        super(new TranslatableText("createWorld.customize.flat.title"));
        this.parent = arg;
        this.field_24565 = consumer;
        this.config = arg2;
    }

    public FlatChunkGeneratorConfig method_29055() {
        return this.config;
    }

    public void method_29054(FlatChunkGeneratorConfig arg) {
        this.config = arg;
    }

    @Override
    protected void init() {
        this.tileText = new TranslatableText("createWorld.customize.flat.tile");
        this.heightText = new TranslatableText("createWorld.customize.flat.height");
        this.layers = new SuperflatLayersListWidget();
        this.children.add(this.layers);
        this.widgetButtonRemoveLayer = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.flat.removeLayer"), arg -> {
            if (!this.method_2147()) {
                return;
            }
            List<FlatChunkGeneratorLayer> list = this.config.getLayers();
            int i = this.layers.children().indexOf(this.layers.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.layers.setSelected(list.isEmpty() ? null : (SuperflatLayersListWidget.SuperflatLayerItem)this.layers.children().get(Math.min(i, list.size() - 1)));
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.presets"), arg -> {
            this.client.openScreen(new PresetsScreen(this));
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, arg -> {
            this.field_24565.accept(this.config);
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> {
            this.client.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.method_2145();
        }));
        this.config.updateLayerBlocks();
        this.method_2145();
    }

    public void method_2145() {
        this.widgetButtonRemoveLayer.active = this.method_2147();
        this.layers.method_19372();
    }

    private boolean method_2147() {
        return this.layers.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.layers.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        int k = this.width / 2 - 92 - 16;
        this.drawTextWithShadow(arg, this.textRenderer, this.tileText, k, 32, 0xFFFFFF);
        this.drawTextWithShadow(arg, this.textRenderer, this.heightText, k + 2 + 213 - this.textRenderer.getWidth(this.heightText), 32, 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatLayersListWidget
    extends AlwaysSelectedEntryListWidget<SuperflatLayerItem> {
        public SuperflatLayersListWidget() {
            super(CustomizeFlatLevelScreen.this.client, CustomizeFlatLevelScreen.this.width, CustomizeFlatLevelScreen.this.height, 43, CustomizeFlatLevelScreen.this.height - 60, 24);
            for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++i) {
                this.addEntry(new SuperflatLayerItem());
            }
        }

        @Override
        public void setSelected(@Nullable SuperflatLayerItem arg) {
            FlatChunkGeneratorLayer lv;
            Item lv2;
            super.setSelected(arg);
            if (arg != null && (lv2 = (lv = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(arg) - 1)).getBlockState().getBlock().asItem()) != Items.AIR) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", lv2.getName(new ItemStack(lv2))).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
            CustomizeFlatLevelScreen.this.method_2145();
        }

        @Override
        protected boolean isFocused() {
            return CustomizeFlatLevelScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 70;
        }

        public void method_19372() {
            int i = this.children().indexOf(this.getSelected());
            this.clearEntries();
            for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++j) {
                this.addEntry(new SuperflatLayerItem());
            }
            List list = this.children();
            if (i >= 0 && i < list.size()) {
                this.setSelected((SuperflatLayerItem)list.get(i));
            }
        }

        @Environment(value=EnvType.CLIENT)
        class SuperflatLayerItem
        extends AlwaysSelectedEntryListWidget.Entry<SuperflatLayerItem> {
            private SuperflatLayerItem() {
            }

            @Override
            public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                String string3;
                FlatChunkGeneratorLayer lv = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - i - 1);
                BlockState lv2 = lv.getBlockState();
                Item lv3 = lv2.getBlock().asItem();
                if (lv3 == Items.AIR) {
                    if (lv2.isOf(Blocks.WATER)) {
                        lv3 = Items.WATER_BUCKET;
                    } else if (lv2.isOf(Blocks.LAVA)) {
                        lv3 = Items.LAVA_BUCKET;
                    }
                }
                ItemStack lv4 = new ItemStack(lv3);
                this.method_19375(arg, k, j, lv4);
                CustomizeFlatLevelScreen.this.textRenderer.draw(arg, lv3.getName(lv4), (float)(k + 18 + 5), (float)(j + 3), 0xFFFFFF);
                if (i == 0) {
                    String string = I18n.translate("createWorld.customize.flat.layer.top", lv.getThickness());
                } else if (i == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
                    String string2 = I18n.translate("createWorld.customize.flat.layer.bottom", lv.getThickness());
                } else {
                    string3 = I18n.translate("createWorld.customize.flat.layer", lv.getThickness());
                }
                CustomizeFlatLevelScreen.this.textRenderer.draw(arg, string3, (float)(k + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getWidth(string3)), (float)(j + 3), 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    SuperflatLayersListWidget.this.setSelected(this);
                    CustomizeFlatLevelScreen.this.method_2145();
                    return true;
                }
                return false;
            }

            private void method_19375(MatrixStack arg, int i, int j, ItemStack arg2) {
                this.method_19373(arg, i + 1, j + 1);
                RenderSystem.enableRescaleNormal();
                if (!arg2.isEmpty()) {
                    CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(arg2, i + 2, j + 2);
                }
                RenderSystem.disableRescaleNormal();
            }

            private void method_19373(MatrixStack arg, int i, int j) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatLayersListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
                DrawableHelper.drawTexture(arg, i, j, CustomizeFlatLevelScreen.this.getZOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}

