/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5218;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileDownload {
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile File backupFile;
    private volatile File resourcePackPath;
    private volatile HttpGet httpRequest;
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String string) {
        CloseableHttpClient closeableHttpClient = null;
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(string);
            closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpGet);
            long l = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
            return l;
        }
        catch (Throwable throwable) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        }
        finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                }
                catch (IOException iOException) {
                    LOGGER.error("Could not close http client", (Throwable)iOException);
                }
            }
        }
    }

    public void downloadWorld(WorldDownload arg, String string, RealmsDownloadLatestWorldScreen.DownloadStatus arg2, LevelStorage arg3) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            CloseableHttpClient closeableHttpClient = null;
            try {
                this.backupFile = File.createTempFile("backup", ".tar.gz");
                this.httpRequest = new HttpGet(arg.downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse httpResponse = closeableHttpClient.execute((HttpUriRequest)this.httpRequest);
                arg2.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.httpRequest.abort();
                    return;
                }
                FileOutputStream outputStream2 = new FileOutputStream(this.backupFile);
                ProgressListener lv3 = new ProgressListener(string.trim(), this.backupFile, arg3, arg2);
                DownloadCountingOutputStream lv4 = new DownloadCountingOutputStream(outputStream2);
                lv4.setListener(lv3);
                IOUtils.copy((InputStream)httpResponse.getEntity().getContent(), (OutputStream)((Object)lv4));
                return;
            }
            catch (Exception exception3) {
                LOGGER.error("Caught exception while downloading: " + exception3.getMessage());
                this.error = true;
                return;
            }
            finally {
                block40: {
                    block41: {
                        CloseableHttpResponse httpResponse4;
                        this.httpRequest.releaseConnection();
                        if (this.backupFile != null) {
                            this.backupFile.delete();
                        }
                        if (this.error) break block40;
                        if (arg.resourcePackUrl.isEmpty() || arg.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.backupFile = File.createTempFile("resources", ".tar.gz");
                            this.httpRequest = new HttpGet(arg.resourcePackUrl);
                            httpResponse4 = closeableHttpClient.execute((HttpUriRequest)this.httpRequest);
                            arg2.totalBytes = Long.parseLong(httpResponse4.getFirstHeader("Content-Length").getValue());
                            if (httpResponse4.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.httpRequest.abort();
                                return;
                            }
                        }
                        catch (Exception exception4) {
                            LOGGER.error("Caught exception while downloading: " + exception4.getMessage());
                            this.error = true;
                        }
                        FileOutputStream outputStream4 = new FileOutputStream(this.backupFile);
                        ResourcePackProgressListener lv7 = new ResourcePackProgressListener(this.backupFile, arg2, arg);
                        DownloadCountingOutputStream lv8 = new DownloadCountingOutputStream(outputStream4);
                        lv8.setListener(lv7);
                        IOUtils.copy((InputStream)httpResponse4.getEntity().getContent(), (OutputStream)((Object)lv8));
                        break block40;
                        finally {
                            this.httpRequest.releaseConnection();
                            if (this.backupFile != null) {
                                this.backupFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if (closeableHttpClient != null) {
                    try {
                        closeableHttpClient.close();
                    }
                    catch (IOException iOException3) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.httpRequest != null) {
            this.httpRequest.abort();
        }
        if (this.backupFile != null) {
            this.backupFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String string) {
        string = string.replaceAll("[\\./\"]", "_");
        for (String string2 : INVALID_FILE_NAMES) {
            if (!string.equalsIgnoreCase(string2)) continue;
            string = "_" + string + "_";
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void untarGzipArchive(String string, File file, LevelStorage arg) throws IOException {
        File file2;
        TarArchiveInputStream tarArchiveInputStream;
        String string3;
        block72: {
            char c;
            Pattern pattern = Pattern.compile(".*-([0-9]+)$");
            int i = 1;
            char[] arrc = SharedConstants.INVALID_CHARS_LEVEL_NAME;
            int n = arrc.length;
            for (int n2 = 0; n2 < n; string = string.replace(c, '_'), ++n2) {
                c = arrc[n2];
            }
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = "Realm";
            }
            string = FileDownload.findAvailableFolderName(string);
            try {
                for (LevelSummary lv : arg.getLevelList()) {
                    if (!lv.getName().toLowerCase(Locale.ROOT).startsWith(string.toLowerCase(Locale.ROOT))) continue;
                    Matcher matcher = pattern.matcher(lv.getName());
                    if (matcher.matches()) {
                        if (Integer.valueOf(matcher.group(1)) <= i) continue;
                        i = Integer.valueOf(matcher.group(1));
                        continue;
                    }
                    ++i;
                }
            }
            catch (Exception exception) {
                LOGGER.error("Error getting level list", (Throwable)exception);
                this.error = true;
                return;
            }
            if (!arg.isLevelNameValid(string) || i > 1) {
                String string2 = string + (i == 1 ? "" : "-" + i);
                if (!arg.isLevelNameValid(string2)) {
                    boolean bl = false;
                    while (!bl) {
                        string2 = string + (++i == 1 ? "" : "-" + i);
                        if (!arg.isLevelNameValid(string2)) continue;
                        bl = true;
                    }
                }
            } else {
                string3 = string;
            }
            tarArchiveInputStream = null;
            file2 = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");
            try {
                file2.mkdir();
                tarArchiveInputStream = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream(file))));
                TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
                while (tarArchiveEntry != null) {
                    File file3 = new File(file2, tarArchiveEntry.getName().replace("world", string3));
                    if (tarArchiveEntry.isDirectory()) {
                        file3.mkdirs();
                    } else {
                        file3.createNewFile();
                        try (FileOutputStream fileOutputStream = new FileOutputStream(file3);){
                            IOUtils.copy((InputStream)tarArchiveInputStream, (OutputStream)fileOutputStream);
                        }
                    }
                    tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
                }
                if (tarArchiveInputStream == null) break block72;
            }
            catch (Exception exception2) {
                LOGGER.error("Error extracting world", (Throwable)exception2);
                this.error = true;
                return;
            }
            tarArchiveInputStream.close();
        }
        if (file != null) {
            file.delete();
        }
        try {
            Throwable throwable = null;
            try (LevelStorage.Session lv2 = arg.createSession(string3);){
                lv2.save(string3.trim());
                Path path = lv2.getDirectory(class_5218.LEVEL_DAT);
                FileDownload.readNbtFile(path.toFile());
            }
            catch (Throwable path) {
                Throwable throwable2 = path;
                throw path;
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to rename unpacked realms level {}", (Object)string3, (Object)iOException);
        }
        this.resourcePackPath = new File(file2, string3 + File.separator + "resources.zip");
        return;
        finally {
            if (tarArchiveInputStream != null) {
                tarArchiveInputStream.close();
            }
            if (file != null) {
                file.delete();
            }
            try (LevelStorage.Session lv3 = arg.createSession(string3);){
                lv3.save(string3.trim());
                Path path2 = lv3.getDirectory(class_5218.LEVEL_DAT);
                FileDownload.readNbtFile(path2.toFile());
            }
            catch (IOException iOException2) {
                LOGGER.error("Failed to rename unpacked realms level {}", (Object)string3, (Object)iOException2);
            }
            this.resourcePackPath = new File(file2, string3 + File.separator + "resources.zip");
        }
    }

    private static void readNbtFile(File file) {
        if (file.exists()) {
            try {
                CompoundTag lv = NbtIo.readCompressed(new FileInputStream(file));
                CompoundTag lv2 = lv.getCompound("Data");
                lv2.remove("Player");
                NbtIo.writeCompressed(lv, new FileOutputStream(file));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DownloadCountingOutputStream
    extends CountingOutputStream {
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream outputStream) {
            super(outputStream);
        }

        public void setListener(ActionListener actionListener) {
            this.listener = actionListener;
        }

        protected void afterWrite(int i) throws IOException {
            super.afterWrite(i);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object)this, 0, null));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        private ResourcePackProgressListener(File file, RealmsDownloadLatestWorldScreen.DownloadStatus arg2, WorldDownload arg3) {
            this.tempFile = file;
            this.downloadStatus = arg2;
            this.worldDownload = arg3;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)actionEvent.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String string = Hashing.sha1().hashBytes(Files.toByteArray((File)this.tempFile)).toString();
                    if (string.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File)this.tempFile, (File)FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + string + "). Deleting it.");
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file", (Object)iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorage levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        private ProgressListener(String string, File file, LevelStorage arg2, RealmsDownloadLatestWorldScreen.DownloadStatus arg3) {
            this.worldName = string;
            this.tempFile = file;
            this.levelStorageSource = arg2;
            this.downloadStatus = arg3;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)actionEvent.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                }
                catch (IOException iOException) {
                    LOGGER.error("Error extracting archive", (Throwable)iOException);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}

