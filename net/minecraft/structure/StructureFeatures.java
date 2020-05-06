/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatures {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<?> MINESHAFT = StructureFeatures.register("Mineshaft", Feature.MINESHAFT);
    public static final StructureFeature<?> PILLAGER_OUTPOST = StructureFeatures.register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
    public static final StructureFeature<?> FORTRESS = StructureFeatures.register("Fortress", Feature.NETHER_BRIDGE);
    public static final StructureFeature<?> STRONGHOLD = StructureFeatures.register("Stronghold", Feature.STRONGHOLD);
    public static final StructureFeature<?> JUNGLE_PYRAMID = StructureFeatures.register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
    public static final StructureFeature<?> OCEAN_RUIN = StructureFeatures.register("Ocean_Ruin", Feature.OCEAN_RUIN);
    public static final StructureFeature<?> DESERT_PYRAMID = StructureFeatures.register("Desert_Pyramid", Feature.DESERT_PYRAMID);
    public static final StructureFeature<?> IGLOO = StructureFeatures.register("Igloo", Feature.IGLOO);
    public static final StructureFeature<?> RUINED_PORTAL = StructureFeatures.register("Ruined_Portal", Feature.RUINED_PORTAL);
    public static final StructureFeature<?> SWAMP_HUT = StructureFeatures.register("Swamp_Hut", Feature.SWAMP_HUT);
    public static final StructureFeature<?> MONUMENT = StructureFeatures.register("Monument", Feature.OCEAN_MONUMENT);
    public static final StructureFeature<?> END_CITY = StructureFeatures.register("EndCity", Feature.END_CITY);
    public static final StructureFeature<?> MANSION = StructureFeatures.register("Mansion", Feature.WOODLAND_MANSION);
    public static final StructureFeature<?> BURIED_TREASURE = StructureFeatures.register("Buried_Treasure", Feature.BURIED_TREASURE);
    public static final StructureFeature<?> SHIPWRECK = StructureFeatures.register("Shipwreck", Feature.SHIPWRECK);
    public static final StructureFeature<?> VILLAGE = StructureFeatures.register("Village", Feature.VILLAGE);
    public static final StructureFeature<?> NETHER_FOSSIL = StructureFeatures.register("Nether_Fossil", Feature.NETHER_FOSSIL);
    public static final StructureFeature<?> BASTION_REMNANT = StructureFeatures.register("Bastion_Remnant", Feature.BASTION_REMNANT);

    private static StructureFeature<?> register(String string, StructureFeature<?> arg) {
        return Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), arg);
    }

    public static void initialize() {
    }

    @Nullable
    public static StructureStart readStructureStart(ChunkGenerator<?> arg, StructureManager arg2, CompoundTag arg3) {
        String string = arg3.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        StructureFeature<?> lv = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
        if (lv == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        int i = arg3.getInt("ChunkX");
        int j = arg3.getInt("ChunkZ");
        int k = arg3.getInt("references");
        BlockBox lv2 = arg3.contains("BB") ? new BlockBox(arg3.getIntArray("BB")) : BlockBox.empty();
        ListTag lv3 = arg3.getList("Children", 10);
        try {
            StructureStart lv4 = lv.getStructureStartFactory().create(lv, i, j, lv2, k, arg.getSeed());
            for (int l = 0; l < lv3.size(); ++l) {
                CompoundTag lv5 = lv3.getCompound(l);
                String string2 = lv5.getString("id");
                StructurePieceType lv6 = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
                if (lv6 == null) {
                    LOGGER.error("Unknown structure piece id: {}", (Object)string2);
                    continue;
                }
                try {
                    StructurePiece lv7 = lv6.load(arg2, lv5);
                    lv4.children.add(lv7);
                    continue;
                }
                catch (Exception exception) {
                    LOGGER.error("Exception loading structure piece with id {}", (Object)string2, (Object)exception);
                }
            }
            return lv4;
        }
        catch (Exception exception2) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception2);
            return null;
        }
    }
}

