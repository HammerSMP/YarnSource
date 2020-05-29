/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TranslationStorage
extends Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern field_25288 = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z])");
    private final Map<String, String> translations;
    private final boolean field_25289;

    private TranslationStorage(Map<String, String> map, boolean bl) {
        this.translations = map;
        this.field_25289 = bl;
    }

    public static TranslationStorage load(ResourceManager arg, List<LanguageDefinition> list) {
        HashMap map = Maps.newHashMap();
        boolean bl = false;
        for (LanguageDefinition lv : list) {
            bl |= lv.isRightToLeft();
            String string = String.format("lang/%s.json", lv.getCode());
            for (String string2 : arg.getAllNamespaces()) {
                try {
                    Identifier lv2 = new Identifier(string2, string);
                    TranslationStorage.load(arg.getAllResources(lv2), map);
                }
                catch (FileNotFoundException lv2) {
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", (Object)string2, (Object)string, (Object)exception.toString());
                }
            }
        }
        return new TranslationStorage((Map<String, String>)ImmutableMap.copyOf((Map)map), bl);
    }

    private static void load(List<Resource> list, Map<String, String> map) {
        for (Resource lv : list) {
            try {
                InputStream inputStream = lv.getInputStream();
                Throwable throwable = null;
                try {
                    Language.method_29425(inputStream, (arg_0, arg_1) -> map.put(arg_0, arg_1));
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (inputStream == null) continue;
                    if (throwable != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    inputStream.close();
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to load translations from {}", (Object)lv, (Object)iOException);
            }
        }
    }

    @Override
    public String get(String string) {
        return this.translations.getOrDefault(string, string);
    }

    @Override
    public boolean hasTranslation(String string) {
        return this.translations.containsKey(string);
    }

    @Override
    public boolean method_29428() {
        return this.field_25289;
    }

    @Override
    public String method_29426(String string, boolean bl) {
        if (!this.field_25289) {
            return string;
        }
        if (bl && string.indexOf(37) != -1) {
            string = TranslationStorage.method_29389(string);
        }
        return this.method_29390(string);
    }

    public static String method_29389(String string) {
        Matcher matcher = field_25288.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        int i = 1;
        while (matcher.find()) {
            String string2 = matcher.group(1);
            String string3 = string2 != null ? string2 : Integer.toString(i++);
            String string4 = matcher.group(2);
            String string5 = Matcher.quoteReplacement("\u2066%" + string3 + "$" + string4 + "\u2069");
            matcher.appendReplacement(stringBuffer, string5);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private String method_29390(String string) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(10);
        }
        catch (ArabicShapingException arabicShapingException) {
            return string;
        }
    }
}

