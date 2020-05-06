/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ZipResourcePack;

public class FileResourcePackProvider
implements ResourcePackProvider {
    private static final FileFilter POSSIBLE_PACK = file -> {
        boolean bl = file.isFile() && file.getName().endsWith(".zip");
        boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
        return bl || bl2;
    };
    private final File packsFolder;

    public FileResourcePackProvider(File file) {
        this.packsFolder = file;
    }

    @Override
    public <T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> arg) {
        File[] files;
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }
        if ((files = this.packsFolder.listFiles(POSSIBLE_PACK)) == null) {
            return;
        }
        for (File file : files) {
            String string = "file/" + file.getName();
            T lv = ResourcePackProfile.of(string, false, this.createResourcePack(file), arg, ResourcePackProfile.InsertionPosition.TOP);
            if (lv == null) continue;
            map.put(string, lv);
        }
    }

    private Supplier<ResourcePack> createResourcePack(File file) {
        if (file.isDirectory()) {
            return () -> new DirectoryResourcePack(file);
        }
        return () -> new ZipResourcePack(file);
    }
}

