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

    private static final Color evenNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_NORMAL_EVEN, new Color(230, 230, 230));
    private static final Color oddNormalBackground = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_NORMAL_ODD, new Color(200, 200, 200));
    private static final Color evenReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_READONLY_EVEN, new Color(200, 230, 200));
    private static final Color oddReadOnlyBackground = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_READONLY_ODD, new Color(170, 200, 170));
    private static final Color selectedBackground = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_SELECTED, new Color(200, 200, 255));

    private static final Color REMOVED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_REMOVED_BG, new Color(150, 150, 150));
    private static final Color REMOVED_FG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_REMOVED_FG, new Color(80, 80, 80));
    private static final Color INVALID_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_INVALID, new Color(200, 130, 130));
    private static final Color CHANGED_BG_COLOR = BindingUI.getColor(BindingUI.BACKGROUND_STATE_MARKER_CHANGED, new Color(186, 207, 226));
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
        if (value.isRemoved() && !selected) {
            component.setForeground(REMOVED_FG_COLOR);
            component.setBackground(REMOVED_BG_COLOR);
        }
        else if (!valid) {
            component.setForeground(normalFGColor);
            component.setBackground(INVALID_BG_COLOR);
        }
        else {
            if (dirty && !value.isRemoved()) {
                component.setForeground(normalFGColor);
                component.setBackground(CHANGED_BG_COLOR);
            }
            else {
                component.setForeground(normalFGColor);
                if (!editable) {
                    if (row % 2 == 0) {
                        component.setBackground(columnColorOverride == null ? evenReadOnlyBackground : columnColorOverride.getEvenReadOnlyColor());
                    }
                    else {
                        component.setBackground(columnColorOverride == null ? oddReadOnlyBackground : columnColorOverride.getOddReadOnlyColor());
                    }
                }
                else {
                    if (row % 2 == 0) {
                        component.setBackground(columnColorOverride == null ? evenNormalBackground : columnColorOverride.getEvenNormalColor());
                    }
                    else {
                        component.setBackground(columnColorOverride == null ? oddNormalBackground : columnColorOverride.getOddNormalColor());
                    }
                }
            }
        }
        if (selected) {
            component.setBackground(makeColorSelected(component.getBackground()));
        }
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

    /**
     * to get a consistend selection colour behavior TODO: description
     */
    public static Color makeColorSelected(Color color) {
        return Colors.blend(color, selectedBackground, 0.5);
    }

   

}
