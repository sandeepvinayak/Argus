/*
 * Copyright (c) 2016, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.dva.argus.service.tsdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.salesforce.dva.argus.service.metric.transform.Transform;
import com.salesforce.dva.argus.service.metric.transform.TransformFactory;
import com.salesforce.dva.argus.system.SystemAssert;
import com.salesforce.dva.argus.system.SystemException;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates parameters used to drive a query of metric data.
 *
 * @author  Tom Valine (tvaline@salesforce.com), Bhinav Sura (bhinav.sura@salesforce.com)
 */
@JsonInclude(Include.NON_NULL)
public class MetricQuery extends AnnotationQuery {

	//~ Instance fields ******************************************************************************************************************************

	private String _namespace;
	private Aggregator _aggregator;
	private Aggregator _downsampler;
	private Long _downsamplingPeriod;
	private MetricQueryContext _metricQueryContext;
	private String[] _percentile;
	private boolean _showHistogramBuckets;

	//~ Constructors *********************************************************************************************************************************

	/**
	 * Creates a new MetricQuery object by performing a shallow copy of the given MetricQuery object.
	 *
	 * @param  clone  The MetricQuery object to clone. Cannot be null.
	 */
	public MetricQuery(MetricQuery clone) {
		SystemAssert.requireArgument(clone != null, "The object to clone cannot be null.");
		_scope = clone.getScope();
		_metric = clone.getMetric();
		setTags(clone.getTags());
		setStartTimestamp(clone.getStartTimestamp());
		setEndTimestamp(clone.getEndTimestamp());
		setNamespace(clone.getNamespace());
		setAggregator(clone.getAggregator());
		setDownsampler(clone.getDownsampler());
		setDownsamplingPeriod(clone.getDownsamplingPeriod());
		setMetricQueryContext(clone.getMetricQueryContext());
	}

	/**
	 * Creates a new Metric Query object.
	 *
	 * @param  scope           The scope of the metric. Cannot be null or empty.
	 * @param  metric          The name of the metric. Cannot be null or empty.
	 * @param  tags            The tags associated with the metric. Can be null or empty.
	 * @param  startTimestamp  The start time stamp for the query. Cannot be null.
	 * @param  endTimestamp    The end time for the query. If null, defaults to the current system time.
	 */
	public MetricQuery(String scope, String metric, Map<String, String> tags, Long startTimestamp, Long endTimestamp) {
		super(scope, metric, tags, startTimestamp, endTimestamp);
		SystemAssert.requireArgument(metric != null, "Metric cannot be null");
	}

	/** Creates a new Metric object. */
	protected MetricQuery() {
		super();
	}

	//~ Methods **************************************************************************************************************************************

	/**
	 * Returns the namespace of the query.
	 *
	 * @return  The query namespace.  Can be null.
	 */
	public String getNamespace() {
		return _namespace;
	}

	/**
	 * Sets the query namespace.
	 *
	 * @param  namespace  The namespace.  May be null.
	 */
	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

    /**
     * Sets the query percentile. (Used to only retrieve histogram data) 
     *
     * @param  percentile  The percentile for histogram data.
     */
    public void setPercentile(String[] percentile) {
    _percentile = percentile;
    }
    
    /**
     * Sets the showHistogramBuckets (Used to only retrieve histogram data)
     *
     * @param  showHistogramBuckets 
     */
    public void setShowHistogramBuckets(boolean showHistogramBuckets) {
        _showHistogramBuckets = showHistogramBuckets;
    }	
    
	/**
	 * Returns the method used to aggregate query results.
	 *
	 * @return  The aggregator method used.
	 */
	public Aggregator getAggregator() {
		return _aggregator;
	}

	/**
	 * Sets the method used to aggregate query results.
	 *
	 * @param  aggregator  The aggregator method to use.
	 */
	public void setAggregator(Aggregator aggregator) {
		_aggregator = aggregator;
	}

	/**
	 * Returns the method used to downsample query results.
	 *
	 * @return  The method used to downsample the query results.
	 */
	public Aggregator getDownsampler() {
		return _downsampler;
	}
	
    /**
     * Returns the query percentile.
     *
     * @return  query percentile.
     */
    public String[] getPercentile() {
        return _percentile;
    }
    
    /**
     * Returns if the histogram buckets should be shown or not
     *
     * @return  Should histogram buckets  be shown or not
     */
    public boolean getShowHistogramBuckets() {
        return _showHistogramBuckets;
    }   


	/**
	 * Sets the method used to downsample the query results.
	 *
	 * @param  downsampler  The method used to downsample the query results.
	 */
	public void setDownsampler(Aggregator downsampler) {
		_downsampler = downsampler;
	}

	/**
	 * Returns the time interval in milliseconds used to downsample the query results.
	 *
	 * @return  The downsample interval.
	 */
	public Long getDownsamplingPeriod() {
		return _downsamplingPeriod;
	}

	/**
	 * Sets the time interval in milliseconds used to downsample the query results.
	 *
	 * @param  downsamplingPeriod  The downsample interval.
	 */
	public void setDownsamplingPeriod(Long downsamplingPeriod) {
		_downsamplingPeriod = downsamplingPeriod;
	}

	/**
	 * Returns the context for this query
	 *
	 * @return  The context for query
	 */
	public MetricQueryContext getMetricQueryContext() {
		return _metricQueryContext;
	}

	/**
	 * Sets any extra context for this query
	 *
	 * @param metricQueryContext The metric query context
	 */
	public void setMetricQueryContext(MetricQueryContext metricQueryContext) {
		_metricQueryContext = metricQueryContext;
	}

	/**
	 * Returns the TSDB metric name.
	 *
	 * @return  The TSDB metric name.
	 */
	@JsonIgnore
	public String getTSDBMetricName() {
		StringBuilder sb = new StringBuilder();

		sb.append(getMetric()).append(DefaultTSDBService.DELIMITER).append(getScope());

		if (_namespace != null && !_namespace.isEmpty()) {
			sb.append(DefaultTSDBService.DELIMITER).append(getNamespace());
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();

		result = prime * result + ((_aggregator == null) ? 0 : _aggregator.hashCode());
		result = prime * result + ((_downsampler == null) ? 0 : _downsampler.hashCode());
		result = prime * result + ((_downsamplingPeriod == null) ? 0 : _downsamplingPeriod.hashCode());
		result = prime * result + ((_namespace == null) ? 0 : _namespace.hashCode());
		result = prime * result + ((_metricQueryContext == null) ? 0 : _metricQueryContext.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		MetricQuery other = (MetricQuery) obj;

		if (_aggregator != other._aggregator) {
			return false;
		}
		if (_downsampler != other._downsampler) {
			return false;
		}
		if (_downsamplingPeriod == null) {
			if (other._downsamplingPeriod != null) {
				return false;
			}
		} else if (!_downsamplingPeriod.equals(other._downsamplingPeriod)) {
			return false;
		}
		if (_namespace == null) {
			if (other._namespace != null) {
				return false;
			}
		} else if (!_namespace.equals(other._namespace)) {
			return false;
		} else if (!_metricQueryContext.equals(other._metricQueryContext)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the TSDB formatted representation of the query.
	 * TODO: This is implementation specific and needs to be moved to the service interface.
	 *
	 * @return  The TSDB formatted representation of the query.
	 *
	 * @throws  SystemException  If UTF-8 encoding is not supported on the system.
	 */
	@Override
	public String toString() {
	    String pattern = "start={0,number,#}&end={1,number,#}&m={2}{3}&ms=true&show_tsuids=true";
	    long start = Math.max(0, getStartTimestamp() - 1);
	    long end = Math.max(start, getEndTimestamp() + 1);
	    StringBuilder sb = new StringBuilder();

	    sb.append(getAggregator() == null ? "avg" : getAggregator().getDescription()).append(":");
	    if (getDownsampler() != null) {
	        sb.append(getDownsamplingPeriod()).append("ms").append("-").append(getDownsampler().getDescription()).append(":");
	    }

	    if (getPercentile() != null) {
	        sb.append(getPercentile()).append(":");
	    }

	    if (getShowHistogramBuckets() != false) {
	        sb.append("show-histogram-buckets").append(":");
	    }	      
	    sb.append(getTSDBMetricName());

	    Map<String, String> tags = new HashMap<>(getTags());

	    try {
	        return MessageFormat.format(pattern, start, end, sb.toString(), toTagParameterArray(tags));
	    } catch (UnsupportedEncodingException ex) {
	        throw new SystemException(ex);
	    }
	}

	//~ Enums ****************************************************************************************************************************************

	/**
	 * The supported methods for aggregation and downsampling.
	 *
	 * @author  Tom Valine (tvaline@salesforce.com), Bhinav Sura (bhinav.sura@salesforce.com)
	 */
	public enum Aggregator {

		MIN("min"),
		MAX("max"),
		SUM("sum"),
		AVG("avg"),
		DEV("dev"),
		
		// these 3 aggregators is IMIN, IMAX and ISUM are also used for providing the default opentsdb interpolated aggregations
		IMIN("min"),
		IMAX("max"),
		ISUM("sum"),
		
		ZIMSUM("zimsum"),
		COUNT("count"),
		MIMMIN("mimmin"),
		MIMMAX("mimmax"),
		FIRST("first"),
		LAST("last"),
		NONE("none");

		private final String _description;

		private Aggregator(String description) {
			_description = description;
		}

		/**
		 * Returns the element corresponding to the given name.
		 *
		 * @param   name  The aggregator name.
		 *
		 * @return  The corresponding aggregator element.
		 */
		public static Aggregator fromString(String name) {
			if (name != null && !name.isEmpty()) {
				for (Aggregator aggregator : Aggregator.values()) {
					if (name.equalsIgnoreCase(aggregator.name())) {
						return aggregator;
					}
				}
			}
			return null;
		}

		/**
		 * Returns the short hand description of the method.
		 *
		 * @return  The method description.
		 */
		public String getDescription() {
			return _description;
		}
		
	    public static Transform correspondingTransform(Aggregator agg, TransformFactory factory) {
	    	
	    	Transform transform;
			switch(agg) {    			
				case MIN:
					transform = factory.getTransform(TransformFactory.Function.MIN.getName());
					break;
				case MAX:
					transform = factory.getTransform(TransformFactory.Function.MAX.getName());
					break;
				case SUM: 
					transform = factory.getTransform(TransformFactory.Function.ZEROIFMISSINGSUM.getName());
					break;
				case AVG:
					transform = factory.getTransform(TransformFactory.Function.AVERAGE.getName());
					break;
				case DEV:
					transform = factory.getTransform(TransformFactory.Function.DEVIATION.getName());
					break;
				case ZIMSUM: 
					transform = factory.getTransform(TransformFactory.Function.ZEROIFMISSINGSUM.getName());
					break;
				case COUNT:
					transform = factory.getTransform(TransformFactory.Function.COUNT.getName());
					break;
				case MIMMIN:
					transform = factory.getTransform(TransformFactory.Function.MIN.getName());
					break;
				case MIMMAX:
					transform = factory.getTransform(TransformFactory.Function.MAX.getName());
					break;
				case NONE:
					transform = factory.getTransform(TransformFactory.Function.IDENTITY.getName());
					break;
				default:
					throw new IllegalArgumentException("Aggregator not legal: " + agg);
			}
	    	
			return transform;
	    }		
	}
	
	/**
	 * Encapsulates parameters used to provide additional context to a metric query if needed
	 *
	 * @author  Dilip Devaraj(ddevaraj@salesforce.com)
	 */	
	protected class MetricQueryContext {
		private String _readEndPoint;
		
		/**
		 * Returns the endpoint for this query
		 *
		 * @return  The endpoint for query
		 */
		public String getReadEndPoint() {
			return _readEndPoint;
		}

		/**
		 * Sets the endpoint for this query
		 *
		 * @param  readEndPoint  The endpoint for query
		 */
		public void setReadEndPoint(String readEndPoint) {
			_readEndPoint = readEndPoint;
		}
		
		@Override
		public String toString() {
			return "MetricQueryContext [_readEndPoint=" + _readEndPoint + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_readEndPoint == null) ? 0 : _readEndPoint.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetricQueryContext other = (MetricQueryContext) obj;
			if (_readEndPoint == null) {
				if (other._readEndPoint != null)
					return false;
			} else if (!_readEndPoint.equals(other._readEndPoint))
				return false;
			return true;
		}
	}
}
/* Copyright (c) 2016, Salesforce.com, Inc.  All rights reserved. */
