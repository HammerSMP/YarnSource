/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Session;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileUpload {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File file;
    private final long worldId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final UploadStatus uploadStatus;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private CompletableFuture<UploadResult> uploadTask;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

    public FileUpload(File file, long l, int i, UploadInfo arg, Session arg2, String string, UploadStatus arg3) {
        this.file = file;
        this.worldId = l;
        this.slotId = i;
        this.uploadInfo = arg;
        this.sessionId = arg2.getSessionId();
        this.username = arg2.getUsername();
        this.clientVersion = string;
        this.uploadStatus = arg3;
    }

    public void upload(Consumer<UploadResult> consumer) {
        if (this.uploadTask != null) {
            return;
        }
        this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0));
        this.uploadTask.thenAccept((Consumer)consumer);
    }

    public void cancel() {
        this.cancelled.set(true);
        if (this.uploadTask != null) {
            this.uploadTask.cancel(false);
            this.uploadTask = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UploadResult requestUpload(int i) {
        UploadResult.Builder lv = new UploadResult.Builder();
        if (this.cancelled.get()) {
            return lv.build();
        }
        this.uploadStatus.totalBytes = this.file.length();
        HttpPost httpPost = new HttpPost("http://" + this.uploadInfo.getUploadEndpoint() + ":" + this.uploadInfo.getPort() + "/upload" + "/" + this.worldId + "/" + this.slotId);
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
        try {
            this.setupRequest(httpPost);
            CloseableHttpResponse httpResponse = closeableHttpClient.execute((HttpUriRequest)httpPost);
            long l = this.getRetryDelaySeconds((HttpResponse)httpResponse);
            if (this.shouldRetry(l, i)) {
                UploadResult uploadResult = this.retryUploadAfter(l, i);
                return uploadResult;
            }
            this.handleResponse((HttpResponse)httpResponse, lv);
        }
        catch (Exception exception) {
            if (!this.cancelled.get()) {
                LOGGER.error("Caught exception while uploading: ", (Throwable)exception);
            }
        }
        finally {
            this.cleanup(httpPost, closeableHttpClient);
        }
        return lv.build();
    }

    private void cleanup(HttpPost httpPost, CloseableHttpClient closeableHttpClient) {
        httpPost.releaseConnection();
        if (closeableHttpClient != null) {
            try {
                closeableHttpClient.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to close Realms upload client");
            }
        }
    }

    private void setupRequest(HttpPost httpPost) throws FileNotFoundException {
        httpPost.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
        CustomInputStreamEntity lv = new CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
        lv.setContentType("application/octet-stream");
        httpPost.setEntity((HttpEntity)lv);
    }

    private void handleResponse(HttpResponse httpResponse, UploadResult.Builder arg) throws IOException {
        String string;
        int i = httpResponse.getStatusLine().getStatusCode();
        if (i == 401) {
            LOGGER.debug("Realms server returned 401: " + (Object)httpResponse.getFirstHeader("WWW-Authenticate"));
        }
        arg.withStatusCode(i);
        if (httpResponse.getEntity() != null && (string = EntityUtils.toString((HttpEntity)httpResponse.getEntity(), (String)"UTF-8")) != null) {
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(string).getAsJsonObject().get("errorMsg");
                Optional<String> optional = Optional.ofNullable(jsonElement).map(JsonElement::getAsString);
                arg.withErrorMessage(optional.orElse(null));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private boolean shouldRetry(long l, int i) {
        return l > 0L && i + 1 < 5;
    }

    private UploadResult retryUploadAfter(long l, int i) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(l).toMillis());
        return this.requestUpload(i + 1);
    }

    private long getRetryDelaySeconds(HttpResponse httpResponse) {
        return Optional.ofNullable(httpResponse.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
    }

    public boolean isFinished() {
        return this.uploadTask.isDone() || this.uploadTask.isCancelled();
    }

    @Environment(value=EnvType.CLIENT)
    static class CustomInputStreamEntity
    extends InputStreamEntity {
        private final long length;
        private final InputStream content;
        private final UploadStatus uploadStatus;

        public CustomInputStreamEntity(InputStream inputStream, long l, UploadStatus arg) {
            super(inputStream);
            this.content = inputStream;
            this.length = l;
            this.uploadStatus = arg;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void writeTo(OutputStream outputStream) throws IOException {
            block7: {
                Args.notNull((Object)outputStream, (String)"Output stream");
                try (InputStream inputStream = this.content;){
                    int j;
                    byte[] bs = new byte[4096];
                    if (this.length < 0L) {
                        int i;
                        while ((i = inputStream.read(bs)) != -1) {
                            outputStream.write(bs, 0, i);
                            UploadStatus uploadStatus = this.uploadStatus;
                            Long.valueOf(uploadStatus.bytesWritten + (long)i);
                            uploadStatus.bytesWritten = uploadStatus.bytesWritten;
                        }
                        break block7;
                    }
                    for (long l = this.length; l > 0L; l -= (long)j) {
                        j = inputStream.read(bs, 0, (int)Math.min(4096L, l));
                        if (j == -1) {
                            break;
                        }
                        outputStream.write(bs, 0, j);
                        UploadStatus uploadStatus = this.uploadStatus;
                        Long.valueOf(uploadStatus.bytesWritten + (long)j);
                        uploadStatus.bytesWritten = uploadStatus.bytesWritten;
                        outputStream.flush();
                    }
                }
            }
        }
    }
}

