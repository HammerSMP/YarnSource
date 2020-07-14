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
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class BeaconScreen
extends HandledScreen<BeaconScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
    private DoneButtonWidget doneButton;
    private boolean consumeGem;
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;

    public BeaconScreen(final BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 219;
        handler.addListener(new ScreenHandlerListener(){

            @Override
            public void onHandlerRegistered(ScreenHandler handler2, DefaultedList<ItemStack> stacks) {
            }

            @Override
            public void onSlotUpdate(ScreenHandler handler2, int slotId, ItemStack stack) {
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler2, int property, int value) {
                BeaconScreen.this.primaryEffect = handler.getPrimaryEffect();
                BeaconScreen.this.secondaryEffect = handler.getSecondaryEffect();
                BeaconScreen.this.consumeGem = true;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.doneButton = this.addButton(new DoneButtonWidget(this.x + 164, this.y + 107));
        this.addButton(new CancelButtonWidget(this.x + 190, this.y + 107));
        this.consumeGem = true;
        this.doneButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        int i = ((BeaconScreenHandler)this.handler).getProperties();
        if (this.consumeGem && i >= 0) {
            this.consumeGem = false;
            for (int j = 0; j <= 2; ++j) {
                int k = BeaconBlockEntity.EFFECTS_BY_LEVEL[j].length;
                int l = k * 22 + (k - 1) * 2;
                for (int m = 0; m < k; ++m) {
                    StatusEffect lv = BeaconBlockEntity.EFFECTS_BY_LEVEL[j][m];
                    EffectButtonWidget lv2 = new EffectButtonWidget(this.x + 76 + m * 24 - l / 2, this.y + 22 + j * 25, lv, true);
                    this.addButton(lv2);
                    if (j >= i) {
                        lv2.active = false;
                        continue;
                    }
                    if (lv != this.primaryEffect) continue;
                    lv2.setDisabled(true);
                }
            }
            int n = 3;
            int o = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].length + 1;
            int p = o * 22 + (o - 1) * 2;
            for (int q = 0; q < o - 1; ++q) {
                StatusEffect lv3 = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][q];
                EffectButtonWidget lv4 = new EffectButtonWidget(this.x + 167 + q * 24 - p / 2, this.y + 47, lv3, false);
                this.addButton(lv4);
                if (3 >= i) {
                    lv4.active = false;
                    continue;
                }
                if (lv3 != this.secondaryEffect) continue;
                lv4.setDisabled(true);
            }
            if (this.primaryEffect != null) {
                EffectButtonWidget lv5 = new EffectButtonWidget(this.x + 167 + (o - 1) * 24 - p / 2, this.y + 47, this.primaryEffect, false);
                this.addButton(lv5);
                if (3 >= i) {
                    lv5.active = false;
                } else if (this.primaryEffect == this.secondaryEffect) {
                    lv5.setDisabled(true);
                }
            }
        }
        this.doneButton.active = ((BeaconScreenHandler)this.handler).hasPayment() && this.primaryEffect != null;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("block.minecraft.beacon.primary", new Object[0]), 62, 10, 0xE0E0E0);
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("block.minecraft.beacon.secondary", new Object[0]), 169, 10, 0xE0E0E0);
        for (AbstractButtonWidget lv : this.buttons) {
            if (!lv.isHovered()) continue;
            lv.renderToolTip(matrices, mouseX - this.x, mouseY - this.y);
            break;
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.itemRenderer.zOffset = 100.0f;
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.NETHERITE_INGOT), k + 20, l + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), k + 41, l + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), k + 41 + 22, l + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), k + 42 + 44, l + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), k + 42 + 66, l + 109);
        this.itemRenderer.zOffset = 0.0f;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Environment(value=EnvType.CLIENT)
    class CancelButtonWidget
    extends IconButtonWidget {
        public CancelButtonWidget(int x, int y) {
            super(x, y, 112, 220);
        }

        @Override
        public void onPress() {
            ((BeaconScreen)BeaconScreen.this).client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(((BeaconScreen)BeaconScreen.this).client.player.currentScreenHandler.syncId));
            BeaconScreen.this.client.openScreen(null);
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            BeaconScreen.this.renderTooltip(matrices, ScreenTexts.CANCEL, mouseX, mouseY);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DoneButtonWidget
    extends IconButtonWidget {
        public DoneButtonWidget(int x, int y) {
            super(x, y, 90, 220);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BeaconScreen.this.primaryEffect), StatusEffect.getRawId(BeaconScreen.this.secondaryEffect)));
            ((BeaconScreen)BeaconScreen.this).client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(((BeaconScreen)BeaconScreen.this).client.player.currentScreenHandler.syncId));
            BeaconScreen.this.client.openScreen(null);
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            BeaconScreen.this.renderTooltip(matrices, ScreenTexts.DONE, mouseX, mouseY);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class IconButtonWidget
    extends BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int x, int y, int u, int v) {
            super(x, y);
            this.u = u;
            this.v = v;
        }

        @Override
        protected void renderExtra(MatrixStack arg) {
            this.drawTexture(arg, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EffectButtonWidget
    extends BaseButtonWidget {
        private final StatusEffect effect;
        private final Sprite sprite;
        private final boolean primary;

        public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary) {
            super(x, y);
            this.effect = statusEffect;
            this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.primary = primary;
        }

        @Override
        public void onPress() {
            if (this.isDisabled()) {
                return;
            }
            if (this.primary) {
                BeaconScreen.this.primaryEffect = this.effect;
            } else {
                BeaconScreen.this.secondaryEffect = this.effect;
            }
            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
        }

        @Override
        public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
            TranslatableText lv = new TranslatableText(this.effect.getTranslationKey());
            if (!this.primary && this.effect != StatusEffects.REGENERATION) {
                lv.append(" II");
            }
            BeaconScreen.this.renderTooltip(matrices, lv, mouseX, mouseY);
        }

        @Override
        protected void renderExtra(MatrixStack arg) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(this.sprite.getAtlas().getId());
            EffectButtonWidget.drawSprite(arg, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.sprite);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class BaseButtonWidget
    extends AbstractPressableButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            super(x, y, 22, 22, LiteralText.EMPTY);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int k = 219;
            int l = 0;
            if (!this.active) {
                l += this.width * 2;
            } else if (this.disabled) {
                l += this.width * 1;
            } else if (this.isHovered()) {
                l += this.width * 3;
            }
            this.drawTexture(matrices, this.x, this.y, l, 219, this.width, this.height);
            this.renderExtra(matrices);
        }

        protected abstract void renderExtra(MatrixStack var1);

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }
}

