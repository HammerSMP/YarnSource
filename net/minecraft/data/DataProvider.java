/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashFunction
 *  com.google.common.hash.Hashing
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 */
package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import net.minecraft.data.DataCache;

public interface DataProvider {
    public static final HashFunction SHA1 = Hashing.sha1();

    public void run(DataCache var1) throws IOException;

    public String getName();

    public static void writeToPath(Gson gson, DataCache arg, JsonElement jsonElement, Path path) throws IOException {
        String string = gson.toJson(jsonElement);
        String string2 = SHA1.hashUnencodedChars((CharSequence)string).toString();
        if (!Objects.equals(arg.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                bufferedWriter.write(string);
            }
        }
        arg.updateSha1(path, string2);
    }
}

