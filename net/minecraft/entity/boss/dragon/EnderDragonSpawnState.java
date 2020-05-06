/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public enum EnderDragonSpawnState {
    START{

        @Override
        public void run(ServerWorld arg, EnderDragonFight arg2, List<EndCrystalEntity> list, int i, BlockPos arg3) {
            BlockPos lv = new BlockPos(0, 128, 0);
            for (EndCrystalEntity lv2 : list) {
                lv2.setBeamTarget(lv);
            }
            arg2.setSpawnState(PREPARING_TO_SUMMON_PILLARS);
        }
    }
    ,
    PREPARING_TO_SUMMON_PILLARS{

        @Override
        public void run(ServerWorld arg, EnderDragonFight arg2, List<EndCrystalEntity> list, int i, BlockPos arg3) {
            if (i < 100) {
                if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
                    arg.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                arg2.setSpawnState(SUMMONING_PILLARS);
            }
        }
    }
    ,
    SUMMONING_PILLARS{

        @Override
        public void run(ServerWorld arg, EnderDragonFight arg2, List<EndCrystalEntity> list, int i, BlockPos arg3) {
            boolean bl2;
            int j = 40;
            boolean bl = i % 40 == 0;
            boolean bl3 = bl2 = i % 40 == 39;
            if (bl || bl2) {
                int k = i / 40;
                List<EndSpikeFeature.Spike> list2 = EndSpikeFeature.getSpikes(arg);
                if (k < list2.size()) {
                    EndSpikeFeature.Spike lv = list2.get(k);
                    if (bl) {
                        for (EndCrystalEntity lv2 : list) {
                            lv2.setBeamTarget(new BlockPos(lv.getCenterX(), lv.getHeight() + 1, lv.getCenterZ()));
                        }
                    } else {
                        int l = 10;
                        for (BlockPos lv3 : BlockPos.iterate(new BlockPos(lv.getCenterX() - 10, lv.getHeight() - 10, lv.getCenterZ() - 10), new BlockPos(lv.getCenterX() + 10, lv.getHeight() + 10, lv.getCenterZ() + 10))) {
                            arg.removeBlock(lv3, false);
                        }
                        arg.createExplosion(null, (float)lv.getCenterX() + 0.5f, lv.getHeight(), (float)lv.getCenterZ() + 0.5f, 5.0f, Explosion.DestructionType.DESTROY);
                        EndSpikeFeatureConfig lv4 = new EndSpikeFeatureConfig(true, (List<EndSpikeFeature.Spike>)ImmutableList.of((Object)lv), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.configure(lv4).generate(arg, arg.getStructureAccessor(), arg.getChunkManager().getChunkGenerator(), new Random(), new BlockPos(lv.getCenterX(), 45, lv.getCenterZ()));
                    }
                } else if (bl) {
                    arg2.setSpawnState(SUMMONING_DRAGON);
                }
            }
        }
    }
    ,
    SUMMONING_DRAGON{

        @Override
        public void run(ServerWorld arg, EnderDragonFight arg2, List<EndCrystalEntity> list, int i, BlockPos arg3) {
            if (i >= 100) {
                arg2.setSpawnState(END);
                arg2.resetEndCrystals();
                for (EndCrystalEntity lv : list) {
                    lv.setBeamTarget(null);
                    arg.createExplosion(lv, lv.getX(), lv.getY(), lv.getZ(), 6.0f, Explosion.DestructionType.NONE);
                    lv.remove();
                }
            } else if (i >= 80) {
                arg.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if (i == 0) {
                for (EndCrystalEntity lv2 : list) {
                    lv2.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if (i < 5) {
                arg.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    }
    ,
    END{

        @Override
        public void run(ServerWorld arg, EnderDragonFight arg2, List<EndCrystalEntity> list, int i, BlockPos arg3) {
        }
    };


    public abstract void run(ServerWorld var1, EnderDragonFight var2, List<EndCrystalEntity> var3, int var4, BlockPos var5);
}

