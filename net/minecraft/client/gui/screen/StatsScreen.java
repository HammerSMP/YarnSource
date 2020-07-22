/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class StatsScreen
extends Screen
implements StatsListener {
    protected final Screen parent;
    private GeneralStatsListWidget generalStats;
    private ItemStatsListWidget itemStats;
    private EntityStatsListWidget mobStats;
    private final StatHandler statHandler;
    @Nullable
    private AlwaysSelectedEntryListWidget<?> selectedList;
    private boolean downloadingStats = true;

    public StatsScreen(Screen parent, StatHandler statHandler) {
        super(new TranslatableText("gui.stats"));
        this.parent = parent;
        this.statHandler = statHandler;
    }

    @Override
    protected void init() {
        this.downloadingStats = true;
        this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
    }

    public void createLists() {
        this.generalStats = new GeneralStatsListWidget(this.client);
        this.itemStats = new ItemStatsListWidget(this.client);
        this.mobStats = new EntityStatsListWidget(this.client);
    }

    public void createButtons() {
        this.addButton(new ButtonWidget(this.width / 2 - 120, this.height - 52, 80, 20, new TranslatableText("stat.generalButton"), arg -> this.selectStatList(this.generalStats)));
        ButtonWidget lv = this.addButton(new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableText("stat.itemsButton"), arg -> this.selectStatList(this.itemStats)));
        ButtonWidget lv2 = this.addButton(new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, new TranslatableText("stat.mobsButton"), arg -> this.selectStatList(this.mobStats)));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.DONE, arg -> this.client.openScreen(this.parent)));
        if (this.itemStats.children().isEmpty()) {
            lv.active = false;
        }
        if (this.mobStats.children().isEmpty()) {
            lv2.active = false;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.downloadingStats) {
            this.renderBackground(matrices);
            this.drawCenteredString(matrices, this.textRenderer, I18n.translate("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 0xFFFFFF);
            this.textRenderer.getClass();
            this.drawCenteredString(matrices, this.textRenderer, PROGRESS_BAR_STAGES[(int)(Util.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)], this.width / 2, this.height / 2 + 9 * 2, 0xFFFFFF);
        } else {
            this.getSelectedStatList().render(matrices, mouseX, mouseY, delta);
            this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void onStatsReady() {
        if (this.downloadingStats) {
            this.createLists();
            this.createButtons();
            this.selectStatList(this.generalStats);
            this.downloadingStats = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !this.downloadingStats;
    }

    @Nullable
    public AlwaysSelectedEntryListWidget<?> getSelectedStatList() {
        return this.selectedList;
    }

    public void selectStatList(@Nullable AlwaysSelectedEntryListWidget<?> list) {
        this.children.remove(this.generalStats);
        this.children.remove(this.itemStats);
        this.children.remove(this.mobStats);
        if (list != null) {
            this.children.add(0, list);
            this.selectedList = list;
        }
    }

    private static String method_27027(Stat<Identifier> arg) {
        return "stat." + arg.getValue().toString().replace(':', '.');
    }

    private int getColumnX(int index) {
        return 115 + 40 * index;
    }

    private void renderStatItem(MatrixStack arg, int i, int j, Item arg2) {
        this.renderIcon(arg, i + 1, j + 1, 0, 0);
        RenderSystem.enableRescaleNormal();
        this.itemRenderer.renderGuiItemIcon(arg2.getStackForRender(), i + 2, j + 2);
        RenderSystem.disableRescaleNormal();
    }

    private void renderIcon(MatrixStack arg, int i, int j, int k, int l) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(STATS_ICON_TEXTURE);
        StatsScreen.drawTexture(arg, i, j, this.getZOffset(), k, l, 18, 18, 128, 128);
    }

    @Environment(value=EnvType.CLIENT)
    class EntityStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public EntityStatsListWidget(MinecraftClient arg2) {
            StatsScreen.this.textRenderer.getClass();
            super(arg2, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);
            for (EntityType entityType : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(entityType)) <= 0 && StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(entityType)) <= 0) continue;
                this.addEntry(new Entry(entityType));
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final EntityType<?> entityType;

            public Entry(EntityType<?> arg2) {
                this.entityType = arg2;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                String string = I18n.translate(Util.createTranslationKey("entity", EntityType.getId(this.entityType)), new Object[0]);
                int p = StatsScreen.this.statHandler.getStat(Stats.KILLED.getOrCreateStat(this.entityType));
                int q = StatsScreen.this.statHandler.getStat(Stats.KILLED_BY.getOrCreateStat(this.entityType));
                EntityStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, string, x + 2, y + 1, 0xFFFFFF);
                StatsScreen.this.textRenderer.getClass();
                EntityStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, this.getKilledString(string, p), x + 2 + 10, y + 1 + 9, p == 0 ? 0x606060 : 0x909090);
                StatsScreen.this.textRenderer.getClass();
                EntityStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, this.getKilledByString(string, q), x + 2 + 10, y + 1 + 9 * 2, q == 0 ? 0x606060 : 0x909090);
            }

            private String getKilledString(String entityName, int killCount) {
                String string2 = Stats.KILLED.getTranslationKey();
                if (killCount == 0) {
                    return I18n.translate(string2 + ".none", entityName);
                }
                return I18n.translate(string2, killCount, entityName);
            }

            private String getKilledByString(String entityName, int killCount) {
                String string2 = Stats.KILLED_BY.getTranslationKey();
                if (killCount == 0) {
                    return I18n.translate(string2 + ".none", entityName);
                }
                return I18n.translate(string2, entityName, killCount);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ItemStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        protected final List<StatType<Block>> blockStatTypes;
        protected final List<StatType<Item>> itemStatTypes;
        private final int[] HEADER_ICON_SPRITE_INDICES;
        protected int selectedHeaderColumn;
        protected final List<Item> items;
        protected final Comparator<Item> comparator;
        @Nullable
        protected StatType<?> selectedStatType;
        protected int field_18760;

        public ItemStatsListWidget(MinecraftClient client) {
            super(client, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.HEADER_ICON_SPRITE_INDICES = new int[]{3, 4, 1, 2, 5, 6};
            this.selectedHeaderColumn = -1;
            this.comparator = new ItemComparator();
            this.blockStatTypes = Lists.newArrayList();
            this.blockStatTypes.add(Stats.MINED);
            this.itemStatTypes = Lists.newArrayList((Object[])new StatType[]{Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED});
            this.setRenderHeader(true, 20);
            Set set = Sets.newIdentityHashSet();
            for (Item lv : Registry.ITEM) {
                boolean bl = false;
                for (StatType<Item> statType : this.itemStatTypes) {
                    if (!statType.hasStat(lv) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(lv)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(lv);
            }
            for (Block lv3 : Registry.BLOCK) {
                boolean bl2 = false;
                for (StatType<ItemConvertible> statType : this.blockStatTypes) {
                    if (!statType.hasStat(lv3) || StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(lv3)) <= 0) continue;
                    bl2 = true;
                }
                if (!bl2) continue;
                set.add(lv3.asItem());
            }
            set.remove(Items.AIR);
            this.items = Lists.newArrayList((Iterable)set);
            for (int i = 0; i < this.items.size(); ++i) {
                this.addEntry(new Entry());
            }
        }

        @Override
        protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator arg2) {
            if (!this.client.mouse.wasLeftButtonClicked()) {
                this.selectedHeaderColumn = -1;
            }
            for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; ++k) {
                StatsScreen.this.renderIcon(matrices, x + StatsScreen.this.getColumnX(k) - 18, y + 1, 0, this.selectedHeaderColumn == k ? 0 : 18);
            }
            if (this.selectedStatType != null) {
                int l = StatsScreen.this.getColumnX(this.getHeaderIndex(this.selectedStatType)) - 36;
                int m = this.field_18760 == 1 ? 2 : 1;
                StatsScreen.this.renderIcon(matrices, x + l, y + 1, 18 * m, 0);
            }
            for (int n = 0; n < this.HEADER_ICON_SPRITE_INDICES.length; ++n) {
                int o = this.selectedHeaderColumn == n ? 1 : 0;
                StatsScreen.this.renderIcon(matrices, x + StatsScreen.this.getColumnX(n) - 18 + o, y + 1 + o, 18 * this.HEADER_ICON_SPRITE_INDICES[n], 18);
            }
        }

        @Override
        public int getRowWidth() {
            return 375;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width / 2 + 140;
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
        }

        @Override
        protected void clickedHeader(int x, int y) {
            this.selectedHeaderColumn = -1;
            for (int k = 0; k < this.HEADER_ICON_SPRITE_INDICES.length; ++k) {
                int l = x - StatsScreen.this.getColumnX(k);
                if (l < -36 || l > 0) continue;
                this.selectedHeaderColumn = k;
                break;
            }
            if (this.selectedHeaderColumn >= 0) {
                this.selectStatType(this.getStatType(this.selectedHeaderColumn));
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

        private StatType<?> getStatType(int headerColumn) {
            return headerColumn < this.blockStatTypes.size() ? this.blockStatTypes.get(headerColumn) : this.itemStatTypes.get(headerColumn - this.blockStatTypes.size());
        }

        private int getHeaderIndex(StatType<?> statType) {
            int i = this.blockStatTypes.indexOf(statType);
            if (i >= 0) {
                return i;
            }
            int j = this.itemStatTypes.indexOf(statType);
            if (j >= 0) {
                return j + this.blockStatTypes.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(MatrixStack arg, int i, int j) {
            if (j < this.top || j > this.bottom) {
                return;
            }
            Entry lv = (Entry)this.getEntryAtPosition(i, j);
            int k = (this.width - this.getRowWidth()) / 2;
            if (lv != null) {
                if (i < k + 40 || i > k + 40 + 20) {
                    return;
                }
                Item lv2 = this.items.get(this.children().indexOf(lv));
                this.render(arg, this.getText(lv2), i, j);
            } else {
                TranslatableText lv3 = null;
                int l = i - k;
                for (int m = 0; m < this.HEADER_ICON_SPRITE_INDICES.length; ++m) {
                    int n = StatsScreen.this.getColumnX(m);
                    if (l < n - 18 || l > n) continue;
                    lv3 = new TranslatableText(this.getStatType(m).getTranslationKey());
                    break;
                }
                this.render(arg, lv3, i, j);
            }
        }

        protected void render(MatrixStack arg, @Nullable Text arg2, int i, int j) {
            if (arg2 == null) {
                return;
            }
            int k = i + 12;
            int l = j - 12;
            int m = StatsScreen.this.textRenderer.getWidth(arg2);
            this.fillGradient(arg, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 400.0f);
            StatsScreen.this.textRenderer.drawWithShadow(arg, arg2, (float)k, (float)l, -1);
            RenderSystem.popMatrix();
        }

        protected Text getText(Item item) {
            return item.getName();
        }

        protected void selectStatType(StatType<?> statType) {
            if (statType != this.selectedStatType) {
                this.selectedStatType = statType;
                this.field_18760 = -1;
            } else if (this.field_18760 == -1) {
                this.field_18760 = 1;
            } else {
                this.selectedStatType = null;
                this.field_18760 = 0;
            }
            this.items.sort(this.comparator);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private Entry() {
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                Item lv = ((StatsScreen)StatsScreen.this).itemStats.items.get(index);
                StatsScreen.this.renderStatItem(matrices, x + 40, y, lv);
                for (int p = 0; p < ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.size(); ++p) {
                    Stat<?> lv3;
                    if (lv instanceof BlockItem) {
                        Stat<Block> lv2 = ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.get(p).getOrCreateStat(((BlockItem)lv).getBlock());
                    } else {
                        lv3 = null;
                    }
                    this.render(matrices, lv3, x + StatsScreen.this.getColumnX(p), y, index % 2 == 0);
                }
                for (int q = 0; q < ((StatsScreen)StatsScreen.this).itemStats.itemStatTypes.size(); ++q) {
                    this.render(matrices, ((StatsScreen)StatsScreen.this).itemStats.itemStatTypes.get(q).getOrCreateStat(lv), x + StatsScreen.this.getColumnX(q + ((StatsScreen)StatsScreen.this).itemStats.blockStatTypes.size()), y, index % 2 == 0);
                }
            }

            protected void render(MatrixStack arg, @Nullable Stat<?> arg2, int i, int j, boolean bl) {
                String string = arg2 == null ? "-" : arg2.format(StatsScreen.this.statHandler.getStat(arg2));
                ItemStatsListWidget.this.drawStringWithShadow(arg, StatsScreen.this.textRenderer, string, i - StatsScreen.this.textRenderer.getWidth(string), j + 5, bl ? 0xFFFFFF : 0x909090);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class ItemComparator
        implements Comparator<Item> {
            private ItemComparator() {
            }

            @Override
            public int compare(Item arg, Item arg2) {
                int n;
                int m;
                if (ItemStatsListWidget.this.selectedStatType == null) {
                    boolean i = false;
                    boolean j = false;
                } else if (ItemStatsListWidget.this.blockStatTypes.contains(ItemStatsListWidget.this.selectedStatType)) {
                    StatType<?> lv = ItemStatsListWidget.this.selectedStatType;
                    int k = arg instanceof BlockItem ? StatsScreen.this.statHandler.getStat(lv, ((BlockItem)arg).getBlock()) : -1;
                    int l = arg2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(lv, ((BlockItem)arg2).getBlock()) : -1;
                } else {
                    StatType<?> lv2 = ItemStatsListWidget.this.selectedStatType;
                    m = StatsScreen.this.statHandler.getStat(lv2, arg);
                    n = StatsScreen.this.statHandler.getStat(lv2, arg2);
                }
                if (m == n) {
                    return ItemStatsListWidget.this.field_18760 * Integer.compare(Item.getRawId(arg), Item.getRawId(arg2));
                }
                return ItemStatsListWidget.this.field_18760 * Integer.compare(m, n);
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.compare((Item)object, (Item)object2);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class GeneralStatsListWidget
    extends AlwaysSelectedEntryListWidget<Entry> {
        public GeneralStatsListWidget(MinecraftClient arg22) {
            super(arg22, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            ObjectArrayList objectArrayList = new ObjectArrayList(Stats.CUSTOM.iterator());
            objectArrayList.sort(Comparator.comparing(arg -> I18n.translate(StatsScreen.method_27027(arg), new Object[0])));
            for (Stat lv : objectArrayList) {
                this.addEntry(new Entry(lv));
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            StatsScreen.this.renderBackground(matrices);
        }

        @Environment(value=EnvType.CLIENT)
        class Entry
        extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final Stat<Identifier> stat;

            private Entry(Stat<Identifier> stat) {
                this.stat = stat;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                MutableText lv = new TranslatableText(StatsScreen.method_27027(this.stat)).formatted(Formatting.GRAY);
                GeneralStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, lv.getString(), x + 2, y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
                String string = this.stat.format(StatsScreen.this.statHandler.getStat(this.stat));
                GeneralStatsListWidget.this.drawStringWithShadow(matrices, StatsScreen.this.textRenderer, string, x + 2 + 213 - StatsScreen.this.textRenderer.getWidth(string), y + 1, index % 2 == 0 ? 0xFFFFFF : 0x909090);
            }
        }
    }
}

