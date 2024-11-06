package com.restart.controller;
import java.util.List;
import com.restart.dto.SlotDto;
import com.restart.entity.Card;
import com.restart.entity.Deck;
import com.restart.entity.Slot;
import com.restart.entity.SlotId;
import com.restart.service.CardServiceImpl;
import com.restart.service.SlotServiceImpl;
import com.restart.service.DeckServiceImpl;
import com.restart.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class SlotController {
	
	@Autowired
	private SlotServiceImpl slotService;
	@Autowired
	private CardServiceImpl cardService;
	@Autowired
	private DeckServiceImpl deckService;
	@Autowired
	private UserServiceImpl userService;
	
	
	//Aggiorna o rimuove uno slot
	@PostMapping("/auth/updateSlot")
	public ResponseEntity<Slot> addSlot(@RequestBody SlotDto slot) {

		int deckId = slot.getIdDeck();
		try {
			if (deckService.getDeckById(deckId).get().getUser() != userService.getAuthenticatedUser()
					&& deckService.getDeckById(deckId).isPresent()){
				throw new RuntimeException("proprietario non corrispondente");
			}

		} catch (Exception e){
			return ResponseEntity.noContent().build();
		}

		Slot slotRequest = new Slot();
		slotRequest.setId(new SlotId());

		// Recupera il deck dal database usando l'ID passato nella richiesta
		Deck deck = deckService.getDeckById(slot.getIdDeck())
				.orElseThrow(() -> new RuntimeException("Card not found with ID: " + slotRequest.getId().getIdCard()));;
		Card card = cardService.getCardById(slot.getIdCard())
				.orElseThrow(() -> new RuntimeException("Card not found with ID: " + slotRequest.getId().getIdCard()));


		// Associa idCard e idDeck
		slotRequest.getId().setIdDeck(slot.getIdDeck());
		slotRequest.getId().setIdCard(slot.getIdCard());
		slotRequest.setQuantity(slot.getQuantity());


		// Associa carta e deck
		slotRequest.setDeck(deck);
		slotRequest.setCard(card);


		// Salva lo slot se la quantità è maggiore di 0
		if(slotRequest.getQuantity()>0){
			Slot savedSlot = slotService.addSlot(slotRequest);
			return ResponseEntity.ok(savedSlot);
		}
		//altrimenti lo rimuove
		else {
			slotService.removeSlot(slotRequest.getId());
			return ResponseEntity.ok(slotRequest);
		}

	}
	
	//Rimuove uno slot
	@PostMapping("/auth/removeSlot")
	public ResponseEntity<String> removeSlot(@RequestBody SlotDto slot){
		int deckId = slot.getIdDeck();
		try {
			if (deckService.getDeckById(deckId).get().getUser() != userService.getAuthenticatedUser()
					&& deckService.getDeckById(deckId).isPresent()){
				throw new RuntimeException("proprietario non corrispondente");
			}

		} catch (Exception e){
			return ResponseEntity.noContent().build();
		}
		SlotId slotRequest = new SlotId();
		slotRequest.setIdDeck(slot.getIdDeck());
		slotRequest.setIdCard(slot.getIdCard());

		try {
            slotService.removeSlot(slotRequest);
            return ResponseEntity.ok("Slot eliminato con successo");
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
	}

	//Aggiunge una lista di slot
	@PostMapping("/auth/saveSlots")
	public ResponseEntity<List<SlotDto>> addSlots(@RequestBody List<SlotDto> slotsRequest){
		int deckId = slotsRequest.get(0).getIdDeck();
		try {
			if (deckService.getDeckById(deckId).get().getUser() != userService.getAuthenticatedUser()
					&& deckService.getDeckById(deckId).isPresent()){
				throw new RuntimeException("proprietario non corrispondente");
			}

		} catch (Exception e){
			return ResponseEntity.noContent().build();
		}
		List<Slot> oldSlots = slotService.getSlotsByDeckId(slotsRequest.get(0).getIdDeck());
		for(Slot slot : oldSlots){
			SlotDto slotDto = new SlotDto();
			slotDto.setIdDeck(slot.getId().getIdDeck());
			slotDto.setIdCard(slot.getId().getIdCard());
			slotDto.setQuantity(0);
			removeSlot(slotDto);
		}
		for(SlotDto slot : slotsRequest){
			addSlot(slot);
		}
	return ResponseEntity.ok(slotsRequest);
	}

	@GetMapping("/auth/slotsByDeck")
	public ResponseEntity<List<Slot>> getSlotsByDeck(@RequestParam int deckId){
		try {
			if (deckService.getDeckById(deckId).get().getUser() != userService.getAuthenticatedUser()
			&& deckService.getDeckById(deckId).isPresent()){
				throw new RuntimeException("proprietario non corrispondente");
			}
			List<Slot> slots = slotService.getSlotsByDeckId(deckId);
			return ResponseEntity.ok(slots);
		} catch (Exception e){
			return ResponseEntity.noContent().build();
		}
	}

}
