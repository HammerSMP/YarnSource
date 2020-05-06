/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ConnectScreen
extends Screen {
    private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private ClientConnection connection;
    private boolean connectingCancelled;
    private final Screen parent;
    private Text status = new TranslatableText("connect.connecting");
    private long narratorTimer = -1L;

    public ConnectScreen(Screen arg, MinecraftClient arg2, ServerInfo arg3) {
        super(NarratorManager.EMPTY);
        this.client = arg2;
        this.parent = arg;
        ServerAddress lv = ServerAddress.parse(arg3.address);
        arg2.disconnect();
        arg2.setCurrentServerEntry(arg3);
        this.connect(lv.getAddress(), lv.getPort());
    }

    public ConnectScreen(Screen arg, MinecraftClient arg2, String string, int i) {
        super(NarratorManager.EMPTY);
        this.client = arg2;
        this.parent = arg;
        arg2.disconnect();
        this.connect(string, i);
    }

    private void connect(final String string, final int i) {
        LOGGER.info("Connecting to {}, {}", (Object)string, (Object)i);
        Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()){

            @Override
            public void run() {
                InetAddress inetAddress = null;
                try {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    inetAddress = InetAddress.getByName(string);
                    ConnectScreen.this.connection = ClientConnection.connect(inetAddress, i, ConnectScreen.this.client.options.shouldUseNativeTransport());
                    ConnectScreen.this.connection.setPacketListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, ConnectScreen.this.client, ConnectScreen.this.parent, arg2 -> ConnectScreen.this.setStatus(arg2)));
                    ConnectScreen.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
                    ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.client.getSession().getProfile()));
                }
                catch (UnknownHostException unknownHostException) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)unknownHostException);
                    ConnectScreen.this.client.execute(() -> ConnectScreen.this.client.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host"))));
                }
                catch (Exception exception) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)exception);
                    String string2 = inetAddress == null ? exception.toString() : exception.toString().replaceAll(inetAddress + ":" + i, "");
                    ConnectScreen.this.client.execute(() -> ConnectScreen.this.client.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", string2))));
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    private void setStatus(Text arg) {
        this.status = arg;
    }

    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, arg -> {
            this.connectingCancelled = true;
            if (this.connection != null) {
                this.connection.disconnect(new TranslatableText("connect.aborted"));
            }
            this.client.openScreen(this.parent);
        }));
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        long l = Util.getMeasuringTimeMs();
        if (l - this.narratorTimer > 2000L) {
            this.narratorTimer = l;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.joining").getString());
        }
        this.drawStringWithShadow(arg, this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(arg, i, j, f);
    }
}

