/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class HandledScreen<T extends ScreenHandler>
extends Screen
implements ScreenHandlerProvider<T> {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;
    protected int titleX;
    protected int titleY;
    protected int playerInventoryTitleX;
    protected int playerInventoryTitleY;
    protected final T handler;
    protected final PlayerInventory playerInventory;
    protected int x;
    protected int y;
    protected Slot focusedSlot;
    private Slot touchDragSlotStart;
    private boolean touchIsRightClickDrag;
    private ItemStack touchDragStack = ItemStack.EMPTY;
    private int touchDropX;
    private int touchDropY;
    private Slot touchDropOriginSlot;
    private long touchDropTime;
    private ItemStack touchDropReturningStack = ItemStack.EMPTY;
    private Slot touchHoveredSlot;
    private long touchDropTimer;
    protected final Set<Slot> cursorDragSlots = Sets.newHashSet();
    protected boolean isCursorDragging;
    private int heldButtonType;
    private int heldButtonCode;
    private boolean cancelNextRelease;
    private int draggedStackRemainder;
    private long lastButtonClickTime;
    private Slot lastClickedSlot;
    private int lastClickedButton;
    private boolean isDoubleClicking;
    private ItemStack quickMovingStack = ItemStack.EMPTY;

    public HandledScreen(T arg, PlayerInventory arg2, Text arg3) {
        super(arg3);
        this.handler = arg;
        this.playerInventory = arg2;
        this.cancelNextRelease = true;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        ItemStack lv3;
        int k = this.x;
        int l = this.y;
        this.drawBackground(arg, f, i, j);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        super.render(arg, i, j, f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(k, l, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableRescaleNormal();
        this.focusedSlot = null;
        int m = 240;
        int n = 240;
        RenderSystem.glMultiTexCoord2f(33986, 240.0f, 240.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int o = 0; o < ((ScreenHandler)this.handler).slots.size(); ++o) {
            Slot lv = ((ScreenHandler)this.handler).slots.get(o);
            if (lv.doDrawHoveringEffect()) {
                this.drawSlot(arg, lv);
            }
            if (!this.isPointOverSlot(lv, i, j) || !lv.doDrawHoveringEffect()) continue;
            this.focusedSlot = lv;
            RenderSystem.disableDepthTest();
            int p = lv.x;
            int q = lv.y;
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(arg, p, q, p + 16, q + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
        this.drawForeground(arg, i, j);
        PlayerInventory lv2 = this.client.player.inventory;
        ItemStack itemStack = lv3 = this.touchDragStack.isEmpty() ? lv2.getCursorStack() : this.touchDragStack;
        if (!lv3.isEmpty()) {
            int r = 8;
            int s = this.touchDragStack.isEmpty() ? 8 : 16;
            String string = null;
            if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
                lv3 = lv3.copy();
                lv3.setCount(MathHelper.ceil((float)lv3.getCount() / 2.0f));
            } else if (this.isCursorDragging && this.cursorDragSlots.size() > 1) {
                lv3 = lv3.copy();
                lv3.setCount(this.draggedStackRemainder);
                if (lv3.isEmpty()) {
                    string = "" + (Object)((Object)Formatting.YELLOW) + "0";
                }
            }
            this.drawItem(lv3, i - k - 8, j - l - s, string);
        }
        if (!this.touchDropReturningStack.isEmpty()) {
            float g = (float)(Util.getMeasuringTimeMs() - this.touchDropTime) / 100.0f;
            if (g >= 1.0f) {
                g = 1.0f;
                this.touchDropReturningStack = ItemStack.EMPTY;
            }
            int t = this.touchDropOriginSlot.x - this.touchDropX;
            int u = this.touchDropOriginSlot.y - this.touchDropY;
            int v = this.touchDropX + (int)((float)t * g);
            int w = this.touchDropY + (int)((float)u * g);
            this.drawItem(this.touchDropReturningStack, v, w, null);
        }
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    protected void drawMouseoverTooltip(MatrixStack arg, int i, int j) {
        if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            this.renderTooltip(arg, this.focusedSlot.getStack(), i, j);
        }
    }

    private void drawItem(ItemStack arg, int i, int j, String string) {
        RenderSystem.translatef(0.0f, 0.0f, 32.0f);
        this.setZOffset(200);
        this.itemRenderer.zOffset = 200.0f;
        this.itemRenderer.renderInGuiWithOverrides(arg, i, j);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, arg, i, j - (this.touchDragStack.isEmpty() ? 0 : 8), string);
        this.setZOffset(0);
        this.itemRenderer.zOffset = 0.0f;
    }

    protected void drawForeground(MatrixStack arg, int i, int j) {
        this.textRenderer.draw(arg, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
        this.textRenderer.draw(arg, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
    }

    protected abstract void drawBackground(MatrixStack var1, float var2, int var3, int var4);

    private void drawSlot(MatrixStack arg, Slot arg2) {
        Pair<Identifier, Identifier> pair;
        int i = arg2.x;
        int j = arg2.y;
        ItemStack lv = arg2.getStack();
        boolean bl = false;
        boolean bl2 = arg2 == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack lv2 = this.client.player.inventory.getCursorStack();
        String string = null;
        if (arg2 == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !lv.isEmpty()) {
            lv = lv.copy();
            lv.setCount(lv.getCount() / 2);
        } else if (this.isCursorDragging && this.cursorDragSlots.contains(arg2) && !lv2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot(arg2, lv2, true) && ((ScreenHandler)this.handler).canInsertIntoSlot(arg2)) {
                lv = lv2.copy();
                bl = true;
                ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, lv, arg2.getStack().isEmpty() ? 0 : arg2.getStack().getCount());
                int k = Math.min(lv.getMaxCount(), arg2.getMaxStackAmount(lv));
                if (lv.getCount() > k) {
                    string = Formatting.YELLOW.toString() + k;
                    lv.setCount(k);
                }
            } else {
                this.cursorDragSlots.remove(arg2);
                this.calculateOffset();
            }
        }
        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0f;
        if (lv.isEmpty() && arg2.doDrawHoveringEffect() && (pair = arg2.getBackgroundSprite()) != null) {
            Sprite lv3 = this.client.getSpriteAtlas((Identifier)pair.getFirst()).apply((Identifier)pair.getSecond());
            this.client.getTextureManager().bindTexture(lv3.getAtlas().getId());
            HandledScreen.drawSprite(arg, i, j, this.getZOffset(), 16, 16, lv3);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                HandledScreen.fill(arg, i, j, i + 16, j + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderInGuiWithOverrides(this.client.player, lv, i, j);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, lv, i, j, string);
        }
        this.itemRenderer.zOffset = 0.0f;
        this.setZOffset(0);
    }

    private void calculateOffset() {
        ItemStack lv = this.client.player.inventory.getCursorStack();
        if (lv.isEmpty() || !this.isCursorDragging) {
            return;
        }
        if (this.heldButtonType == 2) {
            this.draggedStackRemainder = lv.getMaxCount();
            return;
        }
        this.draggedStackRemainder = lv.getCount();
        for (Slot lv2 : this.cursorDragSlots) {
            ItemStack lv3 = lv.copy();
            ItemStack lv4 = lv2.getStack();
            int i = lv4.isEmpty() ? 0 : lv4.getCount();
            ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, lv3, i);
            int j = Math.min(lv3.getMaxCount(), lv2.getMaxStackAmount(lv3));
            if (lv3.getCount() > j) {
                lv3.setCount(j);
            }
            this.draggedStackRemainder -= lv3.getCount() - i;
        }
    }

    private Slot getSlotAt(double d, double e) {
        for (int i = 0; i < ((ScreenHandler)this.handler).slots.size(); ++i) {
            Slot lv = ((ScreenHandler)this.handler).slots.get(i);
            if (!this.isPointOverSlot(lv, d, e) || !lv.doDrawHoveringEffect()) continue;
            return lv;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (super.mouseClicked(d, e, i)) {
            return true;
        }
        boolean bl = this.client.options.keyPickItem.matchesMouse(i);
        Slot lv = this.getSlotAt(d, e);
        long l = Util.getMeasuringTimeMs();
        this.isDoubleClicking = this.lastClickedSlot == lv && l - this.lastButtonClickTime < 250L && this.lastClickedButton == i;
        this.cancelNextRelease = false;
        if (i == 0 || i == 1 || bl) {
            int j = this.x;
            int k = this.y;
            boolean bl2 = this.isClickOutsideBounds(d, e, j, k, i);
            int m = -1;
            if (lv != null) {
                m = lv.id;
            }
            if (bl2) {
                m = -999;
            }
            if (this.client.options.touchscreen && bl2 && this.client.player.inventory.getCursorStack().isEmpty()) {
                this.client.openScreen(null);
                return true;
            }
            if (m != -1) {
                if (this.client.options.touchscreen) {
                    if (lv != null && lv.hasStack()) {
                        this.touchDragSlotStart = lv;
                        this.touchDragStack = ItemStack.EMPTY;
                        this.touchIsRightClickDrag = i == 1;
                    } else {
                        this.touchDragSlotStart = null;
                    }
                } else if (!this.isCursorDragging) {
                    if (this.client.player.inventory.getCursorStack().isEmpty()) {
                        if (this.client.options.keyPickItem.matchesMouse(i)) {
                            this.onMouseClick(lv, m, i, SlotActionType.CLONE);
                        } else {
                            boolean bl3 = m != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
                            SlotActionType lv2 = SlotActionType.PICKUP;
                            if (bl3) {
                                this.quickMovingStack = lv != null && lv.hasStack() ? lv.getStack().copy() : ItemStack.EMPTY;
                                lv2 = SlotActionType.QUICK_MOVE;
                            } else if (m == -999) {
                                lv2 = SlotActionType.THROW;
                            }
                            this.onMouseClick(lv, m, i, lv2);
                        }
                        this.cancelNextRelease = true;
                    } else {
                        this.isCursorDragging = true;
                        this.heldButtonCode = i;
                        this.cursorDragSlots.clear();
                        if (i == 0) {
                            this.heldButtonType = 0;
                        } else if (i == 1) {
                            this.heldButtonType = 1;
                        } else if (this.client.options.keyPickItem.matchesMouse(i)) {
                            this.heldButtonType = 2;
                        }
                    }
                }
            }
        }
        this.lastClickedSlot = lv;
        this.lastButtonClickTime = l;
        this.lastClickedButton = i;
        return true;
    }

    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        return d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        Slot lv = this.getSlotAt(d, e);
        ItemStack lv2 = this.client.player.inventory.getCursorStack();
        if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
            if (i == 0 || i == 1) {
                if (this.touchDragStack.isEmpty()) {
                    if (lv != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
                        this.touchDragStack = this.touchDragSlotStart.getStack().copy();
                    }
                } else if (this.touchDragStack.getCount() > 1 && lv != null && ScreenHandler.canInsertItemIntoSlot(lv, this.touchDragStack, false)) {
                    long l = Util.getMeasuringTimeMs();
                    if (this.touchHoveredSlot == lv) {
                        if (l - this.touchDropTimer > 500L) {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.onMouseClick(lv, lv.id, 1, SlotActionType.PICKUP);
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.touchDropTimer = l + 750L;
                            this.touchDragStack.decrement(1);
                        }
                    } else {
                        this.touchHoveredSlot = lv;
                        this.touchDropTimer = l;
                    }
                }
            }
        } else if (this.isCursorDragging && lv != null && !lv2.isEmpty() && (lv2.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && ScreenHandler.canInsertItemIntoSlot(lv, lv2, true) && lv.canInsert(lv2) && ((ScreenHandler)this.handler).canInsertIntoSlot(lv)) {
            this.cursorDragSlots.add(lv);
            this.calculateOffset();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        Slot lv = this.getSlotAt(d, e);
        int j = this.x;
        int k = this.y;
        boolean bl = this.isClickOutsideBounds(d, e, j, k, i);
        int l = -1;
        if (lv != null) {
            l = lv.id;
        }
        if (bl) {
            l = -999;
        }
        if (this.isDoubleClicking && lv != null && i == 0 && ((ScreenHandler)this.handler).canInsertIntoSlot(ItemStack.EMPTY, lv)) {
            if (HandledScreen.hasShiftDown()) {
                if (!this.quickMovingStack.isEmpty()) {
                    for (Slot lv2 : ((ScreenHandler)this.handler).slots) {
                        if (lv2 == null || !lv2.canTakeItems(this.client.player) || !lv2.hasStack() || lv2.inventory != lv.inventory || !ScreenHandler.canInsertItemIntoSlot(lv2, this.quickMovingStack, true)) continue;
                        this.onMouseClick(lv2, lv2.id, i, SlotActionType.QUICK_MOVE);
                    }
                }
            } else {
                this.onMouseClick(lv, l, i, SlotActionType.PICKUP_ALL);
            }
            this.isDoubleClicking = false;
            this.lastButtonClickTime = 0L;
        } else {
            if (this.isCursorDragging && this.heldButtonCode != i) {
                this.isCursorDragging = false;
                this.cursorDragSlots.clear();
                this.cancelNextRelease = true;
                return true;
            }
            if (this.cancelNextRelease) {
                this.cancelNextRelease = false;
                return true;
            }
            if (this.touchDragSlotStart != null && this.client.options.touchscreen) {
                if (i == 0 || i == 1) {
                    if (this.touchDragStack.isEmpty() && lv != this.touchDragSlotStart) {
                        this.touchDragStack = this.touchDragSlotStart.getStack();
                    }
                    boolean bl2 = ScreenHandler.canInsertItemIntoSlot(lv, this.touchDragStack, false);
                    if (l != -1 && !this.touchDragStack.isEmpty() && bl2) {
                        this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, i, SlotActionType.PICKUP);
                        this.onMouseClick(lv, l, 0, SlotActionType.PICKUP);
                        if (this.client.player.inventory.getCursorStack().isEmpty()) {
                            this.touchDropReturningStack = ItemStack.EMPTY;
                        } else {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, i, SlotActionType.PICKUP);
                            this.touchDropX = MathHelper.floor(d - (double)j);
                            this.touchDropY = MathHelper.floor(e - (double)k);
                            this.touchDropOriginSlot = this.touchDragSlotStart;
                            this.touchDropReturningStack = this.touchDragStack;
                            this.touchDropTime = Util.getMeasuringTimeMs();
                        }
                    } else if (!this.touchDragStack.isEmpty()) {
                        this.touchDropX = MathHelper.floor(d - (double)j);
                        this.touchDropY = MathHelper.floor(e - (double)k);
                        this.touchDropOriginSlot = this.touchDragSlotStart;
                        this.touchDropReturningStack = this.touchDragStack;
                        this.touchDropTime = Util.getMeasuringTimeMs();
                    }
                    this.touchDragStack = ItemStack.EMPTY;
                    this.touchDragSlotStart = null;
                }
            } else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(0, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                for (Slot lv3 : this.cursorDragSlots) {
                    this.onMouseClick(lv3, lv3.id, ScreenHandler.packQuickCraftData(1, this.heldButtonType), SlotActionType.QUICK_CRAFT);
                }
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData(2, this.heldButtonType), SlotActionType.QUICK_CRAFT);
            } else if (!this.client.player.inventory.getCursorStack().isEmpty()) {
                if (this.client.options.keyPickItem.matchesMouse(i)) {
                    this.onMouseClick(lv, l, i, SlotActionType.CLONE);
                } else {
                    boolean bl3;
                    boolean bl2 = bl3 = l != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
                    if (bl3) {
                        this.quickMovingStack = lv != null && lv.hasStack() ? lv.getStack().copy() : ItemStack.EMPTY;
                    }
                    this.onMouseClick(lv, l, i, bl3 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
                }
            }
        }
        if (this.client.player.inventory.getCursorStack().isEmpty()) {
            this.lastButtonClickTime = 0L;
        }
        this.isCursorDragging = false;
        return true;
    }

    private boolean isPointOverSlot(Slot arg, double d, double e) {
        return this.isPointWithinBounds(arg.x, arg.y, 16, 16, d, e);
    }

    protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
        int m = this.x;
        int n = this.y;
        return (d -= (double)m) >= (double)(i - 1) && d < (double)(i + k + 1) && (e -= (double)n) >= (double)(j - 1) && e < (double)(j + l + 1);
    }

    protected void onMouseClick(Slot arg, int i, int j, SlotActionType arg2) {
        if (arg != null) {
            i = arg.id;
        }
        this.client.interactionManager.clickSlot(((ScreenHandler)this.handler).syncId, i, j, arg2, this.client.player);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 256 || this.client.options.keyInventory.matchesKey(i, j)) {
            this.client.player.closeHandledScreen();
            return true;
        }
        this.handleHotbarKeyPressed(i, j);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.keyPickItem.matchesKey(i, j)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.keyDrop.matchesKey(i, j)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, HandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }

    protected boolean handleHotbarKeyPressed(int i, int j) {
        if (this.client.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
            if (this.client.options.keySwapHands.matchesKey(i, j)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return true;
            }
            for (int k = 0; k < 9; ++k) {
                if (!this.client.options.keysHotbar[k].matchesKey(i, j)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, k, SlotActionType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.client.player == null) {
            return;
        }
        ((ScreenHandler)this.handler).close(this.client.player);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.client.player.isAlive() || this.client.player.removed) {
            this.client.player.closeHandledScreen();
        }
    }

    @Override
    public T getScreenHandler() {
        return this.handler;
    }
}

