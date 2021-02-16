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
	
	// timings
	public long lastTime;

	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);
//		File metagenesFile = new File(statisticsFile.getParent()+"/metagenes.txt");
//		File metagenesStringFile = new File(statisticsFile.getParent()+"/metagenes_string.txt");
//		File translatedMetagenesFile = new File(statisticsFile.getParent()+"/translatedmetagenes.txt");
//		File translatedMetagenesStringFile = new File(statisticsFile.getParent()+"/translatedmetagenes_string.txt");
//		File genotypesFile = new File(statisticsFile.getParent()+"/genotypes.txt");
//		File genotypesStringFile = new File(statisticsFile.getParent()+"/genotypes_string.txt");
//		File phenotypesFile = new File(statisticsFile.getParent()+"/phenotypes.txt");
//		File phenotypesStringFile = new File(statisticsFile.getParent()+"/phenotypes_string.txt");
//		File metamaskFile = new File(statisticsFile.getParent()+"/metamask.txt");
//		File metamaskStringFile = new File(statisticsFile.getParent()+"/metamask_string.txt");
		
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
				//metagenesLog = state.output.addLog(metagenesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//metagenesStringLog = state.output.addLog(metagenesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//genotypesLog = state.output.addLog(genotypesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//genotypesStringLog = state.output.addLog(genotypesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//phenotypesLog = state.output.addLog(phenotypesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//phenotypesStringLog = state.output.addLog(phenotypesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//translatedMetagenesLog = state.output.addLog(translatedMetagenesFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//translatedMetagenesStringLog = state.output.addLog(translatedMetagenesStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//metamaskLog = state.output.addLog(metamaskFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
				//metamaskStringLog=state.output.addLog(metamaskStringFile, !state.parameters.getBoolean(base.push(P_COMPRESS), null, false), state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
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
	
		double[] meanFitnessThisGen = new double[subpops]; // per-subpop mean fitness this generation
		
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
					
					// hook for KozaShortStatistics etc.
					gatherExtraSubpopStatistics(state, x, y);
				}
			}
			// compute mean fitness stats
			meanFitnessThisGen[x] = (totalIndsThisGen[x] > 0 ? totalFitnessThisGen[x] / totalIndsThisGen[x] : 0);

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
		
		Individual popBestOfGeneration = null;
		Individual popBestSoFar = null;

		for (int x = 0; x < subpops; x++)
		{
			popTotalInds += totalIndsThisGen[x];
			popTotalIndsSoFar += totalIndsSoFar[x];
			popTotalSize += totalSizeThisGen[x];
			popTotalSizeSoFar += totalSizeSoFar[x];
			popTotalFitness += totalFitnessThisGen[x];
			
			if (bestOfGeneration[x] != null && (popBestOfGeneration == null || bestOfGeneration[x].fitness.betterThan(popBestOfGeneration.fitness))) popBestOfGeneration = bestOfGeneration[x];
			if (bestSoFar[x] != null && (popBestSoFar == null || bestSoFar[x].fitness.betterThan(popBestSoFar.fitness))) popBestSoFar = bestSoFar[x];

			// hook for KozaShortStatistics etc.
			gatherExtraPopStatistics(state, x);
		}

		// build mean
		popMeanFitness = (popTotalInds > 0 ? popTotalFitness / popTotalInds : 0); // average out
		
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
		}

		// hook for KozaShortStatistics etc.
		if (output) printExtraPopStatisticsAfter(state);

		// we're done!
		if (output) state.output.println("", statisticslog);
	}
}
