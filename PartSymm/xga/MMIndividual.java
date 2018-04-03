package xga;

import ec.EvolutionState;

public class MMIndividual extends XGAIndividual
{

	private static final long serialVersionUID = 1L;

	@Override
	public int[] getGenome()
	{
		return genome;
	}

	@Override
	public int[] getPhenome()
	{
		int[] phenome = genome.clone();
		//final String[] args = new String({"-file xga/hc.params"});
		//ec.Evolve.main(args);
		return phenome;
	}

	@Override
	public void mirror(EvolutionState state, int thread)
	{
		//Not implemented for Metamask Individuals
		//ec.Evolve e = new ec.Evolve();
		
		
	}

}
