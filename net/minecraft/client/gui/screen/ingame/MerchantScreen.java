/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.VillagerData;

@Environment(value=EnvType.CLIENT)
public class MerchantScreen
extends HandledScreen<MerchantScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private int selectedIndex;
    private final WidgetButtonPage[] offers = new WidgetButtonPage[7];
    private int indexStartOffset;
    private boolean scrolling;

    public MerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 276;
        this.playerInventoryTitleX = 107;
    }

    private void syncRecipeIndex() {
        ((MerchantScreenHandler)this.handler).setRecipeIndex(this.selectedIndex);
        ((MerchantScreenHandler)this.handler).switchTo(this.selectedIndex);
        this.client.getNetworkHandler().sendPacket(new SelectVillagerTradeC2SPacket(this.selectedIndex));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 16 + 2;
        for (int l = 0; l < 7; ++l) {
            this.offers[l] = this.addButton(new WidgetButtonPage(i + 5, k, l, arg -> {
                if (arg instanceof WidgetButtonPage) {
                    this.selectedIndex = ((WidgetButtonPage)arg).getIndex() + this.indexStartOffset;
                    this.syncRecipeIndex();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        int k = ((MerchantScreenHandler)this.handler).getLevelProgress();
        if (k > 0 && k <= 5 && ((MerchantScreenHandler)this.handler).isLeveled()) {
            String string = "- " + I18n.translate("merchant.level." + k, new Object[0]);
            int l = this.textRenderer.getWidth(this.title);
            int m = this.textRenderer.getWidth(string);
            int n = l + m + 3;
            int o = 49 + this.backgroundWidth / 2 - n / 2;
            this.textRenderer.draw(matrices, this.title, (float)o, 6.0f, 0x404040);
            this.textRenderer.draw(matrices, string, (float)(o + l + 3), 6.0f, 0x404040);
        } else {
            this.textRenderer.draw(matrices, this.title, (float)(49 + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2), 6.0f, 0x404040);
        }
        this.textRenderer.draw(matrices, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 0x404040);
        String string2 = I18n.translate("merchant.trades", new Object[0]);
        int p = this.textRenderer.getWidth(string2);
        this.textRenderer.draw(matrices, string2, (float)(5 - p / 2 + 48), 6.0f, 0x404040);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        MerchantScreen.drawTexture(matrices, k, l, this.getZOffset(), 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 512);
        TraderOfferList lv = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!lv.isEmpty()) {
            int m = this.selectedIndex;
            if (m < 0 || m >= lv.size()) {
                return;
            }
            TradeOffer lv2 = (TradeOffer)lv.get(m);
            if (lv2.isDisabled()) {
                this.client.getTextureManager().bindTexture(TEXTURE);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                MerchantScreen.drawTexture(matrices, this.x + 83 + 99, this.y + 35, this.getZOffset(), 311.0f, 0.0f, 28, 21, 256, 512);
            }
        }
    }

    private void drawLevelInfo(MatrixStack arg, int i, int j, TradeOffer arg2) {
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = ((MerchantScreenHandler)this.handler).getLevelProgress();
        int l = ((MerchantScreenHandler)this.handler).getExperience();
        if (k >= 5) {
            return;
        }
        MerchantScreen.drawTexture(arg, i + 136, j + 16, this.getZOffset(), 0.0f, 186.0f, 102, 5, 256, 512);
        int m = VillagerData.getLowerLevelExperience(k);
        if (l < m || !VillagerData.canLevelUp(k)) {
            return;
        }
        int n = 100;
        float f = 100.0f / (float)(VillagerData.getUpperLevelExperience(k) - m);
        int o = Math.min(MathHelper.floor(f * (float)(l - m)), 100);
        MerchantScreen.drawTexture(arg, i + 136, j + 16, this.getZOffset(), 0.0f, 191.0f, o + 1, 5, 256, 512);
        int p = ((MerchantScreenHandler)this.handler).getTraderRewardedExperience();
        if (p > 0) {
            int q = Math.min(MathHelper.floor((float)p * f), 100 - o);
            MerchantScreen.drawTexture(arg, i + 136 + o + 1, j + 16 + 1, this.getZOffset(), 2.0f, 182.0f, q, 3, 256, 512);
        }
    }

    private void method_20221(MatrixStack arg, int i, int j, TraderOfferList arg2) {
        int k = arg2.size() + 1 - 7;
        if (k > 1) {
            int l = 139 - (27 + (k - 1) * 139 / k);
            int m = 1 + l / k + 139 / k;
            int n = 113;
            int o = Math.min(113, this.indexStartOffset * m);
            if (this.indexStartOffset == k - 1) {
                o = 113;
            }
            MerchantScreen.drawTexture(arg, i + 94, j + 18 + o, this.getZOffset(), 0.0f, 199.0f, 6, 27, 256, 512);
        } else {
            MerchantScreen.drawTexture(arg, i + 94, j + 18, this.getZOffset(), 6.0f, 199.0f, 6, 27, 256, 512);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        TraderOfferList lv = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!lv.isEmpty()) {
            int k = (this.width - this.backgroundWidth) / 2;
            int l = (this.height - this.backgroundHeight) / 2;
            int m = l + 16 + 1;
            int n = k + 5 + 5;
            RenderSystem.pushMatrix();
            RenderSystem.enableRescaleNormal();
            this.client.getTextureManager().bindTexture(TEXTURE);
            this.method_20221(matrices, k, l, lv);
            int o = 0;
            for (TradeOffer lv2 : lv) {
                if (this.canScroll(lv.size()) && (o < this.indexStartOffset || o >= 7 + this.indexStartOffset)) {
                    ++o;
                    continue;
                }
                ItemStack lv3 = lv2.getOriginalFirstBuyItem();
                ItemStack lv4 = lv2.getAdjustedFirstBuyItem();
                ItemStack lv5 = lv2.getSecondBuyItem();
                ItemStack lv6 = lv2.getMutableSellItem();
                this.itemRenderer.zOffset = 100.0f;
                int p = m + 2;
                this.method_20222(matrices, lv4, lv3, n, p);
                if (!lv5.isEmpty()) {
                    this.itemRenderer.renderInGui(lv5, k + 5 + 35, p);
                    this.itemRenderer.renderGuiItemOverlay(this.textRenderer, lv5, k + 5 + 35, p);
                }
                this.method_20223(matrices, lv2, k, p);
                this.itemRenderer.renderInGui(lv6, k + 5 + 68, p);
                this.itemRenderer.renderGuiItemOverlay(this.textRenderer, lv6, k + 5 + 68, p);
                this.itemRenderer.zOffset = 0.0f;
                m += 20;
                ++o;
            }
            int q = this.selectedIndex;
            TradeOffer lv7 = (TradeOffer)lv.get(q);
            if (((MerchantScreenHandler)this.handler).isLeveled()) {
                this.drawLevelInfo(matrices, k, l, lv7);
            }
            if (lv7.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, mouseX, mouseY) && ((MerchantScreenHandler)this.handler).canRefreshTrades()) {
                this.renderTooltip(matrices, new TranslatableText("merchant.deprecated"), mouseX, mouseY);
            }
            for (WidgetButtonPage lv8 : this.offers) {
                if (lv8.isHovered()) {
                    lv8.renderToolTip(matrices, mouseX, mouseY);
                }
                lv8.visible = lv8.index < ((MerchantScreenHandler)this.handler).getRecipes().size();
            }
            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void method_20223(MatrixStack arg, TradeOffer arg2, int i, int j) {
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(TEXTURE);
        if (arg2.isDisabled()) {
            MerchantScreen.drawTexture(arg, i + 5 + 35 + 20, j + 3, this.getZOffset(), 25.0f, 171.0f, 10, 9, 256, 512);
        } else {
            MerchantScreen.drawTexture(arg, i + 5 + 35 + 20, j + 3, this.getZOffset(), 15.0f, 171.0f, 10, 9, 256, 512);
        }
    }

    private void method_20222(MatrixStack arg, ItemStack arg2, ItemStack arg3, int i, int j) {
        this.itemRenderer.renderInGui(arg2, i, j);
        if (arg3.getCount() == arg2.getCount()) {
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, arg2, i, j);
        } else {
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, arg3, i, j, arg3.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, arg2, i + 14, j, arg2.getCount() == 1 ? "1" : null);
            this.client.getTextureManager().bindTexture(TEXTURE);
            this.setZOffset(this.getZOffset() + 300);
            MerchantScreen.drawTexture(arg, i + 7, j + 12, this.getZOffset(), 0.0f, 176.0f, 9, 2, 256, 512);
            this.setZOffset(this.getZOffset() - 300);
        }
    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int i = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = (int)((double)this.indexStartOffset - amount);
            this.indexStartOffset = MathHelper.clamp(this.indexStartOffset, 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int j = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.scrolling) {
            int k = this.y + 18;
            int l = k + 139;
            int m = j - 7;
            float h = ((float)mouseY - (float)k - 13.5f) / ((float)(l - k) - 27.0f);
            h = h * (float)m + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int)h, 0, m);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        if (this.canScroll(((MerchantScreenHandler)this.handler).getRecipes().size()) && mouseX > (double)(j + 94) && mouseX < (double)(j + 94 + 6) && mouseY > (double)(k + 18) && mouseY <= (double)(k + 18 + 139 + 1)) {
            this.scrolling = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Environment(value=EnvType.CLIENT)
    class WidgetButtonPage
    extends ButtonWidget {
        final int index;

        public WidgetButtonPage(int i, int j, int k, ButtonWidget.PressAction arg2) {
            super(i, j, 89, 20, LiteralText.EMPTY, arg2);
            this.index = k;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            if (this.hovered && ((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
                if (mouseX < this.x + 20) {
                    ItemStack lv = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getAdjustedFirstBuyItem();
                    MerchantScreen.this.renderTooltip(matrices, lv, mouseX, mouseY);
                } else if (mouseX < this.x + 50 && mouseX > this.x + 30) {
                    ItemStack lv2 = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSecondBuyItem();
                    if (!lv2.isEmpty()) {
                        MerchantScreen.this.renderTooltip(matrices, lv2, mouseX, mouseY);
                    }
                } else if (mouseX > this.x + 65) {
                    ItemStack lv3 = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getMutableSellItem();
                    MerchantScreen.this.renderTooltip(matrices, lv3, mouseX, mouseY);
                }
            }
        }
    }
}

