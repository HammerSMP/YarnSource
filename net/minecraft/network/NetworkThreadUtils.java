/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network;

import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.thread.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkThreadUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends PacketListener> void forceMainThread(Packet<T> arg, T arg2, ServerWorld arg3) throws OffThreadException {
        NetworkThreadUtils.forceMainThread(arg, arg2, arg3.getServer());
    }

    public static <T extends PacketListener> void forceMainThread(Packet<T> arg, T arg2, ThreadExecutor<?> arg3) throws OffThreadException {
        if (!arg3.isOnThread()) {
            arg3.execute(() -> {
                if (arg2.getConnection().isOpen()) {
                    arg.apply(arg2);
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: " + arg);
                }
            });
            throw OffThreadException.INSTANCE;
        }
    }
}

