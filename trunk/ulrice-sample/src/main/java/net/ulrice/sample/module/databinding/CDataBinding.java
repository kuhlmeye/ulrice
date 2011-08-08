/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.util.LinkedList;
import java.util.UUID;

import javax.swing.JComponent;

import net.ulrice.module.IFModel;
import net.ulrice.module.impl.AbstractController;

/**
 * @author christof
 * 
 */
public class CDataBinding extends AbstractController {

    /**
     * @see net.ulrice.module.impl.AbstractController#instanciateModel()
     */
    @Override
    protected IFModel instantiateModel() {
        return new MDataBinding();
    }

    private final VDataBinding vDataBinding = new VDataBinding();
    @Override
    protected JComponent instantiateView() {
        return vDataBinding.getView();
    }

    /**
     * @see net.ulrice.module.impl.AbstractController#postCreationEvent(net.ulrice.module.IFModule)
     */
    @Override
    public void postCreate() {
        super.postCreate();

        MDataBinding model = (MDataBinding) getModel();
        VDataBinding view = (VDataBinding) getView();

        model.getNameAM().addViewAdapter(view.getTextFieldGA1());
        model.getNameAM().addViewAdapter(view.getTextFieldGA2());
        
        model.getTableAM().addViewAdapter(view.getListGA());

        model.personList = new LinkedList<Person>();
        for (int i = 0; i < 10000; i++) {
            model.personList.add(createPerson());
        }

        model.getNameAM().read();
        model.getTableAM().read();

        view.getListGA().sizeColumns(false);
    }

    public static Person createPerson() {
        Person result = new Person(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), (int) ((Math.random() * 100.0) / 1));
        return result;
    }
}
