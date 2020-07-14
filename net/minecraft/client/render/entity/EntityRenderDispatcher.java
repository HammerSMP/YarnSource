/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.AreaEffectCloudEntityRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.CaveSpiderEntityRenderer;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.CodEntityRenderer;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.DolphinEntityRenderer;
import net.minecraft.client.render.entity.DonkeyEntityRenderer;
import net.minecraft.client.render.entity.DragonFireballEntityRenderer;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EndermiteEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.render.entity.EvokerFangsEntityRenderer;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.FireworkEntityRenderer;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.render.entity.GiantEntityRenderer;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.HoglinEntityRenderer;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.render.entity.HuskEntityRenderer;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.entity.LeashKnotEntityRenderer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.LlamaSpitEntityRenderer;
import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.MooshroomEntityRenderer;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.PandaEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PolarBearEntityRenderer;
import net.minecraft.client.render.entity.PufferfishEntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.render.entity.RavagerEntityRenderer;
import net.minecraft.client.render.entity.SalmonEntityRenderer;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.SilverfishEntityRenderer;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.SnowGolemEntityRenderer;
import net.minecraft.client.render.entity.SpectralArrowEntityRenderer;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.StrayEntityRenderer;
import net.minecraft.client.render.entity.StriderEntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.client.render.entity.VexEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.VindicatorEntityRenderer;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.client.render.entity.WitchEntityRenderer;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.WitherSkeletonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.ZoglinEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.ZombieHorseEntityRenderer;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@Environment(value=EnvType.CLIENT)
public class EntityRenderDispatcher {
    private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(new Identifier("textures/misc/shadow.png"));
    private final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();
    private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.newHashMap();
    private final PlayerEntityRenderer playerRenderer;
    private final TextRenderer textRenderer;
    public final TextureManager textureManager;
    private World world;
    public Camera camera;
    private Quaternion rotation;
    public Entity targetedEntity;
    public final GameOptions gameOptions;
    private boolean renderShadows = true;
    private boolean renderHitboxes;

    public <E extends Entity> int getLight(E entity, float tickDelta) {
        return this.getRenderer(entity).getLight(entity, tickDelta);
    }

    private <T extends Entity> void register(EntityType<T> arg, EntityRenderer<? super T> arg2) {
        this.renderers.put(arg, arg2);
    }

    private void registerRenderers(ItemRenderer arg, ReloadableResourceManager arg2) {
        this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudEntityRenderer(this));
        this.register(EntityType.ARMOR_STAND, new ArmorStandEntityRenderer(this));
        this.register(EntityType.ARROW, new ArrowEntityRenderer(this));
        this.register(EntityType.BAT, new BatEntityRenderer(this));
        this.register(EntityType.BEE, new BeeEntityRenderer(this));
        this.register(EntityType.BLAZE, new BlazeEntityRenderer(this));
        this.register(EntityType.BOAT, new BoatEntityRenderer(this));
        this.register(EntityType.CAT, new CatEntityRenderer(this));
        this.register(EntityType.CAVE_SPIDER, new CaveSpiderEntityRenderer(this));
        this.register(EntityType.CHEST_MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.CHICKEN, new ChickenEntityRenderer(this));
        this.register(EntityType.COD, new CodEntityRenderer(this));
        this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.COW, new CowEntityRenderer(this));
        this.register(EntityType.CREEPER, new CreeperEntityRenderer(this));
        this.register(EntityType.DOLPHIN, new DolphinEntityRenderer(this));
        this.register(EntityType.DONKEY, new DonkeyEntityRenderer(this, 0.87f));
        this.register(EntityType.DRAGON_FIREBALL, new DragonFireballEntityRenderer(this));
        this.register(EntityType.DROWNED, new DrownedEntityRenderer(this));
        this.register(EntityType.EGG, new FlyingItemEntityRenderer(this, arg));
        this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianEntityRenderer(this));
        this.register(EntityType.END_CRYSTAL, new EndCrystalEntityRenderer(this));
        this.register(EntityType.ENDER_DRAGON, new EnderDragonEntityRenderer(this));
        this.register(EntityType.ENDERMAN, new EndermanEntityRenderer(this));
        this.register(EntityType.ENDERMITE, new EndermiteEntityRenderer(this));
        this.register(EntityType.ENDER_PEARL, new FlyingItemEntityRenderer(this, arg));
        this.register(EntityType.EVOKER_FANGS, new EvokerFangsEntityRenderer(this));
        this.register(EntityType.EVOKER, new EvokerEntityRenderer(this));
        this.register(EntityType.EXPERIENCE_BOTTLE, new FlyingItemEntityRenderer(this, arg));
        this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbEntityRenderer(this));
        this.register(EntityType.EYE_OF_ENDER, new FlyingItemEntityRenderer(this, arg, 1.0f, true));
        this.register(EntityType.FALLING_BLOCK, new FallingBlockEntityRenderer(this));
        this.register(EntityType.FIREBALL, new FlyingItemEntityRenderer(this, arg, 3.0f, true));
        this.register(EntityType.FIREWORK_ROCKET, new FireworkEntityRenderer(this, arg));
        this.register(EntityType.FISHING_BOBBER, new FishingBobberEntityRenderer(this));
        this.register(EntityType.FOX, new FoxEntityRenderer(this));
        this.register(EntityType.FURNACE_MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.GHAST, new GhastEntityRenderer(this));
        this.register(EntityType.GIANT, new GiantEntityRenderer(this, 6.0f));
        this.register(EntityType.GUARDIAN, new GuardianEntityRenderer(this));
        this.register(EntityType.HOGLIN, new HoglinEntityRenderer(this));
        this.register(EntityType.HOPPER_MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.HORSE, new HorseEntityRenderer(this));
        this.register(EntityType.HUSK, new HuskEntityRenderer(this));
        this.register(EntityType.ILLUSIONER, new IllusionerEntityRenderer(this));
        this.register(EntityType.IRON_GOLEM, new IronGolemEntityRenderer(this));
        this.register(EntityType.ITEM, new ItemEntityRenderer(this, arg));
        this.register(EntityType.ITEM_FRAME, new ItemFrameEntityRenderer(this, arg));
        this.register(EntityType.LEASH_KNOT, new LeashKnotEntityRenderer(this));
        this.register(EntityType.LIGHTNING_BOLT, new LightningEntityRenderer(this));
        this.register(EntityType.LLAMA, new LlamaEntityRenderer(this));
        this.register(EntityType.LLAMA_SPIT, new LlamaSpitEntityRenderer(this));
        this.register(EntityType.MAGMA_CUBE, new MagmaCubeEntityRenderer(this));
        this.register(EntityType.MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.MOOSHROOM, new MooshroomEntityRenderer(this));
        this.register(EntityType.MULE, new DonkeyEntityRenderer(this, 0.92f));
        this.register(EntityType.OCELOT, new OcelotEntityRenderer(this));
        this.register(EntityType.PAINTING, new PaintingEntityRenderer(this));
        this.register(EntityType.PANDA, new PandaEntityRenderer(this));
        this.register(EntityType.PARROT, new ParrotEntityRenderer(this));
        this.register(EntityType.PHANTOM, new PhantomEntityRenderer(this));
        this.register(EntityType.PIG, new PigEntityRenderer(this));
        this.register(EntityType.PIGLIN, new PiglinEntityRenderer(this, false));
        this.register(EntityType.PIGLIN_BRUTE, new PiglinEntityRenderer(this, false));
        this.register(EntityType.PILLAGER, new PillagerEntityRenderer(this));
        this.register(EntityType.POLAR_BEAR, new PolarBearEntityRenderer(this));
        this.register(EntityType.POTION, new FlyingItemEntityRenderer(this, arg));
        this.register(EntityType.PUFFERFISH, new PufferfishEntityRenderer(this));
        this.register(EntityType.RABBIT, new RabbitEntityRenderer(this));
        this.register(EntityType.RAVAGER, new RavagerEntityRenderer(this));
        this.register(EntityType.SALMON, new SalmonEntityRenderer(this));
        this.register(EntityType.SHEEP, new SheepEntityRenderer(this));
        this.register(EntityType.SHULKER_BULLET, new ShulkerBulletEntityRenderer(this));
        this.register(EntityType.SHULKER, new ShulkerEntityRenderer(this));
        this.register(EntityType.SILVERFISH, new SilverfishEntityRenderer(this));
        this.register(EntityType.SKELETON_HORSE, new ZombieHorseEntityRenderer(this));
        this.register(EntityType.SKELETON, new SkeletonEntityRenderer(this));
        this.register(EntityType.SLIME, new SlimeEntityRenderer(this));
        this.register(EntityType.SMALL_FIREBALL, new FlyingItemEntityRenderer(this, arg, 0.75f, true));
        this.register(EntityType.SNOWBALL, new FlyingItemEntityRenderer(this, arg));
        this.register(EntityType.SNOW_GOLEM, new SnowGolemEntityRenderer(this));
        this.register(EntityType.SPAWNER_MINECART, new MinecartEntityRenderer(this));
        this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowEntityRenderer(this));
        this.register(EntityType.SPIDER, new SpiderEntityRenderer(this));
        this.register(EntityType.SQUID, new SquidEntityRenderer(this));
        this.register(EntityType.STRAY, new StrayEntityRenderer(this));
        this.register(EntityType.TNT_MINECART, new TntMinecartEntityRenderer(this));
        this.register(EntityType.TNT, new TntEntityRenderer(this));
        this.register(EntityType.TRADER_LLAMA, new LlamaEntityRenderer(this));
        this.register(EntityType.TRIDENT, new TridentEntityRenderer(this));
        this.register(EntityType.TROPICAL_FISH, new TropicalFishEntityRenderer(this));
        this.register(EntityType.TURTLE, new TurtleEntityRenderer(this));
        this.register(EntityType.VEX, new VexEntityRenderer(this));
        this.register(EntityType.VILLAGER, new VillagerEntityRenderer(this, arg2));
        this.register(EntityType.VINDICATOR, new VindicatorEntityRenderer(this));
        this.register(EntityType.WANDERING_TRADER, new WanderingTraderEntityRenderer(this));
        this.register(EntityType.WITCH, new WitchEntityRenderer(this));
        this.register(EntityType.WITHER, new WitherEntityRenderer(this));
        this.register(EntityType.WITHER_SKELETON, new WitherSkeletonEntityRenderer(this));
        this.register(EntityType.WITHER_SKULL, new WitherSkullEntityRenderer(this));
        this.register(EntityType.WOLF, new WolfEntityRenderer(this));
        this.register(EntityType.ZOGLIN, new ZoglinEntityRenderer(this));
        this.register(EntityType.ZOMBIE_HORSE, new ZombieHorseEntityRenderer(this));
        this.register(EntityType.ZOMBIE, new ZombieEntityRenderer(this));
        this.register(EntityType.ZOMBIFIED_PIGLIN, new PiglinEntityRenderer(this, true));
        this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerEntityRenderer(this, arg2));
        this.register(EntityType.STRIDER, new StriderEntityRenderer(this));
    }

    public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer arg2, ReloadableResourceManager arg3, TextRenderer arg4, GameOptions arg5) {
        this.textureManager = textureManager;
        this.textRenderer = arg4;
        this.gameOptions = arg5;
        this.registerRenderers(arg2, arg3);
        this.playerRenderer = new PlayerEntityRenderer(this);
        this.modelRenderers.put("default", this.playerRenderer);
        this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || this.renderers.containsKey(entityType)) continue;
            throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getId(entityType));
        }
    }

    public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            String string = ((AbstractClientPlayerEntity)entity).getModel();
            PlayerEntityRenderer lv = this.modelRenderers.get(string);
            if (lv != null) {
                return lv;
            }
            return this.playerRenderer;
        }
        return this.renderers.get(entity.getType());
    }

    public void configure(World world, Camera camera, Entity target) {
        this.world = world;
        this.camera = camera;
        this.rotation = camera.getRotation();
        this.targetedEntity = target;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public void setRenderShadows(boolean value) {
        this.renderShadows = value;
    }

    public void setRenderHitboxes(boolean value) {
        this.renderHitboxes = value;
    }

    public boolean shouldRenderHitboxes() {
        return this.renderHitboxes;
    }

    public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
        EntityRenderer<E> lv = this.getRenderer(entity);
        return lv.shouldRender(entity, frustum, x, y, z);
    }

    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderer<E> lv = this.getRenderer(entity);
        try {
            double m;
            float n;
            Vec3d lv2 = lv.getPositionOffset(entity, tickDelta);
            double j = x + lv2.getX();
            double k = y + lv2.getY();
            double l = z + lv2.getZ();
            matrices.push();
            matrices.translate(j, k, l);
            lv.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            if (entity.doesRenderOnFire()) {
                this.renderFire(matrices, vertexConsumers, entity);
            }
            matrices.translate(-lv2.getX(), -lv2.getY(), -lv2.getZ());
            if (this.gameOptions.entityShadows && this.renderShadows && lv.shadowRadius > 0.0f && !entity.isInvisible() && (n = (float)((1.0 - (m = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ())) / 256.0) * (double)lv.shadowOpacity)) > 0.0f) {
                EntityRenderDispatcher.renderShadow(matrices, vertexConsumers, entity, n, tickDelta, this.world, lv.shadowRadius);
            }
            if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                this.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
            }
            matrices.pop();
        }
        catch (Throwable throwable) {
            CrashReport lv3 = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection lv4 = lv3.addElement("Entity being rendered");
            entity.populateCrashReport(lv4);
            CrashReportSection lv5 = lv3.addElement("Renderer details");
            lv5.add("Assigned renderer", lv);
            lv5.add("Location", CrashReportSection.createPositionString(x, y, z));
            lv5.add("Rotation", Float.valueOf(yaw));
            lv5.add("Delta", Float.valueOf(tickDelta));
            throw new CrashException(lv3);
        }
    }

    private void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        float g = entity.getWidth() / 2.0f;
        this.drawBox(matrices, vertices, entity, 1.0f, 1.0f, 1.0f);
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
            double h = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
            for (EnderDragonPart lv : ((EnderDragonEntity)entity).getBodyParts()) {
                matrices.push();
                double i = d + MathHelper.lerp((double)tickDelta, lv.lastRenderX, lv.getX());
                double j = e + MathHelper.lerp((double)tickDelta, lv.lastRenderY, lv.getY());
                double k = h + MathHelper.lerp((double)tickDelta, lv.lastRenderZ, lv.getZ());
                matrices.translate(i, j, k);
                this.drawBox(matrices, vertices, lv, 0.25f, 1.0f, 0.0f);
                matrices.pop();
            }
        }
        if (entity instanceof LivingEntity) {
            float l = 0.01f;
            WorldRenderer.drawBox(matrices, vertices, -g, entity.getStandingEyeHeight() - 0.01f, -g, g, entity.getStandingEyeHeight() + 0.01f, g, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        Vec3d lv2 = entity.getRotationVec(tickDelta);
        Matrix4f lv3 = matrices.peek().getModel();
        vertices.vertex(lv3, 0.0f, entity.getStandingEyeHeight(), 0.0f).color(0, 0, 255, 255).next();
        vertices.vertex(lv3, (float)(lv2.x * 2.0), (float)((double)entity.getStandingEyeHeight() + lv2.y * 2.0), (float)(lv2.z * 2.0)).color(0, 0, 255, 255).next();
    }

    private void drawBox(MatrixStack matrix, VertexConsumer vertices, Entity entity, float red, float green, float blue) {
        Box lv = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrix, vertices, lv, red, green, blue, 1.0f);
    }

    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        Sprite lv = ModelLoader.FIRE_0.getSprite();
        Sprite lv2 = ModelLoader.FIRE_1.getSprite();
        matrices.push();
        float f = entity.getWidth() * 1.4f;
        matrices.scale(f, f, f);
        float g = 0.5f;
        float h = 0.0f;
        float i = entity.getHeight() / f;
        float j = 0.0f;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
        matrices.translate(0.0, 0.0, -0.3f + (float)((int)i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer lv3 = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        MatrixStack.Entry lv4 = matrices.peek();
        while (i > 0.0f) {
            Sprite lv5 = l % 2 == 0 ? lv : lv2;
            float m = lv5.getMinU();
            float n = lv5.getMinV();
            float o = lv5.getMaxU();
            float p = lv5.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, g - 0.0f, 0.0f - j, k, o, p);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, -g - 0.0f, 0.0f - j, k, m, p);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, -g - 0.0f, 1.4f - j, k, m, n);
            EntityRenderDispatcher.drawFireVertex(lv4, lv3, g - 0.0f, 1.4f - j, k, o, n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k += 0.03f;
            ++l;
        }
        matrices.pop();
    }

    private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
        vertices.vertex(entry.getModel(), x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(0, 10).light(240).normal(entry.getNormal(), 0.0f, 1.0f, 0.0f).next();
    }

    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
        MobEntity lv;
        float i = radius;
        if (entity instanceof MobEntity && (lv = (MobEntity)entity).isBaby()) {
            i *= 0.5f;
        }
        double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double j = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
        int k = MathHelper.floor(d - (double)i);
        int l = MathHelper.floor(d + (double)i);
        int m = MathHelper.floor(e - (double)i);
        int n = MathHelper.floor(e);
        int o = MathHelper.floor(j - (double)i);
        int p = MathHelper.floor(j + (double)i);
        MatrixStack.Entry lv2 = matrices.peek();
        VertexConsumer lv3 = vertexConsumers.getBuffer(SHADOW_LAYER);
        for (BlockPos lv4 : BlockPos.iterate(new BlockPos(k, m, o), new BlockPos(l, n, p))) {
            EntityRenderDispatcher.renderShadowPart(lv2, lv3, world, lv4, d, e, j, i, opacity);
        }
    }

    private static void renderShadowPart(MatrixStack.Entry entry, VertexConsumer vertices, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity) {
        BlockPos lv = pos.down();
        BlockState lv2 = world.getBlockState(lv);
        if (lv2.getRenderType() == BlockRenderType.INVISIBLE || world.getLightLevel(pos) <= 3) {
            return;
        }
        if (!lv2.isFullCube(world, lv)) {
            return;
        }
        VoxelShape lv3 = lv2.getOutlineShape(world, pos.down());
        if (lv3.isEmpty()) {
            return;
        }
        float i = (float)(((double)opacity - (y - (double)pos.getY()) / 2.0) * 0.5 * (double)world.getBrightness(pos));
        if (i >= 0.0f) {
            if (i > 1.0f) {
                i = 1.0f;
            }
            Box lv4 = lv3.getBoundingBox();
            double j = (double)pos.getX() + lv4.minX;
            double k = (double)pos.getX() + lv4.maxX;
            double l = (double)pos.getY() + lv4.minY;
            double m = (double)pos.getZ() + lv4.minZ;
            double n = (double)pos.getZ() + lv4.maxZ;
            float o = (float)(j - x);
            float p = (float)(k - x);
            float q = (float)(l - y);
            float r = (float)(m - z);
            float s = (float)(n - z);
            float t = -o / 2.0f / radius + 0.5f;
            float u = -p / 2.0f / radius + 0.5f;
            float v = -r / 2.0f / radius + 0.5f;
            float w = -s / 2.0f / radius + 0.5f;
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, o, q, r, t, v);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, o, q, s, t, w);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, p, q, s, u, w);
            EntityRenderDispatcher.drawShadowVertex(entry, vertices, i, p, q, r, u, v);
        }
    }

    private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, float alpha, float x, float y, float z, float u, float v) {
        vertices.vertex(entry.getModel(), x, y, z).color(1.0f, 1.0f, 1.0f, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(entry.getNormal(), 0.0f, 1.0f, 0.0f).next();
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }

    public double getSquaredDistanceToCamera(Entity entity) {
        return this.camera.getPos().squaredDistanceTo(entity.getPos());
    }

    public double getSquaredDistanceToCamera(double x, double y, double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }

    public Quaternion getRotation() {
        return this.rotation;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
}

