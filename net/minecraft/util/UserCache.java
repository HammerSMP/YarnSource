/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.ProfileLookupCallback
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserCache {
    private static final Logger field_25805 = LogManager.getLogger();
    private static boolean useRemote;
    private final Map<String, Entry> byName = Maps.newConcurrentMap();
    private final Map<UUID, Entry> byUuid = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = new GsonBuilder().create();
    private final File cacheFile;
    private final AtomicLong field_25724 = new AtomicLong();

    public UserCache(GameProfileRepository profileRepository, File cacheFile) {
        this.profileRepository = profileRepository;
        this.cacheFile = cacheFile;
        Lists.reverse(this.load()).forEach(this::method_30164);
    }

    private void method_30164(Entry arg) {
        UUID uUID;
        GameProfile gameProfile = arg.getProfile();
        arg.method_30171(this.method_30169());
        String string = gameProfile.getName();
        if (string != null) {
            this.byName.put(string.toLowerCase(Locale.ROOT), arg);
        }
        if ((uUID = gameProfile.getId()) != null) {
            this.byUuid.put(uUID, arg);
        }
    }

    @Nullable
    private static GameProfile findProfileByName(GameProfileRepository repository, String name) {
        final AtomicReference atomicReference = new AtomicReference();
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback(){

            public void onProfileLookupSucceeded(GameProfile profile) {
                atomicReference.set(profile);
            }

            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                atomicReference.set(null);
            }
        };
        repository.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profileLookupCallback);
        GameProfile gameProfile = (GameProfile)atomicReference.get();
        if (!UserCache.shouldUseRemote() && gameProfile == null) {
            UUID uUID = PlayerEntity.getUuidFromProfile(new GameProfile(null, name));
            gameProfile = new GameProfile(uUID, name);
        }
        return gameProfile;
    }

    public static void setUseRemote(boolean value) {
        useRemote = value;
    }

    private static boolean shouldUseRemote() {
        return useRemote;
    }

    public void add(GameProfile gameProfile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        Entry lv = new Entry(gameProfile, date);
        this.method_30164(lv);
        this.save();
    }

    private long method_30169() {
        return this.field_25724.incrementAndGet();
    }

    @Nullable
    public GameProfile findByName(String string) {
        GameProfile gameProfile2;
        String string2 = string.toLowerCase(Locale.ROOT);
        Entry lv = this.byName.get(string2);
        boolean bl = false;
        if (lv != null && new Date().getTime() >= lv.expirationDate.getTime()) {
            this.byUuid.remove(lv.getProfile().getId());
            this.byName.remove(lv.getProfile().getName().toLowerCase(Locale.ROOT));
            bl = true;
            lv = null;
        }
        if (lv != null) {
            lv.method_30171(this.method_30169());
            GameProfile gameProfile = lv.getProfile();
        } else {
            gameProfile2 = UserCache.findProfileByName(this.profileRepository, string2);
            if (gameProfile2 != null) {
                this.add(gameProfile2);
                bl = false;
            }
        }
        if (bl) {
            this.save();
        }
        return gameProfile2;
    }

    @Nullable
    public GameProfile getByUuid(UUID uUID) {
        Entry lv = this.byUuid.get(uUID);
        if (lv == null) {
            return null;
        }
        lv.method_30171(this.method_30169());
        return lv.getProfile();
    }

    private static DateFormat method_30170() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Entry> load() {
        ArrayList list = Lists.newArrayList();
        try (BufferedReader reader2 = Files.newReader((File)this.cacheFile, (Charset)StandardCharsets.UTF_8);){
            JsonArray jsonArray = (JsonArray)this.gson.fromJson((Reader)reader2, JsonArray.class);
            if (jsonArray == null) {
                ArrayList arrayList = list;
                return arrayList;
            }
            DateFormat dateFormat = UserCache.method_30170();
            jsonArray.forEach(jsonElement -> {
                Entry lv = UserCache.method_30167(jsonElement, dateFormat);
                if (lv != null) {
                    list.add(lv);
                }
            });
            return list;
        }
        catch (FileNotFoundException reader2) {
            return list;
        }
        catch (JsonParseException | IOException exception) {
            field_25805.warn("Failed to load profile cache {}", (Object)this.cacheFile, (Object)exception);
        }
        return list;
    }

    public void save() {
        JsonArray jsonArray = new JsonArray();
        DateFormat dateFormat = UserCache.method_30170();
        this.getLastAccessedEntries(1000).forEach(arg -> jsonArray.add(UserCache.method_30165(arg, dateFormat)));
        String string = this.gson.toJson((JsonElement)jsonArray);
        try (BufferedWriter writer = Files.newWriter((File)this.cacheFile, (Charset)StandardCharsets.UTF_8);){
            writer.write(string);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<Entry> getLastAccessedEntries(int i) {
        return ImmutableList.copyOf(this.byUuid.values()).stream().sorted(Comparator.comparing(Entry::method_30172).reversed()).limit(i);
    }

    private static JsonElement method_30165(Entry arg, DateFormat dateFormat) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", arg.getProfile().getName());
        UUID uUID = arg.getProfile().getId();
        jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
        jsonObject.addProperty("expiresOn", dateFormat.format(arg.getExpirationDate()));
        return jsonObject;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static Entry method_30167(JsonElement jsonElement, DateFormat dateFormat) {
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
                    date = dateFormat.parse(jsonElement4.getAsString());
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
            if (string2 == null || string == null || date == null) {
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

    static class Entry {
        private final GameProfile profile;
        private final Date expirationDate;
        private volatile long field_25726;

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

        public void method_30171(long l) {
            this.field_25726 = l;
        }

        public long method_30172() {
            return this.field_25726;
        }
    }
}

