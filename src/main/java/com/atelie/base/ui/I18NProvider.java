package com.atelie.base.ui;

import com.vaadin.flow.i18n.DefaultI18NProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class I18NProvider extends DefaultI18NProvider {
    public I18NProvider(List<Locale> providedLocales) {
        super(providedLocales);
    }
}