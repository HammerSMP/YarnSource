/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.AsyncTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TextureManager
implements ResourceReloadListener,
TextureTickListener,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_IDENTIFIER = new Identifier("");
    private final Map<Identifier, AbstractTexture> textures = Maps.newHashMap();
    private final Set<TextureTickListener> tickListeners = Sets.newHashSet();
    private final Map<String, Integer> dynamicIdCounters = Maps.newHashMap();
    private final ResourceManager resourceContainer;

    public TextureManager(ResourceManager arg) {
        this.resourceContainer = arg;
    }

    public void bindTexture(Identifier arg) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bindTextureInner(arg));
        } else {
            this.bindTextureInner(arg);
        }
    }

    private void bindTextureInner(Identifier arg) {
        AbstractTexture lv = this.textures.get(arg);
        if (lv == null) {
            lv = new ResourceTexture(arg);
            this.registerTexture(arg, lv);
        }
        lv.bindTexture();
    }

    public void registerTexture(Identifier arg, AbstractTexture arg2) {
        AbstractTexture lv = this.textures.put(arg, arg2 = this.method_24303(arg, arg2));
        if (lv != arg2) {
            if (lv != null && lv != MissingSprite.getMissingSpriteTexture()) {
                lv.clearGlId();
                this.tickListeners.remove(lv);
            }
            if (arg2 instanceof TextureTickListener) {
                this.tickListeners.add((TextureTickListener)((Object)arg2));
            }
        }
    }

    private AbstractTexture method_24303(Identifier arg, AbstractTexture arg2) {
        try {
            arg2.load(this.resourceContainer);
            return arg2;
        }
        catch (IOException iOException) {
            if (arg != MISSING_IDENTIFIER) {
                LOGGER.warn("Failed to load texture: {}", (Object)arg, (Object)iOException);
            }
            return MissingSprite.getMissingSpriteTexture();
        }
        catch (Throwable throwable) {
            CrashReport lv = CrashReport.create(throwable, "Registering texture");
            CrashReportSection lv2 = lv.addElement("Resource location being registered");
            AbstractTexture lv3 = arg2;
            lv2.add("Resource location", arg);
            lv2.add("Texture object class", () -> lv3.getClass().getName());
            throw new CrashException(lv);
        }
    }

    @Nullable
    public AbstractTexture getTexture(Identifier arg) {
        return this.textures.get(arg);
    }

    public Identifier registerDynamicTexture(String string, NativeImageBackedTexture arg) {
        Integer integer = this.dynamicIdCounters.get(string);
        if (integer == null) {
            integer = 1;
        } else {
            Integer n = integer;
            Integer n2 = integer = Integer.valueOf(integer + 1);
        }
        this.dynamicIdCounters.put(string, integer);
        Identifier lv = new Identifier(String.format("dynamic/%s_%d", string, integer));
        this.registerTexture(lv, arg);
        return lv;
    }

    public CompletableFuture<Void> loadTextureAsync(Identifier arg, Executor executor) {
        if (!this.textures.containsKey(arg)) {
            AsyncTexture lv = new AsyncTexture(this.resourceContainer, arg, executor);
            this.textures.put(arg, lv);
            return lv.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(arg, lv), TextureManager::runOnRenderThread);
        }
        return CompletableFuture.completedFuture(null);
    }

    private static void runOnRenderThread(Runnable runnable) {
        MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(runnable::run));
    }

    @Override
    public void tick() {
        for (TextureTickListener lv : this.tickListeners) {
            lv.tick();
        }
    }

    public void destroyTexture(Identifier arg) {
        AbstractTexture lv = this.getTexture(arg);
        if (lv != null) {
            TextureUtil.method_24957(lv.getGlId());
        }
    }

    @Override
    public void close() {
        this.textures.values().forEach(AbstractTexture::clearGlId);
        this.textures.clear();
        this.tickListeners.clear();
        this.dynamicIdCounters.clear();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        return ((CompletableFuture)CompletableFuture.allOf(TitleScreen.loadTexturesAsync(this, executor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, executor)).thenCompose(arg::whenPrepared)).thenAcceptAsync(void_ -> {
            MissingSprite.getMissingSpriteTexture();
            RealmsMainScreen.method_23765(this.resourceContainer);
            Iterator<Map.Entry<Identifier, AbstractTexture>> iterator = this.textures.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, AbstractTexture> entry = iterator.next();
                Identifier lv = entry.getKey();
                AbstractTexture lv2 = entry.getValue();
                if (lv2 == MissingSprite.getMissingSpriteTexture() && !lv.equals(MissingSprite.getMissingSpriteId())) {
                    iterator.remove();
                    continue;
                }
                lv2.registerTexture(this, arg2, lv, executor2);
            }
        }, runnable -> RenderSystem.recordRenderCall(runnable::run));
    }
}

