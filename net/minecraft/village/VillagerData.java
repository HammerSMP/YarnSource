/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerData {
    private static final int[] LEVEL_BASE_EXPERIENCE = new int[]{0, 10, 70, 150, 250};
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType arg, VillagerProfession arg2, int i) {
        this.type = arg;
        this.profession = arg2;
        this.level = Math.max(1, i);
    }

    public VillagerData(Dynamic<?> dynamic) {
        this(Registry.VILLAGER_TYPE.get(Identifier.tryParse(dynamic.get("type").asString(""))), Registry.VILLAGER_PROFESSION.get(Identifier.tryParse(dynamic.get("profession").asString(""))), dynamic.get("level").asInt(1));
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

    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.VILLAGER_TYPE.getId(this.type).toString()), (Object)dynamicOps.createString("profession"), (Object)dynamicOps.createString(Registry.VILLAGER_PROFESSION.getId(this.profession).toString()), (Object)dynamicOps.createString("level"), (Object)dynamicOps.createInt(this.level)));
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

