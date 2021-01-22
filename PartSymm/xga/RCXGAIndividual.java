package xga;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.vector.DoubleVectorIndividual;

public abstract class RCXGAIndividual extends DoubleVectorIndividual 
{
	private static final long serialVersionUID = 1L;
	public abstract double[] getGenome();
	public abstract double[] getPhenome();
	public abstract int[] getMetas();
	public abstract void mirror(EvolutionState state, int thread);
	public abstract int getHammingDistance(int[] before, int[] after);
	public abstract int getLevenshteinDistance(int[] before, int[] after);

	public String getArrayString(int[] a)
	{
		StringBuilder m = new StringBuilder();
		for(int x = 0; x < a.length; x++)
		{
			m.append(a[x]);
		}
		return m.toString();
	}
	public int[] getMetagenesTranslation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int randomValueFromClosedInterval(int min, int max, MersenneTwisterFast random)
    {
    if (max - min < 0) // we had an overflow
        {
        int l = 0;
        do l = random.nextInt();
        while(l < min || l > max);
        return l;
        }
    else return min + random.nextInt(max - min + 1);
    }
}
