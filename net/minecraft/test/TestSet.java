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
import javax.annotation.Nullable;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestListener;

public class TestSet {
    private final Collection<GameTest> tests = Lists.newArrayList();
    @Nullable
    private TestListener listener;

    public TestSet() {
    }

    public TestSet(Collection<GameTest> collection) {
        this.tests.addAll(collection);
    }

    public void add(GameTest arg) {
        this.tests.add(arg);
        if (this.listener != null) {
            arg.addListener(this.listener);
        }
    }

    public void addListener(TestListener arg) {
        this.listener = arg;
        this.tests.forEach(arg2 -> arg2.addListener(arg));
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

