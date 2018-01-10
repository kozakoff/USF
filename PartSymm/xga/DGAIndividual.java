package xga;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.*;

public class DGAIndividual extends XGAIndividual {

	private static final long serialVersionUID = 1L;

	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof IntegerVectorSpecies))
		{
			state.output.fatal("DGAIndividual requires an IntegerVectorSpecies", base, def);
		}
        
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    
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
		//m.append("\r\n");
		
		return m.toString();
	}
	
	@Override
	public int[] getGenome() 
	{
		int[] thisGenome = new int[genome.length-1];
		
		for (int x = 1; x < genome.length; x++)
		{
			thisGenome[x-1] = genome[x]; //Use bits as they are stored
		}
		
		return thisGenome;
	}

	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then, flip everything to opposite including meta gene.
		if(genome[0]==1)
		{
			for (int x = 1; x < genome.length; x++)
			{
				genome[x] = (genome[x]==1 ? 0 : 1); //Flip all of the bits
			}	
		}
		else
		{
			//Use bits as they are stored
		}
	}
	
}
