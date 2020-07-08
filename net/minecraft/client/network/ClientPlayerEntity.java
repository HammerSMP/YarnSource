/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartInsideSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerEntity
extends AbstractClientPlayerEntity {
    public final ClientPlayNetworkHandler networkHandler;
    private final StatHandler statHandler;
    private final ClientRecipeBook recipeBook;
    private final List<ClientPlayerTickable> tickables = Lists.newArrayList();
    private int clientPermissionLevel = 0;
    private double lastX;
    private double lastBaseY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;
    private boolean lastOnGround;
    private boolean inSneakingPose;
    private boolean lastSneaking;
    private boolean lastSprinting;
    private int ticksSinceLastPositionPacketSent;
    private boolean healthInitialized;
    private String serverBrand;
    public Input input;
    protected final MinecraftClient client;
    protected int ticksLeftToDoubleTapSprint;
    public int ticksSinceSprintingChanged;
    public float renderYaw;
    public float renderPitch;
    public float lastRenderYaw;
    public float lastRenderPitch;
    private int field_3938;
    private float field_3922;
    public float nextNauseaStrength;
    public float lastNauseaStrength;
    private boolean usingItem;
    private Hand activeHand;
    private boolean riding;
    private boolean autoJumpEnabled = true;
    private int ticksToNextAutojump;
    private boolean field_3939;
    private int underwaterVisibilityTicks;
    private boolean showsDeathScreen = true;

    public ClientPlayerEntity(MinecraftClient arg, ClientWorld arg2, ClientPlayNetworkHandler arg3, StatHandler arg4, ClientRecipeBook arg5, boolean bl, boolean bl2) {
        super(arg2, arg3.getProfile());
        this.client = arg;
        this.networkHandler = arg3;
        this.statHandler = arg4;
        this.recipeBook = arg5;
        this.lastSneaking = bl;
        this.lastSprinting = bl2;
        this.tickables.add(new AmbientSoundPlayer(this, arg.getSoundManager()));
        this.tickables.add(new BubbleColumnSoundPlayer(this));
        this.tickables.add(new BiomeEffectSoundPlayer(this, arg.getSoundManager(), arg2.getBiomeAccess()));
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        return false;
    }

    @Override
    public void heal(float f) {
    }

    @Override
    public boolean startRiding(Entity arg, boolean bl) {
        if (!super.startRiding(arg, bl)) {
            return false;
        }
        if (arg instanceof AbstractMinecartEntity) {
            this.client.getSoundManager().play(new MinecartInsideSoundInstance(this, (AbstractMinecartEntity)arg));
        }
        if (arg instanceof BoatEntity) {
            this.prevYaw = arg.yaw;
            this.yaw = arg.yaw;
            this.setHeadYaw(arg.yaw);
        }
        return true;
    }

    @Override
    public void method_29239() {
        super.method_29239();
        this.riding = false;
    }

    @Override
    public float getPitch(float f) {
        return this.pitch;
    }

    @Override
    public float getYaw(float f) {
        if (this.hasVehicle()) {
            return super.getYaw(f);
        }
        return this.yaw;
    }

    @Override
    public void tick() {
        if (!this.world.isChunkLoaded(new BlockPos(this.getX(), 0.0, this.getZ()))) {
            return;
        }
        super.tick();
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
            Entity lv = this.getRootVehicle();
            if (lv != this && lv.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket(new VehicleMoveC2SPacket(lv));
            }
        } else {
            this.sendMovementPackets();
        }
        for (ClientPlayerTickable lv2 : this.tickables) {
            lv2.tick();
        }
    }

    public float getMoodPercentage() {
        for (ClientPlayerTickable lv : this.tickables) {
            if (!(lv instanceof BiomeEffectSoundPlayer)) continue;
            return ((BiomeEffectSoundPlayer)lv).getMoodPercentage();
        }
        return 0.0f;
    }

    private void sendMovementPackets() {
        boolean bl2;
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode lv = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, lv));
            this.lastSprinting = bl;
        }
        if ((bl2 = this.isSneaking()) != this.lastSneaking) {
            ClientCommandC2SPacket.Mode lv2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, lv2));
            this.lastSneaking = bl2;
        }
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.yaw - this.lastYaw;
            double h = this.pitch - this.lastPitch;
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl5 = bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d lv3 = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(lv3.x, -999.0, lv3.z, this.yaw, this.pitch, this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch, this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }
            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.yaw;
                this.lastPitch = this.pitch;
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.autoJump;
        }
    }

    @Override
    public boolean dropSelectedItem(boolean bl) {
        PlayerActionC2SPacket.Action lv = bl ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(lv, BlockPos.ORIGIN, Direction.DOWN));
        return this.inventory.removeStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1) != ItemStack.EMPTY;
    }

    public void sendChatMessage(String string) {
        this.networkHandler.sendPacket(new ChatMessageC2SPacket(string));
    }

    @Override
    public void swingHand(Hand arg) {
        super.swingHand(arg);
        this.networkHandler.sendPacket(new HandSwingC2SPacket(arg));
    }

    @Override
    public void requestRespawn() {
        this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
    }

    @Override
    protected void applyDamage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return;
        }
        this.setHealth(this.getHealth() - f);
    }

    @Override
    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.currentScreenHandler.syncId));
        this.closeScreen();
    }

    public void closeScreen() {
        this.inventory.setCursorStack(ItemStack.EMPTY);
        super.closeHandledScreen();
        this.client.openScreen(null);
    }

    public void updateHealth(float f) {
        if (this.healthInitialized) {
            float g = this.getHealth() - f;
            if (g <= 0.0f) {
                this.setHealth(f);
                if (g < 0.0f) {
                    this.timeUntilRegen = 10;
                }
            } else {
                this.lastDamageTaken = g;
                this.setHealth(this.getHealth());
                this.timeUntilRegen = 20;
                this.applyDamage(DamageSource.GENERIC, g);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(f);
            this.healthInitialized = true;
        }
    }

    @Override
    public void sendAbilitiesUpdate() {
        this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.abilities));
    }

    @Override
    public boolean isMainPlayer() {
        return true;
    }

    @Override
    public boolean isHoldingOntoLadder() {
        return !this.abilities.flying && super.isHoldingOntoLadder();
    }

    @Override
    public boolean shouldSpawnSprintingParticles() {
        return !this.abilities.flying && super.shouldSpawnSprintingParticles();
    }

    @Override
    public boolean shouldDisplaySoulSpeedEffects() {
        return !this.abilities.flying && super.shouldDisplaySoulSpeedEffects();
    }

    protected void startRidingJump() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor(this.method_3151() * 100.0f)));
    }

    public void openRidingInventory() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
    }

    public void setServerBrand(String string) {
        this.serverBrand = string;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public StatHandler getStatHandler() {
        return this.statHandler;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void onRecipeDisplayed(Recipe<?> arg) {
        if (this.recipeBook.shouldDisplay(arg)) {
            this.recipeBook.onRecipeDisplayed(arg);
            this.networkHandler.sendPacket(new RecipeBookDataC2SPacket(arg));
        }
    }

    @Override
    protected int getPermissionLevel() {
        return this.clientPermissionLevel;
    }

    public void setClientPermissionLevel(int i) {
        this.clientPermissionLevel = i;
    }

    @Override
    public void sendMessage(Text arg, boolean bl) {
        if (bl) {
            this.client.inGameHud.setOverlayMessage(arg, false);
        } else {
            this.client.inGameHud.getChatHud().addMessage(arg);
        }
    }

    @Override
    protected void pushOutOfBlocks(double d, double e, double f) {
        BlockPos lv = new BlockPos(d, e, f);
        if (this.cannotFitAt(lv)) {
            double g = d - (double)lv.getX();
            double h = f - (double)lv.getZ();
            Direction lv2 = null;
            double i = 9999.0;
            if (!this.cannotFitAt(lv.west()) && g < i) {
                i = g;
                lv2 = Direction.WEST;
            }
            if (!this.cannotFitAt(lv.east()) && 1.0 - g < i) {
                i = 1.0 - g;
                lv2 = Direction.EAST;
            }
            if (!this.cannotFitAt(lv.north()) && h < i) {
                i = h;
                lv2 = Direction.NORTH;
            }
            if (!this.cannotFitAt(lv.south()) && 1.0 - h < i) {
                i = 1.0 - h;
                lv2 = Direction.SOUTH;
            }
            if (lv2 != null) {
                Vec3d lv3 = this.getVelocity();
                switch (lv2) {
                    case WEST: {
                        this.setVelocity(-0.1, lv3.y, lv3.z);
                        break;
                    }
                    case EAST: {
                        this.setVelocity(0.1, lv3.y, lv3.z);
                        break;
                    }
                    case NORTH: {
                        this.setVelocity(lv3.x, lv3.y, -0.1);
                        break;
                    }
                    case SOUTH: {
                        this.setVelocity(lv3.x, lv3.y, 0.1);
                    }
                }
            }
        }
    }

    private boolean cannotFitAt(BlockPos arg) {
        Box lv = this.getBoundingBox();
        BlockPos.Mutable lv2 = arg.mutableCopy();
        for (int i = MathHelper.floor(lv.minY); i < MathHelper.ceil(lv.maxY); ++i) {
            lv2.setY(i);
            if (this.doesNotSuffocate(lv2)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setSprinting(boolean bl) {
        super.setSprinting(bl);
        this.ticksSinceSprintingChanged = 0;
    }

    public void setExperience(float f, int i, int j) {
        this.experienceProgress = f;
        this.totalExperience = i;
        this.experienceLevel = j;
    }

    @Override
    public void sendSystemMessage(Text arg, UUID uUID) {
        this.client.inGameHud.getChatHud().addMessage(arg);
    }

    @Override
    public void handleStatus(byte b) {
        if (b >= 24 && b <= 28) {
            this.setClientPermissionLevel(b - 24);
        } else {
            super.handleStatus(b);
        }
    }

    public void setShowsDeathScreen(boolean bl) {
        this.showsDeathScreen = bl;
    }

    public boolean showsDeathScreen() {
        return this.showsDeathScreen;
    }

    @Override
    public void playSound(SoundEvent arg, float f, float g) {
        this.world.playSound(this.getX(), this.getY(), this.getZ(), arg, this.getSoundCategory(), f, g, false);
    }

    @Override
    public void playSound(SoundEvent arg, SoundCategory arg2, float f, float g) {
        this.world.playSound(this.getX(), this.getY(), this.getZ(), arg, arg2, f, g, false);
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    @Override
    public void setCurrentHand(Hand arg) {
        ItemStack lv = this.getStackInHand(arg);
        if (lv.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.setCurrentHand(arg);
        this.usingItem = true;
        this.activeHand = arg;
    }

    @Override
    public boolean isUsingItem() {
        return this.usingItem;
    }

    @Override
    public void clearActiveItem() {
        super.clearActiveItem();
        this.usingItem = false;
    }

    @Override
    public Hand getActiveHand() {
        return this.activeHand;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (LIVING_FLAGS.equals(arg)) {
            Hand lv;
            boolean bl = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
            Hand hand = lv = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (bl && !this.usingItem) {
                this.setCurrentHand(lv);
            } else if (!bl && this.usingItem) {
                this.clearActiveItem();
            }
        }
        if (FLAGS.equals(arg) && this.isFallFlying() && !this.field_3939) {
            this.client.getSoundManager().play(new ElytraSoundInstance(this));
        }
    }

    public boolean hasJumpingMount() {
        Entity lv = this.getVehicle();
        return this.hasVehicle() && lv instanceof JumpingMount && ((JumpingMount)((Object)lv)).canJump();
    }

    public float method_3151() {
        return this.field_3922;
    }

    @Override
    public void openEditSignScreen(SignBlockEntity arg) {
        this.client.openScreen(new SignEditScreen(arg));
    }

    @Override
    public void openCommandBlockMinecartScreen(CommandBlockExecutor arg) {
        this.client.openScreen(new MinecartCommandBlockScreen(arg));
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity arg) {
        this.client.openScreen(new CommandBlockScreen(arg));
    }

    @Override
    public void openStructureBlockScreen(StructureBlockBlockEntity arg) {
        this.client.openScreen(new StructureBlockScreen(arg));
    }

    @Override
    public void openJigsawScreen(JigsawBlockEntity arg) {
        this.client.openScreen(new JigsawBlockScreen(arg));
    }

    @Override
    public void openEditBookScreen(ItemStack arg, Hand arg2) {
        Item lv = arg.getItem();
        if (lv == Items.WRITABLE_BOOK) {
            this.client.openScreen(new BookEditScreen(this, arg, arg2));
        }
    }

    @Override
    public void addCritParticles(Entity arg) {
        this.client.particleManager.addEmitter(arg, ParticleTypes.CRIT);
    }

    @Override
    public void addEnchantedHitParticles(Entity arg) {
        this.client.particleManager.addEmitter(arg, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isSneaking() {
        return this.input != null && this.input.sneaking;
    }

    @Override
    public boolean isInSneakingPose() {
        return this.inSneakingPose;
    }

    public boolean shouldSlowDown() {
        return this.isInSneakingPose() || this.shouldLeaveSwimmingPose();
    }

    @Override
    public void tickNewAi() {
        super.tickNewAi();
        if (this.isCamera()) {
            this.sidewaysSpeed = this.input.movementSideways;
            this.forwardSpeed = this.input.movementForward;
            this.jumping = this.input.jumping;
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            this.renderPitch = (float)((double)this.renderPitch + (double)(this.pitch - this.renderPitch) * 0.5);
            this.renderYaw = (float)((double)this.renderYaw + (double)(this.yaw - this.renderYaw) * 0.5);
        }
    }

    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }

    @Override
    public void tickMovement() {
        ItemStack lv;
        boolean bl5;
        ++this.ticksSinceSprintingChanged;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }
        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.abilities.flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
        this.input.tick(this.shouldSlowDown());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle()) {
            this.input.movementSideways *= 0.2f;
            this.input.movementForward *= 0.2f;
            this.ticksLeftToDoubleTapSprint = 0;
        }
        boolean bl4 = false;
        if (this.ticksToNextAutojump > 0) {
            --this.ticksToNextAutojump;
            bl4 = true;
            this.input.jumping = true;
        }
        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() + (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() + (double)this.getWidth() * 0.35);
        }
        if (bl2) {
            this.ticksLeftToDoubleTapSprint = 0;
        }
        boolean bl6 = bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0f || this.abilities.allowFlying;
        if (!(!this.onGround && !this.isSubmergedInWater() || bl2 || bl3 || !this.isWalking() || this.isSprinting() || !bl5 || this.isUsingItem() || this.hasStatusEffect(StatusEffects.BLINDNESS))) {
            if (this.ticksLeftToDoubleTapSprint > 0 || this.client.options.keySprint.isPressed()) {
                this.setSprinting(true);
            } else {
                this.ticksLeftToDoubleTapSprint = 7;
            }
        }
        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.isWalking() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            boolean bl7;
            boolean bl62 = !this.input.hasForwardMovement() || !bl5;
            boolean bl8 = bl7 = bl62 || this.horizontalCollision || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl62 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl7) {
                this.setSprinting(false);
            }
        }
        boolean bl8 = false;
        if (this.abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }
        if (this.input.jumping && !bl8 && !bl && !this.abilities.flying && !this.hasVehicle() && !this.isClimbing() && (lv = this.getEquippedStack(EquipmentSlot.CHEST)).getItem() == Items.ELYTRA && ElytraItem.isUsable(lv) && this.checkFallFlying()) {
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        this.field_3939 = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking && this.method_29920()) {
            this.knockDownwards();
        }
        if (this.isSubmergedIn(FluidTags.WATER)) {
            int i = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + i, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }
        if (this.abilities.flying && this.isCamera()) {
            int j = 0;
            if (this.input.sneaking) {
                --j;
            }
            if (this.input.jumping) {
                ++j;
            }
            if (j != 0) {
                this.setVelocity(this.getVelocity().add(0.0, (float)j * this.abilities.getFlySpeed() * 3.0f, 0.0));
            }
        }
        if (this.hasJumpingMount()) {
            JumpingMount lv2 = (JumpingMount)((Object)this.getVehicle());
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.field_3922 = 0.0f;
                }
            }
            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                lv2.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0f));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.field_3922 = 0.0f;
            } else if (bl) {
                ++this.field_3938;
                this.field_3922 = this.field_3938 < 10 ? (float)this.field_3938 * 0.1f : 0.8f + 2.0f / (float)(this.field_3938 - 9) * 0.1f;
            }
        } else {
            this.field_3922 = 0.0f;
        }
        super.tickMovement();
        if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            this.abilities.flying = false;
            this.sendAbilitiesUpdate();
        }
    }

    private void updateNausea() {
        this.lastNauseaStrength = this.nextNauseaStrength;
        if (this.inNetherPortal) {
            if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
                if (this.client.currentScreen instanceof HandledScreen) {
                    this.closeHandledScreen();
                }
                this.client.openScreen(null);
            }
            if (this.nextNauseaStrength == 0.0f) {
                this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f, 0.25f));
            }
            this.nextNauseaStrength += 0.0125f;
            if (this.nextNauseaStrength >= 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
            this.inNetherPortal = false;
        } else if (this.hasStatusEffect(StatusEffects.NAUSEA) && this.getStatusEffect(StatusEffects.NAUSEA).getDuration() > 60) {
            this.nextNauseaStrength += 0.006666667f;
            if (this.nextNauseaStrength > 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
        } else {
            if (this.nextNauseaStrength > 0.0f) {
                this.nextNauseaStrength -= 0.05f;
            }
            if (this.nextNauseaStrength < 0.0f) {
                this.nextNauseaStrength = 0.0f;
            }
        }
        this.tickNetherPortalCooldown();
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.riding = false;
        if (this.getVehicle() instanceof BoatEntity) {
            BoatEntity lv = (BoatEntity)this.getVehicle();
            lv.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }
    }

    public boolean isRiding() {
        return this.riding;
    }

    @Override
    @Nullable
    public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect arg) {
        if (arg == StatusEffects.NAUSEA) {
            this.lastNauseaStrength = 0.0f;
            this.nextNauseaStrength = 0.0f;
        }
        return super.removeStatusEffectInternal(arg);
    }

    @Override
    public void move(MovementType arg, Vec3d arg2) {
        double d = this.getX();
        double e = this.getZ();
        super.move(arg, arg2);
        this.autoJump((float)(this.getX() - d), (float)(this.getZ() - e));
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    protected void autoJump(float f, float g) {
        if (!this.shouldAutoJump()) {
            return;
        }
        Vec3d lv = this.getPos();
        Vec3d lv2 = lv.add(f, 0.0, g);
        Vec3d lv3 = new Vec3d(f, 0.0, g);
        float h = this.getMovementSpeed();
        float i = (float)lv3.lengthSquared();
        if (i <= 0.001f) {
            Vec2f lv4 = this.input.getMovementInput();
            float j = h * lv4.x;
            float k = h * lv4.y;
            float l = MathHelper.sin(this.yaw * ((float)Math.PI / 180));
            float m = MathHelper.cos(this.yaw * ((float)Math.PI / 180));
            lv3 = new Vec3d(j * m - k * l, lv3.y, k * m + j * l);
            i = (float)lv3.lengthSquared();
            if (i <= 0.001f) {
                return;
            }
        }
        float n = MathHelper.fastInverseSqrt(i);
        Vec3d lv5 = lv3.multiply(n);
        Vec3d lv6 = this.getRotationVecClient();
        float o = (float)(lv6.x * lv5.x + lv6.z * lv5.z);
        if (o < -0.15f) {
            return;
        }
        ShapeContext lv7 = ShapeContext.of(this);
        BlockPos lv8 = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        BlockState lv9 = this.world.getBlockState(lv8);
        if (!lv9.getCollisionShape(this.world, lv8, lv7).isEmpty()) {
            return;
        }
        BlockState lv10 = this.world.getBlockState(lv8 = lv8.up());
        if (!lv10.getCollisionShape(this.world, lv8, lv7).isEmpty()) {
            return;
        }
        float p = 7.0f;
        float q = 1.2f;
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            q += (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
        }
        float r = Math.max(h * 7.0f, 1.0f / n);
        Vec3d lv11 = lv;
        Vec3d lv12 = lv2.add(lv5.multiply(r));
        float s = this.getWidth();
        float t = this.getHeight();
        Box lv13 = new Box(lv11, lv12.add(0.0, t, 0.0)).expand(s, 0.0, s);
        lv11 = lv11.add(0.0, 0.51f, 0.0);
        lv12 = lv12.add(0.0, 0.51f, 0.0);
        Vec3d lv14 = lv5.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        Vec3d lv15 = lv14.multiply(s * 0.5f);
        Vec3d lv16 = lv11.subtract(lv15);
        Vec3d lv17 = lv12.subtract(lv15);
        Vec3d lv18 = lv11.add(lv15);
        Vec3d lv19 = lv12.add(lv15);
        Iterator iterator = this.world.getCollisions(this, lv13, arg -> true).flatMap(arg -> arg.getBoundingBoxes().stream()).iterator();
        float u = Float.MIN_VALUE;
        while (iterator.hasNext()) {
            Box lv20 = (Box)iterator.next();
            if (!lv20.intersects(lv16, lv17) && !lv20.intersects(lv18, lv19)) continue;
            u = (float)lv20.maxY;
            Vec3d lv21 = lv20.getCenter();
            BlockPos lv22 = new BlockPos(lv21);
            int v = 1;
            while ((float)v < q) {
                BlockState lv26;
                BlockPos lv23 = lv22.up(v);
                BlockState lv24 = this.world.getBlockState(lv23);
                VoxelShape lv25 = lv24.getCollisionShape(this.world, lv23, lv7);
                if (!lv25.isEmpty() && (double)(u = (float)lv25.getMax(Direction.Axis.Y) + (float)lv23.getY()) - this.getY() > (double)q) {
                    return;
                }
                if (v > 1 && !(lv26 = this.world.getBlockState(lv8 = lv8.up())).getCollisionShape(this.world, lv8, lv7).isEmpty()) {
                    return;
                }
                ++v;
            }
            break block0;
        }
        if (u == Float.MIN_VALUE) {
            return;
        }
        float w = (float)((double)u - this.getY());
        if (w <= 0.5f || w > q) {
            return;
        }
        this.ticksToNextAutojump = 1;
    }

    private boolean shouldAutoJump() {
        return this.isAutoJumpEnabled() && this.ticksToNextAutojump <= 0 && this.onGround && !this.clipAtLedge() && !this.hasVehicle() && this.hasMovementInput() && (double)this.getJumpVelocityMultiplier() >= 1.0;
    }

    private boolean hasMovementInput() {
        Vec2f lv = this.input.getMovementInput();
        return lv.x != 0.0f || lv.y != 0.0f;
    }

    private boolean isWalking() {
        double d = 0.8;
        return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.8;
    }

    public float getUnderwaterVisibility() {
        if (!this.isSubmergedIn(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float g = 100.0f;
        if ((float)this.underwaterVisibilityTicks >= 600.0f) {
            return 1.0f;
        }
        float h = MathHelper.clamp((float)this.underwaterVisibilityTicks / 100.0f, 0.0f, 1.0f);
        float i = (float)this.underwaterVisibilityTicks < 100.0f ? 0.0f : MathHelper.clamp(((float)this.underwaterVisibilityTicks - 100.0f) / 500.0f, 0.0f, 1.0f);
        return h * 0.6f + i * 0.39999998f;
    }

    @Override
    public boolean isSubmergedInWater() {
        return this.isSubmergedInWater;
    }

    @Override
    protected boolean updateWaterSubmersionState() {
        boolean bl = this.isSubmergedInWater;
        boolean bl2 = super.updateWaterSubmersionState();
        if (this.isSpectator()) {
            return this.isSubmergedInWater;
        }
        if (!bl && bl2) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
            this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
        }
        if (bl && !bl2) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.isSubmergedInWater;
    }
}

