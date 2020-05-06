/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ServerAddress {
    private final String address;
    private final int port;

    private ServerAddress(String string, int i) {
        this.address = string;
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

    public static ServerAddress parse(String string) {
        int j;
        int i;
        if (string == null) {
            return null;
        }
        String[] strings = string.split(":");
        if (string.startsWith("[") && (i = string.indexOf("]")) > 0) {
            String string2 = string.substring(1, i);
            String string3 = string.substring(i + 1).trim();
            if (string3.startsWith(":") && !string3.isEmpty()) {
                string3 = string3.substring(1);
                strings = new String[]{string2, string3};
            } else {
                strings = new String[]{string2};
            }
        }
        if (strings.length > 2) {
            strings = new String[]{string};
        }
        String string4 = strings[0];
        int n = j = strings.length > 1 ? ServerAddress.portOrDefault(strings[1], 25565) : 25565;
        if (j == 25565) {
            String[] strings2 = ServerAddress.resolveSrv(string4);
            string4 = strings2[0];
            j = ServerAddress.portOrDefault(strings2[1], 25565);
        }
        return new ServerAddress(string4, j);
    }

    private static String[] resolveSrv(String string) {
        try {
            String string2 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext dirContext = new InitialDirContext(hashtable);
            Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + string, new String[]{"SRV"});
            String[] strings = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{strings[3], strings[2]};
        }
        catch (Throwable throwable) {
            return new String[]{string, Integer.toString(25565)};
        }
    }

    private static int portOrDefault(String string, int i) {
        try {
            return Integer.parseInt(string.trim());
        }
        catch (Exception exception) {
            return i;
        }
    }
}

