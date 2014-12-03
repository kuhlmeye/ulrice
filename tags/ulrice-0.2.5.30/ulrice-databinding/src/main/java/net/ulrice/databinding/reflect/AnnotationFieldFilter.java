/**
 * created by: EEXHDBT
 * created at: 11.12.2008, 12:39:37
 */
package net.ulrice.databinding.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class AnnotationFieldFilter implements FieldFilter {

    private final Class<? extends Annotation> annotationClass;

    public AnnotationFieldFilter(final Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public boolean accept(Field aField) {
        return aField.getAnnotation(annotationClass) != null;
    }
}
