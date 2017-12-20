package xga;

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

		if (!(ind instanceof XGAIndividual))
		{
			state.output.fatal("Whoa!  It's not a FGAIndividual!!!", null);
		}

		XGAIndividual ind2 = (XGAIndividual) ind;
		int lastMetaGene = 0;

		double sum = 0.0;
		for (int x = 0; x < ind2.genome.length; x+=2)
		{
			//In this loop x eq the meta gene and x+1 eq the actual gene 
			if(ind2.genome[x] != 2)
			{
				lastMetaGene = ind2.genome[x];
			}
			
			switch(lastMetaGene)
			{
			case 0: //Inactive meta gene, use the bit as-is for fitness calculation
				sum += ((ind2.genome[x+1])==1 ? 1 : 0);
				break;
			case 1: //Active meta gene, flip the bit for fitness calculation
				sum += ((ind2.genome[x+1])==1 ? 0 : 1);
				break;
			case 2: //No meta gene
				//This never happens
				break;
			}
		}

		if (!(ind2.fitness instanceof SimpleFitness))
		{
			state.output.fatal("Whoa!  It's not a SimpleFitness!!!", null);
		}
		
		double genomeLength = ((double) ind2.genome.length / 2.0);
		double fitnessValue = sum / genomeLength; //The fitness value
		boolean isIdeal = (sum == genomeLength ? true : false); //Is the individual ideal?  

		((SimpleFitness) ind2.fitness).setFitness(state, fitnessValue, isIdeal);
		ind2.evaluated = true;
	}
	
}
