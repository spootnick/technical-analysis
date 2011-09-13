package spootnick.result;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table( name = "ACTION" )
public class Action implements Serializable {

	private Long id;
	private Result result;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="result_id")
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
}
