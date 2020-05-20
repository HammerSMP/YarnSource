/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LootContext {
    private final Random random;
    private final float luck;
    private final ServerWorld world;
    private final Function<Identifier, LootTable> tableGetter;
    private final Set<LootTable> activeTables = Sets.newLinkedHashSet();
    private final Function<Identifier, LootCondition> conditionGetter;
    private final Set<LootCondition> conditions = Sets.newLinkedHashSet();
    private final Map<LootContextParameter<?>, Object> parameters;
    private final Map<Identifier, Dropper> drops;

    private LootContext(Random random, float f, ServerWorld arg, Function<Identifier, LootTable> function, Function<Identifier, LootCondition> function2, Map<LootContextParameter<?>, Object> map, Map<Identifier, Dropper> map2) {
        this.random = random;
        this.luck = f;
        this.world = arg;
        this.tableGetter = function;
        this.conditionGetter = function2;
        this.parameters = ImmutableMap.copyOf(map);
        this.drops = ImmutableMap.copyOf(map2);
    }

    public boolean hasParameter(LootContextParameter<?> arg) {
        return this.parameters.containsKey(arg);
    }

    public void drop(Identifier arg, Consumer<ItemStack> consumer) {
        Dropper lv = this.drops.get(arg);
        if (lv != null) {
            lv.add(this, consumer);
        }
    }

    @Nullable
    public <T> T get(LootContextParameter<T> arg) {
        return (T)this.parameters.get(arg);
    }

    public boolean markActive(LootTable arg) {
        return this.activeTables.add(arg);
    }

    public void markInactive(LootTable arg) {
        this.activeTables.remove(arg);
    }

    public boolean addCondition(LootCondition arg) {
        return this.conditions.add(arg);
    }

    public void removeCondition(LootCondition arg) {
        this.conditions.remove(arg);
    }

    public LootTable getSupplier(Identifier arg) {
        return this.tableGetter.apply(arg);
    }

    public LootCondition getCondition(Identifier arg) {
        return this.conditionGetter.apply(arg);
    }

    public Random getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.luck;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public static enum EntityTarget {
        THIS("this", LootContextParameters.THIS_ENTITY),
        KILLER("killer", LootContextParameters.KILLER_ENTITY),
        DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        private final String type;
        private final LootContextParameter<? extends Entity> parameter;

        private EntityTarget(String string2, LootContextParameter<? extends Entity> arg) {
            this.type = string2;
            this.parameter = arg;
        }

        public LootContextParameter<? extends Entity> getParameter() {
            return this.parameter;
        }

        public static EntityTarget fromString(String string) {
            for (EntityTarget lv : EntityTarget.values()) {
                if (!lv.type.equals(string)) continue;
                return lv;
            }
            throw new IllegalArgumentException("Invalid entity target " + string);
        }

        public static class Serializer
        extends TypeAdapter<EntityTarget> {
            public void write(JsonWriter jsonWriter, EntityTarget arg) throws IOException {
                jsonWriter.value(arg.type);
            }

            public EntityTarget read(JsonReader jsonReader) throws IOException {
                return EntityTarget.fromString(jsonReader.nextString());
            }

            public /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                return this.read(jsonReader);
            }

            public /* synthetic */ void write(JsonWriter jsonWriter, Object object) throws IOException {
                this.write(jsonWriter, (EntityTarget)((Object)object));
            }
        }
    }

    public static class Builder {
        private final ServerWorld world;
        private final Map<LootContextParameter<?>, Object> parameters = Maps.newIdentityHashMap();
        private final Map<Identifier, Dropper> drops = Maps.newHashMap();
        private Random random;
        private float luck;

        public Builder(ServerWorld arg) {
            this.world = arg;
        }

        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        public Builder random(long l) {
            if (l != 0L) {
                this.random = new Random(l);
            }
            return this;
        }

        public Builder random(long l, Random random) {
            this.random = l == 0L ? random : new Random(l);
            return this;
        }

        public Builder luck(float f) {
            this.luck = f;
            return this;
        }

        public <T> Builder parameter(LootContextParameter<T> arg, T object) {
            this.parameters.put(arg, object);
            return this;
        }

        public <T> Builder optionalParameter(LootContextParameter<T> arg, @Nullable T object) {
            if (object == null) {
                this.parameters.remove(arg);
            } else {
                this.parameters.put(arg, object);
            }
            return this;
        }

        public Builder putDrop(Identifier arg, Dropper arg2) {
            Dropper lv = this.drops.put(arg, arg2);
            if (lv != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
            }
            return this;
        }

        public ServerWorld getWorld() {
            return this.world;
        }

        public <T> T get(LootContextParameter<T> arg) {
            Object object = this.parameters.get(arg);
            if (object == null) {
                throw new IllegalArgumentException("No parameter " + arg);
            }
            return (T)object;
        }

        @Nullable
        public <T> T getNullable(LootContextParameter<T> arg) {
            return (T)this.parameters.get(arg);
        }

        public LootContext build(LootContextType arg) {
            Sets.SetView set = Sets.difference(this.parameters.keySet(), arg.getAllowed());
            if (!set.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + (Object)set);
            }
            Sets.SetView set2 = Sets.difference(arg.getRequired(), this.parameters.keySet());
            if (!set2.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + (Object)set2);
            }
            Random random = this.random;
            if (random == null) {
                random = new Random();
            }
            MinecraftServer minecraftServer = this.world.getServer();
            return new LootContext(random, this.luck, this.world, minecraftServer.getLootManager()::getTable, minecraftServer.getPredicateManager()::get, this.parameters, this.drops);
        }
    }

    @FunctionalInterface
    public static interface Dropper {
        public void add(LootContext var1, Consumer<ItemStack> var2);
    }
}

