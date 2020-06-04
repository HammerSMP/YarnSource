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
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.TestClassArgumentType;
import net.minecraft.command.arguments.TestFunctionArgumentType;
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
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").then(CommandManager.literal("runthis").executes(commandContext -> TestCommand.executeRunThis((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("runthese").executes(commandContext -> TestCommand.executeRunThese((ServerCommandSource)commandContext.getSource())))).then(((LiteralArgumentBuilder)CommandManager.literal("runfailed").executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), false, 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.method_29411((ServerCommandSource)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow")))))))).then(CommandManager.literal("run").then(((RequiredArgumentBuilder)CommandManager.argument("testName", TestFunctionArgumentType.testFunction()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, "testName"), 0))).then(CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, "testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("runall").executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("testClassName", TestClassArgumentType.testClass()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), 0, 8))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(CommandManager.literal("export").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeExport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(CommandManager.literal("exportthis").executes(commandContext -> TestCommand.method_29413((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("import").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeImport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("pos").executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), "pos"))).then(CommandManager.argument("var", StringArgumentType.word()).executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"var")))))).then(CommandManager.literal("create").then(((RequiredArgumentBuilder)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width")))).then(CommandManager.argument("height", IntegerArgumentType.integer()).then(CommandManager.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"height"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"depth"))))))))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), 200))).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"radius"))))));
    }

    private static int executeCreate(ServerCommandSource arg, String string, int i, int j, int k) {
        if (i > 48 || j > 48 || k > 48) {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
        ServerWorld lv = arg.getWorld();
        BlockPos lv2 = new BlockPos(arg.getPosition());
        BlockPos lv3 = new BlockPos(lv2.getX(), arg.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv2).getY(), lv2.getZ() + 3);
        StructureTestUtil.createTestArea(string.toLowerCase(), lv3, new BlockPos(i, j, k), BlockRotation.NONE, lv);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < k; ++m) {
                BlockPos lv4 = new BlockPos(lv3.getX() + l, lv3.getY() + 1, lv3.getZ() + m);
                Block lv5 = Blocks.POLISHED_ANDESITE;
                BlockStateArgument lv6 = new BlockStateArgument(lv5.getDefaultState(), Collections.EMPTY_SET, null);
                lv6.setBlockState(lv, lv4, 2);
            }
        }
        StructureTestUtil.placeStartButton(lv3, new BlockPos(1, 0, -1), BlockRotation.NONE, lv);
        return 0;
    }

    private static int executePos(ServerCommandSource arg, String string) throws CommandSyntaxException {
        ServerWorld lv3;
        BlockHitResult lv = (BlockHitResult)arg.getPlayer().rayTrace(10.0, 1.0f, false);
        BlockPos lv2 = lv.getBlockPos();
        Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(lv2, 15, lv3 = arg.getWorld());
        if (!optional.isPresent()) {
            optional = StructureTestUtil.findContainingStructureBlock(lv2, 200, lv3);
        }
        if (!optional.isPresent()) {
            arg.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + lv2));
            return 0;
        }
        StructureBlockBlockEntity lv4 = (StructureBlockBlockEntity)lv3.getBlockEntity(optional.get());
        BlockPos lv5 = lv2.subtract(optional.get());
        String string2 = lv5.getX() + ", " + lv5.getY() + ", " + lv5.getZ();
        String string3 = lv4.getStructurePath();
        MutableText lv6 = new LiteralText(string2).setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + string + " = new BlockPos(" + string2 + ");")));
        arg.sendFeedback(new LiteralText("Position relative to " + string3 + ": ").append(lv6), false);
        DebugInfoSender.addGameTestMarker(lv3, new BlockPos(lv2), string2, -2147418368, 10000);
        return 1;
    }

    private static int executeRunThis(ServerCommandSource arg) {
        ServerWorld lv2;
        BlockPos lv = new BlockPos(arg.getPosition());
        BlockPos lv3 = StructureTestUtil.findNearestStructureBlock(lv, 15, lv2 = arg.getWorld());
        if (lv3 == null) {
            TestCommand.sendMessage(lv2, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        TestUtil.clearDebugMarkers(lv2);
        TestCommand.run(lv2, lv3, null);
        return 1;
    }

    private static int executeRunThese(ServerCommandSource arg) {
        ServerWorld lv2;
        BlockPos lv = new BlockPos(arg.getPosition());
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(lv, 200, lv2 = arg.getWorld());
        if (collection.isEmpty()) {
            TestCommand.sendMessage(lv2, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
            return 1;
        }
        TestUtil.clearDebugMarkers(lv2);
        TestCommand.sendMessage(arg, "Running " + collection.size() + " tests...");
        TestSet lv3 = new TestSet();
        collection.forEach(arg3 -> TestCommand.run(lv2, arg3, lv3));
        return 1;
    }

    private static void run(ServerWorld arg, BlockPos arg2, @Nullable TestSet arg3) {
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg.getBlockEntity(arg2);
        String string = lv.getStructurePath();
        TestFunction lv2 = TestFunctions.getTestFunctionOrThrow(string);
        GameTest lv3 = new GameTest(lv2, lv.getRotation(), arg);
        if (arg3 != null) {
            arg3.add(lv3);
            lv3.addListener(new Listener(arg, arg3));
        }
        TestCommand.setWorld(lv2, arg);
        Box lv4 = StructureTestUtil.getStructureBoundingBox(lv);
        BlockPos lv5 = new BlockPos(lv4.minX, lv4.minY, lv4.minZ);
        TestUtil.startTest(lv3, lv5, TestManager.INSTANCE);
    }

    private static void onCompletion(ServerWorld arg, TestSet arg2) {
        if (arg2.isDone()) {
            TestCommand.sendMessage(arg, "GameTest done! " + arg2.getTestCount() + " tests were run", Formatting.WHITE);
            if (arg2.failed()) {
                TestCommand.sendMessage(arg, "" + arg2.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
            } else {
                TestCommand.sendMessage(arg, "All required tests passed :)", Formatting.GREEN);
            }
            if (arg2.hasFailedOptionalTests()) {
                TestCommand.sendMessage(arg, "" + arg2.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
            }
        }
    }

    private static int executeClearAll(ServerCommandSource arg, int i) {
        ServerWorld lv = arg.getWorld();
        TestUtil.clearDebugMarkers(lv);
        BlockPos lv2 = new BlockPos(arg.getPosition().x, (double)arg.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(arg.getPosition())).getY(), arg.getPosition().z);
        TestUtil.clearTests(lv, lv2, TestManager.INSTANCE, MathHelper.clamp(i, 0, 1024));
        return 1;
    }

    private static int executeRun(ServerCommandSource arg, TestFunction arg2, int i) {
        ServerWorld lv = arg.getWorld();
        BlockPos lv2 = new BlockPos(arg.getPosition());
        int j = arg.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv2).getY();
        BlockPos lv3 = new BlockPos(lv2.getX(), j, lv2.getZ() + 3);
        TestUtil.clearDebugMarkers(lv);
        TestCommand.setWorld(arg2, lv);
        BlockRotation lv4 = StructureTestUtil.method_29408(i);
        GameTest lv5 = new GameTest(arg2, lv4, lv);
        TestUtil.startTest(lv5, lv3, TestManager.INSTANCE);
        return 1;
    }

    private static void setWorld(TestFunction arg, ServerWorld arg2) {
        Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(arg.getBatchId());
        if (consumer != null) {
            consumer.accept(arg2);
        }
    }

    private static int executeRunAll(ServerCommandSource arg, int i, int j) {
        TestUtil.clearDebugMarkers(arg.getWorld());
        Collection<TestFunction> collection = TestFunctions.getTestFunctions();
        TestCommand.sendMessage(arg, "Running all " + collection.size() + " tests...");
        TestFunctions.method_29406();
        TestCommand.run(arg, collection, i, j);
        return 1;
    }

    private static int executeRunAll(ServerCommandSource arg, String string, int i, int j) {
        Collection<TestFunction> collection = TestFunctions.getTestFunctions(string);
        TestUtil.clearDebugMarkers(arg.getWorld());
        TestCommand.sendMessage(arg, "Running " + collection.size() + " tests from " + string + "...");
        TestFunctions.method_29406();
        TestCommand.run(arg, collection, i, j);
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

    private static void run(ServerCommandSource arg2, Collection<TestFunction> collection, int i, int j) {
        BlockPos lv = new BlockPos(arg2.getPosition());
        BlockPos lv2 = new BlockPos(lv.getX(), arg2.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, lv).getY(), lv.getZ() + 3);
        ServerWorld lv3 = arg2.getWorld();
        BlockRotation lv4 = StructureTestUtil.method_29408(i);
        Collection<GameTest> collection2 = TestUtil.runTestFunctions(collection, lv2, lv4, lv3, TestManager.INSTANCE, j);
        TestSet lv5 = new TestSet(collection2);
        lv5.addListener(new Listener(lv3, lv5));
        lv5.method_29407(arg -> TestFunctions.method_29404(arg.method_29403()));
    }

    private static void sendMessage(ServerCommandSource arg, String string) {
        arg.sendFeedback(new LiteralText(string), false);
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

    private static int executeExport(ServerCommandSource arg, String string) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, new String[0]);
        Identifier lv = new Identifier("minecraft", string);
        Path path2 = arg.getWorld().getStructureManager().getStructurePath(lv, ".nbt");
        Path path3 = NbtProvider.convertNbtToSnbt(path2, string, path);
        if (path3 == null) {
            TestCommand.sendMessage(arg, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        }
        catch (IOException iOException) {
            TestCommand.sendMessage(arg, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.sendMessage(arg, "Exported " + string + " to " + path3.toAbsolutePath());
        return 0;
    }

    private static int executeImport(ServerCommandSource arg, String string) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, string + ".snbt");
        Identifier lv = new Identifier("minecraft", string);
        Path path2 = arg.getWorld().getStructureManager().getStructurePath(lv, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string2 = IOUtils.toString((Reader)bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);
            NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
            TestCommand.sendMessage(arg, "Imported to " + path2.toAbsolutePath());
            return 0;
        }
        catch (CommandSyntaxException | IOException exception) {
            System.err.println("Failed to load structure " + string);
            exception.printStackTrace();
            return 1;
        }
    }

    private static void sendMessage(ServerWorld arg3, String string, Formatting arg22) {
        arg3.getPlayers(arg -> true).forEach(arg2 -> arg2.sendSystemMessage(new LiteralText((Object)((Object)arg22) + string), Util.NIL_UUID));
    }

    static class Listener
    implements TestListener {
        private final ServerWorld world;
        private final TestSet tests;

        public Listener(ServerWorld arg, TestSet arg2) {
            this.world = arg;
            this.tests = arg2;
        }

        @Override
        public void onStarted(GameTest arg) {
        }

        @Override
        public void onFailed(GameTest arg) {
            TestCommand.onCompletion(this.world, this.tests);
        }
    }
}

