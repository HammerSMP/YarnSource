/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class Screen
extends AbstractParentElement
implements TickableElement,
Drawable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet((Object[])new String[]{"http", "https"});
    protected final Text title;
    protected final List<Element> children = Lists.newArrayList();
    @Nullable
    protected MinecraftClient client;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractButtonWidget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected TextRenderer textRenderer;
    private URI clickedLink;

    protected Screen(Text arg) {
        this.title = arg;
    }

    public Text getTitle() {
        return this.title;
    }

    public String getNarrationMessage() {
        return this.getTitle().getString();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        for (int k = 0; k < this.buttons.size(); ++k) {
            this.buttons.get(k).render(arg, i, j, f);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (i == 258) {
            boolean bl;
            boolean bl2 = bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.client.openScreen(null);
    }

    protected <T extends AbstractButtonWidget> T addButton(T arg) {
        this.buttons.add(arg);
        return this.addChild(arg);
    }

    protected <T extends Element> T addChild(T arg) {
        this.children.add(arg);
        return arg;
    }

    protected void renderTooltip(MatrixStack arg, ItemStack arg2, int i, int j) {
        this.renderTooltip(arg, this.getTooltipFromItem(arg2), i, j);
    }

    public List<Text> getTooltipFromItem(ItemStack arg) {
        return arg.getTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
    }

    public void renderTooltip(MatrixStack arg, class_5348 arg2, int i, int j) {
        this.renderTooltip(arg, Arrays.asList(arg2), i, j);
    }

    /*
     * WARNING - void declaration
     */
    public void renderTooltip(MatrixStack arg, List<? extends class_5348> list, int i, int j) {
        int n;
        if (list.isEmpty()) {
            return;
        }
        int k = 0;
        for (class_5348 class_53482 : list) {
            int l = this.textRenderer.getWidth(class_53482);
            if (l <= k) continue;
            k = l;
        }
        int m = i + 12;
        int n2 = j - 12;
        int o = k;
        int p = 8;
        if (list.size() > 1) {
            p += 2 + (list.size() - 1) * 10;
        }
        if (m + k > this.width) {
            m -= 28 + k;
        }
        if (n2 + p + 6 > this.height) {
            n = this.height - p - 6;
        }
        arg.push();
        int q = -267386864;
        int r = 0x505000FF;
        int s = 1344798847;
        int t = 400;
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f lv4 = arg.peek().getModel();
        Screen.fillGradient(lv4, lv3, m - 3, n - 4, m + o + 3, n - 3, 400, -267386864, -267386864);
        Screen.fillGradient(lv4, lv3, m - 3, n + p + 3, m + o + 3, n + p + 4, 400, -267386864, -267386864);
        Screen.fillGradient(lv4, lv3, m - 3, n - 3, m + o + 3, n + p + 3, 400, -267386864, -267386864);
        Screen.fillGradient(lv4, lv3, m - 4, n - 3, m - 3, n + p + 3, 400, -267386864, -267386864);
        Screen.fillGradient(lv4, lv3, m + o + 3, n - 3, m + o + 4, n + p + 3, 400, -267386864, -267386864);
        Screen.fillGradient(lv4, lv3, m - 3, n - 3 + 1, m - 3 + 1, n + p + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(lv4, lv3, m + o + 2, n - 3 + 1, m + o + 3, n + p + 3 - 1, 400, 0x505000FF, 1344798847);
        Screen.fillGradient(lv4, lv3, m - 3, n - 3, m + o + 3, n - 3 + 1, 400, 0x505000FF, 0x505000FF);
        Screen.fillGradient(lv4, lv3, m - 3, n + p + 2, m + o + 3, n + p + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        lv3.end();
        BufferRenderer.draw(lv3);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate lv5 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        arg.translate(0.0, 0.0, 400.0);
        for (int u = 0; u < list.size(); ++u) {
            class_5348 lv6 = list.get(u);
            if (lv6 != null) {
                void var7_11;
                this.textRenderer.draw(lv6, (float)m, (float)var7_11, -1, true, lv4, (VertexConsumerProvider)lv5, false, 0, 0xF000F0);
            }
            if (u == 0) {
                var7_11 += 2;
            }
            var7_11 += 10;
        }
        lv5.draw();
        arg.pop();
    }

    protected void renderTextHoverEffect(MatrixStack arg, @Nullable Style arg2, int i, int j) {
        if (arg2 == null || arg2.getHoverEvent() == null) {
            return;
        }
        HoverEvent lv = arg2.getHoverEvent();
        HoverEvent.ItemStackContent lv2 = lv.getValue(HoverEvent.Action.SHOW_ITEM);
        if (lv2 != null) {
            this.renderTooltip(arg, lv2.asStack(), i, j);
        } else {
            HoverEvent.EntityContent lv3 = lv.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (lv3 != null) {
                if (this.client.options.advancedItemTooltips) {
                    this.renderTooltip(arg, lv3.asTooltip(), i, j);
                }
            } else {
                Text lv4 = lv.getValue(HoverEvent.Action.SHOW_TEXT);
                if (lv4 != null) {
                    this.renderTooltip(arg, this.client.textRenderer.wrapLines(lv4, Math.max(this.width / 2, 200)), i, j);
                }
            }
        }
    }

    protected void insertText(String string, boolean bl) {
    }

    public boolean handleTextClick(@Nullable Style arg) {
        if (arg == null) {
            return false;
        }
        ClickEvent lv = arg.getClickEvent();
        if (Screen.hasShiftDown()) {
            if (arg.getInsertion() != null) {
                this.insertText(arg.getInsertion(), false);
            }
        } else if (lv != null) {
            block21: {
                if (lv.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.client.options.chatLinks) {
                        return false;
                    }
                    try {
                        URI uRI = new URI(lv.getValue());
                        String string = uRI.getScheme();
                        if (string == null) {
                            throw new URISyntaxException(lv.getValue(), "Missing protocol");
                        }
                        if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(lv.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
                        }
                        if (this.client.options.chatLinksPrompt) {
                            this.clickedLink = uRI;
                            this.client.openScreen(new ConfirmChatLinkScreen(this::confirmLink, lv.getValue(), false));
                            break block21;
                        }
                        this.openLink(uRI);
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        LOGGER.error("Can't open url for {}", (Object)lv, (Object)uRISyntaxException);
                    }
                } else if (lv.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uRI2 = new File(lv.getValue()).toURI();
                    this.openLink(uRI2);
                } else if (lv.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(lv.getValue(), true);
                } else if (lv.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(lv.getValue(), false);
                } else if (lv.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.client.keyboard.setClipboard(lv.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)lv);
                }
            }
            return true;
        }
        return false;
    }

    public void sendMessage(String string) {
        this.sendMessage(string, true);
    }

    public void sendMessage(String string, boolean bl) {
        if (bl) {
            this.client.inGameHud.getChatHud().addToMessageHistory(string);
        }
        this.client.player.sendChatMessage(string);
    }

    public void init(MinecraftClient arg, int i, int j) {
        this.client = arg;
        this.itemRenderer = arg.getItemRenderer();
        this.textRenderer = arg.textRenderer;
        this.width = i;
        this.height = j;
        this.buttons.clear();
        this.children.clear();
        this.setFocused(null);
        this.init();
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    protected void init() {
    }

    @Override
    public void tick() {
    }

    public void removed() {
    }

    public void renderBackground(MatrixStack arg) {
        this.renderBackground(arg, 0);
    }

    public void renderBackground(MatrixStack arg, int i) {
        if (this.client.world != null) {
            this.fillGradient(arg, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderBackgroundTexture(i);
        }
    }

    public void renderBackgroundTexture(int i) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(0.0, this.height, 0.0).texture(0.0f, (float)this.height / 32.0f + (float)i).color(64, 64, 64, 255).next();
        lv2.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)i).color(64, 64, 64, 255).next();
        lv2.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, i).color(64, 64, 64, 255).next();
        lv2.vertex(0.0, 0.0, 0.0).texture(0.0f, i).color(64, 64, 64, 255).next();
        lv.draw();
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean bl) {
        if (bl) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.client.openScreen(this);
    }

    private void openLink(URI uRI) {
        Util.getOperatingSystem().open(uRI);
    }

    public static boolean hasControlDown() {
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
    }

    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
    }

    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 346);
    }

    public static boolean isCut(int i) {
        return i == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int i) {
        return i == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int i) {
        return i == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int i) {
        return i == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(MinecraftClient arg, int i, int j) {
        this.init(arg, i, j);
    }

    public static void wrapScreenError(Runnable runnable, String string, String string2) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, string);
            CrashReportSection lv2 = lv.addElement("Affected screen");
            lv2.add("Screen name", () -> string2);
            throw new CrashException(lv);
        }
    }

    protected boolean isValidCharacterForName(String string, char c, int i) {
        int j = string.indexOf(58);
        int k = string.indexOf(47);
        if (c == ':') {
            return (k == -1 || i <= k) && j == -1;
        }
        if (c == '/') {
            return i > j;
        }
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return true;
    }
}

