/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.ColorArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.EntitySummonArgumentType;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.ItemEnchantmentArgumentType;
import net.minecraft.command.arguments.ItemPredicateArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NbtTagArgumentType;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.command.arguments.OperationArgumentType;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import net.minecraft.command.arguments.SwizzleArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import net.minecraft.command.arguments.TestClassArgumentType;
import net.minecraft.command.arguments.TestFunctionArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.command.arguments.UuidArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<?>, Entry<?>> classMap = Maps.newHashMap();
    private static final Map<Identifier, Entry<?>> idMap = Maps.newHashMap();

    public static <T extends ArgumentType<?>> void register(String id, Class<T> class_, ArgumentSerializer<T> arg) {
        Identifier lv = new Identifier(id);
        if (classMap.containsKey(class_)) {
            throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
        }
        if (idMap.containsKey(lv)) {
            throw new IllegalArgumentException("'" + lv + "' is already a registered serializer!");
        }
        Entry lv2 = new Entry(class_, arg, lv);
        classMap.put(class_, lv2);
        idMap.put(lv, lv2);
    }

    public static void register() {
        BrigadierArgumentTypes.register();
        ArgumentTypes.register("entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
        ArgumentTypes.register("game_profile", GameProfileArgumentType.class, new ConstantArgumentSerializer<GameProfileArgumentType>(GameProfileArgumentType::gameProfile));
        ArgumentTypes.register("block_pos", BlockPosArgumentType.class, new ConstantArgumentSerializer<BlockPosArgumentType>(BlockPosArgumentType::blockPos));
        ArgumentTypes.register("column_pos", ColumnPosArgumentType.class, new ConstantArgumentSerializer<ColumnPosArgumentType>(ColumnPosArgumentType::columnPos));
        ArgumentTypes.register("vec3", Vec3ArgumentType.class, new ConstantArgumentSerializer<Vec3ArgumentType>(Vec3ArgumentType::vec3));
        ArgumentTypes.register("vec2", Vec2ArgumentType.class, new ConstantArgumentSerializer<Vec2ArgumentType>(Vec2ArgumentType::vec2));
        ArgumentTypes.register("block_state", BlockStateArgumentType.class, new ConstantArgumentSerializer<BlockStateArgumentType>(BlockStateArgumentType::blockState));
        ArgumentTypes.register("block_predicate", BlockPredicateArgumentType.class, new ConstantArgumentSerializer<BlockPredicateArgumentType>(BlockPredicateArgumentType::blockPredicate));
        ArgumentTypes.register("item_stack", ItemStackArgumentType.class, new ConstantArgumentSerializer<ItemStackArgumentType>(ItemStackArgumentType::itemStack));
        ArgumentTypes.register("item_predicate", ItemPredicateArgumentType.class, new ConstantArgumentSerializer<ItemPredicateArgumentType>(ItemPredicateArgumentType::itemPredicate));
        ArgumentTypes.register("color", ColorArgumentType.class, new ConstantArgumentSerializer<ColorArgumentType>(ColorArgumentType::color));
        ArgumentTypes.register("component", TextArgumentType.class, new ConstantArgumentSerializer<TextArgumentType>(TextArgumentType::text));
        ArgumentTypes.register("message", MessageArgumentType.class, new ConstantArgumentSerializer<MessageArgumentType>(MessageArgumentType::message));
        ArgumentTypes.register("nbt_compound_tag", NbtCompoundTagArgumentType.class, new ConstantArgumentSerializer<NbtCompoundTagArgumentType>(NbtCompoundTagArgumentType::nbtCompound));
        ArgumentTypes.register("nbt_tag", NbtTagArgumentType.class, new ConstantArgumentSerializer<NbtTagArgumentType>(NbtTagArgumentType::nbtTag));
        ArgumentTypes.register("nbt_path", NbtPathArgumentType.class, new ConstantArgumentSerializer<NbtPathArgumentType>(NbtPathArgumentType::nbtPath));
        ArgumentTypes.register("objective", ObjectiveArgumentType.class, new ConstantArgumentSerializer<ObjectiveArgumentType>(ObjectiveArgumentType::objective));
        ArgumentTypes.register("objective_criteria", ObjectiveCriteriaArgumentType.class, new ConstantArgumentSerializer<ObjectiveCriteriaArgumentType>(ObjectiveCriteriaArgumentType::objectiveCriteria));
        ArgumentTypes.register("operation", OperationArgumentType.class, new ConstantArgumentSerializer<OperationArgumentType>(OperationArgumentType::operation));
        ArgumentTypes.register("particle", ParticleArgumentType.class, new ConstantArgumentSerializer<ParticleArgumentType>(ParticleArgumentType::particle));
        ArgumentTypes.register("rotation", RotationArgumentType.class, new ConstantArgumentSerializer<RotationArgumentType>(RotationArgumentType::rotation));
        ArgumentTypes.register("scoreboard_slot", ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer<ScoreboardSlotArgumentType>(ScoreboardSlotArgumentType::scoreboardSlot));
        ArgumentTypes.register("score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
        ArgumentTypes.register("swizzle", SwizzleArgumentType.class, new ConstantArgumentSerializer<SwizzleArgumentType>(SwizzleArgumentType::swizzle));
        ArgumentTypes.register("team", TeamArgumentType.class, new ConstantArgumentSerializer<TeamArgumentType>(TeamArgumentType::team));
        ArgumentTypes.register("item_slot", ItemSlotArgumentType.class, new ConstantArgumentSerializer<ItemSlotArgumentType>(ItemSlotArgumentType::itemSlot));
        ArgumentTypes.register("resource_location", IdentifierArgumentType.class, new ConstantArgumentSerializer<IdentifierArgumentType>(IdentifierArgumentType::identifier));
        ArgumentTypes.register("mob_effect", MobEffectArgumentType.class, new ConstantArgumentSerializer<MobEffectArgumentType>(MobEffectArgumentType::mobEffect));
        ArgumentTypes.register("function", FunctionArgumentType.class, new ConstantArgumentSerializer<FunctionArgumentType>(FunctionArgumentType::function));
        ArgumentTypes.register("entity_anchor", EntityAnchorArgumentType.class, new ConstantArgumentSerializer<EntityAnchorArgumentType>(EntityAnchorArgumentType::entityAnchor));
        ArgumentTypes.register("int_range", NumberRangeArgumentType.IntRangeArgumentType.class, new NumberRangeArgumentType.IntRangeArgumentType.Serializer());
        ArgumentTypes.register("float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, new NumberRangeArgumentType.FloatRangeArgumentType.Serializer());
        ArgumentTypes.register("item_enchantment", ItemEnchantmentArgumentType.class, new ConstantArgumentSerializer<ItemEnchantmentArgumentType>(ItemEnchantmentArgumentType::itemEnchantment));
        ArgumentTypes.register("entity_summon", EntitySummonArgumentType.class, new ConstantArgumentSerializer<EntitySummonArgumentType>(EntitySummonArgumentType::entitySummon));
        ArgumentTypes.register("dimension", DimensionArgumentType.class, new ConstantArgumentSerializer<DimensionArgumentType>(DimensionArgumentType::dimension));
        ArgumentTypes.register("time", TimeArgumentType.class, new ConstantArgumentSerializer<TimeArgumentType>(TimeArgumentType::time));
        ArgumentTypes.register("uuid", UuidArgumentType.class, new ConstantArgumentSerializer<UuidArgumentType>(UuidArgumentType::uuid));
        if (SharedConstants.isDevelopment) {
            ArgumentTypes.register("test_argument", TestFunctionArgumentType.class, new ConstantArgumentSerializer<TestFunctionArgumentType>(TestFunctionArgumentType::testFunction));
            ArgumentTypes.register("test_class", TestClassArgumentType.class, new ConstantArgumentSerializer<TestClassArgumentType>(TestClassArgumentType::testClass));
        }
    }

    @Nullable
    private static Entry<?> byId(Identifier arg) {
        return idMap.get(arg);
    }

    @Nullable
    private static Entry<?> byClass(ArgumentType<?> argumentType) {
        return classMap.get(argumentType.getClass());
    }

    public static <T extends ArgumentType<?>> void toPacket(PacketByteBuf arg, T argumentType) {
        Entry<?> lv = ArgumentTypes.byClass(argumentType);
        if (lv == null) {
            LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", argumentType, argumentType.getClass());
            arg.writeIdentifier(new Identifier(""));
            return;
        }
        arg.writeIdentifier(lv.id);
        lv.serializer.toPacket(argumentType, arg);
    }

    @Nullable
    public static ArgumentType<?> fromPacket(PacketByteBuf arg) {
        Identifier lv = arg.readIdentifier();
        Entry<?> lv2 = ArgumentTypes.byId(lv);
        if (lv2 == null) {
            LOGGER.error("Could not deserialize {}", (Object)lv);
            return null;
        }
        return lv2.serializer.fromPacket(arg);
    }

    private static <T extends ArgumentType<?>> void toJson(JsonObject jsonObject, T argumentType) {
        Entry<?> lv = ArgumentTypes.byClass(argumentType);
        if (lv == null) {
            LOGGER.error("Could not serialize argument {} ({})!", argumentType, argumentType.getClass());
            jsonObject.addProperty("type", "unknown");
        } else {
            jsonObject.addProperty("type", "argument");
            jsonObject.addProperty("parser", lv.id.toString());
            JsonObject jsonObject2 = new JsonObject();
            lv.serializer.toJson(argumentType, jsonObject2);
            if (jsonObject2.size() > 0) {
                jsonObject.add("properties", (JsonElement)jsonObject2);
            }
        }
    }

    public static <S> JsonObject toJson(CommandDispatcher<S> commandDispatcher, CommandNode<S> commandNode) {
        Collection collection;
        JsonObject jsonObject = new JsonObject();
        if (commandNode instanceof RootCommandNode) {
            jsonObject.addProperty("type", "root");
        } else if (commandNode instanceof LiteralCommandNode) {
            jsonObject.addProperty("type", "literal");
        } else if (commandNode instanceof ArgumentCommandNode) {
            ArgumentTypes.toJson(jsonObject, ((ArgumentCommandNode)commandNode).getType());
        } else {
            LOGGER.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
            jsonObject.addProperty("type", "unknown");
        }
        JsonObject jsonObject2 = new JsonObject();
        for (CommandNode commandNode2 : commandNode.getChildren()) {
            jsonObject2.add(commandNode2.getName(), (JsonElement)ArgumentTypes.toJson(commandDispatcher, commandNode2));
        }
        if (jsonObject2.size() > 0) {
            jsonObject.add("children", (JsonElement)jsonObject2);
        }
        if (commandNode.getCommand() != null) {
            jsonObject.addProperty("executable", Boolean.valueOf(true));
        }
        if (commandNode.getRedirect() != null && !(collection = commandDispatcher.getPath(commandNode.getRedirect())).isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (String string : collection) {
                jsonArray.add(string);
            }
            jsonObject.add("redirect", (JsonElement)jsonArray);
        }
        return jsonObject;
    }

    static class Entry<T extends ArgumentType<?>> {
        public final Class<T> argClass;
        public final ArgumentSerializer<T> serializer;
        public final Identifier id;

        private Entry(Class<T> argumentClass, ArgumentSerializer<T> serializer, Identifier id) {
            this.argClass = argumentClass;
            this.serializer = serializer;
            this.id = id;
        }
    }
}

