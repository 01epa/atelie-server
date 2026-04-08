package com.atelie.base.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class AbstractView extends VerticalLayout {
    protected String t(String key) {
        return getTranslation(key);
    }

    protected String t(String key, Object... args) {
        return getTranslation(key, args);
    }

    protected String t(Enum<?> e) {
        return getTranslation(e.getClass().getSimpleName() + "." + e.name());
    }
}
