/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class TextureStitcherCannotFitException
extends RuntimeException {
    private final Collection<Sprite.Info> sprites;

    public TextureStitcherCannotFitException(Sprite.Info arg, Collection<Sprite.Info> collection) {
        super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", arg.getId(), arg.getWidth(), arg.getHeight()));
        this.sprites = collection;
    }

    public Collection<Sprite.Info> getSprites() {
        return this.sprites;
    }
}

