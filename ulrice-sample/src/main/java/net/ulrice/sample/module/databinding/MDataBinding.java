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
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
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
		tableAM.addColumn(new ColumnDefinition<String>(new IFDynamicModelValueAccessor() {

			@Override
			public Object getValue(Object root) {
				return ((Person)root).getLastName();
			}

			@Override
			public void setValue(Object root, Object value) {				
			}

			@Override
			public String getAttributeId() {
				return "LastName";
			}

			@Override
			public Class<?> getModelType(Class<?> rootType) {
				return String.class;
			}
			
		}, String.class));
		
		tableAM.addColumn(new ColumnDefinition<String>(new IFDynamicModelValueAccessor() {

			@Override
			public Object getValue(Object root) {
				return ((Person)root).getFirstName();
			}

			@Override
			public void setValue(Object root, Object value) {				
			}

			@Override
			public String getAttributeId() {
				return "FirstName";
			}

			@Override
			public Class<?> getModelType(Class<?> rootType) {
				return String.class;
			}
			
		}, String.class));
		tableAM.addColumn(new ColumnDefinition<String>(new IFDynamicModelValueAccessor() {

			@Override
			public Object getValue(Object root) {
				return ((Person)root).getAddress();
			}

			@Override
			public void setValue(Object root, Object value) {				
			}

			@Override
			public String getAttributeId() {
				return "Address";
			}

			@Override
			public Class<?> getModelType(Class<?> rootType) {
				return String.class;
			}
			
		}, String.class));
		tableAM.addColumn(new ColumnDefinition<String>(new IFDynamicModelValueAccessor() {

			@Override
			public Object getValue(Object root) {
				return ((Person)root).getAge();
			}

			@Override
			public void setValue(Object root, Object value) {				
			}

			@Override
			public String getAttributeId() {
				return "Age";
			}

			@Override
			public Class<?> getModelType(Class<?> rootType) {
				return String.class;
			}
			
		}, String.class));
		
//		tableAM.getColumnByIndex(2).setCellRenderer(new AbstractUTableRenderer() {
//			
//			JLabel label = new JLabel();
//			
//			@Override
//			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//
//				label.setText("<html>" + value.toString().replace("\n", "<br>") + "<html>");
//				int rowHeight = label.getPreferredSize().height;
//				table.setRowHeight(row, rowHeight);
//				return label; 
//			}
//			
//			
//		});
	}

	public GenericAM<String> getNameAM() {
		return nameAM;
	}

	public TableAM getTableAM() {
		return tableAM;
	}
}
