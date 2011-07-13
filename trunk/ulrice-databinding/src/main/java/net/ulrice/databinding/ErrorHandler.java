package net.ulrice.databinding;


public class ErrorHandler {
    public static void handle (Exception exc) {
        throw new RuntimeException (exc);  //TODO Dummy - placeholder for "real" error handling
    }
}
