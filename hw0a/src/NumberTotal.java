/**
 * Exercise sourced from Practice-It by the University of Washington.
 * Original problems available at: https://practiceit.cs.washington.edu/
 *
 * @author Erik Kizior
 */

// TODO: What is the output of the following program?
public class NumberTotal {
    public static void main(String[] args) {
        int total = 25;
        for (int number = 1; number <= (total / 2); number++) {
            total = total - number;
            System.out.println(total + " " + number);
        }
    }
}

/* Before running the code, type your answer below.

The loop continues while number <= total / 2, where total shrinks each iteration:

number=1: total = 25 - 1 = 24  -> "24 1"
number=2: total = 24 - 2 = 22  -> "22 2"
number=3: total = 22 - 3 = 19  -> "19 3"
number=4: total = 19 - 4 = 15  -> "15 4"
number=5: total = 15 - 5 = 10  -> "10 5"
number=6: 10 / 2 = 5, 6 <= 5 is false, loop ends.

Output:
24 1
22 2
19 3
15 4
10 5

Then, click the green play button to check your work. */