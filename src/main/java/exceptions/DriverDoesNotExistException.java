package exceptions;
public class DriverDoesNotExistException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public DriverDoesNotExistException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public DriverDoesNotExistException(String s)
  {
    super(s);
  }
}