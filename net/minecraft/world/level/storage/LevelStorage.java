/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5315;
import net.minecraft.class_5359;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.SessionLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> field_25020 = ImmutableList.of((Object)"RandomSeed", (Object)"generatorName", (Object)"generatorOptions", (Object)"generatorVersion", (Object)"legacy_custom_options", (Object)"MapFeatures", (Object)"BonusChest");
    private final Path savesDirectory;
    private final Path backupsDirectory;
    private final DataFixer dataFixer;

    public LevelStorage(Path path, Path path2, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        try {
            Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.savesDirectory = path;
        this.backupsDirectory = path2;
    }

    public static LevelStorage create(Path path) {
        return new LevelStorage(path, path.resolve("../backups"), Schemas.getFixer());
    }

    private static Pair<GeneratorOptions, Lifecycle> method_29010(Dynamic<?> dynamic, DataFixer dataFixer, int i) {
        Dynamic dynamic2 = dynamic.get("WorldGenSettings").orElseEmptyMap();
        for (String string : field_25020) {
            Optional optional = dynamic.get(string).result();
            if (!optional.isPresent()) continue;
            dynamic2 = dynamic2.set(string, (Dynamic)optional.get());
        }
        Dynamic dynamic3 = dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, i, SharedConstants.getGameVersion().getWorldVersion());
        DataResult dataResult = GeneratorOptions.CODEC.parse(dynamic3);
        return Pair.of((Object)dataResult.resultOrPartial(Util.method_29188("WorldGenSettings: ", ((Logger)LOGGER)::error)).orElseGet(GeneratorOptions::getDefaultOptions), (Object)dataResult.lifecycle());
    }

    private static class_5359 method_29580(Dynamic<?> dynamic) {
        return class_5359.field_25394.parse(dynamic).resultOrPartial(((Logger)LOGGER)::error).orElse(class_5359.field_25393);
    }

    /*
     * WARNING - void declaration
     */
    @Environment(value=EnvType.CLIENT)
    public List<LevelSummary> getLevelList() throws LevelStorageException {
        File[] files;
        if (!Files.isDirectory(this.savesDirectory, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableText("selectWorld.load_folder_access").getString());
        }
        ArrayList list = Lists.newArrayList();
        for (File file : files = this.savesDirectory.toFile().listFiles()) {
            void bl2;
            if (!file.isDirectory()) continue;
            try {
                boolean bl = SessionLock.isLocked(file.toPath());
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read {} lock", (Object)file, (Object)exception);
                continue;
            }
            LevelSummary lv = this.readLevelProperties(file, this.method_29014(file, (boolean)bl2));
            if (lv == null) continue;
            list.add(lv);
        }
        return list;
    }

    private int getCurrentVersion() {
        return 19133;
    }

    @Nullable
    private <T> T readLevelProperties(File file, BiFunction<File, DataFixer, T> biFunction) {
        T object;
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (object = biFunction.apply(file2, this.dataFixer)) != null) {
            return object;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return biFunction.apply(file2, this.dataFixer);
        }
        return null;
    }

    @Nullable
    private static class_5359 method_29583(File file, DataFixer dataFixer) {
        try {
            CompoundTag lv = NbtIo.readCompressed(new FileInputStream(file));
            CompoundTag lv2 = lv.getCompound("Data");
            lv2.remove("Player");
            int i = lv2.contains("DataVersion", 99) ? lv2.getInt("DataVersion") : -1;
            Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv2), i, SharedConstants.getGameVersion().getWorldVersion());
            return dynamic.get("DataPacks").result().map(LevelStorage::method_29580).orElse(class_5359.field_25393);
        }
        catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)file, (Object)exception);
            return null;
        }
    }

    private static BiFunction<File, DataFixer, LevelProperties> readLevelProperties(DynamicOps<Tag> dynamicOps, class_5359 arg) {
        return (file, dataFixer) -> {
            try {
                CompoundTag lv = NbtIo.readCompressed(new FileInputStream((File)file));
                CompoundTag lv2 = lv.getCompound("Data");
                CompoundTag lv3 = lv2.contains("Player", 10) ? lv2.getCompound("Player") : null;
                lv2.remove("Player");
                int i = lv2.contains("DataVersion", 99) ? lv2.getInt("DataVersion") : -1;
                Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic(dynamicOps, (Object)lv2), i, SharedConstants.getGameVersion().getWorldVersion());
                Pair<GeneratorOptions, Lifecycle> pair = LevelStorage.method_29010(dynamic, dataFixer, i);
                class_5315 lv4 = class_5315.method_29023(dynamic);
                LevelInfo lv5 = LevelInfo.method_28383(dynamic, arg);
                return LevelProperties.method_29029((Dynamic<Tag>)dynamic, dataFixer, i, lv3, lv5, lv4, (GeneratorOptions)pair.getFirst(), (Lifecycle)pair.getSecond());
            }
            catch (Exception exception) {
                LOGGER.error("Exception reading {}", file, (Object)exception);
                return null;
            }
        };
    }

    private BiFunction<File, DataFixer, LevelSummary> method_29014(File file, boolean bl) {
        return (file2, dataFixer) -> {
            try {
                CompoundTag lv = NbtIo.readCompressed(new FileInputStream((File)file2));
                CompoundTag lv2 = lv.getCompound("Data");
                lv2.remove("Player");
                int i = lv2.contains("DataVersion", 99) ? lv2.getInt("DataVersion") : -1;
                Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)lv2), i, SharedConstants.getGameVersion().getWorldVersion());
                class_5315 lv3 = class_5315.method_29023(dynamic);
                int j = lv3.method_29022();
                if (j == 19132 || j == 19133) {
                    boolean bl2 = j != this.getCurrentVersion();
                    File file3 = new File(file, "icon.png");
                    class_5359 lv4 = dynamic.get("DataPacks").result().map(LevelStorage::method_29580).orElse(class_5359.field_25393);
                    LevelInfo lv5 = LevelInfo.method_28383(dynamic, lv4);
                    return new LevelSummary(lv5, lv3, file.getName(), bl2, bl, file3);
                }
                return null;
            }
            catch (Exception exception) {
                LOGGER.error("Exception reading {}", file2, (Object)exception);
                return null;
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLevelNameValid(String string) {
        try {
            Path path = this.savesDirectory.resolve(string);
            Files.createDirectory(path, new FileAttribute[0]);
            Files.deleteIfExists(path);
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean levelExists(String string) {
        return Files.isDirectory(this.savesDirectory.resolve(string), new LinkOption[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public Path getSavesDirectory() {
        return this.savesDirectory;
    }

    @Environment(value=EnvType.CLIENT)
    public Path getBackupsDirectory() {
        return this.backupsDirectory;
    }

    public Session createSession(String string) throws IOException {
        return new Session(string);
    }

    public class Session
    implements AutoCloseable {
        private final SessionLock lock;
        private final Path directory;
        private final String directoryName;
        private final Map<WorldSavePath, Path> field_24190 = Maps.newHashMap();

        public Session(String string) throws IOException {
            this.directoryName = string;
            this.directory = LevelStorage.this.savesDirectory.resolve(string);
            this.lock = SessionLock.create(this.directory);
        }

        public String getDirectoryName() {
            return this.directoryName;
        }

        public Path getDirectory(WorldSavePath arg2) {
            return this.field_24190.computeIfAbsent(arg2, arg -> this.directory.resolve(arg.getRelativePath()));
        }

        public File method_27424(RegistryKey<World> arg) {
            return DimensionType.getSaveDirectory(arg, this.directory.toFile());
        }

        private void checkValid() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public WorldSaveHandler method_27427() {
            this.checkValid();
            return new WorldSaveHandler(this, LevelStorage.this.dataFixer);
        }

        public boolean needsConversion() {
            LevelSummary lv = this.method_29584();
            return lv != null && lv.method_29586().method_29022() != LevelStorage.this.getCurrentVersion();
        }

        public boolean convert(ProgressListener arg) {
            this.checkValid();
            return AnvilLevelStorage.convertLevel(this, arg);
        }

        @Nullable
        public LevelSummary method_29584() {
            this.checkValid();
            return (LevelSummary)LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.this.method_29014(this.directory.toFile(), false));
        }

        @Nullable
        public SaveProperties readLevelProperties(DynamicOps<Tag> dynamicOps, class_5359 arg) {
            this.checkValid();
            return (SaveProperties)LevelStorage.this.readLevelProperties(this.directory.toFile(), LevelStorage.readLevelProperties((DynamicOps<Tag>)dynamicOps, arg));
        }

        @Nullable
        public class_5359 method_29585() {
            this.checkValid();
            return (class_5359)LevelStorage.this.readLevelProperties(this.directory.toFile(), (file, dataFixer) -> LevelStorage.method_29583(file, dataFixer));
        }

        public void method_27425(DimensionTracker arg, SaveProperties arg2) {
            this.method_27426(arg, arg2, null);
        }

        public void method_27426(DimensionTracker arg, SaveProperties arg2, @Nullable CompoundTag arg3) {
            File file = this.directory.toFile();
            CompoundTag lv = arg2.cloneWorldTag(arg, arg3);
            CompoundTag lv2 = new CompoundTag();
            lv2.put("Data", lv);
            try {
                File file2 = File.createTempFile("level", ".dat", file);
                NbtIo.writeCompressed(lv2, new FileOutputStream(file2));
                File file3 = new File(file, "level.dat_old");
                File file4 = new File(file, "level.dat");
                Util.method_27760(file4, file2, file3);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to save level {}", (Object)file, (Object)exception);
            }
        }

        public File getIconFile() {
            this.checkValid();
            return this.directory.resolve("icon.png").toFile();
        }

        @Environment(value=EnvType.CLIENT)
        public void deleteSessionLock() throws IOException {
            this.checkValid();
            final Path path = this.directory.resolve("session.lock");
            for (int i = 1; i <= 5; ++i) {
                LOGGER.info("Attempt {}...", (Object)i);
                try {
                    Files.walkFileTree(this.directory, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                        @Override
                        public FileVisitResult visitFile(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                            if (!path2.equals(path)) {
                                LOGGER.debug("Deleting {}", (Object)path2);
                                Files.delete(path2);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path path2, IOException iOException) throws IOException {
                            if (iOException != null) {
                                throw iOException;
                            }
                            if (path2.equals(Session.this.directory)) {
                                Session.this.lock.close();
                                Files.deleteIfExists(path);
                            }
                            Files.delete(path2);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public /* synthetic */ FileVisitResult postVisitDirectory(Object object, IOException iOException) throws IOException {
                            return this.postVisitDirectory((Path)object, iOException);
                        }

                        @Override
                        public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                            return this.visitFile((Path)object, basicFileAttributes);
                        }
                    });
                    break;
                }
                catch (IOException iOException) {
                    if (i < 5) {
                        LOGGER.warn("Failed to delete {}", (Object)this.directory, (Object)iOException);
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    throw iOException;
                }
            }
        }

        @Environment(value=EnvType.CLIENT)
        public void save(String string) throws IOException {
            this.checkValid();
            File file = new File(LevelStorage.this.savesDirectory.toFile(), this.directoryName);
            if (!file.exists()) {
                return;
            }
            File file2 = new File(file, "level.dat");
            if (file2.exists()) {
                CompoundTag lv = NbtIo.readCompressed(new FileInputStream(file2));
                CompoundTag lv2 = lv.getCompound("Data");
                lv2.putString("LevelName", string);
                NbtIo.writeCompressed(lv, new FileOutputStream(file2));
            }
        }

        @Environment(value=EnvType.CLIENT)
        public long createBackup() throws IOException {
            this.checkValid();
            String string = LocalDateTime.now().format(TIME_FORMATTER) + "_" + this.directoryName;
            Path path = LevelStorage.this.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Path path2 = path.resolve(FileNameUtil.getNextUniqueName(path, string, ".zip"));
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2, new OpenOption[0])));){
                final Path path3 = Paths.get(this.directoryName, new String[0]);
                Files.walkFileTree(this.directory, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                        if (path.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String string = path3.resolve(Session.this.directory.relativize(path)).toString().replace('\\', '/');
                        ZipEntry zipEntry = new ZipEntry(string);
                        zipOutputStream.putNextEntry(zipEntry);
                        com.google.common.io.Files.asByteSource((File)path.toFile()).copyTo((OutputStream)zipOutputStream);
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                        return this.visitFile((Path)object, basicFileAttributes);
                    }
                });
            }
            return Files.size(path2);
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }
    }
}

