/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class BuiltinModelItemRenderer {
    private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray(ShulkerBoxBlockEntity[]::new);
    private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
    public static final BuiltinModelItemRenderer INSTANCE = new BuiltinModelItemRenderer();
    private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
    private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
    private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
    private final BannerBlockEntity renderBanner = new BannerBlockEntity();
    private final BedBlockEntity renderBed = new BedBlockEntity();
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
    private final ShieldEntityModel modelShield = new ShieldEntityModel();
    private final TridentEntityModel modelTrident = new TridentEntityModel();

    /*
     * WARNING - void declaration
     */
    public void render(ItemStack arg, ModelTransformation.Mode arg2, MatrixStack arg3, VertexConsumerProvider arg4, int i, int j) {
        Item lv = arg.getItem();
        if (lv instanceof BlockItem) {
            void lv13;
            Block lv2 = ((BlockItem)lv).getBlock();
            if (lv2 instanceof AbstractSkullBlock) {
                GameProfile gameProfile = null;
                if (arg.hasTag()) {
                    CompoundTag lv3 = arg.getTag();
                    if (lv3.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(lv3.getCompound("SkullOwner"));
                    } else if (lv3.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)lv3.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, lv3.getString("SkullOwner"));
                        gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                        lv3.remove("SkullOwner");
                        lv3.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                    }
                }
                SkullBlockEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)lv2).getSkullType(), gameProfile, 0.0f, arg3, arg4, i);
                return;
            }
            if (lv2 instanceof AbstractBannerBlock) {
                this.renderBanner.readFrom(arg, ((AbstractBannerBlock)lv2).getColor());
                BannerBlockEntity lv4 = this.renderBanner;
            } else if (lv2 instanceof BedBlock) {
                this.renderBed.setColor(((BedBlock)lv2).getColor());
                BedBlockEntity lv5 = this.renderBed;
            } else if (lv2 == Blocks.CONDUIT) {
                ConduitBlockEntity lv6 = this.renderConduit;
            } else if (lv2 == Blocks.CHEST) {
                ChestBlockEntity lv7 = this.renderChestNormal;
            } else if (lv2 == Blocks.ENDER_CHEST) {
                EnderChestBlockEntity lv8 = this.renderChestEnder;
            } else if (lv2 == Blocks.TRAPPED_CHEST) {
                ChestBlockEntity lv9 = this.renderChestTrapped;
            } else if (lv2 instanceof ShulkerBoxBlock) {
                DyeColor lv10 = ShulkerBoxBlock.getColor(lv);
                if (lv10 == null) {
                    ShulkerBoxBlockEntity lv11 = RENDER_SHULKER_BOX;
                } else {
                    ShulkerBoxBlockEntity lv12 = RENDER_SHULKER_BOX_DYED[lv10.getId()];
                }
            } else {
                return;
            }
            BlockEntityRenderDispatcher.INSTANCE.renderEntity(lv13, arg3, arg4, i, j);
            return;
        }
        if (lv == Items.SHIELD) {
            boolean bl = arg.getSubTag("BlockEntityTag") != null;
            arg3.push();
            arg3.scale(1.0f, -1.0f, -1.0f);
            SpriteIdentifier lv14 = bl ? ModelLoader.SHIELD_BASE : ModelLoader.SHIELD_BASE_NO_PATTERN;
            VertexConsumer lv15 = lv14.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.method_29711(arg4, this.modelShield.getLayer(lv14.getAtlasId()), true, arg.hasGlint()));
            this.modelShield.method_23775().render(arg3, lv15, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            if (bl) {
                List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(ShieldItem.getColor(arg), BannerBlockEntity.getPatternListTag(arg));
                BannerBlockEntityRenderer.renderCanvas(arg3, arg4, i, j, this.modelShield.method_23774(), lv14, false, list, arg.hasGlint());
            } else {
                this.modelShield.method_23774().render(arg3, lv15, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            arg3.pop();
        } else if (lv == Items.TRIDENT) {
            arg3.push();
            arg3.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer lv16 = ItemRenderer.method_29711(arg4, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, arg.hasGlint());
            this.modelTrident.render(arg3, lv16, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            arg3.pop();
        }
    }
}

