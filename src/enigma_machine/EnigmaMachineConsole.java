package enigma_machine;


import java.util.Scanner;

public class EnigmaMachineConsole {

	private static void addPlugboardPairOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Pick First Character: ");
		char c1 = sc.nextLine().charAt(0);
		System.out.print("Pick Second Character: ");
		char c2 = sc.nextLine().charAt(0);
		System.out.println();
		
		if (machine.addPlugboardPair(c1, c2)) {
			System.out.println("Pair Added!\n");
		} else {
			System.out.println("Invalid Pair!\n");
		}
		
	}
	
	private static void removePlugboardPairOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Pick First Character: ");
		char c1 = sc.nextLine().charAt(0);
		System.out.print("Pick Second Character: ");
		char c2 = sc.nextLine().charAt(0);
		System.out.println();
		
		if (machine.deletePlugboardPair(c1, c2)) {
			System.out.println("Pair Deleted!\n");
		} else {
			System.out.println("Invalid Pair!\n");
		}
		
	}
	
	private static void setRotorOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Set Slow Rotor Setting (1-26): ");
		int slowSetting = sc.nextInt();
		sc.nextLine();
		System.out.print("Set Middle Rotor Setting (1-26): ");
		int middleSetting = sc.nextInt();
		sc.nextLine();
		System.out.print("Set Slow Rotor Setting (1-26): ");
		int fastSetting = sc.nextInt();
		sc.nextLine();
		System.out.println();
		
		if (machine.setRotors(slowSetting, middleSetting, fastSetting)) {
			System.out.println("Rotor Settings Set!\n");
		} else {
			System.out.println("Invalid Rotor Settings!\n");
		}
		
	}
	
	private static void encryptOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Enter a Message to Encrypt: ");
		String toEncrypt = sc.nextLine();
		System.out.print("Encrypted Message: " + machine.encryptAndDecrypt(toEncrypt) + "\n\n");
		
	}

	private static void decryptOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Enter a Message to Decrypt: ");
		String toDecrypt = sc.nextLine();
		System.out.print("Decrypted Message: " + machine.encryptAndDecrypt(toDecrypt) + "\n\n");
		
	}
	
	private static void decipherOption(EnigmaMachine machine, Scanner sc) {
		
		System.out.print("Enter the Encrypted Message: ");
		String encrypted = sc.nextLine();
		System.out.print("Enter Beginning of the Decrypted Message (as much as you want): ");
		String beginning = sc.nextLine();
		System.out.print("Decrypted Message: " + machine.breakCode(beginning, encrypted) + "\n\n");
		
	}

	public static void main(String[] args) {
		
		EnigmaMachine machine = new EnigmaMachine();
		Scanner sc = new Scanner(System.in);
		boolean quit = false;
		
		while (!quit) {
			
			System.out.println("Pick a Following Setting:\n");
			System.out.println("0: Quit");
			System.out.println("1: Add a Plugboard Pair");
			System.out.println("2: Remove a Plugboard Pair");
			System.out.println("3: Set Rotor Positions");
			System.out.println("4: Encrypt a Message");
			System.out.println("5: Decrypt a Message");
			System.out.println("6: Decipher an Encrypted Message");
			System.out.print("\nOption Selected: ");
			int option = sc.nextInt();
			sc.nextLine();
			System.out.println();
			
			switch(option) {
			
			case 0:
				
				quit = true;
				break;
				
			case 1:
				
				addPlugboardPairOption(machine, sc);
				break;
				
			case 2: 
				
				removePlugboardPairOption(machine, sc);
				break;
				
			case 3:
				
				setRotorOption(machine, sc);
				break;
				
			case 4:
				
				encryptOption(machine, sc);
				break;
				
			case 5:
				
				decryptOption(machine, sc);
				break;
				
			case 6:
				
				decipherOption(machine, sc);
				break;
				
			default:
				System.out.println("Invalid Option!");
			}
		}
		
		
		
		
	}
}
