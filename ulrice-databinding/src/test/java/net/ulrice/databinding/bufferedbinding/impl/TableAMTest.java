package net.ulrice.databinding.bufferedbinding.impl;


import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the list attribute model. 
 * @author christof
 */
public class TableAMTest {

	private TableAM tableAM;
	
	public List<Person> list;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    //TODO: Ulrice configuration needed!!!
		tableAM = new TableAM(new IndexedReflectionMVA(this, "list"), null);
		tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "name"), String.class, ColumnType.NewEditable));
		tableAM.addColumn(new ColumnDefinition<Integer>(new DynamicReflectionMVA(Person.class, "age"), Integer.class));
		
		tableAM.setUniqueConstraint("Person.name");
		
		list = new LinkedList<Person>();

		Person a = new Person();
		a.name = "Max Mustermann";
		a.age = 18;
		
		Person b = new Person();
		b.name = "Petra Musterfrau";
		b.age = 20;
		
		list.clear();
		list.add(a);
		list.add(b);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests, if data could be read into the data-model.
	 */
	@Test
	public void read() {
		tableAM.read();				
		
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
    	
		assertEquals(2, tableAM.getRowCount());
		assertEquals("Max Mustermann", ((Person)tableAM.getElementAt(0).writeObject()).name);
		assertEquals("Petra Musterfrau", ((Person)tableAM.getElementAt(1).writeObject()).name);
	}
	

    
    @Test
    public void directReadTestAndClear() {
    	tableAM.read(list, false);
    	
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());

		assertEquals(2, tableAM.getRowCount());
		assertEquals("Max Mustermann", ((Person)tableAM.getElementAt(0).writeObject()).name);
		assertEquals("Petra Musterfrau", ((Person)tableAM.getElementAt(1).writeObject()).name);
		

    	tableAM.read(list, true);

		assertEquals(4, tableAM.getRowCount());
		assertEquals("Max Mustermann", ((Person)tableAM.getElementAt(2).writeObject()).name);
		assertEquals("Petra Musterfrau", ((Person)tableAM.getElementAt(3).writeObject()).name);
		
		tableAM.clear();
		assertEquals(0, tableAM.getRowCount());

    	Assert.assertEquals(false, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
    }
    
    @Test
    public void incrementalReadTest() {
        tableAM.read(list, false);
        
        Assert.assertEquals(true, tableAM.isInitialized());
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(true, tableAM.isValid());

        assertEquals(2, tableAM.getRowCount());
        assertEquals("Max Mustermann", ((Person)tableAM.getElementAt(0).writeObject()).name);
        assertEquals("Petra Musterfrau", ((Person)tableAM.getElementAt(1).writeObject()).name);

        List<Person> list2 = new LinkedList<Person>();

        Person c = new Person();
        c.name = "Max Mustermann2";
        c.age = 19;
        
        Person d = new Person();
        d.name = "Petra Musterfrau2";
        d.age = 21;
        
        list2.add(c);
        list2.add(d);
        
        list.addAll(list2);

        tableAM.read(list2, true);

        assertEquals(4, tableAM.getRowCount());
        assertEquals("Max Mustermann2", ((Person)tableAM.getElementAt(2).writeObject()).name);
        assertEquals("Petra Musterfrau2", ((Person)tableAM.getElementAt(3).writeObject()).name);
        
        list.remove(3);
        list.remove(2);
    }
	
	/**
	 * Tests, if the values are read into the internal element objects.
	 */
	@Test
	public void readCellValues() {
    	Assert.assertEquals(false, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
		
		tableAM.read();
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
    	
		assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt(0));
		assertEquals(18, tableAM.getElementAt(0).getValueAt(1));
		assertEquals("Petra Musterfrau", tableAM.getElementAt(1).getValueAt(0));
		assertEquals(20, tableAM.getElementAt(1).getValueAt(1));
	}
	
	/**
	 * Tests, if the data state mechanism of an element works.
	 */
	@Test
	public void updateCellValues() {
		tableAM.read();
        tableAM.getElementAt(0).setValueAt(0, "Otto Normal");
        assertEquals("Otto Normal", tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(true, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(true, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
    	
		Assert.assertEquals(0, tableAM.getCreatedObjects().size());
		Assert.assertEquals(1, tableAM.getModifiedObjects().size());
		Assert.assertEquals(0, tableAM.getDeletedObjects().size());	

        // Change back.
        tableAM.getElementAt(0).setValueAt(0, "Max Mustermann");
        assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(false, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
        
        tableAM.getElementAt(0).setValueAt("Person.name", "Otto Normal");
        assertEquals("Otto Normal", tableAM.getElementAt(0).getValueAt("Person.name"));
    	Assert.assertEquals(true, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(true, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
        
        // Change back.
        tableAM.getElementAt(0).setValueAt("Person.name", "Max Mustermann");
        assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt("Person.name"));
    	Assert.assertEquals(false, tableAM.getElementAt(0).isDirty());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.isInitialized());
    	Assert.assertEquals(false, tableAM.isDirty());
    	Assert.assertEquals(true, tableAM.isValid());
	}

	/**
	 * Tests writing of changed element values-
	 */
	@Test
	public void writeChangedValues() {
	    tableAM.read();
        tableAM.getElementAt(0).setValueAt(0, "Otto Normal");
	    tableAM.write();
	    assertEquals("Otto Normal", list.get(0).name);
	}
	
	@Test
	public void getElementById() {
		tableAM.read();
		Long uniqueId = tableAM.getElementAt(0).getUniqueId();
		Assert.assertEquals(tableAM.getElementAt(0), tableAM.getElementById(uniqueId));
	}

    @Test
    public void addRow() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        tableAM.addElement(null);
        Assert.assertEquals(3, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        Assert.assertEquals(null, tableAM.getElementAt(2).getValueAt(0));
        Assert.assertTrue(tableAM.getElementAt(2).isDirty());
        Assert.assertEquals(0, tableAM.getElementAt(2).getValueAt(1));
        
        Assert.assertEquals(1, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());             
    }
    
    @Test
    public void addRowAndModify() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        Element element = tableAM.addElement(null);
        Assert.assertEquals(3, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        Assert.assertEquals(null, tableAM.getElementAt(2).getValueAt(0));
        Assert.assertTrue(tableAM.getElementAt(2).isDirty());
        Assert.assertEquals(0, tableAM.getElementAt(2).getValueAt(1));
        
        element.setValueAt(0, "Test");
        
        Assert.assertEquals(1, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());             
    }
    
    @Test
    public void addRowAndModifyAndAddTwice() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        Element element = tableAM.addElement(null);
        Assert.assertEquals(3, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
                
        element.setValueAt(0, "Test");
        element = tableAM.addElement(null);
        element = tableAM.addElement(null);
    }
    
       
    @Test
    /**
     * bug:3137
     */
    public void uniqueErrorLostOnNonKeyChangeOften() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isValid());
        
        String max = (String) tableAM.getValueAt(0, 0);
        String petra = (String) tableAM.getValueAt(1, 0);
        tableAM.getElementAt(1).setValueAt(0, max);
        

        assertFalse("unique key error petra has the name of max", tableAM.isValid());
        
        tableAM.getElementAt(0).setValueAt(1, 20); 
//        tableAM.getElementAt(1).setValueAt(1, 20);
        
        assertFalse("changed values not in the constraint, tableAm must still be invalid", tableAM.isValid());
    
        tableAM.getElementAt(1).setValueAt(0, petra);
        assertTrue("differnt names, unique again", tableAM.isValid());
        
    }
    
    @Test
    /**
     * bug:3137
     */
    public void uniqueErrorLost() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isValid());
        
        String max = (String) tableAM.getValueAt(0, 0);
        tableAM.getElementAt(1).setValueAt(0, max);
       
        assertFalse("unique key error petra has the name of max", tableAM.isValid());
        
        Element elem = tableAM.getElementAt(0);
        assertFalse("element has unique key error", elem.isValid());
        elem.setCurrentValue(elem.getCurrentValue()); //wert neu setzen     
        assertFalse("elment still not unique", elem.isValid());
        Person p = (Person) elem.getCurrentValue();
        p.name = "NIX";
        elem.setCurrentValue(p);
        assertTrue("elment is now valid", elem.isValid());
    }
    

    

    
    @Test
    public void modifyRowAndDelete() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        tableAM.getElementAt(0).setValueAt(0, "Test");
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        
        Assert.assertEquals(0, tableAM.getCreatedObjects().size());
        Assert.assertEquals(1, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());    
        
        tableAM.delElement(tableAM.getElementAt(0));
        
        Assert.assertEquals(0, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(1, tableAM.getDeletedObjects().size());      
    }
    
    @Test
    public void addRowAndRemove() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        Element element = tableAM.addElement(null);
        Assert.assertEquals(3, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        Assert.assertEquals(null, tableAM.getElementAt(2).getValueAt(0));
        Assert.assertTrue(tableAM.getElementAt(2).isDirty());
        Assert.assertEquals(0, tableAM.getElementAt(2).getValueAt(1));
                        
        tableAM.delElement(element);
                
        Assert.assertEquals(0, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());             
    }
    
    @Test
    public void addAddRowAndAdd() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());

        Element elementA = tableAM.addElement(null);
        Element elementB = tableAM.addElement(null);
        tableAM.delElement(elementA);
        Element elementC = tableAM.addElement(null);
                
        Assert.assertEquals(2, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());             
    }
    
    
    
    @Test
    public void addRowAndModifyAndRemove() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        Element element = tableAM.addElement(null);
        Assert.assertEquals(3, tableAM.getRowCount());
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        Assert.assertEquals(null, tableAM.getElementAt(2).getValueAt(0));
        Assert.assertTrue(tableAM.getElementAt(2).isDirty());
        Assert.assertEquals(0, tableAM.getElementAt(2).getValueAt(1));
        
        tableAM.delElement(element);
        
        Assert.assertEquals(0, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(0, tableAM.getDeletedObjects().size());             
    }
    
    @Test
    public void deleteRow() {
        tableAM.read();
        Assert.assertEquals(2, tableAM.getRowCount());
        
        tableAM.delElement(0);
        if(tableAM.isDisplayRemovedEntries()) {
            Assert.assertEquals(2, tableAM.getRowCount());
            Assert.assertEquals("Max Mustermann", tableAM.getElementAt(0).getValueAt(0));
            Assert.assertEquals("Petra Musterfrau", tableAM.getElementAt(1).getValueAt(0));
            Assert.assertEquals(20, tableAM.getElementAt(1).getValueAt(1));
            Assert.assertTrue(tableAM.getElementAt(0).isRemoved());
            Assert.assertFalse(tableAM.getElementAt(1).isDirty());
        } else {
            Assert.assertEquals(1, tableAM.getRowCount());
            Assert.assertEquals("Petra Musterfrau", tableAM.getElementAt(0).getValueAt(0));
            Assert.assertFalse(tableAM.getElementAt(0).isDirty());
            Assert.assertEquals(20, tableAM.getElementAt(0).getValueAt(1));
        }
        Assert.assertTrue(tableAM.isDirty());
        Assert.assertTrue(tableAM.isValid());
        
        Assert.assertEquals(0, tableAM.getCreatedObjects().size());
        Assert.assertEquals(0, tableAM.getModifiedObjects().size());
        Assert.assertEquals(1, tableAM.getDeletedObjects().size());             
        Assert.assertEquals("Max Mustermann", ((Person)tableAM.getDeletedObjects().get(0)).name);               
    }

    @Test
    public void commitElement() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        element.setValueAt(0, "Other Value");

        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        
        tableAM.commitElement(element);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        
        Assert.assertEquals("Other Value", tableAM.getValueAt(0, 0));
    }

    @Test
    public void commitNewElement() {
        tableAM.read();
        Element element = tableAM.addElement(null);
        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        
        tableAM.commitElement(element);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        
    }
	
	@Test
	public void rollbackElement() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        element.setValueAt(0, "Other Value");

        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        
        tableAM.rollbackElement(element);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        
        Assert.assertEquals("Max Mustermann", tableAM.getValueAt(0, 0));
	    
	}
	
	@Test
	public void markFaulty() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        element.setValueAt(0, "Other Value");

        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
        
        tableAM.markAsFaulty(element, "Error", null);

        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(false, tableAM.isValid());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(false, element.isValid());
        
        Assert.assertEquals("Other Value", tableAM.getValueAt(0, 0));
	}
	
    @Test
    public void originalValueDirty() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        Object value = element.getCurrentValue();
        element.setCurrentValue(value, true, true);
        
        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
        
        Object cellValue = element.getValueAt(0);
        element.setValueAt(0, "Test");
        
        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
        
        element.setValueAt(0, cellValue);
        
        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
        
        element.setCurrentValue(value, false, true);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
    }
    
    @Test
    public void originalValueValid() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        Object value = element.getCurrentValue();
        element.setCurrentValue(value, false, false);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        Assert.assertEquals(false, tableAM.isValid());
        
        Object cellValue = element.getValueAt(0);
        element.setValueAt(0, "Test");
        
        Assert.assertEquals(true, tableAM.isDirty());
        Assert.assertEquals(true, element.isDirty());
        Assert.assertEquals(false, tableAM.isValid());
        
        element.setValueAt(0, cellValue);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        Assert.assertEquals(false, tableAM.isValid());
        
        element.setCurrentValue(value, false, true);
        
        Assert.assertEquals(false, tableAM.isDirty());
        Assert.assertEquals(false, element.isDirty());
        Assert.assertEquals(true, tableAM.isValid());
    }
    
    @Test
    public void updateValueExtern() {
    	tableAM.read();
    	
    	Person person = (Person)tableAM.getCurrentValueAt(0);
    	person.age = 25;
    	tableAM.getElementAt(0).setCurrentValue(person, true, false);

    	Assert.assertEquals(0, tableAM.getCreatedObjects().size());
    	Assert.assertEquals(1, tableAM.getModifiedObjects().size());
    	Assert.assertEquals(0, tableAM.getDeletedObjects().size());
    }
    
    @Test 
    public void uniqueConstraintElementChangeTest() {
    	tableAM.read();
    	
    	tableAM.getElementAt(1).setValueAt(0, tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(false, tableAM.isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(1).isValid());
    	
    	tableAM.getElementAt(1).setValueAt(0, "Test");
    	Assert.assertEquals(true, tableAM.isValid());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(true, tableAM.getElementAt(1).isValid());    	    	
    }   
    
    @Test 
    public void uniqueConstraintClearTest() {
    	tableAM.read();
    	
    	tableAM.getElementAt(1).setValueAt(0, tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(false, tableAM.isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(1).isValid());
    	
    	tableAM.clear();
    	Assert.assertEquals(true, tableAM.isValid());   	    	
    }       
    
    @Test 
    public void uniqueConstraintRemoveElementTest() {
    	tableAM.read();
    	
    	tableAM.getElementAt(1).setValueAt(0, tableAM.getElementAt(0).getValueAt(0));
    	Assert.assertEquals(false, tableAM.isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(0).isValid());
    	Assert.assertEquals(false, tableAM.getElementAt(1).isValid());
    	
    	tableAM.delElement(0);
    	Assert.assertEquals(true, tableAM.isValid());
    	Assert.assertEquals(true, tableAM.getElementAt(0).isValid());
    }    
    
    @Test
    public void newEditableColumnTypeTest() {
    	tableAM.read();
    	
    	Assert.assertEquals(false, tableAM.isCellEditable(0,  0));
    	Assert.assertEquals(true, tableAM.isCellEditable(0,  1));
    	
    	tableAM.addElement(null);
    	Assert.assertEquals(true, tableAM.isCellEditable(2,  0));

    	tableAM.commitElement(tableAM.getElementAt(2));
    	Assert.assertEquals(false, tableAM.isCellEditable(2,  0));    	
    }
    
    @Test
    public void insertedElementIsDirty() {
        tableAM.read();
        Element element = tableAM.addElement(null);
        Assert.assertEquals(true, element.isDirty());
    }
    
    @Test
    public void removedElementIsDirty() {
        tableAM.read();
        tableAM.delElement(0);        
        Assert.assertEquals(true, tableAM.getDeletedElements().get(0).isDirty());
    }
    
    @Test
    public void removedElementIsInIdMap() {
        tableAM.read();
        Element element = tableAM.getElementAt(0);
        tableAM.delElement(0);        
        Assert.assertNotNull(tableAM.getElementById(element.getUniqueId()));
    }     
    
    @Test
    public void emptyTableValidationCheck() {
    	tableAM.addValidator(new AbstractValidator() {

			@Override
			protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
				ValidationResult result = new ValidationResult();
				if(tableAM.getRowCount() == 0) {
					result.addValidationError(new ValidationError(bindingId, "Table must not be empty.", null));
				}
				return result;
			}
		});
    	
    	tableAM.read(new ArrayList<Person>(), false);
    	Assert.assertFalse(tableAM.isValid());
    	
    	tableAM.addElement(new Person());
    	Assert.assertTrue(tableAM.isValid());
    	
    	tableAM.delElement(0);
    	Assert.assertFalse(tableAM.isValid());
    } 
    
    @Test
    public void tableValidationInformedAboutCellUpdates() {
    	tableAM.addValidator(new AbstractValidator() {

			@Override
			protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
				ValidationResult result = new ValidationResult();
				List<Element> elements = (List<Element>) attribute;
				if(elements.size() != 1 || !"A".equals(elements.get(0).getValueAt(0))) {
					result.addValidationError(new ValidationError(bindingId, "Check not successful.", null));
				}
				return result;
			}
		});
    	
    	tableAM.read(new ArrayList<Person>(), false);
    	Assert.assertFalse(tableAM.isValid());
    	
    	tableAM.addElement(new Person());
    	tableAM.getElementAt(0).setValueAt(0, "A");
    	Assert.assertTrue(tableAM.isValid());

    	tableAM.getElementAt(0).setValueAt(0, "B");
    	Assert.assertFalse(tableAM.isValid());
    }
    
    @Test
    public void tableValidationInteractionWithCellValidator() {
    	tableAM.addValidator(new AbstractValidator() {

			@Override
			protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
				ValidationResult result = new ValidationResult();
				List<Element> elements = (List<Element>) attribute;
				if(elements.size() != 1) {
					result.addValidationError(new ValidationError(bindingId, "Check not successful.", null));
				}
				return result;
			}
		});
    	
    	tableAM.getColumns().get(0).addValidator(new AbstractValidator() {

			@Override
			protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
				ValidationResult result = new ValidationResult();
				if(attribute == null || !"A".equals(attribute)) {
					result.addValidationError(new ValidationError(bindingId, "Check not successful.", null));
				}
				return result;
			}
		});
    	
    	tableAM.read(new ArrayList<Person>(), false);
    	Assert.assertFalse(tableAM.isValid());
    	
    	tableAM.addElement(new Person());
    	tableAM.getElementAt(0).setValueAt(0, "A");
    	Assert.assertTrue(tableAM.isValid());

    	tableAM.getElementAt(0).setValueAt(0, "B");
    	Assert.assertFalse(tableAM.isValid());
    }
    
	
	public static class Person {
		public String name;
		public int age;
	}
}
