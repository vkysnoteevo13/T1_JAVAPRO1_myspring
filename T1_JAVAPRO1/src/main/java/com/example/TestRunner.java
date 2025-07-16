package com.example;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

// Класс для запуска тестов
class TestRunner {
    public static void runTests(Class<?> testClass) {
        try {
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Собираем все методы
            Method beforeSuite = null;
            Method afterSuite = null;
            List<Method> beforeTests = new ArrayList<>();
            List<Method> afterTests = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();

            // Проверяем все методы класса
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BeforeSuite.class)) {
                    beforeSuite = method;
                }
                if (method.isAnnotationPresent(AfterSuite.class)) {
                    afterSuite = method;
                }
                if (method.isAnnotationPresent(BeforeTest.class)) {
                    beforeTests.add(method);
                }
                if (method.isAnnotationPresent(AfterTest.class)) {
                    afterTests.add(method);
                }
                if (method.isAnnotationPresent(Test.class)) {
                    Test testAnnotation = method.getAnnotation(Test.class);
                    int priority = testAnnotation.priority();
                    if (priority < 1 || priority > 10) {
                        throw new RuntimeException("Priority must be between 1 and 10");
                    }
                    testMethods.add(method);
                }
            }

            // Выполняем BeforeSuite
            if (beforeSuite != null) {
                beforeSuite.invoke(null);
            }

            // Сортируем тесты по приоритету (от большего к меньшему)
            testMethods.sort((m1, m2) -> {
                int p1 = m1.getAnnotation(Test.class).priority();
                int p2 = m2.getAnnotation(Test.class).priority();
                return Integer.compare(p2, p1);
            });

            // Выполняем тесты
            for (Method testMethod : testMethods) {
                // Выполняем BeforeTest
                for (Method beforeTest : beforeTests) {
                    beforeTest.invoke(testInstance);
                }

                // Проверяем наличие CsvSource
                if (testMethod.isAnnotationPresent(CsvSource.class)) {
                    CsvSource csvSource = testMethod.getAnnotation(CsvSource.class);
                    String[] values = csvSource.value().split(",\\s*");
                    Class<?>[] paramTypes = testMethod.getParameterTypes();

                    // Преобразуем строковые значения в соответствующие типы
                    Object[] params = new Object[values.length];
                    for (int i = 0; i < values.length; i++) {
                        params[i] = convertToType(values[i], paramTypes[i]);
                    }

                    testMethod.invoke(testInstance, params);
                } else {
                    testMethod.invoke(testInstance);
                }

                // Выполняем AfterTest
                for (Method afterTest : afterTests) {
                    afterTest.invoke(testInstance);
                }
            }

            // Выполняем AfterSuite
            if (afterSuite != null) {
                afterSuite.invoke(null);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error running tests: " + e.getMessage(), e);
        }
    }

    // Метод для конвертации строки в соответствующий тип
    private static Object convertToType(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == String.class) {
            return value;
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        throw new RuntimeException("Unsupported parameter type: " + type.getName());
    }
}
