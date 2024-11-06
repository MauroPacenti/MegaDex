package com.restart.service;

import java.util.List;

import com.restart.entity.SlotId;
import org.springframework.stereotype.Service;
import com.restart.entity.DeckPass;
import com.restart.entity.Slot;

@Service
public interface SlotService {
	List<Slot> getSlot();

	List<Slot> getSlotsByDeckId(int deckId);
	
	Slot addSlot(Slot slot);
	
	void removeSlot(SlotId slotId);

	DeckPass validateSlots(List<Slot> slots);
}
