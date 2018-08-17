package xga;

import java.util.ArrayList;

import ec.*;
import ec.util.*;

public class HCEvolutionState extends EvolutionState
{
	private static final long serialVersionUID = 1L;
    public final static String P_MINMETAMASKGENE = "min-metamask-gene";
    public final static String P_MAXMETAMASKGENE = "max-metamask-gene";
    public final static String P_METAMASKSIZE = "metamask-size";
    public final static String P_METAMASKGENS = "metamask-generations";
    public final static String P_METAMASKEVOLVEPROB = "metamask-evolve-prob";
    public final static String P_METAMASKRANDRESET = "metamask-rand-reset";
    
	protected long minMetamaskGene;
	protected long maxMetamaskGene;
	protected int[] metamask;
	protected int[] prevMetamask;
	protected int metamaskSum = 0;
	protected int prevMetamaskSum = 0;
	protected boolean metamaskRandomMutation = false;
	protected double averageFitness = 0.0;
	protected double prevAverageFitness = 0.0;
	public ArrayList<int[]> metamaskChanges = new ArrayList<int[]>();
	public int metamaskSize;
	public int metamaskGenerations;
	public double metamaskEvolveProb;
	public boolean resetMetas = false;
	
	
	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		Parameter p;
		
		p = new Parameter(P_METAMASKSIZE);
		metamaskSize = parameters.getInt(p,null,1);
		
        if (metamaskSize==0)
            state.output.fatal("Must have a metamask size > 0",base.push(P_METAMASKSIZE),p.push(P_METAMASKSIZE));
        
        //state.output.println(String.format("metamaskSize: %d", metamaskSize),0);

		// create the arrays
		metamask = new int[metamaskSize];
		prevMetamask = new int[metamaskSize];
		
		// LOADING GLOBAL MIN/MAX GENES
		p = new Parameter(P_MINMETAMASKGENE);
		minMetamaskGene = parameters.getLongWithDefault(p, null, 0);
		p = new Parameter(P_MAXMETAMASKGENE);
		maxMetamaskGene = parameters.getLong(p, null, minMetamaskGene);
		p = new Parameter(P_METAMASKGENS);
		metamaskGenerations = parameters.getInt(p, null, 0);
		p = new Parameter(P_METAMASKEVOLVEPROB);
		metamaskEvolveProb = parameters.getDouble(p, null, 0.0);
		
		// Use random reset (true) or fitness based reset (false) 
		p = new Parameter(P_METAMASKRANDRESET);
		metamaskRandomMutation = parameters.getBoolean(p, null, false);
		
		//state.output.println(String.format("metamaskGenerations: %d", metamaskGenerations), 0);
		
		initMetamask(state, 0);
	}
	
	public void startFresh()
	{
		output.message("Setting up");
		setup(this, null); // a garbage Parameter

		// POPULATION INITIALIZATION
		output.message("Initializing Generation 0");
		statistics.preInitializationStatistics(this);
		population = initializer.initialPopulation(this, 0); // unthreaded
		statistics.postInitializationStatistics(this);

		// Compute generations from evaluations if necessary
		if (numEvaluations > UNDEFINED)
		{
			// compute a generation's number of individuals
			int generationSize = 0;
			for (int sub = 0; sub < population.subpops.length; sub++)
			{
				generationSize += population.subpops[sub].individuals.length; // so our sum total 'generationSize' will be the initial total number of
																				// individuals
			}

			if (numEvaluations < generationSize)
			{
				numEvaluations = generationSize;
				numGenerations = 1;
				output.warning("Using evaluations, but evaluations is less than the initial total population size (" + generationSize + ").  Setting to the populatiion size.");
			}
			else
			{
				if (numEvaluations % generationSize != 0) output.warning("Using evaluations, but initial total population size does not divide evenly into it.  Modifying evaluations to a smaller value (" + ((numEvaluations / generationSize) * generationSize) + ") which divides evenly."); // note integer division
				numGenerations = (int) (numEvaluations / generationSize); // note integer division
				numEvaluations = numGenerations * generationSize;
			}
			output.message("Generations will be " + numGenerations);
		}

		// INITIALIZE CONTACTS -- done after initialization to allow
		// a hook for the user to do things in Initializer before
		// an attempt is made to connect to island models etc.
		exchanger.initializeContacts(this);
		evaluator.initializeContacts(this);
	}

	public String getMetamask()
	{
		StringBuilder m = new StringBuilder();
		
		for(int i = 0; i < metamask.length; i++)
		{
			m.append(metamask[i]);
		}
		
		return m.toString();
	}
	
	public int evolve()
	{
		if (generation > 0) output.message("Generation " + generation);

		//output.println(String.format(" Metamask is: %s", getMetamask()), 0);
		
		if(((generation % metamaskGenerations) == 0) && (generation > 0))
		{
			resetMetas = true;
			mutateMetamask(this,0);
			//output.println(String.format("New metamask: %s", getMetamask()), 0);
			//output.println(String.format("resetMetas true at generation: %d", generation), 0);
		}
		else
		{
			resetMetas = false;
		}
		
		if (resetMetas)
		{
			for (Subpopulation s : this.population.subpops)
			{
				for (Individual i : s.individuals)
				{
					HCIndividual thisInd = (HCIndividual) i;
					thisInd.resetMetas(this, 0);
				}
			}
		}
				
		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// SHOULD WE QUIT?
		String runCompleteMessage = evaluator.runComplete(this);
		if ((runCompleteMessage != null) && quitOnRunComplete)
		{
			output.message(runCompleteMessage);
			return R_SUCCESS;
		}

		// SHOULD WE QUIT?
		if (generation == numGenerations - 1) { return R_FAILURE; }

		// PRE-BREEDING EXCHANGING
		statistics.prePreBreedingExchangeStatistics(this);
		population = exchanger.preBreedingExchangePopulation(this);
		statistics.postPreBreedingExchangeStatistics(this);

		String exchangerWantsToShutdown = exchanger.runComplete(this);
		if (exchangerWantsToShutdown != null)
		{
			output.message(exchangerWantsToShutdown);
			return R_SUCCESS;
		}

		// BREEDING
		statistics.preBreedingStatistics(this);

		population = breeder.breedPopulation(this);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		population = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// INCREMENT GENERATION AND CHECKPOINT
		generation++;
		if (checkpoint && generation % checkpointModulo == 0)
		{
			output.message("Checkpointing");
			statistics.preCheckpointStatistics(this);
			Checkpoint.setCheckpoint(this);
			statistics.postCheckpointStatistics(this);
		}
		
		return R_NOTDONE;
	}

	/**
	 * @param result
	 */
	public void finish(int result)
	{
		// Output.message("Finishing");
		/* finish up -- we completed. */
		statistics.finalStatistics(this, result);
		finisher.finishPopulation(this, result);
		exchanger.closeContacts(this, result);
		evaluator.closeContacts(this, result);
	}
	
	private double getAverageFitness(EvolutionState state, int thread)
	{
		double fitness_sum = 0.0;
		
		for(int x = 0; x < state.population.subpops[0].individuals.length; x++)
		{
			fitness_sum += state.population.subpops[0].individuals[x].fitness.fitness(); 
		}
		
		return (fitness_sum / state.population.subpops[0].individuals.length);
	}
	
	private void mutateMetamask(EvolutionState state, int thread)
	{
		averageFitness = getAverageFitness(state, thread);
		//double thisMetamaskEvolveProb = (averageFitness == 0 ? metamaskEvolveProb : ((1-averageFitness)/200));
		double thisMetamaskEvolveProb = metamaskEvolveProb;
		
		if(!metamaskRandomMutation)
		{
			metamaskSum = 0;
			for(int x = 0; x < metamask.length; x++)
			{
				metamaskSum += metamask[x];
			}
			
			if((averageFitness >= prevAverageFitness) || (metamaskChanges.size() == 0))
			{
				//Store new high fitness			
				prevAverageFitness = averageFitness;
				
				if(averageFitness != 1)
				{
					//Store new high metamask
					prevMetamask = metamask;
				
					//The last change we made was positive so discard the previously saved changes.
					if(metamaskChanges.size() > 0) { metamaskChanges.removeAll(metamaskChanges); }

					//state.output.println(String.format("averageFitness = %f",averageFitness), 0);
					//state.output.println(String.format("thisMetamaskEvolveProb = %f",thisMetamaskEvolveProb), 0);
					
					//Mutate the metamask to see if we can make it better.
					for(int x = 0; x < metamask.length; x++)
					{
						if (state.random[thread].nextBoolean(thisMetamaskEvolveProb)) //metamaskEvolveProb/(1 - 
						{
							metamask[x] = 1 - metamask[x]; //Flip the bit
							metamaskChanges.add(new int[] { x, metamask[x] });
						}
					}
				}
			}
			else
			{
				if(metamaskChanges.size() > 0) 
				{
					//state.output.println(String.format("Rollback some changes..."), 0);
					//state.output.println(String.format("metamaskChanges.size() is: %d",metamaskChanges.size()), 0);
					int randomIndex = randomValueFromClosedInterval((int)0, (int)(metamaskChanges.size()-1), state.random[thread]);
					//state.output.println(String.format("randomIndex is: %d",randomIndex), 0);
					
					for(int x = 0; x < metamaskChanges.size(); x++)
					{
						int[] thisChange = metamaskChanges.get(x); //randomIndex
						int index = thisChange[0];
						int value = 1 - thisChange[1];
					
						//Reverting this change in the metamask
						metamask[index] = value;	
						//state.output.println(String.format("Reverted index: %d from %d to %d", index, thisChange[1], value), 0);
					}
			
					//Removing the change from metamaskChanges
					//metamaskChanges.remove(randomIndex);
					metamaskChanges.removeAll(metamaskChanges);
				}
				
				for(int x = 0; x < metamask.length; x++)
				{
					if (state.random[thread].nextBoolean(thisMetamaskEvolveProb)) //metamaskEvolveProb/(1 - 
					{
						metamask[x] = 1 - metamask[x]; //Flip the bit
						metamaskChanges.add(new int[] { x, metamask[x] });
					}
				}
			}
		}
		else
		{
			for(int x = 0; x < metamask.length; x++)
			{
				if (state.random[thread].nextBoolean(thisMetamaskEvolveProb)) 
				{
					metamask[x] = 1 - metamask[x]; //Flip the bit
				}
			}
		}
		
		int oneCount = 0;
		for(int x = 0; x < metamask.length; x++)
		{
			if(metamask[x] == 1) 
			{
				oneCount++;
			}
		}
		state.output.println(String.format("At gen %d metamask has %d 1's (%.2f%%) and %d 0's",state.generation, oneCount, (float)oneCount/(float)metamask.length, metamask.length-oneCount), 0);
	}
	
	private void initMetamask(EvolutionState state, int thread)
	{
		for(int x = 0; x < metamask.length; x++)
		{
			metamask[x] = 0;
		}
		
		for(int x = 0; x < metamask.length; x++)
		{
			if (state.random[thread].nextBoolean(metamaskEvolveProb)) 
			{
				//metamask[x] = 1; //randomValueFromClosedInterval((int)minMetamaskGene, (int)maxMetamaskGene, state.random[thread]);
			}
		}
		
		
		//state.output.println(String.format("Metamask initialized."),0);
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
		else return min + random.nextInt(max - min + 1);
	}
}
