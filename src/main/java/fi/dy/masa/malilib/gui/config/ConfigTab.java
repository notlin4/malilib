package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ConfigTab extends ScreenTab
{
    /**
     * @return the ModInfo of the mod this tab belongs to.
     *         Used on the config screens when showing options from multiple categories
     *         or from all mods, and also used by the config status indicator widgets.
     */
    ModInfo getModInfo();

    /**
     * @return the width of the config option edit widgets on the config screen.
     *         This is used for nicely aligned positioning of the reset button after the edit widgets.
     */
    int getConfigWidgetsWidth();

    /**
     * @return the list of all "tab-owned base configs" on this tab (= without extension mod-added configs),
     *         without expanding any config groups
     */
    List<? extends ConfigInfo> getConfigs();

    /**
     * @return The full list of configs on this tab, possibly sorted by their display name,
     *         if enabled in the malilib configs.
     *         The normal mod-owned base list gets appended with any possible extra options that extension mods
     *         want to show on the same tab with the parent mod's config options.
     */
    default List<ConfigInfo> getExtendedSortedConfigList()
    {
        ArrayList<ConfigInfo> list = new ArrayList<>(this.getConfigs());
        List<ConfigInfo> extensionConfigs = Registry.CONFIG_TAB_EXTENSION.getExtensionConfigsForTab(this);

        if (MaLiLibConfigs.Experimental.SORT_EXTENSION_MOD_OPTIONS.getBooleanValue())
        {
            ConfigUtils.sortConfigsInPlaceByDisplayName(extensionConfigs);
        }

        list.addAll(extensionConfigs);

        if (MaLiLibConfigs.Experimental.SORT_CONFIGS_BY_NAME.getBooleanValue())
        {
            ConfigUtils.sortConfigsInPlaceByDisplayName(list);
        }

        return list;
    }

    default List<ConfigOnTab> getTabbedConfigs()
    {
        ArrayList<ConfigOnTab> list = new ArrayList<>();

        for (ConfigInfo config : this.getExtendedSortedConfigList())
        {
            list.add(new ConfigOnTab(this, config, 0));
        }

        return list;
    }

    /**
     * @return a full list of configs on this tab, including the configs from
     *         any possible nested expandable/collapsible config groups
     */
    default List<ConfigOnTab> getTabbedExpandedConfigs()
    {
        ArrayList<ConfigOnTab> expandedList = new ArrayList<>();

        for (ConfigInfo config : this.getExtendedSortedConfigList())
        {
            expandedList.add(new ConfigOnTab(this, config, 0));
            config.addNestedOptionsToList(expandedList, this, 1, true);
        }

        return expandedList;
    }

    /**
     * Returns a full list of configs on this tab, including the configs from
     * any possible nested expandable/collapsible config groups, wrapped in
     * ConfigOnTab to include the tab information, which includes the owning mod.
     */
    default void offerTabbedExpandedConfigs(Consumer<ConfigOnTab> configConsumer)
    {
        this.getTabbedExpandedConfigs().forEach(configConsumer);
    }
}
