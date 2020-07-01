/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.FloatRangeArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.NumberRange;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class EntitySelectorOptions {
    private static final Map<String, SelectorOption> options = Maps.newHashMap();
    public static final DynamicCommandExceptionType UNKNOWN_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.unknown", object));
    public static final DynamicCommandExceptionType INAPPLICABLE_OPTION_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.inapplicable", object));
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType TOO_SMALL_LEVEL_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType IRREVERSIBLE_SORT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.sort.irreversible", object));
    public static final DynamicCommandExceptionType INVALID_MODE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.mode.invalid", object));
    public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.entity.options.type.invalid", object));

    private static void putOption(String string, SelectorHandler arg, Predicate<EntitySelectorReader> predicate, Text arg2) {
        options.put(string, new SelectorOption(arg, predicate, arg2));
    }

    public static void register() {
        if (!options.isEmpty()) {
            return;
        }
        EntitySelectorOptions.putOption("name", arg2 -> {
            int i = arg2.getReader().getCursor();
            boolean bl = arg2.readNegationCharacter();
            String string = arg2.getReader().readString();
            if (arg2.excludesName() && !bl) {
                arg2.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)arg2.getReader(), (Object)"name");
            }
            if (bl) {
                arg2.setExcludesName(true);
            } else {
                arg2.setSelectsName(true);
            }
            arg2.setPredicate(arg -> arg.getName().getString().equals(string) != bl);
        }, arg -> !arg.selectsName(), new TranslatableText("argument.entity.options.name.description"));
        EntitySelectorOptions.putOption("distance", arg -> {
            int i = arg.getReader().getCursor();
            NumberRange.FloatRange lv = NumberRange.FloatRange.parse(arg.getReader());
            if (lv.getMin() != null && ((Float)lv.getMin()).floatValue() < 0.0f || lv.getMax() != null && ((Float)lv.getMax()).floatValue() < 0.0f) {
                arg.getReader().setCursor(i);
                throw NEGATIVE_DISTANCE_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader());
            }
            arg.setDistance(lv);
            arg.setLocalWorldOnly();
        }, arg -> arg.getDistance().isDummy(), new TranslatableText("argument.entity.options.distance.description"));
        EntitySelectorOptions.putOption("level", arg -> {
            int i = arg.getReader().getCursor();
            NumberRange.IntRange lv = NumberRange.IntRange.parse(arg.getReader());
            if (lv.getMin() != null && (Integer)lv.getMin() < 0 || lv.getMax() != null && (Integer)lv.getMax() < 0) {
                arg.getReader().setCursor(i);
                throw NEGATIVE_LEVEL_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader());
            }
            arg.setLevelRange(lv);
            arg.setIncludesNonPlayers(false);
        }, arg -> arg.getLevelRange().isDummy(), new TranslatableText("argument.entity.options.level.description"));
        EntitySelectorOptions.putOption("x", arg -> {
            arg.setLocalWorldOnly();
            arg.setX(arg.getReader().readDouble());
        }, arg -> arg.getX() == null, new TranslatableText("argument.entity.options.x.description"));
        EntitySelectorOptions.putOption("y", arg -> {
            arg.setLocalWorldOnly();
            arg.setY(arg.getReader().readDouble());
        }, arg -> arg.getY() == null, new TranslatableText("argument.entity.options.y.description"));
        EntitySelectorOptions.putOption("z", arg -> {
            arg.setLocalWorldOnly();
            arg.setZ(arg.getReader().readDouble());
        }, arg -> arg.getZ() == null, new TranslatableText("argument.entity.options.z.description"));
        EntitySelectorOptions.putOption("dx", arg -> {
            arg.setLocalWorldOnly();
            arg.setDx(arg.getReader().readDouble());
        }, arg -> arg.getDx() == null, new TranslatableText("argument.entity.options.dx.description"));
        EntitySelectorOptions.putOption("dy", arg -> {
            arg.setLocalWorldOnly();
            arg.setDy(arg.getReader().readDouble());
        }, arg -> arg.getDy() == null, new TranslatableText("argument.entity.options.dy.description"));
        EntitySelectorOptions.putOption("dz", arg -> {
            arg.setLocalWorldOnly();
            arg.setDz(arg.getReader().readDouble());
        }, arg -> arg.getDz() == null, new TranslatableText("argument.entity.options.dz.description"));
        EntitySelectorOptions.putOption("x_rotation", arg -> arg.setPitchRange(FloatRangeArgument.parse(arg.getReader(), true, MathHelper::wrapDegrees)), arg -> arg.getPitchRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.x_rotation.description"));
        EntitySelectorOptions.putOption("y_rotation", arg -> arg.setYawRange(FloatRangeArgument.parse(arg.getReader(), true, MathHelper::wrapDegrees)), arg -> arg.getYawRange() == FloatRangeArgument.ANY, new TranslatableText("argument.entity.options.y_rotation.description"));
        EntitySelectorOptions.putOption("limit", arg -> {
            int i = arg.getReader().getCursor();
            int j = arg.getReader().readInt();
            if (j < 1) {
                arg.getReader().setCursor(i);
                throw TOO_SMALL_LEVEL_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader());
            }
            arg.setLimit(j);
            arg.setHasLimit(true);
        }, arg -> !arg.isSenderOnly() && !arg.hasLimit(), new TranslatableText("argument.entity.options.limit.description"));
        EntitySelectorOptions.putOption("sort", arg -> {
            void biConsumer5;
            int i = arg.getReader().getCursor();
            String string = arg.getReader().readUnquotedString();
            arg.setSuggestionProvider((suggestionsBuilder, consumer) -> CommandSource.suggestMatching(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder));
            switch (string) {
                case "nearest": {
                    BiConsumer<Vec3d, List<? extends Entity>> biConsumer = EntitySelectorReader.NEAREST;
                    break;
                }
                case "furthest": {
                    BiConsumer<Vec3d, List<? extends Entity>> biConsumer2 = EntitySelectorReader.FURTHEST;
                    break;
                }
                case "random": {
                    BiConsumer<Vec3d, List<? extends Entity>> biConsumer3 = EntitySelectorReader.RANDOM;
                    break;
                }
                case "arbitrary": {
                    BiConsumer<Vec3d, List<? extends Entity>> biConsumer4 = EntitySelectorReader.ARBITRARY;
                    break;
                }
                default: {
                    arg.getReader().setCursor(i);
                    throw IRREVERSIBLE_SORT_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)string);
                }
            }
            arg.setSorter((BiConsumer<Vec3d, List<? extends Entity>>)biConsumer5);
            arg.setHasSorter(true);
        }, arg -> !arg.isSenderOnly() && !arg.hasSorter(), new TranslatableText("argument.entity.options.sort.description"));
        EntitySelectorOptions.putOption("gamemode", arg -> {
            arg.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
                boolean bl = !arg.excludesGameMode();
                boolean bl2 = true;
                if (!string.isEmpty()) {
                    if (string.charAt(0) == '!') {
                        bl = false;
                        string = string.substring(1);
                    } else {
                        bl2 = false;
                    }
                }
                for (GameMode lv : GameMode.values()) {
                    if (lv == GameMode.NOT_SET || !lv.getName().toLowerCase(Locale.ROOT).startsWith(string)) continue;
                    if (bl2) {
                        suggestionsBuilder.suggest('!' + lv.getName());
                    }
                    if (!bl) continue;
                    suggestionsBuilder.suggest(lv.getName());
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = arg.getReader().getCursor();
            boolean bl = arg.readNegationCharacter();
            if (arg.excludesGameMode() && !bl) {
                arg.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)"gamemode");
            }
            String string = arg.getReader().readUnquotedString();
            GameMode lv = GameMode.byName(string, GameMode.NOT_SET);
            if (lv == GameMode.NOT_SET) {
                arg.getReader().setCursor(i);
                throw INVALID_MODE_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)string);
            }
            arg.setIncludesNonPlayers(false);
            arg.setPredicate(arg2 -> {
                if (!(arg2 instanceof ServerPlayerEntity)) {
                    return false;
                }
                GameMode lv = ((ServerPlayerEntity)arg2).interactionManager.getGameMode();
                return bl ? lv != lv : lv == lv;
            });
            if (bl) {
                arg.setHasNegatedGameMode(true);
            } else {
                arg.setSelectsGameMode(true);
            }
        }, arg -> !arg.selectsGameMode(), new TranslatableText("argument.entity.options.gamemode.description"));
        EntitySelectorOptions.putOption("team", arg2 -> {
            boolean bl = arg2.readNegationCharacter();
            String string = arg2.getReader().readUnquotedString();
            arg2.setPredicate(arg -> {
                if (!(arg instanceof LivingEntity)) {
                    return false;
                }
                AbstractTeam lv = arg.getScoreboardTeam();
                String string2 = lv == null ? "" : lv.getName();
                return string2.equals(string) != bl;
            });
            if (bl) {
                arg2.setExcludesTeam(true);
            } else {
                arg2.setSelectsTeam(true);
            }
        }, arg -> !arg.selectsTeam(), new TranslatableText("argument.entity.options.team.description"));
        EntitySelectorOptions.putOption("type", arg -> {
            arg.setSuggestionProvider((suggestionsBuilder, consumer) -> {
                CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder, String.valueOf('!'));
                CommandSource.suggestIdentifiers(EntityTypeTags.getContainer().method_30211(), suggestionsBuilder, "!#");
                if (!arg.excludesEntityType()) {
                    CommandSource.suggestIdentifiers(Registry.ENTITY_TYPE.getIds(), suggestionsBuilder);
                    CommandSource.suggestIdentifiers(EntityTypeTags.getContainer().method_30211(), suggestionsBuilder, String.valueOf('#'));
                }
                return suggestionsBuilder.buildFuture();
            });
            int i = arg.getReader().getCursor();
            boolean bl = arg.readNegationCharacter();
            if (arg.excludesEntityType() && !bl) {
                arg.getReader().setCursor(i);
                throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)"type");
            }
            if (bl) {
                arg.setExcludesEntityType();
            }
            if (arg.readTagCharacter()) {
                Identifier lv = Identifier.fromCommandInput(arg.getReader());
                arg.setPredicate(arg2 -> arg2.getServer().getTagManager().method_30221().method_30213(lv).contains(arg2.getType()) != bl);
            } else {
                Identifier lv2 = Identifier.fromCommandInput(arg.getReader());
                EntityType lv3 = (EntityType)Registry.ENTITY_TYPE.getOrEmpty(lv2).orElseThrow(() -> {
                    arg.getReader().setCursor(i);
                    return INVALID_TYPE_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)lv2.toString());
                });
                if (Objects.equals(EntityType.PLAYER, lv3) && !bl) {
                    arg.setIncludesNonPlayers(false);
                }
                arg.setPredicate(arg2 -> Objects.equals(lv3, arg2.getType()) != bl);
                if (!bl) {
                    arg.setEntityType(lv3);
                }
            }
        }, arg -> !arg.selectsEntityType(), new TranslatableText("argument.entity.options.type.description"));
        EntitySelectorOptions.putOption("tag", arg2 -> {
            boolean bl = arg2.readNegationCharacter();
            String string = arg2.getReader().readUnquotedString();
            arg2.setPredicate(arg -> {
                if ("".equals(string)) {
                    return arg.getScoreboardTags().isEmpty() != bl;
                }
                return arg.getScoreboardTags().contains(string) != bl;
            });
        }, arg -> true, new TranslatableText("argument.entity.options.tag.description"));
        EntitySelectorOptions.putOption("nbt", arg -> {
            boolean bl = arg.readNegationCharacter();
            CompoundTag lv = new StringNbtReader(arg.getReader()).parseCompoundTag();
            arg.setPredicate(arg2 -> {
                ItemStack lv2;
                CompoundTag lv = arg2.toTag(new CompoundTag());
                if (arg2 instanceof ServerPlayerEntity && !(lv2 = ((ServerPlayerEntity)arg2).inventory.getMainHandStack()).isEmpty()) {
                    lv.put("SelectedItem", lv2.toTag(new CompoundTag()));
                }
                return NbtHelper.matches(lv, lv, true) != bl;
            });
        }, arg -> true, new TranslatableText("argument.entity.options.nbt.description"));
        EntitySelectorOptions.putOption("scores", arg2 -> {
            StringReader stringReader = arg2.getReader();
            HashMap map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();
            while (stringReader.canRead() && stringReader.peek() != '}') {
                stringReader.skipWhitespace();
                String string = stringReader.readUnquotedString();
                stringReader.skipWhitespace();
                stringReader.expect('=');
                stringReader.skipWhitespace();
                NumberRange.IntRange lv = NumberRange.IntRange.parse(stringReader);
                map.put(string, lv);
                stringReader.skipWhitespace();
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                arg2.setPredicate(arg -> {
                    ServerScoreboard lv = arg.getServer().getScoreboard();
                    String string = arg.getEntityName();
                    for (Map.Entry entry : map.entrySet()) {
                        ScoreboardObjective lv2 = lv.getNullableObjective((String)entry.getKey());
                        if (lv2 == null) {
                            return false;
                        }
                        if (!lv.playerHasObjective(string, lv2)) {
                            return false;
                        }
                        ScoreboardPlayerScore lv3 = lv.getPlayerScore(string, lv2);
                        int i = lv3.getScore();
                        if (((NumberRange.IntRange)entry.getValue()).test(i)) continue;
                        return false;
                    }
                    return true;
                });
            }
            arg2.setSelectsScores(true);
        }, arg -> !arg.selectsScores(), new TranslatableText("argument.entity.options.scores.description"));
        EntitySelectorOptions.putOption("advancements", arg2 -> {
            StringReader stringReader = arg2.getReader();
            HashMap map = Maps.newHashMap();
            stringReader.expect('{');
            stringReader.skipWhitespace();
            while (stringReader.canRead() && stringReader.peek() != '}') {
                stringReader.skipWhitespace();
                Identifier lv = Identifier.fromCommandInput(stringReader);
                stringReader.skipWhitespace();
                stringReader.expect('=');
                stringReader.skipWhitespace();
                if (stringReader.canRead() && stringReader.peek() == '{') {
                    HashMap map2 = Maps.newHashMap();
                    stringReader.skipWhitespace();
                    stringReader.expect('{');
                    stringReader.skipWhitespace();
                    while (stringReader.canRead() && stringReader.peek() != '}') {
                        stringReader.skipWhitespace();
                        String string = stringReader.readUnquotedString();
                        stringReader.skipWhitespace();
                        stringReader.expect('=');
                        stringReader.skipWhitespace();
                        boolean bl = stringReader.readBoolean();
                        map2.put(string, arg -> arg.isObtained() == bl);
                        stringReader.skipWhitespace();
                        if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                        stringReader.skip();
                    }
                    stringReader.skipWhitespace();
                    stringReader.expect('}');
                    stringReader.skipWhitespace();
                    map.put(lv, arg -> {
                        for (Map.Entry entry : map2.entrySet()) {
                            CriterionProgress lv = arg.getCriterionProgress((String)entry.getKey());
                            if (lv != null && ((Predicate)entry.getValue()).test(lv)) continue;
                            return false;
                        }
                        return true;
                    });
                } else {
                    boolean bl2 = stringReader.readBoolean();
                    map.put(lv, arg -> arg.isDone() == bl2);
                }
                stringReader.skipWhitespace();
                if (!stringReader.canRead() || stringReader.peek() != ',') continue;
                stringReader.skip();
            }
            stringReader.expect('}');
            if (!map.isEmpty()) {
                arg2.setPredicate(arg -> {
                    if (!(arg instanceof ServerPlayerEntity)) {
                        return false;
                    }
                    ServerPlayerEntity lv = (ServerPlayerEntity)arg;
                    PlayerAdvancementTracker lv2 = lv.getAdvancementTracker();
                    ServerAdvancementLoader lv3 = lv.getServer().getAdvancementLoader();
                    for (Map.Entry entry : map.entrySet()) {
                        Advancement lv4 = lv3.get((Identifier)entry.getKey());
                        if (lv4 != null && ((Predicate)entry.getValue()).test(lv2.getProgress(lv4))) continue;
                        return false;
                    }
                    return true;
                });
                arg2.setIncludesNonPlayers(false);
            }
            arg2.setSelectsAdvancements(true);
        }, arg -> !arg.selectsAdvancements(), new TranslatableText("argument.entity.options.advancements.description"));
        EntitySelectorOptions.putOption("predicate", arg -> {
            boolean bl = arg.readNegationCharacter();
            Identifier lv = Identifier.fromCommandInput(arg.getReader());
            arg.setPredicate(arg2 -> {
                if (!(arg2.world instanceof ServerWorld)) {
                    return false;
                }
                ServerWorld lv = (ServerWorld)arg2.world;
                LootCondition lv2 = lv.getServer().getPredicateManager().get(lv);
                if (lv2 == null) {
                    return false;
                }
                LootContext lv3 = new LootContext.Builder(lv).parameter(LootContextParameters.THIS_ENTITY, arg2).parameter(LootContextParameters.POSITION, arg2.getBlockPos()).build(LootContextTypes.SELECTOR);
                return bl ^ lv2.test(lv3);
            });
        }, arg -> true, new TranslatableText("argument.entity.options.predicate.description"));
    }

    public static SelectorHandler getHandler(EntitySelectorReader arg, String string, int i) throws CommandSyntaxException {
        SelectorOption lv = options.get(string);
        if (lv != null) {
            if (lv.condition.test(arg)) {
                return lv.handler;
            }
            throw INAPPLICABLE_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)string);
        }
        arg.getReader().setCursor(i);
        throw UNKNOWN_OPTION_EXCEPTION.createWithContext((ImmutableStringReader)arg.getReader(), (Object)string);
    }

    public static void suggestOptions(EntitySelectorReader arg, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, SelectorOption> entry : options.entrySet()) {
            if (!entry.getValue().condition.test(arg) || !entry.getKey().toLowerCase(Locale.ROOT).startsWith(string)) continue;
            suggestionsBuilder.suggest(entry.getKey() + '=', (Message)entry.getValue().description);
        }
    }

    static class SelectorOption {
        public final SelectorHandler handler;
        public final Predicate<EntitySelectorReader> condition;
        public final Text description;

        private SelectorOption(SelectorHandler arg, Predicate<EntitySelectorReader> predicate, Text arg2) {
            this.handler = arg;
            this.condition = predicate;
            this.description = arg2;
        }
    }

    public static interface SelectorHandler {
        public void handle(EntitySelectorReader var1) throws CommandSyntaxException;
    }
}

