/**
 * 
 */
package net.ulrice.frame.impl.statusbar;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.ulrice.ui.UI;

/**
 * @author christof
 *
 */
public class StatusbarClockRenderer implements IFStatusbarClockRenderer {

	private DateFormat dateDF;
	private DateFormat timeDF;
	
	JPanel clockPanel;
	JLabel dateLabel;
	JLabel timeLabel;

	public StatusbarClockRenderer() {
		dateDF = DateFormat.getDateInstance(DateFormat.SHORT);
		timeDF = DateFormat.getTimeInstance(DateFormat.SHORT);
		
		dateLabel = new JLabel();
		dateLabel.setFont(UIManager.getFont(UI.STATUSBAR_CLOCKRENDERER_DATE_FONT));
		timeLabel = new JLabel();
		timeLabel.setFont(UIManager.getFont(UI.STATUSBAR_CLOCKRENDERER_TIME_FONT));
		
		dateLabel.setHorizontalAlignment(JLabel.CENTER);
		timeLabel.setHorizontalAlignment(JLabel.CENTER);
		
		clockPanel = new JPanel();
		clockPanel.setLayout(new BorderLayout());
		clockPanel.add(timeLabel, BorderLayout.NORTH);
		clockPanel.add(dateLabel, BorderLayout.SOUTH);
	}
	
	/**
	 * @see net.ulrice.frame.impl.statusbar.IFStatusbarClockRenderer#getClockRenderer()
	 */
	@Override
	public JComponent getClockRenderer() {
		
		Date currentDate = new Date();
		
		dateLabel.setText(dateDF.format(currentDate));
		timeLabel.setText(timeDF.format(currentDate));
		
		return clockPanel;
	}

}
