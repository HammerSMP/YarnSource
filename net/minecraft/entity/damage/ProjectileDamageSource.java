/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectileDamageSource
extends EntityDamageSource {
    private final Entity attacker;

    public ProjectileDamageSource(String name, Entity projectile, @Nullable Entity attacker) {
        super(name, projectile);
        this.attacker = attacker;
    }

    @Override
    @Nullable
    public Entity getSource() {
        return this.source;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.attacker;
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        Text lv = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
        ItemStack lv2 = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        String string2 = string + ".item";
        if (!lv2.isEmpty() && lv2.hasCustomName()) {
            return new TranslatableText(string2, entity.getDisplayName(), lv, lv2.toHoverableText());
        }
        return new TranslatableText(string, entity.getDisplayName(), lv);
    }
}

