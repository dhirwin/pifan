package org.pifan.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dave Irwin (d.irwin@delcan.com)
 */
public class PropertiesUtils {
    /**
     * Define the root logger.
     */
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * 
     */
    private PropertiesUtils() {
    }

    /**
     * 
     * @param properties
     * @param keyPrefixFilter
     * @return
     */
    public static final Properties filter(Properties properties, String keyPrefixFilter) {
        Properties ret = new Properties();

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(keyPrefixFilter)) {
                ret.setProperty(key, properties.getProperty(key));
            }
        }

        return ret;
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final boolean containsKey(Properties properties, String key) {
        return properties.containsKey(key);
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final boolean containsKey(Map<String, String> properties, String key) {
        return properties.containsKey(key);
    }

    /**
     * Put the given string value only if the existing key does not already exist.
     * 
     * @param properties
     * @param key
     * @param value
     */
    public static final void putStringIfAbsent(Properties properties, String key, String value) {
        if (properties.containsKey(key)) {
            // don't do anything
        } else {
            properties.setProperty(key, value);
        }
    }

    /**
     * Get the string property from the properties object. If the key doesn't exist
     * this method will return a null value.
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final String getStringValue(Properties properties, String key) {
        return getStringValue(properties, key, null);
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final String getStringValue(Properties properties, String key, String defaultValue) {
        try {
            String str = properties.getProperty(key);
            if (str == null) {
                return defaultValue;
            }

            return str;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Put the given integer value only if the existing key does not already exist.
     * 
     * @param properties
     * @param key
     * @param value
     */
    public static final void putIntegerIfAbsent(Properties properties, String key, Integer value) {
        if (properties.containsKey(key)) {
            // don't do anything
        } else {
            properties.setProperty(key, value.toString());
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final boolean getBooleanValue(Properties properties, String key) {
        return getBooleanValue(properties, key, false);
    }

    /**
     * Attempt to retrieve a boolean value from the properties given the key name.
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final boolean getBooleanValue(Properties properties, String key, boolean defaultValue) {
        try {
            String str = properties.getProperty(key);

            if (str == null) {
                return defaultValue;
            }

            return Boolean.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final int getIntegerValue(Properties properties, String key) {
        String str = properties.getProperty(key);
        if (str == null) {
            throw new RuntimeException("No existing int property with key: " + key);
        }

        return Integer.valueOf(str);
    }

    /**
     * 
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final int getIntegerValue(Properties properties, String key, int defaultValue) {
        try {
            String str = properties.getProperty(key);
            if (str == null) {
                return defaultValue;
            }

            return Integer.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final Long getLongValue(Properties properties, String key) {
        String str = properties.getProperty(key);
        if (str == null) {
            throw new RuntimeException("No existing long property with key: " + key);
        }

        return Long.valueOf(str);
    }

    /**
     * Attempt to retrieve a long value from the properties given the key name.
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final long getLongValue(Properties properties, String key, long defaultValue) {
        try {
            String str = properties.getProperty(key);
            if (str == null) {
                return defaultValue;
            }

            return Long.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Attempt to retrieve a long value from the properties given the key name.
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final long getLongValue(Map<String, String> properties, String key, long defaultValue) {
        try {
            String str = properties.get(key);
            if (str == null) {
                return defaultValue;
            }

            return Long.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final float getFloatValue(Properties properties, String key) {
        String str = properties.getProperty(key);
        if (str == null) {
            throw new RuntimeException("No existing float property with key: " + key);
        }

        return Float.valueOf(str);
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final float getFloatValue(Properties properties, String key, float defaultValue) {
        try {
            String str = properties.getProperty(key);
            if (str == null) {
                return defaultValue;
            }

            return Float.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final float getFloatValue(Map<String, String> properties, String key, float defaultValue) {
        try {
            String str = properties.get(key);
            if (str == null) {
                return defaultValue;
            }

            return Float.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final double getDoubleValue(Properties properties, String key) {
        String str = properties.getProperty(key);
        if (str == null) {
            throw new RuntimeException("No existing double property with key: " + key);
        }

        return Double.valueOf(str);
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final double getDoubleValue(Properties properties, String key, double defaultValue) {
        try {
            String str = properties.getProperty(key);
            if (str == null) {
                return defaultValue;
            }

            return Double.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @return
     */
    public static final Map<String, String> toMap(Properties properties) {
        Map<String, String> ret = new HashMap<>();
        for (Object key : properties.keySet()) {
            if (key instanceof String) {
                String keyStr = (String) key;
                ret.put(keyStr, properties.getProperty(keyStr));
            }
        }

        return ret;
    }

    /**
     * 
     * @param properties
     * @return
     */
    public static final Properties fromMap(Map<String, String> properties) {
        Properties ret = new Properties();
        for (String key : properties.keySet()) {
            if (key instanceof String) {
                String keyStr = key;
                ret.put(keyStr, properties.get(keyStr));
            }
        }

        return ret;
    }

    /**
     * 
     * @param props1
     * @param props2
     * @return
     */
    public static final Properties merge(Properties props1, Properties props2) {
        Properties ret = new Properties();

        ret.putAll(props1);
        ret.putAll(props2);

        return ret;
    }

    /**
     * 
     * @param properties
     * @param key
     * @return
     */
    public static final String getStringValue(Map<String, String> properties, String key) {
        String str = properties.get(key);
        if (str == null) {
            return null;
        }

        return str;
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final String getStringValue(Map<String, String> properties, String key, String defaultValue) {
        try {
            String str = properties.get(key);
            if (str == null) {
                return defaultValue;
            }

            return str;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Attempt to retrieve a boolean value from the properties given the key name.
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final boolean getBooleanValue(Map<String, String> properties, String key, boolean defaultValue) {
        try {
            String str = properties.get(key);

            if (str == null) {
                return defaultValue;
            }

            return Boolean.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     * @param properties
     * @param key
     * @param defaultValue
     * @return
     */
    public static final int getIntegerValue(Map<String, String> properties, String key, int defaultValue) {
        try {
            String str = properties.get(key);

            if (str == null) {
                return defaultValue;
            }

            return Integer.valueOf(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 
     */
    public static class PropertyBuilder {
        /**
         * 
         */
        private final Map<String, String> props = new HashMap<>();

        /**
         * 
         */
        private PropertyBuilder() {
            super();
        }

        /**
         * 
         * @param key
         * @param val
         * @return
         */
        public final PropertyBuilder set(String key, String val) {
            this.props.put(key, val);
            return this;
        }

        /**
         * 
         * @return
         */
        public final Map<String, String> get() {
            return this.props;
        }
    }

    /**
     * 
     * @return
     */
    public static final PropertyBuilder newBuilder() {
        return new PropertyBuilder();
    }
}