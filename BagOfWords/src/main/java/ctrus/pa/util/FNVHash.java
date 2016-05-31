/*******************************************************************************
 * Copyright (c) 2015, 2016  Naveen Kulkarni
 *
 * This file is part of Bag of Words program. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Naveen Kulkarni (naveen.kulkarni@research.iiit.ac.in)
 *     
 *******************************************************************************/

package ctrus.pa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* 
 * For all practical non cryptographic hashing, a 32 bit FNV hash is good.
 * More details on FNV hash can be found here - 
 * @link http://www.isthe.com/chongo/tech/comp/fnv/index.html
 * A brief introduction to various hashing functions can be found here -
 * @link http://eternallyconfuzzled.com/tuts/algorithms/jsw_tut_hashing.aspx
 */
public class FNVHash {
	// 32-bit hash 
	private static final int FNV_PRIME_32 			= 0x01000193;
	private static final int FNV_OFFSET_BASIS_32 	= 0x811C9DC5;
	
	// 64-bit hash
	private static final long FNV_PRIME_64 			= 0x100000001b3L;
	private static final long FNV_OFFSET_BASIS_64 	= 0xcbf29ce484222325L;
	
	// Seeds	
	private static final Random rnd = new Random(9742);
	private int seed1 = 0;
	private int seed2 = 0;

	private FNVHash(int s1, int s2) {
		seed1 = s1;
		seed2 = s2;
	}
	
	// By changing seeds we can get different FNV hash functions
	public final int hash32(byte[] data) {
		int magic = FNV_OFFSET_BASIS_32;
		for(int i=0; i<data.length; i++) {
			magic *= FNV_PRIME_32 ^ seed1;
			magic *= FNV_PRIME_32 ^ seed2;
			magic ^= data[i];			
		}
		return magic;
	}
	
	// By changing seeds we can get different FNV hash functions
	public final long hash64(byte[] data) {
		long magic = FNV_OFFSET_BASIS_64;
		for(int i=0; i<data.length; i++) {
			magic *= FNV_PRIME_64 ^ seed1;
			magic *= FNV_PRIME_64 ^ seed2;
			magic ^= data[i];			
		}
		return magic;
	}	
	
	public final String hash32(String data) {
		return Integer.toHexString(hash32(data.getBytes()));
	}
	
	public final String hash64(String data) {
		return Long.toHexString(hash64(data.getBytes()));
	}
	
	public static final FNVHash newHashFunction() {
		return new FNVHash(rnd.nextInt(), rnd.nextInt());
	}
	
	public static final List<FNVHash> newHashFunctions(int numberOfFunctions) {
		List<FNVHash> hashFns = new ArrayList<FNVHash>();
		for(int i=0; i<numberOfFunctions; i++)
			hashFns.add(new FNVHash(rnd.nextInt(), rnd.nextInt()));
		return hashFns;
	}
	
}
