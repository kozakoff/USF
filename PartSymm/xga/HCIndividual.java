package xga;

import java.util.Arrays;

import ec.*;
import ec.util.*;

public class HCIndividual extends XGAIndividual {
	
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
	
	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)
		
		Parameter def = defaultBase();
    
		if (!(species instanceof HCSpecies))
		{
			state.output.fatal("HCIndividual requires an HCSpecies", base, def);
		}
        
		HCSpecies s = (HCSpecies) species;
    
		genome = new int[s.genomeSize*2];
    }
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
		HCEvolutionState thisState = (HCEvolutionState)state;
		HCSpecies s = (HCSpecies) species;
		String m = "";
		int[] phenOld, phenNew;
		
		setGenotypeMetasToTwos();
		
		m = getArrayString(thisState.metamask);
		//state.output.println(String.format("         Metamask: %s", m),0);
		
		m = getArrayString(getMetas(state));
		//state.output.println(String.format("       Meta genes: %s", m),0);
		
		phenOld = getPhenome();
		
		combineMetamaskAndGenotypeMetas(thisState,s);
		
		m = getArrayString(getMetas(state));
		//state.output.println(String.format("Masked Meta Genes: %s", m),0);
		
		m = getArrayString(phenOld);
		//state.output.println(String.format(" Phenotype B4 Fix: %s", m),0);
		
		fixGenes(phenOld, thisState);
		
		phenNew = getPhenome();
		
		m = getArrayString(phenNew);
		//state.output.println(String.format("    New Phenotype: %s", m),0);
	}
	
	public String getArrayString(int[] a)
	{
		StringBuilder m = new StringBuilder();
		for(int x = 0; x < a.length; x++)
		{
			m.append(a[x]);
		}
		return m.toString();
	}
	
	public void resetMetas(EvolutionState state, int thread)
	{
		HCEvolutionState thisState = (HCEvolutionState)state;
		HCSpecies s = (HCSpecies) species;
		String m = "";
		int[] phenOld, phenNew;
		
		phenOld = getPhenome();
		
		setGenotypeMetasToTwos();
		
		m = getArrayString(thisState.metamask);
		//state.output.println(String.format("         Metamask: %s", m),0);
		
		m = getArrayString(getMetas(state));
		//state.output.println(String.format("       Meta genes: %s", m),0);
		
		m = getArrayString(getGenome());
		//state.output.println(String.format("  Genotype B4 Rst: %s", m),0);
		
		m = getArrayString(phenOld);
		//state.output.println(String.format(" Phenotype B4 Rst: %s", m),0);
		
		fixGenes(phenOld, thisState);
		
		phenNew = getPhenome();
		m = getArrayString(phenNew);
		//state.output.println(String.format("Phenotype Aft Rst: %s", m),0);
		
		m = getArrayString(getGenome());
		//state.output.println(String.format(" Genotype Aft Rst: %s", m),0);
		
		phenOld = phenNew;
		
		m = getArrayString(phenOld);
		//state.output.println(String.format(" Phenotype B4 Fix: %s", m),0);
		
		combineMetamaskAndGenotypeMetas(thisState,s);
		
		m = getArrayString(getMetas(state));
		//state.output.println(String.format("Masked Meta Genes: %s", m),0);
		
		fixGenes(phenOld, thisState);
		
		phenNew = getPhenome();
		m = getArrayString(phenNew);
		//state.output.println(String.format("    New Phenotype: %s", m),0);
	}
	
	private void checkIfPhenotypesEq(int[] p1,int[] p2) throws Exception
	{
		if(!Arrays.equals(p1,p2))
		{
			throw new Exception("Phenotypes are not equal!!");
		}
	}
	
	private void setGenotypeMetasToTwos()
	{
		for(int x = 0; x < genome.length; x+=2) { genome[x] = 2; }
	}
	
	private void fixGenes(int[] OriginalPhenotype, HCEvolutionState state)
	{
		int[] newPhenotype = getPhenome();
 
		for (int x = 0; x < OriginalPhenotype.length; x++)
		{
			if(newPhenotype[x] != OriginalPhenotype[x])
			{
				genome[(x*2)+1] = (genome[(x*2)+1]==1 ? 0 : 1);
			}
		}
		
		try
		{
			checkIfPhenotypesEq(OriginalPhenotype, getPhenome());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			state.output.fatal(String.format("Org Phenotype: %s\r\nNew Phenotype: %s", getArrayString(OriginalPhenotype), getArrayString(getPhenome())));
			System.exit(100);
		}
	}
	
	private void combineMetamaskAndGenotypeMetas(HCEvolutionState thisState, HCSpecies species)
	{
		for (int x = 0; x < genome.length; x+=2)
		{
			if(thisState.metamask[x/2] == 1)
			{
				genome[x] = randomValueFromClosedInterval((int)species.minMetaGene(x), (int)species.maxMetaGene(x), thisState.random[0]);
			}
			else
			{
				genome[x] = 2;
			}
		}
	}
	
	public void defaultMutate(EvolutionState state, int thread) 
	{
		HCSpecies s = (HCSpecies) species;
		for (int x = 0; x < genome.length; x++)
		{
			if (state.random[thread].nextBoolean(s.mutationProbability(x))) 
			{
				int old = genome[x];
				for (int retries = 0; retries < s.duplicateRetries(x) + 1; retries++) 
				{
					switch (s.mutationType(x)) 
					{
					case HCSpecies.C_RESET_MUTATION:
						if((x % 2) == 0)
						{
							if(genome[x] != 2)
							{
								genome[x] = randomValueFromClosedInterval((int)s.minMetaGene(x), (int)s.maxMetaGene(x), state.random[thread]);
							}
						}
						else
						{
							genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
						}
						
						break;
					case HCSpecies.C_RANDOM_WALK_MUTATION:
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
						state.output.fatal("In HCIndividual.defaultMutate, default case occurred when it shouldn't have");
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
		
		HCSpecies s = (HCSpecies) species;
		
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

	public int[] getMetas(EvolutionState state) 
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

