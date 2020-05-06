/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanServerPinger
extends Thread {
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private final String motd;
    private final DatagramSocket socket;
    private boolean running = true;
    private final String addressPort;

    public LanServerPinger(String string, String string2) throws IOException {
        super("LanServerPinger #" + THREAD_ID.incrementAndGet());
        this.motd = string;
        this.addressPort = string2;
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        String string = LanServerPinger.createAnnouncement(this.motd, this.addressPort);
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        while (!this.isInterrupted() && this.running) {
            try {
                InetAddress inetAddress = InetAddress.getByName("224.0.2.60");
                DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length, inetAddress, 4445);
                this.socket.send(datagramPacket);
            }
            catch (IOException iOException) {
                LOGGER.warn("LanServerPinger: {}", (Object)iOException.getMessage());
                break;
            }
            try {
                LanServerPinger.sleep(1500L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.running = false;
    }

    public static String createAnnouncement(String string, String string2) {
        return "[MOTD]" + string + "[/MOTD][AD]" + string2 + "[/AD]";
    }

    public static String parseAnnouncementMotd(String string) {
        int i = string.indexOf("[MOTD]");
        if (i < 0) {
            return "missing no";
        }
        int j = string.indexOf("[/MOTD]", i + "[MOTD]".length());
        if (j < i) {
            return "missing no";
        }
        return string.substring(i + "[MOTD]".length(), j);
    }

    public static String parseAnnouncementAddressPort(String string) {
        int i = string.indexOf("[/MOTD]");
        if (i < 0) {
            return null;
        }
        int j = string.indexOf("[/MOTD]", i + "[/MOTD]".length());
        if (j >= 0) {
            return null;
        }
        int k = string.indexOf("[AD]", i + "[/MOTD]".length());
        if (k < 0) {
            return null;
        }
        int l = string.indexOf("[/AD]", k + "[AD]".length());
        if (l < k) {
            return null;
        }
        return string.substring(k + "[AD]".length(), l);
    }
}

