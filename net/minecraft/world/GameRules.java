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
    private static final Map<RuleKey<?>, RuleType<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(arg -> RuleKey.method_20772(arg)));
    public static final RuleKey<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> MOB_GRIEFING = GameRules.register("mobGriefing", RuleCategory.MOBS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", RuleCategory.UPDATES, IntRule.method_20764(3));
    public static final RuleKey<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", RuleCategory.MISC, BooleanRule.method_20757(false, (minecraftServer, arg) -> {
        byte b = arg.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity lv : minecraftServer.getPlayerManager().getPlayerList()) {
            lv.networkHandler.sendPacket(new EntityStatusS2CPacket(lv, b));
        }
    }));
    public static final RuleKey<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", RuleCategory.PLAYER, IntRule.method_20764(10));
    public static final RuleKey<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", RuleCategory.MOBS, IntRule.method_20764(24));
    public static final RuleKey<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", RuleCategory.MISC, IntRule.method_20764(65536));
    public static final RuleKey<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", RuleCategory.MOBS, BooleanRule.method_20755(false));
    public static final RuleKey<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", RuleCategory.PLAYER, BooleanRule.method_20757(false, (minecraftServer, arg) -> {
        for (ServerPlayerEntity lv : minecraftServer.getPlayerManager().getPlayerList()) {
            lv.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, arg.get() ? 1.0f : 0.0f));
        }
    }));
    public static final RuleKey<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    private final Map<RuleKey<?>, Rule<?>> rules;

    private static <T extends Rule<T>> RuleKey<T> register(String string, RuleCategory arg, RuleType<T> arg2) {
        RuleKey lv = new RuleKey(string, arg);
        RuleType<T> lv2 = RULE_TYPES.put(lv, arg2);
        if (lv2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + string);
        }
        return lv;
    }

    public GameRules() {
        this.rules = (Map)RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((RuleType)entry.getValue()).createRule()));
    }

    @Environment(value=EnvType.CLIENT)
    private GameRules(Map<RuleKey<?>, Rule<?>> map) {
        this.rules = map;
    }

    public <T extends Rule<T>> T get(RuleKey<T> arg) {
        return (T)this.rules.get(arg);
    }

    public CompoundTag toNbt() {
        CompoundTag lv = new CompoundTag();
        this.rules.forEach((arg2, arg3) -> lv.putString(((RuleKey)arg2).name, arg3.serialize()));
        return lv;
    }

    public void load(CompoundTag arg) {
        this.rules.forEach((arg2, arg3) -> {
            if (arg.contains(((RuleKey)arg2).name)) {
                arg3.deserialize(arg.getString(((RuleKey)arg2).name));
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void forEachType(RuleTypeConsumer arg) {
        RULE_TYPES.forEach((arg2, arg3) -> GameRules.accept(arg, arg2, arg3));
    }

    private static <T extends Rule<T>> void accept(RuleTypeConsumer arg, RuleKey<?> arg2, RuleType<?> arg3) {
        RuleKey<?> lv = arg2;
        RuleType<?> lv2 = arg3;
        arg.accept(lv, lv2);
        lv2.method_27336(arg, lv);
    }

    public void setAllValues(GameRules arg, @Nullable MinecraftServer minecraftServer) {
        arg.rules.keySet().forEach(arg2 -> this.setValue((RuleKey)arg2, arg, minecraftServer));
    }

    private <T extends Rule<T>> void setValue(RuleKey<T> arg, GameRules arg2, @Nullable MinecraftServer minecraftServer) {
        T lv = arg2.get(arg);
        ((Rule)this.get(arg)).setValue(lv, minecraftServer);
    }

    public boolean getBoolean(RuleKey<BooleanRule> arg) {
        return this.get(arg).get();
    }

    public int getInt(RuleKey<IntRule> arg) {
        return this.get(arg).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static RuleType<BooleanRule> create(boolean bl, BiConsumer<MinecraftServer, BooleanRule> biConsumer) {
            return new RuleType<BooleanRule>(BoolArgumentType::bool, arg -> new BooleanRule((RuleType<BooleanRule>)arg, bl), biConsumer, RuleTypeConsumer::acceptBoolean);
        }

        private static RuleType<BooleanRule> create(boolean bl) {
            return BooleanRule.create(bl, (minecraftServer, arg) -> {});
        }

        public BooleanRule(RuleType<BooleanRule> arg, boolean bl) {
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
        @Environment(value=EnvType.CLIENT)
        protected BooleanRule copy() {
            return new BooleanRule(this.type, this.value);
        }

        @Override
        public void setValue(BooleanRule arg, @Nullable MinecraftServer minecraftServer) {
            this.value = arg.value;
            this.changed(minecraftServer);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20755(boolean bl) {
            return BooleanRule.create(bl);
        }

        static /* synthetic */ RuleType method_20757(boolean bl, BiConsumer biConsumer) {
            return BooleanRule.create(bl, biConsumer);
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static RuleType<IntRule> create(int i, BiConsumer<MinecraftServer, IntRule> biConsumer) {
            return new RuleType<IntRule>(IntegerArgumentType::integer, arg -> new IntRule((RuleType<IntRule>)arg, i), biConsumer, RuleTypeConsumer::acceptInt);
        }

        private static RuleType<IntRule> create(int i) {
            return IntRule.create(i, (minecraftServer, arg) -> {});
        }

        public IntRule(RuleType<IntRule> arg, int i) {
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
        @Environment(value=EnvType.CLIENT)
        protected IntRule copy() {
            return new IntRule(this.type, this.value);
        }

        @Override
        public void setValue(IntRule arg, @Nullable MinecraftServer minecraftServer) {
            this.value = arg.value;
            this.changed(minecraftServer);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20764(int i) {
            return IntRule.create(i);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        protected final RuleType<T> type;

        public Rule(RuleType<T> arg) {
            this.type = arg;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> commandContext, String string) {
            this.setFromArgument(commandContext, string);
            this.changed(((ServerCommandSource)commandContext.getSource()).getMinecraftServer());
        }

        protected void changed(@Nullable MinecraftServer minecraftServer) {
            if (minecraftServer != null) {
                ((RuleType)this.type).changeCallback.accept(minecraftServer, this.getThis());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        @Environment(value=EnvType.CLIENT)
        protected abstract T copy();

        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static class RuleType<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<RuleType<T>, T> ruleFactory;
        private final BiConsumer<MinecraftServer, T> changeCallback;
        private final RuleAcceptor<T> field_24104;

        private RuleType(Supplier<ArgumentType<?>> supplier, Function<RuleType<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer, RuleAcceptor<T> arg) {
            this.argumentType = supplier;
            this.ruleFactory = function;
            this.changeCallback = biConsumer;
            this.field_24104 = arg;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String string) {
            return CommandManager.argument(string, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void method_27336(RuleTypeConsumer arg, RuleKey<T> arg2) {
            this.field_24104.call(arg, arg2, this);
        }
    }

    public static final class RuleKey<T extends Rule<T>> {
        private final String name;
        private final RuleCategory category;

        public RuleKey(String string, RuleCategory arg) {
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
            return object instanceof RuleKey && ((RuleKey)object).name.equals(this.name);
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
        public RuleCategory getCategory() {
            return this.category;
        }
    }

    public static interface RuleTypeConsumer {
        default public <T extends Rule<T>> void accept(RuleKey<T> arg, RuleType<T> arg2) {
        }

        default public void acceptBoolean(RuleKey<BooleanRule> arg, RuleType<BooleanRule> arg2) {
        }

        default public void acceptInt(RuleKey<IntRule> arg, RuleType<IntRule> arg2) {
        }
    }

    static interface RuleAcceptor<T extends Rule<T>> {
        public void call(RuleTypeConsumer var1, RuleKey<T> var2, RuleType<T> var3);
    }

    public static enum RuleCategory {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String category;

        private RuleCategory(String string2) {
            this.category = string2;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCategory() {
            return this.category;
        }
    }
}

