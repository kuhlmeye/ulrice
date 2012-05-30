/**
 * 
 */
package net.ulrice.sample.module.databinding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.Timer;

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
        
        model.getTableAM().addViewAdapter(view.getListGA());

        model.personList = new LinkedList<Person>();

        model.getNameAM().read();
        
        model.getTableAM().read();
        
        SwingWorker<List<Person>, List<Person>> worker = new SwingWorker<List<Person>, List<Person>>() {

			@Override
			protected List<Person> doInBackground() throws Exception {

		        List<List<Person>> list = new ArrayList<List<Person>>();
		        for (int i = 0; i < 100; i++) {
		        	list.add(new ArrayList<Person>(1000));
		            for (int j = 0; j < 1000; j++) {
		            	list.get(i).add(createPerson());
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
			}        	
        };

        worker.execute();


        view.getListGA().sizeColumns(false);
    }

    public static Person createPerson() {
        Person result = new Person(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), (int) ((Math.random() * 100.0) / 1));
        return result;
    }
    
    @Override
    public void onClose(IFClosing closing) {
        
        closing.doClose();
    }
}
