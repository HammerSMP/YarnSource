/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity
extends BlockEntity {
    private final Text[] text = new Text[]{LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY};
    private boolean editable = true;
    private PlayerEntity editor;
    private final Text[] textBeingEdited = new Text[4];
    private DyeColor textColor = DyeColor.BLACK;

    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        for (int i = 0; i < 4; ++i) {
            String string = Text.Serializer.toJson(this.text[i]);
            arg.putString("Text" + (i + 1), string);
        }
        arg.putString("Color", this.textColor.getName());
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        this.editable = false;
        super.fromTag(arg, arg2);
        this.textColor = DyeColor.byName(arg2.getString("Color"), DyeColor.BLACK);
        for (int i = 0; i < 4; ++i) {
            String string = arg2.getString("Text" + (i + 1));
            MutableText lv = Text.Serializer.fromJson(string.isEmpty() ? "\"\"" : string);
            if (this.world instanceof ServerWorld) {
                try {
                    this.text[i] = Texts.parse(this.getCommandSource(null), lv, null, 0);
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    this.text[i] = lv;
                }
            } else {
                this.text[i] = lv;
            }
            this.textBeingEdited[i] = null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Text getTextOnRow(int i) {
        return this.text[i];
    }

    public void setTextOnRow(int i, Text arg) {
        this.text[i] = arg;
        this.textBeingEdited[i] = null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Text getTextBeingEditedOnRow(int i, UnaryOperator<Text> unaryOperator) {
        if (this.textBeingEdited[i] == null && this.text[i] != null) {
            this.textBeingEdited[i] = (Text)unaryOperator.apply(this.text[i]);
        }
        return this.textBeingEdited[i];
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Environment(value=EnvType.CLIENT)
    public void setEditable(boolean bl) {
        this.editable = bl;
        if (!bl) {
            this.editor = null;
        }
    }

    public void setEditor(PlayerEntity arg) {
        this.editor = arg;
    }

    public PlayerEntity getEditor() {
        return this.editor;
    }

    public boolean onActivate(PlayerEntity arg) {
        for (Text lv : this.text) {
            ClickEvent lv3;
            Style lv2;
            Style style = lv2 = lv == null ? null : lv.getStyle();
            if (lv2 == null || lv2.getClickEvent() == null || (lv3 = lv2.getClickEvent()).getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            arg.getServer().getCommandManager().execute(this.getCommandSource((ServerPlayerEntity)arg), lv3.getValue());
        }
        return true;
    }

    public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity arg) {
        String string = arg == null ? "Sign" : arg.getName().getString();
        Text lv = arg == null ? new LiteralText("Sign") : arg.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(this.pos), Vec2f.ZERO, (ServerWorld)this.world, 2, string, lv, this.world.getServer(), arg);
    }

    public DyeColor getTextColor() {
        return this.textColor;
    }

    public boolean setTextColor(DyeColor arg) {
        if (arg != this.getTextColor()) {
            this.textColor = arg;
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
        return false;
    }
}

