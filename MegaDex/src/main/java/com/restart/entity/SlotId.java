package com.restart.entity;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@Embeddable
public class SlotId implements Serializable {
	
	//Id del deck a cui è associato lo slot
	@Column(name = "id_deck", nullable = false)
	private Integer idDeck;

	//Id della carta associata allo slot inserita nel deck
	@Nationalized
	@Column(name = "id_card", nullable = false, length = 50)
	private String idCard;

	
	//Metodo equals e hashcode per la gestione dei tipi
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SlotId other = (SlotId) obj;
		return Objects.equals(idCard, other.idCard) && Objects.equals(idDeck, other.idDeck);
	}
	 
	 
	@Override
	public int hashCode() {
		return Objects.hash(idCard, idDeck);
	}
	
}
