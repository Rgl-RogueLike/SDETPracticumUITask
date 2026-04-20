package utils;

import helpers.ParameterProvider;

import java.util.Random;

public class TestDataUtils {

    public static int randomQuantityProduct() {
        Random random = new Random();
        return random.nextInt(Integer.parseInt(ParameterProvider.get("max.products.quantity")))
                + Integer.parseInt(ParameterProvider.get("min.products.quantity"));
    }
}
