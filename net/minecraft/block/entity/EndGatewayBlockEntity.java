/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayBlockEntity
extends EndPortalBlockEntity
implements Tickable {
    private static final Logger LOGGER = LogManager.getLogger();
    private long age;
    private int teleportCooldown;
    @Nullable
    private BlockPos exitPortalPos;
    private boolean exactTeleport;

    public EndGatewayBlockEntity() {
        super(BlockEntityType.END_GATEWAY);
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        arg.putLong("Age", this.age);
        if (this.exitPortalPos != null) {
            arg.put("ExitPortal", NbtHelper.fromBlockPos(this.exitPortalPos));
        }
        if (this.exactTeleport) {
            arg.putBoolean("ExactTeleport", this.exactTeleport);
        }
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.age = arg2.getLong("Age");
        if (arg2.contains("ExitPortal", 10)) {
            this.exitPortalPos = NbtHelper.toBlockPos(arg2.getCompound("ExitPortal"));
        }
        this.exactTeleport = arg2.getBoolean("ExactTeleport");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 256.0;
    }

    @Override
    public void tick() {
        boolean bl = this.isRecentlyGenerated();
        boolean bl2 = this.needsCooldownBeforeTeleporting();
        ++this.age;
        if (bl2) {
            --this.teleportCooldown;
        } else if (!this.world.isClient) {
            List<Entity> list = this.world.getNonSpectatingEntities(Entity.class, new Box(this.getPos()));
            if (!list.isEmpty()) {
                this.tryTeleportingEntity(list.get(this.world.random.nextInt(list.size())));
            }
            if (this.age % 2400L == 0L) {
                this.startTeleportCooldown();
            }
        }
        if (bl != this.isRecentlyGenerated() || bl2 != this.needsCooldownBeforeTeleporting()) {
            this.markDirty();
        }
    }

    public boolean isRecentlyGenerated() {
        return this.age < 200L;
    }

    public boolean needsCooldownBeforeTeleporting() {
        return this.teleportCooldown > 0;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRecentlyGeneratedBeamHeight(float f) {
        return MathHelper.clamp(((float)this.age + f) / 200.0f, 0.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public float getCooldownBeamHeight(float f) {
        return 1.0f - MathHelper.clamp(((float)this.teleportCooldown - f) / 40.0f, 0.0f, 1.0f);
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 8, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public void startTeleportCooldown() {
        if (!this.world.isClient) {
            this.teleportCooldown = 40;
            this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, 0);
            this.markDirty();
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int i, int j) {
        if (i == 1) {
            this.teleportCooldown = 40;
            return true;
        }
        return super.onSyncedBlockEvent(i, j);
    }

    public void tryTeleportingEntity(Entity arg) {
        if (!(this.world instanceof ServerWorld) || this.needsCooldownBeforeTeleporting()) {
            return;
        }
        this.teleportCooldown = 100;
        if (this.exitPortalPos == null && this.world.getRegistryKey() == World.END) {
            this.createPortal((ServerWorld)this.world);
        }
        if (this.exitPortalPos != null) {
            Entity lv5;
            BlockPos lv;
            BlockPos blockPos = lv = this.exactTeleport ? this.exitPortalPos : this.findBestPortalExitPos();
            if (arg instanceof EnderPearlEntity) {
                Entity lv2 = ((EnderPearlEntity)arg).getOwner();
                if (lv2 instanceof ServerPlayerEntity) {
                    Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)lv2, this.world.getBlockState(this.getPos()));
                }
                if (lv2 != null) {
                    Entity lv3 = lv2;
                    arg.remove();
                } else {
                    Entity lv4 = arg;
                }
            } else {
                lv5 = arg.getRootVehicle();
            }
            lv5.teleport((double)lv.getX() + 0.5, lv.getY(), (double)lv.getZ() + 0.5);
        }
        this.startTeleportCooldown();
    }

    private BlockPos findBestPortalExitPos() {
        BlockPos lv = EndGatewayBlockEntity.findExitPortalPos(this.world, this.exitPortalPos, 5, false);
        LOGGER.debug("Best exit position for portal at {} is {}", (Object)this.exitPortalPos, (Object)lv);
        return lv.up();
    }

    private void createPortal(ServerWorld arg) {
        Vec3d lv = new Vec3d(this.getPos().getX(), 0.0, this.getPos().getZ()).normalize();
        Vec3d lv2 = lv.multiply(1024.0);
        int i = 16;
        while (EndGatewayBlockEntity.getChunk(arg, lv2).getHighestNonEmptySectionYOffset() > 0 && i-- > 0) {
            LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)lv2);
            lv2 = lv2.add(lv.multiply(-16.0));
        }
        i = 16;
        while (EndGatewayBlockEntity.getChunk(arg, lv2).getHighestNonEmptySectionYOffset() == 0 && i-- > 0) {
            LOGGER.debug("Skipping forward past empty chunk at {}", (Object)lv2);
            lv2 = lv2.add(lv.multiply(16.0));
        }
        LOGGER.debug("Found chunk at {}", (Object)lv2);
        WorldChunk lv3 = EndGatewayBlockEntity.getChunk(arg, lv2);
        this.exitPortalPos = EndGatewayBlockEntity.findPortalPosition(lv3);
        if (this.exitPortalPos == null) {
            this.exitPortalPos = new BlockPos(lv2.x + 0.5, 75.0, lv2.z + 0.5);
            LOGGER.debug("Failed to find suitable block, settling on {}", (Object)this.exitPortalPos);
            Feature.END_ISLAND.configure(FeatureConfig.DEFAULT).generate(arg, arg.getStructureAccessor(), arg.getChunkManager().getChunkGenerator(), new Random(this.exitPortalPos.asLong()), this.exitPortalPos);
        } else {
            LOGGER.debug("Found block at {}", (Object)this.exitPortalPos);
        }
        this.exitPortalPos = EndGatewayBlockEntity.findExitPortalPos(arg, this.exitPortalPos, 16, true);
        LOGGER.debug("Creating portal at {}", (Object)this.exitPortalPos);
        this.exitPortalPos = this.exitPortalPos.up(10);
        this.createPortal(arg, this.exitPortalPos);
        this.markDirty();
    }

    private static BlockPos findExitPortalPos(BlockView arg, BlockPos arg2, int i, boolean bl) {
        Vec3i lv = null;
        for (int j = -i; j <= i; ++j) {
            block1: for (int k = -i; k <= i; ++k) {
                if (j == 0 && k == 0 && !bl) continue;
                for (int l = 255; l > (lv == null ? 0 : lv.getY()); --l) {
                    BlockPos lv2 = new BlockPos(arg2.getX() + j, l, arg2.getZ() + k);
                    BlockState lv3 = arg.getBlockState(lv2);
                    if (!lv3.isFullCube(arg, lv2) || !bl && lv3.isOf(Blocks.BEDROCK)) continue;
                    lv = lv2;
                    continue block1;
                }
            }
        }
        return lv == null ? arg2 : lv;
    }

    private static WorldChunk getChunk(World arg, Vec3d arg2) {
        return arg.getChunk(MathHelper.floor(arg2.x / 16.0), MathHelper.floor(arg2.z / 16.0));
    }

    @Nullable
    private static BlockPos findPortalPosition(WorldChunk arg) {
        ChunkPos lv = arg.getPos();
        BlockPos lv2 = new BlockPos(lv.getStartX(), 30, lv.getStartZ());
        int i = arg.getHighestNonEmptySectionYOffset() + 16 - 1;
        BlockPos lv3 = new BlockPos(lv.getEndX(), i, lv.getEndZ());
        BlockPos lv4 = null;
        double d = 0.0;
        for (BlockPos lv5 : BlockPos.iterate(lv2, lv3)) {
            BlockState lv6 = arg.getBlockState(lv5);
            BlockPos lv7 = lv5.up();
            BlockPos lv8 = lv5.up(2);
            if (!lv6.isOf(Blocks.END_STONE) || arg.getBlockState(lv7).isFullCube(arg, lv7) || arg.getBlockState(lv8).isFullCube(arg, lv8)) continue;
            double e = lv5.getSquaredDistance(0.0, 0.0, 0.0, true);
            if (lv4 != null && !(e < d)) continue;
            lv4 = lv5;
            d = e;
        }
        return lv4;
    }

    private void createPortal(ServerWorld arg, BlockPos arg2) {
        Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig(this.getPos(), false)).generate(arg, arg.getStructureAccessor(), arg.getChunkManager().getChunkGenerator(), new Random(), arg2);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldDrawSide(Direction arg) {
        return Block.shouldDrawSide(this.getCachedState(), this.world, this.getPos(), arg);
    }

    @Environment(value=EnvType.CLIENT)
    public int getDrawnSidesCount() {
        int i = 0;
        for (Direction lv : Direction.values()) {
            i += this.shouldDrawSide(lv) ? 1 : 0;
        }
        return i;
    }

    public void setExitPortalPos(BlockPos arg, boolean bl) {
        this.exactTeleport = bl;
        this.exitPortalPos = arg;
    }
}

