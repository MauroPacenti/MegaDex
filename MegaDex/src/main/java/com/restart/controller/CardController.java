package com.restart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.restart.entity.Card;
import com.restart.service.CardService;


@RestController
public class CardController {
	@Autowired
	private CardService service;
	
	
	@GetMapping("/cards")
	public ResponseEntity<List<Card>> getCards(){
		return new ResponseEntity<>(service.getCards(),HttpStatus.OK);
	}
	
	@GetMapping("/cards/{name}")
	public List<Card> getCardsByName(@PathVariable String name) {
		return service.getCardsByName(name);
	}
	
	@GetMapping("/cards/supertype={supertype}")
	public List<Card> getCardsBySupertype(@PathVariable String supertype) {
		return service.getCardsBySupertypeName(supertype);
	}
	
	@GetMapping("/cards/type={type}")
	public List<Card> getCardsByType(@PathVariable String type) {
		return service.getCardsByType(type);
	}
	@GetMapping("/cards/subtype={subtype}")
	public List<Card> getCardsBySubtype(@PathVariable String subtype) {
		return service.getCardsBySubtype(subtype);
	}
	@GetMapping("/cards/set={set}")
	public List<Card> getCardsBySet(@PathVariable String set) {
		return service.getCardsBySet(set);
	}
}