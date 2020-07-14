/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ModelPredicateProviderRegistry {
    private static final Map<Identifier, ModelPredicateProvider> GLOBAL = Maps.newHashMap();
    private static final Identifier DAMAGED_ID = new Identifier("damaged");
    private static final Identifier DAMAGE_ID = new Identifier("damage");
    private static final ModelPredicateProvider DAMAGED_PROVIDER = (arg, arg2, arg3) -> arg.isDamaged() ? 1.0f : 0.0f;
    private static final ModelPredicateProvider DAMAGE_PROVIDER = (arg, arg2, arg3) -> MathHelper.clamp((float)arg.getDamage() / (float)arg.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC = Maps.newHashMap();

    private static ModelPredicateProvider register(Identifier id, ModelPredicateProvider provider) {
        GLOBAL.put(id, provider);
        return provider;
    }

    private static void register(Item item, Identifier id, ModelPredicateProvider provider) {
        ITEM_SPECIFIC.computeIfAbsent(item, arg -> Maps.newHashMap()).put(id, provider);
    }

    @Nullable
    public static ModelPredicateProvider get(Item item, Identifier id) {
        ModelPredicateProvider lv;
        if (item.getMaxDamage() > 0) {
            if (DAMAGE_ID.equals(id)) {
                return DAMAGE_PROVIDER;
            }
            if (DAMAGED_ID.equals(id)) {
                return DAMAGED_PROVIDER;
            }
        }
        if ((lv = GLOBAL.get(id)) != null) {
            return lv;
        }
        Map<Identifier, ModelPredicateProvider> map = ITEM_SPECIFIC.get(item);
        if (map == null) {
            return null;
        }
        return map.get(id);
    }

    static {
        ModelPredicateProviderRegistry.register(new Identifier("lefthanded"), (arg, arg2, arg3) -> arg3 == null || arg3.getMainArm() == Arm.RIGHT ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(new Identifier("cooldown"), (arg, arg2, arg3) -> arg3 instanceof PlayerEntity ? ((PlayerEntity)arg3).getItemCooldownManager().getCooldownProgress(arg.getItem(), 0.0f) : 0.0f);
        ModelPredicateProviderRegistry.register(new Identifier("custom_model_data"), (arg, arg2, arg3) -> arg.hasTag() ? (float)arg.getTag().getInt("CustomModelData") : 0.0f);
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pull"), (arg, arg2, arg3) -> {
            if (arg3 == null) {
                return 0.0f;
            }
            if (arg3.getActiveItem() != arg) {
                return 0.0f;
            }
            return (float)(arg.getMaxUseTime() - arg3.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(Items.BOW, new Identifier("pulling"), (arg, arg2, arg3) -> arg3 != null && arg3.isUsingItem() && arg3.getActiveItem() == arg ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CLOCK, new Identifier("time"), new ModelPredicateProvider(){
            private double time;
            private double step;
            private long lastTick;

            @Override
            public float call(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3) {
                double e;
                Entity lv;
                Entity entity = lv = arg3 != null ? arg3 : arg.getHolder();
                if (lv == null) {
                    return 0.0f;
                }
                if (arg2 == null && lv.world instanceof ClientWorld) {
                    arg2 = (ClientWorld)lv.world;
                }
                if (arg2 == null) {
                    return 0.0f;
                }
                if (arg2.getDimension().isNatural()) {
                    double d = arg2.method_30274(1.0f);
                } else {
                    e = Math.random();
                }
                e = this.getTime(arg2, e);
                return (float)e;
            }

            private double getTime(World world, double skyAngle) {
                if (world.getTime() != this.lastTick) {
                    this.lastTick = world.getTime();
                    double e = skyAngle - this.time;
                    e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
                    this.step += e * 0.1;
                    this.step *= 0.9;
                    this.time = MathHelper.floorMod(this.time + this.step, 1.0);
                }
                return this.time;
            }
        });
        ModelPredicateProviderRegistry.register(Items.COMPASS, new Identifier("angle"), new ModelPredicateProvider(){
            private final AngleRandomizer value = new AngleRandomizer();
            private final AngleRandomizer speed = new AngleRandomizer();

            @Override
            public float call(ItemStack arg, @Nullable ClientWorld arg2, @Nullable LivingEntity arg3) {
                double h;
                Entity lv;
                Entity entity = lv = arg3 != null ? arg3 : arg.getHolder();
                if (lv == null) {
                    return 0.0f;
                }
                if (arg2 == null && lv.world instanceof ClientWorld) {
                    arg2 = (ClientWorld)lv.world;
                }
                BlockPos lv2 = CompassItem.hasLodestone(arg) ? this.getLodestonePos(arg2, arg.getOrCreateTag()) : this.getSpawnPos(arg2);
                long l = arg2.getTime();
                if (lv2 == null || lv.getPos().squaredDistanceTo((double)lv2.getX() + 0.5, lv.getPos().getY(), (double)lv2.getZ() + 0.5) < (double)1.0E-5f) {
                    if (this.speed.shouldUpdate(l)) {
                        this.speed.update(l, Math.random());
                    }
                    double d = this.speed.value + (double)((float)arg.hashCode() / 2.14748365E9f);
                    return MathHelper.floorMod((float)d, 1.0f);
                }
                boolean bl = arg3 instanceof PlayerEntity && ((PlayerEntity)arg3).isMainPlayer();
                double e = 0.0;
                if (bl) {
                    e = arg3.yaw;
                } else if (lv instanceof ItemFrameEntity) {
                    e = this.getItemFrameAngleOffset((ItemFrameEntity)lv);
                } else if (lv instanceof ItemEntity) {
                    e = 180.0f - ((ItemEntity)lv).method_27314(0.5f) / ((float)Math.PI * 2) * 360.0f;
                } else if (arg3 != null) {
                    e = arg3.bodyYaw;
                }
                e = MathHelper.floorMod(e / 360.0, 1.0);
                double f = this.getAngleToPos(Vec3d.ofCenter(lv2), lv) / 6.2831854820251465;
                if (bl) {
                    if (this.value.shouldUpdate(l)) {
                        this.value.update(l, 0.5 - (e - 0.25));
                    }
                    double g = f + this.value.value;
                } else {
                    h = 0.5 - (e - 0.25 - f);
                }
                return MathHelper.floorMod((float)h, 1.0f);
            }

            @Nullable
            private BlockPos getSpawnPos(ClientWorld world) {
                return world.getDimension().isNatural() ? world.getSpawnPos() : null;
            }

            @Nullable
            private BlockPos getLodestonePos(World world, CompoundTag tag) {
                Optional<RegistryKey<World>> optional;
                boolean bl = tag.contains("LodestonePos");
                boolean bl2 = tag.contains("LodestoneDimension");
                if (bl && bl2 && (optional = CompassItem.getLodestoneDimension(tag)).isPresent() && world.getRegistryKey() == optional.get()) {
                    return NbtHelper.toBlockPos(tag.getCompound("LodestonePos"));
                }
                return null;
            }

            private double getItemFrameAngleOffset(ItemFrameEntity itemFrame) {
                Direction lv = itemFrame.getHorizontalFacing();
                int i = lv.getAxis().isVertical() ? 90 * lv.getDirection().offset() : 0;
                return MathHelper.wrapDegrees(180 + lv.getHorizontal() * 90 + itemFrame.getRotation() * 45 + i);
            }

            private double getAngleToPos(Vec3d pos, Entity entity) {
                return Math.atan2(pos.getZ() - entity.getZ(), pos.getX() - entity.getX());
            }
        });
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pull"), (arg, arg2, arg3) -> {
            if (arg3 == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(arg)) {
                return 0.0f;
            }
            return (float)(arg.getMaxUseTime() - arg3.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(arg);
        });
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("pulling"), (arg, arg2, arg3) -> arg3 != null && arg3.isUsingItem() && arg3.getActiveItem() == arg && !CrossbowItem.isCharged(arg) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("charged"), (arg, arg2, arg3) -> arg3 != null && CrossbowItem.isCharged(arg) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.CROSSBOW, new Identifier("firework"), (arg, arg2, arg3) -> arg3 != null && CrossbowItem.isCharged(arg) && CrossbowItem.hasProjectile(arg, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.ELYTRA, new Identifier("broken"), (arg, arg2, arg3) -> ElytraItem.isUsable(arg) ? 0.0f : 1.0f);
        ModelPredicateProviderRegistry.register(Items.FISHING_ROD, new Identifier("cast"), (arg, arg2, arg3) -> {
            boolean bl2;
            if (arg3 == null) {
                return 0.0f;
            }
            boolean bl = arg3.getMainHandStack() == arg;
            boolean bl3 = bl2 = arg3.getOffHandStack() == arg;
            if (arg3.getMainHandStack().getItem() instanceof FishingRodItem) {
                bl2 = false;
            }
            return (bl || bl2) && arg3 instanceof PlayerEntity && ((PlayerEntity)arg3).fishHook != null ? 1.0f : 0.0f;
        });
        ModelPredicateProviderRegistry.register(Items.SHIELD, new Identifier("blocking"), (arg, arg2, arg3) -> arg3 != null && arg3.isUsingItem() && arg3.getActiveItem() == arg ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(Items.TRIDENT, new Identifier("throwing"), (arg, arg2, arg3) -> arg3 != null && arg3.isUsingItem() && arg3.getActiveItem() == arg ? 1.0f : 0.0f);
    }

    @Environment(value=EnvType.CLIENT)
    static class AngleRandomizer {
        private double value;
        private double speed;
        private long lastUpdateTime;

        private AngleRandomizer() {
        }

        private boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        private void update(long time, double d) {
            this.lastUpdateTime = time;
            double e = d - this.value;
            e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
            this.speed += e * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
        }
    }
}

