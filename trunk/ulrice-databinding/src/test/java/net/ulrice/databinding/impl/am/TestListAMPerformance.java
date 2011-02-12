/**
 * 
 */
package net.ulrice.databinding.impl.am;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JTable;

import net.ulrice.databinding.impl.da.ReflectionDA;
import net.ulrice.databinding.impl.ga.TableGA;

/**
 * @author christof
 *
 */
public class TestListAMPerformance {
    
    private static ListAM<List<Person>, Person> listAM;   
    public static List<Person> list;
    
    public static void main(String[] args) {
    
        measureDirectReadTime(100000);
        measureDirectReadTime(100000);
        measureDirectReadTime(100000);
        measureDirectReadTime(100000);
        measureDirectReadTime(100000);                
    }

    /**
     * 
     */
    private static void measureDirectReadTime(int listSize) {
        listAM = new ListAM<List<Person>, Person>("PersonList", null);
        listAM.addColumn(new ColumnDefinition<String>("name", new ReflectionDA<String>(null, "name"), String.class));
        listAM.addColumn(new ColumnDefinition<Integer>("age", new ReflectionDA<Integer>(null, "age"), Integer.class));
        
        TableGA tableGA = new TableGA("PersonList");
        tableGA.getComponent();
        
        list = new LinkedList<Person>();
        for(int i = 0; i < listSize; i++) {
            list.add(createPerson());
        }
        
        System.out.print("Direct reading " + listSize + " Persons from list...");
        long startTime = System.currentTimeMillis();
        listAM.directRead(list);
        System.out.println("took " + (System.currentTimeMillis() - startTime) + "ms.");

        System.out.print("-Filtering for age > 50...");
        startTime = System.currentTimeMillis();
        tableGA.getFilter().setFilterValue("age", "> 50");
        System.out.println("took " + (System.currentTimeMillis() - startTime) + "ms.");

    }
    
    public static Person createPerson() {
        Person result = new Person();
        result.name = UUID.randomUUID().toString();
        result.age = (int)((Math.random()*100.0)/100);
        return result;
    }
    
    public static class Person {
        public String name;
        public int age;
    }
}
