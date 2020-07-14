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
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerList {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<ServerInfo> servers = Lists.newArrayList();

    public ServerList(MinecraftClient client) {
        this.client = client;
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
            File file = File.createTempFile("servers", ".dat", this.client.runDirectory);
            NbtIo.write(lv3, file);
            File file2 = new File(this.client.runDirectory, "servers.dat_old");
            File file3 = new File(this.client.runDirectory, "servers.dat");
            Util.method_27760(file3, file, file2);
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

    public void swapEntries(int index1, int j) {
        ServerInfo lv = this.get(index1);
        this.servers.set(index1, this.get(j));
        this.servers.set(j, lv);
        this.saveFile();
    }

    public void set(int index, ServerInfo arg) {
        this.servers.set(index, arg);
    }

    public static void updateServerListEntry(ServerInfo e) {
        ServerList lv = new ServerList(MinecraftClient.getInstance());
        lv.loadFile();
        for (int i = 0; i < lv.size(); ++i) {
            ServerInfo lv2 = lv.get(i);
            if (!lv2.name.equals(e.name) || !lv2.address.equals(e.address)) continue;
            lv.set(i, e);
            break;
        }
        lv.saveFile();
    }
}

