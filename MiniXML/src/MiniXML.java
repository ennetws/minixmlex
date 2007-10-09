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

class WeakTag extends TagSet{
	public WeakTag(String n){
		super(n);
	}
	
	public String toString(){
		String temp ="";
		
		/*
		for (int i = 0 ; i < name.length();i++){
			temp += "CHAR\t" + (int)name.charAt(i)+" or = "+name.charAt(i)+"\n";
		}*/
		
		return temp;
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
		if (s.name == null || e.name == null || 
				s.name.length() != e.name.length())
			return false;
			
		return (s.name.equals(e.name.substring(1)));
	}
}

class MiniXMLex {

	public int lineNumber 	= 0;
	public int errorCount 	= 0;

	public BufferedReader input;
	public Vector<TagSet> tags = new Vector<TagSet>(); 
	public String output;
	
	public String buffer;
	public boolean EOF;

	public boolean verbose = true;
	public int errorCode;
	public int checkEnd;
	public String nonTags;

	public MiniXMLex(BufferedReader input) {
		this.input = input;
		this.output = "";
		this.nonTags = "";
		
		this.EOF = false;
		this.checkEnd = 0;
	}
	
	public int tagCount(){
		return tags.size() * 2;
	}
	
	public String nextToken(){
		String token 	= "";
		errorCode 		= -1;
		boolean isTag 	= false;
		
		if (buffer == null)
			return null;
		
		int i = 0;
		
		for (i = 0; i < buffer.length() ; i++){
			char c = buffer.charAt(i);
			
			if (c == '<'){
				if (i == 0)
					isTag = true;
				
				if (!isTag)
				{
					int closeTag = buffer.lastIndexOf('>');
					String nest  = buffer.substring(i,closeTag+1);
					String temp  = buffer.substring(closeTag+1);
					
					tags.add(nextTag(nest));
					
					buffer = temp;
					
					i = -1;
				}	
			}else if (c == '>'){
				break;
			}else{
				if (!isTag)
				{
					if (verbose)
						output += "CHAR\t" + (int)c +"\tor = "+ c +"\n";
					else
						output += c;
				}else{
					if(c >= '0' && c <= '9')
						errorCode = 1;	
				}
				
				token += c;
			}
		}
		
		if (i < buffer.length())
			buffer = buffer.substring(i+1);
		
		return token;
	}
	
	public TagSet nextTag(String buf){
		buffer = buf;
		String temp = buffer;
		
		String tokenStart,tokenMiddle = null, tokenEnd = null;

		StartTag 	x = null;
		TagSet 		y = null;
		EndTag		z = null;

		tokenStart = nextToken();
		
		if (errorCode != -1)
			error();

		if (buffer.indexOf('<') == -1)
		{
			buffer = "";
			
			if (buf.charAt(0) == '<')
			output += new StartTag(tokenStart) + "\n";
			
			return null;
		}

		x = new StartTag(tokenStart);
		
		output += x + "\n";
		
		int hasEndTag = buffer.indexOf("</"+x.name+">");
		
		// Safe Case
		if (hasEndTag > -1)
		{
			tokenMiddle = buffer.substring(0,hasEndTag);
			temp = buffer;
			y = nextTag(tokenMiddle);
			buffer = temp.substring(tokenMiddle.length());

			tokenEnd = nextToken();
			z = new EndTag(tokenEnd);
			output += z +"\n";
			
			return new TagSet(x, y, z);
		}else
		{
			error("closing tag missing.");
			return null;
		}
	}
	
	public String readMore(){
		
		if (this.EOF)
			return null;
			
		String temp = null;
		
		try{
			temp = input.readLine();
		}catch(IOException e){error(e.getMessage());}
		
		if (temp != null)
		{
			//output += "CHAR\t" + (int)('\n') +"\n";	
			
			lineNumber++;
		}else
		{
			this.EOF = true;
		}
		
		return temp;
	}
	
	public String analyise(){
		lineNumber 	= 0;
		errorCount 	= 0;
		
		try{
			buffer = input.readLine();
			
			while (!EOF && buffer != null)
			{
				lineNumber++;
				
				if(verbose && lineNumber > 1)	
					output += "CHAR\t" + (int)('\n') +"\n";	
			
				// For each line:
				while (!buffer.equals(""))
					tags.add(nextTag(buffer));
				
				buffer = input.readLine();
			}
	
			if(verbose)	
				output += "END" +"\n";
			else
				output +=  "\n";
		
		}catch(IOException e){
			error(e.getMessage());
		}
		
		return output;
	}
	
	public void basicAnalyise()
	{
		int cur = -1;
		
		try{
			cur = input.read();
	
			while (cur != -1){
				
				char c = (char)cur;
				
				switch (c){
					case '<':
						
						checkEnd = input.read();
						
						// BEGIN tags
						if (checkEnd != -1 && ((char)checkEnd) != '/')
						{
							getNextTag("BEGIN");
						}else
						{
							// End tags
							getNextTag("END");
						}
						break;
					
					default:
						nonTags += (char)cur;
						output += "CHAR\t" + cur + "\n";				
				}
				
				cur = input.read();
			}
			
		}catch(IOException e){System.out.println(e.getMessage());}
		
		output += "END\n";
	}
	
	public String getNextTag(String type){
		int cur = -1;
		char c = '\0';
		
		String tagName = "";
		
		if ((char)checkEnd != '/')
			tagName += (char)checkEnd;
			
		try{
			cur = input.read();
			
			boolean tagEnd = false;
			
			while (cur != -1 && !tagEnd)
			{
				c = (char) cur;

				switch(c)
				{
					case '>':
						output += type + "\t" + tagName + "\n";
						tagEnd = true;
						return tagName;
					default:
					{
						tagName += c;

						if (!((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z')))
							error("tag name contains an illegal character.");
					}
				}
				
				cur = input.read();
			}
		}catch(IOException e){System.out.println(e.getMessage());}
		return tagName;
	}

	public void error(String message){
		errorCount++;
		System.out.println("ERROR: in line("+lineNumber+"): "+ message);
	}
	
	public void error(){
		switch(errorCode)
		{
		case 1:
			error("tag names should not have digits.");
			break;
			
		default:
			
		}
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
			
			//System.out.print(lex.analyise());
			lex.basicAnalyise();
			System.out.print(lex.output);
			
			System.out.println(  "==============================\n");
			
			System.out.println(lex.nonTags);
			
			if (lex.errorCount > 0)
				System.out.println("Found ("+lex.errorCount+") errors.");
			else
				System.out.println("ALL OK.");
						
			input.close();
		}catch(IOException e){
			System.out.println("\nERROR: " + e.getMessage());
			System.exit(-1);
		}
	}
	
}
