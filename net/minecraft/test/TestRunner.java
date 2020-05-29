/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPos pos;
    private final ServerWorld world;
    private final TestManager testManager;
    private final int sizeZ;
    private final List<GameTest> tests = Lists.newArrayList();
    private final Map<GameTest, BlockPos> field_25300 = Maps.newHashMap();
    private final List<Pair<GameTestBatch, Collection<GameTest>>> batches = Lists.newArrayList();
    private TestSet currentBatchTests;
    private int currentBatchIndex = 0;
    private BlockPos.Mutable reusablePos;

    public TestRunner(Collection<GameTestBatch> collection, BlockPos arg, BlockRotation arg2, ServerWorld arg32, TestManager arg4, int i) {
        this.reusablePos = arg.mutableCopy();
        this.pos = arg;
        this.world = arg32;
        this.testManager = arg4;
        this.sizeZ = i;
        collection.forEach(arg3 -> {
            ArrayList collection = Lists.newArrayList();
            Collection<TestFunction> collection2 = arg3.getTestFunctions();
            for (TestFunction lv : collection2) {
                GameTest lv2 = new GameTest(lv, arg2, arg32);
                collection.add(lv2);
                this.tests.add(lv2);
            }
            this.batches.add((Pair<GameTestBatch, Collection<GameTest>>)Pair.of((Object)arg3, (Object)collection));
        });
    }

    public List<GameTest> getTests() {
        return this.tests;
    }

    public void run() {
        this.runBatch(0);
    }

    private void runBatch(int i) {
        this.currentBatchIndex = i;
        this.currentBatchTests = new TestSet();
        if (i >= this.batches.size()) {
            return;
        }
        Pair<GameTestBatch, Collection<GameTest>> pair = this.batches.get(this.currentBatchIndex);
        GameTestBatch lv = (GameTestBatch)pair.getFirst();
        Collection collection = (Collection)pair.getSecond();
        this.method_29401(collection);
        lv.setWorld(this.world);
        String string = lv.getId();
        LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
        collection.forEach(arg -> {
            this.currentBatchTests.add((GameTest)arg);
            this.currentBatchTests.addListener(new TestListener(){

                @Override
                public void onStarted(GameTest arg) {
                }

                @Override
                public void onFailed(GameTest arg) {
                    TestRunner.this.onTestCompleted(arg);
                }
            });
            BlockPos lv = this.field_25300.get(arg);
            TestUtil.startTest(arg, lv, this.testManager);
        });
    }

    private void onTestCompleted(GameTest arg) {
        if (this.currentBatchTests.isDone()) {
            this.runBatch(this.currentBatchIndex + 1);
        }
    }

    private void method_29401(Collection<GameTest> collection) {
        int i = 0;
        Box lv = new Box(this.reusablePos);
        for (GameTest lv2 : collection) {
            BlockPos lv3 = new BlockPos(this.reusablePos);
            StructureBlockBlockEntity lv4 = StructureTestUtil.method_22250(lv2.getStructureName(), lv3, lv2.method_29402(), 2, this.world, true);
            Box lv5 = StructureTestUtil.getStructureBoundingBox(lv4);
            lv2.setPos(lv4.getPos());
            this.field_25300.put(lv2, new BlockPos(this.reusablePos));
            lv = lv.union(lv5);
            this.reusablePos.move((int)lv5.getXLength() + 5, 0, 0);
            if (i++ % this.sizeZ != this.sizeZ - 1) continue;
            this.reusablePos.move(0, 0, (int)lv.getZLength() + 6);
            this.reusablePos.setX(this.pos.getX());
            lv = new Box(this.reusablePos);
        }
    }
}

