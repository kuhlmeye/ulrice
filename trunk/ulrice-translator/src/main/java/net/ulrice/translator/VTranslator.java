package net.ulrice.translator;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;

public class VTranslator extends JPanel {	
	
	private static final long serialVersionUID = 76684780973728542L;
	
	private UTableViewAdapter dictionaryVA;
	private UTableViewAdapter usagesVA;
	private UTableViewAdapter translationsVA;
	
	public void initialize(CTranslator Controller) {
		
		dictionaryVA = new UTableViewAdapter(new UTableComponent(0));
		usagesVA = new UTableViewAdapter(new UTableComponent(0));
		translationsVA = new UTableViewAdapter(new UTableComponent(0));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Usage", usagesVA.getComponent());
		tabbedPane.addTab("Translations", translationsVA.getComponent());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(dictionaryVA.getComponent());
		add(tabbedPane);
	}

	public JComponent getView() {
		return this;
	}

	public UTableViewAdapter getDictionaryVA() {
		return dictionaryVA;
	}
	
	public UTableViewAdapter getUsagesVA() {
		return usagesVA;
	}
	
	public UTableViewAdapter getTranslationsVA() {
		return translationsVA;
	}	
}
