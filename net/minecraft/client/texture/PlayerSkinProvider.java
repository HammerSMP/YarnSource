/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager arg, File file, MinecraftSessionService minecraftSessionService) {
        this.textureManager = arg;
        this.skinCacheDir = file;
        this.sessionService = minecraftSessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile gameProfile) throws Exception {
                try {
                    return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
                }
                catch (Throwable throwable) {
                    return Maps.newHashMap();
                }
            }

            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((GameProfile)object);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(minecraftProfileTexture, type, null);
    }

    public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureAvailableCallback arg) {
        String string = Hashing.sha1().hashUnencodedChars((CharSequence)minecraftProfileTexture.getHash()).toString();
        Identifier lv = new Identifier("skins/" + string);
        AbstractTexture lv2 = this.textureManager.getTexture(lv);
        if (lv2 != null) {
            if (arg != null) {
                arg.onSkinTextureAvailable(type, lv, minecraftProfileTexture);
            }
        } else {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture lv3 = new PlayerSkinTexture(file2, minecraftProfileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (arg != null) {
                    arg.onSkinTextureAvailable(type, lv, minecraftProfileTexture);
                }
            });
            this.textureManager.registerTexture(lv, lv3);
        }
        return lv;
    }

    public void loadSkin(GameProfile gameProfile, SkinTextureAvailableCallback arg, boolean bl) {
        Runnable runnable = () -> {
            HashMap map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(gameProfile, bl));
            }
            catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                gameProfile.getProperties().clear();
                if (gameProfile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    gameProfile.getProperties().putAll((Multimap)MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(this.sessionService.getTextures(gameProfile, false));
                } else {
                    this.sessionService.fillProfileProperties(gameProfile, bl);
                    try {
                        map.putAll(this.sessionService.getTextures(gameProfile, bl));
                    }
                    catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of((Object)MinecraftProfileTexture.Type.SKIN, (Object)MinecraftProfileTexture.Type.CAPE).forEach(type -> {
                if (map.containsKey(type)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(type), (MinecraftProfileTexture.Type)type, arg);
                }
            })));
        };
        Util.getServerWorkerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
        return (Map)this.skinCache.getUnchecked((Object)gameProfile);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

