package xga;

import ec.*;
import ec.util.*;

public class FGAIndividual extends XGAIndividual {
	
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
	private int defMetaVal;
 
	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof FGASpecies))
		{
			state.output.fatal("FGAIndividual requires an FGASpecies", base, def);
		}
        
		FGASpecies s = (FGASpecies) species;
    
		genome = new int[s.genomeSize*2];
		metaGenesBeforeMutation = getMetas();
		metaGenesAfterMutation = getMetas();
    }
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
		//state.output.println(String.format("In the FGAIndividual reset"),0);
		FGASpecies s = (FGASpecies) species;
		for (int x = 0; x < genome.length; x++)
		{
			if((x % 2) == 0)
			{
				genome[x] = randomValueFromClosedInterval((int)s.minMetaGene(x), (int)s.maxMetaGene(x), state.random[thread]);
			}
			else
			{
				genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
			}
		}
	}
	
	public void defaultMutate(EvolutionState state, int thread) 
	{
//		metaGenesBeforeMutation = getMetas();
//		genotypeBeforeMutation = getGenome();
//		phenotypeBeforeMutation = getPhenome();
		
		FGASpecies s = (FGASpecies) species;
		for (int x = 0; x < genome.length; x++)
		{
			if (state.random[thread].nextBoolean(s.mutationProbability(x))) 
			{
				int old = genome[x];
				for (int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) 
				{
					switch (s.mutationType(x)) 
					{
					case FGASpecies.C_RESET_MUTATION:
						if((x % 2) == 0)
						{
							genome[x] = randomValueFromClosedInterval((int)s.minMetaGene(x), (int)s.maxMetaGene(x), state.random[thread]);
						}
						else
						{
							genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
						}
						
						break;
					case FGASpecies.C_RANDOM_WALK_MUTATION:
						int min, max;
						
						if((x % 2) == 0)
						{
							min = (int)s.minMetaGene(x);
							max = (int)s.maxMetaGene(x);
						}
						else
						{
							min = (int)s.minGene(x);
							max = (int)s.maxGene(x);
						}
						
						if (!s.mutationIsBounded(x)) 
						{
							max = Integer.MAX_VALUE;
							min = Integer.MIN_VALUE;
						}
						do 
						{
							int n = (int) (state.random[thread].nextBoolean() ? 1 : -1);
							int g = genome[x];
							if ((n == 1 && g < max) || (n == -1 && g > min))
								genome[x] = g + n;
							else if ((n == -1 && g < max) || (n == 1 && g > min))
								genome[x] = g - n;
						} while (state.random[thread].nextBoolean(s.randomWalkProbability(x)));
						break;
					default:
						state.output.fatal("In FGAIndividual.defaultMutate, default case occurred when it shouldn't have");
						break;
					}
					if (genome[x] != old)
					{
						break;
					}
				}
			}
		}
		
//		metaGenesAfterMutation = getMetas();
//		genotypeAfterMutation = getGenome();
//		phenotypeAfterMutation = getPhenome();
//		
//		metaGenesHammingDistanceFromMutation = getHammingDistance(metaGenesBeforeMutation,metaGenesAfterMutation);
//		metaGenesLevenshteinDistanceFromMutation = getLevenshteinDistance(metaGenesBeforeMutation,metaGenesAfterMutation);
//		genotypeHammingDistanceFromMutation = getHammingDistance(genotypeBeforeMutation,genotypeAfterMutation);
//		genotypeLevenshteinDistanceFromMutation = getLevenshteinDistance(genotypeBeforeMutation,genotypeAfterMutation);
//		phenotypeHammingDistanceFromMutation = getHammingDistance(phenotypeBeforeMutation,phenotypeAfterMutation);
//		phenotypeLevenshteinDistanceFromMutation = getLevenshteinDistance(phenotypeBeforeMutation,phenotypeAfterMutation);
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
		//metaGenesBeforeMirror = getMetas();
		//genotypeBeforeMirror = getGenome();
		//phenotypeBeforeMirror = getPhenome();
		
		//Probability, then,  
		//Meta gene values
		//2 - No meta gene present
		//1 - Flip meta
		//0 - Do not flip
		
		FGASpecies s = (FGASpecies) species;
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
			currMetaGene = genome[x];
			if(currMetaGene != 2)
			{
				lastMetaGene = genome[x];
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
					genome[x+1] = (genome[x+1]==1 ? 1 : 0);
				}
				else
				{
					genome[x+1] = (genome[x+1]==1 ? 0 : 1);
				}
			}
		}
		
		//metaGenesAfterMirror = getMetas();
		//genotypeAfterMirror = getGenome();
		//phenotypeAfterMirror = getPhenome();
		
		//metaGenesHammingDistanceFromMirror = getHammingDistance(metaGenesBeforeMirror,metaGenesAfterMirror);
		//metaGenesLevenshteinDistanceFromMirror = getLevenshteinDistance(metaGenesBeforeMirror,metaGenesAfterMirror);
		//genotypeHammingDistanceFromMirror = getHammingDistance(genotypeBeforeMirror,genotypeAfterMirror);
		//genotypeLevenshteinDistanceFromMirror = getLevenshteinDistance(genotypeBeforeMirror,genotypeAfterMirror);
		//phenotypeHammingDistanceFromMirror = getHammingDistance(phenotypeBeforeMirror,phenotypeAfterMirror);
		//phenotypeLevenshteinDistanceFromMirror = getLevenshteinDistance(phenotypeBeforeMirror,phenotypeAfterMirror);
	}
	
	public String genotypeToStringForHumans() 
	{
		StringBuilder m = new StringBuilder();
		StringBuilder s = new StringBuilder();
		StringBuilder t = new StringBuilder();
		
		m.append("Meta: ");
		s.append("Geno: ");
		t.append("Phen: ");
		
		for (int i = 0; i < genome.length; i+=2) 
		{
			m.append(genome[i]);
			s.append(genome[i+1]);
		}
		
		int[] thisPhenome = getPhenome();
		
		for (int i = 0; i < thisPhenome.length; i++) 
		{
			t.append(thisPhenome[i]);
		}
			
		m.append("\r\n");
		m.append(s);
		m.append("\r\n");
		m.append(t);
		m.append("\r\nMirror Prob String: ");
		
		if(mirrorString.length() > 0)
		{
			m.append(mirrorString.substring(0,mirrorString.length()-1));	
		}
		else
		{
			m.append(mirrorString);
		}
		
		return m.toString();
	}

	public int[] getMetas() 
	{
		int[] metas = new int[genome.length/2];

		for (int x = 0; x < genome.length; x+=2)
		{
			metas[x/2] = genome[(x)];
		}
		
		return metas;
	}
	
	public int[] getGenome() 
	{
		int[] thisGenome = new int[genome.length/2];

		for (int x = 0; x < thisGenome.length; x++)
		{
			thisGenome[x] = genome[(x*2)+1];
		}
		
		return thisGenome;
	}

	@Override
	public int[] getPhenome() 
	{
		int currMetaGene, lastMetaGene = defMetaVal;
		int[] phenome = new int[genome.length/2];
		
		for (int x = 0; x < genome.length; x+=2)
		{
			//In this loop x eq the meta gene and x+1 eq the actual gene 
			currMetaGene = genome[x];
			if(currMetaGene != 2)
			{
				lastMetaGene = genome[x];
			}
			
			if (lastMetaGene == 0) 
			{
				phenome[x / 2] = (genome[x + 1] == 1 ? 1 : 0);
			} 
			else 
			{
				phenome[x / 2] = (genome[x + 1] == 1 ? 0 : 1);
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
			currMetaGene = genome[i];
			if(currMetaGene != 2)
			{
				lastMetaGene = genome[i];
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

