package xga;

import ec.EvolutionState;
import ec.simple.*;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

public class XGAProblem extends ec.Problem implements SimpleProblemForm 
{
	private static final long serialVersionUID = 1L;
	public final static String P_RRFITNESSCALC = "rr-fitness-calc";
	public final static String P_RRCHUNKSIZE = "rr-chunk-size";
	protected boolean useRRFitnessCalc = false;
	protected int rrChunkSize = 0;
	
	
	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();

		// Should we use RR for fitness calculation?
		useRRFitnessCalc = state.parameters.getBoolean(base.push(P_RRFITNESSCALC), def.push(P_RRFITNESSCALC), false);
		
		if(useRRFitnessCalc)
		{
			state.output.println("Using RR Fitness calculation.", 0);
			rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
		}
		else
		{
			state.output.println("Using MaxOnes Fitness calculation.", 0);
		}
	}
	
	public void evaluate(final ec.EvolutionState state, final ec.Individual ind, final int subpopulation, final int threadnum) 
	{
		if (ind.evaluated)
		{
			return; // don't evaluate the individual if it's already evaluated
		}

		int[] genome = null;
		int[] metagenes = null;
		int zeroCount = 0, oneCount = 0, twoCount = 0;
		ec.Individual ind2 = ind;
		
		if (ind instanceof XGAIndividual)
		{
			genome = ((XGAIndividual)ind2).getPhenome();
			metagenes = ((XGAIndividual)ind2).getMetas();
			
			for(int x = 0; x < metagenes.length; x++)
			{
				switch(metagenes[x])
				{
					case 0:
						zeroCount++;
						break;
					case 1:
						oneCount++;
						break;
					case 2:
						twoCount++;
						break;
				}
			}
		}
		else if((ind instanceof IntegerVectorIndividual))
		{
			genome = ((IntegerVectorIndividual)ind2).genome;
		}
		else
		{
			state.output.fatal("ERROR: XGAProblem requires either a IntegerVectorIndividual or XGAIndividual.",null);
		}

		double sum = 0.0;
		
		try 
		{
			if(useRRFitnessCalc)
			{
				if((genome.length%rrChunkSize) != 0)
				{
					throw new Exception(String.format("genome-size needs to be evenly divisible by the parameter %s.", P_RRCHUNKSIZE));
				}
				else
				{
					for(int x = 0; x < genome.length; x+=rrChunkSize)
					{
						boolean countChunk = true;
						for(int y = x; y < x+rrChunkSize; y++)
						{
							if(genome[y] == 0) 
							{ 
								countChunk = false;
								break;
							}
						}
						
						if(countChunk)
						{
							sum += rrChunkSize;
						}
					}
				}
			}
			else
			{
				for (int x = 0; x < genome.length; x++)
				{
					sum += genome[x];
				}
			}
		}
		catch(Exception ex) {
			state.output.fatal(ex.getMessage(), null);
			System.exit(1);
		}

		if (!(ind2.fitness instanceof XGAFitness))
		{
			state.output.fatal("Whoa!  It's not a XGAFitness!!!", null);
		}
		
		double genomeLength = (double) genome.length;
		double fitnessValue = sum / genomeLength; //The fitness value
		boolean isIdeal = (sum == genomeLength ? true : false); //Is the individual ideal?
		

		((XGAFitness) ind2.fitness).setFitness(state, fitnessValue, isIdeal);
		((XGAFitness) ind2.fitness).setMetaGenesHammingDistanceFromMutation(((XGAIndividual)ind2).getMetaGenesHammingDistanceFromMutation());
		((XGAFitness) ind2.fitness).setMetaGenesLevenshteinDistanceFromMutation(((XGAIndividual)ind2).getMetaGenesLevenshteinDistanceFromMutation());
		((XGAFitness) ind2.fitness).metaGenesZeroCount = zeroCount;
		((XGAFitness) ind2.fitness).metaGenesOneCount = oneCount;
		((XGAFitness) ind2.fitness).metaGenesTwoCount = twoCount;
		
		//((XGAFitness) ind2.fitness).setFitness(state, fitnessValue, isIdeal);
		//state.output.println(String.format("Generation: %d, Fitness: %2f", state.generation, fitnessValue), 0);
		ind2.evaluated = true;
	}
}
