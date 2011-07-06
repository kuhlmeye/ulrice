package net.ulrice.simpledatabinding.viewaccess.heuristic;

import javax.swing.JComboBox;

import net.ulrice.simpledatabinding.viewaccess.ViewAdapter;
import net.ulrice.simpledatabinding.viewaccess.ViewAdapterDescriptor;
import net.ulrice.simpledatabinding.viewaccess.impl.JComboBoxViewAdapter;


public class JComboBoxAdapterDescriptor implements ViewAdapterDescriptor {

	@Override
	public boolean canHandle(Object viewElement) {
		return viewElement instanceof JComboBox; //TODO Items sind ObjectWithPresentation
	}

	@Override
	public ViewAdapter createInstance(Object viewElement) {
		return new JComboBoxViewAdapter ((JComboBox) viewElement);
	}
}
