package net.ulrice.sample.module.databinding.radiobutton;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.ulrice.databinding.bufferedbinding.IFBindingGroupEventListener;
import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.viewadapter.impl.JRadioButtonViewAdapter;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.sample.SingleObjectModel;

public class CRadioButtonSample extends AbstractController implements IFBindingGroupEventListener {
	
	ColorSet colorSet;
	
	SingleObjectModel<ColorSet> model = new SingleObjectModel<ColorSet>(colorSet, ColorSet.class);
	
	JPanel main = new JPanel();
	BindingGroup bg = new BindingGroup();

	@Override
	public JComponent getView() {
		return main;
	}

	@Override
	public void postCreate() {
		JRadioButton redButton = new JRadioButton();
		JRadioButton greenButton = new JRadioButton();
		JRadioButton blueButton = new JRadioButton();
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(redButton);
		buttonGroup.add(greenButton);
		buttonGroup.add(blueButton);
		
		JLabel redLabel = new JLabel("Red");
		JLabel greenLabel = new JLabel("Green");
		JLabel blueLabel = new JLabel("Blue");
		
		main.add(redLabel);
		main.add(redButton);
		main.add(greenLabel);
		main.add(greenButton);
		main.add(blueLabel);
		main.add(blueButton);
		
		JRadioButtonViewAdapter redAdapter = new JRadioButtonViewAdapter(redButton, Color.red);
		JRadioButtonViewAdapter greenAdapter = new JRadioButtonViewAdapter(greenButton, Color.green);
		JRadioButtonViewAdapter blueAdapter = new JRadioButtonViewAdapter(blueButton, Color.blue);
		
//		Map<JRadioButton, Color> buttonMap = new HashMap<JRadioButton, Color>();
//		buttonMap.put(redButton, Color.red);
//		buttonMap.put(greenButton, Color.green);
//		buttonMap.put(blueButton, Color.blue);
//		
//		JRadioButtonViewAdapter radioAdapter = new JRadioButtonViewAdapter<Color>(buttonMap);
//		bg.bind(model.getAttributeModel("c"), radioAdapter);
		
		bg.bind(model.getAttributeModel("c"), redAdapter);
		bg.bind(model.getAttributeModel("c"), greenAdapter);
		bg.bind(model.getAttributeModel("c"), blueAdapter);
		
		bg.addBindingGroupChangeListener(this);
		
		colorSet = new ColorSet();
		colorSet.setC(Color.blue);
		model.setData(colorSet);
		bg.read();
	}

	@Override
	public void onClose(IFClosing closing) {
		closing.doClose();
	}

	@Override
	public void bindingGroupChanged() {
		System.out.println("Color was set to : " + model.getData().getC());
	}

}
