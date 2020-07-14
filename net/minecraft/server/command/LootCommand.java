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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)LootCommand.addTargetArguments(CommandManager.literal("loot").requires(arg -> arg.hasPermissionLevel(2)), (argumentBuilder, arg) -> argumentBuilder.then(CommandManager.literal("fish").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, arg))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), arg)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), arg)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeFish((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), arg)))))).then(CommandManager.literal("loot").then(CommandManager.argument("loot_table", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> LootCommand.executeLoot((CommandContext<ServerCommandSource>)commandContext, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "loot_table"), arg)))).then(CommandManager.literal("kill").then(CommandManager.argument("target", EntityArgumentType.entity()).executes(commandContext -> LootCommand.executeKill((CommandContext<ServerCommandSource>)commandContext, EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), arg)))).then(CommandManager.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStack.EMPTY, arg))).then(CommandManager.argument("tool", ItemStackArgumentType.itemStack()).executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemStackArgumentType.getItemStackArgument(commandContext, "tool").createStack(1, false), arg)))).then(CommandManager.literal("mainhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.MAINHAND), arg)))).then(CommandManager.literal("offhand").executes(commandContext -> LootCommand.executeMine((CommandContext<ServerCommandSource>)commandContext, BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), LootCommand.getHeldItem((ServerCommandSource)commandContext.getSource(), EquipmentSlot.OFFHAND), arg)))))));
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T rootArgument, SourceConstructor sourceConstructor) {
        return (T)rootArgument.then(((LiteralArgumentBuilder)CommandManager.literal("replace").then(CommandManager.literal("entity").then(CommandManager.argument("entities", EntityArgumentType.entities()).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, arg) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, arg)).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, arg) -> LootCommand.executeReplace(EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "entities"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, arg))))))).then(CommandManager.literal("block").then(CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()), (commandContext, list, arg) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), list.size(), list, arg)).then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, arg) -> LootCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, arg))))))).then(CommandManager.literal("insert").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()), (commandContext, list, arg) -> LootCommand.executeInsert((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, arg)))).then(CommandManager.literal("give").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("players", EntityArgumentType.players()), (commandContext, list, arg) -> LootCommand.executeGive(EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "players"), list, arg)))).then(CommandManager.literal("spawn").then(sourceConstructor.construct((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targetPos", Vec3ArgumentType.vec3()), (commandContext, list, arg) -> LootCommand.executeSpawn((ServerCommandSource)commandContext.getSource(), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "targetPos"), list, arg))));
    }

    private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
        BlockEntity lv = source.getWorld().getBlockEntity(pos);
        if (!(lv instanceof Inventory)) {
            throw ReplaceItemCommand.BLOCK_FAILED_EXCEPTION.create();
        }
        return (Inventory)((Object)lv);
    }

    private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory lv = LootCommand.getBlockInventory(source, targetPos);
        ArrayList list2 = Lists.newArrayListWithCapacity((int)stacks.size());
        for (ItemStack lv2 : stacks) {
            if (!LootCommand.insert(lv, lv2.copy())) continue;
            lv.markDirty();
            list2.add(lv2);
        }
        messageSender.accept(list2);
        return list2.size();
    }

    private static boolean insert(Inventory inventory, ItemStack stack) {
        boolean bl = false;
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); ++i) {
            ItemStack lv = inventory.getStack(i);
            if (!inventory.isValid(i, stack)) continue;
            if (lv.isEmpty()) {
                inventory.setStack(i, stack);
                bl = true;
                break;
            }
            if (!LootCommand.itemsMatch(lv, stack)) continue;
            int j = stack.getMaxCount() - lv.getCount();
            int k = Math.min(stack.getCount(), j);
            stack.decrement(k);
            lv.increment(k);
            bl = true;
        }
        return bl;
    }

    private static int executeBlock(ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        Inventory lv = LootCommand.getBlockInventory(source, targetPos);
        int k = lv.size();
        if (slot < 0 || slot >= k) {
            throw ReplaceItemCommand.SLOT_INAPPLICABLE_EXCEPTION.create((Object)slot);
        }
        ArrayList list2 = Lists.newArrayListWithCapacity((int)stacks.size());
        for (int l = 0; l < stackCount; ++l) {
            ItemStack lv2;
            int m = slot + l;
            ItemStack itemStack = lv2 = l < stacks.size() ? stacks.get(l) : ItemStack.EMPTY;
            if (!lv.isValid(m, lv2)) continue;
            lv.setStack(m, lv2);
            list2.add(lv2);
        }
        messageSender.accept(list2);
        return list2.size();
    }

    private static boolean itemsMatch(ItemStack first, ItemStack second) {
        return first.getItem() == second.getItem() && first.getDamage() == second.getDamage() && first.getCount() <= first.getMaxCount() && Objects.equals(first.getTag(), second.getTag());
    }

    private static int executeGive(Collection<ServerPlayerEntity> players, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList list2 = Lists.newArrayListWithCapacity((int)stacks.size());
        for (ItemStack lv : stacks) {
            for (ServerPlayerEntity lv2 : players) {
                if (!lv2.inventory.insertStack(lv.copy())) continue;
                list2.add(lv);
            }
        }
        messageSender.accept(list2);
        return list2.size();
    }

    private static void replace(Entity entity, List<ItemStack> stacks, int slot, int stackCount, List<ItemStack> addedStacks) {
        for (int k = 0; k < stackCount; ++k) {
            ItemStack lv;
            ItemStack itemStack = lv = k < stacks.size() ? stacks.get(k) : ItemStack.EMPTY;
            if (!entity.equip(slot + k, lv.copy())) continue;
            addedStacks.add(lv);
        }
    }

    private static int executeReplace(Collection<? extends Entity> targets, int slot, int stackCount, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ArrayList list2 = Lists.newArrayListWithCapacity((int)stacks.size());
        for (Entity entity : targets) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity lv2 = (ServerPlayerEntity)entity;
                lv2.playerScreenHandler.sendContentUpdates();
                LootCommand.replace(entity, stacks, slot, stackCount, list2);
                lv2.playerScreenHandler.sendContentUpdates();
                continue;
            }
            LootCommand.replace(entity, stacks, slot, stackCount, list2);
        }
        messageSender.accept(list2);
        return list2.size();
    }

    private static int executeSpawn(ServerCommandSource source, Vec3d pos, List<ItemStack> stacks, FeedbackMessage messageSender) throws CommandSyntaxException {
        ServerWorld lv = source.getWorld();
        stacks.forEach(arg3 -> {
            ItemEntity lv = new ItemEntity(lv, arg2.x, arg2.y, arg2.z, arg3.copy());
            lv.setToDefaultPickupDelay();
            lv.spawnEntity(lv);
        });
        messageSender.accept(stacks);
        return stacks.size();
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks) {
        if (stacks.size() == 1) {
            ItemStack lv = stacks.get(0);
            source.sendFeedback(new TranslatableText("commands.drop.success.single", lv.getCount(), lv.toHoverableText()), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.drop.success.multiple", stacks.size()), false);
        }
    }

    private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks, Identifier lootTable) {
        if (stacks.size() == 1) {
            ItemStack lv = stacks.get(0);
            source.sendFeedback(new TranslatableText("commands.drop.success.single_with_table", lv.getCount(), lv.toHoverableText(), lootTable), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.drop.success.multiple_with_table", stacks.size(), lootTable), false);
        }
    }

    private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
        Entity lv = source.getEntityOrThrow();
        if (lv instanceof LivingEntity) {
            return ((LivingEntity)lv).getEquippedStack(slot);
        }
        throw NO_HELD_ITEMS_EXCEPTION.create((Object)lv.getDisplayName());
    }

    private static int executeMine(CommandContext<ServerCommandSource> context, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)context.getSource();
        ServerWorld lv2 = lv.getWorld();
        BlockState lv3 = lv2.getBlockState(pos);
        BlockEntity lv4 = lv2.getBlockEntity(pos);
        LootContext.Builder lv5 = new LootContext.Builder(lv2).parameter(LootContextParameters.POSITION, pos).parameter(LootContextParameters.BLOCK_STATE, lv3).optionalParameter(LootContextParameters.BLOCK_ENTITY, lv4).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).parameter(LootContextParameters.TOOL, stack);
        List<ItemStack> list2 = lv3.getDroppedStacks(lv5);
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(lv, list, lv3.getBlock().getLootTableId()));
    }

    private static int executeKill(CommandContext<ServerCommandSource> context, Entity entity, Target constructor) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw NO_LOOT_TABLE_EXCEPTION.create((Object)entity.getDisplayName());
        }
        Identifier lv = ((LivingEntity)entity).getLootTable();
        ServerCommandSource lv2 = (ServerCommandSource)context.getSource();
        LootContext.Builder lv3 = new LootContext.Builder(lv2.getWorld());
        Entity lv4 = lv2.getEntity();
        if (lv4 instanceof PlayerEntity) {
            lv3.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)lv4);
        }
        lv3.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
        lv3.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, lv4);
        lv3.optionalParameter(LootContextParameters.KILLER_ENTITY, lv4);
        lv3.parameter(LootContextParameters.THIS_ENTITY, entity);
        lv3.parameter(LootContextParameters.POSITION, new BlockPos(lv2.getPosition()));
        LootTable lv5 = lv2.getMinecraftServer().getLootManager().getTable(lv);
        List<ItemStack> list2 = lv5.generateLoot(lv3.build(LootContextTypes.ENTITY));
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(lv2, list, lv));
    }

    private static int executeLoot(CommandContext<ServerCommandSource> context, Identifier lootTable, Target constructor) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)context.getSource();
        LootContext.Builder lv2 = new LootContext.Builder(lv.getWorld()).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).parameter(LootContextParameters.POSITION, new BlockPos(lv.getPosition()));
        return LootCommand.getFeedbackMessageSingle(context, lootTable, lv2.build(LootContextTypes.CHEST), constructor);
    }

    private static int executeFish(CommandContext<ServerCommandSource> context, Identifier lootTable, BlockPos pos, ItemStack stack, Target constructor) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)context.getSource();
        LootContext lv2 = new LootContext.Builder(lv.getWorld()).parameter(LootContextParameters.POSITION, pos).parameter(LootContextParameters.TOOL, stack).optionalParameter(LootContextParameters.THIS_ENTITY, lv.getEntity()).build(LootContextTypes.FISHING);
        return LootCommand.getFeedbackMessageSingle(context, lootTable, lv2, constructor);
    }

    private static int getFeedbackMessageSingle(CommandContext<ServerCommandSource> context, Identifier lootTable, LootContext lootContext, Target constructor) throws CommandSyntaxException {
        ServerCommandSource lv = (ServerCommandSource)context.getSource();
        LootTable lv2 = lv.getMinecraftServer().getLootManager().getTable(lootTable);
        List<ItemStack> list2 = lv2.generateLoot(lootContext);
        return constructor.accept(context, list2, list -> LootCommand.sendDroppedFeedback(lv, list));
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

