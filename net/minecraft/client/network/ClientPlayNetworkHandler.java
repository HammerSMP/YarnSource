/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.class_5455;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.realms.DisconnectedRealmsScreen;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.TagManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientPlayNetworkHandler
implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ClientConnection connection;
    private final GameProfile profile;
    private final Screen loginScreen;
    private MinecraftClient client;
    private ClientWorld world;
    private ClientWorld.Properties worldProperties;
    private boolean positionLookSetup;
    private final Map<UUID, PlayerListEntry> playerListEntries = Maps.newHashMap();
    private final ClientAdvancementManager advancementHandler;
    private final ClientCommandSource commandSource;
    private TagManager tagManager = TagManager.EMPTY;
    private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
    private int chunkLoadDistance = 3;
    private final Random random = new Random();
    private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher();
    private final RecipeManager recipeManager = new RecipeManager();
    private final UUID sessionId = UUID.randomUUID();
    private Set<RegistryKey<World>> worldKeys;
    private class_5455 registryTracker = class_5455.method_30528();

    public ClientPlayNetworkHandler(MinecraftClient arg, Screen arg2, ClientConnection arg3, GameProfile gameProfile) {
        this.client = arg;
        this.loginScreen = arg2;
        this.connection = arg3;
        this.profile = gameProfile;
        this.advancementHandler = new ClientAdvancementManager(arg);
        this.commandSource = new ClientCommandSource(this, arg);
    }

    public ClientCommandSource getCommandSource() {
        return this.commandSource;
    }

    public void clearWorld() {
        this.world = null;
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    @Override
    public void onGameJoin(GameJoinS2CPacket arg) {
        ClientWorld.Properties lv4;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
        if (!this.connection.isLocal()) {
            RequiredTagListRegistry.clearAllTags();
        }
        ArrayList arrayList = Lists.newArrayList(arg.method_29443());
        Collections.shuffle(arrayList);
        this.worldKeys = Sets.newLinkedHashSet((Iterable)arrayList);
        this.registryTracker = arg.getDimension();
        RegistryKey<DimensionType> lv = arg.method_29444();
        RegistryKey<World> lv2 = arg.getDimensionId();
        DimensionType lv3 = this.registryTracker.method_30518().get(lv);
        this.chunkLoadDistance = arg.getChunkLoadDistance();
        boolean bl = arg.isDebugWorld();
        boolean bl2 = arg.isFlatWorld();
        this.worldProperties = lv4 = new ClientWorld.Properties(Difficulty.NORMAL, arg.isHardcore(), bl2);
        this.world = new ClientWorld(this, lv4, lv2, lv, lv3, this.chunkLoadDistance, this.client::getProfiler, this.client.worldRenderer, bl, arg.getSha256Seed());
        this.client.joinWorld(this.world);
        if (this.client.player == null) {
            this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook());
            this.client.player.yaw = -180.0f;
            if (this.client.getServer() != null) {
                this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
            }
        }
        this.client.debugRenderer.reset();
        this.client.player.afterSpawn();
        int i = arg.getEntityId();
        this.world.addPlayer(i, this.client.player);
        this.client.player.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(this.client.player);
        this.client.cameraEntity = this.client.player;
        this.client.openScreen(new DownloadingTerrainScreen());
        this.client.player.setEntityId(i);
        this.client.player.setReducedDebugInfo(arg.hasReducedDebugInfo());
        this.client.player.setShowsDeathScreen(arg.showsDeathScreen());
        this.client.interactionManager.setGameMode(arg.getGameMode());
        this.client.interactionManager.method_30108(arg.method_30116());
        this.client.options.onPlayerModelPartChange();
        this.connection.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
        this.client.getGame().onStartGameSession();
    }

    @Override
    public void onEntitySpawn(EntitySpawnS2CPacket arg) {
        Object lv42;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        double d = arg.getX();
        double e = arg.getY();
        double f = arg.getZ();
        EntityType<?> lv = arg.getEntityTypeId();
        if (lv == EntityType.CHEST_MINECART) {
            ChestMinecartEntity lv2 = new ChestMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.FURNACE_MINECART) {
            FurnaceMinecartEntity lv3 = new FurnaceMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.TNT_MINECART) {
            TntMinecartEntity lv4 = new TntMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.SPAWNER_MINECART) {
            SpawnerMinecartEntity lv5 = new SpawnerMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.HOPPER_MINECART) {
            HopperMinecartEntity lv6 = new HopperMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.COMMAND_BLOCK_MINECART) {
            CommandBlockMinecartEntity lv7 = new CommandBlockMinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.MINECART) {
            MinecartEntity lv8 = new MinecartEntity(this.world, d, e, f);
        } else if (lv == EntityType.FISHING_BOBBER) {
            Entity lv9 = this.world.getEntityById(arg.getEntityData());
            if (lv9 instanceof PlayerEntity) {
                FishingBobberEntity lv10 = new FishingBobberEntity(this.world, (PlayerEntity)lv9, d, e, f);
            } else {
                Object lv11 = null;
            }
        } else if (lv == EntityType.ARROW) {
            ArrowEntity lv12 = new ArrowEntity(this.world, d, e, f);
            Entity lv13 = this.world.getEntityById(arg.getEntityData());
            if (lv13 != null) {
                ((PersistentProjectileEntity)lv12).setOwner(lv13);
            }
        } else if (lv == EntityType.SPECTRAL_ARROW) {
            SpectralArrowEntity lv14 = new SpectralArrowEntity(this.world, d, e, f);
            Entity lv15 = this.world.getEntityById(arg.getEntityData());
            if (lv15 != null) {
                ((PersistentProjectileEntity)lv14).setOwner(lv15);
            }
        } else if (lv == EntityType.TRIDENT) {
            TridentEntity lv16 = new TridentEntity(this.world, d, e, f);
            Entity lv17 = this.world.getEntityById(arg.getEntityData());
            if (lv17 != null) {
                ((PersistentProjectileEntity)lv16).setOwner(lv17);
            }
        } else if (lv == EntityType.SNOWBALL) {
            SnowballEntity lv18 = new SnowballEntity(this.world, d, e, f);
        } else if (lv == EntityType.LLAMA_SPIT) {
            LlamaSpitEntity lv19 = new LlamaSpitEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.ITEM_FRAME) {
            ItemFrameEntity lv20 = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(arg.getEntityData()));
        } else if (lv == EntityType.LEASH_KNOT) {
            LeashKnotEntity lv21 = new LeashKnotEntity(this.world, new BlockPos(d, e, f));
        } else if (lv == EntityType.ENDER_PEARL) {
            EnderPearlEntity lv22 = new EnderPearlEntity(this.world, d, e, f);
        } else if (lv == EntityType.EYE_OF_ENDER) {
            EyeOfEnderEntity lv23 = new EyeOfEnderEntity(this.world, d, e, f);
        } else if (lv == EntityType.FIREWORK_ROCKET) {
            FireworkRocketEntity lv24 = new FireworkRocketEntity(this.world, d, e, f, ItemStack.EMPTY);
        } else if (lv == EntityType.FIREBALL) {
            FireballEntity lv25 = new FireballEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.DRAGON_FIREBALL) {
            DragonFireballEntity lv26 = new DragonFireballEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.SMALL_FIREBALL) {
            SmallFireballEntity lv27 = new SmallFireballEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.WITHER_SKULL) {
            WitherSkullEntity lv28 = new WitherSkullEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.SHULKER_BULLET) {
            ShulkerBulletEntity lv29 = new ShulkerBulletEntity(this.world, d, e, f, arg.getVelocityX(), arg.getVelocityY(), arg.getVelocityZ());
        } else if (lv == EntityType.EGG) {
            EggEntity lv30 = new EggEntity(this.world, d, e, f);
        } else if (lv == EntityType.EVOKER_FANGS) {
            EvokerFangsEntity lv31 = new EvokerFangsEntity(this.world, d, e, f, 0.0f, 0, null);
        } else if (lv == EntityType.POTION) {
            PotionEntity lv32 = new PotionEntity(this.world, d, e, f);
        } else if (lv == EntityType.EXPERIENCE_BOTTLE) {
            ExperienceBottleEntity lv33 = new ExperienceBottleEntity(this.world, d, e, f);
        } else if (lv == EntityType.BOAT) {
            BoatEntity lv34 = new BoatEntity(this.world, d, e, f);
        } else if (lv == EntityType.TNT) {
            TntEntity lv35 = new TntEntity(this.world, d, e, f, null);
        } else if (lv == EntityType.ARMOR_STAND) {
            ArmorStandEntity lv36 = new ArmorStandEntity(this.world, d, e, f);
        } else if (lv == EntityType.END_CRYSTAL) {
            EndCrystalEntity lv37 = new EndCrystalEntity(this.world, d, e, f);
        } else if (lv == EntityType.ITEM) {
            ItemEntity lv38 = new ItemEntity(this.world, d, e, f);
        } else if (lv == EntityType.FALLING_BLOCK) {
            FallingBlockEntity lv39 = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(arg.getEntityData()));
        } else if (lv == EntityType.AREA_EFFECT_CLOUD) {
            AreaEffectCloudEntity lv40 = new AreaEffectCloudEntity(this.world, d, e, f);
        } else if (lv == EntityType.LIGHTNING_BOLT) {
            LightningEntity lv41 = new LightningEntity((EntityType<? extends LightningEntity>)EntityType.LIGHTNING_BOLT, (World)this.world);
        } else {
            lv42 = null;
        }
        if (lv42 != null) {
            int i = arg.getId();
            lv42.updateTrackedPosition(d, e, f);
            lv42.refreshPositionAfterTeleport(d, e, f);
            lv42.pitch = (float)(arg.getPitch() * 360) / 256.0f;
            lv42.yaw = (float)(arg.getYaw() * 360) / 256.0f;
            lv42.setEntityId(i);
            lv42.setUuid(arg.getUuid());
            this.world.addEntity(i, lv42);
            if (lv42 instanceof AbstractMinecartEntity) {
                this.client.getSoundManager().play(new MovingMinecartSoundInstance(lv42));
            }
        }
    }

    @Override
    public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        double d = arg.getX();
        double e = arg.getY();
        double f = arg.getZ();
        ExperienceOrbEntity lv = new ExperienceOrbEntity(this.world, d, e, f, arg.getExperience());
        lv.updateTrackedPosition(d, e, f);
        lv.yaw = 0.0f;
        lv.pitch = 0.0f;
        lv.setEntityId(arg.getId());
        this.world.addEntity(arg.getId(), lv);
    }

    @Override
    public void onPaintingSpawn(PaintingSpawnS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        PaintingEntity lv = new PaintingEntity(this.world, arg.getPos(), arg.getFacing(), arg.getMotive());
        lv.setEntityId(arg.getId());
        lv.setUuid(arg.getPaintingUuid());
        this.world.addEntity(arg.getId(), lv);
    }

    @Override
    public void onVelocityUpdate(EntityVelocityUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getId());
        if (lv == null) {
            return;
        }
        lv.setVelocityClient((double)arg.getVelocityX() / 8000.0, (double)arg.getVelocityY() / 8000.0, (double)arg.getVelocityZ() / 8000.0);
    }

    @Override
    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.id());
        if (lv != null && arg.getTrackedValues() != null) {
            lv.getDataTracker().writeUpdatedEntries(arg.getTrackedValues());
        }
    }

    @Override
    public void onPlayerSpawn(PlayerSpawnS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        double d = arg.getX();
        double e = arg.getY();
        double f = arg.getZ();
        float g = (float)(arg.getYaw() * 360) / 256.0f;
        float h = (float)(arg.getPitch() * 360) / 256.0f;
        int i = arg.getId();
        OtherClientPlayerEntity lv = new OtherClientPlayerEntity(this.client.world, this.getPlayerListEntry(arg.getPlayerUuid()).getProfile());
        lv.setEntityId(i);
        lv.resetPosition(d, e, f);
        lv.updateTrackedPosition(d, e, f);
        lv.updatePositionAndAngles(d, e, f, g, h);
        this.world.addPlayer(i, lv);
    }

    @Override
    public void onEntityPosition(EntityPositionS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getId());
        if (lv == null) {
            return;
        }
        double d = arg.getX();
        double e = arg.getY();
        double f = arg.getZ();
        lv.updateTrackedPosition(d, e, f);
        if (!lv.isLogicalSideForUpdatingMovement()) {
            float g = (float)(arg.getYaw() * 360) / 256.0f;
            float h = (float)(arg.getPitch() * 360) / 256.0f;
            lv.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
            lv.setOnGround(arg.isOnGround());
        }
    }

    @Override
    public void onHeldItemChange(HeldItemChangeS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (PlayerInventory.isValidHotbarIndex(arg.getSlot())) {
            this.client.player.inventory.selectedSlot = arg.getSlot();
        }
    }

    @Override
    public void onEntityUpdate(EntityS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = arg.getEntity(this.world);
        if (lv == null) {
            return;
        }
        if (!lv.isLogicalSideForUpdatingMovement()) {
            if (arg.isPositionChanged()) {
                Vec3d lv2 = arg.method_30302(lv.method_30227());
                lv.method_30228(lv2);
                float f = arg.hasRotation() ? (float)(arg.getYaw() * 360) / 256.0f : lv.yaw;
                float g = arg.hasRotation() ? (float)(arg.getPitch() * 360) / 256.0f : lv.pitch;
                lv.updateTrackedPositionAndAngles(lv2.getX(), lv2.getY(), lv2.getZ(), f, g, 3, false);
            } else if (arg.hasRotation()) {
                float h = (float)(arg.getYaw() * 360) / 256.0f;
                float i = (float)(arg.getPitch() * 360) / 256.0f;
                lv.updateTrackedPositionAndAngles(lv.getX(), lv.getY(), lv.getZ(), h, i, 3, false);
            }
            lv.setOnGround(arg.isOnGround());
        }
    }

    @Override
    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = arg.getEntity(this.world);
        if (lv == null) {
            return;
        }
        float f = (float)(arg.getHeadYaw() * 360) / 256.0f;
        lv.updateTrackedHeadRotation(f, 3);
    }

    @Override
    public void onEntitiesDestroy(EntitiesDestroyS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        for (int i = 0; i < arg.getEntityIds().length; ++i) {
            int j = arg.getEntityIds()[i];
            this.world.removeEntity(j);
        }
    }

    @Override
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket arg) {
        double o;
        double n;
        double k;
        double j;
        double g;
        double f;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        Vec3d lv2 = lv.getVelocity();
        boolean bl = arg.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.X);
        boolean bl2 = arg.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Y);
        boolean bl3 = arg.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Z);
        if (bl) {
            double d = lv2.getX();
            double e = lv.getX() + arg.getX();
            lv.lastRenderX += arg.getX();
        } else {
            f = 0.0;
            lv.lastRenderX = g = arg.getX();
        }
        if (bl2) {
            double h = lv2.getY();
            double i = lv.getY() + arg.getY();
            lv.lastRenderY += arg.getY();
        } else {
            j = 0.0;
            lv.lastRenderY = k = arg.getY();
        }
        if (bl3) {
            double l = lv2.getZ();
            double m = lv.getZ() + arg.getZ();
            lv.lastRenderZ += arg.getZ();
        } else {
            n = 0.0;
            lv.lastRenderZ = o = arg.getZ();
        }
        if (lv.age > 0 && lv.getVehicle() != null) {
            ((PlayerEntity)lv).method_29239();
        }
        lv.setPos(g, k, o);
        lv.prevX = g;
        lv.prevY = k;
        lv.prevZ = o;
        lv.setVelocity(f, j, n);
        float p = arg.getYaw();
        float q = arg.getPitch();
        if (arg.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.X_ROT)) {
            q += lv.pitch;
        }
        if (arg.getFlags().contains((Object)PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            p += lv.yaw;
        }
        lv.updatePositionAndAngles(g, k, o, p, q);
        this.connection.send(new TeleportConfirmC2SPacket(arg.getTeleportId()));
        this.connection.send(new PlayerMoveC2SPacket.Both(lv.getX(), lv.getY(), lv.getZ(), lv.yaw, lv.pitch, false));
        if (!this.positionLookSetup) {
            this.positionLookSetup = true;
            this.client.openScreen(null);
        }
    }

    @Override
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        arg.method_30621((arg_0, arg_1) -> this.world.setBlockStateWithoutNeighborUpdates(arg_0, arg_1));
    }

    @Override
    public void onChunkData(ChunkDataS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        int i = arg.getX();
        int j = arg.getZ();
        BiomeArray lv = arg.getBiomeArray() == null ? null : new BiomeArray(this.registryTracker.method_30530(Registry.BIOME_KEY), arg.getBiomeArray());
        WorldChunk lv2 = this.world.getChunkManager().loadChunkFromPacket(i, j, lv, arg.getReadBuffer(), arg.getHeightmaps(), arg.getVerticalStripBitmask(), arg.isFullChunk());
        if (lv2 != null && arg.isFullChunk()) {
            this.world.addEntitiesToChunk(lv2);
        }
        for (int k = 0; k < 16; ++k) {
            this.world.scheduleBlockRenders(i, k, j);
        }
        for (CompoundTag lv3 : arg.getBlockEntityTagList()) {
            BlockPos lv4 = new BlockPos(lv3.getInt("x"), lv3.getInt("y"), lv3.getInt("z"));
            BlockEntity lv5 = this.world.getBlockEntity(lv4);
            if (lv5 == null) continue;
            lv5.fromTag(this.world.getBlockState(lv4), lv3);
        }
        if (!arg.method_30144()) {
            this.world.getLightingProvider().setLightEnabled(lv2.getPos(), false);
            int l = arg.getVerticalStripBitmask();
            for (int m = 0; m < 16; ++m) {
                if ((l & 1 << m) == 0) continue;
                this.world.getLightingProvider().queueData(LightType.BLOCK, ChunkSectionPos.from(lv2.getPos(), m), new ChunkNibbleArray(), false);
                this.world.getLightingProvider().queueData(LightType.SKY, ChunkSectionPos.from(lv2.getPos(), m), new ChunkNibbleArray(), false);
            }
            this.world.getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
            this.world.getLightingProvider().setLightEnabled(lv2.getPos(), true);
            lv2.getLightSourcesStream().forEach(arg2 -> this.world.getLightingProvider().addLightSource((BlockPos)arg2, lv2.getLuminance((BlockPos)arg2)));
        }
    }

    @Override
    public void onUnloadChunk(UnloadChunkS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        int i = arg.getX();
        int j = arg.getZ();
        ClientChunkManager lv = this.world.getChunkManager();
        lv.unload(i, j);
        LightingProvider lv2 = lv.getLightingProvider();
        for (int k = 0; k < 16; ++k) {
            this.world.scheduleBlockRenders(i, k, j);
            lv2.updateSectionStatus(ChunkSectionPos.from(i, k, j), true);
        }
        lv2.setLightEnabled(new ChunkPos(i, j), false);
    }

    @Override
    public void onBlockUpdate(BlockUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.world.setBlockStateWithoutNeighborUpdates(arg.getPos(), arg.getState());
    }

    @Override
    public void onDisconnect(DisconnectS2CPacket arg) {
        this.connection.disconnect(arg.getReason());
    }

    @Override
    public void onDisconnected(Text arg) {
        this.client.disconnect();
        if (this.loginScreen != null) {
            if (this.loginScreen instanceof RealmsScreen) {
                this.client.openScreen(new DisconnectedRealmsScreen(this.loginScreen, "disconnect.lost", arg));
            } else {
                this.client.openScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", arg));
            }
        } else {
            this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", arg));
        }
    }

    public void sendPacket(Packet<?> arg) {
        this.connection.send(arg);
    }

    @Override
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getEntityId());
        LivingEntity lv2 = (LivingEntity)this.world.getEntityById(arg.getCollectorEntityId());
        if (lv2 == null) {
            lv2 = this.client.player;
        }
        if (lv != null) {
            if (lv instanceof ExperienceOrbEntity) {
                this.world.playSound(lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.world.playSound(lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            this.client.particleManager.addParticle(new ItemPickupParticle(this.client.getEntityRenderManager(), this.client.getBufferBuilders(), this.world, lv, lv2));
            if (lv instanceof ItemEntity) {
                ItemEntity lv3 = (ItemEntity)lv;
                ItemStack lv4 = lv3.getStack();
                lv4.decrement(arg.getStackAmount());
                if (lv4.isEmpty()) {
                    this.world.removeEntity(arg.getEntityId());
                }
            } else {
                this.world.removeEntity(arg.getEntityId());
            }
        }
    }

    @Override
    public void onGameMessage(GameMessageS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.inGameHud.addChatMessage(arg.getLocation(), arg.getMessage(), arg.getSenderUuid());
    }

    @Override
    public void onEntityAnimation(EntityAnimationS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getId());
        if (lv == null) {
            return;
        }
        if (arg.getAnimationId() == 0) {
            LivingEntity lv2 = (LivingEntity)lv;
            lv2.swingHand(Hand.MAIN_HAND);
        } else if (arg.getAnimationId() == 3) {
            LivingEntity lv3 = (LivingEntity)lv;
            lv3.swingHand(Hand.OFF_HAND);
        } else if (arg.getAnimationId() == 1) {
            lv.animateDamage();
        } else if (arg.getAnimationId() == 2) {
            PlayerEntity lv4 = (PlayerEntity)lv;
            lv4.wakeUp(false, false);
        } else if (arg.getAnimationId() == 4) {
            this.client.particleManager.addEmitter(lv, ParticleTypes.CRIT);
        } else if (arg.getAnimationId() == 5) {
            this.client.particleManager.addEmitter(lv, ParticleTypes.ENCHANTED_HIT);
        }
    }

    @Override
    public void onMobSpawn(MobSpawnS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        double d = arg.getX();
        double e = arg.getY();
        double f = arg.getZ();
        float g = (float)(arg.getYaw() * 360) / 256.0f;
        float h = (float)(arg.getPitch() * 360) / 256.0f;
        LivingEntity lv = (LivingEntity)EntityType.createInstanceFromId(arg.getEntityTypeId(), this.client.world);
        if (lv != null) {
            lv.updateTrackedPosition(d, e, f);
            lv.bodyYaw = (float)(arg.getHeadYaw() * 360) / 256.0f;
            lv.headYaw = (float)(arg.getHeadYaw() * 360) / 256.0f;
            if (lv instanceof EnderDragonEntity) {
                EnderDragonPart[] lvs = ((EnderDragonEntity)lv).getBodyParts();
                for (int i = 0; i < lvs.length; ++i) {
                    lvs[i].setEntityId(i + arg.getId());
                }
            }
            lv.setEntityId(arg.getId());
            lv.setUuid(arg.getUuid());
            lv.updatePositionAndAngles(d, e, f, g, h);
            lv.setVelocity((float)arg.getVelocityX() / 8000.0f, (float)arg.getVelocityY() / 8000.0f, (float)arg.getVelocityZ() / 8000.0f);
            this.world.addEntity(arg.getId(), lv);
            if (lv instanceof BeeEntity) {
                PassiveBeeSoundInstance lv3;
                boolean bl = ((BeeEntity)lv).hasAngerTime();
                if (bl) {
                    AggressiveBeeSoundInstance lv2 = new AggressiveBeeSoundInstance((BeeEntity)lv);
                } else {
                    lv3 = new PassiveBeeSoundInstance((BeeEntity)lv);
                }
                this.client.getSoundManager().playNextTick(lv3);
            }
        } else {
            LOGGER.warn("Skipping Entity with id {}", (Object)arg.getEntityTypeId());
        }
    }

    @Override
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.world.method_29089(arg.getTime());
        this.client.world.setTimeOfDay(arg.getTimeOfDay());
    }

    @Override
    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.world.setSpawnPos(arg.getPos());
    }

    @Override
    public void onEntityPassengersSet(EntityPassengersSetS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getId());
        if (lv == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean bl = lv.hasPassengerDeep(this.client.player);
        lv.removeAllPassengers();
        for (int i : arg.getPassengerIds()) {
            Entity lv2 = this.world.getEntityById(i);
            if (lv2 == null) continue;
            lv2.startRiding(lv, true);
            if (lv2 != this.client.player || bl) continue;
            this.client.inGameHud.setOverlayMessage(new TranslatableText("mount.onboard", this.client.options.keySneak.getBoundKeyLocalizedText()), false);
        }
    }

    @Override
    public void onEntityAttach(EntityAttachS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getAttachedEntityId());
        if (lv instanceof MobEntity) {
            ((MobEntity)lv).setHoldingEntityId(arg.getHoldingEntityId());
        }
    }

    private static ItemStack getActiveTotemOfUndying(PlayerEntity arg) {
        for (Hand lv : Hand.values()) {
            ItemStack lv2 = arg.getStackInHand(lv);
            if (lv2.getItem() != Items.TOTEM_OF_UNDYING) continue;
            return lv2;
        }
        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void onEntityStatus(EntityStatusS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = arg.getEntity(this.world);
        if (lv != null) {
            if (arg.getStatus() == 21) {
                this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)lv));
            } else if (arg.getStatus() == 35) {
                int i = 40;
                this.client.particleManager.addEmitter(lv, ParticleTypes.TOTEM_OF_UNDYING, 30);
                this.world.playSound(lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ITEM_TOTEM_USE, lv.getSoundCategory(), 1.0f, 1.0f, false);
                if (lv == this.client.player) {
                    this.client.gameRenderer.showFloatingItem(ClientPlayNetworkHandler.getActiveTotemOfUndying(this.client.player));
                }
            } else {
                lv.handleStatus(arg.getStatus());
            }
        }
    }

    @Override
    public void onHealthUpdate(HealthUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.player.updateHealth(arg.getHealth());
        this.client.player.getHungerManager().setFoodLevel(arg.getFood());
        this.client.player.getHungerManager().setSaturationLevelClient(arg.getSaturation());
    }

    @Override
    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.player.setExperience(arg.getBarProgress(), arg.getExperienceLevel(), arg.getExperience());
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        RegistryKey<DimensionType> lv = arg.method_29445();
        RegistryKey<World> lv2 = arg.getDimension();
        DimensionType lv3 = this.registryTracker.method_30518().get(lv);
        ClientPlayerEntity lv4 = this.client.player;
        int i = lv4.getEntityId();
        this.positionLookSetup = false;
        if (lv2 != lv4.world.getRegistryKey()) {
            ClientWorld.Properties lv6;
            Scoreboard lv5 = this.world.getScoreboard();
            boolean bl = arg.isDebugWorld();
            boolean bl2 = arg.isFlatWorld();
            this.worldProperties = lv6 = new ClientWorld.Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), bl2);
            this.world = new ClientWorld(this, lv6, lv2, lv, lv3, this.chunkLoadDistance, this.client::getProfiler, this.client.worldRenderer, bl, arg.getSha256Seed());
            this.world.setScoreboard(lv5);
            this.client.joinWorld(this.world);
            this.client.openScreen(new DownloadingTerrainScreen());
        }
        this.world.finishRemovingEntities();
        String string = lv4.getServerBrand();
        this.client.cameraEntity = null;
        ClientPlayerEntity lv7 = this.client.interactionManager.createPlayer(this.world, lv4.getStatHandler(), lv4.getRecipeBook(), lv4.isSneaking(), lv4.isSprinting());
        lv7.setEntityId(i);
        this.client.player = lv7;
        if (lv2 != lv4.world.getRegistryKey()) {
            this.client.getMusicTracker().stop();
        }
        this.client.cameraEntity = lv7;
        lv7.getDataTracker().writeUpdatedEntries(lv4.getDataTracker().getAllEntries());
        if (arg.shouldKeepPlayerAttributes()) {
            lv7.getAttributes().setFrom(lv4.getAttributes());
        }
        lv7.afterSpawn();
        lv7.setServerBrand(string);
        this.world.addPlayer(i, lv7);
        lv7.yaw = -180.0f;
        lv7.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities(lv7);
        lv7.setReducedDebugInfo(lv4.getReducedDebugInfo());
        lv7.setShowsDeathScreen(lv4.showsDeathScreen());
        if (this.client.currentScreen instanceof DeathScreen) {
            this.client.openScreen(null);
        }
        this.client.interactionManager.setGameMode(arg.getGameMode());
        this.client.interactionManager.method_30108(arg.method_30117());
    }

    @Override
    public void onExplosion(ExplosionS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Explosion lv = new Explosion(this.client.world, null, arg.getX(), arg.getY(), arg.getZ(), arg.getRadius(), arg.getAffectedBlocks());
        lv.affectWorld(true);
        this.client.player.setVelocity(this.client.player.getVelocity().add(arg.getPlayerVelocityX(), arg.getPlayerVelocityY(), arg.getPlayerVelocityZ()));
    }

    @Override
    public void onOpenHorseScreen(OpenHorseScreenS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getHorseId());
        if (lv instanceof HorseBaseEntity) {
            ClientPlayerEntity lv2 = this.client.player;
            HorseBaseEntity lv3 = (HorseBaseEntity)lv;
            SimpleInventory lv4 = new SimpleInventory(arg.getSlotCount());
            HorseScreenHandler lv5 = new HorseScreenHandler(arg.getSyncId(), lv2.inventory, lv4, lv3);
            lv2.currentScreenHandler = lv5;
            this.client.openScreen(new HorseScreen(lv5, lv2.inventory, lv3));
        }
    }

    @Override
    public void onOpenScreen(OpenScreenS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        HandledScreens.open(arg.getScreenHandlerType(), this.client, arg.getSyncId(), arg.getName());
    }

    @Override
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        ItemStack lv2 = arg.getItemStack();
        int i = arg.getSlot();
        this.client.getTutorialManager().onSlotUpdate(lv2);
        if (arg.getSyncId() == -1) {
            if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
                lv.inventory.setCursorStack(lv2);
            }
        } else if (arg.getSyncId() == -2) {
            lv.inventory.setStack(i, lv2);
        } else {
            boolean bl = false;
            if (this.client.currentScreen instanceof CreativeInventoryScreen) {
                CreativeInventoryScreen lv3 = (CreativeInventoryScreen)this.client.currentScreen;
                boolean bl2 = bl = lv3.getSelectedTab() != ItemGroup.INVENTORY.getIndex();
            }
            if (arg.getSyncId() == 0 && arg.getSlot() >= 36 && i < 45) {
                ItemStack lv4;
                if (!lv2.isEmpty() && ((lv4 = lv.playerScreenHandler.getSlot(i).getStack()).isEmpty() || lv4.getCount() < lv2.getCount())) {
                    lv2.setCooldown(5);
                }
                lv.playerScreenHandler.setStackInSlot(i, lv2);
            } else if (!(arg.getSyncId() != lv.currentScreenHandler.syncId || arg.getSyncId() == 0 && bl)) {
                lv.currentScreenHandler.setStackInSlot(i, lv2);
            }
        }
    }

    @Override
    public void onGuiActionConfirm(ConfirmGuiActionS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ScreenHandler lv = null;
        ClientPlayerEntity lv2 = this.client.player;
        if (arg.getId() == 0) {
            lv = lv2.playerScreenHandler;
        } else if (arg.getId() == lv2.currentScreenHandler.syncId) {
            lv = lv2.currentScreenHandler;
        }
        if (lv != null && !arg.wasAccepted()) {
            this.sendPacket(new ConfirmGuiActionC2SPacket(arg.getId(), arg.getActionId(), true));
        }
    }

    @Override
    public void onInventory(InventoryS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        if (arg.getSyncId() == 0) {
            lv.playerScreenHandler.updateSlotStacks(arg.getContents());
        } else if (arg.getSyncId() == lv.currentScreenHandler.syncId) {
            lv.currentScreenHandler.updateSlotStacks(arg.getContents());
        }
    }

    @Override
    public void onSignEditorOpen(SignEditorOpenS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        BlockEntity lv = this.world.getBlockEntity(arg.getPos());
        if (!(lv instanceof SignBlockEntity)) {
            lv = new SignBlockEntity();
            lv.setLocation(this.world, arg.getPos());
        }
        this.client.player.openEditSignScreen((SignBlockEntity)lv);
    }

    @Override
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket arg) {
        boolean bl;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        BlockPos lv = arg.getPos();
        BlockEntity lv2 = this.client.world.getBlockEntity(lv);
        int i = arg.getBlockEntityType();
        boolean bl2 = bl = i == 2 && lv2 instanceof CommandBlockBlockEntity;
        if (i == 1 && lv2 instanceof MobSpawnerBlockEntity || bl || i == 3 && lv2 instanceof BeaconBlockEntity || i == 4 && lv2 instanceof SkullBlockEntity || i == 6 && lv2 instanceof BannerBlockEntity || i == 7 && lv2 instanceof StructureBlockBlockEntity || i == 8 && lv2 instanceof EndGatewayBlockEntity || i == 9 && lv2 instanceof SignBlockEntity || i == 11 && lv2 instanceof BedBlockEntity || i == 5 && lv2 instanceof ConduitBlockEntity || i == 12 && lv2 instanceof JigsawBlockEntity || i == 13 && lv2 instanceof CampfireBlockEntity || i == 14 && lv2 instanceof BeehiveBlockEntity) {
            lv2.fromTag(this.client.world.getBlockState(lv), arg.getCompoundTag());
        }
        if (bl && this.client.currentScreen instanceof CommandBlockScreen) {
            ((CommandBlockScreen)this.client.currentScreen).updateCommandBlock();
        }
    }

    @Override
    public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        if (lv.currentScreenHandler != null && lv.currentScreenHandler.syncId == arg.getSyncId()) {
            lv.currentScreenHandler.setProperty(arg.getPropertyId(), arg.getValue());
        }
    }

    @Override
    public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getId());
        if (lv != null) {
            arg.method_30145().forEach(pair -> lv.equipStack((EquipmentSlot)((Object)((Object)pair.getFirst())), (ItemStack)pair.getSecond()));
        }
    }

    @Override
    public void onCloseScreen(CloseScreenS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.player.closeScreen();
    }

    @Override
    public void onBlockEvent(BlockEventS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.world.addSyncedBlockEvent(arg.getPos(), arg.getBlock(), arg.getType(), arg.getData());
    }

    @Override
    public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.world.setBlockBreakingInfo(arg.getEntityId(), arg.getPos(), arg.getProgress());
    }

    @Override
    public void onGameStateChange(GameStateChangeS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        GameStateChangeS2CPacket.Reason lv2 = arg.getReason();
        float f = arg.getValue();
        int i = MathHelper.floor(f + 0.5f);
        if (lv2 == GameStateChangeS2CPacket.NO_RESPAWN_BLOCK) {
            ((PlayerEntity)lv).sendMessage(new TranslatableText("block.minecraft.spawn.not_valid"), false);
        } else if (lv2 == GameStateChangeS2CPacket.RAIN_STARTED) {
            this.world.getLevelProperties().setRaining(true);
            this.world.setRainGradient(0.0f);
        } else if (lv2 == GameStateChangeS2CPacket.RAIN_STOPPED) {
            this.world.getLevelProperties().setRaining(false);
            this.world.setRainGradient(1.0f);
        } else if (lv2 == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
            this.client.interactionManager.setGameMode(GameMode.byId(i));
        } else if (lv2 == GameStateChangeS2CPacket.GAME_WON) {
            if (i == 0) {
                this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
                this.client.openScreen(new DownloadingTerrainScreen());
            } else if (i == 1) {
                this.client.openScreen(new CreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN))));
            }
        } else if (lv2 == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN) {
            GameOptions lv3 = this.client.options;
            if (f == 0.0f) {
                this.client.openScreen(new DemoScreen());
            } else if (f == 101.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.movement", lv3.keyForward.getBoundKeyLocalizedText(), lv3.keyLeft.getBoundKeyLocalizedText(), lv3.keyBack.getBoundKeyLocalizedText(), lv3.keyRight.getBoundKeyLocalizedText()));
            } else if (f == 102.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.jump", lv3.keyJump.getBoundKeyLocalizedText()));
            } else if (f == 103.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.inventory", lv3.keyInventory.getBoundKeyLocalizedText()));
            } else if (f == 104.0f) {
                this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.day.6", lv3.keyScreenshot.getBoundKeyLocalizedText()));
            }
        } else if (lv2 == GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER) {
            this.world.playSound(lv, lv.getX(), lv.getEyeY(), lv.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18f, 0.45f);
        } else if (lv2 == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
            this.world.setRainGradient(f);
        } else if (lv2 == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
            this.world.setThunderGradient(f);
        } else if (lv2 == GameStateChangeS2CPacket.PUFFERFISH_STING) {
            this.world.playSound(lv, lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (lv2 == GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT) {
            this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, lv.getX(), lv.getY(), lv.getZ(), 0.0, 0.0, 0.0);
            if (i == 1) {
                this.world.playSound(lv, lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0f, 1.0f);
            }
        } else if (lv2 == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
            this.client.player.setShowsDeathScreen(f == 0.0f);
        }
    }

    @Override
    public void onMapUpdate(MapUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        MapRenderer lv = this.client.gameRenderer.getMapRenderer();
        String string = FilledMapItem.getMapName(arg.getId());
        MapState lv2 = this.client.world.getMapState(string);
        if (lv2 == null) {
            MapState lv3;
            lv2 = new MapState(string);
            if (lv.getTexture(string) != null && (lv3 = lv.getState(lv.getTexture(string))) != null) {
                lv2 = lv3;
            }
            this.client.world.putMapState(lv2);
        }
        arg.apply(lv2);
        lv.updateTexture(lv2);
    }

    @Override
    public void onWorldEvent(WorldEventS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (arg.isGlobal()) {
            this.client.world.syncGlobalEvent(arg.getEventId(), arg.getPos(), arg.getData());
        } else {
            this.client.world.syncWorldEvent(arg.getEventId(), arg.getPos(), arg.getData());
        }
    }

    @Override
    public void onAdvancements(AdvancementUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.advancementHandler.onAdvancements(arg);
    }

    @Override
    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Identifier lv = arg.getTabId();
        if (lv == null) {
            this.advancementHandler.selectTab(null, false);
        } else {
            Advancement lv2 = this.advancementHandler.getManager().get(lv);
            this.advancementHandler.selectTab(lv2, false);
        }
    }

    @Override
    public void onCommandTree(CommandTreeS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.commandDispatcher = new CommandDispatcher(arg.getCommandTree());
    }

    @Override
    public void onStopSound(StopSoundS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.getSoundManager().stopSounds(arg.getSoundId(), arg.getCategory());
    }

    @Override
    public void onCommandSuggestions(CommandSuggestionsS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.commandSource.onCommandSuggestions(arg.getCompletionId(), arg.getSuggestions());
    }

    @Override
    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.recipeManager.setRecipes(arg.getRecipes());
        SearchableContainer<RecipeResultCollection> lv = this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT);
        lv.clear();
        ClientRecipeBook lv2 = this.client.player.getRecipeBook();
        lv2.reload(this.recipeManager.values());
        lv2.getOrderedResults().forEach(lv::add);
        lv.reload();
    }

    @Override
    public void onLookAt(LookAtS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Vec3d lv = arg.getTargetPosition(this.world);
        if (lv != null) {
            this.client.player.lookAt(arg.getSelfAnchor(), lv);
        }
    }

    @Override
    public void onTagQuery(TagQueryResponseS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (!this.dataQueryHandler.handleQueryResponse(arg.getTransactionId(), arg.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)arg.getTransactionId());
        }
    }

    @Override
    public void onStatistics(StatisticsS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        for (Map.Entry<Stat<?>, Integer> entry : arg.getStatMap().entrySet()) {
            Stat<?> lv = entry.getKey();
            int i = entry.getValue();
            this.client.player.getStatHandler().setStat(this.client.player, lv, i);
        }
        if (this.client.currentScreen instanceof StatsListener) {
            ((StatsListener)((Object)this.client.currentScreen)).onStatsReady();
        }
    }

    @Override
    public void onUnlockRecipes(UnlockRecipesS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientRecipeBook lv = this.client.player.getRecipeBook();
        lv.setOptions(arg.isFurnaceFilteringCraftable());
        UnlockRecipesS2CPacket.Action lv2 = arg.getAction();
        switch (lv2) {
            case REMOVE: {
                for (Identifier lv3 : arg.getRecipeIdsToChange()) {
                    this.recipeManager.get(lv3).ifPresent(lv::remove);
                }
                break;
            }
            case INIT: {
                for (Identifier lv4 : arg.getRecipeIdsToChange()) {
                    this.recipeManager.get(lv4).ifPresent(lv::add);
                }
                for (Identifier lv5 : arg.getRecipeIdsToInit()) {
                    this.recipeManager.get(lv5).ifPresent(lv::display);
                }
                break;
            }
            case ADD: {
                for (Identifier lv6 : arg.getRecipeIdsToChange()) {
                    this.recipeManager.get(lv6).ifPresent(arg2 -> {
                        lv.add((Recipe<?>)arg2);
                        lv.display((Recipe<?>)arg2);
                        RecipeToast.show(this.client.getToastManager(), arg2);
                    });
                }
                break;
            }
        }
        lv.getOrderedResults().forEach(arg2 -> arg2.initialize(lv));
        if (this.client.currentScreen instanceof RecipeBookProvider) {
            ((RecipeBookProvider)((Object)this.client.currentScreen)).refreshRecipeBook();
        }
    }

    @Override
    public void onEntityPotionEffect(EntityStatusEffectS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getEntityId());
        if (!(lv instanceof LivingEntity)) {
            return;
        }
        StatusEffect lv2 = StatusEffect.byRawId(arg.getEffectId());
        if (lv2 == null) {
            return;
        }
        StatusEffectInstance lv3 = new StatusEffectInstance(lv2, arg.getDuration(), arg.getAmplifier(), arg.isAmbient(), arg.shouldShowParticles(), arg.shouldShowIcon());
        lv3.setPermanent(arg.isPermanent());
        ((LivingEntity)lv).applyStatusEffect(lv3);
    }

    @Override
    public void onSynchronizeTags(SynchronizeTagsS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        TagManager lv = arg.getTagManager();
        Multimap<Identifier, Identifier> multimap = RequiredTagListRegistry.getMissingTags(lv);
        if (!multimap.isEmpty()) {
            LOGGER.warn("Incomplete server tags, disconnecting. Missing: {}", multimap);
            this.connection.disconnect(new TranslatableText("multiplayer.disconnect.missing_tags"));
            return;
        }
        this.tagManager = lv;
        if (!this.connection.isLocal()) {
            lv.apply();
        }
        this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
    }

    @Override
    public void onCombatEvent(CombatEventS2CPacket arg) {
        Entity lv;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (arg.type == CombatEventS2CPacket.Type.ENTITY_DIED && (lv = this.world.getEntityById(arg.entityId)) == this.client.player) {
            if (this.client.player.showsDeathScreen()) {
                this.client.openScreen(new DeathScreen(arg.deathMessage, this.world.getLevelProperties().isHardcore()));
            } else {
                this.client.player.requestRespawn();
            }
        }
    }

    @Override
    public void onDifficulty(DifficultyS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.worldProperties.setDifficulty(arg.getDifficulty());
        this.worldProperties.setDifficultyLocked(arg.isDifficultyLocked());
    }

    @Override
    public void onSetCameraEntity(SetCameraEntityS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = arg.getEntity(this.world);
        if (lv != null) {
            this.client.setCameraEntity(lv);
        }
    }

    @Override
    public void onWorldBorder(WorldBorderS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        arg.apply(this.world.getWorldBorder());
    }

    @Override
    public void onTitle(TitleS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        TitleS2CPacket.Action lv = arg.getAction();
        Text lv2 = null;
        Text lv3 = null;
        Text lv4 = arg.getText() != null ? arg.getText() : LiteralText.EMPTY;
        switch (lv) {
            case TITLE: {
                lv2 = lv4;
                break;
            }
            case SUBTITLE: {
                lv3 = lv4;
                break;
            }
            case ACTIONBAR: {
                this.client.inGameHud.setOverlayMessage(lv4, false);
                return;
            }
            case RESET: {
                this.client.inGameHud.setTitles(null, null, -1, -1, -1);
                this.client.inGameHud.setDefaultTitleFade();
                return;
            }
        }
        this.client.inGameHud.setTitles(lv2, lv3, arg.getFadeInTicks(), arg.getStayTicks(), arg.getFadeOutTicks());
    }

    @Override
    public void onPlayerListHeader(PlayerListHeaderS2CPacket arg) {
        this.client.inGameHud.getPlayerListWidget().setHeader(arg.getHeader().getString().isEmpty() ? null : arg.getHeader());
        this.client.inGameHud.getPlayerListWidget().setFooter(arg.getFooter().getString().isEmpty() ? null : arg.getFooter());
    }

    @Override
    public void onRemoveEntityEffect(RemoveEntityStatusEffectS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = arg.getEntity(this.world);
        if (lv instanceof LivingEntity) {
            ((LivingEntity)lv).removeStatusEffectInternal(arg.getEffectType());
        }
    }

    @Override
    public void onPlayerList(PlayerListS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        for (PlayerListS2CPacket.Entry lv : arg.getEntries()) {
            if (arg.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
                this.playerListEntries.remove(lv.getProfile().getId());
                continue;
            }
            PlayerListEntry lv2 = this.playerListEntries.get(lv.getProfile().getId());
            if (arg.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
                lv2 = new PlayerListEntry(lv);
                this.playerListEntries.put(lv2.getProfile().getId(), lv2);
            }
            if (lv2 == null) continue;
            switch (arg.getAction()) {
                case ADD_PLAYER: {
                    lv2.setGameMode(lv.getGameMode());
                    lv2.setLatency(lv.getLatency());
                    lv2.setDisplayName(lv.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    lv2.setGameMode(lv.getGameMode());
                    break;
                }
                case UPDATE_LATENCY: {
                    lv2.setLatency(lv.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    lv2.setDisplayName(lv.getDisplayName());
                }
            }
        }
    }

    @Override
    public void onKeepAlive(KeepAliveS2CPacket arg) {
        this.sendPacket(new KeepAliveC2SPacket(arg.getId()));
    }

    @Override
    public void onPlayerAbilities(PlayerAbilitiesS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ClientPlayerEntity lv = this.client.player;
        lv.abilities.flying = arg.isFlying();
        lv.abilities.creativeMode = arg.isCreativeMode();
        lv.abilities.invulnerable = arg.isInvulnerable();
        lv.abilities.allowFlying = arg.allowFlying();
        lv.abilities.setFlySpeed(arg.getFlySpeed());
        lv.abilities.setWalkSpeed(arg.getWalkSpeed());
    }

    @Override
    public void onPlaySound(PlaySoundS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.world.playSound(this.client.player, arg.getX(), arg.getY(), arg.getZ(), arg.getSound(), arg.getCategory(), arg.getVolume(), arg.getPitch());
    }

    @Override
    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getEntityId());
        if (lv == null) {
            return;
        }
        this.client.world.playSoundFromEntity(this.client.player, lv, arg.getSound(), arg.getCategory(), arg.getVolume(), arg.getPitch());
    }

    @Override
    public void onPlaySoundId(PlaySoundIdS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.getSoundManager().play(new PositionedSoundInstance(arg.getSoundId(), arg.getCategory(), arg.getVolume(), arg.getPitch(), false, 0, SoundInstance.AttenuationType.LINEAR, arg.getX(), arg.getY(), arg.getZ(), false));
    }

    @Override
    public void onResourcePackSend(ResourcePackSendS2CPacket arg) {
        String string = arg.getURL();
        String string2 = arg.getSHA1();
        if (!this.validateResourcePackUrl(string)) {
            return;
        }
        if (string.startsWith("level://")) {
            try {
                String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
                File file = new File(this.client.runDirectory, "saves");
                File file2 = new File(file, string3);
                if (file2.isFile()) {
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
                    CompletableFuture<Void> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2, ResourcePackSource.PACK_SOURCE_WORLD);
                    this.feedbackAfterDownload(completableFuture);
                    return;
                }
            }
            catch (UnsupportedEncodingException string3) {
                // empty catch block
            }
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return;
        }
        ServerInfo lv = this.client.getCurrentServerEntry();
        if (lv != null && lv.getResourcePack() == ServerInfo.ResourcePackState.ENABLED) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
            this.feedbackAfterDownload(this.client.getResourcePackDownloader().download(string, string2));
        } else if (lv == null || lv.getResourcePack() == ServerInfo.ResourcePackState.PROMPT) {
            this.client.execute(() -> this.client.openScreen(new ConfirmScreen(bl -> {
                this.client = MinecraftClient.getInstance();
                ServerInfo lv = this.client.getCurrentServerEntry();
                if (bl) {
                    if (lv != null) {
                        lv.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
                    }
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
                    this.feedbackAfterDownload(this.client.getResourcePackDownloader().download(string, string2));
                } else {
                    if (lv != null) {
                        lv.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
                    }
                    this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
                }
                ServerList.updateServerListEntry(lv);
                this.client.openScreen(null);
            }, new TranslatableText("multiplayer.texturePrompt.line1"), new TranslatableText("multiplayer.texturePrompt.line2"))));
        } else {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
        }
    }

    private boolean validateResourcePackUrl(String string) {
        try {
            URI uRI = new URI(string);
            String string2 = uRI.getScheme();
            boolean bl = "level".equals(string2);
            if (!("http".equals(string2) || "https".equals(string2) || bl)) {
                throw new URISyntaxException(string, "Wrong protocol");
            }
            if (bl && (string.contains("..") || !string.endsWith("/resources.zip"))) {
                throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return false;
        }
        return true;
    }

    private void feedbackAfterDownload(CompletableFuture<?> completableFuture) {
        ((CompletableFuture)completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED))).exceptionally(throwable -> {
            this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
            return null;
        });
    }

    private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status arg) {
        this.connection.send(new ResourcePackStatusC2SPacket(arg));
    }

    @Override
    public void onBossBar(BossBarS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.inGameHud.getBossBarHud().handlePacket(arg);
    }

    @Override
    public void onCooldownUpdate(CooldownUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (arg.getCooldown() == 0) {
            this.client.player.getItemCooldownManager().remove(arg.getItem());
        } else {
            this.client.player.getItemCooldownManager().set(arg.getItem(), arg.getCooldown());
        }
    }

    @Override
    public void onVehicleMove(VehicleMoveS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.client.player.getRootVehicle();
        if (lv != this.client.player && lv.isLogicalSideForUpdatingMovement()) {
            lv.updatePositionAndAngles(arg.getX(), arg.getY(), arg.getZ(), arg.getYaw(), arg.getPitch());
            this.connection.send(new VehicleMoveC2SPacket(lv));
        }
    }

    @Override
    public void onOpenWrittenBook(OpenWrittenBookS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ItemStack lv = this.client.player.getStackInHand(arg.getHand());
        if (lv.getItem() == Items.WRITTEN_BOOK) {
            this.client.openScreen(new BookScreen(new BookScreen.WrittenBookContents(lv)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onCustomPayload(CustomPayloadS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Identifier lv = arg.getChannel();
        PacketByteBuf lv2 = null;
        try {
            lv2 = arg.getData();
            if (CustomPayloadS2CPacket.BRAND.equals(lv)) {
                this.client.player.setServerBrand(lv2.readString(32767));
            } else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(lv)) {
                int i = lv2.readInt();
                float f = lv2.readFloat();
                Path lv3 = Path.fromBuffer(lv2);
                this.client.debugRenderer.pathfindingDebugRenderer.addPath(i, lv3, f);
            } else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(lv)) {
                long l = lv2.readVarLong();
                BlockPos lv4 = lv2.readBlockPos();
                ((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).addNeighborUpdate(l, lv4);
            } else if (CustomPayloadS2CPacket.DEBUG_CAVES.equals(lv)) {
                BlockPos lv5 = lv2.readBlockPos();
                int j = lv2.readInt();
                ArrayList list = Lists.newArrayList();
                ArrayList list2 = Lists.newArrayList();
                for (int k = 0; k < j; ++k) {
                    list.add(lv2.readBlockPos());
                    list2.add(Float.valueOf(lv2.readFloat()));
                }
                this.client.debugRenderer.caveDebugRenderer.method_3704(lv5, list, list2);
            } else if (CustomPayloadS2CPacket.DEBUG_STRUCTURES.equals(lv)) {
                DimensionType lv6 = this.registryTracker.method_30518().get(lv2.readIdentifier());
                BlockBox lv7 = new BlockBox(lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt());
                int m = lv2.readInt();
                ArrayList list3 = Lists.newArrayList();
                ArrayList list4 = Lists.newArrayList();
                for (int n = 0; n < m; ++n) {
                    list3.add(new BlockBox(lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt()));
                    list4.add(lv2.readBoolean());
                }
                this.client.debugRenderer.structureDebugRenderer.method_3871(lv7, list3, list4, lv6);
            } else if (CustomPayloadS2CPacket.DEBUG_WORLDGEN_ATTEMPT.equals(lv)) {
                ((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer).method_3872(lv2.readBlockPos(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat());
            } else if (CustomPayloadS2CPacket.DEBUG_VILLAGE_SECTIONS.equals(lv)) {
                int o = lv2.readInt();
                for (int p = 0; p < o; ++p) {
                    this.client.debugRenderer.villageSectionsDebugRenderer.addSection(lv2.readChunkSectionPos());
                }
                int q = lv2.readInt();
                for (int r = 0; r < q; ++r) {
                    this.client.debugRenderer.villageSectionsDebugRenderer.removeSection(lv2.readChunkSectionPos());
                }
            } else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(lv)) {
                BlockPos lv8 = lv2.readBlockPos();
                String string = lv2.readString();
                int s = lv2.readInt();
                VillageDebugRenderer.PointOfInterest lv9 = new VillageDebugRenderer.PointOfInterest(lv8, string, s);
                this.client.debugRenderer.villageDebugRenderer.addPointOfInterest(lv9);
            } else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(lv)) {
                BlockPos lv10 = lv2.readBlockPos();
                this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(lv10);
            } else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(lv)) {
                BlockPos lv11 = lv2.readBlockPos();
                int t = lv2.readInt();
                this.client.debugRenderer.villageDebugRenderer.setFreeTicketCount(lv11, t);
            } else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(lv)) {
                BlockPos lv12 = lv2.readBlockPos();
                int u = lv2.readInt();
                int v = lv2.readInt();
                ArrayList list5 = Lists.newArrayList();
                for (int w = 0; w < v; ++w) {
                    int x = lv2.readInt();
                    boolean bl = lv2.readBoolean();
                    String string2 = lv2.readString(255);
                    list5.add(new GoalSelectorDebugRenderer.GoalSelector(lv12, x, string2, bl));
                }
                this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(u, list5);
            } else if (CustomPayloadS2CPacket.DEBUG_RAIDS.equals(lv)) {
                int y = lv2.readInt();
                ArrayList collection = Lists.newArrayList();
                for (int z = 0; z < y; ++z) {
                    collection.add(lv2.readBlockPos());
                }
                this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(collection);
            } else if (CustomPayloadS2CPacket.DEBUG_BRAIN.equals(lv)) {
                Path lv15;
                double d = lv2.readDouble();
                double e = lv2.readDouble();
                double g = lv2.readDouble();
                PositionImpl lv13 = new PositionImpl(d, e, g);
                UUID uUID = lv2.readUuid();
                int aa = lv2.readInt();
                String string3 = lv2.readString();
                String string4 = lv2.readString();
                int ab = lv2.readInt();
                float h = lv2.readFloat();
                float ac = lv2.readFloat();
                String string5 = lv2.readString();
                boolean bl2 = lv2.readBoolean();
                if (bl2) {
                    Path lv14 = Path.fromBuffer(lv2);
                } else {
                    lv15 = null;
                }
                boolean bl3 = lv2.readBoolean();
                VillageDebugRenderer.Brain lv16 = new VillageDebugRenderer.Brain(uUID, aa, string3, string4, ab, h, ac, lv13, string5, lv15, bl3);
                int ad = lv2.readInt();
                for (int ae = 0; ae < ad; ++ae) {
                    String string6 = lv2.readString();
                    lv16.field_18927.add(string6);
                }
                int af = lv2.readInt();
                for (int ag = 0; ag < af; ++ag) {
                    String string7 = lv2.readString();
                    lv16.field_18928.add(string7);
                }
                int ah = lv2.readInt();
                for (int ai = 0; ai < ah; ++ai) {
                    String string8 = lv2.readString();
                    lv16.field_19374.add(string8);
                }
                int aj = lv2.readInt();
                for (int ak = 0; ak < aj; ++ak) {
                    BlockPos lv17 = lv2.readBlockPos();
                    lv16.pointsOfInterest.add(lv17);
                }
                int al = lv2.readInt();
                for (int am = 0; am < al; ++am) {
                    BlockPos lv18 = lv2.readBlockPos();
                    lv16.field_25287.add(lv18);
                }
                int an = lv2.readInt();
                for (int ao = 0; ao < an; ++ao) {
                    String string9 = lv2.readString();
                    lv16.field_19375.add(string9);
                }
                this.client.debugRenderer.villageDebugRenderer.addBrain(lv16);
            } else if (CustomPayloadS2CPacket.DEBUG_BEE.equals(lv)) {
                double ap = lv2.readDouble();
                double aq = lv2.readDouble();
                double ar = lv2.readDouble();
                PositionImpl lv19 = new PositionImpl(ap, aq, ar);
                UUID uUID2 = lv2.readUuid();
                int as = lv2.readInt();
                boolean bl4 = lv2.readBoolean();
                BlockPos lv20 = null;
                if (bl4) {
                    lv20 = lv2.readBlockPos();
                }
                boolean bl5 = lv2.readBoolean();
                BlockPos lv21 = null;
                if (bl5) {
                    lv21 = lv2.readBlockPos();
                }
                int at = lv2.readInt();
                boolean bl6 = lv2.readBoolean();
                Path lv22 = null;
                if (bl6) {
                    lv22 = Path.fromBuffer(lv2);
                }
                BeeDebugRenderer.Bee lv23 = new BeeDebugRenderer.Bee(uUID2, as, lv19, lv22, lv20, lv21, at);
                int au = lv2.readInt();
                for (int av = 0; av < au; ++av) {
                    String string10 = lv2.readString();
                    lv23.labels.add(string10);
                }
                int aw = lv2.readInt();
                for (int ax = 0; ax < aw; ++ax) {
                    BlockPos lv24 = lv2.readBlockPos();
                    lv23.blacklist.add(lv24);
                }
                this.client.debugRenderer.beeDebugRenderer.addBee(lv23);
            } else if (CustomPayloadS2CPacket.DEBUG_HIVE.equals(lv)) {
                BlockPos lv25 = lv2.readBlockPos();
                String string11 = lv2.readString();
                int ay = lv2.readInt();
                int az = lv2.readInt();
                boolean bl7 = lv2.readBoolean();
                BeeDebugRenderer.Hive lv26 = new BeeDebugRenderer.Hive(lv25, string11, ay, az, bl7, this.world.getTime());
                this.client.debugRenderer.beeDebugRenderer.addHive(lv26);
            } else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR.equals(lv)) {
                this.client.debugRenderer.gameTestDebugRenderer.clear();
            } else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(lv)) {
                BlockPos lv27 = lv2.readBlockPos();
                int ba = lv2.readInt();
                String string12 = lv2.readString();
                int bb = lv2.readInt();
                this.client.debugRenderer.gameTestDebugRenderer.addMarker(lv27, ba, string12, bb);
            } else {
                LOGGER.warn("Unknown custom packed identifier: {}", (Object)lv);
            }
        }
        finally {
            if (lv2 != null) {
                lv2.release();
            }
        }
    }

    @Override
    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Scoreboard lv = this.world.getScoreboard();
        String string = arg.getName();
        if (arg.getMode() == 0) {
            lv.addObjective(string, ScoreboardCriterion.DUMMY, arg.getDisplayName(), arg.getType());
        } else if (lv.containsObjective(string)) {
            ScoreboardObjective lv2 = lv.getNullableObjective(string);
            if (arg.getMode() == 1) {
                lv.removeObjective(lv2);
            } else if (arg.getMode() == 2) {
                lv2.setRenderType(arg.getType());
                lv2.setDisplayName(arg.getDisplayName());
            }
        }
    }

    @Override
    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Scoreboard lv = this.world.getScoreboard();
        String string = arg.getObjectiveName();
        switch (arg.getUpdateMode()) {
            case CHANGE: {
                ScoreboardObjective lv2 = lv.getObjective(string);
                ScoreboardPlayerScore lv3 = lv.getPlayerScore(arg.getPlayerName(), lv2);
                lv3.setScore(arg.getScore());
                break;
            }
            case REMOVE: {
                lv.resetPlayerScore(arg.getPlayerName(), lv.getNullableObjective(string));
            }
        }
    }

    @Override
    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Scoreboard lv = this.world.getScoreboard();
        String string = arg.getName();
        ScoreboardObjective lv2 = string == null ? null : lv.getObjective(string);
        lv.setObjectiveSlot(arg.getSlot(), lv2);
    }

    @Override
    public void onTeam(TeamS2CPacket arg) {
        Team lv3;
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Scoreboard lv = this.world.getScoreboard();
        if (arg.getMode() == 0) {
            Team lv2 = lv.addTeam(arg.getTeamName());
        } else {
            lv3 = lv.getTeam(arg.getTeamName());
        }
        if (arg.getMode() == 0 || arg.getMode() == 2) {
            AbstractTeam.CollisionRule lv5;
            lv3.setDisplayName(arg.getDisplayName());
            lv3.setColor(arg.getPlayerPrefix());
            lv3.setFriendlyFlagsBitwise(arg.getFlags());
            AbstractTeam.VisibilityRule lv4 = AbstractTeam.VisibilityRule.getRule(arg.getNameTagVisibilityRule());
            if (lv4 != null) {
                lv3.setNameTagVisibilityRule(lv4);
            }
            if ((lv5 = AbstractTeam.CollisionRule.getRule(arg.getCollisionRule())) != null) {
                lv3.setCollisionRule(lv5);
            }
            lv3.setPrefix(arg.getPrefix());
            lv3.setSuffix(arg.getSuffix());
        }
        if (arg.getMode() == 0 || arg.getMode() == 3) {
            for (String string : arg.getPlayerList()) {
                lv.addPlayerToTeam(string, lv3);
            }
        }
        if (arg.getMode() == 4) {
            for (String string2 : arg.getPlayerList()) {
                lv.removePlayerFromTeam(string2, lv3);
            }
        }
        if (arg.getMode() == 1) {
            lv.removeTeam(lv3);
        }
    }

    @Override
    public void onParticle(ParticleS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        if (arg.getCount() == 0) {
            double d = arg.getSpeed() * arg.getOffsetX();
            double e = arg.getSpeed() * arg.getOffsetY();
            double f = arg.getSpeed() * arg.getOffsetZ();
            try {
                this.world.addParticle(arg.getParameters(), arg.isLongDistance(), arg.getX(), arg.getY(), arg.getZ(), d, e, f);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)arg.getParameters());
            }
        } else {
            for (int i = 0; i < arg.getCount(); ++i) {
                double g = this.random.nextGaussian() * (double)arg.getOffsetX();
                double h = this.random.nextGaussian() * (double)arg.getOffsetY();
                double j = this.random.nextGaussian() * (double)arg.getOffsetZ();
                double k = this.random.nextGaussian() * (double)arg.getSpeed();
                double l = this.random.nextGaussian() * (double)arg.getSpeed();
                double m = this.random.nextGaussian() * (double)arg.getSpeed();
                try {
                    this.world.addParticle(arg.getParameters(), arg.isLongDistance(), arg.getX() + g, arg.getY() + h, arg.getZ() + j, k, l, m);
                    continue;
                }
                catch (Throwable throwable2) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)arg.getParameters());
                    return;
                }
            }
        }
    }

    @Override
    public void onEntityAttributes(EntityAttributesS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        Entity lv = this.world.getEntityById(arg.getEntityId());
        if (lv == null) {
            return;
        }
        if (!(lv instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + lv + ")");
        }
        AttributeContainer lv2 = ((LivingEntity)lv).getAttributes();
        for (EntityAttributesS2CPacket.Entry lv3 : arg.getEntries()) {
            EntityAttributeInstance lv4 = lv2.getCustomInstance(lv3.getId());
            if (lv4 == null) {
                LOGGER.warn("Entity {} does not have attribute {}", (Object)lv, (Object)Registry.ATTRIBUTE.getId(lv3.getId()));
                continue;
            }
            lv4.setBaseValue(lv3.getBaseValue());
            lv4.clearModifiers();
            for (EntityAttributeModifier lv5 : lv3.getModifiers()) {
                lv4.addTemporaryModifier(lv5);
            }
        }
    }

    @Override
    public void onCraftFailedResponse(CraftFailedResponseS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ScreenHandler lv = this.client.player.currentScreenHandler;
        if (lv.syncId != arg.getSyncId() || !lv.isNotRestricted(this.client.player)) {
            return;
        }
        this.recipeManager.get(arg.getRecipeId()).ifPresent(arg2 -> {
            if (this.client.currentScreen instanceof RecipeBookProvider) {
                RecipeBookWidget lv = ((RecipeBookProvider)((Object)this.client.currentScreen)).getRecipeBookWidget();
                lv.showGhostRecipe((Recipe<?>)arg2, arg.slots);
            }
        });
    }

    @Override
    public void onLightUpdate(LightUpdateS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        int i = arg.getChunkX();
        int j = arg.getChunkZ();
        LightingProvider lv = this.world.getChunkManager().getLightingProvider();
        int k = arg.getSkyLightMask();
        int l = arg.getFilledSkyLightMask();
        Iterator<byte[]> iterator = arg.getSkyLightUpdates().iterator();
        this.updateLighting(i, j, lv, LightType.SKY, k, l, iterator, arg.method_30006());
        int m = arg.getBlockLightMask();
        int n = arg.getFilledBlockLightMask();
        Iterator<byte[]> iterator2 = arg.getBlockLightUpdates().iterator();
        this.updateLighting(i, j, lv, LightType.BLOCK, m, n, iterator2, arg.method_30006());
    }

    @Override
    public void onSetTradeOffers(SetTradeOffersS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        ScreenHandler lv = this.client.player.currentScreenHandler;
        if (arg.getSyncId() == lv.syncId && lv instanceof MerchantScreenHandler) {
            ((MerchantScreenHandler)lv).setOffers(new TraderOfferList(arg.getOffers().toTag()));
            ((MerchantScreenHandler)lv).setExperienceFromServer(arg.getExperience());
            ((MerchantScreenHandler)lv).setLevelProgress(arg.getLevelProgress());
            ((MerchantScreenHandler)lv).setCanLevel(arg.isLeveled());
            ((MerchantScreenHandler)lv).setRefreshTrades(arg.isRefreshable());
        }
    }

    @Override
    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.chunkLoadDistance = arg.getDistance();
        this.world.getChunkManager().updateLoadDistance(arg.getDistance());
    }

    @Override
    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.world.getChunkManager().setChunkMapCenter(arg.getChunkX(), arg.getChunkZ());
    }

    @Override
    public void onPlayerActionResponse(PlayerActionResponseS2CPacket arg) {
        NetworkThreadUtils.forceMainThread(arg, this, this.client);
        this.client.interactionManager.processPlayerActionResponse(this.world, arg.getBlockPos(), arg.getBlockState(), arg.getAction(), arg.isApproved());
    }

    private void updateLighting(int i, int j, LightingProvider arg, LightType arg2, int k, int l, Iterator<byte[]> iterator, boolean bl) {
        for (int m = 0; m < 18; ++m) {
            boolean bl3;
            int n = -1 + m;
            boolean bl2 = (k & 1 << m) != 0;
            boolean bl4 = bl3 = (l & 1 << m) != 0;
            if (!bl2 && !bl3) continue;
            arg.queueData(arg2, ChunkSectionPos.from(i, n, j), bl2 ? new ChunkNibbleArray((byte[])iterator.next().clone()) : new ChunkNibbleArray(), bl);
            this.world.scheduleBlockRenders(i, n, j);
        }
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }

    public Collection<PlayerListEntry> getPlayerList() {
        return this.playerListEntries.values();
    }

    @Nullable
    public PlayerListEntry getPlayerListEntry(UUID uUID) {
        return this.playerListEntries.get(uUID);
    }

    @Nullable
    public PlayerListEntry getPlayerListEntry(String string) {
        for (PlayerListEntry lv : this.playerListEntries.values()) {
            if (!lv.getProfile().getName().equals(string)) continue;
            return lv;
        }
        return null;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public ClientAdvancementManager getAdvancementHandler() {
        return this.advancementHandler;
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public ClientWorld getWorld() {
        return this.world;
    }

    public TagManager getTagManager() {
        return this.tagManager;
    }

    public DataQueryHandler getDataQueryHandler() {
        return this.dataQueryHandler;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }

    public Set<RegistryKey<World>> getWorldKeys() {
        return this.worldKeys;
    }

    public class_5455 getRegistryTracker() {
        return this.registryTracker;
    }
}

