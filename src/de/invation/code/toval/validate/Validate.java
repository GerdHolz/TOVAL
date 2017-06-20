package de.invation.code.toval.validate;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import de.invation.code.toval.time.TimeUtils;
import de.invation.code.toval.time.TimeValue;
import de.invation.code.toval.validate.ParameterException.ErrorCode;

public class Validate {
	
	private static boolean validation = true;
	
	public static void disableValidation(){
		validation = false;
	}
	
	public static void enableValidation(){
		validation = true;
	}
	
	//------- NULL and Empty Checks -------------------------------------------------------------------------
	
	/**
	 * Checks if the given object is <code>null</code>
	 * @param o The object to validate.
	 * @throws ParameterException if the given object is <code>null</code>.
	 */
	public static <O extends Object> void notNull(O o) {
		if(!validation) return;
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER);
	}
	
	/**
	 * Checks if the given object is <code>null</code>
	 * @param o The object to validate.
	 * @param message The error message to include in the Exception in case the validation fails.
	 * @throws ParameterException if the given object is <code>null</code>.
	 */
	public static <O extends Object> void notNull(O o, String message) {
		if(!validation) return;
		notNull(message);
		if(o == null)
			throw new ParameterException(ErrorCode.NULLPOINTER, message);
	}
	
	/**
	 * Checks if the given iterable object contains <code>null</code> values.
	 * @param coll The iterable object to validate.
	 * @throws ParameterException if the given object contains any <code>null</code> values.
	 */
	public static <O extends Object> void noNullElements(Iterable<O> coll)  {
		if(!validation) return;
		notNull(coll);
		for(O o: coll){
			try{
				notNull(o);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	/**
	 * Checks if the given array contains <code>null</code> values.
	 * @param arr The array to validate.
	 * @throws ParameterException if the given array contains any <code>null</code> values.
	 */
	public static <O extends Object> void noNullElements(O[] arr)  {
		if(!validation) return;
		notNull(arr);
		for(int i=0; i<arr.length; i++){
			try{
				notNull(arr[i]);
			} catch(ParameterException e1){
				throw new ParameterException(ErrorCode.NULLELEMENTS);
			}
		}
	}
	
	/**
	 * Checks if the given iterable object is empty.
	 * @param coll The iterable object to validate.
	 * @throws ParameterException if the given object is empty.
	 */
	public static <O extends Object> void notEmpty(Iterable<O> coll)  {
		if(!validation) return;
		notNull(coll);
		if(!coll.iterator().hasNext())
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	/**
	 * Checks if the given array is empty.
	 * @param arr The array to validate.
	 * @throws ParameterException if the given array is empty.
	 */
	public static <O extends Object> void notEmpty(O[] arr)  {
		if(!validation) return;
		notNull(arr);
		if(arr.length == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	/**
	 * Checks if the given String is empty.
	 * @param string The String to validate.
	 * @throws ParameterException if the given String is empty.
	 */
	public static void notEmpty(String string)  {
		if(!validation) return;
		notNull(string);
		if(string.length() == 0)
			throw new ParameterException(ErrorCode.EMPTY);
	}
	
	/**
	 * Checks if the given String is empty.
	 * @param string The String to validate.
	 * @param message The error message to include in the Exception in case the validation fails.
	 * @throws ParameterException if the given String is empty.
	 */
	public static void notEmpty(String string, String message)  {
		if(!validation) return;
		notNull(message);
		notNull(string, message);
		if(string.length() == 0)
			throw new ParameterException(ErrorCode.EMPTY, message);
	}
	
	//------- Boolean checks ----------------------------------------------------------------------
	
	/**
	 * Checks if the given boolean expression evaluates to <code>true</code>.
	 * @param expression The expression to evaluate.
	 * @throws ParameterException if the given expression does not evaluate to <code>true</code>.
	 */
	public static void isTrue(Boolean expression) {
		if(!validation) return;
		notNull(expression);
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT);
	}
	
	/**
	 * Checks if the given boolean expression evaluates to <code>true</code>.
	 * @param expression The expression to evaluate.
	 * @param constraint Textual description of the expression to include in the exception in case the validation fails.
	 * @throws ParameterException if the given expression does not evaluate to <code>true</code>.
	 */
	public static void isTrue(Boolean expression, String constraint) {
		if(!validation) return;
		notNull(expression);
		notNull(constraint);
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT, "Parameter must fulfill constraint \""+constraint+"\"");
	}
	
	/**
	 * Checks if the given boolean expression evaluates to <code>false</code>.
	 * @param expression The expression to evaluate.
	 * @throws ParameterException if the given expression does not evaluate to <code>false</code>.
	 */
	public static void isFalse(Boolean expression) {
		if(!validation) return;
		notNull(expression);
		if(expression)
			throw new ParameterException(ErrorCode.CONSTRAINT);
	}
	
	/**
	 * Checks if the given boolean expression evaluates to <code>false</code>.
	 * @param expression The expression to evaluate.
	 * @param constraint Textual description of the expression to include in the exception in case the validation fails.
	 * @throws ParameterException if the given expression does not evaluate to <code>false</code>.
	 */
	public static void isFalse(Boolean expression, String constraint) {
		if(!validation) return;
		notNull(expression);
		notNull(constraint);
		if(!expression)
			throw new ParameterException(ErrorCode.CONSTRAINT, "Parameter must not fulfill constraint \""+constraint+"\"");
	}
	
	
	//------- Range checks -------------------------------------------------------------------------
	
	/**
	 * Checks if the given parameters can be used as min/max bounds.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @throws ParameterException if the maximum is bigger than the minimum.
	 */
	public static void minMax(Integer min, Integer max) {
		if(!validation) return;
		Validate.notNull(min);
		Validate.notNull(max);
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	/**
	 * Checks if the given parameters can be used as min/max bounds.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @throws ParameterException if the maximum is bigger than the minimum.
	 */
	public static void minMax(Long min, Long max) {
		if(!validation) return;
		Validate.notNull(min);
		Validate.notNull(max);
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	/**
	 * Checks if the given parameters can be used as min/max bounds.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @throws ParameterException if the maximum is bigger than the minimum.
	 */
	public static void minMax(Double min, Double max) {
		if(!validation) return;
		Validate.notNull(min);
		Validate.notNull(max);
		if(min > max)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Minumum is bigger than maximum.");
	}
	
	//------- Comparisons ----------------------------------------------------------------------------
	
	/**
	 * Checks if the given value is bigger than the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is smaller or equal to the reference value.
	 */
	public static <T extends Object> void bigger(Comparable<T> value, T reference)  {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(reference);
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller or equal "+reference);
	}
	
	/**
	 * Checks if the given value is bigger than the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @param message The error message to include into the Exception in case the validation fails.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is smaller or equal to the reference value.
	 */
	public static <T extends Object> void bigger(Comparable<T> value, T reference, String message)  {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(reference);
		Validate.notNull(message);
		if(value.compareTo(reference) <= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	/**
	 * Checks if the given value is bigger or equal to the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is smaller than the reference value.
	 */
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference)  {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(reference);
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is smaller than "+reference);
	}
	
	/**
	 * Checks if the given value is bigger or equal to the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @param message The error message to include into the Exception in case the validation fails.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is smaller than the reference value.
	 */
	public static <T extends Object> void biggerEqual(Comparable<T> value, T reference, String message)  {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(reference);
		Validate.notNull(message);
		if(value.compareTo(reference) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	/**
	 * Checks if the given value is smaller than the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is bigger or equal to the reference value.
	 */
	public static <T extends Object> void smaller(Comparable<T> value, T reference)  {
		if(!validation) return;
		notNull(value);
		notNull(reference);
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger or equal "+reference);
	}
	
	/**
	 * Checks if the given value is smaller than the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @param message The error message to include into the Exception in case the validation fails.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is bigger or equal to the reference value.
	 */
	public static <T extends Object> void smaller(Comparable<T> value, T reference, String message)  {
		if(!validation) return;
		notNull(value);
		notNull(reference);
		notNull(message);
		if(value.compareTo(reference) >= 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	/**
	 * Checks if the given value is smaller or equal to the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is bigger than the reference value.
	 */
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference)  {
		if(!validation) return;
		notNull(value);
		notNull(reference);
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is bigger "+reference);
	}
	
	/**
	 * Checks if the given value is smaller or equal to the reference value.
	 * @param value The value to validate.
	 * @param reference The reference value to check against.
	 * @param message The error message to include into the Exception in case the validation fails.
	 * @throws ParameterException if one of the parameters is <code>null</code> of the value is bigger than the reference value.
	 */
	public static <T extends Object> void smallerEqual(Comparable<T> value, T reference, String message)  {
		if(!validation) return;
		notNull(value);
		notNull(reference);
		notNull(message);
		if(value.compareTo(reference) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	/**
	 * Checks if the given value lies in between the start and end values (inclusive).
	 * @param start The start value (lower bound).
	 * @param end The end value (upper bound).
	 * @param value The comparable value to validate.
	 * @throws ParameterException if some parameters are <code>null</code>, the given value lies not between the start and end values. 
	 */
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value)  {
		if(!validation) return;
		Validate.notNull(start);
		Validate.notNull(end);
		Validate.notNull(value);
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]: " + value);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, "Parameter is not in range ["+start+";"+end+"]: " + value);
	}

	/**
	 * Checks if the given value lies in between the start and end values (inclusive).
	 * @param start The start value (lower bound).
	 * @param end The end value (upper bound).
	 * @param value The comparable value to validate.
	 * @param message The error message to include into the Exception in case the validation fails.
	 * @throws ParameterException if some parameters are <code>null</code>, the given value lies not between the start and end values. 
	 */
	public static <T extends Object> void inclusiveBetween(T start, T end, Comparable<T> value, String message)  {
		if(!validation) return;
		Validate.notNull(start);
		Validate.notNull(end);
		Validate.notNull(value);
		Validate.notNull(message);
		if(value.compareTo(start) < 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
		if(value.compareTo(end) > 0)
			throw new ParameterException(ErrorCode.RANGEVIOLATION, message);
	}
	
	
	//------- Typing properties ----------------------------------------------------------------------
	
	/**
	 * Checks if a given value has a specific type.
	 * @param value The value to validate.
	 * @param type The target type for the given value.
	 * @throws ParameterException if the type of the given value is not a valid subclass of the target type.
	 */
	public static <T,V extends Object> void type(V value, Class<T> type) {
		if(!validation) return;
		notNull(value);
		notNull(type);
		if(!type.isAssignableFrom(value.getClass())){
			throw new TypeException(type.getCanonicalName());
		}
	}
	
	/**
	 * Checks if the elements of a given collection have a specific type.
	 * @param valueColl The collection of elements to validate.
	 * @param type The target type.
	 * @throws ParameterException if the type of at least one element is not a valid subclass of the target type.
	 */
	public static <T,V extends Object> void type(Collection<V> valueColl, Class<T> type) {
		for(V value: valueColl)
			type(value, type);
	}
	
	//------- Numerical checks ----------------------------------------------------------------------------
	
	//------- Integer --------------------------------
	
	/**
	 * Checks if the given integer is positive.
	 * @param value The integer value to validate.
	 * @throws ParameterException if the given integer value is <code>null</code> or smaller/equal to 0.
	 */
	public static void positive(Integer value) {
		if(!validation) return;
		notNull(value);
		if(value <= 0)
			throw new ParameterException(ErrorCode.NOTPOSITIVE);
	}
	
	/**
	 * Checks if the given integer is NOT positive.
	 * @param value The integer value to validate.
	 * @throws ParameterException if the given integer value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Integer value) {
		if(!validation) return;
		notNull(value);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE);
	}
	
	/**
	 * Checks if the given integer value is NOT positive.
	 * @param value The integer value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given integer value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Integer value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE, message);
	}
	
	/**
	 * Checks if the given integer is negative.
	 * @param value The integer value to validate.
	 * @throws ParameterException if the given integer value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Integer value) {
		if(!validation) return;
		Validate.notNull(value);
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	/**
	 * Checks if the given integer is negative.
	 * @param value The integer value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given integer value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Integer value, String message) {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	/**
	 * Checks if the given integer is NOT negative.
	 * @param value The integer value to validate.
	 * @throws ParameterException if the given integer value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Integer value) {
		if(!validation) return;
		Validate.notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	/**
	 * Checks if the given integer is NOT negative.
	 * @param value The integer value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given integer value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Integer value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	//------- Long -----------------------------------
	
	/**
	 * Checks if the given long value is positive.
	 * @param value The long value to validate.
	 * @throws ParameterException if the given long value is <code>null</code> or smaller/equal to 0.
	 */
	public static void positive(Long value) {
		if(!validation) return;
		notNull(value);
		if(value <= 0)
			throw new ParameterException(ErrorCode.NOTPOSITIVE);
	}
	
	/**
	 * Checks if the given long is NOT positive.
	 * @param value The long value to validate.
	 * @throws ParameterException if the given long value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Long value) {
		if(!validation) return;
		notNull(value);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE);
	}
	
	/**
	 * Checks if the given long value is NOT positive.
	 * @param value The long value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given long value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Long value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE, message);
	}
	
	/**
	 * Checks if the given long value is negative.
	 * @param value The long value to validate.
	 * @throws ParameterException if the given long value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Long value) {
		if(!validation) return;
		Validate.notNull(value);
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	/**
	 * Checks if the given long value is negative.
	 * @param value The long value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given long value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Long value, String message) {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0L)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	/**
	 * Checks if the given long value is NOT negative.
	 * @param value The long value to validate.
	 * @throws ParameterException if the given long value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Long value) {
		if(!validation) return;
		notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	/**
	 * Checks if the given long value is NOT negative.
	 * @param value The long value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given long value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Long value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	//------- Double ---------------------------------
	
	/**
	 * Checks if the given double value is positive.
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value is <code>null</code> or smaller/equal to 0.
	 */
	public static void positive(Double value) {
		if(!validation) return;
		notNull(value);
		if(value <= 0)
			throw new ParameterException(ErrorCode.NOTPOSITIVE);
	}
	
	/**
	 * Checks if the given double is NOT positive.
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Double value) {
		if(!validation) return;
		notNull(value);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE);
	}
	
	/**
	 * Checks if the given double is NOT positive.
	 * @param value The double value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given double value is <code>null</code> or bigger than 0.
	 */
	public static void notPositive(Double value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value > 0)
			throw new ParameterException(ErrorCode.POSITIVE, message);
	}
	
	/**
	 * Checks if the given double is negative.
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Double value) {
		if(!validation) return;
		Validate.notNull(value);
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE);
	}
	
	/**
	 * Checks if the given double is negative.
	 * @param value The double value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given double value is <code>null</code> or bigger/equal to 0.
	 */
	public static void negative(Double value, String message) {
		if(!validation) return;
		Validate.notNull(value);
		Validate.notNull(message);
		if(value >= 0.0)
			throw new ParameterException(ErrorCode.NOTNEGATIVE, message);
	}
	
	/**
	 * Checks if the given double is NOT negative.
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Double value) {
		if(!validation) return;
		notNull(value);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE);
	}
	
	/**
	 * Checks if the given double is NOT negative.
	 * @param value The double value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given double value is <code>null</code> or smaller than 0.
	 */
	public static void notNegative(Double value, String message) {
		if(!validation) return;
		notNull(value);
		notNull(message);
		if(value < 0)
			throw new ParameterException(ErrorCode.NEGATIVE, message);
	}
	
	//------- Numerical checks on Strings -----------------------------------------------------------------
	
	/**
	 * Checks if the given String is an integer value.
	 * @param value The String value to validate.
	 * @return The parsed integer value.
	 * @throws ParameterException if the given String value cannot be parsed as integer.
	 */
	public static Integer isInteger(String value) {
		Validate.notNull(value);
		Validate.notEmpty(value);
		Integer intValue = null;
		try{
			intValue = 	Integer.parseInt(value);
		} catch(NumberFormatException e){
			throw new TypeException("Integer");
		}
		return intValue;
	}
	
	/**
	 * Checks if the given String is a double value.
	 * @param value The String value to validate.
	 * @return The parsed double value.
	 * @throws ParameterException if the given String value cannot be parsed as double.
	 */
	public static Double isDouble(String value) {
		Validate.notNull(value);
		Validate.notEmpty(value);
		Double doubleValue = null;
		try{
			doubleValue = Double.parseDouble(value);
		} catch(NumberFormatException e){
			throw new TypeException("Double");
		}
		return doubleValue;
	}
	
	
	//------- Integer from String ----------------------------------------------------------------
	
	/**
	 * Checks if the given String is a positive integer value.<br>
	 * This method tries to parse an integer value and then checks if it is bigger than 0.
	 * @param value The String value to validate.
	 * @return The parsed integer value
	 * @throws ParameterException if the given String value cannot be parsed as integer or its value is smaller or equal to 0.
	 */
	public static Integer positiveInteger(String value) {
		Integer intValue = Validate.isInteger(value);
		positive(intValue);
		return intValue;
	}
	
	/**
	 * Checks if the given String is NOT a positive integer value.<br>
	 * This method tries to parse an integer value and then checks if it is smaller or equal to 0.
	 * @param value The String value to validate.
	 * @return The parsed integer value
	 * @throws ParameterException if the given String value cannot be parsed as integer or its value is bigger than 0.
	 */
	public static Integer notPositiveInteger(String value) {
		Integer intValue = Validate.isInteger(value);
		notPositive(intValue);
		return intValue;
	}
	
	/**
	 * Checks if the given String is a negative integer value.<br>
	 * This method tries to parse an integer value and then checks if it is smaller than 0.
	 * @param value The String value to validate.
	 * @return The parsed integer value
	 * @throws ParameterException if the given String value cannot be parsed as integer or its value is bigger or equal to 0.
	 */
	public static Integer negativeInteger(String value) {
		Integer intValue = Validate.isInteger(value);
		negative(intValue);
		return intValue;
	}
	
	/**
	 * Checks if the given String is NOT a negative integer value.<br>
	 * This method tries to parse an integer value and then checks if it is bigger or equal to 0.
	 * @param value The String value to validate.
	 * @return The parsed integer value
	 * @throws ParameterException if the given String value cannot be parsed as integer or its value is smaller than 0.
	 */
	public static Integer notNegativeInteger(String value) {
		Integer intValue = Validate.isInteger(value);
		notNegative(intValue);
		return intValue;
	}
	
	
	//------- Double from String -----------------------------------------------------------------
	
	/**
	 * Checks if the given String is a positive double value.<br>
	 * This method tries to parse a double value and then checks if it is bigger than 0.
	 * @param value The String value to validate.
	 * @return The parsed double value
	 * @throws ParameterException if the given String value cannot be parsed as double or its value is smaller or equal to 0.
	 */
	public static Double positiveDouble(String value) {
		Double doubleValue = Validate.isDouble(value);
		positive(doubleValue);
		return doubleValue;
	}
	
	/**
	 * Checks if the given String is NOT a positive double value.<br>
	 * This method tries to parse a double value and then checks if it is smaller or equal to 0.
	 * @param value The String value to validate.
	 * @return The parsed double value
	 * @throws ParameterException if the given String value cannot be parsed as double or its value is bigger than 0.
	 */
	public static Double notPositiveDouble(String value) {
		Double doubleValue = Validate.isDouble(value);
		notPositive(doubleValue);
		return doubleValue;
	}
	
	/**
	 * Checks if the given String is a negative double value.<br>
	 * This method tries to parse a double value and then checks if it is smaller than 0.
	 * @param value The String value to validate.
	 * @return The parsed double value
	 * @throws ParameterException if the given String value cannot be parsed as double or its value is bigger or equal to 0.
	 */
	public static Double negativeDouble(String value) {
		Double doubleValue = Validate.isDouble(value);
		negative(doubleValue);
		return doubleValue;
	}
	
	/**
	 * Checks if the given String is NOT a negative double value.<br>
	 * This method tries to parse a double value and then checks if it is bigger or equal to 0.
	 * @param value The String value to validate.
	 * @return The parsed double value
	 * @throws ParameterException if the given String value cannot be parsed as double or its value is smaller than 0.
	 */
	public static Double notNegativeDouble(String value) {
		Double doubleValue = Validate.isDouble(value);
		notNegative(doubleValue);
		return doubleValue;
	}


	//------- Percentages and probabilities -------------------------------------------------------
	
	/**
	 * Checks if the given double is a probability (lies between 0.0 and 1.0, inclusive).
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value lies outside the bounds.
	 */
	public static void probability(Double value) {
		if(!validation) return;
		inclusiveBetween(0.0, 1.0, value);
	}
	
	/**
	 * Checks if the given double is a probability (lies between 0.0 and 1.0, inclusive).
	 * @param value The double value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given double value lies outside the bounds.
	 */
	public static void probability(Double value, String message) {
		if(!validation) return;
		Validate.inclusiveBetween(0.0, 1.0, value, message);
	}
	
	/**
	 * Checks if the given String value is a probability (double value between 0.0 and 1.0, inclusive).
	 * @param value The String value to validate.
	 * @return The parsed double value.
	 * @throws ParameterException if the given String value cannot be parsed to a double or lies outside the bounds.
	 */
	public static Double probability(String value) {
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 1.0, doubleValue);
		return doubleValue;
	}
	
	/**
	 * Checks if the given String value is a probability (double value between 0.0 and 1.0, inclusive).
	 * @param value The String value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @return The parsed double value.
	 * @throws ParameterException if the given String value cannot be parsed to a double or lies outside the bounds.
	 */
	public static Double probability(String value, String message) {
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 1.0, doubleValue, message);
		return doubleValue;
	}
	
	/**
	 * Checks if the given double is a percentage (lies between 0.0 and 100.0, inclusive).
	 * @param value The double value to validate.
	 * @throws ParameterException if the given double value lies outside the bounds.
	 */
	public static void percentage(Double value) {
		if(!validation) return;
		inclusiveBetween(0.0, 100.0, value);
	}
	
	/**
	 * Checks if the given double is a percentage (lies between 0.0 and 100.0, inclusive).
	 * @param value The double value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @throws ParameterException if the given double value lies outside the bounds.
	 */
	public static void percentage(Double value, String message) {
		if(!validation) return;
		inclusiveBetween(0.0, 100.0, value, message);
	}
	
	/**
	 * Checks if the given String value is a percentage (double value between 0.0 and 100.0, inclusive).
	 * @param value The String value to validate.
	 * @return The parsed double value.
	 * @throws ParameterException if the given String value cannot be parsed to a double or lies outside the bounds.
	 */
	public static Double percentage(String value) {
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 100.0, doubleValue);
		return doubleValue;
	}
	
	/**
	 * Checks if the given String value is a percentage (double value between 0.0 and 100.0, inclusive).
	 * @param value The String value to validate.
	 * @param message The error message to include in the exception in case the validation fails.
	 * @return The parsed double value.
	 * @throws ParameterException if the given String value cannot be parsed to a double or lies outside the bounds.
	 */
	public static Double percentage(String value, String message) {
		Double doubleValue = notNegativeDouble(value);
		Validate.inclusiveBetween(0.0, 100.0, doubleValue, message);
		return doubleValue;
	}
	
	
	//------- File (name) validation --------------------------------------------------------------
	
	/**
	 * Checks if the given String is a valid file name.<br>
	 * This method assumes that the String does not contain any path information but just the file name.<br>
	 * file names must neither be <code>null</code> nor empty or contain file separators.
	 * @param fileName The file name to validate.
	 * @throws ParameterException if the given String is not a valid file name.
	 */
	public static void fileName(String fileName) {
		if(!validation) return;
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		if(fileName.contains(File.separator))
			throw new ParameterException("File name contains path separator char!");
	}
	
	
	/**
	 * Checks if there is an existing file with the given name and this file is a directory.<br>
	 * This method assumes that the given String contains the complete file path.
	 * @param fileName The file name (+path) to validate.
	 * @return A file reference for the given file name.
	 * @throws ParameterException if such a file does not exist or is not a directory.
	 */
	public static File directory(String fileName) {
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.directory(new File(fileName));
	}
	
	/**
	 * Checks if the given file exists and is a directory.
	 * @param file The file to validate.
	 * @return The validated file reference.
	 * @throws ParameterException if the file does not exist or is not a directory.
	 */
	public static File directory(File file) {
		Validate.exists(file);
		if(!file.isDirectory())
			throw new ParameterException("\""+file+"\" is not a directory!");
		return file;
	}
	
	/**
	 * Checks if there is an existing file with the given name and this file is NOT a directory.<br>
	 * This method assumes that the given String contains the complete file path.
	 * @param fileName The file name (+path) to validate.
	 * @return A file reference for the given file name.
	 * @throws ParameterException if such a file does not exist or is a directory.
	 */
	public static File noDirectory(String fileName) {
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.noDirectory(new File(fileName));
	}
	
	/**
	 * Checks if the given file exists and is not a directory.
	 * @param file The file to validate.
	 * @return The validated file reference.
	 * @throws ParameterException if the file exists or is a directory.
	 */
	public static File noDirectory(File file) {
		Validate.exists(file);
		if(file.isDirectory())
			throw new ParameterException("\""+file+"\" is a directory!");
		return file;
	}
	
	/**
	 * Checks if there is an existing file with the given name.<br>
	 * This method assumes that the given String contains the complete file path.
	 * @param fileName The file name (+path) to validate.
	 * @return A file reference for the given file name.
	 * @throws ParameterException if such a file does not exist.
	 */
	public static File exists(String fileName) {
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.exists(new File(fileName));
	}
	
	/**
	 * Checks if the given file exists.<br>
	 * @param file The file to validate.
	 * @return The validated file reference.
	 * @throws ParameterException if the file does not exist.
	 */
	public static File exists(File file) {
		Validate.notNull(file);
		if(!file.exists())
			throw new ParameterException("\""+file+"\" does not exist!");
		return file;
	}
	
	/**
	 * Checks if there is NOT an existing file with the given name.<br>
	 * This method assumes that the given String contains the complete file path.
	 * @param fileName The file name (+path) to validate.
	 * @return A file reference for the given file name.
	 * @throws ParameterException if such a file exists.
	 */
	public static File notExists(String fileName) {
		Validate.notNull(fileName);
		Validate.notEmpty(fileName);
		return Validate.notExists(new File(fileName));
	}
	
	/**
	 * Checks if the given file does not exist.<br>
	 * @param file The file to validate.
	 * @return The validated file reference.
	 * @throws ParameterException if the file exists.
	 */
	public static File notExists(File file) {
		Validate.notNull(file);
		if(file.exists())
			throw new ParameterException("\""+file+"\" exists!");
		return file;
	}
	
	public static final void dateInActualYear(Date date){
		if(!TimeUtils.inActualYear(date))
			throw new ParameterException("Date not in actual year");
	}
	
	public static final void dateInFuture(Date date){
		if(!date.after(TimeUtils.today()))
			throw new ParameterException("Date not in future");
	}
	
	public static final void dateInFuture(Date date, TimeValue maxDiffFromNow){
		Date maxDate = new Date();
		maxDate.setTime(TimeUtils.today().getTime() + maxDiffFromNow.getValueInMilliseconds());
		dateInFuture(date, maxDate);
	}
	
	public static final void dateInFuture(Date date, Date maxDate){
		dateInFuture(date);
		if(date.after(maxDate))
			throw new ParameterException("Date too far in future");
	}
	
	public static final void dateNotInFuture(Date date){
		Date today = TimeUtils.today();
		if(today.equals(date))
			return;
		if(date.after(today))
			throw new ParameterException("Date in future");
	}
	
	public static final void dateInPast(Date date, TimeValue maxDiffFromNow){
		Date minDate = new Date();
		minDate.setTime(TimeUtils.today().getTime() - maxDiffFromNow.getValueInMilliseconds());
		dateInFuture(date, minDate);
	}
	
	public static final void dateInPast(Date date, Date minDate){
		dateInFuture(date);
		if(date.before(minDate))
			throw new ParameterException("Date too far in past");
	}
	
	public static final void dateInPast(Date date){
		if(!date.before(TimeUtils.today()))
			throw new ParameterException("Date not in past");
	}
	
	public static final void dateNotInPast(Date date){
		Date today = TimeUtils.today();
		if(today.equals(date))
			return;
		if(date.before(today))
			throw new ParameterException("Date in past");
	}
	
	public static void main(String[] args)  {
		File f = new File("/Users/stocker/Eclipse/Workspace Uni/SWAT20/bin/classes/de/uni/freiburg/iig/telematik/swat/patterns/logic/patterns/ifnet");
		System.out.println(f.exists());
		File f2 = new File("/Users/stocker/Eclipse/Workspace%20Uni/SWAT20/bin/classes/de/uni/freiburg/iig/telematik/swat/patterns/logic/patterns/ifnet");
		System.out.println(f2.exists());
	}

}
