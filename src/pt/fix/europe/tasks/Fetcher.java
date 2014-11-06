package pt.fix.europe.tasks;

import open311.requests.Parameter;

public interface Fetcher {
	Object fetchData(Parameter ... parameters);
}
