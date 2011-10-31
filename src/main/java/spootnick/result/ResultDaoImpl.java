package spootnick.result;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
	public Collection<Result> load(Date from, String symbol) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(Result.class);
		if(from != null)
			crit.add(Restrictions.ge("executionDate", from));
		if(symbol != null)
			crit.add(Restrictions.like("symbol", "%"+symbol+"%"));
		return crit.list();
	}

}
