/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
 *  com.google.common.primitives.Floats
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestions
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  it.unimi.dsi.fastutil.ints.Int2ShortMap
 *  it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler
implements ServerPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public final ClientConnection connection;
    private final MinecraftServer server;
    public ServerPlayerEntity player;
    private int ticks;
    private long lastKeepAliveTime;
    private boolean waitingForKeepAlive;
    private long keepAliveId;
    private int messageCooldown;
    private int creativeItemDropThreshold;
    private final Int2ShortMap transactions = new Int2ShortOpenHashMap();
    private double lastTickX;
    private double lastTickY;
    private double lastTickZ;
    private double updatedX;
    private double updatedY;
    private double updatedZ;
    private Entity topmostRiddenEntity;
    private double lastTickRiddenX;
    private double lastTickRiddenY;
    private double lastTickRiddenZ;
    private double updatedRiddenX;
    private double updatedRiddenY;
    private double updatedRiddenZ;
    private Vec3d requestedTeleportPos;
    private int requestedTeleportId;
    private int teleportRequestTick;
    private boolean floating;
    private int floatingTicks;
    private boolean ridingEntity;
    private int vehicleFloatingTicks;
    private int movePacketsCount;
    private int lastTickMovePacketsCount;

    public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        this.server = server;
        this.connection = connection;
        connection.setPacketListener(this);
        this.player = player;
        player.networkHandler = this;
    }

    public void tick() {
        this.syncWithPlayerPosition();
        this.player.prevX = this.player.getX();
        this.player.prevY = this.player.getY();
        this.player.prevZ = this.player.getZ();
        this.player.playerTick();
        this.player.updatePositionAndAngles(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
        ++this.ticks;
        this.lastTickMovePacketsCount = this.movePacketsCount;
        if (this.floating && !this.player.isSleeping()) {
            if (++this.floatingTicks > 80) {
                LOGGER.warn("{} was kicked for floating too long!", (Object)this.player.getName().getString());
                this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.floating = false;
            this.floatingTicks = 0;
        }
        this.topmostRiddenEntity = this.player.getRootVehicle();
        if (this.topmostRiddenEntity == this.player || this.topmostRiddenEntity.getPrimaryPassenger() != this.player) {
            this.topmostRiddenEntity = null;
            this.ridingEntity = false;
            this.vehicleFloatingTicks = 0;
        } else {
            this.lastTickRiddenX = this.topmostRiddenEntity.getX();
            this.lastTickRiddenY = this.topmostRiddenEntity.getY();
            this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
            this.updatedRiddenX = this.topmostRiddenEntity.getX();
            this.updatedRiddenY = this.topmostRiddenEntity.getY();
            this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
            if (this.ridingEntity && this.player.getRootVehicle().getPrimaryPassenger() == this.player) {
                if (++this.vehicleFloatingTicks > 80) {
                    LOGGER.warn("{} was kicked for floating a vehicle too long!", (Object)this.player.getName().getString());
                    this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
                    return;
                }
            } else {
                this.ridingEntity = false;
                this.vehicleFloatingTicks = 0;
            }
        }
        this.server.getProfiler().push("keepAlive");
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastKeepAliveTime >= 15000L) {
            if (this.waitingForKeepAlive) {
                this.disconnect(new TranslatableText("disconnect.timeout"));
            } else {
                this.waitingForKeepAlive = true;
                this.lastKeepAliveTime = l;
                this.keepAliveId = l;
                this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
            }
        }
        this.server.getProfiler().pop();
        if (this.messageCooldown > 0) {
            --this.messageCooldown;
        }
        if (this.creativeItemDropThreshold > 0) {
            --this.creativeItemDropThreshold;
        }
        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
            this.disconnect(new TranslatableText("multiplayer.disconnect.idling"));
        }
    }

    public void syncWithPlayerPosition() {
        this.lastTickX = this.player.getX();
        this.lastTickY = this.player.getY();
        this.lastTickZ = this.player.getZ();
        this.updatedX = this.player.getX();
        this.updatedY = this.player.getY();
        this.updatedZ = this.player.getZ();
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    private boolean isHost() {
        return this.server.isHost(this.player.getGameProfile());
    }

    public void disconnect(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.connection.disconnect(reason)));
        this.connection.disableAutoRead();
        this.server.submitAndJoin(this.connection::handleDisconnection);
    }

    @Override
    public void onPlayerInput(PlayerInputC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.method_14218(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
    }

    private static boolean validatePlayerMove(PlayerMoveC2SPacket packet) {
        if (!(Doubles.isFinite((double)packet.getX(0.0)) && Doubles.isFinite((double)packet.getY(0.0)) && Doubles.isFinite((double)packet.getZ(0.0)) && Floats.isFinite((float)packet.getPitch(0.0f)) && Floats.isFinite((float)packet.getYaw(0.0f)))) {
            return true;
        }
        return Math.abs(packet.getX(0.0)) > 3.0E7 || Math.abs(packet.getY(0.0)) > 3.0E7 || Math.abs(packet.getZ(0.0)) > 3.0E7;
    }

    private static boolean validateVehicleMove(VehicleMoveC2SPacket packet) {
        return !Doubles.isFinite((double)packet.getX()) || !Doubles.isFinite((double)packet.getY()) || !Doubles.isFinite((double)packet.getZ()) || !Floats.isFinite((float)packet.getPitch()) || !Floats.isFinite((float)packet.getYaw());
    }

    @Override
    public void onVehicleMove(VehicleMoveC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (ServerPlayNetworkHandler.validateVehicleMove(packet)) {
            this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_vehicle_movement"));
            return;
        }
        Entity lv = this.player.getRootVehicle();
        if (lv != this.player && lv.getPrimaryPassenger() == this.player && lv == this.topmostRiddenEntity) {
            ServerWorld lv2 = this.player.getServerWorld();
            double d = lv.getX();
            double e = lv.getY();
            double f = lv.getZ();
            double g = packet.getX();
            double h = packet.getY();
            double i = packet.getZ();
            float j = packet.getYaw();
            float k = packet.getPitch();
            double l = g - this.lastTickRiddenX;
            double m = h - this.lastTickRiddenY;
            double n = i - this.lastTickRiddenZ;
            double p = l * l + m * m + n * n;
            double o = lv.getVelocity().lengthSquared();
            if (p - o > 100.0 && !this.isHost()) {
                LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", (Object)lv.getName().getString(), (Object)this.player.getName().getString(), (Object)l, (Object)m, (Object)n);
                this.connection.send(new VehicleMoveS2CPacket(lv));
                return;
            }
            boolean bl = lv2.doesNotCollide(lv, lv.getBoundingBox().contract(0.0625));
            l = g - this.updatedRiddenX;
            m = h - this.updatedRiddenY - 1.0E-6;
            n = i - this.updatedRiddenZ;
            lv.move(MovementType.PLAYER, new Vec3d(l, m, n));
            double q = m;
            l = g - lv.getX();
            m = h - lv.getY();
            if (m > -0.5 || m < 0.5) {
                m = 0.0;
            }
            n = i - lv.getZ();
            p = l * l + m * m + n * n;
            boolean bl2 = false;
            if (p > 0.0625) {
                bl2 = true;
                LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", (Object)lv.getName().getString(), (Object)this.player.getName().getString(), (Object)Math.sqrt(p));
            }
            lv.updatePositionAndAngles(g, h, i, j, k);
            boolean bl3 = lv2.doesNotCollide(lv, lv.getBoundingBox().contract(0.0625));
            if (bl && (bl2 || !bl3)) {
                lv.updatePositionAndAngles(d, e, f, j, k);
                this.connection.send(new VehicleMoveS2CPacket(lv));
                return;
            }
            this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
            this.player.increaseTravelMotionStats(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
            this.ridingEntity = q >= -0.03125 && !this.server.isFlightEnabled() && this.method_29780(lv);
            this.updatedRiddenX = lv.getX();
            this.updatedRiddenY = lv.getY();
            this.updatedRiddenZ = lv.getZ();
        }
    }

    private boolean method_29780(Entity arg) {
        return arg.world.method_29546(arg.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
    }

    @Override
    public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (packet.getTeleportId() == this.requestedTeleportId) {
            this.player.updatePositionAndAngles(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
            this.updatedX = this.requestedTeleportPos.x;
            this.updatedY = this.requestedTeleportPos.y;
            this.updatedZ = this.requestedTeleportPos.z;
            if (this.player.isInTeleportationState()) {
                this.player.onTeleportationDone();
            }
            this.requestedTeleportPos = null;
        }
    }

    @Override
    public void onRecipeBookData(RecipeBookDataC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
    }

    @Override
    public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.getRecipeBook().setCategoryOptions(packet.getCategory(), packet.isGuiOpen(), packet.isFilteringCraftable());
    }

    @Override
    public void onAdvancementTab(AdvancementTabC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
            Identifier lv = packet.getTabToOpen();
            Advancement lv2 = this.server.getAdvancementLoader().get(lv);
            if (lv2 != null) {
                this.player.getAdvancementTracker().setDisplayTab(lv2);
            }
        }
    }

    @Override
    public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        StringReader stringReader = new StringReader(packet.getPartialCommand());
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        ParseResults parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, (Object)this.player.getCommandSource());
        this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseResults).thenAccept(suggestions -> this.connection.send(new CommandSuggestionsS2CPacket(packet.getCompletionId(), (Suggestions)suggestions)));
    }

    @Override
    public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendSystemMessage(new TranslatableText("advMode.notEnabled"), Util.NIL_UUID);
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendSystemMessage(new TranslatableText("advMode.notAllowed"), Util.NIL_UUID);
            return;
        }
        CommandBlockExecutor lv = null;
        CommandBlockBlockEntity lv2 = null;
        BlockPos lv3 = packet.getBlockPos();
        BlockEntity lv4 = this.player.world.getBlockEntity(lv3);
        if (lv4 instanceof CommandBlockBlockEntity) {
            lv2 = (CommandBlockBlockEntity)lv4;
            lv = lv2.getCommandExecutor();
        }
        String string = packet.getCommand();
        boolean bl = packet.shouldTrackOutput();
        if (lv != null) {
            CommandBlockBlockEntity.Type lv5 = lv2.getCommandBlockType();
            Direction lv6 = this.player.world.getBlockState(lv3).get(CommandBlock.FACING);
            switch (packet.getType()) {
                case SEQUENCE: {
                    BlockState lv7 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
                    this.player.world.setBlockState(lv3, (BlockState)((BlockState)lv7.with(CommandBlock.FACING, lv6)).with(CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                    break;
                }
                case AUTO: {
                    BlockState lv8 = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
                    this.player.world.setBlockState(lv3, (BlockState)((BlockState)lv8.with(CommandBlock.FACING, lv6)).with(CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                    break;
                }
                default: {
                    BlockState lv9 = Blocks.COMMAND_BLOCK.getDefaultState();
                    this.player.world.setBlockState(lv3, (BlockState)((BlockState)lv9.with(CommandBlock.FACING, lv6)).with(CommandBlock.CONDITIONAL, packet.isConditional()), 2);
                }
            }
            lv4.cancelRemoval();
            this.player.world.setBlockEntity(lv3, lv4);
            lv.setCommand(string);
            lv.shouldTrackOutput(bl);
            if (!bl) {
                lv.setLastOutput(null);
            }
            lv2.setAuto(packet.isAlwaysActive());
            if (lv5 != packet.getType()) {
                lv2.method_23359();
            }
            lv.markDirty();
            if (!ChatUtil.isEmpty(string)) {
                this.player.sendSystemMessage(new TranslatableText("advMode.setCommand.success", string), Util.NIL_UUID);
            }
        }
    }

    @Override
    public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.server.areCommandBlocksEnabled()) {
            this.player.sendSystemMessage(new TranslatableText("advMode.notEnabled"), Util.NIL_UUID);
            return;
        }
        if (!this.player.isCreativeLevelTwoOp()) {
            this.player.sendSystemMessage(new TranslatableText("advMode.notAllowed"), Util.NIL_UUID);
            return;
        }
        CommandBlockExecutor lv = packet.getMinecartCommandExecutor(this.player.world);
        if (lv != null) {
            lv.setCommand(packet.getCommand());
            lv.shouldTrackOutput(packet.shouldTrackOutput());
            if (!packet.shouldTrackOutput()) {
                lv.setLastOutput(null);
            }
            lv.markDirty();
            this.player.sendSystemMessage(new TranslatableText("advMode.setCommand.success", packet.getCommand()), Util.NIL_UUID);
        }
    }

    @Override
    public void onPickFromInventory(PickFromInventoryC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.inventory.swapSlotWithHotbar(packet.getSlot());
        this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getStack(this.player.inventory.selectedSlot)));
        this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, packet.getSlot(), this.player.inventory.getStack(packet.getSlot())));
        this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
    }

    @Override
    public void onRenameItem(RenameItemC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.currentScreenHandler instanceof AnvilScreenHandler) {
            AnvilScreenHandler lv = (AnvilScreenHandler)this.player.currentScreenHandler;
            String string = SharedConstants.stripInvalidChars(packet.getName());
            if (string.length() <= 35) {
                lv.setNewItemName(string);
            }
        }
    }

    @Override
    public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.currentScreenHandler instanceof BeaconScreenHandler) {
            ((BeaconScreenHandler)this.player.currentScreenHandler).setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
        }
    }

    @Override
    public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos lv = packet.getPos();
        BlockState lv2 = this.player.world.getBlockState(lv);
        BlockEntity lv3 = this.player.world.getBlockEntity(lv);
        if (lv3 instanceof StructureBlockBlockEntity) {
            StructureBlockBlockEntity lv4 = (StructureBlockBlockEntity)lv3;
            lv4.setMode(packet.getMode());
            lv4.setStructureName(packet.getStructureName());
            lv4.setOffset(packet.getOffset());
            lv4.setSize(packet.getSize());
            lv4.setMirror(packet.getMirror());
            lv4.setRotation(packet.getRotation());
            lv4.setMetadata(packet.getMetadata());
            lv4.setIgnoreEntities(packet.getIgnoreEntities());
            lv4.setShowAir(packet.shouldShowAir());
            lv4.setShowBoundingBox(packet.shouldShowBoundingBox());
            lv4.setIntegrity(packet.getIntegrity());
            lv4.setSeed(packet.getSeed());
            if (lv4.hasStructureName()) {
                String string = lv4.getStructureName();
                if (packet.getAction() == StructureBlockBlockEntity.Action.SAVE_AREA) {
                    if (lv4.saveStructure()) {
                        this.player.sendMessage(new TranslatableText("structure_block.save_success", string), false);
                    } else {
                        this.player.sendMessage(new TranslatableText("structure_block.save_failure", string), false);
                    }
                } else if (packet.getAction() == StructureBlockBlockEntity.Action.LOAD_AREA) {
                    if (!lv4.isStructureAvailable()) {
                        this.player.sendMessage(new TranslatableText("structure_block.load_not_found", string), false);
                    } else if (lv4.loadStructure(this.player.getServerWorld())) {
                        this.player.sendMessage(new TranslatableText("structure_block.load_success", string), false);
                    } else {
                        this.player.sendMessage(new TranslatableText("structure_block.load_prepare", string), false);
                    }
                } else if (packet.getAction() == StructureBlockBlockEntity.Action.SCAN_AREA) {
                    if (lv4.detectStructureSize()) {
                        this.player.sendMessage(new TranslatableText("structure_block.size_success", string), false);
                    } else {
                        this.player.sendMessage(new TranslatableText("structure_block.size_failure"), false);
                    }
                }
            } else {
                this.player.sendMessage(new TranslatableText("structure_block.invalid_structure_name", packet.getStructureName()), false);
            }
            lv4.markDirty();
            this.player.world.updateListeners(lv, lv2, lv2, 3);
        }
    }

    @Override
    public void onJigsawUpdate(UpdateJigsawC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos lv = packet.getPos();
        BlockState lv2 = this.player.world.getBlockState(lv);
        BlockEntity lv3 = this.player.world.getBlockEntity(lv);
        if (lv3 instanceof JigsawBlockEntity) {
            JigsawBlockEntity lv4 = (JigsawBlockEntity)lv3;
            lv4.setAttachmentType(packet.getAttachmentType());
            lv4.setTargetPool(packet.getTargetPool());
            lv4.setPool(packet.getPool());
            lv4.setFinalState(packet.getFinalState());
            lv4.setJoint(packet.getJointType());
            lv4.markDirty();
            this.player.world.updateListeners(lv, lv2, lv2, 3);
        }
    }

    @Override
    public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.isCreativeLevelTwoOp()) {
            return;
        }
        BlockPos lv = packet.getPos();
        BlockEntity lv2 = this.player.world.getBlockEntity(lv);
        if (lv2 instanceof JigsawBlockEntity) {
            JigsawBlockEntity lv3 = (JigsawBlockEntity)lv2;
            lv3.generate(this.player.getServerWorld(), packet.getMaxDepth(), packet.shouldKeepJigsaws());
        }
    }

    @Override
    public void onVillagerTradeSelect(SelectVillagerTradeC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        int i = packet.getTradeId();
        ScreenHandler lv = this.player.currentScreenHandler;
        if (lv instanceof MerchantScreenHandler) {
            MerchantScreenHandler lv2 = (MerchantScreenHandler)lv;
            lv2.setRecipeIndex(i);
            lv2.switchTo(i);
        }
    }

    @Override
    public void onBookUpdate(BookUpdateC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        ItemStack lv = packet.getBook();
        if (lv.isEmpty()) {
            return;
        }
        if (!WritableBookItem.isValid(lv.getTag())) {
            return;
        }
        ItemStack lv2 = this.player.getStackInHand(packet.getHand());
        if (lv.getItem() == Items.WRITABLE_BOOK && lv2.getItem() == Items.WRITABLE_BOOK) {
            if (packet.wasSigned()) {
                ItemStack lv3 = new ItemStack(Items.WRITTEN_BOOK);
                CompoundTag lv4 = lv2.getTag();
                if (lv4 != null) {
                    lv3.setTag(lv4.copy());
                }
                lv3.putSubTag("author", StringTag.of(this.player.getName().getString()));
                lv3.putSubTag("title", StringTag.of(lv.getTag().getString("title")));
                ListTag lv5 = lv.getTag().getList("pages", 8);
                for (int i = 0; i < lv5.size(); ++i) {
                    String string = lv5.getString(i);
                    LiteralText lv6 = new LiteralText(string);
                    string = Text.Serializer.toJson(lv6);
                    lv5.set(i, StringTag.of(string));
                }
                lv3.putSubTag("pages", lv5);
                this.player.setStackInHand(packet.getHand(), lv3);
            } else {
                lv2.putSubTag("pages", lv.getTag().getList("pages", 8));
            }
        }
    }

    @Override
    public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.hasPermissionLevel(2)) {
            return;
        }
        Entity lv = this.player.getServerWorld().getEntityById(packet.getEntityId());
        if (lv != null) {
            CompoundTag lv2 = lv.toTag(new CompoundTag());
            this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), lv2));
        }
    }

    @Override
    public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.hasPermissionLevel(2)) {
            return;
        }
        BlockEntity lv = this.player.getServerWorld().getBlockEntity(packet.getPos());
        CompoundTag lv2 = lv != null ? lv.toTag(new CompoundTag()) : null;
        this.player.networkHandler.sendPacket(new TagQueryResponseS2CPacket(packet.getTransactionId(), lv2));
    }

    @Override
    public void onPlayerMove(PlayerMoveC2SPacket packet) {
        boolean bl;
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (ServerPlayNetworkHandler.validatePlayerMove(packet)) {
            this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
            return;
        }
        ServerWorld lv = this.player.getServerWorld();
        if (this.player.notInAnyWorld) {
            return;
        }
        if (this.ticks == 0) {
            this.syncWithPlayerPosition();
        }
        if (this.requestedTeleportPos != null) {
            if (this.ticks - this.teleportRequestTick > 20) {
                this.teleportRequestTick = this.ticks;
                this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
            }
            return;
        }
        this.teleportRequestTick = this.ticks;
        if (this.player.hasVehicle()) {
            this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
            this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
            return;
        }
        double d = this.player.getX();
        double e = this.player.getY();
        double f = this.player.getZ();
        double g = this.player.getY();
        double h = packet.getX(this.player.getX());
        double i = packet.getY(this.player.getY());
        double j = packet.getZ(this.player.getZ());
        float k = packet.getYaw(this.player.yaw);
        float l = packet.getPitch(this.player.pitch);
        double m = h - this.lastTickX;
        double n = i - this.lastTickY;
        double o = j - this.lastTickZ;
        double p = this.player.getVelocity().lengthSquared();
        double q = m * m + n * n + o * o;
        if (this.player.isSleeping()) {
            if (q > 1.0) {
                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), packet.getYaw(this.player.yaw), packet.getPitch(this.player.pitch));
            }
            return;
        }
        ++this.movePacketsCount;
        int r = this.movePacketsCount - this.lastTickMovePacketsCount;
        if (r > 5) {
            LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", (Object)this.player.getName().getString(), (Object)r);
            r = 1;
        }
        if (!(this.player.isInTeleportationState() || this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) && this.player.isFallFlying())) {
            float s;
            float f2 = s = this.player.isFallFlying() ? 300.0f : 100.0f;
            if (q - p > (double)(s * (float)r) && !this.isHost()) {
                LOGGER.warn("{} moved too quickly! {},{},{}", (Object)this.player.getName().getString(), (Object)m, (Object)n, (Object)o);
                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.yaw, this.player.pitch);
                return;
            }
        }
        Box lv2 = this.player.getBoundingBox();
        m = h - this.updatedX;
        n = i - this.updatedY;
        o = j - this.updatedZ;
        boolean bl2 = bl = n > 0.0;
        if (this.player.isOnGround() && !packet.isOnGround() && bl) {
            this.player.jump();
        }
        this.player.move(MovementType.PLAYER, new Vec3d(m, n, o));
        double t = n;
        m = h - this.player.getX();
        n = i - this.player.getY();
        if (n > -0.5 || n < 0.5) {
            n = 0.0;
        }
        o = j - this.player.getZ();
        q = m * m + n * n + o * o;
        boolean bl22 = false;
        if (!this.player.isInTeleportationState() && q > 0.0625 && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
            bl22 = true;
            LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
        }
        this.player.updatePositionAndAngles(h, i, j, k, l);
        if (!this.player.noClip && !this.player.isSleeping() && (bl22 && lv.doesNotCollide(this.player, lv2) || this.isPlayerNotCollidingWithBlocks(lv, lv2))) {
            this.requestTeleport(d, e, f, k, l);
            return;
        }
        this.floating = t >= -0.03125 && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR && !this.server.isFlightEnabled() && !this.player.abilities.allowFlying && !this.player.hasStatusEffect(StatusEffects.LEVITATION) && !this.player.isFallFlying() && this.method_29780(this.player);
        this.player.getServerWorld().getChunkManager().updateCameraPosition(this.player);
        this.player.handleFall(this.player.getY() - g, packet.isOnGround());
        this.player.setOnGround(packet.isOnGround());
        if (bl) {
            this.player.fallDistance = 0.0f;
        }
        this.player.increaseTravelMotionStats(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
        this.updatedX = this.player.getX();
        this.updatedY = this.player.getY();
        this.updatedZ = this.player.getZ();
    }

    private boolean isPlayerNotCollidingWithBlocks(WorldView arg3, Box arg22) {
        Stream<VoxelShape> stream = arg3.getCollisions(this.player, this.player.getBoundingBox().contract(1.0E-5f), arg -> true);
        VoxelShape lv = VoxelShapes.cuboid(arg22.contract(1.0E-5f));
        return stream.anyMatch(arg2 -> !VoxelShapes.matchesAnywhere(arg2, lv, BooleanBiFunction.AND));
    }

    public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
        this.teleportRequest(x, y, z, yaw, pitch, Collections.emptySet());
    }

    public void teleportRequest(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> set) {
        double i = set.contains((Object)PlayerPositionLookS2CPacket.Flag.X) ? this.player.getX() : 0.0;
        double j = set.contains((Object)PlayerPositionLookS2CPacket.Flag.Y) ? this.player.getY() : 0.0;
        double k = set.contains((Object)PlayerPositionLookS2CPacket.Flag.Z) ? this.player.getZ() : 0.0;
        float l = set.contains((Object)PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.yaw : 0.0f;
        float m = set.contains((Object)PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.pitch : 0.0f;
        this.requestedTeleportPos = new Vec3d(x, y, z);
        if (++this.requestedTeleportId == Integer.MAX_VALUE) {
            this.requestedTeleportId = 0;
        }
        this.teleportRequestTick = this.ticks;
        this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
        this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - i, y - j, z - k, yaw - l, pitch - m, set, this.requestedTeleportId));
    }

    @Override
    public void onPlayerAction(PlayerActionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        BlockPos lv = packet.getPos();
        this.player.updateLastActionTime();
        PlayerActionC2SPacket.Action lv2 = packet.getAction();
        switch (lv2) {
            case SWAP_ITEM_WITH_OFFHAND: {
                if (!this.player.isSpectator()) {
                    ItemStack lv3 = this.player.getStackInHand(Hand.OFF_HAND);
                    this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
                    this.player.setStackInHand(Hand.MAIN_HAND, lv3);
                    this.player.clearActiveItem();
                }
                return;
            }
            case DROP_ITEM: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(false);
                }
                return;
            }
            case DROP_ALL_ITEMS: {
                if (!this.player.isSpectator()) {
                    this.player.dropSelectedItem(true);
                }
                return;
            }
            case RELEASE_USE_ITEM: {
                this.player.stopUsingItem();
                return;
            }
            case START_DESTROY_BLOCK: 
            case ABORT_DESTROY_BLOCK: 
            case STOP_DESTROY_BLOCK: {
                this.player.interactionManager.processBlockBreakingAction(lv, lv2, packet.getDirection(), this.server.getWorldHeight());
                return;
            }
        }
        throw new IllegalArgumentException("Invalid player action");
    }

    private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item lv = stack.getItem();
        return (lv instanceof BlockItem || lv instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(lv);
    }

    @Override
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        ServerWorld lv = this.player.getServerWorld();
        Hand lv2 = packet.getHand();
        ItemStack lv3 = this.player.getStackInHand(lv2);
        BlockHitResult lv4 = packet.getBlockHitResult();
        BlockPos lv5 = lv4.getBlockPos();
        Direction lv6 = lv4.getSide();
        this.player.updateLastActionTime();
        if (lv5.getY() < this.server.getWorldHeight()) {
            if (this.requestedTeleportPos == null && this.player.squaredDistanceTo((double)lv5.getX() + 0.5, (double)lv5.getY() + 0.5, (double)lv5.getZ() + 0.5) < 64.0 && lv.canPlayerModifyAt(this.player, lv5)) {
                ActionResult lv7 = this.player.interactionManager.interactBlock(this.player, lv, lv3, lv2, lv4);
                if (lv6 == Direction.UP && !lv7.isAccepted() && lv5.getY() >= this.server.getWorldHeight() - 1 && ServerPlayNetworkHandler.canPlace(this.player, lv3)) {
                    MutableText lv8 = new TranslatableText("build.tooHigh", this.server.getWorldHeight()).formatted(Formatting.RED);
                    this.player.networkHandler.sendPacket(new GameMessageS2CPacket(lv8, MessageType.GAME_INFO, Util.NIL_UUID));
                } else if (lv7.shouldSwingHand()) {
                    this.player.swingHand(lv2, true);
                }
            }
        } else {
            MutableText lv9 = new TranslatableText("build.tooHigh", this.server.getWorldHeight()).formatted(Formatting.RED);
            this.player.networkHandler.sendPacket(new GameMessageS2CPacket(lv9, MessageType.GAME_INFO, Util.NIL_UUID));
        }
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(lv, lv5));
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(lv, lv5.offset(lv6)));
    }

    @Override
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        ServerWorld lv = this.player.getServerWorld();
        Hand lv2 = packet.getHand();
        ItemStack lv3 = this.player.getStackInHand(lv2);
        this.player.updateLastActionTime();
        if (lv3.isEmpty()) {
            return;
        }
        ActionResult lv4 = this.player.interactionManager.interactItem(this.player, lv, lv3, lv2);
        if (lv4.shouldSwingHand()) {
            this.player.swingHand(lv2, true);
        }
    }

    @Override
    public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.isSpectator()) {
            for (ServerWorld lv : this.server.getWorlds()) {
                Entity lv2 = packet.getTarget(lv);
                if (lv2 == null) continue;
                this.player.teleport(lv, lv2.getX(), lv2.getY(), lv2.getZ(), lv2.yaw, lv2.pitch);
                return;
            }
        }
    }

    @Override
    public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
    }

    @Override
    public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        Entity lv = this.player.getVehicle();
        if (lv instanceof BoatEntity) {
            ((BoatEntity)lv).setPaddleMovings(packet.isLeftPaddling(), packet.isRightPaddling());
        }
    }

    @Override
    public void onDisconnected(Text reason) {
        LOGGER.info("{} lost connection: {}", (Object)this.player.getName().getString(), (Object)reason.getString());
        this.server.forcePlayerSampleUpdate();
        this.server.getPlayerManager().broadcastChatMessage(new TranslatableText("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
        this.player.onDisconnect();
        this.server.getPlayerManager().remove(this.player);
        if (this.isHost()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.stop(false);
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
        if (packet instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket lv = (GameMessageS2CPacket)packet;
            ChatVisibility lv2 = this.player.getClientChatVisibility();
            if (lv2 == ChatVisibility.HIDDEN && lv.getLocation() != MessageType.GAME_INFO) {
                return;
            }
            if (lv2 == ChatVisibility.SYSTEM && !lv.isNonChat()) {
                return;
            }
        }
        try {
            this.connection.send(packet, listener);
        }
        catch (Throwable throwable) {
            CrashReport lv3 = CrashReport.create(throwable, "Sending packet");
            CrashReportSection lv4 = lv3.addElement("Packet being sent");
            lv4.add("Packet class", () -> packet.getClass().getCanonicalName());
            throw new CrashException(lv3);
        }
    }

    @Override
    public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (packet.getSelectedSlot() < 0 || packet.getSelectedSlot() >= PlayerInventory.getHotbarSize()) {
            LOGGER.warn("{} tried to set an invalid carried item", (Object)this.player.getName().getString());
            return;
        }
        if (this.player.inventory.selectedSlot != packet.getSelectedSlot() && this.player.getActiveHand() == Hand.MAIN_HAND) {
            this.player.clearActiveItem();
        }
        this.player.inventory.selectedSlot = packet.getSelectedSlot();
        this.player.updateLastActionTime();
    }

    @Override
    public void onGameMessage(ChatMessageC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
            this.sendPacket(new GameMessageS2CPacket(new TranslatableText("chat.cannotSend").formatted(Formatting.RED), MessageType.SYSTEM, Util.NIL_UUID));
            return;
        }
        this.player.updateLastActionTime();
        String string = StringUtils.normalizeSpace((String)packet.getChatMessage());
        for (int i = 0; i < string.length(); ++i) {
            if (SharedConstants.isValidChar(string.charAt(i))) continue;
            this.disconnect(new TranslatableText("multiplayer.disconnect.illegal_characters"));
            return;
        }
        if (string.startsWith("/")) {
            this.executeCommand(string);
        } else {
            TranslatableText lv = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
            this.server.getPlayerManager().broadcastChatMessage(lv, MessageType.CHAT, this.player.getUuid());
        }
        this.messageCooldown += 20;
        if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
            this.disconnect(new TranslatableText("disconnect.spam"));
        }
    }

    private void executeCommand(String input) {
        this.server.getCommandManager().execute(this.player.getCommandSource(), input);
    }

    @Override
    public void onHandSwing(HandSwingC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        this.player.swingHand(packet.getHand());
    }

    @Override
    public void onClientCommand(ClientCommandC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        switch (packet.getMode()) {
            case PRESS_SHIFT_KEY: {
                this.player.setSneaking(true);
                break;
            }
            case RELEASE_SHIFT_KEY: {
                this.player.setSneaking(false);
                break;
            }
            case START_SPRINTING: {
                this.player.setSprinting(true);
                break;
            }
            case STOP_SPRINTING: {
                this.player.setSprinting(false);
                break;
            }
            case STOP_SLEEPING: {
                if (!this.player.isSleeping()) break;
                this.player.wakeUp(false, true);
                this.requestedTeleportPos = this.player.getPos();
                break;
            }
            case START_RIDING_JUMP: {
                if (!(this.player.getVehicle() instanceof JumpingMount)) break;
                JumpingMount lv = (JumpingMount)((Object)this.player.getVehicle());
                int i = packet.getMountJumpHeight();
                if (!lv.canJump() || i <= 0) break;
                lv.startJumping(i);
                break;
            }
            case STOP_RIDING_JUMP: {
                if (!(this.player.getVehicle() instanceof JumpingMount)) break;
                JumpingMount lv2 = (JumpingMount)((Object)this.player.getVehicle());
                lv2.stopJumping();
                break;
            }
            case OPEN_INVENTORY: {
                if (!(this.player.getVehicle() instanceof HorseBaseEntity)) break;
                ((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
                break;
            }
            case START_FALL_FLYING: {
                if (this.player.checkFallFlying()) break;
                this.player.stopFallFlying();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid client command!");
            }
        }
    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        ServerWorld lv = this.player.getServerWorld();
        Entity lv2 = packet.getEntity(lv);
        this.player.updateLastActionTime();
        this.player.setSneaking(packet.isPlayerSneaking());
        if (lv2 != null) {
            double d = 36.0;
            if (this.player.squaredDistanceTo(lv2) < 36.0) {
                Hand lv3 = packet.getHand();
                ItemStack lv4 = lv3 != null ? this.player.getStackInHand(lv3).copy() : ItemStack.EMPTY;
                Optional<Object> optional = Optional.empty();
                if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT) {
                    optional = Optional.of(this.player.interact(lv2, lv3));
                } else if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
                    optional = Optional.of(lv2.interactAt(this.player, packet.getHitPosition(), lv3));
                } else if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                    if (lv2 instanceof ItemEntity || lv2 instanceof ExperienceOrbEntity || lv2 instanceof PersistentProjectileEntity || lv2 == this.player) {
                        this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
                        LOGGER.warn("Player {} tried to attack an invalid entity", (Object)this.player.getName().getString());
                        return;
                    }
                    this.player.attack(lv2);
                }
                if (optional.isPresent() && ((ActionResult)((Object)optional.get())).isAccepted()) {
                    Criteria.PLAYER_INTERACTED_WITH_ENTITY.test(this.player, lv4, lv2);
                    if (((ActionResult)((Object)optional.get())).shouldSwingHand()) {
                        this.player.swingHand(lv3, true);
                    }
                }
            }
        }
    }

    @Override
    public void onClientStatus(ClientStatusC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        ClientStatusC2SPacket.Mode lv = packet.getMode();
        switch (lv) {
            case PERFORM_RESPAWN: {
                if (this.player.notInAnyWorld) {
                    this.player.notInAnyWorld = false;
                    this.player = this.server.getPlayerManager().respawnPlayer(this.player, true);
                    Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
                    break;
                }
                if (this.player.getHealth() > 0.0f) {
                    return;
                }
                this.player = this.server.getPlayerManager().respawnPlayer(this.player, false);
                if (!this.server.isHardcore()) break;
                this.player.setGameMode(GameMode.SPECTATOR);
                this.player.getServerWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
                break;
            }
            case REQUEST_STATS: {
                this.player.getStatHandler().sendStats(this.player);
            }
        }
    }

    @Override
    public void onGuiClose(GuiCloseC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.closeCurrentScreen();
    }

    @Override
    public void onClickWindow(ClickWindowC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.currentScreenHandler.syncId == packet.getSyncId() && this.player.currentScreenHandler.isNotRestricted(this.player)) {
            if (this.player.isSpectator()) {
                DefaultedList<ItemStack> lv = DefaultedList.of();
                for (int i = 0; i < this.player.currentScreenHandler.slots.size(); ++i) {
                    lv.add(this.player.currentScreenHandler.slots.get(i).getStack());
                }
                this.player.onHandlerRegistered(this.player.currentScreenHandler, lv);
            } else {
                ItemStack lv2 = this.player.currentScreenHandler.onSlotClick(packet.getSlot(), packet.getClickData(), packet.getActionType(), this.player);
                if (ItemStack.areEqual(packet.getStack(), lv2)) {
                    this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getActionId(), true));
                    this.player.skipPacketSlotUpdates = true;
                    this.player.currentScreenHandler.sendContentUpdates();
                    this.player.updateCursorStack();
                    this.player.skipPacketSlotUpdates = false;
                } else {
                    this.transactions.put(this.player.currentScreenHandler.syncId, packet.getActionId());
                    this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getActionId(), false));
                    this.player.currentScreenHandler.setPlayerRestriction(this.player, false);
                    DefaultedList<ItemStack> lv3 = DefaultedList.of();
                    for (int j = 0; j < this.player.currentScreenHandler.slots.size(); ++j) {
                        ItemStack lv4 = this.player.currentScreenHandler.slots.get(j).getStack();
                        lv3.add(lv4.isEmpty() ? ItemStack.EMPTY : lv4);
                    }
                    this.player.onHandlerRegistered(this.player.currentScreenHandler, lv3);
                }
            }
        }
    }

    @Override
    public void onCraftRequest(CraftRequestC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.isSpectator() || this.player.currentScreenHandler.syncId != packet.getSyncId() || !this.player.currentScreenHandler.isNotRestricted(this.player) || !(this.player.currentScreenHandler instanceof AbstractRecipeScreenHandler)) {
            return;
        }
        this.server.getRecipeManager().get(packet.getRecipe()).ifPresent(arg2 -> ((AbstractRecipeScreenHandler)this.player.currentScreenHandler).fillInputSlots(packet.shouldCraftAll(), (Recipe<?>)arg2, this.player));
    }

    @Override
    public void onButtonClick(ButtonClickC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        if (this.player.currentScreenHandler.syncId == packet.getSyncId() && this.player.currentScreenHandler.isNotRestricted(this.player) && !this.player.isSpectator()) {
            this.player.currentScreenHandler.onButtonClick(this.player, packet.getButtonId());
            this.player.currentScreenHandler.sendContentUpdates();
        }
    }

    @Override
    public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (this.player.interactionManager.isCreative()) {
            boolean bl3;
            BlockPos lv3;
            BlockEntity lv4;
            boolean bl = packet.getSlot() < 0;
            ItemStack lv = packet.getItemStack();
            CompoundTag lv2 = lv.getSubTag("BlockEntityTag");
            if (!lv.isEmpty() && lv2 != null && lv2.contains("x") && lv2.contains("y") && lv2.contains("z") && (lv4 = this.player.world.getBlockEntity(lv3 = new BlockPos(lv2.getInt("x"), lv2.getInt("y"), lv2.getInt("z")))) != null) {
                CompoundTag lv5 = lv4.toTag(new CompoundTag());
                lv5.remove("x");
                lv5.remove("y");
                lv5.remove("z");
                lv.putSubTag("BlockEntityTag", lv5);
            }
            boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
            boolean bl4 = bl3 = lv.isEmpty() || lv.getDamage() >= 0 && lv.getCount() <= 64 && !lv.isEmpty();
            if (bl2 && bl3) {
                if (lv.isEmpty()) {
                    this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), ItemStack.EMPTY);
                } else {
                    this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), lv);
                }
                this.player.playerScreenHandler.setPlayerRestriction(this.player, true);
                this.player.playerScreenHandler.sendContentUpdates();
            } else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
                this.creativeItemDropThreshold += 20;
                this.player.dropItem(lv, true);
            }
        }
    }

    @Override
    public void onConfirmTransaction(ConfirmGuiActionC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        int i = this.player.currentScreenHandler.syncId;
        if (i == packet.getWindowId() && this.transactions.getOrDefault(i, (short)(packet.getSyncId() + 1)) == packet.getSyncId() && !this.player.currentScreenHandler.isNotRestricted(this.player) && !this.player.isSpectator()) {
            this.player.currentScreenHandler.setPlayerRestriction(this.player, true);
        }
    }

    @Override
    public void onSignUpdate(UpdateSignC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.updateLastActionTime();
        ServerWorld lv = this.player.getServerWorld();
        BlockPos lv2 = packet.getPos();
        if (lv.isChunkLoaded(lv2)) {
            BlockState lv3 = lv.getBlockState(lv2);
            BlockEntity lv4 = lv.getBlockEntity(lv2);
            if (!(lv4 instanceof SignBlockEntity)) {
                return;
            }
            SignBlockEntity lv5 = (SignBlockEntity)lv4;
            if (!lv5.isEditable() || lv5.getEditor() != this.player) {
                LOGGER.warn("Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
                return;
            }
            String[] strings = packet.getText();
            for (int i = 0; i < strings.length; ++i) {
                lv5.setTextOnRow(i, new LiteralText(Formatting.strip(strings[i])));
            }
            lv5.markDirty();
            lv.updateListeners(lv2, lv3, lv3, 3);
        }
    }

    @Override
    public void onKeepAlive(KeepAliveC2SPacket packet) {
        if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
            int i = (int)(Util.getMeasuringTimeMs() - this.lastKeepAliveTime);
            this.player.pingMilliseconds = (this.player.pingMilliseconds * 3 + i) / 4;
            this.waitingForKeepAlive = false;
        } else if (!this.isHost()) {
            this.disconnect(new TranslatableText("disconnect.timeout"));
        }
    }

    @Override
    public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.abilities.flying = packet.isFlying() && this.player.abilities.allowFlying;
    }

    @Override
    public void onClientSettings(ClientSettingsC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        this.player.setClientSettings(packet);
    }

    @Override
    public void onCustomPayload(CustomPayloadC2SPacket packet) {
    }

    @Override
    public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.hasPermissionLevel(2) && !this.isHost()) {
            return;
        }
        this.server.setDifficulty(packet.getDifficulty(), false);
    }

    @Override
    public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        if (!this.player.hasPermissionLevel(2) && !this.isHost()) {
            return;
        }
        this.server.setDifficultyLocked(packet.isDifficultyLocked());
    }
}

