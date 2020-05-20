/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class RenderLayer
extends RenderPhase {
    private static final RenderLayer SOLID = RenderLayer.of("solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 0x200000, true, false, MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true));
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.of("cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131072, true, false, MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA).build(true));
    private static final RenderLayer CUTOUT = RenderLayer.of("cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 131072, true, false, MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA).build(true));
    private static final RenderLayer TRANSLUCENT = RenderLayer.of("translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, true, true, RenderLayer.createTranslucentPhaseData());
    private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = RenderLayer.of("translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 262144, false, true, RenderLayer.createTranslucentPhaseData());
    private static final RenderLayer LEASH = RenderLayer.of("leash", VertexFormats.POSITION_COLOR_LIGHT, 7, 256, MultiPhaseParameters.builder().texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false));
    private static final RenderLayer WATER_MASK = RenderLayer.of("water_mask", VertexFormats.POSITION, 7, 256, MultiPhaseParameters.builder().texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false));
    private static final RenderLayer ARMOR_GLINT = RenderLayer.of("armor_glint", VertexFormats.POSITION_TEXTURE, 7, 256, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
    private static final RenderLayer ARMOR_ENTITY_GLINT = RenderLayer.of("armor_entity_glint", VertexFormats.POSITION_TEXTURE, 7, 256, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
    private static final RenderLayer GLINT = RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, 7, 256, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(GLINT_TEXTURING).build(false));
    private static final RenderLayer ENTITY_GLINT = RenderLayer.of("entity_glint", VertexFormats.POSITION_TEXTURE, 7, 256, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(ENTITY_GLINT_TEXTURING).build(false));
    private static final RenderLayer LIGHTNING = RenderLayer.of("lightning", VertexFormats.POSITION_COLOR, 7, 256, false, true, MultiPhaseParameters.builder().writeMaskState(COLOR_MASK).transparency(LIGHTNING_TRANSPARENCY).shadeModel(SMOOTH_SHADE_MODEL).build(false));
    public static final MultiPhase LINES = RenderLayer.of("lines", VertexFormats.POSITION_COLOR, 1, 256, MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty())).layering(VIEW_OFFSET_Z_LAYERING).transparency(TRANSLUCENT_TRANSPARENCY).writeMaskState(COLOR_MASK).build(false));
    private final VertexFormat vertexFormat;
    private final int drawMode;
    private final int expectedBufferSize;
    private final boolean hasCrumbling;
    private final boolean translucent;
    private final Optional<RenderLayer> optionalThis;

    public static RenderLayer getSolid() {
        return SOLID;
    }

    public static RenderLayer getCutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderLayer getCutout() {
        return CUTOUT;
    }

    private static MultiPhaseParameters createTranslucentPhaseData() {
        return MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL).lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).transparency(TRANSLUCENT_TRANSPARENCY).build(true);
    }

    public static RenderLayer getTranslucent() {
        return TRANSLUCENT;
    }

    public static RenderLayer getTranslucentNoCrumbling() {
        return TRANSLUCENT_NO_CRUMBLING;
    }

    public static RenderLayer getArmorCutoutNoCull(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(true);
        return RenderLayer.of("armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, lv);
    }

    public static RenderLayer getEntitySolid(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, lv);
    }

    public static RenderLayer getEntityCutout(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, lv);
    }

    public static RenderLayer getCutoutNoCull(Identifier arg, boolean bl) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(bl);
        return RenderLayer.of("entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, lv);
    }

    public static RenderLayer getEntityCutoutNoCull(Identifier arg) {
        return RenderLayer.getCutoutNoCull(arg, true);
    }

    public static RenderLayer method_28115(Identifier arg, boolean bl) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(bl);
        return RenderLayer.of("entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, lv);
    }

    public static RenderLayer method_28116(Identifier arg) {
        return RenderLayer.method_28115(arg, true);
    }

    public static RenderLayer getEntityTranslucentCull(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of("entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, lv);
    }

    public static RenderLayer getEntityTranslucent(Identifier arg, boolean bl) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(bl);
        return RenderLayer.of("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, lv);
    }

    public static RenderLayer getEntityTranslucent(Identifier arg) {
        return RenderLayer.getEntityTranslucent(arg, true);
    }

    public static RenderLayer getEntitySmoothCutout(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).alpha(HALF_ALPHA).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).shadeModel(SMOOTH_SHADE_MODEL).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(true);
        return RenderLayer.of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, lv);
    }

    public static RenderLayer getBeaconBeam(Identifier arg, boolean bl) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(bl ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).writeMaskState(bl ? COLOR_MASK : ALL_MASK).fog(NO_FOG).build(false);
        return RenderLayer.of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, lv);
    }

    public static RenderLayer getEntityDecal(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).depthTest(EQUAL_DEPTH_TEST).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false);
        return RenderLayer.of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, lv);
    }

    public static RenderLayer getEntityNoOutline(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).writeMaskState(COLOR_MASK).build(false);
        return RenderLayer.of("entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, lv);
    }

    public static RenderLayer getEntityShadow(Identifier arg) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(ENABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).writeMaskState(COLOR_MASK).depthTest(LEQUAL_DEPTH_TEST).layering(VIEW_OFFSET_Z_LAYERING).build(false);
        return RenderLayer.of("entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, false, lv);
    }

    public static RenderLayer getEntityAlpha(Identifier arg, float f) {
        MultiPhaseParameters lv = MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).alpha(new RenderPhase.Alpha(f)).cull(DISABLE_CULLING).build(true);
        return RenderLayer.of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, lv);
    }

    public static RenderLayer getEyes(Identifier arg) {
        RenderPhase.Texture lv = new RenderPhase.Texture(arg, false, false);
        return RenderLayer.of("eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, MultiPhaseParameters.builder().texture(lv).transparency(ADDITIVE_TRANSPARENCY).writeMaskState(COLOR_MASK).fog(BLACK_FOG).build(false));
    }

    public static RenderLayer getEnergySwirl(Identifier arg, float f, float g) {
        return RenderLayer.of("energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, false, true, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).texturing(new RenderPhase.OffsetTexturing(f, g)).fog(BLACK_FOG).transparency(ADDITIVE_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
    }

    public static RenderLayer getLeash() {
        return LEASH;
    }

    public static RenderLayer getWaterMask() {
        return WATER_MASK;
    }

    public static RenderLayer getOutline(Identifier arg) {
        return RenderLayer.getOutline(arg, DISABLE_CULLING);
    }

    public static RenderLayer getOutline(Identifier arg, RenderPhase.Cull arg2) {
        return RenderLayer.of("outline", VertexFormats.POSITION_COLOR_TEXTURE, 7, 256, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).cull(arg2).depthTest(ALWAYS_DEPTH_TEST).alpha(ONE_TENTH_ALPHA).texturing(OUTLINE_TEXTURING).fog(NO_FOG).target(OUTLINE_TARGET).build(OutlineMode.IS_OUTLINE));
    }

    public static RenderLayer method_27948() {
        return ARMOR_GLINT;
    }

    public static RenderLayer method_27949() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderLayer getGlint() {
        return GLINT;
    }

    public static RenderLayer getEntityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderLayer getBlockBreaking(Identifier arg) {
        RenderPhase.Texture lv = new RenderPhase.Texture(arg, false, false);
        return RenderLayer.of("crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, false, true, MultiPhaseParameters.builder().texture(lv).alpha(ONE_TENTH_ALPHA).transparency(CRUMBLING_TRANSPARENCY).writeMaskState(COLOR_MASK).layering(POLYGON_OFFSET_LAYERING).build(false));
    }

    public static RenderLayer getText(Identifier arg) {
        return RenderLayer.of("text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).build(false));
    }

    public static RenderLayer getTextSeeThrough(Identifier arg) {
        return RenderLayer.of("text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, 7, 256, false, true, MultiPhaseParameters.builder().texture(new RenderPhase.Texture(arg, false, false)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).depthTest(ALWAYS_DEPTH_TEST).writeMaskState(COLOR_MASK).build(false));
    }

    public static RenderLayer getLightning() {
        return LIGHTNING;
    }

    public static RenderLayer getEndPortal(int i) {
        RenderPhase.Texture lv4;
        RenderPhase.Transparency lv3;
        if (i <= 1) {
            RenderPhase.Transparency lv = TRANSLUCENT_TRANSPARENCY;
            RenderPhase.Texture lv2 = new RenderPhase.Texture(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false);
        } else {
            lv3 = ADDITIVE_TRANSPARENCY;
            lv4 = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false);
        }
        return RenderLayer.of("end_portal", VertexFormats.POSITION_COLOR, 7, 256, false, true, MultiPhaseParameters.builder().transparency(lv3).texture(lv4).texturing(new RenderPhase.PortalTexturing(i)).fog(BLACK_FOG).build(false));
    }

    public static RenderLayer getLines() {
        return LINES;
    }

    public RenderLayer(String string, VertexFormat arg, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, runnable, runnable2);
        this.vertexFormat = arg;
        this.drawMode = i;
        this.expectedBufferSize = j;
        this.hasCrumbling = bl;
        this.translucent = bl2;
        this.optionalThis = Optional.of(this);
    }

    public static MultiPhase of(String string, VertexFormat arg, int i, int j, MultiPhaseParameters arg2) {
        return RenderLayer.of(string, arg, i, j, false, false, arg2);
    }

    public static MultiPhase of(String string, VertexFormat arg, int i, int j, boolean bl, boolean bl2, MultiPhaseParameters arg2) {
        return MultiPhase.of(string, arg, i, j, bl, bl2, arg2);
    }

    public void draw(BufferBuilder arg, int i, int j, int k) {
        if (!arg.isBuilding()) {
            return;
        }
        if (this.translucent) {
            arg.sortQuads(i, j, k);
        }
        arg.end();
        this.startDrawing();
        BufferRenderer.draw(arg);
        this.endDrawing();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static List<RenderLayer> getBlockLayers() {
        return ImmutableList.of((Object)RenderLayer.getSolid(), (Object)RenderLayer.getCutoutMipped(), (Object)RenderLayer.getCutout(), (Object)RenderLayer.getTranslucent());
    }

    public int getExpectedBufferSize() {
        return this.expectedBufferSize;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public int getDrawMode() {
        return this.drawMode;
    }

    public Optional<RenderLayer> getAffectedOutline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public boolean hasCrumbling() {
        return this.hasCrumbling;
    }

    public Optional<RenderLayer> asOptional() {
        return this.optionalThis;
    }

    @Environment(value=EnvType.CLIENT)
    static final class MultiPhase
    extends RenderLayer {
        private static final ObjectOpenCustomHashSet<MultiPhase> CACHE = new ObjectOpenCustomHashSet((Hash.Strategy)HashStrategy.INSTANCE);
        private final MultiPhaseParameters phases;
        private final int hash;
        private final Optional<RenderLayer> affectedOutline;
        private final boolean outline;

        private MultiPhase(String string, VertexFormat arg, int i, int j, boolean bl, boolean bl2, MultiPhaseParameters arg22) {
            super(string, arg, i, j, bl, bl2, () -> arg22.phases.forEach(RenderPhase::startDrawing), () -> arg22.phases.forEach(RenderPhase::endDrawing));
            this.phases = arg22;
            this.affectedOutline = arg22.outlineMode == OutlineMode.AFFECTS_OUTLINE ? arg22.texture.getId().map(arg2 -> MultiPhase.getOutline(arg2, arg22.cull)) : Optional.empty();
            this.outline = arg22.outlineMode == OutlineMode.IS_OUTLINE;
            this.hash = Objects.hash(super.hashCode(), arg22);
        }

        private static MultiPhase of(String string, VertexFormat arg, int i, int j, boolean bl, boolean bl2, MultiPhaseParameters arg2) {
            return (MultiPhase)CACHE.addOrGet((Object)new MultiPhase(string, arg, i, j, bl, bl2, arg2));
        }

        @Override
        public Optional<RenderLayer> getAffectedOutline() {
            return this.affectedOutline;
        }

        @Override
        public boolean isOutline() {
            return this.outline;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return this == object;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }

        @Override
        public String toString() {
            return "RenderType[" + this.phases + ']';
        }

        @Environment(value=EnvType.CLIENT)
        static enum HashStrategy implements Hash.Strategy<MultiPhase>
        {
            INSTANCE;


            public int hashCode(@Nullable MultiPhase arg) {
                if (arg == null) {
                    return 0;
                }
                return arg.hash;
            }

            public boolean equals(@Nullable MultiPhase arg, @Nullable MultiPhase arg2) {
                if (arg == arg2) {
                    return true;
                }
                if (arg == null || arg2 == null) {
                    return false;
                }
                return Objects.equals(arg.phases, arg2.phases);
            }

            public /* synthetic */ boolean equals(@Nullable Object object, @Nullable Object object2) {
                return this.equals((MultiPhase)object, (MultiPhase)object2);
            }

            public /* synthetic */ int hashCode(@Nullable Object object) {
                return this.hashCode((MultiPhase)object);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class MultiPhaseParameters {
        private final RenderPhase.Texture texture;
        private final RenderPhase.Transparency transparency;
        private final RenderPhase.DiffuseLighting diffuseLighting;
        private final RenderPhase.ShadeModel shadeModel;
        private final RenderPhase.Alpha alpha;
        private final RenderPhase.DepthTest depthTest;
        private final RenderPhase.Cull cull;
        private final RenderPhase.Lightmap lightmap;
        private final RenderPhase.Overlay overlay;
        private final RenderPhase.Fog fog;
        private final RenderPhase.Layering layering;
        private final RenderPhase.Target target;
        private final RenderPhase.Texturing texturing;
        private final RenderPhase.WriteMaskState writeMaskState;
        private final RenderPhase.LineWidth lineWidth;
        private final OutlineMode outlineMode;
        private final ImmutableList<RenderPhase> phases;

        private MultiPhaseParameters(RenderPhase.Texture arg, RenderPhase.Transparency arg2, RenderPhase.DiffuseLighting arg3, RenderPhase.ShadeModel arg4, RenderPhase.Alpha arg5, RenderPhase.DepthTest arg6, RenderPhase.Cull arg7, RenderPhase.Lightmap arg8, RenderPhase.Overlay arg9, RenderPhase.Fog arg10, RenderPhase.Layering arg11, RenderPhase.Target arg12, RenderPhase.Texturing arg13, RenderPhase.WriteMaskState arg14, RenderPhase.LineWidth arg15, OutlineMode arg16) {
            this.texture = arg;
            this.transparency = arg2;
            this.diffuseLighting = arg3;
            this.shadeModel = arg4;
            this.alpha = arg5;
            this.depthTest = arg6;
            this.cull = arg7;
            this.lightmap = arg8;
            this.overlay = arg9;
            this.fog = arg10;
            this.layering = arg11;
            this.target = arg12;
            this.texturing = arg13;
            this.writeMaskState = arg14;
            this.lineWidth = arg15;
            this.outlineMode = arg16;
            this.phases = ImmutableList.of((Object)this.texture, (Object)this.transparency, (Object)this.diffuseLighting, (Object)this.shadeModel, (Object)this.alpha, (Object)this.depthTest, (Object)this.cull, (Object)this.lightmap, (Object)this.overlay, (Object)this.fog, (Object)this.layering, (Object)this.target, (Object[])new RenderPhase[]{this.texturing, this.writeMaskState, this.lineWidth});
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            MultiPhaseParameters lv = (MultiPhaseParameters)object;
            return this.outlineMode == lv.outlineMode && this.phases.equals(lv.phases);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.phases, this.outlineMode});
        }

        public String toString() {
            return "CompositeState[" + this.phases + ", outlineProperty=" + (Object)((Object)this.outlineMode) + ']';
        }

        public static Builder builder() {
            return new Builder();
        }

        @Environment(value=EnvType.CLIENT)
        public static class Builder {
            private RenderPhase.Texture texture = RenderPhase.NO_TEXTURE;
            private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
            private RenderPhase.DiffuseLighting diffuseLighting = RenderPhase.DISABLE_DIFFUSE_LIGHTING;
            private RenderPhase.ShadeModel shadeModel = RenderPhase.SHADE_MODEL;
            private RenderPhase.Alpha alpha = RenderPhase.ZERO_ALPHA;
            private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
            private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
            private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
            private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
            private RenderPhase.Fog fog = RenderPhase.FOG;
            private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
            private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
            private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
            private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
            private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINEWIDTH;

            private Builder() {
            }

            public Builder texture(RenderPhase.Texture arg) {
                this.texture = arg;
                return this;
            }

            public Builder transparency(RenderPhase.Transparency arg) {
                this.transparency = arg;
                return this;
            }

            public Builder diffuseLighting(RenderPhase.DiffuseLighting arg) {
                this.diffuseLighting = arg;
                return this;
            }

            public Builder shadeModel(RenderPhase.ShadeModel arg) {
                this.shadeModel = arg;
                return this;
            }

            public Builder alpha(RenderPhase.Alpha arg) {
                this.alpha = arg;
                return this;
            }

            public Builder depthTest(RenderPhase.DepthTest arg) {
                this.depthTest = arg;
                return this;
            }

            public Builder cull(RenderPhase.Cull arg) {
                this.cull = arg;
                return this;
            }

            public Builder lightmap(RenderPhase.Lightmap arg) {
                this.lightmap = arg;
                return this;
            }

            public Builder overlay(RenderPhase.Overlay arg) {
                this.overlay = arg;
                return this;
            }

            public Builder fog(RenderPhase.Fog arg) {
                this.fog = arg;
                return this;
            }

            public Builder layering(RenderPhase.Layering arg) {
                this.layering = arg;
                return this;
            }

            public Builder target(RenderPhase.Target arg) {
                this.target = arg;
                return this;
            }

            public Builder texturing(RenderPhase.Texturing arg) {
                this.texturing = arg;
                return this;
            }

            public Builder writeMaskState(RenderPhase.WriteMaskState arg) {
                this.writeMaskState = arg;
                return this;
            }

            public Builder lineWidth(RenderPhase.LineWidth arg) {
                this.lineWidth = arg;
                return this;
            }

            public MultiPhaseParameters build(boolean bl) {
                return this.build(bl ? OutlineMode.AFFECTS_OUTLINE : OutlineMode.NONE);
            }

            public MultiPhaseParameters build(OutlineMode arg) {
                return new MultiPhaseParameters(this.texture, this.transparency, this.diffuseLighting, this.shadeModel, this.alpha, this.depthTest, this.cull, this.lightmap, this.overlay, this.fog, this.layering, this.target, this.texturing, this.writeMaskState, this.lineWidth, arg);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum OutlineMode {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        private OutlineMode(String string2) {
            this.name = string2;
        }

        public String toString() {
            return this.name;
        }
    }
}

