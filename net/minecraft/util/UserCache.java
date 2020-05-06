/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.ProfileLookupCallback
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;

public class UserCache {
    public static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static boolean useRemote;
    private final Map<String, Entry> byName = Maps.newHashMap();
    private final Map<UUID, Entry> byUuid = Maps.newHashMap();
    private final Deque<GameProfile> byAccessTime = Lists.newLinkedList();
    private final GameProfileRepository profileRepository;
    protected final Gson gson;
    private final File cacheFile;
    private static final TypeToken<List<Entry>> ENTRY_LIST_TYPE;

    public UserCache(GameProfileRepository gameProfileRepository, File file) {
        this.profileRepository = gameProfileRepository;
        this.cacheFile = file;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Entry.class, (Object)new JsonConverter());
        this.gson = gsonBuilder.create();
        this.load();
    }

    private static GameProfile findProfileByName(GameProfileRepository gameProfileRepository, String string) {
        final GameProfile[] gameProfiles = new GameProfile[1];
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile gameProfile) {
                gameProfiles[0] = gameProfile;
            }

            public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                gameProfiles[0] = null;
            }
        };
        gameProfileRepository.findProfilesByNames(new String[]{string}, Agent.MINECRAFT, profileLookupCallback);
        if (!UserCache.shouldUseRemote() && gameProfiles[0] == null) {
            UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, string));
            GameProfile gameProfile = new GameProfile(uUID, string);
            profileLookupCallback.onProfileLookupSucceeded(gameProfile);
        }
        return gameProfiles[0];
    }

    public static void setUseRemote(boolean bl) {
        useRemote = bl;
    }

    private static boolean shouldUseRemote() {
        return useRemote;
    }

    public void add(GameProfile gameProfile) {
        this.add(gameProfile, null);
    }

    private void add(GameProfile gameProfile, Date date) {
        UUID uUID = gameProfile.getId();
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(2, 1);
            date = calendar.getTime();
        }
        Entry lv = new Entry(gameProfile, date);
        if (this.byUuid.containsKey(uUID)) {
            Entry lv2 = this.byUuid.get(uUID);
            this.byName.remove(lv2.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove((Object)gameProfile);
        }
        this.byName.put(gameProfile.getName().toLowerCase(Locale.ROOT), lv);
        this.byUuid.put(uUID, lv);
        this.byAccessTime.addFirst(gameProfile);
        this.save();
    }

    @Nullable
    public GameProfile findByName(String string) {
        String string2 = string.toLowerCase(Locale.ROOT);
        Entry lv = this.byName.get(string2);
        if (lv != null && new Date().getTime() >= lv.expirationDate.getTime()) {
            this.byUuid.remove(lv.getProfile().getId());
            this.byName.remove(lv.getProfile().getName().toLowerCase(Locale.ROOT));
            this.byAccessTime.remove((Object)lv.getProfile());
            lv = null;
        }
        if (lv != null) {
            GameProfile gameProfile = lv.getProfile();
            this.byAccessTime.remove((Object)gameProfile);
            this.byAccessTime.addFirst(gameProfile);
        } else {
            GameProfile gameProfile2 = UserCache.findProfileByName(this.profileRepository, string2);
            if (gameProfile2 != null) {
                this.add(gameProfile2);
                lv = this.byName.get(string2);
            }
        }
        this.save();
        return lv == null ? null : lv.getProfile();
    }

    @Nullable
    public GameProfile getByUuid(UUID uUID) {
        Entry lv = this.byUuid.get(uUID);
        return lv == null ? null : lv.getProfile();
    }

    private Entry getEntry(UUID uUID) {
        Entry lv = this.byUuid.get(uUID);
        if (lv != null) {
            GameProfile gameProfile = lv.getProfile();
            this.byAccessTime.remove((Object)gameProfile);
            this.byAccessTime.addFirst(gameProfile);
        }
        return lv;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        BufferedReader bufferedReader;
        block5: {
            bufferedReader = null;
            try {
                bufferedReader = Files.newReader((File)this.cacheFile, (Charset)StandardCharsets.UTF_8);
                List<Entry> list = JsonHelper.deserialize(this.gson, (Reader)bufferedReader, ENTRY_LIST_TYPE);
                this.byName.clear();
                this.byUuid.clear();
                this.byAccessTime.clear();
                if (list == null) break block5;
                for (Entry lv : Lists.reverse(list)) {
                    if (lv == null) continue;
                    this.add(lv.getProfile(), lv.getExpirationDate());
                }
            }
            catch (FileNotFoundException fileNotFoundException) {
                IOUtils.closeQuietly(bufferedReader);
            }
            catch (JsonParseException jsonParseException) {
                IOUtils.closeQuietly(bufferedReader);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedReader);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((Reader)bufferedReader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() {
        String string = this.gson.toJson(this.getLastAccessedEntries(1000));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newWriter((File)this.cacheFile, (Charset)StandardCharsets.UTF_8);
            bufferedWriter.write(string);
        }
        catch (FileNotFoundException fileNotFoundException) {
            IOUtils.closeQuietly(bufferedWriter);
            return;
        }
        catch (IOException iOException) {
            IOUtils.closeQuietly(bufferedWriter);
            return;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedWriter);
            throw throwable;
        }
        IOUtils.closeQuietly((Writer)bufferedWriter);
    }

    private List<Entry> getLastAccessedEntries(int i) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList((Iterator)Iterators.limit(this.byAccessTime.iterator(), (int)i));
        for (GameProfile gameProfile : list2) {
            Entry lv = this.getEntry(gameProfile.getId());
            if (lv == null) continue;
            list.add(lv);
        }
        return list;
    }

    static {
        ENTRY_LIST_TYPE = new TypeToken<List<Entry>>(){};
    }

    class Entry {
        private final GameProfile profile;
        private final Date expirationDate;

        private Entry(GameProfile gameProfile, Date date) {
            this.profile = gameProfile;
            this.expirationDate = date;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }

    class JsonConverter
    implements JsonDeserializer<Entry>,
    JsonSerializer<Entry> {
        private JsonConverter() {
        }

        public JsonElement serialize(Entry arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", arg.getProfile().getName());
            UUID uUID = arg.getProfile().getId();
            jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
            jsonObject.addProperty("expiresOn", EXPIRATION_DATE_FORMAT.format(arg.getExpirationDate()));
            return jsonObject;
        }

        /*
         * WARNING - void declaration
         */
        public Entry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                void uUID2;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement jsonElement2 = jsonObject.get("name");
                JsonElement jsonElement3 = jsonObject.get("uuid");
                JsonElement jsonElement4 = jsonObject.get("expiresOn");
                if (jsonElement2 == null || jsonElement3 == null) {
                    return null;
                }
                String string = jsonElement3.getAsString();
                String string2 = jsonElement2.getAsString();
                Date date = null;
                if (jsonElement4 != null) {
                    try {
                        date = EXPIRATION_DATE_FORMAT.parse(jsonElement4.getAsString());
                    }
                    catch (ParseException parseException) {
                        date = null;
                    }
                }
                if (string2 == null || string == null) {
                    return null;
                }
                try {
                    UUID uUID = UUID.fromString(string);
                }
                catch (Throwable throwable) {
                    return null;
                }
                return new Entry(new GameProfile((UUID)uUID2, string2), date);
            }
            return null;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Entry)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

