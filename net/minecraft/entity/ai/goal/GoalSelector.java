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
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PrioritizedGoal REPLACEABLE_GOAL = new PrioritizedGoal(Integer.MAX_VALUE, new Goal(){

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
    private final Map<Goal.Control, PrioritizedGoal> goalsByControl = new EnumMap<Goal.Control, PrioritizedGoal>(Goal.Control.class);
    private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
    private final Supplier<Profiler> profiler;
    private final EnumSet<Goal.Control> disabledControls = EnumSet.noneOf(Goal.Control.class);
    private int timeInterval = 3;

    public GoalSelector(Supplier<Profiler> profiler) {
        this.profiler = profiler;
    }

    public void add(int priority, Goal goal) {
        this.goals.add(new PrioritizedGoal(priority, goal));
    }

    public void remove(Goal goal) {
        this.goals.stream().filter(arg2 -> arg2.getGoal() == goal).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::stop);
        this.goals.removeIf(arg2 -> arg2.getGoal() == goal);
    }

    public void tick() {
        Profiler profiler = this.profiler.get();
        profiler.push("goalCleanup");
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
        profiler.pop();
        profiler.push("goalUpdate");
        
        // whatmst in the fuck
        this.goals.stream().filter(arg -> !arg.isRunning())
            .filter(arg ->
                arg.getControls().
                    stream().noneMatch(
                        this.disabledControls::contains
                    ))
                    .filter(arg -> 
                        arg.getControls()
                            .stream().allMatch(
                                arg2 -> this.goalsByControl.getOrDefault(arg2, REPLACEABLE_GOAL)
                                    .canBeReplacedBy((PrioritizedGoal)arg)
                                ))
                            .filter(PrioritizedGoal::canStart)
                        .forEach(lgoals -> {
                            lgoals.getControls().forEach(currentGoal -> {
                                    PrioritizedGoal newGoal = this.goalsByControl.getOrDefault(currentGoal, REPLACEABLE_GOAL);
                                    goal.stop();
                                    this.goalsByControl.put((Goal.Control)((Object)((Object)currentGoal)), (PrioritizedGoal)lgoals);
                            });
                            lgoals.start();
                        });

        profiler.pop();
        profiler.push("goalTick");
        this.getRunningGoals().forEach(PrioritizedGoal::tick);
        profiler.pop();
    }

    public Stream<PrioritizedGoal> getRunningGoals() {
        return this.goals.stream().filter(PrioritizedGoal::isRunning);
    }

    public void disableControl(Goal.Control control) {
        this.disabledControls.add(control);
    }

    public void enableControl(Goal.Control control) {
        this.disabledControls.remove((Object)control);
    }

    public void setControlEnabled(Goal.Control control, boolean enabled) {
        if (enabled) {
            this.enableControl(control);
        } else {
            this.disableControl(control);
        }
    }
}

