package vitro.util;

import java.util.Random;

/**
* An implementation of the Mersenne Twister algorithm.
*
* @author Jason Hiebel
**/
public class MTRandom extends Random {
	public static final long serialVersionUID = 1L;

	// state details
	private static final int    stateSize = 624;

	// state information
	private long[] state;
	private int    index;

	/**
	* Create a new Mersenne Twister generator based on the JVM's nanotime.
	*
	* @see java.lang.System#nanoTime
	**/
	public MTRandom() {
		this(System.nanoTime());
	}

	/**
	* Create a new Mersenne Twister generator based on a specified seed. Note
	* that only the last 32 bits of the seed are used.
	*
	* @param seed the generating seed.
	**/
	public MTRandom(long seed) {
		super(seed);
		setSeed(seed);
	}

	/**
	* Set this generator's seed. Note that this reconstructs the object in its
	* entirety. Note that only the last 32 bits of the seed are used.
	*
	* @param seed the generating seed.
	**/
	public void setSeed(long seed) {
		this.state = new long[stateSize];
		this.index = 0;

		initializeState(seed);
	}

	/**
	* Get an integer value with the specified number of bits. Implements the
	* core Mersenne Twister algorithm. Additionally implements the bit culling 
	* algorithm
	* <pre> {@code (int)(seed >>> (48 - bits)) } </pre>
	* as described in the super class.
	*
	* @see java.util.Random#next
	*
	* @param  bits the number of bits in the generated random value.
	* @return the generated random value.
	**/
	protected int next(int bits) {
		final int FACTOR0 = 0x9D2C5680;
		final int FACTOR1 = 0xEFC60000;

		// if we have looped, update the state
		if(index == 0) {
			generateState();
		}

		// bitwise magic
		int val = (int)state[index];
		val ^= (val >>> 11);
		val ^= (val <<   7) & FACTOR0;
		val ^= (val <<  15) & FACTOR1;
		val ^= (val >>> 18);

		// increment the index and cull the bits to the specified value.
		index = (index + 1) % stateSize;
		return val >>> (32 - bits);
	}

	// generate a set of values based on the seed
	private void initializeState(long seed) {
		final long MASK   = 0xFFFFFFFF;
		final long FACTOR = 0x6C078965;

		state[0] = seed & MASK;
		for(int x = 1; x < stateSize; x++) {
			state[x] = (FACTOR * (state[x - 1] ^ (state[x - 1] >>> 30)) + x) & MASK;
		}
	}

	// generate a set of values based on the previously generated values
	private void generateState() {
		final long MASK   = 0x7FFFFFFF;
		final long FACTOR = 0x9908B0DF;

		for(int x = 0; x < stateSize; x++) {
			long val = ((0x1 << 32) & state[x]) + MASK & (state[(x + 1) % stateSize]);
			state[x] = state[(x + 397) % stateSize] ^ (val >>> 1);
			if((val % 2) != 0) {
				state[x] = state[x] ^ FACTOR;
			}
		}
	}
}
