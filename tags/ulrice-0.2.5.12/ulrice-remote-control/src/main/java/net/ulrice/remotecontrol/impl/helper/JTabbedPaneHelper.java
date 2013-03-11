package net.ulrice.remotecontrol.impl.helper;

import java.awt.Component;
import java.awt.Robot;

import javax.swing.JTabbedPane;

import net.ulrice.remotecontrol.ComponentListData;
import net.ulrice.remotecontrol.RemoteControlException;
import net.ulrice.remotecontrol.util.RegularMatcher;

public class JTabbedPaneHelper extends AbstractJComponentHelper<JTabbedPane> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<JTabbedPane> getType() {
        return JTabbedPane.class;
    }

    private int indexOfTab(JTabbedPane component, String title) {
        
        RegularMatcher matcher = new RegularMatcher(title);
        
        for (int i=0; i<component.getTabCount(); i+=1) {
            
            if (matcher.matches(component.getTitleAt(i))) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#click(java.awt.Robot, java.awt.Component, java.lang.String)
     */
    @Override
    public boolean click(Robot robot, JTabbedPane component, String text) throws RemoteControlException {
        int index = indexOfTab(component, text);
        
        if (index < 0) {
            return false;
        }
        
        return click(robot, component, index);
    }

    /**
     * {@inheritDoc}
     * @see net.ulrice.remotecontrol.impl.helper.AbstractComponentHelper#click(java.awt.Robot, java.awt.Component, int)
     */
    @Override
    public boolean click(Robot robot, JTabbedPane component, int index) throws RemoteControlException {
        if (index >= component.getTabCount()) {
            return false;
        }

        return super.click(robot, component, component.getBoundsAt(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData(JTabbedPane component) throws RemoteControlException {
        ComponentListData result = new ComponentListData();

        for (int i = 0; i < component.getTabCount(); i += 1) {
            Component tabComponentAt = component.getTabComponentAt(i);

            result.addEntry((tabComponentAt != null) ? tabComponentAt.getClass().getName() : Void.class, component.getSelectedIndex() == i);
        }

        return result;
    }

}
