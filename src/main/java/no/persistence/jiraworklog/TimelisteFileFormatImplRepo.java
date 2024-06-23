package no.persistence.jiraworklog;

import no.persistence.jiraworklog.model.TimelisteFileFormat;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TimelisteFileFormatImplRepo {
    private static Map<String, TimelisteFileFormat> _implMap;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
    }

    public static Map<String, TimelisteFileFormat> getSupportedTimelisteFormats() {
        if (_implMap == null) {
            _implMap = Collections.unmodifiableMap(buildImplMap());
        }
        return _implMap;
    }

    private static Map<String, TimelisteFileFormat> buildImplMap() {
        Map<String, TimelisteFileFormat> map = new TreeMap<>();
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage("no.persistence.jiraworklog"));
        Set<Class<? extends TimelisteFileFormat>> implClasses = reflections.getSubTypesOf(TimelisteFileFormat.class);
        for (Class<? extends TimelisteFileFormat> c : implClasses) {
            try {
                TimelisteFileFormat impl = (TimelisteFileFormat) c.getDeclaredConstructors()[0].newInstance();
                map.put(impl.getFormatName(), impl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public static TimelisteFileFormat getByName(String name) {
        if (name == null) {
            name = TimelisteFileFormatDefaultImpl.STANDARD;
        }
        return getSupportedTimelisteFormats().get(name);
    }
}
