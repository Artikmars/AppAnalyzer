package com.artamonov.appanalyzer;

import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    private String version;
    private String lastUpdateTime;
    private String lastRunTime;
    private String firstInstallTime;


    private Drawable icon;
    private byte[] byteIcon;
    private boolean isOnline;
    private String appSource;
    private String trustLevel;
    private String appRequestedPermissions;

    public String getAppGrantedPermissions() {
        return appGrantedPermissions;
    }

    public void setAppGrantedPermissions(String appGrantedPermissions) {
        this.appGrantedPermissions = appGrantedPermissions;
    }

    private String appGrantedPermissions;

    public String getAppRequestedPermissions() {
        return appRequestedPermissions;
    }

    public void setAppRequestedPermissions(String appRequestedPermissions) {
        this.appRequestedPermissions = appRequestedPermissions;
    }

    public String getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(String trustLevel) {
        this.trustLevel = trustLevel;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public byte[] getByteIcon() {
        return byteIcon;
    }

    public void setByteIcon(byte[] byteIcon) {
        this.byteIcon = byteIcon;
    }
    /*public AppList(String name, String version, String lastUpdateTime, String lastRunTime, Drawable icon) {
        this.name = name;
        this.version = version;
        this.lastUpdateTime = lastUpdateTime;
        this.lastRunTime = lastRunTime;
        this.icon = icon;
    }

    public AppList(String appName, String versionName, String lastUpdateTime, String lastRunTime) {
        this.name = appName;
        this.version = versionName;
        this.lastUpdateTime = lastUpdateTime;
        this.lastRunTime = lastRunTime;
    }
*/

    public String getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(String firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(String lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

}
