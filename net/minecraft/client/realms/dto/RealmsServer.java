/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServerPing;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.realms.util.RealmsUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServer
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsWorldOptions> slots;
    public boolean expired;
    public boolean expiredTrial;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public String minigameImage;
    public RealmsServerPing serverPing = new RealmsServerPing();

    public String getDescription() {
        return this.motd;
    }

    public String getName() {
        return this.name;
    }

    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.motd = description;
    }

    public void updateServerPing(RealmsServerPlayerList serverPlayerList) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (String string : serverPlayerList.players) {
            if (string.equals(MinecraftClient.getInstance().getSession().getUuid())) continue;
            String string2 = "";
            try {
                string2 = RealmsUtil.uuidToName(string);
            }
            catch (Exception exception) {
                LOGGER.error("Could not get name for " + string, (Throwable)exception);
                continue;
            }
            list.add(string2);
            ++i;
        }
        this.serverPing.nrOfPlayers = String.valueOf(i);
        this.serverPing.playerList = Joiner.on((char)'\n').join((Iterable)list);
    }

    public static RealmsServer parse(JsonObject node) {
        RealmsServer lv = new RealmsServer();
        try {
            lv.id = JsonUtils.getLongOr("id", node, -1L);
            lv.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
            lv.name = JsonUtils.getStringOr("name", node, null);
            lv.motd = JsonUtils.getStringOr("motd", node, null);
            lv.state = RealmsServer.getState(JsonUtils.getStringOr("state", node, State.CLOSED.name()));
            lv.owner = JsonUtils.getStringOr("owner", node, null);
            if (node.get("players") != null && node.get("players").isJsonArray()) {
                lv.players = RealmsServer.parseInvited(node.get("players").getAsJsonArray());
                RealmsServer.sortInvited(lv);
            } else {
                lv.players = Lists.newArrayList();
            }
            lv.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
            lv.expired = JsonUtils.getBooleanOr("expired", node, false);
            lv.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", node, false);
            lv.worldType = RealmsServer.getWorldType(JsonUtils.getStringOr("worldType", node, WorldType.NORMAL.name()));
            lv.ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
            lv.slots = node.get("slots") != null && node.get("slots").isJsonArray() ? RealmsServer.parseSlots(node.get("slots").getAsJsonArray()) : RealmsServer.getEmptySlots();
            lv.minigameName = JsonUtils.getStringOr("minigameName", node, null);
            lv.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
            lv.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
            lv.minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: " + exception.getMessage());
        }
        return lv;
    }

    private static void sortInvited(RealmsServer server) {
        server.players.sort((arg, arg2) -> ComparisonChain.start().compareFalseFirst(arg2.getAccepted(), arg.getAccepted()).compare((Comparable)((Object)arg.getName().toLowerCase(Locale.ROOT)), (Comparable)((Object)arg2.getName().toLowerCase(Locale.ROOT))).result());
    }

    private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
        ArrayList list = Lists.newArrayList();
        for (JsonElement jsonElement : jsonArray) {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                PlayerInfo lv = new PlayerInfo();
                lv.setName(JsonUtils.getStringOr("name", jsonObject, null));
                lv.setUuid(JsonUtils.getStringOr("uuid", jsonObject, null));
                lv.setOperator(JsonUtils.getBooleanOr("operator", jsonObject, false));
                lv.setAccepted(JsonUtils.getBooleanOr("accepted", jsonObject, false));
                lv.setOnline(JsonUtils.getBooleanOr("online", jsonObject, false));
                list.add(lv);
            }
            catch (Exception exception) {}
        }
        return list;
    }

    private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray json) {
        HashMap map = Maps.newHashMap();
        for (JsonElement jsonElement : json) {
            try {
                RealmsWorldOptions lv2;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement2 = jsonParser.parse(jsonObject.get("options").getAsString());
                if (jsonElement2 == null) {
                    RealmsWorldOptions lv = RealmsWorldOptions.getDefaults();
                } else {
                    lv2 = RealmsWorldOptions.parse(jsonElement2.getAsJsonObject());
                }
                int i = JsonUtils.getIntOr("slotId", jsonObject, -1);
                map.put(i, lv2);
            }
            catch (Exception exception) {}
        }
        for (int j = 1; j <= 3; ++j) {
            if (map.containsKey(j)) continue;
            map.put(j, RealmsWorldOptions.getEmptyDefaults());
        }
        return map;
    }

    private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
        HashMap map = Maps.newHashMap();
        map.put(1, RealmsWorldOptions.getEmptyDefaults());
        map.put(2, RealmsWorldOptions.getEmptyDefaults());
        map.put(3, RealmsWorldOptions.getEmptyDefaults());
        return map;
    }

    public static RealmsServer parse(String json) {
        try {
            return RealmsServer.parse(new JsonParser().parse(json).getAsJsonObject());
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: " + exception.getMessage());
            return new RealmsServer();
        }
    }

    private static State getState(String state) {
        try {
            return State.valueOf(state);
        }
        catch (Exception exception) {
            return State.CLOSED;
        }
    }

    private static WorldType getWorldType(String state) {
        try {
            return WorldType.valueOf(state);
        }
        catch (Exception exception) {
            return WorldType.NORMAL;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer lv = (RealmsServer)obj;
        return new EqualsBuilder().append(this.id, lv.id).append((Object)this.name, (Object)lv.name).append((Object)this.motd, (Object)lv.motd).append((Object)this.state, (Object)lv.state).append((Object)this.owner, (Object)lv.owner).append(this.expired, lv.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer lv = new RealmsServer();
        lv.id = this.id;
        lv.remoteSubscriptionId = this.remoteSubscriptionId;
        lv.name = this.name;
        lv.motd = this.motd;
        lv.state = this.state;
        lv.owner = this.owner;
        lv.players = this.players;
        lv.slots = this.cloneSlots(this.slots);
        lv.expired = this.expired;
        lv.expiredTrial = this.expiredTrial;
        lv.daysLeft = this.daysLeft;
        lv.serverPing = new RealmsServerPing();
        lv.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        lv.serverPing.playerList = this.serverPing.playerList;
        lv.worldType = this.worldType;
        lv.ownerUUID = this.ownerUUID;
        lv.minigameName = this.minigameName;
        lv.activeSlot = this.activeSlot;
        lv.minigameId = this.minigameId;
        lv.minigameImage = this.minigameImage;
        return lv;
    }

    public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> slots) {
        HashMap map2 = Maps.newHashMap();
        for (Map.Entry<Integer, RealmsWorldOptions> entry : slots.entrySet()) {
            map2.put(entry.getKey(), entry.getValue().clone());
        }
        return map2;
    }

    public String getWorldName(int slotId) {
        return this.name + " (" + this.slots.get(slotId).getSlotName(slotId) + ")";
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WorldType {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        CLOSED,
        OPEN,
        UNINITIALIZED;

    }

    @Environment(value=EnvType.CLIENT)
    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String owner) {
            this.refOwner = owner;
        }

        @Override
        public int compare(RealmsServer arg, RealmsServer arg2) {
            return ComparisonChain.start().compareTrueFirst(arg.state == State.UNINITIALIZED, arg2.state == State.UNINITIALIZED).compareTrueFirst(arg.expiredTrial, arg2.expiredTrial).compareTrueFirst(arg.owner.equals(this.refOwner), arg2.owner.equals(this.refOwner)).compareFalseFirst(arg.expired, arg2.expired).compareTrueFirst(arg.state == State.OPEN, arg2.state == State.OPEN).compare(arg.id, arg2.id).result();
        }

        @Override
        public /* synthetic */ int compare(Object one, Object two) {
            return this.compare((RealmsServer)one, (RealmsServer)two);
        }
    }
}

