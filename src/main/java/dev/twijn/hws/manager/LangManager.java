package dev.twijn.hws.manager;

import dev.twijn.hws.HWSPlugin;
import dev.twijn.hws.objects.Configuration;
import org.bukkit.ChatColor;

import java.io.FileNotFoundException;

public class LangManager {

    private Configuration lang;

    private String prefix;
    private String extension;

    private String normal;
    private String error;

    public LangManager() throws FileNotFoundException {
        Configuration config = HWSPlugin.getInstance().getConfiguration("lang/lang.yml");

        // TODO: this probably shouldn't be throwing this exception.
        if (config == null) throw new FileNotFoundException("lang/lang.yml configuration does not exist");

        String enabled = config.getYAML().getString("enabled-language");

        lang = HWSPlugin.getInstance().getConfiguration("lang/" + enabled + ".yml");

        if (lang == null) throw new FileNotFoundException("lang/" + enabled + ".yml configuration does not exist");

        prefix = getNoPrefix("prefix");
        extension = getNoPrefix("extension-prefix");

        normal = getNoPrefix("message-color");
        error = getNoPrefix("error-color");
    }

    public String getNormal(String node) {
        return prefix + normal + getNoPrefix(node);
    }

    public String getExtension(String node) {
        return extension + normal + getNoPrefix(node);
    }

    public String getError(String node) {
        return prefix + error + getNoPrefix(node);
    }

    public String getNoPrefix(String node) {
        String value = lang.getYAML().getString(node);
        return ChatColor.translateAlternateColorCodes('&', value == null ? node : value);
    }

}
