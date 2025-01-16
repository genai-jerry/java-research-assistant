package com.genaipeople.researcher.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    private static Config instance;
    private Properties properties;
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    private Config() {
        loadProperties();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading configuration: " + ex.getMessage(), ex);
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    public void setValue(String key, String value) {
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving configuration: " + ex.getMessage(), ex);
        }
    }

    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public List<String> getListValue(String key) {
        String value = getValue(key);
        return value != null ? Arrays.asList(value.split(",")) : new ArrayList<>();
    }
} 