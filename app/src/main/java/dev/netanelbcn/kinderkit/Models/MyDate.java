package dev.netanelbcn.kinderkit.Models;

public class MyDate {
    private int day;
    private int year;
    private int month;

    public MyDate(int day, int month, int year) {
        this.day = day;
        this.year = year;
        this.month = month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyDate myDate = (MyDate) o;
        return day == myDate.day && year == myDate.year && month == myDate.month;
    }


    public int getDay() {
        return day;
    }

    public MyDate setDay(int day) {
        this.day = day;
        return this;
    }

    public int getYear() {
        return year;
    }

    public MyDate setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public MyDate setMonth(int month) {
        this.month = month;
        return this;
    }

    public int compareTo(MyDate date) {
        if (this.year > date.getYear()) {
            return 1;
        } else if (this.year < date.getYear()) {
            return -1;
        } else {
            if (this.month > date.getMonth()) {
                return 1;
            } else if (this.month < date.getMonth()) {
                return -1;
            } else {
                if (this.day > date.getDay()) {
                    return 1;
                } else if (this.day < date.getDay()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public String toString() {
        return getDay() + "/" + getMonth() + "/" + getYear();
    }
}
