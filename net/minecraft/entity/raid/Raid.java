/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;

public class Raid {
    private static final TranslatableText EVENT_TEXT = new TranslatableText("event.minecraft.raid");
    private static final TranslatableText VICTORY_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.victory");
    private static final TranslatableText DEFEAT_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.defeat");
    private static final Text VICTORY_TITLE = EVENT_TEXT.shallowCopy().append(" - ").append(VICTORY_SUFFIX_TEXT);
    private static final Text DEFEAT_TITLE = EVENT_TEXT.shallowCopy().append(" - ").append(DEFEAT_SUFFIX_TEXT);
    private final Map<Integer, RaiderEntity> waveToCaptain = Maps.newHashMap();
    private final Map<Integer, Set<RaiderEntity>> waveToRaiders = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerWorld world;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int wavesSpawned;
    private final ServerBossBar bar = new ServerBossBar(EVENT_TEXT, BossBar.Color.RED, BossBar.Style.NOTCHED_10);
    private int postRaidTicks;
    private int preRaidTicks;
    private final Random random = new Random();
    private final int waveCount;
    private Status status;
    private int finishCooldown;
    private Optional<BlockPos> preCalculatedRavagerSpawnLocation = Optional.empty();

    public Raid(int i, ServerWorld arg, BlockPos arg2) {
        this.id = i;
        this.world = arg;
        this.active = true;
        this.preRaidTicks = 300;
        this.bar.setPercent(0.0f);
        this.center = arg2;
        this.waveCount = this.getMaxWaves(arg.getDifficulty());
        this.status = Status.ONGOING;
    }

    public Raid(ServerWorld arg, CompoundTag arg2) {
        this.world = arg;
        this.id = arg2.getInt("Id");
        this.started = arg2.getBoolean("Started");
        this.active = arg2.getBoolean("Active");
        this.ticksActive = arg2.getLong("TicksActive");
        this.badOmenLevel = arg2.getInt("BadOmenLevel");
        this.wavesSpawned = arg2.getInt("GroupsSpawned");
        this.preRaidTicks = arg2.getInt("PreRaidTicks");
        this.postRaidTicks = arg2.getInt("PostRaidTicks");
        this.totalHealth = arg2.getFloat("TotalHealth");
        this.center = new BlockPos(arg2.getInt("CX"), arg2.getInt("CY"), arg2.getInt("CZ"));
        this.waveCount = arg2.getInt("NumGroups");
        this.status = Status.fromName(arg2.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (arg2.contains("HeroesOfTheVillage", 9)) {
            ListTag lv = arg2.getList("HeroesOfTheVillage", 11);
            for (int i = 0; i < lv.size(); ++i) {
                this.heroesOfTheVillage.add(NbtHelper.toUuidNew(lv.get(i)));
            }
        }
    }

    public boolean isFinished() {
        return this.hasWon() || this.hasLost();
    }

    public boolean isPreRaid() {
        return this.hasSpawned() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
    }

    public boolean hasSpawned() {
        return this.wavesSpawned > 0;
    }

    public boolean hasStopped() {
        return this.status == Status.STOPPED;
    }

    public boolean hasWon() {
        return this.status == Status.VICTORY;
    }

    public boolean hasLost() {
        return this.status == Status.LOSS;
    }

    public World getWorld() {
        return this.world;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.wavesSpawned;
    }

    private Predicate<ServerPlayerEntity> isInRaidDistance() {
        return arg -> {
            BlockPos lv = arg.getBlockPos();
            return arg.isAlive() && this.world.getRaidAt(lv) == this;
        };
    }

    private void updateBarToPlayers() {
        HashSet set = Sets.newHashSet(this.bar.getPlayers());
        List<ServerPlayerEntity> list = this.world.getPlayers(this.isInRaidDistance());
        for (ServerPlayerEntity lv : list) {
            if (set.contains(lv)) continue;
            this.bar.addPlayer(lv);
        }
        for (ServerPlayerEntity lv2 : set) {
            if (list.contains(lv2)) continue;
            this.bar.removePlayer(lv2);
        }
    }

    public int getMaxAcceptableBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

    public void start(PlayerEntity arg) {
        if (arg.hasStatusEffect(StatusEffects.BAD_OMEN)) {
            this.badOmenLevel += arg.getStatusEffect(StatusEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxAcceptableBadOmenLevel());
        }
        arg.removeStatusEffect(StatusEffects.BAD_OMEN);
    }

    public void invalidate() {
        this.active = false;
        this.bar.clearPlayers();
        this.status = Status.STOPPED;
    }

    public void tick() {
        if (this.hasStopped()) {
            return;
        }
        if (this.status == Status.ONGOING) {
            boolean bl = this.active;
            this.active = this.world.isChunkLoaded(this.center);
            if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                this.invalidate();
                return;
            }
            if (bl != this.active) {
                this.bar.setVisible(this.active);
            }
            if (!this.active) {
                return;
            }
            if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
                this.method_20511();
            }
            if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
                if (this.wavesSpawned > 0) {
                    this.status = Status.LOSS;
                } else {
                    this.invalidate();
                }
            }
            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
                this.invalidate();
                return;
            }
            int i = this.getRaiderCount();
            if (i == 0 && this.shouldSpawnMoreGroups()) {
                if (this.preRaidTicks > 0) {
                    boolean bl3;
                    boolean bl2 = this.preCalculatedRavagerSpawnLocation.isPresent();
                    boolean bl4 = bl3 = !bl2 && this.preRaidTicks % 5 == 0;
                    if (bl2 && !this.world.getChunkManager().shouldTickChunk(new ChunkPos(this.preCalculatedRavagerSpawnLocation.get()))) {
                        bl3 = true;
                    }
                    if (bl3) {
                        int j = 0;
                        if (this.preRaidTicks < 100) {
                            j = 1;
                        } else if (this.preRaidTicks < 40) {
                            j = 2;
                        }
                        this.preCalculatedRavagerSpawnLocation = this.preCalculateRavagerSpawnLocation(j);
                    }
                    if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
                        this.updateBarToPlayers();
                    }
                    --this.preRaidTicks;
                    this.bar.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0f, 0.0f, 1.0f));
                } else if (this.preRaidTicks == 0 && this.wavesSpawned > 0) {
                    this.preRaidTicks = 300;
                    this.bar.setName(EVENT_TEXT);
                    return;
                }
            }
            if (this.ticksActive % 20L == 0L) {
                this.updateBarToPlayers();
                this.removeObsoleteRaiders();
                if (i > 0) {
                    if (i <= 2) {
                        this.bar.setName(EVENT_TEXT.shallowCopy().append(" - ").append(new TranslatableText("event.minecraft.raid.raiders_remaining", i)));
                    } else {
                        this.bar.setName(EVENT_TEXT);
                    }
                } else {
                    this.bar.setName(EVENT_TEXT);
                }
            }
            boolean bl4 = false;
            int k = 0;
            while (this.canSpawnRaiders()) {
                BlockPos lv;
                BlockPos blockPos = lv = this.preCalculatedRavagerSpawnLocation.isPresent() ? this.preCalculatedRavagerSpawnLocation.get() : this.getRavagerSpawnLocation(k, 20);
                if (lv != null) {
                    this.started = true;
                    this.spawnNextWave(lv);
                    if (!bl4) {
                        this.playRaidHorn(lv);
                        bl4 = true;
                    }
                } else {
                    ++k;
                }
                if (k <= 3) continue;
                this.invalidate();
                break;
            }
            if (this.hasStarted() && !this.shouldSpawnMoreGroups() && i == 0) {
                if (this.postRaidTicks < 40) {
                    ++this.postRaidTicks;
                } else {
                    this.status = Status.VICTORY;
                    for (UUID uUID : this.heroesOfTheVillage) {
                        Entity lv2 = this.world.getEntity(uUID);
                        if (!(lv2 instanceof LivingEntity) || lv2.isSpectator()) continue;
                        LivingEntity lv3 = (LivingEntity)lv2;
                        lv3.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (!(lv3 instanceof ServerPlayerEntity)) continue;
                        ServerPlayerEntity lv4 = (ServerPlayerEntity)lv3;
                        lv4.incrementStat(Stats.RAID_WIN);
                        Criteria.HERO_OF_THE_VILLAGE.trigger(lv4);
                    }
                }
            }
            this.markDirty();
        } else if (this.isFinished()) {
            ++this.finishCooldown;
            if (this.finishCooldown >= 600) {
                this.invalidate();
                return;
            }
            if (this.finishCooldown % 20 == 0) {
                this.updateBarToPlayers();
                this.bar.setVisible(true);
                if (this.hasWon()) {
                    this.bar.setPercent(0.0f);
                    this.bar.setName(VICTORY_TITLE);
                } else {
                    this.bar.setName(DEFEAT_TITLE);
                }
            }
        }
    }

    private void method_20511() {
        Stream<ChunkSectionPos> stream = ChunkSectionPos.stream(ChunkSectionPos.from(this.center), 2);
        stream.filter(this.world::isNearOccupiedPointOfInterest).map(ChunkSectionPos::getCenterPos).min(Comparator.comparingDouble(arg -> arg.getSquaredDistance(this.center))).ifPresent(this::setCenter);
    }

    private Optional<BlockPos> preCalculateRavagerSpawnLocation(int i) {
        for (int j = 0; j < 3; ++j) {
            BlockPos lv = this.getRavagerSpawnLocation(i, 1);
            if (lv == null) continue;
            return Optional.of(lv);
        }
        return Optional.empty();
    }

    private boolean shouldSpawnMoreGroups() {
        if (this.hasExtraWave()) {
            return !this.hasSpawnedExtraWave();
        }
        return !this.hasSpawnedFinalWave();
    }

    private boolean hasSpawnedFinalWave() {
        return this.getGroupsSpawned() == this.waveCount;
    }

    private boolean hasExtraWave() {
        return this.badOmenLevel > 1;
    }

    private boolean hasSpawnedExtraWave() {
        return this.getGroupsSpawned() > this.waveCount;
    }

    private boolean isSpawningExtraWave() {
        return this.hasSpawnedFinalWave() && this.getRaiderCount() == 0 && this.hasExtraWave();
    }

    private void removeObsoleteRaiders() {
        Iterator<Set<RaiderEntity>> iterator = this.waveToRaiders.values().iterator();
        HashSet set = Sets.newHashSet();
        while (iterator.hasNext()) {
            Set<RaiderEntity> set2 = iterator.next();
            for (RaiderEntity lv : set2) {
                BlockPos lv2 = lv.getBlockPos();
                if (lv.removed || lv.dimension != this.world.method_27983() || this.center.getSquaredDistance(lv2) >= 12544.0) {
                    set.add(lv);
                    continue;
                }
                if (lv.age <= 600) continue;
                if (this.world.getEntity(lv.getUuid()) == null) {
                    set.add(lv);
                }
                if (!this.world.isNearOccupiedPointOfInterest(lv2) && lv.getDespawnCounter() > 2400) {
                    lv.setOutOfRaidCounter(lv.getOutOfRaidCounter() + 1);
                }
                if (lv.getOutOfRaidCounter() < 30) continue;
                set.add(lv);
            }
        }
        for (RaiderEntity lv3 : set) {
            this.removeFromWave(lv3, true);
        }
    }

    private void playRaidHorn(BlockPos arg) {
        float f = 13.0f;
        int i = 64;
        Collection<ServerPlayerEntity> collection = this.bar.getPlayers();
        for (ServerPlayerEntity lv : this.world.getPlayers()) {
            Vec3d lv2 = lv.getPos();
            Vec3d lv3 = Vec3d.ofCenter(arg);
            float g = MathHelper.sqrt((lv3.x - lv2.x) * (lv3.x - lv2.x) + (lv3.z - lv2.z) * (lv3.z - lv2.z));
            double d = lv2.x + (double)(13.0f / g) * (lv3.x - lv2.x);
            double e = lv2.z + (double)(13.0f / g) * (lv3.z - lv2.z);
            if (!(g <= 64.0f) && !collection.contains(lv)) continue;
            lv.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, d, lv.getY(), e, 64.0f, 1.0f));
        }
    }

    private void spawnNextWave(BlockPos arg) {
        boolean bl = false;
        int i = this.wavesSpawned + 1;
        this.totalHealth = 0.0f;
        LocalDifficulty lv = this.world.getLocalDifficulty(arg);
        boolean bl2 = this.isSpawningExtraWave();
        for (Member lv2 : Member.VALUES) {
            int j = this.getCount(lv2, i, bl2) + this.getBonusCount(lv2, this.random, i, lv, bl2);
            int k = 0;
            for (int l = 0; l < j; ++l) {
                RaiderEntity lv3 = (RaiderEntity)lv2.type.create(this.world);
                if (!bl && lv3.canLead()) {
                    lv3.setPatrolLeader(true);
                    this.setWaveCaptain(i, lv3);
                    bl = true;
                }
                this.addRaider(i, lv3, arg, false);
                if (lv2.type != EntityType.RAVAGER) continue;
                RaiderEntity lv4 = null;
                if (i == this.getMaxWaves(Difficulty.NORMAL)) {
                    lv4 = EntityType.PILLAGER.create(this.world);
                } else if (i >= this.getMaxWaves(Difficulty.HARD)) {
                    lv4 = k == 0 ? (RaiderEntity)EntityType.EVOKER.create(this.world) : (RaiderEntity)EntityType.VINDICATOR.create(this.world);
                }
                ++k;
                if (lv4 == null) continue;
                this.addRaider(i, lv4, arg, false);
                lv4.refreshPositionAndAngles(arg, 0.0f, 0.0f);
                lv4.startRiding(lv3);
            }
        }
        this.preCalculatedRavagerSpawnLocation = Optional.empty();
        ++this.wavesSpawned;
        this.updateBar();
        this.markDirty();
    }

    public void addRaider(int i, RaiderEntity arg, @Nullable BlockPos arg2, boolean bl) {
        boolean bl2 = this.addToWave(i, arg);
        if (bl2) {
            arg.setRaid(this);
            arg.setWave(i);
            arg.setAbleToJoinRaid(true);
            arg.setOutOfRaidCounter(0);
            if (!bl && arg2 != null) {
                arg.updatePosition((double)arg2.getX() + 0.5, (double)arg2.getY() + 1.0, (double)arg2.getZ() + 0.5);
                arg.initialize(this.world, this.world.getLocalDifficulty(arg2), SpawnReason.EVENT, null, null);
                arg.addBonusForWave(i, false);
                arg.setOnGround(true);
                this.world.spawnEntity(arg);
            }
        }
    }

    public void updateBar() {
        this.bar.setPercent(MathHelper.clamp(this.getCurrentRaiderHealth() / this.totalHealth, 0.0f, 1.0f));
    }

    public float getCurrentRaiderHealth() {
        float f = 0.0f;
        for (Set<RaiderEntity> set : this.waveToRaiders.values()) {
            for (RaiderEntity lv : set) {
                f += lv.getHealth();
            }
        }
        return f;
    }

    private boolean canSpawnRaiders() {
        return this.preRaidTicks == 0 && (this.wavesSpawned < this.waveCount || this.isSpawningExtraWave()) && this.getRaiderCount() == 0;
    }

    public int getRaiderCount() {
        return this.waveToRaiders.values().stream().mapToInt(Set::size).sum();
    }

    public void removeFromWave(RaiderEntity arg, boolean bl) {
        boolean bl2;
        Set<RaiderEntity> set = this.waveToRaiders.get(arg.getWave());
        if (set != null && (bl2 = set.remove(arg))) {
            if (bl) {
                this.totalHealth -= arg.getHealth();
            }
            arg.setRaid(null);
            this.updateBar();
            this.markDirty();
        }
    }

    private void markDirty() {
        this.world.getRaidManager().markDirty();
    }

    public static ItemStack getOminousBanner() {
        ItemStack lv = new ItemStack(Items.WHITE_BANNER);
        CompoundTag lv2 = lv.getOrCreateSubTag("BlockEntityTag");
        ListTag lv3 = new BannerPattern.Patterns().add(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).add(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).add(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).add(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).add(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).add(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).add(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).add(BannerPattern.BORDER, DyeColor.BLACK).toTag();
        lv2.put("Patterns", lv3);
        lv.getOrCreateTag().putInt("HideFlags", 32);
        lv.setCustomName(new TranslatableText("block.minecraft.ominous_banner").formatted(Formatting.GOLD));
        return lv;
    }

    @Nullable
    public RaiderEntity getCaptain(int i) {
        return this.waveToCaptain.get(i);
    }

    @Nullable
    private BlockPos getRavagerSpawnLocation(int i, int j) {
        int k = i == 0 ? 2 : 2 - i;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = 0; l < j; ++l) {
            float f = this.world.random.nextFloat() * ((float)Math.PI * 2);
            int m = this.center.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0f * (float)k) + this.world.random.nextInt(5);
            int n = this.center.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0f * (float)k) + this.world.random.nextInt(5);
            int o = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, m, n);
            lv.set(m, o, n);
            if (this.world.isNearOccupiedPointOfInterest(lv) && i < 2 || !this.world.isRegionLoaded(lv.getX() - 10, lv.getY() - 10, lv.getZ() - 10, lv.getX() + 10, lv.getY() + 10, lv.getZ() + 10) || !this.world.getChunkManager().shouldTickChunk(new ChunkPos(lv)) || !SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, lv, EntityType.RAVAGER) && (!this.world.getBlockState((BlockPos)lv.down()).isOf(Blocks.SNOW) || !this.world.getBlockState(lv).isAir())) continue;
            return lv;
        }
        return null;
    }

    private boolean addToWave(int i, RaiderEntity arg) {
        return this.addToWave(i, arg, true);
    }

    public boolean addToWave(int i, RaiderEntity arg, boolean bl) {
        this.waveToRaiders.computeIfAbsent(i, integer -> Sets.newHashSet());
        Set<RaiderEntity> set = this.waveToRaiders.get(i);
        RaiderEntity lv = null;
        for (RaiderEntity lv2 : set) {
            if (!lv2.getUuid().equals(arg.getUuid())) continue;
            lv = lv2;
            break;
        }
        if (lv != null) {
            set.remove(lv);
            set.add(arg);
        }
        set.add(arg);
        if (bl) {
            this.totalHealth += arg.getHealth();
        }
        this.updateBar();
        this.markDirty();
        return true;
    }

    public void setWaveCaptain(int i, RaiderEntity arg) {
        this.waveToCaptain.put(i, arg);
        arg.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
        arg.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0f);
    }

    public void removeLeader(int i) {
        this.waveToCaptain.remove(i);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    private void setCenter(BlockPos arg) {
        this.center = arg;
    }

    public int getRaidId() {
        return this.id;
    }

    private int getCount(Member arg, int i, boolean bl) {
        return bl ? arg.countInWave[this.waveCount] : arg.countInWave[i];
    }

    /*
     * WARNING - void declaration
     */
    private int getBonusCount(Member arg, Random random, int i, LocalDifficulty arg2, boolean bl) {
        void o;
        Difficulty lv = arg2.getGlobalDifficulty();
        boolean bl2 = lv == Difficulty.EASY;
        boolean bl3 = lv == Difficulty.NORMAL;
        switch (arg) {
            case WITCH: {
                if (!bl2 && i > 2 && i != 4) {
                    boolean j = true;
                    break;
                }
                return 0;
            }
            case PILLAGER: 
            case VINDICATOR: {
                if (bl2) {
                    int k = random.nextInt(2);
                    break;
                }
                if (bl3) {
                    boolean l = true;
                    break;
                }
                int m = 2;
                break;
            }
            case RAVAGER: {
                boolean n = !bl2 && bl;
                break;
            }
            default: {
                return 0;
            }
        }
        return o > 0 ? random.nextInt((int)(o + true)) : 0;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundTag toTag(CompoundTag arg) {
        arg.putInt("Id", this.id);
        arg.putBoolean("Started", this.started);
        arg.putBoolean("Active", this.active);
        arg.putLong("TicksActive", this.ticksActive);
        arg.putInt("BadOmenLevel", this.badOmenLevel);
        arg.putInt("GroupsSpawned", this.wavesSpawned);
        arg.putInt("PreRaidTicks", this.preRaidTicks);
        arg.putInt("PostRaidTicks", this.postRaidTicks);
        arg.putFloat("TotalHealth", this.totalHealth);
        arg.putInt("NumGroups", this.waveCount);
        arg.putString("Status", this.status.getName());
        arg.putInt("CX", this.center.getX());
        arg.putInt("CY", this.center.getY());
        arg.putInt("CZ", this.center.getZ());
        ListTag lv = new ListTag();
        for (UUID uUID : this.heroesOfTheVillage) {
            lv.add(NbtHelper.fromUuidNew(uUID));
        }
        arg.put("HeroesOfTheVillage", lv);
        return arg;
    }

    public int getMaxWaves(Difficulty arg) {
        switch (arg) {
            case EASY: {
                return 3;
            }
            case NORMAL: {
                return 5;
            }
            case HARD: {
                return 7;
            }
        }
        return 0;
    }

    public float getEnchantmentChance() {
        int i = this.getBadOmenLevel();
        if (i == 2) {
            return 0.1f;
        }
        if (i == 3) {
            return 0.25f;
        }
        if (i == 4) {
            return 0.5f;
        }
        if (i == 5) {
            return 0.75f;
        }
        return 0.0f;
    }

    public void addHero(Entity arg) {
        this.heroesOfTheVillage.add(arg.getUuid());
    }

    static enum Member {
        VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
        EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
        PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
        WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
        RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        private static final Member[] VALUES;
        private final EntityType<? extends RaiderEntity> type;
        private final int[] countInWave;

        private Member(EntityType<? extends RaiderEntity> arg, int[] is) {
            this.type = arg;
            this.countInWave = is;
        }

        static {
            VALUES = Member.values();
        }
    }

    static enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final Status[] VALUES;

        private static Status fromName(String string) {
            for (Status lv : VALUES) {
                if (!string.equalsIgnoreCase(lv.name())) continue;
                return lv;
            }
            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            VALUES = Status.values();
        }
    }
}

