/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements TextureTickListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Deprecated
    public static final Identifier BLOCK_ATLAS_TEX = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    @Deprecated
    public static final Identifier PARTICLE_ATLAS_TEX = new Identifier("textures/atlas/particles.png");
    private final List<Sprite> animatedSprites = Lists.newArrayList();
    private final Set<Identifier> spritesToLoad = Sets.newHashSet();
    private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
    private final Identifier id;
    private final int maxTextureSize;

    public SpriteAtlasTexture(Identifier arg) {
        this.id = arg;
        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager arg) throws IOException {
    }

    public void upload(Data arg) {
        this.spritesToLoad.clear();
        this.spritesToLoad.addAll(arg.spriteIds);
        LOGGER.info("Created: {}x{}x{} {}-atlas", (Object)arg.width, (Object)arg.height, (Object)arg.field_21795, (Object)this.id);
        TextureUtil.method_24959(this.getGlId(), arg.field_21795, arg.width, arg.height);
        this.clear();
        for (Sprite lv : arg.sprites) {
            this.sprites.put(lv.getId(), lv);
            try {
                lv.upload();
            }
            catch (Throwable throwable) {
                CrashReport lv2 = CrashReport.create(throwable, "Stitching texture atlas");
                CrashReportSection lv3 = lv2.addElement("Texture being stitched together");
                lv3.add("Atlas path", this.id);
                lv3.add("Sprite", lv);
                throw new CrashException(lv2);
            }
            if (!lv.isAnimated()) continue;
            this.animatedSprites.add(lv);
        }
    }

    public Data stitch(ResourceManager arg2, Stream<Identifier> stream, Profiler arg22, int i) {
        int q;
        arg22.push("preparing");
        Set<Identifier> set = stream.peek(arg -> {
            if (arg == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int j = this.maxTextureSize;
        TextureStitcher lv = new TextureStitcher(j, j, i);
        int k = Integer.MAX_VALUE;
        int l = 1 << i;
        arg22.swap("extracting_frames");
        for (Sprite.Info lv2 : this.loadSprites(arg2, set)) {
            k = Math.min(k, Math.min(lv2.getWidth(), lv2.getHeight()));
            int m = Math.min(Integer.lowestOneBit(lv2.getWidth()), Integer.lowestOneBit(lv2.getHeight()));
            if (m < l) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", (Object)lv2.getId(), (Object)lv2.getWidth(), (Object)lv2.getHeight(), (Object)MathHelper.log2(l), (Object)MathHelper.log2(m));
                l = m;
            }
            lv.add(lv2);
        }
        int n = Math.min(k, l);
        int o = MathHelper.log2(n);
        if (o < i) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", (Object)this.id, (Object)i, (Object)o, (Object)n);
            int p = o;
        } else {
            q = i;
        }
        arg22.swap("register");
        lv.add(MissingSprite.getMissingInfo());
        arg22.swap("stitching");
        try {
            lv.stitch();
        }
        catch (TextureStitcherCannotFitException lv3) {
            CrashReport lv4 = CrashReport.create(lv3, "Stitching");
            CrashReportSection lv5 = lv4.addElement("Stitcher");
            lv5.add("Sprites", lv3.getSprites().stream().map(arg -> String.format("%s[%dx%d]", arg.getId(), arg.getWidth(), arg.getHeight())).collect(Collectors.joining(",")));
            lv5.add("Max Texture Size", j);
            throw new CrashException(lv4);
        }
        arg22.swap("loading");
        List<Sprite> list = this.method_18161(arg2, lv, q);
        arg22.pop();
        return new Data(set, lv.getWidth(), lv.getHeight(), q, list);
    }

    private Collection<Sprite.Info> loadSprites(ResourceManager arg, Set<Identifier> set) {
        ArrayList list = Lists.newArrayList();
        ConcurrentLinkedQueue<Sprite.Info> concurrentLinkedQueue = new ConcurrentLinkedQueue<Sprite.Info>();
        for (Identifier lv : set) {
            if (MissingSprite.getMissingSpriteId().equals(lv)) continue;
            list.add(CompletableFuture.runAsync(() -> {
                void lv7;
                Identifier lv = this.getTexturePath(lv);
                try (Resource lv2 = arg.getResource(lv);){
                    PngFile lv3 = new PngFile(lv2.toString(), lv2.getInputStream());
                    AnimationResourceMetadata lv4 = lv2.getMetadata(AnimationResourceMetadata.READER);
                    if (lv4 == null) {
                        lv4 = AnimationResourceMetadata.EMPTY;
                    }
                    Pair<Integer, Integer> pair = lv4.method_24141(lv3.width, lv3.height);
                    Sprite.Info lv5 = new Sprite.Info(lv, (Integer)pair.getFirst(), (Integer)pair.getSecond(), lv4);
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)lv, (Object)runtimeException);
                    return;
                }
                catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)lv, (Object)iOException);
                    return;
                }
                concurrentLinkedQueue.add((Sprite.Info)lv7);
            }, Util.getServerWorkerExecutor()));
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return concurrentLinkedQueue;
    }

    private List<Sprite> method_18161(ResourceManager arg, TextureStitcher arg22, int i) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        ArrayList list = Lists.newArrayList();
        arg22.getStitchedSprites((arg2, j, k, l, m) -> {
            if (arg2 == MissingSprite.getMissingInfo()) {
                MissingSprite lv = MissingSprite.getMissingSprite(this, i, j, k, l, m);
                concurrentLinkedQueue.add(lv);
            } else {
                list.add(CompletableFuture.runAsync(() -> {
                    Sprite lv = this.loadSprite(arg, arg2, j, k, i, l, m);
                    if (lv != null) {
                        concurrentLinkedQueue.add(lv);
                    }
                }, Util.getServerWorkerExecutor()));
            }
        });
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentLinkedQueue);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Sprite loadSprite(ResourceManager arg, Sprite.Info arg2, int i, int j, int k, int l, int m) {
        Identifier lv = this.getTexturePath(arg2.getId());
        try (Resource lv2 = arg.getResource(lv);){
            NativeImage lv3 = NativeImage.read(lv2.getInputStream());
            Sprite sprite = new Sprite(this, arg2, k, i, j, l, m, lv3);
            return sprite;
        }
        catch (RuntimeException runtimeException) {
            LOGGER.error("Unable to parse metadata from {}", (Object)lv, (Object)runtimeException);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)lv, (Object)iOException);
            return null;
        }
    }

    private Identifier getTexturePath(Identifier arg) {
        return new Identifier(arg.getNamespace(), String.format("textures/%s%s", arg.getPath(), ".png"));
    }

    public void tickAnimatedSprites() {
        this.bindTexture();
        for (Sprite lv : this.animatedSprites) {
            lv.tickAnimation();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::tickAnimatedSprites);
        } else {
            this.tickAnimatedSprites();
        }
    }

    public Sprite getSprite(Identifier arg) {
        Sprite lv = this.sprites.get(arg);
        if (lv == null) {
            return this.sprites.get(MissingSprite.getMissingSpriteId());
        }
        return lv;
    }

    public void clear() {
        for (Sprite lv : this.sprites.values()) {
            lv.close();
        }
        this.sprites.clear();
        this.animatedSprites.clear();
    }

    public Identifier getId() {
        return this.id;
    }

    public void method_24198(Data arg) {
        this.setFilter(false, arg.field_21795 > 0);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Data {
        final Set<Identifier> spriteIds;
        final int width;
        final int height;
        final int field_21795;
        final List<Sprite> sprites;

        public Data(Set<Identifier> set, int i, int j, int k, List<Sprite> list) {
            this.spriteIds = set;
            this.width = i;
            this.height = j;
            this.field_21795 = k;
            this.sprites = list;
        }
    }
}

