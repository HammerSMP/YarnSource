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
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.Vanishable;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem
extends ToolItem
implements Vanishable {
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> field_23745;

    public SwordItem(ToolMaterial arg, int i, float f, Item.Settings arg2) {
        super(arg, arg2);
        this.attackDamage = (float)i + arg.getAttackDamage();
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_DAMAGE, (Object)new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_SPEED, (Object)new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)f, EntityAttributeModifier.Operation.ADDITION));
        this.field_23745 = builder.build();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canMine(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        return !arg4.isCreative();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack arg, BlockState arg2) {
        if (arg2.isOf(Blocks.COBWEB)) {
            return 15.0f;
        }
        Material lv = arg2.getMaterial();
        if (lv == Material.PLANT || lv == Material.REPLACEABLE_PLANT || lv == Material.UNUSED_PLANT || arg2.isIn(BlockTags.LEAVES) || lv == Material.GOURD) {
            return 1.5f;
        }
        return 1.0f;
    }

    @Override
    public boolean postHit(ItemStack arg2, LivingEntity arg22, LivingEntity arg3) {
        arg2.damage(1, arg3, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack arg2, World arg22, BlockState arg3, BlockPos arg4, LivingEntity arg5) {
        if (arg3.getHardness(arg22, arg4) != 0.0f) {
            arg2.damage(2, arg5, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean isEffectiveOn(BlockState arg) {
        return arg.isOf(Blocks.COBWEB);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot arg) {
        if (arg == EquipmentSlot.MAINHAND) {
            return this.field_23745;
        }
        return super.getAttributeModifiers(arg);
    }
}

