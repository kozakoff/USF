package xga;

import ec.*;
import ec.vector.*;
//import ec.util.*;
//import java.io.*;

public class FGAIndividual extends IntegerVectorIndividual {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
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
	}
	
	public String genotypeToStringForHumans() 
	{
		StringBuilder m = new StringBuilder();
		StringBuilder s = new StringBuilder();
		StringBuilder p = new StringBuilder();
		
		m.append("Meta: ");
		s.append("Geno: ");
		p.append("Phen: ");
		
		int currMetaGene, lastMetaGene = 0;
		
		for (int i = 0; i < genome.length; i+=2) 
		{
			m.append(genome[i]);
			s.append(genome[i+1]);
			
			//In this loop i eq the meta gene and i+1 eq the actual gene 
			currMetaGene = genome[i];
			if(currMetaGene != 2)
			{
				lastMetaGene = genome[i];
			}
		
			if(lastMetaGene == 0)
			{
				p.append(genome[i+1]==1 ? "1" : "0");
			}
			else
			{
				p.append(genome[i+1]==1 ? "0" : "1");
			}
		}
		
		m.append("\r\n");
		m.append(s);
		m.append("\r\n");
		m.append(p);
		
		return m.toString();
	}
}
