/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class SignEditScreen
extends Screen {
    private final SignBlockEntityRenderer.SignModel field_21525 = new SignBlockEntityRenderer.SignModel();
    private final SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;
    private final String[] field_24285 = Util.make(new String[4], strings -> Arrays.fill(strings, ""));

    public SignEditScreen(SignBlockEntity arg) {
        super(new TranslatableText("sign.edit"));
        this.sign = arg;
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, ScreenTexts.DONE, arg -> this.finishEditing()));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(() -> this.field_24285[this.currentRow], string -> {
            this.field_24285[this.currentRow] = string;
            this.sign.setTextOnRow(this.currentRow, new LiteralText((String)string));
        }, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client), string -> this.client.textRenderer.getWidth((String)string) <= 90);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        ClientPlayNetworkHandler lv = this.client.getNetworkHandler();
        if (lv != null) {
            lv.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3)));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState().getBlock())) {
            this.finishEditing();
        }
    }

    private void finishEditing() {
        this.sign.markDirty();
        this.client.openScreen(null);
    }

    @Override
    public boolean charTyped(char c, int i) {
        this.selectionManager.insert(c);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (i == 264 || i == 257 || i == 335) {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (this.selectionManager.handleSpecialKey(i)) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        arg.push();
        arg.translate(this.width / 2, 0.0, 50.0);
        float g = 93.75f;
        arg.scale(93.75f, -93.75f, 93.75f);
        arg.translate(0.0, -1.3125, 0.0);
        BlockState lv = this.sign.getCachedState();
        boolean bl = lv.getBlock() instanceof SignBlock;
        if (!bl) {
            arg.translate(0.0, -0.3125, 0.0);
        }
        boolean bl2 = this.ticksSinceOpened / 6 % 2 == 0;
        float h = 0.6666667f;
        arg.push();
        arg.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumerProvider.Immediate lv2 = this.client.getBufferBuilders().getEntityVertexConsumers();
        SpriteIdentifier lv3 = SignBlockEntityRenderer.getModelTexture(lv.getBlock());
        VertexConsumer lv4 = lv3.getVertexConsumer(lv2, this.field_21525::getLayer);
        this.field_21525.field.render(arg, lv4, 0xF000F0, OverlayTexture.DEFAULT_UV);
        if (bl) {
            this.field_21525.foot.render(arg, lv4, 0xF000F0, OverlayTexture.DEFAULT_UV);
        }
        arg.pop();
        float k = 0.010416667f;
        arg.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        arg.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int l = this.sign.getTextColor().getSignColor();
        int m = this.selectionManager.getSelectionStart();
        int n = this.selectionManager.getSelectionEnd();
        int o = this.currentRow * 10 - this.field_24285.length * 5;
        Matrix4f lv5 = arg.peek().getModel();
        for (int p = 0; p < this.field_24285.length; ++p) {
            String string = this.field_24285[p];
            if (string == null) continue;
            if (this.textRenderer.isRightToLeft()) {
                string = this.textRenderer.mirror(string);
            }
            float q = -this.client.textRenderer.getWidth(string) / 2;
            this.client.textRenderer.draw(string, q, p * 10 - this.field_24285.length * 5, l, false, lv5, lv2, false, 0, 0xF000F0, false);
            if (p != this.currentRow || m < 0 || !bl2) continue;
            int r = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(m, string.length()), 0)));
            int s = r - this.client.textRenderer.getWidth(string) / 2;
            if (m < string.length()) continue;
            this.client.textRenderer.draw("_", s, o, l, false, lv5, lv2, false, 0, 0xF000F0, false);
        }
        lv2.draw();
        for (int t = 0; t < this.field_24285.length; ++t) {
            String string2 = this.field_24285[t];
            if (string2 == null || t != this.currentRow || m < 0) continue;
            int u = this.client.textRenderer.getWidth(string2.substring(0, Math.max(Math.min(m, string2.length()), 0)));
            int v = u - this.client.textRenderer.getWidth(string2) / 2;
            if (bl2 && m < string2.length()) {
                this.client.textRenderer.getClass();
                SignEditScreen.fill(arg, v, o - 1, v + 1, o + 9, 0xFF000000 | l);
            }
            if (n == m) continue;
            int w = Math.min(m, n);
            int x = Math.max(m, n);
            int y = this.client.textRenderer.getWidth(string2.substring(0, w)) - this.client.textRenderer.getWidth(string2) / 2;
            int z = this.client.textRenderer.getWidth(string2.substring(0, x)) - this.client.textRenderer.getWidth(string2) / 2;
            int aa = Math.min(y, z);
            int ab = Math.max(y, z);
            Tessellator lv6 = Tessellator.getInstance();
            BufferBuilder lv7 = lv6.getBuffer();
            RenderSystem.disableTexture();
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            lv7.begin(7, VertexFormats.POSITION_COLOR);
            this.client.textRenderer.getClass();
            lv7.vertex(lv5, aa, o + 9, 0.0f).color(0, 0, 255, 255).next();
            this.client.textRenderer.getClass();
            lv7.vertex(lv5, ab, o + 9, 0.0f).color(0, 0, 255, 255).next();
            lv7.vertex(lv5, ab, o, 0.0f).color(0, 0, 255, 255).next();
            lv7.vertex(lv5, aa, o, 0.0f).color(0, 0, 255, 255).next();
            lv7.end();
            BufferRenderer.draw(lv7);
            RenderSystem.disableColorLogicOp();
            RenderSystem.enableTexture();
        }
        arg.pop();
        DiffuseLighting.enableGuiDepthLighting();
        super.render(arg, i, j, f);
    }
}

