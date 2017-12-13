package fga;

import ec.*;
import ec.vector.*;
import ec.util.*;
import java.io.*;

public class FGAIndividual extends BitVectorIndividual {
	
	private static final long serialVersionUID = 1L;
	public static final String P_BITVECTORINDIVIDUAL = "bit-vect-ind";
	public boolean[] genome;
	public int[] metaGenome;
	
	public Parameter defaultBase()
    {
		return VectorDefaults.base().push(P_BITVECTORINDIVIDUAL);
    }
	
	public void setup(final EvolutionState state, final Parameter base)
    {
		super.setup(state,base);  // actually unnecessary (Individual.setup() is empty)
		//state.output.systemMessage("Parameter: " + base);
		BitVectorSpecies s = (BitVectorSpecies)species;  // where my default info is stored
		genome = new boolean[s.genomeSize];
		metaGenome = new int[s.genomeSize];
    }
	 
	public Object clone()
    {
		FGAIndividual myobj = (FGAIndividual) (super.clone());
    
		// must clone the genome
		myobj.genome = (boolean[])(genome.clone());
		myobj.metaGenome = (int[])(metaGenome.clone());
    
		return myobj;
    } 
		
	/** Initializes the individual by randomly flipping the bits */
	public void reset(EvolutionState state, int thread) 
	{
		super.reset(state, thread);
		metaReset(state, thread);
	}
	
	public void metaReset(EvolutionState state, int thread) 
	{
		for (int x = 0; x < genome.length; x++)
		{
			metaGenome[x] = randomValueFromClosedInterval(0, 2, state.random[thread]);
		}
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
		else
		{
			return min + random.nextInt(max - min + 1);
		}
	}

	public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
	{
		super.defaultCrossover(state, thread, ind);
		VectorSpecies s = (VectorSpecies) species; // where my default info is stored
		FGAIndividual i = (FGAIndividual) ind;
		int point, tmpInt;

		int len = Math.min(metaGenome.length, i.metaGenome.length);
		if (len != metaGenome.length || len != i.metaGenome.length)
		{
			state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");
		}
		switch (s.crossoverType) 
		{
		case VectorSpecies.C_ONE_POINT:
			point = state.random[thread].nextInt((len / s.chunksize));
			for (int x = 0; x < point * s.chunksize; x++) {
				tmpInt = i.metaGenome[x];
				i.metaGenome[x] = metaGenome[x];
				metaGenome[x] = tmpInt;
			}
			break;
		case VectorSpecies.C_ONE_POINT_NO_NOP:
			point = state.random[thread].nextInt((len / s.chunksize) - 1) + 1; // so it goes from 1 .. len-1
			for (int x = 0; x < point * s.chunksize; x++) {
				tmpInt = i.metaGenome[x];
				i.metaGenome[x] = metaGenome[x];
				metaGenome[x] = tmpInt;
			}
			break;
		case VectorSpecies.C_TWO_POINT: {
			point = state.random[thread].nextInt((len / s.chunksize));
			int point0 = state.random[thread].nextInt((len / s.chunksize));
			if (point0 > point) {
				int p = point0;
				point0 = point;
				point = p;
			}
			for (int x = point0 * s.chunksize; x < point * s.chunksize; x++) {
				tmpInt = i.metaGenome[x];
				i.metaGenome[x] = metaGenome[x];
				metaGenome[x] = tmpInt;
			}
		}
			break;
		case VectorSpecies.C_TWO_POINT_NO_NOP: {
			point = state.random[thread].nextInt((len / s.chunksize));
			int point0 = 0;
			do {
				point0 = state.random[thread].nextInt((len / s.chunksize));
			} while (point0 == point); // NOP
			if (point0 > point) {
				int p = point0;
				point0 = point;
				point = p;
			}
			for (int x = point0 * s.chunksize; x < point * s.chunksize; x++) {
				tmpInt = i.metaGenome[x];
				i.metaGenome[x] = metaGenome[x];
				metaGenome[x] = tmpInt;
			}
		}
			break;
		case VectorSpecies.C_ANY_POINT:
			for (int x = 0; x < len / s.chunksize; x++)
				if (state.random[thread].nextBoolean(s.crossoverProbability))
					for (int y = x * s.chunksize; y < (x + 1) * s.chunksize; y++) {
						tmpInt = i.metaGenome[y];
						i.metaGenome[y] = metaGenome[y];
						metaGenome[y] = tmpInt;
					}
			break;
		default:
			state.output.fatal("In valid crossover type in BitVectorIndividual.");
			break;
		}
	}
	
	
	public void writeGenotype(final EvolutionState state, final DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(genome.length);
		for (int x = 0; x < genome.length; x++)
		{
			dataOutput.writeBoolean(genome[x]); // inefficient: booleans are written out as bytes
		}
		
		dataOutput.writeInt(metaGenome.length);
		for (int x = 0; x < metaGenome.length; x++)
		{
			dataOutput.writeInt(metaGenome[x]);
		}

	}
	
	public boolean equals(Object ind) {
		if (ind == null)
			return false;
		if (!(this.getClass().equals(ind.getClass())))
			return false; // SimpleRuleIndividuals are special.
		BitVectorIndividual i = (BitVectorIndividual) ind;
		if (genome.length != i.genome.length)
			return false;
		for (int j = 0; j < genome.length; j++)
			if (genome[j] != i.genome[j])
				return false;
		return true;
	}
}
