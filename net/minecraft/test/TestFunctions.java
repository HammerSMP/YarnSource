/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;

public class TestFunctions {
    private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.newArrayList();
    private static final Set<String> testClasses = Sets.newHashSet();
    private static final Map<String, Consumer<ServerWorld>> WORLD_SETTERS = Maps.newHashMap();
    private static final Collection<TestFunction> field_25302 = Sets.newHashSet();

    public static Collection<TestFunction> getTestFunctions(String string) {
        return TEST_FUNCTIONS.stream().filter(arg -> TestFunctions.isInClass(arg, string)).collect(Collectors.toList());
    }

    public static Collection<TestFunction> getTestFunctions() {
        return TEST_FUNCTIONS;
    }

    public static Collection<String> getTestClasses() {
        return testClasses;
    }

    public static boolean testClassExists(String string) {
        return testClasses.contains(string);
    }

    @Nullable
    public static Consumer<ServerWorld> getWorldSetter(String string) {
        return WORLD_SETTERS.get(string);
    }

    public static Optional<TestFunction> getTestFunction(String string) {
        return TestFunctions.getTestFunctions().stream().filter(arg -> arg.getStructurePath().equalsIgnoreCase(string)).findFirst();
    }

    public static TestFunction getTestFunctionOrThrow(String string) {
        Optional<TestFunction> optional = TestFunctions.getTestFunction(string);
        if (!optional.isPresent()) {
            throw new IllegalArgumentException("Can't find the test function for " + string);
        }
        return optional.get();
    }

    private static boolean isInClass(TestFunction arg, String string) {
        return arg.getStructurePath().toLowerCase().startsWith(string.toLowerCase() + ".");
    }

    public static Collection<TestFunction> method_29405() {
        return field_25302;
    }

    public static void method_29404(TestFunction arg) {
        field_25302.add(arg);
    }

    public static void method_29406() {
        field_25302.clear();
    }
}

