/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class ServerMetadata {
    private Text description;
    private Players players;
    private Version version;
    private String favicon;

    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text arg) {
        this.description = arg;
    }

    public Players getPlayers() {
        return this.players;
    }

    public void setPlayers(Players arg) {
        this.players = arg;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version arg) {
        this.version = arg;
    }

    public void setFavicon(String string) {
        this.favicon = string;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class Deserializer
    implements JsonDeserializer<ServerMetadata>,
    JsonSerializer<ServerMetadata> {
        public ServerMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "status");
            ServerMetadata lv = new ServerMetadata();
            if (jsonObject.has("description")) {
                lv.setDescription((Text)jsonDeserializationContext.deserialize(jsonObject.get("description"), Text.class));
            }
            if (jsonObject.has("players")) {
                lv.setPlayers((Players)jsonDeserializationContext.deserialize(jsonObject.get("players"), Players.class));
            }
            if (jsonObject.has("version")) {
                lv.setVersion((Version)jsonDeserializationContext.deserialize(jsonObject.get("version"), Version.class));
            }
            if (jsonObject.has("favicon")) {
                lv.setFavicon(JsonHelper.getString(jsonObject, "favicon"));
            }
            return lv;
        }

        public JsonElement serialize(ServerMetadata arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (arg.getDescription() != null) {
                jsonObject.add("description", jsonSerializationContext.serialize((Object)arg.getDescription()));
            }
            if (arg.getPlayers() != null) {
                jsonObject.add("players", jsonSerializationContext.serialize((Object)arg.getPlayers()));
            }
            if (arg.getVersion() != null) {
                jsonObject.add("version", jsonSerializationContext.serialize((Object)arg.getVersion()));
            }
            if (arg.getFavicon() != null) {
                jsonObject.addProperty("favicon", arg.getFavicon());
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ServerMetadata)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Version {
        private final String gameVersion;
        private final int protocolVersion;

        public Version(String string, int i) {
            this.gameVersion = string;
            this.protocolVersion = i;
        }

        public String getGameVersion() {
            return this.gameVersion;
        }

        public int getProtocolVersion() {
            return this.protocolVersion;
        }

        public static class Serializer
        implements JsonDeserializer<Version>,
        JsonSerializer<Version> {
            public Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "version");
                return new Version(JsonHelper.getString(jsonObject, "name"), JsonHelper.getInt(jsonObject, "protocol"));
            }

            public JsonElement serialize(Version arg, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", arg.getGameVersion());
                jsonObject.addProperty("protocol", (Number)arg.getProtocolVersion());
                return jsonObject;
            }

            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((Version)object, type, jsonSerializationContext);
            }

            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.deserialize(jsonElement, type, jsonDeserializationContext);
            }
        }
    }

    public static class Players {
        private final int max;
        private final int online;
        private GameProfile[] sample;

        public Players(int i, int j) {
            this.max = i;
            this.online = j;
        }

        public int getPlayerLimit() {
            return this.max;
        }

        public int getOnlinePlayerCount() {
            return this.online;
        }

        public GameProfile[] getSample() {
            return this.sample;
        }

        public void setSample(GameProfile[] gameProfiles) {
            this.sample = gameProfiles;
        }

        public static class Deserializer
        implements JsonDeserializer<Players>,
        JsonSerializer<Players> {
            public Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonArray jsonArray;
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "players");
                Players lv = new Players(JsonHelper.getInt(jsonObject, "max"), JsonHelper.getInt(jsonObject, "online"));
                if (JsonHelper.hasArray(jsonObject, "sample") && (jsonArray = JsonHelper.getArray(jsonObject, "sample")).size() > 0) {
                    GameProfile[] gameProfiles = new GameProfile[jsonArray.size()];
                    for (int i = 0; i < gameProfiles.length; ++i) {
                        JsonObject jsonObject2 = JsonHelper.asObject(jsonArray.get(i), "player[" + i + "]");
                        String string = JsonHelper.getString(jsonObject2, "id");
                        gameProfiles[i] = new GameProfile(UUID.fromString(string), JsonHelper.getString(jsonObject2, "name"));
                    }
                    lv.setSample(gameProfiles);
                }
                return lv;
            }

            public JsonElement serialize(Players arg, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("max", (Number)arg.getPlayerLimit());
                jsonObject.addProperty("online", (Number)arg.getOnlinePlayerCount());
                if (arg.getSample() != null && arg.getSample().length > 0) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < arg.getSample().length; ++i) {
                        JsonObject jsonObject2 = new JsonObject();
                        UUID uUID = arg.getSample()[i].getId();
                        jsonObject2.addProperty("id", uUID == null ? "" : uUID.toString());
                        jsonObject2.addProperty("name", arg.getSample()[i].getName());
                        jsonArray.add((JsonElement)jsonObject2);
                    }
                    jsonObject.add("sample", (JsonElement)jsonArray);
                }
                return jsonObject;
            }

            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((Players)object, type, jsonSerializationContext);
            }

            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.deserialize(jsonElement, type, jsonDeserializationContext);
            }
        }
    }
}

