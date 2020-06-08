/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientBuiltinResourcePackProvider
implements ResourcePackProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final DefaultResourcePack pack;
    private final File serverPacksRoot;
    private final ReentrantLock lock = new ReentrantLock();
    private final ResourceIndex index;
    @Nullable
    private CompletableFuture<?> downloadTask;
    @Nullable
    private ClientResourcePackProfile serverContainer;

    public ClientBuiltinResourcePackProvider(File file, ResourceIndex arg) {
        this.serverPacksRoot = file;
        this.index = arg;
        this.pack = new DefaultClientResourcePack(arg);
    }

    @Override
    public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.Factory<T> arg) {
        T lv2;
        T lv = ResourcePackProfile.of("vanilla", true, () -> this.pack, arg, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN);
        if (lv != null) {
            consumer.accept((ClientResourcePackProfile)lv);
        }
        if (this.serverContainer != null) {
            consumer.accept(this.serverContainer);
        }
        if ((lv2 = this.method_25454(arg)) != null) {
            consumer.accept((ClientResourcePackProfile)lv2);
        }
    }

    public DefaultResourcePack getPack() {
        return this.pack;
    }

    public static Map<String, String> getDownloadHeaders() {
        HashMap map = Maps.newHashMap();
        map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
        map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
        map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
        map.put("X-Minecraft-Version-ID", SharedConstants.getGameVersion().getId());
        map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
        map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> download(String string, String string2) {
        String string3 = DigestUtils.sha1Hex((String)string);
        String string4 = ALPHANUMERAL.matcher(string2).matches() ? string2 : "";
        this.lock.lock();
        try {
            CompletableFuture<?> completableFuture2;
            this.clear();
            this.deleteOldServerPack();
            File file = new File(this.serverPacksRoot, string3);
            if (file.exists()) {
                CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("");
            } else {
                ProgressScreen lv = new ProgressScreen();
                Map<String, String> map = ClientBuiltinResourcePackProvider.getDownloadHeaders();
                MinecraftClient lv2 = MinecraftClient.getInstance();
                lv2.submitAndJoin(() -> lv2.openScreen(lv));
                completableFuture2 = NetworkUtils.download(file, string, map, 0x6400000, lv, lv2.getNetworkProxy());
            }
            CompletableFuture<?> completableFuture = this.downloadTask = ((CompletableFuture)completableFuture2.thenCompose(object -> {
                if (!this.verifyFile(string4, file)) {
                    return Util.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"));
                }
                return this.loadServerPack(file, ResourcePackSource.PACK_SOURCE_SERVER);
            })).whenComplete((void_, throwable) -> {
                if (throwable != null) {
                    LOGGER.warn("Pack application failed: {}, deleting file {}", (Object)throwable.getMessage(), (Object)file);
                    ClientBuiltinResourcePackProvider.delete(file);
                }
            });
            return completableFuture;
        }
        finally {
            this.lock.unlock();
        }
    }

    private static void delete(File file) {
        try {
            Files.delete(file.toPath());
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to delete file {}: {}", (Object)file, (Object)iOException.getMessage());
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            if (this.downloadTask != null) {
                this.downloadTask.cancel(true);
            }
            this.downloadTask = null;
            if (this.serverContainer != null) {
                this.serverContainer = null;
                MinecraftClient.getInstance().reloadResourcesConcurrently();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - void declaration
     */
    private boolean verifyFile(String string, File file) {
        try {
            void string3;
            try (FileInputStream fileInputStream = new FileInputStream(file);){
                String string2 = DigestUtils.sha1Hex((InputStream)fileInputStream);
            }
            if (string.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", (Object)file);
                return true;
            }
            if (string3.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", (Object)file, (Object)string);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", (Object)file, (Object)string, (Object)string3);
        }
        catch (IOException iOException) {
            LOGGER.warn("File {} couldn't be hashed.", (Object)file, (Object)iOException);
        }
        return false;
    }

    private void deleteOldServerPack() {
        try {
            ArrayList list = Lists.newArrayList((Iterable)FileUtils.listFiles((File)this.serverPacksRoot, (IOFileFilter)TrueFileFilter.TRUE, null));
            list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;
            for (File file : list) {
                if (i++ < 10) continue;
                LOGGER.info("Deleting old server resource pack {}", (Object)file.getName());
                FileUtils.deleteQuietly((File)file);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalArgumentException.getMessage());
        }
    }

    /*
     * WARNING - void declaration
     */
    public CompletableFuture<Void> loadServerPack(File file, ResourcePackSource arg) {
        void lv7;
        void lv6;
        try (ZipResourcePack lv = new ZipResourcePack(file);){
            PackResourceMetadata lv2 = lv.parseMetadata(PackResourceMetadata.READER);
            NativeImage lv3 = ClientResourcePackProfile.method_29713(lv);
        }
        catch (IOException iOException) {
            return Util.completeExceptionally(new IOException(String.format("Invalid resourcepack at %s", file), iOException));
        }
        LOGGER.info("Applying server pack {}", (Object)file);
        this.serverContainer = new ClientResourcePackProfile("server", true, () -> new ZipResourcePack(file), new TranslatableText("resourcePack.server.name"), lv6.getDescription(), ResourcePackCompatibility.from(lv6.getPackFormat()), ResourcePackProfile.InsertionPosition.TOP, true, arg, (NativeImage)lv7);
        return MinecraftClient.getInstance().reloadResourcesConcurrently();
    }

    @Nullable
    private <T extends ResourcePackProfile> T method_25454(ResourcePackProfile.Factory<T> arg) {
        File file2;
        T lv = null;
        File file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"));
        if (file != null && file.isFile()) {
            lv = ClientBuiltinResourcePackProvider.method_25453(arg, () -> ClientBuiltinResourcePackProvider.method_16048(file));
        }
        if (lv == null && SharedConstants.isDevelopment && (file2 = this.index.findFile("../resourcepacks/programmer_art")) != null && file2.isDirectory()) {
            lv = ClientBuiltinResourcePackProvider.method_25453(arg, () -> ClientBuiltinResourcePackProvider.method_25455(file2));
        }
        return lv;
    }

    @Nullable
    private static <T extends ResourcePackProfile> T method_25453(ResourcePackProfile.Factory<T> arg, Supplier<ResourcePack> supplier) {
        return ResourcePackProfile.of("programer_art", false, supplier, arg, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_BUILTIN);
    }

    private static DirectoryResourcePack method_25455(File file) {
        return new DirectoryResourcePack(file){

            @Override
            public String getName() {
                return "Programmer Art";
            }
        };
    }

    private static ResourcePack method_16048(File file) {
        return new ZipResourcePack(file){

            @Override
            public String getName() {
                return "Programmer Art";
            }
        };
    }
}

