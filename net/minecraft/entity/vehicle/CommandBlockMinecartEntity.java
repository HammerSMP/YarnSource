/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity
extends AbstractMinecartEntity {
    private static final TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Text> LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
    private final CommandBlockExecutor commandExecutor = new CommandExecutor();
    private int lastExecuted;

    public CommandBlockMinecartEntity(EntityType<? extends CommandBlockMinecartEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public CommandBlockMinecartEntity(World arg, double d, double e, double f) {
        super(EntityType.COMMAND_BLOCK_MINECART, arg, d, e, f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(COMMAND, "");
        this.getDataTracker().startTracking(LAST_OUTPUT, LiteralText.EMPTY);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.commandExecutor.deserialize(arg);
        this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
        this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        this.commandExecutor.serialize(arg);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.COMMAND_BLOCK;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    @Override
    public void onActivatorRail(int i, int j, int k, boolean bl) {
        if (bl && this.age - this.lastExecuted >= 4) {
            this.getCommandExecutor().execute(this.world);
            this.lastExecuted = this.age;
        }
    }

    @Override
    public boolean interact(PlayerEntity arg, Hand arg2) {
        this.commandExecutor.interact(arg);
        return true;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (LAST_OUTPUT.equals(arg)) {
            try {
                this.commandExecutor.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
            }
            catch (Throwable throwable) {}
        } else if (COMMAND.equals(arg)) {
            this.commandExecutor.setCommand(this.getDataTracker().get(COMMAND));
        }
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    public class CommandExecutor
    extends CommandBlockExecutor {
        @Override
        public ServerWorld getWorld() {
            return (ServerWorld)CommandBlockMinecartEntity.this.world;
        }

        @Override
        public void markDirty() {
            CommandBlockMinecartEntity.this.getDataTracker().set(COMMAND, this.getCommand());
            CommandBlockMinecartEntity.this.getDataTracker().set(LAST_OUTPUT, this.getLastOutput());
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Vec3d getPos() {
            return CommandBlockMinecartEntity.this.getPos();
        }

        @Environment(value=EnvType.CLIENT)
        public CommandBlockMinecartEntity getMinecart() {
            return CommandBlockMinecartEntity.this;
        }

        @Override
        public ServerCommandSource getSource() {
            return new ServerCommandSource(this, CommandBlockMinecartEntity.this.getPos(), CommandBlockMinecartEntity.this.getRotationClient(), this.getWorld(), 2, this.getCustomName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getWorld().getServer(), CommandBlockMinecartEntity.this);
        }
    }
}
