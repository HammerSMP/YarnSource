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
import net.minecraft.tag.TagGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
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

    public CreativeInventoryScreen(PlayerEntity player) {
        super(new CreativeScreenHandler(player), player.inventory, LiteralText.EMPTY);
        player.currentScreenHandler = this.handler;
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
    protected void onMouseClick(@Nullable Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (this.isCreativeInventorySlot(slot)) {
            this.searchBox.setCursorToEnd();
            this.searchBox.setSelectionEnd(0);
        }
        boolean bl = actionType == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType = actionType = invSlot == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
        if (slot != null || selectedTab == ItemGroup.INVENTORY.getIndex() || actionType == SlotActionType.QUICK_CRAFT) {
            if (slot != null && !slot.canTakeItems(this.client.player)) {
                return;
            }
            if (slot == this.deleteItemSlot && bl) {
                for (int k = 0; k < this.client.player.playerScreenHandler.getStacks().size(); ++k) {
                    this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, k);
                }
            } else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
                if (slot == this.deleteItemSlot) {
                    this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else if (actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
                    ItemStack lv = slot.takeStack(clickData == 0 ? 1 : slot.getStack().getMaxCount());
                    ItemStack lv2 = slot.getStack();
                    this.client.player.dropItem(lv, true);
                    this.client.interactionManager.dropCreativeStack(lv);
                    this.client.interactionManager.clickCreativeStack(lv2, ((CreativeSlot)((CreativeSlot)slot)).slot.id);
                } else if (actionType == SlotActionType.THROW && !this.client.player.inventory.getCursorStack().isEmpty()) {
                    this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(this.client.player.inventory.getCursorStack());
                    this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else {
                    this.client.player.playerScreenHandler.onSlotClick(slot == null ? invSlot : ((CreativeSlot)((CreativeSlot)slot)).slot.id, clickData, actionType, this.client.player);
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            } else if (actionType != SlotActionType.QUICK_CRAFT && slot.inventory == inventory) {
                PlayerInventory lv3 = this.client.player.inventory;
                ItemStack lv4 = lv3.getCursorStack();
                ItemStack lv5 = slot.getStack();
                if (actionType == SlotActionType.SWAP) {
                    if (!lv5.isEmpty()) {
                        ItemStack lv6 = lv5.copy();
                        lv6.setCount(lv6.getMaxCount());
                        this.client.player.inventory.setStack(clickData, lv6);
                        this.client.player.playerScreenHandler.sendContentUpdates();
                    }
                    return;
                }
                if (actionType == SlotActionType.CLONE) {
                    if (lv3.getCursorStack().isEmpty() && slot.hasStack()) {
                        ItemStack lv7 = slot.getStack().copy();
                        lv7.setCount(lv7.getMaxCount());
                        lv3.setCursorStack(lv7);
                    }
                    return;
                }
                if (actionType == SlotActionType.THROW) {
                    if (!lv5.isEmpty()) {
                        ItemStack lv8 = lv5.copy();
                        lv8.setCount(clickData == 0 ? 1 : lv8.getMaxCount());
                        this.client.player.dropItem(lv8, true);
                        this.client.interactionManager.dropCreativeStack(lv8);
                    }
                    return;
                }
                if (!lv4.isEmpty() && !lv5.isEmpty() && lv4.isItemEqualIgnoreDamage(lv5) && ItemStack.areTagsEqual(lv4, lv5)) {
                    if (clickData == 0) {
                        if (bl) {
                            lv4.setCount(lv4.getMaxCount());
                        } else if (lv4.getCount() < lv4.getMaxCount()) {
                            lv4.increment(1);
                        }
                    } else {
                        lv4.decrement(1);
                    }
                } else if (lv5.isEmpty() || !lv4.isEmpty()) {
                    if (clickData == 0) {
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
                ItemStack lv9 = slot == null ? ItemStack.EMPTY : ((CreativeScreenHandler)this.handler).getSlot(slot.id).getStack();
                ((CreativeScreenHandler)this.handler).onSlotClick(slot == null ? invSlot : slot.id, clickData, actionType, this.client.player);
                if (ScreenHandler.unpackQuickCraftStage(clickData) == 2) {
                    for (int l = 0; l < 9; ++l) {
                        this.client.interactionManager.clickCreativeStack(((CreativeScreenHandler)this.handler).getSlot(45 + l).getStack(), 36 + l);
                    }
                } else if (slot != null) {
                    ItemStack lv10 = ((CreativeScreenHandler)this.handler).getSlot(slot.id).getStack();
                    this.client.interactionManager.clickCreativeStack(lv10, slot.id - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    int m = 45 + clickData;
                    if (actionType == SlotActionType.SWAP) {
                        this.client.interactionManager.clickCreativeStack(lv9, m - ((CreativeScreenHandler)this.handler).slots.size() + 9 + 36);
                    } else if (actionType == SlotActionType.THROW && !lv9.isEmpty()) {
                        ItemStack lv11 = lv9.copy();
                        lv11.setCount(clickData == 0 ? 1 : lv11.getMaxCount());
                        this.client.player.dropItem(lv11, true);
                        this.client.interactionManager.dropCreativeStack(lv11);
                    }
                    this.client.player.playerScreenHandler.sendContentUpdates();
                }
            }
        } else {
            PlayerInventory lv12 = this.client.player.inventory;
            if (!lv12.getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
                if (clickData == 0) {
                    this.client.player.dropItem(lv12.getCursorStack(), true);
                    this.client.interactionManager.dropCreativeStack(lv12.getCursorStack());
                    lv12.setCursorStack(ItemStack.EMPTY);
                }
                if (clickData == 1) {
                    ItemStack lv13 = lv12.getCursorStack().split(1);
                    this.client.player.dropItem(lv13, true);
                    this.client.interactionManager.dropCreativeStack(lv13);
                }
            }
        }
    }

    private boolean isCreativeInventorySlot(@Nullable Slot slot) {
        return slot != null && slot.inventory == inventory;
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
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
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
    public boolean charTyped(char chr, int keyCode) {
        if (this.ignoreTypedCharacter) {
            return false;
        }
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(chr, keyCode)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.client.options.keyChat.matchesKey(keyCode, scanCode)) {
                this.ignoreTypedCharacter = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        boolean bl = !this.isCreativeInventorySlot(this.focusedSlot) || this.focusedSlot.hasStack();
        boolean bl2 = InputUtil.fromKeyCode(keyCode, scanCode).method_30103().isPresent();
        if (bl && bl2 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
            this.ignoreTypedCharacter = true;
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.search();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
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
                string = Language.getInstance().reorder(string, false);
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
        TagGroup<Item> lv = ItemTags.getTagGroup();
        lv.getTagIds().stream().filter(predicate2).forEach(arg2 -> this.searchResultTags.put((Identifier)arg2, lv.getTag((Identifier)arg2)));
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        ItemGroup lv = ItemGroup.GROUPS[selectedTab];
        if (lv.hasTooltip()) {
            RenderSystem.disableBlend();
            this.textRenderer.draw(matrices, I18n.translate(lv.getTranslationKey(), new Object[0]), 8.0f, 6.0f, 0x404040);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double f = mouseX - (double)this.x;
            double g = mouseY - (double)this.y;
            for (ItemGroup lv : ItemGroup.GROUPS) {
                if (!this.isClickInTab(lv, f, g)) continue;
                return true;
            }
            if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.isClickInScrollbar(mouseX, mouseY)) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double f = mouseX - (double)this.x;
            double g = mouseY - (double)this.y;
            this.scrolling = false;
            for (ItemGroup lv : ItemGroup.GROUPS) {
                if (!this.isClickInTab(lv, f, g)) continue;
                this.setSelectedTab(lv);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean hasScrollbar() {
        return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && ((CreativeScreenHandler)this.handler).shouldShowScrollbar();
    }

    private void setSelectedTab(ItemGroup group) {
        int i = selectedTab;
        selectedTab = group.getIndex();
        this.cursorDragSlots.clear();
        ((CreativeScreenHandler)this.handler).itemList.clear();
        if (group == ItemGroup.HOTBAR) {
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
        } else if (group != ItemGroup.SEARCH) {
            group.appendStacks(((CreativeScreenHandler)this.handler).itemList);
        }
        if (group == ItemGroup.INVENTORY) {
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
            if (group == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setFocusUnlocked(false);
                this.searchBox.setSelected(true);
                if (i != group.getIndex()) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        }
        int i = (((CreativeScreenHandler)this.handler).itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
        ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], mouseX, mouseY);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 112;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            ((CreativeScreenHandler)this.handler).scrollItems(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        for (ItemGroup lv : ItemGroup.GROUPS) {
            if (this.renderTabTooltipIfHovered(matrices, lv, mouseX, mouseY)) break;
        }
        if (this.deleteItemSlot != null && selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(matrices, new TranslatableText("inventory.binSlot"), mouseX, mouseY);
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        if (selectedTab == ItemGroup.SEARCH.getIndex()) {
            Map<Enchantment, Integer> map;
            List<Text> list = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            ArrayList list2 = Lists.newArrayList(list);
            Item lv = stack.getItem();
            ItemGroup lv2 = lv.getGroup();
            if (lv2 == null && lv == Items.ENCHANTED_BOOK && (map = EnchantmentHelper.get(stack)).size() == 1) {
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
            this.renderTooltip(matrices, list2, x, y);
        } else {
            super.renderTooltip(matrices, stack, x, y);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        ItemGroup lv = ItemGroup.GROUPS[selectedTab];
        for (ItemGroup lv2 : ItemGroup.GROUPS) {
            this.client.getTextureManager().bindTexture(TEXTURE);
            if (lv2.getIndex() == selectedTab) continue;
            this.renderTabIcon(matrices, lv2);
        }
        this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + lv.getTexture()));
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = this.x + 175;
        int l = this.y + 18;
        int m = l + 112;
        this.client.getTextureManager().bindTexture(TEXTURE);
        if (lv.hasScrollbar()) {
            this.drawTexture(matrices, k, l + (int)((float)(m - l - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabIcon(matrices, lv);
        if (lv == ItemGroup.INVENTORY) {
            InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, this.x + 88 - mouseX, this.y + 45 - 30 - mouseY, this.client.player);
        }
    }

    protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
        int i = group.getColumn();
        int j = 28 * i;
        int k = 0;
        if (group.isSpecial()) {
            j = this.backgroundWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }
        k = group.isTopRow() ? (k -= 32) : (k += this.backgroundHeight);
        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
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

    public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
        ClientPlayerEntity lv = client.player;
        HotbarStorage lv2 = client.getCreativeHotbarStorage();
        HotbarStorageEntry lv3 = lv2.getSavedHotbar(index);
        if (restore) {
            for (int j = 0; j < PlayerInventory.getHotbarSize(); ++j) {
                ItemStack lv4 = ((ItemStack)lv3.get(j)).copy();
                lv.inventory.setStack(j, lv4);
                client.interactionManager.clickCreativeStack(lv4, 36 + j);
            }
            lv.playerScreenHandler.sendContentUpdates();
        } else if (save) {
            for (int k = 0; k < PlayerInventory.getHotbarSize(); ++k) {
                lv3.set(k, lv.inventory.getStack(k).copy());
            }
            Text lv5 = client.options.keysHotbar[index].getBoundKeyLocalizedText();
            Text lv6 = client.options.keyLoadToolbarActivator.getBoundKeyLocalizedText();
            client.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", lv6, lv5), false);
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
        public boolean canTakeItems(PlayerEntity playerEntity) {
            if (super.canTakeItems(playerEntity) && this.hasStack()) {
                return this.getStack().getSubTag("CustomCreativeLock") == null;
            }
            return !this.hasStack();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CreativeSlot
    extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot slot, int invSlot, int x, int y) {
            super(slot.inventory, invSlot, x, y);
            this.slot = slot;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            return this.slot.onTakeItem(player, stack);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.slot.canInsert(stack);
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
        public void setStack(ItemStack stack) {
            this.slot.setStack(stack);
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
        public int getMaxStackAmount(ItemStack stack) {
            return this.slot.getMaxStackAmount(stack);
        }

        @Override
        @Nullable
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.slot.takeStack(amount);
        }

        @Override
        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
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
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        public void scrollItems(float position) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(position * (float)i) + 0.5);
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
        public ItemStack transferSlot(PlayerEntity player, int index) {
            Slot lv;
            if (index >= this.slots.size() - 9 && index < this.slots.size() && (lv = (Slot)this.slots.get(index)) != null && lv.hasStack()) {
                lv.setStack(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
            return slot.inventory != inventory;
        }

        @Override
        public boolean canInsertIntoSlot(Slot slot) {
            return slot.inventory != inventory;
        }
    }
}

