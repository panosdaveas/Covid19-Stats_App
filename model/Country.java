/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author panos
 */
@Entity
@Table(name = "COUNTRY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c")
	, @NamedQuery(name = "Country.findByCountry", query = "SELECT c FROM Country c WHERE c.country = :country")
	, @NamedQuery(name = "Country.findByName", query = "SELECT c FROM Country c WHERE c.name = :name")
	, @NamedQuery(name = "Country.findByLat", query = "SELECT c FROM Country c WHERE c.lat = :lat")
	, @NamedQuery(name = "Country.findByLong1", query = "SELECT c FROM Country c WHERE c.long1 = :long1")})
public class Country implements Serializable {

	@Transient
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	private static final long serialVersionUID = 1L;
	@Id
        @Basic(optional = false)
        @Column(name = "COUNTRY")
	private Integer country;
	@Basic(optional = false)
        @Column(name = "NAME")
	private String name;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Column(name = "LAT")
	private Double lat;
	@Column(name = "LONG")
	private Double long1;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
	private List<Coviddata> coviddataList;

	public Country() {
	}

	public Country(Integer country) {
		this.country = country;
	}

	public Country(Integer country, String name) {
		this.country = country;
		this.name = name;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		Integer oldCountry = this.country;
		this.country = country;
		changeSupport.firePropertyChange("country", oldCountry, country);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		changeSupport.firePropertyChange("name", oldName, name);
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		Double oldLat = this.lat;
		this.lat = lat;
		changeSupport.firePropertyChange("lat", oldLat, lat);
	}

	public Double getLong1() {
		return long1;
	}

	public void setLong1(Double long1) {
		Double oldLong1 = this.long1;
		this.long1 = long1;
		changeSupport.firePropertyChange("long1", oldLong1, long1);
	}

	@XmlTransient
	public List<Coviddata> getCoviddataList() {
		return coviddataList;
	}

	public void setCoviddataList(List<Coviddata> coviddataList) {
		this.coviddataList = coviddataList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (country != null ? country.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Country)) {
			return false;
		}
		Country other = (Country) object;
		if ((this.country == null && other.country != null) || (this.country != null && !this.country.equals(other.country))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "model.Country[ country=" + country + " ]";
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}
	
}
