const axios = require('axios');

const API_BASE = 'http://localhost:8081/api';

async function testOrderAndStock() {
  try {
    // 1. Login
    console.log('Logging in...');
    const loginRes = await axios.post(`${API_BASE}/auth/login`, {
      username: 'admin',
      password: '1234'
    });
    const { token, branchId, userId } = loginRes.data;
    console.log(`Login successful. User Branch ID: ${branchId}`);

    // 2. Get Menu Items to find one to order
    console.log('Fetching menu items...');
    const menuRes = await axios.get(`${API_BASE}/menu-items`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const item = menuRes.data[0];
    if (!item) {
        console.error('No menu items found');
        return;
    }
    console.log(`Selected item for order: ${item.name} (ID: ${item.menuItemId})`);

    // 3. Try to place an order
    console.log('Placing order...');
    try {
        const orderRequest = {
            branchId: branchId,
            cashierUserId: userId,
            orderType: 'DINE_IN',
            status: 'PENDING',
            items: [
                {
                    menuItemId: item.menuItemId,
                    quantity: 1
                }
            ]
        };
        
        const orderRes = await axios.post(`${API_BASE}/orders`, orderRequest, {
            headers: { Authorization: `Bearer ${token}` }
        });
        console.log('Order created successfully:', orderRes.data.orderNo);
    } catch (err) {
        console.error('Order failed (EXPECTED if no stock):', err.response?.data?.message || err.message);
    }

    // 4. Manually add stock to branch and try again
    console.log('\nAdding stock to branch...');
    // We need to find the ingredients for this menu item first
    const recipeRes = await axios.get(`${API_BASE}/menu-items/${item.menuItemId}/recipe`, {
        headers: { Authorization: `Bearer ${token}` }
    });
    const recipes = recipeRes.data;
    if (recipes.length === 0) {
        console.log('No recipe found for this item, stock check might be skipped if no ingredients defined.');
    } else {
        const ingredientId = recipes[0].ingredient.ingredientId;
        console.log(`Adding stock for ingredient: ${recipes[0].ingredient.name} (ID: ${ingredientId})`);
        
        // Manual adjustment
        await axios.post(`${API_BASE}/stock-adjustments`, {
            ingredientId: ingredientId,
            branchId: branchId,
            qtyChange: 100,
            reasonType: 'RESTOCK',
            note: 'Manual test restock'
        }, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        // Approve it (usually requires PIN but depends on backend logic)
        // For simplicity, let's see if we can just get current stock now
        const stockRes = await axios.get(`${API_BASE}/inventory/branch/${branchId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        console.log(`Current branch inventory count: ${stockRes.data.length}`);
    }

  } catch (error) {
    if (error.response) {
        console.error('Error:', error.response.status, error.response.data);
    } else {
        console.error('Error:', error.message);
    }
  }
}

testOrderAndStock();
