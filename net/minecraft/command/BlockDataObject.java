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
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BlockDataObject
implements DataCommandObject {
    private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.data.block.invalid"));
    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = string -> new DataCommand.ObjectType((String)string){
        final /* synthetic */ String field_13787;
        {
            this.field_13787 = string;
        }

        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            BlockPos lv = BlockPosArgumentType.getLoadedBlockPos(context, this.field_13787 + "Pos");
            BlockEntity lv2 = ((ServerCommandSource)context.getSource()).getWorld().getBlockEntity(lv);
            if (lv2 == null) {
                throw INVALID_BLOCK_EXCEPTION.create();
            }
            return new BlockDataObject(lv2, lv);
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
            return argument.then(CommandManager.literal("block").then(argumentAdder.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.field_13787 + "Pos", BlockPosArgumentType.blockPos()))));
        }
    };
    private final BlockEntity blockEntity;
    private final BlockPos pos;

    public BlockDataObject(BlockEntity blockEntity, BlockPos pos) {
        this.blockEntity = blockEntity;
        this.pos = pos;
    }

    @Override
    public void setTag(CompoundTag tag) {
        tag.putInt("x", this.pos.getX());
        tag.putInt("y", this.pos.getY());
        tag.putInt("z", this.pos.getZ());
        BlockState lv = this.blockEntity.getWorld().getBlockState(this.pos);
        this.blockEntity.fromTag(lv, tag);
        this.blockEntity.markDirty();
        this.blockEntity.getWorld().updateListeners(this.pos, lv, lv, 3);
    }

    @Override
    public CompoundTag getTag() {
        return this.blockEntity.toTag(new CompoundTag());
    }

    @Override
    public Text feedbackModify() {
        return new TranslatableText("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public Text feedbackQuery(Tag tag) {
        return new TranslatableText("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), tag.toText());
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath arg, double scale, int result) {
        return new TranslatableText("commands.data.block.get", arg, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), result);
    }
}

