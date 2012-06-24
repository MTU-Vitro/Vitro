package vitro.tools;

import java.util.*;
import vitro.*;

public interface Setup {

	public Controller init(List<Agent> agents);

	public void eval(Controller controller, long elapsed);

}