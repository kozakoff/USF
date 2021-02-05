package xga;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.vector.*;
import ec.util.Parameter;

public class RCXGAMirrorPipeline extends BreedingPipeline 
{
	private static final long serialVersionUID = 1L;
	public static final String P_MIRROR = "mirror";
    public static final int NUM_SOURCES = 1;
    
	@Override
	public Parameter defaultBase() { return VectorDefaults.base().push(P_MIRROR);	}

	@Override
	public int numSources() { return NUM_SOURCES; }
	
	@Override
	public int produce(final int min, final int max, final int start, final int subpopulation, final Individual[] inds, final EvolutionState state, final int thread) 
	{
		// grab individuals from our source and stick 'em right into inds.
		// we'll modify them from there
		int n = sources[0].produce(min, max, start, subpopulation, inds, state, thread);

		// should we bother?
		if (!state.random[thread].nextBoolean(likelihood))
		{
			return reproduce(n, start, subpopulation, inds, state, thread, false); // DON'T produce children from source, we already did
		}

		// clone the individuals if necessary
		if (!(sources[0] instanceof BreedingPipeline))
		{
			for (int q = start; q < n + start; q++)
			{
				inds[q] = (Individual) (inds[q].clone());
			}
		}

		// mirror 'em
		for (int q = start; q < n + start; q++) 
		{
			((RCXGAIndividual) inds[q]).mirror(state, thread);
			((VectorIndividual) inds[q]).evaluated = false;
		}

		return n;
	}

}
