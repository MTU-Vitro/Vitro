package vitro.util;

import java.util.*;

public class BoolTable {

	private Map<Object, Boolean> literals = new HashMap<Object, Boolean>();
	private Set<Map<Object, Boolean>> clauses = new HashSet<Map<Object, Boolean>>();
	
	public BoolTable() {
	
	}
	
	public void assign(Object literal, boolean isTrue) {
		if(literals.containsKey(literal) && literals.get(literal) != isTrue) {
			throw new UnsupportedOperationException("Literal contradicts current knowledge.");
		}
		literals.put(literal, isTrue);
		
		// resolve any existing clauses with this new literal
		for(Map<Object, Boolean> clause : clauses) {
			if(clause.containsKey(literal) && clause.get(literal) != isTrue) {
				clause.remove(literal);
			}
		}
	}
	
	public boolean conflicts(Object literal, boolean isTrue) {
		return known(literal) && evaluate(literal) != isTrue;
	}
	
	public void assign(Map<? extends Object, Boolean> clause) {
		// resolve this clause with any existing literals
		Map<Object, Boolean> newClause = new HashMap<Object, Boolean>(clause);
		for(Object key : literals.keySet()) {
			if(newClause.containsKey(key) && newClause.get(key) != literals.get(key)) {
				newClause.remove(key);
			}
		}
		
		if(newClause.size() == 1) {
			// the clause is essentially a literal
			Object obj = Groups.first(newClause.keySet());
			assign(obj, clause.get(obj));
		}
		else {
			clauses.add(newClause);
		
			// run resolution
			resolve();
		}
	}
	
	public boolean conflicts(Map<Object, Boolean> clause) {
		return false;
	}
	
	private void resolve() {
		// clause on clause resolution
	}
	
	public boolean known(Object literal) {
		return literals.keySet().contains(literal);
	}
	
	public boolean evaluate(Object literal) {
		if(!known(literal)) {
			throw new UnsupportedOperationException("No known assignment for the given literal.");
		}
		return literals.get(literal);
	}
	
	public String toString() {
		String ret = "\n";
		for(Object literal : literals.keySet()) {
			ret += "\t" + literalString(literal, literals.get(literal)) + " && \n";
		}
		//for(Map<Object, Boolean> clause : clauses) {
		//	ret += clauseString(clause) + " && ";
		//}
		return ret.substring(0, ret.length() - 4);
	}
	
	private String literalString(Object literal, boolean isTrue) {
		return (isTrue ? "" : "!") + literal;
	}
	
	private String clauseString(Map<Object, Boolean> clause) {
		String ret = "";
		for(Object literal : clause.keySet()) {
			ret += literalString(literal, clause.get(literal)) + " || ";
		}
		return ret.substring(0, ret.length() - 4);
	}
}
