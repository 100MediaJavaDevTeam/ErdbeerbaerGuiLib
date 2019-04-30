package de.erdbeerbaerlp.guilib.components;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class  EnumSlider extends Slider {
	private Runnable action;
	private Enum<?> enumValue;
	private Enum<?>[] enumValues;
	
	private int prevIndex;
	public <T extends Enum<T>> EnumSlider(int xPos, int yPos, String displayStr, Class<T> enumClass, T currentVal, Runnable changeAction) {
		this(xPos, yPos, 150, 20, displayStr, "", enumClass, currentVal, true, changeAction);
	}
	public <T extends Enum<T>> EnumSlider(int xPos, int yPos, int width, int height, String prefix, String suf, Class<T> enumClass, T currentVal, boolean drawStr) {
		this(xPos, yPos, width, height, prefix, suf, enumClass, currentVal, drawStr, null);
	}
	public <T extends Enum<T>> EnumSlider(int xPos, int yPos, int width, int height, String prefix, String suf, Class<T> enumClass, T currentVal, boolean drawStr, Runnable changeAction) {
		super(xPos, yPos, width, height, prefix, suf, -1, -1, -1, false, drawStr, null);
		
		this.action = changeAction;
		this.enumValue = currentVal;
		this.enumValues = enumClass.getEnumConstants();
		this.maxValue = enumValues.length;
		this.showDecimal = false;
		this.minValue = 0;
		this.sliderValue = (getCurrentIndex() - minValue) / (maxValue - minValue);

        displayString = dispString + this.enumValue.name() + suffix;

        drawString = drawStr;
        if(!drawString)
        {
            displayString = "";
        }
		
	}
	public Enum<?> getEnum() {
		return enumValue;
	}
	@Override
	public void updateSlider() {
        if (this.sliderValue < 0.0F)
        {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F)
        {
            this.sliderValue = 1.0F;
        }

        String val = this.enumValue.name();

        if(drawString)
        {
            displayString = dispString + val + suffix;
        }
        if(prevIndex != getCurrentIndex()) this.onValueChanged();
	}
	public void onValueChanged() {
		if(this.action != null) action.run();
	}
	@Override
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
		this.prevIndex = getCurrentIndex();
        if (this.visible)
        {
            if (this.dragging)
            {
            	final int index = getCurrentIndex();
            	this.sliderValue = (float)(par2 - (this.x + 4)) / (float)(this.width - 8);
                int sliderValue = (int)Math.round(this.sliderValue * (maxValue - minValue) + this.minValue);
                if(sliderValue < 0) sliderValue=0;
                if(index == -1) {
    				this.enumValue = enumValues[0];
    			}else {
    				if(sliderValue < this.enumValues.length) {
    					this.enumValue = enumValues[sliderValue];
    				}
    			}
                updateSlider();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		if (superMousePressed(par2, par3))
        {
			final int index = getCurrentIndex();
			
			this.sliderValue = (float)(par2 - (this.x + 4)) / (float)(this.width - 8);
            int sliderValue = (int)Math.round(this.sliderValue * (maxValue - minValue) + this.minValue);
            if(sliderValue <0) return false;
            if(index == -1) {
				this.enumValue = enumValues[0];
			}else {
				if(sliderValue < this.enumValues.length) {
					this.enumValue = enumValues[sliderValue];
				}
			}
            updateSlider();
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
	}
	private int getCurrentIndex() {
		int out = 0;
		for(Enum<?> e : this.enumValues) {
			if(e.name() == this.enumValue.name()) return out;
			out++;
		}
		return -1;
	}
	public boolean superMousePressed(int mouseX, int mouseY)
    {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
	public void mouseClick(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.prevIndex = getCurrentIndex();
		this.mousePressed(mc, mouseX, mouseY);
	}
}