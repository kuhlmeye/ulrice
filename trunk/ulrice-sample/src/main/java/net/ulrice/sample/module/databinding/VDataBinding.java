/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.ulrice.databinding.viewadapter.impl.BorderStateMarker;
import net.ulrice.databinding.viewadapter.impl.DetailedTooltipHandler;
import net.ulrice.databinding.viewadapter.impl.JTableViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;

/**
 * @author christof
 *
 */
public class VDataBinding extends JPanel {

	private JTextComponentViewAdapter textFieldGA1;
	private JTextComponentViewAdapter textFieldGA2;
    private JTableViewAdapter listGA;

	/**
	 * @see net.ulrice.module.IFView#initialize()
	 */
	public void initialize(CDataBinding controller) {
		JTextField tf1 = new JTextField();
		JTextField tf2 = new JTextField();
		
		textFieldGA1 = new JTextComponentViewAdapter(tf1);
		BorderStateMarker borderStateMarker = new BorderStateMarker();
		textFieldGA1.setStateMarker(borderStateMarker);
		textFieldGA1.setTooltipHandler(new DetailedTooltipHandler());
		tf1.setBorder(borderStateMarker);
		textFieldGA2 = new JTextComponentViewAdapter(tf2);
		
		listGA = new JTableViewAdapter(new JTable());
		
		setLayout(new BorderLayout());
		add(textFieldGA1.getComponent(), BorderLayout.NORTH);
        add(new JScrollPane(listGA.getComponent()), BorderLayout.CENTER);
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

    /**
     * @return the listGA
     */
    public JTableViewAdapter getListGA() {
        return listGA;
    }

}
