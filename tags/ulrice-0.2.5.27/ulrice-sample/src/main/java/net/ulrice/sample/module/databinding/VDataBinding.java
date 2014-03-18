/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.ulrice.databinding.viewadapter.impl.BackgroundStateMarker;
import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;

/**
 * @author christof
 */
public class VDataBinding extends JPanel {

    
	private static final long serialVersionUID = -3696333575593895827L;

    private final JTextComponentViewAdapter textFieldGA1;
    private final JTextComponentViewAdapter textFieldGA2;
    
    private final UTableComponent table = new UTableComponent(1);

    public VDataBinding() {
        final JTextField tf1 = new JTextField();
        final JTextField tf2 = new JTextField();
        
        table.setCellStateMarker(new BackgroundStateMarker());
        
        textFieldGA1 = new JTextComponentViewAdapter(tf1, null);
        BorderStateMarker borderStateMarker = new BorderStateMarker(textFieldGA1.getComponent().getBorder() != null, false, false);
        textFieldGA1.setStateMarker(borderStateMarker);        
        textFieldGA1.setTooltipHandler(new DetailedTooltipHandler());
        
        tf1.setBorder(borderStateMarker);
        textFieldGA2 = new JTextComponentViewAdapter(tf2, null);

        setLayout(new BorderLayout());
        add(textFieldGA1.getComponent(), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(textFieldGA2.getComponent(), BorderLayout.SOUTH);
    }

    /**
     * @see net.ulrice.module.IFView#getView()
     */
    public JComponent getView() {
        return this;
    }

    /**
     * @return the textFieldGA1
     */
    public JTextComponentViewAdapter getTextFieldGA1() {
        return textFieldGA1;
    }

    /**
     * @return the textFieldGA2
     */
    public JTextComponentViewAdapter getTextFieldGA2() {
        return textFieldGA2;
    }
    
    public UTableComponent getTable() {
    	return table;
    }
}
