package Controller;


/*
 * Switch class calls the execute method of the Command interface based on the 
 * given object type.
 */

public class Switch 
{
    private Command saveCommand;
    
    public Switch( Command save) 
    {
            saveCommand = save;  
           
    }
    public void flipUp( ) 
    {  
              saveCommand . execute ( ) ;                           
    }
    
}
