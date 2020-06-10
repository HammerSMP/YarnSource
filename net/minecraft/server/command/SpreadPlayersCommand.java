/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.teams", object, object2, object3, object4));
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableText("commands.spreadplayers.failed.entities", object, object2, object3, object4));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("spreadplayers").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("center", Vec2ArgumentType.vec2()).then(CommandManager.argument("spreadDistance", FloatArgumentType.floatArg((float)0.0f)).then(((RequiredArgumentBuilder)CommandManager.argument("maxRange", FloatArgumentType.floatArg((float)1.0f)).then(CommandManager.argument("respectTeams", BoolArgumentType.bool()).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> SpreadPlayersCommand.execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "center"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"maxRange"), 256, BoolArgumentType.getBool((CommandContext)commandContext, (String)"respectTeams"), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))))).then(CommandManager.literal("under").then(CommandManager.argument("maxHeight", IntegerArgumentType.integer((int)0)).then(CommandManager.argument("respectTeams", BoolArgumentType.bool()).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> SpreadPlayersCommand.execute((ServerCommandSource)commandContext.getSource(), Vec2ArgumentType.getVec2((CommandContext<ServerCommandSource>)commandContext, "center"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"maxRange"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"maxHeight"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"respectTeams"), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets")))))))))));
    }

    private static int execute(ServerCommandSource arg, Vec2f arg2, float f, float g, int i, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
        Random random = new Random();
        double d = arg2.x - g;
        double e = arg2.y - g;
        double h = arg2.x + g;
        double j = arg2.y + g;
        Pile[] lvs = SpreadPlayersCommand.makePiles(random, bl ? SpreadPlayersCommand.getPileCountRespectingTeams(collection) : collection.size(), d, e, h, j);
        SpreadPlayersCommand.spread(arg2, f, arg.getWorld(), random, d, e, h, j, i, lvs, bl);
        double k = SpreadPlayersCommand.getMinDistance(collection, arg.getWorld(), lvs, i, bl);
        arg.sendFeedback(new TranslatableText("commands.spreadplayers.success." + (bl ? "teams" : "entities"), lvs.length, Float.valueOf(arg2.x), Float.valueOf(arg2.y), String.format(Locale.ROOT, "%.2f", k)), true);
        return lvs.length;
    }

    private static int getPileCountRespectingTeams(Collection<? extends Entity> collection) {
        HashSet set = Sets.newHashSet();
        for (Entity entity : collection) {
            if (entity instanceof PlayerEntity) {
                set.add(entity.getScoreboardTeam());
                continue;
            }
            set.add(null);
        }
        return set.size();
    }

    private static void spread(Vec2f arg, double d, ServerWorld arg2, Random random, double e, double f, double g, double h, int i, Pile[] args, boolean bl) throws CommandSyntaxException {
        int k;
        boolean bl2 = true;
        double j = 3.4028234663852886E38;
        for (k = 0; k < 10000 && bl2; ++k) {
            bl2 = false;
            j = 3.4028234663852886E38;
            for (int l = 0; l < args.length; ++l) {
                Pile lv = args[l];
                int m = 0;
                Pile lv2 = new Pile();
                for (int n = 0; n < args.length; ++n) {
                    if (l == n) continue;
                    Pile lv3 = args[n];
                    double o = lv.getDistance(lv3);
                    j = Math.min(o, j);
                    if (!(o < d)) continue;
                    ++m;
                    lv2.x = lv2.x + (lv3.x - lv.x);
                    lv2.z = lv2.z + (lv3.z - lv.z);
                }
                if (m > 0) {
                    lv2.x = lv2.x / (double)m;
                    lv2.z = lv2.z / (double)m;
                    double p = lv2.absolute();
                    if (p > 0.0) {
                        lv2.normalize();
                        lv.subtract(lv2);
                    } else {
                        lv.setPileLocation(random, e, f, g, h);
                    }
                    bl2 = true;
                }
                if (!lv.clamp(e, f, g, h)) continue;
                bl2 = true;
            }
            if (bl2) continue;
            for (Pile lv4 : args) {
                if (lv4.isSafe(arg2, i)) continue;
                lv4.setPileLocation(random, e, f, g, h);
                bl2 = true;
            }
        }
        if (j == 3.4028234663852886E38) {
            j = 0.0;
        }
        if (k >= 10000) {
            if (bl) {
                throw FAILED_TEAMS_EXCEPTION.create((Object)args.length, (Object)Float.valueOf(arg.x), (Object)Float.valueOf(arg.y), (Object)String.format(Locale.ROOT, "%.2f", j));
            }
            throw FAILED_ENTITIES_EXCEPTION.create((Object)args.length, (Object)Float.valueOf(arg.x), (Object)Float.valueOf(arg.y), (Object)String.format(Locale.ROOT, "%.2f", j));
        }
    }

    private static double getMinDistance(Collection<? extends Entity> collection, ServerWorld arg, Pile[] args, int i, boolean bl) {
        double d = 0.0;
        int j = 0;
        HashMap map = Maps.newHashMap();
        for (Entity entity : collection) {
            Pile lv4;
            if (bl) {
                AbstractTeam lv2;
                AbstractTeam abstractTeam = lv2 = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
                if (!map.containsKey(lv2)) {
                    map.put(lv2, args[j++]);
                }
                Pile lv3 = (Pile)map.get(lv2);
            } else {
                lv4 = args[j++];
            }
            entity.teleport((double)MathHelper.floor(lv4.x) + 0.5, lv4.getY(arg, i), (double)MathHelper.floor(lv4.z) + 0.5);
            double e = Double.MAX_VALUE;
            for (Pile lv5 : args) {
                if (lv4 == lv5) continue;
                double f = lv4.getDistance(lv5);
                e = Math.min(f, e);
            }
            d += e;
        }
        if (collection.size() < 2) {
            return 0.0;
        }
        return d /= (double)collection.size();
    }

    private static Pile[] makePiles(Random random, int i, double d, double e, double f, double g) {
        Pile[] lvs = new Pile[i];
        for (int j = 0; j < lvs.length; ++j) {
            Pile lv = new Pile();
            lv.setPileLocation(random, d, e, f, g);
            lvs[j] = lv;
        }
        return lvs;
    }

    static class Pile {
        private double x;
        private double z;

        Pile() {
        }

        double getDistance(Pile arg) {
            double d = this.x - arg.x;
            double e = this.z - arg.z;
            return Math.sqrt(d * d + e * e);
        }

        void normalize() {
            double d = this.absolute();
            this.x /= d;
            this.z /= d;
        }

        float absolute() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Pile arg) {
            this.x -= arg.x;
            this.z -= arg.z;
        }

        public boolean clamp(double d, double e, double f, double g) {
            boolean bl = false;
            if (this.x < d) {
                this.x = d;
                bl = true;
            } else if (this.x > f) {
                this.x = f;
                bl = true;
            }
            if (this.z < e) {
                this.z = e;
                bl = true;
            } else if (this.z > g) {
                this.z = g;
                bl = true;
            }
            return bl;
        }

        public int getY(BlockView arg, int i) {
            BlockPos.Mutable lv = new BlockPos.Mutable(this.x, (double)(i + 1), this.z);
            boolean bl = arg.getBlockState(lv).isAir();
            lv.move(Direction.DOWN);
            boolean bl2 = arg.getBlockState(lv).isAir();
            while (lv.getY() > 0) {
                lv.move(Direction.DOWN);
                boolean bl3 = arg.getBlockState(lv).isAir();
                if (!bl3 && bl2 && bl) {
                    return lv.getY() + 1;
                }
                bl = bl2;
                bl2 = bl3;
            }
            return i + 1;
        }

        public boolean isSafe(BlockView arg, int i) {
            BlockPos lv = new BlockPos(this.x, (double)(this.getY(arg, i) - 1), this.z);
            BlockState lv2 = arg.getBlockState(lv);
            Material lv3 = lv2.getMaterial();
            return lv.getY() < i && !lv3.isLiquid() && lv3 != Material.FIRE;
        }

        public void setPileLocation(Random random, double d, double e, double f, double g) {
            this.x = MathHelper.nextDouble(random, d, f);
            this.z = MathHelper.nextDouble(random, e, g);
        }
    }
}

