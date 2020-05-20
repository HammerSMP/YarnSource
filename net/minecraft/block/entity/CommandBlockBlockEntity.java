/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockBlockEntity
extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private boolean needsUpdatePacket;
    private final CommandBlockExecutor commandExecutor = new CommandBlockExecutor(){

        @Override
        public void setCommand(String string) {
            super.setCommand(string);
            CommandBlockBlockEntity.this.markDirty();
        }

        @Override
        public ServerWorld getWorld() {
            return (ServerWorld)CommandBlockBlockEntity.this.world;
        }

        @Override
        public void markDirty() {
            BlockState lv = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
            this.getWorld().updateListeners(CommandBlockBlockEntity.this.pos, lv, lv, 3);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Vec3d getPos() {
            return Vec3d.ofCenter(CommandBlockBlockEntity.this.pos);
        }

        @Override
        public ServerCommandSource getSource() {
            return new ServerCommandSource(this, Vec3d.ofCenter(CommandBlockBlockEntity.this.pos), Vec2f.ZERO, this.getWorld(), 2, this.getCustomName().getString(), this.getCustomName(), this.getWorld().getServer(), null);
        }
    };

    public CommandBlockBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        this.commandExecutor.serialize(arg);
        arg.putBoolean("powered", this.isPowered());
        arg.putBoolean("conditionMet", this.isConditionMet());
        arg.putBoolean("auto", this.isAuto());
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.commandExecutor.deserialize(arg2);
        this.powered = arg2.getBoolean("powered");
        this.conditionMet = arg2.getBoolean("conditionMet");
        this.setAuto(arg2.getBoolean("auto"));
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        if (this.needsUpdatePacket()) {
            this.setNeedsUpdatePacket(false);
            CompoundTag lv = this.toTag(new CompoundTag());
            return new BlockEntityUpdateS2CPacket(this.pos, 2, lv);
        }
        return null;
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public void setPowered(boolean bl) {
        this.powered = bl;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAuto() {
        return this.auto;
    }

    public void setAuto(boolean bl) {
        boolean bl2 = this.auto;
        this.auto = bl;
        if (!bl2 && bl && !this.powered && this.world != null && this.getCommandBlockType() != Type.SEQUENCE) {
            this.method_23360();
        }
    }

    public void method_23359() {
        Type lv = this.getCommandBlockType();
        if (lv == Type.AUTO && (this.powered || this.auto) && this.world != null) {
            this.method_23360();
        }
    }

    private void method_23360() {
        Block lv = this.getCachedState().getBlock();
        if (lv instanceof CommandBlock) {
            this.updateConditionMet();
            this.world.getBlockTickScheduler().schedule(this.pos, lv, 1);
        }
    }

    public boolean isConditionMet() {
        return this.conditionMet;
    }

    public boolean updateConditionMet() {
        this.conditionMet = true;
        if (this.isConditionalCommandBlock()) {
            BlockEntity lv2;
            BlockPos lv = this.pos.offset(this.world.getBlockState(this.pos).get(CommandBlock.FACING).getOpposite());
            this.conditionMet = this.world.getBlockState(lv).getBlock() instanceof CommandBlock ? (lv2 = this.world.getBlockEntity(lv)) instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)lv2).getCommandExecutor().getSuccessCount() > 0 : false;
        }
        return this.conditionMet;
    }

    public boolean needsUpdatePacket() {
        return this.needsUpdatePacket;
    }

    public void setNeedsUpdatePacket(boolean bl) {
        this.needsUpdatePacket = bl;
    }

    public Type getCommandBlockType() {
        BlockState lv = this.getCachedState();
        if (lv.isOf(Blocks.COMMAND_BLOCK)) {
            return Type.REDSTONE;
        }
        if (lv.isOf(Blocks.REPEATING_COMMAND_BLOCK)) {
            return Type.AUTO;
        }
        if (lv.isOf(Blocks.CHAIN_COMMAND_BLOCK)) {
            return Type.SEQUENCE;
        }
        return Type.REDSTONE;
    }

    public boolean isConditionalCommandBlock() {
        BlockState lv = this.world.getBlockState(this.getPos());
        if (lv.getBlock() instanceof CommandBlock) {
            return lv.get(CommandBlock.CONDITIONAL);
        }
        return false;
    }

    @Override
    public void cancelRemoval() {
        this.resetBlock();
        super.cancelRemoval();
    }

    public static enum Type {
        SEQUENCE,
        AUTO,
        REDSTONE;

    }
}

