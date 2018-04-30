package xga;

import ec.*;
import ec.util.*;

public class HCEvolutionState extends EvolutionState
{
	private static final long serialVersionUID = 1L;
    public final static String P_MINMETAMASKGENE = "min-metamask-gene";
    public final static String P_MAXMETAMASKGENE = "max-metamask-gene";
    public final static String P_METAMASKSIZE = "metamask-size";
    
	protected long minMetamaskGene;
	protected long maxMetamaskGene;
	protected int[] metamask;
	public int metamaskSize;
	
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

		// LOADING GLOBAL MIN/MAX GENES
		p = new Parameter(P_MINMETAMASKGENE);
		minMetamaskGene = parameters.getLongWithDefault(p, null, 0);
		p = new Parameter(P_MAXMETAMASKGENE);
		maxMetamaskGene = parameters.getLong(p, null, minMetamaskGene);
		
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

	public int evolve()
	{
		if (generation > 0) output.message("Generation " + generation);

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
		
		for(int x = 0; x < population.subpops.length; x++)
		{
			for(int y = 0; y < population.subpops[x].individuals.length; x++)
			{
				HCIndividual ind = (HCIndividual)population.subpops[x].individuals[y];
				ind.resetMetas(this, 0);
			}
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
	
	private void initMetamask(EvolutionState state, int thread)
	{
		for(int x = 0; x < metamask.length; x++)
		{
			metamask[x] = randomValueFromClosedInterval((int)minMetamaskGene, (int)maxMetamaskGene, state.random[thread]);
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
