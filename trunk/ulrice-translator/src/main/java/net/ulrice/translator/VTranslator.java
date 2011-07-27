package net.ulrice.translator;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.IFView;

public class VTranslator extends JPanel implements IFView<CTranslator> {	
	
	private static final long serialVersionUID = 76684780973728542L;
	
	private UTableViewAdapter dictionaryVA;
	private UTableViewAdapter usagesVA;
	private UTableViewAdapter translationsVA;
	
	@Override
	public void initialize(CTranslator Controller) {
		
		dictionaryVA = new UTableViewAdapter();
		usagesVA = new UTableViewAdapter();
		translationsVA = new UTableViewAdapter();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.addTab("Usage", usagesVA.getComponent());
		tabbedPane.addTab("Translations", translationsVA.getComponent());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(dictionaryVA.getComponent());
		add(tabbedPane);
	}

	@Override
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
