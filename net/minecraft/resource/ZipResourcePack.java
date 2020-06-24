/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

public class ZipResourcePack
extends AbstractFileResourcePack {
    public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on((char)'/').omitEmptyStrings().limit(3);
    private ZipFile file;

    public ZipResourcePack(File file) {
        super(file);
    }

    private ZipFile getZipFile() throws IOException {
        if (this.file == null) {
            this.file = new ZipFile(this.base);
        }
        return this.file;
    }

    @Override
    protected InputStream openFile(String string) throws IOException {
        ZipFile zipFile = this.getZipFile();
        ZipEntry zipEntry = zipFile.getEntry(string);
        if (zipEntry == null) {
            throw new ResourceNotFoundException(this.base, string);
        }
        return zipFile.getInputStream(zipEntry);
    }

    @Override
    public boolean containsFile(String string) {
        try {
            return this.getZipFile().getEntry(string) != null;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public Set<String> getNamespaces(ResourceType arg) {
        void zipFile2;
        try {
            ZipFile zipFile = this.getZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile2.entries();
        HashSet set = Sets.newHashSet();
        while (enumeration.hasMoreElements()) {
            ArrayList list;
            ZipEntry zipEntry = enumeration.nextElement();
            String string = zipEntry.getName();
            if (!string.startsWith(arg.getDirectory() + "/") || (list = Lists.newArrayList((Iterable)TYPE_NAMESPACE_SPLITTER.split((CharSequence)string))).size() <= 1) continue;
            String string2 = (String)list.get(1);
            if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
                set.add(string2);
                continue;
            }
            this.warnNonLowerCaseNamespace(string2);
        }
        return set;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.file != null) {
            IOUtils.closeQuietly((Closeable)this.file);
            this.file = null;
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public Collection<Identifier> findResources(ResourceType arg, String string, String string2, int i, Predicate<String> predicate) {
        void zipFile2;
        try {
            ZipFile zipFile = this.getZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile2.entries();
        ArrayList list = Lists.newArrayList();
        String string3 = arg.getDirectory() + "/" + string + "/";
        String string4 = string3 + string2 + "/";
        while (enumeration.hasMoreElements()) {
            String string6;
            String[] strings;
            String string5;
            ZipEntry zipEntry = enumeration.nextElement();
            if (zipEntry.isDirectory() || (string5 = zipEntry.getName()).endsWith(".mcmeta") || !string5.startsWith(string4) || (strings = (string6 = string5.substring(string3.length())).split("/")).length < i + 1 || !predicate.test(strings[strings.length - 1])) continue;
            list.add(new Identifier(string, string6));
        }
        return list;
    }
}

