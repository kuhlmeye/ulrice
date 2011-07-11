package net.ulrice.databinding.impl.am;

import java.util.List;

import javax.swing.event.TableModelEvent;

import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

/**
 * @author christof
 * 
 */
public class GenericTableAM<T, S> extends AbstractTableAM<T, S>  {


	private IFIndexedModelValueAccessor tableMVA;
	
	private IFModelValueAccessor numRowsMVA;
	
	public GenericTableAM(String id, IFIndexedModelValueAccessor tableMVA, IFModelValueAccessor numRowsMVA, boolean editable) {
		super(id, editable);
		this.tableMVA = tableMVA;
		this.numRowsMVA = numRowsMVA;
	}
	
	public GenericTableAM(String id, IFIndexedModelValueAccessor tableMVA, IFModelValueAccessor numRowsMVA) {
		this(id, tableMVA, numRowsMVA, true);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#read()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void read() {
		
		int numRows = (Integer)numRowsMVA.getValue();
		for(int i = 0; i < numRows; i++) {
			S value = (S)tableMVA.getValue(i);
			Element<S> elem = createElement(value);
			elem.readObject();
			elementIdMap.put(elem.getUniqueId(), elem);				
			elements.add(elem);
		}
		// TODO Refine event.
		fireTableChanged(new TableModelEvent(this));
	}


	/**
	 * @see net.ulrice.databinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {
		
		int numRows = elements.size();
		numRowsMVA.setValue(numRows);
		
		for(int i = 0; i < numRows; i++) {
			Element<S> elem = elements.get(i);
			elem.writeObject();
			tableMVA.setValue(i, elem.getValueObject());
		}
	}
}

