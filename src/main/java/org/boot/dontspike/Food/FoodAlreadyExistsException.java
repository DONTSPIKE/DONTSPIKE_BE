package org.boot.dontspike.Food;

public class FoodAlreadyExistsException extends RuntimeException {
    public FoodAlreadyExistsException(String s) {
        super(s);
    }
}
