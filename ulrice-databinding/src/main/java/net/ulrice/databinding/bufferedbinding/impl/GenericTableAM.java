package net.ulrice.databinding.bufferedbinding.impl;

import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

/**
 * @author christof
 * 
 */
public class GenericTableAM extends AbstractTableAM  {


	private IFIndexedModelValueAccessor tableMVA;
	
	private IFModelValueAccessor numRowsMVA;
	
	public GenericTableAM(IFIndexedModelValueAccessor tableMVA, IFModelValueAccessor numRowsMVA, boolean readOnly) {
		super(tableMVA.getAttributeId(), readOnly);
		this.tableMVA = tableMVA;
		this.numRowsMVA = numRowsMVA;
	}
	
	public GenericTableAM(IFIndexedModelValueAccessor tableMVA, IFModelValueAccessor numRowsMVA) {
		this(tableMVA, numRowsMVA, false);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void read() {
		
		int numRows = (Integer)numRowsMVA.getValue();
		for(int i = 0; i < numRows; i++) {
			Object value = tableMVA.getValue(i);
			Element elem = createElement(value);
			elem.readObject();
			elementIdMap.put(elem.getUniqueId(), elem);				
			elements.add(elem);
		}
		fireUpdateViews();
	}


	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {		
		int numRows = elements.size();
		numRowsMVA.setValue(numRows);
		
		for(int i = 0; i < numRows; i++) {
			Element elem = elements.get(i);
			elem.writeObject();
			tableMVA.setValue(i, elem.getOriginalValue());
		}
	}

}

