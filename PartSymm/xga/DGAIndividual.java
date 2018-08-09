package xga;

import ec.EvolutionState;
import ec.util.Parameter;


public class DGAIndividual extends XGAIndividual 
{
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();
	private int metaGeneBeforeMutation;
	private int metaGeneAfterMutation;

	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof XGASpecies))
		{
			state.output.fatal("DGAIndividual requires an XGASpecies", base, def);
		}
        
		XGASpecies s = (XGASpecies) species;
    
		genome = new int[s.genomeSize+1];
    }
	
	public String genotypeToStringForHumans() 
	{
		StringBuilder m = new StringBuilder();
		StringBuilder s = new StringBuilder();
		
		m.append("Meta: ");
		s.append("Geno: ");
		
		m.append(genome[0]);
		
		for (int i = 1; i < genome.length; i++) 
		{
			s.append(genome[i]);
		}
			
		m.append("\r\n");
		m.append(s);
		m.append("\r\nMirror Prob String: ");
		m.append(mirrorString);
				
		return m.toString();
	}
	
	@Override
	public int[] getGenome() 
	{
		int[] thisGenome = new int[genome.length-1];
		
		for (int x = 1; x < genome.length; x++)
		{
			thisGenome[x-1] = genome[x];
		}
		
		return thisGenome;
	}

	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then, flip everything to opposite including meta gene.
		XGASpecies s = (XGASpecies) species;
		boolean yesMirror = state.random[thread].nextBoolean(s.mirrorProbability);
		
		mirrorString.setLength(0);
		mirrorString.append(yesMirror);
		
		if(yesMirror) 
		{
			if(genome[0]==1)
			{
				for (int x = 1; x < genome.length; x++)
				{
					genome[x] = (genome[x]==1 ? 0 : 1); //Flip all of the bits
				}	
			}
		}
	}

	public void defaultMutate(EvolutionState state, int thread) 
	{
		metaGeneBeforeMutation = genome[0];
		super.defaultMutate(state, thread);
		metaGeneAfterMutation = genome[0];
	}
	
	@Override
	public int[] getPhenome() 
	{
		int[] phenome = new int[genome.length-1];
		
		if(genome[0] == 1)
		{
			for (int x = 0; x < phenome.length; x++)
			{
				phenome[x] = (genome[x+1]==1 ? 0 : 1);
			}
		}
		else
		{
			System.arraycopy(genome, 1, phenome, 0, genome.length-1);
		}
		
		return phenome;
	}
	
	public int getMetaGenesHammingDistanceFromMutation()
	{
		int count = 0;
		if(metaGeneBeforeMutation != metaGeneAfterMutation) 
		{
			count++;
		}
		return count;
	}
	
	public int getMetaGenesLevenshteinDistanceFromMutation()
	{
		int count = 0;
		if(metaGeneBeforeMutation != metaGeneAfterMutation) 
		{
			count++;
		}
		return count;
	}

	
}
