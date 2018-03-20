package xga;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import java.util.*;

public class HCSpecies extends XGASpecies
{
	private static final long serialVersionUID = 1L;

	public final static String P_MINMETAGENE = "min-meta-gene";
    public final static String P_MAXMETAGENE = "max-meta-gene";
    public final static String P_NUMBEROFMETAMASKS = "number-of-metamasks";
    
	protected long minMetaGene;
	protected long maxMetaGene;
	protected long numberOfMetamasks;
	protected int[] currentMetaMask;
	protected Stack<int[]> metaGenome = new Stack<int[]>();
	protected int[] thisGenome;

	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();

		// LOADING GLOBAL MIN/MAX GENES
		long minMetaGene = state.parameters.getLongWithDefault(base.push(P_MINMETAGENE), def.push(P_MINMETAGENE), 0);
		long maxMetaGene = state.parameters.getLong(base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE), minMetaGene);
		long numberOfMetamasks = state.parameters.getLongWithDefault(base.push(P_MINMETAGENE), def.push(P_MINMETAGENE), 1);
		
		if (maxMetaGene < minMetaGene)
		{
			state.output.fatal("HCSpecies must have a default min-meta-gene which is <= the default max-meta-gene", base.push(P_MAXMETAGENE), def.push(P_MAXMETAGENE));
		}
		
		// VERIFY
		if (maxMetaGene < minMetaGene) 
		{
			state.output.fatal("HCpecies must have a min-gene[" + minMetaGene + "] which is <= the max-gene[" + maxMetaGene + "]");
		}

		// check to see if these longs are within the data type of the particular
		// individual
		if (!inNumericalTypeRange(minMetaGene)) 
		{
			state.output.fatal("This HCSpecies has a prototype of the kind: " + i_prototype.getClass().getName()
					+ ", but doesn't have a min-meta-gene[" + minMetaGene
					+ "] value within the range of this prototype's genome's data types");
		}

		if (!inNumericalTypeRange(maxMetaGene)) 
		{
			state.output.fatal("This HCSpecies has a prototype of the kind: " + i_prototype.getClass().getName()
					+ ", but doesn't have a max-meta-gene[" + maxMetaGene
					+ "] value within the range of this prototype's genome's data types");
		}
		
		thisGenome = new int[genomeSize];
		
		for (int x = 0; x < numberOfMetamasks; x++)
		{
			for (int y = 0; y < genomeSize; y++)
			{
				thisGenome[y] = randomValueFromClosedInterval((int)minMetaGene, (int)maxMetaGene, state.random[0]);
			}
			metaGenome.push(thisGenome);
		}
		
		currentMetaMask = metaGenome.pop();
	}
	
	public int randomValueFromClosedInterval(int min, int max, MersenneTwisterFast random)
	{
		if (max - min < 0) // we had an overflow
		{
			int l = 0;
			do
			{
				l = random.nextInt();
			}
			while (l < min || l > max);
			return l;
		}
		else
		{
			return min + random.nextInt(max - min + 1);
		}
	}
}
