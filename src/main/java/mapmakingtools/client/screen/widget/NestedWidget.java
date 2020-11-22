package mapmakingtools.client.screen.widget;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.Widget;

import javax.annotation.Nullable;
import java.util.List;

public class NestedWidget extends Widget implements INestedGuiEventHandler {

    protected List<Widget> children = Lists.newArrayList();
    @Nullable
    private IGuiEventListener focused;
    // Was the current focused element clicked on
    private boolean isDragging;

    public NestedWidget(int xIn, int yIn, int widthIn, int heightIn, String msg) {
        super(xIn, yIn, widthIn, heightIn, msg);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return this.children;
    }

    @Override
    public final boolean isDragging() {
       return this.isDragging;
    }

    @Override
    public final void setDragging(boolean dragging) {
       this.isDragging = dragging;
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused() {
       return this.focused;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener focused) {
       this.focused = focused;
    }

 // Methods for a Widget that is also a INestedGuiEventHandler

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (INestedGuiEventHandler.super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        } else if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        return false;
     }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double changeX, double changeY) {
        if (INestedGuiEventHandler.super.mouseDragged(mouseX, mouseY, mouseButton, changeX, changeY)) {
            return true;
        } else if (super.mouseDragged(mouseX, mouseY, mouseButton, changeX, changeY)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (INestedGuiEventHandler.super.mouseReleased(mouseX, mouseY, mouseButton)) {
            return true;
        } else if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double change) {
        if (INestedGuiEventHandler.super.mouseScrolled(mouseX, mouseY, change)) {
            return true;
        } else if (super.mouseScrolled(mouseX, mouseY, change)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (INestedGuiEventHandler.super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (INestedGuiEventHandler.super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (INestedGuiEventHandler.super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            return true;
        } else if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean changeFocus(boolean shiftNotDown) {
        if (INestedGuiEventHandler.super.changeFocus(shiftNotDown)) {
            return true;
        } else if (super.changeFocus(shiftNotDown)) {
            return true;
        }

        return false;
    }
}