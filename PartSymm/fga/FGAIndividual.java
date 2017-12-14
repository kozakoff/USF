package fga;

import ec.*;
import ec.vector.*;
import ec.util.*;
import java.io.*;

public class FGAIndividual extends IntegerVectorIndividual {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes the individual by randomly choosing Integers uniformly from
	 * mingene to maxgene.
	 */
	public void reset(EvolutionState state, int thread) 
	{
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		for (int x = 0; x < genome.length; x++)
		{
			if((x % 2) == 0)
			{
				genome[x] = randomValueFromClosedInterval(0, 1, state.random[thread]);
			}
			else
			{
				genome[x] = randomValueFromClosedInterval(0, 2, state.random[thread]);
			}
			
		}
	}
}
