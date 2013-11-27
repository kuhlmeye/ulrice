/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import net.ulrice.Ulrice;
import net.ulrice.databinding.bufferedbinding.impl.ListDataProvider;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;
import net.ulrice.process.AbstractProcess;

/**
 * @author christof
 * 
 */
public class CDataBinding extends AbstractController {
    protected static final int SIZE = 10000;
	private final MDataBinding model = new MDataBinding();
    private final VDataBinding view = new VDataBinding();

    @Override
    public JComponent getView() {
        return view.getView();
    }

    /**
     * @see net.ulrice.module.impl.AbstractController#postCreationEvent(net.ulrice.module.IFModule)
     */
    @Override
    public void postCreate() {
        super.postCreate();

        model.getNameAM().addViewAdapter(view.getTextFieldGA1());
        model.getNameAM().addViewAdapter(view.getTextFieldGA2());
        
        UTableViewAdapter tableVA = new UTableViewAdapter(view.getTable(), null);
        model.getTableAM().addViewAdapter(tableVA);
        model.getTableAM().setReadOnly(true);

        view.getTable().init(tableVA);
        view.getTable().updateColumnModel();
        
        
        
        view.getTable().setEnableCopyPaste(true, true);

        model.personList = new LinkedList<Person>();        

        model.getNameAM().read();        
        model.getTableAM().read();
        
        
        
        AbstractProcess<Void,Void> loader = model.getTableAM().createLoader(this, true, new ListDataProvider<Person>() {

			@Override
			public List<Person> getData() {
		        List<Person> list = new ArrayList<Person>(SIZE);
	            for (int j = 0; j < SIZE; j++) {
	            	list.add(PersonGenerator.createRandomPerson());
	            }
				return list;
			}
		});

        //Ulrice.getProcessManager().registerProcess(loader);
        loader.execute();
    }
    
    @Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
