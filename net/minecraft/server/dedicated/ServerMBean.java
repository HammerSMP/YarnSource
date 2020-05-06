/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerMBean
implements DynamicMBean {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer server;
    private final MBeanInfo beanInfo;
    private final Map<String, Entry> entries = Stream.of(new Entry("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class), new Entry("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap(arg -> Entry.method_27186(arg), Function.identity()));

    private ServerMBean(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        MBeanAttributeInfo[] mBeanAttributeInfos = (MBeanAttributeInfo[])this.entries.values().stream().map(object -> ((Entry)object).createInfo()).toArray(MBeanAttributeInfo[]::new);
        this.beanInfo = new MBeanInfo(ServerMBean.class.getSimpleName(), "metrics for dedicated server", mBeanAttributeInfos, null, null, new MBeanNotificationInfo[0]);
    }

    public static void register(MinecraftServer minecraftServer) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new ServerMBean(minecraftServer), new ObjectName("net.minecraft.server:type=Server"));
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException jMException) {
            LOGGER.warn("Failed to initialise server as JMX bean", (Throwable)jMException);
        }
    }

    private float getAverageTickTime() {
        return this.server.getTickTime();
    }

    private long[] getTickTimes() {
        return this.server.lastTickLengths;
    }

    @Override
    @Nullable
    public Object getAttribute(String string) {
        Entry lv = this.entries.get(string);
        return lv == null ? null : lv.getter.get();
    }

    @Override
    public void setAttribute(Attribute attribute) {
    }

    @Override
    public AttributeList getAttributes(String[] strings) {
        List<Attribute> list = Arrays.stream(strings).map(this.entries::get).filter(Objects::nonNull).map(arg -> new Attribute(((Entry)arg).name, ((Entry)arg).getter.get())).collect(Collectors.toList());
        return new AttributeList(list);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributeList) {
        return new AttributeList();
    }

    @Override
    @Nullable
    public Object invoke(String string, Object[] objects, String[] strings) {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.beanInfo;
    }

    static final class Entry {
        private final String name;
        private final Supplier<Object> getter;
        private final String description;
        private final Class<?> type;

        private Entry(String string, Supplier<Object> supplier, String string2, Class<?> arg) {
            this.name = string;
            this.getter = supplier;
            this.description = string2;
            this.type = arg;
        }

        private MBeanAttributeInfo createInfo() {
            return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
        }
    }
}

