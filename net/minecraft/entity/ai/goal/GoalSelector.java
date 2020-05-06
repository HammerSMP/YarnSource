/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WeightedGoal;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final WeightedGoal activeGoal = new WeightedGoal(Integer.MAX_VALUE, new Goal(){

        @Override
        public boolean canStart() {
            return false;
        }
    }){

        @Override
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Control, WeightedGoal> goalsByControl = new EnumMap<Goal.Control, WeightedGoal>(Goal.Control.class);
    private final Set<WeightedGoal> goals = Sets.newLinkedHashSet();
    private final Supplier<Profiler> profiler;
    private final EnumSet<Goal.Control> disabledControls = EnumSet.noneOf(Goal.Control.class);
    private int timeInterval = 3;

    public GoalSelector(Supplier<Profiler> supplier) {
        this.profiler = supplier;
    }

    public void add(int i, Goal arg) {
        this.goals.add(new WeightedGoal(i, arg));
    }

    public void remove(Goal arg) {
        this.goals.stream().filter(arg2 -> arg2.getGoal() == arg).filter(WeightedGoal::isRunning).forEach(WeightedGoal::stop);
        this.goals.removeIf(arg2 -> arg2.getGoal() == arg);
    }

    public void tick() {
        Profiler lv = this.profiler.get();
        lv.push("goalCleanup");
        this.getRunningGoals().filter(arg -> {
            if (!arg.isRunning()) return true;
            if (arg.getControls().stream().anyMatch(this.disabledControls::contains)) return true;
            if (arg.shouldContinue()) return false;
            return true;
        }).forEach(Goal::stop);
        this.goalsByControl.forEach((arg, arg2) -> {
            if (!arg2.isRunning()) {
                this.goalsByControl.remove(arg);
            }
        });
        lv.pop();
        lv.push("goalUpdate");
        this.goals.stream().filter(arg -> !arg.isRunning()).filter(arg -> arg.getControls().stream().noneMatch(this.disabledControls::contains)).filter(arg -> arg.getControls().stream().allMatch(arg2 -> this.goalsByControl.getOrDefault(arg2, activeGoal).canBeReplacedBy((WeightedGoal)arg))).filter(WeightedGoal::canStart).forEach(arg -> {
            arg.getControls().forEach(arg2 -> {
                WeightedGoal lv = this.goalsByControl.getOrDefault(arg2, activeGoal);
                lv.stop();
                this.goalsByControl.put((Goal.Control)((Object)((Object)arg2)), (WeightedGoal)arg);
            });
            arg.start();
        });
        lv.pop();
        lv.push("goalTick");
        this.getRunningGoals().forEach(WeightedGoal::tick);
        lv.pop();
    }

    public Stream<WeightedGoal> getRunningGoals() {
        return this.goals.stream().filter(WeightedGoal::isRunning);
    }

    public void disableControl(Goal.Control arg) {
        this.disabledControls.add(arg);
    }

    public void enableControl(Goal.Control arg) {
        this.disabledControls.remove((Object)arg);
    }

    public void setControlEnabled(Goal.Control arg, boolean bl) {
        if (bl) {
            this.enableControl(arg);
        } else {
            this.disableControl(arg);
        }
    }
}

