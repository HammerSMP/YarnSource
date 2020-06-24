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
    private static final Map<Key<?>, Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(arg -> Key.method_20772(arg)));
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
    public static final Key<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanRule.method_20757(false, (minecraftServer, arg) -> {
        byte b = arg.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity lv : minecraftServer.getPlayerManager().getPlayerList()) {
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
    public static final Key<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanRule.method_20757(false, (minecraftServer, arg) -> {
        for (ServerPlayerEntity lv : minecraftServer.getPlayerManager().getPlayerList()) {
            lv.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, arg.get() ? 1.0f : 0.0f));
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

    private static <T extends Rule<T>> Key<T> register(String string, Category arg, Type<T> arg2) {
        Key lv = new Key(string, arg);
        Type<T> lv2 = RULE_TYPES.put(lv, arg2);
        if (lv2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + string);
        }
        return lv;
    }

    public GameRules(DynamicLike<?> dynamicLike) {
        this();
        this.load(dynamicLike);
    }

    public GameRules() {
        this.rules = (Map)RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Type)entry.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Rule<?>> map) {
        this.rules = map;
    }

    public <T extends Rule<T>> T get(Key<T> arg) {
        return (T)this.rules.get(arg);
    }

    public CompoundTag toNbt() {
        CompoundTag lv = new CompoundTag();
        this.rules.forEach((arg2, arg3) -> lv.putString(((Key)arg2).name, arg3.serialize()));
        return lv;
    }

    private void load(DynamicLike<?> dynamicLike) {
        this.rules.forEach((arg, arg2) -> dynamicLike.get(((Key)arg).name).asString().result().ifPresent(arg2::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void forEachType(TypeConsumer arg) {
        RULE_TYPES.forEach((arg2, arg3) -> GameRules.accept(arg, arg2, arg3));
    }

    private static <T extends Rule<T>> void accept(TypeConsumer arg, Key<?> arg2, Type<?> arg3) {
        Key<?> lv = arg2;
        Type<?> lv2 = arg3;
        arg.accept(lv, lv2);
        lv2.accept(arg, lv);
    }

    @Environment(value=EnvType.CLIENT)
    public void setAllValues(GameRules arg, @Nullable MinecraftServer minecraftServer) {
        arg.rules.keySet().forEach(arg2 -> this.setValue((Key)arg2, arg, minecraftServer));
    }

    @Environment(value=EnvType.CLIENT)
    private <T extends Rule<T>> void setValue(Key<T> arg, GameRules arg2, @Nullable MinecraftServer minecraftServer) {
        T lv = arg2.get(arg);
        ((Rule)this.get(arg)).setValue(lv, minecraftServer);
    }

    public boolean getBoolean(Key<BooleanRule> arg) {
        return this.get(arg).get();
    }

    public int getInt(Key<IntRule> arg) {
        return this.get(arg).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static Type<BooleanRule> create(boolean bl, BiConsumer<MinecraftServer, BooleanRule> biConsumer) {
            return new Type<BooleanRule>(BoolArgumentType::bool, arg -> new BooleanRule((Type<BooleanRule>)arg, bl), biConsumer, TypeConsumer::acceptBoolean);
        }

        private static Type<BooleanRule> create(boolean bl) {
            return BooleanRule.create(bl, (minecraftServer, arg) -> {});
        }

        public BooleanRule(Type<BooleanRule> arg, boolean bl) {
            super(arg);
            this.value = bl;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
            this.value = BoolArgumentType.getBool(commandContext, (String)string);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean bl, @Nullable MinecraftServer minecraftServer) {
            this.value = bl;
            this.changed(minecraftServer);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String string) {
            this.value = Boolean.parseBoolean(string);
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

        private static Type<IntRule> create(int i, BiConsumer<MinecraftServer, IntRule> biConsumer) {
            return new Type<IntRule>(IntegerArgumentType::integer, arg -> new IntRule((Type<IntRule>)arg, i), biConsumer, TypeConsumer::acceptInt);
        }

        private static Type<IntRule> create(int i) {
            return IntRule.create(i, (minecraftServer, arg) -> {});
        }

        public IntRule(Type<IntRule> arg, int i) {
            super(arg);
            this.value = i;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
            this.value = IntegerArgumentType.getInteger(commandContext, (String)string);
        }

        public int get() {
            return this.value;
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String string) {
            this.value = IntRule.parseInt(string);
        }

        @Environment(value=EnvType.CLIENT)
        public boolean validate(String string) {
            try {
                this.value = Integer.parseInt(string);
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int parseInt(String string) {
            if (!string.isEmpty()) {
                try {
                    return Integer.parseInt(string);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)string);
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

        public Rule(Type<T> arg) {
            this.type = arg;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> commandContext, String string) {
            this.setFromArgument(commandContext, string);
            this.changed(((ServerCommandSource)commandContext.getSource()).getMinecraftServer());
        }

        protected void changed(@Nullable MinecraftServer minecraftServer) {
            if (minecraftServer != null) {
                ((Type)this.type).changeCallback.accept(minecraftServer, this.getThis());
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

        private Type(Supplier<ArgumentType<?>> supplier, Function<Type<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer, Acceptor<T> arg) {
            this.argumentType = supplier;
            this.ruleFactory = function;
            this.changeCallback = biConsumer;
            this.ruleAcceptor = arg;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String string) {
            return CommandManager.argument(string, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void accept(TypeConsumer arg, Key<T> arg2) {
            this.ruleAcceptor.call(arg, arg2, this);
        }
    }

    public static final class Key<T extends Rule<T>> {
        private final String name;
        private final Category category;

        public Key(String string, Category arg) {
            this.name = string;
            this.category = arg;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            return object instanceof Key && ((Key)object).name.equals(this.name);
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

    public static interface TypeConsumer {
        default public <T extends Rule<T>> void accept(Key<T> arg, Type<T> arg2) {
        }

        default public void acceptBoolean(Key<BooleanRule> arg, Type<BooleanRule> arg2) {
        }

        default public void acceptInt(Key<IntRule> arg, Type<IntRule> arg2) {
        }
    }

    static interface Acceptor<T extends Rule<T>> {
        public void call(TypeConsumer var1, Key<T> var2, Type<T> var3);
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

        private Category(String string2) {
            this.category = string2;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCategory() {
            return this.category;
        }
    }
}

