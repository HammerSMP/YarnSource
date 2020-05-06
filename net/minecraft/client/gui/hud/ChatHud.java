/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChatHud
extends DrawableHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine> visibleMessages = Lists.newArrayList();
    private final Deque<Text> field_23934 = Queues.newArrayDeque();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private long field_23935 = 0L;

    public ChatHud(MinecraftClient arg) {
        this.client = arg;
    }

    public void render(MatrixStack arg, int i) {
        if (this.method_23677()) {
            return;
        }
        this.method_27149();
        int j = this.getVisibleLineCount();
        int k = this.visibleMessages.size();
        if (k <= 0) {
            return;
        }
        boolean bl = false;
        if (this.isChatFocused()) {
            bl = true;
        }
        double d = this.getChatScale();
        int l = MathHelper.ceil((double)this.getWidth() / d);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(2.0f, 8.0f, 0.0f);
        RenderSystem.scaled(d, d, 1.0);
        double e = this.client.options.chatOpacity * (double)0.9f + (double)0.1f;
        double f = this.client.options.textBackgroundOpacity;
        double g = 9.0 * (this.client.options.chatLineSpacing + 1.0);
        double h = -8.0 * (this.client.options.chatLineSpacing + 1.0) + 4.0 * this.client.options.chatLineSpacing;
        int m = 0;
        for (int n = 0; n + this.scrolledLines < this.visibleMessages.size() && n < j; ++n) {
            int o;
            ChatHudLine lv = this.visibleMessages.get(n + this.scrolledLines);
            if (lv == null || (o = i - lv.getCreationTick()) >= 200 && !bl) continue;
            double p = bl ? 1.0 : ChatHud.getMessageOpacityMultiplier(o);
            int q = (int)(255.0 * p * e);
            int r = (int)(255.0 * p * f);
            ++m;
            if (q <= 3) continue;
            boolean s = false;
            double t = (double)(-n) * g;
            arg.push();
            arg.translate(0.0, 0.0, 50.0);
            ChatHud.fill(arg, -2, (int)(t - g), 0 + l + 4, (int)t, r << 24);
            RenderSystem.enableBlend();
            arg.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(arg, lv.getText(), 0.0f, (float)((int)(t + h)), 0xFFFFFF + (q << 24));
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            arg.pop();
        }
        if (!this.field_23934.isEmpty()) {
            int u = (int)(128.0 * e);
            int v = (int)(255.0 * f);
            arg.push();
            arg.translate(0.0, 0.0, 50.0);
            ChatHud.fill(arg, -2, 0, l + 4, 9, v << 24);
            RenderSystem.enableBlend();
            arg.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(arg, new TranslatableText("chat.queue", this.field_23934.size()), 0.0f, 1.0f, 0xFFFFFF + (u << 24));
            arg.pop();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
        if (bl) {
            this.client.textRenderer.getClass();
            int w = 9;
            RenderSystem.translatef(-3.0f, 0.0f, 0.0f);
            int x = k * w + k;
            int y = m * w + m;
            int z = this.scrolledLines * y / k;
            int aa = y * y / x;
            if (x != y) {
                int ab = z > 0 ? 170 : 96;
                int ac = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                ChatHud.fill(arg, 0, -z, 2, -z - aa, ac + (ab << 24));
                ChatHud.fill(arg, 2, -z, 1, -z - aa, 0xCCCCCC + (ab << 24));
            }
        }
        RenderSystem.popMatrix();
    }

    private boolean method_23677() {
        return this.client.options.chatVisibility == ChatVisibility.HIDDEN;
    }

    private static double getMessageOpacityMultiplier(int i) {
        double d = (double)i / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = MathHelper.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    public void clear(boolean bl) {
        this.visibleMessages.clear();
        this.messages.clear();
        if (bl) {
            this.messageHistory.clear();
        }
    }

    public void addMessage(Text arg) {
        this.addMessage(arg, 0);
    }

    public void addMessage(Text arg, int i) {
        this.addMessage(arg, i, this.client.inGameHud.getTicks(), false);
        LOGGER.info("[CHAT] {}", (Object)arg.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void addMessage(Text arg, int i, int j, boolean bl) {
        if (i != 0) {
            this.removeMessage(i);
        }
        int k = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<Text> list = ChatMessages.breakRenderedChatMessageLines(arg, k, this.client.textRenderer);
        boolean bl2 = this.isChatFocused();
        for (Text lv : list) {
            if (bl2 && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1.0);
            }
            this.visibleMessages.add(0, new ChatHudLine(j, lv, i));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!bl) {
            this.messages.add(0, new ChatHudLine(j, arg, i));
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void reset() {
        this.visibleMessages.clear();
        this.resetScroll();
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine lv = this.messages.get(i);
            this.addMessage(lv.getText(), lv.getId(), lv.getCreationTick(), true);
        }
    }

    public List<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String string) {
        if (this.messageHistory.isEmpty() || !this.messageHistory.get(this.messageHistory.size() - 1).equals(string)) {
            this.messageHistory.add(string);
        }
    }

    public void resetScroll() {
        this.scrolledLines = 0;
        this.hasUnreadNewMessages = false;
    }

    public void scroll(double d) {
        this.scrolledLines = (int)((double)this.scrolledLines + d);
        int i = this.visibleMessages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }
        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.hasUnreadNewMessages = false;
        }
    }

    public boolean method_27146(double d, double e) {
        if (!this.isChatFocused() || this.client.options.hudHidden || this.method_23677() || this.field_23934.isEmpty()) {
            return false;
        }
        double f = d - 2.0;
        double g = (double)this.client.getWindow().getScaledHeight() - e - 40.0;
        if (f <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && g < 0.0 && g > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
            this.addMessage(this.field_23934.remove());
            this.field_23935 = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Nullable
    public Text getText(double d, double e) {
        if (!this.isChatFocused() || this.client.options.hudHidden || this.method_23677()) {
            return null;
        }
        double f = d - 2.0;
        double g = (double)this.client.getWindow().getScaledHeight() - e - 40.0;
        f = MathHelper.floor(f / this.getChatScale());
        g = MathHelper.floor(g / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0)));
        if (f < 0.0 || g < 0.0) {
            return null;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (f <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale())) {
            this.client.textRenderer.getClass();
            if (g < (double)(9 * i + i)) {
                this.client.textRenderer.getClass();
                int j = (int)(g / 9.0 + (double)this.scrolledLines);
                if (j >= 0 && j < this.visibleMessages.size()) {
                    ChatHudLine lv = this.visibleMessages.get(j);
                    return this.client.textRenderer.getTextHandler().trimToWidth(lv.getText(), (int)f);
                }
            }
        }
        return null;
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    public void removeMessage(int i) {
        Iterator<ChatHudLine> iterator = this.visibleMessages.iterator();
        while (iterator.hasNext()) {
            ChatHudLine lv = iterator.next();
            if (lv.getId() != i) continue;
            iterator.remove();
        }
        iterator = this.messages.iterator();
        while (iterator.hasNext()) {
            ChatHudLine lv2 = iterator.next();
            if (lv2.getId() != i) continue;
            iterator.remove();
            break;
        }
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.chatWidth);
    }

    public int getHeight() {
        return ChatHud.getHeight((this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused) / (this.client.options.chatLineSpacing + 1.0));
    }

    public double getChatScale() {
        return this.client.options.chatScale;
    }

    public static int getWidth(double d) {
        int i = 320;
        int j = 40;
        return MathHelper.floor(d * 280.0 + 40.0);
    }

    public static int getHeight(double d) {
        int i = 180;
        int j = 20;
        return MathHelper.floor(d * 160.0 + 20.0);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / 9;
    }

    private long method_27148() {
        return (long)(this.client.options.chatDelay * 1000.0);
    }

    private void method_27149() {
        if (this.field_23934.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        if (l - this.field_23935 >= this.method_27148()) {
            this.addMessage(this.field_23934.remove());
            this.field_23935 = l;
        }
    }

    public void method_27147(Text arg) {
        if (this.client.options.chatDelay <= 0.0) {
            this.addMessage(arg);
        } else {
            long l = System.currentTimeMillis();
            if (l - this.field_23935 >= this.method_27148()) {
                this.addMessage(arg);
                this.field_23935 = l;
            } else {
                this.field_23934.add(arg);
            }
        }
    }
}

