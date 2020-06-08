/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DataPackScreen
extends AbstractPackScreen {
    private static final Identifier UNKNOWN_PACK_TEXTURE = new Identifier("textures/misc/unknown_pack.png");

    public DataPackScreen(Screen arg2, final DataPackSettings arg22, final BiConsumer<DataPackSettings, ResourcePackManager<ResourcePackProfile>> biConsumer, final File file) {
        super(arg2, new TranslatableText("dataPack.title"), new Function<Runnable, ResourcePackOrganizer<?>>(){
            private DataPackSettings settings;
            private final ResourcePackManager<ResourcePackProfile> packManager;
            {
                this.settings = arg22;
                this.packManager = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, ResourcePackSource.field_25347));
            }

            @Override
            public ResourcePackOrganizer<?> apply(Runnable runnable) {
                this.packManager.scanPacks();
                List<String> list3 = this.settings.getEnabled();
                List list22 = DataPackScreen.method_29630(this.packManager, list3.stream());
                List list32 = DataPackScreen.method_29630(this.packManager, this.packManager.getNames().stream().filter(string -> !list3.contains(string)));
                return new ResourcePackOrganizer<ResourcePackProfile>(runnable, (arg, arg2) -> arg2.bindTexture(UNKNOWN_PACK_TEXTURE), Lists.reverse((List)list22), list32, (list, list2, bl) -> {
                    List list3 = (List)Lists.reverse((List)list).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    List list4 = (List)list2.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    this.settings = new DataPackSettings(list3, list4);
                    if (!bl) {
                        this.packManager.setEnabledProfiles(list3);
                        biConsumer.accept(this.settings, this.packManager);
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

