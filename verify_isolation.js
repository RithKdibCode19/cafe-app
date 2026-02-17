const axios = require('axios');

const API_BASE = 'http://localhost:8081/api';

async function testStockIsolation() {
  try {
    // 1. Login
    console.log('Logging in...');
    const loginRes = await axios.post(`${API_BASE}/auth/login`, {
      username: 'admin',
      password: '1234'
    });
    const { token, userId } = loginRes.data;
    console.log('Login successful');

    // 2. Get Branches
    const branchesRes = await axios.get(`${API_BASE}/branches`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const branches = branchesRes.data;
    const b1 = branches[0];
    const b2 = branches.find(b => b.branchId !== b1.branchId);
    
    console.log(`Testing with Branch 1: ${b1.name} (ID: ${b1.branchId}) and Branch 2: ${b2.name} (ID: ${b2.branchId})`);

    // 3. Get an ingredient
    const ingRes = await axios.get(`${API_BASE}/ingredients`, {
        headers: { Authorization: `Bearer ${token}` }
    });
    const ingredient = ingRes.data[0];
    console.log(`Target Ingredient: ${ingredient.name} (ID: ${ingredient.ingredientId})`);

    // 4. Create Adjustment for Branch 1
    console.log(`\nCreating adjustment (+100) for Branch 1 (${b1.name})...`);
    const adjRes = await axios.post(`${API_BASE}/stock-adjustments`, {
        ingredientId: ingredient.ingredientId,
        branchId: b1.branchId,
        qtyChange: 100,
        reasonType: 'COUNT_CORRECTION',
        note: 'Isolation Test',
        createdBy: userId,
        date: new Date().toISOString()
    }, {
        headers: { Authorization: `Bearer ${token}` }
    });
    const adjustmentId = adjRes.data.adjustmentId;

    // 5. Approve Adjustment with UNIQUE PIN '9999'
    console.log(`Approving adjustment ${adjustmentId} with PIN 9999...`);
    await axios.put(`${API_BASE}/stock-adjustments/${adjustmentId}/approve`, null, {
        headers: { Authorization: `Bearer ${token}` },
        params: { pinCode: '9999' }
    });

    // 6. Check Inventory
    console.log('\nVerifying stock levels...');
    
    const s1Res = await axios.get(`${API_BASE}/inventory/branch/${b1.branchId}`, {
        headers: { Authorization: `Bearer ${token}` }
    });
    const b1Stock = s1Res.data.find(s => s.ingredientId === ingredient.ingredientId);
    console.log(`Branch 1 (${b1.name}) stock: ${b1Stock ? b1Stock.currentStock : 0}`);

    const s2Res = await axios.get(`${API_BASE}/inventory/branch/${b2.branchId}`, {
        headers: { Authorization: `Bearer ${token}` }
    });
    const b2Stock = s2Res.data.find(s => s.ingredientId === ingredient.ingredientId);
    console.log(`Branch 2 (${b2.name}) stock: ${b2Stock ? b2Stock.currentStock : 0}`);

    if ((b1Stock?.currentStock || 0) > 0 && (b2Stock?.currentStock || 0) === 0) {
        console.log('\nSUCCESS: Stock isolation confirmed. Branch 1 has stock, Branch 2 remains at zero.');
    } else {
        console.log('\nRESULTS: Branch 1:', b1Stock?.currentStock || 0, 'Branch 2:', b2Stock?.currentStock || 0);
    }

  } catch (error) {
    if (error.response) {
        console.error('Error:', error.response.status, error.response.data);
    } else {
        console.error('Error:', error.message);
    }
  }
}

testStockIsolation();
