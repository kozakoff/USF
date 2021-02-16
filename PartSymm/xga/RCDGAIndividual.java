package xga;

import ec.EvolutionState;
import ec.util.Parameter;


public class RCDGAIndividual extends RCXGAIndividual 
{
	private static final long serialVersionUID = 1L;
	private StringBuilder mirrorString = new StringBuilder();

	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)

		Parameter def = defaultBase();
    
		if (!(species instanceof RCXGASpecies))
		{
			state.output.fatal("RCDGAIndividual requires an RCXGASpecies", base, def);
		}
        
		RCXGASpecies s = (RCXGASpecies) species;
    
		genome = new double[s.genomeSize+1];
    }
	
	public void reset(EvolutionState state, int thread)  
	{
		super.reset(state, thread);
		if((int)genome[0] <= 0) 
		{
			genome[0] = 0.0;
		}
		else 
		{
			genome[0] = 1.0;
		}
	}
	
//	public String genotypeToStringForHumans() 
//	{
//		//StringBuilder m = new StringBuilder();
//		//StringBuilder s = new StringBuilder();
//		
//		//m.append("Meta: ");
//		//s.append("Geno: ");
//		
//		//m.append(genome[0]);
//		
//		for (int i = 1; i < genome.length; i++) 
//		{
//			s.append(genome[i]);
//		}
//			
//		//m.append("\r\n");
//		//m.append(s);
//		//m.append("\r\nMirror Prob String: ");
//		//m.append(mirrorString);
//				
//		//return m.toString();
//	}
	
	@Override
	public double[] getGenome() 
	{
		double[] thisGenome = new double[genome.length-1];
		
		for (int x = 1; x < genome.length; x++)
		{
			thisGenome[x-1] = genome[x];
		}
		
		return thisGenome;
	}

	public void mirror(EvolutionState state, int thread)
	{
		//Probability, then, flip everything to opposite including meta gene.
		RCXGASpecies s = (RCXGASpecies) species;
		boolean yesMirror = state.random[thread].nextBoolean(s.mirrorProbability);
		
		mirrorString.setLength(0);
		mirrorString.append(yesMirror);
		
		if(yesMirror) 
		{
			if((int)genome[0]==1)
			{
				for (int x = 1; x < genome.length; x++)
				{
					genome[x] = genome[x] * -1.0;
					//genome[x] = (genome[x]==1 ? 0 : 1); //Flip all of the bits
				}	
			}
		}
	}

	public void defaultMutate(EvolutionState state, int thread) 
	{
		super.defaultMutate(state, thread);
		if((int)genome[0] <= 0) 
		{
			genome[0] = 0.0;
		}
		else 
		{
			genome[0] = 1.0;
		}
	}
	
	public void defaultCrossover(EvolutionState state, int thread, ec.vector.VectorIndividual ind)
	{
		super.defaultCrossover(state, thread, ind);
	}
	
	@Override
	public double[] getPhenome() 
	{
		double[] phenome = new double[genome.length-1];
		
		if((int)genome[0] == 1)
		{
			for (int x = 0; x < phenome.length; x++)
			{
				phenome[x] = genome[x] * -1.0;
			}
		}
		else
		{
			System.arraycopy(genome, 1, phenome, 0, genome.length-1);
		}
		
		return phenome;
	}
	
	public int getHammingDistance(int[] before, int[] after)
	{
		int count = 0;
		if(before != after) 
		{
			count++;
		}
		return count;
	}
	
	public int getLevenshteinDistance(int[] before, int[] after)
	{
		int count = 0;
		if(before != after) 
		{
			count++;
		}
		return count;
	}

	@Override
	public int[] getMetas() {
		// TODO Auto-generated method stub
		return new int[(int)genome[0]];
	}

	@Override
	public int[] getMetagenesTranslation() {
		
		int[] translated = new int[1];

		translated[0] = (int)genome[0];
	
		return translated;
	}
}
