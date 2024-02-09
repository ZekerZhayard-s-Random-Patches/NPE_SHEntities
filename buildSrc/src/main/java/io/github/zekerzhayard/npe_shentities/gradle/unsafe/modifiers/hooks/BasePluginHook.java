package io.github.zekerzhayard.npe_shentities.gradle.unsafe.modifiers.hooks;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.gradle.common.BasePlugin;
import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.delayed.DelayedString;

public class BasePluginHook {
    private static String clientJarUrl;
    private static String serverJarUrl;
    private static String assetsIndexUrl;
    private static String versionJsonUrl;

    public static String resolve(String pattern, BasePlugin<?> plugin) throws Throwable {
        if (pattern.contains("{CLIENT_JAR_URL}")) {
            if (clientJarUrl == null) {
                try (
                    InputStream is = new FileInputStream(new DelayedFile(plugin.project, Constants.VERSION_JSON, plugin).resolveDelayed());
                    InputStreamReader isr = new InputStreamReader(is)
                ) {
                    clientJarUrl = new JsonParser().parse(isr).getAsJsonObject().getAsJsonObject("downloads").getAsJsonObject("client").getAsJsonPrimitive("url").getAsString();
                }
            }
            pattern = pattern.replace("{CLIENT_JAR_URL}", clientJarUrl);
        }

        if (pattern.contains("{SERVER_JAR_URL}")) {
            if (serverJarUrl == null) {
                try (
                    InputStream is = new FileInputStream(new DelayedFile(plugin.project, Constants.VERSION_JSON, plugin).resolveDelayed());
                    InputStreamReader isr = new InputStreamReader(is)
                ) {
                    serverJarUrl = new JsonParser().parse(isr).getAsJsonObject().getAsJsonObject("downloads").getAsJsonObject("server").getAsJsonPrimitive("url").getAsString();
                }
            }
            pattern = pattern.replace("{SERVER_JAR_URL}", serverJarUrl);
        }

        if (pattern.contains("{ASSETS_INDEX_URL}")) {
            if (assetsIndexUrl == null) {
                try (
                    InputStream is = new FileInputStream(new DelayedFile(plugin.project, Constants.VERSION_JSON, plugin).resolveDelayed());
                    InputStreamReader isr = new InputStreamReader(is)
                ) {
                    assetsIndexUrl = new JsonParser().parse(isr).getAsJsonObject().getAsJsonObject("assetIndex").getAsJsonPrimitive("url").getAsString();
                }
            }
            pattern = pattern.replace("{ASSETS_INDEX_URL}", assetsIndexUrl);
        }

        if (pattern.contains("{VERSION_JSON_URL}")) {
            if (versionJsonUrl == null) {
                try (
                    InputStream is = new URL("https://piston-meta.mojang.com/mc/game/version_manifest.json").openStream();
                    InputStreamReader isr = new InputStreamReader(is);
                ) {
                    for (JsonElement element : new JsonParser().parse(isr).getAsJsonObject().getAsJsonArray("versions")) {
                        JsonObject object = element.getAsJsonObject();
                        if (object.getAsJsonPrimitive("id").getAsString().equals(new DelayedString(plugin.project, "{MC_VERSION}", plugin).resolveDelayed())) {
                            versionJsonUrl = object.getAsJsonPrimitive("url").getAsString();
                            break;
                        }
                    }
                }
            }
            pattern = pattern.replace("{VERSION_JSON_URL}", versionJsonUrl);
        }

        return pattern;
    }
}
