/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HungerManager {
    private int foodLevel = 20;
    private float foodSaturationLevel = 5.0f;
    private float exhaustion;
    private int foodStarvationTimer;
    private int prevFoodLevel = 20;

    public void add(int i, float f) {
        this.foodLevel = Math.min(i + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)i * f * 2.0f, (float)this.foodLevel);
    }

    public void eat(Item arg, ItemStack arg2) {
        if (arg.isFood()) {
            FoodComponent lv = arg.getFoodComponent();
            this.add(lv.getHunger(), lv.getSaturationModifier());
        }
    }

    public void update(PlayerEntity arg) {
        boolean bl;
        Difficulty lv = arg.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0f) {
            this.exhaustion -= 4.0f;
            if (this.foodSaturationLevel > 0.0f) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0f, 0.0f);
            } else if (lv != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if ((bl = arg.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) && this.foodSaturationLevel > 0.0f && arg.canFoodHeal() && this.foodLevel >= 20) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 10) {
                float f = Math.min(this.foodSaturationLevel, 6.0f);
                arg.heal(f / 6.0f);
                this.addExhaustion(f);
                this.foodStarvationTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && arg.canFoodHeal()) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                arg.heal(1.0f);
                this.addExhaustion(6.0f);
                this.foodStarvationTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodStarvationTimer;
            if (this.foodStarvationTimer >= 80) {
                if (arg.getHealth() > 10.0f || lv == Difficulty.HARD || arg.getHealth() > 1.0f && lv == Difficulty.NORMAL) {
                    arg.damage(DamageSource.STARVE, 1.0f);
                }
                this.foodStarvationTimer = 0;
            }
        } else {
            this.foodStarvationTimer = 0;
        }
    }

    public void fromTag(CompoundTag arg) {
        if (arg.contains("foodLevel", 99)) {
            this.foodLevel = arg.getInt("foodLevel");
            this.foodStarvationTimer = arg.getInt("foodTickTimer");
            this.foodSaturationLevel = arg.getFloat("foodSaturationLevel");
            this.exhaustion = arg.getFloat("foodExhaustionLevel");
        }
    }

    public void toTag(CompoundTag arg) {
        arg.putInt("foodLevel", this.foodLevel);
        arg.putInt("foodTickTimer", this.foodStarvationTimer);
        arg.putFloat("foodSaturationLevel", this.foodSaturationLevel);
        arg.putFloat("foodExhaustionLevel", this.exhaustion);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public boolean isNotFull() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float f) {
        this.exhaustion = Math.min(this.exhaustion + f, 40.0f);
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int i) {
        this.foodLevel = i;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSaturationLevelClient(float f) {
        this.foodSaturationLevel = f;
    }
}

