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
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
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
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.TransformingVertexConsumer;
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
import net.minecraft.item.BlockItem;
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

    public ItemRenderer(TextureManager manager, BakedModelManager bakery, ItemColors colorMap) {
        this.textureManager = manager;
        this.models = new ItemModels(bakery);
        for (Item lv : Registry.ITEM) {
            if (WITHOUT_MODELS.contains(lv)) continue;
            this.models.putModel(lv, new ModelIdentifier(Registry.ITEM.getId(lv), "inventory"));
        }
        this.colorMap = colorMap;
    }

    public ItemModels getModels() {
        return this.models;
    }

    private void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        Random random = new Random();
        long l = 42L;
        for (Direction lv : Direction.values()) {
            random.setSeed(42L);
            this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, lv, random), stack, light, overlay);
        }
        random.setSeed(42L);
        this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    public void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model) {
        boolean bl2;
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        boolean bl = bl2 = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED;
        if (stack.getItem() == Items.TRIDENT && bl2) {
            model = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
        }
        model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
        matrices.translate(-0.5, -0.5, -0.5);
        if (model.isBuiltin() || stack.getItem() == Items.TRIDENT && !bl2) {
            BuiltinModelItemRenderer.INSTANCE.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
        } else {
            VertexConsumer lv7;
            boolean bl4;
            if (renderMode != ModelTransformation.Mode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem) {
                Block lv = ((BlockItem)stack.getItem()).getBlock();
                boolean bl3 = !(lv instanceof TransparentBlock) && !(lv instanceof StainedGlassPaneBlock);
            } else {
                bl4 = true;
            }
            RenderLayer lv2 = RenderLayers.getItemLayer(stack, bl4);
            if (stack.getItem() == Items.COMPASS && stack.hasGlint()) {
                matrices.push();
                MatrixStack.Entry lv3 = matrices.peek();
                if (renderMode == ModelTransformation.Mode.GUI) {
                    lv3.getModel().multiply(0.5f);
                } else if (renderMode.isFirstPerson()) {
                    lv3.getModel().multiply(0.75f);
                }
                if (bl4) {
                    VertexConsumer lv4 = ItemRenderer.getTransformingDirectGlintVertexConsumer(vertexConsumers, lv2, lv3);
                } else {
                    VertexConsumer lv5 = ItemRenderer.getTransformingGlintVertexConsumer(vertexConsumers, lv2, lv3);
                }
                matrices.pop();
            } else if (bl4) {
                VertexConsumer lv6 = ItemRenderer.getDirectGlintVertexConsumer(vertexConsumers, lv2, true, stack.hasGlint());
            } else {
                lv7 = ItemRenderer.getGlintVertexConsumer(vertexConsumers, lv2, true, stack.hasGlint());
            }
            this.renderBakedItemModel(model, stack, light, overlay, matrices, lv7);
        }
        matrices.pop();
    }

    public static VertexConsumer getArmorVertexConsumer(VertexConsumerProvider arg, RenderLayer arg2, boolean bl, boolean glint) {
        if (glint) {
            return VertexConsumers.dual(arg.getBuffer(bl ? RenderLayer.getArmorGlint() : RenderLayer.getArmorEntityGlint()), arg.getBuffer(arg2));
        }
        return arg.getBuffer(arg2);
    }

    public static VertexConsumer getTransformingGlintVertexConsumer(VertexConsumerProvider arg, RenderLayer arg2, MatrixStack.Entry arg3) {
        return VertexConsumers.dual(new TransformingVertexConsumer(arg.getBuffer(RenderLayer.getGlint()), arg3.getModel(), arg3.getNormal()), arg.getBuffer(arg2));
    }

    public static VertexConsumer getTransformingDirectGlintVertexConsumer(VertexConsumerProvider arg, RenderLayer arg2, MatrixStack.Entry arg3) {
        return VertexConsumers.dual(new TransformingVertexConsumer(arg.getBuffer(RenderLayer.getGlintDirect()), arg3.getModel(), arg3.getNormal()), arg.getBuffer(arg2));
    }

    public static VertexConsumer getGlintVertexConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            if (MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull()) {
                return VertexConsumers.dual(vertexConsumers.getBuffer(RenderLayer.method_30676()), vertexConsumers.getBuffer(layer));
            }
            return VertexConsumers.dual(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
        }
        return vertexConsumers.getBuffer(layer);
    }

    public static VertexConsumer getDirectGlintVertexConsumer(VertexConsumerProvider arg, RenderLayer layer, boolean bl, boolean glint) {
        if (glint) {
            return VertexConsumers.dual(arg.getBuffer(bl ? RenderLayer.getGlintDirect() : RenderLayer.getEntityGlintDirect()), arg.getBuffer(layer));
        }
        return arg.getBuffer(layer);
    }

    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
        boolean bl = !stack.isEmpty();
        MatrixStack.Entry lv = matrices.peek();
        for (BakedQuad lv2 : quads) {
            int k = -1;
            if (bl && lv2.hasColor()) {
                k = this.colorMap.getColorMultiplier(stack, lv2.getColorIndex());
            }
            float f = (float)(k >> 16 & 0xFF) / 255.0f;
            float g = (float)(k >> 8 & 0xFF) / 255.0f;
            float h = (float)(k & 0xFF) / 255.0f;
            vertices.quad(lv, lv2, f, g, h, light, overlay);
        }
    }

    public BakedModel getHeldItemModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
        BakedModel lv3;
        Item lv = stack.getItem();
        if (lv == Items.TRIDENT) {
            BakedModel lv2 = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        } else {
            lv3 = this.models.getModel(stack);
        }
        ClientWorld lv4 = world instanceof ClientWorld ? (ClientWorld)world : null;
        BakedModel lv5 = lv3.getOverrides().apply(lv3, stack, lv4, entity);
        return lv5 == null ? this.models.getModelManager().getMissingModel() : lv5;
    }

    public void renderItem(ItemStack stack, ModelTransformation.Mode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        this.renderItem(null, stack, transformationType, false, matrices, vertexConsumers, null, light, overlay);
    }

    public void renderItem(@Nullable LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay) {
        if (item.isEmpty()) {
            return;
        }
        BakedModel lv = this.getHeldItemModel(item, world, entity);
        this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, lv);
    }

    public void renderGuiItemIcon(ItemStack stack, int x, int y) {
        this.renderGuiItemModel(stack, x, y, this.getHeldItemModel(stack, null, null));
    }

    protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model) {
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
        RenderSystem.translatef(x, y, 100.0f + this.zOffset);
        RenderSystem.translatef(8.0f, 8.0f, 0.0f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.scalef(16.0f, 16.0f, 16.0f);
        MatrixStack lv = new MatrixStack();
        VertexConsumerProvider.Immediate lv2 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl2 = bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        this.renderItem(stack, ModelTransformation.Mode.GUI, false, lv, lv2, 0xF000F0, OverlayTexture.DEFAULT_UV, model);
        lv2.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public void renderInGuiWithOverrides(ItemStack stack, int x, int y) {
        this.innerRenderInGui(MinecraftClient.getInstance().player, stack, x, y);
    }

    public void renderInGui(ItemStack stack, int x, int y) {
        this.innerRenderInGui(null, stack, x, y);
    }

    public void renderInGuiWithOverrides(LivingEntity entity, ItemStack stack, int x, int y) {
        this.innerRenderInGui(entity, stack, x, y);
    }

    private void innerRenderInGui(@Nullable LivingEntity entity, ItemStack itemStack, int x, int y) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.zOffset += 50.0f;
        try {
            this.renderGuiItemModel(itemStack, x, y, this.getHeldItemModel(itemStack, null, entity));
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Rendering item");
            CrashReportSection lv2 = lv.addElement("Item being rendered");
            lv2.add("Item Type", () -> String.valueOf(itemStack.getItem()));
            lv2.add("Item Damage", () -> String.valueOf(itemStack.getDamage()));
            lv2.add("Item NBT", () -> String.valueOf(itemStack.getTag()));
            lv2.add("Item Foil", () -> String.valueOf(itemStack.hasGlint()));
            throw new CrashException(lv);
        }
        this.zOffset -= 50.0f;
    }

    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
        this.renderGuiItemOverlay(renderer, stack, x, y, null);
    }

    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        ClientPlayerEntity lv5;
        float m;
        if (stack.isEmpty()) {
            return;
        }
        MatrixStack lv = new MatrixStack();
        if (stack.getCount() != 1 || countLabel != null) {
            String string2 = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
            lv.translate(0.0, 0.0, this.zOffset + 200.0f);
            VertexConsumerProvider.Immediate lv2 = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            renderer.draw(string2, (float)(x + 19 - 2 - renderer.getWidth(string2)), (float)(y + 6 + 3), 0xFFFFFF, true, lv.peek().getModel(), (VertexConsumerProvider)lv2, false, 0, 0xF000F0);
            lv2.draw();
        }
        if (stack.isDamaged()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator lv3 = Tessellator.getInstance();
            BufferBuilder lv4 = lv3.getBuffer();
            float f = stack.getDamage();
            float g = stack.getMaxDamage();
            float h = Math.max(0.0f, (g - f) / g);
            int k = Math.round(13.0f - f * 13.0f / g);
            int l = MathHelper.hsvToRgb(h / 3.0f, 1.0f, 1.0f);
            this.renderGuiQuad(lv4, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(lv4, x + 2, y + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        float f = m = (lv5 = MinecraftClient.getInstance().player) == null ? 0.0f : lv5.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (m > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator lv6 = Tessellator.getInstance();
            BufferBuilder lv7 = lv6.getBuffer();
            this.renderGuiQuad(lv7, x, y + MathHelper.floor(16.0f * (1.0f - m)), 16, MathHelper.ceil(16.0f * m), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        buffer.begin(7, VertexFormats.POSITION_COLOR);
        buffer.vertex(x + 0, y + 0, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + 0, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + 0, 0.0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
    }

    @Override
    public void apply(ResourceManager manager) {
        this.models.reloadModels();
    }
}

