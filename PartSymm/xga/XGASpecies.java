package xga;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.IntegerVectorSpecies;

public class XGASpecies extends IntegerVectorSpecies 
{
	private static final long serialVersionUID = 1L;

	public final static String P_MIRRORPROB = "mirror-prob";

	protected double mirrorProbability;
	
	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();
		
		mirrorProbability = state.parameters.getDoubleWithMax(base.push(P_MIRRORPROB),def.push(P_MIRRORPROB),0.0,1.0);
        
		if (mirrorProbability==-1.0)
		{
			state.output.fatal("If it's going to use mirroring, XGASpecies must have a mirror probability between 0.0 and 0.1 inclusive",base.push(P_MIRRORPROB),def.push(P_MIRRORPROB));
		}
	}
}
