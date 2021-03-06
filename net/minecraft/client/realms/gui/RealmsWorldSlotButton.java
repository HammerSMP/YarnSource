/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldSlotButton
extends ButtonWidget
implements TickableElement {
    public static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
    public static final Identifier EMPTY_FRAME = new Identifier("realms", "textures/gui/realms/empty_frame.png");
    public static final Identifier PANORAMA_0 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final Identifier PANORAMA_2 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final Identifier PANORAMA_3 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Text> toolTipSetter;
    private final int slotIndex;
    private int animTick;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int x, int y, int width, int height, Supplier<RealmsServer> serverDataProvider, Consumer<Text> toolTipSetter, int id, ButtonWidget.PressAction action) {
        super(x, y, width, height, LiteralText.EMPTY, action);
        this.serverDataProvider = serverDataProvider;
        this.slotIndex = id;
        this.toolTipSetter = toolTipSetter;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    @Override
    public void tick() {
        boolean bl5;
        String string4;
        long m;
        String string3;
        boolean bl4;
        boolean bl;
        ++this.animTick;
        RealmsServer lv = this.serverDataProvider.get();
        if (lv == null) {
            return;
        }
        RealmsWorldOptions lv2 = lv.slots.get(this.slotIndex);
        boolean bl2 = bl = this.slotIndex == 4;
        if (bl) {
            boolean bl22 = lv.worldType == RealmsServer.WorldType.MINIGAME;
            String string = "Minigame";
            long l = lv.minigameId;
            String string2 = lv.minigameImage;
            boolean bl3 = lv.minigameId == -1;
        } else {
            bl4 = lv.activeSlot == this.slotIndex && lv.worldType != RealmsServer.WorldType.MINIGAME;
            string3 = lv2.getSlotName(this.slotIndex);
            m = lv2.templateId;
            string4 = lv2.templateImage;
            bl5 = lv2.empty;
        }
        Action lv3 = RealmsWorldSlotButton.method_27455(lv, bl4, bl);
        Pair<Text, Text> pair = this.method_27454(lv, string3, bl5, bl, lv3);
        this.state = new State(bl4, string3, m, string4, bl5, bl, lv3, (Text)pair.getFirst());
        this.setMessage((Text)pair.getSecond());
    }

    private static Action method_27455(RealmsServer arg, boolean bl, boolean bl2) {
        if (bl) {
            if (!arg.expired && arg.state != RealmsServer.State.UNINITIALIZED) {
                return Action.JOIN;
            }
        } else if (bl2) {
            if (!arg.expired) {
                return Action.SWITCH_SLOT;
            }
        } else {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    private Pair<Text, Text> method_27454(RealmsServer arg, String string, boolean bl, boolean bl2, Action arg2) {
        TranslatableText lv5;
        MutableText lv3;
        if (arg2 == Action.NOTHING) {
            return Pair.of(null, (Object)new LiteralText(string));
        }
        if (bl2) {
            if (bl) {
                Text lv = LiteralText.EMPTY;
            } else {
                MutableText lv2 = new LiteralText(" ").append(string).append(" ").append(arg.minigameName);
            }
        } else {
            lv3 = new LiteralText(" ").append(string);
        }
        if (arg2 == Action.JOIN) {
            TranslatableText lv4 = new TranslatableText("mco.configure.world.slot.tooltip.active");
        } else {
            lv5 = bl2 ? new TranslatableText("mco.configure.world.slot.tooltip.minigame") : new TranslatableText("mco.configure.world.slot.tooltip");
        }
        MutableText lv6 = lv5.shallowCopy().append(lv3);
        return Pair.of((Object)lv5, (Object)lv6);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.state == null) {
            return;
        }
        this.drawSlotFrame(matrices, this.x, this.y, mouseX, mouseY, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }

    private void drawSlotFrame(MatrixStack matrices, int x, int y, int mouseX, int mouseY, boolean bl, String text, int m, long n, @Nullable String string2, boolean bl2, boolean bl3, Action arg2, @Nullable Text arg3) {
        boolean bl5;
        boolean bl4 = this.isHovered();
        if (this.isMouseOver(mouseX, mouseY) && arg3 != null) {
            this.toolTipSetter.accept(arg3);
        }
        MinecraftClient lv = MinecraftClient.getInstance();
        TextureManager lv2 = lv.getTextureManager();
        if (bl3) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
        } else if (bl2) {
            lv2.bindTexture(EMPTY_FRAME);
        } else if (string2 != null && n != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
        } else if (m == 1) {
            lv2.bindTexture(PANORAMA_0);
        } else if (m == 2) {
            lv2.bindTexture(PANORAMA_2);
        } else if (m == 3) {
            lv2.bindTexture(PANORAMA_3);
        }
        if (bl) {
            float f = 0.85f + 0.15f * MathHelper.cos((float)this.animTick * 0.2f);
            RenderSystem.color4f(f, f, f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrices, x + 3, y + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        lv2.bindTexture(SLOT_FRAME);
        boolean bl6 = bl5 = bl4 && arg2 != Action.NOTHING;
        if (bl5) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (bl) {
            RenderSystem.color4f(0.8f, 0.8f, 0.8f, 1.0f);
        } else {
            RenderSystem.color4f(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.drawTexture(matrices, x, y, 0.0f, 0.0f, 80, 80, 80, 80);
        this.drawCenteredString(matrices, lv.textRenderer, text, x + 40, y + 66, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        private final boolean isCurrentlyActiveSlot;
        private final String slotName;
        private final long imageId;
        private final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        private final Text actionPrompt;

        State(boolean isCurrentlyActiveSlot, String slotName, long imageId, @Nullable String image, boolean empty, boolean minigame, Action action, @Nullable Text actionPrompt) {
            this.isCurrentlyActiveSlot = isCurrentlyActiveSlot;
            this.slotName = slotName;
            this.imageId = imageId;
            this.image = image;
            this.empty = empty;
            this.minigame = minigame;
            this.action = action;
            this.actionPrompt = actionPrompt;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;

    }
}

