/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestListener;

public class TestSet {
    private final Collection<GameTest> tests = Lists.newArrayList();
    @Nullable
    private Collection<TestListener> field_25303 = Lists.newArrayList();

    public TestSet() {
    }

    public TestSet(Collection<GameTest> collection) {
        this.tests.addAll(collection);
    }

    public void add(GameTest arg) {
        this.tests.add(arg);
        this.field_25303.forEach(arg::addListener);
    }

    public void addListener(TestListener arg) {
        this.field_25303.add(arg);
        this.tests.forEach(arg2 -> arg2.addListener(arg));
    }

    public void method_29407(final Consumer<GameTest> consumer) {
        this.addListener(new TestListener(){

            @Override
            public void onStarted(GameTest arg) {
            }

            @Override
            public void onFailed(GameTest arg) {
                consumer.accept(arg);
            }
        });
    }

    public int getFailedRequiredTestCount() {
        return (int)this.tests.stream().filter(GameTest::isFailed).filter(GameTest::isRequired).count();
    }

    public int getFailedOptionalTestCount() {
        return (int)this.tests.stream().filter(GameTest::isFailed).filter(GameTest::isOptional).count();
    }

    public int getCompletedTestCount() {
        return (int)this.tests.stream().filter(GameTest::isCompleted).count();
    }

    public boolean failed() {
        return this.getFailedRequiredTestCount() > 0;
    }

    public boolean hasFailedOptionalTests() {
        return this.getFailedOptionalTestCount() > 0;
    }

    public int getTestCount() {
        return this.tests.size();
    }

    public boolean isDone() {
        return this.getCompletedTestCount() == this.getTestCount();
    }

    public String getResultString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('[');
        this.tests.forEach(arg -> {
            if (!arg.isStarted()) {
                stringBuffer.append(' ');
            } else if (arg.isPassed()) {
                stringBuffer.append('+');
            } else if (arg.isFailed()) {
                stringBuffer.append(arg.isRequired() ? (char)'X' : (char)'x');
            } else {
                stringBuffer.append('_');
            }
        });
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    public String toString() {
        return this.getResultString();
    }
}

