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

    public BlockStateModelGenerator(Consumer<BlockStateSupplier> consumer, BiConsumer<Identifier, Supplier<JsonElement>> biConsumer, Consumer<Item> consumer2) {
        this.blockStateCollector = consumer;
        this.modelCollector = biConsumer;
        this.simpleItemModelExemptionCollector = consumer2;
    }

    private void excludeFromSimpleItemModelGeneration(Block arg) {
        this.simpleItemModelExemptionCollector.accept(arg.asItem());
    }

    private void registerParentedItemModel(Block arg, Identifier arg2) {
        this.modelCollector.accept(ModelIds.getItemModelId(arg.asItem()), new SimpleModelSupplier(arg2));
    }

    private void registerParentedItemModel(Item arg, Identifier arg2) {
        this.modelCollector.accept(ModelIds.getItemModelId(arg), new SimpleModelSupplier(arg2));
    }

    private void registerItemModel(Item arg) {
        Models.GENERATED.upload(ModelIds.getItemModelId(arg), Texture.layer0(arg), this.modelCollector);
    }

    private void registerItemModel(Block arg) {
        Item lv = arg.asItem();
        if (lv != Items.AIR) {
            Models.GENERATED.upload(ModelIds.getItemModelId(lv), Texture.layer0(arg), this.modelCollector);
        }
    }

    private void registerItemModel(Block arg, String string) {
        Item lv = arg.asItem();
        Models.GENERATED.upload(ModelIds.getItemModelId(lv), Texture.layer0(Texture.getSubId(arg, string)), this.modelCollector);
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

    private static VariantsBlockStateSupplier createBlockStateWithRandomHorizontalRotations(Block arg, Identifier arg2) {
        return VariantsBlockStateSupplier.create(arg, BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(arg2));
    }

    private static BlockStateVariant[] createModelVariantWithRandomHorizontalRotations(Identifier arg) {
        return new BlockStateVariant[]{BlockStateVariant.create().put(VariantSettings.MODEL, arg), BlockStateVariant.create().put(VariantSettings.MODEL, arg).put(VariantSettings.Y, VariantSettings.Rotation.R90), BlockStateVariant.create().put(VariantSettings.MODEL, arg).put(VariantSettings.Y, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, arg).put(VariantSettings.Y, VariantSettings.Rotation.R270)};
    }

    private static VariantsBlockStateSupplier createBlockStateWithTwoModelAndRandomInversion(Block arg, Identifier arg2, Identifier arg3) {
        return VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, arg2), BlockStateVariant.create().put(VariantSettings.MODEL, arg3), BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R180), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180));
    }

    private static BlockStateVariantMap createBooleanModelMap(BooleanProperty arg, Identifier arg2, Identifier arg3) {
        return BlockStateVariantMap.create(arg).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3));
    }

    private void registerMirrorable(Block arg) {
        Identifier lv = TexturedModel.CUBE_ALL.upload(arg, this.modelCollector);
        Identifier lv2 = TexturedModel.CUBE_MIRRORED_ALL.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(arg, lv, lv2));
    }

    private void registerRotatable(Block arg) {
        Identifier lv = TexturedModel.CUBE_ALL.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(arg, lv));
    }

    private static BlockStateSupplier createButtonBlockState(Block arg, Identifier arg2, Identifier arg3) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.POWERED).register((Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register((Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg3))).coordinate(BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING).register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create()).register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)).register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R180)));
    }

    private static BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> fillDoorVariantMap(BlockStateVariantMap.QuadrupleProperty<Direction, DoubleBlockHalf, DoorHinge, Boolean> arg, DoubleBlockHalf arg2, Identifier arg3, Identifier arg4) {
        return arg.register(Direction.EAST, arg2, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.SOUTH, arg2, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, arg2, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.NORTH, arg2, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, arg2, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.SOUTH, arg2, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, arg2, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.NORTH, arg2, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.EAST, arg2, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, arg2, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, arg2, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, arg2, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.EAST, arg2, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.SOUTH, arg2, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.WEST, arg2, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.NORTH, arg2, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180));
    }

    private static BlockStateSupplier createDoorBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4, Identifier arg5) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.fillDoorVariantMap(BlockStateModelGenerator.fillDoorVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOUBLE_BLOCK_HALF, Properties.DOOR_HINGE, Properties.OPEN), DoubleBlockHalf.LOWER, arg2, arg3), DoubleBlockHalf.UPPER, arg4, arg5));
    }

    private static BlockStateSupplier createFenceBlockState(Block arg, Identifier arg2, Identifier arg3) {
        return MultipartBlockStateSupplier.create(arg).with(BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true));
    }

    private static BlockStateSupplier createWallBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4) {
        return MultipartBlockStateSupplier.create(arg).with((When)When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).with((When)When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST_WALL_SHAPE, WallShape.LOW), BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.NORTH_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.EAST_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST_WALL_SHAPE, WallShape.TALL), BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true));
    }

    private static BlockStateSupplier createFenceGateBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4, Identifier arg5) {
        return VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.UVLOCK, true)).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()).coordinate(BlockStateVariantMap.create(Properties.IN_WALL, Properties.OPEN).register((Boolean)false, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register((Boolean)true, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg5)).register((Boolean)false, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register((Boolean)true, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)));
    }

    private static BlockStateSupplier createStairsBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.STAIR_SHAPE).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.NORTH, BlockHalf.BOTTOM, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.STRAIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.OUTER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.EAST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.WEST, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).register(Direction.SOUTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).register(Direction.NORTH, BlockHalf.TOP, StairShape.INNER_LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)));
    }

    private static BlockStateSupplier createOrientableTrapdoorBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R0)).register(Direction.EAST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.WEST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.X, VariantSettings.Rotation.R180).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
    }

    private static BlockStateSupplier createTrapdoorBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF, Properties.OPEN).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.EAST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.WEST, BlockHalf.TOP, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.NORTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.SOUTH, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.BOTTOM, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)).register(Direction.SOUTH, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.EAST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.WEST, BlockHalf.TOP, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, arg4).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }

    private static VariantsBlockStateSupplier createSingletonBlockState(Block arg, Identifier arg2) {
        return VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, arg2));
    }

    private static BlockStateVariantMap createAxisRotatedVariantMap() {
        return BlockStateVariantMap.create(Properties.AXIS).register(Direction.Axis.Y, BlockStateVariant.create()).register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }

    private static BlockStateSupplier createAxisRotatedBlockState(Block arg, Identifier arg2) {
        return VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).coordinate(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    private void registerAxisRotated(Block arg, TexturedModel.Factory arg2) {
        Identifier lv = arg2.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(arg, lv));
    }

    private void registerNorthDefaultHorizontalRotated(Block arg, TexturedModel.Factory arg2) {
        Identifier lv = arg2.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private static BlockStateSupplier createAxisRotatedBlockState(Block arg, Identifier arg2, Identifier arg3) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.AXIS).register(Direction.Axis.Y, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R90)).register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, arg3).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90)));
    }

    private void registerAxisRotated(Block arg, TexturedModel.Factory arg2, TexturedModel.Factory arg3) {
        Identifier lv = arg2.upload(arg, this.modelCollector);
        Identifier lv2 = arg3.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(arg, lv, lv2));
    }

    private Identifier createSubModel(Block arg, String string, Model arg2, Function<Identifier, Texture> function) {
        return arg2.upload(arg, string, function.apply(Texture.getSubId(arg, string)), this.modelCollector);
    }

    private static BlockStateSupplier createPressurePlateBlockState(Block arg, Identifier arg2, Identifier arg3) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.POWERED, arg3, arg2));
    }

    private static BlockStateSupplier createSlabBlockState(Block arg, Identifier arg2, Identifier arg3, Identifier arg4) {
        return VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.SLAB_TYPE).register(SlabType.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, arg2)).register(SlabType.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(SlabType.DOUBLE, BlockStateVariant.create().put(VariantSettings.MODEL, arg4)));
    }

    private void registerSimpleCubeAll(Block arg) {
        this.registerSingleton(arg, TexturedModel.CUBE_ALL);
    }

    private void registerSingleton(Block arg, TexturedModel.Factory arg2) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, arg2.upload(arg, this.modelCollector)));
    }

    private void registerSingleton(Block arg, Texture arg2, Model arg3) {
        Identifier lv = arg3.upload(arg, arg2, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv));
    }

    private BlockTexturePool registerTexturePool(Block arg, TexturedModel arg2) {
        return new BlockTexturePool(arg2.getTexture()).base(arg, arg2.getModel());
    }

    private BlockTexturePool registerTexturePool(Block arg, TexturedModel.Factory arg2) {
        TexturedModel lv = arg2.get(arg);
        return new BlockTexturePool(lv.getTexture()).base(arg, lv.getModel());
    }

    private BlockTexturePool registerCubeAllModelTexturePool(Block arg) {
        return this.registerTexturePool(arg, TexturedModel.CUBE_ALL);
    }

    private BlockTexturePool registerTexturePool(Texture arg) {
        return new BlockTexturePool(arg);
    }

    private void registerDoor(Block arg) {
        Texture lv = Texture.topBottom(arg);
        Identifier lv2 = Models.DOOR_BOTTOM.upload(arg, lv, this.modelCollector);
        Identifier lv3 = Models.DOOR_BOTTOM_RH.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.DOOR_TOP.upload(arg, lv, this.modelCollector);
        Identifier lv5 = Models.DOOR_TOP_RH.upload(arg, lv, this.modelCollector);
        this.registerItemModel(arg.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(arg, lv2, lv3, lv4, lv5));
    }

    private void registerOrientableTrapdoor(Block arg) {
        Texture lv = Texture.texture(arg);
        Identifier lv2 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(arg, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(arg, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createOrientableTrapdoorBlockState(arg, lv2, lv3, lv4));
        this.registerParentedItemModel(arg, lv3);
    }

    private void registerTrapdoor(Block arg) {
        Texture lv = Texture.texture(arg);
        Identifier lv2 = Models.TEMPLATE_TRAPDOOR_TOP.upload(arg, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(arg, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(arg, lv2, lv3, lv4));
        this.registerParentedItemModel(arg, lv3);
    }

    private LogTexturePool registerLog(Block arg) {
        return new LogTexturePool(Texture.sideAndEndForTop(arg));
    }

    private void registerSimpleState(Block arg) {
        this.registerStateWithModelReference(arg, arg);
    }

    private void registerStateWithModelReference(Block arg, Block arg2) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, ModelIds.getBlockModelId(arg2)));
    }

    private void registerTintableCross(Block arg, TintType arg2) {
        this.registerItemModel(arg);
        this.registerTintableCrossBlockState(arg, arg2);
    }

    private void registerTintableCross(Block arg, TintType arg2, Texture arg3) {
        this.registerItemModel(arg);
        this.registerTintableCrossBlockState(arg, arg2, arg3);
    }

    private void registerTintableCrossBlockState(Block arg, TintType arg2) {
        Texture lv = Texture.cross(arg);
        this.registerTintableCrossBlockState(arg, arg2, lv);
    }

    private void registerTintableCrossBlockState(Block arg, TintType arg2, Texture arg3) {
        Identifier lv = arg2.getCrossModel().upload(arg, arg3, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv));
    }

    private void registerFlowerPotPlant(Block arg, Block arg2, TintType arg3) {
        this.registerTintableCross(arg, arg3);
        Texture lv = Texture.plant(arg);
        Identifier lv2 = arg3.getFlowerPotCrossModel().upload(arg2, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg2, lv2));
    }

    private void registerCoralFan(Block arg, Block arg2) {
        TexturedModel lv = TexturedModel.CORAL_FAN.get(arg);
        Identifier lv2 = lv.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv2));
        Identifier lv3 = Models.CORAL_WALL_FAN.upload(arg2, lv.getTexture(), this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg2, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
        this.registerItemModel(arg);
    }

    private void registerGourd(Block arg, Block arg2) {
        this.registerItemModel(arg.asItem());
        Texture lv = Texture.stem(arg);
        Texture lv2 = Texture.stemAndUpper(arg, arg2);
        Identifier lv3 = Models.STEM_FRUIT.upload(arg2, lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg2, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING).register(Direction.WEST, BlockStateVariant.create()).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.AGE_7).register(integer -> BlockStateVariant.create().put(VariantSettings.MODEL, Models.STEM_GROWTH_STAGES[integer].upload(arg, lv, this.modelCollector)))));
    }

    private void registerCoral(Block arg, Block arg2, Block arg3, Block arg4, Block arg5, Block arg6, Block arg7, Block arg8) {
        this.registerTintableCross(arg, TintType.NOT_TINTED);
        this.registerTintableCross(arg2, TintType.NOT_TINTED);
        this.registerSimpleCubeAll(arg3);
        this.registerSimpleCubeAll(arg4);
        this.registerCoralFan(arg5, arg7);
        this.registerCoralFan(arg6, arg8);
    }

    private void registerDoubleBlock(Block arg, TintType arg2) {
        this.registerItemModel(arg, "_top");
        Identifier lv = this.createSubModel(arg, "_top", arg2.getCrossModel(), Texture::cross);
        Identifier lv2 = this.createSubModel(arg, "_bottom", arg2.getCrossModel(), Texture::cross);
        this.registerDoubleBlock(arg, lv, lv2);
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

    private void registerDoubleBlock(Block arg, Identifier arg2, Identifier arg3) {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.DOUBLE_BLOCK_HALF).register(DoubleBlockHalf.LOWER, BlockStateVariant.create().put(VariantSettings.MODEL, arg3)).register(DoubleBlockHalf.UPPER, BlockStateVariant.create().put(VariantSettings.MODEL, arg2))));
    }

    private void registerTurnableRail(Block arg) {
        Texture lv = Texture.rail(arg);
        Texture lv2 = Texture.rail(Texture.getSubId(arg, "_corner"));
        Identifier lv3 = Models.RAIL_FLAT.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.RAIL_CURVED.upload(arg, lv2, this.modelCollector);
        Identifier lv5 = Models.TEMPLATE_RAIL_RAISED_NE.upload(arg, lv, this.modelCollector);
        Identifier lv6 = Models.TEMPLATE_RAIL_RAISED_SW.upload(arg, lv, this.modelCollector);
        this.registerItemModel(arg);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.RAIL_SHAPE).register(RailShape.NORTH_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).register(RailShape.EAST_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv6).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.ASCENDING_NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).register(RailShape.ASCENDING_SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).register(RailShape.SOUTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).register(RailShape.SOUTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(RailShape.NORTH_WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(RailShape.NORTH_EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerStraightRail(Block arg) {
        Identifier lv = this.createSubModel(arg, "", Models.RAIL_FLAT, Texture::rail);
        Identifier lv2 = this.createSubModel(arg, "", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
        Identifier lv3 = this.createSubModel(arg, "", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
        Identifier lv4 = this.createSubModel(arg, "_on", Models.RAIL_FLAT, Texture::rail);
        Identifier lv5 = this.createSubModel(arg, "_on", Models.TEMPLATE_RAIL_RAISED_NE, Texture::rail);
        Identifier lv6 = this.createSubModel(arg, "_on", Models.TEMPLATE_RAIL_RAISED_SW, Texture::rail);
        BlockStateVariantMap lv7 = BlockStateVariantMap.create(Properties.POWERED, Properties.STRAIGHT_RAIL_SHAPE).register((arg7, arg8) -> {
            switch (arg8) {
                case NORTH_SOUTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv4 : lv);
                }
                case EAST_WEST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv4 : lv).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_EAST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv5 : lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_WEST: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv6 : lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90);
                }
                case ASCENDING_NORTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv5 : lv2);
                }
                case ASCENDING_SOUTH: {
                    return BlockStateVariant.create().put(VariantSettings.MODEL, arg7 != false ? lv6 : lv3);
                }
            }
            throw new UnsupportedOperationException("Fix you generator!");
        });
        this.registerItemModel(arg);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(lv7));
    }

    private BuiltinModelPool registerBuiltin(Identifier arg, Block arg2) {
        return new BuiltinModelPool(arg, arg2);
    }

    private BuiltinModelPool registerBuiltin(Block arg, Block arg2) {
        return new BuiltinModelPool(ModelIds.getBlockModelId(arg), arg2);
    }

    private void registerBuiltinWithParticle(Block arg, Item arg2) {
        Identifier lv = Models.PARTICLE.upload(arg, Texture.particle(arg2), this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv));
    }

    private void registerBuiltinWithParticle(Block arg, Identifier arg2) {
        Identifier lv = Models.PARTICLE.upload(arg, Texture.particle(arg2), this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv));
    }

    private void registerCarpet(Block arg, Block arg2) {
        this.registerSingleton(arg, TexturedModel.CUBE_ALL);
        Identifier lv = TexturedModel.CARPET.get(arg).upload(arg2, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg2, lv));
    }

    private void registerRandomHorizontalRotations(TexturedModel.Factory arg, Block ... args) {
        for (Block lv : args) {
            Identifier lv2 = arg.upload(lv, this.modelCollector);
            this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(lv, lv2));
        }
    }

    private void registerSouthDefaultHorizontalFacing(TexturedModel.Factory arg, Block ... args) {
        for (Block lv : args) {
            Identifier lv2 = arg.upload(lv, this.modelCollector);
            this.blockStateCollector.accept(VariantsBlockStateSupplier.create(lv, BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
        }
    }

    private void registerGlassPane(Block arg, Block arg2) {
        this.registerSimpleCubeAll(arg);
        Texture lv = Texture.paneAndTopForEdge(arg, arg2);
        Identifier lv2 = Models.TEMPLATE_GLASS_PANE_POST.upload(arg2, lv, this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(arg2, lv, this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(arg2, lv, this.modelCollector);
        Identifier lv5 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(arg2, lv, this.modelCollector);
        Identifier lv6 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(arg2, lv, this.modelCollector);
        Item lv7 = arg2.asItem();
        Models.GENERATED.upload(ModelIds.getItemModelId(lv7), Texture.layer0(arg), this.modelCollector);
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(arg2).with(BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv6)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv6).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv5).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }

    private void registerCommandBlock(Block arg) {
        Texture lv = Texture.sideFrontBack(arg);
        Identifier lv2 = Models.TEMPLATE_COMMAND_BLOCK.upload(arg, lv, this.modelCollector);
        Identifier lv3 = this.createSubModel(arg, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, arg2 -> lv.copyAndAdd(TextureKey.SIDE, (Identifier)arg2));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.CONDITIONAL, lv3, lv2)).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
    }

    private void registerAnvil(Block arg) {
        Identifier lv = TexturedModel.TEMPLATE_ANVIL.upload(arg, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private List<BlockStateVariant> getBambooBlockStateVariants(int i2) {
        String string = "_age" + i2;
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

    private static <T extends Comparable<T>> BlockStateVariantMap createValueFencedModelMap(Property<T> arg, T comparable, Identifier arg2, Identifier arg3) {
        BlockStateVariant lv = BlockStateVariant.create().put(VariantSettings.MODEL, arg2);
        BlockStateVariant lv2 = BlockStateVariant.create().put(VariantSettings.MODEL, arg3);
        return BlockStateVariantMap.create(arg).register(comparable2 -> {
            boolean bl = comparable2.compareTo(comparable) >= 0;
            return bl ? lv : lv2;
        });
    }

    private void registerBeehive(Block arg, Function<Block, Texture> function) {
        Texture lv = function.apply(arg).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
        Texture lv2 = lv.copyAndAdd(TextureKey.FRONT, Texture.getSubId(arg, "_front_honey"));
        Identifier lv3 = Models.ORIENTABLE_WITH_BOTTOM.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.ORIENTABLE_WITH_BOTTOM.upload(arg, "_honey", lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.HONEY_LEVEL, 5, lv4, lv3)));
    }

    private void registerCrop(Block arg, Property<Integer> arg2, int ... is) {
        if (arg2.getValues().size() != is.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        BlockStateVariantMap lv = BlockStateVariantMap.create(arg2).register(arg_0 -> this.method_25589(is, (Int2ObjectMap)int2ObjectMap, arg, arg_0));
        this.registerItemModel(arg.asItem());
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(lv));
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

    private void registerCooker(Block arg, TexturedModel.Factory arg22) {
        Identifier lv = arg22.upload(arg, this.modelCollector);
        Identifier lv2 = Texture.getSubId(arg, "_front_on");
        Identifier lv3 = arg22.get(arg).texture(arg2 -> arg2.put(TextureKey.FRONT, lv2)).upload(arg, "_on", this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT, lv3, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
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

    private void registorSmoothStone() {
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

    private void registerMushroomBlock(Block arg) {
        Identifier lv = Models.TEMPLATE_SINGLE_FACE.upload(arg, Texture.texture(arg), this.modelCollector);
        Identifier lv2 = ModelIds.getMinecraftNamespacedBlock("mushroom_block_inside");
        this.blockStateCollector.accept(MultipartBlockStateSupplier.create(arg).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.UP, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.DOWN, true), BlockStateVariant.create().put(VariantSettings.MODEL, lv).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, true)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.UP, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.UVLOCK, false)).with((When)When.create().set(Properties.DOWN, false), BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.UVLOCK, false)));
        this.registerParentedItemModel(arg, TexturedModel.CUBE_ALL.upload(arg, "_inventory", this.modelCollector));
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

    private void registerCubeWithCustomTexture(Block arg, Block arg2, BiFunction<Block, Block, Texture> biFunction) {
        Texture lv = biFunction.apply(arg, arg2);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, Models.CUBE.upload(arg, lv, this.modelCollector)));
    }

    private void registerPumpkins() {
        Texture lv = Texture.sideEnd(Blocks.PUMPKIN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(Blocks.PUMPKIN, ModelIds.getBlockModelId(Blocks.PUMPKIN)));
        this.registerNorthDefaultHorizontalRotatable(Blocks.CARVED_PUMPKIN, lv);
        this.registerNorthDefaultHorizontalRotatable(Blocks.JACK_O_LANTERN, lv);
    }

    private void registerNorthDefaultHorizontalRotatable(Block arg, Texture arg2) {
        Identifier lv = Models.ORIENTABLE.upload(arg, arg2.copyAndAdd(TextureKey.FRONT, Texture.getId(arg)), this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerCauldron() {
        this.registerItemModel(Items.CAULDRON);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CAULDRON).coordinate(BlockStateVariantMap.create(Properties.LEVEL_3).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAULDRON))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level1"))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level2"))).register((Integer)3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.CAULDRON, "_level3")))));
    }

    private void registerCubeColumn(Block arg, Block arg2) {
        Texture lv = new Texture().put(TextureKey.END, Texture.getSubId(arg2, "_top")).put(TextureKey.SIDE, Texture.getId(arg));
        this.registerSingleton(arg, lv, Models.CUBE_COLUMN);
    }

    private void registerChorusFlower() {
        Texture lv = Texture.texture(Blocks.CHORUS_FLOWER);
        Identifier lv2 = Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, lv, this.modelCollector);
        Identifier lv3 = this.createSubModel(Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, arg2 -> lv.copyAndAdd(TextureKey.TEXTURE, (Identifier)arg2));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.CHORUS_FLOWER).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.AGE_5, 5, lv3, lv2)));
    }

    private void registerFurnaceLikeOrientable(Block arg) {
        Texture lv = new Texture().put(TextureKey.TOP, Texture.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.SIDE, Texture.getSubId(Blocks.FURNACE, "_side")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front"));
        Texture lv2 = new Texture().put(TextureKey.SIDE, Texture.getSubId(Blocks.FURNACE, "_top")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front_vertical"));
        Identifier lv3 = Models.ORIENTABLE.upload(arg, lv, this.modelCollector);
        Identifier lv4 = Models.ORIENTABLE_VERTICAL.upload(arg, lv2, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, lv4).put(VariantSettings.X, VariantSettings.Rotation.R180)).register(Direction.UP, BlockStateVariant.create().put(VariantSettings.MODEL, lv4)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv3).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
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

    private void registerNetherrackBottomCustomTop(Block arg) {
        Texture lv = new Texture().put(TextureKey.BOTTOM, Texture.getId(Blocks.NETHERRACK)).put(TextureKey.TOP, Texture.getId(arg)).put(TextureKey.SIDE, Texture.getSubId(arg, "_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, Models.CUBE_BOTTOM_TOP.upload(arg, lv, this.modelCollector)));
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

    private List<Identifier> getFireFloorModels(Block arg) {
        Identifier lv = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(arg, "_floor0"), Texture.fire0(arg), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId(arg, "_floor1"), Texture.fire1(arg), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2);
    }

    private List<Identifier> getFireSideModels(Block arg) {
        Identifier lv = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(arg, "_side0"), Texture.fire0(arg), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId(arg, "_side1"), Texture.fire1(arg), this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(arg, "_side_alt0"), Texture.fire0(arg), this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId(arg, "_side_alt1"), Texture.fire1(arg), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2, (Object)lv3, (Object)lv4);
    }

    private List<Identifier> getFireUpModels(Block arg) {
        Identifier lv = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(arg, "_up0"), Texture.fire0(arg), this.modelCollector);
        Identifier lv2 = Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId(arg, "_up1"), Texture.fire1(arg), this.modelCollector);
        Identifier lv3 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(arg, "_up_alt0"), Texture.fire0(arg), this.modelCollector);
        Identifier lv4 = Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId(arg, "_up_alt1"), Texture.fire1(arg), this.modelCollector);
        return ImmutableList.of((Object)lv, (Object)lv2, (Object)lv3, (Object)lv4);
    }

    private static List<BlockStateVariant> buildBlockStateVariants(List<Identifier> list, UnaryOperator<BlockStateVariant> unaryOperator) {
        return list.stream().map(arg -> BlockStateVariant.create().put(VariantSettings.MODEL, arg)).map(unaryOperator).collect(Collectors.toList());
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

    private void registerLantern(Block arg) {
        Identifier lv = TexturedModel.TEMPLATE_LANTERN.upload(arg, this.modelCollector);
        Identifier lv2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(arg, this.modelCollector);
        this.registerItemModel(arg.asItem());
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.HANGING, lv2, lv)));
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

    private void registerTopSoil(Block arg, Identifier arg2, BlockStateVariant arg3) {
        List<BlockStateVariant> list = Arrays.asList(BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(arg2));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateVariantMap.create(Properties.SNOWY).register((Boolean)true, arg3).register((Boolean)false, list)));
    }

    private void registerCocoa() {
        this.registerItemModel(Items.COCOA_BEANS);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.COCOA).coordinate(BlockStateVariantMap.create(Properties.AGE_2).register((Integer)0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage0"))).register((Integer)1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage1"))).register((Integer)2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(Blocks.COCOA, "_stage2")))).coordinate(BlockStateModelGenerator.createSouthDefaultHorizontalRotationStates()));
    }

    private void registerGrassPath() {
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithRandomHorizontalRotations(Blocks.GRASS_PATH, ModelIds.getBlockModelId(Blocks.GRASS_PATH)));
    }

    private void registerPressurePlate(Block arg, Block arg2) {
        Texture lv = Texture.texture(arg2);
        Identifier lv2 = Models.PRESSURE_PLATE_UP.upload(arg, lv, this.modelCollector);
        Identifier lv3 = Models.PRESSURE_PLATE_DOWN.upload(arg, lv, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createValueFencedModelMap(Properties.POWER, 1, lv3, lv2)));
    }

    private void registerHopper() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.HOPPER);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.HOPPER, "_side");
        this.registerItemModel(Items.HOPPER);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.HOPPER).coordinate(BlockStateVariantMap.create(Properties.HOPPER_FACING).register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.MODEL, lv)).register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv2)).register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R180)).register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, lv2).put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

    private void registerInfested(Block arg, Block arg2) {
        Identifier lv = ModelIds.getBlockModelId(arg);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg2, BlockStateVariant.create().put(VariantSettings.MODEL, lv)));
        this.registerParentedItemModel(arg2, lv);
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

    private void registerNorthDefaultHorizontalRotation(Block arg) {
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(arg))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
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

    private void registerPiston(Block arg, Identifier arg2, Texture arg3) {
        Identifier lv = Models.TEMPLATE_PISTON.upload(arg, arg3, this.modelCollector);
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg).coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.EXTENDED, arg2, lv)).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
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

    private void registerTorch(Block arg, Block arg2) {
        Texture lv = Texture.torch(arg);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, Models.TEMPLATE_TORCH.upload(arg, lv, this.modelCollector)));
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(arg2, BlockStateVariant.create().put(VariantSettings.MODEL, Models.TEMPLATE_TORCH_WALL.upload(arg2, lv, this.modelCollector))).coordinate(BlockStateModelGenerator.createEastDefaultHorizontalRotationStates()));
        this.registerItemModel(arg);
        this.excludeFromSimpleItemModelGeneration(arg2);
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
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.REPEATER).coordinate(BlockStateVariantMap.create(Properties.DELAY, Properties.LOCKED, Properties.POWERED).register((integer, arg, arg2) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('_').append(integer).append("tick");
            if (arg2.booleanValue()) {
                stringBuilder.append("_on");
            }
            if (arg.booleanValue()) {
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
        this.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.TRIPWIRE_HOOK).coordinate(BlockStateVariantMap.create(Properties.ATTACHED, Properties.POWERED).register((arg, arg2) -> BlockStateVariant.create().put(VariantSettings.MODEL, Texture.getSubId(Blocks.TRIPWIRE_HOOK, (arg != false ? "_attached" : "") + (arg2 != false ? "_on" : ""))))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private Identifier getTurtleEggModel(int i, String string, Texture arg) {
        switch (i) {
            case 1: {
                return Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock(string + "turtle_egg"), arg, this.modelCollector);
            }
            case 2: {
                return Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("two_" + string + "turtle_eggs"), arg, this.modelCollector);
            }
            case 3: {
                return Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("three_" + string + "turtle_eggs"), arg, this.modelCollector);
            }
            case 4: {
                return Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock("four_" + string + "turtle_eggs"), arg, this.modelCollector);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Identifier getTurtleEggModel(Integer integer, Integer integer2) {
        switch (integer2) {
            case 0: {
                return this.getTurtleEggModel(integer, "", Texture.all(Texture.getId(Blocks.TURTLE_EGG)));
            }
            case 1: {
                return this.getTurtleEggModel(integer, "slightly_cracked_", Texture.all(Texture.getSubId(Blocks.TURTLE_EGG, "_slightly_cracked")));
            }
            case 2: {
                return this.getTurtleEggModel(integer, "very_cracked_", Texture.all(Texture.getSubId(Blocks.TURTLE_EGG, "_very_cracked")));
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

    private void registerShulkerBox(Block arg) {
        this.registerSingleton(arg, TexturedModel.PARTICLE);
        Models.TEMPLATE_SHULKER_BOX.upload(ModelIds.getItemModelId(arg.asItem()), Texture.particle(arg), this.modelCollector);
    }

    private void registerPlantPart(Block arg, Block arg2, TintType arg3) {
        this.registerTintableCrossBlockState(arg, arg3);
        this.registerTintableCrossBlockState(arg2, arg3);
    }

    private void registerBed(Block arg, Block arg2) {
        Models.TEMPLATE_BED.upload(ModelIds.getItemModelId(arg.asItem()), Texture.particle(arg2), this.modelCollector);
    }

    private void registerInfestedStone() {
        Identifier lv = ModelIds.getBlockModelId(Blocks.STONE);
        Identifier lv2 = ModelIds.getBlockSubModelId(Blocks.STONE, "_mirrored");
        this.blockStateCollector.accept(BlockStateModelGenerator.createBlockStateWithTwoModelAndRandomInversion(Blocks.INFESTED_STONE, lv, lv2));
        this.registerParentedItemModel(Blocks.INFESTED_STONE, lv);
    }

    private void registerRoots(Block arg, Block arg2) {
        this.registerTintableCross(arg, TintType.NOT_TINTED);
        Texture lv = Texture.plant(Texture.getSubId(arg, "_pot"));
        Identifier lv2 = TintType.NOT_TINTED.getFlowerPotCrossModel().upload(arg2, lv, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg2, lv2));
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

    private BlockStateVariant addJigsawOrientationToVariant(JigsawOrientation arg, BlockStateVariant arg2) {
        switch (arg) {
            case DOWN_NORTH: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R90);
            }
            case DOWN_SOUTH: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case DOWN_WEST: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case DOWN_EAST: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
            case UP_NORTH: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case UP_SOUTH: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R270);
            }
            case UP_WEST: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
            case UP_EAST: {
                return arg2.put(VariantSettings.X, VariantSettings.Rotation.R270).put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case NORTH_UP: {
                return arg2;
            }
            case SOUTH_UP: {
                return arg2.put(VariantSettings.Y, VariantSettings.Rotation.R180);
            }
            case WEST_UP: {
                return arg2.put(VariantSettings.Y, VariantSettings.Rotation.R270);
            }
            case EAST_UP: {
                return arg2.put(VariantSettings.Y, VariantSettings.Rotation.R90);
            }
        }
        throw new UnsupportedOperationException("Rotation " + arg + " can't be expressed with existing x and y values");
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
        this.registerTintableCross(Blocks.NETHER_SPROUTS, TintType.NOT_TINTED);
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
        this.registerTexturePool(Blocks.SANDSTONE, TexturedModel.WALL_CUBE_BUTTOM_TOP).wall(Blocks.SANDSTONE_WALL).stairs(Blocks.SANDSTONE_STAIRS).slab(Blocks.SANDSTONE_SLAB);
        this.registerTexturePool(Blocks.SMOOTH_SANDSTONE, TexturedModel.getCubeAll(Texture.getSubId(Blocks.SANDSTONE, "_top"))).slab(Blocks.SMOOTH_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_SANDSTONE_STAIRS);
        this.registerTexturePool(Blocks.CUT_SANDSTONE, TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).texture(arg -> arg.put(TextureKey.SIDE, Texture.getId(Blocks.CUT_SANDSTONE)))).slab(Blocks.CUT_SANDSTONE_SLAB);
        this.registerTexturePool(Blocks.RED_SANDSTONE, TexturedModel.WALL_CUBE_BUTTOM_TOP).wall(Blocks.RED_SANDSTONE_WALL).stairs(Blocks.RED_SANDSTONE_STAIRS).slab(Blocks.RED_SANDSTONE_SLAB);
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
        this.registorSmoothStone();
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

        public BuiltinModelPool(Identifier arg2, Block arg3) {
            this.modelId = Models.PARTICLE.upload(arg2, Texture.particle(arg3), (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
        }

        public BuiltinModelPool includeWithItem(Block ... args) {
            for (Block lv : args) {
                BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(lv, this.modelId));
            }
            return this;
        }

        public BuiltinModelPool includeWithoutItem(Block ... args) {
            for (Block lv : args) {
                BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(lv);
            }
            return this.includeWithItem(args);
        }

        public BuiltinModelPool includeWithItem(Model arg, Block ... args) {
            for (Block lv : args) {
                arg.upload(ModelIds.getItemModelId(lv.asItem()), Texture.particle(lv), (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            }
            return this.includeWithItem(args);
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

        public LogTexturePool(Texture arg2) {
            this.texture = arg2;
        }

        public LogTexturePool wood(Block arg) {
            Texture lv = this.texture.copyAndAdd(TextureKey.END, this.texture.getTexture(TextureKey.SIDE));
            Identifier lv2 = Models.CUBE_COLUMN.upload(arg, lv, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(arg, lv2));
            return this;
        }

        public LogTexturePool stem(Block arg) {
            Identifier lv = Models.CUBE_COLUMN.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(arg, lv));
            return this;
        }

        public LogTexturePool log(Block arg) {
            Identifier lv = Models.CUBE_COLUMN.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.CUBE_COLUMN_HORIZONTAL.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(arg, lv, lv2));
            return this;
        }
    }

    class BlockTexturePool {
        private final Texture texture;
        @Nullable
        private Identifier baseModelId;

        public BlockTexturePool(Texture arg2) {
            this.texture = arg2;
        }

        public BlockTexturePool base(Block arg, Model arg2) {
            this.baseModelId = arg2.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, this.baseModelId));
            return this;
        }

        public BlockTexturePool base(Function<Texture, Identifier> function) {
            this.baseModelId = function.apply(this.texture);
            return this;
        }

        public BlockTexturePool button(Block arg) {
            Identifier lv = Models.BUTTON.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.BUTTON_PRESSED.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createButtonBlockState(arg, lv, lv2));
            Identifier lv3 = Models.BUTTON_INVENTORY.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(arg, lv3);
            return this;
        }

        public BlockTexturePool wall(Block arg) {
            Identifier lv = Models.TEMPLATE_WALL_POST.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.TEMPLATE_WALL_SIDE.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.TEMPLATE_WALL_SIDE_TALL.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createWallBlockState(arg, lv, lv2, lv3));
            Identifier lv4 = Models.WALL_INVENTORY.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(arg, lv4);
            return this;
        }

        public BlockTexturePool fence(Block arg) {
            Identifier lv = Models.FENCE_POST.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.FENCE_SIDE.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceBlockState(arg, lv, lv2));
            Identifier lv3 = Models.FENCE_INVENTORY.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.registerParentedItemModel(arg, lv3);
            return this;
        }

        public BlockTexturePool fenceGate(Block arg) {
            Identifier lv = Models.TEMPLATE_FENCE_GATE_OPEN.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.TEMPLATE_FENCE_GATE.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv4 = Models.TEMPLATE_FENCE_GATE_WALL.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(arg, lv, lv2, lv3, lv4));
            return this;
        }

        public BlockTexturePool pressurePlate(Block arg) {
            Identifier lv = Models.PRESSURE_PLATE_UP.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.PRESSURE_PLATE_DOWN.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createPressurePlateBlockState(arg, lv, lv2));
            return this;
        }

        public BlockTexturePool sign(Block arg, Block arg2) {
            Identifier lv = Models.PARTICLE.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg, lv));
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(arg2, lv));
            BlockStateModelGenerator.this.registerItemModel(arg.asItem());
            BlockStateModelGenerator.this.excludeFromSimpleItemModelGeneration(arg2);
            return this;
        }

        public BlockTexturePool slab(Block arg) {
            if (this.baseModelId == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            Identifier lv = Models.SLAB.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.SLAB_TOP.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState(arg, lv, lv2, this.baseModelId));
            return this;
        }

        public BlockTexturePool stairs(Block arg) {
            Identifier lv = Models.INNER_STAIRS.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv2 = Models.STAIRS.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            Identifier lv3 = Models.OUTER_STAIRS.upload(arg, this.texture, (BiConsumer<Identifier, Supplier<JsonElement>>)BlockStateModelGenerator.this.modelCollector);
            BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createStairsBlockState(arg, lv, lv2, lv3));
            return this;
        }
    }
}

