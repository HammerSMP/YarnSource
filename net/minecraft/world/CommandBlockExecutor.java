/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ResultConsumer
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import com.mojang.brigadier.ResultConsumer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class CommandBlockExecutor
implements CommandOutput {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Text DEFAULT_NAME = new LiteralText("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    private Text lastOutput;
    private String command = "";
    private Text customName = DEFAULT_NAME;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int i) {
        this.successCount = i;
    }

    public Text getLastOutput() {
        return this.lastOutput == null ? LiteralText.EMPTY : this.lastOutput;
    }

    public CompoundTag serialize(CompoundTag arg) {
        arg.putString("Command", this.command);
        arg.putInt("SuccessCount", this.successCount);
        arg.putString("CustomName", Text.Serializer.toJson(this.customName));
        arg.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            arg.putString("LastOutput", Text.Serializer.toJson(this.lastOutput));
        }
        arg.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            arg.putLong("LastExecution", this.lastExecution);
        }
        return arg;
    }

    public void deserialize(CompoundTag arg) {
        this.command = arg.getString("Command");
        this.successCount = arg.getInt("SuccessCount");
        if (arg.contains("CustomName", 8)) {
            this.setCustomName(Text.Serializer.fromJson(arg.getString("CustomName")));
        }
        if (arg.contains("TrackOutput", 1)) {
            this.trackOutput = arg.getBoolean("TrackOutput");
        }
        if (arg.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Text.Serializer.fromJson(arg.getString("LastOutput"));
            }
            catch (Throwable throwable) {
                this.lastOutput = new LiteralText(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }
        if (arg.contains("UpdateLastExecution")) {
            this.updateLastExecution = arg.getBoolean("UpdateLastExecution");
        }
        this.lastExecution = this.updateLastExecution && arg.contains("LastExecution") ? arg.getLong("LastExecution") : -1L;
    }

    public void setCommand(String string) {
        this.command = string;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean execute(World arg) {
        if (arg.isClient || arg.getTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new LiteralText("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        MinecraftServer minecraftServer = this.getWorld().getServer();
        if (minecraftServer != null && minecraftServer.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
            try {
                this.lastOutput = null;
                ServerCommandSource lv = this.getSource().withConsumer((ResultConsumer<ServerCommandSource>)((ResultConsumer)(commandContext, bl, i) -> {
                    if (bl) {
                        ++this.successCount;
                    }
                }));
                minecraftServer.getCommandManager().execute(lv, this.command);
            }
            catch (Throwable throwable) {
                CrashReport lv2 = CrashReport.create(throwable, "Executing command block");
                CrashReportSection lv3 = lv2.addElement("Command to be executed");
                lv3.add("Command", this::getCommand);
                lv3.add("Name", () -> this.getCustomName().getString());
                throw new CrashException(lv2);
            }
        }
        this.lastExecution = this.updateLastExecution ? arg.getTime() : -1L;
        return true;
    }

    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable Text arg) {
        this.customName = arg != null ? arg : DEFAULT_NAME;
    }

    @Override
    public void sendSystemMessage(Text arg, UUID uUID) {
        if (this.trackOutput) {
            this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(arg);
            this.markDirty();
        }
    }

    public abstract ServerWorld getWorld();

    public abstract void markDirty();

    public void setLastOutput(@Nullable Text arg) {
        this.lastOutput = arg;
    }

    public void shouldTrackOutput(boolean bl) {
        this.trackOutput = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isTrackingOutput() {
        return this.trackOutput;
    }

    public boolean interact(PlayerEntity arg) {
        if (!arg.isCreativeLevelTwoOp()) {
            return false;
        }
        if (arg.getEntityWorld().isClient) {
            arg.openCommandBlockMinecartScreen(this);
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public abstract Vec3d getPos();

    public abstract ServerCommandSource getSource();

    @Override
    public boolean shouldReceiveFeedback() {
        return this.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.getWorld().getGameRules().getBoolean(GameRules.COMMAND_BLOCK_OUTPUT);
    }
}

