/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer
implements SynchronousResourceReloadListener {
    public static final Identifier ENCHANTED_ITEM_GLINT = new Identifier("textures/misc/enchanted_item_glint.png");
    private static final Set<Item> WITHOUT_MODELS = Sets.newHashSet((Object[])new Item[]{Items.AIR});
    public float zOffset;
    private final ItemModels models;
    private final TextureManager textureManager;
    private final ItemColors colorMap;

    public ItemRenderer(TextureManager arg, BakedModelManager arg2, ItemColors arg3) {
        this.textureManager = arg;
        this.models = new ItemModels(arg2);
        for (Item lv : Registry.ITEM) {
            if (WITHOUT_MODELS.contains(lv)) continue;
            this.models.putModel(lv, new ModelIdentifier(Registry.ITEM.getId(lv), "inventory"));
        }
        this.colorMap = arg3;
    }

    public ItemModels getModels() {
        return this.models;
    }

    private void renderBakedItemModel(BakedModel arg, ItemStack arg2, int i, int j, MatrixStack arg3, VertexConsumer arg4) {
        Random random = new Random();
        long l = 42L;
        for (Direction lv : Direction.values()) {
            random.setSeed(42L);
            this.renderBakedItemQuads(arg3, arg4, arg.getQuads(null, lv, random), arg2, i, j);
        }
        random.setSeed(42L);
        this.renderBakedItemQuads(arg3, arg4, arg.getQuads(null, null, random), arg2, i, j);
    }

    public void renderItem(ItemStack arg, ModelTransformation.Mode arg2, boolean bl, MatrixStack arg3, VertexConsumerProvider arg4, int i, int j, BakedModel arg5) {
        boolean bl3;
        if (arg.isEmpty()) {
            return;
        }
        arg3.push();
        boolean bl2 = arg2 == ModelTransformation.Mode.GUI;
        boolean bl4 = bl3 = bl2 || arg2 == ModelTransformation.Mode.GROUND || arg2 == ModelTransformation.Mode.FIXED;
        if (arg.getItem() == Items.TRIDENT && bl3) {
            arg5 = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
        }
        arg5.getTransformation().getTransformation(arg2).apply(bl, arg3);
        arg3.translate(-0.5, -0.5, -0.5);
        if (arg5.isBuiltin() || arg.getItem() == Items.TRIDENT && !bl3) {
            BuiltinModelItemRenderer.INSTANCE.render(arg, arg3, arg4, i, j);
        } else {
            RenderLayer lv = RenderLayers.getItemLayer(arg, arg2 != ModelTransformation.Mode.GROUND);
            VertexConsumer lv2 = ItemRenderer.getArmorVertexConsumer(arg4, lv, true, arg.hasEnchantmentGlint());
            this.renderBakedItemModel(arg5, arg, i, j, arg3, lv2);
        }
        arg3.pop();
    }

    public static VertexConsumer method_27952(VertexConsumerProvider arg, RenderLayer arg2, boolean bl, boolean bl2) {
        if (bl2) {
            return VertexConsumers.dual(arg.getBuffer(bl ? RenderLayer.method_27948() : RenderLayer.method_27949()), arg.getBuffer(arg2));
        }
        return arg.getBuffer(arg2);
    }

    public static VertexConsumer getArmorVertexConsumer(VertexConsumerProvider arg, RenderLayer arg2, boolean bl, boolean bl2) {
        if (bl2) {
            return VertexConsumers.dual(arg.getBuffer(bl ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), arg.getBuffer(arg2));
        }
        return arg.getBuffer(arg2);
    }

    private void renderBakedItemQuads(MatrixStack arg, VertexConsumer arg2, List<BakedQuad> list, ItemStack arg3, int i, int j) {
        boolean bl = !arg3.isEmpty();
        MatrixStack.Entry lv = arg.peek();
        for (BakedQuad lv2 : list) {
            int k = -1;
            if (bl && lv2.hasColor()) {
                k = this.colorMap.getColorMultiplier(arg3, lv2.getColorIndex());
            }
            float f = (float)(k >> 16 & 0xFF) / 255.0f;
            float g = (float)(k >> 8 & 0xFF) / 255.0f;
            float h = (float)(k & 0xFF) / 255.0f;
            arg2.quad(lv, lv2, f, g, h, i, j);
        }
    }

    public BakedModel getHeldItemModel(ItemStack arg, @Nullable World arg2, @Nullable LivingEntity arg3) {
        BakedModel lv3;
        Item lv = arg.getItem();
        if (lv == Items.TRIDENT) {
            BakedModel lv2 = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        } else {
            lv3 = this.models.getModel(arg);
        }
        ClientWorld lv4 = arg2 instanceof ClientWorld ? (ClientWorld)arg2 : null;
        BakedModel lv5 = lv3.getOverrides().apply(lv3, arg, lv4, arg3);
        return lv5 == null ? this.models.getModelManager().getMissingModel() : lv5;
    }

    public void renderItem(ItemStack arg, ModelTransformation.Mode arg2, int i, int j, MatrixStack arg3, VertexConsumerProvider arg4) {
        this.renderItem(null, arg, arg2, false, arg3, arg4, null, i, j);
    }

    public void renderItem(@Nullable LivingEntity arg, ItemStack arg2, ModelTransformation.Mode arg3, boolean bl, MatrixStack arg4, VertexConsumerProvider arg5, @Nullable World arg6, int i, int j) {
        if (arg2.isEmpty()) {
            return;
        }
        BakedModel lv = this.getHeldItemModel(arg2, arg6, arg);
        this.renderItem(arg2, arg3, bl, arg4, arg5, i, j, lv);
    }

    public void renderGuiItemIcon(ItemStack arg, int i, int j) {
        this.renderGuiItemModel(arg, i, j, this.getHeldItemModel(arg, null, null));
    }

    protected void renderGuiItemModel(ItemStack arg, int i, int j, BakedModel arg2) {
        boolean bl;
        RenderSystem.pushMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.translatef(i, j, 100.0f + this.zOffset);
        RenderSystem.translatef(8.0f, 8.0f, 0.0f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.scalef(16.0f, 16.0f, 16.0f);
        MatrixStack lv = new MatrixStack();
        VertexConsumerProvider.Immediate lv2 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl2 = bl = !arg2.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        this.renderItem(arg, ModelTransformation.Mode.GUI, false, lv, lv2, 0xF000F0, OverlayTexture.DEFAULT_UV, arg2);
        lv2.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public void renderGuiItem(ItemStack arg, int i, int j) {
        this.renderGuiItem(MinecraftClient.getInstance().player, arg, i, j);
    }

    public void method_27953(ItemStack arg, int i, int j) {
        this.renderGuiItem(null, arg, i, j);
    }

    public void method_27951(LivingEntity arg, ItemStack arg2, int i, int j) {
        this.renderGuiItem(arg, arg2, i, j);
    }

    private void renderGuiItem(@Nullable LivingEntity arg, ItemStack arg2, int i, int j) {
        if (arg2.isEmpty()) {
            return;
        }
        this.zOffset += 50.0f;
        try {
            this.renderGuiItemModel(arg2, i, j, this.getHeldItemModel(arg2, null, arg));
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Rendering item");
            CrashReportSection lv2 = lv.addElement("Item being rendered");
            lv2.add("Item Type", () -> String.valueOf(arg2.getItem()));
            lv2.add("Item Damage", () -> String.valueOf(arg2.getDamage()));
            lv2.add("Item NBT", () -> String.valueOf(arg2.getTag()));
            lv2.add("Item Foil", () -> String.valueOf(arg2.hasEnchantmentGlint()));
            throw new CrashException(lv);
        }
        this.zOffset -= 50.0f;
    }

    public void renderGuiItemOverlay(TextRenderer arg, ItemStack arg2, int i, int j) {
        this.renderGuiItemOverlay(arg, arg2, i, j, null);
    }

    public void renderGuiItemOverlay(TextRenderer arg, ItemStack arg2, int i, int j, @Nullable String string) {
        ClientPlayerEntity lv5;
        float m;
        if (arg2.isEmpty()) {
            return;
        }
        MatrixStack lv = new MatrixStack();
        if (arg2.getCount() != 1 || string != null) {
            String string2 = string == null ? String.valueOf(arg2.getCount()) : string;
            lv.translate(0.0, 0.0, this.zOffset + 200.0f);
            VertexConsumerProvider.Immediate lv2 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            arg.draw(string2, (float)(i + 19 - 2 - arg.getWidth(string2)), (float)(j + 6 + 3), 0xFFFFFF, true, lv.peek().getModel(), (VertexConsumerProvider)lv2, false, 0, 0xF000F0);
            lv2.draw();
        }
        if (arg2.isDamaged()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator lv3 = Tessellator.getInstance();
            BufferBuilder lv4 = lv3.getBuffer();
            float f = arg2.getDamage();
            float g = arg2.getMaxDamage();
            float h = Math.max(0.0f, (g - f) / g);
            int k = Math.round(13.0f - f * 13.0f / g);
            int l = MathHelper.hsvToRgb(h / 3.0f, 1.0f, 1.0f);
            this.renderGuiQuad(lv4, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(lv4, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        float f = m = (lv5 = MinecraftClient.getInstance().player) == null ? 0.0f : lv5.getItemCooldownManager().getCooldownProgress(arg2.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (m > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator lv6 = Tessellator.getInstance();
            BufferBuilder lv7 = lv6.getBuffer();
            this.renderGuiQuad(lv7, i, j + MathHelper.floor(16.0f * (1.0f - m)), 16, MathHelper.ceil(16.0f * m), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private void renderGuiQuad(BufferBuilder arg, int i, int j, int k, int l, int m, int n, int o, int p) {
        arg.begin(7, VertexFormats.POSITION_COLOR);
        arg.vertex(i + 0, j + 0, 0.0).color(m, n, o, p).next();
        arg.vertex(i + 0, j + l, 0.0).color(m, n, o, p).next();
        arg.vertex(i + k, j + l, 0.0).color(m, n, o, p).next();
        arg.vertex(i + k, j + 0, 0.0).color(m, n, o, p).next();
        Tessellator.getInstance().draw();
    }

    @Override
    public void apply(ResourceManager arg) {
        this.models.reloadModels();
    }
}

