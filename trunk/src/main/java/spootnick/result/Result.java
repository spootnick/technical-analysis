package spootnick.result;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.persistence.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table( name = "RESULT" )
public class Result implements Serializable{

	private Long id;
	private String symbol;
	private Double change;
	private Double priceChange;
	private Date execution;
	private Date quote;
	private Integer windowSize;
	private Integer quoteCount;
	private Set<Action> actions;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	//@Transient
	public Double getChange() {
		return change;
	}

	public void setChange(Double change) {
		this.change = change;
	}

	@Column(name="price_change")
	public Double getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(Double priceChange) {
		this.priceChange = priceChange;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Column(name="execution")
	public Date getExecution() {
		return execution;
	}

	public void setExecution(Date execution) {
		this.execution = execution;
	}

	@Column(name="window_size")
	public Integer getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}

	@Column(name="quote_count")
	public Integer getQuoteCount() {
		return quoteCount;
	}

	public void setQuoteCount(Integer quoteCount) {
		this.quoteCount = quoteCount;
	}

	@Column(name="quote")
	public Date getQuote() {
		return quote;
	}

	public void setQuote(Date quote) {
		this.quote = quote;
	}

	@OneToMany(mappedBy="result")
	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
}
