package xga;

import ec.*;
import ec.util.*;

public class FGAIndividual extends XGAIndividual {
	
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
 
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
    }
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
		state.output.println(String.format("In the FGAIndividual reset"),0);
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
	
	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then,  
		//Meta gene values
		//2 - No meta gene present
		//1 - Flip meta
		//0 - Do not flip
		
		FGASpecies s = (FGASpecies) species;
		int currMetaGene, lastMetaGene = 0;
		boolean yesMirror = false;
		
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
					genome[x] = (genome[x+1]==1 ? 1 : 0);
				}
				else
				{
					genome[x] = (genome[x+1]==1 ? 0 : 1);
				}
			}
		}
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
		int currMetaGene, lastMetaGene = 0;
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
}

