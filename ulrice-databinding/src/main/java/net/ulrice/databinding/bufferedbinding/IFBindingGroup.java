/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;


/**
 * @author christof
 *
 */
public interface IFBindingGroup {

    public abstract boolean isDirty();

    public abstract boolean isValid();

    public abstract void write();

    public abstract void read();
    
    void addBindingGroupChangeListener(IFBindingGroupEventListener l);
    
    void removeBindingGroupChangeListener(IFBindingGroupEventListener l);
}
