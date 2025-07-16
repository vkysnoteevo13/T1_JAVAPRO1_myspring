package com.example;

class TestClass {
    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("BeforeSuite executed");
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("AfterSuite executed");
    }

    @BeforeTest
    public void beforeEachTest() {
        System.out.println("BeforeTest executed");
    }

    @AfterTest
    public void afterEachTest() {
        System.out.println("AfterTest executed");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("Test1 executed (priority 1)");
    }

    @Test(priority = 8)
    public void test2() {
        System.out.println("Test2 executed (priority 8)");
    }

    @Test
    public void test3() {
        System.out.println("Test3 executed (priority 5)");
    }

    @Test(priority = 3)
    @CsvSource("10, Java, 20, true")
    public void testWithCsv(int a, String b, int c, boolean d) {
        System.out.println("TestWithCsv executed: a=" + a + ", b=" + b + ", c=" + c + ", d=" + d);
    }
}
