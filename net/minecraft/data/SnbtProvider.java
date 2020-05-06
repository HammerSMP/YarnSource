/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider
implements DataProvider {
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
    public void run(DataCache arg) throws IOException {
        Path path3 = this.root.getOutput();
        ArrayList list = Lists.newArrayList();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path2 -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt((Path)path2, this.getFileName(path22, (Path)path2)), Util.getServerWorkerExecutor())));
        }
        Util.combine(list).join().stream().filter(Objects::nonNull).forEach(arg2 -> this.write(arg, (CompressedData)arg2, path3));
    }

    @Override
    public String getName() {
        return "SNBT -> NBT";
    }

    private String getFileName(Path path, Path path2) {
        String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private CompressedData toCompressedNbt(Path path, String string) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
            String string2 = IOUtils.toString((Reader)bufferedReader);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(this.write(string, StringNbtReader.parse(string2)), byteArrayOutputStream);
            byte[] bs = byteArrayOutputStream.toByteArray();
            String string3 = SHA1.hashBytes(bs).toString();
            CompressedData compressedData = new CompressedData(string, bs, string3);
            return compressedData;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", (Object)string, (Object)path, (Object)commandSyntaxException);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", (Object)string, (Object)path, (Object)iOException);
        }
        return null;
    }

    private void write(DataCache arg, CompressedData arg2, Path path) {
        Path path2 = path.resolve(arg2.name + ".nbt");
        try {
            if (!Objects.equals(arg.getOldSha1(path2), arg2.sha1) || !Files.exists(path2, new LinkOption[0])) {
                Files.createDirectories(path2.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);){
                    outputStream.write(arg2.bytes);
                }
            }
            arg.updateSha1(path2, arg2.sha1);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", (Object)arg2.name, (Object)path2, (Object)iOException);
        }
    }

    @FunctionalInterface
    public static interface Tweaker {
        public CompoundTag write(String var1, CompoundTag var2);
    }

    static class CompressedData {
        private final String name;
        private final byte[] bytes;
        private final String sha1;

        public CompressedData(String string, byte[] bs, String string2) {
            this.name = string;
            this.bytes = bs;
            this.sha1 = string2;
        }
    }
}

