package fga;

import ec.simple.*;

public class FGAProblem extends ec.Problem implements SimpleProblemForm 
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

		if (!(ind instanceof FGAIndividual))
		{
			state.output.fatal("Whoa!  It's not a FGAIndividual!!!", null);
		}

		FGAIndividual ind2 = (FGAIndividual) ind;

		int sum = 0;
		for (int x = 0; x < ind2.genome.length; x++)
		{
			sum += ((ind2.genome[x])==1 ? 1 : 0);
		}

		if (!(ind2.fitness instanceof SimpleFitness))
		{
			state.output.fatal("Whoa!  It's not a SimpleFitness!!!", null);
		}

		((SimpleFitness) ind2.fitness).setFitness(state,
				// ...the fitness...
				((double) sum) / ind2.genome.length,
				// ... is the individual ideal? Indicate here...
				sum == ind2.genome.length);
		ind2.evaluated = true;
	}
	
}
