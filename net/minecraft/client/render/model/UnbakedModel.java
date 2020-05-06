/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel {
    public Collection<Identifier> getModelDependencies();

    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<Pair<String, String>> var2);

    @Nullable
    public BakedModel bake(ModelLoader var1, Function<SpriteIdentifier, Sprite> var2, ModelBakeSettings var3, Identifier var4);
}

