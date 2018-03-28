package xga;

import ec.EvolutionState;
import ec.util.Parameter;

public class HCSpecies extends XGASpecies
{
	private static final long serialVersionUID = 1L;
	public final static String P_MINMETAGENE = "min-meta-gene";
    public final static String P_MAXMETAGENE = "max-meta-gene";
    
	protected long[] minMetaGene;
	protected long[] maxMetaGene;
	
	protected int[] metaMask;
	private int lastGenThatResetMetaMask = -1;

	public long maxMetaGene(int gene) 
	{
		long[] m = maxMetaGene;
		if (m.length <= gene)
		{
			gene = m.length - 1;
		}
		return m[gene];
	}

	public long minMetaGene(int gene) {
		long[] m = minMetaGene;
		if (m.length <= gene)
		{
			gene = m.length - 1;
		}
		return m[gene];
	}

	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();

		// create the arrays
		minMetaGene = new long[genomeSize + 1];
		maxMetaGene = new long[genomeSize + 1];
		metaMask = new int[genomeSize / 2];

		// LOADING GLOBAL MIN/MAX GENES
		long _minMetaGene = state.parameters.getLongWithDefault(base.push(P_MINMETAGENE), def.push(P_MINMETAGENE), 0);
		long _maxMetaGene = state.parameters.getLong(base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE), _minMetaGene);
		
		if (_maxMetaGene < _minMetaGene)
		{
			state.output.fatal("HCSpecies must have a default min-meta-gene which is <= the default max-meta-gene", base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE));
		}
		
		fill(minMetaGene, _minMetaGene);
		fill(maxMetaGene, _maxMetaGene);

		// VERIFY
		for (int x = 0; x < genomeSize + 1; x++) 
		{
			if (maxMetaGene[x] < minMetaGene[x])
			{
				state.output.fatal("HCSpecies must have a min-gene[" + x + "] which is <= the max-gene[" + x + "]");
			}

			// check to see if these longs are within the data type of the particular
			// individual
			if (!inNumericalTypeRange(minMetaGene[x]))
			{
				state.output.fatal("This HCSpecies has a prototype of the kind: " + i_prototype.getClass().getName() + ", but doesn't have a min-meta-gene[" + x + "] value within the range of this prototype's genome's data types");
			}
			
			if (!inNumericalTypeRange(maxMetaGene[x]))
			{
				state.output.fatal("This HCSpecies has a prototype of the kind: " + i_prototype.getClass().getName() + ", but doesn't have a max-meta-gene[" + x + "] value within the range of this prototype's genome's data types");
			}
		}
		
		state.output.println(String.format("Generation from HCSpecies.setup: %d", state.generation),0);
		
	}
	
	public void resetMetaMask(EvolutionState state, int thread, HCIndividual ind)
	{
		if(state.generation != lastGenThatResetMetaMask)
		{
			lastGenThatResetMetaMask = state.generation;
			
			if((state.generation % 5) == 0)
			{
				for (int x = 0; x < metaMask.length; x++)
				{
					metaMask[x] = ind.randomValueFromClosedInterval((int)minMetaGene(x), (int)maxMetaGene(x), state.random[thread]);
				}
				state.output.println(String.format("Reset meta mask at generation: %d", state.generation),0);				
			}
		}
	}
}
