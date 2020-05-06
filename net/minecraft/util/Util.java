/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.Hash;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService SERVER_WORKER_EXECUTOR = Util.createServerWorkerExecutor();
    private static final ExecutorService field_24477 = Util.method_27959();
    public static LongSupplier nanoTimeSupplier = System::nanoTime;
    private static final Logger LOGGER = LogManager.getLogger();

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getValueAsString(Property<T> arg, Object object) {
        return arg.name((Comparable)object);
    }

    public static String createTranslationKey(String string, @Nullable Identifier arg) {
        if (arg == null) {
            return string + ".unregistered_sadface";
        }
        return string + '.' + arg.getNamespace() + '.' + arg.getPath().replace('/', '.');
    }

    public static long getMeasuringTimeMs() {
        return Util.getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }

    public static long getEpochTimeMs() {
        return Instant.now().toEpochMilli();
    }

    private static ExecutorService createServerWorkerExecutor() {
        ForkJoinPool executorService2;
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
        if (i <= 0) {
            ListeningExecutorService executorService = MoreExecutors.newDirectExecutorService();
        } else {
            executorService2 = new ForkJoinPool(i, forkJoinPool -> {
                ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool){

                    @Override
                    protected void onTermination(Throwable throwable) {
                        if (throwable != null) {
                            LOGGER.warn("{} died", (Object)this.getName(), (Object)throwable);
                        } else {
                            LOGGER.debug("{} shutdown", (Object)this.getName());
                        }
                        super.onTermination(throwable);
                    }
                };
                forkJoinWorkerThread.setName("Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
                return forkJoinWorkerThread;
            }, Util::method_18347, true);
        }
        return executorService2;
    }

    public static Executor getServerWorkerExecutor() {
        return SERVER_WORKER_EXECUTOR;
    }

    public static Executor method_27958() {
        return field_24477;
    }

    public static void shutdownServerWorkerExecutor() {
        Util.method_27957(SERVER_WORKER_EXECUTOR);
        Util.method_27957(field_24477);
    }

    private static void method_27957(ExecutorService executorService) {
        boolean bl2;
        executorService.shutdown();
        try {
            boolean bl = executorService.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException interruptedException) {
            bl2 = false;
        }
        if (!bl2) {
            executorService.shutdownNow();
        }
    }

    private static ExecutorService method_27959() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("IO-Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            thread.setUncaughtExceptionHandler(Util::method_18347);
            return thread;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static <T> CompletableFuture<T> completeExceptionally(Throwable throwable) {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(throwable);
        return completableFuture;
    }

    @Environment(value=EnvType.CLIENT)
    public static void throwUnchecked(Throwable throwable) {
        throw throwable instanceof RuntimeException ? (RuntimeException)throwable : new RuntimeException(throwable);
    }

    private static void method_18347(Thread thread, Throwable throwable) {
        Util.throwOrPause(throwable);
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof CrashException) {
            Bootstrap.println(((CrashException)throwable).getReport().asString());
            System.exit(-1);
        }
        LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
    }

    public static OperatingSystem getOperatingSystem() {
        String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (string.contains("win")) {
            return OperatingSystem.WINDOWS;
        }
        if (string.contains("mac")) {
            return OperatingSystem.OSX;
        }
        if (string.contains("solaris")) {
            return OperatingSystem.SOLARIS;
        }
        if (string.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        if (string.contains("linux")) {
            return OperatingSystem.LINUX;
        }
        if (string.contains("unix")) {
            return OperatingSystem.LINUX;
        }
        return OperatingSystem.UNKNOWN;
    }

    public static Stream<String> getJVMFlags() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T next(Iterable<T> iterable, @Nullable T object) {
        Iterator<T> iterator = iterable.iterator();
        T object2 = iterator.next();
        if (object != null) {
            T object3 = object2;
            do {
                if (object3 == object) {
                    if (!iterator.hasNext()) break;
                    return iterator.next();
                }
                if (!iterator.hasNext()) continue;
                object3 = iterator.next();
            } while (true);
        }
        return object2;
    }

    public static <T> T previous(Iterable<T> iterable, @Nullable T object) {
        Iterator<T> iterator = iterable.iterator();
        T object2 = null;
        while (iterator.hasNext()) {
            T object3 = iterator.next();
            if (object3 == object) {
                if (object2 != null) break;
                object2 = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : object);
                break;
            }
            object2 = object3;
        }
        return object2;
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    public static <K> Hash.Strategy<K> identityHashStrategy() {
        return IdentityHashStrategy.INSTANCE;
    }

    public static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> list) {
        ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
        CompletableFuture[] completableFutures = new CompletableFuture[list.size()];
        CompletableFuture completableFuture = new CompletableFuture();
        list.forEach(completableFuture2 -> {
            int i = list2.size();
            list2.add(null);
            completableFutures[i] = completableFuture2.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completableFuture.completeExceptionally((Throwable)throwable);
                } else {
                    list2.set(i, object);
                }
            });
        });
        return CompletableFuture.allOf(completableFutures).applyToEither((CompletionStage)completableFuture, arg -> list2);
    }

    public static <T> Stream<T> stream(Optional<? extends T> optional) {
        return (Stream)DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <T> Optional<T> ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        } else {
            runnable.run();
        }
        return optional;
    }

    public static Runnable debugRunnable(Runnable runnable, Supplier<String> supplier) {
        return runnable;
    }

    public static <T extends Throwable> T throwOrPause(T throwable) {
        if (SharedConstants.isDevelopment) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", throwable);
            try {
                do {
                    Thread.sleep(1000L);
                    LOGGER.error("paused");
                } while (true);
            }
            catch (InterruptedException interruptedException) {
                return throwable;
            }
        }
        return throwable;
    }

    public static String getInnermostMessage(Throwable throwable) {
        if (throwable.getCause() != null) {
            return Util.getInnermostMessage(throwable.getCause());
        }
        if (throwable.getMessage() != null) {
            return throwable.getMessage();
        }
        return throwable.toString();
    }

    public static <T> T getRandom(T[] objects, Random random) {
        return objects[random.nextInt(objects.length)];
    }

    public static int getRandom(int[] is, Random random) {
        return is[random.nextInt(is.length)];
    }

    public static void method_27760(File file, File file2, File file3) {
        if (file3.exists()) {
            file3.delete();
        }
        file.renameTo(file3);
        if (file.exists()) {
            file.delete();
        }
        file2.renameTo(file);
        if (file2.exists()) {
            file2.delete();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static int moveCursor(String string, int i, int j) {
        int k = string.length();
        if (j >= 0) {
            for (int l = 0; i < k && l < j; ++l) {
                if (!Character.isHighSurrogate(string.charAt(i++)) || i >= k || !Character.isLowSurrogate(string.charAt(i))) continue;
                ++i;
            }
        } else {
            for (int m = j; i > 0 && m < 0; ++m) {
                if (!Character.isLowSurrogate(string.charAt(--i)) || i <= 0 || !Character.isHighSurrogate(string.charAt(i - 1))) continue;
                --i;
            }
        }
        return i;
    }

    static enum IdentityHashStrategy implements Hash.Strategy<Object>
    {
        INSTANCE;


        public int hashCode(Object object) {
            return System.identityHashCode(object);
        }

        public boolean equals(Object object, Object object2) {
            return object == object2;
        }
    }

    public static enum OperatingSystem {
        LINUX,
        SOLARIS,
        WINDOWS{

            @Override
            @Environment(value=EnvType.CLIENT)
            protected String[] getURLOpenCommand(URL uRL) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
            }
        }
        ,
        OSX{

            @Override
            @Environment(value=EnvType.CLIENT)
            protected String[] getURLOpenCommand(URL uRL) {
                return new String[]{"open", uRL.toString()};
            }
        }
        ,
        UNKNOWN;


        @Environment(value=EnvType.CLIENT)
        public void open(URL uRL) {
            try {
                Process process = AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(uRL)));
                for (String string : IOUtils.readLines((InputStream)process.getErrorStream())) {
                    LOGGER.error(string);
                }
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            }
            catch (IOException | PrivilegedActionException exception) {
                LOGGER.error("Couldn't open url '{}'", (Object)uRL, (Object)exception);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public void open(URI uRI) {
            try {
                this.open(uRI.toURL());
            }
            catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uRI, (Object)malformedURLException);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public void open(File file) {
            try {
                this.open(file.toURI().toURL());
            }
            catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open file '{}'", (Object)file, (Object)malformedURLException);
            }
        }

        @Environment(value=EnvType.CLIENT)
        protected String[] getURLOpenCommand(URL uRL) {
            String string = uRL.toString();
            if ("file".equals(uRL.getProtocol())) {
                string = string.replace("file:", "file://");
            }
            return new String[]{"xdg-open", string};
        }

        @Environment(value=EnvType.CLIENT)
        public void open(String string) {
            try {
                this.open(new URI(string).toURL());
            }
            catch (IllegalArgumentException | MalformedURLException | URISyntaxException exception) {
                LOGGER.error("Couldn't open uri '{}'", (Object)string, (Object)exception);
            }
        }
    }
}

