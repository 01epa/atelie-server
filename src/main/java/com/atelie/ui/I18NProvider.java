package com.atelie.ui;

import com.vaadin.flow.i18n.DefaultI18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class I18NProvider extends DefaultI18NProvider {
    private final static List<Locale> locales = List.of(Locale.of("ru"));

    private static final String BUNDLE_NAME = "vaadin-i18n/translations";

    public I18NProvider(List<Locale> providedLocales) {
        super(providedLocales);
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        return MessageFormat.format(bundle.getString(key), params);
    }
}