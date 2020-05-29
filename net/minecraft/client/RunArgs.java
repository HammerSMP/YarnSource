/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.PropertyMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.resource.DirectResourceIndex;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.util.Session;

@Environment(value=EnvType.CLIENT)
public class RunArgs {
    public final Network network;
    public final WindowSettings windowSettings;
    public final Directories directories;
    public final Game game;
    public final AutoConnect autoConnect;

    public RunArgs(Network arg, WindowSettings arg2, Directories arg3, Game arg4, AutoConnect arg5) {
        this.network = arg;
        this.windowSettings = arg2;
        this.directories = arg3;
        this.game = arg4;
        this.autoConnect = arg5;
    }

    @Environment(value=EnvType.CLIENT)
    public static class AutoConnect {
        @Nullable
        public final String serverAddress;
        public final int serverPort;

        public AutoConnect(@Nullable String string, int i) {
            this.serverAddress = string;
            this.serverPort = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Directories {
        public final File runDir;
        public final File resourcePackDir;
        public final File assetDir;
        @Nullable
        public final String assetIndex;

        public Directories(File file, File file2, File file3, @Nullable String string) {
            this.runDir = file;
            this.resourcePackDir = file2;
            this.assetDir = file3;
            this.assetIndex = string;
        }

        public ResourceIndex getResourceIndex() {
            return this.assetIndex == null ? new DirectResourceIndex(this.assetDir) : new ResourceIndex(this.assetDir, this.assetIndex);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Network {
        public final Session session;
        public final PropertyMap field_3298;
        public final PropertyMap profileProperties;
        public final Proxy netProxy;

        public Network(Session arg, PropertyMap propertyMap, PropertyMap propertyMap2, Proxy proxy) {
            this.session = arg;
            this.field_3298 = propertyMap;
            this.profileProperties = propertyMap2;
            this.netProxy = proxy;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Game {
        public final boolean demo;
        public final String version;
        public final String versionType;
        public final boolean multiplayerDisabled;
        public final boolean onlineChatDisabled;

        public Game(boolean bl, String string, String string2, boolean bl2, boolean bl3) {
            this.demo = bl;
            this.version = string;
            this.versionType = string2;
            this.multiplayerDisabled = bl2;
            this.onlineChatDisabled = bl3;
        }
    }
}

