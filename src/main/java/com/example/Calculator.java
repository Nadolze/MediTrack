package com.example;

public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    public int subtract(int a, int b)
    {
        return a - b;
    }

    public int multiply(int a, int b)
    {
        return a * b;
    }

    public static void main(String[] args) {
        Calculator c = new Calculator();
        int result = c.add(2, 3);
        System.out.println("Ergebnis: " + result);
    }
}
