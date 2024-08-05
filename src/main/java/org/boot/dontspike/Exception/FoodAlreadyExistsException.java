package org.boot.dontspike.Exception;

public class FoodAlreadyExistsException extends RuntimeException {
    public FoodAlreadyExistsException(String s) {
        super(s);
    }
}
