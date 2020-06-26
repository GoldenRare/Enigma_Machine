package enigma_machine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EnigmaMachine {

	private HashMap<Character, Character> plugboard;
	private HashMap<Character, Integer> fastRotorForward;
	private HashMap<Integer, Integer> middleRotorForward; 
	private HashMap<Integer, Integer> slowRotorForward;
	private HashMap<Integer, Integer> reflector;
	private HashMap<Integer, Integer> slowRotorBackward; 
	private HashMap<Integer, Integer> middleRotorBackward; 
	private HashMap<Integer, Character> fastRotorBackward;
	
	private int fastRotorSetting;
	private int middleRotorSetting;
	private int slowRotorSetting;
	
	public EnigmaMachine() {
		
		this.plugboard = initializePlugboard();
		
		this.fastRotorForward = initializeFirstRotorForward();
		this.middleRotorForward = initializeSecondRotorForward();
		this.slowRotorForward = initializeThirdRotorForward();
		
		this.reflector = initializeReflector();
		
		this.slowRotorBackward = flipHashMap(this.slowRotorForward);
		this.middleRotorBackward = flipHashMap(this.middleRotorForward);
		this.fastRotorBackward = flipHashMap(this.fastRotorForward);
		
		this.fastRotorSetting = this.middleRotorSetting = this.slowRotorSetting = 1;
		
	}
	
	// Supports up to 13 pairs (Original was 10)
	public boolean addPlugboardPair(char c1, char c2) {
		
		c1 = Character.toUpperCase(c1);
		c2 = Character.toUpperCase(c2);
		boolean result = false;
		
		if ((c1 < 'A') || (c1 > 'Z') || (c2 < 'A') || (c2 > 'Z') || (c1 == c2)) return result;
		
		if ((c1 == this.plugboard.get(c1)) && (c2 == this.plugboard.get(c2))) {
			
			this.plugboard.put(c1, c2);
			this.plugboard.put(c2, c1);
			result = true;
		
		}
		
		
		return result;
	}
	
	public boolean deletePlugboardPair(char c1, char c2) {
		
		c1 = Character.toUpperCase(c1);
		c2 = Character.toUpperCase(c2);
		boolean result = false;
		
		if ((c1 < 'A') || (c1 > 'Z') || (c2 < 'A') || (c2 > 'Z') || (c1 == c2)) return result;
		
		if ((c1 == this.plugboard.get(c2)) && (c2 == this.plugboard.get(c1))) {
			
			this.plugboard.put(c1, c1);
			this.plugboard.put(c2, c2);
			result = true;
			
		}
		
		return result;
	}
	
	public boolean setRotors(int slow, int middle, int fast) {
		
		boolean result = false;
		
		if ((slow < 1) || (slow > 26) || (middle < 1) || (middle > 26) || (fast < 1) || (fast > 26)) return result;
		
		setRotor(this.slowRotorSetting, slow, this.slowRotorForward);
		setRotor(this.middleRotorSetting, middle, this.middleRotorForward);
		setRotor(this.fastRotorSetting, fast, this.fastRotorForward);
		
		this.slowRotorBackward = flipHashMap(this.slowRotorForward);
		this.middleRotorBackward = flipHashMap(this.middleRotorForward);
		this.fastRotorBackward = flipHashMap(this.fastRotorForward);
		
		this.slowRotorSetting = slow;
		this.middleRotorSetting = middle;
		this.fastRotorSetting = fast;
		
		result = true;
		return result;
		
	}
	
	public String encryptAndDecrypt(String s) {
		
		String result = "";
		
		for (int i = 0; i < s.length(); i++) {
			
			char c = Character.toUpperCase(s.charAt(i));
			if ((c < 'A') || (c > 'Z')) {
				result += s.charAt(i);
				continue;
			}
			
			char output = goThroughMachine(c);
			result += output;
			
		}
		
		return result;
	}

	// Making a series of deductions if we assume the plugboard setting of one of the settings
	public String breakCode(String beginning, String encrypted) {

		EnigmaMachine machine = new EnigmaMachine();
		Set<String> deductions = new HashSet<String>();
		Set<String> pairsInUse = new HashSet<String>();
		Set<Character> charsInUse = new HashSet<Character>();
		char guess = 'A';
		int fastSetting, middleSetting, slowSetting;
		fastSetting = middleSetting = slowSetting = 1;
		
		for (int i = -1; i < beginning.length(); i++) { //Should include the condition that we check if 26 26 26 was reached (Beginning not the beginning)
			
			//Initialize our guess and settings
			if (i == -1) {
				
				machine = new EnigmaMachine();
				machine.setRotors(slowSetting, middleSetting, fastSetting);
				
				if (guess > 'Z') {
					
					machine.updateRotors();
					slowSetting = machine.slowRotorSetting;
					middleSetting = machine.middleRotorSetting;
					fastSetting = machine.fastRotorSetting;
					deductions = new HashSet<String>();
					guess = 'A';
					
				}
				
				char firstChar = Character.toUpperCase(beginning.charAt(0));

				if (!deductions.contains("" + firstChar + guess)) {
					
					makeDeductions(firstChar, guess, charsInUse, pairsInUse, deductions, machine);
					
				} else {

					i = -2;
				}
				
				guess++;
				
			} else {
				
				char encryptedChar = Character.toUpperCase(encrypted.charAt(i));
				char beginningChar = Character.toUpperCase(beginning.charAt(i));
				
				if ((encryptedChar < 'A') || (encryptedChar > 'Z') || (beginningChar < 'A') || (beginningChar > 'Z')) continue;
				
				if (charsInUse.contains(encryptedChar)) {
					
					char c = machine.outputChar(encryptedChar);
					if (!pairsInUse.contains("" + c + beginningChar)) {
						if (!charsInUse.contains(beginningChar) && !charsInUse.contains(c)) {
								
							makeDeductions(c, beginningChar, charsInUse, pairsInUse, deductions, machine);
								
						} else {
								
							i = -2;
							pairsInUse = new HashSet<String>();
							charsInUse = new HashSet<Character>();
							
						}	
					} 
					
				} else if (charsInUse.contains(beginningChar)) {
					
					char c = machine.outputChar(beginningChar);
					if (!pairsInUse.contains("" + c + encryptedChar)) {
						if (!charsInUse.contains(encryptedChar) && !charsInUse.contains(c)) {
							
							makeDeductions(c, encryptedChar, charsInUse, pairsInUse, deductions, machine);
							
						} else {

							i = -2;
							pairsInUse = new HashSet<String>();
							charsInUse = new HashSet<Character>();
							
						}
					}	
				} else machine.updateRotors(); //Cannot make a deduction since char is not in use
			}		
		}

		machine.setRotors(slowSetting, middleSetting, fastSetting);
		return machine.encryptAndDecrypt(encrypted);
		
	}

	
	
	public HashMap<Character, Character> getPlugboard() {
		
		return this.plugboard;
		
	}
	
	public HashMap<Character, Integer> fastRotorForward() {
		
		return this.fastRotorForward;
		
	}
	
	public HashMap<Integer, Integer> middleRotorForward() {
		
		return this.middleRotorForward;
		
	}
	
	public HashMap<Integer, Integer> slowRotorForward() {
		
		return this.slowRotorForward;
		
	}
	
	public int fastRotorSetting() {
		
		return this.fastRotorSetting;
		
	}
	
	public int middleRotorSetting() {
		
		return this.middleRotorSetting;
		
	}
	
	public int slowRotorSetting() {
		
		return this.slowRotorSetting;
		
	}
	
	private HashMap<Character, Character> initializePlugboard() {
		
		HashMap<Character, Character> defaultPlugboard = new HashMap<Character, Character>(); 
		for (char i = 'A'; i <= 'Z'; i++) {
			
			defaultPlugboard.put(i, i);
			
		}
		
		return defaultPlugboard;
	}
	
	private HashMap<Character, Integer> initializeFirstRotorForward() {
		
		HashMap<Character, Integer> firstRotorForward = new HashMap<Character, Integer>();
		
		firstRotorForward.put('A', 11);
		firstRotorForward.put('B', 5);
		firstRotorForward.put('C', 2);
		firstRotorForward.put('D', 21);
		firstRotorForward.put('E', 23);
		firstRotorForward.put('F', 13);
		firstRotorForward.put('G', 24);
		firstRotorForward.put('H', 10);
		firstRotorForward.put('I', 8);
		firstRotorForward.put('J', 18);
		firstRotorForward.put('K', 15);
		firstRotorForward.put('L', 14);
		firstRotorForward.put('M', 17);
		firstRotorForward.put('N', 7);
		firstRotorForward.put('O', 25);
		firstRotorForward.put('P', 20);
		firstRotorForward.put('Q', 19);
		firstRotorForward.put('R', 12);
		firstRotorForward.put('S', 16);
		firstRotorForward.put('T', 6);
		firstRotorForward.put('U', 3);
		firstRotorForward.put('V', 1);
		firstRotorForward.put('W', 26);
		firstRotorForward.put('X', 22);
		firstRotorForward.put('Y', 4);
		firstRotorForward.put('Z', 9);
		
		return firstRotorForward;
	}
	
	private HashMap<Integer, Integer> initializeSecondRotorForward() {
		
		HashMap<Integer, Integer> secondRotorForward = new HashMap<Integer, Integer>();
		
		secondRotorForward.put(1, 5);
		secondRotorForward.put(2, 26);
		secondRotorForward.put(3, 12);
		secondRotorForward.put(4, 6);
		secondRotorForward.put(5, 8);
		secondRotorForward.put(6, 13);
		secondRotorForward.put(7, 16);
		secondRotorForward.put(8, 4);
		secondRotorForward.put(9, 25);
		secondRotorForward.put(10, 21);
		secondRotorForward.put(11, 10);
		secondRotorForward.put(12, 7);
		secondRotorForward.put(13, 22);
		secondRotorForward.put(14, 1);
		secondRotorForward.put(15, 3);
		secondRotorForward.put(16, 15);
		secondRotorForward.put(17, 19);
		secondRotorForward.put(18, 17);
		secondRotorForward.put(19, 18);
		secondRotorForward.put(20, 14);
		secondRotorForward.put(21, 23);
		secondRotorForward.put(22, 20);
		secondRotorForward.put(23, 9);
		secondRotorForward.put(24, 2);
		secondRotorForward.put(25, 11);
		secondRotorForward.put(26, 24);
		
		return secondRotorForward;
	}
	
	private HashMap<Integer, Integer> initializeThirdRotorForward() {
		
		HashMap<Integer, Integer> thirdRotorForward = new HashMap<Integer, Integer>();
		
		thirdRotorForward.put(1, 8);
		thirdRotorForward.put(2, 4);
		thirdRotorForward.put(3, 12);
		thirdRotorForward.put(4, 20);
		thirdRotorForward.put(5, 18);
		thirdRotorForward.put(6, 15);
		thirdRotorForward.put(7, 16);
		thirdRotorForward.put(8, 10);
		thirdRotorForward.put(9, 3);
		thirdRotorForward.put(10, 2);
		thirdRotorForward.put(11, 13);
		thirdRotorForward.put(12, 7);
		thirdRotorForward.put(13, 19);
		thirdRotorForward.put(14, 14);
		thirdRotorForward.put(15, 23);
		thirdRotorForward.put(16, 5);
		thirdRotorForward.put(17, 11);
		thirdRotorForward.put(18, 24);
		thirdRotorForward.put(19, 21);
		thirdRotorForward.put(20, 22);
		thirdRotorForward.put(21, 17);
		thirdRotorForward.put(22, 1);
		thirdRotorForward.put(23, 25);
		thirdRotorForward.put(24, 9);
		thirdRotorForward.put(25, 26);
		thirdRotorForward.put(26, 6);
		
		return thirdRotorForward;
		
	}
	
	private HashMap<Integer, Integer> initializeReflector() {
		
		HashMap<Integer, Integer> reflector = new HashMap<Integer, Integer>();
		
		reflector.put(1, 6);
		reflector.put(2, 10);
		reflector.put(3, 15);
		reflector.put(4, 25);
		reflector.put(5, 23);
		reflector.put(6, 1);
		reflector.put(7, 17);
		reflector.put(8, 12);
		reflector.put(9, 22);
		reflector.put(10, 2);
		reflector.put(11, 21);
		reflector.put(12, 8);
		reflector.put(13, 16);
		reflector.put(14, 19);
		reflector.put(15, 3);
		reflector.put(16, 13);
		reflector.put(17, 7);
		reflector.put(18, 26);
		reflector.put(19, 14);
		reflector.put(20, 24);
		reflector.put(21, 11);
		reflector.put(22, 9);
		reflector.put(23, 5);
		reflector.put(24, 20);
		reflector.put(25, 4);
		reflector.put(26, 18);
		
		return reflector;
		
	}
	
	private <K, V> HashMap<V, K> flipHashMap(HashMap<K, V> map) {
		
		HashMap<V, K> flippedMap = new HashMap<V, K>();
		
		for (K key : map.keySet()) {
			
			flippedMap.put(map.get(key), key);
			
		}
			
		return flippedMap;
	}
	
	private <K> void setRotor(int oldSetting, int newSetting, HashMap<K, Integer> rotorConfig) {
		
		int difference = newSetting - oldSetting - 1;
		
		if (difference != -1) {
			
			for (K key : rotorConfig.keySet()) {
				
				rotorConfig.put(key, ((rotorConfig.get(key) + difference + 26) % 26) + 1);
			}
		}
	}
	
	private char outputChar(char c) {
		
		c = Character.toUpperCase(c);
		if ((c < 'A') || (c > 'Z')) return 0;
		
		c = this.plugboard.get(c);
		int j = this.fastRotorForward.get(c);
		j = this.middleRotorForward.get(j);
		j = this.slowRotorForward.get(j);
		j = this.reflector.get(j);
		j = this.slowRotorBackward.get(j);
		j = this.middleRotorBackward.get(j);
		c = this.fastRotorBackward.get(j);
		
		int fastSetting = this.fastRotorSetting + 1;
		int middleSetting = this.middleRotorSetting;
		int slowSetting = this.slowRotorSetting;
		
		if (fastSetting == 27) {
			
			fastSetting = 1;
			middleSetting += 1;
			
		}
		
		if (middleSetting == 27) {
			
			middleSetting = 1;
			slowSetting += 1;
			
		}
		
		if (slowSetting == 27) {
			
			slowSetting = 1;
		}
		
		this.setRotors(slowSetting, middleSetting, fastSetting);
	
		return c;
		
	}
	/*
	private boolean potentialWord(String guess, String potentialWord) {
		
		boolean result = false;
		
		for (int i = 0; i < potentialWord.length(); i++) {
			
			if (guess.charAt(i) == potentialWord.charAt(i)) return result;
			
		}
		
		result = true;
		return result;
	}*/
	
	
	
	private boolean makePair(char output, char neededOutput, Set<String> deductions) {
		
		String lookup = "" + output + neededOutput;
		String lookupBackwards = "" + neededOutput + output;
		
		return deductions.add(lookup) && deductions.add(lookupBackwards);
		
		
	}
	
	private char goThroughMachine(char c) {
		
		char start = this.plugboard.get(c);
		int middle = this.fastRotorForward.get(start);
		middle = this.middleRotorForward.get(middle);
		middle = this.slowRotorForward.get(middle);
		middle = this.reflector.get(middle);
		middle = this.slowRotorBackward.get(middle);
		middle = this.middleRotorBackward.get(middle);
		char end = this.fastRotorBackward.get(middle);
		end = this.plugboard.get(end);
		
		updateRotors();
		return end;
	}
	
	private void updateRotors() {
		
		int fastSetting = this.fastRotorSetting + 1;
		int middleSetting = this.middleRotorSetting;
		int slowSetting = this.slowRotorSetting;
		
		if (fastSetting == 27) {
			
			fastSetting = 1;
			middleSetting += 1;
			
		}
		
		if (middleSetting == 27) {
			
			middleSetting = 1;
			slowSetting += 1;
			
		}
		
		if (slowSetting == 27) {
			
			slowSetting = 1;
			
		}
		
		setRotors(slowSetting, middleSetting, fastSetting);
		
	}
	
	private void makeDeductions(char firstChar, char secondChar, Set<Character> charsInUse, Set<String> pairsInUse,
			Set<String> deductions, EnigmaMachine machine) {
		
		makePair(firstChar, secondChar, pairsInUse);
		makePair(firstChar, secondChar, deductions); 
		charsInUse.add(firstChar); charsInUse.add(secondChar);
		machine.addPlugboardPair(firstChar, secondChar);
		
	}
	
	public static void main(String[] args) {
		
		EnigmaMachine machine = new EnigmaMachine(); 
		machine.addPlugboardPair('b', 'c'); machine.addPlugboardPair('h', 'g'); machine.addPlugboardPair('e', 'l'); machine.addPlugboardPair('o', 'z'); machine.addPlugboardPair('a', 'm');
		machine.setRotors(26, 26, 26);
		  
		String encrypted = machine.encryptAndDecrypt("Randomrrrrrrrrrrrrrr text with absolutely no meaning to it");
		machine.setRotors(26, 26, 26);
		String decrypted = machine.encryptAndDecrypt(encrypted);
		  
		System.out.println("Encrypted Message: " + encrypted);  
		System.out.println("Decrypted Message: " + decrypted);
		System.out.println("Decrypted Message: " + machine.breakCode("Randomrrrrrrrrrrrrrr", encrypted));
		 
		
	}
	
}
