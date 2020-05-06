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

    public ClientResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, ResourcePack arg, PackResourceMetadata arg2, ResourcePackProfile.InsertionPosition arg3) {
        super(string, bl, supplier, arg, arg2, arg3);
        NativeImage lv = null;
        try (InputStream inputStream = arg.openRoot("pack.png");){
            lv = NativeImage.read(inputStream);
        }
        catch (IOException | IllegalArgumentException exception) {
            // empty catch block
        }
        this.icon = lv;
    }

    public ClientResourcePackProfile(String string, boolean bl, Supplier<ResourcePack> supplier, Text arg, Text arg2, ResourcePackCompatibility arg3, ResourcePackProfile.InsertionPosition arg4, boolean bl2, @Nullable NativeImage arg5) {
        super(string, bl, supplier, arg, arg2, arg3, arg4, bl2);
        this.icon = arg5;
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

