/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.DynamicOps
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DynamicOps;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.class_5217;
import net.minecraft.class_5321;
import net.minecraft.class_5322;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity
extends PlayerEntity
implements ScreenHandlerListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private String clientLanguage = "en_US";
    public ServerPlayNetworkHandler networkHandler;
    public final MinecraftServer server;
    public final ServerPlayerInteractionManager interactionManager;
    private final List<Integer> removedEntities = Lists.newLinkedList();
    private final PlayerAdvancementTracker advancementTracker;
    private final ServerStatHandler statHandler;
    private float lastHealthScore = Float.MIN_VALUE;
    private int lastFoodScore = Integer.MIN_VALUE;
    private int lastAirScore = Integer.MIN_VALUE;
    private int lastArmorScore = Integer.MIN_VALUE;
    private int lastLevelScore = Integer.MIN_VALUE;
    private int lastExperienceScore = Integer.MIN_VALUE;
    private float syncedHealth = -1.0E8f;
    private int syncedFoodLevel = -99999999;
    private boolean syncedSaturationIsZero = true;
    private int syncedExperience = -99999999;
    private int field_13998 = 60;
    private ChatVisibility clientChatVisibility;
    private boolean clientChatColorsEnabled = true;
    private long lastActionTime = Util.getMeasuringTimeMs();
    private Entity cameraEntity;
    private boolean inTeleportationState;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook;
    private Vec3d levitationStartPos;
    private int levitationStartTick;
    private boolean disconnected;
    @Nullable
    private Vec3d enteredNetherPos;
    private ChunkSectionPos cameraPosition = ChunkSectionPos.from(0, 0, 0);
    private class_5321<DimensionType> spawnPointDimension = DimensionType.field_24753;
    private BlockPos spawnPointPosition;
    private boolean spawnPointSet;
    private int screenHandlerSyncId;
    public boolean field_13991;
    public int pingMilliseconds;
    public boolean notInAnyWorld;

    public ServerPlayerEntity(MinecraftServer minecraftServer, ServerWorld arg, GameProfile gameProfile, ServerPlayerInteractionManager arg2) {
        super(arg, arg.method_27911(), gameProfile);
        arg2.player = this;
        this.interactionManager = arg2;
        this.server = minecraftServer;
        this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
        this.statHandler = minecraftServer.getPlayerManager().createStatHandler(this);
        this.advancementTracker = minecraftServer.getPlayerManager().getAdvancementTracker(this);
        this.stepHeight = 1.0f;
        this.moveToSpawn(arg);
    }

    private void moveToSpawn(ServerWorld arg) {
        BlockPos lv = arg.method_27911();
        if (arg.getDimension().hasSkyLight() && arg.getServer().method_27728().getGameMode() != GameMode.ADVENTURE) {
            long l;
            long m;
            int i = Math.max(0, this.server.getSpawnRadius(arg));
            int j = MathHelper.floor(arg.getWorldBorder().getDistanceInsideBorder(lv.getX(), lv.getZ()));
            if (j < i) {
                i = j;
            }
            if (j <= 1) {
                i = 1;
            }
            int k = (m = (l = (long)(i * 2 + 1)) * l) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)m;
            int n = this.method_14244(k);
            int o = new Random().nextInt(k);
            for (int p = 0; p < k; ++p) {
                int q = (o + n * p) % k;
                int r = q % (i * 2 + 1);
                int s = q / (i * 2 + 1);
                BlockPos lv2 = class_5322.method_29197(arg, lv, i, r, s);
                if (lv2 == null) continue;
                this.refreshPositionAndAngles(lv2, 0.0f, 0.0f);
                if (!arg.doesNotCollide(this)) {
                    continue;
                }
                break;
            }
        } else {
            this.refreshPositionAndAngles(lv, 0.0f, 0.0f);
            while (!arg.doesNotCollide(this) && this.getY() < 255.0) {
                this.updatePosition(this.getX(), this.getY() + 1.0, this.getZ());
            }
        }
    }

    private int method_14244(int i) {
        return i <= 16 ? i - 1 : 17;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("playerGameType", 99)) {
            if (this.getServer().shouldForceGameMode()) {
                this.interactionManager.setGameMode(this.getServer().getDefaultGameMode());
            } else {
                this.interactionManager.setGameMode(GameMode.byId(arg.getInt("playerGameType")));
            }
        }
        if (arg.contains("enteredNetherPosition", 10)) {
            CompoundTag lv = arg.getCompound("enteredNetherPosition");
            this.enteredNetherPos = new Vec3d(lv.getDouble("x"), lv.getDouble("y"), lv.getDouble("z"));
        }
        this.seenCredits = arg.getBoolean("seenCredits");
        if (arg.contains("recipeBook", 10)) {
            this.recipeBook.fromTag(arg.getCompound("recipeBook"));
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
        if (arg.contains("SpawnX", 99) && arg.contains("SpawnY", 99) && arg.contains("SpawnZ", 99)) {
            this.spawnPointPosition = new BlockPos(arg.getInt("SpawnX"), arg.getInt("SpawnY"), arg.getInt("SpawnZ"));
            this.spawnPointSet = arg.getBoolean("SpawnForced");
            if (arg.contains("SpawnDimension")) {
                this.spawnPointDimension = DimensionType.field_24751.parse((DynamicOps)NbtOps.INSTANCE, (Object)arg.get("SpawnDimension")).resultOrPartial(((Logger)LOGGER)::error).orElse(DimensionType.field_24753);
            }
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("playerGameType", this.interactionManager.getGameMode().getId());
        arg.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPos != null) {
            CompoundTag lv = new CompoundTag();
            lv.putDouble("x", this.enteredNetherPos.x);
            lv.putDouble("y", this.enteredNetherPos.y);
            lv.putDouble("z", this.enteredNetherPos.z);
            arg.put("enteredNetherPosition", lv);
        }
        Entity lv2 = this.getRootVehicle();
        Entity lv3 = this.getVehicle();
        if (lv3 != null && lv2 != this && lv2.hasPlayerRider()) {
            CompoundTag lv4 = new CompoundTag();
            CompoundTag lv5 = new CompoundTag();
            lv2.saveToTag(lv5);
            lv4.putUuidNew("Attach", lv3.getUuid());
            lv4.put("Entity", lv5);
            arg.put("RootVehicle", lv4);
        }
        arg.put("recipeBook", this.recipeBook.toTag());
        arg.putString("Dimension", this.world.method_27983().method_29177().toString());
        if (this.spawnPointPosition != null) {
            arg.putInt("SpawnX", this.spawnPointPosition.getX());
            arg.putInt("SpawnY", this.spawnPointPosition.getY());
            arg.putInt("SpawnZ", this.spawnPointPosition.getZ());
            arg.putBoolean("SpawnForced", this.spawnPointSet);
            Identifier.field_25139.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.spawnPointDimension.method_29177()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(arg2 -> arg.put("SpawnDimension", (Tag)arg2));
        }
    }

    public void setExperiencePoints(int i) {
        float f = this.getNextLevelExperience();
        float g = (f - 1.0f) / f;
        this.experienceProgress = MathHelper.clamp((float)i / f, 0.0f, g);
        this.syncedExperience = -1;
    }

    public void setExperienceLevel(int i) {
        this.experienceLevel = i;
        this.syncedExperience = -1;
    }

    @Override
    public void addExperienceLevels(int i) {
        super.addExperienceLevels(i);
        this.syncedExperience = -1;
    }

    @Override
    public void applyEnchantmentCosts(ItemStack arg, int i) {
        super.applyEnchantmentCosts(arg, i);
        this.syncedExperience = -1;
    }

    public void onSpawn() {
        this.currentScreenHandler.addListener(this);
    }

    @Override
    public void enterCombat() {
        super.enterCombat();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTER_COMBAT));
    }

    @Override
    public void endCombat() {
        super.endCombat();
        this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END_COMBAT));
    }

    @Override
    protected void onBlockCollision(BlockState arg) {
        Criteria.ENTER_BLOCK.trigger(this, arg);
    }

    @Override
    protected ItemCooldownManager createCooldownManager() {
        return new ServerItemCooldownManager(this);
    }

    @Override
    public void tick() {
        this.interactionManager.update();
        --this.field_13998;
        if (this.timeUntilRegen > 0) {
            --this.timeUntilRegen;
        }
        this.currentScreenHandler.sendContentUpdates();
        if (!this.world.isClient && !this.currentScreenHandler.canUse(this)) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }
        while (!this.removedEntities.isEmpty()) {
            int i = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
            int[] is = new int[i];
            Iterator<Integer> iterator = this.removedEntities.iterator();
            int j = 0;
            while (iterator.hasNext() && j < i) {
                is[j++] = iterator.next();
                iterator.remove();
            }
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(is));
        }
        Entity lv = this.getCameraEntity();
        if (lv != this) {
            if (lv.isAlive()) {
                this.updatePositionAndAngles(lv.getX(), lv.getY(), lv.getZ(), lv.yaw, lv.pitch);
                this.getServerWorld().getChunkManager().updateCameraPosition(this);
                if (this.shouldDismount()) {
                    this.setCameraEntity(this);
                }
            } else {
                this.setCameraEntity(this);
            }
        }
        Criteria.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            Criteria.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
        }
        this.advancementTracker.sendUpdate(this);
    }

    public void playerTick() {
        try {
            if (!this.isSpectator() || this.world.isChunkLoaded(this.getBlockPos())) {
                super.tick();
            }
            for (int i = 0; i < this.inventory.size(); ++i) {
                Packet<?> lv2;
                ItemStack lv = this.inventory.getStack(i);
                if (!lv.getItem().isNetworkSynced() || (lv2 = ((NetworkSyncedItem)lv.getItem()).createSyncPacket(lv, this.world, this)) == null) continue;
                this.networkHandler.sendPacket(lv2);
            }
            if (this.getHealth() != this.syncedHealth || this.syncedFoodLevel != this.hungerManager.getFoodLevel() || this.hungerManager.getSaturationLevel() == 0.0f != this.syncedSaturationIsZero) {
                this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
                this.syncedHealth = this.getHealth();
                this.syncedFoodLevel = this.hungerManager.getFoodLevel();
                boolean bl = this.syncedSaturationIsZero = this.hungerManager.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
                this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
                this.updateScores(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.lastHealthScore));
            }
            if (this.hungerManager.getFoodLevel() != this.lastFoodScore) {
                this.lastFoodScore = this.hungerManager.getFoodLevel();
                this.updateScores(ScoreboardCriterion.FOOD, MathHelper.ceil(this.lastFoodScore));
            }
            if (this.getAir() != this.lastAirScore) {
                this.lastAirScore = this.getAir();
                this.updateScores(ScoreboardCriterion.AIR, MathHelper.ceil(this.lastAirScore));
            }
            if (this.getArmor() != this.lastArmorScore) {
                this.lastArmorScore = this.getArmor();
                this.updateScores(ScoreboardCriterion.ARMOR, MathHelper.ceil(this.lastArmorScore));
            }
            if (this.totalExperience != this.lastExperienceScore) {
                this.lastExperienceScore = this.totalExperience;
                this.updateScores(ScoreboardCriterion.XP, MathHelper.ceil(this.lastExperienceScore));
            }
            if (this.experienceLevel != this.lastLevelScore) {
                this.lastLevelScore = this.experienceLevel;
                this.updateScores(ScoreboardCriterion.LEVEL, MathHelper.ceil(this.lastLevelScore));
            }
            if (this.totalExperience != this.syncedExperience) {
                this.syncedExperience = this.totalExperience;
                this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.age % 20 == 0) {
                Criteria.LOCATION.trigger(this);
            }
        }
        catch (Throwable throwable) {
            CrashReport lv3 = CrashReport.create(throwable, "Ticking player");
            CrashReportSection lv4 = lv3.addElement("Player being ticked");
            this.populateCrashReport(lv4);
            throw new CrashException(lv3);
        }
    }

    private void updateScores(ScoreboardCriterion arg2, int i) {
        this.getScoreboard().forEachScore(arg2, this.getEntityName(), arg -> arg.setScore(i));
    }

    @Override
    public void onDeath(DamageSource arg) {
        boolean bl = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
        if (bl) {
            Text lv = this.getDamageTracker().getDeathMessage();
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, lv), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> {
                if (!future.isSuccess()) {
                    int i = 256;
                    String string = lv.asTruncatedString(256);
                    TranslatableText lv = new TranslatableText("death.attack.message_too_long", new LiteralText(string).formatted(Formatting.YELLOW));
                    MutableText lv2 = new TranslatableText("death.attack.even_more_magic", this.getDisplayName()).styled(arg2 -> arg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, lv)));
                    this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, lv2));
                }
            }));
            AbstractTeam lv2 = this.getScoreboardTeam();
            if (lv2 == null || lv2.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
                this.server.getPlayerManager().broadcastChatMessage(lv, MessageType.SYSTEM, Util.field_25140);
            } else if (lv2.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerManager().sendToTeam(this, lv);
            } else if (lv2.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerManager().sendToOtherTeams(this, lv);
            }
        } else {
            this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED));
        }
        this.dropShoulderEntities();
        if (!this.isSpectator()) {
            this.drop(arg);
        }
        this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
        LivingEntity lv3 = this.getPrimeAdversary();
        if (lv3 != null) {
            this.incrementStat(Stats.KILLED_BY.getOrCreateStat(lv3.getType()));
            lv3.updateKilledAdvancementCriterion(this, this.scoreAmount, arg);
            this.onKilledBy(lv3);
        }
        this.world.sendEntityStatus(this, (byte)3);
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
        this.getDamageTracker().update();
    }

    @Override
    public void updateKilledAdvancementCriterion(Entity arg, int i, DamageSource arg2) {
        if (arg == this) {
            return;
        }
        super.updateKilledAdvancementCriterion(arg, i, arg2);
        this.addScore(i);
        String string = this.getEntityName();
        String string2 = arg.getEntityName();
        this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
        if (arg instanceof PlayerEntity) {
            this.incrementStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
        } else {
            this.incrementStat(Stats.MOB_KILLS);
        }
        this.updateScoreboardScore(string, string2, ScoreboardCriterion.TEAM_KILLS);
        this.updateScoreboardScore(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
        Criteria.PLAYER_KILLED_ENTITY.trigger(this, arg, arg2);
    }

    private void updateScoreboardScore(String string, String string2, ScoreboardCriterion[] args) {
        int i;
        Team lv = this.getScoreboard().getPlayerTeam(string2);
        if (lv != null && (i = lv.getColor().getColorIndex()) >= 0 && i < args.length) {
            this.getScoreboard().forEachScore(args[i], string, ScoreboardPlayerScore::incrementScore);
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        boolean bl;
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        boolean bl2 = bl = this.server.isDedicated() && this.isPvpEnabled() && "fall".equals(arg.name);
        if (!bl && this.field_13998 > 0 && arg != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (arg instanceof EntityDamageSource) {
            PersistentProjectileEntity lv2;
            Entity lv3;
            Entity lv = arg.getAttacker();
            if (lv instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)lv)) {
                return false;
            }
            if (lv instanceof PersistentProjectileEntity && (lv3 = (lv2 = (PersistentProjectileEntity)lv).getOwner()) instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)lv3)) {
                return false;
            }
        }
        return super.damage(arg, f);
    }

    @Override
    public boolean shouldDamagePlayer(PlayerEntity arg) {
        if (!this.isPvpEnabled()) {
            return false;
        }
        return super.shouldDamagePlayer(arg);
    }

    private boolean isPvpEnabled() {
        return this.server.isPvpEnabled();
    }

    @Override
    @Nullable
    public Entity changeDimension(class_5321<DimensionType> arg) {
        float h;
        this.inTeleportationState = true;
        class_5321<DimensionType> lv = this.world.method_27983();
        if (lv == DimensionType.field_24755 && arg == DimensionType.field_24753) {
            this.detach();
            this.getServerWorld().removePlayer(this);
            if (!this.notInAnyWorld) {
                this.notInAnyWorld = true;
                this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        ServerWorld lv2 = this.server.getWorld(lv);
        ServerWorld lv3 = this.server.getWorld(arg);
        class_5217 lv4 = lv3.getLevelProperties();
        this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(arg.method_29177(), BiomeAccess.hashSeed(lv3.getSeed()), this.interactionManager.getGameMode(), lv3.method_27982(), lv3.method_28125(), true));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(lv4.getDifficulty(), lv4.isDifficultyLocked()));
        PlayerManager lv5 = this.server.getPlayerManager();
        lv5.sendCommandTree(this);
        lv2.removePlayer(this);
        this.removed = false;
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        float g = this.pitch;
        float i = h = this.yaw;
        lv2.getProfiler().push("moving");
        if (lv == DimensionType.field_24753 && arg == DimensionType.field_24755) {
            BlockPos lv6 = ServerWorld.field_25144;
            d = lv6.getX();
            e = lv6.getY();
            f = lv6.getZ();
            h = 90.0f;
            g = 0.0f;
        } else {
            if (lv == DimensionType.field_24753 && arg == DimensionType.field_24754) {
                this.enteredNetherPos = this.getPos();
            }
            Registry<DimensionType> lv7 = this.server.method_29174().method_29116();
            DimensionType lv8 = lv7.method_29107(lv);
            DimensionType lv9 = lv7.method_29107(arg);
            double j = 8.0;
            if (!lv8.method_28539() && lv9.method_28539()) {
                d /= 8.0;
                f /= 8.0;
            } else if (lv8.method_28539() && !lv9.method_28539()) {
                d *= 8.0;
                f *= 8.0;
            }
        }
        this.refreshPositionAndAngles(d, e, f, h, g);
        lv2.getProfiler().pop();
        lv2.getProfiler().push("placing");
        double k = Math.min(-2.9999872E7, lv3.getWorldBorder().getBoundWest() + 16.0);
        double l = Math.min(-2.9999872E7, lv3.getWorldBorder().getBoundNorth() + 16.0);
        double m = Math.min(2.9999872E7, lv3.getWorldBorder().getBoundEast() - 16.0);
        double n = Math.min(2.9999872E7, lv3.getWorldBorder().getBoundSouth() - 16.0);
        d = MathHelper.clamp(d, k, m);
        f = MathHelper.clamp(f, l, n);
        this.refreshPositionAndAngles(d, e, f, h, g);
        if (arg == DimensionType.field_24755) {
            int o = MathHelper.floor(this.getX());
            int p = MathHelper.floor(this.getY()) - 1;
            int q = MathHelper.floor(this.getZ());
            ServerWorld.method_29200(lv3);
            this.refreshPositionAndAngles(o, p, q, h, 0.0f);
            this.setVelocity(Vec3d.ZERO);
        } else if (!lv3.getPortalForcer().usePortal(this, i)) {
            lv3.getPortalForcer().createPortal(this);
            lv3.getPortalForcer().usePortal(this, i);
        }
        lv2.getProfiler().pop();
        this.setWorld(lv3);
        lv3.onPlayerChangeDimension(this);
        this.dimensionChanged(lv2);
        this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), h, g);
        this.interactionManager.setWorld(lv3);
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        lv5.sendWorldInfo(this, lv3);
        lv5.sendPlayerStatus(this);
        for (StatusEffectInstance lv10 : this.getStatusEffects()) {
            this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), lv10));
        }
        this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
        this.syncedExperience = -1;
        this.syncedHealth = -1.0f;
        this.syncedFoodLevel = -1;
        return this;
    }

    private void dimensionChanged(ServerWorld arg) {
        DimensionType lv = arg.getDimension();
        DimensionType lv2 = this.world.getDimension();
        class_5321<DimensionType> lv3 = arg.method_27983();
        class_5321<DimensionType> lv4 = this.world.method_27983();
        Criteria.CHANGED_DIMENSION.trigger(this, lv3, lv4);
        if (lv.method_28542() && lv2.method_28541() && this.enteredNetherPos != null) {
            Criteria.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
        }
        if (lv4 != DimensionType.field_24754) {
            this.enteredNetherPos = null;
        }
    }

    @Override
    public boolean canBeSpectated(ServerPlayerEntity arg) {
        if (arg.isSpectator()) {
            return this.getCameraEntity() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.canBeSpectated(arg);
    }

    private void sendBlockEntityUpdate(BlockEntity arg) {
        BlockEntityUpdateS2CPacket lv;
        if (arg != null && (lv = arg.toUpdatePacket()) != null) {
            this.networkHandler.sendPacket(lv);
        }
    }

    @Override
    public void sendPickup(Entity arg, int i) {
        super.sendPickup(arg, i);
        this.currentScreenHandler.sendContentUpdates();
    }

    @Override
    public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos arg2) {
        Direction lv = this.world.getBlockState(arg2).get(HorizontalFacingBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.OTHER_PROBLEM));
        }
        if (!this.world.getDimension().method_28537()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE));
        }
        if (!this.isBedTooFarAway(arg2, lv)) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.TOO_FAR_AWAY));
        }
        if (this.isBedObstructed(arg2, lv)) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.OBSTRUCTED));
        }
        this.setSpawnPoint(this.world.method_27983(), arg2, false, true);
        if (this.world.isDay()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW));
        }
        if (!this.isCreative()) {
            double d = 8.0;
            double e = 5.0;
            Vec3d lv2 = Vec3d.ofBottomCenter(arg2);
            List<HostileEntity> list = this.world.getEntities(HostileEntity.class, new Box(lv2.getX() - 8.0, lv2.getY() - 5.0, lv2.getZ() - 8.0, lv2.getX() + 8.0, lv2.getY() + 5.0, lv2.getZ() + 8.0), arg -> arg.isAngryAt(this));
            if (!list.isEmpty()) {
                return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_SAFE));
            }
        }
        Either either = super.trySleep(arg2).ifRight(arg -> {
            this.incrementStat(Stats.SLEEP_IN_BED);
            Criteria.SLEPT_IN_BED.trigger(this);
        });
        ((ServerWorld)this.world).updateSleepingPlayers();
        return either;
    }

    @Override
    public void sleep(BlockPos arg) {
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        super.sleep(arg);
    }

    private boolean isBedTooFarAway(BlockPos arg, Direction arg2) {
        return this.isBedTooFarAway(arg) || this.isBedTooFarAway(arg.offset(arg2.getOpposite()));
    }

    private boolean isBedTooFarAway(BlockPos arg) {
        Vec3d lv = Vec3d.ofBottomCenter(arg);
        return Math.abs(this.getX() - lv.getX()) <= 3.0 && Math.abs(this.getY() - lv.getY()) <= 2.0 && Math.abs(this.getZ() - lv.getZ()) <= 3.0;
    }

    private boolean isBedObstructed(BlockPos arg, Direction arg2) {
        BlockPos lv = arg.up();
        return !this.doesNotSuffocate(lv) || !this.doesNotSuffocate(lv.offset(arg2.getOpposite()));
    }

    @Override
    public void wakeUp(boolean bl, boolean bl2) {
        if (this.isSleeping()) {
            this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
        }
        super.wakeUp(bl, bl2);
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
    }

    @Override
    public boolean startRiding(Entity arg, boolean bl) {
        Entity lv = this.getVehicle();
        if (!super.startRiding(arg, bl)) {
            return false;
        }
        Entity lv2 = this.getVehicle();
        if (lv2 != lv && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
        return true;
    }

    @Override
    public void stopRiding() {
        Entity lv = this.getVehicle();
        super.stopRiding();
        Entity lv2 = this.getVehicle();
        if (lv2 != lv && this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource arg) {
        return super.isInvulnerableTo(arg) || this.isInTeleportationState() || this.abilities.invulnerable && arg == DamageSource.WITHER;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
    }

    @Override
    protected void applyFrostWalker(BlockPos arg) {
        if (!this.isSpectator()) {
            super.applyFrostWalker(arg);
        }
    }

    public void handleFall(double d, boolean bl) {
        BlockPos lv = this.getLandingPos();
        if (!this.world.isChunkLoaded(lv)) {
            return;
        }
        super.fall(d, bl, this.world.getBlockState(lv), lv);
    }

    @Override
    public void openEditSignScreen(SignBlockEntity arg) {
        arg.setEditor(this);
        this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(arg.getPos()));
    }

    private void incrementScreenHandlerSyncId() {
        this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
    }

    @Override
    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory arg) {
        if (arg == null) {
            return OptionalInt.empty();
        }
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeHandledScreen();
        }
        this.incrementScreenHandlerSyncId();
        ScreenHandler lv = arg.createMenu(this.screenHandlerSyncId, this.inventory, this);
        if (lv == null) {
            if (this.isSpectator()) {
                this.sendMessage(new TranslatableText("container.spectatorCantOpen").formatted(Formatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.networkHandler.sendPacket(new OpenScreenS2CPacket(lv.syncId, lv.getType(), arg.getDisplayName()));
        lv.addListener(this);
        this.currentScreenHandler = lv;
        return OptionalInt.of(this.screenHandlerSyncId);
    }

    @Override
    public void sendTradeOffers(int i, TraderOfferList arg, int j, int k, boolean bl, boolean bl2) {
        this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(i, arg, j, k, bl, bl2));
    }

    @Override
    public void openHorseInventory(HorseBaseEntity arg, Inventory arg2) {
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeHandledScreen();
        }
        this.incrementScreenHandlerSyncId();
        this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, arg2.size(), arg.getEntityId()));
        this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.inventory, arg2, arg);
        this.currentScreenHandler.addListener(this);
    }

    @Override
    public void openEditBookScreen(ItemStack arg, Hand arg2) {
        Item lv = arg.getItem();
        if (lv == Items.WRITTEN_BOOK) {
            if (WrittenBookItem.resolve(arg, this.getCommandSource(), this)) {
                this.currentScreenHandler.sendContentUpdates();
            }
            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(arg2));
        }
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity arg) {
        arg.setNeedsUpdatePacket(true);
        this.sendBlockEntityUpdate(arg);
    }

    @Override
    public void onSlotUpdate(ScreenHandler arg, int i, ItemStack arg2) {
        if (arg.getSlot(i) instanceof CraftingResultSlot) {
            return;
        }
        if (arg == this.playerScreenHandler) {
            Criteria.INVENTORY_CHANGED.trigger(this, this.inventory, arg2);
        }
        if (this.field_13991) {
            return;
        }
        this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(arg.syncId, i, arg2));
    }

    public void openHandledScreen(ScreenHandler arg) {
        this.onHandlerRegistered(arg, arg.getStacks());
    }

    @Override
    public void onHandlerRegistered(ScreenHandler arg, DefaultedList<ItemStack> arg2) {
        this.networkHandler.sendPacket(new InventoryS2CPacket(arg.syncId, arg2));
        this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }

    @Override
    public void onPropertyUpdate(ScreenHandler arg, int i, int j) {
        this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(arg.syncId, i, j));
    }

    @Override
    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
        this.closeCurrentScreen();
    }

    public void updateCursorStack() {
        if (this.field_13991) {
            return;
        }
        this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
    }

    public void closeCurrentScreen() {
        this.currentScreenHandler.close(this);
        this.currentScreenHandler = this.playerScreenHandler;
    }

    public void method_14218(float f, float g, boolean bl, boolean bl2) {
        if (this.hasVehicle()) {
            if (f >= -1.0f && f <= 1.0f) {
                this.sidewaysSpeed = f;
            }
            if (g >= -1.0f && g <= 1.0f) {
                this.forwardSpeed = g;
            }
            this.jumping = bl;
            this.setSneaking(bl2);
        }
    }

    @Override
    public void increaseStat(Stat<?> arg2, int i) {
        this.statHandler.increaseStat(this, arg2, i);
        this.getScoreboard().forEachScore(arg2, this.getEntityName(), arg -> arg.incrementScore(i));
    }

    @Override
    public void resetStat(Stat<?> arg) {
        this.statHandler.setStat(this, arg, 0);
        this.getScoreboard().forEachScore(arg, this.getEntityName(), ScoreboardPlayerScore::clearScore);
    }

    @Override
    public int unlockRecipes(Collection<Recipe<?>> collection) {
        return this.recipeBook.unlockRecipes(collection, this);
    }

    @Override
    public void unlockRecipes(Identifier[] args) {
        ArrayList list = Lists.newArrayList();
        for (Identifier lv : args) {
            this.server.getRecipeManager().get(lv).ifPresent(list::add);
        }
        this.unlockRecipes(list);
    }

    @Override
    public int lockRecipes(Collection<Recipe<?>> collection) {
        return this.recipeBook.lockRecipes(collection, this);
    }

    @Override
    public void addExperience(int i) {
        super.addExperience(i);
        this.syncedExperience = -1;
    }

    public void onDisconnect() {
        this.disconnected = true;
        this.removeAllPassengers();
        if (this.isSleeping()) {
            this.wakeUp(true, false);
        }
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public void markHealthDirty() {
        this.syncedHealth = -1.0E8f;
    }

    @Override
    public void sendMessage(Text arg, boolean bl) {
        this.networkHandler.sendPacket(new GameMessageS2CPacket(arg, bl ? MessageType.GAME_INFO : MessageType.CHAT, Util.field_25140));
    }

    @Override
    protected void consumeItem() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, 9));
            super.consumeItem();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor arg, Vec3d arg2) {
        super.lookAt(arg, arg2);
        this.networkHandler.sendPacket(new LookAtS2CPacket(arg, arg2.x, arg2.y, arg2.z));
    }

    public void method_14222(EntityAnchorArgumentType.EntityAnchor arg, Entity arg2, EntityAnchorArgumentType.EntityAnchor arg3) {
        Vec3d lv = arg3.positionAt(arg2);
        super.lookAt(arg, lv);
        this.networkHandler.sendPacket(new LookAtS2CPacket(arg, arg2, arg3));
    }

    public void copyFrom(ServerPlayerEntity arg, boolean bl) {
        if (bl) {
            this.inventory.clone(arg.inventory);
            this.setHealth(arg.getHealth());
            this.hungerManager = arg.hungerManager;
            this.experienceLevel = arg.experienceLevel;
            this.totalExperience = arg.totalExperience;
            this.experienceProgress = arg.experienceProgress;
            this.setScore(arg.getScore());
            this.lastNetherPortalPosition = arg.lastNetherPortalPosition;
            this.lastNetherPortalDirectionVector = arg.lastNetherPortalDirectionVector;
            this.lastNetherPortalDirection = arg.lastNetherPortalDirection;
        } else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || arg.isSpectator()) {
            this.inventory.clone(arg.inventory);
            this.experienceLevel = arg.experienceLevel;
            this.totalExperience = arg.totalExperience;
            this.experienceProgress = arg.experienceProgress;
            this.setScore(arg.getScore());
        }
        this.enchantmentTableSeed = arg.enchantmentTableSeed;
        this.enderChestInventory = arg.enderChestInventory;
        this.getDataTracker().set(PLAYER_MODEL_PARTS, arg.getDataTracker().get(PLAYER_MODEL_PARTS));
        this.syncedExperience = -1;
        this.syncedHealth = -1.0f;
        this.syncedFoodLevel = -1;
        this.recipeBook.copyFrom(arg.recipeBook);
        this.removedEntities.addAll(arg.removedEntities);
        this.seenCredits = arg.seenCredits;
        this.enteredNetherPos = arg.enteredNetherPos;
        this.setShoulderEntityLeft(arg.getShoulderEntityLeft());
        this.setShoulderEntityRight(arg.getShoulderEntityRight());
    }

    @Override
    protected void onStatusEffectApplied(StatusEffectInstance arg) {
        super.onStatusEffectApplied(arg);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), arg));
        if (arg.getEffectType() == StatusEffects.LEVITATION) {
            this.levitationStartTick = this.age;
            this.levitationStartPos = this.getPos();
        }
        Criteria.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void onStatusEffectUpgraded(StatusEffectInstance arg, boolean bl) {
        super.onStatusEffectUpgraded(arg, bl);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), arg));
        Criteria.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void onStatusEffectRemoved(StatusEffectInstance arg) {
        super.onStatusEffectRemoved(arg);
        this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), arg.getEffectType()));
        if (arg.getEffectType() == StatusEffects.LEVITATION) {
            this.levitationStartPos = null;
        }
        Criteria.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    public void requestTeleport(double d, double e, double f) {
        this.networkHandler.requestTeleport(d, e, f, this.yaw, this.pitch);
    }

    @Override
    public void positAfterTeleport(double d, double e, double f) {
        this.networkHandler.requestTeleport(d, e, f, this.yaw, this.pitch);
        this.networkHandler.syncWithPlayerPosition();
    }

    @Override
    public void addCritParticles(Entity arg) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(arg, 4));
    }

    @Override
    public void addEnchantedHitParticles(Entity arg) {
        this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(arg, 5));
    }

    @Override
    public void sendAbilitiesUpdate() {
        if (this.networkHandler == null) {
            return;
        }
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        this.updatePotionVisibility();
    }

    public ServerWorld getServerWorld() {
        return (ServerWorld)this.world;
    }

    @Override
    public void setGameMode(GameMode arg) {
        this.interactionManager.setGameMode(arg);
        this.networkHandler.sendPacket(new GameStateChangeS2CPacket(3, arg.getId()));
        if (arg == GameMode.SPECTATOR) {
            this.dropShoulderEntities();
            this.stopRiding();
        } else {
            this.setCameraEntity(this);
        }
        this.sendAbilitiesUpdate();
        this.markEffectsDirty();
    }

    @Override
    public boolean isSpectator() {
        return this.interactionManager.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return this.interactionManager.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public void sendSystemMessage(Text arg, UUID uUID) {
        this.sendMessage(arg, MessageType.SYSTEM, uUID);
    }

    public void sendMessage(Text arg, MessageType arg2, UUID uUID) {
        this.networkHandler.sendPacket(new GameMessageS2CPacket(arg, arg2, uUID), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> {
            if (!(future.isSuccess() || arg2 != MessageType.GAME_INFO && arg2 != MessageType.SYSTEM)) {
                int i = 256;
                String string = arg.asTruncatedString(256);
                MutableText lv = new LiteralText(string).formatted(Formatting.YELLOW);
                this.networkHandler.sendPacket(new GameMessageS2CPacket(new TranslatableText("multiplayer.message_not_delivered", lv).formatted(Formatting.RED), MessageType.SYSTEM, uUID));
            }
        }));
    }

    public String getIp() {
        String string = this.networkHandler.connection.getAddress().toString();
        string = string.substring(string.indexOf("/") + 1);
        string = string.substring(0, string.indexOf(":"));
        return string;
    }

    public void setClientSettings(ClientSettingsC2SPacket arg) {
        this.clientLanguage = arg.getLanguage();
        this.clientChatVisibility = arg.getChatVisibility();
        this.clientChatColorsEnabled = arg.hasChatColors();
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)arg.getPlayerModelBitMask());
        this.getDataTracker().set(MAIN_ARM, (byte)(arg.getMainArm() != Arm.LEFT ? 1 : 0));
    }

    public ChatVisibility getClientChatVisibility() {
        return this.clientChatVisibility;
    }

    public void sendResourcePackUrl(String string, String string2) {
        this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(string, string2));
    }

    @Override
    protected int getPermissionLevel() {
        return this.server.getPermissionLevel(this.getGameProfile());
    }

    public void updateLastActionTime() {
        this.lastActionTime = Util.getMeasuringTimeMs();
    }

    public ServerStatHandler getStatHandler() {
        return this.statHandler;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void onStoppedTracking(Entity arg) {
        if (arg instanceof PlayerEntity) {
            this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(arg.getEntityId()));
        } else {
            this.removedEntities.add(arg.getEntityId());
        }
    }

    public void onStartedTracking(Entity arg) {
        this.removedEntities.remove((Object)arg.getEntityId());
    }

    @Override
    protected void updatePotionVisibility() {
        if (this.isSpectator()) {
            this.clearPotionSwirls();
            this.setInvisible(true);
        } else {
            super.updatePotionVisibility();
        }
    }

    public Entity getCameraEntity() {
        return this.cameraEntity == null ? this : this.cameraEntity;
    }

    public void setCameraEntity(Entity arg) {
        Entity lv = this.getCameraEntity();
        Entity entity = this.cameraEntity = arg == null ? this : arg;
        if (lv != this.cameraEntity) {
            this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            this.requestTeleport(this.cameraEntity.getX(), this.cameraEntity.getY(), this.cameraEntity.getZ());
        }
    }

    @Override
    protected void tickNetherPortalCooldown() {
        if (this.netherPortalCooldown > 0 && !this.inTeleportationState) {
            --this.netherPortalCooldown;
        }
    }

    @Override
    public void attack(Entity arg) {
        if (this.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            this.setCameraEntity(arg);
        } else {
            super.attack(arg);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Text method_14206() {
        return null;
    }

    @Override
    public void swingHand(Hand arg) {
        super.swingHand(arg);
        this.resetLastAttackedTicks();
    }

    public boolean isInTeleportationState() {
        return this.inTeleportationState;
    }

    public void onTeleportationDone() {
        this.inTeleportationState = false;
    }

    public PlayerAdvancementTracker getAdvancementTracker() {
        return this.advancementTracker;
    }

    public void teleport(ServerWorld arg, double d, double e, double f, float g, float h) {
        this.setCameraEntity(this);
        this.stopRiding();
        if (arg == this.world) {
            this.networkHandler.requestTeleport(d, e, f, g, h);
        } else {
            ServerWorld lv = this.getServerWorld();
            class_5217 lv2 = arg.getLevelProperties();
            this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(arg.method_27983().method_29177(), BiomeAccess.hashSeed(arg.getSeed()), this.interactionManager.getGameMode(), arg.method_27982(), arg.method_28125(), true));
            this.networkHandler.sendPacket(new DifficultyS2CPacket(lv2.getDifficulty(), lv2.isDifficultyLocked()));
            this.server.getPlayerManager().sendCommandTree(this);
            lv.removePlayer(this);
            this.removed = false;
            this.refreshPositionAndAngles(d, e, f, g, h);
            this.setWorld(arg);
            arg.onPlayerTeleport(this);
            this.dimensionChanged(lv);
            this.networkHandler.requestTeleport(d, e, f, g, h);
            this.interactionManager.setWorld(arg);
            this.server.getPlayerManager().sendWorldInfo(this, arg);
            this.server.getPlayerManager().sendPlayerStatus(this);
        }
    }

    @Nullable
    public BlockPos getSpawnPointPosition() {
        return this.spawnPointPosition;
    }

    public class_5321<DimensionType> getSpawnPointDimension() {
        return this.spawnPointDimension;
    }

    public boolean isSpawnPointSet() {
        return this.spawnPointSet;
    }

    public void setSpawnPoint(class_5321<DimensionType> arg, BlockPos arg2, boolean bl, boolean bl2) {
        if (arg2 != null) {
            boolean bl3;
            boolean bl4 = bl3 = arg2.equals(this.spawnPointPosition) && arg.equals(this.spawnPointDimension);
            if (bl2 && !bl3) {
                this.sendSystemMessage(new TranslatableText("block.minecraft.set_spawn"), Util.field_25140);
            }
            this.spawnPointPosition = arg2;
            this.spawnPointDimension = arg;
            this.spawnPointSet = bl;
        } else {
            this.spawnPointPosition = null;
            this.spawnPointDimension = DimensionType.field_24753;
            this.spawnPointSet = false;
        }
    }

    public void sendInitialChunkPackets(ChunkPos arg, Packet<?> arg2, Packet<?> arg3) {
        this.networkHandler.sendPacket(arg3);
        this.networkHandler.sendPacket(arg2);
    }

    public void sendUnloadChunkPacket(ChunkPos arg) {
        if (this.isAlive()) {
            this.networkHandler.sendPacket(new UnloadChunkS2CPacket(arg.x, arg.z));
        }
    }

    public ChunkSectionPos getCameraPosition() {
        return this.cameraPosition;
    }

    public void setCameraPosition(ChunkSectionPos arg) {
        this.cameraPosition = arg;
    }

    @Override
    public void playSound(SoundEvent arg, SoundCategory arg2, float f, float g) {
        this.networkHandler.sendPacket(new PlaySoundS2CPacket(arg, arg2, this.getX(), this.getY(), this.getZ(), f, g));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new PlayerSpawnS2CPacket(this);
    }

    @Override
    public ItemEntity dropItem(ItemStack arg, boolean bl, boolean bl2) {
        ItemEntity lv = super.dropItem(arg, bl, bl2);
        if (lv == null) {
            return null;
        }
        this.world.spawnEntity(lv);
        ItemStack lv2 = lv.getStack();
        if (bl2) {
            if (!lv2.isEmpty()) {
                this.increaseStat(Stats.DROPPED.getOrCreateStat(lv2.getItem()), arg.getCount());
            }
            this.incrementStat(Stats.DROP);
        }
        return lv;
    }
}

