/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_5455;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ServerCommandSource
implements CommandSource {
    public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("permissions.requires.player"));
    public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("permissions.requires.entity"));
    private final CommandOutput output;
    private final Vec3d position;
    private final ServerWorld world;
    private final int level;
    private final String simpleName;
    private final Text name;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final ResultConsumer<ServerCommandSource> resultConsumer;
    private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
    private final Vec2f rotation;

    public ServerCommandSource(CommandOutput arg, Vec3d arg2, Vec2f arg3, ServerWorld arg4, int i2, String string, Text arg5, MinecraftServer minecraftServer, @Nullable Entity arg6) {
        this(arg, arg2, arg3, arg4, i2, string, arg5, minecraftServer, arg6, false, (ResultConsumer<ServerCommandSource>)((ResultConsumer)(commandContext, bl, i) -> {}), EntityAnchorArgumentType.EntityAnchor.FEET);
    }

    protected ServerCommandSource(CommandOutput arg, Vec3d arg2, Vec2f arg3, ServerWorld arg4, int i, String string, Text arg5, MinecraftServer minecraftServer, @Nullable Entity arg6, boolean bl, ResultConsumer<ServerCommandSource> resultConsumer, EntityAnchorArgumentType.EntityAnchor arg7) {
        this.output = arg;
        this.position = arg2;
        this.world = arg4;
        this.silent = bl;
        this.entity = arg6;
        this.level = i;
        this.simpleName = string;
        this.name = arg5;
        this.server = minecraftServer;
        this.resultConsumer = resultConsumer;
        this.entityAnchor = arg7;
        this.rotation = arg3;
    }

    public ServerCommandSource withEntity(Entity arg) {
        if (this.entity == arg) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, arg.getName().getString(), arg.getDisplayName(), this.server, arg, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withPosition(Vec3d arg) {
        if (this.position.equals(arg)) {
            return this;
        }
        return new ServerCommandSource(this.output, arg, this.rotation, this.world, this.level, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withRotation(Vec2f arg) {
        if (this.rotation.equals(arg)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, arg, this.world, this.level, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withConsumer(ResultConsumer<ServerCommandSource> resultConsumer) {
        if (this.resultConsumer.equals(resultConsumer)) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.server, this.entity, this.silent, resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource mergeConsumers(ResultConsumer<ServerCommandSource> resultConsumer, BinaryOperator<ResultConsumer<ServerCommandSource>> binaryOperator) {
        ResultConsumer resultConsumer2 = (ResultConsumer)binaryOperator.apply(this.resultConsumer, resultConsumer);
        return this.withConsumer((ResultConsumer<ServerCommandSource>)resultConsumer2);
    }

    public ServerCommandSource withSilent() {
        if (this.silent) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.server, this.entity, true, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withLevel(int i) {
        if (i == this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, i, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withMaxLevel(int i) {
        if (i <= this.level) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, i, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor arg) {
        if (arg == this.entityAnchor) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, arg);
    }

    public ServerCommandSource withWorld(ServerWorld arg) {
        if (arg == this.world) {
            return this;
        }
        return new ServerCommandSource(this.output, this.position, this.rotation, arg, this.level, this.simpleName, this.name, this.server, this.entity, this.silent, this.resultConsumer, this.entityAnchor);
    }

    public ServerCommandSource withLookingAt(Entity arg, EntityAnchorArgumentType.EntityAnchor arg2) throws CommandSyntaxException {
        return this.withLookingAt(arg2.positionAt(arg));
    }

    public ServerCommandSource withLookingAt(Vec3d arg) throws CommandSyntaxException {
        Vec3d lv = this.entityAnchor.positionAt(this);
        double d = arg.x - lv.x;
        double e = arg.y - lv.y;
        double f = arg.z - lv.z;
        double g = MathHelper.sqrt(d * d + f * f);
        float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2f(h, i));
    }

    public Text getDisplayName() {
        return this.name;
    }

    public String getName() {
        return this.simpleName;
    }

    @Override
    public boolean hasPermissionLevel(int i) {
        return this.level >= i;
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrThrow() throws CommandSyntaxException {
        if (this.entity == null) {
            throw REQUIRES_ENTITY_EXCEPTION.create();
        }
        return this.entity;
    }

    public ServerPlayerEntity getPlayer() throws CommandSyntaxException {
        if (!(this.entity instanceof ServerPlayerEntity)) {
            throw REQUIRES_PLAYER_EXCEPTION.create();
        }
        return (ServerPlayerEntity)this.entity;
    }

    public Vec2f getRotation() {
        return this.rotation;
    }

    public MinecraftServer getMinecraftServer() {
        return this.server;
    }

    public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
        return this.entityAnchor;
    }

    public void sendFeedback(Text arg, boolean bl) {
        if (this.output.shouldReceiveFeedback() && !this.silent) {
            this.output.sendSystemMessage(arg, Util.NIL_UUID);
        }
        if (bl && this.output.shouldBroadcastConsoleToOps() && !this.silent) {
            this.sendToOps(arg);
        }
    }

    private void sendToOps(Text arg) {
        MutableText lv = new TranslatableText("chat.type.admin", this.getDisplayName(), arg).formatted(Formatting.GRAY, Formatting.ITALIC);
        if (this.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
            for (ServerPlayerEntity lv2 : this.server.getPlayerManager().getPlayerList()) {
                if (lv2 == this.output || !this.server.getPlayerManager().isOperator(lv2.getGameProfile())) continue;
                lv2.sendSystemMessage(lv, Util.NIL_UUID);
            }
        }
        if (this.output != this.server && this.server.getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
            this.server.sendSystemMessage(lv, Util.NIL_UUID);
        }
    }

    public void sendError(Text arg) {
        if (this.output.shouldTrackOutput() && !this.silent) {
            this.output.sendSystemMessage(new LiteralText("").append(arg).formatted(Formatting.RED), Util.NIL_UUID);
        }
    }

    public void onCommandComplete(CommandContext<ServerCommandSource> commandContext, boolean bl, int i) {
        if (this.resultConsumer != null) {
            this.resultConsumer.onCommandComplete(commandContext, bl, i);
        }
    }

    @Override
    public Collection<String> getPlayerNames() {
        return Lists.newArrayList((Object[])this.server.getPlayerNames());
    }

    @Override
    public Collection<String> getTeamNames() {
        return this.server.getScoreboard().getTeamNames();
    }

    @Override
    public Collection<Identifier> getSoundIds() {
        return Registry.SOUND_EVENT.getIds();
    }

    @Override
    public Stream<Identifier> getRecipeIds() {
        return this.server.getRecipeManager().keys();
    }

    @Override
    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return null;
    }

    @Override
    public Set<RegistryKey<World>> getWorldKeys() {
        return this.server.getWorldRegistryKeys();
    }

    @Override
    public class_5455 method_30497() {
        return this.server.method_30611();
    }
}

