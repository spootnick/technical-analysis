package spootnick.result;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ResultDaoImpl implements ResultDao {

	private Logger log = LoggerFactory.getLogger(ResultDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	
	
	@Transactional
	public void save(Result result){
		this.sessionFactory.getCurrentSession().save(result);
		log.debug("{} saved",result);
		
	}



	@Transactional
	@Override
	public Result test() {
		Result ret = (Result) this.sessionFactory.getCurrentSession().get(Result.class, 0);
		ret.getActions().size();
		return ret;
	}
}
