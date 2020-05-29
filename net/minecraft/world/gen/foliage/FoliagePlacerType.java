/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.JungleFoliagePlacer;
import net.minecraft.world.gen.feature.MegaPineFoliagePlacer;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;

public class FoliagePlacerType<P extends FoliagePlacer> {
    public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = FoliagePlacerType.method_28850("blob_foliage_placer", BlobFoliagePlacer.CODEC);
    public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = FoliagePlacerType.method_28850("spruce_foliage_placer", SpruceFoliagePlacer.field_24936);
    public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = FoliagePlacerType.method_28850("pine_foliage_placer", PineFoliagePlacer.CODEC);
    public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = FoliagePlacerType.method_28850("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
    public static final FoliagePlacerType<BushFoliagePlacer> BUSH_FOLIAGE_PLACER = FoliagePlacerType.method_28850("bush_foliage_placer", BushFoliagePlacer.CODEC);
    public static final FoliagePlacerType<LargeOakFoliagePlacer> FANCY_FOLIAGE_PLACER = FoliagePlacerType.method_28850("fancy_foliage_placer", LargeOakFoliagePlacer.CODEC);
    public static final FoliagePlacerType<JungleFoliagePlacer> JUNGLE_FOLIAGE_PLACER = FoliagePlacerType.method_28850("jungle_foliage_placer", JungleFoliagePlacer.CODEC);
    public static final FoliagePlacerType<MegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER = FoliagePlacerType.method_28850("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
    public static final FoliagePlacerType<DarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER = FoliagePlacerType.method_28850("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
    private final Codec<P> field_24932;

    private static <P extends FoliagePlacer> FoliagePlacerType<P> method_28850(String string, Codec<P> codec) {
        return Registry.register(Registry.FOLIAGE_PLACER_TYPE, string, new FoliagePlacerType<P>(codec));
    }

    private FoliagePlacerType(Codec<P> codec) {
        this.field_24932 = codec;
    }

    public Codec<P> method_28849() {
        return this.field_24932;
    }
}

