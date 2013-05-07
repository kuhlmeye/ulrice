/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.RegExValidator;
import net.ulrice.databinding.viewadapter.utable.AbstractUTableRenderer;

/**
 * @author christof
 */
public class MDataBinding {

	public String name;
	public List<Person> personList = new ArrayList<Person>();

	private GenericAM<String> nameAM;
	private TableAM tableAM;

	public MDataBinding() {
		IFAttributeInfo attributeInfo = new IFAttributeInfo() {
		};

		nameAM = new GenericAM<String>(new ReflectionMVA(this, "name"), attributeInfo);
		nameAM.addValidator(new RegExValidator<String>("(hallo|hi)", "Validation failed. Only 'hallo' or 'hi' is allowed"));
		name = "hallo";

		tableAM = new TableAM(new IndexedReflectionMVA(this, "personList"), attributeInfo);
		tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "lastName"), String.class));
		tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "firstName"), String.class));
		tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "address"), String.class));
		tableAM.addColumn(new ColumnDefinition<Integer>(new DynamicReflectionMVA(Person.class, "age"), Integer.class));
		
		tableAM.getColumnByIndex(2).setCellRenderer(new AbstractUTableRenderer() {
			
			JLabel label = new JLabel();
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				label.setText("<html>" + value.toString().replace("\n", "<br>") + "<html>");
				int rowHeight = label.getPreferredSize().height;
				table.setRowHeight(row, rowHeight);
				return label; 
			}
			
			
		});
	}

	public GenericAM<String> getNameAM() {
		return nameAM;
	}

	public TableAM getTableAM() {
		return tableAM;
	}
}
