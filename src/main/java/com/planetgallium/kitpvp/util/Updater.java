package com.planetgallium.kitpvp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

public class Updater {
   
    private JavaPlugin plugin;
    private final String API_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
    private final String REQUEST_METHOD = "POST";
    private String RESOURCE_ID = "";
    private final String HOST = "https://www.spigotmc.org";
    private final String QUERY = "/api/general.php";
    private String WRITE_STRING;
   
    private String version;
    private String oldVersion;
   
    private Updater.UpdateResult result = Updater.UpdateResult.DISABLED;
   
    private HttpURLConnection connection;
   
    public enum UpdateResult {
        NO_UPDATE,
        DISABLED,
        FAIL_SPIGOT,
        FAIL_NOVERSION,
        BAD_RESOURCEID,
        UPDATE_AVAILABLE
    }
   
    public Updater(JavaPlugin plugin, Integer resourceId, boolean disabled) {
        RESOURCE_ID = resourceId + "";
        this.plugin = plugin;
        oldVersion = this.plugin.getDescription().getVersion();
       
        if (disabled) {
            result = UpdateResult.DISABLED;
            return;
        }

        try {
            connection = (HttpURLConnection) new URL(HOST + QUERY).openConnection();
        } catch (IOException e) {
            result = UpdateResult.FAIL_SPIGOT;
            return;
        }

        WRITE_STRING = "key=" + API_KEY + "&resource=" + RESOURCE_ID;
        run();
    }
   
    private void run() {
        connection.setDoOutput(true);
        try {
            connection.setRequestMethod(REQUEST_METHOD);
            connection.getOutputStream().write(WRITE_STRING.getBytes("UTF-8"));
        } catch (ProtocolException e1) {
            result = UpdateResult.FAIL_SPIGOT;
        } catch (UnsupportedEncodingException e) {
            result = UpdateResult.FAIL_SPIGOT;
        } catch (IOException e) {
            result = UpdateResult.FAIL_SPIGOT;
        }
        String version;
        try {
            version = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        } catch (Exception e) {
            result = UpdateResult.BAD_RESOURCEID;
            return;
        }
        if (version.length() <= 7) {
            this.version = version;
            version.replace("[^A-Za-z]", "").replace("|", "");
            versionCheck();
            return;
        }
        result = UpdateResult.BAD_RESOURCEID;
    }
   
    private void versionCheck() {
        if(shouldUpdate(oldVersion, version)) {
            result = UpdateResult.UPDATE_AVAILABLE;
        } else {
            result = UpdateResult.NO_UPDATE;
        }
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }
   
    public UpdateResult getResult() {
        return result;
    }
   
    public String getVersion() {
        return version;
    }

}
