package spootnick.result;

import java.util.Collection;

public interface ResultDao {

	public Result test();
	
	//public Collection<Result> loadAll();
	public void save(Result result);
}
