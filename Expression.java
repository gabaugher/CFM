package cfm_files;

import cfm_files.AI.Term;

public class Expression {

	AI.Term[] terms;
	String string = null;
	String originalType = null;
	String answer = null;
	
	public Expression() {
		Term[] termList = new Term[19];
		this.terms = termList; 
		this.string = "";
		this.originalType = "";
		this.answer = "";
	}

}
