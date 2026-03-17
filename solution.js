function calculateTotal(...numbers) {
    return numbers.reduce((acc, curr) => {
        if (typeof curr !== 'number') {
            throw new TypeError("Invalid input: All arguments must be numbers");
        }
        return acc + curr;
    }, 0);
}