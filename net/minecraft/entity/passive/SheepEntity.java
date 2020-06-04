/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SheepEntity
extends AnimalEntity
implements Shearable {
    private static final TrackedData<Byte> COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final Map<DyeColor, ItemConvertible> DROPS = Util.make(Maps.newEnumMap(DyeColor.class), enumMap -> {
        enumMap.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        enumMap.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        enumMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        enumMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        enumMap.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        enumMap.put(DyeColor.LIME, Blocks.LIME_WOOL);
        enumMap.put(DyeColor.PINK, Blocks.PINK_WOOL);
        enumMap.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        enumMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        enumMap.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        enumMap.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        enumMap.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        enumMap.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        enumMap.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        enumMap.put(DyeColor.RED, Blocks.RED_WOOL);
        enumMap.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });
    private static final Map<DyeColor, float[]> COLORS = Maps.newEnumMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(arg -> arg, SheepEntity::getDyedColor)));
    private int eatGrassTimer;
    private EatGrassGoal eatGrassGoal;

    private static float[] getDyedColor(DyeColor arg) {
        if (arg == DyeColor.WHITE) {
            return new float[]{0.9019608f, 0.9019608f, 0.9019608f};
        }
        float[] fs = arg.getColorComponents();
        float f = 0.75f;
        return new float[]{fs[0] * 0.75f, fs[1] * 0.75f, fs[2] * 0.75f};
    }

    @Environment(value=EnvType.CLIENT)
    public static float[] getRgbColor(DyeColor arg) {
        return COLORS.get(arg);
    }

    public SheepEntity(EntityType<? extends SheepEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.1, Ingredient.ofItems(Items.WHEAT), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, this.eatGrassGoal);
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    protected void mobTick() {
        this.eatGrassTimer = this.eatGrassGoal.getTimer();
        super.mobTick();
    }

    @Override
    public void tickMovement() {
        if (this.world.isClient) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }
        super.tickMovement();
    }

    public static DefaultAttributeContainer.Builder createSheepAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, (byte)0);
    }

    @Override
    public Identifier getLootTableId() {
        if (this.isSheared()) {
            return this.getType().getLootTableId();
        }
        switch (this.getColor()) {
            default: {
                return LootTables.WHITE_SHEEP_ENTITY;
            }
            case ORANGE: {
                return LootTables.ORANGE_SHEEP_ENTITY;
            }
            case MAGENTA: {
                return LootTables.MAGENTA_SHEEP_ENTITY;
            }
            case LIGHT_BLUE: {
                return LootTables.LIGHT_BLUE_SHEEP_ENTITY;
            }
            case YELLOW: {
                return LootTables.YELLOW_SHEEP_ENTITY;
            }
            case LIME: {
                return LootTables.LIME_SHEEP_ENTITY;
            }
            case PINK: {
                return LootTables.PINK_SHEEP_ENTITY;
            }
            case GRAY: {
                return LootTables.GRAY_SHEEP_ENTITY;
            }
            case LIGHT_GRAY: {
                return LootTables.LIGHT_GRAY_SHEEP_ENTITY;
            }
            case CYAN: {
                return LootTables.CYAN_SHEEP_ENTITY;
            }
            case PURPLE: {
                return LootTables.PURPLE_SHEEP_ENTITY;
            }
            case BLUE: {
                return LootTables.BLUE_SHEEP_ENTITY;
            }
            case BROWN: {
                return LootTables.BROWN_SHEEP_ENTITY;
            }
            case GREEN: {
                return LootTables.GREEN_SHEEP_ENTITY;
            }
            case RED: {
                return LootTables.RED_SHEEP_ENTITY;
            }
            case BLACK: 
        }
        return LootTables.BLACK_SHEEP_ENTITY;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleStatus(b);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getNeckAngle(float f) {
        if (this.eatGrassTimer <= 0) {
            return 0.0f;
        }
        if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
            return 1.0f;
        }
        if (this.eatGrassTimer < 4) {
            return ((float)this.eatGrassTimer - f) / 4.0f;
        }
        return -((float)(this.eatGrassTimer - 40) - f) / 4.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadAngle(float f) {
        if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
            float g = ((float)(this.eatGrassTimer - 4) - f) / 32.0f;
            return 0.62831855f + 0.21991149f * MathHelper.sin(g * 28.7f);
        }
        if (this.eatGrassTimer > 0) {
            return 0.62831855f;
        }
        return this.pitch * ((float)Math.PI / 180);
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg22) {
        ItemStack lv = arg.getStackInHand(arg22);
        if (lv.getItem() == Items.SHEARS) {
            if (!this.world.isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                lv.damage(1, arg, arg2 -> arg2.sendToolBreakStatus(arg22));
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return super.interactMob(arg, arg22);
    }

    @Override
    public void sheared(SoundCategory arg) {
        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, arg, 1.0f, 1.0f);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);
        for (int j = 0; j < i; ++j) {
            ItemEntity lv = this.dropItem(DROPS.get(this.getColor()), 1);
            if (lv == null) continue;
            lv.setVelocity(lv.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }
    }

    @Override
    public boolean isShearable() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("Sheared", this.isSheared());
        arg.putByte("Color", (byte)this.getColor().getId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setSheared(arg.getBoolean("Sheared"));
        this.setColor(DyeColor.byId(arg.getByte("Color")));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15f, 1.0f);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR) & 0xF);
    }

    public void setColor(DyeColor arg) {
        byte b = this.dataTracker.get(COLOR);
        this.dataTracker.set(COLOR, (byte)(b & 0xF0 | arg.getId() & 0xF));
    }

    public boolean isSheared() {
        return (this.dataTracker.get(COLOR) & 0x10) != 0;
    }

    public void setSheared(boolean bl) {
        byte b = this.dataTracker.get(COLOR);
        if (bl) {
            this.dataTracker.set(COLOR, (byte)(b | 0x10));
        } else {
            this.dataTracker.set(COLOR, (byte)(b & 0xFFFFFFEF));
        }
    }

    public static DyeColor generateDefaultColor(Random random) {
        int i = random.nextInt(100);
        if (i < 5) {
            return DyeColor.BLACK;
        }
        if (i < 10) {
            return DyeColor.GRAY;
        }
        if (i < 15) {
            return DyeColor.LIGHT_GRAY;
        }
        if (i < 18) {
            return DyeColor.BROWN;
        }
        if (random.nextInt(500) == 0) {
            return DyeColor.PINK;
        }
        return DyeColor.WHITE;
    }

    @Override
    public SheepEntity createChild(PassiveEntity arg) {
        SheepEntity lv = (SheepEntity)arg;
        SheepEntity lv2 = EntityType.SHEEP.create(this.world);
        lv2.setColor(this.getChildColor(this, lv));
        return lv2;
    }

    @Override
    public void onEatingGrass() {
        this.setSheared(false);
        if (this.isBaby()) {
            this.growUp(60);
        }
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setColor(SheepEntity.generateDefaultColor(arg.getRandom()));
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    private DyeColor getChildColor(AnimalEntity arg, AnimalEntity arg22) {
        DyeColor lv = ((SheepEntity)arg).getColor();
        DyeColor lv2 = ((SheepEntity)arg22).getColor();
        CraftingInventory lv3 = SheepEntity.createDyeMixingCraftingInventory(lv, lv2);
        return this.world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, lv3, this.world).map(arg2 -> arg2.craft(lv3)).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getColor).orElseGet(() -> this.world.random.nextBoolean() ? lv : lv2);
    }

    private static CraftingInventory createDyeMixingCraftingInventory(DyeColor arg, DyeColor arg2) {
        CraftingInventory lv = new CraftingInventory(new ScreenHandler(null, -1){

            @Override
            public boolean canUse(PlayerEntity arg) {
                return false;
            }
        }, 2, 1);
        lv.setStack(0, new ItemStack(DyeItem.byColor(arg)));
        lv.setStack(1, new ItemStack(DyeItem.byColor(arg2)));
        return lv;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.95f * arg2.height;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }
}

