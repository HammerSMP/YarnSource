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
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

@Environment(value=EnvType.CLIENT)
public class class_5407
extends SinglePreparationResourceReloadListener<class_5408> {
    private static final Identifier field_25689 = new Identifier("gpu_warnlist.json");
    private ImmutableMap<String, String> field_25690 = ImmutableMap.of();

    public boolean method_30055() {
        return !this.field_25690.isEmpty();
    }

    @Nullable
    public String method_30060() {
        return (String)this.field_25690.get((Object)"renderer");
    }

    @Nullable
    public String method_30062() {
        return (String)this.field_25690.get((Object)"version");
    }

    @Nullable
    public String method_30063() {
        return (String)this.field_25690.get((Object)"vendor");
    }

    @Override
    protected class_5408 prepare(ResourceManager arg, Profiler arg2) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        arg2.startTick();
        JsonObject jsonObject = class_5407.method_30061(arg, arg2);
        if (jsonObject != null) {
            arg2.push("compile_regex");
            class_5407.method_30057(jsonObject.getAsJsonArray("renderer"), list);
            class_5407.method_30057(jsonObject.getAsJsonArray("version"), list2);
            class_5407.method_30057(jsonObject.getAsJsonArray("vendor"), list3);
            arg2.pop();
        }
        arg2.endTick();
        return new class_5408(list, list2, list3);
    }

    @Override
    protected void apply(class_5408 arg, ResourceManager arg2, Profiler arg3) {
        this.field_25690 = arg.method_30064();
    }

    private static void method_30057(JsonArray jsonArray, List<Pattern> list) {
        jsonArray.forEach(jsonElement -> list.add(Pattern.compile(jsonElement.getAsString(), 2)));
    }

    @Nullable
    private static JsonObject method_30061(ResourceManager arg, Profiler arg2) {
        arg2.push("parse_json");
        JsonObject jsonObject = null;
        try (Resource lv = arg.getResource(field_25689);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(lv.getInputStream(), StandardCharsets.UTF_8));){
            jsonObject = new JsonParser().parse((Reader)bufferedReader).getAsJsonObject();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        arg2.pop();
        return jsonObject;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
        return this.prepare(arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_5408 {
        private final List<Pattern> field_25691;
        private final List<Pattern> field_25692;
        private final List<Pattern> field_25693;

        private class_5408(List<Pattern> list, List<Pattern> list2, List<Pattern> list3) {
            this.field_25691 = list;
            this.field_25692 = list2;
            this.field_25693 = list3;
        }

        private static String method_30066(List<Pattern> list, String string) {
            ArrayList list2 = Lists.newArrayList();
            for (Pattern pattern : list) {
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    list2.add(matcher.group());
                }
            }
            return String.join((CharSequence)", ", list2);
        }

        private ImmutableMap<String, String> method_30064() {
            String string3;
            String string2;
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            String string = class_5408.method_30066(this.field_25691, GlDebugInfo.getRenderer());
            if (!string.isEmpty()) {
                builder.put((Object)"renderer", (Object)string);
            }
            if (!(string2 = class_5408.method_30066(this.field_25692, GlDebugInfo.getVersion())).isEmpty()) {
                builder.put((Object)"version", (Object)string2);
            }
            if (!(string3 = class_5408.method_30066(this.field_25693, GlDebugInfo.getVendor())).isEmpty()) {
                builder.put((Object)"vendor", (Object)string3);
            }
            return builder.build();
        }
    }
}

