package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.screen.Screen;

/**
 * Wrapper class for Post-Rewrite Compatibility
 */
public abstract class BaseScreen extends GuiBase
{
    public BaseScreen() {}

    public void initGui()
    {
        super.initGui();
    }

    public static void openScreen(Screen screen)
    {
        GuiBase.openGui(screen);
    }
}
