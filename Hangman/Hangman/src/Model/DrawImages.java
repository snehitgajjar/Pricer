package Model;

public class DrawImages {
	
/*
 * This class contains methods to create a hangman graphic.
 */
	public void drawMidImage()
	{
		System.out.println("|---------");
		System.out.println("|        |");
		System.out.println("|        -");
		System.out.println("|       | |");
		System.out.println("|        -");
		System.out.println("|        |");
		System.out.println("|     ___|___");
		System.out.println("|        |");
		System.out.println("|       /\\");
		System.out.println("|      /");
		
	}
	
	public void drawWinImage()
	{
		System.out.println("|---------");
		System.out.println("|        |");
		System.out.println("|        -");
		System.out.println("|       | |");
		System.out.println("|        -");
		System.out.println("|        |");
		System.out.println("|     ___|");
		System.out.println("|        |");
		System.out.println("|         ");
		System.out.println("|      	  ");
		
	}
	
	public void drawLoseImage()
	{
		System.out.println("|---------");
		System.out.println("|        |");
		System.out.println("|        -");
		System.out.println("|       | |");
		System.out.println("|        -");
		System.out.println("|        |");
		System.out.println("|     ___|___");
		System.out.println("|        |");
		System.out.println("|       / \\");
		System.out.println("|      /   \\");
		
	}

	
	
}
