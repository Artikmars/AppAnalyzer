package com.artamonov.appanalyzer;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class AppList {

    private String name;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;
    private String version;
    private String lastUpdateTime;
    private String lastRunTime;
    private String firstInstallTime;


    private String gpRating;
    private String gpInstalls;
    private String gpPeople;
    private String gpUpdated;

    public String getGpRating() {
        return gpRating;
    }

    public void setGpRating(String gpRating) {
        this.gpRating = gpRating;
    }

    public String getGpInstalls() {
        return gpInstalls;
    }

    public void setGpInstalls(String gpInstalls) {
        this.gpInstalls = gpInstalls;
    }

    public String getGpPeople() {
        return gpPeople;
    }

    public void setGpPeople(String gpPeople) {
        this.gpPeople = gpPeople;
    }

    public String getGpUpdated() {
        return gpUpdated;
    }

    public void setGpUpdated(String gpUpdated) {
        this.gpUpdated = gpUpdated;
    }

    private Drawable icon;
    private byte[] byteIcon;
    private boolean isOnline;
    private String appSource;
    private String trustLevel;
    private ArrayList<String> appRequestedPermissions;

    private ArrayList<String> appGrantedPermissions;
    private ArrayList<String> requestedPermissionsProtectionLevels;

    public ArrayList<String> getRequestedPermissionsProtectionLevels() {
        return requestedPermissionsProtectionLevels;
    }

    public void setRequestedPermissionsProtectionLevels(ArrayList<String> requestedPermissionsProtectionLevels) {
        this.requestedPermissionsProtectionLevels = requestedPermissionsProtectionLevels;
    }

    public ArrayList<String> getGrantedPermissionsProtectionLevels() {
        return grantedPermissionsProtectionLevels;
    }

    public void setGrantedPermissionsProtectionLevels(ArrayList<String> grantedPermissionsProtectionLevels) {
        this.grantedPermissionsProtectionLevels = grantedPermissionsProtectionLevels;
    }

    private ArrayList<String> grantedPermissionsProtectionLevels;

    public ArrayList<String> getAppGrantedPermissions() {
        return appGrantedPermissions;
    }

    public void setAppGrantedPermissions(ArrayList<String> appGrantedPermissions) {
        this.appGrantedPermissions = appGrantedPermissions;
    }


    public ArrayList<String> getAppRequestedPermissions() {
        return appRequestedPermissions;
    }

    public void setAppRequestedPermissions(ArrayList<String> appRequestedPermissions) {
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
