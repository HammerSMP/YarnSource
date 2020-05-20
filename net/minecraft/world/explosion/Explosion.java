/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Explosion {
    private final boolean createFire;
    private final DestructionType destructionType;
    private final Random random = new Random();
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    private final float power;
    private DamageSource damageSource;
    private final List<BlockPos> affectedBlocks = Lists.newArrayList();
    private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

    @Environment(value=EnvType.CLIENT)
    public Explosion(World arg, @Nullable Entity arg2, double d, double e, double f, float g, List<BlockPos> list) {
        this(arg, arg2, d, e, f, g, false, DestructionType.DESTROY, list);
    }

    @Environment(value=EnvType.CLIENT)
    public Explosion(World arg, @Nullable Entity arg2, double d, double e, double f, float g, boolean bl, DestructionType arg3, List<BlockPos> list) {
        this(arg, arg2, d, e, f, g, bl, arg3);
        this.affectedBlocks.addAll(list);
    }

    public Explosion(World arg, @Nullable Entity arg2, double d, double e, double f, float g, boolean bl, DestructionType arg3) {
        this.world = arg;
        this.entity = arg2;
        this.power = g;
        this.x = d;
        this.y = e;
        this.z = f;
        this.createFire = bl;
        this.destructionType = arg3;
        this.damageSource = DamageSource.explosion(this);
    }

    public static float getExposure(Vec3d arg, Entity arg2) {
        Box lv = arg2.getBoundingBox();
        double d = 1.0 / ((lv.maxX - lv.minX) * 2.0 + 1.0);
        double e = 1.0 / ((lv.maxY - lv.minY) * 2.0 + 1.0);
        double f = 1.0 / ((lv.maxZ - lv.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f;
        }
        int i = 0;
        int j = 0;
        float k = 0.0f;
        while (k <= 1.0f) {
            float l = 0.0f;
            while (l <= 1.0f) {
                float m = 0.0f;
                while (m <= 1.0f) {
                    double p;
                    double o;
                    double n = MathHelper.lerp((double)k, lv.minX, lv.maxX);
                    Vec3d lv2 = new Vec3d(n + g, o = MathHelper.lerp((double)l, lv.minY, lv.maxY), (p = MathHelper.lerp((double)m, lv.minZ, lv.maxZ)) + h);
                    if (arg2.world.rayTrace(new RayTraceContext(lv2, arg, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, arg2)).getType() == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                    m = (float)((double)m + f);
                }
                l = (float)((double)l + e);
            }
            k = (float)((double)k + d);
        }
        return (float)i / (float)j;
    }

    public void collectBlocksAndDamageEntities() {
        HashSet set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d = (float)j / 15.0f * 2.0f - 1.0f;
                    double e = (float)k / 15.0f * 2.0f - 1.0f;
                    double f = (float)l / 15.0f * 2.0f - 1.0f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    double m = this.x;
                    double n = this.y;
                    double o = this.z;
                    float p = 0.3f;
                    for (float h = this.power * (0.7f + this.world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos lv = new BlockPos(m, n, o);
                        BlockState lv2 = this.world.getBlockState(lv);
                        FluidState lv3 = this.world.getFluidState(lv);
                        if (!lv2.isAir() || !lv3.isEmpty()) {
                            float q = Math.max(lv2.getBlock().getBlastResistance(), lv3.getBlastResistance());
                            if (this.entity != null) {
                                q = this.entity.getEffectiveExplosionResistance(this, this.world, lv, lv2, lv3, q);
                            }
                            h -= (q + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, lv, lv2, h))) {
                            set.add(lv);
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlocks.addAll(set);
        float r = this.power * 2.0f;
        int s = MathHelper.floor(this.x - (double)r - 1.0);
        int t = MathHelper.floor(this.x + (double)r + 1.0);
        int u = MathHelper.floor(this.y - (double)r - 1.0);
        int v = MathHelper.floor(this.y + (double)r + 1.0);
        int w = MathHelper.floor(this.z - (double)r - 1.0);
        int x = MathHelper.floor(this.z + (double)r + 1.0);
        List<Entity> list = this.world.getEntities(this.entity, new Box(s, u, w, t, v, x));
        Vec3d lv4 = new Vec3d(this.x, this.y, this.z);
        for (int y = 0; y < list.size(); ++y) {
            PlayerEntity lv6;
            double ac;
            double ab;
            double aa;
            double ad;
            double z;
            Entity lv5 = list.get(y);
            if (lv5.isImmuneToExplosion() || !((z = (double)(MathHelper.sqrt(lv5.squaredDistanceTo(lv4)) / r)) <= 1.0) || (ad = (double)MathHelper.sqrt((aa = lv5.getX() - this.x) * aa + (ab = (lv5 instanceof TntEntity ? lv5.getY() : lv5.getEyeY()) - this.y) * ab + (ac = lv5.getZ() - this.z) * ac)) == 0.0) continue;
            aa /= ad;
            ab /= ad;
            ac /= ad;
            double ae = Explosion.getExposure(lv4, lv5);
            double af = (1.0 - z) * ae;
            lv5.damage(this.getDamageSource(), (int)((af * af + af) / 2.0 * 7.0 * (double)r + 1.0));
            double ag = af;
            if (lv5 instanceof LivingEntity) {
                ag = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)lv5, af);
            }
            lv5.setVelocity(lv5.getVelocity().add(aa * ag, ab * ag, ac * ag));
            if (!(lv5 instanceof PlayerEntity) || (lv6 = (PlayerEntity)lv5).isSpectator() || lv6.isCreative() && lv6.abilities.flying) continue;
            this.affectedPlayers.put(lv6, new Vec3d(aa * af, ab * af, ac * af));
        }
    }

    public void affectWorld(boolean bl) {
        boolean bl2;
        if (this.world.isClient) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
        }
        boolean bl3 = bl2 = this.destructionType != DestructionType.NONE;
        if (bl) {
            if (this.power < 2.0f || !bl2) {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }
        if (bl2) {
            ObjectArrayList objectArrayList = new ObjectArrayList();
            Collections.shuffle(this.affectedBlocks, this.world.random);
            for (BlockPos lv : this.affectedBlocks) {
                BlockState lv2 = this.world.getBlockState(lv);
                Block lv3 = lv2.getBlock();
                if (lv2.isAir()) continue;
                BlockPos lv4 = lv.toImmutable();
                this.world.getProfiler().push("explosion_blocks");
                if (lv3.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
                    BlockEntity lv5 = lv3.hasBlockEntity() ? this.world.getBlockEntity(lv) : null;
                    LootContext.Builder lv6 = new LootContext.Builder((ServerWorld)this.world).random(this.world.random).parameter(LootContextParameters.POSITION, lv).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, lv5).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
                    if (this.destructionType == DestructionType.DESTROY) {
                        lv6.parameter(LootContextParameters.EXPLOSION_RADIUS, Float.valueOf(this.power));
                    }
                    lv2.getDroppedStacks(lv6).forEach(arg2 -> Explosion.method_24023((ObjectArrayList<Pair<ItemStack, BlockPos>>)objectArrayList, arg2, lv4));
                }
                this.world.setBlockState(lv, Blocks.AIR.getDefaultState(), 3);
                lv3.onDestroyedByExplosion(this.world, lv, this);
                this.world.getProfiler().pop();
            }
            for (Pair pair : objectArrayList) {
                Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
            }
        }
        if (this.createFire) {
            for (BlockPos lv7 : this.affectedBlocks) {
                if (this.random.nextInt(3) != 0 || !this.world.getBlockState(lv7).isAir() || !this.world.getBlockState(lv7.down()).isOpaqueFullCube(this.world, lv7.down())) continue;
                this.world.setBlockState(lv7, AbstractFireBlock.getState(this.world, lv7));
            }
        }
    }

    private static void method_24023(ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList, ItemStack arg, BlockPos arg2) {
        int i = objectArrayList.size();
        for (int j = 0; j < i; ++j) {
            Pair pair = (Pair)objectArrayList.get(j);
            ItemStack lv = (ItemStack)pair.getFirst();
            if (!ItemEntity.canMerge(lv, arg)) continue;
            ItemStack lv2 = ItemEntity.merge(lv, arg, 16);
            objectArrayList.set(j, (Object)Pair.of((Object)lv2, (Object)pair.getSecond()));
            if (!arg.isEmpty()) continue;
            return;
        }
        objectArrayList.add((Object)Pair.of((Object)arg, (Object)arg2));
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public void setDamageSource(DamageSource arg) {
        this.damageSource = arg;
    }

    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        Entity lv;
        if (this.entity == null) {
            return null;
        }
        if (this.entity instanceof TntEntity) {
            return ((TntEntity)this.entity).getCausingEntity();
        }
        if (this.entity instanceof LivingEntity) {
            return (LivingEntity)this.entity;
        }
        if (this.entity instanceof ProjectileEntity && (lv = ((ProjectileEntity)this.entity).getOwner()) instanceof LivingEntity) {
            return (LivingEntity)lv;
        }
        return null;
    }

    public void clearAffectedBlocks() {
        this.affectedBlocks.clear();
    }

    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    public static enum DestructionType {
        NONE,
        BREAK,
        DESTROY;

    }
}

