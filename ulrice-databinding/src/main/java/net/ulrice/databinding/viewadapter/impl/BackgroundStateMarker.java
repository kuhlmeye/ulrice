package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;

import javax.swing.JComponent;

import net.ulrice.databinding.bufferedbinding.impl.ColumnColorOverride;
import net.ulrice.databinding.bufferedbinding.impl.Element;
import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.IFCellStateMarker;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.util.Colors;

public class BackgroundStateMarker implements IFStateMarker, IFCellStateMarker {

    private static final Color NORMAL_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_NORMAL_EVEN, new Color(230, 230, 230));
    private static final Color INVALID_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_INVALID, new Color(200, 130, 130));
    private static final Color CHANGED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_CHANGED, new Color(186, 207, 226));
    private static final Color SELECTED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_SELECTED, new Color(200, 200, 255));

    private Color normalFGColor;
    private Color normalBGColor;

    @Override
    public void updateState(Object value, boolean editable, boolean dirty, boolean valid, JComponent component) {
        if (!valid) {
            component.setBackground(INVALID_BG_COLOR);
        }
        else {
            if (dirty) {
                component.setBackground(CHANGED_BG_COLOR);
            }
            else {
                component.setBackground(normalBGColor);
            }
        }
    }

    @Override
    public void updateState(Element value, int row, boolean selected, boolean editable, boolean dirty, boolean valid, ColumnColorOverride columnColorOverride, JComponent component) {
        Color foreground = normalFGColor;
        Color background = columnColorOverride == null ? NORMAL_BG_COLOR : columnColorOverride.getColor();
        
        if (value.isRemoved()) {
            foreground = Colors.blend(foreground, Color.WHITE, 0.25);
        }
        else {
            if (!editable) {
                background = Colors.blend(background, Color.BLACK, 0.85);
            }

            if (!valid) {
                background = Colors.blend(background, INVALID_BG_COLOR, 0.85);
            }
            else if (dirty) {
                background = Colors.blend(background, CHANGED_BG_COLOR, 0.85);
            }
        }

        if ((row % 2) == 1) {
            background = Colors.blend(background, Color.BLACK, 0.95);
        }

        if (selected) {
            background = Colors.blend(background, SELECTED_BG_COLOR, 0.5);
        }

        component.setForeground(foreground);
        component.setBackground(background);
    }

    @Override
    public void initialize(JComponent component) {
        if (this.normalFGColor == null) {
            this.normalFGColor = component.getForeground();
        }
        if (this.normalBGColor == null) {
            this.normalBGColor = component.getBackground();
        }
    }

}
