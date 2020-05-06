/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EulaReader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path eulaFile;
    private final boolean eulaAgreedTo;

    public EulaReader(Path path) {
        this.eulaFile = path;
        this.eulaAgreedTo = SharedConstants.isDevelopment || this.checkEulaAgreement();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean checkEulaAgreement() {
        try (InputStream inputStream = Files.newInputStream(this.eulaFile, new OpenOption[0]);){
            Properties properties = new Properties();
            properties.load(inputStream);
            boolean bl = Boolean.parseBoolean(properties.getProperty("eula", "false"));
            return bl;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load {}", (Object)this.eulaFile);
            this.createEulaFile();
            return false;
        }
    }

    public boolean isEulaAgreedTo() {
        return this.eulaAgreedTo;
    }

    private void createEulaFile() {
        if (SharedConstants.isDevelopment) {
            return;
        }
        try (OutputStream outputStream = Files.newOutputStream(this.eulaFile, new OpenOption[0]);){
            Properties properties = new Properties();
            properties.setProperty("eula", "false");
            properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save {}", (Object)this.eulaFile, (Object)exception);
        }
    }
}

