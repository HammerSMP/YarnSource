/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedList;

public class CompositeTask<E extends LivingEntity>
extends Task<E> {
    private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
    private final Order order;
    private final RunMode runMode;
    private final WeightedList<Task<? super E>> tasks = new WeightedList();

    public CompositeTask(Map<MemoryModuleType<?>, MemoryModuleState> map, Set<MemoryModuleType<?>> set, Order arg, RunMode arg2, List<Pair<Task<? super E>, Integer>> list) {
        super(map);
        this.memoriesToForgetWhenStopped = set;
        this.order = arg;
        this.runMode = arg2;
        list.forEach(pair -> this.tasks.add((Task<E>)pair.getFirst(), (Integer)pair.getSecond()));
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg2, E arg22, long l) {
        return this.tasks.stream().filter(arg -> arg.getStatus() == Task.Status.RUNNING).anyMatch(arg3 -> arg3.shouldKeepRunning(arg2, arg22, l));
    }

    @Override
    protected boolean isTimeLimitExceeded(long l) {
        return false;
    }

    @Override
    protected void run(ServerWorld arg, E arg2, long l) {
        this.order.apply(this.tasks);
        this.runMode.run(this.tasks, arg, arg2, l);
    }

    @Override
    protected void keepRunning(ServerWorld arg2, E arg22, long l) {
        this.tasks.stream().filter(arg -> arg.getStatus() == Task.Status.RUNNING).forEach(arg3 -> arg3.tick(arg2, arg22, l));
    }

    @Override
    protected void finishRunning(ServerWorld arg2, E arg22, long l) {
        this.tasks.stream().filter(arg -> arg.getStatus() == Task.Status.RUNNING).forEach(arg3 -> arg3.stop(arg2, arg22, l));
        this.memoriesToForgetWhenStopped.forEach(((LivingEntity)arg22).getBrain()::forget);
    }

    @Override
    public String toString() {
        Set set = this.tasks.stream().filter(arg -> arg.getStatus() == Task.Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set;
    }

    static enum RunMode {
        RUN_ONE{

            @Override
            public <E extends LivingEntity> void run(WeightedList<Task<? super E>> arg2, ServerWorld arg22, E arg32, long l) {
                arg2.stream().filter(arg -> arg.getStatus() == Task.Status.STOPPED).filter(arg3 -> arg3.tryStarting(arg22, arg32, l)).findFirst();
            }
        }
        ,
        TRY_ALL{

            @Override
            public <E extends LivingEntity> void run(WeightedList<Task<? super E>> arg2, ServerWorld arg22, E arg32, long l) {
                arg2.stream().filter(arg -> arg.getStatus() == Task.Status.STOPPED).forEach(arg3 -> arg3.tryStarting(arg22, arg32, l));
            }
        };


        public abstract <E extends LivingEntity> void run(WeightedList<Task<? super E>> var1, ServerWorld var2, E var3, long var4);
    }

    static enum Order {
        ORDERED(arg -> {}),
        SHUFFLED(WeightedList::shuffle);

        private final Consumer<WeightedList<?>> listModifier;

        private Order(Consumer<WeightedList<?>> consumer) {
            this.listModifier = consumer;
        }

        public void apply(WeightedList<?> arg) {
            this.listModifier.accept(arg);
        }
    }
}

