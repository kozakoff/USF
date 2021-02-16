package xga;

import ec.*;
import ec.util.*;
import ec.vector.FloatVectorSpecies;

public class RCFGAIndividual extends RCXGAIndividual {
	
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
	private int defMetaVal;
 
	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof RCFGASpecies))
		{
			state.output.fatal("RCFGAIndividual requires an RCFGASpecies", base, def);
		}
        
		RCFGASpecies s = (RCFGASpecies) species;
    
		genome = new double[s.genomeSize*2];
    }
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
		//state.output.println(String.format("In the RCFGAIndividual reset"),0);
		RCFGASpecies s = (RCFGASpecies) species;
		MersenneTwisterFast random = state.random[thread];
		for (int x = 0; x < genome.length; x++)
		{
			if((x % 2) == 0)
			{
				genome[x] = (double)(s.minMetaGene(x) + random.nextInt(s.maxMetaGene(x) - s.minMetaGene(x)));
			}
			else
			{
				genome[x] = (s.minGene(x) + random.nextDouble(true, true) * (s.maxGene(x) - s.minGene(x)));
			}
		}
	}
	
	public void defaultMutate(EvolutionState state, int thread) 
	{
		RCFGASpecies s = (RCFGASpecies) species;
		MersenneTwisterFast random = state.random[thread];
		for (int x = 0; x < genome.length; x++)
		{
			if (state.random[thread].nextBoolean(s.mutationProbability(x))) 
			{
				double old = genome[x];
				for (int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) 
				{
					if((x % 2) == 0) 
					{
						genome[x] = (double)(s.minMetaGene(x) + random.nextInt(s.maxMetaGene(x) - s.minMetaGene(x)));
					}
					else
					{
						switch(s.mutationType(x))
                        {
                        case RCFGASpecies.C_GAUSS_MUTATION:
                            gaussianMutation(state, random, (FloatVectorSpecies)s, x);
                            break;
                        case RCFGASpecies.C_POLYNOMIAL_MUTATION:
                            polynomialMutation(state, random, (FloatVectorSpecies)s, x);
                            break;
                        case RCFGASpecies.C_RESET_MUTATION:
                            floatResetMutation(random, (FloatVectorSpecies)s, x);
                            break;
                        case RCFGASpecies.C_INTEGER_RESET_MUTATION:
                            integerResetMutation(random, (FloatVectorSpecies)s, x);
                            break;
                        case RCFGASpecies.C_INTEGER_RANDOM_WALK_MUTATION:
                            integerRandomWalkMutation(random, (FloatVectorSpecies)s, x);
                            break;
                        default:
                            state.output.fatal("In DoubleVectorIndividual.defaultMutate, default case occurred when it shouldn't have");
                            break;
                        }
					}
					
					if (genome[x] != old)
					{
						break;
					}
				}
			}
		}
	}
	
	public void integerRandomWalkMutation(MersenneTwisterFast random, FloatVectorSpecies species, int index) {
		double min = species.minGene(index);
		double max = species.maxGene(index);
		if (!species.mutationIsBounded(index)) {
			// okay, technically these are still bounds, but we can't go beyond this without
			// weird things happening
			max = MAXIMUM_INTEGER_IN_DOUBLE;
			min = -(max);
		}
		do {
			int n = (int) (random.nextBoolean() ? 1 : -1);
			double g = Math.floor(genome[index]);
			if ((n == 1 && g < max) || (n == -1 && g > min))
				genome[index] = g + n;
			else if ((n == -1 && g < max) || (n == 1 && g > min))
				genome[index] = g - n;
		} while (random.nextBoolean(species.randomWalkProbability(index)));
	}

	public void integerResetMutation(MersenneTwisterFast random, FloatVectorSpecies species, int index) {
		int minGene = (int) Math.floor(species.minGene(index));
		int maxGene = (int) Math.floor(species.maxGene(index));
		genome[index] = randomValueFromClosedInterval(minGene, maxGene, random); // minGene + random.nextLong(maxGene -
																					// minGene + 1);
	}

	public void floatResetMutation(MersenneTwisterFast random, FloatVectorSpecies species, int index) {
		double minGene = species.minGene(index);
		double maxGene = species.maxGene(index);
		genome[index] = minGene + random.nextDouble(true, true) * (maxGene - minGene);
	}

	public void gaussianMutation(EvolutionState state, MersenneTwisterFast random, FloatVectorSpecies species,
			int index) {
		double val;
		double min = species.minGene(index);
		double max = species.maxGene(index);
		double stdev = species.gaussMutationStdev(index);
		int outOfBoundsLeftOverTries = species.outOfBoundsRetries;
		boolean givingUpAllowed = species.outOfBoundsRetries != 0;
		do {
			val = random.nextGaussian() * stdev + genome[index];
			outOfBoundsLeftOverTries--;
			if (species.mutationIsBounded(index) && (val > max || val < min)) {
				if (givingUpAllowed && (outOfBoundsLeftOverTries == 0)) {
					val = min + random.nextDouble() * (max - min);
					species.outOfRangeRetryLimitReached(state);// it better get inlined
					break;
				}
			} else
				break;
		} while (true);
		genome[index] = val;
	}

	public void polynomialMutation(EvolutionState state, MersenneTwisterFast random, FloatVectorSpecies species,
			int index) {
		double eta_m = species.mutationDistributionIndex(index);
		boolean alternativePolynomialVersion = species.polynomialIsAlternative(index);

		double rnd, delta1, delta2, mut_pow, deltaq;
		double y, yl, yu, val, xy;
		double y1;

		y1 = y = genome[index]; // ind[index];
		yl = species.minGene(index); // min_realvar[index];
		yu = species.maxGene(index); // max_realvar[index];
		delta1 = (y - yl) / (yu - yl);
		delta2 = (yu - y) / (yu - yl);

		int totalTries = species.outOfBoundsRetries;
		int tries = 0;
		for (tries = 0; tries < totalTries || totalTries == 0; tries++) // keep trying until totalTries is reached if
																		// it's not zero. If it's zero, go on forever.
		{
			rnd = random.nextDouble();
			mut_pow = 1.0 / (eta_m + 1.0);
			if (rnd <= 0.5) {
				xy = 1.0 - delta1;
				val = 2.0 * rnd
						+ (alternativePolynomialVersion ? (1.0 - 2.0 * rnd) * (Math.pow(xy, (eta_m + 1.0))) : 0.0);
				deltaq = Math.pow(val, mut_pow) - 1.0;
			} else {
				xy = 1.0 - delta2;
				val = 2.0 * (1.0 - rnd)
						+ (alternativePolynomialVersion ? 2.0 * (rnd - 0.5) * (Math.pow(xy, (eta_m + 1.0))) : 0.0);
				deltaq = 1.0 - (Math.pow(val, mut_pow));
			}
			y1 = y + deltaq * (yu - yl);
			if (!species.mutationIsBounded(index) || (y1 >= yl && y1 <= yu))
				break; // yay, found one
		}

		// at this point, if tries is totalTries, we failed
		if (totalTries != 0 && tries == totalTries) {
			// just randomize
			y1 = (double) (species.minGene(index)
					+ random.nextDouble(true, true) * (species.maxGene(index) - species.minGene(index))); // (double)(min_realvar[index]
																											// +
																											// random.nextDouble()
																											// *
																											// (max_realvar[index]
																											// -
																											// min_realvar[index]));
			species.outOfRangeRetryLimitReached(state);// it better get inlined
		}
		genome[index] = y1; // ind[index] = y1;
	}

	public void defaultCrossover(EvolutionState state, int thread, ec.vector.VectorIndividual ind)
	{
//		metaGenesBeforeCrossover = getMetas();
//		genotypeBeforeCrossover = getGenome();
//		phenotypeBeforeCrossover = getPhenome();
		
		super.defaultCrossover(state, thread, ind);
		
//		metaGenesAfterCrossover = getMetas();
//		genotypeAfterCrossover = getGenome();
//		phenotypeAfterCrossover = getPhenome();
//		
//		metaGenesHammingDistanceFromCrossover = getHammingDistance(metaGenesBeforeCrossover,metaGenesAfterCrossover);
//		metaGenesLevenshteinDistanceFromCrossover = getLevenshteinDistance(metaGenesBeforeCrossover,metaGenesAfterCrossover);
//		genotypeHammingDistanceFromCrossover = getHammingDistance(genotypeBeforeCrossover,genotypeAfterCrossover);
//		genotypeLevenshteinDistanceFromCrossover = getLevenshteinDistance(genotypeBeforeCrossover,genotypeAfterCrossover);
//		phenotypeHammingDistanceFromCrossover = getHammingDistance(phenotypeBeforeCrossover,phenotypeAfterCrossover);
//		phenotypeLevenshteinDistanceFromCrossover = getLevenshteinDistance(phenotypeBeforeCrossover,phenotypeAfterCrossover);
	}
	
	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then,  
		//Meta gene values
		//2 - No meta gene present
		//1 - Flip meta
		//0 - Do not flip
		
		RCFGASpecies s = (RCFGASpecies) species;
		int currMetaGene, lastMetaGene;
		boolean yesMirror = false;
		
		if(s.defaultMetaValue == -1)
		{
			lastMetaGene = randomValueFromClosedInterval(0, 1, state.random[thread]);
		}
		else
		{
			lastMetaGene = s.defaultMetaValue;
		}
		
		defMetaVal = lastMetaGene;
		
		mirrorString.setLength(0);

		for (int x = 0; x < genome.length; x+=2)
		{
			//In this loop x eq the meta gene and x+1 eq the actual gene 
			currMetaGene = (int)genome[x];
			if(currMetaGene != 2.0)
			{
				lastMetaGene = (int)genome[x];
				yesMirror = state.random[thread].nextBoolean(s.mirrorProbability);
				mirrorString.append((yesMirror ? "T" : "F"));
				mirrorString.append(",");
			}
			else
			{
				//No meta gene so keep lastMetaGene
			}
			
			if(yesMirror) 
			{
				if(lastMetaGene == 0)
				{
					//No change
					genome[x+1] = genome[x+1];
				}
				else
				{
					genome[x+1] = genome[x+1] * -1.0;
				}
			}
		}
	}
	
//	public String genotypeToStringForHumans() 
//	{
//		StringBuilder m = new StringBuilder();
//		StringBuilder s = new StringBuilder();
//		StringBuilder t = new StringBuilder();
//		
//		m.append("Meta: ");
//		s.append("Geno: ");
//		t.append("Phen: ");
//		
//		for (int i = 0; i < genome.length; i+=2) 
//		{
//			m.append(genome[i]);
//			s.append(genome[i+1]);
//		}
//		
//		int[] thisPhenome = getPhenome();
//		
//		for (int i = 0; i < thisPhenome.length; i++) 
//		{
//			t.append(thisPhenome[i]);
//		}
//			
//		m.append("\r\n");
//		m.append(s);
//		m.append("\r\n");
//		m.append(t);
//		m.append("\r\nMirror Prob String: ");
//		
//		if(mirrorString.length() > 0)
//		{
//			m.append(mirrorString.substring(0,mirrorString.length()-1));	
//		}
//		else
//		{
//			m.append(mirrorString);
//		}
//		
//		return m.toString();
//	}

	public int[] getMetas() 
	{
		int[] metas = new int[genome.length/2];

		for (int x = 0; x < genome.length; x+=2)
		{
			metas[x/2] = (int)genome[(x)];
		}
		
		return metas;
	}
	
	public double[] getGenome() 
	{
		double[] thisGenome = new double[genome.length/2];

		for (int x = 0; x < thisGenome.length; x++)
		{
			thisGenome[x] = genome[(x*2)+1];
		}
		
		return thisGenome;
	}

	@Override
	public double[] getPhenome() 
	{
		int currMetaGene, lastMetaGene = defMetaVal;
		double[] phenome = new double[genome.length/2];
		
		for (int x = 0; x < genome.length; x+=2)
		{
			//In this loop x eq the meta gene and x+1 eq the actual gene 
			currMetaGene = (int)genome[x];
			if(currMetaGene != 2)
			{
				lastMetaGene = (int)genome[x];
			}
			
			if(lastMetaGene == 0)
			{
				//No change
				phenome[x / 2] = genome[x+1];
			}
			else
			{
				phenome[x / 2] = genome[x+1] * -1.0;
			}
		}
		
		return phenome;
	}
	
	public int getHammingDistance(int[] before, int[] after)
	{
		int count = 0;
		for(int x = 0; x < before.length; x++)
		{
			if(before[x] != after[x]) 
			{
				count++;
			}
		}
		return count;
	}
	
	public int getLevenshteinDistance(int[] before, int[] after)
	{
		int size = before.length, i = size, j = size, subCost = 0;;
		int distanceMatrix[][] = new int[size][size];
		
		for(j = 0; j < size; j++)
		{
			for(i = 0; i < size; i++)
			{
				distanceMatrix[i][j] = 0;
			}
		}
		
		for(i = 0; i< size; i++) 
		{
			distanceMatrix[i][0] = i;
			distanceMatrix[0][i] = i;
		}
		
		for(j = 1; j < size; j++)
		{
			for(i = 1; i < size; i++)
			{
				if(before[i] == after[j]) 
				{
					subCost = 0;
				}
				else
				{
					subCost = 1;
				}
				distanceMatrix[i][j] = Math.min(Math.min(
							distanceMatrix[i-1][j]+1, 			//deletion 
							distanceMatrix[i][j-1]+1),			//insertion
							distanceMatrix[i-1][j-1]+subCost);	//substitution
			}
		}
		
		return distanceMatrix[size-1][size-1];
	}

	@Override
	public int[] getMetagenesTranslation() {
		
		int[] translated = new int[genome.length/2];
		int currMetaGene, lastMetaGene = 0;
		
		for(int i = 0; i < genome.length; i+=2)
		{
			currMetaGene = (int)genome[i];
			if(currMetaGene != 2)
			{
				lastMetaGene = (int)genome[i];
			}
			else
			{
				//No meta gene so keep lastMetaGene
			}
			
			translated[i/2] = lastMetaGene;
		}
		
		return translated;
	}

}

