/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
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

    public void setFreeTicketCount(BlockPos arg, int i) {
        PointOfInterest lv = this.pointsOfInterest.get(arg);
        if (lv == null) {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + arg);
            return;
        }
        lv.freeTicketCount = i;
    }

    public void addBrain(Brain arg) {
        this.brains.put(arg.uuid, arg);
    }

    @Override
    public void render(MatrixStack arg, VertexConsumerProvider arg2, double d, double e, double f) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.method_24805();
        this.method_23135(d, e, f);
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

    private static void drawPointOfInterest(BlockPos arg) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(arg, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void drawGhostPointOfInterest(BlockPos arg, List<String> list) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(arg, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        VillageDebugRenderer.drawString("" + list, arg, 0, -256);
        VillageDebugRenderer.drawString("Ghost POI", arg, 1, -65536);
    }

    private void drawPointOfInterestInfo(PointOfInterest arg) {
        int i = 0;
        if (this.getVillagerNames(arg).size() < 4) {
            VillageDebugRenderer.drawString("" + this.getVillagerNames(arg), arg, i, -256);
        } else {
            VillageDebugRenderer.drawString("" + this.getVillagerNames(arg).size() + " ticket holders", arg, i, -256);
        }
        VillageDebugRenderer.drawString("Free tickets: " + arg.freeTicketCount, arg, ++i, -256);
        VillageDebugRenderer.drawString(arg.field_18932, arg, ++i, -1);
    }

    private void drawPath(Brain arg, double d, double e, double f) {
        if (arg.path != null) {
            PathfindingDebugRenderer.drawPath(arg.path, 0.5f, false, false, d, e, f);
        }
    }

    private void drawBrain(Brain arg, double d, double e, double f) {
        boolean bl = this.isTargeted(arg);
        int i = 0;
        VillageDebugRenderer.drawString(arg.pos, i, arg.field_19328, -1, 0.03f);
        ++i;
        if (bl) {
            VillageDebugRenderer.drawString(arg.pos, i, arg.profession + " " + arg.xp + " xp", -1, 0.02f);
            ++i;
        }
        if (bl) {
            int j = arg.field_22406 < arg.field_22407 ? -23296 : -1;
            VillageDebugRenderer.drawString(arg.pos, i, "health: " + String.format("%.1f", Float.valueOf(arg.field_22406)) + " / " + String.format("%.1f", Float.valueOf(arg.field_22407)), j, 0.02f);
            ++i;
        }
        if (bl && !arg.field_19372.equals("")) {
            VillageDebugRenderer.drawString(arg.pos, i, arg.field_19372, -98404, 0.02f);
            ++i;
        }
        if (bl) {
            for (String string : arg.field_18928) {
                VillageDebugRenderer.drawString(arg.pos, i, string, -16711681, 0.02f);
                ++i;
            }
        }
        if (bl) {
            for (String string2 : arg.field_18927) {
                VillageDebugRenderer.drawString(arg.pos, i, string2, -16711936, 0.02f);
                ++i;
            }
        }
        if (arg.wantsGolem) {
            VillageDebugRenderer.drawString(arg.pos, i, "Wants Golem", -23296, 0.02f);
            ++i;
        }
        if (bl) {
            for (String string3 : arg.field_19375) {
                if (string3.startsWith(arg.field_19328)) {
                    VillageDebugRenderer.drawString(arg.pos, i, string3, -1, 0.02f);
                } else {
                    VillageDebugRenderer.drawString(arg.pos, i, string3, -23296, 0.02f);
                }
                ++i;
            }
        }
        if (bl) {
            for (String string4 : Lists.reverse(arg.field_19374)) {
                VillageDebugRenderer.drawString(arg.pos, i, string4, -3355444, 0.02f);
                ++i;
            }
        }
        if (bl) {
            this.drawPath(arg, d, e, f);
        }
    }

    private static void drawString(String string, PointOfInterest arg, int i, int j) {
        BlockPos lv = arg.pos;
        VillageDebugRenderer.drawString(string, lv, i, j);
    }

    private static void drawString(String string, BlockPos arg, int i, int j) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)arg.getX() + 0.5;
        double g = (double)arg.getY() + 1.3 + (double)i * 0.2;
        double h = (double)arg.getZ() + 0.5;
        DebugRenderer.drawString(string, f, g, h, j, 0.02f, true, 0.0f, true);
    }

    private static void drawString(Position arg, int i, String string, int j, float f) {
        double d = 2.4;
        double e = 0.25;
        BlockPos lv = new BlockPos(arg);
        double g = (double)lv.getX() + 0.5;
        double h = arg.getY() + 2.4 + (double)i * 0.25;
        double k = (double)lv.getZ() + 0.5;
        float l = 0.5f;
        DebugRenderer.drawString(string, g, h, k, j, f, false, 0.5f, true);
    }

    private Set<String> getVillagerNames(PointOfInterest arg) {
        return this.getBrains(arg.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
    }

    private boolean isTargeted(Brain arg) {
        return Objects.equals(this.targetedEntity, arg.uuid);
    }

    private boolean isClose(Brain arg) {
        ClientPlayerEntity lv = this.client.player;
        BlockPos lv2 = new BlockPos(lv.getX(), arg.pos.getY(), lv.getZ());
        BlockPos lv3 = new BlockPos(arg.pos);
        return lv2.isWithinDistance(lv3, 30.0);
    }

    private Collection<UUID> getBrains(BlockPos arg) {
        return this.brains.values().stream().filter(arg2 -> ((Brain)arg2).isPointOfInterest(arg)).map(Brain::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
        HashMap map = Maps.newHashMap();
        for (Brain lv : this.brains.values()) {
            for (BlockPos lv2 : lv.pointsOfInterest) {
                if (this.pointsOfInterest.containsKey(lv2)) continue;
                List list = (List)map.get(lv2);
                if (list == null) {
                    list = Lists.newArrayList();
                    map.put(lv2, list);
                }
                list.add(lv.field_19328);
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

        public Brain(UUID uUID, int i, String string, String string2, int j, float f, float g, Position arg, String string3, @Nullable Path arg2, boolean bl) {
            this.uuid = uUID;
            this.field_18924 = i;
            this.field_19328 = string;
            this.profession = string2;
            this.xp = j;
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

