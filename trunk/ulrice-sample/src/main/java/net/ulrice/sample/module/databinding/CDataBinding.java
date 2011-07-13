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
        
        model.getListAM().addViewAdapter(view.getListGA());

        model.personList = new LinkedList<MDataBinding.Person>();
        for (int i = 0; i < 1000; i++) {
            model.personList.add(createPerson());
        }

        model.getNameAM().read();
        model.getListAM().read();
    }

    public static MDataBinding.Person createPerson() {
        MDataBinding.Person result = new MDataBinding.Person(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), (int) ((Math.random() * 100.0) / 1));
        return result;
    }
}
