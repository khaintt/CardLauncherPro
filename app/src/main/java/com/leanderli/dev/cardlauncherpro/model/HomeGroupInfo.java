package com.leanderli.dev.cardlauncherpro.model;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/4/5 00051333
 */

public class HomeGroupInfo {

    public ArrayList<SingleGroupInfo> singleGroupInfos;

    public static class SingleGroupInfo {
        public String groupName;
        public ArrayList<AppInfo> appInfos;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public ArrayList<AppInfo> getAppInfos() {
            return appInfos;
        }

        public void setAppInfos(ArrayList<AppInfo> appInfos) {
            this.appInfos = appInfos;
        }
    }

    public ArrayList<SingleGroupInfo> getSingleGroupInfos() {
        return singleGroupInfos;
    }

    public void setSingleGroupInfos(ArrayList<SingleGroupInfo> singleGroupInfos) {
        this.singleGroupInfos = singleGroupInfos;
    }
}
