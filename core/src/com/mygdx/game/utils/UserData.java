package com.mygdx.game.utils;

public class UserData {
    private String name;
    private boolean isDead;
    public UserData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
}
