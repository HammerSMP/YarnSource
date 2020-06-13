/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.PosAndRot;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerInteractionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final ClientPlayNetworkHandler networkHandler;
    private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
    private ItemStack selectedStack = ItemStack.EMPTY;
    private float currentBreakingProgress;
    private float blockBreakingSoundCooldown;
    private int blockBreakingCooldown;
    private boolean breakingBlock;
    private GameMode gameMode = GameMode.SURVIVAL;
    private GameMode previousGameMode = GameMode.SURVIVAL;
    private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, PlayerActionC2SPacket.Action>, PosAndRot> unacknowledgedPlayerActions = new Object2ObjectLinkedOpenHashMap();
    private int lastSelectedSlot;

    public ClientPlayerInteractionManager(MinecraftClient arg, ClientPlayNetworkHandler arg2) {
        this.client = arg;
        this.networkHandler = arg2;
    }

    public void copyAbilities(PlayerEntity arg) {
        this.gameMode.setAbilitites(arg.abilities);
    }

    public void setGameMode(GameMode arg) {
        if (arg != this.gameMode) {
            this.previousGameMode = this.gameMode;
        }
        this.gameMode = arg;
        this.gameMode.setAbilitites(this.client.player.abilities);
    }

    public boolean hasStatusBars() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean breakBlock(BlockPos arg) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, arg, this.gameMode)) {
            return false;
        }
        ClientWorld lv = this.client.world;
        BlockState lv2 = lv.getBlockState(arg);
        if (!this.client.player.getMainHandStack().getItem().canMine(lv2, lv, arg, this.client.player)) {
            return false;
        }
        Block lv3 = lv2.getBlock();
        if ((lv3 instanceof CommandBlock || lv3 instanceof StructureBlock || lv3 instanceof JigsawBlock) && !this.client.player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (lv2.isAir()) {
            return false;
        }
        lv3.onBreak(lv, arg, lv2, this.client.player);
        FluidState lv4 = lv.getFluidState(arg);
        boolean bl = lv.setBlockState(arg, lv4.getBlockState(), 11);
        if (bl) {
            lv3.onBroken(lv, arg, lv2);
        }
        return bl;
    }

    public boolean attackBlock(BlockPos arg, Direction arg2) {
        if (this.client.player.isBlockBreakingRestricted(this.client.world, arg, this.gameMode)) {
            return false;
        }
        if (!this.client.world.getWorldBorder().contains(arg)) {
            return false;
        }
        if (this.gameMode.isCreative()) {
            BlockState lv = this.client.world.getBlockState(arg);
            this.client.getTutorialManager().onBlockAttacked(this.client.world, arg, lv, 1.0f);
            this.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, arg, arg2);
            this.breakBlock(arg);
            this.blockBreakingCooldown = 5;
        } else if (!this.breakingBlock || !this.isCurrentlyBreaking(arg)) {
            boolean bl;
            if (this.breakingBlock) {
                this.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, arg2);
            }
            BlockState lv2 = this.client.world.getBlockState(arg);
            this.client.getTutorialManager().onBlockAttacked(this.client.world, arg, lv2, 0.0f);
            this.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, arg, arg2);
            boolean bl2 = bl = !lv2.isAir();
            if (bl && this.currentBreakingProgress == 0.0f) {
                lv2.onBlockBreakStart(this.client.world, arg, this.client.player);
            }
            if (bl && lv2.calcBlockBreakingDelta(this.client.player, this.client.player.world, arg) >= 1.0f) {
                this.breakBlock(arg);
            } else {
                this.breakingBlock = true;
                this.currentBreakingPos = arg;
                this.selectedStack = this.client.player.getMainHandStack();
                this.currentBreakingProgress = 0.0f;
                this.blockBreakingSoundCooldown = 0.0f;
                this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
            }
        }
        return true;
    }

    public void cancelBlockBreaking() {
        if (this.breakingBlock) {
            BlockState lv = this.client.world.getBlockState(this.currentBreakingPos);
            this.client.getTutorialManager().onBlockAttacked(this.client.world, this.currentBreakingPos, lv, -1.0f);
            this.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN);
            this.breakingBlock = false;
            this.currentBreakingProgress = 0.0f;
            this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, -1);
            this.client.player.resetLastAttackedTicks();
        }
    }

    public boolean updateBlockBreakingProgress(BlockPos arg, Direction arg2) {
        this.syncSelectedSlot();
        if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            return true;
        }
        if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(arg)) {
            this.blockBreakingCooldown = 5;
            BlockState lv = this.client.world.getBlockState(arg);
            this.client.getTutorialManager().onBlockAttacked(this.client.world, arg, lv, 1.0f);
            this.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, arg, arg2);
            this.breakBlock(arg);
            return true;
        }
        if (this.isCurrentlyBreaking(arg)) {
            BlockState lv2 = this.client.world.getBlockState(arg);
            if (lv2.isAir()) {
                this.breakingBlock = false;
                return false;
            }
            this.currentBreakingProgress += lv2.calcBlockBreakingDelta(this.client.player, this.client.player.world, arg);
            if (this.blockBreakingSoundCooldown % 4.0f == 0.0f) {
                BlockSoundGroup lv3 = lv2.getSoundGroup();
                this.client.getSoundManager().play(new PositionedSoundInstance(lv3.getHitSound(), SoundCategory.BLOCKS, (lv3.getVolume() + 1.0f) / 8.0f, lv3.getPitch() * 0.5f, arg));
            }
            this.blockBreakingSoundCooldown += 1.0f;
            this.client.getTutorialManager().onBlockAttacked(this.client.world, arg, lv2, MathHelper.clamp(this.currentBreakingProgress, 0.0f, 1.0f));
            if (this.currentBreakingProgress >= 1.0f) {
                this.breakingBlock = false;
                this.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, arg, arg2);
                this.breakBlock(arg);
                this.currentBreakingProgress = 0.0f;
                this.blockBreakingSoundCooldown = 0.0f;
                this.blockBreakingCooldown = 5;
            }
        } else {
            return this.attackBlock(arg, arg2);
        }
        this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
        return true;
    }

    public float getReachDistance() {
        if (this.gameMode.isCreative()) {
            return 5.0f;
        }
        return 4.5f;
    }

    public void tick() {
        this.syncSelectedSlot();
        if (this.networkHandler.getConnection().isOpen()) {
            this.networkHandler.getConnection().tick();
        } else {
            this.networkHandler.getConnection().handleDisconnection();
        }
    }

    private boolean isCurrentlyBreaking(BlockPos arg) {
        boolean bl;
        ItemStack lv = this.client.player.getMainHandStack();
        boolean bl2 = bl = this.selectedStack.isEmpty() && lv.isEmpty();
        if (!this.selectedStack.isEmpty() && !lv.isEmpty()) {
            bl = lv.getItem() == this.selectedStack.getItem() && ItemStack.areTagsEqual(lv, this.selectedStack) && (lv.isDamageable() || lv.getDamage() == this.selectedStack.getDamage());
        }
        return arg.equals(this.currentBreakingPos) && bl;
    }

    private void syncSelectedSlot() {
        int i = this.client.player.inventory.selectedSlot;
        if (i != this.lastSelectedSlot) {
            this.lastSelectedSlot = i;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
        }
    }

    public ActionResult interactBlock(ClientPlayerEntity arg, ClientWorld arg2, Hand arg3, BlockHitResult arg4) {
        ActionResult lv6;
        ActionResult lv3;
        boolean bl2;
        this.syncSelectedSlot();
        BlockPos lv = arg4.getBlockPos();
        if (!this.client.world.getWorldBorder().contains(lv)) {
            return ActionResult.FAIL;
        }
        ItemStack lv2 = arg.getStackInHand(arg3);
        if (this.gameMode == GameMode.SPECTATOR) {
            this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(arg3, arg4));
            return ActionResult.SUCCESS;
        }
        boolean bl = !arg.getMainHandStack().isEmpty() || !arg.getOffHandStack().isEmpty();
        boolean bl3 = bl2 = arg.shouldCancelInteraction() && bl;
        if (!bl2 && (lv3 = arg2.getBlockState(lv).onUse(arg2, arg, arg3, arg4)).isAccepted()) {
            this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(arg3, arg4));
            return lv3;
        }
        this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(arg3, arg4));
        if (lv2.isEmpty() || arg.getItemCooldownManager().isCoolingDown(lv2.getItem())) {
            return ActionResult.PASS;
        }
        ItemUsageContext lv4 = new ItemUsageContext(arg, arg3, arg4);
        if (this.gameMode.isCreative()) {
            int i = lv2.getCount();
            ActionResult lv5 = lv2.useOnBlock(lv4);
            lv2.setCount(i);
        } else {
            lv6 = lv2.useOnBlock(lv4);
        }
        return lv6;
    }

    public ActionResult interactItem(PlayerEntity arg, World arg2, Hand arg3) {
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(arg3));
        ItemStack lv = arg.getStackInHand(arg3);
        if (arg.getItemCooldownManager().isCoolingDown(lv.getItem())) {
            return ActionResult.PASS;
        }
        int i = lv.getCount();
        TypedActionResult<ItemStack> lv2 = lv.use(arg2, arg, arg3);
        ItemStack lv3 = lv2.getValue();
        if (lv3 != lv) {
            arg.setStackInHand(arg3, lv3);
        }
        return lv2.getResult();
    }

    public ClientPlayerEntity method_29357(ClientWorld arg, StatHandler arg2, ClientRecipeBook arg3) {
        return this.createPlayer(arg, arg2, arg3, false, false);
    }

    public ClientPlayerEntity createPlayer(ClientWorld arg, StatHandler arg2, ClientRecipeBook arg3, boolean bl, boolean bl2) {
        return new ClientPlayerEntity(this.client, arg, this.networkHandler, arg2, arg3, bl, bl2);
    }

    public void attackEntity(PlayerEntity arg, Entity arg2) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(arg2, arg.isSneaking()));
        if (this.gameMode != GameMode.SPECTATOR) {
            arg.attack(arg2);
            arg.resetLastAttackedTicks();
        }
    }

    public ActionResult interactEntity(PlayerEntity arg, Entity arg2, Hand arg3) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(arg2, arg3, arg.isSneaking()));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return arg.interact(arg2, arg3);
    }

    public ActionResult interactEntityAtLocation(PlayerEntity arg, Entity arg2, EntityHitResult arg3, Hand arg4) {
        this.syncSelectedSlot();
        Vec3d lv = arg3.getPos().subtract(arg2.getX(), arg2.getY(), arg2.getZ());
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(arg2, arg4, lv, arg.isSneaking()));
        if (this.gameMode == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        }
        return arg2.interactAt(arg, lv, arg4);
    }

    public ItemStack clickSlot(int i, int j, int k, SlotActionType arg, PlayerEntity arg2) {
        short s = arg2.currentScreenHandler.getNextActionId(arg2.inventory);
        ItemStack lv = arg2.currentScreenHandler.onSlotClick(j, k, arg, arg2);
        this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, arg, lv, s));
        return lv;
    }

    public void clickRecipe(int i, Recipe<?> arg, boolean bl) {
        this.networkHandler.sendPacket(new CraftRequestC2SPacket(i, arg, bl));
    }

    public void clickButton(int i, int j) {
        this.networkHandler.sendPacket(new ButtonClickC2SPacket(i, j));
    }

    public void clickCreativeStack(ItemStack arg, int i) {
        if (this.gameMode.isCreative()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(i, arg));
        }
    }

    public void dropCreativeStack(ItemStack arg) {
        if (this.gameMode.isCreative() && !arg.isEmpty()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, arg));
        }
    }

    public void stopUsingItem(PlayerEntity arg) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
        arg.stopUsingItem();
    }

    public boolean hasExperienceBar() {
        return this.gameMode.isSurvivalLike();
    }

    public boolean hasLimitedAttackSpeed() {
        return !this.gameMode.isCreative();
    }

    public boolean hasCreativeInventory() {
        return this.gameMode.isCreative();
    }

    public boolean hasExtendedReach() {
        return this.gameMode.isCreative();
    }

    public boolean hasRidingInventory() {
        return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof HorseBaseEntity;
    }

    public boolean isFlyingLocked() {
        return this.gameMode == GameMode.SPECTATOR;
    }

    public GameMode getPreviousGameMode() {
        return this.previousGameMode;
    }

    public GameMode getCurrentGameMode() {
        return this.gameMode;
    }

    public boolean isBreakingBlock() {
        return this.breakingBlock;
    }

    public void pickFromInventory(int i) {
        this.networkHandler.sendPacket(new PickFromInventoryC2SPacket(i));
    }

    private void sendPlayerAction(PlayerActionC2SPacket.Action arg, BlockPos arg2, Direction arg3) {
        ClientPlayerEntity lv = this.client.player;
        this.unacknowledgedPlayerActions.put((Object)Pair.of((Object)arg2, (Object)((Object)arg)), (Object)new PosAndRot(lv.getPos(), lv.pitch, lv.yaw));
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(arg, arg2, arg3));
    }

    public void processPlayerActionResponse(ClientWorld arg, BlockPos arg2, BlockState arg3, PlayerActionC2SPacket.Action arg4, boolean bl) {
        PosAndRot lv = (PosAndRot)this.unacknowledgedPlayerActions.remove((Object)Pair.of((Object)arg2, (Object)((Object)arg4)));
        BlockState lv2 = arg.getBlockState(arg2);
        if ((lv == null || !bl || arg4 != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK && lv2 != arg3) && lv2 != arg3) {
            arg.setBlockStateWithoutNeighborUpdates(arg2, arg3);
            if (lv != null) {
                Vec3d lv3 = lv.getPos();
                this.client.player.updatePositionAndAngles(lv3.x, lv3.y, lv3.z, lv.getYaw(), lv.getPitch());
            }
        }
        while (this.unacknowledgedPlayerActions.size() >= 50) {
            Pair pair = (Pair)this.unacknowledgedPlayerActions.firstKey();
            this.unacknowledgedPlayerActions.removeFirst();
            LOGGER.error("Too many unacked block actions, dropping " + (Object)pair);
        }
    }
}

