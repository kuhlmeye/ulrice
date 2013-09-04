package net.ulrice.databinding.reflect;

import java.lang.reflect.Field;

public interface FieldFilter {
    
    /**
     * @return {@code true} if the filter accepts {@code field}, otherwise reutrns {@code false}.
     */
    boolean accept(Field aField);
}
