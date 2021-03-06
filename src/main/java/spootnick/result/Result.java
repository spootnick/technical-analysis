package spootnick.result;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.persistence.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import spootnick.runtime.TradingRule.Move;

@Entity
@Table(name = "RESULT")
public class Result implements Serializable {

	private int id;
	private String symbol;
	private String name;
	private double change;
	private double priceChange;
	private Date executionDate;
	private Date quoteDate;
	private int windowSize;
	private int quoteCount;
	private SortedSet<Position> positions = new TreeSet<Position>();
	private double[] low;
	private double[] high;

	public Result() {

	}

	public Result(int windowSize, int quoteCount) {
		this.windowSize = windowSize;
		this.quoteCount = quoteCount;
		int size = windowSize + quoteCount;
		low = new double[size];
		high = new double[size];
		Arrays.fill(low, Move.UNDER);
		Arrays.fill(high, Move.OVER);
	}

	@Transient
	public double[] getLow() {
		return low;
	}

	@Transient
	public double[] getHigh() {
		return high;
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	// @Transient
	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	@Column(name = "price_change")
	public double getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(double priceChange) {
		this.priceChange = priceChange;
	}

	@Column(name = "execution_date")
	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	@Column(name = "window_size")
	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	@Column(name = "quote_count")
	public int getQuoteCount() {
		return quoteCount;
	}

	public void setQuoteCount(int quoteCount) {
		this.quoteCount = quoteCount;
	}

	@Column(name = "quote_date")
	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	@OneToMany(mappedBy = "result", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@Sort(type = SortType.NATURAL)
	public SortedSet<Position> getPositions() {
		return positions;
	}

	public void setPositions(SortedSet<Position> positions) {
		this.positions = positions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String toString(boolean display) {
		if (display) {
			int c = (int) (change * 100);
			int pc = (int) (priceChange * 100);
			return "symbol: " + symbol + ", change: " + c + " %, price change: " + pc + " %, window: " + windowSize + ", count: " + quoteCount;
		} else {
			return toString();
		}
	}
}
