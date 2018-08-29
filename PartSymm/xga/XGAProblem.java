package xga;

import ec.EvolutionState;
import ec.simple.*;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

public class XGAProblem extends ec.Problem implements SimpleProblemForm 
{
	private static final long serialVersionUID = 1L;
	public final static String P_FITNESSCALC = "fitness-calc";
	public final static String P_RRCHUNKSIZE = "rr-chunk-size";
	protected String fitnessCalc = "";
	protected int rrChunkSize = 0;
	
	
	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();

		// Should we use RR for fitness calculation?
		fitnessCalc = state.parameters.getStringWithDefault(base.push(P_FITNESSCALC), def.push(P_FITNESSCALC),"maxones");
		
		switch(fitnessCalc.toUpperCase())
		{
			case "RR":
				state.output.println("Using RR Fitness calculation.", 0);
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			case "RR2":
				state.output.println("Using RR2 Fitness calculation.", 0);
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			default:
				state.output.println("Using MaxOnes Fitness calculation.", 0);
				break;
		}
	}
	
	public void evaluate(final ec.EvolutionState state, final ec.Individual ind, final int subpopulation, final int threadnum) 
	{
		if (ind.evaluated)
		{
			return; // don't evaluate the individual if it's already evaluated
		}

		int[] genome = null, metagenes = null;
		int zeroCount = 0, oneCount = 0, twoCount = 0;
		double hammingDistance = 0.0, levenshteinDistance = 0.0, fitnessValue = 0.0;
		boolean isIdeal = false;
		
		ec.Individual ind2 = ind;
		
		//Gather some stats about meta genes for XGAIndividuals
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
			hammingDistance = ((XGAIndividual)ind2).getMetaGenesHammingDistanceFromMutation();
			levenshteinDistance = ((XGAIndividual)ind2).getMetaGenesLevenshteinDistanceFromMutation();
		}
		else if((ind instanceof IntegerVectorIndividual))
		{
			genome = ((IntegerVectorIndividual)ind2).genome;
			zeroCount = 0;
			oneCount = 0;
			twoCount = 0;
			hammingDistance = 0;
			levenshteinDistance = 0;
		}
		else
		{
			state.output.fatal("ERROR: XGAProblem requires either a IntegerVectorIndividual or XGAIndividual.",null);
		}


		//Calculate fitness depending on method
		try 
		{
			switch(fitnessCalc.toUpperCase())
			{
			case "RR":
				fitnessValue = calcRRFitness(genome, genome.length);
				break;
			case "RR2":
				fitnessValue = calcRR2Fitness(genome, genome.length*4);
				break;
			default:
				fitnessValue = calcMaxOnesFitness(genome, genome.length);
				break;
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
		
		isIdeal = (fitnessValue == 1 ? true : false); //Is the individual ideal?
		
		((XGAFitness) ind2.fitness).setFitness(state, fitnessValue, isIdeal);
		((XGAFitness) ind2.fitness).setMetaGenesHammingDistanceFromMutation(hammingDistance);
		((XGAFitness) ind2.fitness).setMetaGenesLevenshteinDistanceFromMutation(levenshteinDistance);
		((XGAFitness) ind2.fitness).metaGenesZeroCount = zeroCount;
		((XGAFitness) ind2.fitness).metaGenesOneCount = oneCount;
		((XGAFitness) ind2.fitness).metaGenesTwoCount = twoCount;	
		
		ind2.evaluated = true;
	}
	
	private double calcRR2Fitness(int[] g, int max) throws Exception
	{
		double sum = 0.0;
		int thisChunkSize = 0;
		
		if((g.length%rrChunkSize) != 0)
		{
			throw new Exception(String.format("genome-size needs to be evenly divisible by the parameter %s.", P_RRCHUNKSIZE));
		}
		else
		{
			thisChunkSize = rrChunkSize;
			while(thisChunkSize <= g.length)
			{
				for(int x = 0; x < g.length; x+=thisChunkSize)
				{
					boolean countChunk = true;
									
					for(int y = x; y < x+thisChunkSize; y++)
					{
						if(g[y] == 0) 
						{ 
							countChunk = false;
							break;
						}
					}
					
					if(countChunk)
					{
						sum += thisChunkSize;
					}
				}
				thisChunkSize *= 2;
			}
		}
		return sum/(double)max;
	}
	
//	private double calcRRRPFitness(int[] g, int max) throws Exception
//	{
//		double sum = 0.0;
//		int thisChunkSize = 0;
//		
//		if((g.length%rrChunkSize) != 0)
//		{
//			throw new Exception(String.format("genome-size needs to be evenly divisible by the parameter %s.", P_RRCHUNKSIZE));
//		}
//		else
//		{
//			thisChunkSize = rrChunkSize;
//			
//			for(int x = 0; x < g.length; x+=thisChunkSize)
//			{
//				boolean countChunk1 = false, countChunk2 = false;
//				
//				for(int y = x; y < x+thisChunkSize; y++)
//				{
//					int index1, index2;
//					
//					index1 = y;
//					index2 = (y + thisChunkSize) % g.length;
//					
//					if(g[index1] == 0) 
//					{ 
//						countChunk1 = false;
//						break;
//					}
//					
//					if(g[index2] == 1) 
//					{ 
//						countChunk2 = false;
//						break;
//					}
//				}
//				
//				if(countChunk1)
//				{
//					sum += thisChunkSize;
//				}
//			}
//			thisChunkSize *= 2;
//		}
//		return sum/(double)max;
//	}
	
	
	
	private double calcRRFitness(int[] g, int max) throws Exception
	{
		double sum = 0.0;
		
		if((g.length%rrChunkSize) != 0)
		{
			throw new Exception(String.format("genome-size needs to be evenly divisible by the parameter %s.", P_RRCHUNKSIZE));
		}
		else
		{
			for(int x = 0; x < g.length; x+=rrChunkSize)
			{
				boolean countChunk = true;
				for(int y = x; y < x+rrChunkSize; y++)
				{
					if(g[y] == 0) 
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
		return sum/(double)max;
	}
	
	private double calcMaxOnesFitness(int[] g, int max)
	{
		double sum = 0.0;
		for (int x = 0; x < g.length; x++)
		{
			sum += g[x];
		}
		return sum/(double)max;
	}
	
}
