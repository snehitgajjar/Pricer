package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;



public class Game implements Serializable{
	
	/*
	 * Accessors
	 */
	private char[] originalWord;
	private char[] currentWord;
	private int numberOfTryLeft;
	public static boolean isGameOver=false;
	public static boolean isWinner=false;
	private Game object_1;
	Random random;
	private ArrayList<Character> lettersEntered;
	private static Game instance = null;
	
	
	public static synchronized Game getInstance()
	{
		/*
		 *  Apply singlton pattern to the class. It only allow programmer to create one instance of this class
		 */
	        if (instance == null) 
	        {
	            instance = new Game();
	        }
	        return instance;
	}
	
	/*
	 * stateGame(word) method initialize necessary variables 
	 * for the game and also provides the word with mask and return 
	 * that word.
	 */
	public String startGame(char[] word) 
	{
		lettersEntered=new ArrayList<Character>(); 
		originalWord=word;
		currentWord=new char[word.length];
		
		numberOfTryLeft = ((word.length<5) ? word.length : word.length+2);
	
		for(int i=0;i<currentWord.length;i++)
		{
			currentWord[i]='_';
		}
		
		getNumberOfTryLeft();
		System.out.println("You have guessed : ");
		
	
		return new String(currentWord);
	}
	
	/*
	 * swapchar(char) takes character as a input from the View.Main_Class
	 * and unmask the mask word with given character if word contains that character
	 * and returns updated word.
	 */
	
	
	public String swapChar(char c)
	{
		if(!lettersEntered.contains(c))
		{
			
		
		
		lettersEntered.add(c);
		for(int i=0;i<originalWord.length;i++)
		{
			if(originalWord[i]==c)
			{
				currentWord[i]=c;
				
			}
		}
		numberOfTryLeft--;
		
		 if(Arrays.equals(currentWord,originalWord))
		{
			isWinner=true;
		}
		else if(numberOfTryLeft==0)
		{
			isGameOver=true;
		}
		
	
		 getNumberOfTryLeft();
		 printWordsEntered();
		}
		 return new String(currentWord);
	}
	
	
	 /*
	  * printWordsEntered() method prints the word you have entered until now in game.
	  */
	public void printWordsEntered()
	{
		System.out.print("You have guessed : "+lettersEntered.get(0));
		if(lettersEntered.size()>1)
		{
			for(int i=1;i<lettersEntered.size();i++)
			{
				System.out.print(","+lettersEntered.get(i));
			}
		}
		System.out.println();
	}
	
	/*
	 *  getter  methods for currentWord, originalWord, numberOfTryLeft.
	 *  these are the accessors for the given variables
	 */
	
	public String getCurrentWord()
	{
		return new String(currentWord);
	}
	
	public String getOriginalWord()
	{
		return new String(originalWord);
	}
	
	public int getNumberOfTryLeft()
	{
		return numberOfTryLeft;
	}
	
}
