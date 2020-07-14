/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider
implements DataProvider {
    @Nullable
    private static final Path field_24615 = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator root;
    private final List<Tweaker> write = Lists.newArrayList();

    public SnbtProvider(DataGenerator arg) {
        this.root = arg;
    }

    public SnbtProvider addWriter(Tweaker arg) {
        this.write.add(arg);
        return this;
    }

    private CompoundTag write(String string, CompoundTag arg) {
        CompoundTag lv = arg;
        for (Tweaker lv2 : this.write) {
            lv = lv2.write(string, lv);
        }
        return lv;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path3 = this.root.getOutput();
        ArrayList list = Lists.newArrayList();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path2 -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt((Path)path2, this.getFileName(path22, (Path)path2)), Util.getServerWorkerExecutor())));
        }
        Util.combine(list).join().stream().filter(Objects::nonNull).forEach(arg2 -> this.write(cache, (CompressedData)arg2, path3));
    }

    @Override
    public String getName() {
        return "SNBT -> NBT";
    }

    private String getFileName(Path root, Path file) {
        String string = root.relativize(file).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private CompressedData toCompressedNbt(Path path, String name) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
            String string5;
            String string2 = IOUtils.toString((Reader)bufferedReader);
            CompoundTag lv = this.write(name, StringNbtReader.parse(string2));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(lv, byteArrayOutputStream);
            byte[] bs = byteArrayOutputStream.toByteArray();
            String string3 = SHA1.hashBytes(bs).toString();
            if (field_24615 != null) {
                String string4 = lv.toText("    ", 0).getString() + "\n";
            } else {
                string5 = null;
            }
            CompressedData compressedData = new CompressedData(name, bs, string5, string3);
            return compressedData;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", (Object)name, (Object)path, (Object)commandSyntaxException);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", (Object)name, (Object)path, (Object)iOException);
        }
        return null;
    }

    private void write(DataCache arg, CompressedData arg2, Path path) {
        if (arg2.field_24616 != null) {
            Path path2 = field_24615.resolve(arg2.name + ".snbt");
            try {
                FileUtils.write((File)path2.toFile(), (CharSequence)arg2.field_24616, (Charset)StandardCharsets.UTF_8);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", (Object)arg2.name, (Object)path2, (Object)iOException);
            }
        }
        Path path3 = path.resolve(arg2.name + ".nbt");
        try {
            if (!Objects.equals(arg.getOldSha1(path3), arg2.sha1) || !Files.exists(path3, new LinkOption[0])) {
                Files.createDirectories(path3.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path3, new OpenOption[0]);){
                    outputStream.write(arg2.bytes);
                }
            }
            arg.updateSha1(path3, arg2.sha1);
        }
        catch (IOException iOException2) {
            LOGGER.error("Couldn't write structure {} at {}", (Object)arg2.name, (Object)path3, (Object)iOException2);
        }
    }

    @FunctionalInterface
    public static interface Tweaker {
        public CompoundTag write(String var1, CompoundTag var2);
    }

    static class CompressedData {
        private final String name;
        private final byte[] bytes;
        @Nullable
        private final String field_24616;
        private final String sha1;

        public CompressedData(String name, byte[] bytes, @Nullable String sha1, String string3) {
            this.name = name;
            this.bytes = bytes;
            this.field_24616 = sha1;
            this.sha1 = string3;
        }
    }
}

