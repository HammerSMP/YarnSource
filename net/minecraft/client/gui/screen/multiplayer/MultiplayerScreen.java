/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
    private final Screen parent;
    protected MultiplayerServerListWidget serverListWidget;
    private ServerList serverList;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonJoin;
    private ButtonWidget buttonDelete;
    private List<Text> tooltipText;
    private ServerInfo selectedEntry;
    private LanServerQueryManager.LanServerEntryList lanServers;
    private LanServerQueryManager.LanServerDetector lanServerDetector;
    private boolean initialized;

    public MultiplayerScreen(Screen arg) {
        super(new TranslatableText("multiplayer.title"));
        this.parent = arg;
    }

    @Override
    protected void init() {
        super.init();
        this.client.keyboard.enableRepeatEvents(true);
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            this.serverList.loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();
            try {
                this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
                this.lanServerDetector.start();
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }
            this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
            this.serverListWidget.setServers(this.serverList);
        }
        this.children.add(this.serverListWidget);
        this.buttonJoin = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, new TranslatableText("selectServer.select"), arg -> this.connect()));
        this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, new TranslatableText("selectServer.direct"), arg -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.client.openScreen(new DirectConnectScreen(this, this::directConnect, this.selectedEntry));
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, new TranslatableText("selectServer.add"), arg -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.client.openScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
        }));
        this.buttonEdit = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, new TranslatableText("selectServer.edit"), arg -> {
            MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
            if (lv instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo lv2 = ((MultiplayerServerListWidget.ServerEntry)lv).getServer();
                this.selectedEntry = new ServerInfo(lv2.name, lv2.address, false);
                this.selectedEntry.copyFrom(lv2);
                this.client.openScreen(new AddServerScreen(this, this::editEntry, this.selectedEntry));
            }
        }));
        this.buttonDelete = this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, new TranslatableText("selectServer.delete"), arg -> {
            String string;
            MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
            if (lv instanceof MultiplayerServerListWidget.ServerEntry && (string = ((MultiplayerServerListWidget.ServerEntry)lv).getServer().name) != null) {
                TranslatableText lv2 = new TranslatableText("selectServer.deleteQuestion");
                TranslatableText lv3 = new TranslatableText("selectServer.deleteWarning", string);
                TranslatableText lv4 = new TranslatableText("selectServer.deleteButton");
                Text lv5 = ScreenTexts.CANCEL;
                this.client.openScreen(new ConfirmScreen(this::removeEntry, lv2, lv3, lv4, lv5));
            }
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, new TranslatableText("selectServer.refresh"), arg -> this.refresh()));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.updateButtonActivationStates();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lanServers.needsUpdate()) {
            List<LanServerInfo> list = this.lanServers.getServers();
            this.lanServers.markClean();
            this.serverListWidget.setLanServers(list);
        }
        this.serverListPinger.tick();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.serverListPinger.cancel();
    }

    private void refresh() {
        this.client.openScreen(new MultiplayerScreen(this.parent));
    }

    private void removeEntry(boolean bl) {
        MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (bl && lv instanceof MultiplayerServerListWidget.ServerEntry) {
            this.serverList.remove(((MultiplayerServerListWidget.ServerEntry)lv).getServer());
            this.serverList.saveFile();
            this.serverListWidget.setSelected((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.openScreen(this);
    }

    private void editEntry(boolean bl) {
        MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (bl && lv instanceof MultiplayerServerListWidget.ServerEntry) {
            ServerInfo lv2 = ((MultiplayerServerListWidget.ServerEntry)lv).getServer();
            lv2.name = this.selectedEntry.name;
            lv2.address = this.selectedEntry.address;
            lv2.copyFrom(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.openScreen(this);
    }

    private void addEntry(boolean bl) {
        if (bl) {
            this.serverList.add(this.selectedEntry);
            this.serverList.saveFile();
            this.serverListWidget.setSelected((MultiplayerServerListWidget.Entry)null);
            this.serverListWidget.setServers(this.serverList);
        }
        this.client.openScreen(this);
    }

    private void directConnect(boolean bl) {
        if (bl) {
            this.connect(this.selectedEntry);
        } else {
            this.client.openScreen(this);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 294) {
            this.refresh();
            return true;
        }
        if (this.serverListWidget.getSelected() != null) {
            if (i == 257 || i == 335) {
                this.connect();
                return true;
            }
            return this.serverListWidget.keyPressed(i, j, k);
        }
        return false;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.tooltipText = null;
        this.renderBackground(arg);
        this.serverListWidget.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(arg, i, j, f);
        if (this.tooltipText != null) {
            this.renderTooltip(arg, this.tooltipText, i, j);
        }
    }

    public void connect() {
        MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (lv instanceof MultiplayerServerListWidget.ServerEntry) {
            this.connect(((MultiplayerServerListWidget.ServerEntry)lv).getServer());
        } else if (lv instanceof MultiplayerServerListWidget.LanServerEntry) {
            LanServerInfo lv2 = ((MultiplayerServerListWidget.LanServerEntry)lv).getLanServerEntry();
            this.connect(new ServerInfo(lv2.getMotd(), lv2.getAddressPort(), true));
        }
    }

    private void connect(ServerInfo arg) {
        this.client.openScreen(new ConnectScreen(this, this.client, arg));
    }

    public void select(MultiplayerServerListWidget.Entry arg) {
        this.serverListWidget.setSelected(arg);
        this.updateButtonActivationStates();
    }

    protected void updateButtonActivationStates() {
        this.buttonJoin.active = false;
        this.buttonEdit.active = false;
        this.buttonDelete.active = false;
        MultiplayerServerListWidget.Entry lv = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelected();
        if (lv != null && !(lv instanceof MultiplayerServerListWidget.ScanningEntry)) {
            this.buttonJoin.active = true;
            if (lv instanceof MultiplayerServerListWidget.ServerEntry) {
                this.buttonEdit.active = true;
                this.buttonDelete.active = true;
            }
        }
    }

    public MultiplayerServerListPinger getServerListPinger() {
        return this.serverListPinger;
    }

    public void setTooltip(List<Text> list) {
        this.tooltipText = list;
    }

    public ServerList getServerList() {
        return this.serverList;
    }
}

