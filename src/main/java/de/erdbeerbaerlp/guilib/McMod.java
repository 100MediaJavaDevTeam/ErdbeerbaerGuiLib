package de.erdbeerbaerlp.guilib;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(McMod.MODID)
public class McMod {
    public static final String MODID = "eguilib";
    public static final Logger LOGGER = LogManager.getLogger();

    public McMod() {}

    @HasConfigGui
    private static class ConfigGetter {
        public Screen getConfigGUI(final Screen modList) {
            return new ExampleGUI(modList);
        }
    }
}
