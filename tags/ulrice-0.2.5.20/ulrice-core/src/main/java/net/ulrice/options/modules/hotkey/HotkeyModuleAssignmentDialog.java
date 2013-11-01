package net.ulrice.options.modules.hotkey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import net.ulrice.Ulrice;
import net.ulrice.frame.impl.navigation.ModuleTreeCellRenderer;
import net.ulrice.frame.impl.navigation.ModuleTreeNode;
import net.ulrice.frame.impl.navigation.ModuleTreeNode.NodeType;
import net.ulrice.message.Translation;
import net.ulrice.message.TranslationUsage;
import net.ulrice.module.IFModuleTitleProvider.Usage;

public class HotkeyModuleAssignmentDialog extends JDialog {

	private static final long serialVersionUID = -4947894608084504425L;


	private HotkeyButton selectedButton = null;
	private HotkeyAssignmentModuleTreeModel model = new HotkeyAssignmentModuleTreeModel();
	private JTree moduleTree = new JTree(model);
	
	public HotkeyModuleAssignmentDialog(Set<String> usedFunctionKeys) {
		super(Ulrice.getMainFrame().getFrame());
				
		moduleTree.setCellRenderer(new ModuleTreeCellRenderer());
		
		final ButtonGroup bg = new ButtonGroup();

		setTitle(Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Title, "AssignModuleHotkey").getText());
		
		JPanel keyPanel = new JPanel();
		keyPanel.setLayout(new GridLayout(2, 6));
		createAndAddHotkeyButton("F1", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F2", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F3", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F4", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F5", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F6", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F7", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F8", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F9", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F10", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F11", keyPanel, usedFunctionKeys, bg);
		createAndAddHotkeyButton("F12", keyPanel, usedFunctionKeys, bg);
		
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		Translation okTranslation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Button, "OK");
		buttonPane.add(new JButton(new AbstractAction(okTranslation.isAvailable() ? okTranslation.getText() : okTranslation.getKey()) {

			private static final long serialVersionUID = 5799878044832571736L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		}));
		Translation cancelTranslation = Ulrice.getTranslationProvider().getUlriceTranslation(TranslationUsage.Button, "Cancel");
		buttonPane.add(new JButton(new AbstractAction(cancelTranslation.isAvailable() ? cancelTranslation.getText() : cancelTranslation.getKey()) {

			private static final long serialVersionUID = -8505996574014291447L;

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton = null;
				dispose();
			}
			
		}));
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(keyPanel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(moduleTree), BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		setSize(400, 550);
		setLocationRelativeTo(getParent());
	}

	private HotkeyButton createAndAddHotkeyButton(String buttonName, JPanel keyPanel, Set<String> usedFunctionKeys, final ButtonGroup bg) {
		HotkeyButton hotkeyButton = new HotkeyButton(buttonName, bg, usedFunctionKeys.contains(buttonName));
		keyPanel.add(hotkeyButton);
		hotkeyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				HotkeyButton button = (HotkeyButton) e.getSource();
				if(button.isSelected() && !button.isAlreadyInUse()) {
					selectedButton = button;
				} else {
					selectedButton = null;
				}
			}
		});
		return hotkeyButton;
	}
	
	public static HotkeyModule getAssignment(Set<String> usedFunctionKeys) {

		HotkeyModuleAssignmentDialog dlg = new HotkeyModuleAssignmentDialog(usedFunctionKeys);
		dlg.setModal(true);
		dlg.setVisible(true);

		if(dlg.moduleTree.getSelectionCount() > 0 && dlg.selectedButton != null) {
			Object selObject = dlg.moduleTree.getSelectionPath().getLastPathComponent();
			if(selObject instanceof ModuleTreeNode) {
				ModuleTreeNode module = (ModuleTreeNode) selObject;
				if(module.getNodeType().equals(NodeType.Module)) {
					return new HotkeyModule(module.getModule().getUniqueId(), module.getModule().getModuleTitle(Usage.Default), dlg.selectedButton.getText());
				}
			}
		}
		return null;
	}

}
