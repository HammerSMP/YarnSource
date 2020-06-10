/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.village;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerData {
    private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.VILLAGER_TYPE.fieldOf("type").withDefault(() -> VillagerType.PLAINS).forGetter(arg -> arg.type), (App)Registry.VILLAGER_PROFESSION.fieldOf("profession").withDefault(() -> VillagerProfession.NONE).forGetter(arg -> arg.profession), (App)Codec.INT.fieldOf("level").withDefault((Object)1).forGetter(arg -> arg.level)).apply((Applicative)instance, VillagerData::new));
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType arg, VillagerProfession arg2, int i) {
        this.type = arg;
        this.profession = arg2;
        this.level = Math.max(1, i);
    }

    public VillagerType getType() {
        return this.type;
    }

    public VillagerProfession getProfession() {
        return this.profession;
    }

    public int getLevel() {
        return this.level;
    }

    public VillagerData withType(VillagerType arg) {
        return new VillagerData(arg, this.profession, this.level);
    }

    public VillagerData withProfession(VillagerProfession arg) {
        return new VillagerData(this.type, arg, this.level);
    }

    public VillagerData withLevel(int i) {
        return new VillagerData(this.type, this.profession, i);
    }

    @Environment(value=EnvType.CLIENT)
    public static int getLowerLevelExperience(int i) {
        return VillagerData.canLevelUp(i) ? LEVEL_BASE_EXPERIENCE[i - 1] : 0;
    }

    public static int getUpperLevelExperience(int i) {
        return VillagerData.canLevelUp(i) ? LEVEL_BASE_EXPERIENCE[i] : 0;
    }

    public static boolean canLevelUp(int i) {
        return i >= 1 && i < 5;
    }
}

