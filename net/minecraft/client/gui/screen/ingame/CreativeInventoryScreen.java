/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryScreen
extends AbstractInventoryScreen<CreativeScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final SimpleInventory inventory = new SimpleInventory(45);
    private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
    private float scrollPosition;
    private boolean scrolling;
    private TextFieldWidget searchBox;
    @Nullable
    private List<Slot> slots;
    @Nullable
    private Slot deleteItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTypedCharacter;
    private boolean lastClickOutsideBounds;
    private final Map<Identifier, Tag<Item>> searchResultTags = Maps.newTreeMap();

    public CreativeInventoryScreen(PlayerEntity arg) {
        super(new CreativeScreenHandler(arg), arg.inventory, LiteralText.EMPTY);
        arg.currentScreenHandler = this.handler;
        this.passEvents = true;
        this.backgroundHeight = 136;
        this.backgroundWidth = 195;
    }

    @Override
    public void tick() {
        if (!this.client.interactionManager.hasCreativeInventory()) {
            this.client.openScreen(new InventoryScreen(this.client.player));
        } else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    protected void onMouseClick(@Nullable Slot arg, int i, int j, SlotActionType arg2) {
        if (this.isCreativeInventorySlot(arg)) {
            this.searchBox.setCursorToEnd();
            this.searchBox.setSelectionEnd(0);
        }
        boolean bl = arg2 == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType = arg2 = i == -999 && arg2 == SlotActionType.PICKUP ? SlotActionType.THROW : arg2;
        if (arg != null || selectedTab == ItemGroup.INVENTORY.getIndex() || arg2 == SlotActionType.QUICK_CRAFT) {
            if (arg != null && !arg.canTakeItems(this.client.player)) {
                return;
            }
            if (arg == this.deleteItemSlot && bl) {
                for (int k = 0; k < this.client.player.playerScreenHandler.getStacks().size(); ++k) {
                    this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, k);
                }
            } else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
                if (arg == this.deleteItemSlot) {
                    this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else if (arg2 == SlotActionType.THROW && arg != null && arg.hasStack()) {
                    ItemStack lv = arg.takeStack(j == 0 ? 1 : arg.getStack().getMaxCount());
                    ItemStack lv2 = arg.getStack();
                    this.client.player.dropItem(lv, true);
                    this.client.interactionManager.dropCreativeStack(lv);
                    this.client.interactionManager.clickCreativeStack(lv2, ((CreativeSlot)((CreativeSlot)arg)).slot.id);
                } else if (arg2 == SlotActionType.THROW && !this.client.player.inventory.getCursorStack().isEmpty()) {
                    this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(this.client.player.inventory.getCursorStack());
                    this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else {
                    this.client.player.playerScreenHandler.onSlotClick(arg == null ? i : ((CreativeSlot)((CreativeSlot)arg)).slot.id, j, arg2, this.client.player);
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            } else if (arg2 != SlotActionType.QUICK_CRAFT && arg.inventory == inventory) {
                PlayerInventory lv3 = this.client.player.inventory;
                ItemStack lv4 = lv3.getCursorStack();
                ItemStack lv5 = arg.getStack();
                if (arg2 == SlotActionType.SWAP) {
                    if (!lv5.isEmpty()) {
                        ItemStack lv6 = lv5.copy();
                        lv6.setCount(lv6.getMaxCount());
                        this.client.player.inventory.setStack(j, lv6);
                        this.client.player.playerScreenHandler.sendContentUpdates();
                    }
                    return;
                }
                if (arg2 == SlotActionType.CLONE) {
                    if (lv3.getCursorStack().isEmpty() && arg.hasStack()) {
                        ItemStack lv7 = arg.getStack().copy();
                        lv7.setCount(lv7.getMaxCount());
                        lv3.setCursorStack(lv7);
                    }
                    return;
                }
                if (arg2 == SlotActionType.THROW) {
                    if (!lv5.isEmpty()) {
                        ItemStack lv8 = lv5.copy();
                        lv8.setCount(j == 0 ? 1 : lv8.getMaxCount());
                        this.client.player.dropItem(lv8, true);
                        this.client.interactionManager.dropCreativeStack(lv8);
                    }
                    return;
                }
                if (!lv4.isEmpty() && !lv5.isEmpty() && lv4.isItemEqualIgnoreDamage(lv5) && ItemStack.areTagsEqual(lv4, lv5)) {
                    if (j == 0) {
                        if (bl) {
                            lv4.setCount(lv4.getMaxCount());
                        } else if (lv4.getCount() < lv4.getMaxCount()) {
                            lv4.increment(1);
                        }
                    } else {
                        lv4.decrement(1);
                    }
                } else if (lv5.isEmpty() || !lv4.isEmpty()) {
                    if (j == 0) {
                        lv3.setCursorStack(ItemStack.EMPTY);
                    } else {
                        lv3.getCursorStack().decrement(1);
                    }
                } else {
                    lv3.setCursorStack(lv5.copy());
                    lv4 = lv3.getCursorStack();
                    if (bl) {
                        lv4.setCount(lv4.getMaxCount());
                    }
                }
            } else if (this.handler != null) {
                ItemStack lv9 = arg == null ? ItemStack.EMPTY : ((CreativeScreenHandler)this.handler).getSlot(arg.id).getStack();
                ((CreativeScreenHandler)this.handler).onSlotClick(arg == null ? i : arg.id, j, arg2, this.client.player);
                if (ScreenHandler.unpackQuickCraftStage(j) == 2) {
                    for (int l = 0; l < 9; ++l) {
                        this.client.interactionManager.clickCreativeStack(((CreativeScreenHandler)this.handler).getSlot(45 + l).getStack(), 36 + l);
                    }
                } else if (arg != null) {
                    ItemStack lv10 = ((CreativeScreenHandler)this.handler).getSlot(arg.id).getStack();
                    this.client.interactionManager.clickCreativeStack(lv10, arg.id - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    int m = 45 + j;
                    if (arg2 == SlotActionType.SWAP) {
                        this.client.interactionManager.clickCreativeStack(lv9, m - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    } else if (arg2 == SlotActionType.THROW && !lv9.isEmpty()) {
                        ItemStack lv11 = lv9.copy();
                        lv11.setCount(j == 0 ? 1 : lv11.getMaxCount());
                        this.client.player.dropItem(lv11, true);
                        this.client.interactionManager.dropCreativeStack(lv11);
                    }
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            }
        } else {
            PlayerInventory lv12 = this.client.player.inventory;
            if (!lv12.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
                if (j == 0) {
                    this.client.player.dropItem(lv12.getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(lv12.getCursorStack());
                    lv12.setCursorStack(ItemStack.EMPTY);
                }
                if (j == 1) {
                    ItemStack lv13 = lv12.getCursorStack().split(1);
                    this.client.player.dropItem(lv13, true);
                    this.client.interactionManager.dropCreativeStack(lv13);
                }
            }
        }
    }

    private boolean isCreativeInventorySlot(@Nullable Slot arg) {
        return arg != null && arg.inventory == inventory;
    }

    @Override
    protected void applyStatusEffectOffset() {
        int i = this.x;
        super.applyStatusEffectOffset();
        if (this.searchBox != null && this.x != i) {
            this.searchBox.setX(this.x + 82);
        }
    }

    @Override
    protected void init() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            super.init();
            this.client.keyboard.enableRepeatEvents(true);
            this.textRenderer.getClass();
            this.searchBox = new TextFieldWidget(this.textRenderer, this.x + 82, this.y + 6, 80, 9, new TranslatableText("itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setHasBorder(false);
            this.searchBox.setVisible(false);
            this.searchBox.setEditableColor(0xFFFFFF);
            this.children.add(this.searchBox);
            int i = selectedTab;
            selectedTab = -1;
            this.setSelectedTab(ItemGroup.GROUPS[i]);
            this.client.player.playerScreenHandler.removeListener(this.listener);
            this.listener = new CreativeInventoryListener(this.client);
            this.client.player.playerScreenHandler.addListener(this.listener);
        } else {
            this.client.openScreen(new InventoryScreen(this.client.player));
        }
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.searchBox.getText();
        this.init(arg, i, j);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.search();
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.inventory != null) {
            this.client.player.playerScreenHandler.removeListener(this.listener);
        }
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.ignoreTypedCharacter) {
            return false;
        }
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(c, i)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        this.ignoreTypedCharacter = false;
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.client.options.keyChat.matchesKey(i, j)) {
                this.ignoreTypedCharacter = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            }
            return super.keyPressed(i, j, k);
        }
        boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
        boolean bl2 = InputUtil.fromKeyCode(i, j).method_30103().isPresent();
        if (bl && bl2 && this.handleHotbarKeyPressed(i, j)) {
            this.ignoreTypedCharacter = true;
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(i, j, k)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && i != 256) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(i, j, k);
    }

    private void search() {
        ((CreativeScreenHandler)this.handler).itemList.clear();
        this.searchResultTags.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            for (Item lv : Registry.ITEM) {
                lv.appendStacks(ItemGroup.SEARCH, ((CreativeScreenHandler)this.handler).itemList);
            }
        } else {
            SearchableContainer<ItemStack> lv3;
            if (string.startsWith("#")) {
                string = string.substring(1);
                SearchableContainer<ItemStack> lv2 = this.client.getSearchableContainer(SearchManager.ITEM_TAG);
                this.searchForTags(string);
            } else {
                lv3 = this.client.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }
            ((CreativeScreenHandler)this.handler).itemList.addAll(lv3.findAll(string.toLowerCase(Locale.ROOT)));
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    private void searchForTags(String string) {
        Predicate<Identifier> predicate2;
        int i = string.indexOf(58);
        if (i == -1) {
            Predicate<Identifier> predicate = arg -> arg.getPath().contains(string);
        } else {
            String string2 = string.substring(0, i).trim();
            String string3 = string.substring(i + 1).trim();
            predicate2 = arg -> arg.getNamespace().contains(string2) && arg.getPath().contains(string3);
        }
        TagContainer<Item> lv = ItemTags.getContainer();
        lv.getKeys().stream().filter(predicate2).forEach(arg2 -> this.searchResultTags.put((Identifier)arg2, lv.get((Identifier)arg2)));
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        ItemGroup lv = ItemGroup.GROUPS[selectedTab];
        if (lv.hasTooltip()) {
            RenderSystem.disableBlend();
            this.textRenderer.draw(arg, I18n.translate(lv.getTranslationKey(), new Object[0]), 8.0f, 6.0f, 0x404040);
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            double f = d - (double)this.x;
            double g = e - (double)this.y;
            for (ItemGroup lv : ItemGroup.GROUPS) {
                if (!this.isClickInTab(lv, f, g)) continue;
                return true;
            }
            if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.isClickInScrollbar(d, e)) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (i == 0) {
            double f = d - (double)this.x;
            double g = e - (double)this.y;
            this.scrolling = false;
            for (ItemGroup lv : ItemGroup.GROUPS) {
                if (!this.isClickInTab(lv, f, g)) continue;
                this.setSelectedTab(lv);
                return true;
            }
        }
        return super.mouseReleased(d, e, i);
    }

    private boolean hasScrollbar() {
        return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && ((CreativeScreenHandler)this.handler).shouldShowScrollbar();
    }

    private void setSelectedTab(ItemGroup arg) {
        int i = selectedTab;
        selectedTab = arg.getIndex();
        this.cursorDragSlots.clear();
        ((CreativeScreenHandler)this.handler).itemList.clear();
        if (arg == ItemGroup.HOTBAR) {
            HotbarStorage lv = this.client.getCreativeHotbarStorage();
            for (int j = 0; j < 9; ++j) {
                HotbarStorageEntry lv2 = lv.getSavedHotbar(j);
                if (lv2.isEmpty()) {
                    for (int k = 0; k < 9; ++k) {
                        if (k == j) {
                            ItemStack lv3 = new ItemStack(Items.PAPER);
                            lv3.getOrCreateSubTag("CustomCreativeLock");
                            Text lv4 = this.client.options.keysHotbar[j].getBoundKeyLocalizedText();
                            Text lv5 = this.client.options.keySaveToolbarActivator.getBoundKeyLocalizedText();
                            lv3.setCustomName(new TranslatableText("inventory.hotbarInfo", lv5, lv4));
                            ((CreativeScreenHandler)this.handler).itemList.add(lv3);
                            continue;
                        }
                        ((CreativeScreenHandler)this.handler).itemList.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((CreativeScreenHandler)this.handler).itemList.addAll((Collection<ItemStack>)((Object)lv2));
            }
        } else if (arg != ItemGroup.SEARCH) {
            arg.appendStacks(((CreativeScreenHandler)this.handler).itemList);
        }
        if (arg == ItemGroup.INVENTORY) {
            PlayerScreenHandler lv6 = this.client.player.playerScreenHandler;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf((Collection)((CreativeScreenHandler)this.handler).slots);
            }
            ((CreativeScreenHandler)this.handler).slots.clear();
            for (int l = 0; l < lv6.slots.size(); ++l) {
                int aa;
                int y;
                if (l >= 5 && l < 9) {
                    int m = l - 5;
                    int n = m / 2;
                    int o = m % 2;
                    int p = 54 + n * 54;
                    int q = 6 + o * 27;
                } else if (l >= 0 && l < 5) {
                    int r = -2000;
                    int s = -2000;
                } else if (l == 45) {
                    int t = 35;
                    int u = 20;
                } else {
                    int v = l - 9;
                    int w = v % 9;
                    int x = v / 9;
                    y = 9 + w * 18;
                    if (l >= 36) {
                        int z = 112;
                    } else {
                        aa = 54 + x * 18;
                    }
                }
                CreativeSlot lv7 = new CreativeSlot(lv6.slots.get(l), l, y, aa);
                ((CreativeScreenHandler)this.handler).slots.add(lv7);
            }
            this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
            ((CreativeScreenHandler)this.handler).slots.add(this.deleteItemSlot);
        } else if (i == ItemGroup.INVENTORY.getIndex()) {
            ((CreativeScreenHandler)this.handler).slots.clear();
            ((CreativeScreenHandler)this.handler).slots.addAll(this.slots);
            this.slots = null;
        }
        if (this.searchBox != null) {
            if (arg == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setFocusUnlocked(false);
                this.searchBox.setSelected(true);
                if (i != arg.getIndex()) {
                    this.searchBox.setText("");
                }
                this.search();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.setFocusUnlocked(true);
                this.searchBox.setSelected(false);
                this.searchBox.setText("");
            }
        }
        this.scrollPosition = 0.0f;
        ((CreativeScreenHandler)this.handler).scrollItems(0.0f);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (!this.hasScrollbar()) {
            return false;
        }
        int i = (((CreativeScreenHandler)this.handler).itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition = (float)((double)this.scrollPosition - f / (double)i);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], d, e);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double d, double e) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return d >= (double)k && e >= (double)l && d < (double)m && e < (double)n;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 112;
            this.scrollPosition = ((float)e - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        super.render(arg, i, j, f);
        for (ItemGroup lv : ItemGroup.GROUPS) {
            if (this.renderTabTooltipIfHovered(arg, lv, i, j)) break;
        }
        if (this.deleteItemSlot != null && selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, i, j)) {
            this.renderTooltip(arg, new TranslatableText("inventory.binSlot"), i, j);
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawMouseoverTooltip(arg, i, j);
    }

    @Override
    protected void renderTooltip(MatrixStack arg, ItemStack arg22, int i, int j) {
        if (selectedTab == ItemGroup.SEARCH.getIndex()) {
            Map<Enchantment, Integer> map;
            List<Text> list = arg22.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            ArrayList list2 = Lists.newArrayList(list);
            Item lv = arg22.getItem();
            ItemGroup lv2 = lv.getGroup();
            if (lv2 == null && lv == Items.ENCHANTED_BOOK && (map = EnchantmentHelper.get(arg22)).size() == 1) {
                Enchantment lv3 = map.keySet().iterator().next();
                for (ItemGroup lv4 : ItemGroup.GROUPS) {
                    if (!lv4.containsEnchantments(lv3.type)) continue;
                    lv2 = lv4;
                    break;
                }
            }
            this.searchResultTags.forEach((arg2, arg3) -> {
                if (arg3.contains(lv)) {
                    list2.add(1, new LiteralText("#" + arg2).formatted(Formatting.DARK_PURPLE));
                }
            });
            if (lv2 != null) {
                list2.add(1, new TranslatableText(lv2.getTranslationKey()).formatted(Formatting.BLUE));
            }
            this.renderTooltip(arg, list2, i, j);
        } else {
            super.renderTooltip(arg, arg22, i, j);
        }
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        ItemGroup lv = ItemGroup.GROUPS[selectedTab];
        for (ItemGroup lv2 : ItemGroup.GROUPS) {
            this.client.getTextureManager().bindTexture(TEXTURE);
            if (lv2.getIndex() == selectedTab) continue;
            this.renderTabIcon(arg, lv2);
        }
        this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + lv.getTexture()));
        this.drawTexture(arg, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchBox.render(arg, i, j, f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = this.x + 175;
        int l = this.y + 18;
        int m = l + 112;
        this.client.getTextureManager().bindTexture(TEXTURE);
        if (lv.hasScrollbar()) {
            this.drawTexture(arg, k, l + (int)((float)(m - l - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabIcon(arg, lv);
        if (lv == ItemGroup.INVENTORY) {
            InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, this.x + 88 - i, this.y + 45 - 30 - j, this.client.player);
        }
    }

    protected boolean isClickInTab(ItemGroup arg, double d, double e) {
        int i = arg.getColumn();
        int j = 28 * i;
        int k = 0;
        if (arg.isSpecial()) {
            j = this.backgroundWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }
        k = arg.isTopRow() ? (k -= 32) : (k += this.backgroundHeight);
        return d >= (double)j && d <= (double)(j + 28) && e >= (double)k && e <= (double)(k + 32);
    }

    protected boolean renderTabTooltipIfHovered(MatrixStack arg, ItemGroup arg2, int i, int j) {
        int k = arg2.getColumn();
        int l = 28 * k;
        int m = 0;
        if (arg2.isSpecial()) {
            l = this.backgroundWidth - 28 * (6 - k) + 2;
        } else if (k > 0) {
            l += k;
        }
        m = arg2.isTopRow() ? (m -= 32) : (m += this.backgroundHeight);
        if (this.isPointWithinBounds(l + 3, m + 3, 23, 27, i, j)) {
            this.renderTooltip(arg, new TranslatableText(arg2.getTranslationKey()), i, j);
            return true;
        }
        return false;
    }

    protected void renderTabIcon(MatrixStack arg, ItemGroup arg2) {
        boolean bl = arg2.getIndex() == selectedTab;
        boolean bl2 = arg2.isTopRow();
        int i = arg2.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.x + 28 * i;
        int m = this.y;
        int n = 32;
        if (bl) {
            k += 32;
        }
        if (arg2.isSpecial()) {
            l = this.x + this.backgroundWidth - 28 * (6 - i);
        } else if (i > 0) {
            l += i;
        }
        if (bl2) {
            m -= 28;
        } else {
            k += 64;
            m += this.backgroundHeight - 4;
        }
        this.drawTexture(arg, l, m, j, k, 28, 32);
        this.itemRenderer.zOffset = 100.0f;
        int n2 = bl2 ? 1 : -1;
        RenderSystem.enableRescaleNormal();
        ItemStack lv = arg2.getIcon();
        this.itemRenderer.renderInGuiWithOverrides(lv, l += 6, m += 8 + n2);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, lv, l, m);
        this.itemRenderer.zOffset = 0.0f;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public static void onHotbarKeyPress(MinecraftClient arg, int i, boolean bl, boolean bl2) {
        ClientPlayerEntity lv = arg.player;
        HotbarStorage lv2 = arg.getCreativeHotbarStorage();
        HotbarStorageEntry lv3 = lv2.getSavedHotbar(i);
        if (bl) {
            for (int j = 0; j < PlayerInventory.getHotbarSize(); ++j) {
                ItemStack lv4 = ((ItemStack)lv3.get(j)).copy();
                lv.inventory.setStack(j, lv4);
                arg.interactionManager.clickCreativeStack(lv4, 36 + j);
            }
            lv.playerScreenHandler.sendContentUpdates();
        } else if (bl2) {
            for (int k = 0; k < PlayerInventory.getHotbarSize(); ++k) {
                lv3.set(k, lv.inventory.getStack(k).copy());
            }
            Text lv5 = arg.options.keysHotbar[i].getBoundKeyLocalizedText();
            Text lv6 = arg.options.keyLoadToolbarActivator.getBoundKeyLocalizedText();
            arg.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", lv6, lv5), false);
            lv2.save();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LockableSlot
    extends Slot {
        public LockableSlot(Inventory arg, int i, int j, int k) {
            super(arg, i, j, k);
        }

        @Override
        public boolean canTakeItems(PlayerEntity arg) {
            if (super.canTakeItems(arg) && this.hasStack()) {
                return this.getStack().getSubTag("CustomCreativeLock") == null;
            }
            return !this.hasStack();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CreativeSlot
    extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot arg, int i, int j, int k) {
            super(arg.inventory, i, j, k);
            this.slot = arg;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity arg, ItemStack arg2) {
            return this.slot.onTakeItem(arg, arg2);
        }

        @Override
        public boolean canInsert(ItemStack arg) {
            return this.slot.canInsert(arg);
        }

        @Override
        public ItemStack getStack() {
            return this.slot.getStack();
        }

        @Override
        public boolean hasStack() {
            return this.slot.hasStack();
        }

        @Override
        public void setStack(ItemStack arg) {
            this.slot.setStack(arg);
        }

        @Override
        public void markDirty() {
            this.slot.markDirty();
        }

        @Override
        public int getMaxStackAmount() {
            return this.slot.getMaxStackAmount();
        }

        @Override
        public int getMaxStackAmount(ItemStack arg) {
            return this.slot.getMaxStackAmount(arg);
        }

        @Override
        @Nullable
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        @Override
        public ItemStack takeStack(int i) {
            return this.slot.takeStack(i);
        }

        @Override
        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        @Override
        public boolean canTakeItems(PlayerEntity arg) {
            return this.slot.canTakeItems(arg);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CreativeScreenHandler
    extends ScreenHandler {
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();

        public CreativeScreenHandler(PlayerEntity arg) {
            super(null, 0);
            PlayerInventory lv = arg.inventory;
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new LockableSlot(inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(lv, k, 9 + k * 18, 112));
            }
            this.scrollItems(0.0f);
        }

        @Override
        public boolean canUse(PlayerEntity arg) {
            return true;
        }

        public void scrollItems(float f) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(f * (float)i) + 0.5);
            if (j < 0) {
                j = 0;
            }
            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    if (m >= 0 && m < this.itemList.size()) {
                        inventory.setStack(l + k * 9, this.itemList.get(m));
                        continue;
                    }
                    inventory.setStack(l + k * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean shouldShowScrollbar() {
            return this.itemList.size() > 45;
        }

        @Override
        public ItemStack transferSlot(PlayerEntity arg, int i) {
            Slot lv;
            if (i >= this.slots.size() - 9 && i < this.slots.size() && (lv = (Slot)this.slots.get(i)) != null && lv.hasStack()) {
                lv.setStack(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
            return arg2.inventory != inventory;
        }

        @Override
        public boolean canInsertIntoSlot(Slot arg) {
            return arg.inventory != inventory;
        }
    }
}

