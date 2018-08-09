package xga;

import ec.EvolutionState;
import ec.vector.IntegerVectorIndividual;

public abstract class XGAIndividual extends IntegerVectorIndividual 
{
	private static final long serialVersionUID = 1L;
	public abstract int[] getGenome();
	public abstract int[] getPhenome();
	public abstract void mirror(EvolutionState state, int thread);
	public abstract int getMetaGenesHammingDistanceFromMutation();
	public abstract int getMetaGenesLevenshteinDistanceFromMutation();
}
