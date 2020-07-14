/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
    private final Advancement parent;
    private final AdvancementDisplay display;
    private final AdvancementRewards rewards;
    private final Identifier id;
    private final Map<String, AdvancementCriterion> criteria;
    private final String[][] requirements;
    private final Set<Advancement> children = Sets.newLinkedHashSet();
    private final Text text;

    public Advancement(Identifier id, @Nullable Advancement parent, @Nullable AdvancementDisplay display, AdvancementRewards rewards, Map<String, AdvancementCriterion> criteria, String[][] requirements) {
        this.id = id;
        this.display = display;
        this.criteria = ImmutableMap.copyOf(criteria);
        this.parent = parent;
        this.rewards = rewards;
        this.requirements = requirements;
        if (parent != null) {
            parent.addChild(this);
        }
        if (display == null) {
            this.text = new LiteralText(id.toString());
        } else {
            Text lv = display.getTitle();
            Formatting lv2 = display.getFrame().getTitleFormat();
            MutableText lv3 = Texts.setStyleIfAbsent(lv.shallowCopy(), Style.EMPTY.withColor(lv2)).append("\n").append(display.getDescription());
            MutableText lv4 = lv.shallowCopy().styled(arg2 -> arg2.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, lv3)));
            this.text = Texts.bracketed(lv4).formatted(lv2);
        }
    }

    public Task createTask() {
        return new Task(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
    }

    @Nullable
    public Advancement getParent() {
        return this.parent;
    }

    @Nullable
    public AdvancementDisplay getDisplay() {
        return this.display;
    }

    public AdvancementRewards getRewards() {
        return this.rewards;
    }

    public String toString() {
        return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + '}';
    }

    public Iterable<Advancement> getChildren() {
        return this.children;
    }

    public Map<String, AdvancementCriterion> getCriteria() {
        return this.criteria;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRequirementCount() {
        return this.requirements.length;
    }

    public void addChild(Advancement child) {
        this.children.add(child);
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Advancement)) {
            return false;
        }
        Advancement lv = (Advancement)o;
        return this.id.equals(lv.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String[][] getRequirements() {
        return this.requirements;
    }

    public Text toHoverableText() {
        return this.text;
    }

    public static class Task {
        private Identifier parentId;
        private Advancement parentObj;
        private AdvancementDisplay display;
        private AdvancementRewards rewards = AdvancementRewards.NONE;
        private Map<String, AdvancementCriterion> criteria = Maps.newLinkedHashMap();
        private String[][] requirements;
        private CriterionMerger merger = CriterionMerger.AND;

        private Task(@Nullable Identifier parentId, @Nullable AdvancementDisplay display, AdvancementRewards rewards, Map<String, AdvancementCriterion> criteria, String[][] requirements) {
            this.parentId = parentId;
            this.display = display;
            this.rewards = rewards;
            this.criteria = criteria;
            this.requirements = requirements;
        }

        private Task() {
        }

        public static Task create() {
            return new Task();
        }

        public Task parent(Advancement parent) {
            this.parentObj = parent;
            return this;
        }

        public Task parent(Identifier parentId) {
            this.parentId = parentId;
            return this;
        }

        public Task display(ItemStack icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
            return this.display(new AdvancementDisplay(icon, title, description, background, frame, showToast, announceToChat, hidden));
        }

        public Task display(ItemConvertible icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
            return this.display(new AdvancementDisplay(new ItemStack(icon.asItem()), title, description, background, frame, showToast, announceToChat, hidden));
        }

        public Task display(AdvancementDisplay display) {
            this.display = display;
            return this;
        }

        public Task rewards(AdvancementRewards.Builder arg) {
            return this.rewards(arg.build());
        }

        public Task rewards(AdvancementRewards rewards) {
            this.rewards = rewards;
            return this;
        }

        public Task criterion(String name, CriterionConditions arg) {
            return this.criterion(name, new AdvancementCriterion(arg));
        }

        public Task criterion(String name, AdvancementCriterion arg) {
            if (this.criteria.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate criterion " + name);
            }
            this.criteria.put(name, arg);
            return this;
        }

        public Task criteriaMerger(CriterionMerger merger) {
            this.merger = merger;
            return this;
        }

        public boolean findParent(Function<Identifier, Advancement> parentProvider) {
            if (this.parentId == null) {
                return true;
            }
            if (this.parentObj == null) {
                this.parentObj = parentProvider.apply(this.parentId);
            }
            return this.parentObj != null;
        }

        public Advancement build(Identifier id) {
            if (!this.findParent(arg -> null)) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            }
            if (this.requirements == null) {
                this.requirements = this.merger.createRequirements(this.criteria.keySet());
            }
            return new Advancement(id, this.parentObj, this.display, this.rewards, this.criteria, this.requirements);
        }

        public Advancement build(Consumer<Advancement> consumer, String string) {
            Advancement lv = this.build(new Identifier(string));
            consumer.accept(lv);
            return lv;
        }

        public JsonObject toJson() {
            if (this.requirements == null) {
                this.requirements = this.merger.createRequirements(this.criteria.keySet());
            }
            JsonObject jsonObject = new JsonObject();
            if (this.parentObj != null) {
                jsonObject.addProperty("parent", this.parentObj.getId().toString());
            } else if (this.parentId != null) {
                jsonObject.addProperty("parent", this.parentId.toString());
            }
            if (this.display != null) {
                jsonObject.add("display", this.display.toJson());
            }
            jsonObject.add("rewards", this.rewards.toJson());
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry<String, AdvancementCriterion> entry : this.criteria.entrySet()) {
                jsonObject2.add(entry.getKey(), entry.getValue().toJson());
            }
            jsonObject.add("criteria", (JsonElement)jsonObject2);
            JsonArray jsonArray = new JsonArray();
            for (String[] strings : this.requirements) {
                JsonArray jsonArray2 = new JsonArray();
                for (String string : strings) {
                    jsonArray2.add(string);
                }
                jsonArray.add((JsonElement)jsonArray2);
            }
            jsonObject.add("requirements", (JsonElement)jsonArray);
            return jsonObject;
        }

        public void toPacket(PacketByteBuf buf) {
            if (this.parentId == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeIdentifier(this.parentId);
            }
            if (this.display == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                this.display.toPacket(buf);
            }
            AdvancementCriterion.criteriaToPacket(this.criteria, buf);
            buf.writeVarInt(this.requirements.length);
            for (String[] strings : this.requirements) {
                buf.writeVarInt(strings.length);
                for (String string : strings) {
                    buf.writeString(string);
                }
            }
        }

        public String toString() {
            return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + '}';
        }

        public static Task fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            Identifier lv = obj.has("parent") ? new Identifier(JsonHelper.getString(obj, "parent")) : null;
            AdvancementDisplay lv2 = obj.has("display") ? AdvancementDisplay.fromJson(JsonHelper.getObject(obj, "display")) : null;
            AdvancementRewards lv3 = obj.has("rewards") ? AdvancementRewards.fromJson(JsonHelper.getObject(obj, "rewards")) : AdvancementRewards.NONE;
            Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromJson(JsonHelper.getObject(obj, "criteria"), predicateDeserializer);
            if (map.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            }
            JsonArray jsonArray = JsonHelper.getArray(obj, "requirements", new JsonArray());
            String[][] strings = new String[jsonArray.size()][];
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonArray jsonArray2 = JsonHelper.asArray(jsonArray.get(i), "requirements[" + i + "]");
                strings[i] = new String[jsonArray2.size()];
                for (int j = 0; j < jsonArray2.size(); ++j) {
                    strings[i][j] = JsonHelper.asString(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
                }
            }
            if (strings.length == 0) {
                strings = new String[map.size()][];
                int k = 0;
                for (String string : map.keySet()) {
                    strings[k++] = new String[]{string};
                }
            }
            for (String[] strings2 : strings) {
                if (strings2.length == 0 && map.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }
                String[] arrstring = strings2;
                int n = arrstring.length;
                for (int i = 0; i < n; ++i) {
                    String string2 = arrstring[i];
                    if (map.containsKey(string2)) continue;
                    throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
                }
            }
            for (String string3 : map.keySet()) {
                boolean bl = false;
                for (Object[] arrobject : strings) {
                    if (!ArrayUtils.contains((Object[])arrobject, (Object)string3)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                throw new JsonSyntaxException("Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
            }
            return new Task(lv, lv2, lv3, map, strings);
        }

        public static Task fromPacket(PacketByteBuf buf) {
            Identifier lv = buf.readBoolean() ? buf.readIdentifier() : null;
            AdvancementDisplay lv2 = buf.readBoolean() ? AdvancementDisplay.fromPacket(buf) : null;
            Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromPacket(buf);
            String[][] strings = new String[buf.readVarInt()][];
            for (int i = 0; i < strings.length; ++i) {
                strings[i] = new String[buf.readVarInt()];
                for (int j = 0; j < strings[i].length; ++j) {
                    strings[i][j] = buf.readString(32767);
                }
            }
            return new Task(lv, lv2, AdvancementRewards.NONE, map, strings);
        }

        public Map<String, AdvancementCriterion> getCriteria() {
            return this.criteria;
        }
    }
}

