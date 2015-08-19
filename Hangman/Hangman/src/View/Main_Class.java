package View;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import Controller.RandomWordGenerator;
import Controller.RandomWordGenerator;
import Controller.SaveGame;
import Controller.SaveGameCommand;
import Controller.Switch;
import Model.DrawImages;
import Model.Game;


public class Main_Class {
	
	private String myword;
	private Game wf=null;
	private Scanner scan;
	private ObjectInputStream ois;
	private FileInputStream fis;
	private String str="";
	private static File objectFile,inputFile;
	
	public Main_Class(String inputFilePath)
	{
		objectFile=new File("\\object");	//object file to save game object
		inputFile=new File(inputFilePath);	//inputfile which takes command line argument as path
		
		RandomWordGenerator r=RandomWordGenerator.getRandomWordGeneratorInstance(inputFile);
		
		scan=new Scanner(System.in);
		
		try {
			
			if(objectFile.exists()) 	// checks already game is saved or not
			{
				fis=new FileInputStream(objectFile);			
				ois=new ObjectInputStream(fis);
				wf=(Game)ois.readObject();
				ois.close();
				System.out.println("Welcome back !!");
				wf.printWordsEntered();
				System.out.println("Word : "+wf.getCurrentWord());
		
			}
			else						// else it create new Game
			{
			
				objectFile.createNewFile();
				myword=r.getRandomWord();
				wf=Game.getInstance();
				System.out.println("Word :"+wf.startGame(myword.toCharArray()));
			}
		
		} 
		catch (Exception e1) 
		{
			
			System.out.print("Error in reading from file"+e1);
		}
		
		
		
		while(Game.isGameOver!=true && Game.isWinner!=true )
		{
			
			System.out.println("You have "+wf.getNumberOfTryLeft()+" try remaining.");
			System.out.println("Enter '$' at anytime to save a game.");
			System.out.print("Guess a Letter : ");
			
			char c=scan.next().charAt(0); // get character input from user
			System.out.println();
			if(c=='$')			// to save game
			{
				
				/* 
				 * Below code performs command pattern and it saves file when command is executed
				 */
				SaveGame saveGame=new SaveGame();
				SaveGameCommand saveGameCommand =new SaveGameCommand(saveGame,wf);
				Switch sw = new Switch(saveGameCommand);
				sw.flipUp();
			
				break;
			}
			else
			{
				System.out.println("Word :"+wf.swapChar(c)); //else continue game
			}
		}
		
		if(Game.isWinner==true)
		{
			System.out.println("You won !!!");
			new DrawImages().drawWinImage();	
			objectFile.delete();
		}
		else if(Game.isGameOver==true)
		{
			
			System.out.println("You lost. Better luck next time");
			System.out.println("Correct word was :"+wf.getOriginalWord());
			new DrawImages().drawLoseImage();
			objectFile.delete();
		
			
		}
		else
		{
			System.out.println("Game saved successfully.");
		}
	}
	
	/*
	public void saveObject(Game wordForGame)
	{
		try
		{
			FileOutputStream fs=new FileOutputStream(new File("/Users/snehitgajjar/Documents/workspace/Hangman/Input/object"));
			ObjectOutputStream os=new ObjectOutputStream(fs);
			os.writeObject(wordForGame);
			os.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception in saveObject : "+e);
		}
	}
	
	*/
	
	
	
	public static void main(String[] args)
	{
		/*
		 * Here the instance of the Main_Class is created which 
		 * calls the constructor of the Main_Class.
		 * 
		 * This class contains MVC architecture.
		 * 
		 */
		final Main_Class mC=new Main_Class(args[0]);
	
	}
}
