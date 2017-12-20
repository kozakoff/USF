package xga;

import ec.simple.*;
import ec.vector.*;

public class DGAProblem extends ec.Problem implements SimpleProblemForm 
{
	// ind is the individual to be evaluated.
	// We're given state and threadnum primarily so we
	// have access to a random number generator
	// (in the form: state.random[threadnum] )
	// and to the output facility

	private static final long serialVersionUID = 1L;
	
	public void evaluate(final ec.EvolutionState state, final ec.Individual ind, final int subpopulation, final int threadnum) 
	{
		if (ind.evaluated)
		{
			return; // don't evaluate the individual if it's already evaluated
		}

		if (!(ind instanceof BitVectorIndividual))
		{
			state.output.fatal("Whoa!  It's not a BitVectorIndividual!!!", null);
		}

		BitVectorIndividual ind2 = (BitVectorIndividual) ind;

		double sum = 0.0;
		
		if(ind2.genome[0])
		{
			for (int x = 1; x < ind2.genome.length; x++)
			{
				sum += (ind2.genome[x] ? 0 : 1); //Flip all of the bits
			}	
		}
		else
		{
			for (int x = 1; x < ind2.genome.length; x++)
			{
				sum += (ind2.genome[x] ? 1 : 0); //Use bits as they are stored
			}
		}

		if (!(ind2.fitness instanceof SimpleFitness))
		{
			state.output.fatal("Whoa!  It's not a SimpleFitness!!!", null);
		}
		
		double genomeLength = (double) ind2.genome.length-1;
		double fitnessValue = sum / genomeLength; //The fitness value
		boolean isIdeal = (sum == genomeLength ? true : false); //Is the individual ideal?  

		((SimpleFitness) ind2.fitness).setFitness(state, fitnessValue, isIdeal);
		ind2.evaluated = true;
	}
	
}
