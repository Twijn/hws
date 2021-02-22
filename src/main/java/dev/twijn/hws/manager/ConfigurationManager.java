package dev.twijn.hws.manager;

import dev.twijn.hws.objects.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationManager implements Manager {

    private final File directory;

    public ConfigurationManager(File directory) {
        if (!directory.isDirectory()) throw new IllegalArgumentException("'Directory' must be a parent directory");

        this.directory = directory;
    }

    public List<Configuration> generateConfigurations() {
        List<Configuration> configurations = new ArrayList<Configuration>();

        // Create lang directory
        File langDirectory = new File(directory + File.separator + "lang");
        if (!langDirectory.exists()) langDirectory.mkdirs();

        List<String> builtInLanguages = Arrays.asList("lang.yml", "en-us.yml");

        for (String langName : builtInLanguages) {
            configurations.add(new Configuration(
                    "lang/" + langName,
                    new File(directory + File.separator + "lang", langName),
                    "/config/lang/" + langName
            ));
        }

        for (String langName : langDirectory.list()) {
            if (langName.toLowerCase().endsWith(".yml")) {
                if (!builtInLanguages.contains(langName)) {
                    configurations.add(new Configuration(
                            "lang/" + langName,
                            new File(directory + File.separator + "lang", langName),
                            null
                    ));
                }
            }
        }

        configurations.add(new Configuration(
                "config.yml",
                new File(directory, "config.yml"),
                "/config/config.yml"
        ));

        return configurations;
    }

    public void validate(List<Configuration> configurations) {
        for (Configuration config : configurations) {
            File file = config.getFile();

            if (!file.exists()) {
                try {
                    config.generateFromResource(directory);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            if (file.exists()) config.loadYAML();
        }
    }

}
