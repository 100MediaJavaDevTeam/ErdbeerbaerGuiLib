package de.erdbeerbaerlp.guilib.mixin;

import de.erdbeerbaerlp.guilib.HasConfigGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.fml.client.gui.widget.ModListWidget;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(ModListScreen.class)
public abstract class MixinModListScreen extends Screen {
    @Unique
    private static final Type HAS_CONFIG_GUI_TYPE = Type.getType(HasConfigGui.class);

    @Shadow(remap = false)
    private Button configButton;
    @Shadow(remap = false)
    private ModListWidget.ModEntry selected;

    protected MixinModListScreen(ITextComponent titleIn) {
        super(titleIn);
    }


    @Inject(method = "updateCache", at = @At(value = "RETURN", remap = false), require = 1, remap = false)
    private void onUpdate(CallbackInfo ci) {
        if (selected != null && ModList.get().getModFileById(selected.getInfo().getModId()).getFile().getScanResult().getAnnotations()
                .stream().anyMatch(annData -> HAS_CONFIG_GUI_TYPE.equals(annData.getAnnotationType()))) {
            configButton.active = true;
        }
    }

    @Inject(method = "displayModConfig", at = @At("HEAD"), remap = false)
    private void displayConfig(CallbackInfo ci) {
        if (selected != null) {
            if (ModList.get().getModObjectById(selected.getInfo().getModId()).isPresent()) {
                final Object mod = ModList.get().getModObjectById(selected.getInfo().getModId()).get();
                if (ArrayUtils.contains(mod.getClass().getInterfaces(), HasConfigGui.class)) {
                    try {
                        final Method getCfg = mod.getClass().getDeclaredMethod("getConfigGUI", Screen.class);
                        final Screen s = (Screen) getCfg.invoke(mod, this);
                        minecraft.setScreen(s);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
