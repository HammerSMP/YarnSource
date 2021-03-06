/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;

public class SkullBlockEntity
extends BlockEntity
implements Tickable {
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private GameProfile owner;
    private int ticksPowered;
    private boolean powered;

    public SkullBlockEntity() {
        super(BlockEntityType.SKULL);
    }

    public static void setUserCache(UserCache value) {
        userCache = value;
    }

    public static void setSessionService(MinecraftSessionService value) {
        sessionService = value;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.owner != null) {
            CompoundTag lv = new CompoundTag();
            NbtHelper.fromGameProfile(lv, this.owner);
            tag.put("SkullOwner", lv);
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        String string;
        super.fromTag(state, tag);
        if (tag.contains("SkullOwner", 10)) {
            this.setOwnerAndType(NbtHelper.toGameProfile(tag.getCompound("SkullOwner")));
        } else if (tag.contains("ExtraType", 8) && !ChatUtil.isEmpty(string = tag.getString("ExtraType"))) {
            this.setOwnerAndType(new GameProfile(null, string));
        }
    }

    @Override
    public void tick() {
        BlockState lv = this.getCachedState();
        if (lv.isOf(Blocks.DRAGON_HEAD) || lv.isOf(Blocks.DRAGON_WALL_HEAD)) {
            if (this.world.isReceivingRedstonePower(this.pos)) {
                this.powered = true;
                ++this.ticksPowered;
            } else {
                this.powered = false;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getTicksPowered(float tickDelta) {
        if (this.powered) {
            return (float)this.ticksPowered + tickDelta;
        }
        return this.ticksPowered;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 4, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public void setOwnerAndType(@Nullable GameProfile gameProfile) {
        this.owner = gameProfile;
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        this.owner = SkullBlockEntity.loadProperties(this.owner);
        this.markDirty();
    }

    @Nullable
    public static GameProfile loadProperties(@Nullable GameProfile profile) {
        if (profile == null || ChatUtil.isEmpty(profile.getName())) {
            return profile;
        }
        if (profile.isComplete() && profile.getProperties().containsKey((Object)"textures")) {
            return profile;
        }
        if (userCache == null || sessionService == null) {
            return profile;
        }
        GameProfile gameProfile2 = userCache.findByName(profile.getName());
        if (gameProfile2 == null) {
            return profile;
        }
        Property property = (Property)Iterables.getFirst((Iterable)gameProfile2.getProperties().get((Object)"textures"), null);
        if (property == null) {
            gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
        }
        return gameProfile2;
    }
}

