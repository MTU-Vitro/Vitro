package vitro.tools;

import java.io.*;
import java.util.*;
import vitro.*;

abstract class Scenario {

	abstract void setCompetitors(List<Class> competitors);

	abstract boolean next();
	abstract Anim  preMatch();
	abstract View     match();
	abstract Anim postMatch();

	protected PrintWriter out = null;

	void setLog(PrintWriter out) {
		this.out = out;
	}

	void restore(Scanner in) {
		System.out.println("Error: This Scenario implementation cannot restore from a logfile.");
	}
}