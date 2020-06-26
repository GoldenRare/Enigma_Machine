package enigma_machine_testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import enigma_machine.EnigmaMachine;

class EnigmaMachineTest {

	private final int LENGTH_OF_ALPHABET = 26;
	
	@Test
	void test01_defaultPlugboard() {
	
		EnigmaMachine machine = new EnigmaMachine();
		HashMap<Character, Character> plugboard = machine.getPlugboard();
		
		Set<Character> alphabet = new HashSet<Character>();
		
		for (char i = 'A'; i <= 'Z'; i++) {
			
			alphabet.add(i);
			
		}
		
		
		assertEquals(LENGTH_OF_ALPHABET, plugboard.size());
		assertEquals(alphabet, plugboard.keySet());
		
		for (Character c : alphabet) {
			
			assertEquals(c, plugboard.get(c));
			
		}
		
	}
	//Check the list for the hash pairs
	@Test
	void test02_addPlugboardPair() {
		
		EnigmaMachine machine = new EnigmaMachine();
		boolean expected = true;
		boolean got = machine.addPlugboardPair('a', 'b');
		assertEquals(expected, got); //Adding two lowercase letters

		got = machine.addPlugboardPair('Q', 'Z');
		assertEquals(expected, got); //Adding two uppercase letters
		
		got = machine.addPlugboardPair('X', 'h');
		assertEquals(expected, got); //Adding an uppercase and a lowercase letter
		
		expected = false;
		
		got = machine.addPlugboardPair('Y', '?');
		assertEquals(expected, got); //Adding an invalid character
		
		got = machine.addPlugboardPair('P', 'P');
		assertEquals(expected, got); //Adding the same character
		
		got = machine.addPlugboardPair('q', 'S');
		assertEquals(expected, got); //Adding a pair that is already in another pair;
		
	}
	//Check list for the hash pairs
	@Test
	void test03_deletePlugboardPair() {
		
		EnigmaMachine machine = new EnigmaMachine();
		boolean expected = false;
		boolean got = machine.deletePlugboardPair('c', 'c');
		assertEquals(expected, got); //Deleting the same character

		got = machine.deletePlugboardPair('Q', ':');
		assertEquals(expected, got); //Deleting invalid character
		
		machine.addPlugboardPair('X', 'h');
		got = machine.deletePlugboardPair('x', 'D');
		assertEquals(expected, got); //Deleting something that's already in a different pair
		
		expected = true;
		
		got = machine.deletePlugboardPair('x', 'h');
		assertEquals(expected, got); //Deleting the pair
		
		expected = false;
		got = machine.deletePlugboardPair('x', 'h');
		assertEquals(expected, got); //Deleting the pair again
		
		expected = true;
		
		machine.addPlugboardPair('S', 'Q');
		got = machine.deletePlugboardPair('q', 'S');
		assertEquals(expected, got); //Deleting the pair in opposite parameters
		
	}
	
	@Test
	void test04_setRotors() {
		
		EnigmaMachine machine = new EnigmaMachine();
		boolean expected = false;
		boolean got = machine.setRotors(0, 3, 6);
		assertEquals(expected, got); //Invalid Settings
		
		got = machine.setRotors(2, 27, 0);
		assertEquals(expected, got); //Invalid Settings
		
		got = machine.setRotors(20, 26, 100);
		assertEquals(expected, got); //Invalid Settings
		
		expected = true;
		
		//////////////////////////////////
		got = machine.setRotors(3, 11, 6);
		assertEquals(expected, got); //Valid Setting
		
		//Testing the HashMaps
		assertEquals(fastRotor6thSetting(), machine.fastRotorForward()); //Fast 6th Setting
		assertEquals(middleRotor11thSetting(), machine.middleRotorForward()); //Middle 11th Setting
		assertEquals(slowRotor3rdSetting(), machine.slowRotorForward()); //Slow 3rd Setting
		
		//Testing if state is set
		assertEquals(3, machine.slowRotorSetting());
		assertEquals(11, machine.middleRotorSetting());
		assertEquals(6, machine.fastRotorSetting());
		//////////////////////////////////
		
		//////////////////////////////////
		got = machine.setRotors(3, 11, 6);
		assertEquals(expected, got); //Valid - Setting the same setting again
		
		//Testing the HashMaps
		assertEquals(fastRotor6thSetting(), machine.fastRotorForward()); //Fast 6th Setting
		assertEquals(middleRotor11thSetting(), machine.middleRotorForward()); //Middle 11th Setting
		assertEquals(slowRotor3rdSetting(), machine.slowRotorForward()); //Slow 3rd Setting
		
		//Testing if state is set
		assertEquals(3, machine.slowRotorSetting());
		assertEquals(11, machine.middleRotorSetting());
		assertEquals(6, machine.fastRotorSetting());
		//////////////////////////////////
		
		
		//////////////////////////////////
		EnigmaMachine defaultMachine = new EnigmaMachine();
		got = machine.setRotors(1, 1, 1);
		assertEquals(expected, got); //Valid Setting
		
		//Testing the HashMaps
		assertEquals(defaultMachine.fastRotorForward(), machine.fastRotorForward()); //Default Setting
		assertEquals(defaultMachine.middleRotorForward(), machine.middleRotorForward()); //Default Setting
		assertEquals(defaultMachine.slowRotorForward(), machine.slowRotorForward()); //Default Setting
		
		//Testing if state is set
		assertEquals(1, machine.slowRotorSetting());
		assertEquals(1, machine.middleRotorSetting());
		assertEquals(1, machine.fastRotorSetting());
		//////////////////////////////////
		
	}
	
	@Test
	void test05_encryptAndDecrypt() {
		
		EnigmaMachine machine = new EnigmaMachine();
		String test = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt "
				+ "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris "
				+ "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit "
				+ "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, "
				+ "sunt in culpa qui officia deserunt mollit anim id est laborum."; //369 A-Z
		String encrypted = machine.encryptAndDecrypt(test);
		
		int expected = test.length();
		int got = encrypted.length();
		assertEquals(expected, got); //Strings are the same size
		
		for (int i = 0; i < test.length(); i++) {
			
			char c = Character.toUpperCase(test.charAt(i));
			
			if ((c < 'A') || (c > 'Z')) continue;
			
			assertFalse(c == encrypted.charAt(i)); //An Enigma Machine can't map a letter to itself 
		}
		
		// Rotors set after encrypting //
		got = machine.slowRotorSetting();
		expected = 1;
		assertEquals(expected, got);
		
		got = machine.middleRotorSetting();
		expected = 15;
		assertEquals(expected, got);
		
		got = machine.fastRotorSetting();
		expected = 6;
		assertEquals(expected, got);
		////////////////////////////////
		
		machine.setRotors(1, 1, 1);
		machine.addPlugboardPair('l', 'c'); machine.addPlugboardPair('o', 'e'); machine.addPlugboardPair('t', 'n');
		String encrypted2 = machine.encryptAndDecrypt(test);
		
		expected = test.length();
		got = encrypted2.length();
		assertEquals(expected, got); //Strings are the same size
		assertFalse(encrypted.equals(encrypted2)); //Different since contains plugboard pairs at same settings
		
	}
	
	@Test
	void test06_breakCode() {
		
		EnigmaMachine machine = new EnigmaMachine();
		String test = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt "
				+ "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris "
				+ "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit "
				+ "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, "
				+ "sunt in culpa qui officia deserunt mollit anim id est laborum."; //369 A-Z
		
		machine.setRotors(23, 20, 7);
		machine.addPlugboardPair('l', 'd'); machine.addPlugboardPair('a', 'b'); machine.addPlugboardPair('g', 't'); machine.addPlugboardPair('j', 'e');
		String encrypted = machine.encryptAndDecrypt(test);
		String decryptAttempt = machine.breakCode(test, encrypted);
		System.out.println(decryptAttempt);
		char checkChar = Character.toUpperCase(test.charAt(0));
		for (int i = 0; i < test.length(); i++) {
			
			if ((Character.toUpperCase(encrypted.charAt(i)) == checkChar) || (Character.toUpperCase(test.charAt(i)) == checkChar)) {
				
				// Guarantee that wherever the first character of the beginning occurs , then the character in the break attempt
				// must be correct
				assertEquals(Character.toUpperCase(test.charAt(i)), decryptAttempt.charAt(i));
				
			}
		}
		
		
		
		String beginning = "Lorem ipsum dolor sit amet";
		decryptAttempt = machine.breakCode(beginning, encrypted);
		System.out.println(decryptAttempt);
		checkChar = Character.toUpperCase(beginning.charAt(0));
		for (int i = 0; i < beginning.length(); i++) {
			
			if ((Character.toUpperCase(encrypted.charAt(i)) == checkChar) || (Character.toUpperCase(beginning.charAt(i)) == checkChar)) {
				
				// Guarantee that wherever the first character of the beginning occurs , then the character in the break attempt
				// must be correct
				assertEquals(Character.toUpperCase(beginning.charAt(i)), decryptAttempt.charAt(i));
				
			}
		}
		
	}
	
	private HashMap<Character, Integer> fastRotor6thSetting() {
		
		HashMap<Character, Integer> firstRotorForward = new HashMap<Character, Integer>();
		
		firstRotorForward.put('A', 16);
		firstRotorForward.put('B', 10);
		firstRotorForward.put('C', 7);
		firstRotorForward.put('D', 26);
		firstRotorForward.put('E', 2);
		firstRotorForward.put('F', 18);
		firstRotorForward.put('G', 3);
		firstRotorForward.put('H', 15);
		firstRotorForward.put('I', 13);
		firstRotorForward.put('J', 23);
		firstRotorForward.put('K', 20);
		firstRotorForward.put('L', 19);
		firstRotorForward.put('M', 22);
		firstRotorForward.put('N', 12);
		firstRotorForward.put('O', 4);
		firstRotorForward.put('P', 25);
		firstRotorForward.put('Q', 24);
		firstRotorForward.put('R', 17);
		firstRotorForward.put('S', 21);
		firstRotorForward.put('T', 11);
		firstRotorForward.put('U', 8);
		firstRotorForward.put('V', 6);
		firstRotorForward.put('W', 5);
		firstRotorForward.put('X', 1);
		firstRotorForward.put('Y', 9);
		firstRotorForward.put('Z', 14);
		
		return firstRotorForward;
	}
	
	private HashMap<Integer, Integer> middleRotor11thSetting() {
		
		HashMap<Integer, Integer> secondRotorForward = new HashMap<Integer, Integer>();
		
		secondRotorForward.put(1, 15);
		secondRotorForward.put(2, 10);
		secondRotorForward.put(3, 22);
		secondRotorForward.put(4, 16);
		secondRotorForward.put(5, 18);
		secondRotorForward.put(6, 23);
		secondRotorForward.put(7, 26);
		secondRotorForward.put(8, 14);
		secondRotorForward.put(9, 9);
		secondRotorForward.put(10, 5);
		secondRotorForward.put(11, 20);
		secondRotorForward.put(12, 17);
		secondRotorForward.put(13, 6);
		secondRotorForward.put(14, 11);
		secondRotorForward.put(15, 13);
		secondRotorForward.put(16, 25);
		secondRotorForward.put(17, 3);
		secondRotorForward.put(18, 1);
		secondRotorForward.put(19, 2);
		secondRotorForward.put(20, 24);
		secondRotorForward.put(21, 7);
		secondRotorForward.put(22, 4);
		secondRotorForward.put(23, 19);
		secondRotorForward.put(24, 12);
		secondRotorForward.put(25, 21);
		secondRotorForward.put(26, 8);
		
		return secondRotorForward;
	}
	
	private HashMap<Integer, Integer> slowRotor3rdSetting() {
		
		HashMap<Integer, Integer> thirdRotorForward = new HashMap<Integer, Integer>();
		
		thirdRotorForward.put(1, 10);
		thirdRotorForward.put(2, 6);
		thirdRotorForward.put(3, 14);
		thirdRotorForward.put(4, 22);
		thirdRotorForward.put(5, 20);
		thirdRotorForward.put(6, 17);
		thirdRotorForward.put(7, 18);
		thirdRotorForward.put(8, 12);
		thirdRotorForward.put(9, 5);
		thirdRotorForward.put(10, 4);
		thirdRotorForward.put(11, 15);
		thirdRotorForward.put(12, 9);
		thirdRotorForward.put(13, 21);
		thirdRotorForward.put(14, 16);
		thirdRotorForward.put(15, 25);
		thirdRotorForward.put(16, 7);
		thirdRotorForward.put(17, 13);
		thirdRotorForward.put(18, 26);
		thirdRotorForward.put(19, 23);
		thirdRotorForward.put(20, 24);
		thirdRotorForward.put(21, 19);
		thirdRotorForward.put(22, 3);
		thirdRotorForward.put(23, 1);
		thirdRotorForward.put(24, 11);
		thirdRotorForward.put(25, 2);
		thirdRotorForward.put(26, 8);
		
		return thirdRotorForward;
		
	}

}
