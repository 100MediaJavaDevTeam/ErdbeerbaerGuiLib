package de.erdbeerbaerlp.guilib.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.awt.*;

public class ToggleButton extends Button {
    private boolean value = false;
    private DrawType drawType = DrawType.COLORED_LINE;
    private ResourceLocation offIcon = null;

    /**
     * Creates an new ToggleButton
     *
     * @param xPos          X positon
     * @param yPos          Y Position
     * @param displayString Button Text
     */
    public ToggleButton(int xPos, int yPos, String displayString) {
        this(xPos, yPos, 100, displayString);
    }

    /**
     * Creates an new ToggleButton
     *
     * @param xPos          X positon
     * @param yPos          Y Position
     * @param width         Button Width
     * @param displayString Default String
     */
    public ToggleButton(int xPos, int yPos, int width, String displayString) {
        this(xPos, yPos, width, 20, displayString);
    }

    /**
     * Creates an new ToggleButton
     *
     * @param xPos          X positon
     * @param yPos          Y Position
     * @param width         Button Width
     * @param displayString Default String
     * @param icon          Resource Location for an icon
     */
    public ToggleButton(int xPos, int yPos, int width, String displayString, ResourceLocation icon) {
        this(xPos, yPos, width, 20, displayString, icon);
    }

    /**
     * Creates an new ToggleButton
     *
     * @param xPos          X positon
     * @param yPos          Y Position
     * @param width         Button Width
     * @param displayString Default String
     * @param height        Button Height
     */
    public ToggleButton(int xPos, int yPos, int width, int height, String displayString) {
        this(xPos, yPos, width, height, displayString, null);
    }

    /**
     * Creates an new ToggleButton
     *
     * @param xPos          X positon
     * @param yPos          Y Position
     * @param width         Button Width
     * @param displayString Default String
     * @param icon          Resource Location for an icon
     * @param height        Button Height
     */
    public ToggleButton(int xPos, int yPos, int width, int height, String displayString, ResourceLocation icon) {
        super(xPos, yPos, width, height, displayString, icon);
    }

    /**
     * Creates an new ToggleButton
     *
     * @param xPos X positon
     * @param yPos Y Position
     * @param icon Resource Location for an icon
     */
    public ToggleButton(int xPos, int yPos, String string, ResourceLocation icon) {
        this(xPos, yPos, 100, string, icon);
    }

    /**
     * Creates an new ToggleButton<br>
     * DrawType defaults to {@linkplain DrawType#STRING_OR_ICON}
     *
     * @param xPos    X positon
     * @param yPos    Y Position
     * @param onIcon  Icon image displayed for ON / TRUE
     * @param offIcon Icon image displayed for OFF / FALSE
     */
    public ToggleButton(int xPos, int yPos, ResourceLocation onIcon, ResourceLocation offIcon) {
        this(xPos, yPos, 20, "", onIcon);
        this.offIcon = offIcon;
        this.drawType = DrawType.STRING_OR_ICON;
    }

    @Override
    public void render(int mouseX, int mouseY, float partial) {
        int color = 14737632;
        this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < this.getX() + this.width && mouseY < getY() + this.height;
        if (packedFGColor != 0) {
            color = packedFGColor;
        } else if (!this.enabled) {
            color = 10526880;
        } else if (this.hovered) {
            color = 16777120;
        }
        mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(getX(), getY(), 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(getX() + this.width / 2, getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(mc, mouseX, mouseY);
        int j = getFGColor();
        int bx = this.getX();
        int mwidth = this.width;

        if (BUTTON_ICON != null) {
            Minecraft.getInstance().getTextureManager().bindTexture((this.offIcon != null && (this.drawType == DrawType.STRING_OR_ICON || this.drawType == DrawType.BOTH) ? (this.value ? BUTTON_ICON : offIcon) : BUTTON_ICON));
            blit(bx + 2, getY() + 2, 0, 0, 16, 16, 16, 16);

            // ! MODIFY X !
            bx += 2 + 16;
            mwidth -= 16;
        }
        String buttonText = this.displayString;
        int strWidth = mc.fontRenderer.getStringWidth(buttonText);
        int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
        if (strWidth > mwidth - 6 && strWidth > ellipsisWidth)
            buttonText = mc.fontRenderer.trimStringToWidth(buttonText, mwidth - 6 - ellipsisWidth).trim() + "...";
        this.drawCenteredString(mc.fontRenderer, buttonText + (((drawType == DrawType.STRING_OR_ICON || drawType == DrawType.BOTH) && this.offIcon == null) ? (this.value ? "ON" : "OFF") : ""), bx + mwidth / 2, this.getY() + (this.height - 8) / 2, color);

        if (this.drawType == DrawType.COLORED_LINE || this.drawType == DrawType.BOTH) {
            int col = value ? Color.GREEN.getRGB() : Color.red.getRGB();
            GuiUtils.drawGradientRect(getBlitOffset(), this.getX() + 6, this.getY() + height - 3, this.getX() + this.width - 6, this.getY() + height - 4, col, col);

        }

    }

    /**
     * Sets the DrawType of the button
     */
    public void setDrawType(DrawType type) {
        this.drawType = type;
    }

    /**
     * Gets the button value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Sets the button value
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void mouseClick(double mouseX, double mouseY, int mouseButton) {
        if (this.visible && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height) {
            playPressSound();
            this.value = !this.value;
            onClick();
        }
    }

    public enum DrawType {
        /**
         * Draws An colored line below the string
         */
        COLORED_LINE,
        /**
         * Draws an specified string after the display string
         * Or toggles the icons
         */
        STRING_OR_ICON,
        /**
         * Displays an colored line AND toggles the string/icon
         */
        BOTH;

        /**
         * Only used for the ExampleGUI
         */
        public String getName() {
            switch (this) {
                case COLORED_LINE:
                    return "Colored Line";
                case STRING_OR_ICON:
                    return "String or Icon";
                case BOTH:
                    return "All";
            }
            return name();
        }
    }
}
