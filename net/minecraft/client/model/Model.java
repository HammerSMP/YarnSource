/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class Model
implements Consumer<ModelPart> {
    protected final Function<Identifier, RenderLayer> layerFactory;
    public int textureWidth = 64;
    public int textureHeight = 32;

    public Model(Function<Identifier, RenderLayer> function) {
        this.layerFactory = function;
    }

    @Override
    public void accept(ModelPart arg) {
    }

    public final RenderLayer getLayer(Identifier arg) {
        return this.layerFactory.apply(arg);
    }

    public abstract void render(MatrixStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8);

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((ModelPart)object);
    }
}

