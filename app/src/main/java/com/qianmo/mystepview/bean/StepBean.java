package com.qianmo.mystepview.bean;

/**
 * Created by Administrator on 2017/3/16 0016.
 * E-Mail：543441727@qq.com
 */

public class StepBean {
    public static final int STEP_UNDO = -1;//未完成  undo step
    public static final int STEP_CURRENT = 0;//正在进行 current step
    public static final int STEP_COMPLETED = 1;//已完成 completed step

    private String name;
    private int state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public StepBean() {
    }

    public StepBean(String name, int state) {
        this.name = name;
        this.state = state;
    }
}
