package net.ulrice.databinding.viewadapter.impl.factory;

import javax.swing.JComboBox;

import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewAdapterDescriptor;
import net.ulrice.databinding.viewadapter.impl.JComboBoxViewAdapter;


public class JComboBoxAdapterDescriptor implements IFViewAdapterDescriptor {

	@Override
	public boolean canHandle(Object viewElement) {
		return viewElement instanceof JComboBox; //TODO Items sind ObjectWithPresentation
	}

	@Override
	public IFViewAdapter createInstance(Object viewElement) {
		return new JComboBoxViewAdapter ((JComboBox) viewElement);
	}
}
