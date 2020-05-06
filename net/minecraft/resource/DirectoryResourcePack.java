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

    public static boolean isValidPath(File file, String string) throws IOException {
        String string2 = file.getCanonicalPath();
        if (IS_WINDOWS) {
            string2 = BACKSLASH_MATCHER.replaceFrom((CharSequence)string2, '/');
        }
        return string2.endsWith(string);
    }

    @Override
    protected InputStream openFile(String string) throws IOException {
        File file = this.getFile(string);
        if (file == null) {
            throw new ResourceNotFoundException(this.base, string);
        }
        return new FileInputStream(file);
    }

    @Override
    protected boolean containsFile(String string) {
        return this.getFile(string) != null;
    }

    @Nullable
    private File getFile(String string) {
        try {
            File file = new File(this.base, string);
            if (file.isFile() && DirectoryResourcePack.isValidPath(file, string)) {
                return file;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Set<String> getNamespaces(ResourceType arg) {
        HashSet set = Sets.newHashSet();
        File file = new File(this.base, arg.getDirectory());
        File[] files = file.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (files != null) {
            for (File file2 : files) {
                String string = DirectoryResourcePack.relativize(file, file2);
                if (string.equals(string.toLowerCase(Locale.ROOT))) {
                    set.add(string.substring(0, string.length() - 1));
                    continue;
                }
                this.warnNonLowercaseNamespace(string);
            }
        }
        return set;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Collection<Identifier> findResources(ResourceType arg, String string, String string2, int i, Predicate<String> predicate) {
        File file = new File(this.base, arg.getDirectory());
        ArrayList list = Lists.newArrayList();
        this.findFiles(new File(new File(file, string), string2), i, string, list, string2 + "/", predicate);
        return list;
    }

    private void findFiles(File file, int i, String string, List<Identifier> list, String string2, Predicate<String> predicate) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    if (i <= 0) continue;
                    this.findFiles(file2, i - 1, string, list, string2 + file2.getName() + "/", predicate);
                    continue;
                }
                if (file2.getName().endsWith(".mcmeta") || !predicate.test(file2.getName())) continue;
                try {
                    list.add(new Identifier(string, string2 + file2.getName()));
                }
                catch (InvalidIdentifierException lv) {
                    LOGGER.error(lv.getMessage());
                }
            }
        }
    }
}

