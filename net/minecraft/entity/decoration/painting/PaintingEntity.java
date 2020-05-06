/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PaintingEntity
extends AbstractDecorationEntity {
    public PaintingMotive motive;

    public PaintingEntity(EntityType<? extends PaintingEntity> arg, World arg2) {
        super((EntityType<? extends AbstractDecorationEntity>)arg, arg2);
    }

    public PaintingEntity(World arg, BlockPos arg2, Direction arg3) {
        super(EntityType.PAINTING, arg, arg2);
        ArrayList list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = Registry.PAINTING_MOTIVE.iterator();
        while (iterator.hasNext()) {
            PaintingMotive lv;
            this.motive = lv = (PaintingMotive)iterator.next();
            this.setFacing(arg3);
            if (!this.canStayAttached()) continue;
            list.add(lv);
            int j = lv.getWidth() * lv.getHeight();
            if (j <= i) continue;
            i = j;
        }
        if (!list.isEmpty()) {
            Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                PaintingMotive lv2 = (PaintingMotive)iterator2.next();
                if (lv2.getWidth() * lv2.getHeight() >= i) continue;
                iterator2.remove();
            }
            this.motive = (PaintingMotive)list.get(this.random.nextInt(list.size()));
        }
        this.setFacing(arg3);
    }

    @Environment(value=EnvType.CLIENT)
    public PaintingEntity(World arg, BlockPos arg2, Direction arg3, PaintingMotive arg4) {
        this(arg, arg2, arg3);
        this.motive = arg4;
        this.setFacing(arg3);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        arg.putString("Motive", Registry.PAINTING_MOTIVE.getId(this.motive).toString());
        super.writeCustomDataToTag(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.motive = Registry.PAINTING_MOTIVE.get(Identifier.tryParse(arg.getString("Motive")));
        super.readCustomDataFromTag(arg);
    }

    @Override
    public int getWidthPixels() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getWidth();
    }

    @Override
    public int getHeightPixels() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getHeight();
    }

    @Override
    public void onBreak(@Nullable Entity arg) {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
        if (arg instanceof PlayerEntity) {
            PlayerEntity lv = (PlayerEntity)arg;
            if (lv.abilities.creativeMode) {
                return;
            }
        }
        this.dropItem(Items.PAINTING);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void refreshPositionAndAngles(double d, double e, double f, float g, float h) {
        this.updatePosition(d, e, f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        BlockPos lv = this.attachmentPos.add(d - this.getX(), e - this.getY(), f - this.getZ());
        this.updatePosition(lv.getX(), lv.getY(), lv.getZ());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new PaintingSpawnS2CPacket(this);
    }
}

