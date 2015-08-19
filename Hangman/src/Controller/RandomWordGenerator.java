package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Random;

/*
 *  Abstract class RandomWordGenerator which provides interface for
 *  creating random word from Files,data structures and any other resource.
 *  It also contains FactoryMethod getRandomWordGeneratorInstance(Object obj).
 *  So basically it is a ClassFactory class.
 */

public abstract class RandomWordGenerator
{
	public abstract String getRandomWord();
	public static File file;
	
	public static RandomWordGenerator getRandomWordGeneratorInstance(Object obj)
	{
		RandomWordGenerator randomWordGenerator=null;
		
		if(obj instanceof File)
		{
			file=(File)obj;
			randomWordGenerator= new RandomWordGeneratorFromFile();
		
		}
		return randomWordGenerator;
	}
}

/*
 *  RandomWordGeneratorFromFile which is subclass of RandomWordGenerator 
 *  and it creates random word from file. If anyone wants word resource as array or
 *  any other data stucture so anyone can make subclass of RandomWordGenerator.
 */

 class RandomWordGeneratorFromFile extends RandomWordGenerator {
	
	
	private static final long serialVersionUID = 1L;
	private BufferedReader reader;
	private LineNumberReader  lnr;
	private Random random;
	private int randomNumberLine;
		
	/*
	 * RandomWordGeneratorFromFile() method sets different variable for this class.
	 * 
	 * getNumberOfLineInFile() method gives the number of line of file 
	 */
	
	public RandomWordGeneratorFromFile()
	{
		
		try
		{
			random=new Random();
			reader=new BufferedReader(new FileReader(file));
			lnr = new LineNumberReader(reader);
		}
		catch(Exception e)
		{
			System.out.println("Exception occured in RandomWordGenerator constructor : "+e );
		}
	}
	
	
	public int getNumberOfLineInFile()
	{
		try
		{
			
			lnr.skip(Long.MAX_VALUE);
			lnr.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Exception occured while getting line number : "+e);
		}
		return lnr.getLineNumber(); 
	}
	
	
	/* 
	 * getRandomWord() method returns the random word from the given files.
	 */
	
	public String getRandomWord()
	{
		String line = null;
		
		try
		{
			reader=new BufferedReader(new FileReader(file));
			randomNumberLine=random.nextInt(getNumberOfLineInFile());

			for(int i=0;i<randomNumberLine-1;i++)
			{
				reader.readLine();
				
			}
			
			line=reader.readLine();
			reader.close();
			
			
		}catch(Exception e)
		{
			System.out.println("Exception generated while reading file :"+e);
		}
		
		return line;
	}

}
