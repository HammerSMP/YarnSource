/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderer
extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>> {
    private static final Map<MooshroomEntity.Type, Identifier> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MooshroomEntity.Type.BROWN, new Identifier("textures/entity/cow/brown_mooshroom.png"));
        hashMap.put(MooshroomEntity.Type.RED, new Identifier("textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(EntityRenderDispatcher arg) {
        super(arg, new CowEntityModel(), 0.7f);
        this.addFeature(new MooshroomMushroomFeatureRenderer<MooshroomEntity>(this));
    }

    @Override
    public Identifier getTexture(MooshroomEntity arg) {
        return TEXTURES.get((Object)arg.getMooshroomType());
    }
}

