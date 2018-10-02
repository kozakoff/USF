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

	@Override
	public int[] getMetas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHammingDistance(int[] before, int[] after) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLevenshteinDistance(int[] before, int[] after) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getMetagenesTranslation() {
		// TODO Auto-generated method stub
		return null;
	}

}
