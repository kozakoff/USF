package xga;

import ec.EvolutionState;
import ec.util.Parameter;

public class DGAIndividual extends XGAIndividual {

	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();

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
			else
			{
				//Use bits as they are stored
			}
		}
	}

	@Override
	public int[] getPhenome() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
