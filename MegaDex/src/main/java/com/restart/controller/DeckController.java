package com.restart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.restart.entity.DeckPass;
import com.restart.entity.Deck;
import com.restart.entity.User;
import com.restart.entity.Slot;
import com.restart.service.DeckServiceImpl;
import com.restart.service.UserServiceImpl;
import com.restart.service.CardServiceImpl;
import com.restart.service.SlotServiceImpl;

@RestController
@RequestMapping("/api")
public class DeckController {
	
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private DeckServiceImpl deckService;
	@Autowired
  	private SlotServiceImpl slotService;
  @Autowired
  private CardServiceImpl cardService;
  
  @PostMapping("/auth/decksByUser")
  public ResponseEntity<List<Deck>> getDecksByUserId(@RequestBody Map<String, Integer> requestBody) {
      // Extract user ID from request body
      int userId = requestBody.get("userId");

      // Find the user by ID
      User user = userService.findUserById(userId)
              .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

      // Fetch the decks associated with the user
      List<Deck> decks = deckService.getDecksByUser(user);

      return ResponseEntity.ok(decks);
  }

	@PostMapping("/auth/myDecks")
	public ResponseEntity<List<Deck>> getMyDecks() {
		//Recupera utente autenticato
		User user = userService.getAuthenticatedUser();

		//Trova i deck associati all'utente
		List<Deck> decks = deckService.getDecksByUser(user);

		return ResponseEntity.ok(decks);
	}

	@PostMapping("/auth/myDeck")
	public ResponseEntity<Deck> getMyDeck(@RequestParam int deckId) {
		//Recupera utente autenticato
		User user = userService.getAuthenticatedUser();
		//Trova il deck
		Optional<Deck> deck = deckService.getDeckById(deckId);

		if(deck.isPresent()
				&& deck.get().getUser() != user){
			throw new RuntimeException("Id Deck not matching its owner");
		}
        return deck.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
  
	@PostMapping("/auth/saveDeck")
	public ResponseEntity<Deck> saveDeck(@RequestParam int deckId, @RequestParam String name, @RequestParam String description) {
		Deck deck = new Deck();
	  //Recupera utente autenticato
		User user = userService.getAuthenticatedUser();
		deck.setUser(user);
		deck.setName(name);
		deck.setDescription(description);

		//Associa la lista di slot se esistente
		if(deckService.getDeckById(deckId).isPresent()){
				if(deckService.getDeckById(deckId).get().getUser() != user){
					throw new RuntimeException("Id Deck not matching its owner");
				}
            deck.setSlots(deckService.getDeckById(deckId).get().getSlots());
			deck.setId(deckId);
        }

        //Salva il deck
        Deck newDeck = deckService.saveDeck(deck);
        return ResponseEntity.ok(newDeck);
	}
	
//Rimuove un deck
	@PostMapping("/deb/removeDeck")
	public ResponseEntity<String> removeDeck(@RequestBody Deck deck){
		//Recupera utente autenticato
		User user = userService.getAuthenticatedUser();
		deck.setUser(user);

		//Controlla che il proprietario sia quello giusto
		if(deckService.getDeckById(deck.getId()).isPresent()
				&& deckService.getDeckById(deck.getId()).get().getUser() != user){
					throw new RuntimeException("Id Deck not matching its owner");
		}

		//Rimuove il deck
		try {
			deckService.removeDeck(deck);
			return ResponseEntity.ok("Deck erased successfully");
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}

	//Valida il set di un deck secondo il regolamento
	@PostMapping("/deb/validateDeck")
    public DeckPass validateDeck(@RequestBody List<Slot> deck) {
      //Associa le carte al deck
		for(Slot slot : deck) {
            slot.setCard(cardService.getCardById(slot.getId().getIdCard())
                    .orElseThrow(() -> new RuntimeException("Card not found with ID: " + slot.getId().getIdCard())));
        }  
      //Esegue la validazione del deck e ritorna un DeckPass
		return slotService.validateSlots(deck);
    }
}
