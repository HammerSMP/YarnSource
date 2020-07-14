/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.NameGenerator;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VillageDebugRenderer
implements DebugRenderer.Renderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final Map<BlockPos, PointOfInterest> pointsOfInterest = Maps.newHashMap();
    private final Map<UUID, Brain> brains = Maps.newHashMap();
    @Nullable
    private UUID targetedEntity;

    public VillageDebugRenderer(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void clear() {
        this.pointsOfInterest.clear();
        this.brains.clear();
        this.targetedEntity = null;
    }

    public void addPointOfInterest(PointOfInterest arg) {
        this.pointsOfInterest.put(arg.pos, arg);
    }

    public void removePointOfInterest(BlockPos arg) {
        this.pointsOfInterest.remove(arg);
    }

    public void setFreeTicketCount(BlockPos pos, int freeTicketCount) {
        PointOfInterest lv = this.pointsOfInterest.get(pos);
        if (lv == null) {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + pos);
            return;
        }
        lv.freeTicketCount = freeTicketCount;
    }

    public void addBrain(Brain brain) {
        this.brains.put(brain.uuid, brain);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.method_24805();
        this.method_23135(cameraX, cameraY, cameraZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.client.player.isSpectator()) {
            this.updateTargetedEntity();
        }
    }

    private void method_24805() {
        this.brains.entrySet().removeIf(entry -> {
            Entity lv = this.client.world.getEntityById(((Brain)entry.getValue()).field_18924);
            return lv == null || lv.removed;
        });
    }

    private void method_23135(double d, double e, double f) {
        BlockPos lv = new BlockPos(d, e, f);
        this.brains.values().forEach(arg -> {
            if (this.isClose((Brain)arg)) {
                this.drawBrain((Brain)arg, d, e, f);
            }
        });
        for (BlockPos lv2 : this.pointsOfInterest.keySet()) {
            if (!lv.isWithinDistance(lv2, 30.0)) continue;
            VillageDebugRenderer.drawPointOfInterest(lv2);
        }
        this.pointsOfInterest.values().forEach(arg2 -> {
            if (lv.isWithinDistance(arg2.pos, 30.0)) {
                this.drawPointOfInterestInfo((PointOfInterest)arg2);
            }
        });
        this.getGhostPointsOfInterest().forEach((arg2, list) -> {
            if (lv.isWithinDistance((Vec3i)arg2, 30.0)) {
                this.drawGhostPointOfInterest((BlockPos)arg2, (List<String>)list);
            }
        });
    }

    private static void drawPointOfInterest(BlockPos pos) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void drawGhostPointOfInterest(BlockPos pos, List<String> brains) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        VillageDebugRenderer.drawString("" + brains, pos, 0, -256);
        VillageDebugRenderer.drawString("Ghost POI", pos, 1, -65536);
    }

    private void drawPointOfInterestInfo(PointOfInterest pointOfInterest) {
        int i = 0;
        Set<String> set = this.getVillagerNames(pointOfInterest);
        if (set.size() < 4) {
            VillageDebugRenderer.drawString("Owners: " + set, pointOfInterest, i, -256);
        } else {
            VillageDebugRenderer.drawString("" + set.size() + " ticket holders", pointOfInterest, i, -256);
        }
        ++i;
        Set<String> set2 = this.method_29385(pointOfInterest);
        if (set2.size() < 4) {
            VillageDebugRenderer.drawString("Candidates: " + set2, pointOfInterest, i, -23296);
        } else {
            VillageDebugRenderer.drawString("" + set2.size() + " potential owners", pointOfInterest, i, -23296);
        }
        VillageDebugRenderer.drawString("Free tickets: " + pointOfInterest.freeTicketCount, pointOfInterest, ++i, -256);
        VillageDebugRenderer.drawString(pointOfInterest.field_18932, pointOfInterest, ++i, -1);
    }

    private void drawPath(Brain brain, double cameraX, double cameraY, double cameraZ) {
        if (brain.path != null) {
            PathfindingDebugRenderer.drawPath(brain.path, 0.5f, false, false, cameraX, cameraY, cameraZ);
        }
    }

    private void drawBrain(Brain brain, double cameraX, double cameraY, double cameraZ) {
        boolean bl = this.isTargeted(brain);
        int i = 0;
        VillageDebugRenderer.drawString(brain.pos, i, brain.field_19328, -1, 0.03f);
        ++i;
        if (bl) {
            VillageDebugRenderer.drawString(brain.pos, i, brain.profession + " " + brain.xp + " xp", -1, 0.02f);
            ++i;
        }
        if (bl) {
            int j = brain.field_22406 < brain.field_22407 ? -23296 : -1;
            VillageDebugRenderer.drawString(brain.pos, i, "health: " + String.format("%.1f", Float.valueOf(brain.field_22406)) + " / " + String.format("%.1f", Float.valueOf(brain.field_22407)), j, 0.02f);
            ++i;
        }
        if (bl && !brain.field_19372.equals("")) {
            VillageDebugRenderer.drawString(brain.pos, i, brain.field_19372, -98404, 0.02f);
            ++i;
        }
        if (bl) {
            for (String string : brain.field_18928) {
                VillageDebugRenderer.drawString(brain.pos, i, string, -16711681, 0.02f);
                ++i;
            }
        }
        if (bl) {
            for (String string2 : brain.field_18927) {
                VillageDebugRenderer.drawString(brain.pos, i, string2, -16711936, 0.02f);
                ++i;
            }
        }
        if (brain.wantsGolem) {
            VillageDebugRenderer.drawString(brain.pos, i, "Wants Golem", -23296, 0.02f);
            ++i;
        }
        if (bl) {
            for (String string3 : brain.field_19375) {
                if (string3.startsWith(brain.field_19328)) {
                    VillageDebugRenderer.drawString(brain.pos, i, string3, -1, 0.02f);
                } else {
                    VillageDebugRenderer.drawString(brain.pos, i, string3, -23296, 0.02f);
                }
                ++i;
            }
        }
        if (bl) {
            for (String string4 : Lists.reverse(brain.field_19374)) {
                VillageDebugRenderer.drawString(brain.pos, i, string4, -3355444, 0.02f);
                ++i;
            }
        }
        if (bl) {
            this.drawPath(brain, cameraX, cameraY, cameraZ);
        }
    }

    private static void drawString(String string, PointOfInterest pointOfInterest, int offsetY, int color) {
        BlockPos lv = pointOfInterest.pos;
        VillageDebugRenderer.drawString(string, lv, offsetY, color);
    }

    private static void drawString(String string, BlockPos pos, int offsetY, int color) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)pos.getX() + 0.5;
        double g = (double)pos.getY() + 1.3 + (double)offsetY * 0.2;
        double h = (double)pos.getZ() + 0.5;
        DebugRenderer.drawString(string, f, g, h, color, 0.02f, true, 0.0f, true);
    }

    private static void drawString(Position pos, int offsetY, String string, int color, float size) {
        double d = 2.4;
        double e = 0.25;
        BlockPos lv = new BlockPos(pos);
        double g = (double)lv.getX() + 0.5;
        double h = pos.getY() + 2.4 + (double)offsetY * 0.25;
        double k = (double)lv.getZ() + 0.5;
        float l = 0.5f;
        DebugRenderer.drawString(string, g, h, k, color, size, false, 0.5f, true);
    }

    private Set<String> getVillagerNames(PointOfInterest pointOfInterest) {
        return this.getBrains(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
    }

    private Set<String> method_29385(PointOfInterest arg) {
        return this.method_29386(arg.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
    }

    private boolean isTargeted(Brain brain) {
        return Objects.equals(this.targetedEntity, brain.uuid);
    }

    private boolean isClose(Brain brain) {
        ClientPlayerEntity lv = this.client.player;
        BlockPos lv2 = new BlockPos(lv.getX(), brain.pos.getY(), lv.getZ());
        BlockPos lv3 = new BlockPos(brain.pos);
        return lv2.isWithinDistance(lv3, 30.0);
    }

    private Collection<UUID> getBrains(BlockPos pointOfInterest) {
        return this.brains.values().stream().filter(arg2 -> ((Brain)arg2).isPointOfInterest(pointOfInterest)).map(Brain::getUuid).collect(Collectors.toSet());
    }

    private Collection<UUID> method_29386(BlockPos arg) {
        return this.brains.values().stream().filter(arg2 -> ((Brain)arg2).method_29388(arg)).map(Brain::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
        HashMap map = Maps.newHashMap();
        for (Brain lv : this.brains.values()) {
            for (BlockPos lv2 : Iterables.concat(lv.pointsOfInterest, lv.field_25287)) {
                if (this.pointsOfInterest.containsKey(lv2)) continue;
                map.computeIfAbsent(lv2, arg -> Lists.newArrayList()).add(lv.field_19328);
            }
        }
        return map;
    }

    private void updateTargetedEntity() {
        DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(arg -> {
            this.targetedEntity = arg.getUuid();
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class Brain {
        public final UUID uuid;
        public final int field_18924;
        public final String field_19328;
        public final String profession;
        public final int xp;
        public final float field_22406;
        public final float field_22407;
        public final Position pos;
        public final String field_19372;
        public final Path path;
        public final boolean wantsGolem;
        public final List<String> field_18927 = Lists.newArrayList();
        public final List<String> field_18928 = Lists.newArrayList();
        public final List<String> field_19374 = Lists.newArrayList();
        public final List<String> field_19375 = Lists.newArrayList();
        public final Set<BlockPos> pointsOfInterest = Sets.newHashSet();
        public final Set<BlockPos> field_25287 = Sets.newHashSet();

        public Brain(UUID uUID, int i, String string, String profession, int xp, float f, float g, Position arg, String string3, @Nullable Path arg2, boolean bl) {
            this.uuid = uUID;
            this.field_18924 = i;
            this.field_19328 = string;
            this.profession = profession;
            this.xp = xp;
            this.field_22406 = f;
            this.field_22407 = g;
            this.pos = arg;
            this.field_19372 = string3;
            this.path = arg2;
            this.wantsGolem = bl;
        }

        private boolean isPointOfInterest(BlockPos arg) {
            return this.pointsOfInterest.stream().anyMatch(arg::equals);
        }

        private boolean method_29388(BlockPos arg) {
            return this.field_25287.contains(arg);
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class PointOfInterest {
        public final BlockPos pos;
        public String field_18932;
        public int freeTicketCount;

        public PointOfInterest(BlockPos arg, String string, int i) {
            this.pos = arg;
            this.field_18932 = string;
            this.freeTicketCount = i;
        }
    }
}

