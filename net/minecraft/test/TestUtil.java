/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Streams
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.FailureLoggingTestCompletionListener;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.PositionedException;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestCompletionListener;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunner;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestUtil {
    public static TestCompletionListener field_20573 = new FailureLoggingTestCompletionListener();

    public static void startTest(GameTest arg, BlockPos arg2, TestManager arg3) {
        arg.startCountdown();
        arg3.start(arg);
        arg.addListener(new TestListener(){

            @Override
            public void onStarted(GameTest test) {
                TestUtil.createBeacon(test, Blocks.LIGHT_GRAY_STAINED_GLASS);
            }

            @Override
            public void onFailed(GameTest test) {
                TestUtil.createBeacon(test, test.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
                TestUtil.createLectern(test, Util.getInnermostMessage(test.getThrowable()));
                TestUtil.handleTestFail(test);
            }
        });
        arg.init(arg2, 2);
    }

    public static Collection<GameTest> runTestBatches(Collection<GameTestBatch> batches, BlockPos pos, BlockRotation arg2, ServerWorld arg3, TestManager arg4, int i) {
        TestRunner lv = new TestRunner(batches, pos, arg2, arg3, arg4, i);
        lv.run();
        return lv.getTests();
    }

    public static Collection<GameTest> runTestFunctions(Collection<TestFunction> testFunctions, BlockPos pos, BlockRotation arg2, ServerWorld arg3, TestManager arg4, int i) {
        return TestUtil.runTestBatches(TestUtil.createBatches(testFunctions), pos, arg2, arg3, arg4, i);
    }

    public static Collection<GameTestBatch> createBatches(Collection<TestFunction> testFunctions) {
        HashMap map = Maps.newHashMap();
        testFunctions.forEach(arg -> {
            String string2 = arg.getBatchId();
            Collection collection = map.computeIfAbsent(string2, string -> Lists.newArrayList());
            collection.add(arg);
        });
        return map.keySet().stream().flatMap(string -> {
            Collection collection = (Collection)map.get(string);
            Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(string);
            MutableInt mutableInt = new MutableInt();
            return Streams.stream((Iterable)Iterables.partition((Iterable)collection, (int)100)).map(list -> new GameTestBatch(string + ":" + mutableInt.incrementAndGet(), collection, consumer));
        }).collect(Collectors.toList());
    }

    private static void handleTestFail(GameTest test) {
        Throwable throwable = test.getThrowable();
        String string = (test.isRequired() ? "" : "(optional) ") + test.getStructurePath() + " failed! " + Util.getInnermostMessage(throwable);
        TestUtil.sendMessage(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string);
        if (throwable instanceof PositionedException) {
            PositionedException lv = (PositionedException)throwable;
            TestUtil.addDebugMarker(test.getWorld(), lv.getPos(), lv.getDebugMessage());
        }
        field_20573.onTestFailed(test);
    }

    private static void createBeacon(GameTest test, Block glass) {
        ServerWorld lv = test.getWorld();
        BlockPos lv2 = test.getPos();
        BlockPos lv3 = new BlockPos(-1, -1, -1);
        BlockPos lv4 = Structure.transformAround(lv2.add(lv3), BlockMirror.NONE, test.method_29402(), lv2);
        lv.setBlockState(lv4, Blocks.BEACON.getDefaultState().rotate(test.method_29402()));
        BlockPos lv5 = lv4.add(0, 1, 0);
        lv.setBlockState(lv5, glass.getDefaultState());
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos lv6 = lv4.add(i, -1, j);
                lv.setBlockState(lv6, Blocks.IRON_BLOCK.getDefaultState());
            }
        }
    }

    private static void createLectern(GameTest test, String message) {
        ServerWorld lv = test.getWorld();
        BlockPos lv2 = test.getPos();
        BlockPos lv3 = new BlockPos(-1, 1, -1);
        BlockPos lv4 = Structure.transformAround(lv2.add(lv3), BlockMirror.NONE, test.method_29402(), lv2);
        lv.setBlockState(lv4, Blocks.LECTERN.getDefaultState().rotate(test.method_29402()));
        BlockState lv5 = lv.getBlockState(lv4);
        ItemStack lv6 = TestUtil.createBook(test.getStructurePath(), test.isRequired(), message);
        LecternBlock.putBookIfAbsent(lv, lv4, lv5, lv6);
    }

    private static ItemStack createBook(String structureName, boolean required, String message) {
        ItemStack lv = new ItemStack(Items.WRITABLE_BOOK);
        ListTag lv2 = new ListTag();
        StringBuffer stringBuffer = new StringBuffer();
        Arrays.stream(structureName.split("\\.")).forEach(string -> stringBuffer.append((String)string).append('\n'));
        if (!required) {
            stringBuffer.append("(optional)\n");
        }
        stringBuffer.append("-------------------\n");
        lv2.add(StringTag.of(stringBuffer.toString() + message));
        lv.putSubTag("pages", lv2);
        return lv;
    }

    private static void sendMessage(ServerWorld world, Formatting formatting, String message) {
        world.getPlayers(arg -> true).forEach(arg2 -> arg2.sendSystemMessage(new LiteralText(message).formatted(formatting), Util.NIL_UUID));
    }

    public static void clearDebugMarkers(ServerWorld world) {
        DebugInfoSender.clearGameTestMarkers(world);
    }

    private static void addDebugMarker(ServerWorld world, BlockPos pos, String message) {
        DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
    }

    public static void clearTests(ServerWorld world, BlockPos pos, TestManager testManager, int radius) {
        testManager.clear();
        BlockPos lv = pos.add(-radius, 0, -radius);
        BlockPos lv2 = pos.add(radius, 0, radius);
        BlockPos.stream(lv, lv2).filter(arg2 -> world.getBlockState((BlockPos)arg2).isOf(Blocks.STRUCTURE_BLOCK)).forEach(arg2 -> {
            StructureBlockBlockEntity lv = (StructureBlockBlockEntity)world.getBlockEntity((BlockPos)arg2);
            BlockPos lv2 = lv.getPos();
            BlockBox lv3 = StructureTestUtil.method_29410(lv);
            StructureTestUtil.clearArea(lv3, lv2.getY(), world);
        });
    }
}

