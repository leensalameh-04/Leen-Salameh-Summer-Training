public class Main {

    public static void main(String[] args) {

        // Array of student grades
        int[] grades = {85, 90, 78, 92, 88};

        // Loop through the array
        System.out.println("Student Grades:");

        for (int i = 0; i < grades.length; i++) {
            System.out.println(grades[i]);
        }


        // Calling method with parameter
        int average = calculateAverage(grades);

        System.out.println("Average = " + average);


        // Calling overloaded methods
        System.out.println("Sum of two numbers: " + add(10, 20));

        System.out.println("Sum of three numbers: " + add(10, 20, 30));


        printMessage("Java Day 2 Practice");
    }


    // Method that receives an array as a parameter
    // and returns the average value
    public static int calculateAverage(int[] numbers) {

        int sum = 0;

        for (int number : numbers) {
            sum += number;
        }

        return sum / numbers.length;
    }


    // Method Overloading
    // Same method name with different parameters

    public static int add(int a, int b) {

        return a + b;
    }


    public static int add(int a, int b, int c) {

        return a + b + c;
    }


    // Method with String parameter
    public static void printMessage(String message) {

        System.out.println(message);
    }
}