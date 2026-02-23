package com.hitech.levelstate;

public class channelModel {

    private String channel_name;
    private int delay_time;
    private boolean active;
    private boolean water_marker,energised_marker;

    public channelModel(String channel_name, int delayTime, boolean activeMrk,boolean waterMrk,boolean enrgised) {
        this.channel_name = channel_name;
        this.delay_time = delayTime;
        this.active = activeMrk;
        this.water_marker = waterMrk;
        this.energised_marker = enrgised;
    }

    public boolean getChannelActive() {
        return active;
    }

    public void setChannelActive(boolean activeState) {
        this.active = activeState;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public int getImgid() {
        return delay_time;
    }

    public void setImgid(int delayTime) {
        this.delay_time = delayTime;
    }

    public boolean getCahnnelEnergised() { return energised_marker;}

    public void setChannelEnergised(boolean energised_state)
    {
        this.energised_marker = energised_state;
    }

    public boolean getCahnnelWaterSteam() { return water_marker;}

    public void setChannelWaterSteam(boolean water_or_steam)
    {
        this.water_marker = water_or_steam;
    }
    public int getDelayTime() { return delay_time; }

    public void setDelayTime(int dtime)
    {
        this.delay_time = dtime;
    }


}
