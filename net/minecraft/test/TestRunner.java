/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPos pos;
    private final ServerWorld world;
    private final TestManager testManager;
    private final List<GameTest> tests = Lists.newArrayList();
    private final List<Pair<GameTestBatch, Collection<GameTest>>> batches = Lists.newArrayList();
    private TestSet currentBatchTests;
    private int currentBatchIndex = 0;
    private BlockPos.Mutable reusablePos;
    private int sizeZ = 0;

    public TestRunner(Collection<GameTestBatch> collection, BlockPos arg, ServerWorld arg22, TestManager arg3) {
        this.reusablePos = arg.mutableCopy();
        this.pos = arg;
        this.world = arg22;
        this.testManager = arg3;
        collection.forEach(arg2 -> {
            ArrayList collection = Lists.newArrayList();
            Collection<TestFunction> collection2 = arg2.getTestFunctions();
            for (TestFunction lv : collection2) {
                GameTest lv2 = new GameTest(lv, arg22);
                collection.add(lv2);
                this.tests.add(lv2);
            }
            this.batches.add((Pair<GameTestBatch, Collection<GameTest>>)Pair.of((Object)arg2, (Object)collection));
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
        this.method_23632(collection);
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
            TestUtil.startTest(arg, this.testManager);
        });
    }

    private void onTestCompleted(GameTest arg) {
        if (this.currentBatchTests.isDone()) {
            this.runBatch(this.currentBatchIndex + 1);
        }
    }

    private void method_23632(Collection<GameTest> collection) {
        int i = 0;
        for (GameTest lv : collection) {
            BlockPos lv2 = new BlockPos(this.reusablePos);
            lv.setPos(lv2);
            StructureTestUtil.method_22250(lv.getStructureName(), lv2, 2, this.world, true);
            BlockPos lv3 = lv.getSize();
            int j = lv3 == null ? 1 : lv3.getX();
            int k = lv3 == null ? 1 : lv3.getZ();
            this.sizeZ = Math.max(this.sizeZ, k);
            this.reusablePos.move(j + 4, 0, 0);
            if (i++ % 8 != 0) continue;
            this.reusablePos.move(0, 0, this.sizeZ + 5);
            this.reusablePos.setX(this.pos.getX());
            this.sizeZ = 0;
        }
    }
}

