package de.schegar.alarmclock;

public enum Scenes {
    HOME(0), ALARM(1);

    private int id;

    Scenes(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
