package fga;

import ec.*;
import ec.vector.*;
import ec.util.*;

public class FGAIndividual extends BitVectorIndividual {
	
	private static final long serialVersionUID = 1L;
	public static final String P_BITVECTORINDIVIDUAL = "bit-vect-ind";
	public boolean[] genome;
	
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
    }
	 
	public Object clone()
    {
		FGAIndividual myobj = (FGAIndividual) (super.clone());
    
		// must clone the genome
		myobj.genome = (boolean[])(genome.clone());
    
		return myobj;
    } 
		
}
