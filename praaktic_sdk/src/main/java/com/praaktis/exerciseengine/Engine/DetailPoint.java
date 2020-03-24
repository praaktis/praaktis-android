package com.praaktis.exerciseengine.Engine;

import java.io.Serializable;

public class DetailPoint implements Serializable {
    public float value;
    public int id;

    public DetailPoint(float value, int id) {
        this.value = value;
        this.id = id;
    }
}
