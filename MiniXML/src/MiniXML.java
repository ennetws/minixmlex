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
		super("SET");
		
		startTag 	= start;
		element 	= elemnt;
		endTag 		= end;
	}
	
	public String toString(){
		String temp = "";
		
		// Print out the start tag
		if (startTag != null)
			temp += startTag + "\n";
		
		if (element != null){
			if (element instanceof TagSet)
			{
				// Print out the nested tag
				temp += element.toString()+"\n";
			}else{
				
				// Print out the contents of the element
				for (int i = 0 ; i < element.name.length();i++){
					temp += "CHAR\t" + (int)element.name.charAt(i)+"\n";
				}
			}
		}
		
		// Print out the elements in that tag
		if (startTag != null)
			temp += endTag ;
				
		return temp;
	}
}

class MiniXMLex {

	public int lineNumber 	= 0;
	public int tagCount 	= 0;
	public int errorCount 	= 0;

	public BufferedReader input;
	public Vector<TagSet> tags = new Vector<TagSet>(); 
	
	public String buffer;
	
	public MiniXMLex(BufferedReader input) {
		this.input = input;
	}
	
	public String nextToken(){
		String token = "";
		
		
		
		return token;
	}
	
	public String analyise(){
		String result = "";
		
		try{
		
			while ((buffer = input.readLine()) != null)
			{
				lineNumber++;
				
				
				
				System.out.println("CHAR\t" + (int)('\n'));
			}
			
			System.out.println("END");
		
		}catch(IOException e){
			error("ERROR: in line("+lineNumber+"):"+e.getMessage());
		}
		
		return result;
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
