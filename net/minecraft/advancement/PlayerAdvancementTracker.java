/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.internal.Streams
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.advancement;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancementTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, (Object)new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, (Object)new Identifier.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>(){};
    private final DataFixer field_25324;
    private final PlayerManager field_25325;
    private final File advancementFile;
    private final Map<Advancement, AdvancementProgress> advancementToProgress = Maps.newLinkedHashMap();
    private final Set<Advancement> visibleAdvancements = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityUpdates = Sets.newLinkedHashSet();
    private final Set<Advancement> progressUpdates = Sets.newLinkedHashSet();
    private ServerPlayerEntity owner;
    @Nullable
    private Advancement currentDisplayTab;
    private boolean dirty = true;

    public PlayerAdvancementTracker(DataFixer dataFixer, PlayerManager arg, ServerAdvancementLoader arg2, File file, ServerPlayerEntity arg3) {
        this.field_25324 = dataFixer;
        this.field_25325 = arg;
        this.advancementFile = file;
        this.owner = arg3;
        this.load(arg2);
    }

    public void setOwner(ServerPlayerEntity arg) {
        this.owner = arg;
    }

    public void clearCriteria() {
        for (Criterion<?> lv : Criteria.getCriteria()) {
            lv.endTracking(this);
        }
    }

    public void reload(ServerAdvancementLoader arg) {
        this.clearCriteria();
        this.advancementToProgress.clear();
        this.visibleAdvancements.clear();
        this.visibilityUpdates.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.currentDisplayTab = null;
        this.load(arg);
    }

    private void beginTrackingAllAdvancements(ServerAdvancementLoader arg) {
        for (Advancement lv : arg.getAdvancements()) {
            this.beginTracking(lv);
        }
    }

    private void updateCompleted() {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
            if (!entry.getValue().isDone()) continue;
            list.add(entry.getKey());
            this.progressUpdates.add(entry.getKey());
        }
        for (Advancement lv : list) {
            this.updateDisplay(lv);
        }
    }

    private void rewardEmptyAdvancements(ServerAdvancementLoader arg) {
        for (Advancement lv : arg.getAdvancements()) {
            if (!lv.getCriteria().isEmpty()) continue;
            this.grantCriterion(lv, "");
            lv.getRewards().apply(this.owner);
        }
    }

    private void load(ServerAdvancementLoader arg) {
        if (this.advancementFile.isFile()) {
            try (JsonReader jsonReader = new JsonReader((Reader)new StringReader(Files.toString((File)this.advancementFile, (Charset)StandardCharsets.UTF_8)));){
                jsonReader.setLenient(false);
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Streams.parse((JsonReader)jsonReader));
                if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                    dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                }
                dynamic = this.field_25324.update(DataFixTypes.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                dynamic = dynamic.remove("DataVersion");
                Map map = (Map)GSON.getAdapter(JSON_TYPE).fromJsonTree((JsonElement)dynamic.getValue());
                if (map == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                Stream<Map.Entry> stream = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (Map.Entry entry : stream.collect(Collectors.toList())) {
                    Advancement lv = arg.get((Identifier)entry.getKey());
                    if (lv == null) {
                        LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), (Object)this.advancementFile);
                        continue;
                    }
                    this.initProgress(lv, (AdvancementProgress)entry.getValue());
                }
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.advancementFile, (Object)jsonParseException);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.advancementFile, (Object)iOException);
            }
        }
        this.rewardEmptyAdvancements(arg);
        this.updateCompleted();
        this.beginTrackingAllAdvancements(arg);
    }

    public void save() {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
            AdvancementProgress lv = entry.getValue();
            if (!lv.isAnyObtained()) continue;
            map.put(entry.getKey().getId(), lv);
        }
        if (this.advancementFile.getParentFile() != null) {
            this.advancementFile.getParentFile().mkdirs();
        }
        JsonElement jsonElement = GSON.toJsonTree((Object)map);
        jsonElement.getAsJsonObject().addProperty("DataVersion", (Number)SharedConstants.getGameVersion().getWorldVersion());
        try (FileOutputStream outputStream = new FileOutputStream(this.advancementFile);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)outputStream, Charsets.UTF_8.newEncoder());){
            GSON.toJson(jsonElement, (Appendable)writer);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.advancementFile, (Object)iOException);
        }
    }

    public boolean grantCriterion(Advancement arg, String string) {
        boolean bl = false;
        AdvancementProgress lv = this.getProgress(arg);
        boolean bl2 = lv.isDone();
        if (lv.obtain(string)) {
            this.endTrackingCompleted(arg);
            this.progressUpdates.add(arg);
            bl = true;
            if (!bl2 && lv.isDone()) {
                arg.getRewards().apply(this.owner);
                if (arg.getDisplay() != null && arg.getDisplay().shouldAnnounceToChat() && this.owner.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    this.field_25325.broadcastChatMessage(new TranslatableText("chat.type.advancement." + arg.getDisplay().getFrame().getId(), this.owner.getDisplayName(), arg.toHoverableText()), MessageType.SYSTEM, Util.field_25140);
                }
            }
        }
        if (lv.isDone()) {
            this.updateDisplay(arg);
        }
        return bl;
    }

    public boolean revokeCriterion(Advancement arg, String string) {
        boolean bl = false;
        AdvancementProgress lv = this.getProgress(arg);
        if (lv.reset(string)) {
            this.beginTracking(arg);
            this.progressUpdates.add(arg);
            bl = true;
        }
        if (!lv.isAnyObtained()) {
            this.updateDisplay(arg);
        }
        return bl;
    }

    private void beginTracking(Advancement arg) {
        AdvancementProgress lv = this.getProgress(arg);
        if (lv.isDone()) {
            return;
        }
        for (Map.Entry<String, AdvancementCriterion> entry : arg.getCriteria().entrySet()) {
            Criterion<CriterionConditions> lv4;
            CriterionConditions lv3;
            CriterionProgress lv2 = lv.getCriterionProgress(entry.getKey());
            if (lv2 == null || lv2.isObtained() || (lv3 = entry.getValue().getConditions()) == null || (lv4 = Criteria.getById(lv3.getId())) == null) continue;
            lv4.beginTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(lv3, arg, entry.getKey()));
        }
    }

    private void endTrackingCompleted(Advancement arg) {
        AdvancementProgress lv = this.getProgress(arg);
        for (Map.Entry<String, AdvancementCriterion> entry : arg.getCriteria().entrySet()) {
            Criterion<CriterionConditions> lv4;
            CriterionConditions lv3;
            CriterionProgress lv2 = lv.getCriterionProgress(entry.getKey());
            if (lv2 == null || !lv2.isObtained() && !lv.isDone() || (lv3 = entry.getValue().getConditions()) == null || (lv4 = Criteria.getById(lv3.getId())) == null) continue;
            lv4.endTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(lv3, arg, entry.getKey()));
        }
    }

    public void sendUpdate(ServerPlayerEntity arg) {
        if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
            HashMap map = Maps.newHashMap();
            LinkedHashSet set = Sets.newLinkedHashSet();
            LinkedHashSet set2 = Sets.newLinkedHashSet();
            for (Advancement lv : this.progressUpdates) {
                if (!this.visibleAdvancements.contains(lv)) continue;
                map.put(lv.getId(), this.advancementToProgress.get(lv));
            }
            for (Advancement lv2 : this.visibilityUpdates) {
                if (this.visibleAdvancements.contains(lv2)) {
                    set.add(lv2);
                    continue;
                }
                set2.add(lv2.getId());
            }
            if (this.dirty || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
                arg.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set, set2, map));
                this.visibilityUpdates.clear();
                this.progressUpdates.clear();
            }
        }
        this.dirty = false;
    }

    public void setDisplayTab(@Nullable Advancement arg) {
        Advancement lv = this.currentDisplayTab;
        this.currentDisplayTab = arg != null && arg.getParent() == null && arg.getDisplay() != null ? arg : null;
        if (lv != this.currentDisplayTab) {
            this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
        }
    }

    public AdvancementProgress getProgress(Advancement arg) {
        AdvancementProgress lv = this.advancementToProgress.get(arg);
        if (lv == null) {
            lv = new AdvancementProgress();
            this.initProgress(arg, lv);
        }
        return lv;
    }

    private void initProgress(Advancement arg, AdvancementProgress arg2) {
        arg2.init(arg.getCriteria(), arg.getRequirements());
        this.advancementToProgress.put(arg, arg2);
    }

    private void updateDisplay(Advancement arg) {
        boolean bl = this.canSee(arg);
        boolean bl2 = this.visibleAdvancements.contains(arg);
        if (bl && !bl2) {
            this.visibleAdvancements.add(arg);
            this.visibilityUpdates.add(arg);
            if (this.advancementToProgress.containsKey(arg)) {
                this.progressUpdates.add(arg);
            }
        } else if (!bl && bl2) {
            this.visibleAdvancements.remove(arg);
            this.visibilityUpdates.add(arg);
        }
        if (bl != bl2 && arg.getParent() != null) {
            this.updateDisplay(arg.getParent());
        }
        for (Advancement lv : arg.getChildren()) {
            this.updateDisplay(lv);
        }
    }

    private boolean canSee(Advancement arg) {
        for (int i = 0; arg != null && i <= 2; arg = arg.getParent(), ++i) {
            if (i == 0 && this.hasChildrenDone(arg)) {
                return true;
            }
            if (arg.getDisplay() == null) {
                return false;
            }
            AdvancementProgress lv = this.getProgress(arg);
            if (lv.isDone()) {
                return true;
            }
            if (!arg.getDisplay().isHidden()) continue;
            return false;
        }
        return false;
    }

    private boolean hasChildrenDone(Advancement arg) {
        AdvancementProgress lv = this.getProgress(arg);
        if (lv.isDone()) {
            return true;
        }
        for (Advancement lv2 : arg.getChildren()) {
            if (!this.hasChildrenDone(lv2)) continue;
            return true;
        }
        return false;
    }
}

