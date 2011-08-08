/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.modelaccess.impl.DynamicReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.IndexedReflectionMVA;
import net.ulrice.databinding.modelaccess.impl.ReflectionMVA;
import net.ulrice.databinding.validation.impl.RegExValidator;
import net.ulrice.module.IFModel;

/**
 * @author christof
 *
 */
public class MDataBinding implements IFModel<CDataBinding> {

	public String name;
    public List<Person> personList = new ArrayList<Person>();

    private GenericAM<String> nameAM;
    private TableAM tableAM;
	
	/**
	 * @see net.ulrice.module.IFModel#initialize()
	 */
	@Override
	public void initialize(CDataBinding controller) {
		nameAM = new GenericAM<String>(new ReflectionMVA(this, "name"));
		nameAM.setValidator(new RegExValidator<String>("(hallo|hi)", "Validation failed. Only 'hallo' or 'hi' is allowed"));
		name = "hallo";

        personList.add(new Person("Max", "Mustermann", 18));
        personList.add(new Person("Petra", "Musterfrau", 20));
        personList.add(new Person("Otto", "Normal", 20));
        
        tableAM = new TableAM(new IndexedReflectionMVA(this, "personList"));
        tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "lastName"), String.class));
        tableAM.addColumn(new ColumnDefinition<String>(new DynamicReflectionMVA(Person.class, "firstName"), String.class));
        tableAM.addColumn(new ColumnDefinition<Integer>(new DynamicReflectionMVA(Person.class, "age"), Integer.class));
	}

	/**
	 * @return the nameAM
	 */
	public GenericAM<String> getNameAM() {
		return nameAM;
	}


    public TableAM getTableAM() {
        return tableAM;
    }
}	

