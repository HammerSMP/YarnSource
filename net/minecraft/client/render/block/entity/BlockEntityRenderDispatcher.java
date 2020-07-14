/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
    private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.newHashMap();
    public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
    private final BufferBuilder bufferBuilder = new BufferBuilder(256);
    private TextRenderer textRenderer;
    public TextureManager textureManager;
    public World world;
    public Camera camera;
    public HitResult crosshairTarget;

    private BlockEntityRenderDispatcher() {
        this.register(BlockEntityType.SIGN, new SignBlockEntityRenderer(this));
        this.register(BlockEntityType.MOB_SPAWNER, new MobSpawnerBlockEntityRenderer(this));
        this.register(BlockEntityType.PISTON, new PistonBlockEntityRenderer(this));
        this.register(BlockEntityType.CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.ENDER_CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.TRAPPED_CHEST, new ChestBlockEntityRenderer(this));
        this.register(BlockEntityType.ENCHANTING_TABLE, new EnchantingTableBlockEntityRenderer(this));
        this.register(BlockEntityType.LECTERN, new LecternBlockEntityRenderer(this));
        this.register(BlockEntityType.END_PORTAL, new EndPortalBlockEntityRenderer(this));
        this.register(BlockEntityType.END_GATEWAY, new EndGatewayBlockEntityRenderer(this));
        this.register(BlockEntityType.BEACON, new BeaconBlockEntityRenderer(this));
        this.register(BlockEntityType.SKULL, new SkullBlockEntityRenderer(this));
        this.register(BlockEntityType.BANNER, new BannerBlockEntityRenderer(this));
        this.register(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockBlockEntityRenderer(this));
        this.register(BlockEntityType.SHULKER_BOX, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel(), this));
        this.register(BlockEntityType.BED, new BedBlockEntityRenderer(this));
        this.register(BlockEntityType.CONDUIT, new ConduitBlockEntityRenderer(this));
        this.register(BlockEntityType.BELL, new BellBlockEntityRenderer(this));
        this.register(BlockEntityType.CAMPFIRE, new CampfireBlockEntityRenderer(this));
    }

    private <E extends BlockEntity> void register(BlockEntityType<E> arg, BlockEntityRenderer<E> arg2) {
        this.renderers.put(arg, arg2);
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> get(E arg) {
        return this.renderers.get(arg.getType());
    }

    public void configure(World world, TextureManager textureManager, TextRenderer textRenderer, Camera camera, HitResult crosshairTarget) {
        if (this.world != world) {
            this.setWorld(world);
        }
        this.textureManager = textureManager;
        this.camera = camera;
        this.textRenderer = textRenderer;
        this.crosshairTarget = crosshairTarget;
    }

    public <E extends BlockEntity> void render(E blockEntity, float tickDelta, MatrixStack matrix, VertexConsumerProvider arg3) {
        if (!Vec3d.ofCenter(blockEntity.getPos()).isInRange(this.camera.getPos(), blockEntity.getSquaredRenderDistance())) {
            return;
        }
        BlockEntityRenderer lv = this.get(blockEntity);
        if (lv == null) {
            return;
        }
        if (!blockEntity.hasWorld() || !blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
            return;
        }
        BlockEntityRenderDispatcher.runReported(blockEntity, () -> BlockEntityRenderDispatcher.render(lv, blockEntity, tickDelta, matrix, arg3));
    }

    private static <T extends BlockEntity> void render(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        int j;
        World lv = blockEntity.getWorld();
        if (lv != null) {
            int i = WorldRenderer.getLightmapCoordinates(lv, blockEntity.getPos());
        } else {
            j = 0xF000F0;
        }
        renderer.render(blockEntity, tickDelta, matrices, vertexConsumers, j, OverlayTexture.DEFAULT_UV);
    }

    public <E extends BlockEntity> boolean renderEntity(E entity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        BlockEntityRenderer lv = this.get(entity);
        if (lv == null) {
            return true;
        }
        BlockEntityRenderDispatcher.runReported(entity, () -> lv.render(entity, 0.0f, matrix, vertexConsumerProvider, light, overlay));
        return false;
    }

    private static void runReported(BlockEntity arg, Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Rendering Block Entity");
            CrashReportSection lv2 = lv.addElement("Block Entity Details");
            arg.populateCrashReport(lv2);
            throw new CrashException(lv);
        }
    }

    public void setWorld(@Nullable World arg) {
        this.world = arg;
        if (arg == null) {
            this.camera = null;
        }
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}

