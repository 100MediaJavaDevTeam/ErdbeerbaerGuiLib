package de.erdbeerbaerlp.guilib.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import de.erdbeerbaerlp.guilib.components.GuiComponent;
import de.erdbeerbaerlp.guilib.components.TextField;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public abstract class ExtendedScreen extends Screen {
    private final List<GuiComponent> components = new ArrayList<>();
    private int nextComponentID = 0;
    private int currentPage = 0;
    private Screen parentGui;

    /**
     * The constructor, you need to call this!
     *
     * @param parentGui The gui this was opened from. Can be null
     */
    public ExtendedScreen(@Nullable Screen parentGui) {
        super(new StringTextComponent("An GUI"));
        this.parentGui = parentGui;
        buildGui();
    }

    /**
     * Gets current open page
     *
     * @return current page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Add your components here!
     */
    public abstract void buildGui();

    /**
     * Gets called often to e.g. update components postions
     */
    public abstract void updateGui();

    @Override
    public boolean isPauseScreen() {
        return doesGuiPauseGame();
    }

    /**
     * Should this GUI pause Singleplayer game when open?
     */
    public abstract boolean doesGuiPauseGame();

    /**
     * Should pressing ESC close the GUI?
     */
    public abstract boolean doesEscCloseGui();

    /**
     * Open the next page of the GUI (if available)
     */
    public void nextPage() {
        if (currentPage < Integer.MAX_VALUE) currentPage++;
    }

    /**
     * Open the previous page of the GUI (if available)
     */
    public void prevPage() {
        if (currentPage > 0) currentPage--;
    }

    /**
     * Sets the gui to a specific Page (if it is available!)
     *
     * @param page Page to set the GUI to
     */
    public void setPage(int page) {
        if (page < Integer.MAX_VALUE)
            this.currentPage = page;
    }

    /**
     * Use this to add your components
     *
     * @param component The component to add
     */
    public final void addComponent(GuiComponent component) {
        this.components.add(component);
    }

    /**
     * Use this to add your components
     *
     * @param component The component to add
     * @param page      The page to register the component to
     */
    public final void addComponent(GuiComponent component, int page) {
        this.components.add(component);
        component.assignToPage(page);
    }

    /**
     * Use this to add multiple components at once
     *
     * @param components The components to add
     */
    public final void addAllComponents(GuiComponent... components) {
        for (GuiComponent c : components) {
            this.addComponent(c);
        }
    }

    /**
     * Assigns a component to the page by calling comp.assignToPage(page)
     *
     * @param comp The component to assign
     * @param page The page for the component
     */
    public void assignComponentToPage(GuiComponent comp, int page) {
        comp.assignToPage(page);
    }

    @Override
    @Deprecated
    protected final <T extends Widget> T addButton(T buttonIn) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    protected final void actionPerformed(Widget button) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            if (comp.isVisible() && mouseX >= comp.getX() && mouseY >= comp.getY() && mouseX < comp.getX() + comp.getWidth() && mouseY < comp.getY() + comp.getHeight())
                comp.mouseScrolled(mouseX, mouseY, scroll);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            if (comp.isVisible())
                comp.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            comp.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public final void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        updateGui();
        renderBackground();
        for (final GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            if (comp.isVisible()) comp.render(mouseX, mouseY, partialTicks);
        }
        //Second for to not have components overlap the tooltips
        for (final GuiComponent comp : Lists.reverse(components)) { //Reversing to call front component
            if (!comp.isVisible() || (comp.getAssignedPage() != -1 && comp.getAssignedPage() != currentPage)) continue;
            if (comp.canHaveTooltip() && isHovered(comp, mouseX, mouseY)) {

                final ArrayList<String> list = new ArrayList<>();
                if (comp.getTooltips() != null) {
                    list.addAll(Arrays.asList(comp.getTooltips()));
                }
                if (!list.isEmpty()) {
                    renderTooltip(list, mouseX, mouseY);
                    break;
                }
            }
        }


    }

    public Screen getParentGui() {
        return parentGui;
    }

    public void setParentGui(Screen parentGui) {
        this.parentGui = parentGui;
    }


    /**
     * Override to draw a custom background
     */
    public void renderBackground() {
        if (this.minecraft.world != null && !forceDirtBackground()) {
            this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.minecraft.getTextureManager().bindTexture(getBackground());
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0F, (float) this.height / 32.0F + (float) 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, this.height, 0.0D).tex((float) this.width / 32.0F, (float) this.height / 32.0F + (float) 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float) this.width / 32.0F, (float) 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float) 0).color(64, 64, 64, 255).endVertex();
            tessellator.draw();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
        }
    }

    protected boolean forceDirtBackground() {
        return false;
    }

    /**
     * @return Resource Location of GUIs Background, defaults to {@link net.minecraft.client.gui.AbstractGui#BACKGROUND_LOCATION}
     */
    protected ResourceLocation getBackground() {
        return BACKGROUND_LOCATION;
    }

    private boolean isHovered(GuiComponent comp, int mouseX, int mouseY) {
        if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) return false;
        final int x = comp.getX();
        final int y = comp.getY();
        final int w = comp.getWidth();
        final int h = comp.getHeight();
        return (mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h);
    }

    public final GuiComponent getComponent(int index) {
        return components.get(index);
    }

    /**
     * Opens an GUI
     *
     * @param gui GuiScreen or null
     */
    public final void openGui(@Nullable Screen gui) {
        if (gui == null) minecraft.displayGuiScreen(null);
        else minecraft.displayGuiScreen(gui);
    }

    /**
     * Closes this GUI, returning to parent or null
     */
    public void close() {
        openGui(parentGui);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            if (comp.isVisible() && ((mouseX >= comp.getX() && mouseY >= comp.getY() && mouseX < comp.getX() + comp.getWidth() && mouseY < comp.getY() + comp.getHeight()) || comp instanceof TextField))
                comp.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return true;
    }

    public boolean charTyped(char character, int keyCode) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            comp.charTyped(character, keyCode);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            comp.mouseRelease(mouseX, mouseY, mouseButton);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == 256 && doesEscCloseGui()) {
            //noinspection RedundantCast
            this.minecraft.displayGuiScreen((Screen) null);

            if (this.minecraft.currentScreen == null) {
                this.minecraft.setGameFocused(true);
            }
        }
        for (GuiComponent comp : components) {
            if (comp.getAssignedPage() != -1) if (comp.getAssignedPage() != currentPage) continue;
            comp.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
        }
        return true;
    }

    @Override
    public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
        return super.handleComponentClicked(p_handleComponentClicked_1_);
    }

    @Override
    public void onClose() {
        for (GuiComponent comp : components) {
            comp.unload();
        }
    }
}
