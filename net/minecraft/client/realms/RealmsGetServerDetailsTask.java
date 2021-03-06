/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsConnectTask;
import net.minecraft.client.realms.RealmsMainScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.realms.gui.screen.RealmsBrokenWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsTermsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsGetServerDetailsTask
extends LongRunningTask {
    private final RealmsServer server;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final ReentrantLock connectLock;

    public RealmsGetServerDetailsTask(RealmsMainScreen mainScreen, Screen lastScreen, RealmsServer server, ReentrantLock connectLock) {
        this.lastScreen = lastScreen;
        this.mainScreen = mainScreen;
        this.server = server;
        this.connectLock = connectLock;
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.connect.connecting", new Object[0]));
        RealmsClient lv = RealmsClient.createRealmsClient();
        boolean bl2 = false;
        boolean bl22 = false;
        int i = 5;
        RealmsServerAddress lv2 = null;
        boolean bl3 = false;
        boolean bl4 = false;
        for (int j = 0; j < 40 && !this.aborted(); ++j) {
            try {
                lv2 = lv.join(this.server.id);
                bl2 = true;
            }
            catch (RetryCallException lv3) {
                i = lv3.delaySeconds;
            }
            catch (RealmsServiceException lv4) {
                if (lv4.errorCode == 6002) {
                    bl3 = true;
                    break;
                }
                if (lv4.errorCode == 6006) {
                    bl4 = true;
                    break;
                }
                bl22 = true;
                this.error(lv4.toString());
                LOGGER.error("Couldn't connect to world", (Throwable)lv4);
                break;
            }
            catch (Exception exception) {
                bl22 = true;
                LOGGER.error("Couldn't connect to world", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                break;
            }
            if (bl2) break;
            this.sleep(i);
        }
        if (bl3) {
            RealmsGetServerDetailsTask.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
        } else if (bl4) {
            if (this.server.ownerUUID.equals(MinecraftClient.getInstance().getSession().getUuid())) {
                RealmsGetServerDetailsTask.setScreen(new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME));
            } else {
                RealmsGetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(new TranslatableText("mco.brokenworld.nonowner.title"), new TranslatableText("mco.brokenworld.nonowner.error"), this.lastScreen));
            }
        } else if (!this.aborted() && !bl22) {
            if (bl2) {
                RealmsServerAddress lv5 = lv2;
                if (lv5.resourcePackUrl != null && lv5.resourcePackHash != null) {
                    TranslatableText lv6 = new TranslatableText("mco.configure.world.resourcepack.question.line1");
                    TranslatableText lv7 = new TranslatableText("mco.configure.world.resourcepack.question.line2");
                    RealmsGetServerDetailsTask.setScreen(new RealmsLongConfirmationScreen(bl -> {
                        try {
                            if (bl) {
                                Function<Throwable, Void> function = throwable -> {
                                    MinecraftClient.getInstance().getResourcePackDownloader().clear();
                                    LOGGER.error(throwable);
                                    RealmsGetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(new LiteralText("Failed to download resource pack!"), this.lastScreen));
                                    return null;
                                };
                                try {
                                    ((CompletableFuture)MinecraftClient.getInstance().getResourcePackDownloader().download(arg.resourcePackUrl, arg.resourcePackHash).thenRun(() -> this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, lv5))))).exceptionally(function);
                                }
                                catch (Exception exception) {
                                    function.apply(exception);
                                }
                            } else {
                                RealmsGetServerDetailsTask.setScreen(this.lastScreen);
                            }
                        }
                        finally {
                            if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                                this.connectLock.unlock();
                            }
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, lv6, lv7, true));
                } else {
                    this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsConnectTask(this.lastScreen, lv5)));
                }
            } else {
                this.error(new TranslatableText("mco.errorMessage.connectionFailure"));
            }
        }
    }

    private void sleep(int sleepTimeSeconds) {
        try {
            Thread.sleep(sleepTimeSeconds * 1000);
        }
        catch (InterruptedException interruptedException) {
            LOGGER.warn(interruptedException.getLocalizedMessage());
        }
    }
}

