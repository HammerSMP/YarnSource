/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LeashKnotEntity
extends AbstractDecorationEntity {
    public LeashKnotEntity(EntityType<? extends LeashKnotEntity> arg, World arg2) {
        super((EntityType<? extends AbstractDecorationEntity>)arg, arg2);
    }

    public LeashKnotEntity(World arg, BlockPos arg2) {
        super(EntityType.LEASH_KNOT, arg, arg2);
        this.updatePosition((double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5);
        float f = 0.125f;
        float g = 0.1875f;
        float h = 0.25f;
        this.setBoundingBox(new Box(this.getX() - 0.1875, this.getY() - 0.25 + 0.125, this.getZ() - 0.1875, this.getX() + 0.1875, this.getY() + 0.25 + 0.125, this.getZ() + 0.1875));
        this.teleporting = true;
    }

    @Override
    public void updatePosition(double d, double e, double f) {
        super.updatePosition((double)MathHelper.floor(d) + 0.5, (double)MathHelper.floor(e) + 0.5, (double)MathHelper.floor(f) + 0.5);
    }

    @Override
    protected void updateAttachmentPosition() {
        this.setPos((double)this.attachmentPos.getX() + 0.5, (double)this.attachmentPos.getY() + 0.5, (double)this.attachmentPos.getZ() + 0.5);
    }

    @Override
    public void setFacing(Direction arg) {
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    protected float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return -0.0625f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        return d < 1024.0;
    }

    @Override
    public void onBreak(@Nullable Entity arg) {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
    }

    @Override
    public boolean interact(PlayerEntity arg, Hand arg2) {
        if (this.world.isClient) {
            return true;
        }
        boolean bl = false;
        double d = 7.0;
        List<MobEntity> list = this.world.getNonSpectatingEntities(MobEntity.class, new Box(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0));
        for (MobEntity lv : list) {
            if (lv.getHoldingEntity() != arg) continue;
            lv.attachLeash(this, true);
            bl = true;
        }
        if (!bl) {
            this.remove();
            if (arg.abilities.creativeMode) {
                for (MobEntity lv2 : list) {
                    if (!lv2.isLeashed() || lv2.getHoldingEntity() != this) continue;
                    lv2.detachLeash(true, false);
                }
            }
        }
        return true;
    }

    @Override
    public boolean canStayAttached() {
        return this.world.getBlockState(this.attachmentPos).getBlock().isIn(BlockTags.FENCES);
    }

    public static LeashKnotEntity getOrCreate(World arg, BlockPos arg2) {
        int i = arg2.getX();
        int j = arg2.getY();
        int k = arg2.getZ();
        List<LeashKnotEntity> list = arg.getNonSpectatingEntities(LeashKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0));
        for (LeashKnotEntity lv : list) {
            if (!lv.getDecorationBlockPos().equals(arg2)) continue;
            return lv;
        }
        LeashKnotEntity lv2 = new LeashKnotEntity(arg, arg2);
        arg.spawnEntity(lv2);
        lv2.onPlace();
        return lv2;
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), 0, this.getDecorationBlockPos());
    }
}

