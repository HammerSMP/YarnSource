/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourcePack
implements ResourcePack {
    public static Path resourcePath;
    private static final Logger LOGGER;
    public static Class<?> resourceClass;
    private static final Map<ResourceType, FileSystem> typeToFileSystem;
    public final Set<String> namespaces;

    public DefaultResourcePack(String ... strings) {
        this.namespaces = ImmutableSet.copyOf((Object[])strings);
    }

    @Override
    public InputStream openRoot(String string) throws IOException {
        Path path;
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        if (resourcePath != null && Files.exists(path = resourcePath.resolve(string), new LinkOption[0])) {
            return Files.newInputStream(path, new OpenOption[0]);
        }
        return this.getInputStream(string);
    }

    @Override
    public InputStream open(ResourceType arg, Identifier arg2) throws IOException {
        InputStream inputStream = this.findInputStream(arg, arg2);
        if (inputStream != null) {
            return inputStream;
        }
        throw new FileNotFoundException(arg2.getPath());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType arg, String string, String string2, int i, Predicate<String> predicate) {
        HashSet set = Sets.newHashSet();
        if (resourcePath != null) {
            try {
                DefaultResourcePack.getIdentifiers(set, i, string, resourcePath.resolve(arg.getDirectory()), string2, predicate);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (arg == ResourceType.CLIENT_RESOURCES) {
                Enumeration<URL> enumeration = null;
                try {
                    enumeration = resourceClass.getClassLoader().getResources(arg.getDirectory() + "/");
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                while (enumeration != null && enumeration.hasMoreElements()) {
                    try {
                        URI uRI = ((URL)enumeration.nextElement()).toURI();
                        if (!"file".equals(uRI.getScheme())) continue;
                        DefaultResourcePack.getIdentifiers(set, i, string, Paths.get(uRI), string2, predicate);
                    }
                    catch (IOException | URISyntaxException uRI) {}
                }
            }
        }
        try {
            URL uRL = DefaultResourcePack.class.getResource("/" + arg.getDirectory() + "/.mcassetsroot");
            if (uRL == null) {
                LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set;
            }
            URI uRI2 = uRL.toURI();
            if ("file".equals(uRI2.getScheme())) {
                URL uRL2 = new URL(uRL.toString().substring(0, uRL.toString().length() - ".mcassetsroot".length()));
                Path path = Paths.get(uRL2.toURI());
                DefaultResourcePack.getIdentifiers(set, i, string, path, string2, predicate);
            } else if ("jar".equals(uRI2.getScheme())) {
                Path path2 = typeToFileSystem.get((Object)arg).getPath("/" + arg.getDirectory(), new String[0]);
                DefaultResourcePack.getIdentifiers(set, i, "minecraft", path2, string2, predicate);
            } else {
                LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uRI2);
            }
        }
        catch (FileNotFoundException | NoSuchFileException uRL) {
        }
        catch (IOException | URISyntaxException exception) {
            LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
        }
        return set;
    }

    private static void getIdentifiers(Collection<Identifier> collection, int i, String string, Path path3, String string2, Predicate<String> predicate) throws IOException {
        Path path22 = path3.resolve(string);
        try (Stream<Path> stream = Files.walk(path22.resolve(string2), i, new FileVisitOption[0]);){
            stream.filter(path -> !path.endsWith(".mcmeta") && Files.isRegularFile(path, new LinkOption[0]) && predicate.test(path.getFileName().toString())).map(path2 -> new Identifier(string, path22.relativize((Path)path2).toString().replaceAll("\\\\", "/"))).forEach(collection::add);
        }
    }

    @Nullable
    protected InputStream findInputStream(ResourceType arg, Identifier arg2) {
        Path path;
        String string = DefaultResourcePack.getPath(arg, arg2);
        if (resourcePath != null && Files.exists(path = resourcePath.resolve(arg.getDirectory() + "/" + arg2.getNamespace() + "/" + arg2.getPath()), new LinkOption[0])) {
            try {
                return Files.newInputStream(path, new OpenOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            URL uRL = DefaultResourcePack.class.getResource(string);
            if (DefaultResourcePack.isValidUrl(string, uRL)) {
                return uRL.openStream();
            }
        }
        catch (IOException iOException) {
            return DefaultResourcePack.class.getResourceAsStream(string);
        }
        return null;
    }

    private static String getPath(ResourceType arg, Identifier arg2) {
        return "/" + arg.getDirectory() + "/" + arg2.getNamespace() + "/" + arg2.getPath();
    }

    private static boolean isValidUrl(String string, @Nullable URL uRL) throws IOException {
        return uRL != null && (uRL.getProtocol().equals("jar") || DirectoryResourcePack.isValidPath(new File(uRL.getFile()), string));
    }

    @Nullable
    protected InputStream getInputStream(String string) {
        return DefaultResourcePack.class.getResourceAsStream("/" + string);
    }

    @Override
    public boolean contains(ResourceType arg, Identifier arg2) {
        Path path;
        String string = DefaultResourcePack.getPath(arg, arg2);
        if (resourcePath != null && Files.exists(path = resourcePath.resolve(arg.getDirectory() + "/" + arg2.getNamespace() + "/" + arg2.getPath()), new LinkOption[0])) {
            return true;
        }
        try {
            URL uRL = DefaultResourcePack.class.getResource(string);
            return DefaultResourcePack.isValidUrl(string, uRL);
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType arg) {
        return this.namespaces;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> arg) throws IOException {
        try (InputStream inputStream = this.openRoot("pack.mcmeta");){
            T t = AbstractFileResourcePack.parseMetadata(arg, inputStream);
            return t;
        }
        catch (FileNotFoundException | RuntimeException exception) {
            return null;
        }
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void close() {
    }

    static {
        LOGGER = LogManager.getLogger();
        typeToFileSystem = Util.make(Maps.newHashMap(), hashMap -> {
            Class<DefaultResourcePack> class_ = DefaultResourcePack.class;
            synchronized (DefaultResourcePack.class) {
                for (ResourceType lv : ResourceType.values()) {
                    URL uRL = DefaultResourcePack.class.getResource("/" + lv.getDirectory() + "/.mcassetsroot");
                    try {
                        FileSystem fileSystem2;
                        URI uRI = uRL.toURI();
                        if (!"jar".equals(uRI.getScheme())) continue;
                        try {
                            FileSystem fileSystem = FileSystems.getFileSystem(uRI);
                        }
                        catch (FileSystemNotFoundException fileSystemNotFoundException) {
                            fileSystem2 = FileSystems.newFileSystem(uRI, Collections.emptyMap());
                        }
                        hashMap.put(lv, fileSystem2);
                    }
                    catch (IOException | URISyntaxException exception) {
                        LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
                    }
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        });
    }
}

