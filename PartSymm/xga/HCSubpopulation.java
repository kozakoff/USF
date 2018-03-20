/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package xga;

import ec.*;
import ec.util.*;

public class HCSubpopulation extends Subpopulation
{
	private static final long serialVersionUID = 1L;
	public int[][] metaGenome;
	public 
	
	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		HCSpecies s = (HCSpecies) species;
		
		metaGenome = new int[individuals.length][s.genomeSize];
		
		for (int x = 0; x < individuals.length; x++)
		{
			for (int y = 0; y < s.genomeSize; y++)
			{
				metaGenome[x][y] = randomValueFromClosedInterval((int)s.minMetaGene(x), (int)s.maxMetaGene(x), state.random[0]);
			}	
		}
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
