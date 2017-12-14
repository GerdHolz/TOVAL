/*
 * Copyright (c) 2015, Thomas Stocker
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the disclaimer
 * below) provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of IIG Telematics, Uni Freiburg nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY
 * THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BELIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.invation.code.toval.misc;

import java.util.Random;

/**
 *
 * @author Thomas Stocker
 *
 */
public class RandomUtils {

    private static final java.util.Random rand = new Random();

    /**
     * Returns a random positive double value in the range [0;1).
     *
     * @return Random double value in the range [0;1)
     */
    public static double randomPosDouble() {
        return Math.abs(rand.nextDouble());
    }

    /**
     * Returns a random negative double value in the range (-1;0].
     *
     * @return Random double value in the range (-1;0]
     */
    public static double randomNegDouble() {
        return -randomPosDouble();
    }
    
    /**
     * Returns a random double value in the range [min;max].
     *
     */
    public static double randomDouble(double min, double max){
    	return min + (rand.nextDouble() * (max - min));
    }

    /**
     * Returns a random <code>int</code> value out of a specified range
     *
     * @param lowerBound Lower bound of the target range (inclusive)
     * @param upperBound Upper bound of the target range (exclusive)
     * @return A random <code>int</code> value out of a specified range
     */
    public static int randomIntBetween(int lowerBound, int upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("lower bound higher than upper bound");
        }
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    }

    /**
     * Returns a random <code>long</code> value out of a specified range
     *
     * @param lowerBound Lower bound of the target range (inclusive)
     * @param upperBound Upper bound of the target range (exclusive)
     * @return A random <code>long</code> value out of a specified range
     */
    public static long randomLongBetween(long lowerBound, long upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("lower bound higher than upper bound");
        }
        return lowerBound + (long) (rand.nextDouble() * (upperBound - lowerBound));
    }

    /**
     * A random string generator.
     */
    public static class RandomStringGenerator {

        private final char[] symbols;

        private final Random random = new Random();

        private final char[] buf;

        /**
         * Creates a new RandomStringGenerator of the specified length.
         *
         * @param length Length of the random strings, must be larger than 1.
         * @param includeNumericSymbols
         * @param includeLowerCaseLetters
         * @param includeUpperCaseLetters
         */
        public RandomStringGenerator(int length, boolean includeNumericSymbols, boolean includeLowerCaseLetters, boolean includeUpperCaseLetters) {
            if (length < 1) {
                throw new IllegalArgumentException("length < 1: " + length);
            }
            buf = new char[length];

            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; includeNumericSymbols && ch <= '9'; ++ch) {
                tmp.append(ch);
            }
            for (char ch = 'a'; includeLowerCaseLetters && ch <= 'z'; ++ch) {
                tmp.append(ch);
            }
            for (char ch = 'A'; includeUpperCaseLetters && ch <= 'Z'; ++ch) {
                tmp.append(ch);
            }
            symbols = tmp.toString().toCharArray();
        }

        /**
         * Returns the next random string.
         *
         * @return A random string
         */
        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx) {
                buf[idx] = symbols[random.nextInt(symbols.length)];
            }
            return new String(buf);
        }
    }
}
