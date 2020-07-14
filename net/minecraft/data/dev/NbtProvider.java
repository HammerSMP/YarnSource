/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.dev;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nullable;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator root;

    public NbtProvider(DataGenerator arg) {
        this.root = arg;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path2 = this.root.getOutput();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".nbt")).forEach(path3 -> NbtProvider.convertNbtToSnbt(path3, this.getLocation(path22, (Path)path3), path2));
        }
    }

    @Override
    public String getName() {
        return "NBT to SNBT";
    }

    private String getLocation(Path targetPath, Path rootPath) {
        String string = targetPath.relativize(rootPath).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertNbtToSnbt(Path inputPath, String location, Path outputPath) {
        try {
            CompoundTag lv = NbtIo.readCompressed(Files.newInputStream(inputPath, new OpenOption[0]));
            Text lv2 = lv.toText("    ", 0);
            String string2 = lv2.getString() + "\n";
            Path path3 = outputPath.resolve(location + ".snbt");
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path3, new OpenOption[0]);){
                bufferedWriter.write(string2);
            }
            LOGGER.info("Converted {} from NBT to SNBT", (Object)location);
            return path3;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", (Object)location, (Object)inputPath, (Object)iOException);
            return null;
        }
    }
}

