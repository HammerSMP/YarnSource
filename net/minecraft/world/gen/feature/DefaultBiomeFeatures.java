/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.JungleFoliagePlacer;
import net.minecraft.world.gen.feature.MegaPineFoliagePlacer;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherrackReplaceBlobsFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class DefaultBiomeFeatures {
    private static final BlockState GRASS = Blocks.GRASS.getDefaultState();
    private static final BlockState FERN = Blocks.FERN.getDefaultState();
    private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
    private static final BlockState OAK_LOG = Blocks.OAK_LOG.getDefaultState();
    private static final BlockState OAK_LEAVES = Blocks.OAK_LEAVES.getDefaultState();
    private static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.getDefaultState();
    private static final BlockState JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.getDefaultState();
    private static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.getDefaultState();
    private static final BlockState SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();
    private static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.getDefaultState();
    private static final BlockState ACACIA_LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();
    private static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.getDefaultState();
    private static final BlockState BIRCH_LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
    private static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
    private static final BlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();
    private static final BlockState WATER = Blocks.WATER.getDefaultState();
    private static final BlockState LAVA = Blocks.LAVA.getDefaultState();
    private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final BlockState GRANITE = Blocks.GRANITE.getDefaultState();
    private static final BlockState DIORITE = Blocks.DIORITE.getDefaultState();
    private static final BlockState ANDESITE = Blocks.ANDESITE.getDefaultState();
    private static final BlockState COAL_ORE = Blocks.COAL_ORE.getDefaultState();
    private static final BlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
    private static final BlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
    private static final BlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.getDefaultState();
    private static final BlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.getDefaultState();
    private static final BlockState LAPIS_ORE = Blocks.LAPIS_ORE.getDefaultState();
    private static final BlockState STONE = Blocks.STONE.getDefaultState();
    private static final BlockState EMERALD_ORE = Blocks.EMERALD_ORE.getDefaultState();
    private static final BlockState INFESTED_STONE = Blocks.INFESTED_STONE.getDefaultState();
    private static final BlockState SAND = Blocks.SAND.getDefaultState();
    private static final BlockState CLAY = Blocks.CLAY.getDefaultState();
    private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
    private static final BlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();
    private static final BlockState LARGE_FERN = Blocks.LARGE_FERN.getDefaultState();
    private static final BlockState TALL_GRASS = Blocks.TALL_GRASS.getDefaultState();
    private static final BlockState LILAC = Blocks.LILAC.getDefaultState();
    private static final BlockState ROSE_BUSH = Blocks.ROSE_BUSH.getDefaultState();
    private static final BlockState PEONY = Blocks.PEONY.getDefaultState();
    private static final BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.getDefaultState();
    private static final BlockState RED_MUSHROOM = Blocks.RED_MUSHROOM.getDefaultState();
    private static final BlockState SEAGRASS = Blocks.SEAGRASS.getDefaultState();
    private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
    private static final BlockState BLUE_ICE = Blocks.BLUE_ICE.getDefaultState();
    private static final BlockState LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.getDefaultState();
    private static final BlockState BLUE_ORCHID = Blocks.BLUE_ORCHID.getDefaultState();
    private static final BlockState POPPY = Blocks.POPPY.getDefaultState();
    private static final BlockState DANDELION = Blocks.DANDELION.getDefaultState();
    private static final BlockState DEAD_BUSH = Blocks.DEAD_BUSH.getDefaultState();
    private static final BlockState MELON = Blocks.MELON.getDefaultState();
    private static final BlockState PUMPKIN = Blocks.PUMPKIN.getDefaultState();
    private static final BlockState SWEET_BERRY_BUSH = (BlockState)Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 3);
    private static final BlockState FIRE = Blocks.FIRE.getDefaultState();
    private static final BlockState SOUL_FIRE = Blocks.SOUL_FIRE.getDefaultState();
    private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
    private static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.getDefaultState();
    private static final BlockState CRIMSON_ROOTS = Blocks.CRIMSON_ROOTS.getDefaultState();
    private static final BlockState LILY_PAD = Blocks.LILY_PAD.getDefaultState();
    private static final BlockState SNOW = Blocks.SNOW.getDefaultState();
    private static final BlockState JACK_O_LANTERN = Blocks.JACK_O_LANTERN.getDefaultState();
    private static final BlockState SUNFLOWER = Blocks.SUNFLOWER.getDefaultState();
    private static final BlockState CACTUS = Blocks.CACTUS.getDefaultState();
    private static final BlockState SUGAR_CANE = Blocks.SUGAR_CANE.getDefaultState();
    private static final BlockState RED_MUSHROOM_BLOCK = (BlockState)Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, false);
    private static final BlockState BROWN_MUSHROOM_BLOCK = (BlockState)((BlockState)Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.UP, true)).with(MushroomBlock.DOWN, false);
    private static final BlockState MUSHROOM_BLOCK = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, false)).with(MushroomBlock.DOWN, false);
    private static final BlockState NETHER_GOLD_ORE = Blocks.NETHER_GOLD_ORE.getDefaultState();
    private static final BlockState NETHER_QUARTZ_ORE = Blocks.NETHER_QUARTZ_ORE.getDefaultState();
    private static final BlockState WARPED_STEM = Blocks.WARPED_STEM.getDefaultState();
    private static final BlockState WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.getDefaultState();
    private static final BlockState NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.getDefaultState();
    private static final BlockState CRIMSON_STEM = Blocks.CRIMSON_STEM.getDefaultState();
    private static final BlockState SHROOMLIGHT = Blocks.SHROOMLIGHT.getDefaultState();
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24687 = StructureFeature.PILLAGER_OUTPOST.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> field_24688 = StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.NORMAL));
    public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> field_24689 = StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.MESA));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24690 = StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24691 = StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24692 = StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24693 = StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> field_24694 = StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false));
    public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> field_24695 = StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24696 = StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24697 = StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24698 = StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> field_24699 = StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
    public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> field_24700 = StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3f, 0.9f));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24701 = StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24702 = StructureFeature.NETHER_FOSSIL.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_24703 = StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE);
    public static final ConfiguredStructureFeature<BuriedTreasureFeatureConfig, ? extends StructureFeature<BuriedTreasureFeatureConfig>> field_24704 = StructureFeature.BURIED_TREASURE.configure(new BuriedTreasureFeatureConfig(0.01f));
    public static final ConfiguredStructureFeature<BastionRemnantFeatureConfig, ? extends StructureFeature<BastionRemnantFeatureConfig>> field_24705 = StructureFeature.BASTION_REMNANT.configure(new BastionRemnantFeatureConfig((Map<String, Integer>)BastionRemnantGenerator.START_POOLS_TO_SIZES));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_24706 = StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(new Identifier("village/plains/town_centers"), 6));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_24707 = StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(new Identifier("village/desert/town_centers"), 6));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_24708 = StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(new Identifier("village/savanna/town_centers"), 6));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_24709 = StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(new Identifier("village/snowy/town_centers"), 6));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_24710 = StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(new Identifier("village/taiga/town_centers"), 6));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> STANDARD_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> DESERT_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> JUNGLE_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> SWAMP_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> MOUNTAIN_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> OCEAN_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> NETHER_CONFIGURED_RUINED_PORTAL = StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER));
    public static final TreeFeatureConfig OAK_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    private static final BeehiveTreeDecorator VERY_RARE_BEEHIVES_CONFIG = new BeehiveTreeDecorator(0.002f);
    private static final BeehiveTreeDecorator REGULAR_BEEHIVES_CONFIG = new BeehiveTreeDecorator(0.02f);
    private static final BeehiveTreeDecorator MORE_BEEHIVES_CONFIG = new BeehiveTreeDecorator(0.05f);
    public static final TreeFeatureConfig OAK_TREE_WITH_RARE_BEEHIVES_CONFIG = OAK_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)VERY_RARE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig OAK_TREE_WITH_BEEHIVES_CONFIG = OAK_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)REGULAR_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig OAK_TREE_WITH_MORE_BEEHIVES_CONFIG = OAK_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)MORE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig JUNGLE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(JUNGLE_LOG), new SimpleBlockStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(4, 8, 0), new TwoLayersFeatureSize(1, 0, 1)).decorators((List<TreeDecorator>)ImmutableList.of((Object)new CocoaBeansTreeDecorator(0.2f), (Object)TrunkVineTreeDecorator.field_24965, (Object)LeaveVineTreeDecorator.field_24961)).ignoreVines().build();
    public static final TreeFeatureConfig JUNGLE_SAPLING_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(JUNGLE_LOG), new SimpleBlockStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(4, 8, 0), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    public static final TreeFeatureConfig PINE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(SPRUCE_LOG), new SimpleBlockStateProvider(SPRUCE_LEAVES), new PineFoliagePlacer(1, 0, 1, 0, 3, 1), new StraightTrunkPlacer(6, 4, 0), new TwoLayersFeatureSize(2, 0, 2)).ignoreVines().build();
    public static final TreeFeatureConfig SPRUCE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(SPRUCE_LOG), new SimpleBlockStateProvider(SPRUCE_LEAVES), new SpruceFoliagePlacer(2, 1, 0, 2, 1, 1), new StraightTrunkPlacer(5, 2, 1), new TwoLayersFeatureSize(2, 0, 2)).ignoreVines().build();
    public static final TreeFeatureConfig ACACIA_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(ACACIA_LOG), new SimpleBlockStateProvider(ACACIA_LEAVES), new AcaciaFoliagePlacer(2, 0, 0, 0), new ForkingTrunkPlacer(5, 2, 2), new TwoLayersFeatureSize(1, 0, 2)).ignoreVines().build();
    public static final TreeFeatureConfig BIRCH_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(BIRCH_LOG), new SimpleBlockStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(5, 2, 0), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    public static final TreeFeatureConfig BIRCH_TREE_WITH_RARE_BEEHIVES_CONFIG = BIRCH_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)VERY_RARE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig BIRCH_TREE_WITH_BEEHIVES_CONFIG = BIRCH_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)REGULAR_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig BIRCH_TREE_WITH_MORE_BEEHIVES_CONFIG = BIRCH_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)MORE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig LARGE_BIRCH_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(BIRCH_LOG), new SimpleBlockStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0, 0, 0, 3), new StraightTrunkPlacer(5, 2, 6), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().decorators((List<TreeDecorator>)ImmutableList.of((Object)VERY_RARE_BEEHIVES_CONFIG)).build();
    public static final TreeFeatureConfig SWAMP_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new BlobFoliagePlacer(3, 0, 0, 0, 3), new StraightTrunkPlacer(5, 3, 0), new TwoLayersFeatureSize(1, 0, 1)).maxWaterDepth(1).decorators((List<TreeDecorator>)ImmutableList.of((Object)LeaveVineTreeDecorator.field_24961)).build();
    public static final TreeFeatureConfig FANCY_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new LargeOakFoliagePlacer(2, 0, 4, 0, 4), new LargeOakTrunkPlacer(3, 11, 0), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))).ignoreVines().heightmap(Heightmap.Type.MOTION_BLOCKING).build();
    public static final TreeFeatureConfig FANCY_TREE_WITH_RARE_BEEHIVES_CONFIG = FANCY_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)VERY_RARE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig FANCY_TREE_WITH_BEEHIVES_CONFIG = FANCY_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)REGULAR_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG = FANCY_TREE_CONFIG.setTreeDecorators((List<TreeDecorator>)ImmutableList.of((Object)MORE_BEEHIVES_CONFIG));
    public static final TreeFeatureConfig JUNGLE_GROUND_BUSH_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(JUNGLE_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new BushFoliagePlacer(2, 0, 1, 0, 2), new StraightTrunkPlacer(1, 0, 0), new TwoLayersFeatureSize(0, 0, 0)).heightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).build();
    public static final TreeFeatureConfig DARK_OAK_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(DARK_OAK_LOG), new SimpleBlockStateProvider(DARK_OAK_LEAVES), new DarkOakFoliagePlacer(0, 0, 0, 0), new DarkOakTrunkPlacer(6, 2, 1), new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())).maxWaterDepth(Integer.MAX_VALUE).heightmap(Heightmap.Type.MOTION_BLOCKING).ignoreVines().build();
    public static final TreeFeatureConfig MEGA_SPRUCE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(SPRUCE_LOG), new SimpleBlockStateProvider(SPRUCE_LEAVES), new MegaPineFoliagePlacer(0, 0, 0, 0, 4, 13), new GiantTrunkPlacer(13, 2, 14), new TwoLayersFeatureSize(1, 1, 2)).decorators((List<TreeDecorator>)ImmutableList.of((Object)new AlterGroundTreeDecorator(new SimpleBlockStateProvider(PODZOL)))).build();
    public static final TreeFeatureConfig MEGA_PINE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(SPRUCE_LOG), new SimpleBlockStateProvider(SPRUCE_LEAVES), new MegaPineFoliagePlacer(0, 0, 0, 0, 4, 3), new GiantTrunkPlacer(13, 2, 14), new TwoLayersFeatureSize(1, 1, 2)).decorators((List<TreeDecorator>)ImmutableList.of((Object)new AlterGroundTreeDecorator(new SimpleBlockStateProvider(PODZOL)))).build();
    public static final TreeFeatureConfig MEGA_JUNGLE_TREE_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(JUNGLE_LOG), new SimpleBlockStateProvider(JUNGLE_LEAVES), new JungleFoliagePlacer(2, 0, 0, 0, 2), new MegaJungleTrunkPlacer(10, 2, 19), new TwoLayersFeatureSize(1, 1, 2)).decorators((List<TreeDecorator>)ImmutableList.of((Object)TrunkVineTreeDecorator.field_24965, (Object)LeaveVineTreeDecorator.field_24961)).build();
    public static final RandomPatchFeatureConfig GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(GRASS), SimpleBlockPlacer.field_24871).tries(32).build();
    public static final RandomPatchFeatureConfig TAIGA_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(new WeightedBlockStateProvider().addState(GRASS, 1).addState(FERN, 4), SimpleBlockPlacer.field_24871).tries(32).build();
    public static final RandomPatchFeatureConfig LUSH_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(new WeightedBlockStateProvider().addState(GRASS, 3).addState(FERN, 1), SimpleBlockPlacer.field_24871).blacklist((Set<BlockState>)ImmutableSet.of((Object)PODZOL)).tries(32).build();
    public static final RandomPatchFeatureConfig LILY_OF_THE_VALLEY_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(LILY_OF_THE_VALLEY), SimpleBlockPlacer.field_24871).tries(64).build();
    public static final RandomPatchFeatureConfig BLUE_ORCHID_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(BLUE_ORCHID), SimpleBlockPlacer.field_24871).tries(64).build();
    public static final RandomPatchFeatureConfig DEFAULT_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(new WeightedBlockStateProvider().addState(POPPY, 2).addState(DANDELION, 1), SimpleBlockPlacer.field_24871).tries(64).build();
    public static final RandomPatchFeatureConfig PLAINS_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(PlainsFlowerBlockStateProvider.field_24943, SimpleBlockPlacer.field_24871).tries(64).build();
    public static final RandomPatchFeatureConfig FOREST_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(ForestFlowerBlockStateProvider.field_24941, SimpleBlockPlacer.field_24871).tries(64).build();
    public static final RandomPatchFeatureConfig DEAD_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(DEAD_BUSH), SimpleBlockPlacer.field_24871).tries(4).build();
    public static final RandomPatchFeatureConfig MELON_PATCH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(MELON), SimpleBlockPlacer.field_24871).tries(64).whitelist((Set<Block>)ImmutableSet.of((Object)GRASS_BLOCK.getBlock())).canReplace().cannotProject().build();
    public static final RandomPatchFeatureConfig PUMPKIN_PATCH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(PUMPKIN), SimpleBlockPlacer.field_24871).tries(64).whitelist((Set<Block>)ImmutableSet.of((Object)GRASS_BLOCK.getBlock())).cannotProject().build();
    public static final RandomPatchFeatureConfig SWEET_BERRY_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(SWEET_BERRY_BUSH), SimpleBlockPlacer.field_24871).tries(64).whitelist((Set<Block>)ImmutableSet.of((Object)GRASS_BLOCK.getBlock())).cannotProject().build();
    public static final RandomPatchFeatureConfig NETHER_FIRE_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(FIRE), SimpleBlockPlacer.field_24871).tries(64).whitelist((Set<Block>)ImmutableSet.of((Object)NETHERRACK.getBlock())).cannotProject().build();
    public static final RandomPatchFeatureConfig SOUL_FIRE_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(SOUL_FIRE), new SimpleBlockPlacer()).tries(64).whitelist((Set<Block>)ImmutableSet.of((Object)SOUL_SOIL.getBlock())).cannotProject().build();
    public static final RandomPatchFeatureConfig LILY_PAD_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(LILY_PAD), SimpleBlockPlacer.field_24871).tries(10).build();
    public static final RandomPatchFeatureConfig RED_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(RED_MUSHROOM), SimpleBlockPlacer.field_24871).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig BROWN_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(BROWN_MUSHROOM), SimpleBlockPlacer.field_24871).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig SOUL_SAND_CRIMSON_ROOTS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(CRIMSON_ROOTS), new SimpleBlockPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig LILAC_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(LILAC), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig ROSE_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ROSE_BUSH), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig PEONY_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(PEONY), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig SUNFLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(SUNFLOWER), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig TALL_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(TALL_GRASS), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig LARGE_FERN_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(LARGE_FERN), new DoublePlantPlacer()).tries(64).cannotProject().build();
    public static final RandomPatchFeatureConfig CACTUS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(CACTUS), new ColumnPlacer(1, 2)).tries(10).cannotProject().build();
    public static final RandomPatchFeatureConfig SUGAR_CANE_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(SUGAR_CANE), new ColumnPlacer(2, 2)).tries(20).spreadX(4).spreadY(0).spreadZ(4).cannotProject().needsWater().build();
    public static final BlockPileFeatureConfig HAY_PILE_CONFIG = new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK));
    public static final BlockPileFeatureConfig SNOW_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleBlockStateProvider(SNOW));
    public static final BlockPileFeatureConfig MELON_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleBlockStateProvider(MELON));
    public static final BlockPileFeatureConfig PUMPKIN_PILE_CONFIG = new BlockPileFeatureConfig(new WeightedBlockStateProvider().addState(PUMPKIN, 19).addState(JACK_O_LANTERN, 1));
    public static final BlockPileFeatureConfig BLUE_ICE_PILE_CONFIG = new BlockPileFeatureConfig(new WeightedBlockStateProvider().addState(BLUE_ICE, 1).addState(PACKED_ICE, 5));
    public static final FluidState WATER_FLUID = Fluids.WATER.getDefaultState();
    public static final FluidState LAVA_FLUID = Fluids.LAVA.getDefaultState();
    public static final SpringFeatureConfig WATER_SPRING_CONFIG = new SpringFeatureConfig(WATER_FLUID, true, 4, 1, (Set<Block>)ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE));
    public static final SpringFeatureConfig LAVA_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, true, 4, 1, (Set<Block>)ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE));
    public static final SpringFeatureConfig NETHER_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, false, 4, 1, (Set<Block>)ImmutableSet.of((Object)Blocks.NETHERRACK));
    public static final SpringFeatureConfig MIXED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, true, 4, 1, (Set<Block>)ImmutableSet.of((Object)Blocks.NETHERRACK, (Object)Blocks.SOUL_SAND, (Object)Blocks.GRAVEL, (Object)Blocks.MAGMA_BLOCK, (Object)Blocks.BLACKSTONE));
    public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, false, 5, 0, (Set<Block>)ImmutableSet.of((Object)Blocks.NETHERRACK));
    public static final SpringFeatureConfig SOUL_SAND_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, false, 4, 1, (Set<Block>)ImmutableSet.of((Object)Blocks.SOUL_SAND));
    public static final SpringFeatureConfig ENCLOSED_SOUL_SAND_SPRING_CONFIG = new SpringFeatureConfig(LAVA_FLUID, false, 5, 0, (Set<Block>)ImmutableSet.of((Object)Blocks.SOUL_SAND));
    public static final BasaltColumnsFeatureConfig BASALT_COLUMN_CONFIG = new BasaltColumnsFeatureConfig.Builder().reach(1).height(1, 4).build();
    public static final BasaltColumnsFeatureConfig TALL_BASALT_COLUMN_CONFIG = new BasaltColumnsFeatureConfig.Builder().reach(2, 3).height(5, 10).build();
    public static final NetherrackReplaceBlobsFeatureConfig BASALT_BLOB_CONFIG = new NetherrackReplaceBlobsFeatureConfig.Builder().minReachPos(new Vec3i(3, 3, 3)).maxReachPos(new Vec3i(7, 7, 7)).target(Blocks.NETHERRACK.getDefaultState()).state(Blocks.BASALT.getDefaultState()).build();
    public static final NetherrackReplaceBlobsFeatureConfig BLACKSTONE_BLOB_CONFIG = new NetherrackReplaceBlobsFeatureConfig.Builder().minReachPos(new Vec3i(3, 3, 3)).maxReachPos(new Vec3i(7, 7, 7)).target(Blocks.NETHERRACK.getDefaultState()).state(Blocks.BLACKSTONE.getDefaultState()).build();
    public static final DeltaFeatureConfig DELTA_CONFIG = new DeltaFeatureConfig.Builder().contents(Blocks.LAVA.getDefaultState()).radius(3, 7).rim(Blocks.MAGMA_BLOCK.getDefaultState(), 2).build();
    public static final HugeMushroomFeatureConfig HUGE_RED_MUSHROOM_CONFIG = new HugeMushroomFeatureConfig(new SimpleBlockStateProvider(RED_MUSHROOM_BLOCK), new SimpleBlockStateProvider(MUSHROOM_BLOCK), 2);
    public static final HugeMushroomFeatureConfig HUGE_BROWN_MUSHROOM_CONFIG = new HugeMushroomFeatureConfig(new SimpleBlockStateProvider(BROWN_MUSHROOM_BLOCK), new SimpleBlockStateProvider(MUSHROOM_BLOCK), 3);
    public static final BlockPileFeatureConfig CRIMSON_ROOTS_CONFIG = new BlockPileFeatureConfig(new WeightedBlockStateProvider().addState(Blocks.CRIMSON_ROOTS.getDefaultState(), 87).addState(Blocks.CRIMSON_FUNGUS.getDefaultState(), 11).addState(Blocks.WARPED_FUNGUS.getDefaultState(), 1));
    public static final BlockPileFeatureConfig WARPED_ROOTS_CONFIG = new BlockPileFeatureConfig(new WeightedBlockStateProvider().addState(Blocks.WARPED_ROOTS.getDefaultState(), 85).addState(Blocks.CRIMSON_ROOTS.getDefaultState(), 1).addState(Blocks.WARPED_FUNGUS.getDefaultState(), 13).addState(Blocks.CRIMSON_FUNGUS.getDefaultState(), 1));
    public static final BlockPileFeatureConfig NETHER_SPROUTS_CONFIG = new BlockPileFeatureConfig(new SimpleBlockStateProvider(Blocks.NETHER_SPROUTS.getDefaultState()));

    public static void method_28437(Biome arg) {
        arg.addStructureFeature(field_24689);
        arg.addStructureFeature(field_24697);
    }

    public static void method_28440(Biome arg) {
        arg.addStructureFeature(field_24688);
        arg.addStructureFeature(field_24697);
    }

    public static void method_28441(Biome arg) {
        arg.addStructureFeature(field_24688);
        arg.addStructureFeature(field_24694);
    }

    public static void addLandCarvers(Biome arg) {
        arg.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715f)));
        arg.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02f)));
    }

    public static void addOceanCarvers(Biome arg) {
        arg.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.06666667f)));
        arg.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02f)));
        arg.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CANYON, new ProbabilityConfig(0.02f)));
        arg.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667f)));
    }

    public static void addDefaultLakes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LAKES, Feature.LAKE.configure(new SingleStateFeatureConfig(WATER)).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4))));
        arg.addFeature(GenerationStep.Feature.LAKES, Feature.LAKE.configure(new SingleStateFeatureConfig(LAVA)).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80))));
    }

    public static void addDesertLakes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LAKES, Feature.LAKE.configure(new SingleStateFeatureConfig(LAVA)).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80))));
    }

    public static void addDungeons(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.DUNGEONS.configure(new ChanceDecoratorConfig(8))));
    }

    public static void addMineables(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIRT, 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 256))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GRAVEL, 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 0, 0, 256))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GRANITE, 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIORITE, 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, ANDESITE, 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 80))));
    }

    public static void addDefaultOres(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, COAL_ORE, 17)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 128))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, IRON_ORE, 9)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 64))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GOLD_ORE, 9)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(2, 0, 0, 32))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, REDSTONE_ORE, 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(8, 0, 0, 16))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, DIAMOND_ORE, 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 0, 0, 16))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, LAPIS_ORE, 7)).createDecoratedFeature(Decorator.COUNT_DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(1, 16, 16))));
    }

    public static void addExtraGoldOre(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GOLD_ORE, 9)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 32, 32, 80))));
    }

    public static void addEmeraldOre(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.EMERALD_ORE.configure(new EmeraldOreFeatureConfig(STONE, EMERALD_ORE)).createDecoratedFeature(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT)));
    }

    public static void addInfestedStone(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, INFESTED_STONE, 9)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(7, 0, 0, 64))));
    }

    public static void addDefaultDisks(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.DISK.configure(new DiskFeatureConfig(SAND, 7, 2, Lists.newArrayList((Object[])new BlockState[]{DIRT, GRASS_BLOCK}))).createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(3))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.DISK.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.newArrayList((Object[])new BlockState[]{DIRT, CLAY}))).createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.DISK.configure(new DiskFeatureConfig(GRAVEL, 6, 2, Lists.newArrayList((Object[])new BlockState[]{DIRT, GRASS_BLOCK}))).createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1))));
    }

    public static void addClay(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.DISK.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.newArrayList((Object[])new BlockState[]{DIRT, CLAY}))).createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(new CountDecoratorConfig(1))));
    }

    public static void addMossyRocks(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Feature.FOREST_ROCK.configure(new BoulderFeatureConfig(MOSSY_COBBLESTONE, 0)).createDecoratedFeature(Decorator.FOREST_ROCK.configure(new CountDecoratorConfig(3))));
    }

    public static void addLargeFerns(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(LARGE_FERN_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(7))));
    }

    public static void addSweetBerryBushesSnowy(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(12))));
    }

    public static void addSweetBerryBushes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
    }

    public static void addBamboo(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.BAMBOO.configure(new ProbabilityConfig(0.0f)).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(16))));
    }

    public static void addBambooJungleTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.BAMBOO.configure(new ProbabilityConfig(0.2f)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.05f), Feature.TREE.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.15f), Feature.TREE.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.7f)), Feature.RANDOM_PATCH.configure(LUSH_GRASS_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(30, 0.1f, 1))));
    }

    public static void addTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334f)), Feature.TREE.configure(SPRUCE_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addWaterBiomeOakTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.1f)), Feature.TREE.configure(OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1f, 1))));
    }

    public static void addBirchTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.TREE.configure(BIRCH_TREE_WITH_RARE_BEEHIVES_CONFIG).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addForestTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(BIRCH_TREE_WITH_RARE_BEEHIVES_CONFIG).withChance(0.2f), Feature.TREE.configure(FANCY_TREE_WITH_RARE_BEEHIVES_CONFIG).withChance(0.1f)), Feature.TREE.configure(OAK_TREE_WITH_RARE_BEEHIVES_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addTallBirchTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(LARGE_BIRCH_TREE_CONFIG).withChance(0.5f)), Feature.TREE.configure(BIRCH_TREE_WITH_RARE_BEEHIVES_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addSavannaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(ACACIA_TREE_CONFIG).withChance(0.8f)), Feature.TREE.configure(OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(1, 0.1f, 1))));
    }

    public static void addExtraSavannaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(ACACIA_TREE_CONFIG).withChance(0.8f)), Feature.TREE.configure(OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1f, 1))));
    }

    public static void addMountainTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(SPRUCE_TREE_CONFIG).withChance(0.666f), Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.1f)), Feature.TREE.configure(OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1f, 1))));
    }

    public static void addExtraMountainTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(SPRUCE_TREE_CONFIG).withChance(0.666f), Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.1f)), Feature.TREE.configure(OAK_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(3, 0.1f, 1))));
    }

    public static void addJungleTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.1f), Feature.TREE.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5f), Feature.TREE.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.33333334f)), Feature.TREE.configure(JUNGLE_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(50, 0.1f, 1))));
    }

    public static void addJungleEdgeTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(FANCY_TREE_CONFIG).withChance(0.1f), Feature.TREE.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5f)), Feature.TREE.configure(JUNGLE_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1f, 1))));
    }

    public static void addBadlandsPlateauTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.TREE.configure(OAK_TREE_CONFIG).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(5, 0.1f, 1))));
    }

    public static void addSnowySpruceTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.TREE.configure(SPRUCE_TREE_CONFIG).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.1f, 1))));
    }

    public static void addGiantSpruceTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(MEGA_SPRUCE_TREE_CONFIG).withChance(0.33333334f), Feature.TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334f)), Feature.TREE.configure(SPRUCE_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addGiantTreeTaigaTrees(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(MEGA_SPRUCE_TREE_CONFIG).withChance(0.025641026f), Feature.TREE.configure(MEGA_PINE_TREE_CONFIG).withChance(0.30769232f), Feature.TREE.configure(PINE_TREE_CONFIG).withChance(0.33333334f)), Feature.TREE.configure(SPRUCE_TREE_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(10, 0.1f, 1))));
    }

    public static void addJungleGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(LUSH_GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(25))));
    }

    public static void addSavannaTallGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(TALL_GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(7))));
    }

    public static void addShatteredSavannaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5))));
    }

    public static void addSavannaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20))));
    }

    public static void addBadlandsGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20))));
    }

    public static void addForestFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_RANDOM_SELECTOR.configure(new RandomRandomFeatureConfig((List<ConfiguredFeature<?, ?>>)ImmutableList.of(Feature.RANDOM_PATCH.configure(LILAC_CONFIG), Feature.RANDOM_PATCH.configure(ROSE_BUSH_CONFIG), Feature.RANDOM_PATCH.configure(PEONY_CONFIG), Feature.FLOWER.configure(LILY_OF_THE_VALLEY_CONFIG)), 0)).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(5))));
    }

    public static void addForestGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(2))));
    }

    public static void addSwampFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.TREE.configure(SWAMP_TREE_CONFIG).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(2, 0.1f, 1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.FLOWER.configure(BLUE_ORCHID_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(LILY_PAD_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(4))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(8, 0.25f))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(8, 0.125f))));
    }

    public static void addMushroomFieldsFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_BOOLEAN_SELECTOR.configure(new RandomBooleanFeatureConfig(Feature.HUGE_RED_MUSHROOM.configure(HUGE_RED_MUSHROOM_CONFIG), Feature.HUGE_BROWN_MUSHROOM.configure(HUGE_BROWN_MUSHROOM_CONFIG))).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(1, 0.25f))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(1, 0.125f))));
    }

    public static void addPlainsFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig((List<RandomFeatureEntry<?>>)ImmutableList.of(Feature.TREE.configure(FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG).withChance(0.33333334f)), Feature.TREE.configure(OAK_TREE_WITH_MORE_BEEHIVES_CONFIG))).createDecoratedFeature(Decorator.COUNT_EXTRA_HEIGHTMAP.configure(new CountExtraChanceDecoratorConfig(0, 0.05f, 1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.FLOWER.configure(PLAINS_FLOWER_CONFIG).createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_32.configure(new NoiseHeightmapDecoratorConfig(-0.8, 15, 4))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_DOUBLE.configure(new NoiseHeightmapDecoratorConfig(-0.8, 5, 10))));
    }

    public static void addDesertDeadBushes(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(2))));
    }

    public static void addGiantTaigaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(TAIGA_GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(7))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(3, 0.25f))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(3, 0.125f))));
    }

    public static void addDefaultFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.FLOWER.configure(DEFAULT_FLOWER_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(2))));
    }

    public static void addExtraDefaultFlowers(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.FLOWER.configure(DEFAULT_FLOWER_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_32.configure(new CountDecoratorConfig(4))));
    }

    public static void addDefaultGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
    }

    public static void addTaigaGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(TAIGA_GRASS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP.configure(new CountChanceDecoratorConfig(1, 0.25f))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configure(new CountChanceDecoratorConfig(1, 0.125f))));
    }

    public static void addPlainsTallGrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(TALL_GRASS_CONFIG).createDecoratedFeature(Decorator.NOISE_HEIGHTMAP_32.configure(new NoiseHeightmapDecoratorConfig(-0.8, 0, 7))));
    }

    public static void addDefaultMushrooms(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(4))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(8))));
    }

    public static void addDefaultVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(10))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(32))));
    }

    public static void addBadlandsVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(13))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(32))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(5))));
    }

    public static void addJungleVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(MELON_PATCH_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(1))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.VINES.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_HEIGHT_64.configure(new CountDecoratorConfig(50))));
    }

    public static void addDesertVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(60))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(32))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(10))));
    }

    public static void addSwampVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(20))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceDecoratorConfig(32))));
    }

    public static void addDesertFeatures(Biome arg) {
        arg.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(1000))));
    }

    public static void addFossils(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, Feature.FOSSIL.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.CHANCE_PASSTHROUGH.configure(new ChanceDecoratorConfig(64))));
    }

    public static void addKelp(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.KELP.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))));
    }

    public static void addSeagrassOnStone(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(SEAGRASS, (List<BlockState>)ImmutableList.of((Object)STONE), (List<BlockState>)ImmutableList.of((Object)WATER), (List<BlockState>)ImmutableList.of((Object)WATER))).createDecoratedFeature(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1f))));
    }

    public static void addSeagrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SEAGRASS.configure(new SeagrassFeatureConfig(80, 0.3)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT)));
    }

    public static void addMoreSeagrass(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SEAGRASS.configure(new SeagrassFeatureConfig(80, 0.8)).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT)));
    }

    public static void addLessKelp(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.KELP.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG))));
    }

    public static void addSprings(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SPRING_FEATURE.configure(WATER_SPRING_CONFIG).createDecoratedFeature(Decorator.COUNT_BIASED_RANGE.configure(new RangeDecoratorConfig(50, 8, 8, 256))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.SPRING_FEATURE.configure(LAVA_SPRING_CONFIG).createDecoratedFeature(Decorator.COUNT_VERY_BIASED_RANGE.configure(new RangeDecoratorConfig(20, 8, 16, 256))));
    }

    public static void addIcebergs(Biome arg) {
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Feature.ICEBERG.configure(new SingleStateFeatureConfig(PACKED_ICE)).createDecoratedFeature(Decorator.ICEBERG.configure(new ChanceDecoratorConfig(16))));
        arg.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, Feature.ICEBERG.configure(new SingleStateFeatureConfig(BLUE_ICE)).createDecoratedFeature(Decorator.ICEBERG.configure(new ChanceDecoratorConfig(200))));
    }

    public static void addBlueIce(Biome arg) {
        arg.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Feature.BLUE_ICE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.RANDOM_COUNT_RANGE.configure(new RangeDecoratorConfig(20, 30, 32, 64))));
    }

    public static void addFrozenTopLayer(Biome arg) {
        arg.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT));
    }

    public static void addNetherMineables(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.GRAVEL.getDefaultState(), 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(2, 5, 0, 37))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, Blocks.BLACKSTONE.getDefaultState(), 33)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(2, 5, 10, 37))));
        DefaultBiomeFeatures.addNetherOres(arg, 10, 16);
        DefaultBiomeFeatures.addAncientDebris(arg);
    }

    public static void addNetherOres(Biome arg, int i, int j) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, NETHER_GOLD_ORE, 10)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(i, 10, 20, 128))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHERRACK, NETHER_QUARTZ_ORE, 14)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(j, 10, 20, 128))));
    }

    public static void addAncientDebris(Biome arg) {
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.NO_SURFACE_ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.getDefaultState(), 3)).createDecoratedFeature(Decorator.COUNT_DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(1, 16, 8))));
        arg.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Feature.NO_SURFACE_ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NETHER_ORE_REPLACEABLES, Blocks.ANCIENT_DEBRIS.getDefaultState(), 2)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 8, 16, 128))));
    }

    public static void addCrimsonForestVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_NOT_PLANTED_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(8))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NETHER_FOREST_VEGETATION.configure(CRIMSON_ROOTS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(6))));
    }

    public static void addWarpedForestVegetation(Biome arg) {
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_NOT_PLANTED_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(8))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NETHER_FOREST_VEGETATION.configure(WARPED_ROOTS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(5))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NETHER_FOREST_VEGETATION.configure(NETHER_SPROUTS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP.configure(new CountDecoratorConfig(4))));
        arg.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.TWISTING_VINES.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(10, 0, 0, 128))));
    }
}
