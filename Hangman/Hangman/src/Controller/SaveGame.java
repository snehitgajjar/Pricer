package Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import Model.Game;



/*
 *  This class contains the gameSaved(Game) method which saves the current object
 *  in file if user wants to save game at any instance of time
 */
public class SaveGame
{
	public void gameSaved(Game word)
	{
		try
		{
			FileOutputStream fs=new FileOutputStream(new File("\\object"));
			ObjectOutputStream os=new ObjectOutputStream(fs);
			os.writeObject(word);
			os.close();
		}
		catch(Exception e)
		{
			System.out.println("Error while saving file to file.");
		}

	}
}
	


