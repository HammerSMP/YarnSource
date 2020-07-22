/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").then(CommandManager.literal("runthis").executes(commandContext -> TestCommand.executeRunThis((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("runthese").executes(commandContext -> TestCommand.executeRunThese((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("runfailed").executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), false, 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow")))))))).then(CommandManager.literal("run").then(((RequiredArgumentBuilder)CommandManager.argument("testName", TestFunctionArgumentType.testFunction()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, "testName"), 0))).then(CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, "testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("runall").executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("testClassName", TestClassArgumentType.testClass()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(CommandManager.literal("export").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeExport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(CommandManager.literal("exportthis").executes(commandContext -> TestCommand.method_29413((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("import").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeImport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("pos").executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), "pos"))).then(CommandManager.argument("var", StringArgumentType.word()).executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"var")))))).then(CommandManager.literal("create").then(((RequiredArgumentBuilder)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width")))).then(CommandManager.argument("height", IntegerArgumentType.integer()).then(CommandManager.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"height"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"depth"))))))))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), 200))).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"radius"))))));
    }

    private static int executeCreate(ServerCommandSource source, String structure, int x, int y, int z) {
        if (x > 48 || y > 48 || z > 48) {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
        ServerWorld lv = source.getWorld();
        BlockPos lv2 = new BlockPos(source.getPosition());
        BlockPos lv3 = new BlockPos(lv2.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv2).getY(), lv2.getZ() + 3);
        StructureTestUtil.createTestArea(structure.toLowerCase(), lv3, new BlockPos(x, y, z), BlockRotation.NONE, lv);
        for (int l = 0; l < x; ++l) {
            for (int m = 0; m < z; ++m) {
                BlockPos lv4 = new BlockPos(lv3.getX() + l, lv3.getY() + 1, lv3.getZ() + m);
                Block lv5 = Blocks.POLISHED_ANDESITE;
                BlockStateArgument lv6 = new BlockStateArgument(lv5.getDefaultState(), Collections.EMPTY_SET, null);
                lv6.setBlockState(lv, lv4, 2);
            }
        }
        StructureTestUtil.placeStartButton(lv3, new BlockPos(1, 0, -1), BlockRotation.NONE, lv);
        return 0;
    }

    private static int executePos(ServerCommandSource source, String variableName) throws CommandSyntaxException {
        ServerWorld lv3;
        BlockHitResult lv = (BlockHitResult)source.getPlayer().rayTrace(10.0, 1.0f, false);
        BlockPos lv2 = lv.getBlockPos();
        Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(lv2, 15, lv3 = source.getWorld());
        if (!optional.isPresent()) {
            optional = StructureTestUtil.findContainingStructureBlock(lv2, 200, lv3);
        }
        if (!optional.isPresent()) {
            source.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + lv2));
            return 0;
        }
        StructureBlockBlockEntity lv4 = (StructureBlockBlockEntity)lv3.getBlockEntity(optional.get());
        BlockPos lv5 = lv2.subtract(optional.get());
        String string2 = lv5.getX() + ", " + lv5.getY() + ", " + lv5.getZ();
        String string3 = lv4.getStructurePath();
        MutableText lv6 = new LiteralText(string2).setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string2 + ");")));
        source.sendFeedback(new LiteralText("Position relative to " + string3 + ": ").append(lv6), false);
        DebugInfoSender.addGameTestMarker(lv3, new BlockPos(lv2), string2, -2147418368, 10000);
        return 1;
    }

    private static int executeRunThis(ServerCommandSource source) {
        ServerWorld lv2;
        BlockPos lv = new BlockPos(source.getPosition());
        BlockPos lv3 = StructureTestUtil.findNearestStructureBlock(lv, 15, lv2 = source.getWorld());
        if (lv3 == null) {
            TestCommand.sendMessage(lv2, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        TestUtil.clearDebugMarkers(lv2);
        TestCommand.run(lv2, lv3, null);
        return 1;
    }

    private static int executeRunThese(ServerCommandSource source) {
        ServerWorld lv2;
        BlockPos lv = new BlockPos(source.getPosition());
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(lv, 200, lv2 = source.getWorld());
        if (collection.isEmpty()) {
            TestCommand.sendMessage(lv2, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
            return 1;
        }
        TestUtil.clearDebugMarkers(lv2);
        TestCommand.sendMessage(source, "Running " + collection.size() + " tests...");
        TestSet lv3 = new TestSet();
        collection.forEach(arg3 -> TestCommand.run(lv2, arg3, lv3));
        return 1;
    }

    private static void run(ServerWorld world, BlockPos pos, @Nullable TestSet tests) {
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)world.getBlockEntity(pos);
        String string = lv.getStructurePath();
        TestFunction lv2 = TestFunctions.getTestFunctionOrThrow(string);
        GameTest lv3 = new GameTest(lv2, lv.getRotation(), world);
        if (tests != null) {
            tests.add(lv3);
            lv3.addListener(new Listener(world, tests));
        }
        TestCommand.setWorld(lv2, world);
        Box lv4 = StructureTestUtil.getStructureBoundingBox(lv);
        BlockPos lv5 = new BlockPos(lv4.minX, lv4.minY, lv4.minZ);
        TestUtil.startTest(lv3, lv5, TestManager.INSTANCE);
    }

    private static void onCompletion(ServerWorld world, TestSet tests) {
        if (tests.isDone()) {
            TestCommand.sendMessage(world, "GameTest done! " + tests.getTestCount() + " tests were run", Formatting.WHITE);
            if (tests.failed()) {
                TestCommand.sendMessage(world, "" + tests.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
            } else {
                TestCommand.sendMessage(world, "All required tests passed :)", Formatting.GREEN);
            }
            if (tests.hasFailedOptionalTests()) {
                TestCommand.sendMessage(world, "" + tests.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
            }
        }
    }

    private static int executeClearAll(ServerCommandSource source, int radius) {
        ServerWorld lv = source.getWorld();
        TestUtil.clearDebugMarkers(lv);
        BlockPos lv2 = new BlockPos(source.getPosition().x, (double)source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(source.getPosition())).getY(), source.getPosition().z);
        TestUtil.clearTests(lv, lv2, TestManager.INSTANCE, MathHelper.clamp(radius, 0, 1024));
        return 1;
    }

    private static int executeRun(ServerCommandSource source, TestFunction testFunction, int i) {
        ServerWorld lv = source.getWorld();
        BlockPos lv2 = new BlockPos(source.getPosition());
        int j = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv2).getY();
        BlockPos lv3 = new BlockPos(lv2.getX(), j, lv2.getZ() + 3);
        TestUtil.clearDebugMarkers(lv);
        TestCommand.setWorld(testFunction, lv);
        BlockRotation lv4 = StructureTestUtil.method_29408(i);
        GameTest lv5 = new GameTest(testFunction, lv4, lv);
        TestUtil.startTest(lv5, lv3, TestManager.INSTANCE);
        return 1;
    }

    private static void setWorld(TestFunction testFunction, ServerWorld world) {
        Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(testFunction.getBatchId());
        if (consumer != null) {
            consumer.accept(world);
        }
    }

    private static int executeRunAll(ServerCommandSource source, int i, int j) {
        TestUtil.clearDebugMarkers(source.getWorld());
        Collection<TestFunction> collection = TestFunctions.getTestFunctions();
        TestCommand.sendMessage(source, "Running all " + collection.size() + " tests...");
        TestFunctions.method_29406();
        TestCommand.run(source, collection, i, j);
        return 1;
    }

    private static int executeRunAll(ServerCommandSource source, String testClass, int i, int j) {
        Collection<TestFunction> collection = TestFunctions.getTestFunctions(testClass);
        TestUtil.clearDebugMarkers(source.getWorld());
        TestCommand.sendMessage(source, "Running " + collection.size() + " tests from " + testClass + "...");
        TestFunctions.method_29406();
        TestCommand.run(source, collection, i, j);
        return 1;
    }

    private static int method_29411(ServerCommandSource arg, boolean bl, int i, int j) {
        Collection<TestFunction> collection2;
        if (bl) {
            Collection collection = TestFunctions.method_29405().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
        } else {
            collection2 = TestFunctions.method_29405();
        }
        if (collection2.isEmpty()) {
            TestCommand.sendMessage(arg, "No failed tests to rerun");
            return 0;
        }
        TestUtil.clearDebugMarkers(arg.getWorld());
        TestCommand.sendMessage(arg, "Rerunning " + collection2.size() + " failed tests (" + (bl ? "only required tests" : "including optional tests") + ")");
        TestCommand.run(arg, collection2, i, j);
        return 1;
    }

    private static void run(ServerCommandSource source, Collection<TestFunction> testFunctions, int i, int j) {
        BlockPos lv = new BlockPos(source.getPosition());
        BlockPos lv2 = new BlockPos(lv.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv).getY(), lv.getZ() + 3);
        ServerWorld lv3 = source.getWorld();
        BlockRotation lv4 = StructureTestUtil.method_29408(i);
        Collection<GameTest> collection2 = TestUtil.runTestFunctions(testFunctions, lv2, lv4, lv3, TestManager.INSTANCE, j);
        TestSet lv5 = new TestSet(collection2);
        lv5.addListener(new Listener(lv3, lv5));
        lv5.method_29407(arg -> TestFunctions.method_29404(arg.method_29403()));
    }

    private static void sendMessage(ServerCommandSource source, String message) {
        source.sendFeedback(new LiteralText(message), false);
    }

    private static int method_29413(ServerCommandSource arg) {
        ServerWorld lv2;
        BlockPos lv = new BlockPos(arg.getPosition());
        BlockPos lv3 = StructureTestUtil.findNearestStructureBlock(lv, 15, lv2 = arg.getWorld());
        if (lv3 == null) {
            TestCommand.sendMessage(lv2, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        StructureBlockBlockEntity lv4 = (StructureBlockBlockEntity)lv2.getBlockEntity(lv3);
        String string = lv4.getStructurePath();
        return TestCommand.executeExport(arg, string);
    }

    private static int executeExport(ServerCommandSource source, String structure) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, new String[0]);
        Identifier lv = new Identifier("minecraft", structure);
        Path path2 = source.getWorld().getStructureManager().getStructurePath(lv, ".nbt");
        Path path3 = NbtProvider.convertNbtToSnbt(path2, structure, path);
        if (path3 == null) {
            TestCommand.sendMessage(source, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        }
        catch (IOException iOException) {
            TestCommand.sendMessage(source, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.sendMessage(source, "Exported " + structure + " to " + path3.toAbsolutePath());
        return 0;
    }

    private static int executeImport(ServerCommandSource arg, String structure) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, structure + ".snbt");
        Identifier lv = new Identifier("minecraft", structure);
        Path path2 = arg.getWorld().getStructureManager().getStructurePath(lv, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string2 = IOUtils.toString((Reader)bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            try (OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);){
                NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
            }
            TestCommand.sendMessage(arg, "Imported to " + path2.toAbsolutePath());
            return 0;
        }
        catch (CommandSyntaxException | IOException exception) {
            System.err.println("Failed to load structure " + structure);
            exception.printStackTrace();
            return 1;
        }
    }

    private static void sendMessage(ServerWorld world, String message, Formatting formatting) {
        world.getPlayers(arg -> true).forEach(arg2 -> arg2.sendSystemMessage(new LiteralText((Object)((Object)formatting) + message), Util.NIL_UUID));
    }

    static class Listener
    implements TestListener {
        private final ServerWorld world;
        private final TestSet tests;

        public Listener(ServerWorld world, TestSet tests) {
            this.world = world;
            this.tests = tests;
        }

        @Override
        public void onStarted(GameTest test) {
        }

        @Override
        public void onFailed(GameTest test) {
            TestCommand.onCompletion(this.world, this.tests);
        }
    }
}

