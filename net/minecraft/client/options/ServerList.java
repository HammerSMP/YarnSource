/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.options;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<ServerInfo> servers = Lists.newArrayList();

    public ServerList(MinecraftClient arg) {
        this.client = arg;
        this.loadFile();
    }

    public void loadFile() {
        try {
            this.servers.clear();
            CompoundTag lv = NbtIo.read(new File(this.client.runDirectory, "servers.dat"));
            if (lv == null) {
                return;
            }
            ListTag lv2 = lv.getList("servers", 10);
            for (int i = 0; i < lv2.size(); ++i) {
                this.servers.add(ServerInfo.deserialize(lv2.getCompound(i)));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load server list", (Throwable)exception);
        }
    }

    public void saveFile() {
        try {
            ListTag lv = new ListTag();
            for (ServerInfo lv2 : this.servers) {
                lv.add(lv2.serialize());
            }
            CompoundTag lv3 = new CompoundTag();
            lv3.put("servers", lv);
            NbtIo.safeWrite(lv3, new File(this.client.runDirectory, "servers.dat"));
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save server list", (Throwable)exception);
        }
    }

    public ServerInfo get(int i) {
        return this.servers.get(i);
    }

    public void remove(ServerInfo arg) {
        this.servers.remove(arg);
    }

    public void add(ServerInfo arg) {
        this.servers.add(arg);
    }

    public int size() {
        return this.servers.size();
    }

    public void swapEntries(int i, int j) {
        ServerInfo lv = this.get(i);
        this.servers.set(i, this.get(j));
        this.servers.set(j, lv);
        this.saveFile();
    }

    public void set(int i, ServerInfo arg) {
        this.servers.set(i, arg);
    }

    public static void updateServerListEntry(ServerInfo arg) {
        ServerList lv = new ServerList(MinecraftClient.getInstance());
        lv.loadFile();
        for (int i = 0; i < lv.size(); ++i) {
            ServerInfo lv2 = lv.get(i);
            if (!lv2.name.equals(arg.name) || !lv2.address.equals(arg.address)) continue;
            lv.set(i, arg);
            break;
        }
        lv.saveFile();
    }
}

