/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientLoginNetworkHandler
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    @Nullable
    private final Screen parentGui;
    private final Consumer<Text> statusConsumer;
    private final ClientConnection connection;
    private GameProfile profile;

    public ClientLoginNetworkHandler(ClientConnection arg, MinecraftClient arg2, @Nullable Screen arg3, Consumer<Text> consumer) {
        this.connection = arg;
        this.client = arg2;
        this.parentGui = arg3;
        this.statusConsumer = consumer;
    }

    @Override
    public void onHello(LoginHelloS2CPacket arg) {
        SecretKey secretKey = NetworkEncryptionUtils.generateKey();
        PublicKey publicKey = arg.getPublicKey();
        String string = new BigInteger(NetworkEncryptionUtils.generateServerId(arg.getServerId(), publicKey, secretKey)).toString(16);
        LoginKeyC2SPacket lv = new LoginKeyC2SPacket(secretKey, publicKey, arg.getNonce());
        this.statusConsumer.accept(new TranslatableText("connect.authorizing"));
        NetworkUtils.downloadExecutor.submit(() -> {
            Text lv = this.joinServerSession(string);
            if (lv != null) {
                if (this.client.getCurrentServerEntry() != null && this.client.getCurrentServerEntry().isLocal()) {
                    LOGGER.warn(lv.getString());
                } else {
                    this.connection.disconnect(lv);
                    return;
                }
            }
            this.statusConsumer.accept(new TranslatableText("connect.encrypting"));
            this.connection.send(lv, (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.connection.setupEncryption(secretKey)));
        });
    }

    @Nullable
    private Text joinServerSession(String string) {
        try {
            this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getAccessToken(), string);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.serversUnavailable"));
        }
        catch (InvalidCredentialsException invalidCredentialsException) {
            return new TranslatableText("disconnect.loginFailedInfo", new TranslatableText("disconnect.loginFailedInfo.invalidSession"));
        }
        catch (AuthenticationException authenticationException) {
            return new TranslatableText("disconnect.loginFailedInfo", authenticationException.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getSessionService() {
        return this.client.getSessionService();
    }

    @Override
    public void onLoginSuccess(LoginSuccessS2CPacket arg) {
        this.statusConsumer.accept(new TranslatableText("connect.joining"));
        this.profile = arg.getProfile();
        this.connection.setState(NetworkState.PLAY);
        this.connection.setPacketListener(new ClientPlayNetworkHandler(this.client, this.parentGui, this.connection, this.profile));
    }

    @Override
    public void onDisconnected(Text arg) {
        if (this.parentGui != null && this.parentGui instanceof RealmsScreen) {
            this.client.openScreen(new DisconnectedRealmsScreen(this.parentGui, "connect.failed", arg));
        } else {
            this.client.openScreen(new DisconnectedScreen(this.parentGui, "connect.failed", arg));
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    @Override
    public void onDisconnect(LoginDisconnectS2CPacket arg) {
        this.connection.disconnect(arg.getReason());
    }

    @Override
    public void onCompression(LoginCompressionS2CPacket arg) {
        if (!this.connection.isLocal()) {
            this.connection.setCompressionThreshold(arg.getCompressionThreshold());
        }
    }

    @Override
    public void onQueryRequest(LoginQueryRequestS2CPacket arg) {
        this.statusConsumer.accept(new TranslatableText("connect.negotiating"));
        this.connection.send(new LoginQueryResponseC2SPacket(arg.getQueryId(), null));
    }
}

