package spootnick.result;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	private Date createTime;
	private Date firstQuote;
	private Integer windowSize;
	private Integer quoteCount;
	
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

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	@Column(name="first_quote")
	public Date getFirstQuote() {
		return firstQuote;
	}

	public void setFirstQuote(Date firstQuote) {
		this.firstQuote = firstQuote;
	}
}
