document.addEventListener("DOMContentLoaded", function () {
searchCards();
getDecks();

});
let slots = [];
let currentPage = 1; // Pagina corrente iniziale

// Funzione per inviare la richiesta al controller con i parametri dei filtri
async function searchCards(page = 1) {
    event.preventDefault();
    // Prevenire il comportamento predefinito del form
    const name = document.querySelector('input[name="card-search"]').value;
    const orderBy = document.getElementById("sort-menu").value;
    const type = document.getElementById("filter-type").value;
    const subtype = document.getElementById("filter-subtype").value;
    const supertype = document.getElementById("filter-supertype").value;
    const release = document.getElementById("filter-release").value;

    const params = new URLSearchParams({
        // owned: owned,
        name: name,
        size: 9,
        type: type,
        subtype: subtype,
        supertype: supertype,
        set: release,
        orderBy: orderBy === "Seleziona un'opzione" ? "Id" : orderBy.toLowerCase(),
        direction: "asc",
        page: page,
    });

    try {
        const response = await fetch(`http://localhost:8080/api/auth/filtered-in-sleeves?${params.toString()}`);
        if (!response.ok) {
            throw new Error("Errore durante il recupero delle carte");
        }
        const data = await response.json();
        updateCardList(data.cards);
        updatePagination(data.totalPages); // Passa il numero totale di pagine
    } catch (error) {
        console.error("Errore:", error);
    }
}

// Funzione per mostrare le carte trovate
function updateCardList(cards) {
    const cardListContainer = document.querySelector('.card-list');
    cardListContainer.innerHTML = '';

    if (!cards.length) {
        cardListContainer.innerHTML = '<p>Nessuna carta trovata.</p>';
        return;
    }

    cards.forEach(card => {
        const cardElement = document.createElement('div');
        cardElement.classList.add('card-item');
        cardElement.innerHTML = `
        <div class="card">
        <img src="${card.img}" alt="" onclick="addSlot('${card.id}','${card.img}')">
        </div>
      `;
        cardListContainer.appendChild(cardElement);
    });
}

async function searchSlots(deckId) {
    // event.preventDefault();
    const params = new URLSearchParams({
        deckId: deckId,
    });

    try {
        const response = await fetch(`http://localhost:8080/api/auth/slotsByDeck?${params.toString()}`);
        if (!response.ok) {
            throw new Error("Errore durante il recupero delle carte");
        }
        slots = await response.json();
        updateSlotList(slots);
    } catch (error) {
        console.error("Errore:", error);
    }
}

// Funzione per mostrare le carte trovate
function updateSlotList(slots) {
    const slotListContainer = document.querySelector('.slot-list');
    slotListContainer.innerHTML = '';
    let index=0;

    if (!slots.length) {
        slotListContainer.innerHTML = '<p>Nessuna carta trovata.</p>';
        return;
    }

    slots.forEach(slot => {
        const slotElement = document.createElement('div');
        slotElement.classList.add('slot-item');
        slotElement.innerHTML = `
        <div class="slot" onclick="decreaseQuantity('${index}')">
        <p>${slot.id.idCard}</p><p>${slot.quantity}</p>
        </div>
      `;
        slotElement.style.background = `linear-gradient(rgba(180, 180, 180, 0.6), rgba(180, 180, 180, 0.6)),url(${slot.card.img})`;
        slotListContainer.appendChild(slotElement);
        index++;
    });
}

function decreaseQuantity(index) {
    slots[index].quantity--;
    if(slots[index].quantity === 0)
        slots.splice(index,1);
    updateSlotList(slots);
}

function increaseQuantity(index) {
    slots[index].quantity++;
    updateSlotList(slots);
}

function addSlot(cardId, cardImg) {
    let idDeck = 0;
    let newSlot = 1;
    if(slots.length){
        idDeck = slots[0].id.idDeck;
        slots.forEach(slot => {
            if (slot.id.idCard === cardId){
                slot.quantity++;
                updateSlotList(slots);
                newSlot = 0;
            }
        })
    }
    if(newSlot === 1){
    let slot = {
            id:{
                idCard: cardId,
                idDeck: idDeck
            },
        card: {img: cardImg},
        quantity: 1
    }
    slots.push(slot);
    updateSlotList(slots);
    }
}


// Funzione per aggiornare i bottoni di navigazione
function updatePagination(totalPages) {
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const currentPageDisplay = document.getElementById('current-page');

    currentPageDisplay.innerText = `Pagina ${currentPage} di ${totalPages}`;

    // Abilita/disabilita i bottoni in base alla pagina corrente
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
}

// Funzione per confrontare la carta selezionata con la collezione dell'utente
function getMySleeve(cardId) {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", `http://localhost:8080/api/auth/mySleeve?cardRequest=${cardId}`, false); // false per farlo sincrono
    xhr.send();
    if (xhr.status === 200) {
        return JSON.parse(xhr.responseText);
    } else {
        console.error('Errore nella chiamata GET:', xhr.status);
        return undefined;
    }
}

// Event listeners per i bottoni di navigazione
document.getElementById('prev-page').addEventListener('click', () => {
    if (currentPage > 1) {
        currentPage--;
        searchCards(currentPage);
    }
});
document.getElementById('next-page').addEventListener('click', () => {
    currentPage++;
    searchCards(currentPage);
});
// Aggiungi l'evento submit al form di ricerca
document.getElementById('search-form1').addEventListener('submit', (event) => {
    event.preventDefault();
    currentPage = 1; // Resetta la pagina a 1 quando si effettua una nuova ricerca
    searchCards(currentPage);
});


//funzione per mostrare la carta selezionata
async function selectCard(cardId) {
    const param = new URLSearchParams({id: cardId})
    let card;
    try {
        const response = await fetch(`http://localhost:8080/api/deb/cardById?${param.toString()}`);
        if (!response.ok) {
            throw new Error("Errore durante il recupero della carta");
        }
        card = await response.json();
    } catch (error) {
        console.error("Errore:", error);
    }

    const cardContainer = document.querySelector('.card-details');
    cardContainer.innerHTML = '';

    const cardElement = document.createElement('div');
    const  slot = getMySleeve(card.id);
    let quantity;

    if (slot === undefined)
        quantity = 0;
    else
        quantity = slot.quantity;

    cardElement.classList.add('card-item');
    cardElement.innerHTML = ``;
    cardContainer.appendChild(cardElement);
}


    // Funzione per creare l'elemento della carta selezionata
function createSelectedCard(cardName) {
        const cardList = document.querySelector('.card-list');
        const selectedCardList = document.getElementById('selected-card-list');
        const selectedCards = {}; // Oggetto per tenere traccia delle carte selezionate
        let cardCount = 0; // Conta le carte selezionate
        const li = document.createElement('li');
        li.setAttribute('data-card-name', cardName);

        // Crea il numero delle carte
        const cardCountSpan = document.createElement('span');
        cardCountSpan.classList.add('card-count');
        cardCountSpan.textContent = '1';

        // Crea il nome della carta
        const cardNameSpan = document.createElement('span');
        cardNameSpan.classList.add('card-name');
        cardNameSpan.textContent = cardName;

        // Crea un pulsante per rimuovere la carta
        const removeButton = document.createElement('button');
        removeButton.textContent = 'X';
        removeButton.classList.add('remove-btn');
        removeButton.addEventListener('click', () => {
            const count = parseInt(cardCountSpan.textContent);

            if (count > 1) {
                cardCountSpan.textContent = count - 1; // Riduce il conteggio
            } else {
                li.remove(); // Rimuove l'elemento se non ci sono più carte
                delete selectedCards[cardName]; // Rimuove la carta dall'oggetto
            }

            cardCount--; // Riduce il conteggio totale delle carte
        });

        // Appendi il numero, nome e pulsante all'elemento <li>
        li.appendChild(cardCountSpan);
        li.appendChild(cardNameSpan);
        li.appendChild(removeButton);

        // Aggiunge la carta selezionata alla lista
        selectedCardList.appendChild(li);
    }

async function getDecks(){
    try{
        const response = await fetch('http://localhost:8080/api/auth/myDecks',{
            method: 'POST'
        })
            if(!response){
                throw new Error("Errore durante il recupero dei mazzi");
            }
            const decks =  await response.json();
            loadDecks(decks);

    }
    catch{}
}

function loadDecks(decks) {
    const optionsElement = document.querySelector('.deck-option');
    decks.forEach(deck => {
        const optionElement = document.createElement('option');
        optionElement.value = deck.id;
        optionElement.innerHTML=`
            ${deck.name}
            `;
        optionsElement.appendChild(optionElement);
        });
    }



