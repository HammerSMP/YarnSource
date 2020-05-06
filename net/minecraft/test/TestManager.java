/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.test.GameTest;

public class TestManager {
    public static final TestManager INSTANCE = new TestManager();
    private final Collection<GameTest> tests = Lists.newCopyOnWriteArrayList();

    public void start(GameTest arg) {
        this.tests.add(arg);
    }

    public void clear() {
        this.tests.clear();
    }

    public void tick() {
        this.tests.forEach(GameTest::tick);
        this.tests.removeIf(GameTest::isCompleted);
    }
}

