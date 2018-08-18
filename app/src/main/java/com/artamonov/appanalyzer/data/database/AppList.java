package com.artamonov.appanalyzer.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

@Entity(tableName = "Application")
public class AppList {
    // @PrimaryKey(autoGenerate = true)
    // private int id;
    private String name;
    @PrimaryKey
    @NonNull
    private String packageName;
    private String version;
    private String lastUpdateTime;
    private String lastRunTime;
    private String firstInstallTime;
    private String gpRating;
    private String gpInstalls;
    private String gpPeople;
    private String gpUpdated;
    @Ignore
    private Drawable icon;
    private byte[] byteIcon;
    private boolean isOnline;
    private String appSource;
    private String trustLevel;
    private String dangerousPermissionsAmount;
    private String permissionGroups;

    private String permissionGroupsAmount;

    public String getPermissionGroupsAmount() {
        return permissionGroupsAmount;
    }

    public void setPermissionGroupsAmount(String permissionGroupsAmount) {
        this.permissionGroupsAmount = permissionGroupsAmount;
    }

    public String getPermissionGroups() {
        return permissionGroups;
    }

    public void setPermissionGroups(String permissionGroups) {
        this.permissionGroups = permissionGroups;
    }

    public String getDangerousPermissionsAmount() {
        return dangerousPermissionsAmount;
    }

    public void setDangerousPermissionsAmount(String dangerousPermissionsAmount) {
        this.dangerousPermissionsAmount = dangerousPermissionsAmount;
    }
    /*   private ArrayList<String> appRequestedPermissions;
    private ArrayList<String> appGrantedPermissions;
    private ArrayList<String> requestedPermissionsProtectionLevels;
    private ArrayList<String> grantedPermissionsProtectionLevels;*/

  /*  @Ignore
    public AppList(String packageName, String versionName, String gpInstalls, String gpPeople,
                   String gpRating, String gpUpdated) {
        this.packageName = packageName;
        this.version = versionName;
        this.gpInstalls = gpInstalls;
        this.gpPeople = gpPeople;
        this.gpRating = gpRating;
        this.gpUpdated = gpUpdated;
    }*/

    public AppList(String packageName, String versionName, String gpInstalls, String gpPeople,
                   String gpRating, String gpUpdated) {
        //  this.id = id;
        this.packageName = packageName;
        this.version = versionName;
        this.gpInstalls = gpInstalls;
        this.gpPeople = gpPeople;
        this.gpRating = gpRating;
        this.gpUpdated = gpUpdated;
    }

    public AppList() {

    }

    // public Integer getId() {
    //    return id;
    //  }

    // public void setId(Integer id) {
    //    this.id = id;
    // }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

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
