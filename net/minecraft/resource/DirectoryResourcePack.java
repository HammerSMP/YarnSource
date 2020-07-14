/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.commons.io.filefilter.DirectoryFileFilter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryResourcePack
extends AbstractFileResourcePack {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean IS_WINDOWS = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is((char)'\\');

    public DirectoryResourcePack(File file) {
        super(file);
    }

    public static boolean isValidPath(File file, String filename) throws IOException {
        String string2 = file.getCanonicalPath();
        if (IS_WINDOWS) {
            string2 = BACKSLASH_MATCHER.replaceFrom((CharSequence)string2, '/');
        }
        return string2.endsWith(filename);
    }

    @Override
    protected InputStream openFile(String name) throws IOException {
        File file = this.getFile(name);
        if (file == null) {
            throw new ResourceNotFoundException(this.base, name);
        }
        return new FileInputStream(file);
    }

    @Override
    protected boolean containsFile(String name) {
        return this.getFile(name) != null;
    }

    @Nullable
    private File getFile(String name) {
        try {
            File file = new File(this.base, name);
            if (file.isFile() && DirectoryResourcePack.isValidPath(file, name)) {
                return file;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        HashSet set = Sets.newHashSet();
        File file = new File(this.base, type.getDirectory());
        File[] files = file.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (files != null) {
            for (File file2 : files) {
                String string = DirectoryResourcePack.relativize(file, file2);
                if (string.equals(string.toLowerCase(Locale.ROOT))) {
                    set.add(string.substring(0, string.length() - 1));
                    continue;
                }
                this.warnNonLowerCaseNamespace(string);
            }
        }
        return set;
    }

    @Override
    public void close() {
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        File file = new File(this.base, type.getDirectory());
        ArrayList list = Lists.newArrayList();
        this.findFiles(new File(new File(file, namespace), prefix), maxDepth, namespace, list, prefix + "/", pathFilter);
        return list;
    }

    private void findFiles(File file, int maxDepth, String namespace, List<Identifier> found, String prefix, Predicate<String> pathFilter) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    if (maxDepth <= 0) continue;
                    this.findFiles(file2, maxDepth - 1, namespace, found, prefix + file2.getName() + "/", pathFilter);
                    continue;
                }
                if (file2.getName().endsWith(".mcmeta") || !pathFilter.test(file2.getName())) continue;
                try {
                    found.add(new Identifier(namespace, prefix + file2.getName()));
                }
                catch (InvalidIdentifierException lv) {
                    LOGGER.error(lv.getMessage());
                }
            }
        }
    }
}

