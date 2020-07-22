/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class FallingBlockEntity
extends Entity {
    private BlockState block = Blocks.SAND.getDefaultState();
    public int timeFalling;
    public boolean dropItem = true;
    private boolean destroyedOnLanding;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0f;
    public CompoundTag blockEntityData;
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public FallingBlockEntity(EntityType<? extends FallingBlockEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public FallingBlockEntity(World world, double x, double y, double z, BlockState block) {
        this((EntityType<? extends FallingBlockEntity>)EntityType.FALLING_BLOCK, world);
        this.block = block;
        this.inanimate = true;
        this.updatePosition(x, y + (double)((1.0f - this.getHeight()) / 2.0f), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public void tick() {
        if (this.block.isAir()) {
            this.remove();
            return;
        }
        Block lv = this.block.getBlock();
        if (this.timeFalling++ == 0) {
            BlockPos lv2 = this.getBlockPos();
            if (this.world.getBlockState(lv2).isOf(lv)) {
                this.world.removeBlock(lv2, false);
            } else if (!this.world.isClient) {
                this.remove();
                return;
            }
        }
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (!this.world.isClient) {
            BlockHitResult lv4;
            BlockPos lv3 = this.getBlockPos();
            boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
            boolean bl2 = bl && this.world.getFluidState(lv3).isIn(FluidTags.WATER);
            double d = this.getVelocity().lengthSquared();
            if (bl && d > 1.0 && (lv4 = this.world.rayTrace(new RayTraceContext(new Vec3d(this.prevX, this.prevY, this.prevZ), this.getPos(), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && this.world.getFluidState(lv4.getBlockPos()).isIn(FluidTags.WATER)) {
                lv3 = lv4.getBlockPos();
                bl2 = true;
            }
            if (this.onGround || bl2) {
                BlockState lv5 = this.world.getBlockState(lv3);
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
                if (!lv5.isOf(Blocks.MOVING_PISTON)) {
                    this.remove();
                    if (!this.destroyedOnLanding) {
                        boolean bl5;
                        boolean bl3 = lv5.canReplace(new AutomaticItemPlacementContext(this.world, lv3, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(lv3.down())) && (!bl || !bl2);
                        boolean bl6 = bl5 = this.block.canPlaceAt(this.world, lv3) && !bl4;
                        if (bl3 && bl5) {
                            if (this.block.contains(Properties.WATERLOGGED) && this.world.getFluidState(lv3).getFluid() == Fluids.WATER) {
                                this.block = (BlockState)this.block.with(Properties.WATERLOGGED, true);
                            }
                            if (this.world.setBlockState(lv3, this.block, 3)) {
                                BlockEntity lv6;
                                if (lv instanceof FallingBlock) {
                                    ((FallingBlock)lv).onLanding(this.world, lv3, this.block, lv5, this);
                                }
                                if (this.blockEntityData != null && lv instanceof BlockEntityProvider && (lv6 = this.world.getBlockEntity(lv3)) != null) {
                                    CompoundTag lv7 = lv6.toTag(new CompoundTag());
                                    for (String string : this.blockEntityData.getKeys()) {
                                        Tag lv8 = this.blockEntityData.get(string);
                                        if ("x".equals(string) || "y".equals(string) || "z".equals(string)) continue;
                                        lv7.put(string, lv8.copy());
                                    }
                                    lv6.fromTag(this.block, lv7);
                                    lv6.markDirty();
                                }
                            } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                this.dropItem(lv);
                            }
                        } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            this.dropItem(lv);
                        }
                    } else if (lv instanceof FallingBlock) {
                        ((FallingBlock)lv).onDestroyedOnLanding(this.world, lv3, this);
                    }
                }
            } else if (!(this.world.isClient || (this.timeFalling <= 100 || lv3.getY() >= 1 && lv3.getY() <= 256) && this.timeFalling <= 600)) {
                if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropItem(lv);
                }
                this.remove();
            }
        }
        this.setVelocity(this.getVelocity().multiply(0.98));
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        int i;
        if (this.hurtEntities && (i = MathHelper.ceil(fallDistance - 1.0f)) > 0) {
            ArrayList list = Lists.newArrayList(this.world.getOtherEntities(this, this.getBoundingBox()));
            boolean bl = this.block.isIn(BlockTags.ANVIL);
            DamageSource lv = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
            for (Entity lv2 : list) {
                lv2.damage(lv, Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
            }
            if (bl && (double)this.random.nextFloat() < (double)0.05f + (double)i * 0.05) {
                BlockState lv3 = AnvilBlock.getLandingState(this.block);
                if (lv3 == null) {
                    this.destroyedOnLanding = true;
                } else {
                    this.block = lv3;
                }
            }
        }
        return false;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.put("BlockState", NbtHelper.fromBlockState(this.block));
        tag.putInt("Time", this.timeFalling);
        tag.putBoolean("DropItem", this.dropItem);
        tag.putBoolean("HurtEntities", this.hurtEntities);
        tag.putFloat("FallHurtAmount", this.fallHurtAmount);
        tag.putInt("FallHurtMax", this.fallHurtMax);
        if (this.blockEntityData != null) {
            tag.put("TileEntityData", this.blockEntityData);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        this.block = NbtHelper.toBlockState(tag.getCompound("BlockState"));
        this.timeFalling = tag.getInt("Time");
        if (tag.contains("HurtEntities", 99)) {
            this.hurtEntities = tag.getBoolean("HurtEntities");
            this.fallHurtAmount = tag.getFloat("FallHurtAmount");
            this.fallHurtMax = tag.getInt("FallHurtMax");
        } else if (this.block.isIn(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }
        if (tag.contains("DropItem", 99)) {
            this.dropItem = tag.getBoolean("DropItem");
        }
        if (tag.contains("TileEntityData", 10)) {
            this.blockEntityData = tag.getCompound("TileEntityData");
        }
        if (this.block.isAir()) {
            this.block = Blocks.SAND.getDefaultState();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public World getWorldClient() {
        return this.world;
    }

    public void setHurtEntities(boolean hurtEntities) {
        this.hurtEntities = hurtEntities;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void populateCrashReport(CrashReportSection section) {
        super.populateCrashReport(section);
        section.add("Immitating BlockState", this.block.toString());
    }

    public BlockState getBlockState() {
        return this.block;
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }
}

