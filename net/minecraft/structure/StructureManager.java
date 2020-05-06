/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_5218;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.Structure;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Identifier, Structure> structures = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final MinecraftServer server;
    private final Path generatedPath;

    public StructureManager(MinecraftServer minecraftServer, LevelStorage.Session arg, DataFixer dataFixer) {
        this.server = minecraftServer;
        this.dataFixer = dataFixer;
        this.generatedPath = arg.getDirectory(class_5218.GENERATED).normalize();
        minecraftServer.getDataManager().registerListener(this);
    }

    public Structure getStructureOrBlank(Identifier arg) {
        Structure lv = this.getStructure(arg);
        if (lv == null) {
            lv = new Structure();
            this.structures.put(arg, lv);
        }
        return lv;
    }

    @Nullable
    public Structure getStructure(Identifier arg2) {
        return this.structures.computeIfAbsent(arg2, arg -> {
            Structure lv = this.loadStructureFromFile((Identifier)arg);
            return lv != null ? lv : this.loadStructureFromResource((Identifier)arg);
        });
    }

    @Override
    public void apply(ResourceManager arg) {
        this.structures.clear();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Structure loadStructureFromResource(Identifier arg) {
        Identifier lv = new Identifier(arg.getNamespace(), "structures/" + arg.getPath() + ".nbt");
        try (Resource lv2 = this.server.getDataManager().getResource(lv);){
            Structure structure = this.readStructure(lv2.getInputStream());
            return structure;
        }
        catch (FileNotFoundException fileNotFoundException) {
            return null;
        }
        catch (Throwable throwable6) {
            LOGGER.error("Couldn't load structure {}: {}", (Object)arg, (Object)throwable6.toString());
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Structure loadStructureFromFile(Identifier arg) {
        if (!this.generatedPath.toFile().isDirectory()) {
            return null;
        }
        Path path = this.getAndCheckStructurePath(arg, ".nbt");
        try (FileInputStream inputStream = new FileInputStream(path.toFile());){
            Structure structure = this.readStructure(inputStream);
            return structure;
        }
        catch (FileNotFoundException fileNotFoundException) {
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't load structure from {}", (Object)path, (Object)iOException);
            return null;
        }
    }

    private Structure readStructure(InputStream inputStream) throws IOException {
        CompoundTag lv = NbtIo.readCompressed(inputStream);
        return this.createStructure(lv);
    }

    public Structure createStructure(CompoundTag arg) {
        if (!arg.contains("DataVersion", 99)) {
            arg.putInt("DataVersion", 500);
        }
        Structure lv = new Structure();
        lv.fromTag(NbtHelper.update(this.dataFixer, DataFixTypes.STRUCTURE, arg, arg.getInt("DataVersion")));
        return lv;
    }

    public boolean saveStructure(Identifier arg) {
        Structure lv = this.structures.get(arg);
        if (lv == null) {
            return false;
        }
        Path path = this.getAndCheckStructurePath(arg, ".nbt");
        Path path2 = path.getParent();
        if (path2 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to create parent directory: {}", (Object)path2);
            return false;
        }
        CompoundTag lv2 = lv.toTag(new CompoundTag());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile());){
            NbtIo.writeCompressed(lv2, outputStream);
        }
        catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public Path getStructurePath(Identifier arg, String string) {
        try {
            Path path = this.generatedPath.resolve(arg.getNamespace());
            Path path2 = path.resolve("structures");
            return FileNameUtil.getResourcePath(path2, arg.getPath(), string);
        }
        catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + arg, invalidPathException);
        }
    }

    private Path getAndCheckStructurePath(Identifier arg, String string) {
        if (arg.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + arg);
        }
        Path path = this.getStructurePath(arg, string);
        if (!(path.startsWith(this.generatedPath) && FileNameUtil.isNormal(path) && FileNameUtil.isAllowedName(path))) {
            throw new InvalidIdentifierException("Invalid resource path: " + path);
        }
        return path;
    }

    public void unloadStructure(Identifier arg) {
        this.structures.remove(arg);
    }
}

