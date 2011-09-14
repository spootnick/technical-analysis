package spootnick.result;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table( name = "ACTION" )
public class Action implements Serializable, Comparable<Action> {

	public enum Side {
		LONG, SHORT
	};
	
	private int id;
	private Result result;
	private Date quoteDate;
	private Side side;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	@Column(name="quote_date")
	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	@Override
	public int compareTo(Action other) {
		return quoteDate.compareTo(other.quoteDate);
	}

	@Enumerated(EnumType.STRING)
	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	
}
