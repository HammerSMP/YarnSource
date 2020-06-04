/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5352;
import net.minecraft.class_5359;
import net.minecraft.class_5369;
import net.minecraft.class_5375;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class class_5368
extends class_5375 {
    private static final Identifier field_25446 = new Identifier("textures/misc/unknown_pack.png");

    public class_5368(Screen arg2, final class_5359 arg22, final BiConsumer<class_5359, ResourcePackManager<ResourcePackProfile>> biConsumer, final File file) {
        super(arg2, new TranslatableText("dataPack.title"), new Function<Runnable, class_5369<?>>(){
            private class_5359 field_25450;
            private final ResourcePackManager<ResourcePackProfile> field_25451;
            {
                this.field_25450 = arg22;
                this.field_25451 = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, class_5352.field_25347));
            }

            @Override
            public class_5369<?> apply(Runnable runnable) {
                this.field_25451.scanPacks();
                List<String> list3 = this.field_25450.method_29547();
                List list22 = class_5368.method_29630(this.field_25451, list3.stream());
                List list32 = class_5368.method_29630(this.field_25451, this.field_25451.method_29206().stream().filter(string -> !list3.contains(string)));
                return new class_5369<ResourcePackProfile>(runnable, (arg, arg2) -> arg2.bindTexture(field_25446), Lists.reverse((List)list22), list32, (list, list2, bl) -> {
                    List list3 = (List)Lists.reverse((List)list).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    List list4 = (List)list2.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    this.field_25450 = new class_5359(list3, list4);
                    if (!bl) {
                        this.field_25451.setEnabledProfiles(list3);
                        biConsumer.accept(this.field_25450, this.field_25451);
                    }
                });
            }

            @Override
            public /* synthetic */ Object apply(Object object) {
                return this.apply((Runnable)object);
            }
        }, (MinecraftClient arg) -> file);
    }

    private static List<ResourcePackProfile> method_29630(ResourcePackManager<ResourcePackProfile> arg, Stream<String> stream) {
        return (List)stream.map(arg::getProfile).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
    }
}

