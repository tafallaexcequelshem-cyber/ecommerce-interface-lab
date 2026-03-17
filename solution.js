function getTopScorers(playerList) {
    return playerList
        .filter(player => player.score > 8)
        .map(player => player.name)
        .join(", ");
}

class Item {
    #discount = 0.1;

    constructor(name, price) {
        this.name = name;
        this.price = price;
    }

    get finalPrice() {
        return this.price - (this.price * this.#discount);
    }
}