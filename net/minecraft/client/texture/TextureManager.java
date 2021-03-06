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
import net.minecraft.client.realms.RealmsMainScreen;
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

    public TextureManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
    }

    public void bindTexture(Identifier id) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bindTextureInner(id));
        } else {
            this.bindTextureInner(id);
        }
    }

    private void bindTextureInner(Identifier id) {
        AbstractTexture lv = this.textures.get(id);
        if (lv == null) {
            lv = new ResourceTexture(id);
            this.registerTexture(id, lv);
        }
        lv.bindTexture();
    }

    public void registerTexture(Identifier arg, AbstractTexture arg2) {
        AbstractTexture lv = this.textures.put(arg, arg2 = this.method_24303(arg, arg2));
        if (lv != arg2) {
            if (lv != null && lv != MissingSprite.getMissingSpriteTexture()) {
                this.tickListeners.remove(lv);
                this.method_30299(arg, lv);
            }
            if (arg2 instanceof TextureTickListener) {
                this.tickListeners.add((TextureTickListener)((Object)arg2));
            }
        }
    }

    private void method_30299(Identifier arg, AbstractTexture arg2) {
        if (arg2 != MissingSprite.getMissingSpriteTexture()) {
            try {
                arg2.close();
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to close texture {}", (Object)arg, (Object)exception);
            }
        }
        arg2.clearGlId();
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
            lv2.add("Resource location", arg);
            lv2.add("Texture object class", () -> arg2.getClass().getName());
            throw new CrashException(lv);
        }
    }

    @Nullable
    public AbstractTexture getTexture(Identifier id) {
        return this.textures.get(id);
    }

    public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
        Integer integer = this.dynamicIdCounters.get(prefix);
        if (integer == null) {
            integer = 1;
        } else {
            Integer n = integer;
            Integer n2 = integer = Integer.valueOf(integer + 1);
        }
        this.dynamicIdCounters.put(prefix, integer);
        Identifier lv = new Identifier(String.format("dynamic/%s_%d", prefix, integer));
        this.registerTexture(lv, texture);
        return lv;
    }

    public CompletableFuture<Void> loadTextureAsync(Identifier id, Executor executor) {
        if (!this.textures.containsKey(id)) {
            AsyncTexture lv = new AsyncTexture(this.resourceContainer, id, executor);
            this.textures.put(id, lv);
            return lv.getLoadCompleteFuture().thenRunAsync(() -> this.registerTexture(id, lv), TextureManager::runOnRenderThread);
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

    public void destroyTexture(Identifier id) {
        AbstractTexture lv = this.getTexture(id);
        if (lv != null) {
            TextureUtil.deleteId(lv.getGlId());
        }
    }

    @Override
    public void close() {
        this.textures.forEach((arg_0, arg_1) -> this.method_30299(arg_0, arg_1));
        this.textures.clear();
        this.tickListeners.clear();
        this.dynamicIdCounters.clear();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return ((CompletableFuture)CompletableFuture.allOf(TitleScreen.loadTexturesAsync(this, prepareExecutor), this.loadTextureAsync(AbstractButtonWidget.WIDGETS_LOCATION, prepareExecutor)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
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
                lv2.registerTexture(this, manager, lv, applyExecutor);
            }
        }, runnable -> RenderSystem.recordRenderCall(runnable::run));
    }
}

