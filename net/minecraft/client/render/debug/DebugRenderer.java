/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.CaveDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.CollisionDebugRenderer;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    public final PathfindingDebugRenderer pathfindingDebugRenderer = new PathfindingDebugRenderer();
    public final Renderer waterDebugRenderer;
    public final Renderer chunkBorderDebugRenderer;
    public final Renderer heightmapDebugRenderer;
    public final Renderer collisionDebugRenderer;
    public final Renderer neighborUpdateDebugRenderer;
    public final CaveDebugRenderer caveDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
    public final VillageDebugRenderer villageDebugRenderer;
    public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    public final BeeDebugRenderer beeDebugRenderer;
    public final RaidCenterDebugRenderer raidCenterDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    private boolean showChunkBorder;

    public DebugRenderer(MinecraftClient arg) {
        this.waterDebugRenderer = new WaterDebugRenderer(arg);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(arg);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(arg);
        this.collisionDebugRenderer = new CollisionDebugRenderer(arg);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(arg);
        this.caveDebugRenderer = new CaveDebugRenderer();
        this.structureDebugRenderer = new StructureDebugRenderer(arg);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(arg);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer();
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(arg);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(arg);
        this.villageDebugRenderer = new VillageDebugRenderer(arg);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer(arg);
        this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(arg);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(arg);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
    }

    public void reset() {
        this.pathfindingDebugRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderDebugRenderer.clear();
        this.heightmapDebugRenderer.clear();
        this.collisionDebugRenderer.clear();
        this.neighborUpdateDebugRenderer.clear();
        this.caveDebugRenderer.clear();
        this.structureDebugRenderer.clear();
        this.skyLightDebugRenderer.clear();
        this.worldGenAttemptDebugRenderer.clear();
        this.blockOutlineDebugRenderer.clear();
        this.chunkLoadingDebugRenderer.clear();
        this.villageDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidCenterDebugRenderer.clear();
        this.goalSelectorDebugRenderer.clear();
        this.gameTestDebugRenderer.clear();
    }

    public boolean toggleShowChunkBorder() {
        this.showChunkBorder = !this.showChunkBorder;
        return this.showChunkBorder;
    }

    public void render(MatrixStack arg, VertexConsumerProvider.Immediate arg2, double d, double e, double f) {
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(arg, arg2, d, e, f);
        }
        this.gameTestDebugRenderer.render(arg, arg2, d, e, f);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity arg2, int i) {
        int j;
        Predicate<Entity> predicate;
        Box lv4;
        Vec3d lv2;
        Vec3d lv3;
        if (arg2 == null) {
            return Optional.empty();
        }
        Vec3d lv = arg2.getCameraPosVec(1.0f);
        EntityHitResult lv5 = ProjectileUtil.rayTrace(arg2, lv, lv3 = lv.add(lv2 = arg2.getRotationVec(1.0f).multiply(i)), lv4 = arg2.getBoundingBox().stretch(lv2).expand(1.0), predicate = arg -> !arg.isSpectator() && arg.collides(), j = i * i);
        if (lv5 == null) {
            return Optional.empty();
        }
        if (lv.squaredDistanceTo(lv5.getPos()) > (double)j) {
            return Optional.empty();
        }
        return Optional.of(lv5.getEntity());
    }

    public static void drawBox(BlockPos arg, BlockPos arg2, float f, float g, float h, float i) {
        Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!lv.isReady()) {
            return;
        }
        Vec3d lv2 = lv.getPos().negate();
        Box lv3 = new Box(arg, arg2).offset(lv2);
        DebugRenderer.drawBox(lv3, f, g, h, i);
    }

    public static void drawBox(BlockPos arg, float f, float g, float h, float i, float j) {
        Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!lv.isReady()) {
            return;
        }
        Vec3d lv2 = lv.getPos().negate();
        Box lv3 = new Box(arg).offset(lv2).expand(f);
        DebugRenderer.drawBox(lv3, g, h, i, j);
    }

    public static void drawBox(Box arg, float f, float g, float h, float i) {
        DebugRenderer.drawBox(arg.minX, arg.minY, arg.minZ, arg.maxX, arg.maxY, arg.maxZ, f, g, h, i);
    }

    public static void drawBox(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(5, VertexFormats.POSITION_COLOR);
        WorldRenderer.drawBox(lv2, d, e, f, g, h, i, j, k, l, m);
        lv.draw();
    }

    public static void drawString(String string, int i, int j, int k, int l) {
        DebugRenderer.drawString(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
    }

    public static void drawString(String string, double d, double e, double f, int i) {
        DebugRenderer.drawString(string, d, e, f, i, 0.02f);
    }

    public static void drawString(String string, double d, double e, double f, int i, float g) {
        DebugRenderer.drawString(string, d, e, f, i, g, true, 0.0f, false);
    }

    public static void drawString(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
        MinecraftClient lv = MinecraftClient.getInstance();
        Camera lv2 = lv.gameRenderer.getCamera();
        if (!lv2.isReady() || lv.getEntityRenderManager().gameOptions == null) {
            return;
        }
        TextRenderer lv3 = lv.textRenderer;
        double j = lv2.getPos().x;
        double k = lv2.getPos().y;
        double l = lv2.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)(d - j), (float)(e - k) + 0.07f, (float)(f - l));
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.multMatrix(new Matrix4f(lv2.getRotation()));
        RenderSystem.scalef(g, -g, g);
        RenderSystem.enableTexture();
        if (bl2) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        RenderSystem.scalef(-1.0f, 1.0f, 1.0f);
        float m = bl ? (float)(-lv3.getWidth(string)) / 2.0f : 0.0f;
        RenderSystem.enableAlphaTest();
        VertexConsumerProvider.Immediate lv4 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        lv3.draw(string, m -= h / g, 0.0f, i, false, AffineTransformation.identity().getMatrix(), (VertexConsumerProvider)lv4, bl2, 0, 0xF000F0);
        lv4.draw();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Renderer {
        public void render(MatrixStack var1, VertexConsumerProvider var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }
}

