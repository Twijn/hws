package dev.twijn.hws.objects;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class Configuration {
    private String name;
    private File file;
    private String resource;

    private YamlConfiguration yaml = null;

    public Configuration(String name, File file, String resource) {
        this.name = name;
        this.file = file;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getResource() {
        return resource;
    }

    public YamlConfiguration getYAML() {
        return yaml;
    }

    public void loadYAML() {
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public void generateFromResource(File directory) throws IOException {
        if (resource != null) {
            if (!directory.isDirectory()) throw new IllegalArgumentException("'Directory' must be a directory");

            InputStream in = getClass().getResourceAsStream(getResource());

            if (!file.exists()) file.createNewFile();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader.close();
            writer.close();
        }
    }

}
