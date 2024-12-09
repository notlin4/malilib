package fi.dy.masa.malilib.config.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.util.FileUtils;

public class ConfigUtils
{
    public static Path getConfigDirectory()
    {
        return FileUtils.getMinecraftDirectoryPath().resolve("config");
    }

    /**
     * Returns a Path to a directory by the given name inside the main config directory.
     * Usually the given name would be the modId of the mod calling this.
     */
    public static Path getConfigDirectory(String directoryName)
    {
        return getConfigDirectory().resolve(directoryName);
    }

    /**
     * Returns a Path to a directory by the given name inside the main config directory.
     * Usually the given name would be the modId of the mod calling this.
     * Tries to create the directory and any missing parent directories, if it doesn't exist yet.
     */
    public static Path createAndGetConfigDirectory(String directoryName)
    {
        Path dir = getConfigDirectory(directoryName);
        FileUtils.createDirectoriesIfMissing(dir);
        return dir;
    }

    /**
     * @return The currently active config directory. This takes into account a possible active config profile.
     */
    public static Path getActiveConfigDirectory()
    {
        String profile = MaLiLibConfigs.Experimental.ACTIVE_CONFIG_PROFILE.getValue();
        return getActiveConfigDirectory(profile);
    }

    /**
     * @return The currently active config directory for the given config profile.
     */
    public static Path getActiveConfigDirectory(String profile)
    {
        Path baseConfigDir = getConfigDirectory();

        if (org.apache.commons.lang3.StringUtils.isBlank(profile) == false)
        {
            try
            {
                return baseConfigDir.resolve("config_profiles").resolve(profile);
            }
            catch (InvalidPathException ignore) {}
        }

        return baseConfigDir;
    }

    /**
     * Sort the given list of configs by the config's display name,
     * stripping away any vanilla text formatting codes first.
     * Note: The input list must be modifiable!
     */
    public static List<ConfigInfo> sortConfigsInPlaceByDisplayName(List<ConfigInfo> configs)
    {
        /*
        configs.sort(Comparator.comparing((c) -> TextRendererUtils.stripVanillaFormattingCodes(c.getDisplayName())));
         */
        return configs;
    }
}
