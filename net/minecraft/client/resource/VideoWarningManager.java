/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlDebugInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VideoWarningManager
extends SinglePreparationResourceReloadListener<WarningPatternLoader> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier GPU_WARNLIST_ID = new Identifier("gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean warningScheduled;
    private boolean warned;
    private boolean cancelledAfterWarning;

    public boolean hasWarning() {
        return !this.warnings.isEmpty();
    }

    public boolean canWarn() {
        return this.hasWarning() && !this.warned;
    }

    public void scheduleWarning() {
        this.warningScheduled = true;
    }

    public void acceptAfterWarnings() {
        this.warned = true;
    }

    public void cancelAfterWarnings() {
        this.warned = true;
        this.cancelledAfterWarning = true;
    }

    public boolean shouldWarn() {
        return this.warningScheduled && !this.warned;
    }

    public boolean hasCancelledAfterWarning() {
        return this.cancelledAfterWarning;
    }

    public void reset() {
        this.warningScheduled = false;
        this.warned = false;
        this.cancelledAfterWarning = false;
    }

    @Nullable
    public String getRendererWarning() {
        return (String)this.warnings.get((Object)"renderer");
    }

    @Nullable
    public String getVersionWarning() {
        return (String)this.warnings.get((Object)"version");
    }

    @Nullable
    public String getVendorWarning() {
        return (String)this.warnings.get((Object)"vendor");
    }

    @Override
    protected WarningPatternLoader prepare(ResourceManager arg, Profiler arg2) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        arg2.startTick();
        JsonObject jsonObject = VideoWarningManager.loadWarnlist(arg, arg2);
        if (jsonObject != null) {
            arg2.push("compile_regex");
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("renderer"), list);
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("version"), list2);
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("vendor"), list3);
            arg2.pop();
        }
        arg2.endTick();
        return new WarningPatternLoader(list, list2, list3);
    }

    @Override
    protected void apply(WarningPatternLoader arg, ResourceManager arg2, Profiler arg3) {
        this.warnings = arg.buildWarnings();
    }

    private static void compilePatterns(JsonArray jsonArray, List<Pattern> list) {
        jsonArray.forEach(jsonElement -> list.add(Pattern.compile(jsonElement.getAsString(), 2)));
    }

    @Nullable
    private static JsonObject loadWarnlist(ResourceManager arg, Profiler arg2) {
        arg2.push("parse_json");
        JsonObject jsonObject = null;
        try (Resource lv = arg.getResource(GPU_WARNLIST_ID);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lv.getInputStream(), StandardCharsets.UTF_8));){
            jsonObject = new JsonParser().parse((Reader)bufferedReader).getAsJsonObject();
        }
        catch (JsonSyntaxException | IOException exception) {
            LOGGER.warn("Failed to load GPU warnlist");
        }
        arg2.pop();
        return jsonObject;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
        return this.prepare(arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WarningPatternLoader {
        private final List<Pattern> rendererPatterns;
        private final List<Pattern> versionPatterns;
        private final List<Pattern> vendorPatterns;

        private WarningPatternLoader(List<Pattern> list, List<Pattern> list2, List<Pattern> list3) {
            this.rendererPatterns = list;
            this.versionPatterns = list2;
            this.vendorPatterns = list3;
        }

        private static String buildWarning(List<Pattern> list, String string) {
            ArrayList list2 = Lists.newArrayList();
            for (Pattern pattern : list) {
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    list2.add(matcher.group());
                }
            }
            return String.join((CharSequence)", ", list2);
        }

        private ImmutableMap<String, String> buildWarnings() {
            String string3;
            String string2;
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            String string = WarningPatternLoader.buildWarning(this.rendererPatterns, GlDebugInfo.getRenderer());
            if (!string.isEmpty()) {
                builder.put((Object)"renderer", (Object)string);
            }
            if (!(string2 = WarningPatternLoader.buildWarning(this.versionPatterns, GlDebugInfo.getVersion())).isEmpty()) {
                builder.put((Object)"version", (Object)string2);
            }
            if (!(string3 = WarningPatternLoader.buildWarning(this.vendorPatterns, GlDebugInfo.getVendor())).isEmpty()) {
                builder.put((Object)"vendor", (Object)string3);
            }
            return builder.build();
        }
    }
}

