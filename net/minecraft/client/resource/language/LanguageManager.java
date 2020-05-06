/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
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
    protected static final TranslationStorage STORAGE = new TranslationStorage();
    private String currentLanguageCode;
    private final Map<String, LanguageDefinition> languageDefs = Maps.newHashMap();

    public LanguageManager(String string) {
        this.currentLanguageCode = string;
        I18n.setLanguage(STORAGE);
    }

    public void reloadResources(List<ResourcePack> list) {
        this.languageDefs.clear();
        for (ResourcePack lv : list) {
            try {
                LanguageResourceMetadata lv2 = lv.parseMetadata(LanguageResourceMetadata.READER);
                if (lv2 == null) continue;
                for (LanguageDefinition lv3 : lv2.getLanguageDefinitions()) {
                    if (this.languageDefs.containsKey(lv3.getCode())) continue;
                    this.languageDefs.put(lv3.getCode(), lv3);
                }
            }
            catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)lv.getName(), (Object)exception);
            }
        }
    }

    @Override
    public void apply(ResourceManager arg) {
        ArrayList list = Lists.newArrayList((Object[])new String[]{"en_us"});
        if (!"en_us".equals(this.currentLanguageCode)) {
            list.add(this.currentLanguageCode);
        }
        STORAGE.load(arg, list);
        Language.load(LanguageManager.STORAGE.translations);
    }

    public boolean isRightToLeft() {
        return this.getLanguage() != null && this.getLanguage().isRightToLeft();
    }

    public void setLanguage(LanguageDefinition arg) {
        this.currentLanguageCode = arg.getCode();
    }

    public LanguageDefinition getLanguage() {
        String string = this.languageDefs.containsKey(this.currentLanguageCode) ? this.currentLanguageCode : "en_us";
        return this.languageDefs.get(string);
    }

    public SortedSet<LanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }

    public LanguageDefinition getLanguage(String string) {
        return this.languageDefs.get(string);
    }
}

