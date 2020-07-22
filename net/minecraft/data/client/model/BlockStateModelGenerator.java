/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.JsonElement
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.MultipartBlockStateSupplier;
import net.minecraft.data.client.model.SimpleModelSupplier;
import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.data.client.model.TexturedModel;
import net.minecraft.data.client.model.VariantSettings;
import net.minecraft.data.client.model.VariantsBlockStateSupplier;
import net.minecraft.data.client.model.When;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class BlockStateModelGenerator {
    private final Consumer<BlockStateSupplier> blockStateCollector;
    private final BiConsumer<Identifier, Supplier<JsonElement>> modelCollector;
    private final Consumer<Item> simpleItemModelExemptionCollector;

    public BlockStateModelGenerator(Consumer<BlockStateSupplier> blockStateCollector, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, Consumer<Item> simpleItemModelExemptionCollector) {
        this.blockStateCollector = blockStateCollector;
        this.modelCollector = modelCollector;
        this.simpleItemModelExemptionCollector = simpleItemModelExemptionCollector;
    }

    private void excludeFromSimpleItemModelGeneration(Block block) {
        this.simpleItemModelExemptionCollector.accept(block.asItem());
    }

    private void registerParentedItemModel(Block block, Identifier parentModelId) {
        this.modelCollector.accept(ModelIds.getItemModelId(block.asItem()), new SimpleModelSupplier(parentModelId));
    }

    private void registerParentedItemModel(Item item, Identifier parentModelId) {
        this.modelCollector.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(parentModelId));
    }

    private void registerItemModel(Item item) {
        Models.GENERATED.upload(ModelIds.getItemModelId(item), Texture.layer0(item), this.modelCollector);
    }

    private void registerItemModel(Block block) {
        Item lv = block.asItem();
        if (lv != Items.AIR) {
            Models.GENERATED.upload(ModelIds.getItemModelId(lv), Texture.layer0(block), this.modelCollector);
        }
    }

    private void registerItemModel(Block block, String textureSuffix) {
        Item lv = block.asItem();
        Models.GENERATED.upload(ModelIds.getItemModelId(lv), Texture.layer0(Texture.getSubId(block, textureSuffix)), this.modelCollector);
    }

    private static BlockStateVariantMap createNorthDefaultHorizontalRotationStates() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockStateVariant.create());
    }

    private static BlockStateVariantMap createSouthDefaultHorizontalRotationStates() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).register(Direction.SOUTH, BlockStateVariant.create()).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
    }

    private static BlockStateVariantMap createEastDefaultHorizontalRotationStates() {
        return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).register(Direction.EAST, BlockStateVariant.create()).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270));
    }

    private static BlockStateVariantMap createNorthDefaultRotationStates() {
        return BlockStateVariantMap.create(Properties.FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.UP, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockStateVariant.create()).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    private static VariantsBlockStateSupplier createBlockStateWithRandomHorizontalRotations(Block block, Identifier modelId) {
        return VariantsBlockStateSupplier.create(block, BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(modelId));
    }

    private static BlockStateVariant[] createModelVariantWithRandomHorizontalRotations(Identifier modelId) {
        return new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, modelId), BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)};
    }

    private static VariantsBlockStateSupplier createBlockStateWithTwoModelAndRandomInversion(Block block, Identifier firstModelId, Identifier secondModelId) {
        return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId), BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId), BlockStateVariant.create().put(VariantSettings.MODEL, firstModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, secondModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180));
    }

    private static BlockStateVariantMap createBooleanModelMap(BooleanProperty property, Identifier trueModel, Identifier falseModel) {
        return BlockStateVariantMap.create(property).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, trueModel)).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, falseModel));
    }

    private void registerMirrorable(Block block) {
        Identifier lv = TexturedModel.CUBE_ALL.upload(block, this.modelCollector);
        Identifier lv2 = TexturedModel.CUBE_MIRRORED_ALL.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(block, lv, lv2));
    }

    private void registerRotatable(Block block) {
        Identifier lv = TexturedModel.CUBE_ALL.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(block, lv));
    }

    private static BlockStateSupplier createButtonBlockState(Block buttonBlock, Identifier regularModelId, Identifier pressedModelId) {
        return VariantsBlockStateSupplier.create(buttonBlock).coordinate(BlockStateVariantMap.create(Properties.POWERED).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId)).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, pressedModelId))).coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING).register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create()).register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R180)));
    }

    private static BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> fillDoorVariantMap(BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> variantMap, DoubleBlockHalf targetHalf, Identifier regularModel, Identifier hingeModel) {
        return variantMap.register(Direction.EAST, targetHalf, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel)).register(Direction.SOUTH, targetHalf, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, targetHalf, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.NORTH, targetHalf, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, targetHalf, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel)).register(Direction.SOUTH, targetHalf, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, targetHalf, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.NORTH, targetHalf, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, targetHalf, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, targetHalf, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, targetHalf, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, targetHalf, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, hingeModel)).register(Direction.EAST, targetHalf, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.SOUTH, targetHalf, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel)).register(Direction.WEST, targetHalf, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.NORTH, targetHalf, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, regularModel).put(VariantSettings.Y, VariantSettings.Rotation.R180));
    }

    private static BlockStateSupplier createDoorBlockState(Block doorBlock, Identifier bottomModelId, Identifier bottomHingeModelId, Identifier topModelId, Identifier topHingeModelId) {
        return VariantsBlockStateSupplier.create(doorBlock).coordinate(BlockStateModelGenerator.fillDoorVariantMap(BlockStateModelGenerator.fillDoorVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN), DoubleBlockHalf.LOWER, bottomModelId, bottomHingeModelId), DoubleBlockHalf.UPPER, topModelId, topHingeModelId));
    }

    private static BlockStateSupplier createFenceBlockState(Block fenceBlock, Identifier postModelId, Identifier sideModelId) {
        return MultipartBlockStateSupplier.create(fenceBlock).with(BlockStateVariant.create().put(VariantSettings.MODEL, postModelId)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, sideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true));
    }

    private static BlockStateSupplier createWallBlockState(Block wallBlock, Identifier postModelId, Identifier lowSideModelId, Identifier tallSideModelId) {
        return MultipartBlockStateSupplier.create(wallBlock).with((When)When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, postModelId)).with((When)When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, lowSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, tallSideModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true));
    }

    private static BlockStateSupplier createFenceGateBlockState(Block fenceGateBlock, Identifier openModelId, Identifier closedModelId, Identifier openWallModelId, Identifier closedWallModelId) {
        return VariantsBlockStateSupplier.create(fenceGateBlock, BlockStateVariant.create().put(VariantSettings.UVLOCK, true)).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()).coordinate(BlockStateVariantMap.create(Properties.IN_WALL, Properties.OPEN).register((Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, closedModelId)).register((Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, closedWallModelId)).register((Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId)).register((Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openWallModelId)));
    }

    private static BlockStateSupplier createStairsBlockState(Block stairsBlock, Identifier innerModelId, Identifier regularModelId, Identifier outerModelId) {
        return VariantsBlockStateSupplier.create(stairsBlock).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, regularModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, outerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, innerModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)));
    }

    private static BlockStateSupplier createOrientableTrapdoorBlockState(Block trapdoorBlock, Identifier topModelId, Identifier bottomModelId, Identifier openModelId) {
        return VariantsBlockStateSupplier.create(trapdoorBlock).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R0)).register(Direction.EAST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.WEST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
    }

    private static BlockStateSupplier createTrapdoorBlockState(Block trapdoorBlock, Identifier topModelId, Identifier bottomModelId, Identifier openModelId) {
        return VariantsBlockStateSupplier.create(trapdoorBlock).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(Direction.EAST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(Direction.WEST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, openModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }

    private static VariantsBlockStateSupplier createSingletonBlockState(Block block, Identifier modelId) {
        return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId));
    }

    private static BlockStateVariantMap createAxisRotatedVariantMap() {
        return BlockStateVariantMap.create(Properties.AXIS).register(Direction.Axis.Y, BlockStateVariant.create()).register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    private static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier modelId) {
        return VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, modelId)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    private void registerAxisRotated(Block block, TexturedModel.Factory modelFactory) {
        Identifier lv = modelFactory.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(block, lv));
    }

    private void registerNorthDefaultHorizontalRotated(Block block, TexturedModel.Factory modelFactory) {
        Identifier lv = modelFactory.upload(block, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private static BlockStateSupplier createAxisRotatedBlockState(Block block, Identifier verticalModelId, Identifier horizontalModelId) {
        return VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.AXIS).register(Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, verticalModelId)).register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalModelId).put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, horizontalModelId).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
    }

    private void registerAxisRotated(Block block, TexturedModel.Factory verticalModelFactory, TexturedModel.Factory horizontalModelFactory) {
        Identifier lv = verticalModelFactory.upload(block, this.modelCollector);
        Identifier lv2 = horizontalModelFactory.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(block, lv, lv2));
    }

    private Identifier createSubModel(Block block, String suffix, Model model, Function<Identifier, Texture> textureFactory) {
        return model.upload(block, suffix, textureFactory.apply(Texture.getSubId(block, suffix)), this.modelCollector);
    }

    private static BlockStateSupplier createPressurePlateBlockState(Block pressurePlateBlock, Identifier upModelId, Identifier downModelId) {
        return VariantsBlockStateSupplier.create(pressurePlateBlock).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, downModelId, upModelId));
    }

    private static BlockStateSupplier createSlabBlockState(Block slabBlock, Identifier bottomModelId, Identifier topModelId, Identifier fullModelId) {
        return VariantsBlockStateSupplier.create(slabBlock).coordinate(BlockStateVariantMap.create(Properties.SLAB_TYPE).register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, bottomModelId)).register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)).register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, fullModelId)));
    }

    private void registerSimpleCubeAll(Block block) {
        this.registerSingleton(block, TexturedModel.CUBE_ALL);
    }

    private void registerSingleton(Block block, TexturedModel.Factory modelFactory) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, modelFactory.upload(block, this.modelCollector)));
    }

    private void registerSingleton(Block block, Texture texture, Model model) {
        Identifier lv = model.upload(block, texture, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, lv));
    }

    private BlockTexturePool registerTexturePool(Block block, TexturedModel model) {
        return new BlockTexturePool(model.getTexture()).base(block, model.getModel());
    }

    private BlockTexturePool registerTexturePool(Block block, TexturedModel.Factory modelFactory) {
        TexturedModel lv = modelFactory.get(block);
        return new BlockTexturePool(lv.getTexture()).base(block, lv.getModel());
    }

    private BlockTexturePool registerCubeAllModelTexturePool(Block block) {
        return this.registerTexturePool(block, TexturedModel.CUBE_ALL);
    }

    private BlockTexturePool registerTexturePool(Texture texture) {
        return new BlockTexturePool(texture);
    }

    private void registerDoor(Block doorBlock) {
        Texture lv = Texture.topBottom(doorBlock);
        Identifier lv2 = Models.DOOR_BOTTOM.upload(doorBlock, lv, this.modelCollector);
        Identifier lv3 = Models.DOOR_BOTTOM_RH.upload(doorBlock, lv, this.modelCollector);
        Identifier lv4 = Models.DOOR_TOP.upload(doorBlock, lv, this.modelCollector);
        Identifier lv5 = Models.DOOR_TOP_RH.upload(doorBlock, lv, this.modelCollector);
        this.registerItemModel(doorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(doorBlock, lv2, lv3, lv4, lv5));
    }

    private void registerOrientableTrapdoor(Block trapdoorBlock) {
        Texture lv = Texture.texture(trapdoorBlock);
        Identifier lv2 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(trapdoorBlock, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(trapdoorBlock, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createOrientableTrapdoorBlockState(trapdoorBlock, lv2, lv3, lv4));
        this.registerParentedItemModel(trapdoorBlock, lv3);
    }

    private void registerTrapdoor(Block trapdoorBlock) {
        Texture lv = Texture.texture(trapdoorBlock);
        Identifier lv2 = Models.TEMPLATE_TRAPDOOR_TOP.upload(trapdoorBlock, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(trapdoorBlock, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(trapdoorBlock, lv2, lv3, lv4));
        this.registerParentedItemModel(trapdoorBlock, lv3);
    }

    private LogTexturePool registerLog(Block logBlock) {
        return new LogTexturePool(Texture.sideAndEndForTop(logBlock));
    }

    private void registerSimpleState(Block block) {
        this.registerStateWithModelReference(block, block);
    }

    private void registerStateWithModelReference(Block block, Block modelReference) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, ModelIds.getBlockModelId(modelReference)));
    }

    private void registerTintableCross(Block block, TintType tintType) {
        this.registerItemModel(block);
        this.registerTintableCrossBlockState(block, tintType);
    }

    private void registerTintableCross(Block block, TintType tintType, Texture texture) {
        this.registerItemModel(block);
        this.registerTintableCrossBlockState(block, tintType, texture);
    }

    private void registerTintableCrossBlockState(Block block, TintType tintType) {
        Texture lv = Texture.cross(block);
        this.registerTintableCrossBlockState(block, tintType, lv);
    }

    private void registerTintableCrossBlockState(Block block, TintType tintType, Texture crossTexture) {
        Identifier lv = tintType.getCrossModel().upload(block, crossTexture, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, lv));
    }

    private void registerFlowerPotPlant(Block plantBlock, Block flowerPotBlock, TintType tintType) {
        this.registerTintableCross(plantBlock, tintType);
        Texture lv = Texture.plant(plantBlock);
        Identifier lv2 = tintType.getFlowerPotCrossModel().upload(flowerPotBlock, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(flowerPotBlock, lv2));
    }

    private void registerCoralFan(Block coralFanBlock, Block coralWallFanBlock) {
        TexturedModel lv = TexturedModel.CORAL_FAN.get(coralFanBlock);
        Identifier lv2 = lv.upload(coralFanBlock, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(coralFanBlock, lv2));
        Identifier lv3 = Models.CORAL_WALL_FAN.upload(coralWallFanBlock, lv.getTexture(), this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(coralWallFanBlock, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
        this.registerItemModel(coralFanBlock);
    }

    private void registerGourd(Block stemBlock, Block attachedStemBlock) {
        this.registerItemModel(stemBlock.asItem());
        Texture lv = Texture.stem(stemBlock);
        Texture lv2 = Texture.stemAndUpper(stemBlock, attachedStemBlock);
        Identifier lv3 = Models.STEM_FRUIT.upload(attachedStemBlock, lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(attachedStemBlock, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).register(Direction.WEST, BlockStateVariant.create()).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(stemBlock).coordinate(BlockStateVariantMap.create(Properties.AGE_7).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, Models.STEM_GROWTH_STAGES[integer].upload(stemBlock, lv, this.modelCollector)))));
    }

    private void registerCoral(Block coral, Block deadCoral, Block coralBlock, Block deadCoralBlock, Block coralFan, Block deadCoralFan, Block coralWallFan, Block deadCoralWallFan) {
        this.registerTintableCross(coral, TintType.NOT_TINTED);
        this.registerTintableCross(deadCoral, TintType.NOT_TINTED);
        this.registerSimpleCubeAll(coralBlock);
        this.registerSimpleCubeAll(deadCoralBlock);
        this.registerCoralFan(coralFan, coralWallFan);
        this.registerCoralFan(deadCoralFan, deadCoralWallFan);
    }

    private void registerDoubleBlock(Block doubleBlock, TintType tintType) {
        this.registerItemModel(doubleBlock, "_top");
        Identifier lv = this.createSubModel(doubleBlock, "_top", tintType.getCrossModel(), Texture::cross);
        Identifier lv2 = this.createSubModel(doubleBlock, "_bottom", tintType.getCrossModel(), Texture::cross);
        this.registerDoubleBlock(doubleBlock, lv, lv2);
    }

    private void registerSunflower() {
        this.registerItemModel(Blocks.SUNFLOWER, "_front");
        Identifier lv = ModelIds.getBlockSubModelId(Blocks.SUNFLOWER, "_top");
        Identifier lv2 = this.createSubModel(Blocks.SUNFLOWER, "_bottom", TintType.NOT_TINTED.getCrossModel(), Texture::cross);
        this.registerDoubleBlock(Blocks.SUNFLOWER, lv, lv2);
    }

    private void registerTallSeagrass() {
        Identifier lv = this.createSubModel(Blocks.TALL_SEAGRASS, "_top", Models.TEMPLATE_SEAGRASS, Texture::texture);
        Identifier lv2 = this.createSubModel(Blocks.TALL_SEAGRASS, "_bottom", Models.TEMPLATE_SEAGRASS, Texture::texture);
        this.registerDoubleBlock(Blocks.TALL_SEAGRASS, lv, lv2);
    }

    private void registerDoubleBlock(Block block, Identifier upperHalfModelId, Identifier lowerHalfModelId) {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF).register(DoubleBlockHalf.LOWER, BlockStateVariant.create().put(VariantSettings.MODEL, lowerHalfModelId)).register(DoubleBlockHalf.UPPER, BlockStateVariant.create().put(VariantSettings.MODEL, upperHalfModelId))));
    }

    private void registerTurnableRail(Block rail) {
        Texture lv = Texture.rail(rail);
        Texture lv2 = Texture.rail(Texture.getSubId(rail, "_corner"));
        Identifier lv3 = Models.RAIL_FLAT.upload(rail, lv, this.modelCollector);
        Identifier lv4 = Models.RAIL_CURVED.upload(rail, lv2, this.modelCollector);
        Identifier lv5 = Models.TEMPLATE_RAIL_RAISED_NE.upload(rail, lv, this.modelCollector);
        Identifier lv6 = Models.TEMPLATE_RAIL_RAISED_SW.upload(rail, lv, this.modelCollector);
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(rail).coordinate(BlockStateVariantMap.create(Properties.RAIL_SHAPE).register(RailShape.NORTH_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).register(RailShape.EAST_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv6).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).register(RailShape.ASCENDING_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).register(RailShape.SOUTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).register(RailShape.SOUTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.NORTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(RailShape.NORTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerStraightRail(Block rail) {
        Identifier lv = this.createSubModel(rail, "", Models.RAIL_FLAT, Texture::rail);
        Identifier lv2 = this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
        Identifier lv3 = this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
        Identifier lv4 = this.createSubModel(rail, "_on", Models.RAIL_FLAT, Texture::rail);
        Identifier lv5 = this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
        Identifier lv6 = this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
        BlockStateVariantMap lv7 = BlockStateVariantMap.create(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE).register((boolean_, arg7) -> {
            switch (arg7) {
                case NORTH_SOUTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv4 : lv);
                }
                case EAST_WEST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv4 : lv).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_EAST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv5 : lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_WEST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv6 : lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_NORTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv5 : lv2);
                }
                case ASCENDING_SOUTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, boolean_ != false ? lv6 : lv3);
                }
            }
            throw new UnsupportedOperationException("Fix you generator!");
        });
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(rail).coordinate(lv7));
    }

    private BuiltinModelPool registerBuiltin(Identifier modelId, Block particleBlock) {
        return new BuiltinModelPool(modelId, particleBlock);
    }

    private BuiltinModelPool registerBuiltin(Block block, Block particleBlock) {
        return new BuiltinModelPool(ModelIds.getBlockModelId(block), particleBlock);
    }

    private void registerBuiltinWithParticle(Block block, Item particleSource) {
        Identifier lv = Models.PARTICLE.upload(block, Texture.particle(particleSource), this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, lv));
    }

    private void registerBuiltinWithParticle(Block block, Identifier particleSource) {
        Identifier lv = Models.PARTICLE.upload(block, Texture.particle(particleSource), this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, lv));
    }

    private void registerCarpet(Block wool, Block carpet) {
        this.registerSingleton(wool, TexturedModel.CUBE_ALL);
        Identifier lv = TexturedModel.CARPET.get(wool).upload(carpet, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(carpet, lv));
    }

    private void registerRandomHorizontalRotations(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block lv : blocks) {
            Identifier lv2 = modelFactory.upload(lv, this.modelCollector);
            this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(lv, lv2));
        }
    }

    private void registerSouthDefaultHorizontalFacing(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block lv : blocks) {
            Identifier lv2 = modelFactory.upload(lv, this.modelCollector);
            this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lv, BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
        }
    }

    private void registerGlassPane(Block glass, Block glassPane) {
        this.registerSimpleCubeAll(glass);
        Texture lv = Texture.paneAndTopForEdge(glass, glassPane);
        Identifier lv2 = Models.TEMPLATE_GLASS_PANE_POST.upload(glassPane, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(glassPane, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(glassPane, lv, this.modelCollector);
        Identifier lv5 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(glassPane, lv, this.modelCollector);
        Identifier lv6 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(glassPane, lv, this.modelCollector);
        Item lv7 = glassPane.asItem();
        Models.GENERATED.upload(ModelIds.getItemModelId(lv7), Texture.layer0(glass), this.modelCollector);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(glassPane).with(BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv6).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }

    private void registerCommandBlock(Block commandBlock) {
        Texture lv = Texture.sideFrontBack(commandBlock);
        Identifier lv2 = Models.TEMPLATE_COMMAND_BLOCK.upload(commandBlock, lv, this.modelCollector);
        Identifier lv3 = this.createSubModel(commandBlock, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, arg2 -> lv.copyAndAdd(TextureKey.SIDE, (Identifier)arg2));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(commandBlock).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.CONDITIONAL, lv3, lv2)).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
    }

    private void registerAnvil(Block anvil) {
        Identifier lv = TexturedModel.TEMPLATE_ANVIL.upload(anvil, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(anvil, lv).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private List<BlockStateVariant> getBambooBlockStateVariants(int age) {
        String string = "_age" + age;
        return IntStream.range(1, 5).mapToObj(i -> BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, i + string))).collect(Collectors.toList());
    }

    private void registerBamboo() {
        this.excludeFromSimpleItemModelGeneration(Blocks.BAMBOO);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.BAMBOO).with((When)When.create().set(Properties.AGE_1, 0), this.getBambooBlockStateVariants(0)).with((When)When.create().set(Properties.AGE_1, 1), this.getBambooBlockStateVariants(1)).with((When)When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.SMALL), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_small_leaves"))).with((When)When.create().set(Properties.BAMBOO_LEAVES, BambooLeaves.LARGE), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.BAMBOO, "_large_leaves"))));
    }

    private BlockStateVariantMap createUpDefaultFacingVariantMap() {
        return BlockStateVariantMap.create(Properties.FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(Direction.UP, BlockStateVariant.create()).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    private void registerBarrel() {
        Identifier lv = Texture.getSubId(Blocks.BARREL, "_top_open");
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.BARREL).coordinate(this.createUpDefaultFacingVariantMap()).coordinate(BlockStateVariantMap.create(Properties.OPEN).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.BARREL, this.modelCollector))).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.BARREL).texture(arg2 -> arg2.put(TextureKey.TOP, lv)).upload(Blocks.BARREL, "_open", this.modelCollector)))));
    }

    private static <T extends Comparable<T>> BlockStateVariantMap createValueFencedModelMap(Property<T> property, T fence, Identifier higherOrEqualModelId, Identifier lowerModelId) {
        BlockStateVariant lv = BlockStateVariant.create().put(VariantSettings.MODEL, higherOrEqualModelId);
        BlockStateVariant lv2 = BlockStateVariant.create().put(VariantSettings.MODEL, lowerModelId);
        return BlockStateVariantMap.create(property).register(comparable2 -> {
            boolean bl = comparable2.compareTo(fence) >= 0;
            return bl ? lv : lv2;
        });
    }

    private void registerBeehive(Block beehive, Function<Block, Texture> textureGetter) {
        Texture lv = textureGetter.apply(beehive).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
        Texture lv2 = lv.copyAndAdd(TextureKey.FRONT, Texture.getSubId(beehive, "_front_honey"));
        Identifier lv3 = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, lv, this.modelCollector);
        Identifier lv4 = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_honey", lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(beehive).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.HONEY_LEVEL, 5, lv4, lv3)));
    }

    private void registerCrop(Block crop, Property<Integer> ageProperty, int ... ageTextureIndices) {
        if (ageProperty.getValues().size() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        BlockStateVariantMap lv = BlockStateVariantMap.create(ageProperty).register(arg_0 -> this.method_25589(ageTextureIndices, (Int2ObjectMap)int2ObjectMap, crop, arg_0));
        this.registerItemModel(crop.asItem());
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(crop).coordinate(lv));
    }

    private void registerBell() {
        Identifier lv = ModelIds.getBlockSubModelId(Blocks.BELL, "_floor");
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.BELL, "_ceiling");
        Identifier lv3 = ModelIds.getBlockSubModelId(Blocks.BELL, "_wall");
        Identifier lv4 = ModelIds.getBlockSubModelId(Blocks.BELL, "_between_walls");
        this.registerItemModel(Items.BELL);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.BELL).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.ATTACHMENT).register(Direction.NORTH, Attachment.FLOOR, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).register(Direction.SOUTH, Attachment.FLOOR, BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, Attachment.FLOOR, BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, Attachment.FLOOR, BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, Attachment.CEILING, BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).register(Direction.SOUTH, Attachment.CEILING, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, Attachment.CEILING, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, Attachment.CEILING, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, Attachment.SINGLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.SOUTH, Attachment.SINGLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.EAST, Attachment.SINGLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).register(Direction.WEST, Attachment.SINGLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.SOUTH, Attachment.DOUBLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.NORTH, Attachment.DOUBLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, Attachment.DOUBLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).register(Direction.WEST, Attachment.DOUBLE_WALL, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R180))));
    }

    private void registerGrindstone() {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.GRINDSTONE, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.GRINDSTONE))).coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING).register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create()).register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerCooker(Block cooker, TexturedModel.Factory modelFactory) {
        Identifier lv = modelFactory.upload(cooker, this.modelCollector);
        Identifier lv2 = Texture.getSubId(cooker, "_front_on");
        Identifier lv3 = modelFactory.get(cooker).texture(arg2 -> arg2.put(TextureKey.FRONT, lv2)).upload(cooker, "_on", this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(cooker).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv3, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void method_27166(Block ... args) {
        Identifier lv = ModelIds.getMinecraftNamespacedBlock("campfire_off");
        for (Block lv2 : args) {
            Identifier lv3 = Models.TEMPLATE_CAMPFIRE.upload(lv2, Texture.method_27167(lv2), this.modelCollector);
            this.registerItemModel(lv2.asItem());
            this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lv2).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv3, lv)).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
        }
    }

    private void registerBookshelf() {
        Texture lv = Texture.sideEnd(Texture.getId(Blocks.BOOKSHELF), Texture.getId(Blocks.OAK_PLANKS));
        Identifier lv2 = Models.CUBE_COLUMN.upload(Blocks.BOOKSHELF, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.BOOKSHELF, lv2));
    }

    private void registerRedstone() {
        this.registerItemModel(Items.REDSTONE);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.REDSTONE_WIRE).with(When.anyOf(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE), When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}).set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP})), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_dot"))).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side0"))).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt0"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side_alt1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, (Comparable)((Object)WireConnection.SIDE), (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_side1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).with((When)When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up"))).with((When)When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).with((When)When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP), BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getMinecraftNamespacedBlock("redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }

    private void registerComparator() {
        this.registerItemModel(Items.COMPARATOR);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.COMPARATOR).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()).coordinate(BlockStateVariantMap.create(Properties.COMPARATOR_MODE, Properties.POWERED).register(ComparatorMode.COMPARE, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.COMPARATOR))).register(ComparatorMode.COMPARE, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on"))).register(ComparatorMode.SUBTRACT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_subtract"))).register(ComparatorMode.SUBTRACT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COMPARATOR, "_on_subtract")))));
    }

    private void registerSmoothStone() {
        Texture lv = Texture.all(Blocks.SMOOTH_STONE);
        Texture lv2 = Texture.sideEnd(Texture.getSubId(Blocks.SMOOTH_STONE_SLAB, "_side"), lv.getTexture(TextureKey.TOP));
        Identifier lv3 = Models.SLAB.upload(Blocks.SMOOTH_STONE_SLAB, lv2, this.modelCollector);
        Identifier lv4 = Models.SLAB_TOP.upload(Blocks.SMOOTH_STONE_SLAB, lv2, this.modelCollector);
        Identifier lv5 = Models.CUBE_COLUMN.uploadWithoutVariant(Blocks.SMOOTH_STONE_SLAB, "_double", lv2, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(Blocks.SMOOTH_STONE_SLAB, lv3, lv4, lv5));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SMOOTH_STONE, Models.CUBE_ALL.upload(Blocks.SMOOTH_STONE, lv, this.modelCollector)));
    }

    private void registerBrewingStand() {
        this.registerItemModel(Items.BREWING_STAND);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.BREWING_STAND).with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getId(Blocks.BREWING_STAND))).with((When)When.create().set(Properties.HAS_BOTTLE_0, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_bottle0"))).with((When)When.create().set(Properties.HAS_BOTTLE_1, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_bottle1"))).with((When)When.create().set(Properties.HAS_BOTTLE_2, true), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_bottle2"))).with((When)When.create().set(Properties.HAS_BOTTLE_0, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_empty0"))).with((When)When.create().set(Properties.HAS_BOTTLE_1, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_empty1"))).with((When)When.create().set(Properties.HAS_BOTTLE_2, false), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.BREWING_STAND, "_empty2"))));
    }

    private void registerMushroomBlock(Block mushroomBlock) {
        Identifier lv = Models.TEMPLATE_SINGLE_FACE.upload(mushroomBlock, Texture.texture(mushroomBlock), this.modelCollector);
        Identifier lv2 = ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside");
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(mushroomBlock).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.DOWN, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.UP, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.DOWN, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, false)));
        this.registerParentedItemModel(mushroomBlock, TexturedModel.CUBE_ALL.upload(mushroomBlock, "_inventory", this.modelCollector));
    }

    private void registerCake() {
        this.registerItemModel(Items.CAKE);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CAKE).coordinate(BlockStateVariantMap.create(Properties.BITES).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAKE))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice1"))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice2"))).register((Integer)3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice3"))).register((Integer)4, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice4"))).register((Integer)5, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice5"))).register((Integer)6, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAKE, "_slice6")))));
    }

    private void registerCartographyTable() {
        Texture lv = new Texture().put(TextureKey.PARTICLE, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.DOWN, Texture.getId(Blocks.DARK_OAK_PLANKS)).put(TextureKey.UP, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureKey.NORTH, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.EAST, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureKey.SOUTH, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureKey.WEST, Texture.getSubId(Blocks.CARTOGRAPHY_TABLE, "_side2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.CARTOGRAPHY_TABLE, Models.CUBE.upload(Blocks.CARTOGRAPHY_TABLE, lv, this.modelCollector)));
    }

    private void registerSmithingTable() {
        Texture lv = new Texture().put(TextureKey.PARTICLE, Texture.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.DOWN, Texture.getSubId(Blocks.SMITHING_TABLE, "_bottom")).put(TextureKey.UP, Texture.getSubId(Blocks.SMITHING_TABLE, "_top")).put(TextureKey.NORTH, Texture.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.SOUTH, Texture.getSubId(Blocks.SMITHING_TABLE, "_front")).put(TextureKey.EAST, Texture.getSubId(Blocks.SMITHING_TABLE, "_side")).put(TextureKey.WEST, Texture.getSubId(Blocks.SMITHING_TABLE, "_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SMITHING_TABLE, Models.CUBE.upload(Blocks.SMITHING_TABLE, lv, this.modelCollector)));
    }

    private void registerCubeWithCustomTexture(Block block, Block otherTextureSource, BiFunction<Block, Block, Texture> textureFactory) {
        Texture lv = textureFactory.apply(block, otherTextureSource);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, Models.CUBE.upload(block, lv, this.modelCollector)));
    }

    private void registerPumpkins() {
        Texture lv = Texture.sideEnd(Blocks.PUMPKIN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.PUMPKIN, ModelIds.getBlockModelId(Blocks.PUMPKIN)));
        this.registerNorthDefaultHorizontalRotatable(Blocks.CARVED_PUMPKIN, lv);
        this.registerNorthDefaultHorizontalRotatable(Blocks.JACK_O_LANTERN, lv);
    }

    private void registerNorthDefaultHorizontalRotatable(Block block, Texture texture) {
        Identifier lv = Models.ORIENTABLE.upload(block, texture.copyAndAdd(TextureKey.FRONT, Texture.getId(block)), this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerCauldron() {
        this.registerItemModel(Items.CAULDRON);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CAULDRON).coordinate(BlockStateVariantMap.create(Properties.LEVEL_3).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAULDRON))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level1"))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level2"))).register((Integer)3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level3")))));
    }

    private void registerCubeColumn(Block block, Block endTexture) {
        Texture lv = new Texture().put(TextureKey.END, Texture.getSubId(endTexture, "_top")).put(TextureKey.SIDE, Texture.getId(block));
        this.registerSingleton(block, lv, Models.CUBE_COLUMN);
    }

    private void registerChorusFlower() {
        Texture lv = Texture.texture(Blocks.CHORUS_FLOWER);
        Identifier lv2 = Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, lv, this.modelCollector);
        Identifier lv3 = this.createSubModel(Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, arg2 -> lv.copyAndAdd(TextureKey.TEXTURE, (Identifier)arg2));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CHORUS_FLOWER).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.AGE_5, 5, lv3, lv2)));
    }

    private void registerFurnaceLikeOrientable(Block block) {
        Texture lv = new Texture().put(TextureKey.TOP, Texture.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.SIDE, Texture.getSubId(Blocks.FURNACE, "_side")).put(TextureKey.FRONT, Texture.getSubId(block, "_front"));
        Texture lv2 = new Texture().put(TextureKey.SIDE, Texture.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.FRONT, Texture.getSubId(block, "_front_vertical"));
        Identifier lv3 = Models.ORIENTABLE.upload(block, lv, this.modelCollector);
        Identifier lv4 = Models.ORIENTABLE_VERTICAL.upload(block, lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerEndPortalFrame() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.END_PORTAL_FRAME);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.END_PORTAL_FRAME, "_filled");
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.END_PORTAL_FRAME).coordinate(BlockStateVariantMap.create(Properties.EYE).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, lv2))).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private void registerChorusPlant() {
        Identifier lv = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_side");
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside");
        Identifier lv3 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside1");
        Identifier lv4 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside2");
        Identifier lv5 = ModelIds.getBlockSubModelId(Blocks.CHORUS_PLANT, "_noside3");
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.CHORUS_PLANT).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.DOWN, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2), BlockStateVariant.create().put(VariantSettings.MODEL, lv3), BlockStateVariant.create().put(VariantSettings.MODEL, lv4), BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.UP, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.DOWN, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.WEIGHT, 2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)));
    }

    private void registerComposter() {
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.COMPOSTER).with(BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getId(Blocks.COMPOSTER))).with((When)When.create().set(Properties.LEVEL_8, 1), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents1"))).with((When)When.create().set(Properties.LEVEL_8, 2), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents2"))).with((When)When.create().set(Properties.LEVEL_8, 3), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents3"))).with((When)When.create().set(Properties.LEVEL_8, 4), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents4"))).with((When)When.create().set(Properties.LEVEL_8, 5), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents5"))).with((When)When.create().set(Properties.LEVEL_8, 6), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents6"))).with((When)When.create().set(Properties.LEVEL_8, 7), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents7"))).with((When)When.create().set(Properties.LEVEL_8, 8), BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.COMPOSTER, "_contents_ready"))));
    }

    private void registerNetherrackBottomCustomTop(Block block) {
        Texture lv = new Texture().put(TextureKey.BOTTOM, Texture.getId(Blocks.NETHERRACK)).put(TextureKey.TOP, Texture.getId(block)).put(TextureKey.SIDE, Texture.getSubId(block, "_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, Models.CUBE_BOTTOM_TOP.upload(block, lv, this.modelCollector)));
    }

    private void registerDaylightDetector() {
        Identifier lv = Texture.getSubId(Blocks.DAYLIGHT_DETECTOR, "_side");
        Texture lv2 = new Texture().put(TextureKey.TOP, Texture.getSubId(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureKey.SIDE, lv);
        Texture lv3 = new Texture().put(TextureKey.TOP, Texture.getSubId(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureKey.SIDE, lv);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.DAYLIGHT_DETECTOR).coordinate(BlockStateVariantMap.create(Properties.INVERTED).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(Blocks.DAYLIGHT_DETECTOR, lv2, this.modelCollector))).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(ModelIds.getBlockSubModelId(Blocks.DAYLIGHT_DETECTOR, "_inverted"), lv3, this.modelCollector)))));
    }

    private void registerEndRod() {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.END_ROD, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.END_ROD))).coordinate(this.createUpDefaultFacingVariantMap()));
    }

    private void registerFarmland() {
        Texture lv = new Texture().put(TextureKey.DIRT, Texture.getId(Blocks.DIRT)).put(TextureKey.TOP, Texture.getId(Blocks.FARMLAND));
        Texture lv2 = new Texture().put(TextureKey.DIRT, Texture.getId(Blocks.DIRT)).put(TextureKey.TOP, Texture.getSubId(Blocks.FARMLAND, "_moist"));
        Identifier lv3 = Models.TEMPLATE_FARMLAND.upload(Blocks.FARMLAND, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_FARMLAND.upload(Texture.getSubId(Blocks.FARMLAND, "_moist"), lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.FARMLAND).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.MOISTURE, 7, lv4, lv3)));
    }

    private List<Identifier> getFireFloorModels(Block texture) {
        Identifier lv = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor0"), Texture.fire0(texture), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(texture, "_floor1"), Texture.fire1(texture), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2);
    }

    private List<Identifier> getFireSideModels(Block texture) {
        Identifier lv = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side0"), Texture.fire0(texture), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(texture, "_side1"), Texture.fire1(texture), this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(texture, "_side_alt0"), Texture.fire0(texture), this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(texture, "_side_alt1"), Texture.fire1(texture), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2, (Object)lv3, (Object)lv4);
    }

    private List<Identifier> getFireUpModels(Block texture) {
        Identifier lv = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up0"), Texture.fire0(texture), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(texture, "_up1"), Texture.fire1(texture), this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt0"), Texture.fire0(texture), this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(texture, "_up_alt1"), Texture.fire1(texture), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2, (Object)lv3, (Object)lv4);
    }

    private static List<BlockStateVariant> buildBlockStateVariants(List<Identifier> modelIds, UnaryOperator<BlockStateVariant> processor) {
        return modelIds.stream().map(arg -> BlockStateVariant.create().put(VariantSettings.MODEL, arg)).map(processor).collect(Collectors.toList());
    }

    private void registerFire() {
        When.PropertyCondition lv = When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false).set(Properties.UP, false);
        List<Identifier> list = this.getFireFloorModels(Blocks.FIRE);
        List<Identifier> list2 = this.getFireSideModels(Blocks.FIRE);
        List<Identifier> list3 = this.getFireUpModels(Blocks.FIRE);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.FIRE).with((When)lv, BlockStateModelGenerator.buildBlockStateVariants(list, arg -> arg)).with(When.anyOf(When.create().set(Properties.NORTH, true), lv), BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg)).with(When.anyOf(When.create().set(Properties.EAST, true), lv), BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R90))).with(When.anyOf(When.create().set(Properties.SOUTH, true), lv), BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R180))).with(When.anyOf(When.create().set(Properties.WEST, true), lv), BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R270))).with((When)When.create().set(Properties.UP, true), BlockStateModelGenerator.buildBlockStateVariants(list3, arg -> arg)));
    }

    private void registerSoulFire() {
        List<Identifier> list = this.getFireFloorModels(Blocks.SOUL_FIRE);
        List<Identifier> list2 = this.getFireSideModels(Blocks.SOUL_FIRE);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.SOUL_FIRE).with(BlockStateModelGenerator.buildBlockStateVariants(list, arg -> arg)).with(BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg)).with(BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R90))).with(BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R180))).with(BlockStateModelGenerator.buildBlockStateVariants(list2, arg -> arg.put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerLantern(Block lantern) {
        Identifier lv = TexturedModel.TEMPLATE_LANTERN.upload(lantern, this.modelCollector);
        Identifier lv2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(lantern, this.modelCollector);
        this.registerItemModel(lantern.asItem());
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.HANGING, lv2, lv)));
    }

    private void registerFrostedIce() {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.FROSTED_ICE).coordinate(BlockStateVariantMap.create(Properties.AGE_3).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_0", Models.CUBE_ALL, Texture::all))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_1", Models.CUBE_ALL, Texture::all))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_2", Models.CUBE_ALL, Texture::all))).register((Integer)3, BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.FROSTED_ICE, "_3", Models.CUBE_ALL, Texture::all)))));
    }

    private void registerTopSoils() {
        Identifier lv = Texture.getId(Blocks.DIRT);
        Texture lv2 = new Texture().put(TextureKey.BOTTOM, lv).inherit(TextureKey.BOTTOM, TextureKey.PARTICLE).put(TextureKey.TOP, Texture.getSubId(Blocks.GRASS_BLOCK, "_top")).put(TextureKey.SIDE, Texture.getSubId(Blocks.GRASS_BLOCK, "_snow"));
        BlockStateVariant lv3 = BlockStateVariant.create().put(VariantSettings.MODEL, Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", lv2, this.modelCollector));
        this.registerTopSoil(Blocks.GRASS_BLOCK, ModelIds.getBlockModelId(Blocks.GRASS_BLOCK), lv3);
        Identifier lv4 = TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.MYCELIUM).texture(arg2 -> arg2.put(TextureKey.BOTTOM, lv)).upload(Blocks.MYCELIUM, this.modelCollector);
        this.registerTopSoil(Blocks.MYCELIUM, lv4, lv3);
        Identifier lv5 = TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.PODZOL).texture(arg2 -> arg2.put(TextureKey.BOTTOM, lv)).upload(Blocks.PODZOL, this.modelCollector);
        this.registerTopSoil(Blocks.PODZOL, lv5, lv3);
    }

    private void registerTopSoil(Block topSoil, Identifier modelId, BlockStateVariant snowyVariant) {
        List<BlockStateVariant> list = Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(modelId));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(topSoil).coordinate(BlockStateVariantMap.create(Properties.SNOWY).register((Boolean)true, snowyVariant).register((Boolean)false, list)));
    }

    private void registerCocoa() {
        this.registerItemModel(Items.COCOA_BEANS);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.COCOA).coordinate(BlockStateVariantMap.create(Properties.AGE_2).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage0"))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage1"))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage2")))).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private void registerGrassPath() {
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(Blocks.GRASS_PATH, ModelIds.getBlockModelId(Blocks.GRASS_PATH)));
    }

    private void registerPressurePlate(Block pressurePlate, Block textureSource) {
        Texture lv = Texture.texture(textureSource);
        Identifier lv2 = Models.PRESSURE_PLATE_UP.upload(pressurePlate, lv, this.modelCollector);
        Identifier lv3 = Models.PRESSURE_PLATE_DOWN.upload(pressurePlate, lv, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(pressurePlate).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.POWER, 1, lv3, lv2)));
    }

    private void registerHopper() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.HOPPER);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.HOPPER, "_side");
        this.registerItemModel(Items.HOPPER);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.HOPPER).coordinate(BlockStateVariantMap.create(Properties.HOPPER_FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerInfested(Block modelSource, Block infested) {
        Identifier lv = ModelIds.getBlockModelId(modelSource);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(infested, BlockStateVariant.create().put(VariantSettings.MODEL, lv)));
        this.registerParentedItemModel(infested, lv);
    }

    private void registerIronBars() {
        Identifier lv = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post_ends");
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_post");
        Identifier lv3 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap");
        Identifier lv4 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_cap_alt");
        Identifier lv5 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side");
        Identifier lv6 = ModelIds.getBlockSubModelId(Blocks.IRON_BARS, "_side_alt");
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(Blocks.IRON_BARS).with(BlockStateVariant.create().put(VariantSettings.MODEL, lv)).with((When)When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).with((When)When.create().set(Properties.NORTH, true).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).with((When)When.create().set(Properties.NORTH, false).set(Properties.EAST, true).set(Properties.SOUTH, false).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, true).set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).with((When)When.create().set(Properties.NORTH, false).set(Properties.EAST, false).set(Properties.SOUTH, false).set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv6).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
        this.registerItemModel(Blocks.IRON_BARS);
    }

    private void registerNorthDefaultHorizontalRotation(Block block) {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(block))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerLever() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.LEVER);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.LEVER, "_on");
        this.registerItemModel(Blocks.LEVER);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.LEVER).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, lv, lv2)).coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING).register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create()).register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerLilyPad() {
        this.registerItemModel(Blocks.LILY_PAD);
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(Blocks.LILY_PAD, ModelIds.getBlockModelId(Blocks.LILY_PAD)));
    }

    private void registerNetherPortal() {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.NETHER_PORTAL).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_AXIS).register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ns"))).register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.NETHER_PORTAL, "_ew")))));
    }

    private void registerNetherrack() {
        Identifier lv = TexturedModel.CUBE_ALL.upload(Blocks.NETHERRACK, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.NETHERRACK, BlockStateVariant.create().put(VariantSettings.MODEL, lv), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R270), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R270), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R270), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R270)));
    }

    private void registerObserver() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.OBSERVER);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.OBSERVER, "_on");
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.OBSERVER).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, lv2, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
    }

    private void registerPistons() {
        Texture lv = new Texture().put(TextureKey.BOTTOM, Texture.getSubId(Blocks.PISTON, "_bottom")).put(TextureKey.SIDE, Texture.getSubId(Blocks.PISTON, "_side"));
        Identifier lv2 = Texture.getSubId(Blocks.PISTON, "_top_sticky");
        Identifier lv3 = Texture.getSubId(Blocks.PISTON, "_top");
        Texture lv4 = lv.copyAndAdd(TextureKey.PLATFORM, lv2);
        Texture lv5 = lv.copyAndAdd(TextureKey.PLATFORM, lv3);
        Identifier lv6 = ModelIds.getBlockSubModelId(Blocks.PISTON, "_base");
        this.registerPiston(Blocks.PISTON, lv6, lv5);
        this.registerPiston(Blocks.STICKY_PISTON, lv6, lv4);
        Identifier lv7 = Models.CUBE_BOTTOM_TOP.upload(Blocks.PISTON, "_inventory", lv.copyAndAdd(TextureKey.TOP, lv3), this.modelCollector);
        Identifier lv8 = Models.CUBE_BOTTOM_TOP.upload(Blocks.STICKY_PISTON, "_inventory", lv.copyAndAdd(TextureKey.TOP, lv2), this.modelCollector);
        this.registerParentedItemModel(Blocks.PISTON, lv7);
        this.registerParentedItemModel(Blocks.STICKY_PISTON, lv8);
    }

    private void registerPiston(Block piston, Identifier extendedModelId, Texture texture) {
        Identifier lv = Models.TEMPLATE_PISTON.upload(piston, texture, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(piston).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.EXTENDED, extendedModelId, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
    }

    private void registerPistonHead() {
        Texture lv = new Texture().put(TextureKey.UNSTICKY, Texture.getSubId(Blocks.PISTON, "_top")).put(TextureKey.SIDE, Texture.getSubId(Blocks.PISTON, "_side"));
        Texture lv2 = lv.copyAndAdd(TextureKey.PLATFORM, Texture.getSubId(Blocks.PISTON, "_top_sticky"));
        Texture lv3 = lv.copyAndAdd(TextureKey.PLATFORM, Texture.getSubId(Blocks.PISTON, "_top"));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.PISTON_HEAD).coordinate(BlockStateVariantMap.create(Properties.SHORT, Properties.PISTON_TYPE).register((Boolean)false, PistonType.DEFAULT, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head", lv3, this.modelCollector))).register((Boolean)false, PistonType.STICKY, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head_sticky", lv2, this.modelCollector))).register((Boolean)true, PistonType.DEFAULT, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short", lv3, this.modelCollector))).register((Boolean)true, PistonType.STICKY, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short_sticky", lv2, this.modelCollector)))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
    }

    private void registerScaffolding() {
        Identifier lv = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_stable");
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.SCAFFOLDING, "_unstable");
        this.registerParentedItemModel(Blocks.SCAFFOLDING, lv);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.SCAFFOLDING).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.BOTTOM, lv2, lv)));
    }

    private void registerRedstoneLamp() {
        Identifier lv = TexturedModel.CUBE_ALL.upload(Blocks.REDSTONE_LAMP, this.modelCollector);
        Identifier lv2 = this.createSubModel(Blocks.REDSTONE_LAMP, "_on", Models.CUBE_ALL, Texture::all);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_LAMP).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv2, lv)));
    }

    private void registerTorch(Block torch, Block wallTorch) {
        Texture lv = Texture.torch(torch);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(torch, Models.TEMPLATE_TORCH.upload(torch, lv, this.modelCollector)));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(wallTorch, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_TORCH_WALL.upload(wallTorch, lv, this.modelCollector))).coordinate(BlockStateModelGenerator.createEastDefaultHorizontalRotationStates()));
        this.registerItemModel(torch);
        this.excludeFromSimpleItemModelGeneration(wallTorch);
    }

    private void registerRedstoneTorch() {
        Texture lv = Texture.torch(Blocks.REDSTONE_TORCH);
        Texture lv2 = Texture.torch(Texture.getSubId(Blocks.REDSTONE_TORCH, "_off"));
        Identifier lv3 = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_TORCH.upload(Blocks.REDSTONE_TORCH, "_off", lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_TORCH).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv3, lv4)));
        Identifier lv5 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, lv, this.modelCollector);
        Identifier lv6 = Models.TEMPLATE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, "_off", lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.REDSTONE_WALL_TORCH).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv5, lv6)).coordinate(BlockStateModelGenerator.createEastDefaultHorizontalRotationStates()));
        this.registerItemModel(Blocks.REDSTONE_TORCH);
        this.excludeFromSimpleItemModelGeneration(Blocks.REDSTONE_WALL_TORCH);
    }

    private void registerRepeater() {
        this.registerItemModel(Items.REPEATER);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.REPEATER).coordinate(BlockStateVariantMap.create(Properties.DELAY, Properties.LOCKED, Properties.POWERED).register((integer, boolean_, boolean2) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('_').append(integer).append("tick");
            if (boolean2.booleanValue()) {
                stringBuilder.append("_on");
            }
            if (boolean_.booleanValue()) {
                stringBuilder.append("_locked");
            }
            return BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.REPEATER, stringBuilder.toString()));
        })).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private void registerSeaPickle() {
        this.registerItemModel(Items.SEA_PICKLE);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.SEA_PICKLE).coordinate(BlockStateVariantMap.create(Properties.PICKLES, Properties.WATERLOGGED).register((Integer)1, (Boolean)false, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("dead_sea_pickle")))).register((Integer)2, (Boolean)false, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("two_dead_sea_pickles")))).register((Integer)3, (Boolean)false, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("three_dead_sea_pickles")))).register((Integer)4, (Boolean)false, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("four_dead_sea_pickles")))).register((Integer)1, (Boolean)true, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("sea_pickle")))).register((Integer)2, (Boolean)true, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("two_sea_pickles")))).register((Integer)3, (Boolean)true, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("three_sea_pickles")))).register((Integer)4, (Boolean)true, Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(ModelIds.getMinecraftNamespacedBlock("four_sea_pickles"))))));
    }

    private void registerSnows() {
        Texture lv = Texture.all(Blocks.SNOW);
        Identifier lv2 = Models.CUBE_ALL.upload(Blocks.SNOW_BLOCK, lv, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.SNOW).coordinate(BlockStateVariantMap.create(Properties.LAYERS).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, integer < 8 ? ModelIds.getBlockSubModelId(Blocks.SNOW, "_height" + integer * 2) : lv2))));
        this.registerParentedItemModel(Blocks.SNOW, ModelIds.getBlockSubModelId(Blocks.SNOW, "_height2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.SNOW_BLOCK, lv2));
    }

    private void registerStonecutter() {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.STONECUTTER, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.STONECUTTER))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerStructureBlock() {
        Identifier lv = TexturedModel.CUBE_ALL.upload(Blocks.STRUCTURE_BLOCK, this.modelCollector);
        this.registerParentedItemModel(Blocks.STRUCTURE_BLOCK, lv);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.STRUCTURE_BLOCK).coordinate(BlockStateVariantMap.create(Properties.STRUCTURE_BLOCK_MODE).register(arg -> BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.STRUCTURE_BLOCK, "_" + arg.asString(), Models.CUBE_ALL, Texture::all)))));
    }

    private void registerSweetBerryBush() {
        this.registerItemModel(Items.SWEET_BERRIES);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.SWEET_BERRY_BUSH).coordinate(BlockStateVariantMap.create(Properties.AGE_3).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, this.createSubModel(Blocks.SWEET_BERRY_BUSH, "_stage" + integer, Models.CROSS, Texture::cross)))));
    }

    private void registerTripwire() {
        this.registerItemModel(Items.STRING);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.TRIPWIRE).coordinate(BlockStateVariantMap.create(Properties.ATTACHED, Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.WEST).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns"))).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n"))).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_n")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne"))).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns"))).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_ns")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse"))).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_nsew"))).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n"))).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_n")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne"))).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ne")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns"))).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_ns")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse"))).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nse")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.TRIPWIRE, "_attached_nsew")))));
    }

    private void registerTripwireHook() {
        this.registerItemModel(Blocks.TRIPWIRE_HOOK);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.TRIPWIRE_HOOK).coordinate(BlockStateVariantMap.create(Properties.ATTACHED, Properties.POWERED).register((boolean_, boolean2) -> BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.TRIPWIRE_HOOK, (boolean_ != false ? "_attached" : "") + (boolean2 != false ? "_on" : ""))))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private Identifier getTurtleEggModel(int eggs, String prefix, Texture texture) {
        switch (eggs) {
            case 1: {
                return Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock(prefix + "turtle_egg"), texture, this.modelCollector);
            }
            case 2: {
                return Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("two_" + prefix + "turtle_eggs"), texture, this.modelCollector);
            }
            case 3: {
                return Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("three_" + prefix + "turtle_eggs"), texture, this.modelCollector);
            }
            case 4: {
                return Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("four_" + prefix + "turtle_eggs"), texture, this.modelCollector);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Identifier getTurtleEggModel(Integer eggs, Integer hatch) {
        switch (hatch) {
            case 0: {
                return this.getTurtleEggModel(eggs, "", Texture.all(Texture.getId(Blocks.TURTLE_EGG)));
            }
            case 1: {
                return this.getTurtleEggModel(eggs, "slightly_cracked_", Texture.all(Texture.getSubId(Blocks.TURTLE_EGG, "_slightly_cracked")));
            }
            case 2: {
                return this.getTurtleEggModel(eggs, "very_cracked_", Texture.all(Texture.getSubId(Blocks.TURTLE_EGG, "_very_cracked")));
            }
        }
        throw new UnsupportedOperationException();
    }

    private void registerTurtleEgg() {
        this.registerItemModel(Items.TURTLE_EGG);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.TURTLE_EGG).coordinate(BlockStateVariantMap.create(Properties.EGGS, Properties.HATCH).registerVariants((integer, integer2) -> Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(this.getTurtleEggModel((Integer)integer, (Integer)integer2))))));
    }

    private void registerVine() {
        this.registerItemModel(Blocks.VINE);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.VINE).coordinate(BlockStateVariantMap.create(Properties.EAST, Properties.NORTH, Properties.SOUTH, Properties.UP, Properties.WEST).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1"))).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1"))).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2"))).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2_opposite"))).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2_opposite")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3"))).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_4"))).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_u"))).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u"))).register((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_1u")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u"))).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u_opposite"))).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_2u_opposite")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u"))).register((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u")).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u")).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_3u")).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.VINE, "_4u")))));
    }

    private void registerMagmaBlock() {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.MAGMA_BLOCK, Models.CUBE_ALL.upload(Blocks.MAGMA_BLOCK, Texture.all(ModelIds.getMinecraftNamespacedBlock("magma")), this.modelCollector)));
    }

    private void registerShulkerBox(Block shulkerBox) {
        this.registerSingleton(shulkerBox, TexturedModel.PARTICLE);
        Models.TEMPLATE_SHULKER_BOX.upload(ModelIds.getItemModelId(shulkerBox.asItem()), Texture.particle(shulkerBox), this.modelCollector);
    }

    private void registerPlantPart(Block plant, Block plantStem, TintType tintType) {
        this.registerTintableCrossBlockState(plant, tintType);
        this.registerTintableCrossBlockState(plantStem, tintType);
    }

    private void registerBed(Block bed, Block particleSource) {
        Models.TEMPLATE_BED.upload(ModelIds.getItemModelId(bed.asItem()), Texture.particle(particleSource), this.modelCollector);
    }

    private void registerInfestedStone() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.STONE);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.STONE, "_mirrored");
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(Blocks.INFESTED_STONE, lv, lv2));
        this.registerParentedItemModel(Blocks.INFESTED_STONE, lv);
    }

    private void registerRoots(Block root, Block pottedRoot) {
        this.registerTintableCross(root, TintType.NOT_TINTED);
        Texture lv = Texture.plant(Texture.getSubId(root, "_pot"));
        Identifier lv2 = TintType.NOT_TINTED.getFlowerPotCrossModel().upload(pottedRoot, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(pottedRoot, lv2));
    }

    private void registerRespawnAnchor() {
        Identifier lv = Texture.getSubId(Blocks.RESPAWN_ANCHOR, "_bottom");
        Identifier lv2 = Texture.getSubId(Blocks.RESPAWN_ANCHOR, "_top_off");
        Identifier lv3 = Texture.getSubId(Blocks.RESPAWN_ANCHOR, "_top");
        Identifier[] lvs = new Identifier[5];
        for (int i = 0; i < 5; ++i) {
            Texture lv4 = new Texture().put(TextureKey.BOTTOM, lv).put(TextureKey.TOP, i == 0 ? lv2 : lv3).put(TextureKey.SIDE, Texture.getSubId(Blocks.RESPAWN_ANCHOR, "_side" + i));
            lvs[i] = Models.CUBE_BOTTOM_TOP.upload(Blocks.RESPAWN_ANCHOR, "_" + i, lv4, this.modelCollector);
        }
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.RESPAWN_ANCHOR).coordinate(BlockStateVariantMap.create(Properties.CHARGES).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, lvs[integer]))));
        this.registerParentedItemModel(Items.RESPAWN_ANCHOR, lvs[0]);
    }

    private BlockStateVariant addJigsawOrientationToVariant(JigsawOrientation orientation, BlockStateVariant variant) {
        switch (orientation) {
            case DOWN_NORTH: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R90);
            }
            case DOWN_SOUTH: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case DOWN_WEST: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case DOWN_EAST: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
            case UP_NORTH: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case UP_SOUTH: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R270);
            }
            case UP_WEST: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
            case UP_EAST: {
                return variant.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case NORTH_UP: {
                return variant;
            }
            case SOUTH_UP: {
                return variant.put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case WEST_UP: {
                return variant.put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case EAST_UP: {
                return variant.put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
        }
        throw new UnsupportedOperationException("Rotation " + orientation + " can't be expressed with existing x and y values");
    }

    private void registerJigsaw() {
        Identifier lv = Texture.getSubId(Blocks.JIGSAW, "_top");
        Identifier lv2 = Texture.getSubId(Blocks.JIGSAW, "_bottom");
        Identifier lv3 = Texture.getSubId(Blocks.JIGSAW, "_side");
        Identifier lv4 = Texture.getSubId(Blocks.JIGSAW, "_lock");
        Texture lv5 = new Texture().put(TextureKey.DOWN, lv3).put(TextureKey.WEST, lv3).put(TextureKey.EAST, lv3).put(TextureKey.PARTICLE, lv).put(TextureKey.NORTH, lv).put(TextureKey.SOUTH, lv2).put(TextureKey.UP, lv4);
        Identifier lv6 = Models.CUBE_DIRECTIONAL.upload(Blocks.JIGSAW, lv5, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.JIGSAW, BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).coordinate(BlockStateVariantMap.create(Properties.ORIENTATION).register(arg -> this.addJigsawOrientationToVariant((JigsawOrientation)arg, BlockStateVariant.create()))));
    }

    public void register() {
        this.registerSimpleState(Blocks.AIR);
        this.registerStateWithModelReference(Blocks.CAVE_AIR, Blocks.AIR);
        this.registerStateWithModelReference(Blocks.VOID_AIR, Blocks.AIR);
        this.registerSimpleState(Blocks.BEACON);
        this.registerSimpleState(Blocks.CACTUS);
        this.registerStateWithModelReference(Blocks.BUBBLE_COLUMN, Blocks.WATER);
        this.registerSimpleState(Blocks.DRAGON_EGG);
        this.registerSimpleState(Blocks.DRIED_KELP_BLOCK);
        this.registerSimpleState(Blocks.ENCHANTING_TABLE);
        this.registerSimpleState(Blocks.FLOWER_POT);
        this.registerItemModel(Items.FLOWER_POT);
        this.registerSimpleState(Blocks.HONEY_BLOCK);
        this.registerSimpleState(Blocks.WATER);
        this.registerSimpleState(Blocks.LAVA);
        this.registerSimpleState(Blocks.SLIME_BLOCK);
        this.registerSimpleState(Blocks.CHAIN);
        this.registerItemModel(Items.CHAIN);
        this.registerSimpleState(Blocks.POTTED_BAMBOO);
        this.registerSimpleState(Blocks.POTTED_CACTUS);
        this.registerBuiltinWithParticle(Blocks.BARRIER, Items.BARRIER);
        this.registerItemModel(Items.BARRIER);
        this.registerBuiltinWithParticle(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
        this.registerItemModel(Items.STRUCTURE_VOID);
        this.registerBuiltinWithParticle(Blocks.MOVING_PISTON, Texture.getSubId(Blocks.PISTON, "_side"));
        this.registerSingleton(Blocks.COAL_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.COAL_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.DIAMOND_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.DIAMOND_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.EMERALD_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.EMERALD_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.GOLD_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.NETHER_GOLD_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.GOLD_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.IRON_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.IRON_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.ANCIENT_DEBRIS, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.NETHERITE_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.LAPIS_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.LAPIS_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.NETHER_QUARTZ_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.REDSTONE_ORE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.REDSTONE_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.GILDED_BLACKSTONE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.BLUE_ICE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CHISELED_NETHER_BRICKS, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CLAY, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.COARSE_DIRT, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CRACKED_NETHER_BRICKS, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CRACKED_STONE_BRICKS, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CRYING_OBSIDIAN, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.END_STONE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.GLOWSTONE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.GRAVEL, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.HONEYCOMB_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.ICE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
        this.registerSingleton(Blocks.LODESTONE, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.MELON, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.NETHER_WART_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.NOTE_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.PACKED_ICE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.OBSIDIAN, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.QUARTZ_BRICKS, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SEA_LANTERN, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SHROOMLIGHT, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SOUL_SAND, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SOUL_SOIL, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SPAWNER, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SPONGE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.SEAGRASS, TexturedModel.TEMPLATE_SEAGRASS);
        this.registerItemModel(Items.SEAGRASS);
        this.registerSingleton(Blocks.TNT, TexturedModel.CUBE_BOTTOM_TOP);
        this.registerSingleton(Blocks.TARGET, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.WARPED_WART_BLOCK, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.WET_SPONGE, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, TexturedModel.CUBE_ALL);
        this.registerSingleton(Blocks.CHISELED_QUARTZ_BLOCK, TexturedModel.CUBE_COLUMN.withTexture(arg -> arg.put(TextureKey.SIDE, Texture.getId(Blocks.CHISELED_QUARTZ_BLOCK))));
        this.registerSingleton(Blocks.CHISELED_STONE_BRICKS, TexturedModel.CUBE_ALL);
        this.registerCubeColumn(Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
        this.registerCubeColumn(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        this.registerSingleton(Blocks.CHISELED_POLISHED_BLACKSTONE, TexturedModel.CUBE_ALL);
        this.registerPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
        this.registerPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
        this.registerBookshelf();
        this.registerBrewingStand();
        this.registerCake();
        this.method_27166(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
        this.registerCartographyTable();
        this.registerCauldron();
        this.registerChorusFlower();
        this.registerChorusPlant();
        this.registerComposter();
        this.registerDaylightDetector();
        this.registerEndPortalFrame();
        this.registerEndRod();
        this.registerFarmland();
        this.registerFire();
        this.registerSoulFire();
        this.registerFrostedIce();
        this.registerTopSoils();
        this.registerCocoa();
        this.registerGrassPath();
        this.registerGrindstone();
        this.registerHopper();
        this.registerIronBars();
        this.registerLever();
        this.registerLilyPad();
        this.registerNetherPortal();
        this.registerNetherrack();
        this.registerObserver();
        this.registerPistons();
        this.registerPistonHead();
        this.registerScaffolding();
        this.registerRedstoneTorch();
        this.registerRedstoneLamp();
        this.registerRepeater();
        this.registerSeaPickle();
        this.registerSmithingTable();
        this.registerSnows();
        this.registerStonecutter();
        this.registerStructureBlock();
        this.registerSweetBerryBush();
        this.registerTripwire();
        this.registerTripwireHook();
        this.registerTurtleEgg();
        this.registerVine();
        this.registerMagmaBlock();
        this.registerJigsaw();
        this.registerNorthDefaultHorizontalRotation(Blocks.LADDER);
        this.registerItemModel(Blocks.LADDER);
        this.registerNorthDefaultHorizontalRotation(Blocks.LECTERN);
        this.registerTorch(Blocks.TORCH, Blocks.WALL_TORCH);
        this.registerTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
        this.registerCubeWithCustomTexture(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, Texture::frontSideWithCustomBottom);
        this.registerCubeWithCustomTexture(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, Texture::frontTopSide);
        this.registerNetherrackBottomCustomTop(Blocks.CRIMSON_NYLIUM);
        this.registerNetherrackBottomCustomTop(Blocks.WARPED_NYLIUM);
        this.registerFurnaceLikeOrientable(Blocks.DISPENSER);
        this.registerFurnaceLikeOrientable(Blocks.DROPPER);
        this.registerLantern(Blocks.LANTERN);
        this.registerLantern(Blocks.SOUL_LANTERN);
        this.registerAxisRotated(Blocks.BASALT, TexturedModel.CUBE_COLUMN);
        this.registerAxisRotated(Blocks.POLISHED_BASALT, TexturedModel.CUBE_COLUMN);
        this.registerAxisRotated(Blocks.BONE_BLOCK, TexturedModel.CUBE_COLUMN);
        this.registerRotatable(Blocks.DIRT);
        this.registerRotatable(Blocks.SAND);
        this.registerRotatable(Blocks.RED_SAND);
        this.registerMirrorable(Blocks.BEDROCK);
        this.registerAxisRotated(Blocks.HAY_BLOCK, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.PURPUR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.QUARTZ_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerNorthDefaultHorizontalRotated(Blocks.LOOM, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerPumpkins();
        this.registerBeehive(Blocks.BEE_NEST, Texture::sideFrontTopBottom);
        this.registerBeehive(Blocks.BEEHIVE, Texture::sideFrontEnd);
        this.registerCrop(Blocks.BEETROOTS, Properties.AGE_3, 0, 1, 2, 3);
        this.registerCrop(Blocks.CARROTS, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.registerCrop(Blocks.NETHER_WART, Properties.AGE_3, 0, 1, 1, 2);
        this.registerCrop(Blocks.POTATOES, Properties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.registerCrop(Blocks.WHEAT, Properties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
        this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("banner"), Blocks.OAK_PLANKS).includeWithItem(Models.TEMPLATE_BANNER, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER).includeWithoutItem(Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
        this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("bed"), Blocks.OAK_PLANKS).includeWithoutItem(Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED, Blocks.RED_BED, Blocks.BLACK_BED);
        this.registerBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
        this.registerBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
        this.registerBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        this.registerBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        this.registerBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
        this.registerBed(Blocks.LIME_BED, Blocks.LIME_WOOL);
        this.registerBed(Blocks.PINK_BED, Blocks.PINK_WOOL);
        this.registerBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
        this.registerBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        this.registerBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
        this.registerBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
        this.registerBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
        this.registerBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
        this.registerBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
        this.registerBed(Blocks.RED_BED, Blocks.RED_WOOL);
        this.registerBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
        this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("skull"), Blocks.SOUL_SAND).includeWithItem(Models.TEMPLATE_SKULL, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL).includeWithItem(Blocks.DRAGON_HEAD).includeWithoutItem(Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL);
        this.registerShulkerBox(Blocks.SHULKER_BOX);
        this.registerShulkerBox(Blocks.WHITE_SHULKER_BOX);
        this.registerShulkerBox(Blocks.ORANGE_SHULKER_BOX);
        this.registerShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
        this.registerShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
        this.registerShulkerBox(Blocks.YELLOW_SHULKER_BOX);
        this.registerShulkerBox(Blocks.LIME_SHULKER_BOX);
        this.registerShulkerBox(Blocks.PINK_SHULKER_BOX);
        this.registerShulkerBox(Blocks.GRAY_SHULKER_BOX);
        this.registerShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
        this.registerShulkerBox(Blocks.CYAN_SHULKER_BOX);
        this.registerShulkerBox(Blocks.PURPLE_SHULKER_BOX);
        this.registerShulkerBox(Blocks.BLUE_SHULKER_BOX);
        this.registerShulkerBox(Blocks.BROWN_SHULKER_BOX);
        this.registerShulkerBox(Blocks.GREEN_SHULKER_BOX);
        this.registerShulkerBox(Blocks.RED_SHULKER_BOX);
        this.registerShulkerBox(Blocks.BLACK_SHULKER_BOX);
        this.registerSingleton(Blocks.CONDUIT, TexturedModel.PARTICLE);
        this.excludeFromSimpleItemModelGeneration(Blocks.CONDUIT);
        this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("chest"), Blocks.OAK_PLANKS).includeWithoutItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        this.registerBuiltin(ModelIds.getMinecraftNamespacedBlock("ender_chest"), Blocks.OBSIDIAN).includeWithoutItem(Blocks.ENDER_CHEST);
        this.registerBuiltin(Blocks.END_PORTAL, Blocks.OBSIDIAN).includeWithItem(Blocks.END_PORTAL, Blocks.END_GATEWAY);
        this.registerSimpleCubeAll(Blocks.WHITE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.ORANGE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.MAGENTA_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.YELLOW_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIME_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PINK_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.CYAN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PURPLE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BROWN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GREEN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.RED_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLACK_CONCRETE);
        this.registerRandomHorizontalRotations(TexturedModel.CUBE_ALL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
        this.registerSimpleCubeAll(Blocks.TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.WHITE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.ORANGE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.MAGENTA_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.YELLOW_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIME_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PINK_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.CYAN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PURPLE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BROWN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GREEN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.RED_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLACK_TERRACOTTA);
        this.registerGlassPane(Blocks.GLASS, Blocks.GLASS_PANE);
        this.registerGlassPane(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        this.registerGlassPane(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        this.registerSouthDefaultHorizontalFacing(TexturedModel.TEMPLATE_GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        this.registerCarpet(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        this.registerCarpet(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        this.registerCarpet(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        this.registerCarpet(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        this.registerCarpet(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
        this.registerCarpet(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        this.registerCarpet(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        this.registerCarpet(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        this.registerCarpet(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        this.registerCarpet(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        this.registerCarpet(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        this.registerCarpet(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        this.registerCarpet(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        this.registerCarpet(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        this.registerCarpet(Blocks.RED_WOOL, Blocks.RED_CARPET);
        this.registerCarpet(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        this.registerFlowerPotPlant(Blocks.FERN, Blocks.POTTED_FERN, TintType.TINTED);
        this.registerFlowerPotPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.POPPY, Blocks.POTTED_POPPY, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, TintType.NOT_TINTED);
        this.registerFlowerPotPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, TintType.NOT_TINTED);
        this.registerMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.MUSHROOM_STEM);
        this.registerTintableCross(Blocks.GRASS, TintType.TINTED);
        this.registerTintableCrossBlockState(Blocks.SUGAR_CANE, TintType.TINTED);
        this.registerItemModel(Items.SUGAR_CANE);
        this.registerPlantPart(Blocks.KELP, Blocks.KELP_PLANT, TintType.TINTED);
        this.registerItemModel(Items.KELP);
        this.excludeFromSimpleItemModelGeneration(Blocks.KELP_PLANT);
        this.registerPlantPart(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, TintType.NOT_TINTED);
        this.registerPlantPart(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, TintType.NOT_TINTED);
        this.registerItemModel(Blocks.WEEPING_VINES, "_plant");
        this.excludeFromSimpleItemModelGeneration(Blocks.WEEPING_VINES_PLANT);
        this.registerItemModel(Blocks.TWISTING_VINES, "_plant");
        this.excludeFromSimpleItemModelGeneration(Blocks.TWISTING_VINES_PLANT);
        this.registerTintableCross(Blocks.BAMBOO_SAPLING, TintType.TINTED, Texture.cross(Texture.getSubId(Blocks.BAMBOO, "_stage0")));
        this.registerBamboo();
        this.registerTintableCross(Blocks.COBWEB, TintType.NOT_TINTED);
        this.registerDoubleBlock(Blocks.LILAC, TintType.NOT_TINTED);
        this.registerDoubleBlock(Blocks.ROSE_BUSH, TintType.NOT_TINTED);
        this.registerDoubleBlock(Blocks.PEONY, TintType.NOT_TINTED);
        this.registerDoubleBlock(Blocks.TALL_GRASS, TintType.TINTED);
        this.registerDoubleBlock(Blocks.LARGE_FERN, TintType.TINTED);
        this.registerSunflower();
        this.registerTallSeagrass();
        this.registerCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
        this.registerGourd(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
        this.registerGourd(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        this.registerCubeAllModelTexturePool(Blocks.ACACIA_PLANKS).button(Blocks.ACACIA_BUTTON).fence(Blocks.ACACIA_FENCE).fenceGate(Blocks.ACACIA_FENCE_GATE).pressurePlate(Blocks.ACACIA_PRESSURE_PLATE).sign(Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN).slab(Blocks.ACACIA_SLAB).stairs(Blocks.ACACIA_STAIRS);
        this.registerDoor(Blocks.ACACIA_DOOR);
        this.registerOrientableTrapdoor(Blocks.ACACIA_TRAPDOOR);
        this.registerLog(Blocks.ACACIA_LOG).log(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
        this.registerLog(Blocks.STRIPPED_ACACIA_LOG).log(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
        this.registerFlowerPotPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.BIRCH_PLANKS).button(Blocks.BIRCH_BUTTON).fence(Blocks.BIRCH_FENCE).fenceGate(Blocks.BIRCH_FENCE_GATE).pressurePlate(Blocks.BIRCH_PRESSURE_PLATE).sign(Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN).slab(Blocks.BIRCH_SLAB).stairs(Blocks.BIRCH_STAIRS);
        this.registerDoor(Blocks.BIRCH_DOOR);
        this.registerOrientableTrapdoor(Blocks.BIRCH_TRAPDOOR);
        this.registerLog(Blocks.BIRCH_LOG).log(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
        this.registerLog(Blocks.STRIPPED_BIRCH_LOG).log(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
        this.registerFlowerPotPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.OAK_PLANKS).button(Blocks.OAK_BUTTON).fence(Blocks.OAK_FENCE).fenceGate(Blocks.OAK_FENCE_GATE).pressurePlate(Blocks.OAK_PRESSURE_PLATE).sign(Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN).slab(Blocks.OAK_SLAB).slab(Blocks.PETRIFIED_OAK_SLAB).stairs(Blocks.OAK_STAIRS);
        this.registerDoor(Blocks.OAK_DOOR);
        this.registerTrapdoor(Blocks.OAK_TRAPDOOR);
        this.registerLog(Blocks.OAK_LOG).log(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
        this.registerLog(Blocks.STRIPPED_OAK_LOG).log(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
        this.registerFlowerPotPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.SPRUCE_PLANKS).button(Blocks.SPRUCE_BUTTON).fence(Blocks.SPRUCE_FENCE).fenceGate(Blocks.SPRUCE_FENCE_GATE).pressurePlate(Blocks.SPRUCE_PRESSURE_PLATE).sign(Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN).slab(Blocks.SPRUCE_SLAB).stairs(Blocks.SPRUCE_STAIRS);
        this.registerDoor(Blocks.SPRUCE_DOOR);
        this.registerOrientableTrapdoor(Blocks.SPRUCE_TRAPDOOR);
        this.registerLog(Blocks.SPRUCE_LOG).log(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
        this.registerLog(Blocks.STRIPPED_SPRUCE_LOG).log(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
        this.registerFlowerPotPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.DARK_OAK_PLANKS).button(Blocks.DARK_OAK_BUTTON).fence(Blocks.DARK_OAK_FENCE).fenceGate(Blocks.DARK_OAK_FENCE_GATE).pressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE).sign(Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN).slab(Blocks.DARK_OAK_SLAB).stairs(Blocks.DARK_OAK_STAIRS);
        this.registerDoor(Blocks.DARK_OAK_DOOR);
        this.registerTrapdoor(Blocks.DARK_OAK_TRAPDOOR);
        this.registerLog(Blocks.DARK_OAK_LOG).log(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
        this.registerLog(Blocks.STRIPPED_DARK_OAK_LOG).log(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.registerFlowerPotPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.JUNGLE_PLANKS).button(Blocks.JUNGLE_BUTTON).fence(Blocks.JUNGLE_FENCE).fenceGate(Blocks.JUNGLE_FENCE_GATE).pressurePlate(Blocks.JUNGLE_PRESSURE_PLATE).sign(Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN).slab(Blocks.JUNGLE_SLAB).stairs(Blocks.JUNGLE_STAIRS);
        this.registerDoor(Blocks.JUNGLE_DOOR);
        this.registerOrientableTrapdoor(Blocks.JUNGLE_TRAPDOOR);
        this.registerLog(Blocks.JUNGLE_LOG).log(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
        this.registerLog(Blocks.STRIPPED_JUNGLE_LOG).log(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
        this.registerFlowerPotPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, TintType.NOT_TINTED);
        this.registerSingleton(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
        this.registerCubeAllModelTexturePool(Blocks.CRIMSON_PLANKS).button(Blocks.CRIMSON_BUTTON).fence(Blocks.CRIMSON_FENCE).fenceGate(Blocks.CRIMSON_FENCE_GATE).pressurePlate(Blocks.CRIMSON_PRESSURE_PLATE).sign(Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN).slab(Blocks.CRIMSON_SLAB).stairs(Blocks.CRIMSON_STAIRS);
        this.registerDoor(Blocks.CRIMSON_DOOR);
        this.registerOrientableTrapdoor(Blocks.CRIMSON_TRAPDOOR);
        this.registerLog(Blocks.CRIMSON_STEM).stem(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
        this.registerLog(Blocks.STRIPPED_CRIMSON_STEM).stem(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.registerFlowerPotPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, TintType.NOT_TINTED);
        this.registerRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
        this.registerCubeAllModelTexturePool(Blocks.WARPED_PLANKS).button(Blocks.WARPED_BUTTON).fence(Blocks.WARPED_FENCE).fenceGate(Blocks.WARPED_FENCE_GATE).pressurePlate(Blocks.WARPED_PRESSURE_PLATE).sign(Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN).slab(Blocks.WARPED_SLAB).stairs(Blocks.WARPED_STAIRS);
        this.registerDoor(Blocks.WARPED_DOOR);
        this.registerOrientableTrapdoor(Blocks.WARPED_TRAPDOOR);
        this.registerLog(Blocks.WARPED_STEM).stem(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
        this.registerLog(Blocks.STRIPPED_WARPED_STEM).stem(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
        this.registerFlowerPotPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, TintType.NOT_TINTED);
        this.registerRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
        this.registerTintableCrossBlockState(Blocks.NETHER_SPROUTS, TintType.NOT_TINTED);
        this.registerItemModel(Items.NETHER_SPROUTS);
        this.registerTexturePool(Texture.all(Blocks.STONE)).base(arg -> {
            Identifier lv = Models.CUBE_ALL.upload(Blocks.STONE, (Texture)arg, this.modelCollector);
            Identifier lv2 = Models.CUBE_MIRRORED_ALL.upload(Blocks.STONE, (Texture)arg, this.modelCollector);
            this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(Blocks.STONE, lv, lv2));
            return lv;
        }).slab(Blocks.STONE_SLAB).pressurePlate(Blocks.STONE_PRESSURE_PLATE).button(Blocks.STONE_BUTTON).stairs(Blocks.STONE_STAIRS);
        this.registerDoor(Blocks.IRON_DOOR);
        this.registerTrapdoor(Blocks.IRON_TRAPDOOR);
        this.registerCubeAllModelTexturePool(Blocks.STONE_BRICKS).wall(Blocks.STONE_BRICK_WALL).stairs(Blocks.STONE_BRICK_STAIRS).slab(Blocks.STONE_BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.MOSSY_STONE_BRICKS).wall(Blocks.MOSSY_STONE_BRICK_WALL).stairs(Blocks.MOSSY_STONE_BRICK_STAIRS).slab(Blocks.MOSSY_STONE_BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.COBBLESTONE).wall(Blocks.COBBLESTONE_WALL).stairs(Blocks.COBBLESTONE_STAIRS).slab(Blocks.COBBLESTONE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.MOSSY_COBBLESTONE).wall(Blocks.MOSSY_COBBLESTONE_WALL).stairs(Blocks.MOSSY_COBBLESTONE_STAIRS).slab(Blocks.MOSSY_COBBLESTONE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.PRISMARINE).wall(Blocks.PRISMARINE_WALL).stairs(Blocks.PRISMARINE_STAIRS).slab(Blocks.PRISMARINE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.PRISMARINE_BRICKS).stairs(Blocks.PRISMARINE_BRICK_STAIRS).slab(Blocks.PRISMARINE_BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.DARK_PRISMARINE).stairs(Blocks.DARK_PRISMARINE_STAIRS).slab(Blocks.DARK_PRISMARINE_SLAB);
        this.registerTexturePool(Blocks.SANDSTONE, TexturedModel.WALL_CUBE_BOTTOM_TOP).wall(Blocks.SANDSTONE_WALL).stairs(Blocks.SANDSTONE_STAIRS).slab(Blocks.SANDSTONE_SLAB);
        this.registerTexturePool(Blocks.SMOOTH_SANDSTONE, TexturedModel.getCubeAll(Texture.getSubId(Blocks.SANDSTONE, "_top"))).slab(Blocks.SMOOTH_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_SANDSTONE_STAIRS);
        this.registerTexturePool(Blocks.CUT_SANDSTONE, TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).texture(arg -> arg.put(TextureKey.SIDE, Texture.getId(Blocks.CUT_SANDSTONE)))).slab(Blocks.CUT_SANDSTONE_SLAB);
        this.registerTexturePool(Blocks.RED_SANDSTONE, TexturedModel.WALL_CUBE_BOTTOM_TOP).wall(Blocks.RED_SANDSTONE_WALL).stairs(Blocks.RED_SANDSTONE_STAIRS).slab(Blocks.RED_SANDSTONE_SLAB);
        this.registerTexturePool(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.getCubeAll(Texture.getSubId(Blocks.RED_SANDSTONE, "_top"))).slab(Blocks.SMOOTH_RED_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        this.registerTexturePool(Blocks.CUT_RED_SANDSTONE, TexturedModel.CUBE_COLUMN.get(Blocks.RED_SANDSTONE).texture(arg -> arg.put(TextureKey.SIDE, Texture.getId(Blocks.CUT_RED_SANDSTONE)))).slab(Blocks.CUT_RED_SANDSTONE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.BRICKS).wall(Blocks.BRICK_WALL).stairs(Blocks.BRICK_STAIRS).slab(Blocks.BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.NETHER_BRICKS).fence(Blocks.NETHER_BRICK_FENCE).wall(Blocks.NETHER_BRICK_WALL).stairs(Blocks.NETHER_BRICK_STAIRS).slab(Blocks.NETHER_BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.PURPUR_BLOCK).stairs(Blocks.PURPUR_STAIRS).slab(Blocks.PURPUR_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.DIORITE).wall(Blocks.DIORITE_WALL).stairs(Blocks.DIORITE_STAIRS).slab(Blocks.DIORITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.POLISHED_DIORITE).stairs(Blocks.POLISHED_DIORITE_STAIRS).slab(Blocks.POLISHED_DIORITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.GRANITE).wall(Blocks.GRANITE_WALL).stairs(Blocks.GRANITE_STAIRS).slab(Blocks.GRANITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.POLISHED_GRANITE).stairs(Blocks.POLISHED_GRANITE_STAIRS).slab(Blocks.POLISHED_GRANITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.ANDESITE).wall(Blocks.ANDESITE_WALL).stairs(Blocks.ANDESITE_STAIRS).slab(Blocks.ANDESITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.POLISHED_ANDESITE).stairs(Blocks.POLISHED_ANDESITE_STAIRS).slab(Blocks.POLISHED_ANDESITE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.END_STONE_BRICKS).wall(Blocks.END_STONE_BRICK_WALL).stairs(Blocks.END_STONE_BRICK_STAIRS).slab(Blocks.END_STONE_BRICK_SLAB);
        this.registerTexturePool(Blocks.QUARTZ_BLOCK, TexturedModel.CUBE_COLUMN).stairs(Blocks.QUARTZ_STAIRS).slab(Blocks.QUARTZ_SLAB);
        this.registerTexturePool(Blocks.SMOOTH_QUARTZ, TexturedModel.getCubeAll(Texture.getSubId(Blocks.QUARTZ_BLOCK, "_bottom"))).stairs(Blocks.SMOOTH_QUARTZ_STAIRS).slab(Blocks.SMOOTH_QUARTZ_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.RED_NETHER_BRICKS).slab(Blocks.RED_NETHER_BRICK_SLAB).stairs(Blocks.RED_NETHER_BRICK_STAIRS).wall(Blocks.RED_NETHER_BRICK_WALL);
        this.registerTexturePool(Blocks.BLACKSTONE, TexturedModel.field_23959).wall(Blocks.BLACKSTONE_WALL).stairs(Blocks.BLACKSTONE_STAIRS).slab(Blocks.BLACKSTONE_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.POLISHED_BLACKSTONE_BRICKS).wall(Blocks.POLISHED_BLACKSTONE_BRICK_WALL).stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
        this.registerCubeAllModelTexturePool(Blocks.POLISHED_BLACKSTONE).wall(Blocks.POLISHED_BLACKSTONE_WALL).pressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).button(Blocks.POLISHED_BLACKSTONE_BUTTON).stairs(Blocks.POLISHED_BLACKSTONE_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_SLAB);
        this.registerSmoothStone();
        this.registerTurnableRail(Blocks.RAIL);
        this.registerStraightRail(Blocks.POWERED_RAIL);
        this.registerStraightRail(Blocks.DETECTOR_RAIL);
        this.registerStraightRail(Blocks.ACTIVATOR_RAIL);
        this.registerComparator();
        this.registerCommandBlock(Blocks.COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
        this.registerAnvil(Blocks.ANVIL);
        this.registerAnvil(Blocks.CHIPPED_ANVIL);
        this.registerAnvil(Blocks.DAMAGED_ANVIL);
        this.registerBarrel();
        this.registerBell();
        this.registerCooker(Blocks.FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.SMOKER, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerRedstone();
        this.registerRespawnAnchor();
        this.registerInfested(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        this.registerInfested(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
        this.registerInfested(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        this.registerInfested(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
        this.registerInfestedStone();
        this.registerInfested(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        SpawnEggItem.getAll().forEach(arg -> this.registerParentedItemModel((Item)arg, ModelIds.getMinecraftNamespacedItem("template_spawn_egg")));
    }

    private /* synthetic */ BlockStateVariant method_25589(int[] is, Int2ObjectMap int2ObjectMap, Block arg, Integer integer) {
        int i = is[integer];
        Identifier lv = (Identifier)int2ObjectMap.computeIfAbsent(i, j -> this.createSubModel(arg, "_stage" + i, Models.CROP, Texture::crop));
        return BlockStateVariant.create().put(VariantSettings.MODEL, lv);
    }

    class BuiltinModelPool {
        private final Identifier modelId;

        public BuiltinModelPool(Identifier modelId, Block block) {
            this.modelId = Models.PARTICLE.upload(modelId, Texture.particle(block), (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
        }

        public BuiltinModelPool includeWithItem(Block ... blocks) {
            for (Block lv : blocks) {
                BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(lv, this.modelId));
            }
            return this;
        }

        public BuiltinModelPool includeWithoutItem(Block ... blocks) {
            for (Block lv : blocks) {
                BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(lv);
            }
            return this.includeWithItem(blocks);
        }

        public BuiltinModelPool includeWithItem(Model model, Block ... blocks) {
            for (Block lv : blocks) {
                model.upload(ModelIds.getItemModelId(lv.asItem()), Texture.particle(lv), (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            }
            return this.includeWithItem(blocks);
        }
    }

    static enum TintType {
        TINTED,
        NOT_TINTED;


        public Model getCrossModel() {
            return this == TINTED ? Models.TINTED_CROSS : Models.CROSS;
        }

        public Model getFlowerPotCrossModel() {
            return this == TINTED ? Models.TINTED_FLOWER_POT_CROSS : Models.FLOWER_POT_CROSS;
        }
    }

    class LogTexturePool {
        private final Texture texture;

        public LogTexturePool(Texture texture) {
            this.texture = texture;
        }

        public LogTexturePool wood(Block woodBlock) {
            Texture lv = this.texture.copyAndAdd(TextureKey.END, this.texture.getTexture(TextureKey.SIDE));
            Identifier lv2 = Models.CUBE_COLUMN.upload(woodBlock, lv, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(woodBlock, lv2));
            return this;
        }

        public LogTexturePool stem(Block stemBlock) {
            Identifier lv = Models.CUBE_COLUMN.upload(stemBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(stemBlock, lv));
            return this;
        }

        public LogTexturePool log(Block logBlock) {
            Identifier lv = Models.CUBE_COLUMN.upload(logBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.CUBE_COLUMN_HORIZONTAL.upload(logBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(logBlock, lv, lv2));
            return this;
        }
    }

    class BlockTexturePool {
        private final Texture texture;
        @Nullable
        private Identifier baseModelId;

        public BlockTexturePool(Texture texture) {
            this.texture = texture;
        }

        public BlockTexturePool base(Block block, Model model) {
            this.baseModelId = model.upload(block, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, this.baseModelId));
            return this;
        }

        public BlockTexturePool base(Function<Texture, Identifier> modelFactory) {
            this.baseModelId = modelFactory.apply(this.texture);
            return this;
        }

        public BlockTexturePool button(Block buttonBlock) {
            Identifier lv = Models.BUTTON.upload(buttonBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.BUTTON_PRESSED.upload(buttonBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(buttonBlock, lv, lv2));
            Identifier lv3 = Models.BUTTON_INVENTORY.upload(buttonBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(buttonBlock, lv3);
            return this;
        }

        public BlockTexturePool wall(Block wallBlock) {
            Identifier lv = Models.TEMPLATE_WALL_POST.upload(wallBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.TEMPLATE_WALL_SIDE.upload(wallBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.TEMPLATE_WALL_SIDE_TALL.upload(wallBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(wallBlock, lv, lv2, lv3));
            Identifier lv4 = Models.WALL_INVENTORY.upload(wallBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(wallBlock, lv4);
            return this;
        }

        public BlockTexturePool fence(Block fenceBlock) {
            Identifier lv = Models.FENCE_POST.upload(fenceBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.FENCE_SIDE.upload(fenceBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(fenceBlock, lv, lv2));
            Identifier lv3 = Models.FENCE_INVENTORY.upload(fenceBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(fenceBlock, lv3);
            return this;
        }

        public BlockTexturePool fenceGate(Block fenceGateBlock) {
            Identifier lv = Models.TEMPLATE_FENCE_GATE_OPEN.upload(fenceGateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.TEMPLATE_FENCE_GATE.upload(fenceGateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(fenceGateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv4 = Models.TEMPLATE_FENCE_GATE_WALL.upload(fenceGateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(fenceGateBlock, lv, lv2, lv3, lv4));
            return this;
        }

        public BlockTexturePool pressurePlate(Block pressurePlateBlock) {
            Identifier lv = Models.PRESSURE_PLATE_UP.upload(pressurePlateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.PRESSURE_PLATE_DOWN.upload(pressurePlateBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createPressurePlateBlockState(pressurePlateBlock, lv, lv2));
            return this;
        }

        public BlockTexturePool sign(Block signBlock, Block wallSignBlock) {
            Identifier lv = Models.PARTICLE.upload(signBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(signBlock, lv));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(wallSignBlock, lv));
            BlockStateModelGenerator.this.registerItemModel(signBlock.asItem());
            BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(wallSignBlock);
            return this;
        }

        public BlockTexturePool slab(Block slabBlock) {
            if (this.baseModelId == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            Identifier lv = Models.SLAB.upload(slabBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.SLAB_TOP.upload(slabBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(slabBlock, lv, lv2, this.baseModelId));
            return this;
        }

        public BlockTexturePool stairs(Block stairsBlock) {
            Identifier lv = Models.INNER_STAIRS.upload(stairsBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.STAIRS.upload(stairsBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.OUTER_STAIRS.upload(stairsBlock, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(stairsBlock, lv, lv2, lv3));
            return this;
        }
    }
}

