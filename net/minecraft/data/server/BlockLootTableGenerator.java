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
import net.minecraft.class_5341;
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
    private static final class_5341.Builder WITH_SILK_TOUCH = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))));
    private static final class_5341.Builder WITHOUT_SILK_TOUCH = WITH_SILK_TOUCH.invert();
    private static final class_5341.Builder WITH_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    private static final class_5341.Builder WITH_SILK_TOUCH_OR_SHEARS = WITH_SHEARS.or(WITH_SILK_TOUCH);
    private static final class_5341.Builder WITHOUT_SILK_TOUCH_NOR_SHEARS = WITH_SILK_TOUCH_OR_SHEARS.invert();
    private static final Set<Item> EXPLOSION_IMMUNE = (Set)Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemConvertible::asItem).collect(ImmutableSet.toImmutableSet());
    private static final float[] SAPLING_DROP_CHANCE = new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f};
    private static final float[] JUNGLE_SAPLING_DROP_CHANCE = new float[]{0.025f, 0.027777778f, 0.03125f, 0.041666668f, 0.1f};
    private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

    private static <T> T applyExplosionDecay(ItemConvertible arg, LootFunctionConsumingBuilder<T> arg2) {
        if (!EXPLOSION_IMMUNE.contains(arg.asItem())) {
            return arg2.apply(ExplosionDecayLootFunction.builder());
        }
        return arg2.getThis();
    }

    private static <T> T addSurvivesExplosionCondition(ItemConvertible arg, LootConditionConsumingBuilder<T> arg2) {
        if (!EXPLOSION_IMMUNE.contains(arg.asItem())) {
            return arg2.conditionally(SurvivesExplosionLootCondition.builder());
        }
        return arg2.getThis();
    }

    private static LootTable.Builder drops(ItemConvertible arg) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder drops(Block arg, class_5341.Builder arg2, LootEntry.Builder<?> arg3) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(((LeafEntry.Builder)ItemEntry.builder(arg).conditionally(arg2)).alternatively(arg3)));
    }

    private static LootTable.Builder dropsWithSilkTouch(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.drops(arg, WITH_SILK_TOUCH, arg2);
    }

    private static LootTable.Builder dropsWithShears(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.drops(arg, WITH_SHEARS, arg2);
    }

    private static LootTable.Builder dropsWithSilkTouchOrShears(Block arg, LootEntry.Builder<?> arg2) {
        return BlockLootTableGenerator.drops(arg, WITH_SILK_TOUCH_OR_SHEARS, arg2);
    }

    private static LootTable.Builder drops(Block arg, ItemConvertible arg2) {
        return BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(arg2)));
    }

    private static LootTable.Builder drops(ItemConvertible arg, LootTableRange arg2) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(arg).apply(SetCountLootFunction.builder(arg2)))));
    }

    private static LootTable.Builder drops(Block arg, ItemConvertible arg2, LootTableRange arg3) {
        return BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(arg2).apply(SetCountLootFunction.builder(arg3))));
    }

    private static LootTable.Builder dropsWithSilkTouch(ItemConvertible arg) {
        return LootTable.builder().pool(LootPool.builder().conditionally(WITH_SILK_TOUCH).rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(arg)));
    }

    private static LootTable.Builder pottedPlantDrops(ItemConvertible arg) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Blocks.FLOWER_POT)))).pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder slabDrops(Block arg) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(arg).apply((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block arg, Property<T> arg2, T comparable) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)ItemEntry.builder(arg).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(arg2, comparable))))));
    }

    private static LootTable.Builder nameableContainerDrops(Block arg) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)ItemEntry.builder(arg).apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))));
    }

    private static LootTable.Builder shulkerBoxDrops(Block arg) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Lock", "BlockEntityTag.Lock").withOperation("LootTable", "BlockEntityTag.LootTable").withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed"))).apply(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS))))));
    }

    private static LootTable.Builder bannerDrops(Block arg) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(arg).apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    private static LootTable.Builder beeNestDrops(Block arg) {
        return LootTable.builder().pool(LootPool.builder().conditionally(WITH_SILK_TOUCH).rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(arg).apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).apply(CopyStateFunction.getBuilder(arg).method_21898(BeehiveBlock.HONEY_LEVEL))));
    }

    private static LootTable.Builder beehiveDrops(Block arg) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(((LootEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).conditionally(WITH_SILK_TOUCH)).apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).apply(CopyStateFunction.getBuilder(arg).method_21898(BeehiveBlock.HONEY_LEVEL))).alternatively(ItemEntry.builder(arg))));
    }

    private static LootTable.Builder oreDrops(Block arg, Item arg2) {
        return BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(arg2).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder mushroomBlockDrops(Block arg, ItemConvertible arg2) {
        return BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)ItemEntry.builder(arg2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0f, 2.0f)))).apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))));
    }

    private static LootTable.Builder grassDrops(Block arg) {
        return BlockLootTableGenerator.dropsWithShears(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(RandomChanceLootCondition.builder(0.125f))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    private static LootTable.Builder cropStemDrops(Block arg, Item arg2) {
        return LootTable.builder().pool(BlockLootTableGenerator.applyExplosionDecay(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg2).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, false))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, true))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6))))).apply((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))))));
    }

    private static LootTable.Builder attachedCropStemDrops(Block arg, Item arg2) {
        return LootTable.builder().pool(BlockLootTableGenerator.applyExplosionDecay(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)ItemEntry.builder(arg2).apply(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f))))));
    }

    private static LootTable.Builder dropsWithShears(ItemConvertible arg) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).conditionally(WITH_SHEARS).with(ItemEntry.builder(arg)));
    }

    private static LootTable.Builder leavesDrop(Block arg, Block arg2, float ... fs) {
        return BlockLootTableGenerator.dropsWithSilkTouchOrShears(arg, ((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(arg2))).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, fs))).pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS).with((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))))).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f))));
    }

    private static LootTable.Builder oakLeavesDrop(Block arg, Block arg2, float ... fs) {
        return BlockLootTableGenerator.leavesDrop(arg, arg2, fs).pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS).with((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(Items.APPLE))).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f))));
    }

    private static LootTable.Builder cropDrops(Block arg, Item arg2, Item arg3, class_5341.Builder arg4) {
        return BlockLootTableGenerator.applyExplosionDecay(arg, LootTable.builder().pool(LootPool.builder().with(((LeafEntry.Builder)ItemEntry.builder(arg2).conditionally(arg4)).alternatively(ItemEntry.builder(arg3)))).pool(LootPool.builder().conditionally(arg4).with((LootEntry.Builder<?>)ItemEntry.builder(arg3).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))));
    }

    public static LootTable.Builder dropsNothing() {
        return LootTable.builder();
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
        this.addDrop(Blocks.GRANITE);
        this.addDrop(Blocks.POLISHED_GRANITE);
        this.addDrop(Blocks.DIORITE);
        this.addDrop(Blocks.POLISHED_DIORITE);
        this.addDrop(Blocks.ANDESITE);
        this.addDrop(Blocks.POLISHED_ANDESITE);
        this.addDrop(Blocks.DIRT);
        this.addDrop(Blocks.COARSE_DIRT);
        this.addDrop(Blocks.COBBLESTONE);
        this.addDrop(Blocks.OAK_PLANKS);
        this.addDrop(Blocks.SPRUCE_PLANKS);
        this.addDrop(Blocks.BIRCH_PLANKS);
        this.addDrop(Blocks.JUNGLE_PLANKS);
        this.addDrop(Blocks.ACACIA_PLANKS);
        this.addDrop(Blocks.DARK_OAK_PLANKS);
        this.addDrop(Blocks.OAK_SAPLING);
        this.addDrop(Blocks.SPRUCE_SAPLING);
        this.addDrop(Blocks.BIRCH_SAPLING);
        this.addDrop(Blocks.JUNGLE_SAPLING);
        this.addDrop(Blocks.ACACIA_SAPLING);
        this.addDrop(Blocks.DARK_OAK_SAPLING);
        this.addDrop(Blocks.SAND);
        this.addDrop(Blocks.RED_SAND);
        this.addDrop(Blocks.GOLD_ORE);
        this.addDrop(Blocks.IRON_ORE);
        this.addDrop(Blocks.OAK_LOG);
        this.addDrop(Blocks.SPRUCE_LOG);
        this.addDrop(Blocks.BIRCH_LOG);
        this.addDrop(Blocks.JUNGLE_LOG);
        this.addDrop(Blocks.ACACIA_LOG);
        this.addDrop(Blocks.DARK_OAK_LOG);
        this.addDrop(Blocks.STRIPPED_SPRUCE_LOG);
        this.addDrop(Blocks.STRIPPED_BIRCH_LOG);
        this.addDrop(Blocks.STRIPPED_JUNGLE_LOG);
        this.addDrop(Blocks.STRIPPED_ACACIA_LOG);
        this.addDrop(Blocks.STRIPPED_DARK_OAK_LOG);
        this.addDrop(Blocks.STRIPPED_OAK_LOG);
        this.addDrop(Blocks.STRIPPED_WARPED_STEM);
        this.addDrop(Blocks.STRIPPED_CRIMSON_STEM);
        this.addDrop(Blocks.OAK_WOOD);
        this.addDrop(Blocks.SPRUCE_WOOD);
        this.addDrop(Blocks.BIRCH_WOOD);
        this.addDrop(Blocks.JUNGLE_WOOD);
        this.addDrop(Blocks.ACACIA_WOOD);
        this.addDrop(Blocks.DARK_OAK_WOOD);
        this.addDrop(Blocks.STRIPPED_OAK_WOOD);
        this.addDrop(Blocks.STRIPPED_SPRUCE_WOOD);
        this.addDrop(Blocks.STRIPPED_BIRCH_WOOD);
        this.addDrop(Blocks.STRIPPED_JUNGLE_WOOD);
        this.addDrop(Blocks.STRIPPED_ACACIA_WOOD);
        this.addDrop(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.addDrop(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.addDrop(Blocks.STRIPPED_WARPED_HYPHAE);
        this.addDrop(Blocks.SPONGE);
        this.addDrop(Blocks.WET_SPONGE);
        this.addDrop(Blocks.LAPIS_BLOCK);
        this.addDrop(Blocks.SANDSTONE);
        this.addDrop(Blocks.CHISELED_SANDSTONE);
        this.addDrop(Blocks.CUT_SANDSTONE);
        this.addDrop(Blocks.NOTE_BLOCK);
        this.addDrop(Blocks.POWERED_RAIL);
        this.addDrop(Blocks.DETECTOR_RAIL);
        this.addDrop(Blocks.STICKY_PISTON);
        this.addDrop(Blocks.PISTON);
        this.addDrop(Blocks.WHITE_WOOL);
        this.addDrop(Blocks.ORANGE_WOOL);
        this.addDrop(Blocks.MAGENTA_WOOL);
        this.addDrop(Blocks.LIGHT_BLUE_WOOL);
        this.addDrop(Blocks.YELLOW_WOOL);
        this.addDrop(Blocks.LIME_WOOL);
        this.addDrop(Blocks.PINK_WOOL);
        this.addDrop(Blocks.GRAY_WOOL);
        this.addDrop(Blocks.LIGHT_GRAY_WOOL);
        this.addDrop(Blocks.CYAN_WOOL);
        this.addDrop(Blocks.PURPLE_WOOL);
        this.addDrop(Blocks.BLUE_WOOL);
        this.addDrop(Blocks.BROWN_WOOL);
        this.addDrop(Blocks.GREEN_WOOL);
        this.addDrop(Blocks.RED_WOOL);
        this.addDrop(Blocks.BLACK_WOOL);
        this.addDrop(Blocks.DANDELION);
        this.addDrop(Blocks.POPPY);
        this.addDrop(Blocks.BLUE_ORCHID);
        this.addDrop(Blocks.ALLIUM);
        this.addDrop(Blocks.AZURE_BLUET);
        this.addDrop(Blocks.RED_TULIP);
        this.addDrop(Blocks.ORANGE_TULIP);
        this.addDrop(Blocks.WHITE_TULIP);
        this.addDrop(Blocks.PINK_TULIP);
        this.addDrop(Blocks.OXEYE_DAISY);
        this.addDrop(Blocks.CORNFLOWER);
        this.addDrop(Blocks.WITHER_ROSE);
        this.addDrop(Blocks.LILY_OF_THE_VALLEY);
        this.addDrop(Blocks.BROWN_MUSHROOM);
        this.addDrop(Blocks.RED_MUSHROOM);
        this.addDrop(Blocks.GOLD_BLOCK);
        this.addDrop(Blocks.IRON_BLOCK);
        this.addDrop(Blocks.BRICKS);
        this.addDrop(Blocks.MOSSY_COBBLESTONE);
        this.addDrop(Blocks.OBSIDIAN);
        this.addDrop(Blocks.CRYING_OBSIDIAN);
        this.addDrop(Blocks.TORCH);
        this.addDrop(Blocks.OAK_STAIRS);
        this.addDrop(Blocks.REDSTONE_WIRE);
        this.addDrop(Blocks.DIAMOND_BLOCK);
        this.addDrop(Blocks.CRAFTING_TABLE);
        this.addDrop(Blocks.OAK_SIGN);
        this.addDrop(Blocks.SPRUCE_SIGN);
        this.addDrop(Blocks.BIRCH_SIGN);
        this.addDrop(Blocks.ACACIA_SIGN);
        this.addDrop(Blocks.JUNGLE_SIGN);
        this.addDrop(Blocks.DARK_OAK_SIGN);
        this.addDrop(Blocks.LADDER);
        this.addDrop(Blocks.RAIL);
        this.addDrop(Blocks.COBBLESTONE_STAIRS);
        this.addDrop(Blocks.LEVER);
        this.addDrop(Blocks.STONE_PRESSURE_PLATE);
        this.addDrop(Blocks.OAK_PRESSURE_PLATE);
        this.addDrop(Blocks.SPRUCE_PRESSURE_PLATE);
        this.addDrop(Blocks.BIRCH_PRESSURE_PLATE);
        this.addDrop(Blocks.JUNGLE_PRESSURE_PLATE);
        this.addDrop(Blocks.ACACIA_PRESSURE_PLATE);
        this.addDrop(Blocks.DARK_OAK_PRESSURE_PLATE);
        this.addDrop(Blocks.REDSTONE_TORCH);
        this.addDrop(Blocks.STONE_BUTTON);
        this.addDrop(Blocks.CACTUS);
        this.addDrop(Blocks.SUGAR_CANE);
        this.addDrop(Blocks.JUKEBOX);
        this.addDrop(Blocks.OAK_FENCE);
        this.addDrop(Blocks.PUMPKIN);
        this.addDrop(Blocks.NETHERRACK);
        this.addDrop(Blocks.SOUL_SAND);
        this.addDrop(Blocks.SOUL_SOIL);
        this.addDrop(Blocks.BASALT);
        this.addDrop(Blocks.POLISHED_BASALT);
        this.addDrop(Blocks.SOUL_TORCH);
        this.addDrop(Blocks.CARVED_PUMPKIN);
        this.addDrop(Blocks.JACK_O_LANTERN);
        this.addDrop(Blocks.REPEATER);
        this.addDrop(Blocks.OAK_TRAPDOOR);
        this.addDrop(Blocks.SPRUCE_TRAPDOOR);
        this.addDrop(Blocks.BIRCH_TRAPDOOR);
        this.addDrop(Blocks.JUNGLE_TRAPDOOR);
        this.addDrop(Blocks.ACACIA_TRAPDOOR);
        this.addDrop(Blocks.DARK_OAK_TRAPDOOR);
        this.addDrop(Blocks.STONE_BRICKS);
        this.addDrop(Blocks.MOSSY_STONE_BRICKS);
        this.addDrop(Blocks.CRACKED_STONE_BRICKS);
        this.addDrop(Blocks.CHISELED_STONE_BRICKS);
        this.addDrop(Blocks.IRON_BARS);
        this.addDrop(Blocks.OAK_FENCE_GATE);
        this.addDrop(Blocks.BRICK_STAIRS);
        this.addDrop(Blocks.STONE_BRICK_STAIRS);
        this.addDrop(Blocks.LILY_PAD);
        this.addDrop(Blocks.NETHER_BRICKS);
        this.addDrop(Blocks.NETHER_BRICK_FENCE);
        this.addDrop(Blocks.NETHER_BRICK_STAIRS);
        this.addDrop(Blocks.CAULDRON);
        this.addDrop(Blocks.END_STONE);
        this.addDrop(Blocks.REDSTONE_LAMP);
        this.addDrop(Blocks.SANDSTONE_STAIRS);
        this.addDrop(Blocks.TRIPWIRE_HOOK);
        this.addDrop(Blocks.EMERALD_BLOCK);
        this.addDrop(Blocks.SPRUCE_STAIRS);
        this.addDrop(Blocks.BIRCH_STAIRS);
        this.addDrop(Blocks.JUNGLE_STAIRS);
        this.addDrop(Blocks.COBBLESTONE_WALL);
        this.addDrop(Blocks.MOSSY_COBBLESTONE_WALL);
        this.addDrop(Blocks.FLOWER_POT);
        this.addDrop(Blocks.OAK_BUTTON);
        this.addDrop(Blocks.SPRUCE_BUTTON);
        this.addDrop(Blocks.BIRCH_BUTTON);
        this.addDrop(Blocks.JUNGLE_BUTTON);
        this.addDrop(Blocks.ACACIA_BUTTON);
        this.addDrop(Blocks.DARK_OAK_BUTTON);
        this.addDrop(Blocks.SKELETON_SKULL);
        this.addDrop(Blocks.WITHER_SKELETON_SKULL);
        this.addDrop(Blocks.ZOMBIE_HEAD);
        this.addDrop(Blocks.CREEPER_HEAD);
        this.addDrop(Blocks.DRAGON_HEAD);
        this.addDrop(Blocks.ANVIL);
        this.addDrop(Blocks.CHIPPED_ANVIL);
        this.addDrop(Blocks.DAMAGED_ANVIL);
        this.addDrop(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.addDrop(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.addDrop(Blocks.COMPARATOR);
        this.addDrop(Blocks.DAYLIGHT_DETECTOR);
        this.addDrop(Blocks.REDSTONE_BLOCK);
        this.addDrop(Blocks.QUARTZ_BLOCK);
        this.addDrop(Blocks.CHISELED_QUARTZ_BLOCK);
        this.addDrop(Blocks.QUARTZ_PILLAR);
        this.addDrop(Blocks.QUARTZ_STAIRS);
        this.addDrop(Blocks.ACTIVATOR_RAIL);
        this.addDrop(Blocks.WHITE_TERRACOTTA);
        this.addDrop(Blocks.ORANGE_TERRACOTTA);
        this.addDrop(Blocks.MAGENTA_TERRACOTTA);
        this.addDrop(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.addDrop(Blocks.YELLOW_TERRACOTTA);
        this.addDrop(Blocks.LIME_TERRACOTTA);
        this.addDrop(Blocks.PINK_TERRACOTTA);
        this.addDrop(Blocks.GRAY_TERRACOTTA);
        this.addDrop(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.addDrop(Blocks.CYAN_TERRACOTTA);
        this.addDrop(Blocks.PURPLE_TERRACOTTA);
        this.addDrop(Blocks.BLUE_TERRACOTTA);
        this.addDrop(Blocks.BROWN_TERRACOTTA);
        this.addDrop(Blocks.GREEN_TERRACOTTA);
        this.addDrop(Blocks.RED_TERRACOTTA);
        this.addDrop(Blocks.BLACK_TERRACOTTA);
        this.addDrop(Blocks.ACACIA_STAIRS);
        this.addDrop(Blocks.DARK_OAK_STAIRS);
        this.addDrop(Blocks.SLIME_BLOCK);
        this.addDrop(Blocks.IRON_TRAPDOOR);
        this.addDrop(Blocks.PRISMARINE);
        this.addDrop(Blocks.PRISMARINE_BRICKS);
        this.addDrop(Blocks.DARK_PRISMARINE);
        this.addDrop(Blocks.PRISMARINE_STAIRS);
        this.addDrop(Blocks.PRISMARINE_BRICK_STAIRS);
        this.addDrop(Blocks.DARK_PRISMARINE_STAIRS);
        this.addDrop(Blocks.HAY_BLOCK);
        this.addDrop(Blocks.WHITE_CARPET);
        this.addDrop(Blocks.ORANGE_CARPET);
        this.addDrop(Blocks.MAGENTA_CARPET);
        this.addDrop(Blocks.LIGHT_BLUE_CARPET);
        this.addDrop(Blocks.YELLOW_CARPET);
        this.addDrop(Blocks.LIME_CARPET);
        this.addDrop(Blocks.PINK_CARPET);
        this.addDrop(Blocks.GRAY_CARPET);
        this.addDrop(Blocks.LIGHT_GRAY_CARPET);
        this.addDrop(Blocks.CYAN_CARPET);
        this.addDrop(Blocks.PURPLE_CARPET);
        this.addDrop(Blocks.BLUE_CARPET);
        this.addDrop(Blocks.BROWN_CARPET);
        this.addDrop(Blocks.GREEN_CARPET);
        this.addDrop(Blocks.RED_CARPET);
        this.addDrop(Blocks.BLACK_CARPET);
        this.addDrop(Blocks.TERRACOTTA);
        this.addDrop(Blocks.COAL_BLOCK);
        this.addDrop(Blocks.RED_SANDSTONE);
        this.addDrop(Blocks.CHISELED_RED_SANDSTONE);
        this.addDrop(Blocks.CUT_RED_SANDSTONE);
        this.addDrop(Blocks.RED_SANDSTONE_STAIRS);
        this.addDrop(Blocks.SMOOTH_STONE);
        this.addDrop(Blocks.SMOOTH_SANDSTONE);
        this.addDrop(Blocks.SMOOTH_QUARTZ);
        this.addDrop(Blocks.SMOOTH_RED_SANDSTONE);
        this.addDrop(Blocks.SPRUCE_FENCE_GATE);
        this.addDrop(Blocks.BIRCH_FENCE_GATE);
        this.addDrop(Blocks.JUNGLE_FENCE_GATE);
        this.addDrop(Blocks.ACACIA_FENCE_GATE);
        this.addDrop(Blocks.DARK_OAK_FENCE_GATE);
        this.addDrop(Blocks.SPRUCE_FENCE);
        this.addDrop(Blocks.BIRCH_FENCE);
        this.addDrop(Blocks.JUNGLE_FENCE);
        this.addDrop(Blocks.ACACIA_FENCE);
        this.addDrop(Blocks.DARK_OAK_FENCE);
        this.addDrop(Blocks.END_ROD);
        this.addDrop(Blocks.PURPUR_BLOCK);
        this.addDrop(Blocks.PURPUR_PILLAR);
        this.addDrop(Blocks.PURPUR_STAIRS);
        this.addDrop(Blocks.END_STONE_BRICKS);
        this.addDrop(Blocks.MAGMA_BLOCK);
        this.addDrop(Blocks.NETHER_WART_BLOCK);
        this.addDrop(Blocks.RED_NETHER_BRICKS);
        this.addDrop(Blocks.BONE_BLOCK);
        this.addDrop(Blocks.OBSERVER);
        this.addDrop(Blocks.TARGET);
        this.addDrop(Blocks.WHITE_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.ORANGE_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.MAGENTA_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.YELLOW_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.LIME_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.PINK_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.GRAY_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.CYAN_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.PURPLE_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.BLUE_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.BROWN_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.GREEN_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.RED_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.BLACK_GLAZED_TERRACOTTA);
        this.addDrop(Blocks.WHITE_CONCRETE);
        this.addDrop(Blocks.ORANGE_CONCRETE);
        this.addDrop(Blocks.MAGENTA_CONCRETE);
        this.addDrop(Blocks.LIGHT_BLUE_CONCRETE);
        this.addDrop(Blocks.YELLOW_CONCRETE);
        this.addDrop(Blocks.LIME_CONCRETE);
        this.addDrop(Blocks.PINK_CONCRETE);
        this.addDrop(Blocks.GRAY_CONCRETE);
        this.addDrop(Blocks.LIGHT_GRAY_CONCRETE);
        this.addDrop(Blocks.CYAN_CONCRETE);
        this.addDrop(Blocks.PURPLE_CONCRETE);
        this.addDrop(Blocks.BLUE_CONCRETE);
        this.addDrop(Blocks.BROWN_CONCRETE);
        this.addDrop(Blocks.GREEN_CONCRETE);
        this.addDrop(Blocks.RED_CONCRETE);
        this.addDrop(Blocks.BLACK_CONCRETE);
        this.addDrop(Blocks.WHITE_CONCRETE_POWDER);
        this.addDrop(Blocks.ORANGE_CONCRETE_POWDER);
        this.addDrop(Blocks.MAGENTA_CONCRETE_POWDER);
        this.addDrop(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        this.addDrop(Blocks.YELLOW_CONCRETE_POWDER);
        this.addDrop(Blocks.LIME_CONCRETE_POWDER);
        this.addDrop(Blocks.PINK_CONCRETE_POWDER);
        this.addDrop(Blocks.GRAY_CONCRETE_POWDER);
        this.addDrop(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        this.addDrop(Blocks.CYAN_CONCRETE_POWDER);
        this.addDrop(Blocks.PURPLE_CONCRETE_POWDER);
        this.addDrop(Blocks.BLUE_CONCRETE_POWDER);
        this.addDrop(Blocks.BROWN_CONCRETE_POWDER);
        this.addDrop(Blocks.GREEN_CONCRETE_POWDER);
        this.addDrop(Blocks.RED_CONCRETE_POWDER);
        this.addDrop(Blocks.BLACK_CONCRETE_POWDER);
        this.addDrop(Blocks.KELP);
        this.addDrop(Blocks.DRIED_KELP_BLOCK);
        this.addDrop(Blocks.DEAD_TUBE_CORAL_BLOCK);
        this.addDrop(Blocks.DEAD_BRAIN_CORAL_BLOCK);
        this.addDrop(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
        this.addDrop(Blocks.DEAD_FIRE_CORAL_BLOCK);
        this.addDrop(Blocks.DEAD_HORN_CORAL_BLOCK);
        this.addDrop(Blocks.CONDUIT);
        this.addDrop(Blocks.DRAGON_EGG);
        this.addDrop(Blocks.BAMBOO);
        this.addDrop(Blocks.POLISHED_GRANITE_STAIRS);
        this.addDrop(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        this.addDrop(Blocks.MOSSY_STONE_BRICK_STAIRS);
        this.addDrop(Blocks.POLISHED_DIORITE_STAIRS);
        this.addDrop(Blocks.MOSSY_COBBLESTONE_STAIRS);
        this.addDrop(Blocks.END_STONE_BRICK_STAIRS);
        this.addDrop(Blocks.STONE_STAIRS);
        this.addDrop(Blocks.SMOOTH_SANDSTONE_STAIRS);
        this.addDrop(Blocks.SMOOTH_QUARTZ_STAIRS);
        this.addDrop(Blocks.GRANITE_STAIRS);
        this.addDrop(Blocks.ANDESITE_STAIRS);
        this.addDrop(Blocks.RED_NETHER_BRICK_STAIRS);
        this.addDrop(Blocks.POLISHED_ANDESITE_STAIRS);
        this.addDrop(Blocks.DIORITE_STAIRS);
        this.addDrop(Blocks.BRICK_WALL);
        this.addDrop(Blocks.PRISMARINE_WALL);
        this.addDrop(Blocks.RED_SANDSTONE_WALL);
        this.addDrop(Blocks.MOSSY_STONE_BRICK_WALL);
        this.addDrop(Blocks.GRANITE_WALL);
        this.addDrop(Blocks.STONE_BRICK_WALL);
        this.addDrop(Blocks.NETHER_BRICK_WALL);
        this.addDrop(Blocks.ANDESITE_WALL);
        this.addDrop(Blocks.RED_NETHER_BRICK_WALL);
        this.addDrop(Blocks.SANDSTONE_WALL);
        this.addDrop(Blocks.END_STONE_BRICK_WALL);
        this.addDrop(Blocks.DIORITE_WALL);
        this.addDrop(Blocks.LOOM);
        this.addDrop(Blocks.SCAFFOLDING);
        this.addDrop(Blocks.HONEY_BLOCK);
        this.addDrop(Blocks.HONEYCOMB_BLOCK);
        this.addDrop(Blocks.RESPAWN_ANCHOR);
        this.addDrop(Blocks.LODESTONE);
        this.addDrop(Blocks.WARPED_STEM);
        this.addDrop(Blocks.WARPED_HYPHAE);
        this.addDrop(Blocks.WARPED_FUNGUS);
        this.addDrop(Blocks.WARPED_WART_BLOCK);
        this.addDrop(Blocks.WARPED_ROOTS);
        this.addDrop(Blocks.CRIMSON_STEM);
        this.addDrop(Blocks.CRIMSON_HYPHAE);
        this.addDrop(Blocks.CRIMSON_FUNGUS);
        this.addDrop(Blocks.SHROOMLIGHT);
        this.addDrop(Blocks.CRIMSON_ROOTS);
        this.addDrop(Blocks.CRIMSON_PLANKS);
        this.addDrop(Blocks.WARPED_PLANKS);
        this.addDrop(Blocks.WARPED_PRESSURE_PLATE);
        this.addDrop(Blocks.WARPED_FENCE);
        this.addDrop(Blocks.WARPED_TRAPDOOR);
        this.addDrop(Blocks.WARPED_FENCE_GATE);
        this.addDrop(Blocks.WARPED_STAIRS);
        this.addDrop(Blocks.WARPED_BUTTON);
        this.addDrop(Blocks.WARPED_SIGN);
        this.addDrop(Blocks.CRIMSON_PRESSURE_PLATE);
        this.addDrop(Blocks.CRIMSON_FENCE);
        this.addDrop(Blocks.CRIMSON_TRAPDOOR);
        this.addDrop(Blocks.CRIMSON_FENCE_GATE);
        this.addDrop(Blocks.CRIMSON_STAIRS);
        this.addDrop(Blocks.CRIMSON_BUTTON);
        this.addDrop(Blocks.CRIMSON_SIGN);
        this.addDrop(Blocks.NETHERITE_BLOCK);
        this.addDrop(Blocks.ANCIENT_DEBRIS);
        this.addDrop(Blocks.BLACKSTONE);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICKS);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
        this.addDrop(Blocks.BLACKSTONE_STAIRS);
        this.addDrop(Blocks.BLACKSTONE_WALL);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        this.addDrop(Blocks.CHISELED_POLISHED_BLACKSTONE);
        this.addDrop(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        this.addDrop(Blocks.POLISHED_BLACKSTONE);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_STAIRS);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_BUTTON);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_WALL);
        this.addDrop(Blocks.CHISELED_NETHER_BRICKS);
        this.addDrop(Blocks.CRACKED_NETHER_BRICKS);
        this.addDrop(Blocks.QUARTZ_BRICKS);
        this.addDrop(Blocks.CHAIN);
        this.addDrop(Blocks.FARMLAND, Blocks.DIRT);
        this.addDrop(Blocks.TRIPWIRE, Items.STRING);
        this.addDrop(Blocks.GRASS_PATH, Blocks.DIRT);
        this.addDrop(Blocks.KELP_PLANT, Blocks.KELP);
        this.addDrop(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
        this.addDrop(Blocks.STONE, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.COBBLESTONE));
        this.addDrop(Blocks.GRASS_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DIRT));
        this.addDrop(Blocks.PODZOL, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DIRT));
        this.addDrop(Blocks.MYCELIUM, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DIRT));
        this.addDrop(Blocks.TUBE_CORAL_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DEAD_TUBE_CORAL_BLOCK));
        this.addDrop(Blocks.BRAIN_CORAL_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DEAD_BRAIN_CORAL_BLOCK));
        this.addDrop(Blocks.BUBBLE_CORAL_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
        this.addDrop(Blocks.FIRE_CORAL_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DEAD_FIRE_CORAL_BLOCK));
        this.addDrop(Blocks.HORN_CORAL_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.DEAD_HORN_CORAL_BLOCK));
        this.addDrop(Blocks.CRIMSON_NYLIUM, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.NETHERRACK));
        this.addDrop(Blocks.WARPED_NYLIUM, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.NETHERRACK));
        this.addDrop(Blocks.BOOKSHELF, (Block arg) -> BlockLootTableGenerator.drops(arg, Items.BOOK, ConstantLootTableRange.create(3)));
        this.addDrop(Blocks.CLAY, (Block arg) -> BlockLootTableGenerator.drops(arg, Items.CLAY_BALL, ConstantLootTableRange.create(4)));
        this.addDrop(Blocks.ENDER_CHEST, (Block arg) -> BlockLootTableGenerator.drops(arg, Blocks.OBSIDIAN, ConstantLootTableRange.create(8)));
        this.addDrop(Blocks.SNOW_BLOCK, (Block arg) -> BlockLootTableGenerator.drops(arg, Items.SNOWBALL, ConstantLootTableRange.create(4)));
        this.addDrop(Blocks.CHORUS_PLANT, BlockLootTableGenerator.drops(Items.CHORUS_FRUIT, UniformLootTableRange.between(0.0f, 1.0f)));
        this.addPottedPlantDrop(Blocks.POTTED_OAK_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_SPRUCE_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_BIRCH_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_JUNGLE_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_ACACIA_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_DARK_OAK_SAPLING);
        this.addPottedPlantDrop(Blocks.POTTED_FERN);
        this.addPottedPlantDrop(Blocks.POTTED_DANDELION);
        this.addPottedPlantDrop(Blocks.POTTED_POPPY);
        this.addPottedPlantDrop(Blocks.POTTED_BLUE_ORCHID);
        this.addPottedPlantDrop(Blocks.POTTED_ALLIUM);
        this.addPottedPlantDrop(Blocks.POTTED_AZURE_BLUET);
        this.addPottedPlantDrop(Blocks.POTTED_RED_TULIP);
        this.addPottedPlantDrop(Blocks.POTTED_ORANGE_TULIP);
        this.addPottedPlantDrop(Blocks.POTTED_WHITE_TULIP);
        this.addPottedPlantDrop(Blocks.POTTED_PINK_TULIP);
        this.addPottedPlantDrop(Blocks.POTTED_OXEYE_DAISY);
        this.addPottedPlantDrop(Blocks.POTTED_CORNFLOWER);
        this.addPottedPlantDrop(Blocks.POTTED_LILY_OF_THE_VALLEY);
        this.addPottedPlantDrop(Blocks.POTTED_WITHER_ROSE);
        this.addPottedPlantDrop(Blocks.POTTED_RED_MUSHROOM);
        this.addPottedPlantDrop(Blocks.POTTED_BROWN_MUSHROOM);
        this.addPottedPlantDrop(Blocks.POTTED_DEAD_BUSH);
        this.addPottedPlantDrop(Blocks.POTTED_CACTUS);
        this.addPottedPlantDrop(Blocks.POTTED_BAMBOO);
        this.addPottedPlantDrop(Blocks.POTTED_CRIMSON_FUNGUS);
        this.addPottedPlantDrop(Blocks.POTTED_WARPED_FUNGUS);
        this.addPottedPlantDrop(Blocks.POTTED_CRIMSON_ROOTS);
        this.addPottedPlantDrop(Blocks.POTTED_WARPED_ROOTS);
        this.addDrop(Blocks.ACACIA_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.BIRCH_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.COBBLESTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.DARK_OAK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.DARK_PRISMARINE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.JUNGLE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.NETHER_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.OAK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.PETRIFIED_OAK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.PRISMARINE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.PURPUR_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.QUARTZ_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.CUT_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SPRUCE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.STONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SMOOTH_STONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.POLISHED_GRANITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.POLISHED_DIORITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.END_STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.GRANITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.ANDESITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.DIORITE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.CRIMSON_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.WARPED_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.BLACKSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTableGenerator::slabDrops);
        this.addDrop(Blocks.ACACIA_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.BIRCH_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.DARK_OAK_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.IRON_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.JUNGLE_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.OAK_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.SPRUCE_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.WARPED_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.CRIMSON_DOOR, BlockLootTableGenerator::addDoorDrop);
        this.addDrop(Blocks.BLACK_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.BLUE_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.BROWN_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.CYAN_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.GRAY_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.GREEN_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.LIGHT_BLUE_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.LIGHT_GRAY_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.LIME_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.MAGENTA_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.PURPLE_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.ORANGE_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.PINK_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.RED_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.WHITE_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.YELLOW_BED, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, BedBlock.PART, BedPart.HEAD));
        this.addDrop(Blocks.LILAC, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.addDrop(Blocks.SUNFLOWER, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.addDrop(Blocks.PEONY, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.addDrop(Blocks.ROSE_BUSH, (Block arg) -> BlockLootTableGenerator.dropsWithProperty(arg, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.addDrop(Blocks.TNT, LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(Blocks.TNT, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)ItemEntry.builder(Blocks.TNT).conditionally(BlockStatePropertyLootCondition.builder(Blocks.TNT).properties(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))))));
        this.addDrop(Blocks.COCOA, (Block arg) -> LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(Items.COCOA_BEANS).apply((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2))))))));
        this.addDrop(Blocks.SEA_PICKLE, (Block arg) -> LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(Blocks.SEA_PICKLE, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(arg).apply((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2))))).apply((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3))))).apply((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(4)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4))))))));
        this.addDrop(Blocks.COMPOSTER, (Block arg) -> LootTable.builder().pool(LootPool.builder().with((LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(Items.COMPOSTER)))).pool(LootPool.builder().with(ItemEntry.builder(Items.BONE_MEAL)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))));
        this.addDrop(Blocks.BEACON, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.BREWING_STAND, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.CHEST, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.DISPENSER, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.DROPPER, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.ENCHANTING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.FURNACE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.HOPPER, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.TRAPPED_CHEST, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.SMOKER, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.BLAST_FURNACE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.BARREL, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.CARTOGRAPHY_TABLE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.FLETCHING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.GRINDSTONE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.LECTERN, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.SMITHING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.STONECUTTER, BlockLootTableGenerator::nameableContainerDrops);
        this.addDrop(Blocks.BELL, BlockLootTableGenerator::drops);
        this.addDrop(Blocks.LANTERN, BlockLootTableGenerator::drops);
        this.addDrop(Blocks.SOUL_LANTERN, BlockLootTableGenerator::drops);
        this.addDrop(Blocks.SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.BLACK_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.BLUE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.BROWN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.CYAN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.GRAY_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.GREEN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.LIME_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.MAGENTA_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.ORANGE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.PINK_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.PURPLE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.RED_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.WHITE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.YELLOW_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
        this.addDrop(Blocks.BLACK_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.BLUE_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.BROWN_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.CYAN_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.GRAY_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.GREEN_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.LIGHT_BLUE_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.LIGHT_GRAY_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.LIME_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.MAGENTA_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.ORANGE_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.PINK_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.PURPLE_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.RED_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.WHITE_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.YELLOW_BANNER, BlockLootTableGenerator::bannerDrops);
        this.addDrop(Blocks.PLAYER_HEAD, (Block arg) -> LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)ItemEntry.builder(arg).apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("SkullOwner", "SkullOwner"))))));
        this.addDrop(Blocks.BEE_NEST, BlockLootTableGenerator::beeNestDrops);
        this.addDrop(Blocks.BEEHIVE, BlockLootTableGenerator::beehiveDrops);
        this.addDrop(Blocks.BIRCH_LEAVES, (Block arg) -> BlockLootTableGenerator.leavesDrop(arg, Blocks.BIRCH_SAPLING, SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.ACACIA_LEAVES, (Block arg) -> BlockLootTableGenerator.leavesDrop(arg, Blocks.ACACIA_SAPLING, SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.JUNGLE_LEAVES, (Block arg) -> BlockLootTableGenerator.leavesDrop(arg, Blocks.JUNGLE_SAPLING, JUNGLE_SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.SPRUCE_LEAVES, (Block arg) -> BlockLootTableGenerator.leavesDrop(arg, Blocks.SPRUCE_SAPLING, SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.OAK_LEAVES, (Block arg) -> BlockLootTableGenerator.oakLeavesDrop(arg, Blocks.OAK_SAPLING, SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.DARK_OAK_LEAVES, (Block arg) -> BlockLootTableGenerator.oakLeavesDrop(arg, Blocks.DARK_OAK_SAPLING, SAPLING_DROP_CHANCE));
        BlockStatePropertyLootCondition.Builder lv = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS).properties(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
        this.addDrop(Blocks.BEETROOTS, BlockLootTableGenerator.cropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, lv));
        BlockStatePropertyLootCondition.Builder lv2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT).properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
        this.addDrop(Blocks.WHEAT, BlockLootTableGenerator.cropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, lv2));
        BlockStatePropertyLootCondition.Builder lv3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS).properties(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
        this.addDrop(Blocks.CARROTS, BlockLootTableGenerator.applyExplosionDecay(Blocks.CARROTS, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(Items.CARROT))).pool(LootPool.builder().conditionally(lv3).with((LootEntry.Builder<?>)ItemEntry.builder(Items.CARROT).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3))))));
        BlockStatePropertyLootCondition.Builder lv4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES).properties(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
        this.addDrop(Blocks.POTATOES, BlockLootTableGenerator.applyExplosionDecay(Blocks.POTATOES, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(Items.POTATO))).pool(LootPool.builder().conditionally(lv4).with((LootEntry.Builder<?>)ItemEntry.builder(Items.POTATO).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))).pool(LootPool.builder().conditionally(lv4).with((LootEntry.Builder<?>)ItemEntry.builder(Items.POISONOUS_POTATO).conditionally(RandomChanceLootCondition.builder(0.02f))))));
        this.addDrop(Blocks.SWEET_BERRY_BUSH, (Block arg) -> BlockLootTableGenerator.applyExplosionDecay(arg, LootTable.builder().pool(LootPool.builder().conditionally(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))).with(ItemEntry.builder(Items.SWEET_BERRIES)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).pool(LootPool.builder().conditionally(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))).with(ItemEntry.builder(Items.SWEET_BERRIES)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.addDrop(Blocks.BROWN_MUSHROOM_BLOCK, (Block arg) -> BlockLootTableGenerator.mushroomBlockDrops(arg, Blocks.BROWN_MUSHROOM));
        this.addDrop(Blocks.RED_MUSHROOM_BLOCK, (Block arg) -> BlockLootTableGenerator.mushroomBlockDrops(arg, Blocks.RED_MUSHROOM));
        this.addDrop(Blocks.COAL_ORE, (Block arg) -> BlockLootTableGenerator.oreDrops(arg, Items.COAL));
        this.addDrop(Blocks.EMERALD_ORE, (Block arg) -> BlockLootTableGenerator.oreDrops(arg, Items.EMERALD));
        this.addDrop(Blocks.NETHER_QUARTZ_ORE, (Block arg) -> BlockLootTableGenerator.oreDrops(arg, Items.QUARTZ));
        this.addDrop(Blocks.DIAMOND_ORE, (Block arg) -> BlockLootTableGenerator.oreDrops(arg, Items.DIAMOND));
        this.addDrop(Blocks.NETHER_GOLD_ORE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 6.0f)))).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))));
        this.addDrop(Blocks.LAPIS_ORE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.LAPIS_LAZULI).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 9.0f)))).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))));
        this.addDrop(Blocks.COBWEB, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouchOrShears(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(Items.STRING))));
        this.addDrop(Blocks.DEAD_BUSH, (Block arg) -> BlockLootTableGenerator.dropsWithShears(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootTableRange.between(0.0f, 2.0f))))));
        this.addDrop(Blocks.NETHER_SPROUTS, BlockLootTableGenerator::dropsWithShears);
        this.addDrop(Blocks.SEAGRASS, BlockLootTableGenerator::dropsWithShears);
        this.addDrop(Blocks.VINE, BlockLootTableGenerator::dropsWithShears);
        this.addDrop(Blocks.TALL_SEAGRASS, BlockLootTableGenerator.dropsWithShears(Blocks.SEAGRASS));
        this.addDrop(Blocks.LARGE_FERN, (Block arg) -> BlockLootTableGenerator.dropsWithShears(Blocks.FERN, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(Items.WHEAT_SEEDS))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).conditionally(RandomChanceLootCondition.builder(0.125f))));
        this.addDrop(Blocks.TALL_GRASS, BlockLootTableGenerator.dropsWithShears(Blocks.GRASS, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(Blocks.TALL_GRASS, ItemEntry.builder(Items.WHEAT_SEEDS))).conditionally(BlockStatePropertyLootCondition.builder(Blocks.TALL_GRASS).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).conditionally(RandomChanceLootCondition.builder(0.125f))));
        this.addDrop(Blocks.MELON_STEM, (Block arg) -> BlockLootTableGenerator.cropStemDrops(arg, Items.MELON_SEEDS));
        this.addDrop(Blocks.ATTACHED_MELON_STEM, (Block arg) -> BlockLootTableGenerator.attachedCropStemDrops(arg, Items.MELON_SEEDS));
        this.addDrop(Blocks.PUMPKIN_STEM, (Block arg) -> BlockLootTableGenerator.cropStemDrops(arg, Items.PUMPKIN_SEEDS));
        this.addDrop(Blocks.ATTACHED_PUMPKIN_STEM, (Block arg) -> BlockLootTableGenerator.attachedCropStemDrops(arg, Items.PUMPKIN_SEEDS));
        this.addDrop(Blocks.CHORUS_FLOWER, (Block arg) -> LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(arg))).conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)))));
        this.addDrop(Blocks.FERN, BlockLootTableGenerator::grassDrops);
        this.addDrop(Blocks.GRASS, BlockLootTableGenerator::grassDrops);
        this.addDrop(Blocks.GLOWSTONE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4))))));
        this.addDrop(Blocks.MELON, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.MELON_SLICE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0f, 7.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9))))));
        this.addDrop(Blocks.REDSTONE_ORE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.REDSTONE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 5.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.addDrop(Blocks.SEA_LANTERN, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.applyExplosionDecay(arg, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.PRISMARINE_CRYSTALS).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f)))).apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5))))));
        this.addDrop(Blocks.NETHER_WART, (Block arg) -> LootTable.builder().pool(BlockLootTableGenerator.applyExplosionDecay(arg, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_WART).apply((LootFunction.Builder)SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))).apply((LootFunction.Builder)ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))))));
        this.addDrop(Blocks.SNOW, (Block arg) -> LootTable.builder().pool(LootPool.builder().conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)).with(AlternativeEntry.builder(new LootEntry.Builder[]{AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(2))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(3))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(4))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(5))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(6))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(7))), ItemEntry.builder(Items.SNOWBALL).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))}).conditionally(WITHOUT_SILK_TOUCH), AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Blocks.SNOW).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))).conditionally(BlockStatePropertyLootCondition.builder(arg).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))), ItemEntry.builder(Blocks.SNOW_BLOCK)})}))));
        this.addDrop(Blocks.GRAVEL, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ((LeafEntry.Builder)ItemEntry.builder(Items.FLINT).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1f, 0.14285715f, 0.25f, 1.0f))).alternatively(ItemEntry.builder(arg)))));
        this.addDrop(Blocks.CAMPFIRE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(Items.CHARCOAL).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))));
        this.addDrop(Blocks.GILDED_BLACKSTONE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ((LeafEntry.Builder)((LootEntry.Builder)ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 5.0f)))).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1f, 0.14285715f, 0.25f, 1.0f))).alternatively(ItemEntry.builder(arg)))));
        this.addDrop(Blocks.SOUL_CAMPFIRE, (Block arg) -> BlockLootTableGenerator.dropsWithSilkTouch(arg, (LootEntry.Builder)BlockLootTableGenerator.addSurvivesExplosionCondition(arg, ItemEntry.builder(Items.SOUL_SOIL).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))));
        this.addDropWithSilkTouch(Blocks.GLASS);
        this.addDropWithSilkTouch(Blocks.WHITE_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.ORANGE_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.YELLOW_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.LIME_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.PINK_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.GRAY_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.CYAN_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.PURPLE_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.BLUE_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.BROWN_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.GREEN_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.RED_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.BLACK_STAINED_GLASS);
        this.addDropWithSilkTouch(Blocks.GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
        this.addDropWithSilkTouch(Blocks.ICE);
        this.addDropWithSilkTouch(Blocks.PACKED_ICE);
        this.addDropWithSilkTouch(Blocks.BLUE_ICE);
        this.addDropWithSilkTouch(Blocks.TURTLE_EGG);
        this.addDropWithSilkTouch(Blocks.MUSHROOM_STEM);
        this.addDropWithSilkTouch(Blocks.DEAD_TUBE_CORAL);
        this.addDropWithSilkTouch(Blocks.DEAD_BRAIN_CORAL);
        this.addDropWithSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
        this.addDropWithSilkTouch(Blocks.DEAD_FIRE_CORAL);
        this.addDropWithSilkTouch(Blocks.DEAD_HORN_CORAL);
        this.addDropWithSilkTouch(Blocks.TUBE_CORAL);
        this.addDropWithSilkTouch(Blocks.BRAIN_CORAL);
        this.addDropWithSilkTouch(Blocks.BUBBLE_CORAL);
        this.addDropWithSilkTouch(Blocks.FIRE_CORAL);
        this.addDropWithSilkTouch(Blocks.HORN_CORAL);
        this.addDropWithSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.TUBE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.BRAIN_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.BUBBLE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.FIRE_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.HORN_CORAL_FAN);
        this.addDropWithSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
        this.addDropWithSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        this.addDropWithSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        this.addDropWithSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        this.addDropWithSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        this.addDropWithSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        this.addVinePlantDrop(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
        this.addVinePlantDrop(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
        this.addDrop(Blocks.CAKE, BlockLootTableGenerator.dropsNothing());
        this.addDrop(Blocks.FROSTED_ICE, BlockLootTableGenerator.dropsNothing());
        this.addDrop(Blocks.SPAWNER, BlockLootTableGenerator.dropsNothing());
        this.addDrop(Blocks.FIRE, BlockLootTableGenerator.dropsNothing());
        this.addDrop(Blocks.SOUL_FIRE, BlockLootTableGenerator.dropsNothing());
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

    private void addVinePlantDrop(Block arg, Block arg2) {
        LootTable.Builder lv = BlockLootTableGenerator.dropsWithSilkTouchOrShears(arg, ItemEntry.builder(arg).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.33f, 0.55f, 0.77f, 1.0f)));
        this.addDrop(arg, lv);
        this.addDrop(arg2, lv);
    }

    public static LootTable.Builder addDoorDrop(Block arg) {
        return BlockLootTableGenerator.dropsWithProperty(arg, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    public void addPottedPlantDrop(Block arg2) {
        this.addDrop(arg2, (Block arg) -> BlockLootTableGenerator.pottedPlantDrops(((FlowerPotBlock)arg).getContent()));
    }

    public void addDropWithSilkTouch(Block arg, Block arg2) {
        this.addDrop(arg, BlockLootTableGenerator.dropsWithSilkTouch(arg2));
    }

    public void addDrop(Block arg, ItemConvertible arg2) {
        this.addDrop(arg, BlockLootTableGenerator.drops(arg2));
    }

    public void addDropWithSilkTouch(Block arg) {
        this.addDropWithSilkTouch(arg, arg);
    }

    public void addDrop(Block arg) {
        this.addDrop(arg, arg);
    }

    private void addDrop(Block arg, Function<Block, LootTable.Builder> function) {
        this.addDrop(arg, function.apply(arg));
    }

    private void addDrop(Block arg, LootTable.Builder arg2) {
        this.lootTables.put(arg.getLootTableId(), arg2);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

