package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ParameterProvider {
    private static final String PARAMETERS_PATH = "configurations/config.properties";

    private static ParameterProvider instance;
    private final Map<String, String> parameters;

    private ParameterProvider() {
        try {
            parameters = new HashMap<>();
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PARAMETERS_PATH);
            prop.load(inputStream);
            prop.stringPropertyNames()
                    .forEach(key -> parameters.put(key, prop.getProperty(key)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        if (instance == null) {
            instance = new ParameterProvider();
        }
        return instance.parameters.get(key);
    }
}
