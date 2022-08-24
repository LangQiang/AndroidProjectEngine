package com.lazylite.mod.widget.taskweight.model;


import com.lazylite.mod.widget.taskweight.model.mark.ILandMark;

import org.json.JSONObject;

public class SimpleTaskInfo implements ITask {

    private ILandMark landMark;

    public int taskId;

    public String title;

    public String resultDialogTitle;

    public String rewardStr;

    public int rewardType;

    public boolean isReceived;

    public BtnConfig unfinished;
    public BtnConfig received;
    public BtnConfig finished;

    public SimpleTaskInfo(ILandMark landMark) {
        this.landMark = landMark;
    }

    @Override
    public ILandMark getLandMark() {
        return landMark;
    }

//    public static SimpleTaskInfo parser(String data) {
//        ImageLandMark imageLandMark = new ImageLandMark();
//        SimpleTaskInfo simpleTaskInfo = new SimpleTaskInfo(imageLandMark);
//        return simpleTaskInfo;
//    }

    public static class BtnConfig {
        public String text;
        public String textColor;
        public String bgColor;

        public static BtnConfig fromJson(JSONObject jsonObject) {
            if (jsonObject == null) {
                return null;
            }
            BtnConfig btnConfig = new BtnConfig();
            btnConfig.bgColor = jsonObject.optString("bgColor");
            btnConfig.textColor = jsonObject.optString("textColor");
            btnConfig.text = jsonObject.optString("text");
            return btnConfig;
        }
    }
}
