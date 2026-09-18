/**
 * Exercise sourced from Practice-It by the University of Washington.
 * Original problems available at: https://practiceit.cs.washington.edu/
 *
 * @author Erik Kizior
 */
public class IfElseMystery1 {
    public static void ifElseMystery1(int x, int y) {
        int z = 4;
        if (z <= x) {
            z = x + 1;
        } else {
            z = z + 9;
        }
        if (z <= y) {
            y++;
        }
        System.out.println(z + " " + y);
    }

    // TODO: For each call below, indicate what output is produced.
    public static void main(String[] args) {
        ifElseMystery1(3, 20);
        ifElseMystery1(4, 5);
        ifElseMystery1(5, 5);
        ifElseMystery1(6, 10);
    }
}

/* Before running the code, type your answer below.

ifElseMystery1(3, 20): z = 4; 4 <= 3 is false, so z = 13; 13 <= 20, so y = 21 -> "13 21"
ifElseMystery1(4, 5):  z = 4; 4 <= 4 is true,  so z = 5;  5 <= 5,  so y = 6  -> "5 6"
ifElseMystery1(5, 5):  z = 4; 4 <= 5 is true,  so z = 6;  6 <= 5 is false        -> "6 5"
ifElseMystery1(6, 10): z = 4; 4 <= 6 is true,  so z = 7;  7 <= 10, so y = 11 -> "7 11"

Output:
13 21
5 6
6 5
7 11

Then, click the green play button to check your work. */