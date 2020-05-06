/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class AbstractTeam {
    public boolean isEqual(@Nullable AbstractTeam arg) {
        if (arg == null) {
            return false;
        }
        return this == arg;
    }

    public abstract String getName();

    public abstract MutableText modifyText(Text var1);

    @Environment(value=EnvType.CLIENT)
    public abstract boolean shouldShowFriendlyInvisibles();

    public abstract boolean isFriendlyFireAllowed();

    @Environment(value=EnvType.CLIENT)
    public abstract VisibilityRule getNameTagVisibilityRule();

    @Environment(value=EnvType.CLIENT)
    public abstract Formatting getColor();

    public abstract Collection<String> getPlayerList();

    public abstract VisibilityRule getDeathMessageVisibilityRule();

    public abstract CollisionRule getCollisionRule();

    public static enum CollisionRule {
        ALWAYS("always", 0),
        NEVER("never", 1),
        PUSH_OTHER_TEAMS("pushOtherTeams", 2),
        PUSH_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, CollisionRule> COLLISION_RULES;
        public final String name;
        public final int value;

        @Nullable
        public static CollisionRule getRule(String string) {
            return COLLISION_RULES.get(string);
        }

        private CollisionRule(String string2, int j) {
            this.name = string2;
            this.value = j;
        }

        public Text getTranslationKey() {
            return new TranslatableText("team.collision." + this.name);
        }

        static {
            COLLISION_RULES = Arrays.stream(CollisionRule.values()).collect(Collectors.toMap(arg -> arg.name, arg -> arg));
        }
    }

    public static enum VisibilityRule {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, VisibilityRule> VISIBILITY_RULES;
        public final String name;
        public final int value;

        @Nullable
        public static VisibilityRule getRule(String string) {
            return VISIBILITY_RULES.get(string);
        }

        private VisibilityRule(String string2, int j) {
            this.name = string2;
            this.value = j;
        }

        public Text getTranslationKey() {
            return new TranslatableText("team.visibility." + this.name);
        }

        static {
            VISIBILITY_RULES = Arrays.stream(VisibilityRule.values()).collect(Collectors.toMap(arg -> arg.name, arg -> arg));
        }
    }
}

