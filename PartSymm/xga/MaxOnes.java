/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package xga;


import ec.*;
import ec.simple.*;
import ec.vector.*;

public class MaxOnes extends Problem implements SimpleProblemForm
    {
	private static final long serialVersionUID = 1L;

	public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
        {
        if (ind.evaluated) return;

        if (!(ind instanceof IntegerVectorIndividual))
            state.output.fatal("Whoa!  It's not a IntegerVectorIndividual!!!",null);
        
        int sum=0;
        IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
        
        for(int x=0; x<ind2.genome.length; x++)
            sum += (ind2.genome[x]==1 ? 1 : 0);
        
        if (!(ind2.fitness instanceof XGAFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        ((XGAFitness)ind2.fitness).setFitness(state,
            /// ...the fitness...
            sum/(double)ind2.genome.length,
            ///... is the individual ideal?  Indicate here...
            sum == ind2.genome.length);
        ind2.evaluated = true;
        }
    }
