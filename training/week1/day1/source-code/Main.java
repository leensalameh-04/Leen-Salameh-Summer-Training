public class Main {

    public static void main(String[] args) {

        // Array
        int[] numbers = {10, 20, 30, 40, 50};

        // Loop through array
        System.out.println("Numbers:");

        for (int i = 0; i < numbers.length; i++) {
            System.out.println(numbers[i]);
        }

        // Calling methods
        int sum = calculateSum(numbers);
        System.out.println("Sum = " + sum);

        printMessage("Learning Java");

        // Method overloading
        System.out.println(add(5, 10));
        System.out.println(add(5, 10, 15));
    }


    // Method with parameter and return value
    public static int calculateSum(int[] array) {

        int total = 0;

        for (int number : array) {
            total += number;
        }

        return total;
    }


    // Method with parameter
    public static void printMessage(String message) {

        System.out.println(message);
    }


    // Method Overloading
    public static int add(int a, int b) {

        return a + b;
    }


    public static int add(int a, int b, int c) {

        return a + b + c;
    }
}