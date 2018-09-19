package xga;

import ec.EvolutionState;
import ec.vector.IntegerVectorIndividual;

public abstract class XGAIndividual extends IntegerVectorIndividual 
{
	
	protected int metaGenesHammingDistanceFromMutation = 0;
	protected int metaGenesLevenshteinDistanceFromMutation = 0;
	protected int metaGenesHammingDistanceFromCrossover = 0;
	protected int metaGenesLevenshteinDistanceFromCrossover = 0;
	protected int metaGenesHammingDistanceFromMirror = 0;
	protected int metaGenesLevenshteinDistanceFromMirror = 0;
	
	protected int[] metaGenesBeforeMutation;
	protected int[] metaGenesAfterMutation;
	protected int[] metaGenesBeforeCrossover;
	protected int[] metaGenesAfterCrossover;
	protected int[] metaGenesBeforeMirror;
	protected int[] metaGenesAfterMirror;
	
	protected int genotypeHammingDistanceFromMutation = 0;
	protected int genotypeLevenshteinDistanceFromMutation = 0;
	protected int genotypeHammingDistanceFromCrossover = 0;
	protected int genotypeLevenshteinDistanceFromCrossover = 0;
	protected int genotypeHammingDistanceFromMirror = 0;
	protected int genotypeLevenshteinDistanceFromMirror = 0;
	
	protected int[] genotypeBeforeMutation;
	protected int[] genotypeAfterMutation;
	protected int[] genotypeBeforeCrossover;
	protected int[] genotypeAfterCrossover;
	protected int[] genotypeBeforeMirror;
	protected int[] genotypeAfterMirror;
	
	protected int phenotypeHammingDistanceFromMutation = 0;
	protected int phenotypeLevenshteinDistanceFromMutation = 0;
	protected int phenotypeHammingDistanceFromCrossover = 0;
	protected int phenotypeLevenshteinDistanceFromCrossover = 0;
	protected int phenotypeHammingDistanceFromMirror = 0;
	protected int phenotypeLevenshteinDistanceFromMirror = 0;
	
	protected int[] phenotypeBeforeMutation;
	protected int[] phenotypeAfterMutation;
	protected int[] phenotypeBeforeCrossover;
	protected int[] phenotypeAfterCrossover;
	protected int[] phenotypeBeforeMirror;
	protected int[] phenotypeAfterMirror;
	
	
	private static final long serialVersionUID = 1L;
	public abstract int[] getGenome();
	public abstract int[] getPhenome();
	public abstract int[] getMetas();
	public abstract void mirror(EvolutionState state, int thread);
	public abstract int getHammingDistance(int[] before, int[] after);
	public abstract int getLevenshteinDistance(int[] before, int[] after);

}
