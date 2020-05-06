/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public HeadFeatureRenderer(FeatureRendererContext<T, M> arg) {
        super(arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l) {
        boolean bl;
        ItemStack lv = ((LivingEntity)arg3).getEquippedStack(EquipmentSlot.HEAD);
        if (lv.isEmpty()) {
            return;
        }
        Item lv2 = lv.getItem();
        arg.push();
        boolean bl2 = bl = arg3 instanceof VillagerEntity || arg3 instanceof ZombieVillagerEntity;
        if (((LivingEntity)arg3).isBaby() && !(arg3 instanceof VillagerEntity)) {
            float m = 2.0f;
            float n = 1.4f;
            arg.translate(0.0, 0.03125, 0.0);
            arg.scale(0.7f, 0.7f, 0.7f);
            arg.translate(0.0, 1.0, 0.0);
        }
        ((ModelWithHead)this.getContextModel()).getHead().rotate(arg);
        if (lv2 instanceof BlockItem && ((BlockItem)lv2).getBlock() instanceof AbstractSkullBlock) {
            float o = 1.1875f;
            arg.scale(1.1875f, -1.1875f, -1.1875f);
            if (bl) {
                arg.translate(0.0, 0.0625, 0.0);
            }
            GameProfile gameProfile = null;
            if (lv.hasTag()) {
                String string;
                CompoundTag lv3 = lv.getTag();
                if (lv3.contains("SkullOwner", 10)) {
                    gameProfile = NbtHelper.toGameProfile(lv3.getCompound("SkullOwner"));
                } else if (lv3.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)(string = lv3.getString("SkullOwner")))) {
                    gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
                    lv3.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                }
            }
            arg.translate(-0.5, 0.0, -0.5);
            SkullBlockEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)((BlockItem)lv2).getBlock()).getSkullType(), gameProfile, f, arg, arg2, i);
        } else if (!(lv2 instanceof ArmorItem) || ((ArmorItem)lv2).getSlotType() != EquipmentSlot.HEAD) {
            float p = 0.625f;
            arg.translate(0.0, -0.25, 0.0);
            arg.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
            arg.scale(0.625f, -0.625f, -0.625f);
            if (bl) {
                arg.translate(0.0, 0.1875, 0.0);
            }
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem((LivingEntity)arg3, lv, ModelTransformation.Mode.HEAD, false, arg, arg2, i);
        }
        arg.pop();
    }
}

