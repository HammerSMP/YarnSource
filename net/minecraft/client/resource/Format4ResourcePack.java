/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class Format4ResourcePack
implements ResourcePack {
    private static final Map<String, Pair<ChestType, Identifier>> NEW_TO_OLD_CHEST_TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("textures/entity/chest/normal_left.png", new Pair((Object)ChestType.LEFT, (Object)new Identifier("textures/entity/chest/normal_double.png")));
        hashMap.put("textures/entity/chest/normal_right.png", new Pair((Object)ChestType.RIGHT, (Object)new Identifier("textures/entity/chest/normal_double.png")));
        hashMap.put("textures/entity/chest/normal.png", new Pair((Object)ChestType.SINGLE, (Object)new Identifier("textures/entity/chest/normal.png")));
        hashMap.put("textures/entity/chest/trapped_left.png", new Pair((Object)ChestType.LEFT, (Object)new Identifier("textures/entity/chest/trapped_double.png")));
        hashMap.put("textures/entity/chest/trapped_right.png", new Pair((Object)ChestType.RIGHT, (Object)new Identifier("textures/entity/chest/trapped_double.png")));
        hashMap.put("textures/entity/chest/trapped.png", new Pair((Object)ChestType.SINGLE, (Object)new Identifier("textures/entity/chest/trapped.png")));
        hashMap.put("textures/entity/chest/christmas_left.png", new Pair((Object)ChestType.LEFT, (Object)new Identifier("textures/entity/chest/christmas_double.png")));
        hashMap.put("textures/entity/chest/christmas_right.png", new Pair((Object)ChestType.RIGHT, (Object)new Identifier("textures/entity/chest/christmas_double.png")));
        hashMap.put("textures/entity/chest/christmas.png", new Pair((Object)ChestType.SINGLE, (Object)new Identifier("textures/entity/chest/christmas.png")));
        hashMap.put("textures/entity/chest/ender.png", new Pair((Object)ChestType.SINGLE, (Object)new Identifier("textures/entity/chest/ender.png")));
    });
    private static final List<String> BANNER_PATTERN_TYPES = Lists.newArrayList((Object[])new String[]{"base", "border", "bricks", "circle", "creeper", "cross", "curly_border", "diagonal_left", "diagonal_right", "diagonal_up_left", "diagonal_up_right", "flower", "globe", "gradient", "gradient_up", "half_horizontal", "half_horizontal_bottom", "half_vertical", "half_vertical_right", "mojang", "rhombus", "skull", "small_stripes", "square_bottom_left", "square_bottom_right", "square_top_left", "square_top_right", "straight_cross", "stripe_bottom", "stripe_center", "stripe_downleft", "stripe_downright", "stripe_left", "stripe_middle", "stripe_right", "stripe_top", "triangle_bottom", "triangle_top", "triangles_bottom", "triangles_top"});
    private static final Set<String> SHIELD_PATTERN_TEXTURES = BANNER_PATTERN_TYPES.stream().map(string -> "textures/entity/shield/" + string + ".png").collect(Collectors.toSet());
    private static final Set<String> BANNER_PATTERN_TEXTURES = BANNER_PATTERN_TYPES.stream().map(string -> "textures/entity/banner/" + string + ".png").collect(Collectors.toSet());
    public static final Identifier OLD_SHIELD_BASE_TEXTURE = new Identifier("textures/entity/shield_base.png");
    public static final Identifier OLD_BANNER_BASE_TEXTURE = new Identifier("textures/entity/banner_base.png");
    public static final Identifier IRON_GOLEM_TEXTURE = new Identifier("textures/entity/iron_golem.png");
    private final ResourcePack parent;

    public Format4ResourcePack(ResourcePack arg) {
        this.parent = arg;
    }

    @Override
    public InputStream openRoot(String string) throws IOException {
        return this.parent.openRoot(string);
    }

    @Override
    public boolean contains(ResourceType arg, Identifier arg2) {
        if (!"minecraft".equals(arg2.getNamespace())) {
            return this.parent.contains(arg, arg2);
        }
        String string = arg2.getPath();
        if ("textures/misc/enchanted_item_glint.png".equals(string)) {
            return false;
        }
        if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
            return this.parent.contains(arg, IRON_GOLEM_TEXTURE);
        }
        if ("textures/entity/conduit/wind.png".equals(string) || "textures/entity/conduit/wind_vertical.png".equals(string)) {
            return false;
        }
        if (SHIELD_PATTERN_TEXTURES.contains(string)) {
            return this.parent.contains(arg, OLD_SHIELD_BASE_TEXTURE) && this.parent.contains(arg, arg2);
        }
        if (BANNER_PATTERN_TEXTURES.contains(string)) {
            return this.parent.contains(arg, OLD_BANNER_BASE_TEXTURE) && this.parent.contains(arg, arg2);
        }
        Pair<ChestType, Identifier> pair = NEW_TO_OLD_CHEST_TEXTURES.get(string);
        if (pair != null && this.parent.contains(arg, (Identifier)pair.getSecond())) {
            return true;
        }
        return this.parent.contains(arg, arg2);
    }

    @Override
    public InputStream open(ResourceType arg, Identifier arg2) throws IOException {
        if (!"minecraft".equals(arg2.getNamespace())) {
            return this.parent.open(arg, arg2);
        }
        String string = arg2.getPath();
        if ("textures/entity/iron_golem/iron_golem.png".equals(string)) {
            return this.parent.open(arg, IRON_GOLEM_TEXTURE);
        }
        if (SHIELD_PATTERN_TEXTURES.contains(string)) {
            InputStream inputStream = Format4ResourcePack.openCroppedStream(this.parent.open(arg, OLD_SHIELD_BASE_TEXTURE), this.parent.open(arg, arg2), 64, 2, 2, 12, 22);
            if (inputStream != null) {
                return inputStream;
            }
        } else if (BANNER_PATTERN_TEXTURES.contains(string)) {
            InputStream inputStream2 = Format4ResourcePack.openCroppedStream(this.parent.open(arg, OLD_BANNER_BASE_TEXTURE), this.parent.open(arg, arg2), 64, 0, 0, 42, 41);
            if (inputStream2 != null) {
                return inputStream2;
            }
        } else {
            if ("textures/entity/enderdragon/dragon.png".equals(string) || "textures/entity/enderdragon/dragon_exploding.png".equals(string)) {
                try (NativeImage lv = NativeImage.read(this.parent.open(arg, arg2));){
                    int i = lv.getWidth() / 256;
                    for (int j = 88 * i; j < 200 * i; ++j) {
                        for (int k = 56 * i; k < 112 * i; ++k) {
                            lv.setPixelRgba(k, j, 0);
                        }
                    }
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(lv.getBytes());
                    return byteArrayInputStream;
                }
            }
            if ("textures/entity/conduit/closed_eye.png".equals(string) || "textures/entity/conduit/open_eye.png".equals(string)) {
                return Format4ResourcePack.method_24199(this.parent.open(arg, arg2));
            }
            Pair<ChestType, Identifier> pair = NEW_TO_OLD_CHEST_TEXTURES.get(string);
            if (pair != null) {
                ChestType lv2 = (ChestType)pair.getFirst();
                InputStream inputStream3 = this.parent.open(arg, (Identifier)pair.getSecond());
                if (lv2 == ChestType.SINGLE) {
                    return Format4ResourcePack.cropSingleChestTexture(inputStream3);
                }
                if (lv2 == ChestType.LEFT) {
                    return Format4ResourcePack.cropLeftChestTexture(inputStream3);
                }
                if (lv2 == ChestType.RIGHT) {
                    return Format4ResourcePack.cropRightChestTexture(inputStream3);
                }
            }
        }
        return this.parent.open(arg, arg2);
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static InputStream openCroppedStream(InputStream inputStream, InputStream inputStream2, int i, int j, int k, int l, int m) throws IOException {
        try (NativeImage lv = NativeImage.read(inputStream);){
            Throwable throwable = null;
            try (NativeImage lv2 = NativeImage.read(inputStream2);){
                int n = lv.getWidth();
                int o = lv.getHeight();
                if (n == lv2.getWidth()) {
                    if (o == lv2.getHeight()) {
                        try (NativeImage lv3 = new NativeImage(n, o, true);){
                            int p = n / i;
                            for (int q = k * p; q < m * p; ++q) {
                                for (int r = j * p; r < l * p; ++r) {
                                    int s = NativeImage.method_24033(lv2.getPixelRgba(r, q));
                                    int t = lv.getPixelRgba(r, q);
                                    lv3.setPixelRgba(r, q, NativeImage.method_24031(s, NativeImage.method_24035(t), NativeImage.method_24034(t), NativeImage.method_24033(t)));
                                }
                            }
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(lv3.getBytes());
                            return byteArrayInputStream;
                        }
                    }
                }
                {
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    catch (Throwable throwable3) {
                        throw throwable3;
                    }
                }
            }
        }
    }

    /*
     * Exception decompiling
     */
    public static InputStream method_24199(InputStream inputStream) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    /*
     * Exception decompiling
     */
    public static InputStream cropLeftChestTexture(InputStream inputStream) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    /*
     * Exception decompiling
     */
    public static InputStream cropRightChestTexture(InputStream inputStream) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    /*
     * Exception decompiling
     */
    public static InputStream cropSingleChestTexture(InputStream inputStream) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    @Override
    public Collection<Identifier> findResources(ResourceType arg, String string, String string2, int i, Predicate<String> predicate) {
        return this.parent.findResources(arg, string, string2, i, predicate);
    }

    @Override
    public Set<String> getNamespaces(ResourceType arg) {
        return this.parent.getNamespaces(arg);
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> arg) throws IOException {
        return this.parent.parseMetadata(arg);
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override
    public void close() {
        this.parent.close();
    }

    private static void loadBytes(NativeImage arg, NativeImage arg2, int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2) {
        m *= o;
        k *= o;
        l *= o;
        i *= o;
        j *= o;
        for (int p = 0; p < (n *= o); ++p) {
            for (int q = 0; q < m; ++q) {
                arg2.setPixelRgba(k + q, l + p, arg.getPixelRgba(i + (bl ? m - 1 - q : q), j + (bl2 ? n - 1 - p : p)));
            }
        }
    }
}

