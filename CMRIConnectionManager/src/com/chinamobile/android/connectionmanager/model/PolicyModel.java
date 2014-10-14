package com.chinamobile.android.connectionmanager.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;

import android.net.wifi.WifiInfo;

/**
 * Data model of Policy
 */

public class PolicyModel implements Comparable<PolicyModel>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8887922900449567292L;
	public static final int TYPE_RECOMMEND = 1;
	public static final int TYPE_OVERWRITE = 2;
	
	public static final int STATE_INVALID = 0;
	public static final int STATE_CURRENT = 1;
	public static final int STATE_FUTURE = 2;
	public static final int STATE_EXPIRED = 3;
	
	public static final int TYPE_FORM_NETWORK = 0;
	public static final int TYPE_FORM_PREVIOUS = 1;
	public static final int TYPE_FORM_STATIC = 2;
	
	private Integer rulePriority = -1;
	private int responseType = TYPE_RECOMMEND;
	private int status;
	public long startTimeDistance;
	public long endTimeDistance;
	public boolean pickByCID = true;
	public int resource_from = -1;
	private List<NetworkModel> accessNetworkList = new ArrayList<NetworkModel>();
	private List<_3GModel> location3GList = new ArrayList<_3GModel>();
	private List<WifiModel> locationWlanList = new ArrayList<WifiModel>();
	private List<GeoLocation> geoLocationList = new ArrayList<GeoLocation>();
	private List<TimeAndDate> timeList = new ArrayList<TimeAndDate>();
	
	public Integer getRulePriority() {
		return rulePriority;
	}

	public void setRulePriority(int rulePriority) {
		this.rulePriority = Integer.valueOf(rulePriority);
	}

	public int getResponseType() {
		return responseType;
	}

	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<NetworkModel> getAccessNetworkList() {
		Collections.sort(accessNetworkList);
		return accessNetworkList;
	}

	public void setAccessNetworkList(List<NetworkModel> accessNetworkList) {
		this.accessNetworkList = accessNetworkList;
	}

	public List<_3GModel> getLocation3GList() {
		return location3GList;
	}

	public void setLocation3GList(List<_3GModel> location3gList) {
		location3GList = location3gList;
	}

	public List<WifiModel> getLocationWlanList() {
		return locationWlanList;
	}

	public void setLocationWlanList(List<WifiModel> locationWlanList) {
		this.locationWlanList = locationWlanList;
	}

	public List<GeoLocation> getGeoLocationList() {
		return geoLocationList;
	}

	public void setGeoLocationList(List<GeoLocation> geoLocationList) {
		this.geoLocationList = geoLocationList;
	}

	public List<TimeAndDate> getTimeList() {
		return timeList;
	}

	public void setTimeList(List<TimeAndDate> timeList) {
		this.timeList = timeList;
	}

	/**
	 * Data model of Geo location
	 *
	 */
	public static class GeoLocation {
		private double latitude;
		private double longtitude;
		private long radius;
		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongtitude() {
			return longtitude;
		}

		public void setLongtitude(double longtitude) {
			this.longtitude = longtitude;
		}

		public long getRadius() {
			return radius;
		}

		public void setRadius(long radius) {
			this.radius = radius;
		}
	}
	
	/**
	 * Data model of time and date
	 *
	 */
	public static class TimeAndDate {
		public static final int VALID = 1;// in the now time period
		public static final int FUTURE = 2;// not arrived yet
		public static final int EXPIRED = 3;// expired
		private int startTime = -1;
		private int endTime = -1;
		private int startDate = -1;
		private int endDate = -1;

		public TimeAndDate() {
			
		}
		
		public TimeAndDate(int startTime, int endTime, int startDate, int endDate) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

		public int getStartDate() {
			return startDate;
		}

		public void setStartDate(int startDate) {
			this.startDate = startDate;
		}

		public int getEndDate() {
			return endDate;
		}

		public void setEndDate(int endDate) {
			this.endDate = endDate;
		}

		public long getStartDateTime() {
			int startTimeCopy = startTime;
			int startDateCopy = startDate;
			int endTimeCopy = endTime;
			if(startTimeCopy < 0) {
				startTimeCopy = 000000;
			}
			if(endTimeCopy < 0) {
				endTimeCopy = 240000;
			}
			if(startDateCopy < 0) {
				if(endTimeCopy > startTimeCopy && TimeUtil.getNowTime() >= endTimeCopy) {// is out of today's time, so get date +1
					startDateCopy = TimeUtil.getDateCompareToday(1);
				} else {
					startDateCopy = TimeUtil.getNowDate();
				}
				
			}
			return (long) (startDateCopy * 1E6 + startTimeCopy);
		}
		
		public long getEndDateTime() {
			int startTimeCopy = startTime;
			int endTimeCopy = endTime;
			int endDateCopy = endDate;
			if(startTimeCopy < 0) {
				startTimeCopy = 000000;
			}
			if(endTimeCopy < 0) {
				endTimeCopy = 235959;
			}
			if(endDateCopy < 0) {
				if(endTimeCopy >= startTimeCopy && TimeUtil.getNowTime() < endTimeCopy) {
					endDateCopy = TimeUtil.getNowDate();
				} else {
					endDateCopy = TimeUtil.getDateCompareToday(1);// end time is smaller than start time
														//consider as validity period of the policy the 
														//time starting at TimeStart and ending at 
														//TimeStop of the following day
				}
			}
			return (long) (endDateCopy * 1E6 + endTimeCopy);
		}
		
		@Override
		public String toString() {
			return "TimeAndDate [startTime=" + startTime + ", endTime="
					+ endTime + ", startDate=" + startDate + ", endDate="
					+ endDate + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + endDate;
			result = prime * result + endTime;
			result = prime * result + startDate;
			result = prime * result + startTime;
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
			TimeAndDate other = (TimeAndDate) obj;
			if (endDate != other.endDate)
				return false;
			if (endTime != other.endTime)
				return false;
			if (startDate != other.startDate)
				return false;
			if (startTime != other.startTime)
				return false;
			return true;
		}
		
		
	}

	@Override
	public int compareTo(PolicyModel another) {
		// TODO Auto-generated method stub
//		return this.getRulePriority().compareTo(another.getRulePriority());\
		Long thisL = new Long(this.getTimeList().get(0).getStartDateTime());
		Long anotherL = new Long(another.getTimeList().get(0).getStartDateTime());
		return thisL.compareTo(anotherL);
	}

	/**
	 * clone without {@link TimeAndDate} list
	 */
	public PolicyModel clone() {
		PolicyModel pm = new PolicyModel();
		List<NetworkModel> accessNetworkList = new ArrayList<NetworkModel>();
		accessNetworkList.addAll(this.accessNetworkList);
		List<_3GModel> location3GList = new ArrayList<_3GModel>();
		location3GList.addAll(this.location3GList);
		List<WifiModel> locationWlanList = new ArrayList<WifiModel>();
		locationWlanList.addAll(this.locationWlanList);
		List<GeoLocation> geoLocationList = new ArrayList<GeoLocation>();
		geoLocationList.addAll(this.geoLocationList);
		pm.setRulePriority(rulePriority);
//		pm.setResponseType(responseType);
		pm.setStatus(status);
		pm.startTimeDistance = this.startTimeDistance;
		pm.endTimeDistance = this.endTimeDistance;
		pm.pickByCID = this.pickByCID;
		pm.resource_from = this.resource_from;
		pm.setAccessNetworkList(accessNetworkList);
		pm.setLocation3GList(location3GList);
		pm.setLocationWlanList(locationWlanList);
		pm.setGeoLocationList(geoLocationList);
		return pm;
	}

	@Override
	public String toString() {
		return "PolicyModel [rulePriority=" + rulePriority + ", status=" + status + ", startTimeDistance="
				+ startTimeDistance + ", endTimeDistance=" + endTimeDistance
				+ ", timeList=" + timeList + "]";
	}
}
