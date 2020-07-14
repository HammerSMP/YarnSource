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
import net.minecraft.text.StringRenderable;
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
    private final StringRenderable[] textBeingEdited = new StringRenderable[4];
    private DyeColor textColor = DyeColor.BLACK;

    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        for (int i = 0; i < 4; ++i) {
            String string = Text.Serializer.toJson(this.text[i]);
            tag.putString("Text" + (i + 1), string);
        }
        tag.putString("Color", this.textColor.getName());
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        this.editable = false;
        super.fromTag(state, tag);
        this.textColor = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);
        for (int i = 0; i < 4; ++i) {
            String string = tag.getString("Text" + (i + 1));
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

    public void setTextOnRow(int row, Text text) {
        this.text[row] = text;
        this.textBeingEdited[row] = null;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public StringRenderable getTextBeingEditedOnRow(int row, UnaryOperator<StringRenderable> unaryOperator) {
        if (this.textBeingEdited[row] == null && this.text[row] != null) {
            this.textBeingEdited[row] = (StringRenderable)unaryOperator.apply(this.text[row]);
        }
        return this.textBeingEdited[row];
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

    public void setEditor(PlayerEntity player) {
        this.editor = player;
    }

    public PlayerEntity getEditor() {
        return this.editor;
    }

    public boolean onActivate(PlayerEntity player) {
        for (Text lv : this.text) {
            ClickEvent lv3;
            Style lv2;
            Style style = lv2 = lv == null ? null : lv.getStyle();
            if (lv2 == null || lv2.getClickEvent() == null || (lv3 = lv2.getClickEvent()).getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            player.getServer().getCommandManager().execute(this.getCommandSource((ServerPlayerEntity)player), lv3.getValue());
        }
        return true;
    }

    public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity player) {
        String string = player == null ? "Sign" : player.getName().getString();
        Text lv = player == null ? new LiteralText("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(this.pos), Vec2f.ZERO, (ServerWorld)this.world, 2, string, lv, this.world.getServer(), player);
    }

    public DyeColor getTextColor() {
        return this.textColor;
    }

    public boolean setTextColor(DyeColor value) {
        if (value != this.getTextColor()) {
            this.textColor = value;
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
        return false;
    }
}

