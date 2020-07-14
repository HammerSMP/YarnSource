/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelOverrideList {
    public static final ModelOverrideList EMPTY = new ModelOverrideList();
    private final List<ModelOverride> overrides = Lists.newArrayList();
    private final List<BakedModel> models;

    private ModelOverrideList() {
        this.models = Collections.emptyList();
    }

    public ModelOverrideList(ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelOverride> overrides) {
        this.models = overrides.stream().map(arg3 -> {
            UnbakedModel lv = (UnbakedModel)unbakedModelGetter.apply(arg3.getModelId());
            if (Objects.equals(lv, unbakedModel)) {
                return null;
            }
            return modelLoader.bake(arg3.getModelId(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.models);
        for (int i = overrides.size() - 1; i >= 0; --i) {
            this.overrides.add(overrides.get(i));
        }
    }

    @Nullable
    public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ModelOverride lv = this.overrides.get(i);
                if (!lv.matches(stack, world, entity)) continue;
                BakedModel lv2 = this.models.get(i);
                if (lv2 == null) {
                    return model;
                }
                return lv2;
            }
        }
        return model;
    }
}

