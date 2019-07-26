package de.erdbeerbaerlp.guilib.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class GuiComponent extends Gui {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    protected final FontRenderer fontRenderer;
    public int packedFGColour; //FML  (what does this do?)
    protected Minecraft mc = Minecraft.getMinecraft();
    protected int width;
    protected int height;
    protected int id;
    protected boolean hovered; //Sometimes used by components
    protected boolean visible = true, enabled = true;
    private int x;
    private int y;
    private String[] tooltips = new String[0];
    private int assignedPage = -1;

    protected GuiComponent(int x, int y, int width, int height) {
        this.fontRenderer = mc.fontRenderer;
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
    }

    public boolean canHaveTooltip() {
        return true;
    }

    public final String[] getTooltips() {
        return this.tooltips;
    }

    public final void setTooltips(String... strings) {
        this.tooltips = strings;
    }

    public final boolean isVisible() {
        return this.visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public final int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public final int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public final int getWidth() {
        return width;
    }

    public final void setWidth(int width) {
        this.width = width;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public final int getHeight() {
        return height;
    }

    public void playPressSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, this.enabled ? 1.0F : 0.5f));
    }

    public abstract void draw(int mouseX, int mouseY, float partial);

    public abstract void mouseClick(int mouseX, int mouseY, int mouseButton) throws IOException;

    public abstract void mouseReleased(int mouseX, int mouseY, int state);

    public abstract void keyTyped(char typedChar, int keyCode) throws IOException;

    public abstract void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);

    public void updateComponent() {

    }

    public void handleMouseInput() {

    }

    public void handleKeyboardInput() {

    }

    public final void disable() {
        setEnabled(false);
    }

    public final void enable() {
        setEnabled(true);
    }

    public final void hide() {
        setVisible(false);
    }

    public final void show() {
        setVisible(true);
    }

    public final void assignToPage(int page) {
        assignedPage = page;
    }

    public final int getAssignedPage() {
        return assignedPage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

}
