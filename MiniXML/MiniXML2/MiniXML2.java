/*
 * Ibraheem Alhashim - ennetws@gmail.com
 * 
 * CS 321 - Fall 2007
 * Homework 3
 * 
 * QUESTION 3:
 * 		The MiniXML Lexical Analyzer REVISITED
 */
class MiniXMLex2 {
	
	public MiniXMLex2(BufferedReader input) {
		
	}
	
}

public class MiniXML2 {
	public static String egLine = "\tPlease try:  \"java MiniXML [FILE_NAME]\"\n";
	public static String extension = ".xml";
	
	public static void main (String[] args)
	{
		// Simple command line error checking
		if (args.length < 1){
			System.out.println("\nERROR: Please supply a MiniXML ("+extension+") file.\n");
			System.out.println(egLine);		
			System.exit(-1);
		}
		
		try{
			
			BufferedReader 	input 	= new BufferedReader(new FileReader(args[0]));		
			MiniXMLex2 		lex 	= new MiniXMLex2(input);
			
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
}
