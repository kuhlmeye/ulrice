package net.ulrice.databinding.converter;


/**
 * konvertiert einen Wert in einen anderen Wert, z.B. int in String oder umgekehrt für die Präsentation
 * 
 * @author arno
 */
public interface IFValueConverter {
    Object viewToModel (Object o);
    Object modelToView (Object o);
}
