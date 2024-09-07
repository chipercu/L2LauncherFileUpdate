package com.fuzzy.enums;

import javafx.scene.paint.Color;

public enum GitStatus {

    committed(Color.WHITE),
    modified(Color.DODGERBLUE),
    removed(Color.GREY),
    untracked(Color.FIREBRICK),
    changed(Color.PALEVIOLETRED),
    added(Color.LAWNGREEN),
    missing(Color.MEDIUMVIOLETRED);

    private final Color color;

    GitStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
