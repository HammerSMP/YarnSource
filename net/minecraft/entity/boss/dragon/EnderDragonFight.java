/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ContiguousSet
 *  com.google.common.collect.DiscreteDomain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Range
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.class_5464;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonFight {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Predicate<Entity> VALID_ENTITY = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0, 128.0, 0.0, 192.0));
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(new TranslatableText("entity.minecraft.ender_dragon"), BossBar.Color.PINK, BossBar.Style.PROGRESS).setDragonMusic(true).setThickenFog(true);
    private final ServerWorld world;
    private final List<Integer> gateways = Lists.newArrayList();
    private final BlockPattern endPortalPattern;
    private int dragonSeenTimer;
    private int endCrystalsAlive;
    private int crystalCountTimer;
    private int playerUpdateTimer;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    private UUID dragonUuid;
    private boolean doLegacyCheck = true;
    private BlockPos exitPortalLocation;
    private EnderDragonSpawnState dragonSpawnState;
    private int spawnStateTimer;
    private List<EndCrystalEntity> crystals;

    public EnderDragonFight(ServerWorld arg, long l, CompoundTag arg2) {
        this.world = arg;
        if (arg2.contains("DragonKilled", 99)) {
            if (arg2.containsUuid("Dragon")) {
                this.dragonUuid = arg2.getUuid("Dragon");
            }
            this.dragonKilled = arg2.getBoolean("DragonKilled");
            this.previouslyKilled = arg2.getBoolean("PreviouslyKilled");
            if (arg2.getBoolean("IsRespawning")) {
                this.dragonSpawnState = EnderDragonSpawnState.START;
            }
            if (arg2.contains("ExitPortalLocation", 10)) {
                this.exitPortalLocation = NbtHelper.toBlockPos(arg2.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if (arg2.contains("Gateways", 9)) {
            ListTag lv = arg2.getList("Gateways", 3);
            for (int i = 0; i < lv.size(); ++i) {
                this.gateways.add(lv.getInt(i));
            }
        } else {
            this.gateways.addAll((Collection<Integer>)ContiguousSet.create((Range)Range.closedOpen((Comparable)Integer.valueOf(0), (Comparable)Integer.valueOf(20)), (DiscreteDomain)DiscreteDomain.integers()));
            Collections.shuffle(this.gateways, new Random(l));
        }
        this.endPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.BEDROCK))).build();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        if (this.dragonUuid != null) {
            lv.putUuid("Dragon", this.dragonUuid);
        }
        lv.putBoolean("DragonKilled", this.dragonKilled);
        lv.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.exitPortalLocation != null) {
            lv.put("ExitPortalLocation", NbtHelper.fromBlockPos(this.exitPortalLocation));
        }
        ListTag lv2 = new ListTag();
        for (int i : this.gateways) {
            lv2.add(IntTag.of(i));
        }
        lv.put("Gateways", lv2);
        return lv;
    }

    public void tick() {
        this.bossBar.setVisible(!this.dragonKilled);
        if (++this.playerUpdateTimer >= 20) {
            this.updatePlayers();
            this.playerUpdateTimer = 0;
        }
        if (!this.bossBar.getPlayers().isEmpty()) {
            this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            boolean bl = this.loadChunks();
            if (this.doLegacyCheck && bl) {
                this.convertFromLegacy();
                this.doLegacyCheck = false;
            }
            if (this.dragonSpawnState != null) {
                if (this.crystals == null && bl) {
                    this.dragonSpawnState = null;
                    this.respawnDragon();
                }
                this.dragonSpawnState.run(this.world, this, this.crystals, this.spawnStateTimer++, this.exitPortalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUuid == null || ++this.dragonSeenTimer >= 1200) && bl) {
                    this.checkDragonSeen();
                    this.dragonSeenTimer = 0;
                }
                if (++this.crystalCountTimer >= 100 && bl) {
                    this.countAliveCrystals();
                    this.crystalCountTimer = 0;
                }
            }
        } else {
            this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }
    }

    private void convertFromLegacy() {
        LOGGER.info("Scanning for legacy world dragon fight...");
        boolean bl = this.worldContainsEndPortal();
        if (bl) {
            LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.findEndPortal() == null) {
                this.generateEndPortal(false);
            }
        }
        List<EnderDragonEntity> list = this.world.getAliveEnderDragons();
        if (list.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EnderDragonEntity lv = list.get(0);
            this.dragonUuid = lv.getUuid();
            LOGGER.info("Found that there's a dragon still alive ({})", (Object)lv);
            this.dragonKilled = false;
            if (!bl) {
                LOGGER.info("But we didn't have a portal, let's remove it.");
                lv.remove();
                this.dragonUuid = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }

    private void checkDragonSeen() {
        List<EnderDragonEntity> list = this.world.getAliveEnderDragons();
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createDragon();
        } else {
            LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUuid = list.get(0).getUuid();
        }
    }

    protected void setSpawnState(EnderDragonSpawnState arg) {
        if (this.dragonSpawnState == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.spawnStateTimer = 0;
        if (arg == EnderDragonSpawnState.END) {
            this.dragonSpawnState = null;
            this.dragonKilled = false;
            EnderDragonEntity lv = this.createDragon();
            for (ServerPlayerEntity lv2 : this.bossBar.getPlayers()) {
                Criteria.SUMMONED_ENTITY.trigger(lv2, lv);
            }
        } else {
            this.dragonSpawnState = arg;
        }
    }

    private boolean worldContainsEndPortal() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                WorldChunk lv = this.world.getChunk(i, j);
                for (BlockEntity lv2 : lv.getBlockEntities().values()) {
                    if (!(lv2 instanceof EndPortalBlockEntity)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlockPattern.Result findEndPortal() {
        int k;
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                WorldChunk lv = this.world.getChunk(i, j);
                for (BlockEntity lv2 : lv.getBlockEntities().values()) {
                    BlockPattern.Result lv3;
                    if (!(lv2 instanceof EndPortalBlockEntity) || (lv3 = this.endPortalPattern.searchAround(this.world, lv2.getPos())) == null) continue;
                    BlockPos lv4 = lv3.translate(3, 3, 3).getBlockPos();
                    if (this.exitPortalLocation == null && lv4.getX() == 0 && lv4.getZ() == 0) {
                        this.exitPortalLocation = lv4;
                    }
                    return lv3;
                }
            }
        }
        for (int l = k = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN).getY(); l >= 0; --l) {
            BlockPattern.Result lv5 = this.endPortalPattern.searchAround(this.world, new BlockPos(EndPortalFeature.ORIGIN.getX(), l, EndPortalFeature.ORIGIN.getZ()));
            if (lv5 == null) continue;
            if (this.exitPortalLocation == null) {
                this.exitPortalLocation = lv5.translate(3, 3, 3).getBlockPos();
            }
            return lv5;
        }
        return null;
    }

    private boolean loadChunks() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                Chunk lv = this.world.getChunk(i, j, ChunkStatus.FULL, false);
                if (!(lv instanceof WorldChunk)) {
                    return false;
                }
                ChunkHolder.LevelType lv2 = ((WorldChunk)lv).getLevelType();
                if (lv2.isAfter(ChunkHolder.LevelType.TICKING)) continue;
                return false;
            }
        }
        return true;
    }

    private void updatePlayers() {
        HashSet set = Sets.newHashSet();
        for (ServerPlayerEntity lv : this.world.getPlayers(VALID_ENTITY)) {
            this.bossBar.addPlayer(lv);
            set.add(lv);
        }
        HashSet set2 = Sets.newHashSet(this.bossBar.getPlayers());
        set2.removeAll(set);
        for (ServerPlayerEntity lv2 : set2) {
            this.bossBar.removePlayer(lv2);
        }
    }

    private void countAliveCrystals() {
        this.crystalCountTimer = 0;
        this.endCrystalsAlive = 0;
        for (EndSpikeFeature.Spike lv : EndSpikeFeature.getSpikes(this.world)) {
            this.endCrystalsAlive += this.world.getNonSpectatingEntities(EndCrystalEntity.class, lv.getBoundingBox()).size();
        }
        LOGGER.debug("Found {} end crystals still alive", (Object)this.endCrystalsAlive);
    }

    public void dragonKilled(EnderDragonEntity arg) {
        if (arg.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(0.0f);
            this.bossBar.setVisible(false);
            this.generateEndPortal(true);
            this.generateNewEndGateway();
            if (!this.previouslyKilled) {
                this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN), Blocks.DRAGON_EGG.getDefaultState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }

    private void generateNewEndGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        int i = this.gateways.remove(this.gateways.size() - 1);
        int j = MathHelper.floor(96.0 * Math.cos(2.0 * (-Math.PI + 0.15707963267948966 * (double)i)));
        int k = MathHelper.floor(96.0 * Math.sin(2.0 * (-Math.PI + 0.15707963267948966 * (double)i)));
        this.generateEndGateway(new BlockPos(j, 75, k));
    }

    private void generateEndGateway(BlockPos arg) {
        this.world.syncWorldEvent(3000, arg, 0);
        class_5464.END_GATEWAY_DELAYED.generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), arg);
    }

    private void generateEndPortal(boolean bl) {
        EndPortalFeature lv = new EndPortalFeature(bl);
        if (this.exitPortalLocation == null) {
            this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN).down();
            while (this.world.getBlockState(this.exitPortalLocation).isOf(Blocks.BEDROCK) && this.exitPortalLocation.getY() > this.world.getSeaLevel()) {
                this.exitPortalLocation = this.exitPortalLocation.down();
            }
        }
        lv.configure(FeatureConfig.DEFAULT).generate(this.world, this.world.getChunkManager().getChunkGenerator(), new Random(), this.exitPortalLocation);
    }

    private EnderDragonEntity createDragon() {
        this.world.getWorldChunk(new BlockPos(0, 128, 0));
        EnderDragonEntity lv = EntityType.ENDER_DRAGON.create(this.world);
        lv.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        lv.refreshPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0f, 0.0f);
        this.world.spawnEntity(lv);
        this.dragonUuid = lv.getUuid();
        return lv;
    }

    public void updateFight(EnderDragonEntity arg) {
        if (arg.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(arg.getHealth() / arg.getMaxHealth());
            this.dragonSeenTimer = 0;
            if (arg.hasCustomName()) {
                this.bossBar.setName(arg.getDisplayName());
            }
        }
    }

    public int getAliveEndCrystals() {
        return this.endCrystalsAlive;
    }

    public void crystalDestroyed(EndCrystalEntity arg, DamageSource arg2) {
        if (this.dragonSpawnState != null && this.crystals.contains(arg)) {
            LOGGER.debug("Aborting respawn sequence");
            this.dragonSpawnState = null;
            this.spawnStateTimer = 0;
            this.resetEndCrystals();
            this.generateEndPortal(true);
        } else {
            this.countAliveCrystals();
            Entity lv = this.world.getEntity(this.dragonUuid);
            if (lv instanceof EnderDragonEntity) {
                ((EnderDragonEntity)lv).crystalDestroyed(arg, arg.getBlockPos(), arg2);
            }
        }
    }

    public boolean hasPreviouslyKilled() {
        return this.previouslyKilled;
    }

    public void respawnDragon() {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            BlockPos lv = this.exitPortalLocation;
            if (lv == null) {
                LOGGER.debug("Tried to respawn, but need to find the portal first.");
                BlockPattern.Result lv2 = this.findEndPortal();
                if (lv2 == null) {
                    LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.generateEndPortal(true);
                } else {
                    LOGGER.debug("Found the exit portal & temporarily using it.");
                }
                lv = this.exitPortalLocation;
            }
            ArrayList list = Lists.newArrayList();
            BlockPos lv3 = lv.up(1);
            for (Direction lv4 : Direction.Type.HORIZONTAL) {
                List<EndCrystalEntity> list2 = this.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(lv3.offset(lv4, 2)));
                if (list2.isEmpty()) {
                    return;
                }
                list.addAll(list2);
            }
            LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon(list);
        }
    }

    private void respawnDragon(List<EndCrystalEntity> list) {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            BlockPattern.Result lv = this.findEndPortal();
            while (lv != null) {
                for (int i = 0; i < this.endPortalPattern.getWidth(); ++i) {
                    for (int j = 0; j < this.endPortalPattern.getHeight(); ++j) {
                        for (int k = 0; k < this.endPortalPattern.getDepth(); ++k) {
                            CachedBlockPosition lv2 = lv.translate(i, j, k);
                            if (!lv2.getBlockState().isOf(Blocks.BEDROCK) && !lv2.getBlockState().isOf(Blocks.END_PORTAL)) continue;
                            this.world.setBlockState(lv2.getBlockPos(), Blocks.END_STONE.getDefaultState());
                        }
                    }
                }
                lv = this.findEndPortal();
            }
            this.dragonSpawnState = EnderDragonSpawnState.START;
            this.spawnStateTimer = 0;
            this.generateEndPortal(false);
            this.crystals = list;
        }
    }

    public void resetEndCrystals() {
        for (EndSpikeFeature.Spike lv : EndSpikeFeature.getSpikes(this.world)) {
            List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, lv.getBoundingBox());
            for (EndCrystalEntity lv2 : list) {
                lv2.setInvulnerable(false);
                lv2.setBeamTarget(null);
            }
        }
    }
}

