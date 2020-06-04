/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5352;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ClientResourcePackProfile
extends ResourcePackProfile {
    @Nullable
    private NativeImage icon;
    @Nullable
    private Identifier iconId;

    public ClientResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack arg, PackResourceMetadata arg2, ResourcePackProfile.InsertionPosition arg3, class_5352 arg4) {
        super(string, bl, supplier, arg, arg2, arg3, arg4);
        this.icon = ClientResourcePackProfile.method_29713(arg);
    }

    public ClientResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, Text arg, Text arg2, ResourcePackCompatibility arg3, ResourcePackProfile.InsertionPosition arg4, boolean bl2, class_5352 arg5, @Nullable NativeImage arg6) {
        super(string, bl, supplier, arg, arg2, arg3, arg4, bl2, arg5);
        this.icon = arg6;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static NativeImage method_29713(ResourcePack arg) {
        try (InputStream inputStream = arg.openRoot("pack.png");){
            NativeImage nativeImage = NativeImage.read(inputStream);
            return nativeImage;
        }
        catch (IOException | IllegalArgumentException exception) {
            return null;
        }
    }

    public void drawIcon(TextureManager arg) {
        if (this.iconId == null) {
            this.iconId = this.icon == null ? new Identifier("textures/misc/unknown_pack.png") : arg.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
        }
        arg.bindTexture(this.iconId);
    }

    @Override
    public void close() {
        super.close();
        if (this.icon != null) {
            this.icon.close();
            this.icon = null;
        }
    }
}

