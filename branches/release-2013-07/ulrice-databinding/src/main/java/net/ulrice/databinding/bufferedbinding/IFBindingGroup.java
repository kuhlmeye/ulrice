/**
 * 
 */
package net.ulrice.databinding.bufferedbinding;


/**
 * @author christof
 *
 */
public interface IFBindingGroup {

    boolean isDirty();

    boolean isValid();

    void write();

    void read();
    
    void addBindingGroupChangeListener(IFBindingGroupEventListener l);
    
    void removeBindingGroupChangeListener(IFBindingGroupEventListener l);
}
