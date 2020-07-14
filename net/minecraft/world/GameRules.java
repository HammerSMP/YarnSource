/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.serialization.DynamicLike
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Key<?>, Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> Key.method_20772(key)));
    public static final Key<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_MOB_GRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", Category.UPDATES, IntRule.method_20764(3));
    public static final Key<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanRule.method_20757(false, (server, rule) -> {
        byte b = rule.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity lv : server.getPlayerManager().getPlayerList()) {
            lv.networkHandler.sendPacket(new EntityStatusS2CPacket(lv, b));
        }
    }));
    public static final Key<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntRule.method_20764(10));
    public static final Key<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntRule.method_20764(24));
    public static final Key<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntRule.method_20764(65536));
    public static final Key<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanRule.method_20755(false));
    public static final Key<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanRule.method_20757(false, (server, rule) -> {
        for (ServerPlayerEntity lv : server.getPlayerManager().getPlayerList()) {
            lv.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanRule.method_20755(false));
    private final Map<Key<?>, Rule<?>> rules;

    private static <T extends Rule<T>> Key<T> register(String name, Category category, Type<T> type) {
        Key lv = new Key(name, category);
        Type<T> lv2 = RULE_TYPES.put(lv, type);
        if (lv2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return lv;
    }

    public GameRules(DynamicLike<?> dynamicLike) {
        this();
        this.load(dynamicLike);
    }

    public GameRules() {
        this.rules = (Map)RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ((Type)e.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Rule<?>> rules) {
        this.rules = rules;
    }

    public <T extends Rule<T>> T get(Key<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundTag toNbt() {
        CompoundTag lv = new CompoundTag();
        this.rules.forEach((key, rule) -> lv.putString(((Key)key).name, rule.serialize()));
        return lv;
    }

    private void load(DynamicLike<?> dynamicLike) {
        this.rules.forEach((key, rule) -> dynamicLike.get(((Key)key).name).asString().result().ifPresent(rule::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void accept(Visitor visitor) {
        RULE_TYPES.forEach((key, type) -> GameRules.accept(visitor, key, type));
    }

    private static <T extends Rule<T>> void accept(Visitor consumer, Key<?> key, Type<?> type) {
        Key<?> lv = key;
        Type<?> lv2 = type;
        consumer.visit(lv, lv2);
        lv2.accept(consumer, lv);
    }

    @Environment(value=EnvType.CLIENT)
    public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
        rules.rules.keySet().forEach(key -> this.setValue((Key)key, rules, server));
    }

    @Environment(value=EnvType.CLIENT)
    private <T extends Rule<T>> void setValue(Key<T> key, GameRules rules, @Nullable MinecraftServer server) {
        T lv = rules.get(key);
        ((Rule)this.get(key)).setValue(lv, server);
    }

    public boolean getBoolean(Key<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(Key<IntRule> rule) {
        return this.get(rule).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static Type<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback) {
            return new Type<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((Type<BooleanRule>)type, initialValue), changeCallback, Visitor::visitBoolean);
        }

        private static Type<BooleanRule> create(boolean initialValue) {
            return BooleanRule.create(initialValue, (server, rule) -> {});
        }

        public BooleanRule(Type<BooleanRule> type, boolean initialValue) {
            super(type);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = BoolArgumentType.getBool(context, (String)name);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value, @Nullable MinecraftServer server) {
            this.value = value;
            this.changed(server);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = Boolean.parseBoolean(value);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanRule getThis() {
            return this;
        }

        @Override
        protected BooleanRule copy() {
            return new BooleanRule(this.type, this.value);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void setValue(BooleanRule arg, @Nullable MinecraftServer minecraftServer) {
            this.value = arg.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ Type method_20755(boolean bl) {
            return BooleanRule.create(bl);
        }

        static /* synthetic */ Type method_20757(boolean bl, BiConsumer biConsumer) {
            return BooleanRule.create(bl, biConsumer);
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static Type<IntRule> create(int initialValue, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new Type<IntRule>(IntegerArgumentType::integer, type -> new IntRule((Type<IntRule>)type, initialValue), changeCallback, Visitor::visitInt);
        }

        private static Type<IntRule> create(int initialValue) {
            return IntRule.create(initialValue, (server, rule) -> {});
        }

        public IntRule(Type<IntRule> rule, int initialValue) {
            super(rule);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = IntegerArgumentType.getInteger(context, (String)name);
        }

        public int get() {
            return this.value;
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = IntRule.parseInt(value);
        }

        @Environment(value=EnvType.CLIENT)
        public boolean validate(String input) {
            try {
                this.value = Integer.parseInt(input);
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int parseInt(String input) {
            if (!input.isEmpty()) {
                try {
                    return Integer.parseInt(input);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)input);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntRule getThis() {
            return this;
        }

        @Override
        protected IntRule copy() {
            return new IntRule(this.type, this.value);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void setValue(IntRule arg, @Nullable MinecraftServer minecraftServer) {
            this.value = arg.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ Type method_20764(int i) {
            return IntRule.create(i);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        protected final Type<T> type;

        public Rule(Type<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.changed(((ServerCommandSource)context.getSource()).getMinecraftServer());
        }

        protected void changed(@Nullable MinecraftServer server) {
            if (server != null) {
                ((Type)this.type).changeCallback.accept(server, this.getThis());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        protected abstract T copy();

        @Environment(value=EnvType.CLIENT)
        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static class Type<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<Type<T>, T> ruleFactory;
        private final BiConsumer<MinecraftServer, T> changeCallback;
        private final Acceptor<T> ruleAcceptor;

        private Type(Supplier<ArgumentType<?>> argumentType, Function<Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, Acceptor<T> ruleAcceptor) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.ruleAcceptor = ruleAcceptor;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void accept(Visitor consumer, Key<T> key) {
            this.ruleAcceptor.call(consumer, key, this);
        }
    }

    public static final class Key<T extends Rule<T>> {
        private final String name;
        private final Category category;

        public Key(String name, Category category) {
            this.name = name;
            this.category = category;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof Key && ((Key)obj).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }

        public String getTranslationKey() {
            return "gamerule." + this.name;
        }

        @Environment(value=EnvType.CLIENT)
        public Category getCategory() {
            return this.category;
        }
    }

    public static interface Visitor {
        default public <T extends Rule<T>> void visit(Key<T> key, Type<T> type) {
        }

        default public void visitBoolean(Key<BooleanRule> key, Type<BooleanRule> type) {
        }

        default public void visitInt(Key<IntRule> key, Type<IntRule> type) {
        }
    }

    static interface Acceptor<T extends Rule<T>> {
        public void call(Visitor var1, Key<T> var2, Type<T> var3);
    }

    public static enum Category {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String category;

        private Category(String category) {
            this.category = category;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCategory() {
            return this.category;
        }
    }
}

