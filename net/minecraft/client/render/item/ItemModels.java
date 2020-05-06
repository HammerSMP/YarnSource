/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class ItemModels {
    public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap(256);
    private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap(256);
    private final BakedModelManager modelManager;

    public ItemModels(BakedModelManager arg) {
        this.modelManager = arg;
    }

    public Sprite getSprite(ItemConvertible arg) {
        return this.getSprite(new ItemStack(arg));
    }

    public Sprite getSprite(ItemStack arg) {
        BakedModel lv = this.getModel(arg);
        if (lv == this.modelManager.getMissingModel() && arg.getItem() instanceof BlockItem) {
            return this.modelManager.getBlockModels().getSprite(((BlockItem)arg.getItem()).getBlock().getDefaultState());
        }
        return lv.getSprite();
    }

    public BakedModel getModel(ItemStack arg) {
        BakedModel lv = this.getModel(arg.getItem());
        return lv == null ? this.modelManager.getMissingModel() : lv;
    }

    @Nullable
    public BakedModel getModel(Item arg) {
        return (BakedModel)this.models.get(ItemModels.getModelId(arg));
    }

    private static int getModelId(Item arg) {
        return Item.getRawId(arg);
    }

    public void putModel(Item arg, ModelIdentifier arg2) {
        this.modelIds.put(ItemModels.getModelId(arg), (Object)arg2);
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void reloadModels() {
        this.models.clear();
        for (Map.Entry entry : this.modelIds.entrySet()) {
            this.models.put((Integer)entry.getKey(), (Object)this.modelManager.getModel((ModelIdentifier)entry.getValue()));
        }
    }
}

