package net.ulrice.options.modules.hotkey;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.ulrice.Ulrice;
import net.ulrice.message.Translation;
import net.ulrice.message.TranslationUsage;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.options.modules.IFOptionModule;

/**
 * 
 * @author DL10KUH
 */
public class HotkeyOptionModule implements IFOptionModule {

	private DefaultListModel listModel;
	private JList list;

	private JPanel view;
	
	private Set<String> usedFunctionKey = new HashSet<String>();

	@Override
	public String getName() {
		Translation translation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Title, "ModuleHotkeys");
		return translation.isAvailable() ? translation.getText() : translation.getKey();
	}

	@Override
	public JComponent getView() {
		return view;
	}

	@Override
	public void onInitialize() {
		listModel = new DefaultListModel();
		list = new JList(listModel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(new JButton(new AbstractAction("+") {

			private static final long serialVersionUID = 26800549246253363L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HotkeyModule hotkeyModule = HotkeyModuleAssignmentDialog.getAssignment(usedFunctionKey);
				if(hotkeyModule != null) {
					listModel.addElement(hotkeyModule);
					usedFunctionKey.add(hotkeyModule.getFunctionKey());
				}
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("-") {

			private static final long serialVersionUID = -7479493407914328299L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int selIdx = list.getSelectedIndex();
				if (selIdx >= 0) {
					HotkeyModule hotkeyModule = (HotkeyModule) listModel.getElementAt(selIdx);
					Ulrice.getAppPrefs().removeConfiguration(Ulrice.getModuleManager(), hotkeyModule.getFunctionKey());
					listModel.removeElementAt(selIdx);
					usedFunctionKey.remove(hotkeyModule.getFunctionKey());
				}
			}
		}));

		loadKeyAndAddToList("F1");
		loadKeyAndAddToList("F2");
		loadKeyAndAddToList("F3");
		loadKeyAndAddToList("F4");
		loadKeyAndAddToList("F5");
		loadKeyAndAddToList("F6");
		loadKeyAndAddToList("F7");
		loadKeyAndAddToList("F8");
		loadKeyAndAddToList("F9");
		loadKeyAndAddToList("F10");
		loadKeyAndAddToList("F11");
		loadKeyAndAddToList("F12");

		view = new JPanel(new BorderLayout());
		view.add(new JScrollPane(list), BorderLayout.CENTER);
		view.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void loadKeyAndAddToList(String key) {
		String moduleId = Ulrice.getAppPrefs().getConfiguration(Ulrice.getModuleManager(), key, null);
		if(moduleId != null) {
			String moduleTitle = Ulrice.getModuleManager().getModuleTitle(moduleId, Usage.Default);
			HotkeyModule hotkeyModule = new HotkeyModule(moduleId, moduleTitle, key);
			listModel.addElement(hotkeyModule);
			usedFunctionKey.add(key);
		}
	}

	@Override
	public void onShow() {
	}

	@Override
	public void onHide() {
	}

	@Override
	public void onSave() {
		Ulrice.getModuleManager().clearHotkeys();
		for(int i = 0; i < listModel.getSize(); i++) {
			HotkeyModule hotkeyModule = (HotkeyModule) listModel.get(i);
			Ulrice.getModuleManager().registerHotkey(KeyStroke.getKeyStroke("ctrl " + hotkeyModule.getFunctionKey()), hotkeyModule.getModuleId());
			Ulrice.getAppPrefs().putConfiguration(Ulrice.getModuleManager(), hotkeyModule.getFunctionKey(), hotkeyModule.getModuleId());
		}
	}
}
