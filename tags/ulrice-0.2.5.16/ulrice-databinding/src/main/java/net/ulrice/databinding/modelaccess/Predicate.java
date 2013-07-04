package net.ulrice.databinding.modelaccess;

public interface Predicate {
    boolean getValue (boolean isValid, Object model);
    
    Predicate TRUE = new Predicate() {
        public boolean getValue (boolean isValid, Object model) {
            return true;
        }
    };
    Predicate FALSE = new Predicate() {
        public boolean getValue (boolean isValid, Object model) {
            return false;
        }
    };
}
