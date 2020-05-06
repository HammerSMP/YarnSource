/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceIndex {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, File> index = Maps.newHashMap();
    private final Map<Identifier, File> field_21556 = Maps.newHashMap();

    protected ResourceIndex() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResourceIndex(File file, String string) {
        File file2 = new File(file, "objects");
        File file3 = new File(file, "indexes/" + string + ".json");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newReader((File)file3, (Charset)StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
            if (jsonObject2 != null) {
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    JsonObject jsonObject3 = (JsonObject)entry.getValue();
                    String string2 = (String)entry.getKey();
                    String[] strings = string2.split("/", 2);
                    String string3 = JsonHelper.getString(jsonObject3, "hash");
                    File file4 = new File(file2, string3.substring(0, 2) + "/" + string3);
                    if (strings.length == 1) {
                        this.index.put(strings[0], file4);
                        continue;
                    }
                    this.field_21556.put(new Identifier(strings[0], strings[1]), file4);
                }
            }
        }
        catch (JsonParseException jsonParseException) {
            LOGGER.error("Unable to parse resource index file: {}", (Object)file3);
        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error("Can't find the resource index file: {}", (Object)file3);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader);
        }
    }

    @Nullable
    public File getResource(Identifier arg) {
        return this.field_21556.get(arg);
    }

    @Nullable
    public File findFile(String string) {
        return this.index.get(string);
    }

    public Collection<Identifier> getFilesRecursively(String string, String string2, int i, Predicate<String> predicate) {
        return this.field_21556.keySet().stream().filter(arg -> {
            String string3 = arg.getPath();
            return arg.getNamespace().equals(string2) && !string3.endsWith(".mcmeta") && string3.startsWith(string + "/") && predicate.test(string3);
        }).collect(Collectors.toList());
    }
}

