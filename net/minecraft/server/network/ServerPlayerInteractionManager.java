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

    public ServerPlayerInteractionManager(ServerWorld world) {
        this.world = world;
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

    private float continueMining(BlockState state, BlockPos pos, int i) {
        int j = this.tickCounter - i;
        float f = state.calcBlockBreakingDelta(this.player, this.player.world, pos) * (float)(j + 1);
        int k = (int)(f * 10.0f);
        if (k != this.blockBreakingProgress) {
            this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, k);
            this.blockBreakingProgress = k;
        }
        return f;
    }

    public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        double f;
        double e;
        double d = this.player.getX() - ((double)pos.getX() + 0.5);
        double g = d * d + (e = this.player.getY() - ((double)pos.getY() + 0.5) + 1.5) * e + (f = this.player.getZ() - ((double)pos.getZ() + 0.5)) * f;
        if (g > 36.0) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "too far"));
            return;
        }
        if (pos.getY() >= worldHeight) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "too high"));
            return;
        }
        if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            if (!this.world.canPlayerModifyAt(this.player, pos)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "may not interact"));
                return;
            }
            if (this.isCreative()) {
                this.finishMining(pos, action, "creative destroy");
                return;
            }
            if (this.player.isBlockBreakingRestricted(this.world, pos, this.gameMode)) {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "block action restricted"));
                return;
            }
            this.startMiningTime = this.tickCounter;
            float h = 1.0f;
            BlockState lv = this.world.getBlockState(pos);
            if (!lv.isAir()) {
                lv.onBlockBreakStart(this.world, pos, this.player);
                h = lv.calcBlockBreakingDelta(this.player, this.player.world, pos);
            }
            if (!lv.isAir() && h >= 1.0f) {
                this.finishMining(pos, action, "insta mine");
            } else {
                if (this.mining) {
                    this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
                }
                this.mining = true;
                this.miningPos = pos.toImmutable();
                int j = (int)(h * 10.0f);
                this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, j);
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "actual start of destroying"));
                this.blockBreakingProgress = j;
            }
        } else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
            if (pos.equals(this.miningPos)) {
                int k = this.tickCounter - this.startMiningTime;
                BlockState lv2 = this.world.getBlockState(pos);
                if (!lv2.isAir()) {
                    float l = lv2.calcBlockBreakingDelta(this.player, this.player.world, pos) * (float)(k + 1);
                    if (l >= 0.7f) {
                        this.mining = false;
                        this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, -1);
                        this.finishMining(pos, action, "destroyed");
                        return;
                    }
                    if (!this.failedToMine) {
                        this.mining = false;
                        this.failedToMine = true;
                        this.failedMiningPos = pos;
                        this.failedStartMiningTime = this.startMiningTime;
                    }
                }
            }
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "stopped destroying"));
        } else if (action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
            this.mining = false;
            if (!Objects.equals(this.miningPos, pos)) {
                LOGGER.warn("Mismatch in destroy block pos: " + this.miningPos + " " + pos);
                this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, -1);
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(this.miningPos, this.world.getBlockState(this.miningPos), action, true, "aborted mismatched destroying"));
            }
            this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, -1);
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, "aborted destroying"));
        }
    }

    public void finishMining(BlockPos pos, PlayerActionC2SPacket.Action action, String reason) {
        if (this.tryBreakBlock(pos)) {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, true, reason));
        } else {
            this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, reason));
        }
    }

    public boolean tryBreakBlock(BlockPos pos) {
        BlockState lv = this.world.getBlockState(pos);
        if (!this.player.getMainHandStack().getItem().canMine(lv, this.world, pos, this.player)) {
            return false;
        }
        BlockEntity lv2 = this.world.getBlockEntity(pos);
        Block lv3 = lv.getBlock();
        if ((lv3 instanceof CommandBlock || lv3 instanceof StructureBlock || lv3 instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
            this.world.updateListeners(pos, lv, lv, 3);
            return false;
        }
        if (this.player.isBlockBreakingRestricted(this.world, pos, this.gameMode)) {
            return false;
        }
        lv3.onBreak(this.world, pos, lv, this.player);
        boolean bl = this.world.removeBlock(pos, false);
        if (bl) {
            lv3.onBroken(this.world, pos, lv);
        }
        if (this.isCreative()) {
            return true;
        }
        ItemStack lv4 = this.player.getMainHandStack();
        ItemStack lv5 = lv4.copy();
        boolean bl2 = this.player.isUsingEffectiveTool(lv);
        lv4.postMine(this.world, lv, pos, this.player);
        if (bl && bl2) {
            lv3.afterBreak(this.world, this.player, pos, lv, lv2, lv5);
        }
        return true;
    }

    public ActionResult interactItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        if (player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return ActionResult.PASS;
        }
        int i = stack.getCount();
        int j = stack.getDamage();
        TypedActionResult<ItemStack> lv = stack.use(world, player, hand);
        ItemStack lv2 = lv.getValue();
        if (lv2 == stack && lv2.getCount() == i && lv2.getMaxUseTime() <= 0 && lv2.getDamage() == j) {
            return lv.getResult();
        }
        if (lv.getResult() == ActionResult.FAIL && lv2.getMaxUseTime() > 0 && !player.isUsingItem()) {
            return lv.getResult();
        }
        player.setStackInHand(hand, lv2);
        if (this.isCreative()) {
            lv2.setCount(i);
            if (lv2.isDamageable() && lv2.getDamage() != j) {
                lv2.setDamage(j);
            }
        }
        if (lv2.isEmpty()) {
            player.setStackInHand(hand, ItemStack.EMPTY);
        }
        if (!player.isUsingItem()) {
            player.openHandledScreen(player.playerScreenHandler);
        }
        return lv.getResult();
    }

    public ActionResult interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
        ActionResult lv8;
        ActionResult lv5;
        BlockPos lv = hitResult.getBlockPos();
        BlockState lv2 = world.getBlockState(lv);
        if (this.gameMode == GameMode.SPECTATOR) {
            NamedScreenHandlerFactory lv3 = lv2.createScreenHandlerFactory(world, lv);
            if (lv3 != null) {
                player.openHandledScreen(lv3);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        boolean bl = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        boolean bl2 = player.shouldCancelInteraction() && bl;
        ItemStack lv4 = stack.copy();
        if (!bl2 && (lv5 = lv2.onUse(world, player, hand, hitResult)).isAccepted()) {
            Criteria.ITEM_USED_ON_BLOCK.test(player, lv, lv4);
            return lv5;
        }
        if (stack.isEmpty() || player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return ActionResult.PASS;
        }
        ItemUsageContext lv6 = new ItemUsageContext(player, hand, hitResult);
        if (this.isCreative()) {
            int i = stack.getCount();
            ActionResult lv7 = stack.useOnBlock(lv6);
            stack.setCount(i);
        } else {
            lv8 = stack.useOnBlock(lv6);
        }
        if (lv8.isAccepted()) {
            Criteria.ITEM_USED_ON_BLOCK.test(player, lv, lv4);
        }
        return lv8;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }
}

