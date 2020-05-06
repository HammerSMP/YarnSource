/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.SwizzleArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class ExecuteCommand {
    private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.execute.blocks.toobig", object, object2));
    private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.execute.conditional.fail_count", object));
    private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
        resultConsumer.onCommandComplete(commandContext, bl, i);
        resultConsumer2.onCommandComplete(commandContext, bl, i);
    };
    private static final SuggestionProvider<ServerCommandSource> LOOT_CONDITIONS = (commandContext, suggestionsBuilder) -> {
        LootConditionManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPredicateManager();
        return CommandSource.suggestIdentifiers(lv.getIds(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("execute").requires(arg -> arg.hasPermissionLevel(2)));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("execute").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("run").redirect((CommandNode)commandDispatcher.getRoot()))).then(ExecuteCommand.addConditionArguments((CommandNode<ServerCommandSource>)literalCommandNode, CommandManager.literal("if"), true))).then(ExecuteCommand.addConditionArguments((CommandNode<ServerCommandSource>)literalCommandNode, CommandManager.literal("unless"), false))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withEntity(entity));
            }
            return list;
        })))).then(CommandManager.literal("at").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withWorld((ServerWorld)entity.world).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
            }
            return list;
        })))).then(((LiteralArgumentBuilder)CommandManager.literal("store").then(ExecuteCommand.addStoreArguments((LiteralCommandNode<ServerCommandSource>)literalCommandNode, CommandManager.literal("result"), true))).then(ExecuteCommand.addStoreArguments((LiteralCommandNode<ServerCommandSource>)literalCommandNode, CommandManager.literal("success"), false)))).then(((LiteralArgumentBuilder)CommandManager.literal("positioned").then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withPosition(entity.getPos()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("rotated").then(CommandManager.argument("rot", RotationArgumentType.rotation()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withRotation(RotationArgumentType.getRotation((CommandContext<ServerCommandSource>)commandContext, "rot").toAbsoluteRotation((ServerCommandSource)commandContext.getSource()))))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            for (Entity entity : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withRotation(entity.getRotationClient()));
            }
            return list;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList list = Lists.newArrayList();
            EntityAnchorArgumentType.EntityAnchor lv = EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "anchor");
            for (Entity entity : EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "targets")) {
                list.add(((ServerCommandSource)commandContext.getSource()).withLookingAt(entity, lv));
            }
            return list;
        }))))).then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withLookingAt(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos")))))).then(CommandManager.literal("align").then(CommandManager.argument("axes", SwizzleArgumentType.swizzle()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withPosition(((ServerCommandSource)commandContext.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle((CommandContext<ServerCommandSource>)commandContext, "axes"))))))).then(CommandManager.literal("anchored").then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "anchor")))))).then(CommandManager.literal("in").then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).redirect((CommandNode)literalCommandNode, commandContext -> ((ServerCommandSource)commandContext.getSource()).withWorld(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getWorld(DimensionArgumentType.getDimensionArgument((CommandContext<ServerCommandSource>)commandContext, "dimension")))))));
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(LiteralCommandNode<ServerCommandSource> literalCommandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl) {
        literalArgumentBuilder.then(CommandManager.literal("score").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ObjectiveArgumentType.objective()).redirect(literalCommandNode, commandContext -> ExecuteCommand.executeStoreScore((ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders((CommandContext<ServerCommandSource>)commandContext, "targets"), ObjectiveArgumentType.getObjective((CommandContext<ServerCommandSource>)commandContext, "objective"), bl)))));
        literalArgumentBuilder.then(CommandManager.literal("bossbar").then(((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(BossBarCommand.suggestionProvider).then(CommandManager.literal("value").redirect(literalCommandNode, commandContext -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), true, bl)))).then(CommandManager.literal("max").redirect(literalCommandNode, commandContext -> ExecuteCommand.executeStoreBossbar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), false, bl)))));
        for (DataCommand.ObjectType lv : DataCommand.TARGET_OBJECT_TYPES) {
            lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)literalArgumentBuilder, argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).then(CommandManager.literal("int").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> IntTag.of((int)((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(CommandManager.literal("float").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> FloatTag.of((float)((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(CommandManager.literal("short").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> ShortTag.of((short)((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(CommandManager.literal("long").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> LongTag.of((long)((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(CommandManager.literal("double").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> DoubleTag.of((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")), bl))))).then(CommandManager.literal("byte").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.executeStoreData((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), i -> ByteTag.of((byte)((double)i * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))));
        }
        return literalArgumentBuilder;
    }

    private static ServerCommandSource executeStoreScore(ServerCommandSource arg, Collection<String> collection, ScoreboardObjective arg2, boolean bl) {
        ServerScoreboard lv = arg.getMinecraftServer().getScoreboard();
        return arg.mergeConsumers((ResultConsumer<ServerCommandSource>)((ResultConsumer)(commandContext, bl2, i) -> {
            for (String string : collection) {
                ScoreboardPlayerScore lv = lv.getPlayerScore(string, arg2);
                int j = bl ? i : (bl2 ? 1 : 0);
                lv.setScore(j);
            }
        }), BINARY_RESULT_CONSUMER);
    }

    private static ServerCommandSource executeStoreBossbar(ServerCommandSource arg, CommandBossBar arg2, boolean bl, boolean bl2) {
        return arg.mergeConsumers((ResultConsumer<ServerCommandSource>)((ResultConsumer)(commandContext, bl3, i) -> {
            int j;
            int n = bl2 ? i : (j = bl3 ? 1 : 0);
            if (bl) {
                arg2.setValue(j);
            } else {
                arg2.setMaxValue(j);
            }
        }), BINARY_RESULT_CONSUMER);
    }

    private static ServerCommandSource executeStoreData(ServerCommandSource arg, DataCommandObject arg2, NbtPathArgumentType.NbtPath arg3, IntFunction<Tag> intFunction, boolean bl) {
        return arg.mergeConsumers((ResultConsumer<ServerCommandSource>)((ResultConsumer)(commandContext, bl2, i) -> {
            try {
                CompoundTag lv = arg2.getTag();
                int j = bl ? i : (bl2 ? 1 : 0);
                arg3.put(lv, () -> (Tag)intFunction.apply(j));
                arg2.setTag(lv);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }), BINARY_RESULT_CONSUMER);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(CommandNode<ServerCommandSource> commandNode, LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, boolean bl) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(CommandManager.literal("block").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate()), bl, commandContext -> BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "block").test(new CachedBlockPosition(((ServerCommandSource)commandContext.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), true))))))).then(CommandManager.literal("score").then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targetObjective", ObjectiveArgumentType.objective()).then(CommandManager.literal("=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), bl, commandContext -> ExecuteCommand.testScoreCondition((CommandContext<ServerCommandSource>)commandContext, Integer::equals)))))).then(CommandManager.literal("<").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), bl, commandContext -> ExecuteCommand.testScoreCondition((CommandContext<ServerCommandSource>)commandContext, (integer, integer2) -> integer < integer2)))))).then(CommandManager.literal("<=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), bl, commandContext -> ExecuteCommand.testScoreCondition((CommandContext<ServerCommandSource>)commandContext, (integer, integer2) -> integer <= integer2)))))).then(CommandManager.literal(">").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), bl, commandContext -> ExecuteCommand.testScoreCondition((CommandContext<ServerCommandSource>)commandContext, (integer, integer2) -> integer > integer2)))))).then(CommandManager.literal(">=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("sourceObjective", ObjectiveArgumentType.objective()), bl, commandContext -> ExecuteCommand.testScoreCondition((CommandContext<ServerCommandSource>)commandContext, (integer, integer2) -> integer >= integer2)))))).then(CommandManager.literal("matches").then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("range", NumberRangeArgumentType.numberRange()), bl, commandContext -> ExecuteCommand.testScoreMatch((CommandContext<ServerCommandSource>)commandContext, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument((CommandContext<ServerCommandSource>)commandContext, "range"))))))))).then(CommandManager.literal("blocks").then(CommandManager.argument("start", BlockPosArgumentType.blockPos()).then(CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).then(ExecuteCommand.addBlocksConditionLogic(commandNode, CommandManager.literal("all"), bl, false))).then(ExecuteCommand.addBlocksConditionLogic(commandNode, CommandManager.literal("masked"), bl, true))))))).then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("entities", EntityArgumentType.entities()).fork(commandNode, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, bl, !EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "entities").isEmpty()))).executes(ExecuteCommand.getExistsConditionExecute(bl, commandContext -> EntityArgumentType.getOptionalEntities((CommandContext<ServerCommandSource>)commandContext, "entities").size()))))).then(CommandManager.literal("predicate").then(ExecuteCommand.addConditionLogic(commandNode, CommandManager.argument("predicate", IdentifierArgumentType.identifier()).suggests(LOOT_CONDITIONS), bl, commandContext -> ExecuteCommand.testLootCondition((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.method_23727((CommandContext<ServerCommandSource>)commandContext, "predicate")))));
        for (DataCommand.ObjectType lv : DataCommand.SOURCE_OBJECT_TYPES) {
            literalArgumentBuilder.then(lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("data"), argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).fork(commandNode, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, bl, ExecuteCommand.countPathMatches(lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path")) > 0))).executes(ExecuteCommand.getExistsConditionExecute(bl, commandContext -> ExecuteCommand.countPathMatches(lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path")))))));
        }
        return literalArgumentBuilder;
    }

    private static Command<ServerCommandSource> getExistsConditionExecute(boolean bl, ExistsCondition arg) {
        if (bl) {
            return commandContext -> {
                int i = arg.test((CommandContext<ServerCommandSource>)commandContext);
                if (i > 0) {
                    ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", i), false);
                    return i;
                }
                throw CONDITIONAL_FAIL_EXCEPTION.create();
            };
        }
        return commandContext -> {
            int i = arg.test((CommandContext<ServerCommandSource>)commandContext);
            if (i == 0) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create((Object)i);
        };
    }

    private static int countPathMatches(DataCommandObject arg, NbtPathArgumentType.NbtPath arg2) throws CommandSyntaxException {
        return arg2.count(arg.getTag());
    }

    private static boolean testScoreCondition(CommandContext<ServerCommandSource> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
        String string = ScoreHolderArgumentType.getScoreHolder(commandContext, "target");
        ScoreboardObjective lv = ObjectiveArgumentType.getObjective(commandContext, "targetObjective");
        String string2 = ScoreHolderArgumentType.getScoreHolder(commandContext, "source");
        ScoreboardObjective lv2 = ObjectiveArgumentType.getObjective(commandContext, "sourceObjective");
        ServerScoreboard lv3 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getScoreboard();
        if (!lv3.playerHasObjective(string, lv) || !lv3.playerHasObjective(string2, lv2)) {
            return false;
        }
        ScoreboardPlayerScore lv4 = lv3.getPlayerScore(string, lv);
        ScoreboardPlayerScore lv5 = lv3.getPlayerScore(string2, lv2);
        return biPredicate.test(lv4.getScore(), lv5.getScore());
    }

    private static boolean testScoreMatch(CommandContext<ServerCommandSource> commandContext, NumberRange.IntRange arg) throws CommandSyntaxException {
        String string = ScoreHolderArgumentType.getScoreHolder(commandContext, "target");
        ScoreboardObjective lv = ObjectiveArgumentType.getObjective(commandContext, "targetObjective");
        ServerScoreboard lv2 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getScoreboard();
        if (!lv2.playerHasObjective(string, lv)) {
            return false;
        }
        return arg.test(lv2.getPlayerScore(string, lv).getScore());
    }

    private static boolean testLootCondition(ServerCommandSource arg, LootCondition arg2) {
        ServerWorld lv = arg.getWorld();
        LootContext.Builder lv2 = new LootContext.Builder(lv).put(LootContextParameters.POSITION, new BlockPos(arg.getPosition())).putNullable(LootContextParameters.THIS_ENTITY, arg.getEntity());
        return arg2.test(lv2.build(LootContextTypes.COMMAND));
    }

    private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(CommandContext<ServerCommandSource> commandContext, boolean bl, boolean bl2) {
        if (bl2 == bl) {
            return Collections.singleton(commandContext.getSource());
        }
        return Collections.emptyList();
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, Condition arg) {
        return argumentBuilder.fork(commandNode, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, bl, arg.test((CommandContext<ServerCommandSource>)commandContext))).executes(commandContext -> {
            if (bl == arg.test((CommandContext<ServerCommandSource>)commandContext)) {
                ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw CONDITIONAL_FAIL_EXCEPTION.create();
        });
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(CommandNode<ServerCommandSource> commandNode, ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, boolean bl, boolean bl2) {
        return argumentBuilder.fork(commandNode, commandContext -> ExecuteCommand.getSourceOrEmptyForConditionFork((CommandContext<ServerCommandSource>)commandContext, bl, ExecuteCommand.testBlocksCondition((CommandContext<ServerCommandSource>)commandContext, bl2).isPresent())).executes(bl ? commandContext -> ExecuteCommand.executePositiveBlockCondition((CommandContext<ServerCommandSource>)commandContext, bl2) : commandContext -> ExecuteCommand.executeNegativeBlockCondition((CommandContext<ServerCommandSource>)commandContext, bl2));
    }

    private static int executePositiveBlockCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.testBlocksCondition(commandContext, bl);
        if (optionalInt.isPresent()) {
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
            return optionalInt.getAsInt();
        }
        throw CONDITIONAL_FAIL_EXCEPTION.create();
    }

    private static int executeNegativeBlockCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.testBlocksCondition(commandContext, bl);
        if (optionalInt.isPresent()) {
            throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create((Object)optionalInt.getAsInt());
        }
        ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableText("commands.execute.conditional.pass"), false);
        return 1;
    }

    private static OptionalInt testBlocksCondition(CommandContext<ServerCommandSource> commandContext, boolean bl) throws CommandSyntaxException {
        return ExecuteCommand.testBlocksCondition(((ServerCommandSource)commandContext.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(commandContext, "start"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"), bl);
    }

    private static OptionalInt testBlocksCondition(ServerWorld arg, BlockPos arg2, BlockPos arg3, BlockPos arg4, boolean bl) throws CommandSyntaxException {
        BlockBox lv = new BlockBox(arg2, arg3);
        BlockBox lv2 = new BlockBox(arg4, arg4.add(lv.getDimensions()));
        BlockPos lv3 = new BlockPos(lv2.minX - lv.minX, lv2.minY - lv.minY, lv2.minZ - lv.minZ);
        int i = lv.getBlockCountX() * lv.getBlockCountY() * lv.getBlockCountZ();
        if (i > 32768) {
            throw BLOCKS_TOOBIG_EXCEPTION.create((Object)32768, (Object)i);
        }
        int j = 0;
        for (int k = lv.minZ; k <= lv.maxZ; ++k) {
            for (int l = lv.minY; l <= lv.maxY; ++l) {
                for (int m = lv.minX; m <= lv.maxX; ++m) {
                    BlockPos lv4 = new BlockPos(m, l, k);
                    BlockPos lv5 = lv4.add(lv3);
                    BlockState lv6 = arg.getBlockState(lv4);
                    if (bl && lv6.isOf(Blocks.AIR)) continue;
                    if (lv6 != arg.getBlockState(lv5)) {
                        return OptionalInt.empty();
                    }
                    BlockEntity lv7 = arg.getBlockEntity(lv4);
                    BlockEntity lv8 = arg.getBlockEntity(lv5);
                    if (lv7 != null) {
                        if (lv8 == null) {
                            return OptionalInt.empty();
                        }
                        CompoundTag lv9 = lv7.toTag(new CompoundTag());
                        lv9.remove("x");
                        lv9.remove("y");
                        lv9.remove("z");
                        CompoundTag lv10 = lv8.toTag(new CompoundTag());
                        lv10.remove("x");
                        lv10.remove("y");
                        lv10.remove("z");
                        if (!lv9.equals(lv10)) {
                            return OptionalInt.empty();
                        }
                    }
                    ++j;
                }
            }
        }
        return OptionalInt.of(j);
    }

    @FunctionalInterface
    static interface ExistsCondition {
        public int test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface Condition {
        public boolean test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

