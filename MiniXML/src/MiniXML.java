import java.io.*;
import java.util.Vector;

class Tag{
	String name;
	
	public Tag(String n){
		name = n;
	}
}

class StartTag extends Tag{
	
	public StartTag(String n){
		super(n);
	}
	
	public String toString(){
		return "BEGIN\t" + name;
	}
}

class EndTag extends Tag{
	
	public EndTag(String n){
		super(n);
	}
	
	public String toString(){
		return "END\t" + name;
	}
}

class TagSet extends Tag{
	
	public StartTag startTag;
	public EndTag endTag;
	
	public Tag element;
	
	public TagSet(String n){
		super(n);
		
		startTag 	= null;
		endTag 		= null;
		element 	= null;
	}
	
	public TagSet(StartTag start, Tag elemnt, EndTag end){
		super("");
		
		startTag 	= start;
		element 	= elemnt;
		endTag 		= end;
	}

	public String toString(){
		String temp = "";
		
		// Print out the start tag
		if (startTag != null)
			temp += startTag + "\n";
		
		if (name != null){

			// Print out the contents of the element
			for (int i = 0 ; i < name.length();i++){
				temp += "CHAR\t" + (int)name.charAt(i)+" or = "+name.charAt(i)+"\n";
			}
			
		}
		
		// Print out the elements in that tag
		if (startTag != null)
			temp += endTag ;
				
		return temp;
	}
	
	public static boolean isEndTag(Tag s, Tag e){
		return (s.name.equals(e.name.substring(1)));
	}
}

class MiniXMLex {

	public int lineNumber 	= 0;
	public int errorCount 	= 0;

	public BufferedReader input;
	public Vector<TagSet> tags = new Vector<TagSet>(); 
	
	public String buffer;
	public StartTag currentStart;
	
	public boolean verbose = true;
	
	public String output;
	
	public MiniXMLex(BufferedReader input) {
		this.input = input;
		this.output = "";
	}
	
	public int tagCount(){
		return tags.size() * 2;
	}
	
	public String nextToken(String buf){
		String token = "";
		
		int bufferEnd = buf.length();
		
		boolean isTag = false;
		
		int i = 0;
		
		//Skip white space
		while(buf.charAt(i) == ' ')
			i++;
		
		for (; i < bufferEnd ; i++){
			char c = buf.charAt(i);
			
			if (c == '<'){
				
				isTag = true;
				
				if (token.length() > 0 || currentStart != null)
				{
					// End tag
					if (buf.charAt(i+1) == '/')
					{
						buffer = buffer.substring(buffer.indexOf('<'));
						return token;
					}else{
						
						// Nested case					
						buffer = buffer.substring(i);
						
						tags.add(nextTag());
						
						i 			= -1;
						bufferEnd 	-= (buf.length() - buffer.length());
						buf 		= buffer;
						isTag 		= false;
						
						buffer = buf;
					}
				}
			}
			else if (c == '>'){
				break;
			}
			else
			{
				if (!isTag)
				{
					if (verbose)
						output += "CHAR\t" + (int)c +"\tor = "+ c +"\n";
					else
						output += c;
				}
				
				token += c;
			}
		}

		buffer = buffer.substring(buffer.indexOf('>')+1);
		
		return token;
	}
	
	public TagSet nextTag(){
		currentStart = null;
		
		StartTag 	x = new StartTag(nextToken(buffer));
		
		currentStart = x;
		
		if(verbose)	
			output += x +"\n";
		
		TagSet 		y = new TagSet(nextToken(buffer));
		EndTag 		z = new EndTag("");

		currentStart = null;
		//Check for tags with no contents
		if (TagSet.isEndTag(x, y))
		{
			z = new EndTag(y.name);
			y = new TagSet("");
		}else
		{
			z = new EndTag(nextToken(buffer));
		}
		
		if(verbose)	
			output += z +"\n";
		
		return new TagSet(x, y, z);
	}

	public String analyise(){
		try{
		
			while ((buffer = input.readLine()) != null)
			{
				lineNumber++;
				
				// For each line:
				while (buffer != null && buffer.length() > 0)
					tags.add(nextTag());
				
				if(verbose)	
					output += "CHAR\t" + (int)('\n') +"\n";	
			}
			
			if(verbose)	
				output += "END" +"\n";
			else
				output +=  "\n";
		
		}catch(IOException e){
			error("ERROR: in line("+lineNumber+"):"+e.getMessage());
		}
		
		return output;
	}

	public void error(String message){
		System.out.println(message);
	}
}

public class MiniXML {
	
	public static String egLine = "\tPlease try:  \"java MiniXML [FILE_NAME]\"\n";
	public static String extension = ".xml";
	
	public static void main(String args[]){
		
		// Command line error checking
		if (args.length < 1){
			System.out.println("\nERROR: Please supply a MiniXML ("+extension+") file.\n");
			System.out.println(egLine);		
			System.exit(-1);
		}else if(args.length > 1){
			System.out.println("\nERROR: Too many arguments ("+args.length+")\n");
			System.out.println(egLine);		
			System.exit(-1);
		}else if(!args[0].endsWith(extension)){
			System.out.println("\nERROR: File supplied is not a ("+extension+") file.\n");
			System.out.println(egLine);		
			System.exit(-1);
		}else if (!(new File(args[0])).exists()){
			System.out.println("\nERROR: The file ("+args[0]+") doesn't exists.\n");
			System.out.println(egLine);		
			System.exit(-1);
		}
		
		// Read the MiniXML file and do the lexical analysis
		try{
			BufferedReader input = new BufferedReader(new FileReader(args[0]));		
			
			// Question 1: Output description of the tokens in the input file
			MiniXMLex lex = new MiniXMLex(input);
			
			System.out.println("\n======== LEX ANALYSIS ========"  );
				System.out.print(lex.analyise());
			System.out.println(  "==============================\n");
			
			if (lex.errorCount > 0)
				System.out.println("Found ("+lex.errorCount+") errors.");
						
			input.close();
		}catch(IOException e){
			System.out.println("\nERROR: " + e.getMessage());
			System.exit(-1);
		}
	}
	
}
