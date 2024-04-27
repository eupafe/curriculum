package persistence.exceptions;

public class DaoInitializationException extends Exception{
    /**
    *Prints the message if the exception is thrown
    * @param message Message to be shown
     **/
    public DaoInitializationException(String message){
        super(message);
    }
}
