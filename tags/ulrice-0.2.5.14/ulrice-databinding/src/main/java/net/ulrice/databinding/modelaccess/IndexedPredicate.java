package net.ulrice.databinding.modelaccess;


public interface IndexedPredicate {
    boolean getValue (boolean isValid, int index, Object model);
    
    IndexedPredicate TRUE = new IndexedPredicate() {
        public boolean getValue (boolean isValid, int index, Object model) {
            return true;
        }
    };
    IndexedPredicate FALSE = new IndexedPredicate() {
        public boolean getValue (boolean isValid, int index, Object model) {
            return false;
        }
    };
}
