/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EntityDataObject
implements DataCommandObject {
    private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.data.entity.invalid"));
    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType((String)string){
        final /* synthetic */ String field_13802;
        {
            this.field_13802 = string;
        }

        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
            return new EntityDataObject(EntityArgumentType.getEntity(commandContext, this.field_13802));
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argumentBuilder, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function) {
            return argumentBuilder.then(CommandManager.literal("entity").then(function.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.field_13802, EntityArgumentType.entity()))));
        }
    };
    private final Entity entity;

    public EntityDataObject(Entity arg) {
        this.entity = arg;
    }

    @Override
    public void setTag(CompoundTag arg) throws CommandSyntaxException {
        if (this.entity instanceof PlayerEntity) {
            throw INVALID_ENTITY_EXCEPTION.create();
        }
        UUID uUID = this.entity.getUuid();
        this.entity.fromTag(arg);
        this.entity.setUuid(uUID);
    }

    @Override
    public CompoundTag getTag() {
        return NbtPredicate.entityToTag(this.entity);
    }

    @Override
    public Text feedbackModify() {
        return new TranslatableText("commands.data.entity.modified", this.entity.getDisplayName());
    }

    @Override
    public Text feedbackQuery(Tag arg) {
        return new TranslatableText("commands.data.entity.query", this.entity.getDisplayName(), arg.toText());
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath arg, double d, int i) {
        return new TranslatableText("commands.data.entity.get", arg, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", d), i);
    }
}

