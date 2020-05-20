/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DemoServerPlayerInteractionManager
extends ServerPlayerInteractionManager {
    private boolean sentHelp;
    private boolean demoEnded;
    private int reminderTicks;
    private int tick;

    public DemoServerPlayerInteractionManager(ServerWorld arg) {
        super(arg);
    }

    @Override
    public void update() {
        super.update();
        ++this.tick;
        long l = this.world.getTime();
        long m = l / 24000L + 1L;
        if (!this.sentHelp && this.tick > 20) {
            this.sentHelp = true;
            this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 0.0f));
        }
        boolean bl = this.demoEnded = l > 120500L;
        if (this.demoEnded) {
            ++this.reminderTicks;
        }
        if (l % 24000L == 500L) {
            if (m <= 6L) {
                if (m == 6L) {
                    this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 104.0f));
                } else {
                    this.player.sendSystemMessage(new TranslatableText("demo.day." + m), Util.field_25140);
                }
            }
        } else if (m == 1L) {
            if (l == 100L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 101.0f));
            } else if (l == 175L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 102.0f));
            } else if (l == 250L) {
                this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(5, 103.0f));
            }
        } else if (m == 5L && l % 24000L == 22000L) {
            this.player.sendSystemMessage(new TranslatableText("demo.day.warning"), Util.field_25140);
        }
    }

    private void sendDemoReminder() {
        if (this.reminderTicks > 100) {
            this.player.sendSystemMessage(new TranslatableText("demo.reminder"), Util.field_25140);
            this.reminderTicks = 0;
        }
    }

    @Override
    public void processBlockBreakingAction(BlockPos arg, PlayerActionC2SPacket.Action arg2, Direction arg3, int i) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return;
        }
        super.processBlockBreakingAction(arg, arg2, arg3, i);
    }

    @Override
    public ActionResult interactItem(ServerPlayerEntity arg, World arg2, ItemStack arg3, Hand arg4) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactItem(arg, arg2, arg3, arg4);
    }

    @Override
    public ActionResult interactBlock(ServerPlayerEntity arg, World arg2, ItemStack arg3, Hand arg4, BlockHitResult arg5) {
        if (this.demoEnded) {
            this.sendDemoReminder();
            return ActionResult.PASS;
        }
        return super.interactBlock(arg, arg2, arg3, arg4, arg5);
    }
}

