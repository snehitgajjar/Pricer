package Controller;

import Model.Game;



/*
 *  SaveGameCommand class provides the platform between Command and Swtich for SaveGame class.
 */
public class SaveGameCommand implements Command {
	private SaveGame saveGame;
	private Game word;
	
	public SaveGameCommand(SaveGame saveGame,Game w)
	{
		this.saveGame=saveGame;
		word=w;
	}
	
	@Override
	public void execute() 
	{
		
		saveGame.gameSaved(word);
	}

}
