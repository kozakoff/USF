package xga;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class HCSpecies extends XGASpecies
{
	private static final long serialVersionUID = 1L;
	public final static String P_MINMETAGENE = "min-meta-gene";
    public final static String P_MAXMETAGENE = "max-meta-gene";
    public final static String P_MINMETAMASKGENE = "min-metamask-gene";
    public final static String P_MAXMETAMASKGENE = "max-metamask-gene";
    
	protected long[] minMetaGene;
	protected long[] maxMetaGene;
	protected long minMetamaskGene;
	protected long maxMetamaskGene;
	protected int[] metamask;
	
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
		metamask = new int[genomeSize];

		// LOADING GLOBAL MIN/MAX GENES
		long _minMetaGene = state.parameters.getLongWithDefault(base.push(P_MINMETAGENE), def.push(P_MINMETAGENE), 0);
		long _maxMetaGene = state.parameters.getLong(base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE), _minMetaGene);
		
		if (_maxMetaGene < _minMetaGene)
		{
			state.output.fatal("HCSpecies must have a default min-meta-gene which is <= the default max-meta-gene", base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE));
		}
		
		fill(minMetaGene, _minMetaGene);
		fill(maxMetaGene, _maxMetaGene);
		
		// LOADING GLOBAL MIN/MAX GENES
		minMetamaskGene = state.parameters.getLongWithDefault(base.push(P_MINMETAMASKGENE), def.push(P_MINMETAMASKGENE), 0);
		maxMetamaskGene = state.parameters.getLong(base.push(P_MAXMETAMASKGENE), def.push(P_MAXMETAMASKGENE), minMetamaskGene);

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
		
		//state.output.println(String.format("Generation from HCSpecies.setup: %d", state.generation),0);
		initMetamask(state, 0);
	}
	
	private void initMetamask(EvolutionState state, int thread)
	{
		for(int x = 0; x < metamask.length; x++)
		{
			metamask[x] = randomValueFromClosedInterval((int)minMetamaskGene, (int)maxMetamaskGene, state.random[thread]);
		}
		//state.output.println(String.format("Metamask initialized."),0);
	}
	
	/**
	 * Returns a random value from between min and max inclusive. This method
	 * handles overflows that complicate this computation. Does NOT check that min
	 * is less than or equal to max. You must check this yourself.
	 */
	public int randomValueFromClosedInterval(int min, int max, MersenneTwisterFast random)
	{
		if (max - min < 0) // we had an overflow
		{
			int l = 0;
			do
				l = random.nextInt();
			while (l < min || l > max);
			return l;
		}
		else return min + random.nextInt(max - min + 1);
	}

}
