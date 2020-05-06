/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ClientCommandSource
implements CommandSource {
    private final ClientPlayNetworkHandler networkHandler;
    private final MinecraftClient client;
    private int completionId = -1;
    private CompletableFuture<Suggestions> pendingCompletion;

    public ClientCommandSource(ClientPlayNetworkHandler arg, MinecraftClient arg2) {
        this.networkHandler = arg;
        this.client = arg2;
    }

    @Override
    public Collection<String> getPlayerNames() {
        ArrayList list = Lists.newArrayList();
        for (PlayerListEntry lv : this.networkHandler.getPlayerList()) {
            list.add(lv.getProfile().getName());
        }
        return list;
    }

    @Override
    public Collection<String> getEntitySuggestions() {
        if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return Collections.singleton(((EntityHitResult)this.client.crosshairTarget).getEntity().getUuidAsString());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTeamNames() {
        return this.networkHandler.getWorld().getScoreboard().getTeamNames();
    }

    @Override
    public Collection<Identifier> getSoundIds() {
        return this.client.getSoundManager().getKeys();
    }

    @Override
    public Stream<Identifier> getRecipeIds() {
        return this.networkHandler.getRecipeManager().keys();
    }

    @Override
    public boolean hasPermissionLevel(int i) {
        ClientPlayerEntity lv = this.client.player;
        return lv != null ? lv.hasPermissionLevel(i) : i == 0;
    }

    @Override
    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (this.pendingCompletion != null) {
            this.pendingCompletion.cancel(false);
        }
        this.pendingCompletion = new CompletableFuture();
        int i = ++this.completionId;
        this.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(i, commandContext.getInput()));
        return this.pendingCompletion;
    }

    private static String format(double d) {
        return String.format(Locale.ROOT, "%.2f", d);
    }

    private static String format(int i) {
        return Integer.toString(i);
    }

    @Override
    public Collection<CommandSource.RelativePosition> getBlockPositionSuggestions() {
        HitResult lv = this.client.crosshairTarget;
        if (lv == null || lv.getType() != HitResult.Type.BLOCK) {
            return CommandSource.super.getBlockPositionSuggestions();
        }
        BlockPos lv2 = ((BlockHitResult)lv).getBlockPos();
        return Collections.singleton(new CommandSource.RelativePosition(ClientCommandSource.format(lv2.getX()), ClientCommandSource.format(lv2.getY()), ClientCommandSource.format(lv2.getZ())));
    }

    @Override
    public Collection<CommandSource.RelativePosition> getPositionSuggestions() {
        HitResult lv = this.client.crosshairTarget;
        if (lv == null || lv.getType() != HitResult.Type.BLOCK) {
            return CommandSource.super.getPositionSuggestions();
        }
        Vec3d lv2 = lv.getPos();
        return Collections.singleton(new CommandSource.RelativePosition(ClientCommandSource.format(lv2.x), ClientCommandSource.format(lv2.y), ClientCommandSource.format(lv2.z)));
    }

    public void onCommandSuggestions(int i, Suggestions suggestions) {
        if (i == this.completionId) {
            this.pendingCompletion.complete(suggestions);
            this.pendingCompletion = null;
            this.completionId = -1;
        }
    }
}

