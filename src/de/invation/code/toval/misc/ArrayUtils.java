package de.invation.code.toval.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.invation.code.toval.math.Permutations;
import de.invation.code.toval.reflect.GenericReflection;
import de.invation.code.toval.validate.Validate;

public class ArrayUtils {

	/**
	 * Random number generator.
	 */
	private static final java.util.Random rand = new Random();
	/**
	 * String for value separation.<br>
	 * Used for generating String representations of arrays.
	 */
	public static final char VALUE_SEPARATION = ' ';
	/**
	 * String representation for empty arrays.
	 */
	public static final String EMPTY_ARRAY = "[]";
	/**
	 * Default precision used for String representations of array elements
	 * having type <code>Float</code> or <code>Double</code>.
	 */
	public static final int DEFAULT_PRECISION = 2;
	
	/**
	 * Combines the given arrays into one array.
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		  int totalLength = first.length;
		  for (T[] array : rest) {
		    totalLength += array.length;
		  }
		  T[] result = Arrays.copyOf(first, totalLength);
		  int offset = first.length;
		  for (T[] array : rest) {
		    System.arraycopy(array, 0, result, offset, array.length);
		    offset += array.length;
		  }
		  return result;
		}

	/**
	 * Returns a new array with the given size containing the default value at
	 * each index.
	 * 
	 * @param <T>
	 *            Array type.
	 * @param size
	 *            The desired size of the array.
	 * @param defaultValue
	 *            The default value to use
	 * @return The created array
	 */
	public static <T> T[] createArray(int size, T defaultValue) {
		T[] result = (T[]) GenericReflection.newArray(defaultValue.getClass(), size);
		for (int i = 0; i < result.length; i++) {
			result[i] = defaultValue;
		}
		return result;
	}

	/**
	 * Returns a new array with the given size containing the default value at
	 * each index.
	 * 
	 * @param size
	 *            The desired size of the array.
	 * @param start
	 *            The default value to use
	 * @return The created array
	 */
	public static Integer[] createAndInitializeArray(int size, Integer start) {
		Integer[] result = new Integer[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = start++;
		}
		return result;
	}

	public static byte[] createArray(int size, byte defaultValue) {
		byte[] result = new byte[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = defaultValue;
		}
		return result;
	}

	public static short[] createArray(int size, short defaultValue) {
		short[] result = new short[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = defaultValue;
		}
		return result;
	}

	public static double[] createArray(int size, double defaultValue) {
		double[] result = new double[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = defaultValue;
		}
		return result;
	}

	public static short[] createRandomArray(int size, short maxValue) {
		short[] result = new short[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = (short) (rand.nextInt(maxValue) + 1);
		}
		return result;
	}

	public static int[] createArray(int size, int defaultValue) {
		int[] result = new int[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = defaultValue;
		}
		return result;
	}

	public static int[] createAndInitializeArray(int size, int begin) {
		int[] result = new int[size];
		int c = 0;
		for (int i = begin; i < size + begin; i++)
			result[c++] = i;
		return result;
	}

	/**
	 * Returns a random element of the given array.
	 * 
	 * @param <T>
	 *            Type of array elements
	 * @param arr
	 *            Array
	 * @return Random element of <code>arr</code>
	 */
	public static <T> T getRandomItem(T[] arr) {
		return arr[rand.nextInt(arr.length)];
	}

	/**
	 * Reverses the entries of a given array.
	 * 
	 * @param <T>
	 *            Type of array elements
	 * @param arr
	 *            Array to reverse
	 * @return The same array in reverse order
	 */
	public static <T> T[] reverseArray(T[] arr) {
		for (int left = 0, right = arr.length - 1; left < right; left++, right--) {
			T temp = arr[left];
			arr[left] = arr[right];
			arr[right] = temp;
		}
		return arr;
	}

	public static <T> List<T> toList(T[] arr) {
		return Arrays.asList(arr);
	}

	public static <T> List<String> toStringList(T[] arr) {
		List<String> result = new ArrayList<>();
		for (T t : arr) {
			result.add(t.toString());
		}
		return result;
	}

	/**
	 * Swaps the elements at position <code>a</code> and <code>b</code> in the
	 * array <code>arr</code>.
	 * 
	 * @param <T>
	 *            Type of array elements
	 * @param arr
	 *            Array that contains the elements to be swapped
	 * @param a
	 *            First position
	 * @param b
	 *            Second position
	 */
	public static <T> void swap(T[] arr, int a, int b) {
		if (a < 0 || a > arr.length || b < 0 || b > arr.length)
			throw new IllegalArgumentException("swap position out of bounds.");
		if (a != b) {
			T t = arr[a];
			arr[a] = arr[b];
			arr[b] = t;
		}
	}

	/**
	 * Permutes the elements of the given array.
	 * 
	 * @param <T>
	 *            Array-type
	 * @param arr
	 *            Array to shuffle
	 */
	public static <T> void shuffleArray(T[] arr) {
		for (int i = arr.length; i > 1; i--)
			swap(arr, i - 1, rand.nextInt(i));
	}

	/**
	 * Checks if the given array contains the specified value.<br>
	 * This method works with strict reference comparison.
	 * 
	 * @param <T>
	 *            Type of array elements and <code>value</code>
	 * @param array
	 *            Array to examine
	 * @param value
	 *            Value to search
	 * @return <code>true</code> if <code>array</code> contains
	 *         <code>value</code>, <code>false</code> otherwise
	 */
	public static <T> boolean arrayContainsRef(T[] array, T value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given array contains the specified value.<br>
	 * This method works with the equals method.
	 * 
	 * @param <T>
	 *            Type of array elements and <code>value</code>
	 * @param array
	 *            Array to examine
	 * @param value
	 *            Value to search
	 * @return <code>true</code> if <code>array</code> contains
	 *         <code>value</code>, <code>false</code> otherwise
	 */
	public static <T> boolean arrayContainsEq(T[] array, T value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Divides the given array using the boundaries in <code>cuts</code>.<br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut
	 * at position i divides the given array in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start
	 * and end-indexes<br>
	 * 
	 * @param <T>
	 *            Type of array elements
	 * @param arr
	 *            The array to divide
	 * @param cuts
	 *            Cut positions for divide operations
	 * @return A list of subarrays of <code>arr</code> according to the given
	 *         cut positions
	 */
	public static <T> List<T[]> divideArray(T[] arr, Integer... cuts) {
		Arrays.sort(cuts);
		int c = cuts.length;
		if (cuts[0] < 0 || cuts[c - 1] > arr.length - 1)
			throw new IllegalArgumentException("cut position out of bounds.");
		int startIndex = cuts[0] == 0 ? 1 : 0;
		if (cuts[c - 1] != arr.length - 1) {
			cuts = Arrays.copyOf(cuts, cuts.length + 1);
			cuts[cuts.length - 1] = arr.length - 1;
			c++;
		}
		List<T[]> result = new ArrayList<>(c - startIndex);
		int lastEnd = 0;
		for (int i = startIndex; i <= c - 1; i++) {
			int c2 = i < c - 1 ? 0 : 1;
			result.add(Arrays.copyOfRange(arr, lastEnd, cuts[i] + c2));
			lastEnd = cuts[i];
		}
		return result;
	}

	/**
	 * Divides the given object-array using the boundaries in <code>cuts</code>.
	 * <br>
	 * Cuts are interpreted in an inclusive way, which means that a single cut
	 * at position i divides the given array in 0...i-1 + i...n<br>
	 * This method deals with both cut positions including and excluding start
	 * and end-indexes<br>
	 * 
	 * @param <T>
	 *            Object array type.
	 * @param arr
	 *            Array to divide
	 * @param cuts
	 *            Cut positions for divide operations
	 * @return A list of subarrays of <code>arr</code> according to the given
	 *         cut positions
	 * @see #divideArray(Object[], Integer[])
	 */
	public static <T> List<T[]> divideObjectArray(T[] arr, Integer... cuts) {
		return divideArray(arr, cuts);
	}

	/**
	 * Returns an Iterator for all possible permutations of the given array.
	 * 
	 * @param <T>
	 *            Type of list array
	 * @param arr
	 *            Basic array for permutations
	 * @return Iterator holding all possible permutations
	 */
	public static <T> Iterator<T[]> getPermutations(T[] arr) {
		return new ArrayPermutations<>(arr);
	}

	/**
	 * Generates all permutations of a given array.
	 * 
	 * @param <T>
	 *            Type of array elements.
	 */
	private static class ArrayPermutations<T> extends Permutations<T[]> {

		private final T[] array;

		public ArrayPermutations(T[] array) {
			super(array.length);
			this.array = array;
		}

		/**
		 * Returns a new array with permuted elements.
		 * 
		 * @return A new array with permuted elements
		 */
		@Override
		public T[] next() {
			Integer[] next = super.nextPermutation();
			T[] newArr = array.clone();
			for (int i = 0; i < next.length; i++) {
				newArr[i] = array[next[i]];
			}
			return newArr;
		}
	}

	/**
	 * Returns a String representation of an object-array.<br>
	 * 
	 * @param <T>
	 *            Object array type.
	 * @param arr
	 *            Object-array for String representation
	 * @param valueSeparation
	 *            Value separation character.
	 * @return String representation of <code>arr</code>
	 * @see #getFormat(Object[], int, char)
	 */
	public static <T> String toString(T[] arr, char valueSeparation) {
		return toString(arr, DEFAULT_PRECISION, valueSeparation);
	}

	public static <T> String toString(T[] arr) {
		return toString(arr, DEFAULT_PRECISION, VALUE_SEPARATION);
	}

	/**
	 * Returns a String representation of an object-array.<br>
	 * The specified precision is only applicable for <code>Float</code> and
	 * <code>Double</code> elements.
	 * 
	 * @param <T>
	 *            Object array type.
	 * @param arr
	 *            Object-array for String representation
	 * @param precision
	 *            Precision for Float and Double elements.
	 * @param valueSeparation
	 *            Value separation character.
	 * @return String representation of <code>arr</code>
	 * @see #getFormat(Object[], int, char)
	 */
	public static <T> String toString(T[] arr, int precision, char valueSeparation) {
		if (arr.length > 0)
			return String.format(getFormat(arr, precision, valueSeparation), arr);
		else
			return EMPTY_ARRAY;
	}

	/**
	 * Returns a format-String that can be used to generate a String
	 * representation of an array using the String.format method.
	 * 
	 * @param arr
	 *            Array for which a String representation is desired
	 * @param precision
	 *            Desired precision for <code>Float</code> and
	 *            <code>Double</code> elements
	 * @return Format-String for <code>arr</code>
	 * @see Formatter
	 * @see String#format(String, Object...)
	 */
	private static <T> String getFormat(T[] arr, int precision, char valueSeparation) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < arr.length - 1; i++) {
			builder.append(FormatUtils.getFormat(arr[i], precision));
			builder.append(valueSeparation);
		}
		builder.append(FormatUtils.getFormat(arr[arr.length - 1], precision));
		builder.append(']');
		return builder.toString();
	}

	/**
	 * Checks if all given arrays contain the same values.<br>
	 * Note: Only use this method when the given arrays are sorted and contain
	 * only distinct values.
	 * 
	 * @param arrs
	 * @return
	 */
	public static boolean containSameElementsSorted(short[]... arrs) {
		Validate.notNull(arrs);
		if (arrs.length == 1)
			return true;

		int firstSize = arrs[0].length;
		for (int i = 1; i < arrs.length; i++) {
			if (arrs[i].length != firstSize) {
				return false;
			}
		}

		for (int j = 0; j < firstSize; j++) {
			short firstValue = arrs[0][j];
			for (int k = 1; k < arrs.length; k++) {
				if (arrs[k][j] != firstValue)
					return false;
			}
		}
		return true;
	}

	public static <T> boolean contains(T[] arr, T element) {
		for (T val : arr) {
			if (val.equals(element))
				return true;
		}
		return false;
	}

	/**
	 * Determines the intersection of the given arrays.<br>
	 * Note: Only use this method when the given arrays are sorted and contain
	 * only distinct values.
	 * 
	 * @param arrs
	 * @return
	 */
	public static short[] intersectionSorted(short[]... arrs) {
		if (arrs.length == 0)
			return new short[0];
		if (arrs.length == 1)
			return arrs[0];

		short[][] arrList = new short[arrs.length - 1][];
		short[] minLengthArray = arrs[0];
		for (int i = 1; i < arrs.length; i++) {
			short[] arr = arrs[i];
			if (arr.length < minLengthArray.length) {
				arrList[i - 1] = minLengthArray;
				minLengthArray = arr;
			} else {
				arrList[i - 1] = arr;
			}
		}
		//
		// System.out.println("--");
		// System.out.println(Arrays.toString(minLengthArray));
		// for(short[] otherArr: arrList){
		// System.out.println(Arrays.toString(otherArr));
		// }
		// System.out.println("--");

		short[] pointer = ArrayUtils.createArray(arrs.length - 1, (short) 0);

		List<Short> commonIndices = new ArrayList<>(minLengthArray.length);
		for (short i = 0; i < minLengthArray.length; i++) {
			short stateIndex = minLengthArray[i];
			boolean insert = true;
			for (short j = 0; j < pointer.length; j++) {
				for (short k = pointer[j]; k < arrList[j].length; k++) {
					if (stateIndex < arrList[j][k]) {
						// Array does not contain the state-index
						break;
					} else if (stateIndex > arrList[j][k]) {
						if (k < arrList[j].length - 1) {
							pointer[j] = (short) (pointer[j] + 1);
						} else {
							break;
						}
					} else {
						break;
					}
				}
				if (arrList[j][pointer[j]] != stateIndex) {
					insert = false;
					break;
				}
			}
			if (insert) {
				commonIndices.add(stateIndex);
			}
		}

		short[] result = new short[commonIndices.size()];
		for (int l = 0; l < commonIndices.size(); l++) {
			result[l] = commonIndices.get(l);
		}
		return result;
	}

	public static byte max(byte[] arr) {
		byte maxValue = Byte.MIN_VALUE;
		for (byte value : arr) {
			if (value > maxValue) {
				maxValue = value;
			}
		}
		return maxValue;
	}

	public static byte min(byte[] arr) {
		byte minValue = Byte.MAX_VALUE;
		for (byte value : arr) {
			if (value < minValue) {
				minValue = value;
			}
		}
		return minValue;
	}

	public static MinMaxByte minMax(byte[] arr) {
		byte minValue = Byte.MAX_VALUE;
		byte maxValue = Byte.MIN_VALUE;
		for (byte value : arr) {
			if (value < minValue) {
				minValue = value;
			}
			if (value > maxValue) {
				maxValue = value;
			}
		}
		return new MinMaxByte(minValue, maxValue);
	}

	public static short max(short[] arr) {
		short maxValue = Short.MIN_VALUE;
		for (short value : arr) {
			if (value > maxValue) {
				maxValue = value;
			}
		}
		return maxValue;
	}

	public static short min(short[] arr) {
		short minValue = Short.MAX_VALUE;
		for (short value : arr) {
			if (value < minValue) {
				minValue = value;
			}
		}
		return minValue;
	}

	public static MinMaxShort minMax(short[] arr) {
		short minValue = Short.MAX_VALUE;
		short maxValue = Short.MIN_VALUE;
		for (short value : arr) {
			if (value < minValue) {
				minValue = value;
			}
			if (value > maxValue) {
				maxValue = value;
			}
		}
		return new MinMaxShort(minValue, maxValue);
	}

}
