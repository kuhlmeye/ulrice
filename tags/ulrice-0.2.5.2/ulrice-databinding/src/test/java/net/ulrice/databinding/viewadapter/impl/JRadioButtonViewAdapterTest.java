package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import junit.framework.TestCase;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.UlriceReflectionUtils;

public class JRadioButtonViewAdapterTest extends TestCase {
	
	ColorSet colorSet;
	BindingGroup bg;
	GenericAM<Color> colorAM;
	
	JRadioButton redButton;
	JRadioButton greenButton;
	JRadioButton blueButton;
	
	JRadioButtonViewAdapter redAdapter;
	JRadioButtonViewAdapter greenAdapter;
	JRadioButtonViewAdapter blueAdapter;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bg = new BindingGroup();
		colorSet = new ColorSet();
		
		colorSet.setC(Color.blue);
		
		colorAM = new GenericAM<Color>(new ReflectionMVA(ReflectionMVA.createID(this, "colorSet.c"), this,
                "colorSet.c", false, UlriceReflectionUtils.getFieldType(ColorSet.class, "c")), new IFAttributeInfo() {});
		
		redButton = new JRadioButton();
		greenButton = new JRadioButton();
		blueButton = new JRadioButton();
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(redButton);
		buttonGroup.add(greenButton);
		buttonGroup.add(blueButton);
		
		redAdapter = new JRadioButtonViewAdapter(redButton, Color.red);
		greenAdapter = new JRadioButtonViewAdapter(greenButton, Color.green);
		blueAdapter = new JRadioButtonViewAdapter(blueButton, Color.blue);
		
		bg.bind(colorAM, redAdapter);
		bg.bind(colorAM, greenAdapter);
		bg.bind(colorAM, blueAdapter);
		
		bg.read();
		
	}

	public void testJRadioButtonViewAdapter() {
		assertTrue(blueButton.isSelected());
		
		redButton.setSelected(true);
		assertFalse(blueButton.isSelected());
		assertTrue(redButton.isSelected());
		
		bg.write();
		
		assertEquals(redAdapter.getValue(), colorSet.getC());
	}

}

class ColorSet {
	
	private Color c;
	
	public ColorSet() {
		super();
	}
	
	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

}
