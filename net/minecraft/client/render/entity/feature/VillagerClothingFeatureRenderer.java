/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(value=EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M>
implements SynchronousResourceReloadListener {
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(1, (Object)new Identifier("stone"));
        int2ObjectOpenHashMap.put(2, (Object)new Identifier("iron"));
        int2ObjectOpenHashMap.put(3, (Object)new Identifier("gold"));
        int2ObjectOpenHashMap.put(4, (Object)new Identifier("emerald"));
        int2ObjectOpenHashMap.put(5, (Object)new Identifier("diamond"));
    });
    private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
    private final ReloadableResourceManager resourceManager;
    private final String entityType;

    public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> arg, ReloadableResourceManager arg2, String string) {
        super(arg);
        this.resourceManager = arg2;
        this.entityType = string;
        arg2.registerListener(this);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        if (((Entity)arg3).isInvisible()) {
            return;
        }
        VillagerData lv = ((VillagerDataContainer)arg3).getVillagerData();
        VillagerType lv2 = lv.getType();
        VillagerProfession lv3 = lv.getProfession();
        VillagerResourceMetadata.HatType lv4 = this.getHatType(this.villagerTypeToHat, "type", Registry.VILLAGER_TYPE, lv2);
        VillagerResourceMetadata.HatType lv5 = this.getHatType(this.professionToHat, "profession", Registry.VILLAGER_PROFESSION, lv3);
        Object lv6 = this.getContextModel();
        ((ModelWithHat)lv6).setHatVisible(lv5 == VillagerResourceMetadata.HatType.NONE || lv5 == VillagerResourceMetadata.HatType.PARTIAL && lv4 != VillagerResourceMetadata.HatType.FULL);
        Identifier lv7 = this.findTexture("type", Registry.VILLAGER_TYPE.getId(lv2));
        VillagerClothingFeatureRenderer.renderModel(lv6, lv7, arg, arg2, i, arg3, 1.0f, 1.0f, 1.0f);
        ((ModelWithHat)lv6).setHatVisible(true);
        if (lv3 != VillagerProfession.NONE && !((LivingEntity)arg3).isBaby()) {
            Identifier lv8 = this.findTexture("profession", Registry.VILLAGER_PROFESSION.getId(lv3));
            VillagerClothingFeatureRenderer.renderModel(lv6, lv8, arg, arg2, i, arg3, 1.0f, 1.0f, 1.0f);
            if (lv3 != VillagerProfession.NITWIT) {
                Identifier lv9 = this.findTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(lv.getLevel(), 1, LEVEL_TO_ID.size())));
                VillagerClothingFeatureRenderer.renderModel(lv6, lv9, arg, arg2, i, arg3, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private Identifier findTexture(String string, Identifier arg) {
        return new Identifier(arg.getNamespace(), "textures/entity/" + this.entityType + "/" + string + "/" + arg.getPath() + ".png");
    }

    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<K, VillagerResourceMetadata.HatType> object2ObjectMap, String string, DefaultedRegistry<K> arg, K object) {
        return (VillagerResourceMetadata.HatType)((Object)object2ObjectMap.computeIfAbsent(object, object2 -> {
            try (Resource lv = this.resourceManager.getResource(this.findTexture(string, arg.getId(object)));){
                VillagerResourceMetadata lv2 = lv.getMetadata(VillagerResourceMetadata.READER);
                if (lv2 == null) return VillagerResourceMetadata.HatType.NONE;
                VillagerResourceMetadata.HatType hatType = lv2.getHatType();
                return hatType;
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return VillagerResourceMetadata.HatType.NONE;
        }));
    }

    @Override
    public void apply(ResourceManager arg) {
        this.professionToHat.clear();
        this.villagerTypeToHat.clear();
    }
}

