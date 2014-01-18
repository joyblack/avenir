/*
 * avenir: Predictive analytic based on Hadoop Map Reduce
 * Author: Pranab Ghosh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.avenir.regress;

/**
 * @author pranab
 *
 */
public class LogisticRegressor {
	 private double[] coefficients;
	 private double[] aggregates;
	 private double[] coeffDiff;
	 private double convergeThreshold;
	 private String posClassVal;

	 public LogisticRegressor() {
	 }
	 
	/**
	 * @param coefficients
	 * @param posClassVal
	 */
	public LogisticRegressor(double[] coefficients, String posClassVal) {
		super();
		this.coefficients = coefficients;
		this.posClassVal = posClassVal;  
		aggregates = new double[coefficients.length];
		coeffDiff = new double[coefficients.length];
		for ( int i = 0; i < aggregates.length;  ++i) {
			aggregates[i] = 0;
		}
	}
	
	/**
	 * @param values
	 * @param classValue
	 */
	public void aggregate(int[] values, String classValue) {
		double sum = 0;
		for (int i = 0; i < values.length; ++i) {
			sum += values[i] * coefficients[i];
		}
		double classProbEst =  1.0 / (1.0 + Math.exp(-sum));
		double classProbActual = classValue.equals(posClassVal) ? 1.0 :  0;
		double classProbDiff = classProbActual - classProbEst;
		
		for (int i = 0; i < values.length; ++i) {
			aggregates[i] += values[i] * classProbDiff;
		}		
	}

	/**
	 * @return
	 */
	public double[] getAggregates() {
		return aggregates;
	}

	/**
	 * @param aggregates
	 */
	public void setAggregates(double[] aggregates) {
		this.aggregates = aggregates;
	}

	public void addAggregates(double[] aggregates) {
		if (null == this.aggregates) {
			this.aggregates = new double[aggregates.length];
			for (int i = 0; i < this.aggregates.length; ++i) {
				this.aggregates[i] = 0;
			}
		} 
		
		for (int i = 0; i < aggregates.length;  ++i ) {
			this.aggregates[i]  += aggregates[i];
		}
	}

	/**
	 * 
	 */
	public void updateCoefficients() {
		for (int i = 0; i < coefficients.length; ++i) {
			coeffDiff[i] =  (aggregates[i]  * 100.0) / coefficients[i];
			if (coeffDiff[i] < 0) {
				coeffDiff[i]  =  - coeffDiff[i];
			}
			coeffDiff[i] += aggregates[i];
		}
	}

	/**
	 * @param convergeThreshold
	 */
	public void setConvergeThreshold(double convergeThreshold) {
		this.convergeThreshold = convergeThreshold;
	}
	
	/**
	 * @return
	 */
	public boolean isAllConverged() {
		boolean converged = true;
		for (int i = 0; i <  coeffDiff.length; ++i) {
			if (coeffDiff[i] > convergeThreshold) {
				converged = false;
				break;
			}
		}
		return converged;
	}
	
	/**
	 * @return
	 */
	public boolean isAverageConverged() {
		boolean converged = true;
		double sum = 0;
		for (int i = 0; i <  coeffDiff.length; ++i) {
			sum += coeffDiff[i];
		}
		sum /= coeffDiff.length;
		converged = sum < convergeThreshold;
		return converged;
	}
	
}
