/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.IFClosing;

/**
 * @author christof
 * 
 */
public class CDataBinding extends AbstractController {
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

        view.getTable().init(tableVA);
        view.getTable().updateColumnModel();
        
        
        
        view.getTable().setEnableCopyPaste(true, true);

        model.personList = new LinkedList<Person>();        

        model.getNameAM().read();        
        model.getTableAM().read();
        
        SwingWorker<List<Person>, List<Person>> worker = new SwingWorker<List<Person>, List<Person>>() {

			@Override
			protected List<Person> doInBackground() throws Exception {

		        List<List<Person>> list = new ArrayList<List<Person>>();
		        for (int i = 0; i < 2; i++) {
		        	list.add(new ArrayList<Person>(1000));
		            for (int j = 0; j < 100; j++) {
		            	list.get(i).add(PersonGenerator.createRandomPerson());
		            }
					System.out.println("Publish #" + i);
		        	publish(list.get(i));
		        }   

				return null;
			}
			
			@Override
			protected void process(List<List<Person>> chunks) {
		        for (int i = 0; i < chunks.size(); i++) {
		        	model.getTableAM().read(chunks.get(i), true);
				}			
		        view.getTable().sizeColumns(true);
			}        	
        };

        worker.execute();


        view.getTable().sizeColumns(false);
    }
    
    @Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
