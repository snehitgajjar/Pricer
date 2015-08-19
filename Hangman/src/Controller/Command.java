package Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import Model.Game;



/*
 *  Command interface provide an platform to create an Command Pattern 
 *  which executes when particular switch is set on.
 */
public interface Command 
{
        public abstract void execute ( );
      
}

 