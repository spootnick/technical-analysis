package spootnick.result;

import java.util.Collection;
import java.util.Date;

public interface ResultDao {

	
	
	public Collection<Result> load(Date from, String symbol);
	
	public Result load(Integer id);
	
	public void save(Result result);
}
