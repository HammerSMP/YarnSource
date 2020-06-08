/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.Hashing
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build());
    private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
    private final MultiplayerScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();
    private final Entry scanningEntry = new ScanningEntry();
    private final List<LanServerEntry> lanServers = Lists.newArrayList();

    public MultiplayerServerListWidget(MultiplayerScreen arg, MinecraftClient arg2, int i, int j, int k, int l, int m) {
        super(arg2, i, j, k, l, m);
        this.screen = arg;
    }

    private void updateEntries() {
        this.clearEntries();
        this.servers.forEach(this::addEntry);
        this.addEntry(this.scanningEntry);
        this.lanServers.forEach(this::addEntry);
    }

    @Override
    public void setSelected(Entry arg) {
        super.setSelected(arg);
        if (this.getSelected() instanceof ServerEntry) {
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", ((ServerEntry)((ServerEntry)this.getSelected())).server.name).getString());
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        Entry lv = (Entry)this.getSelected();
        return lv != null && lv.keyPressed(i, j, k) || super.keyPressed(i, j, k);
    }

    @Override
    protected void moveSelection(int i) {
        int j = this.children().indexOf(this.getSelected());
        int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
        Entry lv = (Entry)this.children().get(k);
        if (lv instanceof ScanningEntry) {
            k = MathHelper.clamp(k + (i > 0 ? 1 : -1), 0, this.getItemCount() - 1);
            lv = (Entry)this.children().get(k);
        }
        super.setSelected(lv);
        this.ensureVisible(lv);
        this.screen.updateButtonActivationStates();
    }

    public void setServers(ServerList arg) {
        this.servers.clear();
        for (int i = 0; i < arg.size(); ++i) {
            this.servers.add(new ServerEntry(this.screen, arg.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> list) {
        this.lanServers.clear();
        for (LanServerInfo lv : list) {
            this.lanServers.add(new LanServerEntry(this.screen, lv));
        }
        this.updateEntries();
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry
    extends Entry {
        private final MultiplayerScreen screen;
        private final MinecraftClient client;
        private final ServerInfo server;
        private final Identifier iconTextureId;
        private String iconUri;
        private NativeImageBackedTexture icon;
        private long time;

        protected ServerEntry(MultiplayerScreen arg2, ServerInfo arg3) {
            this.screen = arg2;
            this.server = arg3;
            this.client = MinecraftClient.getInstance();
            this.iconTextureId = new Identifier("servers/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)arg3.address) + "/icon");
            this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            List<Text> list5;
            TranslatableText lv5;
            int z;
            if (!this.server.online) {
                this.server.online = true;
                this.server.ping = -2L;
                this.server.label = LiteralText.EMPTY;
                this.server.playerCountLabel = LiteralText.EMPTY;
                SERVER_PINGER_THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getServerListPinger().add(this.server);
                    }
                    catch (UnknownHostException unknownHostException) {
                        this.server.ping = -1L;
                        this.server.label = new TranslatableText("multiplayer.status.cannot_resolve").formatted(Formatting.DARK_RED);
                    }
                    catch (Exception exception) {
                        this.server.ping = -1L;
                        this.server.label = new TranslatableText("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
                    }
                });
            }
            boolean bl2 = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl3 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl4 = bl2 || bl3;
            this.client.textRenderer.draw(arg, this.server.name, (float)(k + 32 + 3), (float)(j + 1), 0xFFFFFF);
            List<StringRenderable> list = this.client.textRenderer.wrapLines(this.server.label, l - 32 - 2);
            for (int p = 0; p < Math.min(list.size(), 2); ++p) {
                this.client.textRenderer.getClass();
                this.client.textRenderer.draw(arg, list.get(p), (float)(k + 32 + 3), (float)(j + 12 + 9 * p), 0x808080);
            }
            Text lv = bl4 ? this.server.version.shallowCopy().formatted(Formatting.DARK_RED) : this.server.playerCountLabel;
            int q = this.client.textRenderer.getWidth(lv);
            this.client.textRenderer.draw(arg, lv, (float)(k + l - q - 15 - 2), (float)(j + 1), 0x808080);
            int r = 0;
            if (bl4) {
                int s = 5;
                TranslatableText lv2 = new TranslatableText(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
                List<Text> list2 = this.server.playerListSummary;
            } else if (this.server.online && this.server.ping != -2L) {
                if (this.server.ping < 0L) {
                    int t = 5;
                } else if (this.server.ping < 150L) {
                    boolean u = false;
                } else if (this.server.ping < 300L) {
                    boolean v = true;
                } else if (this.server.ping < 600L) {
                    int w = 2;
                } else if (this.server.ping < 1000L) {
                    int x = 3;
                } else {
                    int y = 4;
                }
                if (this.server.ping < 0L) {
                    TranslatableText lv3 = new TranslatableText("multiplayer.status.no_connection");
                    List list3 = Collections.emptyList();
                } else {
                    TranslatableText lv4 = new TranslatableText("multiplayer.status.ping", this.server.ping);
                    List<Text> list4 = this.server.playerListSummary;
                }
            } else {
                r = 1;
                z = (int)(Util.getMeasuringTimeMs() / 100L + (long)(i * 2) & 7L);
                if (z > 4) {
                    z = 8 - z;
                }
                lv5 = new TranslatableText("multiplayer.status.pinging");
                list5 = Collections.emptyList();
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
            DrawableHelper.drawTexture(arg, k + l - 15, j, r * 10, 176 + z * 8, 10, 8, 256, 256);
            if (this.server.getIcon() != null && !this.server.getIcon().equals(this.iconUri)) {
                this.iconUri = this.server.getIcon();
                this.updateIcon();
                this.screen.getServerList().saveFile();
            }
            if (this.icon != null) {
                this.draw(arg, k, j, this.iconTextureId);
            } else {
                this.draw(arg, k, j, UNKNOWN_SERVER_TEXTURE);
            }
            int aa = n - k;
            int ab = o - j;
            if (aa >= l - 15 && aa <= l - 5 && ab >= 0 && ab <= 8) {
                this.screen.setTooltip(Collections.singletonList(lv5));
            } else if (aa >= l - q - 15 - 2 && aa <= l - 15 - 2 && ab >= 0 && ab <= 8) {
                this.screen.setTooltip(list5);
            }
            if (this.client.options.touchscreen || bl) {
                this.client.getTextureManager().bindTexture(SERVER_SELECTION_TEXTURE);
                DrawableHelper.fill(arg, k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int ac = n - k;
                int ad = o - j;
                if (this.method_20136()) {
                    if (ac < 32 && ac > 16) {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(arg, k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i > 0) {
                    if (ac < 16 && ad < 16) {
                        DrawableHelper.drawTexture(arg, k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(arg, k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i < this.screen.getServerList().size() - 1) {
                    if (ac < 16 && ad > 16) {
                        DrawableHelper.drawTexture(arg, k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(arg, k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        protected void draw(MatrixStack arg, int i, int j, Identifier arg2) {
            this.client.getTextureManager().bindTexture(arg2);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(arg, i, j, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean method_20136() {
            return true;
        }

        private void updateIcon() {
            String string = this.server.getIcon();
            if (string == null) {
                this.client.getTextureManager().destroyTexture(this.iconTextureId);
                if (this.icon != null && this.icon.getImage() != null) {
                    this.icon.getImage().close();
                }
                this.icon = null;
            } else {
                try {
                    NativeImage lv = NativeImage.read(string);
                    Validate.validState((lv.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                    Validate.validState((lv.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                    if (this.icon == null) {
                        this.icon = new NativeImageBackedTexture(lv);
                    } else {
                        this.icon.setImage(lv);
                        this.icon.upload();
                    }
                    this.client.getTextureManager().registerTexture(this.iconTextureId, this.icon);
                }
                catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", (Object)this.server.name, (Object)this.server.address, (Object)throwable);
                    this.server.setIcon(null);
                }
            }
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (Screen.hasShiftDown()) {
                MultiplayerServerListWidget lv = this.screen.serverListWidget;
                int l = lv.children().indexOf(this);
                if (i == 264 && l < this.screen.getServerList().size() - 1 || i == 265 && l > 0) {
                    this.swapEntries(l, i == 264 ? l + 1 : l - 1);
                    return true;
                }
            }
            return super.keyPressed(i, j, k);
        }

        private void swapEntries(int i, int j) {
            this.screen.getServerList().swapEntries(i, j);
            this.screen.serverListWidget.setServers(this.screen.getServerList());
            Entry lv = (Entry)this.screen.serverListWidget.children().get(j);
            this.screen.serverListWidget.setSelected(lv);
            MultiplayerServerListWidget.this.ensureVisible(lv);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)MultiplayerServerListWidget.this.getRowLeft();
            double g = e - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
            if (f <= 32.0) {
                if (f < 32.0 && f > 16.0 && this.method_20136()) {
                    this.screen.select(this);
                    this.screen.connect();
                    return true;
                }
                int j = this.screen.serverListWidget.children().indexOf(this);
                if (f < 16.0 && g < 16.0 && j > 0) {
                    this.swapEntries(j, j - 1);
                    return true;
                }
                if (f < 16.0 && g > 16.0 && j < this.screen.getServerList().size() - 1) {
                    this.swapEntries(j, j + 1);
                    return true;
                }
            }
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public ServerInfo getServer() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LanServerEntry
    extends Entry {
        private final MultiplayerScreen screen;
        protected final MinecraftClient client;
        protected final LanServerInfo server;
        private long time;

        protected LanServerEntry(MultiplayerScreen arg, LanServerInfo arg2) {
            this.screen = arg;
            this.server = arg2;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.client.textRenderer.draw(arg, I18n.translate("lanServer.title", new Object[0]), (float)(k + 32 + 3), (float)(j + 1), 0xFFFFFF);
            this.client.textRenderer.draw(arg, this.server.getMotd(), (float)(k + 32 + 3), (float)(j + 12), 0x808080);
            if (this.client.options.hideServerAddress) {
                this.client.textRenderer.draw(arg, I18n.translate("selectServer.hiddenAddress", new Object[0]), (float)(k + 32 + 3), (float)(j + 12 + 11), 0x303030);
            } else {
                this.client.textRenderer.draw(arg, this.server.getAddressPort(), (float)(k + 32 + 3), (float)(j + 12 + 11), 0x303030);
            }
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public LanServerInfo getLanServerEntry() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ScanningEntry
    extends Entry {
        private final MinecraftClient client = MinecraftClient.getInstance();

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            String string3;
            this.client.textRenderer.getClass();
            int p = j + m / 2 - 9 / 2;
            this.client.textRenderer.draw(arg, I18n.translate("lanServer.scanning", new Object[0]), (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(I18n.translate("lanServer.scanning", new Object[0])) / 2), (float)p, 0xFFFFFF);
            switch ((int)(Util.getMeasuringTimeMs() / 300L % 4L)) {
                default: {
                    String string = "O o o";
                    break;
                }
                case 1: 
                case 3: {
                    String string2 = "o O o";
                    break;
                }
                case 2: {
                    string3 = "o o O";
                }
            }
            this.client.textRenderer.getClass();
            this.client.textRenderer.draw(arg, string3, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string3) / 2), (float)(p + 9), 0x808080);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
    }
}

