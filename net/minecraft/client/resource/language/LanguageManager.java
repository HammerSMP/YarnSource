/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanguageManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LanguageDefinition field_25291 = new LanguageDefinition("en_us", "US", "English", false);
    private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of((Object)"en_us", (Object)field_25291);
    private String currentLanguageCode;
    private LanguageDefinition field_25292 = field_25291;

    public LanguageManager(String string) {
        this.currentLanguageCode = string;
    }

    private static Map<String, LanguageDefinition> method_29393(Stream<ResourcePack> stream) {
        HashMap map = Maps.newHashMap();
        stream.forEach(arg -> {
            try {
                LanguageResourceMetadata lv = arg.parseMetadata(LanguageResourceMetadata.READER);
                if (lv != null) {
                    for (LanguageDefinition lv2 : lv.getLanguageDefinitions()) {
                        map.putIfAbsent(lv2.getCode(), lv2);
                    }
                }
            }
            catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)arg.getName(), (Object)exception);
            }
        });
        return ImmutableMap.copyOf((Map)map);
    }

    @Override
    public void apply(ResourceManager arg) {
        this.languageDefs = LanguageManager.method_29393(arg.method_29213());
        LanguageDefinition lv = this.languageDefs.getOrDefault("en_us", field_25291);
        this.field_25292 = this.languageDefs.getOrDefault(this.currentLanguageCode, lv);
        ArrayList list = Lists.newArrayList((Object[])new LanguageDefinition[]{lv});
        if (this.field_25292 != lv) {
            list.add(this.field_25292);
        }
        TranslationStorage lv2 = TranslationStorage.load(arg, list);
        I18n.method_29391(lv2);
        Language.method_29427(lv2);
    }

    public void setLanguage(LanguageDefinition arg) {
        this.currentLanguageCode = arg.getCode();
        this.field_25292 = arg;
    }

    public LanguageDefinition getLanguage() {
        return this.field_25292;
    }

    public SortedSet<LanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }

    public LanguageDefinition getLanguage(String string) {
        return this.languageDefs.get(string);
    }
}

