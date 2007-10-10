/*
 * Ibraheem Alhashim - ennetws@gmail.com
 * 
 * CS 321 - Fall 2007
 * Homework 2
 * 
 * The MiniXML Lexical Analyzer
 */
import java.io.*;

// The MiniXML-Lexical analyzer class
class MiniXMLex {

	public int lineNumber 	= 1;
	public int errorCount 	= 0;
	public int tagCount 	= 0;

	public int 	checkEnd;
	public int	current;
	
	public BufferedReader 	input;
	public String 			output;
	public String 			nonTags;
	
	public MiniXMLex(BufferedReader input) {
		this.input 		= input;
		this.output 	= "";
		this.nonTags 	= "";

		this.checkEnd 	= 0;
		this.current 	= -1;
	}
	
	public int tagCount(){
		return tagCount;
	}
	
	public void analyise()
	{
		try{
			// First character read
			current = input.read();
	
			// Each time check for EOF & stop on any error
			while (current != -1 && errorCount < 1){
				
				switch ((char)current){
				
					case '<':
						
						// Checks for the type of tag
						checkEnd = input.read();
						
						if (checkEnd == '>')
						{
							error("empty BEGIN tag.");
							return;
						}
						
						if (checkEnd != -1 && ((char)checkEnd) != '/')
							getNextTag("BEGIN");
						else
							getNextTag("END");
						
						tagCount++;
						break;
					
					default:
						if (current == '\n')
							lineNumber++;
						
						// This saves non-tags into a separate string 
						// (useful for "striptags")
						nonTags += (char)current;
						
						// Append to the output of our lexical analyzer
						output 	+= "CHAR\t" + current + "\n";				
				}
				
				// Read next character
				current = input.read();
			}
			
		}catch(IOException e){
			error(e.getMessage());
		}
		
		output += "END\n";
	}
	
	// This method returns tag names when encountered
	// and checks for basic illegal cases.
	public String getNextTag(String type){
		
		String tagName = "";
		boolean tagEnd = false;
		
		if ((char)checkEnd != '/'){
			// It is not an END tag, therefore include the 
			// first character
			tagName += (char)checkEnd;
		}
		else if((char)current == '>'){
			// Check for an empty end tag
			error("empty END tag.");
			return tagName;
		}
			
		try{
			current = input.read();

			while (current != -1 && !tagEnd)
			{
				switch((char) current)
				{
					case '>':
						output += type + "\t" + tagName + "\n";
						tagEnd = true;
						return tagName;
						
					case '<':
						error("incomplete tag.");
						return tagName;
						
					default:
					{
						tagName += (char)current;

						char c = (char)current;
						
						// Only alphabet letters are allowed
						if (!((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z')))
							error("tag name contains an illegal character.");
					}
				}
				
				// Read next character in the tag's name
				current = input.read();
			}
			
			if (current == -1)
				error("incomplete tag.");
				
		}catch(IOException e){
			error(e.getMessage());
		}
		
		return tagName;
	}

	// Our error reporting mechanism
	public void error(String message){
		errorCount++;
		System.out.println("ERROR: in line("+lineNumber+"): "+ message);
	}
}

// Our driver class
public class MiniXML {
	
	public static String egLine = "\tPlease try:  \"java MiniXML [FILE_NAME]\"\n";
	public static String extension = ".xml";
	
	public static void main(String args[]){
		
		// Simple command line error checking
		if (args.length < 1){
			System.out.println("\nERROR: Please supply a MiniXML ("+extension+") file.\n");
			System.out.println(egLine);		
			System.exit(-1);
		}

		// Read the MiniXML file and do the lexical analysis
		try{
			
			BufferedReader 	input 	= new BufferedReader(new FileReader(args[0]));		
			MiniXMLex 		lex 	= new MiniXMLex(input);
			
			// Question 1: Output description of the tokens in the input file
			System.out.println("\n======== LEX ANALYSIS ========"  );
				lex.analyise();
				System.out.print(lex.output);
			System.out.println(  "==============================\n");
			
			// Question 2: Part 'striptags'
			System.out.println("====== Stripped Version ======"  );
			System.out.println(lex.nonTags);
			System.out.println(  "==============================\n");
			
			// Question 2: part 'counttags'
			System.out.println("Tag count: " + lex.tagCount() + "\n");
			
			// Report result of analyzation
			if (lex.errorCount > 0)
				System.out.println("MiniXMLex: Found ("+lex.errorCount+") errors.");
			else
				System.out.println("MiniXMLex: ALL OK.");
						
			input.close();
			
		}catch(IOException e){
			System.out.println("\nERROR: " + e.getMessage());
			System.exit(-1);
		}
	}
}
