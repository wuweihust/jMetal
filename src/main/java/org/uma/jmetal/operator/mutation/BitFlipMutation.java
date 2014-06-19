//  BitFlipMutation.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solutiontype as a single encoding.variable.
 */
public class BitFlipMutation extends Mutation {
  /**
   *
   */
  private static final long serialVersionUID = -3349165791496573889L;

  /**
   * Valid solutiontype types to apply this operator
   */
  private static final List<Class<? extends SolutionType>> VALID_TYPES =
    Arrays.asList(BinarySolutionType.class,
      BinaryRealSolutionType.class,
      IntSolutionType.class);

  private Double mutationProbability_ = null;

  /**
   * Constructor
   * Creates a new instance of the Bit Flip mutation operator
   */
  public BitFlipMutation(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation probability
   * @param solution    The solutiontype to mutate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void doMutation(double probability, Solution solution) throws JMetalException {
    try {
      if ((solution.getType().getClass() == BinarySolutionType.class) ||
        (solution.getType().getClass() == BinaryRealSolutionType.class)) {
        for (int i = 0; i < solution.getDecisionVariables().length; i++) {
          for (int j = 0;
               j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
            if (PseudoRandom.randDouble() < probability) {
              ((Binary) solution.getDecisionVariables()[i]).getBits().flip(j);
            }
          }
        }

        if (solution.getType().getClass() == BinaryRealSolutionType.class) {
        for (int i = 0; i < solution.getDecisionVariables().length; i++) {
          ((BinaryReal) solution.getDecisionVariables()[i]).decode();
        }
        }
      } else {
        for (int i = 0; i < solution.getDecisionVariables().length; i++) {
          if (PseudoRandom.randDouble() < probability) {
            int value = PseudoRandom.randInt(
              (int) solution.getDecisionVariables()[i].getLowerBound(),
              (int) solution.getDecisionVariables()[i].getUpperBound());
            solution.getDecisionVariables()[i].setValue(value);
          }
        }
      }
    } catch (ClassCastException e1) {
      Configuration.logger_.log(Level.SEVERE,
        "BitFlipMutation.doMutation: " +
          "ClassCastException error" + e1.getMessage(),
        e1
      );
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doMutation()");
    }
  }

  /**
   * Executes the operation
   *
   * @param object An object containing a solutiontype to mutate
   * @return An object containing the mutated solutiontype
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    Solution solution = (Solution) object;

    if (!VALID_TYPES.contains(solution.getType().getClass())) {
      Configuration.logger_.severe("BitFlipMutation.execute: the solutiontype " +
        "is not of the right type. The type should be 'Binary', " +
        "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    doMutation(mutationProbability_, solution);
    return solution;
  }
}