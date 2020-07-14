/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
extends SinglePreparationResourceReloadListener<ModelLoader>
implements AutoCloseable {
    private Map<Identifier, BakedModel> models;
    @Nullable
    private SpriteAtlasManager atlasManager;
    private final BlockModels blockModelCache;
    private final TextureManager textureManager;
    private final BlockColors colorMap;
    private int mipmap;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> stateLookup;

    public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmap) {
        this.textureManager = textureManager;
        this.colorMap = colorMap;
        this.mipmap = mipmap;
        this.blockModelCache = new BlockModels(this);
    }

    public BakedModel getModel(ModelIdentifier id) {
        return this.models.getOrDefault(id, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    protected ModelLoader prepare(ResourceManager arg, Profiler arg2) {
        arg2.startTick();
        ModelLoader lv = new ModelLoader(arg, this.colorMap, arg2, this.mipmap);
        arg2.endTick();
        return lv;
    }

    @Override
    protected void apply(ModelLoader arg, ResourceManager arg2, Profiler arg3) {
        arg3.startTick();
        arg3.push("upload");
        if (this.atlasManager != null) {
            this.atlasManager.close();
        }
        this.atlasManager = arg.upload(this.textureManager, arg3);
        this.models = arg.getBakedModelMap();
        this.stateLookup = arg.getStateLookup();
        this.missingModel = this.models.get(ModelLoader.MISSING);
        arg3.swap("cache");
        this.blockModelCache.reload();
        arg3.pop();
        arg3.endTick();
    }

    public boolean shouldRerender(BlockState from, BlockState to) {
        int j;
        if (from == to) {
            return false;
        }
        int i = this.stateLookup.getInt((Object)from);
        if (i != -1 && i == (j = this.stateLookup.getInt((Object)to))) {
            FluidState lv2;
            FluidState lv = from.getFluidState();
            return lv != (lv2 = to.getFluidState());
        }
        return true;
    }

    public SpriteAtlasTexture method_24153(Identifier arg) {
        return this.atlasManager.getAtlas(arg);
    }

    @Override
    public void close() {
        if (this.atlasManager != null) {
            this.atlasManager.close();
        }
    }

    public void resetMipmapLevels(int i) {
        this.mipmap = i;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

