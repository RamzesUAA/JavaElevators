package data;

import java.util.EnumSet;

public enum Speed {
    Slow(1), Medium(5), High(10);

    Speed(int speed)
    {
        this.speed = speed;
    }

    private int speed;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public static EnumSet<Speed> speeds = EnumSet.allOf(Speed.class);
}
