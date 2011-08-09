package net.ulrice.databinding;



//TODO use normal 'ulrice' error handling (?) - or make this configurable
public class ErrorHandler {
    public static void handle (Exception exc) {
        throw new RuntimeException (exc);  //TODO Dummy - placeholder for "real" error handling
    }
}
