package tests;

import vitro.util.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class BoolTableTester {

	@Test
	public void testAssignLiteral() {
		BoolTable table = new BoolTable();
		
		table.assign("A", true);
		assertTrue(table.evaluate("A"));
		
		try {
			table.assign("A", false);
			fail();
		}
		catch(UnsupportedOperationException exception) { }
		
		try {
			table.assign("A", true);
		}
		catch(UnsupportedOperationException exception) { 
			fail();
		}
	}
	
	@Test
	public void testAssignClause() {
		BoolTable table = new BoolTable();
		table.assign("A", true);
		
		Map<String, Boolean> clause = new HashMap<String, Boolean>();
		clause.put("A", false);
		clause.put("B", false);
		table.assign(clause);
		
		assertTrue(table.evaluate("A"));
		assertFalse(table.evaluate("B"));
	}

	@Test
	public void testEvaluate() {
		BoolTable table = new BoolTable();
		table.assign("A", true );
		table.assign("B", false);
		
		assertTrue(table.evaluate("A"));
		assertFalse(table.evaluate("B"));
	}
}
