/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.SnbtProvider;
import net.minecraft.data.client.BlockStateDefinitionProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.ItemListProvider;
import net.minecraft.data.server.AdvancementsProvider;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.data.server.EntityTypeTagsProvider;
import net.minecraft.data.server.FluidTagsProvider;
import net.minecraft.data.server.ItemTagsProvider;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.validate.StructureValidatorProvider;

public class Main {
    public static void main(String[] strings) throws IOException {
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("server", "Include server generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("reports", "Include data reports");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("validate", "Validate inputs");
        OptionSpecBuilder optionSpec7 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(strings);
        if (optionSet.has((OptionSpec)optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn((OutputStream)System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec8.value(optionSet), new String[0]);
        boolean bl = optionSet.has((OptionSpec)optionSpec7);
        boolean bl2 = bl || optionSet.has((OptionSpec)optionSpec3);
        boolean bl3 = bl || optionSet.has((OptionSpec)optionSpec2);
        boolean bl4 = bl || optionSet.has((OptionSpec)optionSpec4);
        boolean bl5 = bl || optionSet.has((OptionSpec)optionSpec5);
        boolean bl6 = bl || optionSet.has((OptionSpec)optionSpec6);
        DataGenerator lv = Main.create(path, optionSet.valuesOf((OptionSpec)optionSpec9).stream().map(string -> Paths.get(string, new String[0])).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6);
        lv.run();
    }

    public static DataGenerator create(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate) {
        DataGenerator lv = new DataGenerator(output, inputs);
        if (includeClient || includeServer) {
            lv.install(new SnbtProvider(lv).addWriter(new StructureValidatorProvider()));
        }
        if (includeClient) {
            lv.install(new BlockStateDefinitionProvider(lv));
        }
        if (includeServer) {
            lv.install(new FluidTagsProvider(lv));
            BlockTagsProvider lv2 = new BlockTagsProvider(lv);
            lv.install(lv2);
            lv.install(new ItemTagsProvider(lv, lv2));
            lv.install(new EntityTypeTagsProvider(lv));
            lv.install(new RecipesProvider(lv));
            lv.install(new AdvancementsProvider(lv));
            lv.install(new LootTablesProvider(lv));
        }
        if (includeDev) {
            lv.install(new NbtProvider(lv));
        }
        if (includeReports) {
            lv.install(new BlockListProvider(lv));
            lv.install(new ItemListProvider(lv));
            lv.install(new CommandSyntaxProvider(lv));
        }
        return lv;
    }
}

