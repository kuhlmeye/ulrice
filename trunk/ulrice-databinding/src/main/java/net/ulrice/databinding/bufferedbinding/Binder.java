package net.ulrice.databinding.bufferedbinding;

import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.bufferedbinding.impl.BindingGroup;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.bufferedbinding.impl.ListAM;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.impl.JSpinnerViewAdapter;
import net.ulrice.databinding.viewadapter.impl.JTextComponentViewAdapter;
import net.ulrice.databinding.viewadapter.impl.UListViewAdapter;
import net.ulrice.databinding.viewadapter.utable.UTableComponent;
import net.ulrice.databinding.viewadapter.utable.UTableViewAdapter;

public class Binder {
    
    private IFBinderCallback binderCallback;
    
    public Binder() {
        this(new BinderCallbackAdapter());
    }
    
    public Binder(BinderCallbackAdapter binderCallback) {
        this.binderCallback = binderCallback;
    }

    public UTableViewAdapter bind(UTableComponent component, IFAttributeModel< ?> model) {
        return bind(null, component, model);
    }
    
    public UTableViewAdapter bind(BindingGroup bindingGroup, UTableComponent component, IFAttributeModel< ?> model) {
        UTableViewAdapter viewAdapter = new UTableViewAdapter(component, model.getAttributeInfo());
        viewAdapter.setAttributeModel((TableAM) model);
        component.init(viewAdapter);
        component.updateColumnModel();
        binderCallback.adaptBinding(bindingGroup, component, viewAdapter, model);
        if(bindingGroup != null) {
            bindingGroup.bind(model, viewAdapter);
        } else {
            model.addViewAdapter(viewAdapter);
        }
        return viewAdapter;
    }
    
    public <T> UListViewAdapter bind(JList component, Class<?> viewType, ListAM<T> model) {
        return bind(component, viewType, model);
    }
    
    public <T> UListViewAdapter bind(BindingGroup bindingGroup, JList component, Class<?> viewType, ListAM<T> model) {
        UListViewAdapter viewAdapter = new UListViewAdapter(component, viewType, model.getAttributeInfo());
        binderCallback.adaptBinding(bindingGroup, component, viewAdapter, model);
        if(bindingGroup != null) {
            bindingGroup.bind(model, viewAdapter);
        } else {
            model.addViewAdapter(viewAdapter);
        }        
        return viewAdapter;
    }
    
    public JTextComponentViewAdapter bind(BindingGroup bindingGroup, JTextComponent component, IFAttributeModel<?> model) {
        JTextComponentViewAdapter viewAdapter = new JTextComponentViewAdapter(component, model.getAttributeInfo());
        binderCallback.adaptBinding(bindingGroup, component, viewAdapter, model);
        if(bindingGroup != null) {
            bindingGroup.bind(model, viewAdapter);
        } else {
            model.addViewAdapter(viewAdapter);
        }        
        return viewAdapter;
    }
    
    public JSpinnerViewAdapter bind(BindingGroup bindingGroup, JSpinner component, IFAttributeModel<?> model) {
        JSpinnerViewAdapter viewAdapter = new JSpinnerViewAdapter(component, model.getAttributeInfo());
        binderCallback.adaptBinding(bindingGroup, component, viewAdapter, model);
        if(bindingGroup != null) {
            bindingGroup.bind(model, viewAdapter);
        } else {
            model.addViewAdapter(viewAdapter);
        }        
        return viewAdapter;
    }
}
