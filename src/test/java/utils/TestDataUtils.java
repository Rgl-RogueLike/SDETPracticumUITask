package utils;

import helpers.ParameterProvider;

import java.util.Random;

/**
 * Утилитарный класс для генерации тестовых данных.
 * Предоставляет методы для создания случайных значений, используемых в тестах.
 */
public class TestDataUtils {


    /**
     * Генерирует случайное количество товара в заданном диапазоне.
     * Используется для эмуляции реального пользовательского ввода количества.
     *
     * @return случайное количество товара (включая границы диапазона)
     * @throws java.lang.NumberFormatException если параметры конфигурации не являются числом
     */
    public static int randomQuantityProduct() {
        Random random = new Random();
        return random.nextInt(Integer.parseInt(ParameterProvider.get("max.products.quantity")))
                + Integer.parseInt(ParameterProvider.get("min.products.quantity"));
    }
}
