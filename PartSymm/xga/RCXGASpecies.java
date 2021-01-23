package xga;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.FloatVectorSpecies;

public class RCXGASpecies extends FloatVectorSpecies {
	private static final long serialVersionUID = 1L;

	public final static String P_MIRRORPROB = "mirror-prob";
	public final static String P_DEFMETAVAL = "default-meta-value";

	protected double mirrorProbability;
	protected int defaultMetaValue = -1;
	
	public void setup(final EvolutionState state, final Parameter base) 
	{
		super.setup(state, base);
		
		Parameter def = defaultBase();
		
		mirrorProbability = state.parameters.getDoubleWithMax(base.push(P_MIRRORPROB),def.push(P_MIRRORPROB),0.0,1.0);
        
		if (mirrorProbability==-1.0)
		{
			state.output.fatal("If it's going to use mirroring, XGASpecies must have a mirror probability between 0.0 and 1.0 inclusive",base.push(P_MIRRORPROB),def.push(P_MIRRORPROB));
		}
		
		defaultMetaValue = state.parameters.getIntWithDefault(base.push(P_DEFMETAVAL), def.push(P_DEFMETAVAL), -1);
        
		if (defaultMetaValue>1 || defaultMetaValue<-1)
		{
			state.output.fatal("The default meta value must be either 1, 0, or -1 (random 1 or 0)",base.push(P_DEFMETAVAL),def.push(P_DEFMETAVAL));
		}
	}
}
