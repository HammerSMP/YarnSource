/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
    public Text version = new LiteralText(SharedConstants.getGameVersion().getName());
    public boolean online;
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackState resourcePackState = ResourcePackState.PROMPT;
    @Nullable
    private String icon;
    private boolean local;

    public ServerInfo(String string, String string2, boolean bl) {
        this.name = string;
        this.address = string2;
        this.local = bl;
    }

    public CompoundTag serialize() {
        CompoundTag lv = new CompoundTag();
        lv.putString("name", this.name);
        lv.putString("ip", this.address);
        if (this.icon != null) {
            lv.putString("icon", this.icon);
        }
        if (this.resourcePackState == ResourcePackState.ENABLED) {
            lv.putBoolean("acceptTextures", true);
        } else if (this.resourcePackState == ResourcePackState.DISABLED) {
            lv.putBoolean("acceptTextures", false);
        }
        return lv;
    }

    public ResourcePackState getResourcePack() {
        return this.resourcePackState;
    }

    public void setResourcePackState(ResourcePackState arg) {
        this.resourcePackState = arg;
    }

    public static ServerInfo deserialize(CompoundTag arg) {
        ServerInfo lv = new ServerInfo(arg.getString("name"), arg.getString("ip"), false);
        if (arg.contains("icon", 8)) {
            lv.setIcon(arg.getString("icon"));
        }
        if (arg.contains("acceptTextures", 1)) {
            if (arg.getBoolean("acceptTextures")) {
                lv.setResourcePackState(ResourcePackState.ENABLED);
            } else {
                lv.setResourcePackState(ResourcePackState.DISABLED);
            }
        } else {
            lv.setResourcePackState(ResourcePackState.PROMPT);
        }
        return lv;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(@Nullable String string) {
        this.icon = string;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void copyFrom(ServerInfo arg) {
        this.address = arg.address;
        this.name = arg.name;
        this.setResourcePackState(arg.getResourcePack());
        this.icon = arg.icon;
        this.local = arg.local;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ResourcePackState {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Text name;

        private ResourcePackState(String string2) {
            this.name = new TranslatableText("addServer.resourcePack." + string2);
        }

        public Text getName() {
            return this.name;
        }
    }
}

