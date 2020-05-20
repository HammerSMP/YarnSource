/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem
extends Item
implements Vanishable {
    private final Multimap<EntityAttribute, EntityAttributeModifier> field_23746;

    public TridentItem(Item.Settings arg) {
        super(arg);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_DAMAGE, (Object)new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION));
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_SPEED, (Object)new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Tool modifier", (double)-2.9f, EntityAttributeModifier.Operation.ADDITION));
        this.field_23746 = builder.build();
    }

    @Override
    public boolean canMine(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        return !arg4.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack arg) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack arg) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack arg, World arg22, LivingEntity arg3, int i) {
        if (!(arg3 instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity lv = (PlayerEntity)arg3;
        int j = this.getMaxUseTime(arg) - i;
        if (j < 10) {
            return;
        }
        int k = EnchantmentHelper.getRiptide(arg);
        if (k > 0 && !lv.isTouchingWaterOrRain()) {
            return;
        }
        if (!arg22.isClient) {
            arg.damage(1, lv, arg2 -> arg2.sendToolBreakStatus(arg3.getActiveHand()));
            if (k == 0) {
                TridentEntity lv2 = new TridentEntity(arg22, (LivingEntity)lv, arg);
                lv2.setProperties(lv, lv.pitch, lv.yaw, 0.0f, 2.5f + (float)k * 0.5f, 1.0f);
                if (lv.abilities.creativeMode) {
                    lv2.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
                arg22.spawnEntity(lv2);
                arg22.playSoundFromEntity(null, lv2, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
                if (!lv.abilities.creativeMode) {
                    lv.inventory.removeOne(arg);
                }
            }
        }
        lv.incrementStat(Stats.USED.getOrCreateStat(this));
        if (k > 0) {
            SoundEvent lv5;
            float f = lv.yaw;
            float g = lv.pitch;
            float h = -MathHelper.sin(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
            float l = -MathHelper.sin(g * ((float)Math.PI / 180));
            float m = MathHelper.cos(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
            float n = MathHelper.sqrt(h * h + l * l + m * m);
            float o = 3.0f * ((1.0f + (float)k) / 4.0f);
            lv.addVelocity(h *= o / n, l *= o / n, m *= o / n);
            lv.setPushCooldown(20);
            if (lv.isOnGround()) {
                float p = 1.1999999f;
                lv.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
            }
            if (k >= 3) {
                SoundEvent lv3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
            } else if (k == 2) {
                SoundEvent lv4 = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
            } else {
                lv5 = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
            }
            arg22.playSoundFromEntity(null, lv, lv5, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        if (lv.getDamage() >= lv.getMaxDamage() - 1) {
            return TypedActionResult.fail(lv);
        }
        if (EnchantmentHelper.getRiptide(lv) > 0 && !arg2.isTouchingWaterOrRain()) {
            return TypedActionResult.fail(lv);
        }
        arg2.setCurrentHand(arg3);
        return TypedActionResult.consume(lv);
    }

    @Override
    public boolean postHit(ItemStack arg2, LivingEntity arg22, LivingEntity arg3) {
        arg2.damage(1, arg3, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack arg2, World arg22, BlockState arg3, BlockPos arg4, LivingEntity arg5) {
        if ((double)arg3.getHardness(arg22, arg4) != 0.0) {
            arg2.damage(2, arg5, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot arg) {
        if (arg == EquipmentSlot.MAINHAND) {
            return this.field_23746;
        }
        return super.getAttributeModifiers(arg);
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

