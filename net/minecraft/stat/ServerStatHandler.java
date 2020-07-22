/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.internal.Streams
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DataFixer
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.stat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatHandler
extends StatHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> pendingStats = Sets.newHashSet();
    private int lastStatsUpdate = -300;

    public ServerStatHandler(MinecraftServer server, File file) {
        this.server = server;
        this.file = file;
        if (file.isFile()) {
            try {
                this.parse(server.getDataFixer(), FileUtils.readFileToString((File)file));
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't read statistics file {}", (Object)file, (Object)iOException);
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse statistics file {}", (Object)file, (Object)jsonParseException);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile((File)this.file, (String)this.asString());
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save stats", (Throwable)iOException);
        }
    }

    @Override
    public void setStat(PlayerEntity player, Stat<?> stat, int value) {
        super.setStat(player, stat, value);
        this.pendingStats.add(stat);
    }

    private Set<Stat<?>> takePendingStats() {
        HashSet set = Sets.newHashSet(this.pendingStats);
        this.pendingStats.clear();
        return set;
    }

    public void parse(DataFixer dataFixer, String json) {
        try (JsonReader jsonReader = new JsonReader((Reader)new StringReader(json));){
            jsonReader.setLenient(false);
            JsonElement jsonElement = Streams.parse((JsonReader)jsonReader);
            if (jsonElement.isJsonNull()) {
                LOGGER.error("Unable to parse Stat data from {}", (Object)this.file);
                return;
            }
            CompoundTag lv = ServerStatHandler.jsonToCompound(jsonElement.getAsJsonObject());
            if (!lv.contains("DataVersion", 99)) {
                lv.putInt("DataVersion", 1343);
            }
            if ((lv = NbtHelper.update(dataFixer, DataFixTypes.STATS, lv, lv.getInt("DataVersion"))).contains("stats", 10)) {
                CompoundTag lv2 = lv.getCompound("stats");
                for (String string2 : lv2.getKeys()) {
                    if (!lv2.contains(string2, 10)) continue;
                    Util.ifPresentOrElse(Registry.STAT_TYPE.getOrEmpty(new Identifier(string2)), arg22 -> {
                        CompoundTag lv = lv2.getCompound(string2);
                        for (String string2 : lv.getKeys()) {
                            if (lv.contains(string2, 99)) {
                                Util.ifPresentOrElse(this.createStat((StatType)arg22, string2), arg2 -> this.statMap.put(arg2, lv.getInt(string2)), () -> LOGGER.warn("Invalid statistic in {}: Don't know what {} is", (Object)this.file, (Object)string2));
                                continue;
                            }
                            LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", (Object)this.file, (Object)lv.get(string2), (Object)string2);
                        }
                    }, () -> LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", (Object)this.file, (Object)string2));
                }
            }
        }
        catch (JsonParseException | IOException exception) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.file, (Object)exception);
        }
    }

    private <T> Optional<Stat<T>> createStat(StatType<T> type, String id) {
        return Optional.ofNullable(Identifier.tryParse(id)).flatMap(type.getRegistry()::getOrEmpty).map(type::getOrCreateStat);
    }

    private static CompoundTag jsonToCompound(JsonObject jsonObject) {
        CompoundTag lv = new CompoundTag();
        for (Map.Entry entry : jsonObject.entrySet()) {
            JsonPrimitive jsonPrimitive;
            JsonElement jsonElement = (JsonElement)entry.getValue();
            if (jsonElement.isJsonObject()) {
                lv.put((String)entry.getKey(), ServerStatHandler.jsonToCompound(jsonElement.getAsJsonObject()));
                continue;
            }
            if (!jsonElement.isJsonPrimitive() || !(jsonPrimitive = jsonElement.getAsJsonPrimitive()).isNumber()) continue;
            lv.putInt((String)entry.getKey(), jsonPrimitive.getAsInt());
        }
        return lv;
    }

    protected String asString() {
        HashMap map = Maps.newHashMap();
        for (Object entry : this.statMap.object2IntEntrySet()) {
            Stat stat = (Stat)entry.getKey();
            map.computeIfAbsent(stat.getType(), arg -> new JsonObject()).addProperty(ServerStatHandler.getStatId(stat).toString(), (Number)entry.getIntValue());
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry entry : map.entrySet()) {
            jsonObject.add(Registry.STAT_TYPE.getId((StatType<?>)entry.getKey()).toString(), (JsonElement)entry.getValue());
        }
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("stats", (JsonElement)jsonObject);
        jsonObject2.addProperty("DataVersion", (Number)SharedConstants.getGameVersion().getWorldVersion());
        return jsonObject2.toString();
    }

    private static <T> Identifier getStatId(Stat<T> arg) {
        return arg.getType().getRegistry().getId(arg.getValue());
    }

    public void updateStatSet() {
        this.pendingStats.addAll((Collection<Stat<?>>)this.statMap.keySet());
    }

    public void sendStats(ServerPlayerEntity player) {
        int i = this.server.getTicks();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        if (i - this.lastStatsUpdate > 300) {
            this.lastStatsUpdate = i;
            for (Stat<?> lv : this.takePendingStats()) {
                object2IntMap.put(lv, this.getStat(lv));
            }
        }
        player.networkHandler.sendPacket(new StatisticsS2CPacket((Object2IntMap<Stat<?>>)object2IntMap));
    }
}

