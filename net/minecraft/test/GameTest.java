/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TickLimitExceededException;
import net.minecraft.test.TimedTaskRunner;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class GameTest {
    private final TestFunction testFunction;
    @Nullable
    private BlockPos pos;
    private final ServerWorld world;
    private final Collection<TestListener> listeners = Lists.newArrayList();
    private final int ticksLeft;
    private final Collection<TimedTaskRunner> field_21452 = Lists.newCopyOnWriteArrayList();
    private Object2LongMap<Runnable> field_21453 = new Object2LongOpenHashMap();
    private long expectedStopTime;
    private long field_21455;
    private boolean started = false;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private boolean completed = false;
    private final BlockRotation field_25301;
    @Nullable
    private Throwable throwable;

    public GameTest(TestFunction arg, BlockRotation arg2, ServerWorld arg3) {
        this.testFunction = arg;
        this.world = arg3;
        this.ticksLeft = arg.getTickLimit();
        this.field_25301 = arg.method_29424().rotate(arg2);
    }

    void setPos(BlockPos arg) {
        this.pos = arg;
    }

    void startCountdown() {
        this.expectedStopTime = this.world.getTime() + 1L + this.testFunction.getDuration();
        this.stopwatch.start();
    }

    public void tick() {
        if (this.isCompleted()) {
            return;
        }
        this.field_21455 = this.world.getTime() - this.expectedStopTime;
        if (this.field_21455 < 0L) {
            return;
        }
        if (this.field_21455 == 0L) {
            this.start();
        }
        ObjectIterator objectIterator = this.field_21453.object2LongEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2LongMap.Entry entry = (Object2LongMap.Entry)objectIterator.next();
            if (entry.getLongValue() > this.field_21455) continue;
            try {
                ((Runnable)entry.getKey()).run();
            }
            catch (Exception exception) {
                this.fail(exception);
            }
            objectIterator.remove();
        }
        if (this.field_21455 > (long)this.ticksLeft) {
            if (this.field_21452.isEmpty()) {
                this.fail(new TickLimitExceededException("Didn't succeed or fail within " + this.testFunction.getTickLimit() + " ticks"));
            } else {
                this.field_21452.forEach(arg -> arg.runReported(this.field_21455));
                if (this.throwable == null) {
                    this.fail(new TickLimitExceededException("No sequences finished"));
                }
            }
        } else {
            this.field_21452.forEach(arg -> arg.runSilently(this.field_21455));
        }
    }

    private void start() {
        if (this.started) {
            throw new IllegalStateException("Test already started");
        }
        this.started = true;
        try {
            this.testFunction.start(new StartupParameter(this));
        }
        catch (Exception exception) {
            this.fail(exception);
        }
    }

    public String getStructurePath() {
        return this.testFunction.getStructurePath();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public boolean isPassed() {
        return this.completed && this.throwable == null;
    }

    public boolean isFailed() {
        return this.throwable != null;
    }

    public boolean isStarted() {
        return this.started;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    private void complete() {
        if (!this.completed) {
            this.completed = true;
            this.stopwatch.stop();
        }
    }

    public void fail(Throwable throwable) {
        this.complete();
        this.throwable = throwable;
        this.listeners.forEach(arg -> arg.onFailed(this));
    }

    @Nullable
    public Throwable getThrowable() {
        return this.throwable;
    }

    public String toString() {
        return this.getStructurePath();
    }

    public void addListener(TestListener arg) {
        this.listeners.add(arg);
    }

    public void init(BlockPos arg2, int i) {
        StructureBlockBlockEntity lv = StructureTestUtil.method_22250(this.getStructureName(), arg2, this.method_29402(), i, this.world, false);
        this.setPos(lv.getPos());
        lv.setStructureName(this.getStructurePath());
        StructureTestUtil.placeStartButton(this.pos, new BlockPos(1, 0, -1), this.method_29402(), this.world);
        this.listeners.forEach(arg -> arg.onStarted(this));
    }

    public boolean isRequired() {
        return this.testFunction.isRequired();
    }

    public boolean isOptional() {
        return !this.testFunction.isRequired();
    }

    public String getStructureName() {
        return this.testFunction.getStructureName();
    }

    public BlockRotation method_29402() {
        return this.field_25301;
    }

    public TestFunction method_29403() {
        return this.testFunction;
    }
}

