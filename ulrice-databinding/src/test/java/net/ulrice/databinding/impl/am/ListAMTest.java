package net.ulrice.databinding.impl.am;


import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.bufferedbinding.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.ListAM;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the list attribute model. 
 * @author christof
 */
public class ListAMTest {

	private ListAM listAM;
	
	public List<Person> list;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		listAM = new ListAM("PersonList", new ReflectionMVA(this, "list"));
		listAM.addColumn(new ColumnDefinition<String>("name", new ReflectionMVA(null, "name"), String.class));
		listAM.addColumn(new ColumnDefinition<Integer>("age", new ReflectionMVA(null, "age"), Integer.class));
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
		listAM.read();				
		assertEquals(2, listAM.getRowCount());
		assertEquals("Max Mustermann", ((Person)listAM.getElementAt(0).writeObject()).name);
		assertEquals("Petra Musterfrau", ((Person)listAM.getElementAt(1).writeObject()).name);
	}
	
	/**
	 * Tests, if the values are read into the internal element objects.
	 */
	@Test
	public void readCellValues() {
        assertEquals(DataState.NotInitialized, listAM.getState());
		listAM.read();
		assertEquals(DataState.NotChanged, listAM.getState());
		assertEquals("Max Mustermann", listAM.getElementAt(0).getValueAt(0));
		assertEquals(18, listAM.getElementAt(0).getValueAt(1));
		assertEquals("Petra Musterfrau", listAM.getElementAt(1).getValueAt(0));
		assertEquals(20, listAM.getElementAt(1).getValueAt(1));
	}
	
	/**
	 * Tests, if the data state mechanism of an element works.
	 */
	@Test
	public void updateCellValues() {
		listAM.read();
        listAM.getElementAt(0).setValueAt(0, "Otto Normal");
        assertEquals("Otto Normal", listAM.getElementAt(0).getValueAt(0));
        assertEquals(DataState.Changed, listAM.getElementAt(0).getState());
        assertEquals(DataState.Changed, listAM.getState());
        
        // Change back.
        listAM.getElementAt(0).setValueAt(0, "Max Mustermann");
        assertEquals("Max Mustermann", listAM.getElementAt(0).getValueAt(0));
        assertEquals(DataState.NotChanged, listAM.getElementAt(0).getState());
        assertEquals(DataState.NotChanged, listAM.getState());
        
        listAM.getElementAt(0).setValueAt("name", "Otto Normal");
        assertEquals("Otto Normal", listAM.getElementAt(0).getValueAt("name"));
        assertEquals(DataState.Changed, listAM.getElementAt(0).getState());
        assertEquals(DataState.Changed, listAM.getState());
        
        // Change back.
        listAM.getElementAt(0).setValueAt("name", "Max Mustermann");
        assertEquals("Max Mustermann", listAM.getElementAt(0).getValueAt("name"));
        assertEquals(DataState.NotChanged, listAM.getElementAt(0).getState());
        assertEquals(DataState.NotChanged, listAM.getState());
	}

	/**
	 * Tests writing of changed element values-
	 */
	@Test
	public void writeChangedValues() {
	    listAM.read();
        listAM.getElementAt(0).setValueAt(0, "Otto Normal");
	    listAM.write();
	    assertEquals("Otto Normal", list.get(0).name);
	}
	
	public static class Person {
		public String name;
		public int age;
	}
}
