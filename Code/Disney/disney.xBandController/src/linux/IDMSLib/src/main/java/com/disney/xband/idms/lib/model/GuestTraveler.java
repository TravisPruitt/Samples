package com.disney.xband.idms.lib.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestTraveler
{

		private GuestName name;
		private Date dateOfBirth;
		private String redressNumber;
		private String gender;
		private String travelNumber;
		
		@XmlElement(name="name")
		public GuestName getName() {
			return name;
		}
		
		@XmlElement(name="name")
		public void setName(GuestName name) {
			this.name = name;
		}
		
		@XmlElement(name="dateOfBirth")
		public Date getDateOfBirth() {
			return dateOfBirth;
		}
		
		@XmlElement(name="dateOfBirth")
		public void setDateOfBirth(Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
		
		@XmlElement(name="redressNumber")
		public String getRedressNumber() {
			return redressNumber;
		}
		
		@XmlElement(name="redressNumber")
		public void setRedressNumber(String redressNumber) {
			this.redressNumber = redressNumber;
		}
		
		@XmlElement(name="gender")
		public String getGender() {
			return gender;
		}
		
		@XmlElement(name="gender")
		public void setGender(String gender) {
			this.gender = gender;
		}
		
		@XmlElement(name="travelNumber")
		public String getTravelNumber() {
			return travelNumber;
		}
		
		@XmlElement(name="travelNumber")
		public void setTravelNumber(String travelNumber) {
			this.travelNumber = travelNumber;
		}
		
		
		
}
