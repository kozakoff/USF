package xga;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class DGADoubleVectorIndividual extends DoubleVectorIndividual {
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();

	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof XGAFloatVectorSpecies))
		{
			state.output.fatal("DGAIndividual requires an XGAFloatVectorSpecies", base, def);
		}
        
		XGAFloatVectorSpecies s = (XGAFloatVectorSpecies) species;
    
		genome = new double[s.genomeSize+1];
    }
	
	public double[] getGenotype() 
	{
		double[] genotype = new double[genome.length-1];
		
		for (int x = 1; x < genome.length; x++)
		{
			genotype[x-1] = genome[x];
		}
		
		return genotype;
	}
	
	public double[] getPhenotype() 
	{
		double[] phenotype = new double[genome.length-1];
		
		if(genome[0] >= 0.0)
		{
			for (int x = 0; x < phenotype.length; x++)
			{
				phenotype[x] = (genome[x+1] >= 0.0 ? genome[x+1] * -1 : genome[x+1]);
			}
		}
		else
		{
			System.arraycopy(genome, 1, phenotype, 0, genome.length-1);
		}
		
		return phenotype;
	}

	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then, flip everything to opposite including meta gene.
		XGAFloatVectorSpecies s = (XGAFloatVectorSpecies) species;
		boolean yesMirror = state.random[thread].nextBoolean(s.mirrorProbability);
		
		mirrorString.setLength(0);
		mirrorString.append(yesMirror);
		
		if(yesMirror) 
		{
			if(genome[0] >= 0.0)
			{
				for (int x = 1; x < genome.length; x++)
				{
					genome[x] = (genome[x] >= 0.0 ? genome[x] * -1 : genome[x]); //Flip all of the bits
				}	
			}
		}
	}
}
