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
@Table( name = "POSITION" )
public class Position implements Serializable, Comparable<Position> {

	public enum Side {
		LONG, SHORT
	};
	
	private int id;
	private Result result;
	private Date openDate;
	private Date closeDate;
	private double change;
	//private Side side;
	
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

	@Column(name="open_date")
	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@Column(name="close_date")
	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	
	@Override
	public int compareTo(Position other) {
		return openDate.compareTo(other.openDate);
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}
	
	/*
	@Enumerated(EnumType.STRING)
	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}*/

	
}
