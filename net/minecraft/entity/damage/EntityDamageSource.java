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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;

public class EntityDamageSource
extends DamageSource {
    @Nullable
    protected final Entity source;
    private boolean thorns;

    public EntityDamageSource(String string, @Nullable Entity arg) {
        super(string);
        this.source = arg;
    }

    public EntityDamageSource setThorns() {
        this.thorns = true;
        return this;
    }

    public boolean isThorns() {
        return this.thorns;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.source;
    }

    @Override
    public Text getDeathMessage(LivingEntity arg) {
        ItemStack lv = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        if (!lv.isEmpty() && lv.hasCustomName()) {
            return new TranslatableText(string + ".item", arg.getDisplayName(), this.source.getDisplayName(), lv.toHoverableText());
        }
        return new TranslatableText(string, arg.getDisplayName(), this.source.getDisplayName());
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d getPosition() {
        return this.source != null ? this.source.getPos() : null;
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.source + ")";
    }
}

