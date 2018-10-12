package xga;

import ec.*;
import java.io.*;
import ec.util.*;

public class XGAShortStatistics extends Statistics
{
	private static final long serialVersionUID = 1L;
	public static final String P_STATISTICS_MODULUS = "modulus";
	public static final String P_COMPRESS = "gzip";
	public static final String P_FULL = "gather-full";
	public static final String P_DO_SIZE = "do-size";
	public static final String P_DO_TIME = "do-time";
	public static final String P_DO_SUBPOPS = "do-subpops";
	public static final String P_STATISTICS_FILE = "file";
	
	public static final int GENOME_SIZE = 64;

	public int statisticslog = 0; // stdout by default
	public int metagenesLog = 0; // stdout by default
	public int metagenesStringLog = 0; // stdout by default
	public int translatedMetagenesLog = 0;
	public int translatedMetagenesStringLog = 0;
	public int genotypesLog = 0; // stdout by default
	public int genotypesStringLog = 0; // stdout by default
	public int phenotypesLog = 0; // stdout by default
	public int phenotypesStringLog = 0; // stdout by default
	public int metamaskLog = 0; // stdout by default
	public int metamaskStringLog = 0; // stdout by default
	public int modulus;
	public boolean doSize;
	public boolean doTime;
	public boolean doSubpops;

	public Individual[] bestSoFar;
	public long[] totalSizeSoFar;
	public long[] totalIndsSoFar;
	public long[] totalIndsThisGen; // total assessed individuals
	public long[] totalSizeThisGen; // per-subpop total size of individuals this generation
	public double[] totalFitnessThisGen; // per-subpop mean fitness this generation
	public Individual[] bestOfGeneration; // per-subpop best individual this generation
	public double[] totalMetagenesHammingDistanceFromMutationThisGen; // per-subpop total hamming distance this generation
	public double[] totalMetagenesLevenshteinDistanceFromMutationThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalMetagenesHammingDistanceFromCrossoverThisGen; // per-subpop total hamming distance this generation
	public double[] totalMetagenesLevenshteinDistanceFromCrossoverThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalMetagenesHammingDistanceFromMirrorThisGen; // per-subpop total hamming distance this generation
	public double[] totalMetagenesLevenshteinDistanceFromMirrorThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalMetagenesHammingDistanceFromResetThisGen; // per-subpop total hamming distance this generation
	public double[] totalMetagenesLevenshteinDistanceFromResetThisGen; // per-subpop total levenshtein distance this generation
	
	public double[] totalGenotypeHammingDistanceFromMutationThisGen; // per-subpop total hamming distance this generation
	public double[] totalGenotypeLevenshteinDistanceFromMutationThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalGenotypeHammingDistanceFromCrossoverThisGen; // per-subpop total hamming distance this generation
	public double[] totalGenotypeLevenshteinDistanceFromCrossoverThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalGenotypeHammingDistanceFromMirrorThisGen; // per-subpop total hamming distance this generation
	public double[] totalGenotypeLevenshteinDistanceFromMirrorThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalGenotypeHammingDistanceFromResetThisGen; // per-subpop total hamming distance this generation
	public double[] totalGenotypeLevenshteinDistanceFromResetThisGen; // per-subpop total levenshtein distance this generation
	
	public double[] totalPhenotypeHammingDistanceFromMutationThisGen; // per-subpop total hamming distance this generation
	public double[] totalPhenotypeLevenshteinDistanceFromMutationThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalPhenotypeHammingDistanceFromCrossoverThisGen; // per-subpop total hamming distance this generation
	public double[] totalPhenotypeLevenshteinDistanceFromCrossoverThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalPhenotypeHammingDistanceFromMirrorThisGen; // per-subpop total hamming distance this generation
	public double[] totalPhenotypeLevenshteinDistanceFromMirrorThisGen; // per-subpop total levenshtein distance this generation
	public double[] totalPhenotypeHammingDistanceFromResetThisGen; // per-subpop total hamming distance this generation
	public double[] totalPhenotypeLevenshteinDistanceFromResetThisGen; // per-subpop total levenshtein distance this generation
	
	public int[] totalZeroCountThisGen;
	public int[] totalOneCountThisGen;
	public int[] totalTwoCountThisGen;
	
	// timings
	public long lastTime;

	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);
		File metagenesFile = new File(statisticsFile.getParent()+"/metagenes.txt");
		File metagenesStringFile = new File(statisticsFile.getParent()+"/metagenes_string.txt");
		File translatedMetagenesFile = new File(statisticsFile.getParent()+"/translatedmetagenes.txt");
		File translatedMetagenesStringFile = new File(statisticsFile.getParent()+"/translatedmetagenes_string.txt");
		File genotypesFile = new File(statisticsFile.getParent()+"/genotypes.txt");
		File genotypesStringFile = new File(statisticsFile.getParent()+"/genotypes_string.txt");
		File phenotypesFile = new File(statisticsFile.getParent()+"/phenotypes.txt");
		File phenotypesStringFile = new File(statisticsFile.getParent()+"/phenotypes_string.txt");
		File metamaskFile = new File(statisticsFile.getParent()+"/metamask.txt");
		File metamaskStringFile = new File(statisticsFile.getParent()+"/metamask_string.txt");
		
		modulus = state.parameters.getIntWithDefault(base.push(P_STATISTICS_MODULUS), null, 1);

		if (silentFile)
		{
			statisticslog = Output.NO_LOGS;
		}
		else if (statisticsFile != null)
		{
			try
			{
				statisticslog = state.output.addLog(statisticsFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				metagenesLog = state.output.addLog(metagenesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				metagenesStringLog = state.output.addLog(metagenesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				genotypesLog = state.output.addLog(genotypesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				genotypesStringLog = state.output.addLog(genotypesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				phenotypesLog = state.output.addLog(phenotypesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				phenotypesStringLog = state.output.addLog(phenotypesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				translatedMetagenesLog = state.output.addLog(translatedMetagenesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				translatedMetagenesStringLog = state.output.addLog(translatedMetagenesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				metamaskLog = state.output.addLog(metamaskFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				metamaskStringLog=state.output.addLog(metamaskStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
			}
			catch (IOException i)
			{
				state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
			}
		}
		else state.output.warning("No statistics file specified, printing to stdout at end.", base.push(P_STATISTICS_FILE));

		doSize = state.parameters.getBoolean(base.push(P_DO_SIZE), null, false);
		doTime = state.parameters.getBoolean(base.push(P_DO_TIME), null, false);
		if (state.parameters.exists(base.push(P_FULL), null))
		{
			state.output.warning(P_FULL + " is deprecated.  Use " + P_DO_SIZE + " and " + P_DO_TIME + " instead.  Also be warned that the table columns have been reorganized. ", base.push(P_FULL), null);
			boolean gather = state.parameters.getBoolean(base.push(P_FULL), null, false);
			doSize = doSize || gather;
			doTime = doTime || gather;
		}
		doSubpops = state.parameters.getBoolean(base.push(P_DO_SUBPOPS), null, false);
	}

	public Individual[] getBestSoFar()
	{
		return bestSoFar;
	}

	public void preInitializationStatistics(final EvolutionState state)
	{
		super.preInitializationStatistics(state);
		boolean output = (state.generation % modulus == 0);

		if (output && doTime)
		{
			// Runtime r = Runtime.getRuntime();
			lastTime = System.currentTimeMillis();
		}
	}

	public void postInitializationStatistics(final EvolutionState state)
	{
		super.postInitializationStatistics(state);
		boolean output = (state.generation % modulus == 0);

		// set up our bestSoFar array -- can't do this in setup, because
		// we don't know if the number of subpopulations has been determined yet
		bestSoFar = new Individual[state.population.subpops.length];

		// print out our generation number
		if (output) state.output.print("0 ", statisticslog);

		// gather timings
		totalSizeSoFar = new long[state.population.subpops.length];
		totalIndsSoFar = new long[state.population.subpops.length];

		if (output && doTime)
		{
			// Runtime r = Runtime.getRuntime();
			state.output.print("" + (System.currentTimeMillis() - lastTime) + " ", statisticslog);
		}
	}

	public void preBreedingStatistics(final EvolutionState state)
	{
		super.preBreedingStatistics(state);
		boolean output = (state.generation % modulus == modulus - 1);
		if (output && doTime)
		{
			// Runtime r = Runtime.getRuntime();
			lastTime = System.currentTimeMillis();
		}
	}

	public void postBreedingStatistics(final EvolutionState state)
	{
		super.postBreedingStatistics(state);
		boolean output = (state.generation % modulus == modulus - 1);
		if (output) state.output.print("" + (state.generation + 1) + " ", statisticslog); // 1 because we're putting the
																							// breeding info on the same line as
																							// the generation it *produces*, and
																							// the generation number is
																							// increased *after* breeding
																							// occurs, and statistics for it

		// gather timings
		if (output && doTime)
		{
			// Runtime r = Runtime.getRuntime();
			// long curU = r.totalMemory() - r.freeMemory();
			state.output.print("" + (System.currentTimeMillis() - lastTime) + " ", statisticslog);
		}
	}

	public void preEvaluationStatistics(final EvolutionState state)
	{
		super.preEvaluationStatistics(state);
		boolean output = (state.generation % modulus == 0);

		if (output && doTime)
		{
			// Runtime r = Runtime.getRuntime();
			lastTime = System.currentTimeMillis();
		}
		
		int g = state.generation;
		
		for(int x = 0;x < state.population.subpops.length; x++)
		{
			for(int i = 0; i < state.population.subpops[x].individuals.length; i++)
			{
				if(state.population.subpops[x].individuals[i] instanceof XGAIndividual)
				{
					XGAIndividual ind = (XGAIndividual)state.population.subpops[x].individuals[i];
					
					int[] metagenes = ind.getMetas();
					int[] translatedMetagenes = ind.getMetagenesTranslation();
					int[] genotype = ind.getGenome();
					int[] phenotype = ind.getPhenome();
					
					for(int c = 0; c < metagenes.length; c++)
					{
						if(metagenes[c] == 1)
						{
							state.output.println(String.format("%d,%d", g, c+1), metagenesLog);						
						}
						
						if(translatedMetagenes[c] == 1)
						{
							state.output.println(String.format("%d,%d", g, c+1), translatedMetagenesLog);						
						}
						
						if(genotype[c] == 1)
						{
							state.output.println(String.format("%d,%d", g, c+1), genotypesLog);						
						}
						
						if(phenotype[c] == 1)
						{
							state.output.println(String.format("%d,%d", g, c+1), phenotypesLog);						
						}
					}
					
					state.output.println(String.format("%d,x%s",state.generation, getArrayString(ind.getMetas())), metagenesStringLog);
					state.output.println(String.format("%d,x%s",state.generation, getArrayString(ind.getGenome())), genotypesStringLog);
					state.output.println(String.format("%d,x%s",state.generation, getArrayString(ind.getPhenome())), phenotypesStringLog);
					state.output.println(String.format("%d,x%s",state.generation, getArrayString(ind.getMetagenesTranslation())), translatedMetagenesStringLog);
				}
				else
				{
					break;
				}
			}
		}
		
		if(state instanceof HCEvolutionState)
		{
			HCEvolutionState thisState = (HCEvolutionState)state;
			for(int c = 0; c < thisState.metamask.length; c++)
			{
				if(thisState.metamask[c] == 1)
				{
					state.output.println(String.format("%d,%d", g, c+1), metamaskLog);						
				}
			}
			state.output.println(String.format("%d,%s,x%s",state.generation, getCommaSepArrayString(thisState.metamask),getArrayString(thisState.metamask)), metamaskStringLog);
		}
	}
	
	public String getArrayString(int[] a)
	{
		StringBuilder m = new StringBuilder();
		for(int x = 0; x < a.length; x++)
		{
			m.append(a[x]);
		}
		return m.toString();
	}
	
	public String getCommaSepArrayString(int[] a)
	{
		StringBuilder m = new StringBuilder();
		
		if(a != null) 
		{
			m.append(a[0]);
			
			for(int x = 1; x < a.length; x++)
			{
				m.append(String.format(",%d", a[x]));
			}	
		}
		
		return m.toString();
	}

	// This stuff is used by KozaShortStatistics

	protected void prepareStatistics(EvolutionState state)
	{
	}

	protected void gatherExtraSubpopStatistics(EvolutionState state, int subpop, int individual)
	{
	}

	protected void printExtraSubpopStatisticsBefore(EvolutionState state, int subpop)
	{
	}

	protected void printExtraSubpopStatisticsAfter(EvolutionState state, int subpop)
	{
	}

	protected void gatherExtraPopStatistics(EvolutionState state, int subpop)
	{
	}

	protected void printExtraPopStatisticsBefore(EvolutionState state)
	{
	}

	protected void printExtraPopStatisticsAfter(EvolutionState state)
	{
	}

	/**
	 * Prints out the statistics, but does not end with a println -- this lets
	 * overriding methods print additional statistics on the same line
	 */
	public void postEvaluationStatistics(final EvolutionState state)
	{
		super.postEvaluationStatistics(state);

		boolean output = (state.generation % modulus == 0);

		// gather timings
		if (output && doTime)
		{
			//Runtime r = Runtime.getRuntime();
			//long curU = r.totalMemory() - r.freeMemory();
			state.output.print("" + (System.currentTimeMillis() - lastTime) + " ", statisticslog);
		}

		int subpops = state.population.subpops.length; // number of supopulations
		totalIndsThisGen = new long[subpops]; // total assessed individuals
		bestOfGeneration = new Individual[subpops]; // per-subpop best individual this generation
		totalSizeThisGen = new long[subpops]; // per-subpop total size of individuals this generation
		totalFitnessThisGen = new double[subpops]; // per-subpop mean fitness this generation
	
		totalMetagenesHammingDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean fitness this generation
		totalMetagenesLevenshteinDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean fitness this generation
		totalMetagenesHammingDistanceFromCrossoverThisGen = new double[subpops];
		totalMetagenesLevenshteinDistanceFromCrossoverThisGen = new double[subpops];
		totalMetagenesHammingDistanceFromMirrorThisGen = new double[subpops];
		totalMetagenesLevenshteinDistanceFromMirrorThisGen = new double[subpops];
		totalMetagenesHammingDistanceFromResetThisGen = new double[subpops];
		totalMetagenesLevenshteinDistanceFromResetThisGen = new double[subpops];
		
		totalGenotypeHammingDistanceFromMutationThisGen = new double[subpops];
		totalGenotypeLevenshteinDistanceFromMutationThisGen = new double[subpops];
		totalGenotypeHammingDistanceFromCrossoverThisGen = new double[subpops];
		totalGenotypeLevenshteinDistanceFromCrossoverThisGen = new double[subpops];
		totalGenotypeHammingDistanceFromMirrorThisGen = new double[subpops];
		totalGenotypeLevenshteinDistanceFromMirrorThisGen = new double[subpops];
		totalGenotypeHammingDistanceFromResetThisGen = new double[subpops];
		totalGenotypeLevenshteinDistanceFromResetThisGen = new double[subpops];
		
		totalPhenotypeHammingDistanceFromMutationThisGen = new double[subpops];
		totalPhenotypeLevenshteinDistanceFromMutationThisGen = new double[subpops];
		totalPhenotypeHammingDistanceFromCrossoverThisGen = new double[subpops];
		totalPhenotypeLevenshteinDistanceFromCrossoverThisGen = new double[subpops];
		totalPhenotypeHammingDistanceFromMirrorThisGen = new double[subpops];
		totalPhenotypeLevenshteinDistanceFromMirrorThisGen = new double[subpops];
		totalPhenotypeHammingDistanceFromResetThisGen = new double[subpops];
		totalPhenotypeLevenshteinDistanceFromResetThisGen = new double[subpops];
		
		totalZeroCountThisGen = new int[subpops];
		totalOneCountThisGen = new int[subpops];
		totalTwoCountThisGen = new int[subpops];
		
		double[] meanFitnessThisGen = new double[subpops]; // per-subpop mean fitness this generation
		
		double[] meanMetagenesHammingDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanMetagenesLevenshteinDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation; 
		double[] meanMetagenesHammingDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanMetagenesLevenshteinDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanMetagenesHammingDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanMetagenesLevenshteinDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanMetagenesHammingDistanceFromResetThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanMetagenesLevenshteinDistanceFromResetThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		
		double[] meanGenotypeHammingDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanGenotypeLevenshteinDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation; 
		double[] meanGenotypeHammingDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanGenotypeLevenshteinDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanGenotypeHammingDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanGenotypeLevenshteinDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanGenotypeHammingDistanceFromResetThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanGenotypeLevenshteinDistanceFromResetThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		
		double[] meanPhenotypeHammingDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanPhenotypeLevenshteinDistanceFromMutationThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation; 
		double[] meanPhenotypeHammingDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanPhenotypeLevenshteinDistanceFromCrossoverThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanPhenotypeHammingDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanPhenotypeLevenshteinDistanceFromMirrorThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		double[] meanPhenotypeHammingDistanceFromResetThisGen = new double[subpops]; // per-subpop mean hamming distance this generation; 
		double[] meanPhenotypeLevenshteinDistanceFromResetThisGen = new double[subpops]; // per-subpop mean levenshtein distance this generation;
		
		double[] meanZeroCountThisGen = new double[subpops];
		double[] meanOneCountThisGen = new double[subpops];
		double[] meanTwoCountThisGen = new double[subpops];

		prepareStatistics(state);

		// gather per-subpopulation statistics

		for (int x = 0; x < subpops; x++)
		{
			for (int y = 0; y < state.population.subpops[x].individuals.length; y++)
			{
				if (state.population.subpops[x].individuals[y].evaluated) // he's got a valid fitness
				{
					// update sizes
					long size = state.population.subpops[x].individuals[y].size();
					totalSizeThisGen[x] += size;
					totalSizeSoFar[x] += size;
					totalIndsThisGen[x] += 1;
					totalIndsSoFar[x] += 1;

					// update fitness
					if (bestOfGeneration[x] == null || state.population.subpops[x].individuals[y].fitness.betterThan(bestOfGeneration[x].fitness))
					{
						bestOfGeneration[x] = state.population.subpops[x].individuals[y];
						if (bestSoFar[x] == null || bestOfGeneration[x].fitness.betterThan(bestSoFar[x].fitness)) bestSoFar[x] = (Individual) (bestOfGeneration[x].clone());
					}

					// sum up mean fitness for population
					totalFitnessThisGen[x] += state.population.subpops[x].individuals[y].fitness.fitness();
					
					if(state.population.subpops[x].individuals[y] instanceof XGAIndividual)
					{
					totalMetagenesHammingDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesHammingDistanceFromMutation;
					totalMetagenesLevenshteinDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesLevenshteinDistanceFromMutation;
					totalMetagenesHammingDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesHammingDistanceFromCrossover;
					totalMetagenesLevenshteinDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesLevenshteinDistanceFromCrossover;
					totalMetagenesHammingDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesHammingDistanceFromMirror;
					totalMetagenesLevenshteinDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesLevenshteinDistanceFromMirror;
					totalMetagenesHammingDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesHammingDistanceFromReset;
					totalMetagenesLevenshteinDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).metaGenesLevenshteinDistanceFromReset;
					
					totalGenotypeHammingDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeHammingDistanceFromMutation;
					totalGenotypeLevenshteinDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeLevenshteinDistanceFromMutation;
					totalGenotypeHammingDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeHammingDistanceFromCrossover;
					totalGenotypeLevenshteinDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeLevenshteinDistanceFromCrossover;
					totalGenotypeHammingDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeHammingDistanceFromMirror;
					totalGenotypeLevenshteinDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeLevenshteinDistanceFromMirror;
					totalGenotypeHammingDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeHammingDistanceFromReset;
					totalGenotypeLevenshteinDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).genotypeLevenshteinDistanceFromReset;
					
					totalPhenotypeHammingDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeHammingDistanceFromMutation;
					totalPhenotypeLevenshteinDistanceFromMutationThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeLevenshteinDistanceFromMutation;
					totalPhenotypeHammingDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeHammingDistanceFromCrossover;
					totalPhenotypeLevenshteinDistanceFromCrossoverThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeLevenshteinDistanceFromCrossover;
					totalPhenotypeHammingDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeHammingDistanceFromMirror;
					totalPhenotypeLevenshteinDistanceFromMirrorThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeLevenshteinDistanceFromMirror;
					totalPhenotypeHammingDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeHammingDistanceFromReset;
					totalPhenotypeLevenshteinDistanceFromResetThisGen[x] = ((XGAIndividual)state.population.subpops[x].individuals[y]).phenotypeLevenshteinDistanceFromReset;
					
					totalZeroCountThisGen[x] += ((XGAFitness)state.population.subpops[x].individuals[y].fitness).metaGenesZeroCount;
					totalOneCountThisGen[x] += ((XGAFitness)state.population.subpops[x].individuals[y].fitness).metaGenesOneCount;
					totalTwoCountThisGen[x] += ((XGAFitness)state.population.subpops[x].individuals[y].fitness).metaGenesTwoCount;
					}
					else
					{
						break;
					}
					// hook for KozaShortStatistics etc.
					gatherExtraSubpopStatistics(state, x, y);
				}
			}
			// compute mean fitness stats
			meanFitnessThisGen[x] = (totalIndsThisGen[x] > 0 ? totalFitnessThisGen[x] / totalIndsThisGen[x] : 0);
			
			meanMetagenesHammingDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesHammingDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesLevenshteinDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesLevenshteinDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesHammingDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesHammingDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesLevenshteinDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesLevenshteinDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesHammingDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesHammingDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesLevenshteinDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesLevenshteinDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesHammingDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesHammingDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			meanMetagenesLevenshteinDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalMetagenesLevenshteinDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			
			meanGenotypeHammingDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeHammingDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeLevenshteinDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeLevenshteinDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeHammingDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeHammingDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeLevenshteinDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeLevenshteinDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeHammingDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeHammingDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeLevenshteinDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeLevenshteinDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeHammingDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeHammingDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			meanGenotypeLevenshteinDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalGenotypeLevenshteinDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			
			meanPhenotypeHammingDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeHammingDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeLevenshteinDistanceFromMutationThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeLevenshteinDistanceFromMutationThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeHammingDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeHammingDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeLevenshteinDistanceFromCrossoverThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeLevenshteinDistanceFromCrossoverThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeHammingDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeHammingDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeLevenshteinDistanceFromMirrorThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeLevenshteinDistanceFromMirrorThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeHammingDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeHammingDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			meanPhenotypeLevenshteinDistanceFromResetThisGen[x] = (totalIndsThisGen[x] > 0 ? totalPhenotypeLevenshteinDistanceFromResetThisGen[x] / totalIndsThisGen[x] : 0);
			
			meanZeroCountThisGen[x] = (totalIndsThisGen[x] > 0 ? totalZeroCountThisGen[x] / totalIndsThisGen[x] : 0);
			meanOneCountThisGen[x] = (totalIndsThisGen[x] > 0 ? totalOneCountThisGen[x] / totalIndsThisGen[x] : 0);
			meanTwoCountThisGen[x] = (totalIndsThisGen[x] > 0 ? totalTwoCountThisGen[x] / totalIndsThisGen[x] : 0);
			

			// hook for KozaShortStatistics etc.
			if (output && doSubpops) printExtraSubpopStatisticsBefore(state, x);

			// print out optional average size information
			if (output && doSize && doSubpops)
			{
				state.output.print("" + (totalIndsThisGen[x] > 0 ? ((double) totalSizeThisGen[x]) / totalIndsThisGen[x] : 0) + " ", statisticslog);
				state.output.print("" + (totalIndsSoFar[x] > 0 ? ((double) totalSizeSoFar[x]) / totalIndsSoFar[x] : 0) + " ", statisticslog);
				state.output.print("" + (double) (bestOfGeneration[x].size()) + " ", statisticslog);
				state.output.print("" + (double) (bestSoFar[x].size()) + " ", statisticslog);
			}

			// print out fitness information ******
			if (output && doSubpops)
			{
				state.output.print("" + meanFitnessThisGen[x] + " ", statisticslog);
				state.output.print("" + bestOfGeneration[x].fitness.fitness() + " ", statisticslog);
				state.output.print("" + bestSoFar[x].fitness.fitness() + " ", statisticslog);
			}

			// hook for KozaShortStatistics etc.
			if (output && doSubpops) printExtraSubpopStatisticsAfter(state, x);
		}

		// Now gather per-Population statistics
		long popTotalInds = 0;
		long popTotalIndsSoFar = 0;
		long popTotalSize = 0;
		long popTotalSizeSoFar = 0;
		double popMeanFitness = 0;
		double popTotalFitness = 0;
		
		double popTotalMetagenesHammingDistanceFromMutation = 0;
		double popTotalMetagenesLevenshteinDistanceFromMutation = 0;
		double popTotalMetagenesHammingDistanceFromCrossover = 0;
		double popTotalMetagenesLevenshteinDistanceFromCrossover = 0;
		double popTotalMetagenesHammingDistanceFromMirror = 0;
		double popTotalMetagenesLevenshteinDistanceFromMirror = 0;
		double popTotalMetagenesHammingDistanceFromReset = 0;
		double popTotalMetagenesLevenshteinDistanceFromReset = 0;
		
		double popTotalGenotypeHammingDistanceFromMutation = 0;
		double popTotalGenotypeLevenshteinDistanceFromMutation = 0;
		double popTotalGenotypeHammingDistanceFromCrossover = 0;
		double popTotalGenotypeLevenshteinDistanceFromCrossover = 0;
		double popTotalGenotypeHammingDistanceFromMirror = 0;
		double popTotalGenotypeLevenshteinDistanceFromMirror = 0;
		double popTotalGenotypeHammingDistanceFromReset = 0;
		double popTotalGenotypeLevenshteinDistanceFromReset = 0;
		
		double popTotalPhenotypeHammingDistanceFromMutation = 0;
		double popTotalPhenotypeLevenshteinDistanceFromMutation = 0;
		double popTotalPhenotypeHammingDistanceFromCrossover = 0;
		double popTotalPhenotypeLevenshteinDistanceFromCrossover = 0;
		double popTotalPhenotypeHammingDistanceFromMirror = 0;
		double popTotalPhenotypeLevenshteinDistanceFromMirror = 0;
		double popTotalPhenotypeHammingDistanceFromReset = 0;
		double popTotalPhenotypeLevenshteinDistanceFromReset = 0;
		
		double popMeanMetagenesHammingDistanceFromMutation = 0;
		double popMeanMetagenesLevenshteinDistanceFromMutation = 0;
		double popMeanMetagenesHammingDistanceFromCrossover = 0;
		double popMeanMetagenesLevenshteinDistanceFromCrossover = 0;
		double popMeanMetagenesHammingDistanceFromMirror = 0;
		double popMeanMetagenesLevenshteinDistanceFromMirror = 0;
		double popMeanMetagenesHammingDistanceFromReset = 0;
		double popMeanMetagenesLevenshteinDistanceFromReset = 0;
		
		double popMeanGenotypeHammingDistanceFromMutation = 0;
		double popMeanGenotypeLevenshteinDistanceFromMutation = 0;
		double popMeanGenotypeHammingDistanceFromCrossover = 0;
		double popMeanGenotypeLevenshteinDistanceFromCrossover = 0;
		double popMeanGenotypeHammingDistanceFromMirror = 0;
		double popMeanGenotypeLevenshteinDistanceFromMirror = 0;
		double popMeanGenotypeHammingDistanceFromReset = 0;
		double popMeanGenotypeLevenshteinDistanceFromReset = 0;
		
		double popMeanPhenotypeHammingDistanceFromMutation = 0;
		double popMeanPhenotypeLevenshteinDistanceFromMutation = 0;
		double popMeanPhenotypeHammingDistanceFromCrossover = 0;
		double popMeanPhenotypeLevenshteinDistanceFromCrossover = 0;
		double popMeanPhenotypeHammingDistanceFromMirror = 0;
		double popMeanPhenotypeLevenshteinDistanceFromMirror = 0;
		double popMeanPhenotypeHammingDistanceFromReset = 0;
		double popMeanPhenotypeLevenshteinDistanceFromReset = 0;
		
		double popMeanZeroCount = 0;
		int popTotalZeroCount = 0;
		double popMeanOneCount = 0;
		int popTotalOneCount = 0;
		double popMeanTwoCount = 0;
		int popTotalTwoCount = 0;
		double metamaskPercentOnes = 0;
		int metamaskOneCount = 0;
		Individual popBestOfGeneration = null;
		Individual popBestSoFar = null;

		if(state instanceof HCEvolutionState)
		{
			HCEvolutionState thisState = (HCEvolutionState)state;
			int metamaskLength = thisState.metamask.length;
			
			for(int x = 0; x < metamaskLength; x++)
			{
				if(thisState.metamask[x] == 1)
				{
					metamaskOneCount++;
				}
			}
			metamaskPercentOnes = (double)metamaskOneCount / (double)metamaskLength;
			//state.output.print(String.format(" metamaskPercentOnes == %f ", metamaskPercentOnes), statisticslog); 
		}
		
		for (int x = 0; x < subpops; x++)
		{
			popTotalInds += totalIndsThisGen[x];
			popTotalIndsSoFar += totalIndsSoFar[x];
			popTotalSize += totalSizeThisGen[x];
			popTotalSizeSoFar += totalSizeSoFar[x];
			popTotalFitness += totalFitnessThisGen[x];
			
			popTotalMetagenesHammingDistanceFromMutation += totalMetagenesHammingDistanceFromMutationThisGen[x];
			popTotalMetagenesLevenshteinDistanceFromMutation += totalMetagenesLevenshteinDistanceFromMutationThisGen[x];
			popTotalMetagenesHammingDistanceFromCrossover += totalMetagenesHammingDistanceFromCrossoverThisGen[x];
			popTotalMetagenesLevenshteinDistanceFromCrossover += totalMetagenesLevenshteinDistanceFromCrossoverThisGen[x];
			popTotalMetagenesHammingDistanceFromMirror += totalMetagenesHammingDistanceFromMirrorThisGen[x];
			popTotalMetagenesLevenshteinDistanceFromMirror += totalMetagenesLevenshteinDistanceFromMirrorThisGen[x];
			popTotalMetagenesHammingDistanceFromReset += totalMetagenesHammingDistanceFromResetThisGen[x];
			popTotalMetagenesLevenshteinDistanceFromReset += totalMetagenesLevenshteinDistanceFromResetThisGen[x];
			
			popTotalGenotypeHammingDistanceFromMutation += totalGenotypeHammingDistanceFromMutationThisGen[x];
			popTotalGenotypeLevenshteinDistanceFromMutation += totalGenotypeLevenshteinDistanceFromMutationThisGen[x];
			popTotalGenotypeHammingDistanceFromCrossover += totalGenotypeHammingDistanceFromCrossoverThisGen[x];
			popTotalGenotypeLevenshteinDistanceFromCrossover += totalGenotypeLevenshteinDistanceFromCrossoverThisGen[x];
			popTotalGenotypeHammingDistanceFromMirror += totalGenotypeHammingDistanceFromMirrorThisGen[x];
			popTotalGenotypeLevenshteinDistanceFromMirror += totalGenotypeLevenshteinDistanceFromMirrorThisGen[x];
			popTotalGenotypeHammingDistanceFromReset += totalGenotypeHammingDistanceFromResetThisGen[x];
			popTotalGenotypeLevenshteinDistanceFromReset += totalGenotypeLevenshteinDistanceFromResetThisGen[x];
			
			popTotalPhenotypeHammingDistanceFromMutation += totalPhenotypeHammingDistanceFromMutationThisGen[x];
			popTotalPhenotypeLevenshteinDistanceFromMutation += totalPhenotypeLevenshteinDistanceFromMutationThisGen[x];
			popTotalPhenotypeHammingDistanceFromCrossover += totalPhenotypeHammingDistanceFromCrossoverThisGen[x];
			popTotalPhenotypeLevenshteinDistanceFromCrossover += totalPhenotypeLevenshteinDistanceFromCrossoverThisGen[x];
			popTotalPhenotypeHammingDistanceFromMirror += totalPhenotypeHammingDistanceFromMirrorThisGen[x];
			popTotalPhenotypeLevenshteinDistanceFromMirror += totalPhenotypeLevenshteinDistanceFromMirrorThisGen[x];
			popTotalPhenotypeHammingDistanceFromReset += totalPhenotypeHammingDistanceFromResetThisGen[x];
			popTotalPhenotypeLevenshteinDistanceFromReset += totalPhenotypeLevenshteinDistanceFromResetThisGen[x];
			
			popTotalZeroCount += totalZeroCountThisGen[x];
			popTotalOneCount += totalOneCountThisGen[x];
			popTotalTwoCount += totalTwoCountThisGen[x];
			
			if (bestOfGeneration[x] != null && (popBestOfGeneration == null || bestOfGeneration[x].fitness.betterThan(popBestOfGeneration.fitness))) popBestOfGeneration = bestOfGeneration[x];
			if (bestSoFar[x] != null && (popBestSoFar == null || bestSoFar[x].fitness.betterThan(popBestSoFar.fitness))) popBestSoFar = bestSoFar[x];

			// hook for KozaShortStatistics etc.
			gatherExtraPopStatistics(state, x);
		}

		// build mean
		popMeanFitness = (popTotalInds > 0 ? popTotalFitness / popTotalInds : 0); // average out
		
		popMeanMetagenesHammingDistanceFromMutation = (popTotalInds > 0 ? popTotalMetagenesHammingDistanceFromMutation / popTotalInds : 0);
		popMeanMetagenesLevenshteinDistanceFromMutation = (popTotalInds > 0 ? popTotalMetagenesLevenshteinDistanceFromMutation / popTotalInds : 0);
		popMeanMetagenesHammingDistanceFromCrossover = (popTotalInds > 0 ? popTotalMetagenesHammingDistanceFromCrossover / popTotalInds : 0);
		popMeanMetagenesLevenshteinDistanceFromCrossover = (popTotalInds > 0 ? popTotalMetagenesLevenshteinDistanceFromCrossover / popTotalInds : 0);
		popMeanMetagenesHammingDistanceFromMirror = (popTotalInds > 0 ? popTotalMetagenesHammingDistanceFromMirror / popTotalInds : 0);
		popMeanMetagenesLevenshteinDistanceFromMirror = (popTotalInds > 0 ? popTotalMetagenesLevenshteinDistanceFromMirror / popTotalInds : 0);
		popMeanMetagenesHammingDistanceFromReset = (popTotalInds > 0 ? popTotalMetagenesHammingDistanceFromReset / popTotalInds : 0);
		popMeanMetagenesLevenshteinDistanceFromReset = (popTotalInds > 0 ? popTotalMetagenesLevenshteinDistanceFromReset / popTotalInds : 0);
		
		popMeanGenotypeHammingDistanceFromMutation = (popTotalInds > 0 ? popTotalGenotypeHammingDistanceFromMutation / popTotalInds : 0);
		popMeanGenotypeLevenshteinDistanceFromMutation = (popTotalInds > 0 ? popTotalGenotypeLevenshteinDistanceFromMutation / popTotalInds : 0);
		popMeanGenotypeHammingDistanceFromCrossover = (popTotalInds > 0 ? popTotalGenotypeHammingDistanceFromCrossover / popTotalInds : 0);
		popMeanGenotypeLevenshteinDistanceFromCrossover = (popTotalInds > 0 ? popTotalGenotypeLevenshteinDistanceFromCrossover / popTotalInds : 0);
		popMeanGenotypeHammingDistanceFromMirror = (popTotalInds > 0 ? popTotalGenotypeHammingDistanceFromMirror / popTotalInds : 0);
		popMeanGenotypeLevenshteinDistanceFromMirror = (popTotalInds > 0 ? popTotalGenotypeLevenshteinDistanceFromMirror / popTotalInds : 0);
		popMeanGenotypeHammingDistanceFromReset = (popTotalInds > 0 ? popTotalGenotypeHammingDistanceFromReset / popTotalInds : 0);
		popMeanGenotypeLevenshteinDistanceFromReset = (popTotalInds > 0 ? popTotalGenotypeLevenshteinDistanceFromReset / popTotalInds : 0);

		popMeanPhenotypeHammingDistanceFromMutation = (popTotalInds > 0 ? popTotalPhenotypeHammingDistanceFromMutation / popTotalInds : 0);
		popMeanPhenotypeLevenshteinDistanceFromMutation = (popTotalInds > 0 ? popTotalPhenotypeLevenshteinDistanceFromMutation / popTotalInds : 0);
		popMeanPhenotypeHammingDistanceFromCrossover = (popTotalInds > 0 ? popTotalPhenotypeHammingDistanceFromCrossover / popTotalInds : 0);
		popMeanPhenotypeLevenshteinDistanceFromCrossover = (popTotalInds > 0 ? popTotalPhenotypeLevenshteinDistanceFromCrossover / popTotalInds : 0);
		popMeanPhenotypeHammingDistanceFromMirror = (popTotalInds > 0 ? popTotalPhenotypeHammingDistanceFromMirror / popTotalInds : 0);
		popMeanPhenotypeLevenshteinDistanceFromMirror = (popTotalInds > 0 ? popTotalPhenotypeLevenshteinDistanceFromMirror / popTotalInds : 0);
		popMeanPhenotypeHammingDistanceFromReset = (popTotalInds > 0 ? popTotalPhenotypeHammingDistanceFromReset / popTotalInds : 0);
		popMeanPhenotypeLevenshteinDistanceFromReset = (popTotalInds > 0 ? popTotalPhenotypeLevenshteinDistanceFromReset / popTotalInds : 0);
		
		popMeanZeroCount = (popTotalInds > 0 ? popTotalZeroCount / popTotalInds : 0);
		popMeanOneCount = (popTotalInds > 0 ? popTotalOneCount / popTotalInds : 0);
		popMeanTwoCount = (popTotalInds > 0 ? popTotalTwoCount / popTotalInds : 0);
		
		// hook for KozaShortStatistics etc.
		if (output) printExtraPopStatisticsBefore(state);

		// optionally print out mean size info
		if (output && doSize)
		{
			state.output.print("" + (popTotalInds > 0 ? popTotalSize / popTotalInds : 0) + " ", statisticslog); // mean size of pop this gen
			state.output.print("" + (popTotalIndsSoFar > 0 ? popTotalSizeSoFar / popTotalIndsSoFar : 0) + " ", statisticslog); // mean size of pop so far
			state.output.print("" + (double) (popBestOfGeneration.size()) + " ", statisticslog); // size of best ind of pop this gen
			state.output.print("" + (double) (popBestSoFar.size()) + " ", statisticslog); // size of best ind of pop so far
		}

		// print out fitness info
		if (output)
		{
			state.output.print("" + popMeanFitness + " ", statisticslog); // mean fitness of pop this gen
			state.output.print("" + (double) (popBestOfGeneration.fitness.fitness()) + " ", statisticslog); // best fitness of pop this gen
			state.output.print("" + (double) (popBestSoFar.fitness.fitness()) + " ", statisticslog); // best fitness of pop so far
			state.output.print("" + popMeanMetagenesHammingDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesLevenshteinDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanZeroCount / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanOneCount / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanTwoCount / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + metamaskPercentOnes + " ", statisticslog);

			state.output.print("" + popMeanMetagenesHammingDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesLevenshteinDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesHammingDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesLevenshteinDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesHammingDistanceFromReset / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanMetagenesLevenshteinDistanceFromReset / GENOME_SIZE + " ", statisticslog);
			
			state.output.print("" + popMeanGenotypeHammingDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeLevenshteinDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeHammingDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeLevenshteinDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeHammingDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeLevenshteinDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeHammingDistanceFromReset / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanGenotypeLevenshteinDistanceFromReset / GENOME_SIZE + " ", statisticslog);
			
			state.output.print("" + popMeanPhenotypeHammingDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeLevenshteinDistanceFromMutation / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeHammingDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeLevenshteinDistanceFromCrossover / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeHammingDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeLevenshteinDistanceFromMirror / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeHammingDistanceFromReset / GENOME_SIZE + " ", statisticslog);
			state.output.print("" + popMeanPhenotypeLevenshteinDistanceFromReset / GENOME_SIZE + " ", statisticslog);
		}

		// hook for KozaShortStatistics etc.
		if (output) printExtraPopStatisticsAfter(state);

		// we're done!
		if (output) state.output.println("", statisticslog);
	}
}
