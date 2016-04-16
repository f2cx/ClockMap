/* =============================================================================
 * The following code is based on the great TreeViz project.
 * 
 * http://www.randelshofer.ch/treeviz/
 *
 * Copyright (rc) 2008 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 * 
 * You can license the overall code either under Creative Commons
 * Attribution 3.0, the MIT license, or the GNU Lesser General License LGPL.
 * =============================================================================
 */
package cx.ff.clockmap.util;

/**
 * Immutable complex number of the form x+bi.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-07-07 Created.
 */
public class Complex implements Cloneable {

    private double x;
    private double y;

    /**
     * Creates a complex number with the real part x and the imaginary part y.
     *
     * @param x real part of the complex number
     * @param y imaginary part of the complex number
     */
    public Complex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the real part of the complex number.
     *
     * @return real part.
     */
    public double real() {
        return x;
    }

    /**
     * Returns the imaginary part of the complex number.
     *
     * @return real part.
     */
    public double img() {
        return y;
    }

    /**
     * Returns a complex number whose value is (this + that).
     *
     * @param that A complex number.
     * @return this + that.
     */
    public Complex add(Complex that) {
        return new Complex(this.x + that.x, this.y + that.y);
    }

    /**
     * Returns a complex number whose value is (a + b).
     *
     * @param a A complex number.
     * @param b A complex number.
     * @return a + b.
     */
    public static Complex add(Complex a, Complex b) {
        return a.add(b);
    }

    /**
     * Returns a complex number whose value is (this - that).
     *
     * @param that A complex number.
     * @return this - that.
     */
    public Complex sub(Complex that) {
        return new Complex(this.x - that.x, this.y - that.y);
    }

    /**
     * Returns a complex number whose value is (a - b).
     *
     * @param a A complex number.
     * @param b A complex number.
     * @return a - b.
     */
    public static Complex sub(Complex a, Complex b) {
        return a.sub(b);
    }

    /**
     * Returns a complex number whose value is (this * that).
     *
     * @param that A complex number.
     * @return this * that.
     */
    public Complex mul(Complex that) {
        return new Complex(this.x * that.x - this.y * that.y, this.x * that.y + this.y * that.x);
    }

    /**
     * Returns a complex number whose value is (a * b).
     *
     * @param a A complex number.
     * @param b A complex number.
     * @return a * b.
     */
    public static Complex mul(Complex a, Complex b) {
        return a.mul(b);
    }

    /**
     * Returns a complex number whose value is (this / that).
     *
     * @param that A complex number.
     * @return this / that.
     */
    public Complex div(Complex that) {
        return new Complex((this.x * that.x + this.y * that.y) / (that.x * that.x + that.y * that.y),
                (this.y * that.x - this.x * that.y) / (that.x * that.x + that.y * that.y));
    }

    /**
     * Returns a complex number whose value is (a / b).
     *
     * @param a A complex number.
     * @param b A complex number.
     * @return a / b.
     */
    public static Complex div(Complex a, Complex b) {
        return a.div(b);
    }

    /**
     * Returns the argument of this complex number (the angle in radians with
     * the x-axis in polar coordinates).
     *
     * @return atan2(y, x).
     */
    public double arg() {
        return Math.atan2(y, x);
    }

    /**
     * Returns the modulo of this complex number.
     *
     * @return sqrt(x*x + y*y).
     */
    public double mod() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the principal branch of the square root of this complex number.
     *
     * @return square root.
     */
    public Complex sqrt() {
        double r = Math.sqrt(mod());
        double theta = this.arg() / 2;
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }

    /**
     * Returns true of this complex number is equal to the specified complex
     * number.
     *
     * @return true if equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Complex) {
            Complex that = (Complex) o;
            return that.x == this.x && that.y == this.y;
        }
        return false;
    }

    /**
     * Returns a hash code for this complex number.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    /**
     * Returns a clone of this complex number.
     *
     * @return a clone.
     */
    @Override
    public Complex clone() {
        try {
            return (Complex) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("Cloneable not implemented");
        }
    }

    /**
     * Returns a descriptive string representation of this complex number.
     *
     * @return a descriptive string.
     */
    @Override
    public String toString() {
        if (y >= 0) {
            return "(" + x + "+" + y + "i)";
        } else {
            return "(" + x + "" + y + "i)";
        }
    }

    /**
     * Returns true if this complex numer is not a number (NaN).
     *
     * @return true if NaN.
     */
    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }
}
