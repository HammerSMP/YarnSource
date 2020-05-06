/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public abstract class SpriteAtlasHolder
extends SinglePreparationResourceReloadListener<SpriteAtlasTexture.Data>
implements AutoCloseable {
    private final SpriteAtlasTexture atlas;
    private final String pathPrefix;

    public SpriteAtlasHolder(TextureManager arg, Identifier arg2, String string) {
        this.pathPrefix = string;
        this.atlas = new SpriteAtlasTexture(arg2);
        arg.registerTexture(this.atlas.getId(), this.atlas);
    }

    protected abstract Stream<Identifier> getSprites();

    protected Sprite getSprite(Identifier arg) {
        return this.atlas.getSprite(this.toSpriteId(arg));
    }

    private Identifier toSpriteId(Identifier arg) {
        return new Identifier(arg.getNamespace(), this.pathPrefix + "/" + arg.getPath());
    }

    @Override
    protected SpriteAtlasTexture.Data prepare(ResourceManager arg, Profiler arg2) {
        arg2.startTick();
        arg2.push("stitching");
        SpriteAtlasTexture.Data lv = this.atlas.stitch(arg, this.getSprites().map(this::toSpriteId), arg2, 0);
        arg2.pop();
        arg2.endTick();
        return lv;
    }

    @Override
    protected void apply(SpriteAtlasTexture.Data arg, ResourceManager arg2, Profiler arg3) {
        arg3.startTick();
        arg3.push("upload");
        this.atlas.upload(arg);
        arg3.pop();
        arg3.endTick();
    }

    @Override
    public void close() {
        this.atlas.clear();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager arg, Profiler arg2) {
        return this.prepare(arg, arg2);
    }
}

