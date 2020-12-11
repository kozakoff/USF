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
	
	public static final String V_RR = "rr";
	public static final String V_RR2 = "rr2";
	public static final String V_RRRP1 = "rrrp1";
	public static final String V_RRRP2 = "rrrp2";
	public static final String V_SPHERE = "sphere";
    public static final String V_ROSENBROC = "rosenbroc";
    public static final String V_SCHWEFEL = "schwefel";
    public static final String V_RASTARIGIN = "rastarigin";
    public static final String V_ACKLEY = "ackley";
    public static final String V_MPB = "mpb";
    public static final String V_INV_SPHERE = "inv-sphere";   
    public static final String V_DEJONG_N5 = "dejongn5";
    public static final String V_INV_DEJONG_N5 = "inv-dejongn5";
    
    public static final int PROB_SPHERE = 1;
    public static final int PROB_ROSENBROC = 2;
    public static final int PROB_SCHWEFEL = 3;
    public static final int PROB_RASTARIGIN = 4;
    public static final int PROB_ACKLEY = 5;
       
    public static final int PROB_INV_SPHERE = 6;    
    public static final int PROB_DEJONG_N5 = 7;
    public static final int PROB_INV_DEJONG_N5 = 8;
    public static final int PROB_MPB = 9;
	protected String fitnessCalc = "";
	protected int rrChunkSize = 0;
	
	
	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();

		// Should we use RR for fitness calculation?
		fitnessCalc = state.parameters.getStringWithDefault(base.push(P_FITNESSCALC), def.push(P_FITNESSCALC),"maxones");
		
		switch(fitnessCalc.toLowerCase())
		{
			case V_RR:
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			case V_RR2:
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			case V_RRRP1:
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			case V_RRRP2:
				rrChunkSize = state.parameters.getInt(base.push(P_RRCHUNKSIZE), def.push(P_RRCHUNKSIZE), 0);
				break;
			default:
				state.output.println("Using MaxOnes Fitness calculation.", 0);
				break;
		}
		
		state.output.println(String.format("Using %s Fitness calculation and Chunk Size %s.", fitnessCalc.toUpperCase(), (rrChunkSize == 0 ? "NA" : rrChunkSize)), 0);
		
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
			hammingDistance = ((XGAIndividual)ind2).getHammingDistance(((XGAIndividual)ind2).metaGenesBeforeMutation,((XGAIndividual)ind2).metaGenesAfterMutation);
			levenshteinDistance = ((XGAIndividual)ind2).getLevenshteinDistance(((XGAIndividual)ind2).metaGenesBeforeMutation,((XGAIndividual)ind2).metaGenesAfterMutation);
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
			case V_RR:
				fitnessValue = calcRRFitness(genome, genome.length);
				break;
			case V_RR2:
				fitnessValue = calcRR2Fitness(genome, genome.length*4);
				break;
			case V_RRRP1:
				fitnessValue = calcRRRPFitness(genome, genome.length, false, state);
				break;
			case V_RRRP2:
				fitnessValue = calcRRRPFitness(genome, genome.length + rrChunkSize, true, state);
				break;
			case V_SPHERE:
				
				break;
			case V_ROSENBROC:
				
				break;
			case V_SCHWEFEL:
	
				break;
			case V_RASTARIGIN:
	
				break;
			case V_ACKLEY:
	
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

		//state.output.println(String.format("fitnessValue is %f at gen %d", fitnessValue, state.generation), 0);
		
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
	
	private double calcSphere(double[] g) throws Exception
	{
		double value = 0;
		int len = g.length;
		for( int i = 0 ; i < len ; i++ ) {
            double gi = g[i] ;
            value += (gi * gi);
          }
		return -value;
	}
	
	private double calcRosenbrock() throws Exception
	{
		
		return 0.0;
	}
	
	private double calcSchwefel() throws Exception
	{
		
		return 0.0;
	}
	
	private double calcRatrigin() throws Exception
	{
		
		return 0.0;
	}
	
	private double calcAckley() throws Exception
	{
		
		return 0.0;
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
	
	private double calcRRRPFitness(int[] g, int max, boolean positional, final ec.EvolutionState state) throws Exception
	{
		double sum = 0.0;
		int thisChunkSize = 0;
		boolean startsWith1 = true;
		
		if((g.length%rrChunkSize) != 0)
		{
			throw new Exception(String.format("genome-size needs to be evenly divisible by the parameter %s.", P_RRCHUNKSIZE));
		}
		else
		{
			thisChunkSize = rrChunkSize;
			
			for(int x = 0; x < g.length; x+=thisChunkSize)
			{
				boolean countChunk01 = true, countChunk10 = true;
				
				for(int y = x; y < x+thisChunkSize; y++)
				{
					int index1, index2;
					
					index1 = y;
					index2 = (y + thisChunkSize) % g.length;
					
					if(y < thisChunkSize) 
					{
						if(g[index1] != 1) 
						{
							startsWith1 = false;
						}
					}
					
					if(g[index1] != 0 || g[index2] != 1) 
					{ 
						countChunk01 = false;
						//state.output.println(String.format("countChunk01 is %b at gen %d", countChunk01, state.generation), 0);
					}
					
					if(g[index1] != 1 || g[index2] != 0) 
					{ 
						countChunk10 = false;
						//state.output.println(String.format("countChunk10 is %b at gen %d", countChunk10, state.generation), 0);
					}
				}
				
				if(countChunk01 || countChunk10)
				{
					//state.output.println(String.format("countChunk01 is %b and countChunk10 is %b at gen %d", countChunk01, countChunk10, state.generation), 0);
					sum += thisChunkSize;
				}
				//state.output.println(String.format("sum is %.2f", sum), 0);
			}
			//thisChunkSize *= 2;
		}
		
		if(positional) {
			if(startsWith1) 
			{
				sum += thisChunkSize;
			}
		}
		
		return sum/(double)max;
	}
	
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
