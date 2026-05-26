/**
 * E-Commerce Interface - DOM Scripting
 * This script handles dynamic product rendering, cart management,
 * form validation, and user account management using vanilla JavaScript
 */

// ============================================================================
// CONFIGURATION
// ============================================================================

const API_BASE_URL = 'http://localhost:8080/api/v1';

// ============================================================================
// TASK 1: DATA STRUCTURE - Product Class & Products Array
// ============================================================================

/**
 * Product Class - Represents a single product with id, name, price, and image
 */
class Product {
  constructor(id, name, price, image, category = 'standard', description = '') {
    this.id = id;
    this.name = name;
    this.price = price;
    this.image = image;
    this.category = category;
    this.description = description;
  }
}

/**
 * Mock product data for fallback when backend is unavailable
 */
const FALLBACK_PRODUCTS = [
  new Product(1, 'One Piece - Oversized Tee', 600, '../image/b7e69fc1-f473-49b7-b02c-1e07fb7abdfe.jpg', 'oversized', 'Comfortable oversized fit perfect for casual wear'),
  new Product(2, 'Eternal - Oversized Tee', 300, '../image/e2680c66-259f-4f45-9e28-f5655d6f30f2.jpg', 'oversized', 'Timeless design with premium quality fabric'),
  new Product(3, 'Upseen - Standard Tee', 400, '../image/f2f8f27e-df3f-4e0c-ba53-f6cf4d3957c1.jpg', 'standard', 'Classic standard fit for everyday wear'),
  new Product(4, 'Urban Vibes - Oversized Tee', 550, '../image/b7e69fc1-f473-49b7-b02c-1e07fb7abdfe.jpg', 'oversized', 'Street style inspired oversized tee'),
  new Product(5, 'Minimalist White - Standard Tee', 350, '../image/f2f8f27e-df3f-4e0c-ba53-f6cf4d3957c1.jpg', 'standard', 'Pure white classic fit tee'),
  new Product(6, 'Retro Black - Oversized Tee', 500, '../image/e2680c66-259f-4f45-9e28-f5655d6f30f2.jpg', 'oversized', 'Vintage inspired oversized design'),
  new Product(7, 'Premium Gray - Standard Tee', 450, '../image/b7e69fc1-f473-49b7-b02c-1e07fb7abdfe.jpg', 'standard', 'Premium quality gray standard fit'),
  new Product(8, 'Bold Navy - Oversized Tee', 580, '../image/f2f8f27e-df3f-4e0c-ba53-f6cf4d3957c1.jpg', 'oversized', 'Navy blue oversized comfort fit'),
  new Product(9, 'Summer Cool - Standard Tee', 380, '../image/e2680c66-259f-4f45-9e28-f5655d6f30f2.jpg', 'standard', 'Lightweight perfect for summer'),
  new Product(10, 'Classic Red - Oversized Tee', 520, '../image/b7e69fc1-f473-49b7-b02c-1e07fb7abdfe.jpg', 'oversized', 'Bold red statement piece'),
  new Product(11, 'Earth Tone - Standard Tee', 420, '../image/f2f8f27e-df3f-4e0c-ba53-f6cf4d3957c1.jpg', 'standard', 'Natural earth tone colors'),
  new Product(12, 'Premium Charcoal - Oversized Tee', 600, '../image/e2680c66-259f-4f45-9e28-f5655d6f30f2.jpg', 'oversized', 'Premium charcoal oversized fit')
];

/**
 * Mock user data for account management
 */
const currentUser = {
  name: 'John Doe',
  email: 'john@example.com',
  orderHistory: [
    {
      id: '#12345',
      date: '2025-01-15',
      total: 1400,
      items: [
        { name: 'One Piece - Oversized Tee', qty: 1, price: 600 },
        { name: 'Upseen - Standard Tee', qty: 2, price: 400 }
      ]
    },
    {
      id: '#12346',
      date: '2025-02-20',
      total: 800,
      items: [
        { name: 'Eternal - Oversized Tee', qty: 2, price: 300 }
      ]
    }
  ]
};

/**
 * Products Array - Fetched from backend API or fallback to local data
 */
let products = [];

// ============================================================================
// API INTEGRATION - Fetch Products from Backend
// ============================================================================

/**
 * Convert backend product format to frontend Product class
 * @param {Object} apiProduct - Product from backend API
 * @returns {Product} Formatted product for frontend use
 */
function formatApiProduct(apiProduct) {
  // Determine category from category object
  const categoryName = apiProduct.category ? apiProduct.category.name : 'standard';
  const categoryMap = {
    'Oversized': 'oversized',
    'Standard Round Neck': 'standard',
    'oversized': 'oversized',
    'standard': 'standard'
  };
  
  const category = categoryMap[categoryName] || 'standard';

  return new Product(
    apiProduct.id,
    apiProduct.name,
    apiProduct.price,
    apiProduct.imageUrl || '../image/default.jpg',
    category,
    apiProduct.description || ''
  );
}

/**
 * Fetch products from backend API with error handling
 * @returns {Promise<Array>} Array of Product objects
 */
async function fetchProductsFromBackend() {
  try {
    console.log('Fetching products from backend...');
    
    const response = await fetch(`${API_BASE_URL}/products`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const apiProducts = await response.json();
    
    if (!Array.isArray(apiProducts)) {
      throw new Error('API returned invalid format');
    }

    // Format products from API
    const formattedProducts = apiProducts.map(apiProduct => formatApiProduct(apiProduct));
    
    console.log(`✓ Successfully loaded ${formattedProducts.length} products from backend`);
    return formattedProducts;
    
  } catch (error) {
    console.warn('Failed to fetch products from backend:', error.message);
    console.log('Using fallback local products...');
    return FALLBACK_PRODUCTS;
  }
}

/**
 * Initialize products on app startup
 */
async function initializeProducts() {
  products = await fetchProductsFromBackend();
  
  // If products array is empty after fetch, use fallback
  if (products.length === 0) {
    console.warn('No products available from any source');
    products = FALLBACK_PRODUCTS;
  }
}

// ============================================================================
// CART MANAGEMENT STATE & FUNCTIONS
// ============================================================================

/**
 * Cart state - holds items with id, quantity, and product reference
 */
let cart = [];

/**
 * Add product to cart
 * @param {number} productId - ID of the product to add
 */
function addToCart(productId) {
  const product = products.find(p => p.id === productId);
  
  if (!product) return;

  // Check if product already exists in cart
  const existingItem = cart.find(item => item.productId === productId);
  
  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({
      productId: productId,
      quantity: 1,
      product: product
    });
  }
  
  // Trigger animation and update UI
  animateAddToCart(productId);
  updateCartDisplay();
  console.log(`Added "${product.name}" to cart. Total items: ${getTotalCartItems()}`);
}

/**
 * Remove product from cart
 * @param {number} productId - ID of the product to remove
 */
function removeFromCart(productId) {
  cart = cart.filter(item => item.productId !== productId);
  updateCartDisplay();
  console.log(`Removed product from cart. Total items: ${getTotalCartItems()}`);
}

/**
 * Update cart item quantity
 * @param {number} productId - ID of the product
 * @param {number} quantity - New quantity
 */
function updateCartQuantity(productId, quantity) {
  const item = cart.find(item => item.productId === productId);
  
  if (item) {
    if (quantity <= 0) {
      removeFromCart(productId);
    } else {
      item.quantity = quantity;
      updateCartDisplay();
    }
  }
}

/**
 * Calculate total price using reduce method
 * @returns {number} Total price of all items in cart
 */
function calculateCartTotal() {
  return cart.reduce((total, item) => {
    return total + (item.product.price * item.quantity);
  }, 0);
}

/**
 * Get total number of items in cart
 * @returns {number} Total count of items
 */
function getTotalCartItems() {
  return cart.reduce((total, item) => total + item.quantity, 0);
}

/**
 * Format currency to PHP Peso
 * @param {number} amount - Amount to format
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount) {
  return '₱' + amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/**
 * Animate the "Add to Cart" button with fade-in effect
 * @param {number} productId - ID of the product being added
 */
function animateAddToCart(productId) {
  const button = document.querySelector(`[data-id="${productId}"]`);
  if (button) {
    button.classList.add('fade-in');
    setTimeout(() => {
      button.classList.remove('fade-in');
    }, 600);
  }
}

/**
 * Save cart to localStorage for persistence
 */
function saveCartToStorage() {
  const cartData = cart.map(item => ({
    productId: item.productId,
    quantity: item.quantity
  }));
  localStorage.setItem('ecommerceCart', JSON.stringify(cartData));
}

/**
 * Load cart from localStorage
 */
function loadCartFromStorage() {
  const stored = localStorage.getItem('ecommerceCart');
  if (stored) {
    try {
      const cartData = JSON.parse(stored);
      cart = cartData.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        product: products.find(p => p.id === item.productId)
      })).filter(item => item.product); // Filter out products that no longer exist
      updateCartDisplay();
    } catch (e) {
      console.error('Error loading cart from storage:', e);
    }
  }
}

// ============================================================================
// TASK 2: DYNAMIC PRODUCT RENDERING
// ============================================================================

/**
 * Render products dynamically on products.html
 * Creates product cards with images, details, and add-to-cart buttons
 */
function renderProducts() {
  const productContainer = document.querySelector('#product-container');
  
  if (!productContainer) return; // Not on products page
  
  // Clear existing products (keep filter form and other elements)
  const productArticles = productContainer.querySelectorAll('article.product');
  productArticles.forEach(article => article.remove());

  // Filter products based on current filter state
  const activeCategories = Array.from(document.querySelectorAll('.cat-check:checked'))
    .map(cb => cb.value);
  const selectedPriceRange = document.querySelector('input[name="price"]:checked')?.value || 'all';

  // Filter the products array
  const filteredProducts = products.filter(product => {
    const categoryMatch = activeCategories.includes(product.category);
    
    let priceMatch = false;
    if (selectedPriceRange === 'all') {
      priceMatch = true;
    } else if (selectedPriceRange === 'low') {
      priceMatch = product.price <= 400;
    } else if (selectedPriceRange === 'high') {
      priceMatch = product.price > 400;
    }

    return categoryMatch && priceMatch;
  });

  // Use forEach to iterate through filtered products
  filteredProducts.forEach(product => {
    // Create article element for product card
    const article = document.createElement('article');
    article.className = `product ${product.category}`;
    article.setAttribute('data-price', product.price);

    // Create and append image
    const img = document.createElement('img');
    img.src = product.image;
    img.alt = product.name;
    article.appendChild(img);

    // Create and append product name
    const h2 = document.createElement('h2');
    h2.textContent = product.name;
    article.appendChild(h2);

    // Create and append description
    const description = document.createElement('p');
    description.className = 'description';
    description.textContent = product.description;
    article.appendChild(description);

    // Create and append price
    const price = document.createElement('p');
    price.className = 'price';
    price.textContent = formatCurrency(product.price);
    article.appendChild(price);

    // Create Add to Cart button
    const addBtn = document.createElement('button');
    addBtn.type = 'button';
    addBtn.textContent = 'Add to Cart';
    addBtn.setAttribute('data-id', product.id);
    addBtn.className = 'add-to-cart-btn';
    article.appendChild(addBtn);

    // Append to container
    productContainer.appendChild(article);
  });
}

// ============================================================================
// TASK 3: CART PAGE RENDERING & EVENT DELEGATION
// ============================================================================

/**
 * Render cart items on cart.html page
 * Uses event delegation for quantity changes and remove buttons
 */
function renderCart() {
  const cartList = document.querySelector('.cart-list');
  const cartSummary = document.querySelector('.cart-summary');

  if (!cartList) return; // Not on cart page

  // Clear existing cart items
  cartList.innerHTML = '';

  // Check if cart is empty
  if (cart.length === 0) {
    const emptyMessage = document.createElement('li');
    emptyMessage.textContent = 'Your cart is empty';
    emptyMessage.style.textAlign = 'center';
    emptyMessage.style.padding = '20px';
    cartList.appendChild(emptyMessage);
    
    if (cartSummary) {
      cartSummary.style.display = 'none';
    }
    return;
  }

  if (cartSummary) {
    cartSummary.style.display = 'block';
  }

  // Use forEach to render each cart item
  cart.forEach(item => {
    const li = document.createElement('li');
    li.className = 'cart-item';
    li.setAttribute('data-product-id', item.productId);

    // Image section
    const imageDiv = document.createElement('div');
    imageDiv.className = 'cart-item-image';
    const img = document.createElement('img');
    img.src = item.product.image;
    img.alt = item.product.name;
    imageDiv.appendChild(img);
    li.appendChild(imageDiv);

    // Details section
    const detailsDiv = document.createElement('div');
    detailsDiv.className = 'cart-item-details';
    
    const h3 = document.createElement('h3');
    h3.textContent = item.product.name;
    detailsDiv.appendChild(h3);

    const priceP = document.createElement('p');
    priceP.className = 'price';
    priceP.textContent = formatCurrency(item.product.price);
    detailsDiv.appendChild(priceP);

    li.appendChild(detailsDiv);

    // Actions section
    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'cart-item-actions';

    const label = document.createElement('label');
    label.htmlFor = `qty-${item.productId}`;
    label.textContent = 'Qty:';
    actionsDiv.appendChild(label);

    const qtyInput = document.createElement('input');
    qtyInput.type = 'number';
    qtyInput.id = `qty-${item.productId}`;
    qtyInput.value = item.quantity;
    qtyInput.min = '1';
    qtyInput.className = 'quantity-input';
    qtyInput.setAttribute('data-product-id', item.productId);
    actionsDiv.appendChild(qtyInput);

    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.textContent = 'Remove';
    removeBtn.className = 'remove-btn';
    removeBtn.setAttribute('data-product-id', item.productId);
    actionsDiv.appendChild(removeBtn);

    li.appendChild(actionsDiv);
    cartList.appendChild(li);
  });

  // Update cart summary
  updateCartSummary();
}

/**
 * Update the cart summary section with subtotal and total items
 */
function updateCartSummary() {
  const totalElement = document.querySelector('.total-amount');
  if (totalElement) {
    totalElement.textContent = formatCurrency(calculateCartTotal());
  }
}

/**
 * Update the cart display (called after any cart modification)
 * This is a wrapper that updates both cart page and any cart indicators
 */
function updateCartDisplay() {
  renderCart();
  saveCartToStorage();
  
  // Update any cart count badges if they exist
  const cartCountBadges = document.querySelectorAll('.cart-count');
  cartCountBadges.forEach(badge => {
    badge.textContent = getTotalCartItems();
  });
}

// ============================================================================
// TASK 4: FORM VALIDATION & CHECKOUT
// ============================================================================

/**
 * Validate checkout form
 * Checks all required fields and displays error messages
 */
function validateCheckoutForm(event) {
  event.preventDefault();

  // Get form and all inputs
  const form = event.target;
  const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
  let isValid = true;

  // Clear previous errors
  form.querySelectorAll('.error-message').forEach(msg => msg.remove());
  inputs.forEach(input => input.classList.remove('error'));

  // Validate each field
  inputs.forEach(input => {
    const value = input.value.trim();

    if (value === '') {
      isValid = false;
      input.classList.add('error');

      // Create error message
      const errorMsg = document.createElement('p');
      errorMsg.className = 'error-message';
      errorMsg.textContent = `${input.previousElementSibling?.textContent || 'This field'} is required`;
      errorMsg.style.color = '#d32f2f';
      errorMsg.style.fontSize = '0.875rem';
      errorMsg.style.marginTop = '-10px';
      errorMsg.style.marginBottom = '10px';
      input.parentNode.insertBefore(errorMsg, input.nextSibling);
    } else if (input.type === 'email' && !isValidEmail(value)) {
      isValid = false;
      input.classList.add('error');

      const errorMsg = document.createElement('p');
      errorMsg.className = 'error-message';
      errorMsg.textContent = 'Please enter a valid email address';
      errorMsg.style.color = '#d32f2f';
      errorMsg.style.fontSize = '0.875rem';
      errorMsg.style.marginTop = '-10px';
      errorMsg.style.marginBottom = '10px';
      input.parentNode.insertBefore(errorMsg, input.nextSibling);
    }
  });

  // Check if payment method is selected
  const paymentInputs = form.querySelectorAll('input[name="payment"]');
  const paymentSelected = Array.from(paymentInputs).some(input => input.checked);

  if (paymentInputs.length > 0 && !paymentSelected) {
    isValid = false;
    const paymentGroup = form.querySelector('[role="group"]') || paymentInputs[0].closest('fieldset');
    if (paymentGroup) {
      paymentGroup.classList.add('error');
    }
  }

  if (isValid) {
    // Log success and simulate redirect
    console.log('✓ Form validation successful! Order submitted.');
    alert('Order placed successfully! Redirecting to thank you page...');
    // Simulate order placement
    const orderData = {
      date: new Date().toISOString(),
      items: cart,
      total: calculateCartTotal(),
      timestamp: Date.now()
    };
    localStorage.setItem('lastOrder', JSON.stringify(orderData));
    cart = []; // Clear cart after successful checkout
    saveCartToStorage();
    
    // Redirect after a short delay
    setTimeout(() => {
      window.location.href = 'landing.html';
    }, 1500);
  }
}

/**
 * Simple email validation helper
 * @param {string} email - Email to validate
 * @returns {boolean} True if valid email format
 */
function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

// ============================================================================
// TASK 5: USER ACCOUNT & ORDER HISTORY
// ============================================================================

/**
 * Render user account page with greeting and order history
 */
function renderAccountPage() {
  // Update greeting with user name in header
  const headerH1 = document.querySelector('header h1');
  if (headerH1) {
    headerH1.textContent = `Welcome, ${currentUser.name}!`;
  }

  // Get the order history section
  const orderHistorySection = document.querySelector('.order-history');
  if (!orderHistorySection) return;

  // Clear existing content
  orderHistorySection.innerHTML = '';

  // Create order history container
  currentUser.orderHistory.forEach(order => {
    const details = document.createElement('details');
    details.className = 'order-details';

    const summary = document.createElement('summary');
    summary.textContent = `${order.id} - ${order.date} - ${formatCurrency(order.total)}`;
    details.appendChild(summary);

    // Create order items list
    const ul = document.createElement('ul');
    ul.className = 'order-items';

    order.items.forEach(item => {
      const li = document.createElement('li');
      li.innerHTML = `<strong>${item.name}</strong> × ${item.qty} - ${formatCurrency(item.price * item.qty)}`;
      ul.appendChild(li);
    });

    details.appendChild(ul);
    orderHistorySection.appendChild(details);
  });
}

// ============================================================================
// TASK 6: EVENT LISTENERS & INITIALIZATION
// ============================================================================

/**
 * Set up event delegation for product list (Add to Cart buttons)
 */
function setupProductEventDelegation() {
  const productContainer = document.querySelector('#product-container');
  
  if (productContainer) {
    productContainer.addEventListener('click', (event) => {
      // Event delegation: Check if clicked element is an "Add to Cart" button
      if (event.target.classList.contains('add-to-cart-btn')) {
        const productId = parseInt(event.target.getAttribute('data-id'));
        addToCart(productId);
      }
    });
  }
}

/**
 * Set up event delegation for cart page (Remove buttons and quantity inputs)
 */
function setupCartEventDelegation() {
  const cartList = document.querySelector('.cart-list');
  
  if (cartList) {
    cartList.addEventListener('click', (event) => {
      // Handle remove button clicks
      if (event.target.classList.contains('remove-btn')) {
        const productId = parseInt(event.target.getAttribute('data-product-id'));
        removeFromCart(productId);
      }
    });

    // Handle quantity input changes using event delegation
    cartList.addEventListener('change', (event) => {
      if (event.target.classList.contains('quantity-input')) {
        const productId = parseInt(event.target.getAttribute('data-product-id'));
        const newQuantity = parseInt(event.target.value);
        updateCartQuantity(productId, newQuantity);
      }
    });
  }
}

/**
 * Set up form validation on checkout page
 */
function setupFormValidation() {
  const checkoutForm = document.getElementById('checkout-form');
  if (checkoutForm) {
    checkoutForm.addEventListener('submit', validateCheckoutForm);
  }
}

/**
 * Set up product filters on products page
 */
function setupProductFilters() {
  const filterForm = document.getElementById('filter-form');
  if (filterForm) {
    filterForm.addEventListener('change', () => {
      renderProducts();
    });
  }
}

/**
 * Main initialization function - runs when DOM is loaded
 */
async function initializeApp() {
  console.log('E-Commerce Interface initializing...');

  // Initialize products from backend or fallback
  await initializeProducts();

  // Load cart from storage
  loadCartFromStorage();

  // Initialize based on current page
  const currentPage = window.location.pathname;

  if (currentPage.includes('products.html')) {
    renderProducts();
    setupProductEventDelegation();
    setupProductFilters();
  }

  if (currentPage.includes('cart.html')) {
    renderCart();
    setupCartEventDelegation();
  }

  if (currentPage.includes('checkout.html')) {
    setupFormValidation();
    // Pre-fill cart items in summary
    renderCheckoutSummary();
  }

  if (currentPage.includes('account.html')) {
    renderAccountPage();
  }

  // Setup landing page cart link (if exists)
  setupCartLink();

  console.log('✓ E-Commerce Interface ready');
}

/**
 * Render order summary on checkout page
 */
function renderCheckoutSummary() {
  const summaryList = document.querySelector('.summary-list');
  if (!summaryList) return;

  summaryList.innerHTML = '';

  if (cart.length === 0) {
    const li = document.createElement('li');
    li.textContent = 'No items in cart';
    summaryList.appendChild(li);
    return;
  }

  cart.forEach(item => {
    const li = document.createElement('li');
    const totalPrice = item.product.price * item.quantity;
    li.innerHTML = `${item.product.name} × ${item.quantity} <span class="item-price">${formatCurrency(totalPrice)}</span>`;
    summaryList.appendChild(li);
  });

  // Update total
  const totalElement = document.querySelector('.summary-total-row .total-amount');
  if (totalElement) {
    totalElement.textContent = formatCurrency(calculateCartTotal());
  }
}

/**
 * Setup cart link to show item count on landing page
 */
function setupCartLink() {
  const cartLink = document.querySelector('a[href="cart.html"]');
  if (cartLink && getTotalCartItems() > 0) {
    const badge = document.createElement('span');
    badge.className = 'cart-count';
    badge.textContent = getTotalCartItems();
    badge.style.marginLeft = '5px';
    badge.style.backgroundColor = '#d32f2f';
    badge.style.color = 'white';
    badge.style.borderRadius = '50%';
    badge.style.padding = '2px 6px';
    badge.style.fontSize = '0.75rem';
    cartLink.appendChild(badge);
  }
}

// ============================================================================
// INITIALIZE APP WHEN DOM IS LOADED
// ============================================================================

document.addEventListener('DOMContentLoaded', () => {
  initializeApp().catch(error => {
    console.error('Failed to initialize app:', error);
  });
});
