package com.example.jens.kitchenconverter;

import android.util.Log;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class MyRational {

    private int numerator;
    private int denominator = 0;

    // constructors
    public MyRational(int num, int den) {
        if (den != 0) {
            this.numerator = num / gcdThing(num, den);
            this.denominator = den / gcdThing(num, den);
        }
    };
    public MyRational(Double d) {
        String s = String.valueOf(d);
        int digitsDec = s.length() -1 -s.indexOf('.');

        int den =1;
        for(int i = 0; i < digitsDec; i++) {
            d *= 10;
            den *=10;
        }
        int num = (int) Math.round(d);

        this.numerator = num/gcdThing(num,den);
        this.denominator = den/gcdThing(num,den);
    }
    public MyRational(String s) { // assume that string is decimal (1.5) or simple fraction (3/2) or mixed fraction (1 1/2)
        int num=0;
        int den=0;

        if(!s.contains("/")) { // decimal number
            Double d = Double.valueOf(s);
            int digitsDec = s.length() -1 -s.indexOf('.');

            den =1;
            for(int i = 0; i < digitsDec; i++) {
                d *= 10;
                den *=10;
            }
            num = (int) Math.round(d);

        } else { // fraction or mixed fraction
            String[] numbers = s.split("[ /]");

            switch(numbers.length) {
                case 2: // true fraction
                    num = Integer.valueOf(numbers[0]);
                    den = Integer.valueOf(numbers[1]);
                    break;
                case 3: // mixed fraction
                    num = Integer.valueOf(numbers[1]) + Integer.valueOf(numbers[0]) * Integer.valueOf(numbers[2]);
                    den = Integer.valueOf(numbers[2]);
                    break;
            }
        }
        if (den!=0) {
            this.numerator = num / gcdThing(num, den);
            this.denominator = den / gcdThing(num, den);
        }
    }

    // getters
    public int getNumerator() { return numerator; }
    public int getDenominator() { return denominator; }

    // output Strings
    public String toFractionString() { // return fraction string
        if( denominator==1 ) {
            return String.valueOf(numerator);
        } else {
            return String.valueOf(numerator) + "/" + String.valueOf(denominator);
        }
    }
    public String toDecimalsString() {
        Double value = ((double) numerator) / denominator;
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        return df.format(value);
    }


    // rational arithmetics
    public MyRational reciprocal () {
        return new MyRational(denominator,numerator);
    }
    public MyRational multiply (MyRational rat2) {
        int num = numerator * rat2.getNumerator();
        int den = denominator * rat2.getDenominator();
        return new MyRational(num,den);
    }
    public MyRational divide (MyRational rat2) {
        return multiply(rat2.reciprocal());
    }

    public boolean isSet() {
        if( denominator == 0 ) {
            return false;
        } else {
            return true;
        }
    }

    //validate string (allow fractions, mixed fractions and decimals)
    public static boolean validFraction(String s) {
        String regex = "\\d{1,5}([.]\\d{1,3}|(\\s\\d{1,5})?[/]\\d{1,3})?";
        return s.matches(regex);
    }

    // GCD
    private static int gcdThing(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

}