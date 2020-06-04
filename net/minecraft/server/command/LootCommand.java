/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ReplaceItemCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        LootManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getLootManager();
        return CommandSource.suggestIdentifiers(lv.getTableIds(), suggestionsBuilder);
    };
    private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.drop.no_held_items", object));
    private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.drop.no_loot_table", object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)LootCommand.addTargetArguments(CommandManager.literal("loot").requires(arg -> arg.hasPermissionLevel(2)), (argumentBuilder, arg) -> argumentBuilder.then(CommandManager.literal("fish").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, arg))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), arg)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), arg)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), arg)))))).then(CommandManager.literal("loot").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> LootCommand.executeLoot((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), arg)))).then(CommandManager.literal("kill").then(CommandManager.argument("target", EntityArgumentType.entity()).executes(commandContext -> LootCommand.executeKill((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), arg)))).then(CommandManager.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, arg))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), arg)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), arg)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), arg)))))));
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T argumentBuilder, SourceConstructor arg2) {
        return (T)argumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then(CommandManager.literal("entity").then(CommandManager.argument("entities", EntityArgumentType.entities()).then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, arg) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, arg)).then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, arg) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, arg))))))).then(CommandManager.literal("block").then(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()).then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, arg) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, arg)).then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, arg) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, arg))))))).then(CommandManager.literal("insert").then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()), (commandContext, list, arg) -> LootCommand.executeInsert((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, arg)))).then(CommandManager.literal("give").then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("players", EntityArgumentType.players()), (commandContext, list, arg) -> LootCommand.executeGive(EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "players"), list, arg)))).then(CommandManager.literal("spawn").then(arg2.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", Vec3ArgumentType.vec3()), (commandContext, list, arg) -> LootCommand.executeSpawn((ServerCommandSource)commandContext.getSource(), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, arg))));
    }

    private static Inventory getBlockInventory(ServerCommandSource arg, BlockPos arg2) throws CommandSyntaxException {
        BlockEntity lv = arg.getWorld().getBlockEntity(arg2);
        if (!(lv instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        return (Inventory)((Object)lv);
    }

    private static int executeInsert(ServerCommandSource arg, BlockPos arg2, List<ItemStack> list, FeedbackMessage arg3) throws CommandSyntaxException {
        Inventory lv = LootCommand.getBlockInventory(arg, arg2);
        ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
        for (ItemStack lv2 : list) {
            if (!LootCommand.insert(lv, lv2.copy())) continue;
            lv.markDirty();
            list2.add(lv2);
        }
        arg3.accept(list2);
        return list2.size();
    }

    private static boolean insert(Inventory arg, ItemStack arg2) {
        boolean bl = false;
        for (int i = 0; i < arg.size() && !arg2.isEmpty(); ++i) {
            ItemStack lv = arg.getStack(i);
            if (!arg.isValid(i, arg2)) continue;
            if (lv.isEmpty()) {
                arg.setStack(i, arg2);
                bl = true;
                break;
            }
            if (!LootCommand.itemsMatch(lv, arg2)) continue;
            int j = arg2.getMaxCount() - lv.getCount();
            int k = Math.min(arg2.getCount(), j);
            arg2.decrement(k);
            lv.increment(k);
            bl = true;
        }
        return bl;
    }

    private static int executeBlock(ServerCommandSource arg, BlockPos arg2, int i, int j, List<ItemStack> list, FeedbackMessage arg3) throws CommandSyntaxException {
        Inventory lv = LootCommand.getBlockInventory(arg, arg2);
        int k = lv.size();
        if (i < 0 || i >= k) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create((Object)i);
        }
        ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
        for (int l = 0; l < j; ++l) {
            ItemStack lv2;
            int m = i + l;
            ItemStack itemStack = lv2 = l < list.size() ? list.get(l) : ItemStack.EMPTY;
            if (!lv.isValid(m, lv2)) continue;
            lv.setStack(m, lv2);
            list2.add(lv2);
        }
        arg3.accept(list2);
        return list2.size();
    }

    private static boolean itemsMatch(ItemStack arg, ItemStack arg2) {
        return arg.getItem() == arg2.getItem() && arg.getDamage() == arg2.getDamage() && arg.getCount() <= arg.getMaxCount() && Objects.equals(arg.getTag(), arg2.getTag());
    }

    private static int executeGive(Collection<ServerPlayerEntity> collection, List<ItemStack> list, FeedbackMessage arg) throws CommandSyntaxException {
        ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
        for (ItemStack lv : list) {
            for (ServerPlayerEntity lv2 : collection) {
                if (!lv2.inventory.insertStack(lv.copy())) continue;
                list2.add(lv);
            }
        }
        arg.accept(list2);
        return list2.size();
    }

    private static void replace(Entity arg, List<ItemStack> list, int i, int j, List<ItemStack> list2) {
        for (int k = 0; k < j; ++k) {
            ItemStack lv;
            ItemStack itemStack = lv = k < list.size() ? list.get(k) : ItemStack.EMPTY;
            if (!arg.equip(i + k, lv.copy())) continue;
            list2.add(lv);
        }
    }

    private static int executeReplace(Collection<? extends Entity> collection, int i, int j, List<ItemStack> list, FeedbackMessage arg) throws CommandSyntaxException {
        ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
        for (Entity entity : collection) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity lv2 = (ServerPlayerEntity)entity;
                lv2.playerScreenHandler.sendContentUpdates();
                LootCommand.replace(entity, list, i, j, list2);
                lv2.playerScreenHandler.sendContentUpdates();
                continue;
            }
            LootCommand.replace(entity, list, i, j, list2);
        }
        arg.accept(list2);
        return list2.size();
    }

    private static int executeSpawn(ServerCommandSource arg, Vec3d arg2, List<ItemStack> list, FeedbackMessage arg32) throws CommandSyntaxException {
        ServerWorld lv = arg.getWorld();
        list.forEach(arg3 -> {
            ItemEntity lv = new ItemEntity(lv, arg2.x, arg2.y, arg2.z, arg3.copy());
            lv.setToDefaultPickupDelay();
            lv.spawnEntity(lv);
        });
        arg32.accept(list);
        return list.size();
    }

    private static void sendDroppedFeedback(ServerCommandSource arg, List<ItemStack> list) {
        if (list.size() == 1) {
            ItemStack lv = list.get(0);
            arg.sendFeedback(new TranslatableText("commands.drop.success.single", lv.getCount(), lv.toHoverableText()), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.drop.success.multiple", list.size()), false);
        }
    }

    private static void sendDroppedFeedback(ServerCommandSource arg, List<ItemStack> list, Identifier arg2) {
        if (list.size() == 1) {
            ItemStack lv = list.get(0);
            arg.sendFeedback(new TranslatableText("commands.drop.success.single_with_table", lv.getCount(), lv.toHoverableText(), arg2), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.drop.success.multiple_with_table", list.size(), arg2), false);
        }
    }

    private static ItemStack getHeldItem(ServerCommandSource arg, EquipmentSlot arg2) throws CommandSyntaxException {
        Entity lv = arg.getEntityOrThrow();
        if (lv instanceof LivingEntity) {
            return ((LivingEntity)lv).getEquippedStack(arg2);
        }
        throw NO_HELD_ITEMS_EXCEPTION.create((Object)lv.getDisplayName());
    }

    private static int executeMine(CommandContext<ServerCommandSource> commandContext, BlockPos arg, ItemStack arg2, Target arg3) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
        ServerWorld lv2 = lv.getWorld();
        BlockState lv3 = lv2.getBlockState(arg);
        BlockEntity lv4 = lv2.getBlockEntity(arg);
        LootContext.Builder lv5 = new LootContext.Builder(lv2).parameter(LootContextParameters.POSITION, arg).parameter(LootContextParameters.BLOCK_STATE, lv3).optionalParameter(LootContextParameters.BLOCK_ENTITY, lv4).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).parameter(LootContextParameters.TOOL, arg2);
        List<ItemStack> list2 = lv3.getDroppedStacks(lv5);
        return arg3.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(lv, list, lv3.getBlock().getLootTableId()));
    }

    private static int executeKill(CommandContext<ServerCommandSource> commandContext, Entity arg, Target arg2) throws CommandSyntaxException {
        if (!(arg instanceof LivingEntity)) {
            throw NO_LOOT_TABLE_EXCEPTION.create((Object)arg.getDisplayName());
        }
        Identifier lv = ((LivingEntity)arg).getLootTable();
        ServerCommandSource lv2 = (ServerCommandSource)commandContext.getSource();
        LootContext.Builder lv3 = new LootContext.Builder(lv2.getWorld());
        Entity lv4 = lv2.getEntity();
        if (lv4 instanceof PlayerEntity) {
            lv3.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)lv4);
        }
        lv3.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
        lv3.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, lv4);
        lv3.optionalParameter(LootContextParameters.KILLER_ENTITY, lv4);
        lv3.parameter(LootContextParameters.THIS_ENTITY, arg);
        lv3.parameter(LootContextParameters.POSITION, new BlockPos(lv2.getPosition()));
        LootTable lv5 = lv2.getMinecraftServer().getLootManager().getTable(lv);
        List<ItemStack> list2 = lv5.generateLoot(lv3.build(LootContextTypes.ENTITY));
        return arg2.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(lv2, list, lv));
    }

    private static int executeLoot(CommandContext<ServerCommandSource> commandContext, Identifier arg, Target arg2) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
        LootContext.Builder lv2 = new LootContext.Builder(lv.getWorld()).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).parameter(LootContextParameters.POSITION, new BlockPos(lv.getPosition()));
        return LootCommand.getFeedbackMessageSingle(commandContext, arg, lv2.build(LootContextTypes.CHEST), arg2);
    }

    private static int executeFish(CommandContext<ServerCommandSource> commandContext, Identifier arg, BlockPos arg2, ItemStack arg3, Target arg4) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
        LootContext lv2 = new LootContext.Builder(lv.getWorld()).parameter(LootContextParameters.POSITION, arg2).parameter(LootContextParameters.TOOL, arg3).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).build(LootContextTypes.FISHING);
        return LootCommand.getFeedbackMessageSingle(commandContext, arg, lv2, arg4);
    }

    private static int getFeedbackMessageSingle(CommandContext<ServerCommandSource> commandContext, Identifier arg, LootContext arg2, Target arg3) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
        LootTable lv2 = lv.getMinecraftServer().getLootManager().getTable(arg);
        List<ItemStack> list2 = lv2.generateLoot(arg2);
        return arg3.accept(commandContext, list2, list -> LootCommand.sendDroppedFeedback(lv, list));
    }

    @FunctionalInterface
    static interface SourceConstructor {
        public ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> var1, Target var2);
    }

    @FunctionalInterface
    static interface Target {
        public int accept(CommandContext<ServerCommandSource> var1, List<ItemStack> var2, FeedbackMessage var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface FeedbackMessage {
        public void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }
}

