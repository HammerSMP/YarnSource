/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import java.util.Objects;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerInteractionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerWorld world;
    public ServerPlayerEntity player;
    private GameMode gameMode = GameMode.NOT_SET;
    private GameMode field_25715 = GameMode.NOT_SET;
    private boolean mining;
    private int startMiningTime;
    private BlockPos miningPos = BlockPos.ORIGIN;
    private int tickCounter;
    private boolean failedToMine;
    private BlockPos failedMiningPos = BlockPos.ORIGIN;
    private int failedStartMiningTime;
    private int blockBreakingProgress = -1;

    public ServerPlayerInteractionManager(ServerWorld arg) {
        this.world = arg;
    }

    public void method_30118(GameMode arg) {
        this.setGameMode(arg, arg != this.gameMode ? this.gameMode : this.field_25715);
    }

    public void setGameMode(GameMode arg, GameMode arg2) {
        this.field_25715 = arg2;
        this.gameMode = arg;
        arg.setAbilities(this.player.abilities);
        this.player.sendAbilitiesUpdate();
        this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, this.player));
        this.world.updateSleepingPlayers();
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public GameMode method_30119() {
        return this.field_25715;
    }

    public boolean isSurvivalLike() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean isCreative() {
        return this.gameMode.isCreative();
    }

    public void setGameModeIfNotPresent(GameMode arg) {
        if (this.gameMode == GameMode.NOT_SET) {
            this.gameMode = arg;
        }
        this.method_30118(this.gameMode);
    }

    public void update() {
        ++this.tickCounter;
        if (this.failedToMine) {
            BlockState lv = this.world.getBlockState(this.failedMiningPos);
            if (lv.isAir()) {
                this.failedToMine = false;
            } else {
                float f = this.continueMining(lv, this.failedMiningPos, this.failedStartMiningTime);
                if (f >= 1.0f) {
                    this.failedToMine = false;
                    this.tryBreakBlock(this.failedMiningPos);
                }
            }
        } else if (this.mining) {
            BlockState lv2 = this.world.getBlockState(this.miningPos);
            if (lv2.isAir()) {
                this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, -1);
                this.blockBreakingProgress = -1;
                this.mining = false;
            } else {
                this.continueMining(lv2, this.miningPos, this.startMiningTime);
            }
        }
    }

    private float continueMining(BlockState arg, BlockPos arg2, int i) {
        int j = this.tickCounter - i;
        float f = arg.calcBlockBreakingDelta(this.player, this.player.world, arg2) * (float)(j + 1);
        int k = (int)(f * 10.0f);
        if (k != this.blockBreakingProgress) {
            this.world.setBlockBreakingInfo(this.player.getEntityId(), arg2, k);
            this.blockBreakingProgress = k;
        }
        return f;
    }

    public void processBlockBreakingAction(BlockPos arg, PlayerActionC2SPacket.Action arg2, Direction arg3, int i) {
        double f;
        double e;
        double d = this.player.getX() - ((double)arg.getX() + 0.5);
        double g = d * d + (e = this.player.getY() - ((double)arg.getY() + 0.5) + 1.5) * e + (f = this.player.getZ() - ((double)arg.getZ() + 0.5)) * f;
        if (g > 36.0) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, false, "too far"));
            return;
        }
        if (arg.getY() >= i) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, false, "too high"));
            return;
        }
        if (arg2 == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            if (!this.world.canPlayerModifyAt(this.player, arg)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, false, "may not interact"));
                return;
            }
            if (this.isCreative()) {
                this.finishMining(arg, arg2, "creative destroy");
                return;
            }
            if (this.player.isBlockBreakingRestricted(this.world, arg, this.gameMode)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, false, "block action restricted"));
                return;
            }
            this.startMiningTime = this.tickCounter;
            float h = 1.0f;
            BlockState lv = this.world.getBlockState(arg);
            if (!lv.isAir()) {
                lv.onBlockBreakStart(this.world, arg, this.player);
                h = lv.calcBlockBreakingDelta(this.player, this.player.world, arg);
            }
            if (!lv.isAir() && h >= 1.0f) {
                this.finishMining(arg, arg2, "insta mine");
            } else {
                if (this.mining) {
                    this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
                }
                this.mining = true;
                this.miningPos = arg.toImmutable();
                int j = (int)(h * 10.0f);
                this.world.setBlockBreakingInfo(this.player.getEntityId(), arg, j);
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, true, "actual start of destroying"));
                this.blockBreakingProgress = j;
            }
        } else if (arg2 == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (arg.equals(this.miningPos)) {
                int k = this.tickCounter - this.startMiningTime;
                BlockState lv2 = this.world.getBlockState(arg);
                if (!lv2.isAir()) {
                    float l = lv2.calcBlockBreakingDelta(this.player, this.player.world, arg) * (float)(k + 1);
                    if (l >= 0.7f) {
                        this.mining = false;
                        this.world.setBlockBreakingInfo(this.player.getEntityId(), arg, -1);
                        this.finishMining(arg, arg2, "destroyed");
                        return;
                    }
                    if (!this.failedToMine) {
                        this.mining = false;
                        this.failedToMine = true;
                        this.failedMiningPos = arg;
                        this.failedStartMiningTime = this.startMiningTime;
                    }
                }
            }
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, true, "stopped destroying"));
        } else if (arg2 == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
            this.mining = false;
            if (!Objects.equals(this.miningPos, arg)) {
                LOGGER.warn("Mismatch in destroy block pos: " + this.miningPos + " " + arg);
                this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, -1);
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), arg2, true, "aborted mismatched destroying"));
            }
            this.world.setBlockBreakingInfo(this.player.getEntityId(), arg, -1);
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, true, "aborted destroying"));
        }
    }

    public void finishMining(BlockPos arg, PlayerActionC2SPacket.Action arg2, String string) {
        if (this.tryBreakBlock(arg)) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, true, string));
        } else {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(arg, this.world.getBlockState(arg), arg2, false, string));
        }
    }

    public boolean tryBreakBlock(BlockPos arg) {
        BlockState lv = this.world.getBlockState(arg);
        if (!this.player.getMainHandStack().getItem().canMine(lv, this.world, arg, this.player)) {
            return false;
        }
        BlockEntity lv2 = this.world.getBlockEntity(arg);
        Block lv3 = lv.getBlock();
        if ((lv3 instanceof CommandBlock || lv3 instanceof StructureBlock || lv3 instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
            this.world.updateListeners(arg, lv, lv, 3);
            return false;
        }
        if (this.player.isBlockBreakingRestricted(this.world, arg, this.gameMode)) {
            return false;
        }
        lv3.onBreak(this.world, arg, lv, this.player);
        boolean bl = this.world.removeBlock(arg, false);
        if (bl) {
            lv3.onBroken(this.world, arg, lv);
        }
        if (this.isCreative()) {
            return true;
        }
        ItemStack lv4 = this.player.getMainHandStack();
        ItemStack lv5 = lv4.copy();
        boolean bl2 = this.player.isUsingEffectiveTool(lv);
        lv4.postMine(this.world, lv, arg, this.player);
        if (bl && bl2) {
            lv3.afterBreak(this.world, this.player, arg, lv, lv2, lv5);
        }
        return true;
    }

    public ActionResult interactItem(ServerPlayerEntity arg, World arg2, ItemStack arg3, Hand arg4) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        if (arg.getItemCooldownManager().isCoolingDown(arg3.getItem())) {
            return ActionResult.PASS;
        }
        int i = arg3.getCount();
        int j = arg3.getDamage();
        TypedActionResult<ItemStack> lv = arg3.use(arg2, arg, arg4);
        ItemStack lv2 = lv.getValue();
        if (lv2 == arg3 && lv2.getCount() == i && lv2.getMaxUseTime() <= 0 && lv2.getDamage() == j) {
            return lv.getResult();
        }
        if (lv.getResult() == ActionResult.FAIL && lv2.getMaxUseTime() > 0 && !arg.isUsingItem()) {
            return lv.getResult();
        }
        arg.setStackInHand(arg4, lv2);
        if (this.isCreative()) {
            lv2.setCount(i);
            if (lv2.isDamageable() && lv2.getDamage() != j) {
                lv2.setDamage(j);
            }
        }
        if (lv2.isEmpty()) {
            arg.setStackInHand(arg4, ItemStack.EMPTY);
        }
        if (!arg.isUsingItem()) {
            arg.openHandledScreen(arg.playerScreenHandler);
        }
        return lv.getResult();
    }

    public ActionResult interactBlock(ServerPlayerEntity arg, World arg2, ItemStack arg3, Hand arg4, BlockHitResult arg5) {
        ActionResult lv8;
        ActionResult lv5;
        BlockPos lv = arg5.getBlockPos();
        BlockState lv2 = arg2.getBlockState(lv);
        if (this.gameMode == GameMode.SPECTATOR) {
            NamedScreenHandlerFactory lv3 = lv2.createScreenHandlerFactory(arg2, lv);
            if (lv3 != null) {
                arg.openHandledScreen(lv3);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        boolean bl = !arg.getMainHandStack().isEmpty() || !arg.getOffHandStack().isEmpty();
        boolean bl2 = arg.shouldCancelInteraction() && bl;
        ItemStack lv4 = arg3.copy();
        if (!bl2 && (lv5 = lv2.onUse(arg2, arg, arg4, arg5)).isAccepted()) {
            Criteria.ITEM_USED_ON_BLOCK.test(arg, lv, lv4);
            return lv5;
        }
        if (arg3.isEmpty() || arg.getItemCooldownManager().isCoolingDown(arg3.getItem())) {
            return ActionResult.PASS;
        }
        ItemUsageContext lv6 = new ItemUsageContext(arg, arg4, arg5);
        if (this.isCreative()) {
            int i = arg3.getCount();
            ActionResult lv7 = arg3.useOnBlock(lv6);
            arg3.setCount(i);
        } else {
            lv8 = arg3.useOnBlock(lv6);
        }
        if (lv8.isAccepted()) {
            Criteria.ITEM_USED_ON_BLOCK.test(arg, lv, lv4);
        }
        return lv8;
    }

    public void setWorld(ServerWorld arg) {
        this.world = arg;
    }
}

