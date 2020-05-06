/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.scoreboard;

import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardState
extends PersistentState {
    private static final Logger LOGGER = LogManager.getLogger();
    private Scoreboard scoreboard;
    private CompoundTag tag;

    public ScoreboardState() {
        super("scoreboard");
    }

    public void setScoreboard(Scoreboard arg) {
        this.scoreboard = arg;
        if (this.tag != null) {
            this.fromTag(this.tag);
        }
    }

    @Override
    public void fromTag(CompoundTag arg) {
        if (this.scoreboard == null) {
            this.tag = arg;
            return;
        }
        this.deserializeObjectives(arg.getList("Objectives", 10));
        this.scoreboard.fromTag(arg.getList("PlayerScores", 10));
        if (arg.contains("DisplaySlots", 10)) {
            this.deserializeDisplaySlots(arg.getCompound("DisplaySlots"));
        }
        if (arg.contains("Teams", 9)) {
            this.deserializeTeams(arg.getList("Teams", 10));
        }
    }

    protected void deserializeTeams(ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            AbstractTeam.CollisionRule lv8;
            AbstractTeam.VisibilityRule lv7;
            AbstractTeam.VisibilityRule lv6;
            MutableText lv5;
            MutableText lv4;
            CompoundTag lv = arg.getCompound(i);
            String string = lv.getString("Name");
            if (string.length() > 16) {
                string = string.substring(0, 16);
            }
            Team lv2 = this.scoreboard.addTeam(string);
            MutableText lv3 = Text.Serializer.fromJson(lv.getString("DisplayName"));
            if (lv3 != null) {
                lv2.setDisplayName(lv3);
            }
            if (lv.contains("TeamColor", 8)) {
                lv2.setColor(Formatting.byName(lv.getString("TeamColor")));
            }
            if (lv.contains("AllowFriendlyFire", 99)) {
                lv2.setFriendlyFireAllowed(lv.getBoolean("AllowFriendlyFire"));
            }
            if (lv.contains("SeeFriendlyInvisibles", 99)) {
                lv2.setShowFriendlyInvisibles(lv.getBoolean("SeeFriendlyInvisibles"));
            }
            if (lv.contains("MemberNamePrefix", 8) && (lv4 = Text.Serializer.fromJson(lv.getString("MemberNamePrefix"))) != null) {
                lv2.setPrefix(lv4);
            }
            if (lv.contains("MemberNameSuffix", 8) && (lv5 = Text.Serializer.fromJson(lv.getString("MemberNameSuffix"))) != null) {
                lv2.setSuffix(lv5);
            }
            if (lv.contains("NameTagVisibility", 8) && (lv6 = AbstractTeam.VisibilityRule.getRule(lv.getString("NameTagVisibility"))) != null) {
                lv2.setNameTagVisibilityRule(lv6);
            }
            if (lv.contains("DeathMessageVisibility", 8) && (lv7 = AbstractTeam.VisibilityRule.getRule(lv.getString("DeathMessageVisibility"))) != null) {
                lv2.setDeathMessageVisibilityRule(lv7);
            }
            if (lv.contains("CollisionRule", 8) && (lv8 = AbstractTeam.CollisionRule.getRule(lv.getString("CollisionRule"))) != null) {
                lv2.setCollisionRule(lv8);
            }
            this.deserializeTeamPlayers(lv2, lv.getList("Players", 8));
        }
    }

    protected void deserializeTeamPlayers(Team arg, ListTag arg2) {
        for (int i = 0; i < arg2.size(); ++i) {
            this.scoreboard.addPlayerToTeam(arg2.getString(i), arg);
        }
    }

    protected void deserializeDisplaySlots(CompoundTag arg) {
        for (int i = 0; i < 19; ++i) {
            if (!arg.contains("slot_" + i, 8)) continue;
            String string = arg.getString("slot_" + i);
            ScoreboardObjective lv = this.scoreboard.getNullableObjective(string);
            this.scoreboard.setObjectiveSlot(i, lv);
        }
    }

    protected void deserializeObjectives(ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            CompoundTag lv = arg.getCompound(i);
            ScoreboardCriterion.createStatCriterion(lv.getString("CriteriaName")).ifPresent(arg2 -> {
                String string = lv.getString("Name");
                if (string.length() > 16) {
                    string = string.substring(0, 16);
                }
                MutableText lv = Text.Serializer.fromJson(lv.getString("DisplayName"));
                ScoreboardCriterion.RenderType lv2 = ScoreboardCriterion.RenderType.getType(lv.getString("RenderType"));
                this.scoreboard.addObjective(string, (ScoreboardCriterion)arg2, lv, lv2);
            });
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        if (this.scoreboard == null) {
            LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
            return arg;
        }
        arg.put("Objectives", this.serializeObjectives());
        arg.put("PlayerScores", this.scoreboard.toTag());
        arg.put("Teams", this.serializeTeams());
        this.serializeSlots(arg);
        return arg;
    }

    protected ListTag serializeTeams() {
        ListTag lv = new ListTag();
        Collection<Team> collection = this.scoreboard.getTeams();
        for (Team lv2 : collection) {
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("Name", lv2.getName());
            lv3.putString("DisplayName", Text.Serializer.toJson(lv2.getDisplayName()));
            if (lv2.getColor().getColorIndex() >= 0) {
                lv3.putString("TeamColor", lv2.getColor().getName());
            }
            lv3.putBoolean("AllowFriendlyFire", lv2.isFriendlyFireAllowed());
            lv3.putBoolean("SeeFriendlyInvisibles", lv2.shouldShowFriendlyInvisibles());
            lv3.putString("MemberNamePrefix", Text.Serializer.toJson(lv2.getPrefix()));
            lv3.putString("MemberNameSuffix", Text.Serializer.toJson(lv2.getSuffix()));
            lv3.putString("NameTagVisibility", lv2.getNameTagVisibilityRule().name);
            lv3.putString("DeathMessageVisibility", lv2.getDeathMessageVisibilityRule().name);
            lv3.putString("CollisionRule", lv2.getCollisionRule().name);
            ListTag lv4 = new ListTag();
            for (String string : lv2.getPlayerList()) {
                lv4.add(StringTag.of(string));
            }
            lv3.put("Players", lv4);
            lv.add(lv3);
        }
        return lv;
    }

    protected void serializeSlots(CompoundTag arg) {
        CompoundTag lv = new CompoundTag();
        boolean bl = false;
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective lv2 = this.scoreboard.getObjectiveForSlot(i);
            if (lv2 == null) continue;
            lv.putString("slot_" + i, lv2.getName());
            bl = true;
        }
        if (bl) {
            arg.put("DisplaySlots", lv);
        }
    }

    protected ListTag serializeObjectives() {
        ListTag lv = new ListTag();
        Collection<ScoreboardObjective> collection = this.scoreboard.getObjectives();
        for (ScoreboardObjective lv2 : collection) {
            if (lv2.getCriterion() == null) continue;
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("Name", lv2.getName());
            lv3.putString("CriteriaName", lv2.getCriterion().getName());
            lv3.putString("DisplayName", Text.Serializer.toJson(lv2.getDisplayName()));
            lv3.putString("RenderType", lv2.getRenderType().getName());
            lv.add(lv3);
        }
        return lv;
    }
}

