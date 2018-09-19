package xga;

import ec.EvolutionState;
import ec.util.Parameter;


public class DGAIndividual extends XGAIndividual 
{
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
		metaGenesBeforeMirror = getMetas();
		genotypeBeforeMirror = getGenome();
		phenotypeBeforeMirror = getPhenome();
		
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
		
		metaGenesAfterMirror = getMetas();
		genotypeAfterMirror = getGenome();
		phenotypeAfterMirror = getPhenome();
		
		metaGenesHammingDistanceFromMirror = getHammingDistance(metaGenesBeforeMirror,metaGenesAfterMirror);
		metaGenesLevenshteinDistanceFromMirror = getLevenshteinDistance(metaGenesBeforeMirror,metaGenesAfterMirror);
		genotypeHammingDistanceFromMirror = getHammingDistance(genotypeBeforeMirror,genotypeAfterMirror);
		genotypeLevenshteinDistanceFromMirror = getLevenshteinDistance(genotypeBeforeMirror,genotypeAfterMirror);
		phenotypeHammingDistanceFromMirror = getHammingDistance(phenotypeBeforeMirror,phenotypeAfterMirror);
		phenotypeLevenshteinDistanceFromMirror = getLevenshteinDistance(phenotypeBeforeMirror,phenotypeAfterMirror);
	}

	public void defaultMutate(EvolutionState state, int thread) 
	{
		metaGenesBeforeMutation = getMetas();
		genotypeBeforeMutation = getGenome();
		phenotypeBeforeMutation = getPhenome();
		
		super.defaultMutate(state, thread);
		
		metaGenesAfterMutation = getMetas();
		genotypeAfterMutation = getGenome();
		phenotypeAfterMutation = getPhenome();
		
		metaGenesHammingDistanceFromMutation = getHammingDistance(metaGenesBeforeMutation,metaGenesAfterMutation);
		metaGenesLevenshteinDistanceFromMutation = getLevenshteinDistance(metaGenesBeforeMutation,metaGenesAfterMutation);
		genotypeHammingDistanceFromMutation = getHammingDistance(genotypeBeforeMutation,genotypeAfterMutation);
		genotypeLevenshteinDistanceFromMutation = getLevenshteinDistance(genotypeBeforeMutation,genotypeAfterMutation);
		phenotypeHammingDistanceFromMutation = getHammingDistance(phenotypeBeforeMutation,phenotypeAfterMutation);
		phenotypeLevenshteinDistanceFromMutation = getLevenshteinDistance(phenotypeBeforeMutation,phenotypeAfterMutation);
	}
	
	public void defaultCrossover(EvolutionState state, int thread, ec.vector.VectorIndividual ind)
	{
		metaGenesBeforeCrossover = getMetas();
		genotypeBeforeCrossover = getGenome();
		phenotypeBeforeCrossover = getPhenome();
		
		super.defaultCrossover(state, thread, ind);
		
		metaGenesAfterCrossover = getMetas();
		genotypeAfterCrossover = getGenome();
		phenotypeAfterCrossover = getPhenome();
		
		metaGenesHammingDistanceFromCrossover = getHammingDistance(metaGenesBeforeCrossover,metaGenesAfterCrossover);
		metaGenesLevenshteinDistanceFromCrossover = getLevenshteinDistance(metaGenesBeforeCrossover,metaGenesAfterCrossover);
		genotypeHammingDistanceFromCrossover = getHammingDistance(genotypeBeforeCrossover,genotypeAfterCrossover);
		genotypeLevenshteinDistanceFromCrossover = getLevenshteinDistance(genotypeBeforeCrossover,genotypeAfterCrossover);
		phenotypeHammingDistanceFromCrossover = getHammingDistance(phenotypeBeforeCrossover,phenotypeAfterCrossover);
		phenotypeLevenshteinDistanceFromCrossover = getLevenshteinDistance(phenotypeBeforeCrossover,phenotypeAfterCrossover);
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
		return new int[genome[0]];
	}

	
}
