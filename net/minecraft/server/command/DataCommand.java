/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NbtTagArgumentType;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class DataCommand {
    private static final SimpleCommandExceptionType MERGE_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.invalid", object));
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.get.unknown", object));
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_LIST_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_list", object));
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.expected_object", object));
    private static final DynamicCommandExceptionType MODIFY_INVALID_INDEX_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.data.modify.invalid_index", object));
    public static final List<Function<String, ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY);
    public static final List<ObjectType> TARGET_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<ObjectType> SOURCE_OBJECT_TYPES = (List)OBJECT_TYPE_FACTORIES.stream().map(function -> (ObjectType)function.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("data").requires(arg -> arg.hasPermissionLevel(2));
        for (ObjectType lv : TARGET_OBJECT_TYPES) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("merge"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("nbt", NbtCompoundTagArgumentType.nbtCompound()).executes(commandContext -> DataCommand.executeMerge((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt"))))))).then(lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("get"), argumentBuilder -> argumentBuilder.executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext))).then(((RequiredArgumentBuilder)CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path")))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> DataCommand.executeGet((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))))).then(lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("remove"), argumentBuilder -> argumentBuilder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(commandContext -> DataCommand.executeRemove((ServerCommandSource)commandContext.getSource(), lv.getObject((CommandContext<ServerCommandSource>)commandContext), NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "path"))))))).then(DataCommand.addModifyArgument((argumentBuilder, arg3) -> argumentBuilder.then(CommandManager.literal("insert").then(CommandManager.argument("index", IntegerArgumentType.integer()).then(arg3.create((commandContext, arg, arg2, list) -> {
                int i = IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"index");
                return DataCommand.executeInsert(i, arg, arg2, list);
            })))).then(CommandManager.literal("prepend").then(arg3.create((commandContext, arg, arg2, list) -> DataCommand.executeInsert(0, arg, arg2, list)))).then(CommandManager.literal("append").then(arg3.create((commandContext, arg, arg2, list) -> DataCommand.executeInsert(-1, arg, arg2, list)))).then(CommandManager.literal("set").then(arg3.create((commandContext, arg, arg2, list) -> arg2.put(arg, ((Tag)Iterables.getLast((Iterable)list))::copy)))).then(CommandManager.literal("merge").then(arg3.create((commandContext, arg, arg2, list) -> {
                List<Tag> collection = arg2.getOrInit(arg, CompoundTag::new);
                int i = 0;
                for (Tag lv : collection) {
                    if (!(lv instanceof CompoundTag)) {
                        throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create((Object)lv);
                    }
                    CompoundTag lv2 = (CompoundTag)lv;
                    CompoundTag lv3 = lv2.copy();
                    for (Tag lv4 : list) {
                        if (!(lv4 instanceof CompoundTag)) {
                            throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create((Object)lv4);
                        }
                        lv2.copyFrom((CompoundTag)lv4);
                    }
                    i += lv3.equals(lv2) ? 0 : 1;
                }
                return i;
            })))));
        }
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static int executeInsert(int i, CompoundTag arg, NbtPathArgumentType.NbtPath arg2, List<Tag> list) throws CommandSyntaxException {
        List<Tag> collection = arg2.getOrInit(arg, ListTag::new);
        int j = 0;
        for (Tag lv : collection) {
            if (!(lv instanceof AbstractListTag)) {
                throw MODIFY_EXPECTED_LIST_EXCEPTION.create((Object)lv);
            }
            boolean bl = false;
            AbstractListTag lv2 = (AbstractListTag)lv;
            int k = i < 0 ? lv2.size() + i + 1 : i;
            for (Tag lv3 : list) {
                try {
                    if (!lv2.addTag(k, lv3.copy())) continue;
                    ++k;
                    bl = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    throw MODIFY_INVALID_INDEX_EXCEPTION.create((Object)k);
                }
            }
            j += bl ? 1 : 0;
        }
        return j;
    }

    private static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, ModifyArgumentCreator> biConsumer) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");
        for (ObjectType lv : TARGET_OBJECT_TYPES) {
            lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)literalArgumentBuilder, argumentBuilder -> {
                RequiredArgumentBuilder<ServerCommandSource, NbtPathArgumentType.NbtPath> argumentBuilder2 = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());
                for (ObjectType lv : SOURCE_OBJECT_TYPES) {
                    biConsumer.accept((ArgumentBuilder<ServerCommandSource, ?>)argumentBuilder2, arg3 -> lv.addArgumentsToBuilder((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("from"), argumentBuilder -> argumentBuilder.executes(commandContext -> {
                        List<Tag> list = Collections.singletonList(lv.getObject((CommandContext<ServerCommandSource>)commandContext).getTag());
                        return DataCommand.executeModify((CommandContext<ServerCommandSource>)commandContext, lv, arg3, list);
                    }).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(commandContext -> {
                        DataCommandObject lv = lv.getObject((CommandContext<ServerCommandSource>)commandContext);
                        NbtPathArgumentType.NbtPath lv2 = NbtPathArgumentType.getNbtPath((CommandContext<ServerCommandSource>)commandContext, "sourcePath");
                        List<Tag> list = lv2.get(lv.getTag());
                        return DataCommand.executeModify((CommandContext<ServerCommandSource>)commandContext, lv, arg3, list);
                    }))));
                }
                biConsumer.accept((ArgumentBuilder<ServerCommandSource, ?>)argumentBuilder2, arg2 -> (LiteralArgumentBuilder)CommandManager.literal("value").then(CommandManager.argument("value", NbtTagArgumentType.nbtTag()).executes(commandContext -> {
                    List<Tag> list = Collections.singletonList(NbtTagArgumentType.getTag(commandContext, "value"));
                    return DataCommand.executeModify((CommandContext<ServerCommandSource>)commandContext, lv, arg2, list);
                })));
                return argumentBuilder.then(argumentBuilder2);
            });
        }
        return literalArgumentBuilder;
    }

    private static int executeModify(CommandContext<ServerCommandSource> commandContext, ObjectType arg, ModifyOperation arg2, List<Tag> list) throws CommandSyntaxException {
        DataCommandObject lv = arg.getObject(commandContext);
        NbtPathArgumentType.NbtPath lv2 = NbtPathArgumentType.getNbtPath(commandContext, "targetPath");
        CompoundTag lv3 = lv.getTag();
        int i = arg2.modify(commandContext, lv3, lv2, list);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        lv.setTag(lv3);
        ((ServerCommandSource)commandContext.getSource()).sendFeedback(lv.feedbackModify(), true);
        return i;
    }

    private static int executeRemove(ServerCommandSource arg, DataCommandObject arg2, NbtPathArgumentType.NbtPath arg3) throws CommandSyntaxException {
        CompoundTag lv = arg2.getTag();
        int i = arg3.remove(lv);
        if (i == 0) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        arg2.setTag(lv);
        arg.sendFeedback(arg2.feedbackModify(), true);
        return i;
    }

    private static Tag getTag(NbtPathArgumentType.NbtPath arg, DataCommandObject arg2) throws CommandSyntaxException {
        List<Tag> collection = arg.get(arg2.getTag());
        Iterator iterator = collection.iterator();
        Tag lv = (Tag)iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        }
        return lv;
    }

    /*
     * WARNING - void declaration
     */
    private static int executeGet(ServerCommandSource arg, DataCommandObject arg2, NbtPathArgumentType.NbtPath arg3) throws CommandSyntaxException {
        void m;
        Tag lv = DataCommand.getTag(arg3, arg2);
        if (lv instanceof AbstractNumberTag) {
            int i = MathHelper.floor(((AbstractNumberTag)lv).getDouble());
        } else if (lv instanceof AbstractListTag) {
            int j = ((AbstractListTag)lv).size();
        } else if (lv instanceof CompoundTag) {
            int k = ((CompoundTag)lv).getSize();
        } else if (lv instanceof StringTag) {
            int l = lv.asString().length();
        } else {
            throw GET_UNKNOWN_EXCEPTION.create((Object)arg3.toString());
        }
        arg.sendFeedback(arg2.feedbackQuery(lv), false);
        return (int)m;
    }

    private static int executeGet(ServerCommandSource arg, DataCommandObject arg2, NbtPathArgumentType.NbtPath arg3, double d) throws CommandSyntaxException {
        Tag lv = DataCommand.getTag(arg3, arg2);
        if (!(lv instanceof AbstractNumberTag)) {
            throw GET_INVALID_EXCEPTION.create((Object)arg3.toString());
        }
        int i = MathHelper.floor(((AbstractNumberTag)lv).getDouble() * d);
        arg.sendFeedback(arg2.feedbackGet(arg3, d, i), false);
        return i;
    }

    private static int executeGet(ServerCommandSource arg, DataCommandObject arg2) throws CommandSyntaxException {
        arg.sendFeedback(arg2.feedbackQuery(arg2.getTag()), false);
        return 1;
    }

    private static int executeMerge(ServerCommandSource arg, DataCommandObject arg2, CompoundTag arg3) throws CommandSyntaxException {
        CompoundTag lv2;
        CompoundTag lv = arg2.getTag();
        if (lv.equals(lv2 = lv.copy().copyFrom(arg3))) {
            throw MERGE_FAILED_EXCEPTION.create();
        }
        arg2.setTag(lv2);
        arg.sendFeedback(arg2.feedbackModify(), true);
        return 1;
    }

    public static interface ObjectType {
        public DataCommandObject getObject(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> var1, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> var2);
    }

    static interface ModifyArgumentCreator {
        public ArgumentBuilder<ServerCommandSource, ?> create(ModifyOperation var1);
    }

    static interface ModifyOperation {
        public int modify(CommandContext<ServerCommandSource> var1, CompoundTag var2, NbtPathArgumentType.NbtPath var3, List<Tag> var4) throws CommandSyntaxException;
    }
}

