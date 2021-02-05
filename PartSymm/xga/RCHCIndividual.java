package xga;

import java.util.Arrays;

import ec.*;
import ec.util.*;

public class RCHCIndividual extends RCXGAIndividual {
	
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
	//private int lastGeneration = -1;
	private int defMetaVal;
	public int[] metagenes;
	
	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)
		
		Parameter def = defaultBase();
    
		if (!(species instanceof RCHCSpecies))
		{
			state.output.fatal("HCIndividual requires an RCHCSpecies", base, def);
		}
        
		RCHCSpecies s = (RCHCSpecies) species;
    
		metagenes = new int[s.genomeSize];
    }
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{	
		super.reset(state, thread);
		
		HCEvolutionState thisState = (HCEvolutionState)state;

		for(int x = 0; x < metagenes.length; x++)
		{
			metagenes[x] = thisState.metamask[x];
		}
	}
	
	public String getArrayString(double[] a)
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
		metagenes = thisState.metamask;
	}
	
	private void checkIfPhenotypesEq(double[] p1,double[] p2) throws Exception
	{
		if(!Arrays.equals(p1,p2))
		{
			throw new Exception("Phenotypes are not equal!!");
		}
	}
	
//	private void setGenotypeMetasToTwos()
//	{
//		for(int x = 0; x < metagenes.length; x++) { metagenes[x] = 2; }
//	}
//	
//	private void fixGenes(double[] OriginalPhenotype, HCEvolutionState state)
//	{
//		double[] newPhenotype = getPhenome();
// 
//		for (int x = 0; x < OriginalPhenotype.length; x++)
//		{
//			if(newPhenotype[x] != OriginalPhenotype[x])
//			{
//				genome[(x*2)+1] = (genome[(x*2)+1]==1 ? 0 : 1);
//			}
//		}
//		
//		try
//		{
//			checkIfPhenotypesEq(OriginalPhenotype, getPhenome());
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			state.output.fatal(String.format("Org Phenotype: %s\r\nNew Phenotype: %s", getArrayString(OriginalPhenotype), getArrayString(getPhenome())));
//			System.exit(100);
//		}
//	}
	

	public void defaultCrossover(EvolutionState state, int thread, ec.vector.VectorIndividual ind)
	{
		super.defaultCrossover(state, thread, ind);
	}
	
	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then,  
		//Meta gene values
		//2 - No meta gene present
		//1 - Flip meta
		//0 - Do not flip
		
		RCHCSpecies s = (RCHCSpecies) species;
		
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

		for (int x = 0; x < genome.length; x++)
		{
			//In this loop x eq the meta gene and x+1 eq the actual gene 
			currMetaGene = metagenes[x];
			if(currMetaGene != 2)
			{
				lastMetaGene = metagenes[x];
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
					genome[x] = genome[x];
				}
				else
				{
					genome[x] = genome[x] * -1.0;
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
		
		for (int i = 0; i < genome.length; i++) 
		{
			m.append(metagenes[i]);
			s.append(genome[i]+",");
		}
		
		double[] thisPhenome = getPhenome();
		
		for (int i = 0; i < thisPhenome.length; i++) 
		{
			t.append(thisPhenome[i]+",");
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
		return metagenes;
	}
	
	public double[] getGenome() 
	{
		return genome;
	}

	@Override
	public double[] getPhenome() 
	{
		int currMetaGene, lastMetaGene = defMetaVal;
		double[] phenome = new double[genome.length];
		
		for (int x = 0; x < metagenes.length; x++)
		{
			//In this loop x eq the meta gene and x eq the actual gene 
			currMetaGene = metagenes[x];
			if(currMetaGene != 2)
			{
				lastMetaGene = metagenes[x];
			}
			
			if (lastMetaGene == 0) 
			{
				phenome[x] = genome[x];
			} 
			else 
			{
				phenome[x] = genome[x] * -1.0;
			}
		}
		
		return phenome;
	}

	@Override
	public int[] getMetagenesTranslation() {
		
		int[] translated = new int[metagenes.length];
		int currMetaGene, lastMetaGene = 0;
		
		for(int i = 0; i < metagenes.length; i+=2)
		{
			currMetaGene = metagenes[i];
			if(currMetaGene != 2)
			{
				lastMetaGene = metagenes[i];
			}
			else
			{
				//No meta gene so keep lastMetaGene
			}
			
			translated[i] = lastMetaGene;
		}
		
		return translated;
	}

}
