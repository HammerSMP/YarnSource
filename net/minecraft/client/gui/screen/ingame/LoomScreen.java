/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LoomScreen
extends HandledScreen<LoomScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/loom.png");
    private static final int PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - BannerPattern.field_24417 - 1 + 4 - 1) / 4;
    private final ModelPart bannerField;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> field_21841;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean canApplySpecialPattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int firstPatternButtonId = 1;

    public LoomScreen(LoomScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.bannerField = BannerBlockEntityRenderer.createBanner();
        handler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.x;
        int l = this.y;
        this.drawTexture(matrices, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        Slot lv = ((LoomScreenHandler)this.handler).getBannerSlot();
        Slot lv2 = ((LoomScreenHandler)this.handler).getDyeSlot();
        Slot lv3 = ((LoomScreenHandler)this.handler).getPatternSlot();
        Slot lv4 = ((LoomScreenHandler)this.handler).getOutputSlot();
        if (!lv.hasStack()) {
            this.drawTexture(matrices, k + lv.x, l + lv.y, this.backgroundWidth, 0, 16, 16);
        }
        if (!lv2.hasStack()) {
            this.drawTexture(matrices, k + lv2.x, l + lv2.y, this.backgroundWidth + 16, 0, 16, 16);
        }
        if (!lv3.hasStack()) {
            this.drawTexture(matrices, k + lv3.x, l + lv3.y, this.backgroundWidth + 32, 0, 16, 16);
        }
        int m = (int)(41.0f * this.scrollPosition);
        this.drawTexture(matrices, k + 119, l + 13 + m, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        DiffuseLighting.disableGuiDepthLighting();
        if (this.field_21841 != null && !this.hasTooManyPatterns) {
            VertexConsumerProvider.Immediate lv5 = this.client.getBufferBuilders().getEntityVertexConsumers();
            matrices.push();
            matrices.translate(k + 139, l + 52, 0.0);
            matrices.scale(24.0f, -24.0f, 1.0f);
            matrices.translate(0.5, 0.5, 0.5);
            float g = 0.6666667f;
            matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
            this.bannerField.pitch = 0.0f;
            this.bannerField.pivotY = -32.0f;
            BannerBlockEntityRenderer.method_29999(matrices, lv5, 0xF000F0, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, this.field_21841);
            matrices.pop();
            lv5.draw();
        } else if (this.hasTooManyPatterns) {
            this.drawTexture(matrices, k + lv4.x - 2, l + lv4.y - 2, this.backgroundWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            int n = k + 60;
            int o = l + 13;
            int p = this.firstPatternButtonId + 16;
            for (int q = this.firstPatternButtonId; q < p && q < BannerPattern.COUNT - BannerPattern.field_24417; ++q) {
                int r = q - this.firstPatternButtonId;
                int s = n + r % 4 * 14;
                int t = o + r / 4 * 14;
                this.client.getTextureManager().bindTexture(TEXTURE);
                int u = this.backgroundHeight;
                if (q == ((LoomScreenHandler)this.handler).getSelectedPattern()) {
                    u += 14;
                } else if (mouseX >= s && mouseY >= t && mouseX < s + 14 && mouseY < t + 14) {
                    u += 28;
                }
                this.drawTexture(matrices, s, t, 0, u, 14, 14);
                this.method_22692(q, s, t);
            }
        } else if (this.canApplySpecialPattern) {
            int v = k + 60;
            int w = l + 13;
            this.client.getTextureManager().bindTexture(TEXTURE);
            this.drawTexture(matrices, v, w, 0, this.backgroundHeight, 14, 14);
            int x = ((LoomScreenHandler)this.handler).getSelectedPattern();
            this.method_22692(x, v, w);
        }
        DiffuseLighting.enableGuiDepthLighting();
    }

    private void method_22692(int i, int j, int k) {
        ItemStack lv = new ItemStack(Items.GRAY_BANNER);
        CompoundTag lv2 = lv.getOrCreateSubTag("BlockEntityTag");
        ListTag lv3 = new BannerPattern.Patterns().add(BannerPattern.BASE, DyeColor.GRAY).add(BannerPattern.values()[i], DyeColor.WHITE).toTag();
        lv2.put("Patterns", lv3);
        MatrixStack lv4 = new MatrixStack();
        lv4.push();
        lv4.translate((float)j + 0.5f, k + 16, 0.0);
        lv4.scale(6.0f, -6.0f, 1.0f);
        lv4.translate(0.5, 0.5, 0.0);
        lv4.translate(0.5, 0.5, 0.5);
        float f = 0.6666667f;
        lv4.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate lv5 = this.client.getBufferBuilders().getEntityVertexConsumers();
        this.bannerField.pitch = 0.0f;
        this.bannerField.pivotY = -32.0f;
        List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(DyeColor.GRAY, BannerBlockEntity.getPatternListTag(lv));
        BannerBlockEntityRenderer.method_29999(lv4, lv5, 0xF000F0, OverlayTexture.DEFAULT_UV, this.bannerField, ModelLoader.BANNER_BASE, true, list);
        lv4.pop();
        lv5.draw();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int j = this.x + 60;
            int k = this.y + 13;
            int l = this.firstPatternButtonId + 16;
            for (int m = this.firstPatternButtonId; m < l; ++m) {
                int n = m - this.firstPatternButtonId;
                double f = mouseX - (double)(j + n % 4 * 14);
                double g = mouseY - (double)(k + n / 4 * 14);
                if (!(f >= 0.0) || !(g >= 0.0) || !(f < 14.0) || !(g < 14.0) || !((LoomScreenHandler)this.handler).onButtonClick(this.client.player, m)) continue;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                this.client.interactionManager.clickButton(((LoomScreenHandler)this.handler).syncId, m);
                return true;
            }
            j = this.x + 119;
            k = this.y + 9;
            if (mouseX >= (double)j && mouseX < (double)(j + 12) && mouseY >= (double)k && mouseY < (double)(k + 56)) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollbarClicked && this.canApplyDyePattern) {
            int j = this.y + 13;
            int k = j + 56;
            this.scrollPosition = ((float)mouseY - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            int l = PATTERN_BUTTON_ROW_COUNT - 4;
            int m = (int)((double)(this.scrollPosition * (float)l) + 0.5);
            if (m < 0) {
                m = 0;
            }
            this.firstPatternButtonId = 1 + m * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.canApplyDyePattern) {
            int i = PATTERN_BUTTON_ROW_COUNT - 4;
            this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.firstPatternButtonId = 1 + (int)((double)(this.scrollPosition * (float)i) + 0.5) * 4;
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    private void onInventoryChanged() {
        ItemStack lv = ((LoomScreenHandler)this.handler).getOutputSlot().getStack();
        this.field_21841 = lv.isEmpty() ? null : BannerBlockEntity.method_24280(((BannerItem)lv.getItem()).getColor(), BannerBlockEntity.getPatternListTag(lv));
        ItemStack lv2 = ((LoomScreenHandler)this.handler).getBannerSlot().getStack();
        ItemStack lv3 = ((LoomScreenHandler)this.handler).getDyeSlot().getStack();
        ItemStack lv4 = ((LoomScreenHandler)this.handler).getPatternSlot().getStack();
        CompoundTag lv5 = lv2.getOrCreateSubTag("BlockEntityTag");
        boolean bl = this.hasTooManyPatterns = lv5.contains("Patterns", 9) && !lv2.isEmpty() && lv5.getList("Patterns", 10).size() >= 6;
        if (this.hasTooManyPatterns) {
            this.field_21841 = null;
        }
        if (!(ItemStack.areEqual(lv2, this.banner) && ItemStack.areEqual(lv3, this.dye) && ItemStack.areEqual(lv4, this.pattern))) {
            this.canApplyDyePattern = !lv2.isEmpty() && !lv3.isEmpty() && lv4.isEmpty() && !this.hasTooManyPatterns;
            this.canApplySpecialPattern = !this.hasTooManyPatterns && !lv4.isEmpty() && !lv2.isEmpty() && !lv3.isEmpty();
        }
        this.banner = lv2.copy();
        this.dye = lv3.copy();
        this.pattern = lv4.copy();
    }
}

