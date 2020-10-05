package com.example.myapplication3.ui.main;

public class MyGlobals {
    private float goal=150000.f, now = 100000.f, origin =50000.f;
    private boolean isNew=false;
    private boolean isBack = false;

    public boolean getisBack() {
        return isBack;
    }
    public void setisBack(boolean data) {
        this.isBack = data;
    }

    public float getGoal() {
        return goal;
    }
    public void setGoal(float data) {
        this.goal = data;
    }

    public boolean getisNew() {
        return isNew;
    }
    public void setisNew() {
        this.isNew = true;
    }


    public float getNow() {
        return now;
    }
    public void setNow(float data) {
        this.now = data;
    }

    public float getOrigin() {
        return origin;
    }
    public void setOrigin(float data) {
        this.origin = data;
    }

    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance() {
        if (null == instance) {
            instance = new MyGlobals();
        }
        return instance;
    }
}

