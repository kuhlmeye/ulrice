/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.util.LinkedList;
import java.util.UUID;

import net.ulrice.module.IFModel;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFView;
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
    protected IFModel instanciateModel() {
        return new MDataBinding();
    }

    /**
     * @see net.ulrice.module.impl.AbstractController#instanciateView()
     */
    @Override
    protected IFView instanciateView() {
        return new VDataBinding();
    }

    /**
     * @see net.ulrice.module.impl.AbstractController#postCreationEvent(net.ulrice.module.IFModule)
     */
    @Override
    public void postCreationEvent(IFModule module) {
        super.postCreationEvent(module);

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
