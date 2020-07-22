/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network;

import com.mojang.datafixers.util.Pair;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ServerAddress {
    private final String address;
    private final int port;

    private ServerAddress(String address, int i) {
        this.address = address;
        this.port = i;
    }

    public String getAddress() {
        try {
            return IDN.toASCII(this.address);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return "";
        }
    }

    public int getPort() {
        return this.port;
    }

    public static ServerAddress parse(String address) {
        int j;
        int i;
        if (address == null) {
            return null;
        }
        String[] strings = address.split(":");
        if (address.startsWith("[") && (i = address.indexOf("]")) > 0) {
            String string2 = address.substring(1, i);
            String string3 = address.substring(i + 1).trim();
            if (string3.startsWith(":") && !string3.isEmpty()) {
                string3 = string3.substring(1);
                strings = new String[]{string2, string3};
            } else {
                strings = new String[]{string2};
            }
        }
        if (strings.length > 2) {
            strings = new String[]{address};
        }
        String string4 = strings[0];
        int n = j = strings.length > 1 ? ServerAddress.portOrDefault(strings[1], 25565) : 25565;
        if (j == 25565) {
            Pair<String, Integer> pair = ServerAddress.resolveServer(string4);
            string4 = (String)pair.getFirst();
            j = (Integer)pair.getSecond();
        }
        return new ServerAddress(string4, j);
    }

    private static Pair<String, Integer> resolveServer(String address) {
        try {
            String string2 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext dirContext = new InitialDirContext(hashtable);
            Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + address, new String[]{"SRV"});
            Attribute attribute = attributes.get("srv");
            if (attribute != null) {
                String[] strings = attribute.get().toString().split(" ", 4);
                return Pair.of((Object)strings[3], (Object)ServerAddress.portOrDefault(strings[2], 25565));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Pair.of((Object)address, (Object)25565);
    }

    private static int portOrDefault(String port, int def) {
        try {
            return Integer.parseInt(port.trim());
        }
        catch (Exception exception) {
            return def;
        }
    }
}

