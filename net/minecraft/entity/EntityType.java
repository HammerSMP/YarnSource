/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
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
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = EntityType.register("area_effect_cloud", Builder.create(AreaEffectCloudEntity::new, SpawnGroup.MISC).makeFireImmune().setDimensions(6.0f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<ArmorStandEntity> ARMOR_STAND = EntityType.register("armor_stand", Builder.create(ArmorStandEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 1.975f).maxTrackingRange(10));
    public static final EntityType<ArrowEntity> ARROW = EntityType.register("arrow", Builder.create(ArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<BatEntity> BAT = EntityType.register("bat", Builder.create(BatEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5f, 0.9f).maxTrackingRange(5));
    public static final EntityType<BeeEntity> BEE = EntityType.register("bee", Builder.create(BeeEntity::new, SpawnGroup.CREATURE).setDimensions(0.7f, 0.6f).maxTrackingRange(8));
    public static final EntityType<BlazeEntity> BLAZE = EntityType.register("blaze", Builder.create(BlazeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6f, 1.8f).maxTrackingRange(8));
    public static final EntityType<BoatEntity> BOAT = EntityType.register("boat", Builder.create(BoatEntity::new, SpawnGroup.MISC).setDimensions(1.375f, 0.5625f).maxTrackingRange(10));
    public static final EntityType<CatEntity> CAT = EntityType.register("cat", Builder.create(CatEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(8));
    public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = EntityType.register("cave_spider", Builder.create(CaveSpiderEntity::new, SpawnGroup.MONSTER).setDimensions(0.7f, 0.5f).maxTrackingRange(8));
    public static final EntityType<ChickenEntity> CHICKEN = EntityType.register("chicken", Builder.create(ChickenEntity::new, SpawnGroup.CREATURE).setDimensions(0.4f, 0.7f).maxTrackingRange(10));
    public static final EntityType<CodEntity> COD = EntityType.register("cod", Builder.create(CodEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.3f).maxTrackingRange(4));
    public static final EntityType<CowEntity> COW = EntityType.register("cow", Builder.create(CowEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.4f).maxTrackingRange(10));
    public static final EntityType<CreeperEntity> CREEPER = EntityType.register("creeper", Builder.create(CreeperEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.7f).maxTrackingRange(8));
    public static final EntityType<DolphinEntity> DOLPHIN = EntityType.register("dolphin", Builder.create(DolphinEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.9f, 0.6f));
    public static final EntityType<DonkeyEntity> DONKEY = EntityType.register("donkey", Builder.create(DonkeyEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.5f).maxTrackingRange(10));
    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = EntityType.register("dragon_fireball", Builder.create(DragonFireballEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<DrownedEntity> DROWNED = EntityType.register("drowned", Builder.create(DrownedEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = EntityType.register("elder_guardian", Builder.create(ElderGuardianEntity::new, SpawnGroup.MONSTER).setDimensions(1.9975f, 1.9975f).maxTrackingRange(10));
    public static final EntityType<EndCrystalEntity> END_CRYSTAL = EntityType.register("end_crystal", Builder.create(EndCrystalEntity::new, SpawnGroup.MISC).setDimensions(2.0f, 2.0f).maxTrackingRange(16).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<EnderDragonEntity> ENDER_DRAGON = EntityType.register("ender_dragon", Builder.create(EnderDragonEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(16.0f, 8.0f).maxTrackingRange(10));
    public static final EntityType<EndermanEntity> ENDERMAN = EntityType.register("enderman", Builder.create(EndermanEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 2.9f).maxTrackingRange(8));
    public static final EntityType<EndermiteEntity> ENDERMITE = EntityType.register("endermite", Builder.create(EndermiteEntity::new, SpawnGroup.MONSTER).setDimensions(0.4f, 0.3f).maxTrackingRange(8));
    public static final EntityType<EvokerEntity> EVOKER = EntityType.register("evoker", Builder.create(EvokerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = EntityType.register("evoker_fangs", Builder.create(EvokerFangsEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.8f).maxTrackingRange(6).trackingTickInterval(2));
    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = EntityType.register("experience_orb", Builder.create(ExperienceOrbEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(6).trackingTickInterval(20));
    public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = EntityType.register("eye_of_ender", Builder.create(EyeOfEnderEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(4));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = EntityType.register("falling_block", Builder.create(FallingBlockEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(20));
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = EntityType.register("firework_rocket", Builder.create(FireworkRocketEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<FoxEntity> FOX = EntityType.register("fox", Builder.create(FoxEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(8));
    public static final EntityType<GhastEntity> GHAST = EntityType.register("ghast", Builder.create(GhastEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(4.0f, 4.0f).maxTrackingRange(10));
    public static final EntityType<GiantEntity> GIANT = EntityType.register("giant", Builder.create(GiantEntity::new, SpawnGroup.MONSTER).setDimensions(3.6f, 12.0f).maxTrackingRange(10));
    public static final EntityType<GuardianEntity> GUARDIAN = EntityType.register("guardian", Builder.create(GuardianEntity::new, SpawnGroup.MONSTER).setDimensions(0.85f, 0.85f).maxTrackingRange(8));
    public static final EntityType<HoglinEntity> HOGLIN = EntityType.register("hoglin", Builder.create(HoglinEntity::new, SpawnGroup.MONSTER).setDimensions(1.3964844f, 1.4f).maxTrackingRange(8));
    public static final EntityType<HorseEntity> HORSE = EntityType.register("horse", Builder.create(HorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<HuskEntity> HUSK = EntityType.register("husk", Builder.create(HuskEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<IllusionerEntity> ILLUSIONER = EntityType.register("illusioner", Builder.create(IllusionerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<IronGolemEntity> IRON_GOLEM = EntityType.register("iron_golem", Builder.create(IronGolemEntity::new, SpawnGroup.MISC).setDimensions(1.4f, 2.7f).maxTrackingRange(10));
    public static final EntityType<ItemEntity> ITEM = EntityType.register("item", Builder.create(ItemEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(6).trackingTickInterval(20));
    public static final EntityType<ItemFrameEntity> ITEM_FRAME = EntityType.register("item_frame", Builder.create(ItemFrameEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<FireballEntity> FIREBALL = EntityType.register("fireball", Builder.create(FireballEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<LeashKnotEntity> LEASH_KNOT = EntityType.register("leash_knot", Builder.create(LeashKnotEntity::new, SpawnGroup.MISC).disableSaving().setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<LlamaEntity> LLAMA = EntityType.register("llama", Builder.create(LlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.87f).maxTrackingRange(10));
    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = EntityType.register("llama_spit", Builder.create(LlamaSpitEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = EntityType.register("magma_cube", Builder.create(MagmaCubeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(2.04f, 2.04f).maxTrackingRange(8));
    public static final EntityType<MinecartEntity> MINECART = EntityType.register("minecart", Builder.create(MinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<ChestMinecartEntity> CHEST_MINECART = EntityType.register("chest_minecart", Builder.create(ChestMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = EntityType.register("command_block_minecart", Builder.create(CommandBlockMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = EntityType.register("furnace_minecart", Builder.create(FurnaceMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = EntityType.register("hopper_minecart", Builder.create(HopperMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = EntityType.register("spawner_minecart", Builder.create(SpawnerMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<TntMinecartEntity> TNT_MINECART = EntityType.register("tnt_minecart", Builder.create(TntMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<MuleEntity> MULE = EntityType.register("mule", Builder.create(MuleEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(8));
    public static final EntityType<MooshroomEntity> MOOSHROOM = EntityType.register("mooshroom", Builder.create(MooshroomEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.4f).maxTrackingRange(10));
    public static final EntityType<OcelotEntity> OCELOT = EntityType.register("ocelot", Builder.create(OcelotEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(10));
    public static final EntityType<PaintingEntity> PAINTING = EntityType.register("painting", Builder.create(PaintingEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<PandaEntity> PANDA = EntityType.register("panda", Builder.create(PandaEntity::new, SpawnGroup.CREATURE).setDimensions(1.3f, 1.25f).maxTrackingRange(10));
    public static final EntityType<ParrotEntity> PARROT = EntityType.register("parrot", Builder.create(ParrotEntity::new, SpawnGroup.CREATURE).setDimensions(0.5f, 0.9f).maxTrackingRange(8));
    public static final EntityType<PhantomEntity> PHANTOM = EntityType.register("phantom", Builder.create(PhantomEntity::new, SpawnGroup.MONSTER).setDimensions(0.9f, 0.5f).maxTrackingRange(8));
    public static final EntityType<PigEntity> PIG = EntityType.register("pig", Builder.create(PigEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 0.9f).maxTrackingRange(10));
    public static final EntityType<PiglinEntity> PIGLIN = EntityType.register("piglin", Builder.create(PiglinEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PillagerEntity> PILLAGER = EntityType.register("pillager", Builder.create(PillagerEntity::new, SpawnGroup.MONSTER).spawnableFarFromPlayer().setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PolarBearEntity> POLAR_BEAR = EntityType.register("polar_bear", Builder.create(PolarBearEntity::new, SpawnGroup.CREATURE).setDimensions(1.4f, 1.4f).maxTrackingRange(10));
    public static final EntityType<TntEntity> TNT = EntityType.register("tnt", Builder.create(TntEntity::new, SpawnGroup.MISC).makeFireImmune().setDimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(10));
    public static final EntityType<PufferfishEntity> PUFFERFISH = EntityType.register("pufferfish", Builder.create(PufferfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7f, 0.7f).maxTrackingRange(4));
    public static final EntityType<RabbitEntity> RABBIT = EntityType.register("rabbit", Builder.create(RabbitEntity::new, SpawnGroup.CREATURE).setDimensions(0.4f, 0.5f).maxTrackingRange(8));
    public static final EntityType<RavagerEntity> RAVAGER = EntityType.register("ravager", Builder.create(RavagerEntity::new, SpawnGroup.MONSTER).setDimensions(1.95f, 2.2f).maxTrackingRange(10));
    public static final EntityType<SalmonEntity> SALMON = EntityType.register("salmon", Builder.create(SalmonEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7f, 0.4f).maxTrackingRange(4));
    public static final EntityType<SheepEntity> SHEEP = EntityType.register("sheep", Builder.create(SheepEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.3f).maxTrackingRange(10));
    public static final EntityType<ShulkerEntity> SHULKER = EntityType.register("shulker", Builder.create(ShulkerEntity::new, SpawnGroup.MONSTER).makeFireImmune().spawnableFarFromPlayer().setDimensions(1.0f, 1.0f).maxTrackingRange(10));
    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = EntityType.register("shulker_bullet", Builder.create(ShulkerBulletEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(8));
    public static final EntityType<SilverfishEntity> SILVERFISH = EntityType.register("silverfish", Builder.create(SilverfishEntity::new, SpawnGroup.MONSTER).setDimensions(0.4f, 0.3f).maxTrackingRange(8));
    public static final EntityType<SkeletonEntity> SKELETON = EntityType.register("skeleton", Builder.create(SkeletonEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.99f).maxTrackingRange(8));
    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = EntityType.register("skeleton_horse", Builder.create(SkeletonHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<SlimeEntity> SLIME = EntityType.register("slime", Builder.create(SlimeEntity::new, SpawnGroup.MONSTER).setDimensions(2.04f, 2.04f).maxTrackingRange(10));
    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = EntityType.register("small_fireball", Builder.create(SmallFireballEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<SnowGolemEntity> SNOW_GOLEM = EntityType.register("snow_golem", Builder.create(SnowGolemEntity::new, SpawnGroup.MISC).setDimensions(0.7f, 1.9f).maxTrackingRange(8));
    public static final EntityType<SnowballEntity> SNOWBALL = EntityType.register("snowball", Builder.create(SnowballEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = EntityType.register("spectral_arrow", Builder.create(SpectralArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<SpiderEntity> SPIDER = EntityType.register("spider", Builder.create(SpiderEntity::new, SpawnGroup.MONSTER).setDimensions(1.4f, 0.9f).maxTrackingRange(8));
    public static final EntityType<SquidEntity> SQUID = EntityType.register("squid", Builder.create(SquidEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.8f, 0.8f).maxTrackingRange(8));
    public static final EntityType<StrayEntity> STRAY = EntityType.register("stray", Builder.create(StrayEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.99f).maxTrackingRange(8));
    public static final EntityType<StriderEntity> STRIDER = EntityType.register("strider", Builder.create(StriderEntity::new, SpawnGroup.CREATURE).makeFireImmune().setDimensions(0.9f, 1.7f).maxTrackingRange(10));
    public static final EntityType<EggEntity> EGG = EntityType.register("egg", Builder.create(EggEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<EnderPearlEntity> ENDER_PEARL = EntityType.register("ender_pearl", Builder.create(EnderPearlEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = EntityType.register("experience_bottle", Builder.create(ExperienceBottleEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<PotionEntity> POTION = EntityType.register("potion", Builder.create(PotionEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<TridentEntity> TRIDENT = EntityType.register("trident", Builder.create(TridentEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = EntityType.register("trader_llama", Builder.create(TraderLlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.87f).maxTrackingRange(10));
    public static final EntityType<TropicalFishEntity> TROPICAL_FISH = EntityType.register("tropical_fish", Builder.create(TropicalFishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.4f).maxTrackingRange(4));
    public static final EntityType<TurtleEntity> TURTLE = EntityType.register("turtle", Builder.create(TurtleEntity::new, SpawnGroup.CREATURE).setDimensions(1.2f, 0.4f).maxTrackingRange(10));
    public static final EntityType<VexEntity> VEX = EntityType.register("vex", Builder.create(VexEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.4f, 0.8f).maxTrackingRange(8));
    public static final EntityType<VillagerEntity> VILLAGER = EntityType.register("villager", Builder.create(VillagerEntity::new, SpawnGroup.MISC).setDimensions(0.6f, 1.95f).maxTrackingRange(10));
    public static final EntityType<VindicatorEntity> VINDICATOR = EntityType.register("vindicator", Builder.create(VindicatorEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = EntityType.register("wandering_trader", Builder.create(WanderingTraderEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 1.95f).maxTrackingRange(10));
    public static final EntityType<WitchEntity> WITCH = EntityType.register("witch", Builder.create(WitchEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<WitherEntity> WITHER = EntityType.register("wither", Builder.create(WitherEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.9f, 3.5f).maxTrackingRange(10));
    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = EntityType.register("wither_skeleton", Builder.create(WitherSkeletonEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.7f, 2.4f).maxTrackingRange(8));
    public static final EntityType<WitherSkullEntity> WITHER_SKULL = EntityType.register("wither_skull", Builder.create(WitherSkullEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<WolfEntity> WOLF = EntityType.register("wolf", Builder.create(WolfEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.85f).maxTrackingRange(10));
    public static final EntityType<ZoglinEntity> ZOGLIN = EntityType.register("zoglin", Builder.create(ZoglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(1.3964844f, 1.4f).maxTrackingRange(8));
    public static final EntityType<ZombieEntity> ZOMBIE = EntityType.register("zombie", Builder.create(ZombieEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = EntityType.register("zombie_horse", Builder.create(ZombieHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = EntityType.register("zombie_villager", Builder.create(ZombieVillagerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = EntityType.register("zombified_piglin", Builder.create(ZombifiedPiglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<LightningEntity> LIGHTNING_BOLT = EntityType.register("lightning_bolt", Builder.create(SpawnGroup.MISC).disableSaving().setDimensions(0.0f, 0.0f));
    public static final EntityType<PlayerEntity> PLAYER = EntityType.register("player", Builder.create(SpawnGroup.MISC).disableSaving().disableSummon().setDimensions(0.6f, 1.8f).maxTrackingRange(32).trackingTickInterval(2));
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER = EntityType.register("fishing_bobber", Builder.create(SpawnGroup.MISC).disableSaving().disableSummon().setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(5));
    private final EntityFactory<T> factory;
    private final SpawnGroup spawnGroup;
    private final boolean saveable;
    private final boolean summonable;
    private final boolean fireImmune;
    private final boolean spawnableFarFromPlayer;
    private final int maxTrackDistance;
    private final int trackTickInterval;
    @Nullable
    private String translationKey;
    @Nullable
    private Text name;
    @Nullable
    private Identifier lootTableId;
    private final EntityDimensions dimensions;

    private static <T extends Entity> EntityType<T> register(String string, Builder<T> arg) {
        return Registry.register(Registry.ENTITY_TYPE, string, arg.build(string));
    }

    public static Identifier getId(EntityType<?> arg) {
        return Registry.ENTITY_TYPE.getId(arg);
    }

    public static Optional<EntityType<?>> get(String string) {
        return Registry.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(string));
    }

    public EntityType(EntityFactory<T> arg, SpawnGroup arg2, boolean bl, boolean bl2, boolean bl3, boolean bl4, EntityDimensions arg3, int i, int j) {
        this.factory = arg;
        this.spawnGroup = arg2;
        this.spawnableFarFromPlayer = bl4;
        this.saveable = bl;
        this.summonable = bl2;
        this.fireImmune = bl3;
        this.dimensions = arg3;
        this.maxTrackDistance = i;
        this.trackTickInterval = j;
    }

    @Nullable
    public Entity spawnFromItemStack(World arg, @Nullable ItemStack arg2, @Nullable PlayerEntity arg3, BlockPos arg4, SpawnReason arg5, boolean bl, boolean bl2) {
        return this.spawn(arg, arg2 == null ? null : arg2.getTag(), arg2 != null && arg2.hasCustomName() ? arg2.getName() : null, arg3, arg4, arg5, bl, bl2);
    }

    @Nullable
    public T spawn(World arg, @Nullable CompoundTag arg2, @Nullable Text arg3, @Nullable PlayerEntity arg4, BlockPos arg5, SpawnReason arg6, boolean bl, boolean bl2) {
        T lv = this.create(arg, arg2, arg3, arg4, arg5, arg6, bl, bl2);
        arg.spawnEntity((Entity)lv);
        return lv;
    }

    @Nullable
    public T create(World arg, @Nullable CompoundTag arg2, @Nullable Text arg3, @Nullable PlayerEntity arg4, BlockPos arg5, SpawnReason arg6, boolean bl, boolean bl2) {
        double e;
        T lv = this.create(arg);
        if (lv == null) {
            return null;
        }
        if (bl) {
            ((Entity)lv).updatePosition((double)arg5.getX() + 0.5, arg5.getY() + 1, (double)arg5.getZ() + 0.5);
            double d = EntityType.getOriginY(arg, arg5, bl2, ((Entity)lv).getBoundingBox());
        } else {
            e = 0.0;
        }
        ((Entity)lv).refreshPositionAndAngles((double)arg5.getX() + 0.5, (double)arg5.getY() + e, (double)arg5.getZ() + 0.5, MathHelper.wrapDegrees(arg.random.nextFloat() * 360.0f), 0.0f);
        if (lv instanceof MobEntity) {
            MobEntity lv2 = (MobEntity)lv;
            lv2.headYaw = lv2.yaw;
            lv2.bodyYaw = lv2.yaw;
            lv2.initialize(arg, arg.getLocalDifficulty(lv2.getBlockPos()), arg6, null, arg2);
            lv2.playAmbientSound();
        }
        if (arg3 != null && lv instanceof LivingEntity) {
            ((Entity)lv).setCustomName(arg3);
        }
        EntityType.loadFromEntityTag(arg, arg4, lv, arg2);
        return lv;
    }

    protected static double getOriginY(WorldView arg2, BlockPos arg22, boolean bl, Box arg3) {
        Box lv = new Box(arg22);
        if (bl) {
            lv = lv.stretch(0.0, -1.0, 0.0);
        }
        Stream<VoxelShape> stream = arg2.getCollisions(null, lv, arg -> true);
        return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, arg3, stream, bl ? -2.0 : -1.0);
    }

    public static void loadFromEntityTag(World arg, @Nullable PlayerEntity arg2, @Nullable Entity arg3, @Nullable CompoundTag arg4) {
        if (arg4 == null || !arg4.contains("EntityTag", 10)) {
            return;
        }
        MinecraftServer minecraftServer = arg.getServer();
        if (minecraftServer == null || arg3 == null) {
            return;
        }
        if (!(arg.isClient || !arg3.entityDataRequiresOperator() || arg2 != null && minecraftServer.getPlayerManager().isOperator(arg2.getGameProfile()))) {
            return;
        }
        CompoundTag lv = arg3.toTag(new CompoundTag());
        UUID uUID = arg3.getUuid();
        lv.copyFrom(arg4.getCompound("EntityTag"));
        arg3.setUuid(uUID);
        arg3.fromTag(lv);
    }

    public boolean isSaveable() {
        return this.saveable;
    }

    public boolean isSummonable() {
        return this.summonable;
    }

    public boolean isFireImmune() {
        return this.fireImmune;
    }

    public boolean isSpawnableFarFromPlayer() {
        return this.spawnableFarFromPlayer;
    }

    public SpawnGroup getSpawnGroup() {
        return this.spawnGroup;
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
        }
        return this.translationKey;
    }

    public Text getName() {
        if (this.name == null) {
            this.name = new TranslatableText(this.getTranslationKey());
        }
        return this.name;
    }

    public Identifier getLootTableId() {
        if (this.lootTableId == null) {
            Identifier lv = Registry.ENTITY_TYPE.getId(this);
            this.lootTableId = new Identifier(lv.getNamespace(), "entities/" + lv.getPath());
        }
        return this.lootTableId;
    }

    public float getWidth() {
        return this.dimensions.width;
    }

    public float getHeight() {
        return this.dimensions.height;
    }

    @Nullable
    public T create(World arg) {
        return this.factory.create(this, arg);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Entity createInstanceFromId(int i, World arg) {
        return EntityType.newInstance(arg, Registry.ENTITY_TYPE.get(i));
    }

    public static Optional<Entity> getEntityFromTag(CompoundTag arg, World arg22) {
        return Util.ifPresentOrElse(EntityType.fromTag(arg).map(arg2 -> arg2.create(arg22)), arg2 -> arg2.fromTag(arg), () -> LOGGER.warn("Skipping Entity with id {}", (Object)arg.getString("id")));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private static Entity newInstance(World arg, @Nullable EntityType<?> arg2) {
        return arg2 == null ? null : (Entity)arg2.create(arg);
    }

    public Box createSimpleBoundingBox(double d, double e, double f) {
        float g = this.getWidth() / 2.0f;
        return new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.getHeight(), f + (double)g);
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> fromTag(CompoundTag arg) {
        return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(arg.getString("id")));
    }

    @Nullable
    public static Entity loadEntityWithPassengers(CompoundTag arg, World arg2, Function<Entity, Entity> function) {
        return EntityType.loadEntityFromTag(arg, arg2).map(function).map(arg3 -> {
            if (arg.contains("Passengers", 9)) {
                ListTag lv = arg.getList("Passengers", 10);
                for (int i = 0; i < lv.size(); ++i) {
                    Entity lv2 = EntityType.loadEntityWithPassengers(lv.getCompound(i), arg2, function);
                    if (lv2 == null) continue;
                    lv2.startRiding((Entity)arg3, true);
                }
            }
            return arg3;
        }).orElse(null);
    }

    private static Optional<Entity> loadEntityFromTag(CompoundTag arg, World arg2) {
        try {
            return EntityType.getEntityFromTag(arg, arg2);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException);
            return Optional.empty();
        }
    }

    public int getMaxTrackDistance() {
        return this.maxTrackDistance;
    }

    public int getTrackTickInterval() {
        return this.trackTickInterval;
    }

    public boolean alwaysUpdateVelocity() {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean isIn(Tag<EntityType<?>> arg) {
        return arg.contains(this);
    }

    public static interface EntityFactory<T extends Entity> {
        public T create(EntityType<T> var1, World var2);
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final SpawnGroup spawnGroup;
        private boolean saveable = true;
        private boolean summonable = true;
        private boolean fireImmune;
        private boolean spawnableFarFromPlayer;
        private int maxTrackingRange = 5;
        private int trackingTickInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.changing(0.6f, 1.8f);

        private Builder(EntityFactory<T> arg, SpawnGroup arg2) {
            this.factory = arg;
            this.spawnGroup = arg2;
            this.spawnableFarFromPlayer = arg2 == SpawnGroup.CREATURE || arg2 == SpawnGroup.MISC;
        }

        public static <T extends Entity> Builder<T> create(EntityFactory<T> arg, SpawnGroup arg2) {
            return new Builder<T>(arg, arg2);
        }

        public static <T extends Entity> Builder<T> create(SpawnGroup arg3) {
            return new Builder<Entity>((arg, arg2) -> null, arg3);
        }

        public Builder<T> setDimensions(float f, float g) {
            this.dimensions = EntityDimensions.changing(f, g);
            return this;
        }

        public Builder<T> disableSummon() {
            this.summonable = false;
            return this;
        }

        public Builder<T> disableSaving() {
            this.saveable = false;
            return this;
        }

        public Builder<T> makeFireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> spawnableFarFromPlayer() {
            this.spawnableFarFromPlayer = true;
            return this;
        }

        public Builder<T> maxTrackingRange(int i) {
            this.maxTrackingRange = i;
            return this;
        }

        public Builder<T> trackingTickInterval(int i) {
            this.trackingTickInterval = i;
            return this;
        }

        public EntityType<T> build(String string) {
            if (this.saveable) {
                try {
                    Schemas.getFixer().getSchema(DataFixUtils.makeKey((int)SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TREE, string);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    if (SharedConstants.isDevelopment) {
                        throw illegalArgumentException;
                    }
                    LOGGER.warn("No data fixer registered for entity {}", (Object)string);
                }
            }
            return new EntityType<T>(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.dimensions, this.maxTrackingRange, this.trackingTickInterval);
        }
    }
}

