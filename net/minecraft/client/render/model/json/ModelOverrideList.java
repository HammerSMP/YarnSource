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

    public ModelOverrideList(ModelLoader arg, JsonUnbakedModel arg2, Function<Identifier, UnbakedModel> function, List<ModelOverride> list) {
        this.models = list.stream().map(arg3 -> {
            UnbakedModel lv = (UnbakedModel)function.apply(arg3.getModelId());
            if (Objects.equals(lv, arg2)) {
                return null;
            }
            return arg.bake(arg3.getModelId(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.models);
        for (int i = list.size() - 1; i >= 0; --i) {
            this.overrides.add(list.get(i));
        }
    }

    @Nullable
    public BakedModel apply(BakedModel arg, ItemStack arg2, @Nullable ClientWorld arg3, @Nullable LivingEntity arg4) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ModelOverride lv = this.overrides.get(i);
                if (!lv.matches(arg2, arg3, arg4)) continue;
                BakedModel lv2 = this.models.get(i);
                if (lv2 == null) {
                    return arg;
                }
                return lv2;
            }
        }
        return arg;
    }
}

