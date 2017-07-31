/*
 * StudentsT05.java
 *
 */

package com.sciome.bmdexpress2.util.stat;

public class StudentsT05 {
    private final double[] t05Values = {
        12.71, // df = 1
        4.3, // df = 2
        3.18, // df = 3
        2.78, // df = 4
        2.57, // df = 5
        2.45, // df = 6
        2.37, // df = 7
        2.31, // df = 8
        2.26, // df = 9
        2.23, // df = 10
        2.2, // df = 11
        2.18, // df = 12
        2.16, // df = 13
        2.14, // df = 14
        2.13, // df = 15
        2.12, // df = 16
        2.11, // df = 17
        2.1, // df = 18
        2.09, // df = 19
        2.09, // df = 20
        2.08, // df = 21
        2.07, // df = 22
        2.07, // df = 23
        2.06, // df = 24
        2.06, // df = 25
        2.06, // df = 26
        2.05, // df = 27
        2.05, // df = 28
        2.05, // df = 29
        2.04, // df = 30
        2.02, // df = 40
        2, // df = 60
        1.98, // df = 120
        1.96, // df = 240
        };

    public StudentsT05() {
    }

    public double t05(int df) {
        if (df <= 30) {
            df = df - 1;
        } else if (df <= 40) {
            df = 30;
        } else if (df <= 60) {
            df = 31;
        } else if (df <= 120) {
            df = 32;
        } else {
            df = 33;
        }

        return t05Values[df];
    }

    public static double t05Value(int df) {
        StudentsT05 student = new StudentsT05();

        return student.t05(df);
    }
}
