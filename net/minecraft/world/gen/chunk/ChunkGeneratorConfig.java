/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkGeneratorConfig {
    protected int villageDistance = 32;
    protected final int villageSeparation = 8;
    protected int oceanMonumentSpacing = 32;
    protected int oceanMonumentSeparation = 5;
    protected int strongholdDistance = 32;
    protected int strongholdCount = 128;
    protected int strongholdSpread = 3;
    protected int templeDistance = 32;
    protected final int templeSeparation = 8;
    protected final int shipwreckSpacing = 20;
    protected final int shipwreckSeparation = 8;
    protected int endCityDistance = 20;
    protected final int endCitySeparation = 11;
    protected final int oceanRuinSpacing = 24;
    protected final int oceanRuinSeparation = 4;
    protected int mansionDistance = 80;
    protected final int mansionSeparation = 20;
    protected final int netherStructureSpacing = 30;
    protected final int netherStructureSeparation = 4;
    protected final int netherStructureSeedModifier = 30084232;
    protected int ruinedPortalSpacing = 40;
    protected final int netherRuinedPortalSeparation = 15;
    protected int netherRuinedPortalSpacing = 25;
    protected final int ruinedPortalSeparation = 10;
    protected BlockState defaultBlock = Blocks.STONE.getDefaultState();
    protected BlockState defaultFluid = Blocks.WATER.getDefaultState();

    public int getNetherStructureSpacing() {
        return 30;
    }

    public int getNetherStructureSeparation() {
        return 4;
    }

    public int getNetherStructureSeedModifier() {
        return 30084232;
    }

    public int getVillageDistance() {
        return this.villageDistance;
    }

    public int getVillageSeparation() {
        return 8;
    }

    public int getOceanMonumentSpacing() {
        return this.oceanMonumentSpacing;
    }

    public int getOceanMonumentSeparation() {
        return this.oceanMonumentSeparation;
    }

    public int getStrongholdDistance() {
        return this.strongholdDistance;
    }

    public int getStrongholdCount() {
        return this.strongholdCount;
    }

    public int getStrongholdSpread() {
        return this.strongholdSpread;
    }

    public int getTempleDistance() {
        return this.templeDistance;
    }

    public int getTempleSeparation() {
        return 8;
    }

    public int getShipwreckSpacing() {
        return 24;
    }

    public int getShipwreckSeparation() {
        return 4;
    }

    public int getOceanRuinSpacing() {
        return 20;
    }

    public int getOceanRuinSeparation() {
        return 8;
    }

    public int getEndCityDistance() {
        return this.endCityDistance;
    }

    public int getEndCitySeparation() {
        return 11;
    }

    public int getMansionDistance() {
        return this.mansionDistance;
    }

    public int getMansionSeparation() {
        return 20;
    }

    public int getRuinedPortalSpacing(boolean bl) {
        return bl ? this.netherRuinedPortalSpacing : this.ruinedPortalSpacing;
    }

    public int getRuinedPortalSeparation(boolean bl) {
        return bl ? 10 : 15;
    }

    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }

    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }

    public void setDefaultBlock(BlockState arg) {
        this.defaultBlock = arg;
    }

    public void setDefaultFluid(BlockState arg) {
        this.defaultFluid = arg;
    }

    public int getBedrockCeilingY() {
        return 0;
    }

    public int getBedrockFloorY() {
        return 256;
    }
}

