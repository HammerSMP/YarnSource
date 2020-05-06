/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public class BlockLootTableGenerator
implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
    private static final LootCondition.Builder NEEDS_SILK_TOUCH = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))));
    private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH = NEEDS_SILK_TOUCH.invert();
    private static final LootCondition.Builder NEEDS_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    private static final LootCondition.Builder NEEDS_SILK_TOUCH_SHEARS = NEEDS_SHEARS.withCondition(NEEDS_SILK_TOUCH);
    private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH_SHEARS = NEEDS_SILK_TOUCH_SHEARS.invert();
    private static final Set<Item> ALWAYS_DROPPED_FROM_EXPLOSION = (Set)Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemConvertible::asItem).collect(ImmutableSet.toImmutableSet());
    private static final float[] SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f};
    private static final float[] JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.025f, 0.027777778f, 0.03125f, 0.041666668f, 0.1f};
    private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

    private static <T> T addExplosionDecayLootFunction(ItemConvertible arg, LootFunctionConsumingBuilder<T> arg2) {
        if (!ALWAYS_DROPPED_FROM_EXPLOSION.contains(arg.asItem())) {
            return arg2.withFunction(ExplosionDecayLootFunction.builder());
        }
        return arg2.getThis();
    }

    private static <T> T addSurvivesExplosionLootCondition(ItemConvertible arg, LootConditionConsumingBuilder<T> arg2) {
        if (!ALWAYS_DROPPED_FROM_EXPLOSION.contains(arg.asItem())) {
            return arg2.withCondition(SurvivesExplosionLootCondition.builder());
        }
        return arg2.getThis();
    }

    private static LootTable.Builder create(ItemConvertible arg) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder create(Block arg, LootCondition.Builder arg2, LootEntry.Builder<?> arg3) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(((LeafEntry.Builder)ItemEntry.builder(arg).withCondition(arg2)).withChild(arg3)));
    }

    private static LootTable.Builder createForNeedingSilkTouch(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.create(arg, NEEDS_SILK_TOUCH, arg2);
    }

    private static LootTable.Builder createForNeedingShears(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.create(arg, NEEDS_SHEARS, arg2);
    }

    private static LootTable.Builder createForNeedingSilkTouchShears(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.create(arg, NEEDS_SILK_TOUCH_SHEARS, arg2);
    }

    private static LootTable.Builder createForBlockWithItemDrops(Block arg, ItemConvertible arg2) {
        return BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(arg2)));
    }

    private static LootTable.Builder create(ItemConvertible arg, LootTableRange arg2) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(arg).withFunction(SetCountLootFunction.builder(arg2)))));
    }

    private static LootTable.Builder createForBlockWithItemDrops(Block arg, ItemConvertible arg2, LootTableRange arg3) {
        return BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(arg2).withFunction(SetCountLootFunction.builder(arg3))));
    }

    private static LootTable.Builder createForNeedingSilkTouch(ItemConvertible arg) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(arg)));
    }

    private static LootTable.Builder createForPottedPlant(ItemConvertible arg) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(Blocks.FLOWER_POT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.FLOWER_POT)))).withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder createForSlabs(Block arg) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(arg).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder createForMultiblock(Block arg, Property<T> arg2, T comparable) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(arg).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(arg2, comparable))))));
    }

    private static LootTable.Builder createForNameableContainer(Block arg) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(arg).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))));
    }

    private static LootTable.Builder createForShulkerBox(Block arg) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Lock", "BlockEntityTag.Lock").withOperation("LootTable", "BlockEntityTag.LootTable").withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed"))).withFunction(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS))))));
    }

    private static LootTable.Builder createForBanner(Block arg) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(arg).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    private static LootTable.Builder createForBeeNest(Block arg) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(arg).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).withFunction(CopyStateFunction.getBuilder(arg).method_21898(BeehiveBlock.HONEY_LEVEL))));
    }

    private static LootTable.Builder createForBeehive(Block arg) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(((LootEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).withCondition(NEEDS_SILK_TOUCH)).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).withFunction(CopyStateFunction.getBuilder(arg).method_21898(BeehiveBlock.HONEY_LEVEL))).withChild(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder createForOreWithSingleItemDrop(Block arg, Item arg2) {
        return BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(arg2).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder createForLargeMushroomBlock(Block arg, ItemConvertible arg2) {
        return BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)ItemEntry.builder(arg2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0f, 2.0f)))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))));
    }

    private static LootTable.Builder createForTallGrass(Block arg) {
        return BlockLootTableGenerator.createForNeedingShears(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.WHEAT_SEEDS).withCondition(RandomChanceLootCondition.builder(0.125f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    private static LootTable.Builder createForCropStem(Block arg, Item arg2) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addExplosionDecayLootFunction(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg2).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, false))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, true))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))))));
    }

    private static LootTable.Builder createForAttachedCropStem(Block arg, Item arg2) {
        return LootTable.builder().withPool(BlockLootTableGenerator.addExplosionDecayLootFunction(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(arg2).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f))))));
    }

    private static LootTable.Builder createForBlockNeedingShears(ItemConvertible arg) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(NEEDS_SHEARS).withEntry(ItemEntry.builder(arg)));
    }

    private static LootTable.Builder createForLeaves(Block arg, Block arg2, float ... fs) {
        return BlockLootTableGenerator.createForNeedingSilkTouchShears(arg, ((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(arg2))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, fs))).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f))));
    }

    private static LootTable.Builder createForOakLeaves(Block arg, Block arg2, float ... fs) {
        return BlockLootTableGenerator.createForLeaves(arg, arg2, fs).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(Items.APPLE))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f))));
    }

    private static LootTable.Builder createForCrops(Block arg, Item arg2, Item arg3, LootCondition.Builder arg4) {
        return BlockLootTableGenerator.addExplosionDecayLootFunction(arg, LootTable.builder().withPool(LootPool.builder().withEntry(((LeafEntry.Builder)ItemEntry.builder(arg2).withCondition(arg4)).withChild(ItemEntry.builder(arg3)))).withPool(LootPool.builder().withCondition(arg4).withEntry((LootEntry.Builder<?>)ItemEntry.builder(arg3).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))));
    }

    public static LootTable.Builder createEmpty() {
        return LootTable.builder();
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
        this.registerForSelfDrop(Blocks.GRANITE);
        this.registerForSelfDrop(Blocks.POLISHED_GRANITE);
        this.registerForSelfDrop(Blocks.DIORITE);
        this.registerForSelfDrop(Blocks.POLISHED_DIORITE);
        this.registerForSelfDrop(Blocks.ANDESITE);
        this.registerForSelfDrop(Blocks.POLISHED_ANDESITE);
        this.registerForSelfDrop(Blocks.DIRT);
        this.registerForSelfDrop(Blocks.COARSE_DIRT);
        this.registerForSelfDrop(Blocks.COBBLESTONE);
        this.registerForSelfDrop(Blocks.OAK_PLANKS);
        this.registerForSelfDrop(Blocks.SPRUCE_PLANKS);
        this.registerForSelfDrop(Blocks.BIRCH_PLANKS);
        this.registerForSelfDrop(Blocks.JUNGLE_PLANKS);
        this.registerForSelfDrop(Blocks.ACACIA_PLANKS);
        this.registerForSelfDrop(Blocks.DARK_OAK_PLANKS);
        this.registerForSelfDrop(Blocks.OAK_SAPLING);
        this.registerForSelfDrop(Blocks.SPRUCE_SAPLING);
        this.registerForSelfDrop(Blocks.BIRCH_SAPLING);
        this.registerForSelfDrop(Blocks.JUNGLE_SAPLING);
        this.registerForSelfDrop(Blocks.ACACIA_SAPLING);
        this.registerForSelfDrop(Blocks.DARK_OAK_SAPLING);
        this.registerForSelfDrop(Blocks.SAND);
        this.registerForSelfDrop(Blocks.RED_SAND);
        this.registerForSelfDrop(Blocks.GOLD_ORE);
        this.registerForSelfDrop(Blocks.IRON_ORE);
        this.registerForSelfDrop(Blocks.OAK_LOG);
        this.registerForSelfDrop(Blocks.SPRUCE_LOG);
        this.registerForSelfDrop(Blocks.BIRCH_LOG);
        this.registerForSelfDrop(Blocks.JUNGLE_LOG);
        this.registerForSelfDrop(Blocks.ACACIA_LOG);
        this.registerForSelfDrop(Blocks.DARK_OAK_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_SPRUCE_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_BIRCH_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_JUNGLE_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_ACACIA_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_DARK_OAK_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_OAK_LOG);
        this.registerForSelfDrop(Blocks.STRIPPED_WARPED_STEM);
        this.registerForSelfDrop(Blocks.STRIPPED_CRIMSON_STEM);
        this.registerForSelfDrop(Blocks.OAK_WOOD);
        this.registerForSelfDrop(Blocks.SPRUCE_WOOD);
        this.registerForSelfDrop(Blocks.BIRCH_WOOD);
        this.registerForSelfDrop(Blocks.JUNGLE_WOOD);
        this.registerForSelfDrop(Blocks.ACACIA_WOOD);
        this.registerForSelfDrop(Blocks.DARK_OAK_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_OAK_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_SPRUCE_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_BIRCH_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_JUNGLE_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_ACACIA_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.registerForSelfDrop(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.registerForSelfDrop(Blocks.STRIPPED_WARPED_HYPHAE);
        this.registerForSelfDrop(Blocks.SPONGE);
        this.registerForSelfDrop(Blocks.WET_SPONGE);
        this.registerForSelfDrop(Blocks.LAPIS_BLOCK);
        this.registerForSelfDrop(Blocks.SANDSTONE);
        this.registerForSelfDrop(Blocks.CHISELED_SANDSTONE);
        this.registerForSelfDrop(Blocks.CUT_SANDSTONE);
        this.registerForSelfDrop(Blocks.NOTE_BLOCK);
        this.registerForSelfDrop(Blocks.POWERED_RAIL);
        this.registerForSelfDrop(Blocks.DETECTOR_RAIL);
        this.registerForSelfDrop(Blocks.STICKY_PISTON);
        this.registerForSelfDrop(Blocks.PISTON);
        this.registerForSelfDrop(Blocks.WHITE_WOOL);
        this.registerForSelfDrop(Blocks.ORANGE_WOOL);
        this.registerForSelfDrop(Blocks.MAGENTA_WOOL);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_WOOL);
        this.registerForSelfDrop(Blocks.YELLOW_WOOL);
        this.registerForSelfDrop(Blocks.LIME_WOOL);
        this.registerForSelfDrop(Blocks.PINK_WOOL);
        this.registerForSelfDrop(Blocks.GRAY_WOOL);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_WOOL);
        this.registerForSelfDrop(Blocks.CYAN_WOOL);
        this.registerForSelfDrop(Blocks.PURPLE_WOOL);
        this.registerForSelfDrop(Blocks.BLUE_WOOL);
        this.registerForSelfDrop(Blocks.BROWN_WOOL);
        this.registerForSelfDrop(Blocks.GREEN_WOOL);
        this.registerForSelfDrop(Blocks.RED_WOOL);
        this.registerForSelfDrop(Blocks.BLACK_WOOL);
        this.registerForSelfDrop(Blocks.DANDELION);
        this.registerForSelfDrop(Blocks.POPPY);
        this.registerForSelfDrop(Blocks.BLUE_ORCHID);
        this.registerForSelfDrop(Blocks.ALLIUM);
        this.registerForSelfDrop(Blocks.AZURE_BLUET);
        this.registerForSelfDrop(Blocks.RED_TULIP);
        this.registerForSelfDrop(Blocks.ORANGE_TULIP);
        this.registerForSelfDrop(Blocks.WHITE_TULIP);
        this.registerForSelfDrop(Blocks.PINK_TULIP);
        this.registerForSelfDrop(Blocks.OXEYE_DAISY);
        this.registerForSelfDrop(Blocks.CORNFLOWER);
        this.registerForSelfDrop(Blocks.WITHER_ROSE);
        this.registerForSelfDrop(Blocks.LILY_OF_THE_VALLEY);
        this.registerForSelfDrop(Blocks.BROWN_MUSHROOM);
        this.registerForSelfDrop(Blocks.RED_MUSHROOM);
        this.registerForSelfDrop(Blocks.GOLD_BLOCK);
        this.registerForSelfDrop(Blocks.IRON_BLOCK);
        this.registerForSelfDrop(Blocks.BRICKS);
        this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE);
        this.registerForSelfDrop(Blocks.OBSIDIAN);
        this.registerForSelfDrop(Blocks.CRYING_OBSIDIAN);
        this.registerForSelfDrop(Blocks.TORCH);
        this.registerForSelfDrop(Blocks.OAK_STAIRS);
        this.registerForSelfDrop(Blocks.REDSTONE_WIRE);
        this.registerForSelfDrop(Blocks.DIAMOND_BLOCK);
        this.registerForSelfDrop(Blocks.CRAFTING_TABLE);
        this.registerForSelfDrop(Blocks.OAK_SIGN);
        this.registerForSelfDrop(Blocks.SPRUCE_SIGN);
        this.registerForSelfDrop(Blocks.BIRCH_SIGN);
        this.registerForSelfDrop(Blocks.ACACIA_SIGN);
        this.registerForSelfDrop(Blocks.JUNGLE_SIGN);
        this.registerForSelfDrop(Blocks.DARK_OAK_SIGN);
        this.registerForSelfDrop(Blocks.LADDER);
        this.registerForSelfDrop(Blocks.RAIL);
        this.registerForSelfDrop(Blocks.COBBLESTONE_STAIRS);
        this.registerForSelfDrop(Blocks.LEVER);
        this.registerForSelfDrop(Blocks.STONE_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.OAK_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.SPRUCE_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.BIRCH_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.JUNGLE_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.ACACIA_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.DARK_OAK_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.REDSTONE_TORCH);
        this.registerForSelfDrop(Blocks.STONE_BUTTON);
        this.registerForSelfDrop(Blocks.CACTUS);
        this.registerForSelfDrop(Blocks.SUGAR_CANE);
        this.registerForSelfDrop(Blocks.JUKEBOX);
        this.registerForSelfDrop(Blocks.OAK_FENCE);
        this.registerForSelfDrop(Blocks.PUMPKIN);
        this.registerForSelfDrop(Blocks.NETHERRACK);
        this.registerForSelfDrop(Blocks.SOUL_SAND);
        this.registerForSelfDrop(Blocks.SOUL_SOIL);
        this.registerForSelfDrop(Blocks.BASALT);
        this.registerForSelfDrop(Blocks.POLISHED_BASALT);
        this.registerForSelfDrop(Blocks.SOUL_TORCH);
        this.registerForSelfDrop(Blocks.CARVED_PUMPKIN);
        this.registerForSelfDrop(Blocks.JACK_O_LANTERN);
        this.registerForSelfDrop(Blocks.REPEATER);
        this.registerForSelfDrop(Blocks.OAK_TRAPDOOR);
        this.registerForSelfDrop(Blocks.SPRUCE_TRAPDOOR);
        this.registerForSelfDrop(Blocks.BIRCH_TRAPDOOR);
        this.registerForSelfDrop(Blocks.JUNGLE_TRAPDOOR);
        this.registerForSelfDrop(Blocks.ACACIA_TRAPDOOR);
        this.registerForSelfDrop(Blocks.DARK_OAK_TRAPDOOR);
        this.registerForSelfDrop(Blocks.STONE_BRICKS);
        this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICKS);
        this.registerForSelfDrop(Blocks.CRACKED_STONE_BRICKS);
        this.registerForSelfDrop(Blocks.CHISELED_STONE_BRICKS);
        this.registerForSelfDrop(Blocks.IRON_BARS);
        this.registerForSelfDrop(Blocks.OAK_FENCE_GATE);
        this.registerForSelfDrop(Blocks.BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.STONE_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.LILY_PAD);
        this.registerForSelfDrop(Blocks.NETHER_BRICKS);
        this.registerForSelfDrop(Blocks.NETHER_BRICK_FENCE);
        this.registerForSelfDrop(Blocks.NETHER_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.CAULDRON);
        this.registerForSelfDrop(Blocks.END_STONE);
        this.registerForSelfDrop(Blocks.REDSTONE_LAMP);
        this.registerForSelfDrop(Blocks.SANDSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.TRIPWIRE_HOOK);
        this.registerForSelfDrop(Blocks.EMERALD_BLOCK);
        this.registerForSelfDrop(Blocks.SPRUCE_STAIRS);
        this.registerForSelfDrop(Blocks.BIRCH_STAIRS);
        this.registerForSelfDrop(Blocks.JUNGLE_STAIRS);
        this.registerForSelfDrop(Blocks.COBBLESTONE_WALL);
        this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE_WALL);
        this.registerForSelfDrop(Blocks.FLOWER_POT);
        this.registerForSelfDrop(Blocks.OAK_BUTTON);
        this.registerForSelfDrop(Blocks.SPRUCE_BUTTON);
        this.registerForSelfDrop(Blocks.BIRCH_BUTTON);
        this.registerForSelfDrop(Blocks.JUNGLE_BUTTON);
        this.registerForSelfDrop(Blocks.ACACIA_BUTTON);
        this.registerForSelfDrop(Blocks.DARK_OAK_BUTTON);
        this.registerForSelfDrop(Blocks.SKELETON_SKULL);
        this.registerForSelfDrop(Blocks.WITHER_SKELETON_SKULL);
        this.registerForSelfDrop(Blocks.ZOMBIE_HEAD);
        this.registerForSelfDrop(Blocks.CREEPER_HEAD);
        this.registerForSelfDrop(Blocks.DRAGON_HEAD);
        this.registerForSelfDrop(Blocks.ANVIL);
        this.registerForSelfDrop(Blocks.CHIPPED_ANVIL);
        this.registerForSelfDrop(Blocks.DAMAGED_ANVIL);
        this.registerForSelfDrop(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.COMPARATOR);
        this.registerForSelfDrop(Blocks.DAYLIGHT_DETECTOR);
        this.registerForSelfDrop(Blocks.REDSTONE_BLOCK);
        this.registerForSelfDrop(Blocks.QUARTZ_BLOCK);
        this.registerForSelfDrop(Blocks.CHISELED_QUARTZ_BLOCK);
        this.registerForSelfDrop(Blocks.QUARTZ_PILLAR);
        this.registerForSelfDrop(Blocks.QUARTZ_STAIRS);
        this.registerForSelfDrop(Blocks.ACTIVATOR_RAIL);
        this.registerForSelfDrop(Blocks.WHITE_TERRACOTTA);
        this.registerForSelfDrop(Blocks.ORANGE_TERRACOTTA);
        this.registerForSelfDrop(Blocks.MAGENTA_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.registerForSelfDrop(Blocks.YELLOW_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIME_TERRACOTTA);
        this.registerForSelfDrop(Blocks.PINK_TERRACOTTA);
        this.registerForSelfDrop(Blocks.GRAY_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.registerForSelfDrop(Blocks.CYAN_TERRACOTTA);
        this.registerForSelfDrop(Blocks.PURPLE_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BLUE_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BROWN_TERRACOTTA);
        this.registerForSelfDrop(Blocks.GREEN_TERRACOTTA);
        this.registerForSelfDrop(Blocks.RED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BLACK_TERRACOTTA);
        this.registerForSelfDrop(Blocks.ACACIA_STAIRS);
        this.registerForSelfDrop(Blocks.DARK_OAK_STAIRS);
        this.registerForSelfDrop(Blocks.SLIME_BLOCK);
        this.registerForSelfDrop(Blocks.IRON_TRAPDOOR);
        this.registerForSelfDrop(Blocks.PRISMARINE);
        this.registerForSelfDrop(Blocks.PRISMARINE_BRICKS);
        this.registerForSelfDrop(Blocks.DARK_PRISMARINE);
        this.registerForSelfDrop(Blocks.PRISMARINE_STAIRS);
        this.registerForSelfDrop(Blocks.PRISMARINE_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.DARK_PRISMARINE_STAIRS);
        this.registerForSelfDrop(Blocks.HAY_BLOCK);
        this.registerForSelfDrop(Blocks.WHITE_CARPET);
        this.registerForSelfDrop(Blocks.ORANGE_CARPET);
        this.registerForSelfDrop(Blocks.MAGENTA_CARPET);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_CARPET);
        this.registerForSelfDrop(Blocks.YELLOW_CARPET);
        this.registerForSelfDrop(Blocks.LIME_CARPET);
        this.registerForSelfDrop(Blocks.PINK_CARPET);
        this.registerForSelfDrop(Blocks.GRAY_CARPET);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_CARPET);
        this.registerForSelfDrop(Blocks.CYAN_CARPET);
        this.registerForSelfDrop(Blocks.PURPLE_CARPET);
        this.registerForSelfDrop(Blocks.BLUE_CARPET);
        this.registerForSelfDrop(Blocks.BROWN_CARPET);
        this.registerForSelfDrop(Blocks.GREEN_CARPET);
        this.registerForSelfDrop(Blocks.RED_CARPET);
        this.registerForSelfDrop(Blocks.BLACK_CARPET);
        this.registerForSelfDrop(Blocks.TERRACOTTA);
        this.registerForSelfDrop(Blocks.COAL_BLOCK);
        this.registerForSelfDrop(Blocks.RED_SANDSTONE);
        this.registerForSelfDrop(Blocks.CHISELED_RED_SANDSTONE);
        this.registerForSelfDrop(Blocks.CUT_RED_SANDSTONE);
        this.registerForSelfDrop(Blocks.RED_SANDSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.SMOOTH_STONE);
        this.registerForSelfDrop(Blocks.SMOOTH_SANDSTONE);
        this.registerForSelfDrop(Blocks.SMOOTH_QUARTZ);
        this.registerForSelfDrop(Blocks.SMOOTH_RED_SANDSTONE);
        this.registerForSelfDrop(Blocks.SPRUCE_FENCE_GATE);
        this.registerForSelfDrop(Blocks.BIRCH_FENCE_GATE);
        this.registerForSelfDrop(Blocks.JUNGLE_FENCE_GATE);
        this.registerForSelfDrop(Blocks.ACACIA_FENCE_GATE);
        this.registerForSelfDrop(Blocks.DARK_OAK_FENCE_GATE);
        this.registerForSelfDrop(Blocks.SPRUCE_FENCE);
        this.registerForSelfDrop(Blocks.BIRCH_FENCE);
        this.registerForSelfDrop(Blocks.JUNGLE_FENCE);
        this.registerForSelfDrop(Blocks.ACACIA_FENCE);
        this.registerForSelfDrop(Blocks.DARK_OAK_FENCE);
        this.registerForSelfDrop(Blocks.END_ROD);
        this.registerForSelfDrop(Blocks.PURPUR_BLOCK);
        this.registerForSelfDrop(Blocks.PURPUR_PILLAR);
        this.registerForSelfDrop(Blocks.PURPUR_STAIRS);
        this.registerForSelfDrop(Blocks.END_STONE_BRICKS);
        this.registerForSelfDrop(Blocks.MAGMA_BLOCK);
        this.registerForSelfDrop(Blocks.NETHER_WART_BLOCK);
        this.registerForSelfDrop(Blocks.RED_NETHER_BRICKS);
        this.registerForSelfDrop(Blocks.BONE_BLOCK);
        this.registerForSelfDrop(Blocks.OBSERVER);
        this.registerForSelfDrop(Blocks.TARGET);
        this.registerForSelfDrop(Blocks.WHITE_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.ORANGE_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.MAGENTA_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.YELLOW_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIME_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.PINK_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.GRAY_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.CYAN_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.PURPLE_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BLUE_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BROWN_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.GREEN_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.RED_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.BLACK_GLAZED_TERRACOTTA);
        this.registerForSelfDrop(Blocks.WHITE_CONCRETE);
        this.registerForSelfDrop(Blocks.ORANGE_CONCRETE);
        this.registerForSelfDrop(Blocks.MAGENTA_CONCRETE);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_CONCRETE);
        this.registerForSelfDrop(Blocks.YELLOW_CONCRETE);
        this.registerForSelfDrop(Blocks.LIME_CONCRETE);
        this.registerForSelfDrop(Blocks.PINK_CONCRETE);
        this.registerForSelfDrop(Blocks.GRAY_CONCRETE);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_CONCRETE);
        this.registerForSelfDrop(Blocks.CYAN_CONCRETE);
        this.registerForSelfDrop(Blocks.PURPLE_CONCRETE);
        this.registerForSelfDrop(Blocks.BLUE_CONCRETE);
        this.registerForSelfDrop(Blocks.BROWN_CONCRETE);
        this.registerForSelfDrop(Blocks.GREEN_CONCRETE);
        this.registerForSelfDrop(Blocks.RED_CONCRETE);
        this.registerForSelfDrop(Blocks.BLACK_CONCRETE);
        this.registerForSelfDrop(Blocks.WHITE_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.ORANGE_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.MAGENTA_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.YELLOW_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.LIME_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.PINK_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.GRAY_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.CYAN_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.PURPLE_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.BLUE_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.BROWN_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.GREEN_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.RED_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.BLACK_CONCRETE_POWDER);
        this.registerForSelfDrop(Blocks.KELP);
        this.registerForSelfDrop(Blocks.DRIED_KELP_BLOCK);
        this.registerForSelfDrop(Blocks.DEAD_TUBE_CORAL_BLOCK);
        this.registerForSelfDrop(Blocks.DEAD_BRAIN_CORAL_BLOCK);
        this.registerForSelfDrop(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
        this.registerForSelfDrop(Blocks.DEAD_FIRE_CORAL_BLOCK);
        this.registerForSelfDrop(Blocks.DEAD_HORN_CORAL_BLOCK);
        this.registerForSelfDrop(Blocks.CONDUIT);
        this.registerForSelfDrop(Blocks.DRAGON_EGG);
        this.registerForSelfDrop(Blocks.BAMBOO);
        this.registerForSelfDrop(Blocks.POLISHED_GRANITE_STAIRS);
        this.registerForSelfDrop(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.POLISHED_DIORITE_STAIRS);
        this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE_STAIRS);
        this.registerForSelfDrop(Blocks.END_STONE_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.STONE_STAIRS);
        this.registerForSelfDrop(Blocks.SMOOTH_SANDSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.SMOOTH_QUARTZ_STAIRS);
        this.registerForSelfDrop(Blocks.GRANITE_STAIRS);
        this.registerForSelfDrop(Blocks.ANDESITE_STAIRS);
        this.registerForSelfDrop(Blocks.RED_NETHER_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.POLISHED_ANDESITE_STAIRS);
        this.registerForSelfDrop(Blocks.DIORITE_STAIRS);
        this.registerForSelfDrop(Blocks.BRICK_WALL);
        this.registerForSelfDrop(Blocks.PRISMARINE_WALL);
        this.registerForSelfDrop(Blocks.RED_SANDSTONE_WALL);
        this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICK_WALL);
        this.registerForSelfDrop(Blocks.GRANITE_WALL);
        this.registerForSelfDrop(Blocks.STONE_BRICK_WALL);
        this.registerForSelfDrop(Blocks.NETHER_BRICK_WALL);
        this.registerForSelfDrop(Blocks.ANDESITE_WALL);
        this.registerForSelfDrop(Blocks.RED_NETHER_BRICK_WALL);
        this.registerForSelfDrop(Blocks.SANDSTONE_WALL);
        this.registerForSelfDrop(Blocks.END_STONE_BRICK_WALL);
        this.registerForSelfDrop(Blocks.DIORITE_WALL);
        this.registerForSelfDrop(Blocks.LOOM);
        this.registerForSelfDrop(Blocks.SCAFFOLDING);
        this.registerForSelfDrop(Blocks.HONEY_BLOCK);
        this.registerForSelfDrop(Blocks.HONEYCOMB_BLOCK);
        this.registerForSelfDrop(Blocks.RESPAWN_ANCHOR);
        this.registerForSelfDrop(Blocks.LODESTONE);
        this.registerForSelfDrop(Blocks.WARPED_STEM);
        this.registerForSelfDrop(Blocks.WARPED_HYPHAE);
        this.registerForSelfDrop(Blocks.WARPED_NYLIUM);
        this.registerForSelfDrop(Blocks.WARPED_FUNGUS);
        this.registerForSelfDrop(Blocks.WARPED_WART_BLOCK);
        this.registerForSelfDrop(Blocks.WARPED_ROOTS);
        this.registerForSelfDrop(Blocks.CRIMSON_STEM);
        this.registerForSelfDrop(Blocks.CRIMSON_HYPHAE);
        this.registerForSelfDrop(Blocks.CRIMSON_NYLIUM);
        this.registerForSelfDrop(Blocks.CRIMSON_FUNGUS);
        this.registerForSelfDrop(Blocks.SHROOMLIGHT);
        this.registerForSelfDrop(Blocks.CRIMSON_ROOTS);
        this.registerForSelfDrop(Blocks.CRIMSON_PLANKS);
        this.registerForSelfDrop(Blocks.WARPED_PLANKS);
        this.registerForSelfDrop(Blocks.WARPED_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.WARPED_FENCE);
        this.registerForSelfDrop(Blocks.WARPED_TRAPDOOR);
        this.registerForSelfDrop(Blocks.WARPED_FENCE_GATE);
        this.registerForSelfDrop(Blocks.WARPED_STAIRS);
        this.registerForSelfDrop(Blocks.WARPED_BUTTON);
        this.registerForSelfDrop(Blocks.WARPED_SIGN);
        this.registerForSelfDrop(Blocks.CRIMSON_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.CRIMSON_FENCE);
        this.registerForSelfDrop(Blocks.CRIMSON_TRAPDOOR);
        this.registerForSelfDrop(Blocks.CRIMSON_FENCE_GATE);
        this.registerForSelfDrop(Blocks.CRIMSON_STAIRS);
        this.registerForSelfDrop(Blocks.CRIMSON_BUTTON);
        this.registerForSelfDrop(Blocks.CRIMSON_SIGN);
        this.registerForSelfDrop(Blocks.NETHERITE_BLOCK);
        this.registerForSelfDrop(Blocks.ANCIENT_DEBRIS);
        this.registerForSelfDrop(Blocks.BLACKSTONE);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICKS);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        this.registerForSelfDrop(Blocks.BLACKSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.BLACKSTONE_WALL);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        this.registerForSelfDrop(Blocks.CHISELED_POLISHED_BLACKSTONE);
        this.registerForSelfDrop(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_STAIRS);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BUTTON);
        this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_WALL);
        this.registerForSelfDrop(Blocks.CHISELED_NETHER_BRICKS);
        this.registerForSelfDrop(Blocks.CRACKED_NETHER_BRICKS);
        this.registerForSelfDrop(Blocks.QUARTZ_BRICKS);
        this.registerForSelfDrop(Blocks.CHAIN);
        this.register(Blocks.FARMLAND, Blocks.DIRT);
        this.register(Blocks.TRIPWIRE, Items.STRING);
        this.register(Blocks.GRASS_PATH, Blocks.DIRT);
        this.register(Blocks.KELP_PLANT, Blocks.KELP);
        this.register(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
        this.registerWithFunction(Blocks.STONE, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.COBBLESTONE));
        this.registerWithFunction(Blocks.GRASS_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DIRT));
        this.registerWithFunction(Blocks.PODZOL, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DIRT));
        this.registerWithFunction(Blocks.MYCELIUM, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DIRT));
        this.registerWithFunction(Blocks.TUBE_CORAL_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DEAD_TUBE_CORAL_BLOCK));
        this.registerWithFunction(Blocks.BRAIN_CORAL_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DEAD_BRAIN_CORAL_BLOCK));
        this.registerWithFunction(Blocks.BUBBLE_CORAL_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
        this.registerWithFunction(Blocks.FIRE_CORAL_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DEAD_FIRE_CORAL_BLOCK));
        this.registerWithFunction(Blocks.HORN_CORAL_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.DEAD_HORN_CORAL_BLOCK));
        this.registerWithFunction(Blocks.BOOKSHELF, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Items.BOOK, ConstantLootTableRange.create(3)));
        this.registerWithFunction(Blocks.CLAY, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Items.CLAY_BALL, ConstantLootTableRange.create(4)));
        this.registerWithFunction(Blocks.ENDER_CHEST, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Blocks.OBSIDIAN, ConstantLootTableRange.create(8)));
        this.registerWithFunction(Blocks.SNOW_BLOCK, arg -> BlockLootTableGenerator.createForBlockWithItemDrops(arg, Items.SNOWBALL, ConstantLootTableRange.create(4)));
        this.register(Blocks.CHORUS_PLANT, BlockLootTableGenerator.create(Items.CHORUS_FRUIT, UniformLootTableRange.between(0.0f, 1.0f)));
        this.registerForPottedPlant(Blocks.POTTED_OAK_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_SPRUCE_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_BIRCH_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_JUNGLE_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_ACACIA_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_DARK_OAK_SAPLING);
        this.registerForPottedPlant(Blocks.POTTED_FERN);
        this.registerForPottedPlant(Blocks.POTTED_DANDELION);
        this.registerForPottedPlant(Blocks.POTTED_POPPY);
        this.registerForPottedPlant(Blocks.POTTED_BLUE_ORCHID);
        this.registerForPottedPlant(Blocks.POTTED_ALLIUM);
        this.registerForPottedPlant(Blocks.POTTED_AZURE_BLUET);
        this.registerForPottedPlant(Blocks.POTTED_RED_TULIP);
        this.registerForPottedPlant(Blocks.POTTED_ORANGE_TULIP);
        this.registerForPottedPlant(Blocks.POTTED_WHITE_TULIP);
        this.registerForPottedPlant(Blocks.POTTED_PINK_TULIP);
        this.registerForPottedPlant(Blocks.POTTED_OXEYE_DAISY);
        this.registerForPottedPlant(Blocks.POTTED_CORNFLOWER);
        this.registerForPottedPlant(Blocks.POTTED_LILY_OF_THE_VALLEY);
        this.registerForPottedPlant(Blocks.POTTED_WITHER_ROSE);
        this.registerForPottedPlant(Blocks.POTTED_RED_MUSHROOM);
        this.registerForPottedPlant(Blocks.POTTED_BROWN_MUSHROOM);
        this.registerForPottedPlant(Blocks.POTTED_DEAD_BUSH);
        this.registerForPottedPlant(Blocks.POTTED_CACTUS);
        this.registerForPottedPlant(Blocks.POTTED_BAMBOO);
        this.registerForPottedPlant(Blocks.POTTED_CRIMSON_FUNGUS);
        this.registerForPottedPlant(Blocks.POTTED_WARPED_FUNGUS);
        this.registerForPottedPlant(Blocks.POTTED_CRIMSON_ROOTS);
        this.registerForPottedPlant(Blocks.POTTED_WARPED_ROOTS);
        this.registerWithFunction(Blocks.ACACIA_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.BIRCH_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.COBBLESTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.DARK_OAK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.DARK_PRISMARINE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.JUNGLE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.NETHER_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.OAK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.PETRIFIED_OAK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.PRISMARINE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.PURPUR_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.QUARTZ_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.CUT_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SPRUCE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.STONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SMOOTH_STONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.POLISHED_GRANITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.POLISHED_DIORITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.END_STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.GRANITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.ANDESITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.DIORITE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.CRIMSON_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.WARPED_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.BLACKSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
        this.registerWithFunction(Blocks.ACACIA_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.BIRCH_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.DARK_OAK_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.IRON_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.JUNGLE_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.OAK_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.SPRUCE_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.WARPED_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.CRIMSON_DOOR, BlockLootTableGenerator::method_24817);
        this.registerWithFunction(Blocks.BLACK_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.BLUE_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.BROWN_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.CYAN_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.GRAY_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.GREEN_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.LIGHT_BLUE_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.LIGHT_GRAY_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.LIME_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.MAGENTA_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.PURPLE_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.ORANGE_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.PINK_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.RED_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.WHITE_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.YELLOW_BED, arg -> BlockLootTableGenerator.createForMultiblock(arg, BedBlock.PART, BedPart.HEAD));
        this.registerWithFunction(Blocks.LILAC, arg -> BlockLootTableGenerator.createForMultiblock(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.registerWithFunction(Blocks.SUNFLOWER, arg -> BlockLootTableGenerator.createForMultiblock(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.registerWithFunction(Blocks.PEONY, arg -> BlockLootTableGenerator.createForMultiblock(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.registerWithFunction(Blocks.ROSE_BUSH, arg -> BlockLootTableGenerator.createForMultiblock(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.register(Blocks.TNT, LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(Blocks.TNT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Blocks.TNT).withCondition(BlockStatePropertyLootCondition.builder(Blocks.TNT).method_22584(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))))));
        this.registerWithFunction(Blocks.COCOA, arg -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(Items.COCOA_BEANS).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2))))))));
        this.registerWithFunction(Blocks.SEA_PICKLE, arg -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(Blocks.SEA_PICKLE, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(4)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4))))))));
        this.registerWithFunction(Blocks.COMPOSTER, arg -> LootTable.builder().withPool(LootPool.builder().withEntry((LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(Items.COMPOSTER)))).withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.BONE_MEAL)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))));
        this.registerWithFunction(Blocks.BEACON, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.BREWING_STAND, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.CHEST, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.DISPENSER, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.DROPPER, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.ENCHANTING_TABLE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.FURNACE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.HOPPER, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.TRAPPED_CHEST, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.SMOKER, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.BLAST_FURNACE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.BARREL, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.CARTOGRAPHY_TABLE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.FLETCHING_TABLE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.GRINDSTONE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.LECTERN, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.SMITHING_TABLE, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.STONECUTTER, BlockLootTableGenerator::createForNameableContainer);
        this.registerWithFunction(Blocks.BELL, BlockLootTableGenerator::create);
        this.registerWithFunction(Blocks.LANTERN, BlockLootTableGenerator::create);
        this.registerWithFunction(Blocks.SOUL_LANTERN, BlockLootTableGenerator::create);
        this.registerWithFunction(Blocks.SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.BLACK_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.BLUE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.BROWN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.CYAN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.GRAY_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.GREEN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.LIME_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.MAGENTA_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.ORANGE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.PINK_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.PURPLE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.RED_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.WHITE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.YELLOW_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
        this.registerWithFunction(Blocks.BLACK_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.BLUE_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.BROWN_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.CYAN_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.GRAY_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.GREEN_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.LIGHT_BLUE_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.LIGHT_GRAY_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.LIME_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.MAGENTA_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.ORANGE_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.PINK_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.PURPLE_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.RED_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.WHITE_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.YELLOW_BANNER, BlockLootTableGenerator::createForBanner);
        this.registerWithFunction(Blocks.PLAYER_HEAD, arg -> LootTable.builder().withPool(BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(arg).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("SkullOwner", "SkullOwner"))))));
        this.registerWithFunction(Blocks.BEE_NEST, BlockLootTableGenerator::createForBeeNest);
        this.registerWithFunction(Blocks.BEEHIVE, BlockLootTableGenerator::createForBeehive);
        this.registerWithFunction(Blocks.BIRCH_LEAVES, arg -> BlockLootTableGenerator.createForLeaves(arg, Blocks.BIRCH_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
        this.registerWithFunction(Blocks.ACACIA_LEAVES, arg -> BlockLootTableGenerator.createForLeaves(arg, Blocks.ACACIA_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
        this.registerWithFunction(Blocks.JUNGLE_LEAVES, arg -> BlockLootTableGenerator.createForLeaves(arg, Blocks.JUNGLE_SAPLING, JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES));
        this.registerWithFunction(Blocks.SPRUCE_LEAVES, arg -> BlockLootTableGenerator.createForLeaves(arg, Blocks.SPRUCE_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
        this.registerWithFunction(Blocks.OAK_LEAVES, arg -> BlockLootTableGenerator.createForOakLeaves(arg, Blocks.OAK_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
        this.registerWithFunction(Blocks.DARK_OAK_LEAVES, arg -> BlockLootTableGenerator.createForOakLeaves(arg, Blocks.DARK_OAK_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
        BlockStatePropertyLootCondition.Builder lv = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS).method_22584(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
        this.register(Blocks.BEETROOTS, BlockLootTableGenerator.createForCrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, lv));
        BlockStatePropertyLootCondition.Builder lv2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT).method_22584(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
        this.register(Blocks.WHEAT, BlockLootTableGenerator.createForCrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, lv2));
        BlockStatePropertyLootCondition.Builder lv3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS).method_22584(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
        this.register(Blocks.CARROTS, BlockLootTableGenerator.addExplosionDecayLootFunction(Blocks.CARROTS, LootTable.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.CARROT))).withPool(LootPool.builder().withCondition(lv3).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.CARROT).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3))))));
        BlockStatePropertyLootCondition.Builder lv4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES).method_22584(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
        this.register(Blocks.POTATOES, BlockLootTableGenerator.addExplosionDecayLootFunction(Blocks.POTATOES, LootTable.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.POTATO))).withPool(LootPool.builder().withCondition(lv4).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.POTATO).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))).withPool(LootPool.builder().withCondition(lv4).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.POISONOUS_POTATO).withCondition(RandomChanceLootCondition.builder(0.02f))))));
        this.registerWithFunction(Blocks.SWEET_BERRY_BUSH, arg -> BlockLootTableGenerator.addExplosionDecayLootFunction(arg, LootTable.builder().withPool(LootPool.builder().withCondition(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))).withEntry(ItemEntry.builder(Items.SWEET_BERRIES)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withPool(LootPool.builder().withCondition(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))).withEntry(ItemEntry.builder(Items.SWEET_BERRIES)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.registerWithFunction(Blocks.BROWN_MUSHROOM_BLOCK, arg -> BlockLootTableGenerator.createForLargeMushroomBlock(arg, Blocks.BROWN_MUSHROOM));
        this.registerWithFunction(Blocks.RED_MUSHROOM_BLOCK, arg -> BlockLootTableGenerator.createForLargeMushroomBlock(arg, Blocks.RED_MUSHROOM));
        this.registerWithFunction(Blocks.COAL_ORE, arg -> BlockLootTableGenerator.createForOreWithSingleItemDrop(arg, Items.COAL));
        this.registerWithFunction(Blocks.EMERALD_ORE, arg -> BlockLootTableGenerator.createForOreWithSingleItemDrop(arg, Items.EMERALD));
        this.registerWithFunction(Blocks.NETHER_QUARTZ_ORE, arg -> BlockLootTableGenerator.createForOreWithSingleItemDrop(arg, Items.QUARTZ));
        this.registerWithFunction(Blocks.DIAMOND_ORE, arg -> BlockLootTableGenerator.createForOreWithSingleItemDrop(arg, Items.DIAMOND));
        this.registerWithFunction(Blocks.NETHER_GOLD_ORE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.GOLD_NUGGET).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 6.0f)))).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))));
        this.registerWithFunction(Blocks.LAPIS_ORE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.LAPIS_LAZULI).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 9.0f)))).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))));
        this.registerWithFunction(Blocks.COBWEB, arg -> BlockLootTableGenerator.createForNeedingSilkTouchShears(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(Items.STRING))));
        this.registerWithFunction(Blocks.DEAD_BUSH, arg -> BlockLootTableGenerator.createForNeedingShears(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0f, 2.0f))))));
        this.registerWithFunction(Blocks.NETHER_SPROUTS, BlockLootTableGenerator::createForBlockNeedingShears);
        this.registerWithFunction(Blocks.SEAGRASS, BlockLootTableGenerator::createForBlockNeedingShears);
        this.registerWithFunction(Blocks.VINE, BlockLootTableGenerator::createForBlockNeedingShears);
        this.register(Blocks.TALL_SEAGRASS, BlockLootTableGenerator.createForBlockNeedingShears(Blocks.SEAGRASS));
        this.registerWithFunction(Blocks.LARGE_FERN, arg -> BlockLootTableGenerator.createForNeedingShears(Blocks.FERN, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(Items.WHEAT_SEEDS))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).withCondition(RandomChanceLootCondition.builder(0.125f))));
        this.register(Blocks.TALL_GRASS, BlockLootTableGenerator.createForNeedingShears(Blocks.GRASS, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(Blocks.TALL_GRASS, ItemEntry.builder(Items.WHEAT_SEEDS))).withCondition(BlockStatePropertyLootCondition.builder(Blocks.TALL_GRASS).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).withCondition(RandomChanceLootCondition.builder(0.125f))));
        this.registerWithFunction(Blocks.MELON_STEM, arg -> BlockLootTableGenerator.createForCropStem(arg, Items.MELON_SEEDS));
        this.registerWithFunction(Blocks.ATTACHED_MELON_STEM, arg -> BlockLootTableGenerator.createForAttachedCropStem(arg, Items.MELON_SEEDS));
        this.registerWithFunction(Blocks.PUMPKIN_STEM, arg -> BlockLootTableGenerator.createForCropStem(arg, Items.PUMPKIN_SEEDS));
        this.registerWithFunction(Blocks.ATTACHED_PUMPKIN_STEM, arg -> BlockLootTableGenerator.createForAttachedCropStem(arg, Items.PUMPKIN_SEEDS));
        this.registerWithFunction(Blocks.CHORUS_FLOWER, arg -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(arg))).withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)))));
        this.registerWithFunction(Blocks.FERN, BlockLootTableGenerator::createForTallGrass);
        this.registerWithFunction(Blocks.GRASS, BlockLootTableGenerator::createForTallGrass);
        this.registerWithFunction(Blocks.GLOWSTONE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4))))));
        this.registerWithFunction(Blocks.MELON, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.MELON_SLICE).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0f, 7.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9))))));
        this.registerWithFunction(Blocks.REDSTONE_ORE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.REDSTONE).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 5.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.registerWithFunction(Blocks.SEA_LANTERN, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addExplosionDecayLootFunction(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.PRISMARINE_CRYSTALS).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5))))));
        this.registerWithFunction(Blocks.NETHER_WART, arg -> LootTable.builder().withPool(BlockLootTableGenerator.addExplosionDecayLootFunction(arg, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_WART).withFunction((LootFunction.Builder)SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))).withFunction((LootFunction.Builder)ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))))));
        this.registerWithFunction(Blocks.SNOW, arg -> LootTable.builder().withPool(LootPool.builder().withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)).withEntry(AlternativeEntry.builder(new LootEntry.Builder[]{AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7))), ItemEntry.builder(Items.SNOWBALL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))}).withCondition(DOESNT_NEED_SILK_TOUCH), AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Blocks.SNOW).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))).withCondition(BlockStatePropertyLootCondition.builder(arg).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))), ItemEntry.builder(Blocks.SNOW_BLOCK)})}))));
        this.registerWithFunction(Blocks.GRAVEL, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.FLINT).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1f, 0.14285715f, 0.25f, 1.0f))).withChild(ItemEntry.builder(arg)))));
        this.registerWithFunction(Blocks.CAMPFIRE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(Items.CHARCOAL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))));
        this.registerWithFunction(Blocks.GILDED_BLACKSTONE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ((LeafEntry.Builder)((LootEntry.Builder)ItemEntry.builder(Items.GOLD_NUGGET).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 5.0f)))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1f, 0.14285715f, 0.25f, 1.0f))).withChild(ItemEntry.builder(arg)))));
        this.registerWithFunction(Blocks.SOUL_CAMPFIRE, arg -> BlockLootTableGenerator.createForNeedingSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionLootCondition(arg, ItemEntry.builder(Items.SOUL_SOIL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))));
        this.registerForNeedingSilkTouch(Blocks.GLASS);
        this.registerForNeedingSilkTouch(Blocks.WHITE_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.ORANGE_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.YELLOW_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.LIME_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.PINK_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.GRAY_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.CYAN_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.PURPLE_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.BLUE_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.BROWN_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.GREEN_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.RED_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.BLACK_STAINED_GLASS);
        this.registerForNeedingSilkTouch(Blocks.GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
        this.registerForNeedingSilkTouch(Blocks.ICE);
        this.registerForNeedingSilkTouch(Blocks.PACKED_ICE);
        this.registerForNeedingSilkTouch(Blocks.BLUE_ICE);
        this.registerForNeedingSilkTouch(Blocks.TURTLE_EGG);
        this.registerForNeedingSilkTouch(Blocks.MUSHROOM_STEM);
        this.registerForNeedingSilkTouch(Blocks.DEAD_TUBE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.DEAD_BRAIN_CORAL);
        this.registerForNeedingSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.DEAD_FIRE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.DEAD_HORN_CORAL);
        this.registerForNeedingSilkTouch(Blocks.TUBE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.BRAIN_CORAL);
        this.registerForNeedingSilkTouch(Blocks.BUBBLE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.FIRE_CORAL);
        this.registerForNeedingSilkTouch(Blocks.HORN_CORAL);
        this.registerForNeedingSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.TUBE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.BRAIN_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.BUBBLE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.FIRE_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.HORN_CORAL_FAN);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        this.registerForNeedingSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        this.method_26000(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
        this.method_26000(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
        this.register(Blocks.CAKE, BlockLootTableGenerator.createEmpty());
        this.register(Blocks.FROSTED_ICE, BlockLootTableGenerator.createEmpty());
        this.register(Blocks.SPAWNER, BlockLootTableGenerator.createEmpty());
        this.register(Blocks.FIRE, BlockLootTableGenerator.createEmpty());
        this.register(Blocks.SOUL_FIRE, BlockLootTableGenerator.createEmpty());
        HashSet set = Sets.newHashSet();
        for (Block lv5 : Registry.BLOCK) {
            Identifier lv6 = lv5.getLootTableId();
            if (lv6 == LootTables.EMPTY || !set.add(lv6)) continue;
            LootTable.Builder lv7 = this.lootTables.remove(lv6);
            if (lv7 == null) {
                throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", lv6, Registry.BLOCK.getId(lv5)));
            }
            biConsumer.accept(lv6, lv7);
        }
        if (!this.lootTables.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
        }
    }

    private void method_26000(Block arg, Block arg2) {
        LootTable.Builder lv = BlockLootTableGenerator.createForNeedingSilkTouchShears(arg, ItemEntry.builder(arg).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.33f, 0.55f, 0.77f, 1.0f)));
        this.register(arg, lv);
        this.register(arg2, lv);
    }

    public static LootTable.Builder method_24817(Block arg) {
        return BlockLootTableGenerator.createForMultiblock(arg, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    public void registerForPottedPlant(Block arg2) {
        this.registerWithFunction(arg2, arg -> BlockLootTableGenerator.createForPottedPlant(((FlowerPotBlock)arg).getContent()));
    }

    public void registerForNeedingSilkTouch(Block arg, Block arg2) {
        this.register(arg, BlockLootTableGenerator.createForNeedingSilkTouch(arg2));
    }

    public void register(Block arg, ItemConvertible arg2) {
        this.register(arg, BlockLootTableGenerator.create(arg2));
    }

    public void registerForNeedingSilkTouch(Block arg) {
        this.registerForNeedingSilkTouch(arg, arg);
    }

    public void registerForSelfDrop(Block arg) {
        this.register(arg, arg);
    }

    private void registerWithFunction(Block arg, Function<Block, LootTable.Builder> function) {
        this.register(arg, function.apply(arg));
    }

    private void register(Block arg, LootTable.Builder arg2) {
        this.lootTables.put(arg.getLootTableId(), arg2);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

